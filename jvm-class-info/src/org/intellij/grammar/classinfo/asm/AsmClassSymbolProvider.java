/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.asm;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolProvider;
import org.intellij.grammar.classinfo.MethodInfo;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.intellij.grammar.classinfo.TypeParameterInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.org.objectweb.asm.*;
import org.jetbrains.org.objectweb.asm.signature.SignatureReader;
import org.jetbrains.org.objectweb.asm.signature.SignatureVisitor;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Bytecode-backed {@link JvmClassSymbolProvider}. Loads {@code .class} files through a
 * {@link ClassLoader}'s resources and decodes them with ASM into {@link ClassInfo} records — no
 * JVM-side class initialisation, which is essential for platform classes that can't be loaded at
 * generation time.
 * <p>
 * Canonical FQN form is dotted source-style: {@code com.foo.Outer.Inner}, never the JVM
 * {@code Outer$Inner}. Bytecode-emitted names are normalised via {@link Fqn#fromBytecode}. On the
 * lookup side, {@link #findClassSafe} walks dotted prefixes right-to-left and tries each
 * {@code /}-vs-{@code $} permutation so a dotted FQN like {@code com.foo.Outer.Inner} resolves
 * either {@code com/foo/Outer/Inner.class} or {@code com/foo/Outer$Inner.class}.
 */
public final class AsmClassSymbolProvider implements JvmClassSymbolProvider {

  private static final int ASM_OPCODES = Opcodes.ASM9;

  private final ClassLoader classLoader;

  public AsmClassSymbolProvider() {
    this(AsmClassSymbolProvider.class.getClassLoader());
  }

  public AsmClassSymbolProvider(@NotNull ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  @Override
  public @NotNull Map<Fqn, ClassInfo> resolve(@NotNull Fqn fqn, @NotNull SymbolResolver resolver) {
    ClassInfo info = findClassSafe(fqn, classLoader);
    return info == null ? Map.of() : Map.of(fqn, info);
  }

  public static @Nullable ClassInfo findClassSafe(@Nullable Fqn className) {
    return findClassSafe(className, AsmClassSymbolProvider.class.getClassLoader());
  }

  public static @Nullable ClassInfo findClassSafe(@Nullable Fqn className, @NotNull ClassLoader classLoader) {
    if (className == null) return null;
    String name = className.value();
    try {
      int lastDot = name.length();
      InputStream is;
      do {
        String s = name.substring(0, lastDot).replace('.', '/') +
                   name.substring(lastDot).replace('.', '$') +
                   ".class";
        is = classLoader.getResourceAsStream(s);
        lastDot = name.lastIndexOf('.', lastDot - 1);
      }
      while (is == null && lastDot > 0);

      if (is == null) return null;
      byte[] bytes = FileUtil.loadBytes(is);
      is.close();
      return getClassInfo(className, bytes);
    }
    catch (Exception e) {
      reportException(e, name, null);
    }
    return null;
  }

  private static ClassInfo getClassInfo(Fqn className, byte[] bytes) {
    ClassInfo info = new ClassInfo();
    info.name = className;
    new ClassReader(bytes).accept(new MyClassVisitor(info), 0);
    return info;
  }

  private static MethodInfo getMethodInfo(Fqn className, String methodName, String signature, String[] exceptions) {
    MethodInfo methodInfo = new MethodInfo();
    methodInfo.name = methodName;
    methodInfo.declaringClass = className;

    try {
      MySignatureVisitor visitor = new MySignatureVisitor(methodInfo);
      new SignatureReader(signature).accept(visitor);
      visitor.finishElement(null);

      if (exceptions != null) {
        for (String exception : exceptions) {
          methodInfo.exceptions.add(Fqn.fromBytecode(exception));
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
      myInfo.superClass = superName == null ? null : Fqn.fromBytecode(superName);
      for (String s : interfaces) {
        myInfo.interfaces.add(Fqn.fromBytecode(s));
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
        Fqn anno;
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
              plainOffsets[idx] += 2 + ta.anno.value().length();
            }
            m.annotatedTypes.set(idx, newType);
            if (isArray || bracketIdx < 1) {
              m.annotations.get(ta.index).remove(ta.anno);
            }
          }
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
          Fqn anno = Fqn.fromBytecode(desc.substring(1, desc.length() - 1));
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
          Fqn anno = Fqn.fromBytecode(desc.substring(1, desc.length() - 1));
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
          Fqn anno = Fqn.fromBytecode(desc.substring(1, desc.length() - 1));
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
                List<String> bounds = m.generics.get(typeReference.getTypeParameterIndex()).getExtendsList();
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
    final Deque<State> states = new ArrayDeque<>();

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
      sb.append(Type.getType(String.valueOf(c)).getClassName());
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
      sb.append(Fqn.fromBytecode(s).value());
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

    void finishElement(State finishState) {
      if (sb.isEmpty()) return;
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
                currentGeneric.getExtendsList().add(bound);
              }
              else {
                reportException("current generic must not be null");
              }
            }
            break;
          case EXCEPTION:
            methodInfo.exceptions.add(Fqn.of(sb()));
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
