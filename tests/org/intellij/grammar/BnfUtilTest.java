/*
 * Copyright 2011-2016 Gregory Shrago
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

package org.intellij.grammar;

import com.intellij.testFramework.UsefulTestCase;
import org.intellij.grammar.generator.Case;
import org.intellij.grammar.generator.NameShortener;
import org.intellij.grammar.generator.ParserGeneratorUtil;

import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.NameFormat;
import static org.intellij.grammar.generator.ParserGeneratorUtil.getGetterName;

/**
 * @author gregsh
 */
public class BnfUtilTest extends UsefulTestCase {
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

    assertEquals("getAbcEdf", getGetterName("abc-edf"));
    assertEquals("getMySomething", getGetterName("MY_SOMETHING"));
    assertEquals("getWithSpace", getGetterName("with space"));
    assertEquals("get_WithSpaceAround_", getGetterName(" with space around "));
    assertEquals("_WithSpaceAround_", toIdentifier(" with space around ", Case.CAMEL));

    // mixed underscores in body; w/ singly-underscored margins
    assertEquals("_with__single__under_score__on_margin_", toIdentifier("_with__single__under_score__on_margin_", Case.AS_IS));
    assertEquals("_WITH__SINGLE__UNDER_SCORE__ON_MARGIN_", toIdentifier("_with__single__under_score__on_margin_", Case.UPPER));
    assertEquals("_WithSingleUnderScoreOnMargin_", toIdentifier("_with__single__under_score__on_margin_", Case.CAMEL));
    assertEquals("get_WithSingleUnderScoreOnMargin_", getGetterName("_with__single__under_score__on_margin_"));

    // mixed underscores in body; w/ multiply-underscored margins
    assertEquals("__with__multiple__under_scores__on_margin__", toIdentifier("__with__multiple__under_scores__on_margin__", Case.AS_IS));
    assertEquals("__WITH__MULTIPLE__UNDER_SCORES__ON_MARGIN__", toIdentifier("__with__multiple__under_scores__on_margin__", Case.UPPER));
    assertEquals("__WithMultipleUnderScoresOnMargin__", toIdentifier("__with__multiple__under_scores__on_margin__", Case.CAMEL));
    assertEquals("get__WithMultipleUnderScoresOnMargin__", getGetterName("__with__multiple__under_scores__on_margin__"));

    // mixed underscores; w/ embedded camel-casing
    assertEquals("SomeRule_p_r__inner__lazyVersion", toIdentifier("SomeRule_p_r__inner__lazyVersion", Case.AS_IS));
    assertEquals("SOME_RULE_P_R__INNER__LAZY_VERSION", toIdentifier("SomeRule_p_r__inner__lazyVersion", Case.UPPER));
    assertEquals("SomeRulePRInnerLazyVersion", toIdentifier("SomeRule_p_r__inner__lazyVersion", Case.CAMEL));
    assertEquals("getSomeRulePRInnerLazyVersion", getGetterName("SomeRule_p_r__inner__lazyVersion"));
    
    assertEquals("_12Feb12", toIdentifier("12Feb%12", Case.CAMEL));
    assertEquals("CPRule", toIdentifier("CPRule", Case.CAMEL));
    assertEquals("T_SOME_TYPE", toIdentifier("TSomeType", Case.UPPER));
    assertEquals("TestEOL", toIdentifier("testEOL", Case.CAMEL));
  }
  
  static String toIdentifier(String s, Case c) {
    return ParserGeneratorUtil.toIdentifier(s, null, c);
  }

  static String toIdentifier(String s, String f, Case c) {
    return ParserGeneratorUtil.toIdentifier(s, NameFormat.from(f), c);
  }

  public void testNameShortener1() {
    String longType = "java.util.@org.jetbrains.annotations.NotNull(\"some.text and.more\", arr = [@Nullable]) List<java.util.@Nullable Set<java.lang.Integer>>";
    NameShortener shortener = new NameShortener("com", true);
    shortener.addImports(Arrays.asList("java.util.*", "org.jetbrains.annotations.*"), Collections.emptySet());
    assertEquals("@NotNull(\"some.text and.more\", arr = [@Nullable]) List<@Nullable Set<Integer>>", shortener.shorten(longType));
  }

  public void testNameShortener2() {
    String longType = "java.util.@org.jetbrains.annotations.NotNull(\"some.text and.more\", arr = [@Nullable]) List<sample.@Nullable Inner.Class<java.lang.Integer>>";
    List<String> imports = new ArrayList<>();
    NameShortener.addTypeToImports(longType, Collections.emptyList(), imports);
    assertEquals(Arrays.asList("org.jetbrains.annotations.NotNull", "java.util.List", "sample.Inner", "java.lang.Integer"), imports);
    NameShortener shortener = new NameShortener("com", true);
    shortener.addImports(imports, Collections.emptySet());
    assertEquals("@NotNull(\"some.text and.more\", arr = [@Nullable]) List<@Nullable Inner.Class<Integer>>", shortener.shorten(longType));
  }

  public void testNameShortener3() {
    String longType = "java.util.@org.jetbrains.annotations.NotNull(\"some.text and.more\",arr = [@Nullable]) List<sample.@Nullable Inner.Class<java.lang.Integer>>";
    NameShortener shortener = new NameShortener("sample", false);
    shortener.addImports(Arrays.asList("org.jetbrains.annotations.NotNull", "java.util.List", "sample.Inner", "java.lang.Integer"), Collections.emptySet());
    assertEquals(longType, shortener.shorten(longType));
  }
}
