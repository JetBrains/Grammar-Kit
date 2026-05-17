/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ProcessingContext;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.psi.BnfAttr;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.intellij.grammar.psi.BnfAttributes.getRootAttribute;

/**
 * {@link JavaHelper} backed by the IntelliJ PSI / Java indices.
 * <p>
 * This is the project-level service registered in {@code plugin-java.xml} and the helper actually
 * used while editing inside the IDE. It returns real {@link PsiClass} / {@link PsiMethod} elements
 * (so navigation, find-usages and rename Just Work), and additionally implements
 * {@link #getClassReferences} — a no-op in the bytecode and reflection helpers — to wire BNF
 * attribute string literals into the standard Java reference machinery.
 * <p>
 * When the PSI index does not know about a class (e.g. it is on the classpath but outside the
 * project, or the index is currently unavailable), every override transparently falls back to the
 * inherited {@link JvmSyntaxHelper} behaviour — backed here by an {@link AsmClassSymbolProvider}.
 * This dual strategy is what lets the IDE resolve both project sources and platform/library
 * classes uniformly.
 */
final class PsiHelper extends JvmSyntaxHelper {
  private final JavaPsiFacade myFacade;
  private final PsiElementFactory myElementFactory;
  private final @Nullable GlobalSearchScope myScope;

  PsiHelper(@NotNull Project project, @Nullable GlobalSearchScope scope, @NotNull JvmClassSymbolManager fallbackManager) {
    super(fallbackManager);
    myFacade = JavaPsiFacade.getInstance(project);
    myElementFactory = PsiElementFactory.getInstance(project);
    myScope = scope;
  }

  private static boolean acceptsMethod(PsiElementFactory elementFactory,
                                       PsiMethod method,
                                       int paramCount,
                                       String... paramTypes) {
    boolean varArgs = method.isVarArgs();
    PsiParameterList parameterList = method.getParameterList();
    if (paramCount >= 0 && (!varArgs && paramCount != parameterList.getParametersCount() ||
                            varArgs && paramCount < parameterList.getParametersCount() - 1)) {
      return false;
    }
    if (paramTypes.length == 0) return true;
    if (parameterList.getParametersCount() < paramTypes.length) return false;
    PsiParameter[] psiParameters = parameterList.getParameters();
    for (int i = 0; i < paramTypes.length; i++) {
      String paramType = paramTypes[i];
      PsiParameter parameter = psiParameters[i];
      PsiType psiType = parameter.getType();
      if (ClassSymbolUtil.acceptsName(paramType, psiType.getCanonicalText())) continue;
      try {
        if (psiType.isAssignableFrom(elementFactory.createTypeFromText(paramType, parameter))) continue;
      }
      catch (IncorrectOperationException ignored) {
      }
      return false;
    }
    return true;
  }

  private static boolean acceptsMethod(PsiMethod method, MethodType methodType, Boolean allowAbstract) {
    PsiModifierList modifierList = method.getModifierList();
    return (methodType == MethodType.STATIC) == modifierList.hasModifierProperty(PsiModifier.STATIC) &&
           (!modifierList.hasModifierProperty(PsiModifier.ABSTRACT) || allowAbstract) &&
           !(methodType == MethodType.CONSTRUCTOR && modifierList.hasModifierProperty(PsiModifier.PRIVATE));
  }

  @Override
  public PsiReference @NotNull [] getClassReferences(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    BnfAttr bnfAttr = PsiTreeUtil.getParentOfType(element, BnfAttr.class);
    KnownAttribute<?> attr = bnfAttr == null ? null : KnownAttribute.getAttribute(bnfAttr.getName());
    JavaClassReferenceProvider provider = new JavaClassReferenceProvider() {
      @Override
      public GlobalSearchScope getScope(@NotNull Project project) {
        // Honor the *InputPath / *OutputPath cascade resolved by JavaHelperFactory; null = platform default.
        return myScope;
      }
    };
    provider.setOption(JavaClassReferenceProvider.ALLOW_DOLLAR_NAMES, false);
    provider.setOption(JavaClassReferenceProvider.ADVANCED_RESOLVE, true);
    if (attr == KnownAttribute.EXTENDS || attr == KnownAttribute.IMPLEMENTS || attr == KnownAttribute.STUB_CLASS) {
      provider.setOption(JavaClassReferenceProvider.IMPORTS, Arrays.asList(
        CommonClassNames.DEFAULT_PACKAGE, getRootAttribute(element, KnownAttribute.PSI_PACKAGE)));
    }
    else if (attr == KnownAttribute.MIXIN) {
      provider.setOption(JavaClassReferenceProvider.IMPORTS, Arrays.asList(
        CommonClassNames.DEFAULT_PACKAGE, getRootAttribute(element, KnownAttribute.PSI_PACKAGE),
        getRootAttribute(element, KnownAttribute.PSI_IMPL_PACKAGE)));
    }
    else if (attr != null && attr.getName().endsWith("Class")) {
      provider.setOption(JavaClassReferenceProvider.IMPORTS, Collections.singletonList(CommonClassNames.DEFAULT_PACKAGE));
    }
    provider.setSoft(false);
    return provider.getReferencesByElement(element, context);
  }

  @Override
  public NavigatablePsiElement findClass(String className) {
    PsiClass aClass = findClassSafe(className);
    return aClass != null ? aClass : super.findClass(className);
  }

  private PsiClass findClassSafe(String className) {
    if (className == null) return null;
    try {
      GlobalSearchScope scope = myScope != null ? myScope : GlobalSearchScope.allScope(myFacade.getProject());
      return myFacade.findClass(className, scope);
    }
    catch (IndexNotReadyException e) {
      return null;
    }
  }

  @Override
  public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                               @NotNull MethodType methodType,
                                                               @Nullable String methodName,
                                                               boolean allowAbstract,
                                                               int paramCount,
                                                               String... paramTypes) {
    if (methodName == null) return Collections.emptyList();
    PsiClass aClass = findClassSafe(className);
    if (aClass == null) return super.findClassMethods(className, methodType, methodName, allowAbstract, paramCount, paramTypes);
    List<NavigatablePsiElement> result = new ArrayList<>();
    PsiMethod[] methods = methodType == MethodType.CONSTRUCTOR ? aClass.getConstructors() : aClass.getMethods(); // todo super methods too
    for (PsiMethod method : methods) {
      if (!ClassSymbolUtil.acceptsName(methodName, method.getName())) continue;
      if (!acceptsMethod(method, methodType, allowAbstract)) continue;
      if (!acceptsMethod(myElementFactory, method, paramCount, paramTypes)) continue;
      result.add(method);
    }
    return result;
  }

  @Override
  public @Nullable String getSuperClassName(@Nullable String className) {
    PsiClass aClass = findClassSafe(className);
    PsiClass superClass = aClass != null ? aClass.getSuperClass() : null;
    return superClass != null ? superClass.getQualifiedName() : super.getSuperClassName(className);
  }
}
