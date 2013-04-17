/*
 * Copyright 2011-2013 Gregory Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.intellij.grammar;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import gnu.trove.THashSet;
import org.intellij.grammar.parser.BnfLexer;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfFileImpl;
import org.intellij.grammar.psi.impl.BnfReferenceImpl;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author gregsh
 */
public class BnfCompletionContributor extends CompletionContributor {

  public BnfCompletionContributor() {
    extend(CompletionType.BASIC, psiElement().inFile(PlatformPatterns.instanceOf(BnfFileImpl.class)), new CompletionProvider<CompletionParameters>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters,
                                    ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        BnfCompositeElement attrs = PsiTreeUtil.getParentOfType(position, BnfAttrs.class, BnfAttr.class, BnfParenExpression.class);
        boolean attrCompletion = false;
        if ((attrs instanceof BnfAttrs || isPossibleEmptyAttrs(attrs))) {
          boolean inRule = PsiTreeUtil.getParentOfType(attrs, BnfRule.class) != null;
          ASTNode closingBrace = TreeUtil.findSiblingBackward(attrs.getNode().getLastChildNode(), BnfTypes.BNF_RIGHT_BRACE);
          if (closingBrace == null || position.getTextOffset() <= closingBrace.getStartOffset()) {
            attrCompletion = true;
            for (KnownAttribute attribute : KnownAttribute.getAttributes()) {
              if (inRule && attribute.isGlobal()) continue;
              result.addElement(LookupElementBuilder.create(attribute.getName()).setIcon(BnfIcons.ATTRIBUTE));
            }
          }
        }
        if (!attrCompletion && parameters.getInvocationCount() < 2) {
          for (String keywords : suggestKeywords(parameters.getPosition())) {
            result.addElement(TailTypeDecorator.withTail(LookupElementBuilder.create(keywords), TailType.SPACE));
          }
        }
      }
    });
    extend(CompletionType.BASIC, psiElement().inFile(PlatformPatterns.instanceOf(BnfFileImpl.class)), new CompletionProvider<CompletionParameters>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters,
                                    ProcessingContext context,
                                    @NotNull final CompletionResultSet result) {
        final int offset = parameters.getOffset();
        PsiElement position = parameters.getPosition();
        PsiElement parent = PsiTreeUtil.getParentOfType(position, BnfExpression.class, BnfAttr.class, BnfRule.class, GeneratedParserUtilBase.DummyBlock.class);
        if (parent != null && !(parent instanceof BnfStringLiteralExpression && !(parent.getParent() instanceof BnfAttr))) {
          final BnfLexer lexer = new BnfLexer();
          PsiReference referenceAt = parameters.getPosition().getContainingFile().findReferenceAt(parameters.getOffset());
          final Set<String> existing;
          if (referenceAt instanceof BnfReferenceImpl) {
            existing = new THashSet<String>();
            for (Object o : referenceAt.getVariants()) {
              existing.add(((LookupElement)o).getLookupString());
            }
          }
          else existing = null;
          parameters.getOriginalFile().acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(PsiElement element) {
              if (element instanceof BnfReferenceOrToken || element instanceof BnfStringLiteralExpression) {
                PsiReference reference = element.getTextRange().containsOffset(offset) ? null : element.getReference();
                if (reference != null) {
                  String text = StringUtil.unquoteString(element.getText());
                  if (existing != null && existing.contains(text)) return;
                  lexer.start(text);
                  if (lexer.getTokenType() == BnfTypes.BNF_ID && lexer.getTokenEnd() == text.length()) {
                    result.addElement(LookupElementBuilder.create(text));
                  }
                }
              }
              else {
                super.visitElement(element);
              }
            }
          });
        }
      }
    });
  }

  private static boolean isPossibleEmptyAttrs(PsiElement attrs) {
    if (!(attrs instanceof BnfParenExpression)) return false;
    if (attrs.getFirstChild().getNode().getElementType() != BnfTypes.BNF_LEFT_BRACE) return false;
    if (!(((BnfParenExpression) attrs).getExpression() instanceof BnfReferenceOrToken)) return false;
    return isLastInRuleOrFree(attrs);
  }

  private static boolean isLastInRuleOrFree(PsiElement element) {
    PsiElement parent = PsiTreeUtil.getParentOfType(element, BnfRule.class, GeneratedParserUtilBase.DummyBlock.class);
    if (parent instanceof GeneratedParserUtilBase.DummyBlock) return true;
    if (!(parent instanceof BnfRule)) return false;
    for (PsiElement cur = element, next = cur.getNextSibling();
         next == null || next instanceof PsiComment || next instanceof PsiWhiteSpace;
         cur = next, next = cur.getNextSibling()) {
      if (next == null) {
        PsiElement curParent = cur.getParent();
        while (next == null && curParent != parent) {
          next = curParent.getNextSibling();
          curParent = curParent.getParent();
        }
        if (curParent == parent) return true;
        next = PsiTreeUtil.getDeepestFirst(next);
      }
    }
    return false;
  }

  private static Collection<String> suggestKeywords(PsiElement position) {
    TextRange posRange = position.getTextRange();
    BnfFile posFile = (BnfFile)position.getContainingFile();
    BnfRule statement = PsiTreeUtil.getTopmostParentOfType(position, BnfRule.class);
    final TextRange range;
    if (statement != null) {
      range = new TextRange(statement.getTextRange().getStartOffset(), posRange.getStartOffset());
    }
    else {
      int offset = posRange.getStartOffset();
      for (PsiElement cur = GrammarUtil.getDummyAwarePrevSibling(position); cur != null; cur = GrammarUtil.getDummyAwarePrevSibling(cur)) {
        if (cur instanceof BnfAttrs) offset = cur.getTextRange().getEndOffset();
        else if (cur instanceof BnfRule) offset = cur.getTextRange().getStartOffset();
        else continue;
        break;
      }
      range = new TextRange(offset, posRange.getStartOffset());
    }
    String headText = range.substring(posFile.getText());
    int completionOffset = StringUtil.isEmptyOrSpaces(headText)? 0 : headText.length();
    String text = completionOffset == 0 ? CompletionInitializationContext.DUMMY_IDENTIFIER : headText;

    GeneratedParserUtilBase.CompletionState state = new GeneratedParserUtilBase.CompletionState(completionOffset) {
      @Override
      public String convertItem(Object o) {
        // we do not have other keywords
        return o instanceof String? (String)o : null;
      }
    };
    PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(posFile.getProject());
    PsiFile file = psiFileFactory.createFileFromText("a.bnf", BnfLanguage.INSTANCE, text, true, false);
    file.putUserData(GeneratedParserUtilBase.COMPLETION_STATE_KEY, state);
    TreeUtil.ensureParsed(file.getNode());

    if (completionOffset != 0) {
      TextRange altRange = TextRange.create(posRange.getEndOffset(), Math.min(posRange.getEndOffset() + 100, posFile.getTextLength()));
      String tailText = altRange.substring(posFile.getText());
      String text2 = text + (StringUtil.isEmptyOrSpaces(tailText)? "a ::= " : tailText);
      PsiFile file2 = psiFileFactory.createFileFromText("a.bnf", BnfLanguage.INSTANCE, text2, true, false);
      file2.putUserData(GeneratedParserUtilBase.COMPLETION_STATE_KEY, state);
      TreeUtil.ensureParsed(file2.getNode());
    }
    return state.items;
  }
}
