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
import com.intellij.util.containers.MultiMap;
import org.intellij.grammar.psi.BnfAttrs;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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
    return "Grammar Inspections";
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
  
  private void checkFile(final PsiFile file, final ProblemsHolder problemsHolder) {
    final MultiMap<String, BnfRule> map = new MultiMap<String, BnfRule>();
    
    file.accept(new PsiRecursiveElementWalkingVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        if(element instanceof BnfRule){
          BnfRule rule = (BnfRule)element;
          final String name = rule.getName();
          
          map.putValue(name, rule);
        }
        else if (element instanceof BnfAttrs) {
          return;
        }
        super.visitElement(element);
      }
    });
    
    for (String name : map.keySet()) {
      final Collection<BnfRule> rules = map.get(name);
      if (rules.size() > 1) {
        for (BnfRule rule : rules) {
          problemsHolder.registerProblem(rule.getId(), "'" +name+ "' rule is defined more than once");
        }
      }
    }
  }
}
