/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.search;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.UseScopeEnlarger;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.jetbrains.kotlin.idea.KotlinFileType;
import org.jetbrains.kotlin.psi.KtNamedFunction;
import org.jetbrains.kotlin.psi.KtProperty;

import java.util.Collection;

public class BnfFileUseScopeEnlargerTest extends JavaCodeInsightFixtureTestCase {

  private final UseScopeEnlarger myJavaEnlarger = new BnfJavaFileUseScopeEnlarger();
  private final UseScopeEnlarger myKotlinEnlarger = new BnfKotlinFileUseScopeEnlarger();

  public void testJavaPrivateMemberIsEnlarged() {
    PsiJavaFile file = (PsiJavaFile)myFixture.addFileToProject(
      "Util.java", "class Util { private void priv() {} public void pub() {} }");
    PsiClass cls = file.getClasses()[0];

    assertNotNull(myJavaEnlarger.getAdditionalUseScope(method(cls, "priv")));
  }

  public void testJavaNonPrivateMemberIsNotEnlarged() {
    PsiJavaFile file = (PsiJavaFile)myFixture.addFileToProject(
      "Util.java", "class Util { private void priv() {} public void pub() {} }");
    PsiClass cls = file.getClasses()[0];

    assertNull(myJavaEnlarger.getAdditionalUseScope(method(cls, "pub")));
  }

  public void testKotlinEnlargerIgnoresJavaFile() {
    PsiJavaFile file = (PsiJavaFile)myFixture.addFileToProject(
      "Util.java", "class Util { private void priv() {} }");
    PsiClass cls = file.getClasses()[0];

    assertNull(myKotlinEnlarger.getAdditionalUseScope(method(cls, "priv")));
  }

  public void testKotlinPrivateMemberIsEnlarged() {
    KtNamedFunction priv = kotlinFunction("priv");

    assertNotNull(myKotlinEnlarger.getAdditionalUseScope(priv));
  }

  public void testKotlinNonPrivateMemberIsNotEnlarged() {
    KtNamedFunction pub = kotlinFunction("pub");

    assertNull(myKotlinEnlarger.getAdditionalUseScope(pub));
  }

  public void testJavaLocalVariableIsNotEnlarged() {
    PsiFile file = myFixture.addFileToProject("Util.java", "class Util { void m() { int local = 0; } }");
    PsiLocalVariable local = PsiTreeUtil.findChildOfType(file, PsiLocalVariable.class);
    assertNotNull(local);

    assertNull(myJavaEnlarger.getAdditionalUseScope(local));
  }

  public void testKotlinLocalVariableIsNotEnlarged() {
    PsiFile file = myFixture.addFileToProject("local.kt", "fun m() { val local = 0 }");
    assertEquals("Kotlin plugin must be loaded for this test", KotlinFileType.INSTANCE, file.getFileType());
    KtProperty local = PsiTreeUtil.findChildOfType(file, KtProperty.class);
    assertNotNull(local);

    assertNull(myKotlinEnlarger.getAdditionalUseScope(local));
  }

  public void testJavaEnlargerIgnoresKotlinFile() {
    KtNamedFunction priv = kotlinFunction("priv");

    assertNull(myJavaEnlarger.getAdditionalUseScope(priv));
  }

  public void testEnlargersIgnorePlainTextFile() {
    PsiFile file = myFixture.addFileToProject("notes.txt", "private");

    assertNull(myJavaEnlarger.getAdditionalUseScope(file));
    assertNull(myKotlinEnlarger.getAdditionalUseScope(file));
  }

  private static PsiMethod method(PsiClass cls, String name) {
    PsiMethod[] methods = cls.findMethodsByName(name, false);
    assertEquals("expected a single method named '" + name + "'", 1, methods.length);
    return methods[0];
  }

  private KtNamedFunction kotlinFunction(String name) {
    PsiFile file = myFixture.addFileToProject("util.kt", "private fun priv() {}\nfun pub() {}");
    assertEquals("Kotlin plugin must be loaded for this test", KotlinFileType.INSTANCE, file.getFileType());
    Collection<KtNamedFunction> functions = PsiTreeUtil.findChildrenOfType(file, KtNamedFunction.class);
    for (KtNamedFunction function : functions) {
      if (name.equals(function.getName())) return function;
    }
    fail("expected a function named '" + name + "'");
    return null;
  }
}
