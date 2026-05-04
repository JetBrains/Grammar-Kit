/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.util.ObjectUtils;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.generator.java.JavaNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.org.objectweb.asm.*;
import org.jetbrains.org.objectweb.asm.signature.SignatureReader;
import org.jetbrains.org.objectweb.asm.signature.SignatureVisitor;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * {@link JavaHelper} backed by ASM bytecode parsing.
 * <p>
 * Loads {@code .class} files through the current classloader's resources and extracts class /
 * method information without needing the JVM to actually load the class — important because some
 * referenced classes (e.g. those that depend on the IDE platform) cannot be initialised at
 * generation time. Method signatures, generics, {@code throws} clauses and both regular and type
 * annotations are decoded into {@link ClassInfo} / {@link MethodInfo} records and wrapped in
 * {@link MyElement}s so they look like {@link NavigatablePsiElement}s to callers.
 * <p>
 * This is the default helper outside the IDE (returned by
 * {@link JavaHelper#getJavaHelper(com.intellij.psi.PsiElement)} when no project service is
 * available) and the base class of {@link PsiHelper}, which delegates back to the bytecode lookup
 * whenever the PSI cannot resolve a class — typically for platform classes that are on the
 * classpath but not part of the project's source roots.
 */
public class AsmHelper extends JavaHelper {

  private static final int ASM_OPCODES = Opcodes.ASM9;

  private static boolean acceptsMethod(MethodInfo method, int paramCount, String... paramTypes) {
    if (paramCount >= 0 && paramCount != (method.types.size() - 1) / 2) return false;
    if (paramTypes.length == 0) return true;
    if (paramTypes.length > (method.types.size() - 1) / 2) return false;
    for (int i = 0; i < paramTypes.length; i++) {
      String paramType = paramTypes[i];
      String parameter = method.types.get(2 * i + 1);
      if (acceptsName(paramType, JavaNames.getRawClassName(parameter))) continue;
      ClassInfo info = findClassSafe(paramType);
      if (info != null) {
        if (Objects.equals(info.superClass, parameter)) continue;
        if (info.interfaces.contains(parameter)) continue;
      }
      return false;
    }
    return true;
  }

  private static boolean acceptsMethod(MethodInfo method, MethodType methodType, boolean allowAbstract) {
    return method.methodType == methodType && acceptsModifiers(method.modifiers, methodType, allowAbstract);
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
      while (is == null && lastDot > 0);

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

  private static String fixClassName(String s) {
    return s == null ? null : s.replace('/', '.').replace('$', '.');
  }

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
                                                               boolean allowAbstract,
                                                               int paramCount,
                                                               String... paramTypes) {
    ClassInfo aClass = findClassSafe(className);
    if (aClass == null || methodName == null) return Collections.emptyList();
    List<NavigatablePsiElement> result = new ArrayList<>();
    for (MethodInfo method : aClass.methods) { // todo super methods too
      if (!acceptsName(methodName, method.name)) continue;
      if (!acceptsMethod(method, methodType, allowAbstract)) continue;
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
    List<String> result = annotations.get(paramIndex + 1);
    return result == null ? Collections.emptyList() : result;
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
      m.methodType = "<init>".equals(name) ? MethodType.CONSTRUCTOR :
                     Modifier.isStatic(access) ? MethodType.STATIC :
                     MethodType.INSTANCE;
      myInfo.methods.add(m);
      class ParamTypeAnno {
        int index;
        String anno;
        TypePath path;
      }
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
          return new MyClassVisitor.MyAnnotationVisitor() {
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
          return new MyClassVisitor.MyAnnotationVisitor() {
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
          return new MyClassVisitor.MyAnnotationVisitor() {
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

  private static class MySignatureVisitor extends SignatureVisitor {
    final MethodInfo methodInfo;
    final Deque<MySignatureVisitor.State> states = new ArrayDeque<>();

    /**
     * @noinspection StringBufferField
     */
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
      states.push(MySignatureVisitor.State.BOUNDS);
      return this;
    }

    @Override
    public SignatureVisitor visitClassBound() {
      finishElement(null);
      states.push(MySignatureVisitor.State.BOUNDS);
      return this;
    }

    @Override
    public SignatureVisitor visitSuperclass() {
      finishElement(null);
      states.push(MySignatureVisitor.State.BOUNDS);
      return this;
    }

    @Override
    public SignatureVisitor visitInterface() {
      finishElement(null);
      states.push(MySignatureVisitor.State.BOUNDS);
      return this;
    }

    @Override
    public SignatureVisitor visitParameterType() {
      finishElement(null);
      states.push(MySignatureVisitor.State.PARAM);
      return this;
    }

    @Override
    public SignatureVisitor visitReturnType() {
      finishElement(null);
      states.push(MySignatureVisitor.State.RETURN);
      return this;
    }

    @Override
    public SignatureVisitor visitExceptionType() {
      finishElement(null);
      states.push(MySignatureVisitor.State.EXCEPTION);
      return this;
    }

    @Override
    public void visitBaseType(char c) {
      sb.append(Type.getType(String.valueOf(c)).getClassName());
    }

    @Override
    public void visitTypeVariable(String s) {
      sb.append("<").append(s).append(">");
    }

    @Override
    public SignatureVisitor visitArrayType() {
      states.push(MySignatureVisitor.State.ARRAY);
      return this;
    }

    @Override
    public void visitClassType(String s) {
      states.push(MySignatureVisitor.State.CLASS);
      sb.append(fixClassName(s));
    }

    @Override
    public void visitInnerClassType(String s) {
    }

    @Override
    public void visitTypeArgument() {
      if (states.peekFirst() != MySignatureVisitor.State.GENERIC) {
        states.push(MySignatureVisitor.State.GENERIC);
        sb.append("<");
      }
      else {
        sb.append(", ");
      }
      sb.append("?");
    }

    @Override
    public SignatureVisitor visitTypeArgument(char c) {
      if (states.peekFirst() != MySignatureVisitor.State.GENERIC) {
        states.push(MySignatureVisitor.State.GENERIC);
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
      finishElement(MySignatureVisitor.State.CLASS);
      states.pop();
    }

    private void finishElement(MySignatureVisitor.State finishState) {
      if (sb.isEmpty()) return;
      main:
      while (!states.isEmpty()) {
        if (finishState == states.peekFirst()) break;
        MySignatureVisitor.State state = states.pop();
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

    enum State {PARAM, RETURN, CLASS, ARRAY, GENERIC, BOUNDS, EXCEPTION}
  }
}
