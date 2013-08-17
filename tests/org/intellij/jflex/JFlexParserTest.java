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

package org.intellij.jflex;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.testFramework.ParsingTestCase;
import org.intellij.jflex.parser.JFlexParserDefinition;
import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.io.IOException;

/**
 * @author gregsh
 */
public class JFlexParserTest extends ParsingTestCase {
  public JFlexParserTest() {
    super("jflex/parser", "flex", new JFlexParserDefinition());
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected String getTestDataPath() {
    return "testData";
  }

  public void testSelfFlex() { doTest(true); }
  public void testSelfBnf() { doTest(true); }
  public void testEmpty1() throws Exception { doCodeTest(""); }
  public void testEmpty2() throws Exception { doCodeTest("%%\n"); }
  public void testEmpty3() throws Exception { doCodeTest("%%\n%%"); }
  public void testParserFixes() { doTest(true); }

  @Override
  protected String loadFile(@NonNls String name) throws IOException {
    if (name.startsWith("Self")) {
      String name1 = name.startsWith("SelfFlex") ? "../../../support/org/intellij/jflex/parser/_JFlexLexer.flex" :
                     "../../../support/org/intellij/grammar/parser/_BnfLexer.flex";
      return StringUtil.convertLineSeparators(
        FileUtil.loadFile(new File(myFullDataPath, name1).getCanonicalFile(), CharsetToolkit.UTF8).trim());
    }
    return super.loadFile(name);
  }
}
