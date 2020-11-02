/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
