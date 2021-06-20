/*
 * Copyright 2011-present JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.jflex.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class JFlexVisitor<R> extends PsiElementVisitor {

  public R visitCharRange(@NotNull JFlexCharRange o) {
    return visitClassExpression(o);
  }

  public R visitChoiceExpression(@NotNull JFlexChoiceExpression o) {
    return visitExpression(o);
  }

  public R visitClassExpression(@NotNull JFlexClassExpression o) {
    return visitExpression(o);
  }

  public R visitDeclarationsSection(@NotNull JFlexDeclarationsSection o) {
    return visitFileSection(o);
  }

  public R visitExpression(@NotNull JFlexExpression o) {
    return visitComposite(o);
  }

  public R visitFileSection(@NotNull JFlexFileSection o) {
    return visitComposite(o);
  }

  public R visitJavaCode(@NotNull JFlexJavaCode o) {
    return visitComposite(o);
  }

  public R visitJavaType(@NotNull JFlexJavaType o) {
    return visitComposite(o);
  }

  public R visitLexicalRulesSection(@NotNull JFlexLexicalRulesSection o) {
    return visitFileSection(o);
  }

  public R visitLiteralExpression(@NotNull JFlexLiteralExpression o) {
    return visitExpression(o);
  }

  public R visitLookAhead(@NotNull JFlexLookAhead o) {
    return visitComposite(o);
  }

  public R visitMacroDefinition(@NotNull JFlexMacroDefinition o) {
    return visitNamedElement(o);
  }

  public R visitMacroRefExpression(@NotNull JFlexMacroRefExpression o) {
    return visitExpression(o);
  }

  public R visitMacroReference(@NotNull JFlexMacroReference o) {
    return visitComposite(o);
  }

  public R visitNotExpression(@NotNull JFlexNotExpression o) {
    return visitExpression(o);
  }

  public R visitOption(@NotNull JFlexOption o) {
    return visitComposite(o);
  }

  public R visitParenExpression(@NotNull JFlexParenExpression o) {
    return visitExpression(o);
  }

  public R visitPredefinedClassExpression(@NotNull JFlexPredefinedClassExpression o) {
    return visitExpression(o);
  }

  public R visitQuantifierExpression(@NotNull JFlexQuantifierExpression o) {
    return visitExpression(o);
  }

  public R visitRule(@NotNull JFlexRule o) {
    return visitComposite(o);
  }

  public R visitSequenceExpression(@NotNull JFlexSequenceExpression o) {
    return visitExpression(o);
  }

  public R visitStateDeclaration(@NotNull JFlexStateDeclaration o) {
    return visitComposite(o);
  }

  public R visitStateDefinition(@NotNull JFlexStateDefinition o) {
    return visitNamedElement(o);
  }

  public R visitStateList(@NotNull JFlexStateList o) {
    return visitComposite(o);
  }

  public R visitStateReference(@NotNull JFlexStateReference o) {
    return visitComposite(o);
  }

  public R visitUserCodeSection(@NotNull JFlexUserCodeSection o) {
    return visitFileSection(o);
  }

  public R visitUserValue(@NotNull JFlexUserValue o) {
    return visitComposite(o);
  }

  public R visitNamedElement(@NotNull JFlexNamedElement o) {
    return visitComposite(o);
  }

  public R visitComposite(@NotNull JFlexComposite o) {
    visitElement(o);
    return null;
  }

}
