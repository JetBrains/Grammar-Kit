/*
 * Copyright 2011-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.compiler;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.FakePsiElement;
import com.sun.source.tree.ImportTree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import org.intellij.grammar.java.JavaHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor9;
import javax.lang.model.util.SimpleTypeVisitor9;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JavacJavaHelper extends JavaHelper {
  private final ProcessingEnvironment myEnv;
  private final Trees myTrees;
  private final Elements myElements;
  private final JavaCompiler myCompiler;

  public JavacJavaHelper(@NotNull ProcessingEnvironment processingEnv) {
    myEnv = processingEnv;
    myTrees = Trees.instance(processingEnv);
    myElements = myEnv.getElementUtils();
    myCompiler = JavaCompiler.instance(((JavacProcessingEnvironment)processingEnv).getContext());
  }

  @Nullable TypeElement findTypeElement(@Nullable String className) {
    return myCompiler.isEnterDone() ? myElements.getTypeElement(className) : null;
  }

  @Override
  public boolean isPublic(@Nullable NavigatablePsiElement element) {
    return accept(element, new SimpleElementVisitor9<Boolean, Void>() {
      @Override
      public Boolean visitType(TypeElement e, Void unused) {
        return e.getModifiers().contains(Modifier.PUBLIC);
      }

      @Override
      public Boolean visitExecutable(ExecutableElement e, Void unused) {
        return e.getModifiers().contains(Modifier.PUBLIC);
      }

      @Override
      protected Boolean defaultAction(Element e, Void unused) {
        return false;
      }
    });
  }

  @Override
  public @Nullable NavigatablePsiElement findClass(@Nullable String className) {
    TypeElement element = findTypeElement(className);
    return element == null ? null : new MyElement(element);
  }

  @Override
  public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                               @NotNull MethodType methodType,
                                                               @Nullable String methodName,
                                                               int paramCount, String... paramTypes) {
    if (className == null || methodName == null) {
      return Collections.emptyList();
    }
    else {
      TypeElement element = findTypeElement(className);
      if (element == null) {
        return Collections.emptyList();
      }
      else {
        return myElements.getAllMembers(element).stream()
          .filter(e -> matches(methodType, methodName, paramCount, paramTypes, e))
          .map(MyElement::new)
          .collect(Collectors.toList());
      }
    }
  }

  @Override
  public @Nullable String getSuperClassName(@Nullable String className) {
    if (className == null) {
      return null;
    }
    else {
      TypeElement element = findTypeElement(className);
      if (element == null) {
        return null;
      }
      if (element.getKind() == ElementKind.INTERFACE ||
          element.getKind() == ElementKind.ANNOTATION_TYPE) {
        return "java.lang.Object";
      }
      else {
        TypeMirror superClass = element.getSuperclass();
        return superClass.getKind() == TypeKind.NONE ? null : resolveFirst(element, superClass);
      }
    }
  }

  @Override
  public @NotNull List<String> getMethodTypes(@Nullable NavigatablePsiElement method) {
    return accept(method, simpleVisitor(Collections.emptyList(), e -> {
      List<String> result = new ArrayList<>();
      result.add(resolveFirst(e, e.getReturnType()));
      for (int i = 0; i < e.getParameters().size(); ++i) {
        VariableElement param = e.getParameters().get(i);
        result.add(resolveFirst(e, param.asType()));
        result.add(param.getSimpleName().toString());
      }
      return result;
    }));
  }

  @Override
  public List<TypeParameterInfo> getGenericParameters(NavigatablePsiElement method) {
    return accept(method, simpleVisitor(Collections.emptyList(), e ->
      e.getTypeParameters().stream()
        .map(JavacJavaHelper.this::toTypeParameterInfo)
        .collect(Collectors.toList())));
  }

  @Override
  public List<String> getExceptionList(NavigatablePsiElement method) {
    return accept(method, simpleVisitor(Collections.emptyList(), e ->
      e.getThrownTypes().stream()
        .map(type -> resolveFirst(e, type))
        .collect(Collectors.toList())));
  }

  @Override
  public @NotNull String getDeclaringClass(@Nullable NavigatablePsiElement method) {
    return accept(method, simpleVisitor("", e ->
      e.accept(new SimpleElementVisitor9<String, Void>() {
        @Override
        public String visitType(TypeElement e, Void unused) {
          return e.getQualifiedName().toString();
        }

        @Override
        protected String defaultAction(Element e, Void unused) {
          return e.getEnclosingElement().accept(this, null);
        }
      }, null)));
  }

  @Override
  public @NotNull List<String> getAnnotations(@Nullable NavigatablePsiElement element) {
    return Collections.emptyList();
  }

  @Override
  public @NotNull List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
    return Collections.emptyList();
  }

  private boolean matches(@NotNull MethodType methodType,
                          @NotNull String methodName,
                          int paramCount,
                          String @NotNull [] paramTypes,
                          @NotNull Element element) {
    return element.accept(new SimpleElementVisitor9<Boolean, Void>() {
      @Override
      public Boolean visitExecutable(ExecutableElement e, Void o) {
        Name expectedName = myElements.getName(methodName);
        return !e.getModifiers().contains(Modifier.ABSTRACT) &&
               (methodType != MethodType.CONSTRUCTOR || e.getKind() == ElementKind.CONSTRUCTOR) &&
               (methodType != MethodType.CONSTRUCTOR || !e.getModifiers().contains(Modifier.PRIVATE)) &&
               (methodType != MethodType.INSTANCE || e.getKind() == ElementKind.METHOD) &&
               (methodType != MethodType.INSTANCE || !e.getModifiers().contains(Modifier.STATIC)) &&
               (methodType != MethodType.STATIC || e.getKind() == ElementKind.METHOD) &&
               (methodType != MethodType.STATIC || e.getModifiers().contains(Modifier.STATIC)) &&
               ("*".equals(methodName) || e.getSimpleName().equals(expectedName)) &&
               matchParameters(paramCount, paramTypes, e);
      }

      @Override
      protected Boolean defaultAction(Element e, Void o) {
        return false;
      }
    }, null);
  }

  private boolean matchParameters(int paramCount, String @NotNull [] paramTypes, @NotNull ExecutableElement method) {
    boolean varArgs = method.isVarArgs();
    List<? extends VariableElement> parameters = method.getParameters();
    if (paramCount >= 0 && !varArgs && paramCount != parameters.size() ||
        paramCount >= 0 && paramCount < parameters.size() - 1 ||
        parameters.size() < paramTypes.length) {
      return false;
    }
    for (int i = 0; i < paramTypes.length; ++i) {
      String expectedType = paramTypes[i];
      VariableElement parameter = parameters.get(i);
      TypeMirror parameterType = parameter.asType();
      if (!"*".equals(expectedType) &&
          !isAssignable(method, expectedType, parameterType)) {
        return false;
      }
    }
    return true;
  }

  private boolean isAssignable(@NotNull ExecutableElement context, @NotNull String from, @NotNull TypeMirror to) {
    return to.accept(new SimpleTypeVisitor9<Boolean, Void>() {
      @Override
      public Boolean visitError(ErrorType t, Void unused) {
        return resolve(context, t).stream()
          .anyMatch(from::equals);
      }

      @Override
      protected Boolean defaultAction(TypeMirror e, Void unused) {
        if (e.toString().equals(from)) {
          return true;
        }
        else {
          TypeElement fromElement = findTypeElement(from);
          return fromElement != null && myEnv.getTypeUtils().isAssignable(fromElement.asType(), e);
        }
      }
    }, null);
  }

  private @NotNull String resolveFirst(@NotNull Element context, @NotNull TypeMirror type) {
    return resolve(context, type).stream().findFirst().orElseThrow();
  }

  private @NotNull Collection<String> resolve(@NotNull Element context, @NotNull TypeMirror type) {
    return type.accept(new SimpleTypeVisitor9<Collection<String>, Void>() {
      @Override
      public Collection<String> visitError(ErrorType t, Void unused) {
        String typeName = t.toString();
        String[] typeParts = typeName.split("\\.", 2);
        List<String> specific = getImports(context, typeParts[0])
          .collect(Collectors.toUnmodifiableList());
        if (specific.isEmpty()) {
          String pkg = myElements.getPackageOf(context)
            .getQualifiedName().toString();
          return Stream
            .concat(
              pkg.isEmpty()
              ? Stream.of(typeName)
              : Stream.of(typeName, pkg + "." + typeName),
              getImports(context, "*")
                .map(im -> im.replace("*", typeName)))
            .collect(Collectors.toUnmodifiableList());
        }
        else if (typeParts.length > 1) {
          assert typeParts.length == 2;
          return specific.stream()
            .map(im -> im + "." + typeParts[1])
            .collect(Collectors.toUnmodifiableList());
        }
        else {
          return specific;
        }
      }

      @Override
      protected Collection<String> defaultAction(TypeMirror e, Void unused) {
        return Collections.singleton(e.toString());
      }
    }, null);
  }

  private @NotNull Stream<String> getImports(@NotNull Element context, @NotNull String name) {
    return myTrees.getPath(context).getCompilationUnit().getImports().stream()
      .filter(Predicate.not(ImportTree::isStatic))
      .map(im -> im.getQualifiedIdentifier().toString())
      .filter(im -> im.endsWith("." + name));
  }

  private @NotNull TypeParameterInfo toTypeParameterInfo(@NotNull TypeParameterElement typeParameter) {
    try {
      String parameterName = typeParameter.getSimpleName().toString();
      TypeParameterInfo info = TypeParameterInfo.class.getConstructor(String.class)
        .newInstance(parameterName);
      for (TypeMirror bounds : typeParameter.getBounds()) {
        info.getExtendsList().add(resolveFirst(typeParameter, bounds));
      }
      return info;
    }
    catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  private static <T> SimpleElementVisitor9<T, Void> simpleVisitor(T def, Function<ExecutableElement, T> function) {
    return new SimpleElementVisitor9<>() {
      @Override
      public T visitExecutable(ExecutableElement e, Void unused) {
        return function.apply(e);
      }

      @Override
      protected T defaultAction(Element e, Void unused) {
        return def;
      }
    };
  }

  private static <R, P> R accept(@Nullable NavigatablePsiElement element, @NotNull ElementVisitor<R, P> visitor) {
    Element e = getElement(element);
    if (e == null) {
      return visitor.visitUnknown(null, null);
    }
    else {
      return e.accept(visitor, null);
    }
  }

  @Contract("null -> null")
  private static @Nullable Element getElement(NavigatablePsiElement element) {
    return element instanceof MyElement ? ((MyElement)element).delegate : null;
  }

  private static class MyElement extends FakePsiElement implements NavigatablePsiElement {
    final Element delegate;

    private MyElement(Element delegate) {
      this.delegate = delegate;
    }

    @Override
    public PsiElement getParent() {
      return new MyElement(delegate.getEnclosingElement());
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      else if (o != null && this.getClass() == o.getClass()) {
        MyElement element = (MyElement)o;
        return this.delegate.equals(element.delegate);
      }
      else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return this.delegate.hashCode();
    }

    @Override
    public String toString() {
      return this.delegate.toString();
    }
  }
}