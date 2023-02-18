/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.*;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.FactoryMap;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.NameShortener;
import org.intellij.grammar.psi.BnfAttr;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.org.objectweb.asm.*;
import org.jetbrains.org.objectweb.asm.signature.SignatureReader;
import org.jetbrains.org.objectweb.asm.signature.SignatureVisitor;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.lang.reflect.*;
import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;

/**
 * @author gregsh
 */
public abstract class JavaHelper {

  public enum MethodType { STATIC, INSTANCE, CONSTRUCTOR }

  public static JavaHelper getJavaHelper(@NotNull PsiElement context) {
    JavaHelper service = context.getProject().getService(JavaHelper.class);
    return service == null ? new AsmHelper() : service;
  }

  public abstract boolean isPublic(@Nullable NavigatablePsiElement element);

  public @Nullable NavigatablePsiElement findClass(@Nullable String className) {
    return null;
  }

  public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                               @NotNull MethodType methodType,
                                                               @Nullable String methodName,
                                                               int paramCount,
                                                               String... paramTypes) {
    return Collections.emptyList();
  }

  public @Nullable String getSuperClassName(@Nullable String className) {
    return null;
  }

  public @NotNull List<String> getMethodTypes(@Nullable NavigatablePsiElement method) {
    return Collections.emptyList();
  }

  public List<TypeParameterInfo> getGenericParameters(NavigatablePsiElement method) {
    return Collections.emptyList();
  }

  public List<String> getExceptionList(NavigatablePsiElement method) {
    return Collections.emptyList();
  }

  public @NotNull String getDeclaringClass(@Nullable NavigatablePsiElement method) {
    return "";
  }

  public @NotNull List<String> getAnnotations(@Nullable NavigatablePsiElement element) {
    return Collections.emptyList();
  }

  public @NotNull List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
    return Collections.emptyList();
  }

  public PsiReference @NotNull [] getClassReferences(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    return PsiReference.EMPTY_ARRAY;
  }

  public @Nullable NavigationItem findPackage(@Nullable String packageName) {
    return null;
  }


  private static boolean acceptsName(@Nullable String expected, @Nullable String actual) {
    return "*".equals(expected) || expected != null && expected.equals(actual);
  }

  private static boolean acceptsModifiers(int modifiers, MethodType methodType) {
    return !Modifier.isAbstract(modifiers) &&
           !(methodType == MethodType.CONSTRUCTOR && Modifier.isPrivate(modifiers));
  }

  private static class PsiHelper extends AsmHelper {
    private final JavaPsiFacade myFacade;
    private final PsiElementFactory myElementFactory;

    private PsiHelper(@NotNull Project project) {
      myFacade = JavaPsiFacade.getInstance(project);
      myElementFactory = PsiElementFactory.getInstance(project);
    }

    @Override
    public PsiReference @NotNull [] getClassReferences(@NotNull PsiElement element, @NotNull ProcessingContext context) {
      BnfAttr bnfAttr = PsiTreeUtil.getParentOfType(element, BnfAttr.class);
      KnownAttribute<?> attr = bnfAttr == null ? null : KnownAttribute.getAttribute(bnfAttr.getName());
      JavaClassReferenceProvider provider = new JavaClassReferenceProvider();
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
        return myFacade.findClass(className, GlobalSearchScope.allScope(myFacade.getProject()));
      }
      catch (IndexNotReadyException e) {
        return null;
      }
    }

    @Override
    public NavigationItem findPackage(String packageName) {
      return myFacade.findPackage(packageName);
    }

    @Override
    public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                                 @NotNull MethodType methodType,
                                                                 @Nullable String methodName,
                                                                 int paramCount,
                                                                 String... paramTypes) {
      if (methodName == null) return Collections.emptyList();
      PsiClass aClass = findClassSafe(className);
      if (aClass == null) return super.findClassMethods(className, methodType, methodName, paramCount, paramTypes);
      List<NavigatablePsiElement> result = new ArrayList<>();
      PsiMethod[] methods = methodType == MethodType.CONSTRUCTOR ? aClass.getConstructors() : aClass.getMethods(); // todo super methods too
      for (PsiMethod method : methods) {
        if (!acceptsName(methodName, method.getName())) continue;
        if (!acceptsMethod(method, methodType)) continue;
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

    private static boolean acceptsMethod(PsiElementFactory elementFactory,
                                         PsiMethod method,
                                         int paramCount,
                                         String... paramTypes) {
      boolean varArgs = method.isVarArgs();
      PsiParameterList parameterList = method.getParameterList();
      if (paramCount >= 0 && (!varArgs && paramCount != parameterList.getParametersCount() ||
                              varArgs && paramCount < parameterList.getParametersCount() - 1)) return false;
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

    private static boolean acceptsMethod(PsiMethod method, MethodType methodType) {
      PsiModifierList modifierList = method.getModifierList();
      return (methodType == MethodType.STATIC) == modifierList.hasModifierProperty(PsiModifier.STATIC) &&
             !modifierList.hasModifierProperty(PsiModifier.ABSTRACT) &&
             !(methodType == MethodType.CONSTRUCTOR && modifierList.hasModifierProperty(PsiModifier.PRIVATE));
    }

    @Override
    public @NotNull List<String> getMethodTypes(NavigatablePsiElement method) {
      if (!(method instanceof PsiMethod)) return super.getMethodTypes(method);
      PsiMethod psiMethod = (PsiMethod)method;
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
      if (!(method instanceof PsiMethod)) return super.getGenericParameters(method);

      PsiMethod psiMethod = (PsiMethod)method;
      PsiTypeParameter[] typeParameters = psiMethod.getTypeParameters();
      return ContainerUtil.map(typeParameters, param -> new TypeParameterInfo(
        param.getName(),
        ContainerUtil.map(param.getExtendsListTypes(), bound -> bound.getCanonicalText(false)),
        getAnnotationsInner(param)));
    }

    @Override
    public List<String> getExceptionList(NavigatablePsiElement method) {
      if (!(method instanceof PsiMethod)) return super.getExceptionList(method);

      PsiMethod psiMethod = (PsiMethod)method;
      PsiClassType[] types = psiMethod.getThrowsList().getReferencedTypes();
      return ContainerUtil.map(types, type -> type.getCanonicalText(false));
    }

    @Override
    public @NotNull String getDeclaringClass(@Nullable NavigatablePsiElement method) {
      if (!(method instanceof PsiMethod)) return super.getDeclaringClass(method);
      PsiMethod psiMethod = (PsiMethod)method;
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
      if (!(method instanceof PsiMethod)) return super.getParameterAnnotations(method, paramIndex);
      PsiMethod psiMethod = (PsiMethod)method;
      PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
      if (paramIndex < 0 || paramIndex >= parameters.length) return Collections.emptyList();
      return getAnnotationsInner(parameters[paramIndex]);
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
        if (textToSkip != null && ArrayUtil.indexOf(textToSkip, annotation.getText()) != - 1) continue;
        ContainerUtil.addIfNotNull(result, annotation.getQualifiedName());
      }
      return result;
    }
  }

  public static class ReflectionHelper extends JavaHelper {
    @Override
    public boolean isPublic(@Nullable NavigatablePsiElement element) {
      Object delegate = element instanceof MyElement ? ((MyElement<?>)element).delegate : null;
      int modifiers = delegate instanceof Class ? ((Class<?>)delegate).getModifiers() :
                      delegate instanceof Method ? ((Method)delegate).getModifiers() :
                      0;
      return Modifier.isPublic(modifiers);
    }

    @Override
    public @Nullable NavigatablePsiElement findClass(String className) {
      Class<?> aClass = findClassSafe(className);
      return aClass == null ? null : new MyElement<Class<?>>(aClass);
    }

    private static @Nullable Class<?> findClassSafe(String className) {
      if (className == null) return null;
      try {
        return Class.forName(className);
      }
      catch (Exception e) {
        return null;
      }
    }

    @Override
    public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                                 @NotNull MethodType methodType,
                                                                 @Nullable String methodName,
                                                                 int paramCount,
                                                                 String... paramTypes) {
      Class<?> aClass = findClassSafe(className);
      if (aClass == null || methodName == null) return Collections.emptyList();
      List<NavigatablePsiElement> result = new ArrayList<>();
      Member[] methods = methodType == MethodType.CONSTRUCTOR ? aClass.getDeclaredConstructors() : aClass.getDeclaredMethods();
      for (Member method : methods) {
        if (!acceptsName(methodName, method.getName())) continue;
        if (!acceptsMethod(method, methodType)) continue;
        if (!acceptsMethod(method, paramCount, paramTypes)) continue;
        result.add(new MyElement<>(method));
      }
      return result;
    }

    @Override
    public @Nullable String getSuperClassName(@Nullable String className) {
      Class<?> aClass = findClassSafe(className);
      Class<?> superClass = aClass == null ? null : aClass.getSuperclass();
      return superClass != null && superClass != Object.class ? superClass.getName() : null;
    }

    private static boolean acceptsMethod(Member method, int paramCount, String... paramTypes) {
      Class<?>[] parameterTypes = method instanceof Method? ((Method)method).getParameterTypes() :
                                  method instanceof Constructor ? ((Constructor<?>)method).getParameterTypes() :
                                  ArrayUtil.EMPTY_CLASS_ARRAY;
      if (paramCount >= 0 && paramCount != parameterTypes.length) return false;
      if (paramTypes.length == 0) return true;
      if (paramTypes.length > parameterTypes.length) return false;
      for (int i = 0; i < paramTypes.length; i++) {
        String paramType = paramTypes[i];
        Class<?> parameter = parameterTypes[i];
        if (acceptsName(paramType, parameter.getCanonicalName())) continue;
        Class<?> paramClass = findClassSafe(paramType);
        if (paramClass != null && parameter.isAssignableFrom(paramClass)) continue;
        return false;
      }
      return true;
    }

    private static boolean acceptsMethod(Member method, MethodType methodType) {
      int modifiers = method.getModifiers();
      return (methodType == MethodType.STATIC) == Modifier.isStatic(modifiers) &&
             acceptsModifiers(modifiers, methodType);
    }

    @Override
    public @NotNull List<String> getMethodTypes(NavigatablePsiElement method) {
      if (method == null) return Collections.emptyList();
      Method delegate = ((MyElement<Method>)method).delegate;
      Type[] parameterTypes = delegate.getGenericParameterTypes();
      List<String> result = new ArrayList<>(parameterTypes.length + 1);
      result.add(delegate.getGenericReturnType().toString());
      int paramCounter = 0;
      for (Type parameterType : parameterTypes) {
        result.add(parameterType.toString());
        result.add("p" + (paramCounter++));
      }
      return result;
    }

    @Override
    public List<TypeParameterInfo> getGenericParameters(NavigatablePsiElement method) {
      if (method == null) return Collections.emptyList();
      Method delegate = ((MyElement<Method>)method).delegate;

      TypeVariable<Method>[] typeParameters = delegate.getTypeParameters();
      return ContainerUtil.map(typeParameters, param -> new TypeParameterInfo(
        param.getName(),
        ContainerUtil.mapNotNull(param.getBounds(), type -> {
          String typeName = type.getTypeName();
          return "java.lang.Object".equals(typeName) ? null : typeName;
        }),
        ContainerUtil.mapNotNull(param.getAnnotations(), o -> o.annotationType().getCanonicalName())));
    }

    @Override
    public List<String> getExceptionList(NavigatablePsiElement method) {
      if (method == null) return Collections.emptyList();
      Method delegate = ((MyElement<Method>)method).delegate;

      Class<?>[] exceptionTypes = delegate.getExceptionTypes();
      return ContainerUtil.map(exceptionTypes, Class::getName);
    }

    @Override
    public @NotNull String getDeclaringClass(@Nullable NavigatablePsiElement method) {
      if (method == null) return "";
      return ((MyElement<Method>)method).delegate.getDeclaringClass().getName();
    }

    @Override
    public @NotNull List<String> getAnnotations(NavigatablePsiElement element) {
      if (element == null) return Collections.emptyList();
      AnnotatedElement delegate = ((MyElement<AnnotatedElement>)element).delegate;
      return getAnnotationsInner(delegate);
    }

    @Override
    public @NotNull List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
      if (method == null) return Collections.emptyList();
      Method delegate = ((MyElement<Method>)method).delegate;
      AnnotatedType[] parameterTypes = delegate.getAnnotatedParameterTypes();
      if (paramIndex < 0 || paramIndex >= parameterTypes.length) return Collections.emptyList();
      return getAnnotationsInner(delegate);
    }

    private static @NotNull List<String> getAnnotationsInner(@NotNull AnnotatedElement delegate) {
      Annotation[] annotations = delegate.getDeclaredAnnotations();
      List<String> result = new ArrayList<>(annotations.length);
      for (Annotation annotation : annotations) {
        Class<? extends Annotation> annotationType = annotation.annotationType(); // todo parameters?
        ContainerUtil.addIfNotNull(result, annotationType.getCanonicalName());
      }
      return result;
    }
  }

  public static class AsmHelper extends JavaHelper {

    private static final int ASM_OPCODES = Opcodes.ASM9;

    @Override
    public boolean isPublic(@Nullable NavigatablePsiElement element) {
      Object delegate = element instanceof MyElement ? ((MyElement<?>)element).delegate : null;
      int access = delegate instanceof ClassInfo ? ((ClassInfo)delegate).modifiers :
                   delegate instanceof MethodInfo ? ((MethodInfo)delegate).modifiers :
                   0;
      return Modifier.isPublic(access);
    }

    @Override
    public @Nullable NavigatablePsiElement findClass(String className) {
      ClassInfo info = findClassSafe(className);
      return info == null ? null : new MyElement<>(info);
    }

    @Override
    public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                                 @NotNull MethodType methodType,
                                                                 @Nullable String methodName,
                                                                 int paramCount,
                                                                 String... paramTypes) {
      ClassInfo aClass = findClassSafe(className);
      if (aClass == null || methodName == null) return Collections.emptyList();
      List<NavigatablePsiElement> result = new ArrayList<>();
      for (MethodInfo method : aClass.methods) { // todo super methods too
        if (!acceptsName(methodName, method.name)) continue;
        if (!acceptsMethod(method, methodType)) continue;
        if (!acceptsMethod(method, paramCount, paramTypes)) continue;
        result.add(new MyElement<>(method));
      }
      return result;
    }

    @Override
    public @Nullable String getSuperClassName(@Nullable String className) {
      ClassInfo aClass = findClassSafe(className);
      return aClass == null ? null : aClass.superClass;
    }

    private static boolean acceptsMethod(MethodInfo method, int paramCount, String... paramTypes) {
      if (paramCount >= 0 && paramCount != (method.types.size() - 1) / 2) return false;
      if (paramTypes.length == 0) return true;
      if (paramTypes.length > (method.types.size() - 1) / 2) return false;
      for (int i = 0; i < paramTypes.length; i ++) {
        String paramType = paramTypes[i];
        String parameter = method.types.get(2 * i + 1);
        if (acceptsName(paramType, NameShortener.getRawClassName(parameter))) continue;
        ClassInfo info = findClassSafe(paramType);
        if (info != null) {
          if (Objects.equals(info.superClass, parameter)) continue;
          if (info.interfaces.contains(parameter)) continue;
        }
        return false;
      }
      return true;
    }

    private static boolean acceptsMethod(MethodInfo method, MethodType methodType) {
      return method.methodType == methodType && acceptsModifiers(method.modifiers, methodType);
    }

    @Override
    public @NotNull List<String> getMethodTypes(NavigatablePsiElement method) {
      Object delegate = method == null ? null : ((MyElement<?>)method).delegate;
      if (!(delegate instanceof MethodInfo)) return Collections.emptyList();
      return ((MethodInfo)delegate).annotatedTypes;
    }

    @Override
    public List<TypeParameterInfo> getGenericParameters(NavigatablePsiElement method) {
      Object delegate = method == null ? null : ((MyElement<?>)method).delegate;
      if (!(delegate instanceof MethodInfo)) return Collections.emptyList();
      return ((MethodInfo)delegate).generics;
    }

    @Override
    public List<String> getExceptionList(NavigatablePsiElement method) {
      Object delegate = method == null ? null : ((MyElement<?>)method).delegate;
      if (!(delegate instanceof MethodInfo)) return Collections.emptyList();
      return ((MethodInfo)delegate).exceptions;
    }

    @Override
    public @NotNull String getDeclaringClass(@Nullable NavigatablePsiElement method) {
      Object delegate = method == null ? null : ((MyElement<?>)method).delegate;
      if (!(delegate instanceof MethodInfo)) return "";
      return ((MethodInfo)delegate).declaringClass;
    }

    @Override
    public @NotNull List<String> getAnnotations(NavigatablePsiElement element) {
      Object delegate = element == null ? null : ((MyElement<?>)element).delegate;
      if (delegate instanceof ClassInfo) return ((ClassInfo)delegate).annotations;
      if (delegate instanceof MethodInfo) return ((MethodInfo)delegate).annotations.get(0);
      return Collections.emptyList();
    }

    @Override
    public @NotNull List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
      Object delegate = method == null ? null : ((MyElement<?>)method).delegate;
      if (!(delegate instanceof MethodInfo)) return Collections.emptyList();
      Map<Integer, List<String>> annotations = ((MethodInfo)delegate).annotations;
      if (paramIndex < 0 || paramIndex >= annotations.size()) return Collections.emptyList();
      List<String> result = annotations.get(paramIndex);
      return result == null ? Collections.emptyList() : result;
    }

    private static ClassInfo findClassSafe(String className) {
      if (className == null) return null;
      try {
        int lastDot = className.length();
        InputStream is;
        do {
          String s = className.substring(0, lastDot).replace('.', '/') +
                     className.substring(lastDot).replace('.', '$') +
                     ".class";
          is = JavaHelper.class.getClassLoader().getResourceAsStream(s);
          lastDot = className.lastIndexOf('.', lastDot - 1);
        }
        while(is == null && lastDot > 0);

        if (is == null) return null;
        byte[] bytes = FileUtil.loadBytes(is);
        is.close();
        return getClassInfo(className, bytes);
      }
      catch (Exception e) {
        reportException(e, className, null);
      }
      return null;
    }

    private static ClassInfo getClassInfo(String className, byte[] bytes) {
      ClassInfo info = new ClassInfo();
      info.name = className;
      new ClassReader(bytes).accept(new MyClassVisitor(info), 0);
      return info;
    }

    private static MethodInfo getMethodInfo(String className, String methodName, String signature, String[] exceptions) {
      MethodInfo methodInfo = new MethodInfo();
      methodInfo.name = methodName;
      methodInfo.declaringClass = className;

      try {
        MySignatureVisitor visitor = new MySignatureVisitor(methodInfo);
        new SignatureReader(signature).accept(visitor);
        visitor.finishElement(null);

        if (exceptions != null) {
          for (String exception : exceptions) {
            String fqn = fixClassName(exception);
            methodInfo.exceptions.add(fqn);
          }
        }
      }
      catch (Exception e) {
        reportException(e, className + "#" + methodName + "()", signature);
      }
      return methodInfo;
    }

    private static void reportException(Exception e, String target, String signature) {
      reportException(e.getClass().getSimpleName() + " while reading " + target +
                      (signature == null ? "" : " signature " + signature));
    }

    private static void reportException(String text) {
      //noinspection UseOfSystemOutOrSystemErr
      System.err.println(text);
    }

    private static class MyClassVisitor extends ClassVisitor {

      private final ClassInfo myInfo;

      MyClassVisitor(ClassInfo info) {
        super(ASM_OPCODES);
        myInfo = info;
      }

      @Override
      public void visit(int version,
                        int access,
                        String name,
                        String signature,
                        String superName,
                        String[] interfaces) {
        myInfo.modifiers = access;
        myInfo.superClass = fixClassName(superName);
        for (String s : interfaces) {
          myInfo.interfaces.add(fixClassName(s));
        }
        if (signature != null) {
          new SignatureReader(signature).accept(new SignatureVisitor(ASM_OPCODES) {
            @Override
            public void visitFormalTypeParameter(String name) {
              myInfo.typeParameters.add(name);
            }
          });
        }
      }

      @Override
      public void visitEnd() {
      }

      @Override
      public MethodVisitor visitMethod(int access,
                                       String name,
                                       String desc,
                                       String signature,
                                       String[] exceptions) {
        MethodInfo m = getMethodInfo(myInfo.name, name, ObjectUtils.chooseNotNull(signature, desc), exceptions);
        m.modifiers = access;
        m.methodType = "<init>".equals(name)? MethodType.CONSTRUCTOR :
                                Modifier.isStatic(access) ? MethodType.STATIC :
                                MethodType.INSTANCE;
        myInfo.methods.add(m);
        class ParamTypeAnno { int index; String anno; TypePath path; }
        List<ParamTypeAnno> typeAnnos = new SmartList<>();
        return new MethodVisitor(ASM_OPCODES) {
          @Override
          public void visitEnd() {
            m.annotatedTypes.addAll(m.types);
            int[] plainOffsets = new int[m.types.size()];
            for (ParamTypeAnno ta : typeAnnos) {
              int idx = ta.index == 0 ? 0 : 2 * (ta.index - 1) + 1;
              String prevType = m.annotatedTypes.get(idx);
              boolean isArray = false;
              if (ta.path != null) {
                isArray = true;
                int typePtr = prevType.length();
                for (int i = 0; i < ta.path.getLength(); i++, typePtr -= 2) {
                  if (!(ta.path.getStep(i) == TypePath.ARRAY_ELEMENT && typePtr > 2 &&
                        prevType.charAt(typePtr - 2) == '[' && prevType.charAt(typePtr - 1) == ']')) {
                    isArray = false;
                    break;
                  }
                }
                if (isArray && typePtr > 2 && prevType.charAt(typePtr - 1) == ']') isArray = false;
              }
              if (ta.path != null && !isArray) continue;
              int bracketIdx = prevType.indexOf('[');
              String newType;
              if (!isArray && bracketIdx > 0) {
                boolean addSpace = prevType.charAt(bracketIdx - 1) != ' ';
                newType = prevType.substring(0, bracketIdx) + (addSpace ? " " : "") + "@" + ta.anno + " " + prevType.substring(bracketIdx);
              }
              else {
                int offset = plainOffsets[idx];
                newType = prevType.substring(0, offset) + "@" + ta.anno + " " + prevType.substring(offset);
                plainOffsets[idx] += 2 + ta.anno.length();
              }
              m.annotatedTypes.set(idx, newType);
              if (isArray || bracketIdx < 1) {
                m.annotations.get(ta.index).remove(ta.anno);
              }
            }
          }

          @Override
          public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            String anno = fixClassName(desc.substring(1, desc.length() - 1));
            return new MyAnnotationVisitor() {
              @Override
              public void visitEnd() {
                if (annoParamCounter != 0) return;
                m.annotations.get(0).add(anno);
              }
            };
          }

          @Override
          public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
            String anno = fixClassName(desc.substring(1, desc.length() - 1));
            return new MyAnnotationVisitor() {
              @Override
              public void visitEnd() {
                if (annoParamCounter != 0) return;
                m.annotations.get(parameter + 1).add(anno);
              }
            };
          }

          @Override
          public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            String anno = fixClassName(desc.substring(1, desc.length() - 1));
            TypeReference typeReference = new TypeReference(typeRef);
            return new MyAnnotationVisitor() {
              @Override
              public void visitEnd() {
                if (annoParamCounter != 0) return;
                if (typeReference.getSort() == TypeReference.METHOD_TYPE_PARAMETER) {
                  m.generics.get(typeReference.getTypeParameterIndex()).getAnnotations().add(anno);
                }
                else if (typeReference.getSort() == TypeReference.METHOD_RETURN ||
                         typeReference.getSort() == TypeReference.METHOD_FORMAL_PARAMETER) {
                  ParamTypeAnno o = new ParamTypeAnno();
                  o.index = typeReference.getSort() == TypeReference.METHOD_RETURN ? 0 : typeReference.getFormalParameterIndex() + 1;
                  o.anno = anno;
                  o.path = typePath;
                  typeAnnos.add(o);
                }
                else if (typeReference.getSort() == TypeReference.METHOD_TYPE_PARAMETER_BOUND) {
                  List<String> bounds = m.generics.get(typeReference.getTypeParameterIndex()).extendsList;
                  if (typeReference.getTypeParameterBoundIndex() <= bounds.size()) {
                    String prev = bounds.get(typeReference.getTypeParameterBoundIndex() - 1);
                    bounds.set(typeReference.getTypeParameterBoundIndex() - 1, "@" + anno + " " + prev);
                  }
                }
              }
            };
          }
        };
      }

      static class MyAnnotationVisitor extends AnnotationVisitor {
        int annoParamCounter;

        MyAnnotationVisitor() {
          super(ASM_OPCODES);
        }

        @Override
        public void visit(String s, Object o) {
          annoParamCounter++;
        }

        @Override
        public void visitEnum(String s, String s2, String s3) {
          annoParamCounter++;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String s, String s2) {
          annoParamCounter++;
          return null;
        }

        @Override
        public AnnotationVisitor visitArray(String s) {
          annoParamCounter++;
          return null;
        }
      }
    }

    private static String fixClassName(String s) {
      return s == null ? null : s.replace('/', '.').replace('$', '.');
    }

    private static class MySignatureVisitor extends SignatureVisitor {
      enum State {PARAM, RETURN, CLASS, ARRAY, GENERIC, BOUNDS, EXCEPTION}

      final MethodInfo methodInfo;
      final Deque<State> states = new ArrayDeque<>();

      /** @noinspection StringBufferField*/
      final StringBuilder sb = new StringBuilder();

      MySignatureVisitor(MethodInfo methodInfo) {
        super(ASM_OPCODES);
        this.methodInfo = methodInfo;
      }

      @Override
      public void visitFormalTypeParameter(String s) {
        finishElement(null);
        methodInfo.generics.add(new TypeParameterInfo(s));
      }

      @Override
      public SignatureVisitor visitInterfaceBound() {
        finishElement(null);
        states.push(State.BOUNDS);
        return this;
      }

      @Override
      public SignatureVisitor visitClassBound() {
        finishElement(null);
        states.push(State.BOUNDS);
        return this;
      }

      @Override
      public SignatureVisitor visitSuperclass() {
        finishElement(null);
        states.push(State.BOUNDS);
        return this;
      }

      @Override
      public SignatureVisitor visitInterface() {
        finishElement(null);
        states.push(State.BOUNDS);
        return this;
      }

      @Override
      public SignatureVisitor visitParameterType() {
        finishElement(null);
        states.push(State.PARAM);
        return this;
      }

      @Override
      public SignatureVisitor visitReturnType() {
        finishElement(null);
        states.push(State.RETURN);
        return this;
      }

      @Override
      public SignatureVisitor visitExceptionType() {
        finishElement(null);
        states.push(State.EXCEPTION);
        return this;
      }

      @Override
      public void visitBaseType(char c) {
        sb.append(org.jetbrains.org.objectweb.asm.Type.getType(String.valueOf(c)).getClassName());
      }

      @Override
      public void visitTypeVariable(String s) {
        sb.append("<").append(s).append(">");
      }

      @Override
      public SignatureVisitor visitArrayType() {
        states.push(State.ARRAY);
        return this;
      }

      @Override
      public void visitClassType(String s) {
        states.push(State.CLASS);
        sb.append(fixClassName(s));
      }

      @Override
      public void visitInnerClassType(String s) {
      }

      @Override
      public void visitTypeArgument() {
        if (states.peekFirst() != State.GENERIC) {
          states.push(State.GENERIC);
          sb.append("<");
        }
        else {
          sb.append(", ");
        }
        sb.append("?");
      }

      @Override
      public SignatureVisitor visitTypeArgument(char c) {
        if (states.peekFirst() != State.GENERIC) {
          states.push(State.GENERIC);
          sb.append("<");
        }
        else {
          sb.append(", ");
        }
        if (c == '+') {
          sb.append("? extends ");
        }
        else if (c == '-') {
          sb.append("? super ");
        }
        return this;
      }

      @Override
      public void visitEnd() {
        finishElement(State.CLASS);
        states.pop();
      }

      private void finishElement(State finishState) {
        if (sb.length() == 0) return;
        main:
        while (!states.isEmpty()) {
          if (finishState == states.peekFirst()) break;
          State state = states.pop();
          switch (state) {
            case PARAM:
              methodInfo.types.add(sb());
              methodInfo.types.add("p" + (methodInfo.types.size() / 2));
              break main;
            case RETURN:
              methodInfo.types.add(0, sb());
              break main;
            case ARRAY:
              sb.append("[]");
              break;
            case GENERIC:
              sb.append(">");
              break;
            case BOUNDS:
              String bound = sb();
              if (!"java.lang.Object".equals(bound)) {
                TypeParameterInfo currentGeneric = ContainerUtil.getLastItem(methodInfo.generics);
                if (currentGeneric != null) {
                  currentGeneric.extendsList.add(bound);
                }
                else {
                  reportException("current generic must not be null");
                }
              }
              break;
            case EXCEPTION:
              methodInfo.exceptions.add(sb());
              break;
            case CLASS:
              break;
          }
        }
      }

      private @NotNull String sb() {
        String s = sb.toString();
        sb.setLength(0);
        return s;
      }
    }
  }

  private static class MyElement<T> extends FakePsiElement implements NavigatablePsiElement {

    final T delegate;

    MyElement(T delegate) {
      this.delegate = delegate;
    }

    @Override
    public PsiElement getParent() {
      return null;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      MyElement<?> element = (MyElement<?>)o;

      if (!delegate.equals(element.delegate)) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return delegate.hashCode();
    }

    @Override
    public String toString() {
      return delegate.toString();
    }
  }

  private static class ClassInfo {
    String name;
    String superClass;
    int modifiers;
    final List<String> typeParameters = new SmartList<>();
    final List<String> interfaces = new SmartList<>();
    final List<String> annotations = new SmartList<>();
    final List<MethodInfo> methods = new SmartList<>();
  }

  private static class MethodInfo {
    MethodType methodType;
    String name;
    String declaringClass;
    int modifiers;
    final List<String> types = new SmartList<>();
    final List<String> annotatedTypes = new SmartList<>();
    final Map<Integer, List<String>> annotations = FactoryMap.create(o -> new SmartList<>());
    final List<TypeParameterInfo> generics = new SmartList<>();
    final List<String> exceptions = new SmartList<>();

    @Override
    public String toString() {
      return "MethodInfo{" + name + types + ", @" + annotations.get(0) + "<" + generics + ">" + " throws " + exceptions + '}';
    }
  }

  public static class TypeParameterInfo {
    private final String name;
    private final List<String> extendsList;
    private final List<String> annotations;

    public TypeParameterInfo(@Nullable String name,
                             @NotNull List<String> extendsList,
                             @NotNull List<String> annotations) {
      this.name = name;
      this.extendsList = extendsList;
      this.annotations = annotations;
    }

    public TypeParameterInfo(@NotNull String name) {
      this(name, new SmartList<>(), new SmartList<>());
    }

    public String getName() {
      return name;
    }

    public List<String> getExtendsList() {
      return extendsList;
    }

    public List<String> getAnnotations() {
      return annotations;
    }
  }
}
