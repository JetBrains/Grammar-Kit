/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfReferenceImpl;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static org.intellij.grammar.psi.BnfTypes.BNF_ID;

/**
 * @author gregsh
 */
public class BnfCompletionContributor extends CompletionContributor {

  public BnfCompletionContributor() {
    PsiElementPattern.Capture<PsiElement> placePattern =
      psiElement()
        .inFile(StandardPatterns.instanceOf(BnfFile.class))
        .andNot(psiElement().inside(PsiComment.class));
    extend(CompletionType.BASIC, placePattern, new CompletionProvider<>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters,
                                    @NotNull ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        BnfComposite parent = PsiTreeUtil.getParentOfType(position, BnfAttrs.class, BnfAttr.class, BnfParenExpression.class);
        boolean attrCompletion;
        if (parent instanceof BnfAttrs || isPossibleEmptyAttrs(parent)) {
          attrCompletion = true;
        }
        else if (parent instanceof BnfAttr) {
          BnfAttr attr = (BnfAttr)parent;
          attrCompletion = position == attr.getId() || isOneAfterAnother(attr.getExpression(), position);
        }
        else {
          attrCompletion = false;
        }
        if (attrCompletion) {
          boolean inRule = PsiTreeUtil.getParentOfType(parent, BnfRule.class) != null;
          ASTNode closingBrace = TreeUtil.findSiblingBackward(parent.getNode().getLastChildNode(), BnfTypes.BNF_RIGHT_BRACE);
          attrCompletion = closingBrace == null || position.getTextOffset() <= closingBrace.getStartOffset();
          if (attrCompletion) {
            for (KnownAttribute<?> attribute : KnownAttribute.getAttributes()) {
              if (inRule && attribute.isGlobal()) continue;
              result.addElement(LookupElementBuilder.create(attribute.getName()).withIcon(BnfIcons.ATTRIBUTE));
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
    extend(CompletionType.BASIC, placePattern.andNot(psiElement().inside(false, psiElement(BnfAttr.class))), new CompletionProvider<>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters,
                                    @NotNull ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
        BnfFile file = (BnfFile)parameters.getOriginalFile();
        PsiElement positionRefOrToken = PsiTreeUtil.getParentOfType(parameters.getOriginalPosition(), BnfReferenceOrToken.class);
        Set<String> explicitTokens = RuleGraphHelper.getTokenNameToTextMap(file).keySet();
        for (String s : explicitTokens) {
          result.addElement(LookupElementBuilder.create(s));
        }
        for (BnfRule rule : file.getRules()) {
          for (BnfReferenceOrToken element : SyntaxTraverser.psiTraverser(rule.getExpression()).filter(BnfReferenceOrToken.class)) {
            if (element == positionRefOrToken) continue;
            if (element.resolveRule() == null) {
              result.addElement(LookupElementBuilder.create(element.getText()));
            }
          }
        }
      }
    });
    extend(CompletionType.BASIC, placePattern.withParent(
      psiElement(BnfExpression.class).withReference(BnfReferenceImpl.class)), new CompletionProvider<>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters,
                                    @NotNull ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
        BnfFile bnfFile = (BnfFile)parameters.getOriginalFile();
        PsiElement posExpression = PsiTreeUtil.getParentOfType(parameters.getPosition(), BnfExpression.class);
        boolean isExternal = GrammarUtil.isExternalReference(posExpression);
        for (BnfRule rule : bnfFile.getRules()) {
          boolean fakeRule = ParserGeneratorUtil.Rule.isFake(rule);
          boolean privateRule = ParserGeneratorUtil.Rule.isPrivate(rule);
          if (isExternal && !ParserGeneratorUtil.Rule.isMeta(rule)) continue;
          String idText = rule.getId().getText();
          LookupElementBuilder e = LookupElementBuilder.create(rule, idText)
            .withIcon(rule.getIcon(0))
            .withBoldness(!privateRule)
            .withStrikeoutness(fakeRule);
          if (!Objects.equals(idText, rule.getName())) {
            e = e.withLookupString(rule.getName());
          }
          result.addElement(e);
        }
        if (!isExternal) return;
        BnfRule posRule = PsiTreeUtil.getParentOfType(posExpression, BnfRule.class);
        BnfRule rule = posRule == null ? null : CompletionUtil.getOriginalElement(posRule);
        if (rule == null) return;
        String parserClass = ParserGeneratorUtil.getAttribute(rule, KnownAttribute.PARSER_UTIL_CLASS);
        if (StringUtil.isNotEmpty(parserClass)) {
          JavaHelper helper = JavaHelper.getJavaHelper(rule);
          for (String className = parserClass; className != null; className = helper.getSuperClassName(className)) {
            for (NavigatablePsiElement element : helper
              .findClassMethods(className, JavaHelper.MethodType.STATIC, "*", -1, BnfConstants.PSI_BUILDER_CLASS, "int")) {
              List<String> methodTypes = helper.getMethodTypes(element);
              if ("boolean".equals(ContainerUtil.getFirstItem(methodTypes))) {
                result.addElement(LookupElementBuilder.createWithIcon((PsiNamedElement)element));
              }
            }
          }
        }
      }
    });
  }

  @Override
  public void beforeCompletion(@NotNull CompletionInitializationContext context) {
    BnfFile file = ObjectUtils.tryCast(context.getFile(), BnfFile.class);
    if (file == null) return;
    int offset = context.getStartOffset();
    PsiElement element = file.findElementAt(offset);
    if (PsiUtilCore.getElementType(element) == BNF_ID) {
      context.setDummyIdentifier("");
    }
  }

  @Contract("null -> false")
  private static boolean isPossibleEmptyAttrs(PsiElement attrs) {
    if (!(attrs instanceof BnfParenExpression)) return false;
    if (attrs.getFirstChild().getNode().getElementType() != BnfTypes.BNF_LEFT_BRACE) return false;
    if (!(((BnfParenExpression) attrs).getExpression() instanceof BnfReferenceOrToken)) return false;
    return isLastInRuleOrFree(attrs);
  }

  private static boolean isOneAfterAnother(@Nullable PsiElement e1, @Nullable PsiElement e2) {
    if (e1 == null || e2 == null) return false;
    return e1.getTextRange().getEndOffset() < e2.getTextRange().getStartOffset();
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
    TextRange range;
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
