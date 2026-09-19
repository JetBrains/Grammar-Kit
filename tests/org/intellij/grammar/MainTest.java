/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

/**
 * Runs in an isolated JVM (via the {@code testMain} Gradle task) so that
 * {@link LightPsi} is initialized fresh by {@link Main#run} itself, exactly
 * as it is in the real CLI scenario.
 */
public class MainTest extends TestCase {

  private ByteArrayOutputStream capturedOut;
  private ByteArrayOutputStream capturedErr;
  private PrintStream originalOut;
  private PrintStream originalErr;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    capturedOut = new ByteArrayOutputStream();
    capturedErr = new ByteArrayOutputStream();
    originalOut = System.out;
    originalErr = System.err;
    System.setOut(new PrintStream(capturedOut));
    System.setErr(new PrintStream(capturedErr));
  }

  @Override
  protected void tearDown() throws Exception {
    System.setOut(originalOut);
    System.setErr(originalErr);
    super.tearDown();
  }

  public void testRunNoArgs() {
    assertEquals(0, Main.run(new String[]{}));
    assertContains(stdOut(), "Usage:");
  }

  public void testRunOneArg() {
    assertEquals(0, Main.run(new String[]{"someDir"}));
    assertContains(stdOut(), "Usage:");
  }

  public void testRunOutputDirIsFile() throws Exception {
    File tempFile = FileUtilRt.createTempFile("main-test", ".txt", true);
    assertEquals(0, Main.run(new String[]{tempFile.getAbsolutePath(), "Grammar.bnf"}));
    assertContains(stdOut(), "Output directory not found:");
  }

  public void testRunGrammarDirNotFound() throws Exception {
    File output = FileUtilRt.createTempDirectory("main-test-out", null, true);
    assertEquals(1, Main.run(new String[]{output.getAbsolutePath(), "/nonexistent-dir-xyz/Grammar.bnf"}));
    assertContains(stdErr(), "Grammar directory not found:");
  }

  public void testRunCreatesOutputDirectoryIfAbsent() throws Exception {
    File tempBase = FileUtilRt.createTempDirectory("main-test-base", null, true);
    File subdir = new File(tempBase, "generated");
    assertFalse(subdir.exists());

    File inputDir = FileUtilRt.createTempDirectory("main-test-in", null, true);
    FileUtil.writeToFile(new File(inputDir, "Grammar.bnf"),
                         "{ parser-class=\"com.example.TestParser\" }\nroot ::= 'x'");

    assertEquals(0, Main.run(new String[]{subdir.getAbsolutePath(), inputDir + "/Grammar.bnf"}));
    assertTrue(subdir.exists() && subdir.isDirectory());
  }

  public void testRunSuccessfulGeneration() throws Exception {
    File inputDir = FileUtilRt.createTempDirectory("main-test-in", null, true);
    File output = FileUtilRt.createTempDirectory("main-test-out", null, true);
    FileUtil.writeToFile(new File(inputDir, "Grammar.bnf"),
                         "{ parser-class=\"com.example.TestParser\" }\nroot ::= 'x'");

    assertEquals(0, Main.run(new String[]{output.getAbsolutePath(), inputDir + "/Grammar.bnf"}));
    File[] generated = output.listFiles();
    assertNotNull(generated);
    assertTrue("Expected generated files in output dir", generated.length > 0);
    assertContains(stdOut(), "Grammar.bnf");
    assertContains(stdOut(), "parser generated to");
  }

  public void testRunWildcardGeneratesMultipleParsers() throws Exception {
    File inputDir = FileUtilRt.createTempDirectory("main-test-in", null, true);
    File output = FileUtilRt.createTempDirectory("main-test-out", null, true);
    FileUtil.writeToFile(new File(inputDir, "A.bnf"),
                         "{ parser-class=\"com.example.AParser\" }\nroot ::= 'a'");
    FileUtil.writeToFile(new File(inputDir, "B.bnf"),
                         "{ parser-class=\"com.example.BParser\" }\nroot ::= 'b'");

    assertEquals(0, Main.run(new String[]{output.getAbsolutePath(), inputDir + "/*.bnf"}));
    assertContains(stdOut(), "A.bnf");
    assertContains(stdOut(), "B.bnf");
  }

  /**
   * GitHub #468: a CRLF grammar used to leave a stray {@code \r} at the end of every
   * {@code // rule expression} comment line, which showed up as an extra blank line in the diff.
   * The generated output must be pure LF no matter the grammar's or the host's line separators.
   */
  public void testRunCrlfGrammarGeneratesLfOnlyOutput() throws Exception {
    File inputDir = FileUtilRt.createTempDirectory("main-test-in", null, true);
    File output = FileUtilRt.createTempDirectory("main-test-out", null, true);
    String grammar = """
      { parserClass="com.example.TestParser" }
      root ::= first |
          second |
          third
      first ::= 'a'
      second ::= 'b'
      third ::= 'c'
      """;
    FileUtil.writeToFile(new File(inputDir, "Grammar.bnf"), grammar.replace("\n", "\r\n"));

    assertEquals(0, Main.run(new String[]{output.getAbsolutePath(), inputDir + "/Grammar.bnf"}));

    File parser = findFile(output, "TestParser.java");
    assertNotNull("Expected TestParser.java under " + output, parser);
    String text = FileUtil.loadFile(parser);
    assertContains(text, "// first |");
    assertFalse("Carriage return in generated " + parser.getName(), text.indexOf('\r') >= 0);
  }

  /**
   * A {@code classHeader} pointing at a CRLF file used to leak the same stray {@code \r} into
   * every header line of every generated file - this path never goes through PSI.
   */
  public void testRunCrlfClassHeaderFileGeneratesLfOnlyOutput() throws Exception {
    File inputDir = FileUtilRt.createTempDirectory("main-test-in", null, true);
    File output = FileUtilRt.createTempDirectory("main-test-out", null, true);
    FileUtil.writeToFile(new File(inputDir, "header.txt"), "// first header line\r\n// second header line\r\n");
    FileUtil.writeToFile(new File(inputDir, "Grammar.bnf"),
                         "{ parserClass=\"com.example.TestParser\" classHeader=\"header.txt\" }\nroot ::= 'x'");

    assertEquals(0, Main.run(new String[]{output.getAbsolutePath(), inputDir + "/Grammar.bnf"}));

    File parser = findFile(output, "TestParser.java");
    assertNotNull("Expected TestParser.java under " + output, parser);
    String text = FileUtil.loadFile(parser);
    assertContains(text, "// first header line\n// second header line\n");
    assertFalse("Carriage return in generated " + parser.getName(), text.indexOf('\r') >= 0);
  }

  private static File findFile(File dir, String name) {
    File[] children = dir.listFiles();
    if (children == null) return null;
    for (File child : children) {
      File found = child.isDirectory() ? findFile(child, name) : name.equals(child.getName()) ? child : null;
      if (found != null) return found;
    }
    return null;
  }

  private String stdOut() { return capturedOut.toString(); }
  private String stdErr() { return capturedErr.toString(); }

  private static void assertContains(String text, String substring) {
    assertTrue("Expected <" + substring + "> in:\n" + text, text.contains(substring));
  }
}
