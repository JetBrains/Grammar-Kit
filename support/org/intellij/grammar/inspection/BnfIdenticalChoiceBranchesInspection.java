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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.intellij.grammar.psi.BnfChoice;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 9/2/11
 * Time: 7:20 PM
 *
 * @author Vadim Romansky
 */
public class BnfIdenticalChoiceBranchesInspection extends LocalInspectionTool {
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
    return "Identical choice branches";
  }

  @NotNull
  @Override
  public String getShortName() {
    return "BnfIdenticalChoiceBranchesInspection";
  }

  @Override
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
        if (element instanceof BnfRule) {
          checkExpression(((BnfRule)element).getExpression(), problemsHolder);
        }
        else {
          super.visitElement(element);
        }
      }
    });
  }

  private static void checkExpression(BnfExpression expression, final ProblemsHolder problemsHolder) {
    final HashSet<PsiElement> set = new HashSet<PsiElement>();
    expression.accept(new PsiRecursiveElementWalkingVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        if (element instanceof BnfChoice) {
          checkChoice((BnfChoice)element, set);
        }
        super.visitElement(element);
      }
    });
    for (PsiElement element : set) {
      problemsHolder.registerProblem(element, "\"" + element.getText() + "\"" + " appears in choice more than once", new BnfRemoveExpressionFix());
    }
  }

  private static void checkChoice(BnfChoice choice, Set<PsiElement> set) {
    List<BnfExpression> list = choice.getExpressionList();
    for (BnfExpression e1 : list) {
      for (BnfExpression e2 : list) {
        if (e1 != e2 && GrammarUtil.equalsElement(e1, e2)) {
          set.add(e1);
          set.add(e2);
        }
      }
    }
  }
}
