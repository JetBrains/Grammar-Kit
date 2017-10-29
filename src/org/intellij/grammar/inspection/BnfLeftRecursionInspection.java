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

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new BnfVisitor<Void>() {
      @Override
      public Void visitRule(@NotNull BnfRule o) {
        if (ParserGeneratorUtil.Rule.isFake(o)) return null;
        BnfFile file = (BnfFile)o.getContainingFile();
        ExpressionHelper expressionHelper = ExpressionHelper.getCached(file);
        BnfFirstNextAnalyzer analyzer = new BnfFirstNextAnalyzer();
        String ruleName = o.getName();
        boolean exprParsing = ExpressionGeneratorHelper.getInfoForExpressionParsing(expressionHelper, o) != null;

        if (!exprParsing && analyzer.asStrings(analyzer.calcFirst(o)).contains(ruleName)) {
          holder.registerProblem(o.getId(), "'" + ruleName + "' employs left-recursion unsupported by generator");
        }
        return null;
      }
    };
  }
}
