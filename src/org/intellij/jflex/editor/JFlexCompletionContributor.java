/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.editor;

import com.intellij.application.options.CodeStyle;
import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ProcessingContext;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.jflex.psi.*;
import org.intellij.jflex.psi.impl.JFlexFileImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static org.intellij.jflex.psi.JFlexTypes.*;

/**
 * @author gregsh
 */
public class JFlexCompletionContributor extends CompletionContributor {

  public JFlexCompletionContributor() {
    extend(CompletionType.BASIC, psiElement().inFile(StandardPatterns.instanceOf(JFlexFileImpl.class)), new CompletionProvider<CompletionParameters>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters,
                                    @NotNull ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        JFlexComposite parent = PsiTreeUtil.getParentOfType(
          position, JFlexDeclarationsSection.class, JFlexRule.class, JFlexJavaCode.class, JFlexJavaType.class);
        boolean inJava = parent instanceof JFlexJavaCode || parent instanceof JFlexJavaType;

        if (!inJava && parameters.getInvocationCount() < 2) {
          int start = position.getTextRange().getStartOffset();
          CompletionResultSet result2 =
            start > 0 && parameters.getEditor().getDocument().getText().charAt(start - 1) == '%' ?
            result.withPrefixMatcher(result.getPrefixMatcher().cloneWithPrefix("%" + result.getPrefixMatcher().getPrefix())) :
            result;
          for (String keyword : suggestKeywords(parameters.getPosition())) {
            result2.addElement(createKeywordLookupItem(parameters.getOriginalFile(), keyword));
          }
        }
      }
    });
  }

  @Override
  public void beforeCompletion(@NotNull CompletionInitializationContext context) {
    context.setDummyIdentifier(CompletionUtil.DUMMY_IDENTIFIER_TRIMMED);
  }

  private static Collection<String> suggestKeywords(PsiElement position) {
    if (position instanceof LeafPsiElement && ((LeafPsiElement)position).getElementType() == FLEX_STRING) return Collections.emptySet();
    if (PsiTreeUtil.getParentOfType(position, JFlexUserCodeSection.class) != null) return Collections.emptySet();
    boolean inDeclare = PsiTreeUtil.getParentOfType(position, JFlexDeclarationsSection.class) != null;
    PsiFile flexFile = position.getContainingFile();
    Language language = flexFile.getLanguage();
    String flexFileText = flexFile.getText();
    int positionOffset = position.getTextRange().getEndOffset();

    JFlexExpression expr = PsiTreeUtil.getParentOfType(position, JFlexExpression.class);
    final boolean inMacro =
      expr != null && expr.getText().substring(0, positionOffset - expr.getTextRange().getStartOffset()).indexOf('\n') == -1;

    String fragment = (inDeclare ? "%%\n" : "%%\n%%\n") + flexFileText.substring(flexFileText.lastIndexOf("%%", positionOffset-1) + 2, positionOffset);
    boolean empty = StringUtil.isEmptyOrSpaces(fragment);
    final String text = empty ? CompletionInitializationContext.DUMMY_IDENTIFIER : fragment;
    int completionOffset = empty ? 0 : fragment.length();
    PsiFile file = PsiFileFactory.getInstance(flexFile.getProject()).createFileFromText("a.flex", language, text, true, false);
    GeneratedParserUtilBase.CompletionState state = new GeneratedParserUtilBase.CompletionState(completionOffset) {
      @Nullable
      @Override
      public String convertItem(Object o) {
        if (o == null) return null;
        if (o instanceof IElementType[]) return super.convertItem(o);
        if (o == FLEX_ID || o == FLEX_CHAR || o == FLEX_STRING ||
            o == FLEX_NUMBER || o == FLEX_RAW || o == FLEX_VERSION) return null;
        String text = o.toString();
        return text.length() == 1 || inMacro && text.startsWith("%") || !inMacro && text.startsWith("[") ?
               null : text;
      }
    };
    file.putUserData(GeneratedParserUtilBase.COMPLETION_STATE_KEY, state);
    TreeUtil.ensureParsed(file.getNode());
    return state.items;
  }

  private static LookupElement createKeywordLookupItem(PsiFile psiFile, String keyword) {
    LookupElementBuilder builder = LookupElementBuilder.create(keyword.toLowerCase()).withCaseSensitivity(false).bold();
    boolean braces = keyword.endsWith("{") || keyword.endsWith("}");
    if (!braces) {
      return keyword.startsWith("%") ? TailTypeDecorator.withTail(builder, TailType.SPACE) : builder;
    }
    else {
      final String closing = keyword.endsWith("{") ? keyword.substring(0, keyword.length()-1) + "}" : null;
      return PrioritizedLookupElement.withPriority(builder.withInsertHandler((context, item) -> {
        int caret = context.getTailOffset();
        Document document = context.getDocument();
        StringBuilder sb = new StringBuilder("\n");
        caret += sb.length();
        if (closing != null) {
          int indentSize = ObjectUtils.notNull(CodeStyle.getLanguageSettings(psiFile).getIndentOptions(),
                                               CommonCodeStyleSettings.IndentOptions.DEFAULT_INDENT_OPTIONS).INDENT_SIZE;
          sb.append(StringUtil.repeat(" ", indentSize));
          caret += indentSize;
          sb.append("\n").append(closing).append("\n");
        }
        document.insertString(context.getTailOffset(), sb);
        context.getEditor().getCaretModel().moveToOffset(caret);
      }), 1.d / keyword.length());
    }
  }
}
