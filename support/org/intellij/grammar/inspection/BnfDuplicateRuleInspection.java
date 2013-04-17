/*
 * Copyright 2011-2013 Gregory Shrago
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
import com.intellij.util.Processor;
import gnu.trove.THashSet;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/29/11
 * Time: 1:54 PM
 *
 * @author Vadim Romansky
 */
public class BnfDuplicateRuleInspection extends LocalInspectionTool {

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
    return "Duplicate rule";
  }

  @NotNull
  @Override
  public String getShortName() {
    return "BnfDuplicateRuleInspection";
  }

  public boolean isEnabledByDefault() {
    return true;
  }

  public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
    ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);
    checkFile(file, problemsHolder);
    return problemsHolder.getResultsArray();
  }
  
  private static void checkFile(final PsiFile file, final ProblemsHolder problemsHolder) {
    if (!(file instanceof BnfFile)) return;
    final BnfFile bnfFile = (BnfFile)file;

    final Set<BnfRule> rules = new THashSet<BnfRule>();
    GrammarUtil.processChildrenDummyAware(file, new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        String name = psiElement instanceof BnfRule ? ((BnfRule)psiElement).getName() : null;
        BnfRule rule = name == null? null : bnfFile.getRule(name);
        if (name != null && rule != psiElement) {
          rules.add(rule);
          rules.add((BnfRule)psiElement);
        }
        return true;
      }
    });
    for (BnfRule rule : rules) {
      problemsHolder.registerProblem(rule.getId(), "'" + rule.getName() + "' rule is defined more than once");
    }
  }
}
