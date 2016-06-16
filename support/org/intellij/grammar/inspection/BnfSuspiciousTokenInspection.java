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

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiReference;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.BnfExternalExpression;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.impl.BnfRefOrTokenImpl;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/25/11
 * Time: 7:06 PM
 *
 * @author Vadim Romansky
 */
public class BnfSuspiciousTokenInspection extends LocalInspectionTool {

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
    return "Suspicious token";
  }

  @NotNull
  @Override
  public String getShortName() {
    return "BnfSuspiciousTokenInspection";
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

  private static void checkFile(final PsiFile file, final ProblemsHolder problemsHolder) {
    if (!(file instanceof BnfFile)) return;
    final Set<String> tokens = RuleGraphHelper.getTokenNameToTextMap((BnfFile)file).keySet();
    file.accept(new PsiRecursiveElementWalkingVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        if (element instanceof BnfRule) {
          // do not check external rules
          if (ParserGeneratorUtil.Rule.isExternal((BnfRule)element)) return;
        }
        else if (element instanceof BnfExternalExpression) {
          // do not check external expressions
          return;
        }
        else if (element instanceof BnfRefOrTokenImpl) {
          PsiReference reference = element.getReference();
          Object resolve = reference == null ? null : reference.resolve();
          final String text = element.getText();
          if (resolve == null && !tokens.contains(text) && isTokenTextSuspicious(text)) {
            problemsHolder.registerProblem(element, "'"+text+"' token looks like a reference to a missing rule", new CreateRuleFromTokenFix(text));
          }
        }
        super.visitElement(element);
      }
    });
  }

  public static boolean isTokenTextSuspicious(String text) {
    boolean isLowercase = text.equals(text.toLowerCase());
    boolean isUppercase = !isLowercase && text.equals(text.toUpperCase());
    return !isLowercase && !isUppercase || isLowercase && StringUtil.containsAnyChar(text, "-_");
  }
}
