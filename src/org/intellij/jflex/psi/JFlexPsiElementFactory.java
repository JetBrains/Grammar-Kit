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

package org.intellij.jflex.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.SyntaxTraverser;
import org.intellij.jflex.JFlexLanguage;
import org.intellij.jflex.psi.impl.JFlexPsiImplUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class JFlexPsiElementFactory {

  private static PsiFile createFile(@NotNull Project project, @NotNull String text) {
    return PsiFileFactory.getInstance(project).createFileFromText("a.flex", JFlexLanguage.INSTANCE, text, false, false);
  }

  public static PsiElement createIdFromText(@NotNull Project project, @NotNull String text) {
    return JFlexPsiImplUtil.computeDefinitions(createFile(project, "%%\n" + text+"="), JFlexMacroDefinition.class).get(0).getId();
  }

  public static PsiElement createJavaCodeFromText(@NotNull Project project, @NotNull String text) {
    return SyntaxTraverser.psiTraverser(createFile(project, text)).filter(JFlexJavaCode.class).first();
  }

  public static PsiElement createJavaTypeFromText(@NotNull Project project, @NotNull String text) {
    return SyntaxTraverser.psiTraverser(createFile(project, "%%\n%extends " + text)).filter(JFlexJavaType.class).first();
  }
}
