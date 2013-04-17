/*
 * Copyright 2011-2013 Gregory Shrago
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

import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author gregsh
 */
public class JavaHelper {
  public static JavaHelper getJavaHelper(Project project) {
    JavaHelper service = ServiceManager.getService(project, JavaHelper.class);
    return service == null? new JavaHelper() : service;
  }

  @Nullable
  public PsiReferenceProvider getClassReferenceProvider() { return null; }
  @Nullable
  public NavigatablePsiElement findClass(@Nullable String className) { return null; }
  @Nullable
  public NavigationItem findPackage(@Nullable String packageName) { return null; }
  @Nullable
  public NavigatablePsiElement findClassMethod(String className, String methodName, int paramCount) { return null; }
  @NotNull
  public List<NavigatablePsiElement> getClassMethods(String className, boolean staticMethods) { return Collections.emptyList(); }
  @NotNull
  public List<String> getMethodTypes(@Nullable NavigatablePsiElement method) { return Collections.singletonList("void"); }
  @NotNull
  public List<String> getAnnotations(@Nullable NavigatablePsiElement element) { return Collections.emptyList(); }

  private static class Impl extends JavaHelper {
    private final JavaPsiFacade myFacade;

    private Impl(JavaPsiFacade facade) {
      myFacade = facade;
    }

    @Override
    public PsiReferenceProvider getClassReferenceProvider() {
      JavaClassReferenceProvider provider = new JavaClassReferenceProvider();
      provider.setSoft(false);
      return provider;
    }

    @Override
    public PsiClass findClass(String className) {
      if (className == null) return null;
      return myFacade.findClass(className, GlobalSearchScope.allScope(myFacade.getProject()));
    }

    @Override
    public NavigationItem findPackage(String packageName) {
      return myFacade.findPackage(packageName);
    }

    @Override
    public PsiMethod findClassMethod(String className, String methodName, int paramCount) {
      PsiClass aClass = findClass(className);
      PsiMethod[] methods = aClass == null? PsiMethod.EMPTY_ARRAY : aClass.findMethodsByName(methodName, true);
      for (PsiMethod method : methods) {
        if (paramCount < 0 || paramCount + 2 == method.getParameterList().getParametersCount()) {
          return method;
        }
      }
      return methods.length > 0 ? methods[0] : null;
    }

    @NotNull
    @Override
    public List<NavigatablePsiElement> getClassMethods(String className, boolean staticMethods) {
      PsiClass aClass = findClass(className);
      if (aClass == null) return Collections.emptyList();
      final ArrayList<NavigatablePsiElement> result = new ArrayList<NavigatablePsiElement>();
      for (PsiMethod method : aClass.getAllMethods()) {
        PsiModifierList modifierList = method.getModifierList();
        if (modifierList.hasExplicitModifier(PsiModifier.PUBLIC) &&
            staticMethods == modifierList.hasExplicitModifier(PsiModifier.STATIC)) {
          result.add(method);
        }
      }
      return result;
    }

    @NotNull
    @Override
    public List<String> getMethodTypes(NavigatablePsiElement method) {
      if (method == null) return Collections.emptyList();
      PsiMethod psiMethod = (PsiMethod)method;
      PsiType returnType = psiMethod.getReturnType();
      List<String> strings = new ArrayList<String>();
      strings.add(returnType == null? "" : returnType.getCanonicalText());
      for (PsiParameter parameter : psiMethod.getParameterList().getParameters()) {
        strings.add(parameter.getType().getCanonicalText());
        strings.add(parameter.getName());
      }
      return strings;
    }

    @NotNull
    @Override
    public List<String> getAnnotations(NavigatablePsiElement element) {
      if (element == null) return Collections.emptyList();
      PsiModifierList modifierList = ((PsiModifierListOwner)element).getModifierList();
      if (modifierList == null) return super.getAnnotations(element);
      List<String> strings = new ArrayList<String>();
      for (PsiAnnotation annotation  : modifierList.getAnnotations()) {
        if (annotation.getParameterList().getAttributes().length > 0) continue;
        strings.add(annotation.getQualifiedName());
      }
      return strings;
    }
  }

  public static class ReflectionHelper extends JavaHelper {
    @Nullable
    @Override
    public NavigatablePsiElement findClass(String className) {
      if (className == null) return null;
      try {
        Class<?> aClass = Class.forName(className);
        return new MyElement<Class>(aClass);
      }
      catch (ClassNotFoundException e) {
        return null;
      }
    }

    @Nullable
    @Override
    public NavigatablePsiElement findClassMethod(String className, String methodName, int paramCount) {
      if (className == null) return null;
      try {
        Class<?> aClass = Class.forName(className);
        for (Method method : aClass.getDeclaredMethods()) {
          if (!method.getName().equals(methodName)) continue;
          if (paramCount < 0 || paramCount + 2 == method.getParameterTypes().length) {
            return new MyElement<Method>(method);
          }
        }
        return null;
      }
      catch (ClassNotFoundException e) {
        return null;
      }
    }

    @NotNull
    @Override
    public List<String> getMethodTypes(NavigatablePsiElement method) {
      if (method == null) return Collections.emptyList();
      Method delegate = ((MyElement<Method>) method).myDelegate;
      Type[] parameterTypes = delegate.getGenericParameterTypes();
      ArrayList<String> result = new ArrayList<String>(parameterTypes.length + 1);
      result.add(delegate.getGenericReturnType().toString());
      int paramCounter = 0;
      for (Type parameterType : parameterTypes) {
        result.add(parameterType.toString());
        result.add("p" + (paramCounter ++));
      }
      return result;
    }

    @NotNull
    @Override
    public List<String> getAnnotations(NavigatablePsiElement element) {
      if (element == null) return Collections.emptyList();
      AnnotatedElement delegate = ((MyElement<AnnotatedElement>) element).myDelegate;
      Annotation[] annotations = delegate.getDeclaredAnnotations();
      ArrayList<String> result = new ArrayList<String>(annotations.length);
      for (Annotation annotation : annotations) {
        Class<? extends Annotation> annotationType = annotation.annotationType(); // todo parameters?
        result.add(annotationType.getCanonicalName());
      }
      return result;
    }
  }

  public static class AsmHelper extends JavaHelper {
    @Nullable
    @Override
    public NavigatablePsiElement findClass(String className) {
      if (className == null) return null;
      try {
        InputStream is = getClass().getClassLoader().getResourceAsStream(className.replace('.', '/') + ".class");
        if (is == null) return null;
        byte[] bytes = FileUtil.loadBytes(is);
        is.close();
        ClassInfo info = getClassInfo(className, bytes);
        return new MyElement<ClassInfo>(info);
      }
      catch (IOException e) {
        return null;
      }
    }

    @Nullable
    @Override
    public NavigatablePsiElement findClassMethod(String className, final String methodName, int paramCount) {
      MyElement<ClassInfo> classElement = className == null? null : (MyElement<ClassInfo>)findClass(className);
      ClassInfo aClass = classElement == null? null : classElement.myDelegate;
      if (aClass == null) return null;
      for (MethodInfo method : aClass.methods) {
        if (!method.name.equals(methodName)) continue;
        if (paramCount < 0 || paramCount + 2 == method.types.size()) {
          return new MyElement<MethodInfo>(method);
        }
      }
      return null;
    }

    @NotNull
    @Override
    public List<String> getMethodTypes(NavigatablePsiElement method) {
      if (method == null) return Collections.emptyList();
      MethodInfo signature = ((MyElement<MethodInfo>) method).myDelegate;
      return signature.types;
    }

    @NotNull
    @Override
    public List<String> getAnnotations(NavigatablePsiElement element) {
      Object delegate = element == null? null : ((MyElement<?>) element).myDelegate;
      if (delegate instanceof ClassInfo) return ((ClassInfo) delegate).annotations;
      if (delegate instanceof MethodInfo) return ((MethodInfo) delegate).annotations;
      return Collections.emptyList();
    }

    private static ClassInfo getClassInfo(String className, byte[] bytes) {
      final ClassInfo info = new ClassInfo();
      info.name = className;
      new ClassReader(bytes).accept(new MyClassVisitor(info), 0);
      return info;
    }

    private static MethodInfo getMethodInfo(String name, String signature) {
      final MethodInfo methodInfo = new MethodInfo();
      methodInfo.name = name;

      MySignatureVisitor visitor = new MySignatureVisitor(methodInfo);
      new SignatureReader(signature).accept(visitor);
      visitor.finishElement(null);
      return methodInfo;
    }

    private static class MyClassVisitor extends EmptyVisitor {
      enum State {CLASS, METHOD, ANNO }

      private final ClassInfo myInfo;

      public MyClassVisitor(ClassInfo info) {
        myInfo = info;
        state = State.CLASS;
      }

      private State state;

      private MethodInfo methodInfo;
      private String annoDesc;
      private int annoParamCounter;

      @Override
      public void visitEnd() {
        if (state == State.METHOD) {
          state = State.CLASS;
          myInfo.methods.add(methodInfo);
          methodInfo = null;
        }
        else if (state == State.ANNO) {
          state = State.METHOD;
          if (annoParamCounter == 0) {
            methodInfo.annotations.add(annoDesc.substring(1, annoDesc.length()-1).replace('/', '.'));
          }
          annoParamCounter = 0;
          annoDesc = null;
        }
      }

      @Override
      public MethodVisitor visitMethod(int access,
                                       String name,
                                       String desc,
                                       String signature,
                                       String[] exceptions) {
        state = State.METHOD;
        methodInfo = getMethodInfo(name, ObjectUtils.chooseNotNull(signature, desc));
        return this; // visit annotations
      }

      @Override
      public AnnotationVisitor visitAnnotation(String s, boolean b) {
        if (state == State.METHOD) {
          state = State.ANNO;
          annoDesc = s;
          return this;
        }
        return null;
      }

      @Override
      public void visit(String s, Object o) {
        annoParamCounter ++;
      }

      @Override
      public void visitEnum(String s, String s2, String s3) {
        annoParamCounter ++;
      }

      @Override
      public AnnotationVisitor visitAnnotation(String s, String s2) {
        annoParamCounter ++;
        return null;
      }

      @Override
      public AnnotationVisitor visitArray(String s) {
        annoParamCounter ++;
        return null;
      }
    }

    private static class MySignatureVisitor implements SignatureVisitor {
      enum State {PARAM, RETURN, CLASS, ARRAY, GENERIC}

      private final MethodInfo myMethodInfo;
      private final LinkedList<State> states = new LinkedList<State>();

      private final StringBuilder myBuilder = new StringBuilder();

      public MySignatureVisitor(MethodInfo methodInfo) {
        myMethodInfo = methodInfo;
      }

      @Override
      public void visitFormalTypeParameter(String s) {
      }

      @Override
      public SignatureVisitor visitClassBound() {
        return null;
      }

      @Override
      public SignatureVisitor visitInterfaceBound() {
        return this;
      }

      @Override
      public SignatureVisitor visitSuperclass() {
        return null;
      }

      @Override
      public SignatureVisitor visitInterface() {
        return null;
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
        return null;
      }

      @Override
      public void visitBaseType(char c) {
        myBuilder.append(org.objectweb.asm.Type.getType(String.valueOf(c)).getClassName());
      }

      @Override
      public void visitTypeVariable(String s) {
      }

      @Override
      public SignatureVisitor visitArrayType() {
        states.push(State.ARRAY);
        return this;
      }

      @Override
      public void visitClassType(String s) {
        states.push(State.CLASS);
        myBuilder.append(s.replace('/', '.'));
      }

      @Override
      public void visitInnerClassType(String s) {
      }

      @Override
      public void visitTypeArgument() {
        states.push(State.GENERIC);
        myBuilder.append("<");
      }

      @Override
      public SignatureVisitor visitTypeArgument(char c) {
        if (states.peekFirst() == State.CLASS) {
          states.push(State.GENERIC);
          myBuilder.append("<");
        }
        else {
          finishElement(State.GENERIC);
          myBuilder.append(", ");
        }
        return this;
      }

      @Override
      public void visitEnd() {
        finishElement(State.CLASS);
        states.pop();
      }

      private void finishElement(State finishState) {
        if (myBuilder.length() == 0) return;
        main: while (!states.isEmpty()) {
          if (finishState == states.peekFirst()) break;
          State state = states.pop();
          switch (state) {
            case PARAM:
              myMethodInfo.types.add(myBuilder.toString());
              myMethodInfo.types.add("p"+(myMethodInfo.types.size() / 2));
              myBuilder.setLength(0);
              break main;
            case RETURN:
              myMethodInfo.types.add(0, myBuilder.toString());
              myBuilder.setLength(0);
              break main;
            case ARRAY:
              myBuilder.append("[]");
              break;
            case GENERIC:
              myBuilder.append(">");
              break;
          }
        }
      }
    }
  }

  private static class MyElement<T> extends FakePsiElement implements NavigatablePsiElement {

    private final T myDelegate;

    public MyElement(T delegate) {
      myDelegate = delegate;
    }

    @Override
    public PsiElement getParent() {
      return null;
    }
  }

  private static class ClassInfo {
    String name;
    List<String> annotations = new ArrayList<String>(0);
    List<MethodInfo> methods = new ArrayList<MethodInfo>(0);
  }

  private static class MethodInfo {
    String name;
    List<String> annotations = new ArrayList<String>(0);
    List<String> types = new ArrayList<String>(0);
  }

}
