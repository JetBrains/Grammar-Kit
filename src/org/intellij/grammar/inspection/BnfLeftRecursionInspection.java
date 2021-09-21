/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.generator.ExpressionGeneratorHelper;
import org.intellij.grammar.generator.ExpressionHelper;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.BnfVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class BnfLeftRecursionInspection extends LocalInspectionTool {

  @Override
  public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new BnfVisitor<Void>() {
      final BnfFirstNextAnalyzer analyzer = BnfFirstNextAnalyzer.createAnalyzer(false);

      @Override
      public Void visitRule(@NotNull BnfRule o) {
        if (ParserGeneratorUtil.Rule.isFake(o)) return null;
        BnfFile file = (BnfFile)o.getContainingFile();
        ExpressionHelper expressionHelper = ExpressionHelper.getCached(file);
        String ruleName = o.getName();
        boolean exprParsing = ExpressionGeneratorHelper.getInfoForExpressionParsing(expressionHelper, o) != null;

        if (!exprParsing && BnfFirstNextAnalyzer.asStrings(analyzer.calcFirst(o)).contains(ruleName)) {
          holder.registerProblem(o.getId(), "'" + ruleName + "' employs left-recursion unsupported by generator");
        }
        return null;
      }
    };
  }
}
