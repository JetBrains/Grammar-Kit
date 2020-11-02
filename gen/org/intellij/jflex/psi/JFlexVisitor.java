/*
 * Copyright 2011-present JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.jflex.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class JFlexVisitor extends PsiElementVisitor {

  public void visitCharRange(@NotNull JFlexCharRange o) {
    visitClassExpression(o);
  }

  public void visitChoiceExpression(@NotNull JFlexChoiceExpression o) {
    visitExpression(o);
  }

  public void visitClassExpression(@NotNull JFlexClassExpression o) {
    visitExpression(o);
  }

  public void visitDeclarationsSection(@NotNull JFlexDeclarationsSection o) {
    visitFileSection(o);
  }

  public void visitExpression(@NotNull JFlexExpression o) {
    visitComposite(o);
  }

  public void visitFileSection(@NotNull JFlexFileSection o) {
    visitComposite(o);
  }

  public void visitJavaCode(@NotNull JFlexJavaCode o) {
    visitComposite(o);
  }

  public void visitJavaType(@NotNull JFlexJavaType o) {
    visitComposite(o);
  }

  public void visitLexicalRulesSection(@NotNull JFlexLexicalRulesSection o) {
    visitFileSection(o);
  }

  public void visitLiteralExpression(@NotNull JFlexLiteralExpression o) {
    visitExpression(o);
  }

  public void visitLookAhead(@NotNull JFlexLookAhead o) {
    visitComposite(o);
  }

  public void visitMacroDefinition(@NotNull JFlexMacroDefinition o) {
    visitNamedElement(o);
  }

  public void visitMacroRefExpression(@NotNull JFlexMacroRefExpression o) {
    visitExpression(o);
  }

  public void visitMacroReference(@NotNull JFlexMacroReference o) {
    visitComposite(o);
  }

  public void visitNotExpression(@NotNull JFlexNotExpression o) {
    visitExpression(o);
  }

  public void visitOption(@NotNull JFlexOption o) {
    visitComposite(o);
  }

  public void visitParenExpression(@NotNull JFlexParenExpression o) {
    visitExpression(o);
  }

  public void visitPredefinedClassExpression(@NotNull JFlexPredefinedClassExpression o) {
    visitExpression(o);
  }

  public void visitQuantifierExpression(@NotNull JFlexQuantifierExpression o) {
    visitExpression(o);
  }

  public void visitRule(@NotNull JFlexRule o) {
    visitComposite(o);
  }

  public void visitSequenceExpression(@NotNull JFlexSequenceExpression o) {
    visitExpression(o);
  }

  public void visitStateDeclaration(@NotNull JFlexStateDeclaration o) {
    visitComposite(o);
  }

  public void visitStateDefinition(@NotNull JFlexStateDefinition o) {
    visitNamedElement(o);
  }

  public void visitStateList(@NotNull JFlexStateList o) {
    visitComposite(o);
  }

  public void visitStateReference(@NotNull JFlexStateReference o) {
    visitComposite(o);
  }

  public void visitUserCodeSection(@NotNull JFlexUserCodeSection o) {
    visitFileSection(o);
  }

  public void visitUserValue(@NotNull JFlexUserValue o) {
    visitComposite(o);
  }

  public void visitNamedElement(@NotNull JFlexNamedElement o) {
    visitComposite(o);
  }

  public void visitComposite(@NotNull JFlexComposite o) {
    visitElement(o);
  }

}
