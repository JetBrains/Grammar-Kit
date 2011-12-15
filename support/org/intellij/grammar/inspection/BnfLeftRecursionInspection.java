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

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.util.containers.MultiMap;
import gnu.trove.THashSet;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.psi.BnfAttrs;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * @author gregsh
 */
public class BnfLeftRecursionInspection extends LocalInspectionTool {

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
    return "Left recursion";
  }

  @NotNull
  @Override
  public String getShortName() {
    return "BnfLeftRecursionInspection";
  }

  public boolean isEnabledByDefault() {
    return true;
  }

  public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
    if (file instanceof BnfFile) {
      ArrayList<ProblemDescriptor> list = new ArrayList<ProblemDescriptor>();
      for (BnfRule rule : ((BnfFile)file).getRules()) {
        String ruleName = rule.getName();
        Set<String> strings = BnfFirstNextAnalyzer.calcFirst(rule);
        if (strings.contains(ruleName)) {
          list.add(manager.createProblemDescriptor(rule.getId(), "'" + ruleName + "' employs left-recursion unsupported by generator", isOnTheFly, LocalQuickFix.EMPTY_ARRAY, ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
        }
      }
      if (!list.isEmpty()) return list.toArray(new ProblemDescriptor[list.size()]);
    }
    return ProblemDescriptor.EMPTY_ARRAY;
  }
  
}
