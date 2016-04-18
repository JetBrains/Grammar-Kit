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
    assertEquals("_UnderScored_", ParserGeneratorUtil.toIdentifier("_under__scored_", null, Case.CAMEL));
    assertEquals("_12Feb12", ParserGeneratorUtil.toIdentifier("12Feb%12", null, Case.CAMEL));
    assertEquals("CPRule", ParserGeneratorUtil.toIdentifier("CPRule", null, Case.CAMEL));
    assertEquals("T_SOME_TYPE", ParserGeneratorUtil.toIdentifier("TSomeType", "", Case.UPPER));
  }
}
