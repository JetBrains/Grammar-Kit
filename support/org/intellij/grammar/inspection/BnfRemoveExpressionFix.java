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

package org.intellij.grammar.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.TreeUtil;
import org.intellij.grammar.psi.BnfChoice;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfTypes;
import org.intellij.grammar.refactor.BnfExpressionOptimizer;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class BnfRemoveExpressionFix implements LocalQuickFix {
  @NotNull
  @Override
  public String getName() {
    return getFamilyName();
  }

  @NotNull
  @Override
  public String getFamilyName() {
    return "Remove expression";
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    PsiElement element = descriptor.getPsiElement();
    if (!element.isValid()) return;
    PsiElement parent = element.getParent();
    if (element instanceof BnfExpression && parent instanceof BnfChoice) {
      ASTNode node = element.getNode();
      ASTNode nextOr = TreeUtil.findSibling(node, BnfTypes.BNF_OP_OR);
      ASTNode prevOr = TreeUtil.findSiblingBackward(node, BnfTypes.BNF_OP_OR);
      assert nextOr != null || prevOr != null: "'|' missing in choice";
      if (nextOr != null && prevOr != null) {
        parent.deleteChildRange(prevOr.getTreeNext().getPsi(), nextOr.getPsi());
      }
      else {
        parent.deleteChildRange(prevOr == null? element : prevOr.getPsi(), prevOr == null? nextOr.getPsi() : element);

      }
    }
    else {
      element.delete();
    }
    BnfExpressionOptimizer.optimize(parent);
  }
}
