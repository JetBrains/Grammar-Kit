/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.impl.source.tree.TreeUtil;
import gnu.trove.THashSet;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.psi.BnfChoice;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfTypes;
import org.intellij.grammar.psi.BnfVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @author gregsh
 */
public class BnfUnreachableChoiceBranchInspection extends LocalInspectionTool {
  @Override
  public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new BnfVisitor<Void>() {
      @Override
      public Void visitChoice(@NotNull BnfChoice o) {
        checkChoice(o, holder);
        return null;
      }
    };
  }

  private static void checkChoice(BnfChoice choice, ProblemsHolder problemsHolder) {
    Set<BnfExpression> visited = new THashSet<>();
    THashSet<BnfExpression> first = new THashSet<>();
    BnfFirstNextAnalyzer analyzer = BnfFirstNextAnalyzer.createAnalyzer(true);
    List<BnfExpression> list = choice.getExpressionList();
    for (int i = 0, listSize = list.size() - 1; i < listSize; i++) {
      BnfExpression child = list.get(i);
      Set<BnfExpression> firstSet = analyzer.calcFirstInner(child, first, visited);
      if (firstSet.contains(BnfFirstNextAnalyzer.BNF_MATCHES_NOTHING)) {
        registerProblem(choice, child, "Branch is unable to match anything due to & or ! conditions", problemsHolder);
      }
      else if (firstSet.contains(BnfFirstNextAnalyzer.BNF_MATCHES_EOF)) {
        registerProblem(choice, child, "Branch matches empty input making the rest branches unreachable", problemsHolder);
        break;
      }
      first.clear();
      visited.clear();
    }
  }

  static void registerProblem(BnfExpression choice, BnfExpression branch, String message, ProblemsHolder problemsHolder, LocalQuickFix... fixes) {
    TextRange textRange = branch.getTextRange();
    if (textRange.isEmpty()) {
      ASTNode nextOr = TreeUtil.findSibling(branch.getNode(), BnfTypes.BNF_OP_OR);
      ASTNode prevOr = TreeUtil.findSiblingBackward(branch.getNode(), BnfTypes.BNF_OP_OR);

      int shift = choice.getTextRange().getStartOffset();
      int startOffset = prevOr != null ? prevOr.getStartOffset() - shift : 0;
      TextRange range = new TextRange(startOffset, nextOr != null? nextOr.getStartOffset() + 1 - shift : Math.min(startOffset + 2, choice.getTextLength()));
      problemsHolder.registerProblem(choice, range, message, fixes);
    }
    else {
      problemsHolder.registerProblem(branch, message, fixes);
    }
  }
}
