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

package org.intellij.grammar.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import gnu.trove.THashSet;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.psi.BnfChoice;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

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
        else {
          super.visitElement(element);
        }
      }
    });
  }

  private static void checkChoice(BnfChoice choice, ProblemsHolder problemsHolder) {
    LinkedList<BnfRule> visited = new LinkedList<BnfRule>();
    THashSet<String> first = new THashSet<String>();
    List<BnfExpression> list = choice.getExpressionList();
    for (int i = 0, listSize = list.size() - 1; i < listSize; i++) {
      BnfExpression child = list.get(i);
      if (BnfFirstNextAnalyzer.calcFirstInner(child, first, visited).contains(BnfFirstNextAnalyzer.EMPTY_STRING)) {
        TextRange textRange = choice.getTextRange();
        TextRange nextRange = list.get(i + 1).getTextRange();
        TextRange problemRange = new TextRange(nextRange.getStartOffset() - textRange.getStartOffset(), textRange.getLength());
        problemsHolder.registerProblem(choice, problemRange, "Unreachable choice branches");
      }
      first.clear();
      visited.clear();
    }
  }
  
}
