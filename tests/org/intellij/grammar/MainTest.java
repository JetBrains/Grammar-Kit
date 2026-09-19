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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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
    Files.write(new File(inputDir, "header.txt").toPath(),
                "// first header line \u00ab\r\n// second header line\r\n".getBytes(StandardCharsets.UTF_8));
    FileUtil.writeToFile(new File(inputDir, "Grammar.bnf"),
                         "{ parserClass=\"com.example.TestParser\" classHeader=\"header.txt\" }\nroot ::= 'x'");

    assertEquals(0, Main.run(new String[]{output.getAbsolutePath(), inputDir + "/Grammar.bnf"}));

    File parser = findFile(output, "TestParser.java");
    assertNotNull("Expected TestParser.java under " + output, parser);
    String text = new String(Files.readAllBytes(parser.toPath()), StandardCharsets.UTF_8);
    assertContains(text, "// first header line \u00ab\n// second header line\n");
    assertFalse("Carriage return in generated " + parser.getName(), text.indexOf('\r') >= 0);
  }

  /**
   * Both ends of a standalone run are UTF-8: the grammar is decoded as UTF-8 and the generated
   * files are encoded as UTF-8, so non-ASCII content does not depend on the JVM default charset
   * (which is the platform encoding below JDK 18, and follows {@code -Dfile.encoding} above it).
   */
  public void testRunNonAsciiGrammarRoundTripsAsUtf8() throws Exception {
    File inputDir = FileUtilRt.createTempDirectory("main-test-in", null, true);
    File output = FileUtilRt.createTempDirectory("main-test-out", null, true);
    File grammarFile = new File(inputDir, "Grammar.bnf");
    String grammar = """
      { parserClass="com.example.TestParser" }
      root ::= '\u00ab' | '\u00bb'
      """;
    Files.write(grammarFile.toPath(), grammar.getBytes(StandardCharsets.UTF_8));

    assertEquals(0, Main.run(new String[]{output.getAbsolutePath(), inputDir + "/Grammar.bnf"}));

    File parser = findFile(output, "TestParser.java");
    assertNotNull("Expected TestParser.java under " + output, parser);
    String text = new String(Files.readAllBytes(parser.toPath()), StandardCharsets.UTF_8);
    assertContains(text, "\u00ab");
    assertContains(text, "\u00bb");
    // "\u00c2" is what a UTF-8 grammar decoded as a single-byte charset turns "\u00ab" into
    assertFalse("Grammar decoded with the wrong charset:\n" + text, text.contains("\u00c2"));
  }

  public void testRunAcceptsHostSeparatorPaths() throws Exception {
    File inputDir = FileUtilRt.createTempDirectory("main-test-in", null, true);
    File output = FileUtilRt.createTempDirectory("main-test-out", null, true);
    FileUtil.writeToFile(new File(inputDir, "Grammar.bnf"),
                         "{ parserClass=\"com.example.TestParser\" }\nroot ::= 'x'");

    // on Windows this is a backslash path, which File.separator-only splitting handled, while the
    // forward-slash form the other tests use - and that build scripts produce - did not
    String grammar = inputDir.getPath() + File.separator + "Grammar.bnf";
    assertEquals(0, Main.run(new String[]{output.getAbsolutePath(), grammar}));
    assertNotNull("Expected TestParser.java under " + output, findFile(output, "TestParser.java"));
  }

  public void testRunWildcardProcessesGrammarsInStableOrder() throws Exception {
    File inputDir = FileUtilRt.createTempDirectory("main-test-in", null, true);
    File output = FileUtilRt.createTempDirectory("main-test-out", null, true);
    FileUtil.writeToFile(new File(inputDir, "B.bnf"), "{ parserClass=\"com.example.BParser\" }\nroot ::= 'b'");
    FileUtil.writeToFile(new File(inputDir, "A.bnf"), "{ parserClass=\"com.example.AParser\" }\nroot ::= 'a'");
    FileUtil.writeToFile(new File(inputDir, "C.bnf"), "{ parserClass=\"com.example.CParser\" }\nroot ::= 'c'");

    assertEquals(0, Main.run(new String[]{output.getAbsolutePath(), inputDir + "/*.bnf"}));

    String out = stdOut();
    int a = out.indexOf("A.bnf"), b = out.indexOf("B.bnf"), c = out.indexOf("C.bnf");
    // all three must actually be there: a missing grammar reports -1, which would satisfy a bare
    // a < b < c comparison
    assertTrue("Not every grammar was processed:\n" + out, a >= 0 && b >= 0 && c >= 0);
    assertTrue("Grammars not processed in name order:\n" + out, a < b && b < c);
  }

  public void testRunRootRelativePathResolvesToFilesystemRoot() {
    // "/Grammar.bnf" leaves an empty directory part, which used to resolve to the working
    // directory; the run must look at the filesystem root instead (and find nothing there)
    File output = new File(FileUtilRt.getTempDirectory(), "main-test-root-out");
    assertEquals(0, Main.run(new String[]{output.getAbsolutePath(), File.separator + "NoSuchGrammar.bnf"}));
    assertContains(stdOut(), "No grammars matching 'NoSuchGrammar.bnf' found in: " + File.separator);
  }

  /**
   * A grammar carrying a byte order mark is decoded by that mark, not by the UTF-8 default, and
   * the mark itself does not survive into the generated sources.
   */
  public void testRunBomGrammarsAreDecodedByTheirBom() throws Exception {
    String grammar = """
      { parserClass="com.example.TestParser" }
      root ::= '\u00ab' | '\u00bb'
      """;
    assertBomGrammarGenerates(grammar, StandardCharsets.UTF_8, new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
    assertBomGrammarGenerates(grammar, StandardCharsets.UTF_16LE, new byte[]{(byte)0xFF, (byte)0xFE});
    assertBomGrammarGenerates(grammar, StandardCharsets.UTF_16BE, new byte[]{(byte)0xFE, (byte)0xFF});
  }

  private void assertBomGrammarGenerates(String grammar, java.nio.charset.Charset charset, byte[] bom) throws Exception {
    File inputDir = FileUtilRt.createTempDirectory("main-test-in", null, true);
    File output = FileUtilRt.createTempDirectory("main-test-out", null, true);
    byte[] body = grammar.getBytes(charset);
    byte[] all = new byte[bom.length + body.length];
    System.arraycopy(bom, 0, all, 0, bom.length);
    System.arraycopy(body, 0, all, bom.length, body.length);
    Files.write(new File(inputDir, "Grammar.bnf").toPath(), all);

    assertEquals(charset.name(), 0, Main.run(new String[]{output.getAbsolutePath(), inputDir + "/Grammar.bnf"}));

    File parser = findFile(output, "TestParser.java");
    assertNotNull(charset + ": expected TestParser.java under " + output, parser);
    String text = new String(Files.readAllBytes(parser.toPath()), StandardCharsets.UTF_8);
    assertContains(text, "\u00ab");
    assertFalse(charset + ": byte order mark survived into the output", text.contains("\uFEFF"));
  }

  /** The same for a {@code classHeader} file, whose text is copied into every generated file. */
  public void testRunBomClassHeaderFileDropsTheMark() throws Exception {
    File inputDir = FileUtilRt.createTempDirectory("main-test-in", null, true);
    File output = FileUtilRt.createTempDirectory("main-test-out", null, true);
    Files.write(new File(inputDir, "header.txt").toPath(),
                ("\uFEFF// header \u00ab\n").getBytes(StandardCharsets.UTF_8));
    FileUtil.writeToFile(new File(inputDir, "Grammar.bnf"),
                         "{ parserClass=\"com.example.TestParser\" classHeader=\"header.txt\" }\nroot ::= 'x'");

    assertEquals(0, Main.run(new String[]{output.getAbsolutePath(), inputDir + "/Grammar.bnf"}));

    File parser = findFile(output, "TestParser.java");
    assertNotNull("Expected TestParser.java under " + output, parser);
    String text = new String(Files.readAllBytes(parser.toPath()), StandardCharsets.UTF_8);
    assertTrue("Header not at the very start of:\n" + text, text.startsWith("// header \u00ab\n"));
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
