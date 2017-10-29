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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.util.containers.JBIterable;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.BnfExternalExpression;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.BnfVisitor;
import org.intellij.grammar.psi.impl.BnfRefOrTokenImpl;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/25/11
 * Time: 7:06 PM
 *
 * @author Vadim Romansky
 */
public class BnfSuspiciousTokenInspection extends LocalInspectionTool {
  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new BnfVisitor<Void>() {
      @Override
      public Void visitRule(@NotNull BnfRule o) {
        if (ParserGeneratorUtil.Rule.isExternal(o)) return null;
        JBIterable<BnfRefOrTokenImpl> tokens = GrammarUtil.bnfTraverser(o.getExpression())
          .expand(s -> !(s instanceof BnfExternalExpression))
          .filter(BnfRefOrTokenImpl.class);
        for (BnfRefOrTokenImpl token : tokens) {
          PsiReference reference = token.getReference();
          Object resolve = reference == null ? null : reference.resolve();
          final String text = token.getText();
          if (resolve == null && !tokens.contains(text) && isTokenTextSuspicious(text)) {
            holder.registerProblem(token,
                                   "'" + text + "' token looks like a reference to a missing rule",
                                   new CreateRuleFromTokenFix(text));
          }
        }
        return null;
      }
    };
  }

  public static boolean isTokenTextSuspicious(String text) {
    boolean isLowercase = text.equals(text.toLowerCase());
    boolean isUppercase = !isLowercase && text.equals(text.toUpperCase());
    return !isLowercase && !isUppercase || isLowercase && StringUtil.containsAnyChar(text, "-_");
  }
}
