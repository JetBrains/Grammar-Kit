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
import org.intellij.grammar.generator.ParserGeneratorUtil;

/**
 * @author gregsh
 */
public class BnfUtilTest extends UsefulTestCase {
  public void testIdentifiers() {
    assertEquals("AbcEdf", ParserGeneratorUtil.toIdentifier("abc-edf", null, Case.CAMEL));
    assertEquals("getAbcEdf", ParserGeneratorUtil.getGetterName("abc-edf"));
    assertEquals("getMySomething", ParserGeneratorUtil.getGetterName("MY_SOMETHING"));
    assertEquals("getWithSpace", ParserGeneratorUtil.getGetterName("with space"));
    assertEquals("get_WithSpaceAround_", ParserGeneratorUtil.getGetterName(" with space around "));
    assertEquals("_WithSpaceAround_", ParserGeneratorUtil.toIdentifier(" with space around ", null, Case.CAMEL));

    // mixed underscores in body; w/ singly-underscored margins
    assertEquals("_with__single__under_score__on_margin_", ParserGeneratorUtil.toIdentifier("_with__single__under_score__on_margin_", null, Case.AS_IS));
    assertEquals("_WITH__SINGLE__UNDER_SCORE__ON_MARGIN_", ParserGeneratorUtil.toIdentifier("_with__single__under_score__on_margin_", null, Case.UPPER));
    assertEquals("_WithSingleUnderScoreOnMargin_", ParserGeneratorUtil.toIdentifier("_with__single__under_score__on_margin_", null, Case.CAMEL));
    assertEquals("get_WithSingleUnderScoreOnMargin_", ParserGeneratorUtil.getGetterName("_with__single__under_score__on_margin_"));

    // mixed underscores in body; w/ multiply-underscored margins
    assertEquals("__with__multiple__under_scores__on_margin__", ParserGeneratorUtil.toIdentifier("__with__multiple__under_scores__on_margin__", null, Case.AS_IS));
    assertEquals("__WITH__MULTIPLE__UNDER_SCORES__ON_MARGIN__", ParserGeneratorUtil.toIdentifier("__with__multiple__under_scores__on_margin__", null, Case.UPPER));
    assertEquals("__WithMultipleUnderScoresOnMargin__", ParserGeneratorUtil.toIdentifier("__with__multiple__under_scores__on_margin__", null, Case.CAMEL));
    assertEquals("get__WithMultipleUnderScoresOnMargin__", ParserGeneratorUtil.getGetterName("__with__multiple__under_scores__on_margin__"));

    // mixed underscores; w/ embedded camel-casing
    assertEquals("SomeRule_p_r__inner__lazyVersion", ParserGeneratorUtil.toIdentifier("SomeRule_p_r__inner__lazyVersion", null, Case.AS_IS));
    assertEquals("SOME_RULE_P_R__INNER__LAZY_VERSION", ParserGeneratorUtil.toIdentifier("SomeRule_p_r__inner__lazyVersion", null, Case.UPPER));
    assertEquals("SomeRulePRInnerLazyVersion", ParserGeneratorUtil.toIdentifier("SomeRule_p_r__inner__lazyVersion", null, Case.CAMEL));
    assertEquals("getSomeRulePRInnerLazyVersion", ParserGeneratorUtil.getGetterName("SomeRule_p_r__inner__lazyVersion"));
    
    assertEquals("_12Feb12", ParserGeneratorUtil.toIdentifier("12Feb%12", null, Case.CAMEL));
    assertEquals("CPRule", ParserGeneratorUtil.toIdentifier("CPRule", null, Case.CAMEL));
    assertEquals("T_SOME_TYPE", ParserGeneratorUtil.toIdentifier("TSomeType", "", Case.UPPER));
  }
}
