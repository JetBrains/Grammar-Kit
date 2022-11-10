/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiFile;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/29/11
 * Time: 1:54 PM
 *
 * @author Vadim Romansky
 */
public class BnfDuplicateRuleInspection extends LocalInspectionTool {

  @Override
  public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
    ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);
    checkFile(file, problemsHolder);
    return problemsHolder.getResultsArray();
  }
  
  private static void checkFile(PsiFile file, ProblemsHolder problemsHolder) {
    if (!(file instanceof BnfFile)) return;
    BnfFile bnfFile = (BnfFile)file;

    Set<BnfRule> rules = new LinkedHashSet<>();
    for (BnfRule r : GrammarUtil.bnfTraverser(bnfFile).filter(BnfRule.class)) {
      BnfRule t = bnfFile.getRule(r.getName());
      if (r != t) {
        rules.add(t);
        rules.add(r);
      }
    }
    for (BnfRule rule : rules) {
      problemsHolder.registerProblem(rule.getId(), "'" + rule.getName() + "' rule is defined more than once");
    }
  }
}
