/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.application.PathMacros;
import com.intellij.openapi.vfs.encoding.EncodingManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.intellij.grammar.settings.GrammarKitSettings;

import java.io.File;
import java.util.List;

/**
 * Tests for the configurable-CLI plumbing: the settings round-trip and the command line built
 * from a CLI text. Uses a heavy fixture because both path macros and {@code GrammarKitSettings}
 * are real project services.
 */
public class GrammarKitCliRunnerPlatformTest extends JavaCodeInsightFixtureTestCase {

  private static final String SPACED_MACRO = "GRAMMAR_KIT_TEST_DIR";

  private String myParserCliBefore;
  private String myJFlexCliBefore;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    // The light project, and with it GrammarKitSettings, is cached and shared across tests.
    GrammarKitSettings settings = GrammarKitSettings.getInstance(getProject());
    myParserCliBefore = settings.getParserCli();
    myJFlexCliBefore = settings.getJFlexCli();
  }

  @Override
  protected void tearDown() throws Exception {
    try {
      GrammarKitSettings settings = GrammarKitSettings.getInstance(getProject());
      settings.setParserCli(myParserCliBefore);
      settings.setJFlexCli(myJFlexCliBefore);
    }
    finally {
      super.tearDown();
    }
  }

  public void testSettingsDefaultToEmpty() {
    // A fresh component, not the shared service: the point is the default, not the live state.
    GrammarKitSettings settings = new GrammarKitSettings();
    assertEquals("", settings.getParserCli());
    assertEquals("", settings.getJFlexCli());
  }

  public void testSettingsRoundTrip() {
    GrammarKitSettings settings = GrammarKitSettings.getInstance(getProject());
    settings.setParserCli("  gen-parser.sh  ");
    settings.setJFlexCli("gen-lexer.sh --verbose");

    GrammarKitSettings reloaded = new GrammarKitSettings();
    reloaded.loadState(settings.getState());

    assertEquals("gen-parser.sh", reloaded.getParserCli());
    assertEquals("gen-lexer.sh --verbose", reloaded.getJFlexCli());
  }

  public void testBlankCliReadsAsEmpty() {
    GrammarKitSettings settings = GrammarKitSettings.getInstance(getProject());
    settings.setParserCli("   \t ");
    assertEquals("", settings.getParserCli());
  }

  public void testGrammarPathIsAppendedAndWorkDirIsProjectBaseDir() {
    VirtualFile file = myFixture.configureByText("Grammar.bnf", "root ::= 'x'").getVirtualFile();
    File ioFile = VfsUtilCore.virtualToIoFile(file);

    GeneralCommandLine commandLine = GrammarKitCliRunner.buildCommandLine(getProject(), List.of(file), "gen.sh -q");

    assertEquals("gen.sh", commandLine.getExePath());
    assertEquals(List.of("-q", ioFile.getAbsolutePath()), commandLine.getParametersList().getList());
    assertEquals(new File(getProject().getBasePath()), commandLine.getWorkDirectory());
  }

  public void testEveryGrammarIsAppendedInOrder() {
    VirtualFile first = myFixture.configureByText("First.bnf", "root ::= 'x'").getVirtualFile();
    VirtualFile second = myFixture.configureByText("Second.bnf", "root ::= 'y'").getVirtualFile();

    GeneralCommandLine commandLine =
      GrammarKitCliRunner.buildCommandLine(getProject(), List.of(first, second), "gen.sh");

    assertEquals(List.of(VfsUtilCore.virtualToIoFile(first).getAbsolutePath(),
                         VfsUtilCore.virtualToIoFile(second).getAbsolutePath()),
                 commandLine.getParametersList().getList());
  }

  public void testConsoleCharsetIsThePlatformDefaultNotTheGrammarCharset() {
    VirtualFile file = myFixture.configureByText("Grammar.bnf", "root ::= 'x'").getVirtualFile();

    GeneralCommandLine commandLine = GrammarKitCliRunner.buildCommandLine(getProject(), List.of(file), "gen.sh");

    // The charset decodes what the spawned tool prints; the grammar's own encoding says nothing
    // about that, and forcing it would garble the console and every marker line with it.
    assertEquals(EncodingManager.getInstance().getDefaultConsoleEncoding(), commandLine.getCharset());
  }

  public void testQuotedArgumentStaysOneParameter() {
    VirtualFile file = myFixture.configureByText("Grammar.bnf", "root ::= 'x'").getVirtualFile();

    GeneralCommandLine commandLine =
      GrammarKitCliRunner.buildCommandLine(getProject(), List.of(file), "gen.sh --out \"some dir/gen\"");

    assertEquals("--out", commandLine.getParametersList().get(0));
    assertEquals("some dir/gen", commandLine.getParametersList().get(1));
  }

  public void testPathMacroIsExpanded() {
    VirtualFile file = myFixture.configureByText("Grammar.bnf", "root ::= 'x'").getVirtualFile();

    GeneralCommandLine commandLine =
      GrammarKitCliRunner.buildCommandLine(getProject(), List.of(file), "$PROJECT_DIR$/scripts/gen.sh");

    String exePath = commandLine.getExePath();
    assertFalse(exePath, exePath.contains("$PROJECT_DIR$"));
    assertTrue(exePath, exePath.endsWith("/scripts/gen.sh"));
  }

  public void testGrammarFilesMacroIsSubstitutedInPlace() {
    VirtualFile first = myFixture.configureByText("First.bnf", "root ::= 'x'").getVirtualFile();
    VirtualFile second = myFixture.configureByText("Second.bnf", "root ::= 'y'").getVirtualFile();

    GeneralCommandLine commandLine =
      GrammarKitCliRunner.buildCommandLine(getProject(), List.of(first, second), "gen.sh $GrammarFiles$ --out gen");

    assertEquals(List.of(VfsUtilCore.virtualToIoFile(first).getAbsolutePath(),
                         VfsUtilCore.virtualToIoFile(second).getAbsolutePath(),
                         "--out", "gen"),
                 commandLine.getParametersList().getList());
  }

  public void testGrammarFilesMacroAtTheEndBehavesLikeAppending() {
    VirtualFile file = myFixture.configureByText("Grammar.bnf", "root ::= 'x'").getVirtualFile();

    GeneralCommandLine commandLine =
      GrammarKitCliRunner.buildCommandLine(getProject(), List.of(file), "gen.sh -q $GrammarFiles$");

    assertEquals(List.of("-q", VfsUtilCore.virtualToIoFile(file).getAbsolutePath()),
                 commandLine.getParametersList().getList());
  }

  public void testGrammarFilesMacroEmbeddedInAnArgumentStaysLiteral() {
    VirtualFile file = myFixture.configureByText("Grammar.bnf", "root ::= 'x'").getVirtualFile();

    GeneralCommandLine commandLine =
      GrammarKitCliRunner.buildCommandLine(getProject(), List.of(file), "gen.sh --files=$GrammarFiles$");

    // Not a placeholder, so the paths are appended as usual and the argument is passed through.
    assertEquals(List.of("--files=$GrammarFiles$", VfsUtilCore.virtualToIoFile(file).getAbsolutePath()),
                 commandLine.getParametersList().getList());
  }

  public void testRepeatedGrammarFilesMacroIsRejected() {
    VirtualFile file = myFixture.configureByText("Grammar.bnf", "root ::= 'x'").getVirtualFile();
    try {
      GrammarKitCliRunner.buildCommandLine(getProject(), List.of(file), "gen.sh $GrammarFiles$ -- $GrammarFiles$");
      fail("expected IllegalArgumentException");
    }
    catch (IllegalArgumentException expected) {
      // ok
    }
  }

  public void testGrammarFilesMacroInTheExecutablePositionIsRejected() {
    VirtualFile file = myFixture.configureByText("Grammar.bnf", "root ::= 'x'").getVirtualFile();
    try {
      // Without the check the first grammar becomes the executable, and the run dies at spawn.
      GrammarKitCliRunner.buildCommandLine(getProject(), List.of(file), "$GrammarFiles$ --out gen");
      fail("expected IllegalArgumentException");
    }
    catch (IllegalArgumentException expected) {
      // ok
    }
  }

  public void testNoGrammarFilesIsRejected() {
    try {
      GrammarKitCliRunner.buildCommandLine(getProject(), List.of(), "gen.sh");
      fail("expected IllegalArgumentException");
    }
    catch (IllegalArgumentException expected) {
      // ok
    }
  }

  public void testPathMacroExpandingToAPathWithSpacesStaysOneArgument() {
    VirtualFile file = myFixture.configureByText("Grammar.bnf", "root ::= 'x'").getVirtualFile();
    PathMacros macros = PathMacros.getInstance();
    macros.setMacro(SPACED_MACRO, "/tmp/some dir");
    try {
      GeneralCommandLine commandLine =
        GrammarKitCliRunner.buildCommandLine(getProject(), List.of(file), "gen.sh --out $" + SPACED_MACRO + "$/gen");

      // Splitting before expansion: the space inside the expansion must not split the argument.
      List<String> parameters = commandLine.getParametersList().getList();
      assertEquals(parameters.toString(), 3, parameters.size());
      assertEquals("--out", parameters.get(0));
      assertTrue(parameters.get(1), parameters.get(1).contains("some dir"));
      assertTrue(parameters.get(1), parameters.get(1).endsWith("gen"));
    }
    finally {
      macros.setMacro(SPACED_MACRO, null);
    }
  }

  public void testEmptyCliIsRejected() {
    VirtualFile file = myFixture.configureByText("Grammar.bnf", "root ::= 'x'").getVirtualFile();
    try {
      GrammarKitCliRunner.buildCommandLine(getProject(), List.of(file), "   ");
      fail("expected IllegalArgumentException");
    }
    catch (IllegalArgumentException expected) {
      // ok
    }
  }
}
