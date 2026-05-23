/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.asm;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolProvider;
import org.intellij.grammar.classinfo.JvmTypeRef;
import org.intellij.grammar.classinfo.JvmTypeRefs;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.ParameterSymbol;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.intellij.grammar.classinfo.TargetType;
import org.intellij.grammar.classinfo.TypeParameterSymbol;
import org.intellij.grammar.classinfo.TypeProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.org.objectweb.asm.*;
import org.jetbrains.org.objectweb.asm.signature.SignatureReader;
import org.jetbrains.org.objectweb.asm.signature.SignatureVisitor;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Bytecode-backed {@link JvmClassSymbolProvider}. Loads {@code .class} files through a
 * {@link ClassLoader}'s resources and decodes them with ASM into {@link ClassSymbol} records — no
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
  public @NotNull Map<Fqn, ClassSymbol> resolve(@NotNull Fqn fqn, @NotNull SymbolResolver resolver) {
    ClassSymbol info = findClassSafe(fqn, classLoader);
    return info == null ? Map.of() : Map.of(fqn, info);
  }

  public static @Nullable ClassSymbol findClassSafe(@Nullable Fqn className) {
    return findClassSafe(className, AsmClassSymbolProvider.class.getClassLoader());
  }

  public static @Nullable ClassSymbol findClassSafe(@Nullable Fqn className, @NotNull ClassLoader classLoader) {
    if (className == null) return null;
    String name = className.value();
    try {
      int lastDot = name.length();
      InputStream is;
      do {
        String s = name.substring(0, lastDot).replace('.', '/') +
                   name.substring(lastDot).replace('.', '$') +
                   ".class"; // todo looks stupid
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
      return null;
    }
  }

  private static ClassSymbol getClassInfo(Fqn className, byte[] bytes) {
    ClassSymbol.Builder info = new ClassSymbol.Builder();
    info.name = className;
    new ClassReader(bytes).accept(new MyClassVisitor(info), 0);
    return info.build();
  }

  private static MethodSymbol.Builder getMethodInfo(Fqn className, String methodName, String signature, String[] exceptions) {
    MethodSymbol.Builder method = new MethodSymbol.Builder();
    method.name = methodName;
    method.declaringClass = className;

    try {
      MethodSignatureVisitor visitor = new MethodSignatureVisitor(method);
      new SignatureReader(signature).accept(visitor);

      if (exceptions != null) {
        for (String exception : exceptions) {
          method.exceptions.add(Fqn.fromBytecode(exception));
        }
      }
    }
    catch (Exception e) {
      reportException(e, className + "#" + methodName + "()", signature);
    }
    return method;
  }

  private static void reportException(Exception e, String target, String signature) {
    reportException(e.getClass().getSimpleName() + " while reading " + target +
                    (signature == null ? "" : " signature " + signature));
  }

  private static void reportException(String text) {
    //noinspection UseOfSystemOutOrSystemErr
    System.err.println(text);
  }

  // ===============================================================================================
  // Class visitor
  // ===============================================================================================

  private static final String JAVA_LANG_ANNOTATION_TARGET_DESC = "Ljava/lang/annotation/Target;";

  private static class MyClassVisitor extends ClassVisitor {

    private final ClassSymbol.Builder myInfo;

    MyClassVisitor(ClassSymbol.Builder info) {
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
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
      if (!JAVA_LANG_ANNOTATION_TARGET_DESC.equals(desc)) return null;
      return new AnnotationVisitor(ASM_OPCODES) {
        @Override
        public void visitEnum(String unusedName, String enumDesc, String value) {
          recordTarget(value);
        }

        @Override
        public AnnotationVisitor visitArray(String unusedName) {
          return new AnnotationVisitor(ASM_OPCODES) {
            @Override
            public void visitEnum(String n, String enumDesc, String value) {
              recordTarget(value);
            }
          };
        }

        private void recordTarget(String value) {
          try {
            myInfo.annotationTargets.add(TargetType.valueOf(value));
          }
          catch (IllegalArgumentException ignored) {
            // ElementType value we don't model (e.g. a future JDK addition) — skip silently.
          }
        }
      };
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
      MethodSymbol.Builder m = getMethodInfo(myInfo.name, name, ObjectUtils.chooseNotNull(signature, desc), exceptions);
      m.modifiers = access;
      m.methodType = "<init>".equals(name) ? MethodType.CONSTRUCTOR :
                     Modifier.isStatic(access) ? MethodType.STATIC :
                     MethodType.INSTANCE;
      myInfo.methods.add(m);

      /**
       * Pending type annotations to merge after the method visit completes. We can't apply them as
       * they arrive because the order vs declaration-target annotations isn't fixed, and outer-level
       * type annotations (path == null) must be stripped from {@link MethodSymbol.Builder#annotations}
       * / {@link ParameterSymbol.Builder#annotations} only if they actually moved into the type tree.
       */
      class PendingTypeAnno {
        int index;        // 0 for return type; 1+N for parameter N
        Fqn anno;
        TypePath path;    // null for outer-level
      }
      List<PendingTypeAnno> pending = new SmartList<>();
      return new MethodVisitor(ASM_OPCODES) {
        @Override
        public void visitEnd() {
          for (PendingTypeAnno ta : pending) {
            JvmTypeRef prev = ta.index == 0 ? m.returnType : m.parameters.get(ta.index - 1).type;
            JvmTypeRef next = annotateAt(prev, ta.path, ta.anno);
            boolean applied = !next.equals(prev);
            if (ta.index == 0) m.returnType = next;
            else m.parameters.get(ta.index - 1).type = next;
            if (ta.path == null && applied) {
              // Outer-level type annotation that actually landed on the type tree: strip the
              // duplicate from the declaration-target list (visitParameterAnnotation / visitAnnotation
              // already added it). If the annotation could not be placed (e.g. the target position is
              // a TypeVariable or PrimitiveType, which don't carry annotations in the model), keep the
              // declaration-target copy so the annotation isn't lost.
              (ta.index == 0 ? m.annotations : m.parameters.get(ta.index - 1).annotations).remove(ta.anno);
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
              m.annotations.add(anno);
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
              m.parameters.get(parameter).annotations.add(anno);
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
                m.generics.get(typeReference.getTypeParameterIndex()).annotations.add(anno);
              }
              else if (typeReference.getSort() == TypeReference.METHOD_RETURN ||
                       typeReference.getSort() == TypeReference.METHOD_FORMAL_PARAMETER) {
                PendingTypeAnno o = new PendingTypeAnno();
                o.index = typeReference.getSort() == TypeReference.METHOD_RETURN ? 0 : typeReference.getFormalParameterIndex() + 1;
                o.anno = anno;
                o.path = typePath;
                pending.add(o);
              }
              else if (typeReference.getSort() == TypeReference.METHOD_TYPE_PARAMETER_BOUND) {
                List<JvmTypeRef> bounds = m.generics.get(typeReference.getTypeParameterIndex()).extendsList;
                int boundIdx = typeReference.getTypeParameterBoundIndex() - 1;
                if (boundIdx >= 0 && boundIdx < bounds.size()) {
                  bounds.set(boundIdx, annotateAt(bounds.get(boundIdx), typePath, anno));
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

  // ===============================================================================================
  // Type-annotation walker — appends an Fqn to the annotations list at the position the TypePath
  // points to. Returns a new {@link JvmTypeRef} tree with the annotation inserted.
  // ===============================================================================================

  /**
   * Walk {@code root} along {@code path} and append {@code anno} to the annotations of the resolved
   * node. {@code null} path → annotate the root itself.
   * <p>
   * Supported path steps: {@link TypePath#ARRAY_ELEMENT}, {@link TypePath#WILDCARD_BOUND},
   * {@link TypePath#TYPE_ARGUMENT}. {@link TypePath#INNER_TYPE} is currently a no-op (we represent
   * inner types as a flat dotted FQN, so descending past a {@code .} doesn't change the position).
   */
  private static @NotNull JvmTypeRef annotateAt(@NotNull JvmTypeRef root, @Nullable TypePath path, @NotNull Fqn anno) {
    if (path == null) return appendAnnotation(root, anno);
    return walk(root, path, 0, anno);
  }

  private static @NotNull JvmTypeRef walk(@NotNull JvmTypeRef node, @NotNull TypePath path, int step, @NotNull Fqn anno) {
    if (step == path.getLength()) return appendAnnotation(node, anno);
    int kind = path.getStep(step);
    int argIdx = path.getStepArgument(step);
    if (node instanceof JvmTypeRef.ArrayType a) {
      if (kind == TypePath.ARRAY_ELEMENT) {
        return new JvmTypeRef.ArrayType(walk(a.component(), path, step + 1, anno), a.annotations());
      }
      // Path overshoots — skip silently and leave the tree unchanged.
      return a;
    }
    if (node instanceof JvmTypeRef.UserType u) {
      if (kind == TypePath.INNER_TYPE) {
        // We collapse Outer.Inner into a flat dotted Fqn; descending into the inner class doesn't
        // change which JvmTypeRef carries the annotation. Continue at the same node.
        return walk(u, path, step + 1, anno);
      }
      if (kind == TypePath.TYPE_ARGUMENT && argIdx >= 0 && argIdx < u.args().size()) {
        List<TypeProjection> newArgs = new ArrayList<>(u.args());
        TypeProjection target = newArgs.get(argIdx);
        newArgs.set(argIdx, walkProjection(target, path, step + 1, anno));
        return new JvmTypeRef.UserType(u.name(), u.annotations(), newArgs);
      }
      return u;
    }
    // Primitives, type variables, function types, dynamic types can't be descended into.
    return node;
  }

  private static @NotNull TypeProjection walkProjection(@NotNull TypeProjection projection,
                                                        @NotNull TypePath path,
                                                        int step,
                                                        @NotNull Fqn anno) {
    if (projection instanceof TypeProjection.WithVariance wv) {
      // WILDCARD_BOUND is the step that descends into the bound; for invariant projections the next
      // step applies directly to the bound's type position. Treat both the same: descend into wv.type.
      if (step < path.getLength() && path.getStep(step) == TypePath.WILDCARD_BOUND) {
        return new TypeProjection.WithVariance(wv.variance(), walk(wv.type(), path, step + 1, anno));
      }
      return new TypeProjection.WithVariance(wv.variance(), walk(wv.type(), path, step, anno));
    }
    // Star projection — can't annotate.
    return projection;
  }

  private static @NotNull JvmTypeRef appendAnnotation(@NotNull JvmTypeRef node, @NotNull Fqn anno) {
    if (node instanceof JvmTypeRef.UserType u) return new JvmTypeRef.UserType(u.name(), addToList(u.annotations(), anno), u.args());
    if (node instanceof JvmTypeRef.ArrayType a) return new JvmTypeRef.ArrayType(a.component(), addToList(a.annotations(), anno));
    if (node instanceof JvmTypeRef.FunctionType f) return new JvmTypeRef.FunctionType(addToList(f.annotations(), anno));
    if (node instanceof JvmTypeRef.DynamicType d) return new JvmTypeRef.DynamicType(addToList(d.annotations(), anno));
    // Primitives and type variables don't carry annotations in the model.
    return node;
  }

  private static @NotNull List<Fqn> addToList(@NotNull List<Fqn> existing, @NotNull Fqn anno) {
    List<Fqn> out = new ArrayList<>(existing.size() + 1);
    out.addAll(existing);
    out.add(anno);
    return out;
  }

  // ===============================================================================================
  // Signature visitor — builds JvmTypeRef trees for parameters, return type, bounds, exceptions.
  // ===============================================================================================

  private static class MethodSignatureVisitor extends SignatureVisitor {
    private final MethodSymbol.Builder method;

    MethodSignatureVisitor(@NotNull MethodSymbol.Builder method) {
      super(ASM_OPCODES);
      this.method = method;
    }

    @Override
    public void visitFormalTypeParameter(String s) {
      method.generics.add(new TypeParameterSymbol.Builder(s));
    }

    @Override
    public SignatureVisitor visitClassBound() {
      return new TypeRefBuilder(t -> addBound(t));
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
      return new TypeRefBuilder(t -> addBound(t));
    }

    private void addBound(@NotNull JvmTypeRef t) {
      // Skip implicit `extends Object` bounds — kotlinc/javac emit them but they're noise downstream.
      if (t instanceof JvmTypeRef.UserType u && "java.lang.Object".equals(u.name().value()) && u.args().isEmpty()) return;
      TypeParameterSymbol.Builder current = ContainerUtil.getLastItem(method.generics);
      if (current != null) current.extendsList.add(t);
      else reportException("current generic must not be null");
    }

    @Override
    public SignatureVisitor visitParameterType() {
      return new TypeRefBuilder(t -> {
        ParameterSymbol.Builder p = new ParameterSymbol.Builder();
        p.type = t;
        p.name = "p" + method.parameters.size();
        method.parameters.add(p);
      });
    }

    @Override
    public SignatureVisitor visitReturnType() {
      return new TypeRefBuilder(t -> method.returnType = t);
    }

    @Override
    public SignatureVisitor visitExceptionType() {
      return new TypeRefBuilder(t -> method.exceptions.add(JvmTypeRefs.rawFqn(t)));
    }
  }

  /**
   * Builds a single {@link JvmTypeRef} from a SignatureReader sub-stream. Each instance handles one
   * type position; nested type arguments and array elements are delegated to fresh sub-instances
   * that report their result up via the {@code sink} callback.
   */
  private static class TypeRefBuilder extends SignatureVisitor {
    private final Consumer<JvmTypeRef> sink;
    private String classFqn;
    private final List<TypeProjection> args = new SmartList<>();
    private boolean reported;

    TypeRefBuilder(@NotNull Consumer<JvmTypeRef> sink) {
      super(ASM_OPCODES);
      this.sink = sink;
    }

    @Override
    public void visitBaseType(char c) {
      report(new JvmTypeRef.PrimitiveType(Type.getType(String.valueOf(c)).getClassName()));
    }

    @Override
    public void visitTypeVariable(String s) {
      report(new JvmTypeRef.TypeVariable(s));
    }

    @Override
    public SignatureVisitor visitArrayType() {
      return new TypeRefBuilder(component -> report(new JvmTypeRef.ArrayType(component, List.of())));
    }

    @Override
    public void visitClassType(String s) {
      classFqn = Fqn.fromBytecode(s).value();
    }

    @Override
    public void visitInnerClassType(String s) {
      // Existing behaviour: collapse Outer.Inner into the flat Fqn already produced by
      // Fqn.fromBytecode (which mapped `$` → `.`). Signature-style `LOuter.Inner;` decoding would
      // otherwise drop the inner segment. Append it explicitly to keep the FQN dotted.
      classFqn = classFqn == null ? s : classFqn + "." + s;
      args.clear(); // generics from the outer class don't apply to the inner; reset for new args
    }

    @Override
    public void visitTypeArgument() {
      args.add(new TypeProjection.Star());
    }

    @Override
    public SignatureVisitor visitTypeArgument(char c) {
      TypeProjection.Variance variance = c == '+' ? TypeProjection.Variance.OUT
                                       : c == '-' ? TypeProjection.Variance.IN
                                                  : TypeProjection.Variance.INVARIANT;
      return new TypeRefBuilder(inner -> args.add(new TypeProjection.WithVariance(variance, inner)));
    }

    @Override
    public void visitEnd() {
      // Closes the current class type. Primitives / type variables / arrays already reported.
      if (reported || classFqn == null) return;
      report(new JvmTypeRef.UserType(Fqn.of(classFqn), List.of(), List.copyOf(args)));
    }

    private void report(@NotNull JvmTypeRef ref) {
      if (reported) return;
      reported = true;
      sink.accept(ref);
    }
  }
}
