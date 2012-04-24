/*
 * Copyright 2011-2011 Gregory Shrago
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
package org.intellij.grammar.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class BnfVisitor extends PsiElementVisitor {

  public void visitAttr(@NotNull BnfAttr o) {
    visitNamedElement(o);
  }

  public void visitAttrPattern(@NotNull BnfAttrPattern o) {
    visitCompositeElement(o);
  }

  public void visitAttrs(@NotNull BnfAttrs o) {
    visitCompositeElement(o);
  }

  public void visitChoice(@NotNull BnfChoice o) {
    visitExpression(o);
  }

  public void visitExpression(@NotNull BnfExpression o) {
    visitCompositeElement(o);
  }

  public void visitExternalExpression(@NotNull BnfExternalExpression o) {
    visitExpression(o);
  }

  public void visitListEntry(@NotNull BnfListEntry o) {
    visitCompositeElement(o);
  }

  public void visitLiteralExpression(@NotNull BnfLiteralExpression o) {
    visitExpression(o);
  }

  public void visitModifier(@NotNull BnfModifier o) {
    visitCompositeElement(o);
  }

  public void visitParenExpression(@NotNull BnfParenExpression o) {
    visitParenthesized(o);
  }

  public void visitParenOptExpression(@NotNull BnfParenOptExpression o) {
    visitParenthesized(o);
  }

  public void visitParenthesized(@NotNull BnfParenthesized o) {
    visitExpression(o);
  }

  public void visitPredicate(@NotNull BnfPredicate o) {
    visitExpression(o);
  }

  public void visitPredicateSign(@NotNull BnfPredicateSign o) {
    visitCompositeElement(o);
  }

  public void visitQuantified(@NotNull BnfQuantified o) {
    visitExpression(o);
  }

  public void visitQuantifier(@NotNull BnfQuantifier o) {
    visitCompositeElement(o);
  }

  public void visitReferenceOrToken(@NotNull BnfReferenceOrToken o) {
    visitExpression(o);
  }

  public void visitRule(@NotNull BnfRule o) {
    visitNamedElement(o);
  }

  public void visitSequence(@NotNull BnfSequence o) {
    visitExpression(o);
  }

  public void visitStringLiteralExpression(@NotNull BnfStringLiteralExpression o) {
    visitLiteralExpression(o);
  }

  public void visitValueList(@NotNull BnfValueList o) {
    visitExpression(o);
  }

  public void visitNamedElement(@NotNull BnfNamedElement o) {
    visitCompositeElement(o);
  }

  public void visitCompositeElement(@NotNull BnfCompositeElement o) {
    visitElement(o);
  }

}
