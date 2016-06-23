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

import com.intellij.codeInspection.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.impl.source.tree.TreeUtil;
import gnu.trove.THashSet;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.psi.BnfChoice;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfTypes;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @author gregsh
 */
public class BnfUnreachableChoiceBranchInspection extends LocalInspectionTool {

  @Nls
  @NotNull
  @Override
  public String getGroupDisplayName() {
    return "Grammar/BNF";
  }

  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return "Unreachable choice branch";
  }

  @NotNull
  @Override
  public String getShortName() {
    return "BnfUnreachableChoiceBranchInspection";
  }

  public boolean isEnabledByDefault() {
    return true;
  }

  @Override
  public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
    ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);
    checkFile(file, problemsHolder);
    return problemsHolder.getResultsArray();
  }

  private static void checkFile(PsiFile file, final ProblemsHolder problemsHolder) {
    file.accept(new PsiRecursiveElementWalkingVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        if (element instanceof BnfChoice) {
          checkChoice((BnfChoice)element, problemsHolder);
        }
        super.visitElement(element);
      }
    });
  }

  private static void checkChoice(BnfChoice choice, ProblemsHolder problemsHolder) {
    Set<BnfExpression> visited = new THashSet<BnfExpression>();
    THashSet<BnfExpression> first = new THashSet<BnfExpression>();
    BnfFirstNextAnalyzer analyzer = new BnfFirstNextAnalyzer().setPredicateLookAhead(true);
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
