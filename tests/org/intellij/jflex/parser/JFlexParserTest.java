/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.parser;

import com.intellij.lang.LanguageASTFactory;
import org.intellij.grammar.AbstractParsingTestCase;
import org.jetbrains.annotations.NonNls;

import java.io.IOException;

/**
 * @author gregsh
 */
public class JFlexParserTest extends AbstractParsingTestCase {
  public JFlexParserTest() {
    super("jflex/parser", "flex", new JFlexParserDefinition());
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    addExplicitExtension(LanguageASTFactory.INSTANCE, myLanguage, new JFlexASTFactory());
  }

  public void testSelfFlex() { doTest(true); }
  public void testSelfBnf() { doTest(true); }
  public void testEmpty1() throws Exception { doCodeTest(""); }
  public void testEmpty2() throws Exception { doCodeTest("%%\n"); }
  public void testEmpty3() throws Exception { doCodeTest("%%\n%%"); }
  public void testEOF1() throws Exception { doCodeTest(" %%\n\n%%\n\""); }
  public void testParserFixes() { doTest(true); }
  public void testParserFixes2() { doTest(true); }

  public void testCharClassOp() throws Exception {
    doCodeTest("%%\n%%\nN=[_42a-zA-Z*-?\\a-\\b\\--][[\\w]--\\d]" +
               "[\\wX_--\\d][\\w_||\\d]" +
               "[[\\w_]&&\\d][\\w_~~[\\d]][{EOL}] {}");
  }

  @Override
  protected String loadFile(@NonNls String name) throws IOException {
    String adjusted;
    if ("SelfBnf.flex".equals(name)) adjusted = "../../../src/org/intellij/grammar/parser/_BnfLexer.flex";
    else if ("SelfFlex.flex".equals(name)) adjusted = "../../../src/org/intellij/jflex/parser/_JFlexLexer.flex";
    else adjusted = name;
    return super.loadFile(adjusted);
  }
}
