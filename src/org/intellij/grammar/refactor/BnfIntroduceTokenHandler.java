/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.refactor;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.template.*;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateState;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.command.impl.FinishMarkAction;
import com.intellij.openapi.command.impl.StartMarkAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.introduce.inplace.OccurrencesChooser;
import com.intellij.util.ExceptionUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.text.UniqueNameGenerator;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfElementFactory;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author greg
 */
public class BnfIntroduceTokenHandler implements RefactoringActionHandler {
  public static final String REFACTORING_NAME = "Introduce Token";

  @Override
  public void invoke(@NotNull Project project, PsiElement @NotNull [] elements, DataContext dataContext) {
    // do not support this case
  }

  @Override
  public void invoke(@NotNull Project project,
                     Editor editor,
                     PsiFile file,
                     @Nullable DataContext dataContext) {
    if (!(file instanceof BnfFile)) return;
    BnfFile bnfFile = (BnfFile) file;

    Map<String, String> tokenNameMap = RuleGraphHelper.getTokenNameToTextMap(bnfFile);
    Map<String, String> tokenTextMap = RuleGraphHelper.getTokenTextToNameMap(bnfFile);

    String tokenText;
    String tokenName;
    BnfExpression target = PsiTreeUtil.getParentOfType(file.findElementAt(editor.getCaretModel().getOffset()), BnfReferenceOrToken.class, BnfStringLiteralExpression.class);
    if (target instanceof BnfReferenceOrToken) {
      if (bnfFile.getRule(target.getText()) != null) return;
      if (GrammarUtil.isExternalReference(target)) return;
      tokenName = target.getText();
      tokenText = "\"" + StringUtil.notNullize(tokenNameMap.get(tokenName), tokenName) + "\"";
    }
    else if (target instanceof BnfStringLiteralExpression) {
      if (PsiTreeUtil.getParentOfType(target, BnfAttrs.class) != null) return;
      tokenText = target.getText();
      tokenName = tokenTextMap.get(GrammarUtil.unquote(tokenText));
    }
    else return;

    List<BnfExpression> allOccurrences = new ArrayList<>();
    Map<OccurrencesChooser.ReplaceChoice, List<BnfExpression>> occurrencesMap = new LinkedHashMap<>();
    occurrencesMap.put(OccurrencesChooser.ReplaceChoice.NO, Collections.singletonList(target));
    occurrencesMap.put(OccurrencesChooser.ReplaceChoice.ALL, allOccurrences);

    BnfVisitor<Void> visitor = new BnfVisitor<>() {
      @Override
      public Void visitStringLiteralExpression(@NotNull BnfStringLiteralExpression o) {
        if (Objects.equals(tokenText, o.getText())) {
          allOccurrences.add(o);
        }
        return null;
      }

      @Override
      public Void visitReferenceOrToken(@NotNull BnfReferenceOrToken o) {
        if (GrammarUtil.isExternalReference(o)) return null;
        if (tokenName != null && tokenName.equals(o.getText())) {
          allOccurrences.add(o);
        }
        return null;
      }
    };
    for (PsiElement o : GrammarUtil.bnfTraverserNoAttrs(file)) {
      o.accept(visitor);
    }

    if (occurrencesMap.get(OccurrencesChooser.ReplaceChoice.ALL).size() <= 1 && !ApplicationManager.getApplication().isUnitTestMode()) {
      occurrencesMap.remove(OccurrencesChooser.ReplaceChoice.ALL);
    }

    Pass<OccurrencesChooser.ReplaceChoice> callback = new Pass<>() {
      @Override
      public void pass(OccurrencesChooser.ReplaceChoice choice) {
        WriteCommandAction.writeCommandAction(project, file)
          .withName(REFACTORING_NAME)
          .run(() -> {
            try {
              buildTemplateAndRun(project, editor, bnfFile, occurrencesMap.get(choice), tokenName, tokenText, tokenNameMap.keySet());
            }
            catch (StartMarkAction.AlreadyStartedException e) {
              ExceptionUtil.rethrowAllAsUnchecked(e);
            }
          });
      }
    };
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      callback.pass(OccurrencesChooser.ReplaceChoice.ALL);
    }
    else {
      new OccurrencesChooser<BnfExpression>(editor) {
        @Override
        protected TextRange getOccurrenceRange(BnfExpression occurrence) {
          return occurrence.getTextRange();
        }
      }.showChooser(callback, occurrencesMap);
    }
  }

  private static void buildTemplateAndRun(Project project,
                                          Editor editor,
                                          BnfFile bnfFile, List<BnfExpression> occurrences,
                                          String tokenName,
                                          String tokenText,
                                          Set<String> tokenNames) throws StartMarkAction.AlreadyStartedException {
    StartMarkAction startAction = StartMarkAction.start(editor, project, REFACTORING_NAME);
    BnfListEntry entry = addTokenDefinition(project, bnfFile, tokenName, tokenText, tokenNames);
    PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());

    TemplateBuilderImpl builder = new TemplateBuilderImpl(bnfFile);
    PsiElement tokenId = Objects.requireNonNull(entry.getId());
    PsiElement tokenValue = Objects.requireNonNull(entry.getLiteralExpression());
    if (tokenName == null) {
      builder.replaceElement(tokenId, "TokenName", new TextExpression(tokenId.getText()), true);
    }
    builder.replaceElement(tokenValue, "TokenText", new TextExpression(tokenValue.getText()), true);

    for (BnfExpression occurrence : occurrences) {
      builder.replaceElement(occurrence, "Other", new Expression() {

        @Override
        public @Nullable Result calculateResult(ExpressionContext context) {
          TemplateState state = TemplateManagerImpl.getTemplateState(context.getEditor());
          assert state != null;
          TextResult text = Objects.requireNonNull(state.getVariableValue("TokenText"));
          String curText = GrammarUtil.unquote(text.getText());
          if (ParserGeneratorUtil.isRegexpToken(curText)) {
            return state.getVariableValue("TokenName");
          }
          else {
            return new TextResult("'" + curText + "'");
          }
        }

        @Override
        public @Nullable Result calculateQuickResult(ExpressionContext context) {
          return calculateResult(context);
        }

        @Override
        public LookupElement[] calculateLookupItems(ExpressionContext context) {
          return LookupElement.EMPTY_ARRAY;
        }
      }, false);
    }
    RangeMarker caretMarker = editor.getDocument().createRangeMarker(0, editor.getCaretModel().getOffset());
    caretMarker.setGreedyToRight(true);
    editor.getCaretModel().moveToOffset(0);
    Template template = builder.buildInlineTemplate();
    template.setToShortenLongNames(false);
    template.setToReformat(false);
    TemplateManager.getInstance(project).startTemplate(editor, template, new TemplateEditingAdapter() {

      @Override
      public void templateFinished(@NotNull Template template, boolean brokenOff) {
        handleTemplateFinished(project, editor, caretMarker, startAction);
      }

      @Override
      public void templateCancelled(Template template) {
        handleTemplateFinished(project, editor, caretMarker, startAction);
      }
    });
  }

  private static void handleTemplateFinished(Project project,
                                             Editor editor,
                                             RangeMarker caretMarker,
                                             StartMarkAction startAction) {
    try {
      editor.getCaretModel().moveToOffset(caretMarker.getEndOffset());
      editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
    }
    finally {
      FinishMarkAction.finish(project, editor, startAction);
    }
  }

  private static BnfListEntry addTokenDefinition(Project project,
                                                 BnfFile bnfFile,
                                                 String tokenName,
                                                 String tokenText,
                                                 Set<String> tokenNames) {
    String fixedTokenName = new UniqueNameGenerator(tokenNames, null).generateUniqueName(StringUtil.notNullize(tokenName, "token"));
    String newAttrText = "tokens = [\n    " + fixedTokenName + "=" + StringUtil.notNullize(tokenText, "\"\"") + "\n  ]";
    BnfAttr newAttr = BnfElementFactory.createAttributeFromText(project, newAttrText);
    BnfAttrs attrs = ContainerUtil.getFirstItem(bnfFile.getAttributes());
    BnfAttr tokensAttr = null;
    if (attrs == null) {
      attrs = (BnfAttrs) bnfFile.addAfter(newAttr.getParent(), null);
      bnfFile.addAfter(BnfElementFactory.createLeafFromText(project, "\n"), attrs);
      tokensAttr = attrs.getAttrList().get(0);
      BnfValueList attrExpr = (BnfValueList)Objects.requireNonNull(tokensAttr.getExpression());
      return attrExpr.getListEntryList().get(0);
    }
    else {
      for (BnfAttr attr : attrs.getAttrList()) {
        if (KnownAttribute.TOKENS.getName().equals(attr.getName())) {
          tokensAttr = attr;
        }
      }
      if (tokensAttr == null) {
        List<BnfAttr> attrList = attrs.getAttrList();
        PsiElement anchor = attrList.isEmpty() ? attrs.getFirstChild() : attrList.get(attrList.size() - 1);
        newAttr = (BnfAttr) attrs.addAfter(newAttr, anchor);
        attrs.addAfter(BnfElementFactory.createLeafFromText(project, "\n  "), anchor);
        BnfValueList attrExpr = (BnfValueList)Objects.requireNonNull(newAttr.getExpression());
        return attrExpr.getListEntryList().get(0);
      }
      else {
        BnfExpression expression = tokensAttr.getExpression();
        List<BnfListEntry> entryList = expression instanceof BnfValueList ? ((BnfValueList) expression).getListEntryList() : null;
        if (entryList == null || entryList.isEmpty()) {
          tokensAttr = (BnfAttr)tokensAttr.replace(newAttr);
          BnfValueList attrExpr = (BnfValueList)Objects.requireNonNull(tokensAttr.getExpression());
          return attrExpr.getListEntryList().get(0);
        }
        else {
          for (BnfListEntry entry : entryList) {
            PsiElement id = entry.getId();
            if (id != null && id.getText().equals(tokenName)) {
              return entry;
            }
          }
          BnfValueList attrExpr = (BnfValueList)Objects.requireNonNull(newAttr.getExpression());
          BnfListEntry newValue = attrExpr.getListEntryList().get(0);
          PsiElement anchor = entryList.get(entryList.size() - 1);
          newValue = (BnfListEntry) expression.addAfter(newValue, anchor);
          expression.addAfter(BnfElementFactory.createLeafFromText(project, "\n    "), anchor);
          return newValue;
        }
      }
    }
  }

}

