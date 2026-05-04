/*
 * Copyright 2011-present JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class BnfVisitor<R> extends PsiElementVisitor {

  public R visitAttr(@NotNull BnfAttr o) {
    return visitNamedElement(o);
  }

  public R visitAttrPattern(@NotNull BnfAttrPattern o) {
    return visitComposite(o);
  }

  public R visitAttrs(@NotNull BnfAttrs o) {
    return visitComposite(o);
  }

  public R visitChoice(@NotNull BnfChoice o) {
    return visitExpression(o);
  }

  public R visitExpression(@NotNull BnfExpression o) {
    return visitComposite(o);
  }

  public R visitExternalExpression(@NotNull BnfExternalExpression o) {
    return visitExpression(o);
  }

  public R visitListEntry(@NotNull BnfListEntry o) {
    return visitComposite(o);
  }

  public R visitLiteralExpression(@NotNull BnfLiteralExpression o) {
    return visitExpression(o);
  }

  public R visitModifier(@NotNull BnfModifier o) {
    return visitComposite(o);
  }

  public R visitParenExpression(@NotNull BnfParenExpression o) {
    return visitParenthesized(o);
  }

  public R visitParenOptExpression(@NotNull BnfParenOptExpression o) {
    return visitParenthesized(o);
  }

  public R visitParenthesized(@NotNull BnfParenthesized o) {
    return visitExpression(o);
  }

  public R visitPredicate(@NotNull BnfPredicate o) {
    return visitExpression(o);
  }

  public R visitPredicateSign(@NotNull BnfPredicateSign o) {
    return visitComposite(o);
  }

  public R visitQuantified(@NotNull BnfQuantified o) {
    return visitExpression(o);
  }

  public R visitQuantifier(@NotNull BnfQuantifier o) {
    return visitComposite(o);
  }

  public R visitReferenceOrToken(@NotNull BnfReferenceOrToken o) {
    return visitExpression(o);
  }

  public R visitRule(@NotNull BnfRule o) {
    return visitNamedElement(o);
  }

  public R visitSequence(@NotNull BnfSequence o) {
    return visitExpression(o);
  }

  public R visitStringLiteralExpression(@NotNull BnfStringLiteralExpression o) {
    return visitLiteralExpression(o);
  }

  public R visitValueList(@NotNull BnfValueList o) {
    return visitExpression(o);
  }

  public R visitNamedElement(@NotNull BnfNamedElement o) {
    return visitComposite(o);
  }

  public R visitComposite(@NotNull BnfComposite o) {
    visitElement(o);
    return null;
  }

}
