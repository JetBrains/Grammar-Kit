/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.intellij.grammar.GrammarKitBundle;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfElementFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;

public class BnfConvertOptExpressionIntention extends BaseIntentionAction {

  @Override
  public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getFamilyName() {
    return GrammarKitBundle.message("intention.convert.opt.expression.family");
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
    if (editor == null || file == null) return false;

    int offset = editor.getCaretModel().getOffset();
    PsiElement element = file.getViewProvider().findElementAt(offset);

    if (getQuantifiedOptExpression(element) != null) {
      setText(GrammarKitBundle.message("intention.convert.opt.expression.text1"));
      return true;
    }
    else if (getParenOptExpression(element) != null) {
      setText(GrammarKitBundle.message("intention.convert.opt.expression.text2"));
      return true;
    }
    else {
      return false;
    }
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
    int offset = editor.getCaretModel().getOffset();
    PsiElement element = file.getViewProvider().findElementAt(offset);

    BnfQuantified quantified = getQuantifiedOptExpression(element);
    if (quantified != null) {
      handleQuantifier(project, quantified);
      return;
    }

    BnfParenOptExpression parenOpt = getParenOptExpression(element);
    if (parenOpt != null) {
      handleParenOpt(project, parenOpt);
    }
  }

  private static @Nullable BnfQuantified getQuantifiedOptExpression(PsiElement element) {
    BnfQuantified quantified = getParentOfType(element, BnfQuantified.class);
    return quantified != null && quantified.getQuantifier().getNode().getFirstChildNode().getElementType() == BnfTypes.BNF_OP_OPT
           ? quantified
           : null;
  }

  @Contract("null -> null")
  private static BnfParenOptExpression getParenOptExpression(PsiElement element) {
    return getParentOfType(element, BnfParenOptExpression.class);
  }

  private static void handleQuantifier(@NotNull Project project, @NotNull BnfQuantified expr) {
    BnfExpression operand = skipParenthesesDown(expr.getExpression());
    String newText = "[" + operand.getText() + "]";
    expr.replace(BnfElementFactory.createExpressionFromText(project, newText));
  }

  private static void handleParenOpt(@NotNull Project project, @NotNull BnfParenOptExpression expr) {
    BnfExpression operand = skipBracketsDown(expr.getExpression());
    String newText = isSimple(operand) ? operand.getText() + "?" : "(" + operand.getText() + ")?";
    expr.replace(BnfElementFactory.createExpressionFromText(project, newText));
  }

  private static BnfExpression skipParenthesesDown(BnfExpression expr) {
    while (expr instanceof BnfParenthesized) {
      expr = ((BnfParenthesized)expr).getExpression();
    }
    return expr;
  }

  private static BnfExpression skipBracketsDown(BnfExpression expr) {
    while (expr instanceof BnfParenOptExpression) {
      expr = ((BnfParenOptExpression)expr).getExpression();
    }
    return expr;
  }

  @Contract(pure = true)
  private static boolean isSimple(@NotNull BnfExpression expression) {
    return expression instanceof BnfReferenceOrToken ||
           expression instanceof BnfLiteralExpression ||
           expression instanceof BnfParenthesized ||
           expression instanceof BnfExternalExpression;
  }
}
