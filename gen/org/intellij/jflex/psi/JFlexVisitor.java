/*
 * Copyright 2011-present Greg Shrago
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
package org.intellij.jflex.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNameIdentifierOwner;

public class JFlexVisitor extends PsiElementVisitor {

  public void visitChoiceExpression(@NotNull JFlexChoiceExpression o) {
    visitExpression(o);
  }

  public void visitClassExpression(@NotNull JFlexClassExpression o) {
    visitExpression(o);
  }

  public void visitDeclarationsSection(@NotNull JFlexDeclarationsSection o) {
    visitCompositeElement(o);
  }

  public void visitExpression(@NotNull JFlexExpression o) {
    visitCompositeElement(o);
  }

  public void visitJavaCode(@NotNull JFlexJavaCode o) {
    visitCompositeElement(o);
  }

  public void visitJavaType(@NotNull JFlexJavaType o) {
    visitCompositeElement(o);
  }

  public void visitLexicalRulesSection(@NotNull JFlexLexicalRulesSection o) {
    visitCompositeElement(o);
  }

  public void visitLiteralExpression(@NotNull JFlexLiteralExpression o) {
    visitExpression(o);
  }

  public void visitLookAhead(@NotNull JFlexLookAhead o) {
    visitCompositeElement(o);
  }

  public void visitMacroDefinition(@NotNull JFlexMacroDefinition o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitMacroRefExpression(@NotNull JFlexMacroRefExpression o) {
    visitExpression(o);
  }

  public void visitMacroReference(@NotNull JFlexMacroReference o) {
    visitCompositeElement(o);
  }

  public void visitNotExpression(@NotNull JFlexNotExpression o) {
    visitExpression(o);
  }

  public void visitOption(@NotNull JFlexOption o) {
    visitCompositeElement(o);
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
    visitCompositeElement(o);
  }

  public void visitSequenceExpression(@NotNull JFlexSequenceExpression o) {
    visitExpression(o);
  }

  public void visitStateDeclaration(@NotNull JFlexStateDeclaration o) {
    visitCompositeElement(o);
  }

  public void visitStateDefinition(@NotNull JFlexStateDefinition o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitStateReference(@NotNull JFlexStateReference o) {
    visitCompositeElement(o);
  }

  public void visitUserCodeSection(@NotNull JFlexUserCodeSection o) {
    visitCompositeElement(o);
  }

  public void visitUserValue(@NotNull JFlexUserValue o) {
    visitCompositeElement(o);
  }

  public void visitPsiNameIdentifierOwner(@NotNull PsiNameIdentifierOwner o) {
    visitElement(o);
  }

  public void visitCompositeElement(@NotNull JFlexCompositeElement o) {
    visitElement(o);
  }

}
