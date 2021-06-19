/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
  @Override
  public @NotNull String getName() {
    return getFamilyName();
  }

  @Override
  public @NotNull String getFamilyName() {
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
    BnfExpressionOptimizer.optimize(project, parent);
  }
}
