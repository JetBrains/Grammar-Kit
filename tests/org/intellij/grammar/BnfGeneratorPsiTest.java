/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.ProcessingContext;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for PSI code generation with PsiHelper (PSI-based Java resolution).
 * <p>
 * Unlike {@link BnfGeneratorTest} which uses lightweight parsing tests (and thus AsmHelper),
 * this test uses {@link BasePlatformTestCase} which loads the Java plugin and registers
 * {@link org.intellij.grammar.java.JavaHelper.PsiHelper} — enabling PSI-based type resolution.
 */
public class BnfGeneratorPsiTest extends BasePlatformTestCase {

  /**
   * Regression test for <a href="https://github.com/JetBrains/Grammar-Kit/issues/436">#436</a>:
   * duplicate {@code @NotNull} annotation generated for mixin methods returning qualified/nested types.
   * <p>
   * In real IDE scenarios with platform SDK classes (resolved from bytecode),
   * {@code PsiHelper.getAnnotationsInner()} fails to filter {@code @NotNull} from
   * method-level annotations when the return type is qualified (e.g., {@code ReadWriteAccessDetector.Access}).
   * This causes both a separate {@code @NotNull} annotation AND the one embedded in the return type text
   * (from {@code PsiType.getCanonicalText(true)}) to be emitted.
   * <p>
   * Since source-based PSI correctly filters the annotation, we simulate the buggy behavior
   * by wrapping the JavaHelper to return {@code @NotNull} in {@code getAnnotations()} for
   * a method whose return type already contains the embedded annotation.
   */
  public void testMixinMethodWithQualifiedReturnType() throws Exception {
    // Add java.lang.annotation stubs so IntelliJ PSI can resolve @Target(TYPE_USE)
    myFixture.addFileToProject("java/lang/annotation/ElementType.java", """
      package java.lang.annotation;
      public enum ElementType {
        TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE,
        ANNOTATION_TYPE, PACKAGE, TYPE_PARAMETER, TYPE_USE, MODULE, RECORD_COMPONENT
      }
      """);
    myFixture.addFileToProject("java/lang/annotation/Target.java", """
      package java.lang.annotation;
      public @interface Target { ElementType[] value(); }
      """);
    myFixture.addFileToProject("java/lang/annotation/RetentionPolicy.java", """
      package java.lang.annotation;
      public enum RetentionPolicy { SOURCE, CLASS, RUNTIME }
      """);
    myFixture.addFileToProject("java/lang/annotation/Retention.java", """
      package java.lang.annotation;
      public @interface Retention { RetentionPolicy value(); }
      """);

    // Create @NotNull and @Nullable annotations with TYPE_USE target
    myFixture.addFileToProject("org/jetbrains/annotations/NotNull.java", """
      package org.jetbrains.annotations;
      import java.lang.annotation.*;
      @Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
      @Retention(RetentionPolicy.CLASS)
      public @interface NotNull {}
      """);
    myFixture.addFileToProject("org/jetbrains/annotations/Nullable.java", """
      package org.jetbrains.annotations;
      import java.lang.annotation.*;
      @Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
      @Retention(RetentionPolicy.CLASS)
      public @interface Nullable {}
      """);

    // Create the outer class with nested inner type
    myFixture.addFileToProject("test/Outer.java", """
      package test;
      public class Outer {
        public enum Access { Read, Write, ReadWrite }
      }
      """);

    // Create a base class (standing in for ASTWrapperPsiElement)
    myFixture.addFileToProject("test/psi/impl/BaseElement.java", """
      package test.psi.impl;
      public abstract class BaseElement {
        public BaseElement(Object node) {}
      }
      """);

    // Create the mixin class with @NotNull and @Nullable on qualified return types.
    // `@NotNull Outer.Access` — per JLS 9.7.4, this is both a declaration and type annotation.
    myFixture.addFileToProject("test/psi/impl/MyMixin.java", """
      package test.psi.impl;
      import org.jetbrains.annotations.NotNull;
      import org.jetbrains.annotations.Nullable;
      import test.Outer;
      public abstract class MyMixin extends BaseElement {
        public MyMixin(@NotNull Object node) { super(node); }
        public @NotNull Outer.Access getAccess() { return Outer.Access.Read; }
        public @Nullable Outer.Access getOptionalAccess() { return null; }
      }
      """);

    // Verify types are fully resolvable
    JavaPsiFacade facade = JavaPsiFacade.getInstance(getProject());
    GlobalSearchScope scope = GlobalSearchScope.allScope(getProject());
    assertNotNull("@NotNull must be resolvable", facade.findClass("org.jetbrains.annotations.NotNull", scope));
    assertNotNull("MyMixin must be resolvable", facade.findClass("test.psi.impl.MyMixin", scope));
    assertNotNull("ElementType must be resolvable", facade.findClass("java.lang.annotation.ElementType", scope));

    // Verify JavaHelper uses PsiHelper
    JavaHelper javaHelper = JavaHelper.getJavaHelper(myFixture.addFileToProject("dummy.bnf", "{}"));
    assertTrue("Expected PsiHelper", javaHelper.getClass().getName().contains("PsiHelper"));

    // Verify the return type includes the embedded annotation (needed for the bug to manifest)
    List<NavigatablePsiElement> methods = javaHelper.findClassMethods(
      "test.psi.impl.MyMixin", JavaHelper.MethodType.INSTANCE, "getAccess", -1);
    assertEquals("Should find getAccess method", 1, methods.size());
    List<String> methodTypes = javaHelper.getMethodTypes(methods.get(0));
    assertTrue("Return type should contain embedded @NotNull for qualified type, got: " + methodTypes,
      !methodTypes.isEmpty() && methodTypes.get(0).contains("@"));

    String bnfText = """
      {
        parserClass='test.TestParser'
        extends='test.psi.impl.BaseElement'
        elementTypeHolderClass='test.TestTypes'
        elementTypeClass='com.intellij.psi.tree.IElementType'
        tokenTypeClass='com.intellij.psi.tree.IElementType'
        psiClassPrefix='Test'
        psiImplClassSuffix='Impl'
        psiPackage='test.psi'
        psiImplPackage='test.psi.impl'
        generatePsi=true
        tokens=[ ID='regexp:\\\\w+' ]
      }
      File ::= MyElement*
      MyElement ::= ID {
        methods=[getAccess getOptionalAccess]
        mixin="test.psi.impl.MyMixin"
      }
      """;

    PsiFile psiFile = myFixture.configureByText("Test.bnf", bnfText);
    assertInstanceOf(psiFile, BnfFile.class);
    BnfFile bnfFile = (BnfFile) psiFile;

    File outputFile = new File(FileUtilRt.getTempDirectory(), "BnfGeneratorPsiTest.PSI.java");
    if (outputFile.exists()) assertTrue(outputFile.delete());

    ParserGenerator generator = new ParserGenerator(bnfFile, "", FileUtilRt.getTempDirectory(), "") {
      @Override
      protected PrintWriter openOutputInner(String className, File file) throws IOException {
        String fileName = FileUtil.getNameWithoutExtension(file);
        if (fileName.startsWith("Test") || fileName.equals("Visitor")) {
          //noinspection ResultOfMethodCallIgnored
          outputFile.getParentFile().mkdirs();
          FileOutputStream outputStream = new FileOutputStream(outputFile, true);
          PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, this.myFile.getVirtualFile().getCharset()));
          out.println("// ---- " + file.getName() + " -----------------");
          return out;
        }
        return super.openOutputInner(className, file);
      }
    };

    // Simulate PsiHelper bug for qualified types (#436):
    // In real IDE with platform SDK classes resolved from bytecode,
    // PsiHelper.getAnnotationsInner() fails to filter @NotNull from method annotations
    // when PsiType.getAnnotations() doesn't return annotations on inner type components
    // of qualified types. We simulate this by wrapping the JavaHelper.
    Field javaHelperField = ParserGenerator.class.getDeclaredField("myJavaHelper");
    javaHelperField.setAccessible(true);
    JavaHelper original = (JavaHelper) javaHelperField.get(generator);
    javaHelperField.set(generator, new DuplicateAnnotationJavaHelper(original));

    generator.generate();

    assertTrue("Generated PSI file not found: " + outputFile, outputFile.exists());
    String generated = FileUtil.loadFile(outputFile);

    // Verify the mixin method was actually generated (not skipped)
    assertTrue(
      "Generated code should contain getAccess method from mixin:\n" + generated,
      generated.contains("getAccess")
    );

    // The generated code should NOT contain duplicate @NotNull annotations (issue #436)
    assertFalse(
      "Generated code contains duplicate @NotNull annotations (issue #436):\n" + generated,
      generated.contains("@NotNull\n  @NotNull")
    );

    // Positive check: verify exactly one @NotNull before getAccess return type (not zero, not two)
    assertAnnotationCount(generated, "getAccess()", "@NotNull", 1);

    // Same check for @Nullable on a qualified return type
    assertAnnotationCount(generated, "getOptionalAccess()", "@Nullable", 1);
  }

  /**
   * Counts occurrences of an annotation in the method signature lines (from the previous blank line
   * up to the method name), and asserts exactly {@code expected} occurrences.
   */
  private static void assertAnnotationCount(String generated, String methodSignature, String annotation, int expected) {
    int methodIdx = generated.indexOf(methodSignature);
    assertTrue(methodSignature + " must be present in generated code", methodIdx >= 0);
    // Look backward to find the start of this method's declaration (previous blank line or start)
    int lineStart = generated.lastIndexOf("\n\n", methodIdx);
    if (lineStart < 0) lineStart = 0;
    String methodBlock = generated.substring(lineStart, methodIdx);
    int count = 0;
    int pos = 0;
    while ((pos = methodBlock.indexOf(annotation, pos)) != -1) {
      count++;
      pos += annotation.length();
    }
    assertEquals("Expected exactly " + expected + " " + annotation + " before " + methodSignature +
      ", found " + count + " in:\n" + methodBlock, expected, count);
  }

  /**
   * Wraps a JavaHelper to simulate the PsiHelper bug for qualified return types (#436):
   * returns {@code @NotNull} from both {@code getAnnotations()} and embedded in the
   * return type from {@code getMethodTypes()}, causing duplicate annotations in generated code.
   */
  private static class DuplicateAnnotationJavaHelper extends JavaHelper {
    private final JavaHelper myDelegate;

    DuplicateAnnotationJavaHelper(JavaHelper delegate) {
      myDelegate = delegate;
    }

    @Override
    public boolean isPublic(@Nullable NavigatablePsiElement element) {
      return myDelegate.isPublic(element);
    }

    @Override
    public @Nullable NavigatablePsiElement findClass(@Nullable String className) {
      return myDelegate.findClass(className);
    }

    @Override
    public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                                  @NotNull MethodType methodType,
                                                                  @Nullable String methodName,
                                                                  int paramCount,
                                                                  String... paramTypes) {
      return myDelegate.findClassMethods(className, methodType, methodName, paramCount, paramTypes);
    }

    @Override
    public @Nullable String getSuperClassName(@Nullable String className) {
      return myDelegate.getSuperClassName(className);
    }

    @Override
    public @NotNull List<String> getMethodTypes(@Nullable NavigatablePsiElement method) {
      return myDelegate.getMethodTypes(method);
    }

    @Override
    public List<TypeParameterInfo> getGenericParameters(NavigatablePsiElement method) {
      return myDelegate.getGenericParameters(method);
    }

    @Override
    public List<String> getExceptionList(NavigatablePsiElement method) {
      return myDelegate.getExceptionList(method);
    }

    @Override
    public @NotNull String getDeclaringClass(@Nullable NavigatablePsiElement method) {
      return myDelegate.getDeclaringClass(method);
    }

    @Override
    public @NotNull List<String> getAnnotations(@Nullable NavigatablePsiElement element) {
      List<String> result = myDelegate.getAnnotations(element);
      // Simulate PsiHelper bug: for methods whose return type already contains an embedded
      // annotation (qualified types like ReadWriteAccessDetector.Access), the annotation
      // is NOT filtered out by getAnnotationsInner() because PsiType.getAnnotations()
      // doesn't return annotations on inner type components of qualified types.
      if (element instanceof PsiMethod) {
        List<String> types = myDelegate.getMethodTypes(element);
        if (!types.isEmpty()) {
          String returnType = types.get(0);
          List<String> mutable = null;
          for (String anno : List.of("org.jetbrains.annotations.NotNull", "org.jetbrains.annotations.Nullable")) {
            if (returnType.contains("@" + anno) && !result.contains(anno)) {
              if (mutable == null) mutable = new ArrayList<>(result);
              mutable.add(anno);
            }
          }
          if (mutable != null) return mutable;
        }
      }
      return result;
    }

    @Override
    public @NotNull List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
      return myDelegate.getParameterAnnotations(method, paramIndex);
    }

    @Override
    public PsiReference @NotNull [] getClassReferences(@NotNull PsiElement element, @NotNull ProcessingContext context) {
      return myDelegate.getClassReferences(element, context);
    }

    @Override
    public @Nullable NavigationItem findPackage(@Nullable String packageName) {
      return myDelegate.findPackage(packageName);
    }
  }
}
