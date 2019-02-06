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

package org.intellij.grammar.java;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.QuickFixFactory;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.ContainerUtilRt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.org.objectweb.asm.*;
import org.jetbrains.org.objectweb.asm.signature.SignatureReader;
import org.jetbrains.org.objectweb.asm.signature.SignatureVisitor;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author gregsh
 */
public abstract class JavaHelper {

  public enum MethodType { STATIC, INSTANCE, CONSTRUCTOR }

  public static JavaHelper getJavaHelper(@NotNull PsiElement context) {
    PsiFile file = context.getContainingFile();
    JavaHelper service = ServiceManager.getService(file.getProject(), JavaHelper.class);
    return service == null ? new AsmHelper() : service;
  }

  @Nullable
  public IntentionAction getCreateClassQuickFix(PsiElement context, String className, boolean intf, String superClass) {
    return null;
  }

  public abstract boolean isPublic(@Nullable NavigatablePsiElement element);

  @Nullable
  public NavigatablePsiElement findClass(@Nullable String className) {
    return null;
  }

  @NotNull
  public List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                      @NotNull MethodType methodType,
                                                      @Nullable String methodName,
                                                      int paramCount,
                                                      String... paramTypes) {
    return Collections.emptyList();
  }

  @Nullable
  public String getSuperClassName(@Nullable String className) {
    return null;
  }

  @NotNull
  public List<String> getMethodTypes(@Nullable NavigatablePsiElement method) {
    return Collections.emptyList();
  }

  public List<TypeParameterInfo> getGenericParameters(NavigatablePsiElement method) {
    return Collections.emptyList();
  }

  public List<String> getExceptionList(NavigatablePsiElement method) {
    return Collections.emptyList();
  }

  @NotNull
  public String getDeclaringClass(@Nullable NavigatablePsiElement method) {
    return "";
  }

  @NotNull
  public List<String> getAnnotations(@Nullable NavigatablePsiElement element) {
    return Collections.emptyList();
  }

  @NotNull
  public List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
    return Collections.emptyList();
  }

  @Nullable
  public PsiReferenceProvider getClassReferenceProvider() {
    return null;
  }

  @Nullable
  public NavigationItem findPackage(@Nullable String packageName) {
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

    private PsiHelper(JavaPsiFacade facade, PsiElementFactory elementFactory) {
      myFacade = facade;
      myElementFactory = elementFactory;
    }

    @Override
    public PsiReferenceProvider getClassReferenceProvider() {
      JavaClassReferenceProvider provider = new JavaClassReferenceProvider();
      provider.setOption(JavaClassReferenceProvider.ALLOW_DOLLAR_NAMES, false);
      provider.setOption(JavaClassReferenceProvider.ADVANCED_RESOLVE, true);
      provider.setOption(JavaClassReferenceProvider.DEFAULT_PACKAGE, CommonClassNames.DEFAULT_PACKAGE);
      provider.setSoft(false);
      return provider;
    }

    @Nullable
    @Override
    public IntentionAction getCreateClassQuickFix(PsiElement context, String className, boolean intf, String superClass) {
      return QuickFixFactory.getInstance().createCreateClassOrInterfaceFix(context, className, !intf, superClass);
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

    @NotNull
    @Override
    public List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                        @NotNull MethodType methodType,
                                                        @Nullable String methodName,
                                                        int paramCount,
                                                        String... paramTypes) {
      if (methodName == null) return Collections.emptyList();
      PsiClass aClass = findClassSafe(className);
      if (aClass == null) return super.findClassMethods(className, methodType, methodName, paramCount, paramTypes);
      List<NavigatablePsiElement> result = ContainerUtil.newArrayList();
      PsiMethod[] methods = methodType == MethodType.CONSTRUCTOR ? aClass.getConstructors() : aClass.getMethods();
      for (PsiMethod method : methods) {
        if (!acceptsName(methodName, method.getName())) continue;
        if (!acceptsMethod(method, methodType)) continue;
        if (!acceptsMethod(myElementFactory, method, paramCount, paramTypes)) continue;
        result.add(method);
      }
      return result;
    }

    @Nullable
    @Override
    public String getSuperClassName(@Nullable String className) {
      PsiClass aClass = findClassSafe(className);
      PsiClass superClass = aClass != null ? aClass.getSuperClass() : null;
      return superClass != null ? superClass.getQualifiedName() : super.getSuperClassName(className);
    }

    private static boolean acceptsMethod(PsiElementFactory elementFactory,
                                         PsiMethod method,
                                         int paramCount,
                                         String... paramTypes) {
      PsiParameterList parameterList = method.getParameterList();
      if (paramCount >= 0 && paramCount != parameterList.getParametersCount()) return false;
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
             !(methodType == MethodType.CONSTRUCTOR && modifierList.hasModifierProperty(PsiModifier.PROTECTED));
    }

    @NotNull
    @Override
    public List<String> getMethodTypes(NavigatablePsiElement method) {
      if (!(method instanceof PsiMethod)) return super.getMethodTypes(method);
      PsiMethod psiMethod = (PsiMethod)method;
      PsiType returnType = psiMethod.getReturnType();
      List<String> strings = new ArrayList<>();
      strings.add(returnType == null ? "" : returnType.getCanonicalText());
      for (PsiParameter parameter : psiMethod.getParameterList().getParameters()) {
        PsiType type = parameter.getType();
        boolean generic = type instanceof PsiClassType && ((PsiClassType)type).resolve() instanceof PsiTypeParameter;
        String typeText = (generic ? "<" : "") + type.getCanonicalText(false) + (generic ? ">" : "");
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
      return ContainerUtil.map(typeParameters, TypeParameterInfo::new);
    }

    @Override
    public List<String> getExceptionList(NavigatablePsiElement method) {
      if (!(method instanceof PsiMethod)) return super.getExceptionList(method);

      PsiMethod psiMethod = (PsiMethod)method;
      PsiClassType[] types = psiMethod.getThrowsList().getReferencedTypes();
      return ContainerUtil.map(types, type -> type.getCanonicalText(false));
    }

    @NotNull
    @Override
    public String getDeclaringClass(@Nullable NavigatablePsiElement method) {
      if (!(method instanceof PsiMethod)) return super.getDeclaringClass(method);
      PsiMethod psiMethod = (PsiMethod)method;
      PsiClass aClass = psiMethod.getContainingClass();
      return aClass == null ? "" : StringUtil.notNullize(aClass.getQualifiedName());
    }

    @NotNull
    @Override
    public List<String> getAnnotations(NavigatablePsiElement element) {
      if (!(element instanceof PsiModifierListOwner)) return super.getAnnotations(element);
      return getAnnotationsInner((PsiModifierListOwner)element);
    }

    @NotNull
    @Override
    public List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
      if (!(method instanceof PsiMethod)) return super.getParameterAnnotations(method, paramIndex);
      PsiMethod psiMethod = (PsiMethod)method;
      PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
      if (paramIndex < 0 || paramIndex >= parameters.length) return Collections.emptyList();
      return getAnnotationsInner(parameters[paramIndex]);
    }

    @NotNull
    private static List<String> getAnnotationsInner(PsiModifierListOwner element) {
      PsiModifierList modifierList = element.getModifierList();
      if (modifierList == null) return ContainerUtilRt.emptyList();
      List<String> result = new ArrayList<>();
      for (PsiAnnotation annotation : modifierList.getAnnotations()) {
        if (annotation.getParameterList().getAttributes().length > 0) continue;
        ContainerUtil.addIfNotNull(result, annotation.getQualifiedName());
      }
      return result;
    }
  }

  public static class ReflectionHelper extends JavaHelper {
    @Override
    public boolean isPublic(@Nullable NavigatablePsiElement element) {
      Object delegate = element instanceof MyElement ? ((MyElement)element).delegate : null;
      int modifiers = delegate instanceof Class ? ((Class)delegate).getModifiers() :
                      delegate instanceof Method ? ((Method)delegate).getModifiers() :
                      0;
      return Modifier.isPublic(modifiers);
    }

    @Nullable
    @Override
    public NavigatablePsiElement findClass(String className) {
      Class<?> aClass = findClassSafe(className);
      return aClass == null ? null : new MyElement<Class>(aClass);
    }

    @Nullable
    private static Class<?> findClassSafe(String className) {
      if (className == null) return null;
      try {
        return Class.forName(className);
      }
      catch (Exception e) {
        return null;
      }
    }

    @NotNull
    @Override
    public List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                        @NotNull MethodType methodType,
                                                        @Nullable String methodName,
                                                        int paramCount,
                                                        String... paramTypes) {
      Class<?> aClass = findClassSafe(className);
      if (aClass == null || methodName == null) return Collections.emptyList();
      List<NavigatablePsiElement> result = ContainerUtil.newArrayList();
      Member[] methods = methodType == MethodType.CONSTRUCTOR ? aClass.getDeclaredConstructors() : aClass.getDeclaredMethods();
      for (Member method : methods) {
        if (!acceptsName(methodName, method.getName())) continue;
        if (!acceptsMethod(method, methodType)) continue;
        if (!acceptsMethod(method, paramCount, paramTypes)) continue;
        result.add(new MyElement<>(method));
      }
      return result;
    }

    @Nullable
    @Override
    public String getSuperClassName(@Nullable String className) {
      Class<?> aClass = findClassSafe(className);
      Class<?> superClass = aClass == null ? null : aClass.getSuperclass();
      return superClass != null && superClass != Object.class ? superClass.getName() : null;
    }

    private static boolean acceptsMethod(Member method, int paramCount, String... paramTypes) {
      Class<?>[] parameterTypes = method instanceof Method? ((Method)method).getParameterTypes() :
                                  method instanceof Constructor ? ((Constructor)method).getParameterTypes() :
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

    @NotNull
    @Override
    public List<String> getMethodTypes(NavigatablePsiElement method) {
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
      return ContainerUtil.map(typeParameters, TypeParameterInfo::new);
    }

    @Override
    public List<String> getExceptionList(NavigatablePsiElement method) {
      if (method == null) return Collections.emptyList();
      Method delegate = ((MyElement<Method>)method).delegate;

      Class<?>[] exceptionTypes = delegate.getExceptionTypes();
      return ContainerUtil.map(exceptionTypes, Class::getName);
    }

    @NotNull
    @Override
    public String getDeclaringClass(@Nullable NavigatablePsiElement method) {
      if (method == null) return "";
      return ((MyElement<Method>)method).delegate.getDeclaringClass().getName();
    }

    @NotNull
    @Override
    public List<String> getAnnotations(NavigatablePsiElement element) {
      if (element == null) return Collections.emptyList();
      AnnotatedElement delegate = ((MyElement<AnnotatedElement>)element).delegate;
      return getAnnotationsInner(delegate);
    }

    @NotNull
    @Override
    public List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
      if (method == null) return Collections.emptyList();
      Method delegate = ((MyElement<Method>)method).delegate;
      AnnotatedType[] parameterTypes = delegate.getAnnotatedParameterTypes();
      if (paramIndex < 0 || paramIndex >= parameterTypes.length) return Collections.emptyList();
      return getAnnotationsInner(delegate);
    }

    @NotNull
    private static List<String> getAnnotationsInner(@NotNull AnnotatedElement delegate) {
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
    @Override
    public boolean isPublic(@Nullable NavigatablePsiElement element) {
      Object delegate = element instanceof MyElement ? ((MyElement)element).delegate : null;
      int access = delegate instanceof ClassInfo ? ((ClassInfo)delegate).modifiers :
                   delegate instanceof MethodInfo ? ((MethodInfo)delegate).modifiers :
                   0;
      return Modifier.isPublic(access);
    }

    @Nullable
    @Override
    public NavigatablePsiElement findClass(String className) {
      ClassInfo info = findClassSafe(className);
      return info == null ? null : new MyElement<>(info);
    }

    @NotNull
    @Override
    public List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                        @NotNull MethodType methodType,
                                                        @Nullable final String methodName,
                                                        int paramCount,
                                                        String... paramTypes) {
      ClassInfo aClass = findClassSafe(className);
      if (aClass == null || methodName == null) return Collections.emptyList();
      List<NavigatablePsiElement> result = ContainerUtil.newArrayList();
      for (MethodInfo method : aClass.methods) {
        if (!acceptsName(methodName, method.name)) continue;
        if (!acceptsMethod(method, methodType)) continue;
        if (!acceptsMethod(method, paramCount, paramTypes)) continue;
        result.add(new MyElement<>(method));
      }
      return result;
    }

    @Nullable
    @Override
    public String getSuperClassName(@Nullable String className) {
      ClassInfo aClass = findClassSafe(className);
      return aClass == null ? null : aClass.superClass;
    }

    private static boolean acceptsMethod(MethodInfo method, int paramCount, String... paramTypes) {
      if (paramCount >= 0 && paramCount + 1 != method.types.size()) return false;
      if (paramTypes.length == 0) return true;
      if (paramTypes.length + 1 > method.types.size()) return false;
      for (int i = 0; i < paramTypes.length; i++) {
        String paramType = paramTypes[i];
        String parameter = method.types.get(i + 1);
        if (acceptsName(paramType, parameter)) continue;
        ClassInfo info = findClassSafe(paramType);
        if (info != null) {
          if (Comparing.equal(info.superClass, parameter)) continue;
          if (info.interfaces.contains(parameter)) continue;
        }
        return false;
      }
      return true;
    }

    private static boolean acceptsMethod(MethodInfo method, MethodType methodType) {
      return method.methodType == methodType && acceptsModifiers(method.modifiers, methodType);
    }

    @NotNull
    @Override
    public List<String> getMethodTypes(NavigatablePsiElement method) {
      Object delegate = method == null ? null : ((MyElement<?>)method).delegate;
      if (!(delegate instanceof MethodInfo)) return Collections.emptyList();
      return ((MethodInfo)delegate).types;
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

    @NotNull
    @Override
    public String getDeclaringClass(@Nullable NavigatablePsiElement method) {
      Object delegate = method == null ? null : ((MyElement<?>)method).delegate;
      if (!(delegate instanceof MethodInfo)) return "";
      return ((MethodInfo)delegate).declaringClass;
    }

    @NotNull
    @Override
    public List<String> getAnnotations(NavigatablePsiElement element) {
      Object delegate = element == null ? null : ((MyElement<?>)element).delegate;
      if (delegate instanceof ClassInfo) return ((ClassInfo)delegate).annotations;
      if (delegate instanceof MethodInfo) return ((MethodInfo)delegate).annotations;
      return Collections.emptyList();
    }

    @NotNull
    @Override
    public List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
      Object delegate = method == null ? null : ((MyElement<?>)method).delegate;
      if (!(delegate instanceof MethodInfo)) return Collections.emptyList();
      Map<Integer, List<String>> annotations = ((MethodInfo)delegate).paramAnnotations;
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
      final ClassInfo info = new ClassInfo();
      info.name = className;
      new ClassReader(bytes).accept(new MyClassVisitor(info), 0);
      return info;
    }

    private static MethodInfo getMethodInfo(String className, String methodName, String signature, String[] exceptions) {
      final MethodInfo methodInfo = new MethodInfo();
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
        super(Opcodes.ASM5);
        myInfo = info;
      }

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
          new SignatureReader(signature).accept(new SignatureVisitor(Opcodes.ASM5) {
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
        final MethodInfo m = getMethodInfo(myInfo.name, name, ObjectUtils.chooseNotNull(signature, desc), exceptions);
        m.modifiers = access;
        m.methodType = "<init>".equals(name)? MethodType.CONSTRUCTOR :
                                Modifier.isStatic(access) ? MethodType.STATIC :
                                MethodType.INSTANCE;
        myInfo.methods.add(m);
        return new MethodVisitor(Opcodes.ASM5) {
          @Override
          public AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
            return new MyAnnotationVisitor() {
              @Override
              public void visitEnd() {
                if (annoParamCounter == 0) {
                  m.annotations.add(fixClassName(desc.substring(1, desc.length() - 1)));
                }
              }
            };
          }

          @Override
          public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
            return new MyAnnotationVisitor() {
              @Override
              public void visitEnd() {
                if (annoParamCounter == 0) {
                  List<String> list = m.paramAnnotations.get(parameter);
                  if (list == null) {
                    m.paramAnnotations.put(parameter, list = ContainerUtil.newSmartList());
                  }
                  list.add(fixClassName(desc.substring(1, desc.length() - 1)));
                }
              }
            };
          }
        };
      }

      class MyAnnotationVisitor extends AnnotationVisitor {
        int annoParamCounter;

        MyAnnotationVisitor() {
          super(Opcodes.ASM5);
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
        super(Opcodes.ASM5);
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
        states.push(State.GENERIC);
        sb.append("<");
      }

      @Override
      public SignatureVisitor visitTypeArgument(char c) {
        if (states.peekFirst() == State.CLASS) {
          states.push(State.GENERIC);
          sb.append("<");
        }
        else {
          finishElement(State.GENERIC);
          sb.append(", ");
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

      @NotNull
      private String sb() {
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

      MyElement element = (MyElement)o;

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
    List<String> typeParameters= ContainerUtil.newSmartList();
    List<String> interfaces = ContainerUtil.newSmartList();
    List<String> annotations = ContainerUtil.newSmartList();
    List<MethodInfo> methods = ContainerUtil.newSmartList();
  }

  private static class MethodInfo {
    MethodType methodType;
    String name;
    String declaringClass;
    int modifiers;
    List<String> annotations = ContainerUtil.newSmartList();
    List<String> types = ContainerUtil.newSmartList();
    Map<Integer, List<String>> paramAnnotations = ContainerUtil.newHashMap(0);
    List<TypeParameterInfo> generics = ContainerUtil.newSmartList();
    List<String> exceptions = ContainerUtil.newSmartList();

    @Override
    public String toString() {
      return "MethodInfo{" + name + types + ", @" + annotations + "<" + generics + ">" + " throws " + exceptions + '}';
    }
  }

  public static class TypeParameterInfo {
    private final String name;
    private final List<String> extendsList;

    public TypeParameterInfo(@NotNull PsiTypeParameter parameter) {
      name = parameter.getName();
      extendsList = ContainerUtil.map(parameter.getExtendsListTypes(), bound -> bound.getCanonicalText(false));
    }

    public TypeParameterInfo(@NotNull TypeVariable<Method> parameter) {
      name = parameter.getName();
      extendsList = ContainerUtil.mapNotNull(parameter.getBounds(), type -> {
        String typeName = type.getTypeName();
        return "java.lang.Object".equals(typeName) ? null : typeName;
      });
    }

    public TypeParameterInfo(@NotNull String name) {
      this.name = name;
      this.extendsList = ContainerUtil.newSmartList();
    }

    public String getName() {
      return name;
    }

    public List<String> getExtendsList() {
      return extendsList;
    }
  }
}
