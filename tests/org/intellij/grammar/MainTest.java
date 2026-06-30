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

  private String stdOut() { return capturedOut.toString(); }
  private String stdErr() { return capturedErr.toString(); }

  private static void assertContains(String text, String substring) {
    assertTrue("Expected <" + substring + "> in:\n" + text, text.contains(substring));
  }
}
