/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.testFramework.UsefulTestCase;
import org.intellij.grammar.generator.Case;
import org.intellij.grammar.generator.NameFormat;
import org.intellij.grammar.generator.NameShortener;
import org.intellij.grammar.generator.java.JavaNameShortener;
import org.intellij.grammar.generator.java.JavaRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author gregsh
 */
public class BnfUtilTest extends UsefulTestCase {
  private final @NotNull JavaRenderer R = JavaRenderer.INSTANCE;

  private String toIdentifier(String s, Case c) {
    return R.toIdentifier(s, null, c);
  }

  private String toIdentifier(String s, String f, Case c) {
    return R.toIdentifier(s, NameFormat.from(f), c);
  }

  public void testAttributeDescriptions() {
    for (KnownAttribute<?> attribute : KnownAttribute.getAttributes()) {
      assertNotNull("No description for attribute: " + attribute.getName(), attribute.getDescription());
    }
  }

  public void testIdentifiers() {
    assertEquals("AbcEdf", toIdentifier("abc-edf", Case.CAMEL));
    assertEquals("SampleAbcEdfElement", toIdentifier("abc-edf", "Sample/Element", Case.CAMEL));

    // w/ a single-letter psiClass-prefix coincidentally matching the 1st letter of name being transformed
    assertEquals("CContinueStatementElement", toIdentifier("continue-statement", "C/Element", Case.CAMEL));
    // w/ a psiClass-suffix that matches the rule name suffix but has a different meaning & context and should be preserved
    assertEquals("HtmlBodyElementElement", toIdentifier("body-element", "Html/Element", Case.CAMEL));

    assertEquals("getAbcEdf", R.getGetterName("abc-edf"));
    assertEquals("getMySomething", R.getGetterName("MY_SOMETHING"));
    assertEquals("getWithSpace", R.getGetterName("with space"));
    assertEquals("get_WithSpaceAround_", R.getGetterName(" with space around "));
    assertEquals("_WithSpaceAround_", toIdentifier(" with space around ", Case.CAMEL));

    // mixed underscores in body; w/ singly-underscored margins
    assertEquals("_with__single__under_score__on_margin_", toIdentifier("_with__single__under_score__on_margin_", Case.AS_IS));
    assertEquals("_WITH__SINGLE__UNDER_SCORE__ON_MARGIN_", toIdentifier("_with__single__under_score__on_margin_", Case.UPPER));
    assertEquals("_WithSingleUnderScoreOnMargin_", toIdentifier("_with__single__under_score__on_margin_", Case.CAMEL));
    assertEquals("get_WithSingleUnderScoreOnMargin_", R.getGetterName("_with__single__under_score__on_margin_"));

    // mixed underscores in body; w/ multiply-underscored margins
    assertEquals("__with__multiple__under_scores__on_margin__", toIdentifier("__with__multiple__under_scores__on_margin__", Case.AS_IS));
    assertEquals("__WITH__MULTIPLE__UNDER_SCORES__ON_MARGIN__", toIdentifier("__with__multiple__under_scores__on_margin__", Case.UPPER));
    assertEquals("__WithMultipleUnderScoresOnMargin__", toIdentifier("__with__multiple__under_scores__on_margin__", Case.CAMEL));
    assertEquals("get__WithMultipleUnderScoresOnMargin__", R.getGetterName("__with__multiple__under_scores__on_margin__"));

    // mixed underscores; w/ embedded camel-casing
    assertEquals("SomeRule_p_r__inner__lazyVersion", toIdentifier("SomeRule_p_r__inner__lazyVersion", Case.AS_IS));
    assertEquals("SOME_RULE_P_R__INNER__LAZY_VERSION", toIdentifier("SomeRule_p_r__inner__lazyVersion", Case.UPPER));
    assertEquals("SomeRulePRInnerLazyVersion", toIdentifier("SomeRule_p_r__inner__lazyVersion", Case.CAMEL));
    assertEquals("getSomeRulePRInnerLazyVersion", R.getGetterName("SomeRule_p_r__inner__lazyVersion"));

    assertEquals("_12Feb12", toIdentifier("12Feb%12", Case.CAMEL));
    assertEquals("CPRule", toIdentifier("CPRule", Case.CAMEL));
    assertEquals("T_SOME_TYPE", toIdentifier("TSomeType", Case.UPPER));
    assertEquals("TestEOL", toIdentifier("testEOL", Case.CAMEL));
  }

  public void testNameShortener1() {
    String longType =
      "java.util.@org.jetbrains.annotations.NotNull(\"some.text and.more\", arr = [@Nullable]) List<java.util.@Nullable Set<java.lang.Integer>>";
    JavaNameShortener shortener = new JavaNameShortener("com", true);
    shortener.addImports(Arrays.asList("java.util.*", "org.jetbrains.annotations.*"), Collections.emptySet());
    assertEquals("@NotNull(\"some.text and.more\", arr = [@Nullable]) List<@Nullable Set<Integer>>", shortener.shorten(longType));
  }

  public void testNameShortener2() {
    String longType =
      "java.util.@org.jetbrains.annotations.NotNull(\"some.text and.more\", arr = [@Nullable]) List<sample.@Nullable Inner.Class<java.lang.Integer>>";
    List<String> imports = new ArrayList<>();
    NameShortener.addTypeToImports(longType, Collections.emptyList(), imports);
    assertEquals(Arrays.asList("org.jetbrains.annotations.NotNull", "java.util.List", "sample.Inner", "java.lang.Integer"), imports);
    JavaNameShortener shortener = new JavaNameShortener("com", true);
    shortener.addImports(imports, Collections.emptySet());
    assertEquals("@NotNull(\"some.text and.more\", arr = [@Nullable]) List<@Nullable Inner.Class<Integer>>", shortener.shorten(longType));
  }

  public void testNameShortener3() {
    String longType =
      "java.util.@org.jetbrains.annotations.NotNull(\"some.text and.more\",arr = [@Nullable]) List<sample.@Nullable Inner.Class<java.lang.Integer>>";
    JavaNameShortener shortener = new JavaNameShortener("sample", false);
    shortener.addImports(Arrays.asList("org.jetbrains.annotations.NotNull", "java.util.List", "sample.Inner", "java.lang.Integer"),
                         Collections.emptySet());
    assertEquals(longType, shortener.shorten(longType));
  }
}
