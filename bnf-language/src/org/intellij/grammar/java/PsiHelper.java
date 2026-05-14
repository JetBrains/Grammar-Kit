/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.TypeParameterInfo;
import org.intellij.grammar.classinfo.asm.AsmClassSymbolProvider;
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

  PsiHelper(@NotNull Project project, @Nullable GlobalSearchScope scope) {
    super(new JvmClassSymbolManager(List.of(new AsmClassSymbolProvider())));
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
      if (acceptsName(paramType, psiType.getCanonicalText())) continue;
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

  private static @NotNull List<String> getAnnotationsInner(PsiModifierListOwner element) {
    PsiModifierList modifierList = element.getModifierList();
    if (modifierList == null) return Collections.emptyList();
    PsiType typeToSkip = element instanceof PsiMethod ? ((PsiMethod)element).getReturnType() :
                         element instanceof PsiVariable ? ((PsiVariable)element).getType() : null;
    PsiAnnotation[] annoToSkip = typeToSkip == null ? null :
                                 typeToSkip instanceof PsiArrayType ? ((PsiArrayType)typeToSkip).getComponentType().getAnnotations() :
                                 typeToSkip.getAnnotations();
    String[] textToSkip = annoToSkip == null ? null :
                          ContainerUtil.map(annoToSkip, PsiElement::getText, ArrayUtil.EMPTY_STRING_ARRAY);
    List<String> result = new ArrayList<>();
    for (PsiAnnotation annotation : modifierList.getAnnotations()) {
      if (annotation.getParameterList().getAttributes().length > 0) continue;
      if (textToSkip != null && ArrayUtil.indexOf(textToSkip, annotation.getText()) != -1) continue;
      ContainerUtil.addIfNotNull(result, annotation.getQualifiedName());
    }
    return result;
  }

  @Override
  public PsiReference @NotNull [] getClassReferences(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    BnfAttr bnfAttr = PsiTreeUtil.getParentOfType(element, BnfAttr.class);
    KnownAttribute<?> attr = bnfAttr == null ? null : KnownAttribute.getAttribute(bnfAttr.getName());
    JavaClassReferenceProvider provider = new JavaClassReferenceProvider() {
      @Override
      public GlobalSearchScope getScope(@NotNull Project project) {
        // Honor the *InputPath / *OutputPath cascade resolved by PsiHelperFactory; null = platform default.
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
  public boolean isPublic(@Nullable NavigatablePsiElement element) {
    return element instanceof PsiModifierListOwner && ((PsiModifierListOwner)element).hasModifierProperty("public");
  }

  @Override
  public NavigatablePsiElement findClass(String className) {
    PsiClass aClass = findClassSafe(className);
    return aClass != null ? aClass : super.findClass(className);
  }

  private PsiClass findClassSafe(String className) {
    if (className == null) return null;
    try {
      return myFacade.findClass(className, myScope != null ? myScope : GlobalSearchScope.allScope(myFacade.getProject()));
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
      if (!acceptsName(methodName, method.getName())) continue;
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

  @Override
  public @NotNull List<String> getMethodTypes(NavigatablePsiElement method) {
    if (!(method instanceof PsiMethod psiMethod)) return super.getMethodTypes(method);
    PsiType returnType = psiMethod.getReturnType();
    List<String> strings = new ArrayList<>();
    strings.add(returnType == null ? "" : returnType.getCanonicalText(true));
    for (PsiParameter parameter : psiMethod.getParameterList().getParameters()) {
      PsiType type = parameter.getType();
      boolean generic = type instanceof PsiClassType && ((PsiClassType)type).resolve() instanceof PsiTypeParameter;
      String typeText = (generic ? "<" : "") + type.getCanonicalText(true) + (generic ? ">" : "");
      strings.add(typeText);
      strings.add(parameter.getName());
    }
    return strings;
  }

  @Override
  public List<TypeParameterInfo> getGenericParameters(NavigatablePsiElement method) {
    if (!(method instanceof PsiMethod psiMethod)) return super.getGenericParameters(method);

    PsiTypeParameter[] typeParameters = psiMethod.getTypeParameters();
    return ContainerUtil.map(typeParameters, param -> new TypeParameterInfo(
      param.getName(),
      ContainerUtil.map(param.getExtendsListTypes(), bound -> bound.getCanonicalText(false)),
      ContainerUtil.map(getAnnotationsInner(param), Fqn::of)));
  }

  @Override
  public List<String> getExceptionList(NavigatablePsiElement method) {
    if (!(method instanceof PsiMethod psiMethod)) return super.getExceptionList(method);

    PsiClassType[] types = psiMethod.getThrowsList().getReferencedTypes();
    return ContainerUtil.map(types, type -> type.getCanonicalText(false));
  }

  @Override
  public @NotNull String getDeclaringClass(@Nullable NavigatablePsiElement method) {
    if (!(method instanceof PsiMethod psiMethod)) return super.getDeclaringClass(method);
    PsiClass aClass = psiMethod.getContainingClass();
    return aClass == null ? "" : StringUtil.notNullize(aClass.getQualifiedName());
  }

  @Override
  public @NotNull List<String> getAnnotations(NavigatablePsiElement element) {
    if (!(element instanceof PsiModifierListOwner)) return super.getAnnotations(element);
    return getAnnotationsInner((PsiModifierListOwner)element);
  }

  @Override
  public @NotNull List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
    if (!(method instanceof PsiMethod psiMethod)) return super.getParameterAnnotations(method, paramIndex);
    PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
    if (paramIndex < 0 || paramIndex >= parameters.length) return Collections.emptyList();
    return getAnnotationsInner(parameters[paramIndex]);
  }
}
