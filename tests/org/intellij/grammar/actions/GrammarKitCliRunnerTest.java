/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.execution.process.ProcessOutputTypes;
import junit.framework.TestCase;
import org.intellij.grammar.actions.GrammarKitCliRunner.GeneratedFilesCollector;

import java.io.File;
import java.util.List;

/**
 * Tests for the {@code grammar-kit:generated} output convention, by which a command reports the
 * files it produced so that only those are refreshed.
 */
public class GrammarKitCliRunnerTest extends TestCase {

  private static final File WORK_DIR = new File(new File("").getAbsoluteFile(), "grammars");

  public void testNoMarkerLinesCollectNothing() {
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    collector.append("compiling Grammar.bnf\ndone in 12ms\n");
    assertTrue(collector.finish().isEmpty());
  }

  public void testRelativePathResolvesAgainstWorkDirectory() {
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    collector.append("grammar-kit:generated gen/Parser.java\n");
    assertEquals(List.of(new File(WORK_DIR, "gen/Parser.java")), collector.finish());
  }

  public void testAbsolutePathIsKeptAsIs() {
    File absolute = new File(WORK_DIR, "out/Parser.java").getAbsoluteFile();
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    collector.append("grammar-kit:generated " + absolute.getPath() + "\n");
    assertEquals(List.of(absolute), collector.finish());
  }

  public void testColonFormIsNotAMarker() {
    // One spelling only: the separator is whitespace.
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    collector.append("grammar-kit:generated: gen/Parser.java\n");
    assertTrue(collector.finish().isEmpty());
  }

  public void testPathWithSpacesNeedsNoQuoting() {
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    collector.append("grammar-kit:generated gen dir/My Parser.java\n");
    assertEquals(List.of(new File(WORK_DIR, "gen dir/My Parser.java")), collector.finish());
  }

  public void testLineSplitAcrossChunks() {
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    collector.append("grammar-kit:gen");
    collector.append("erated gen/Par");
    collector.append("ser.java\nnoise\n");
    assertEquals(List.of(new File(WORK_DIR, "gen/Parser.java")), collector.finish());
  }

  public void testTrailingLineWithoutNewlineIsConsumedOnFinish() {
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    collector.append("grammar-kit:generated gen/Parser.java");
    assertEquals(List.of(new File(WORK_DIR, "gen/Parser.java")), collector.finish());
  }

  public void testCrlfOutputIsHandled() {
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    collector.append("grammar-kit:generated gen/Parser.java\r\n");
    assertEquals(List.of(new File(WORK_DIR, "gen/Parser.java")), collector.finish());
  }

  public void testOrderIsPreservedAndDuplicatesCollapsed() {
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    collector.append("""
                       grammar-kit:generated gen/Parser.java
                       grammar-kit:generated gen/Psi.java
                       grammar-kit:generated gen/Parser.java
                       """);
    assertEquals(List.of(new File(WORK_DIR, "gen/Parser.java"), new File(WORK_DIR, "gen/Psi.java")),
                 collector.finish());
  }

  public void testStderrChunkDoesNotSpliceAStdoutMarker() {
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    collector.append(ProcessOutputTypes.STDOUT, "grammar-kit:generated gen/Par");
    collector.append(ProcessOutputTypes.STDERR, "warning: unused rule\n");
    collector.append(ProcessOutputTypes.STDOUT, "ser.java\n");
    assertEquals(List.of(new File(WORK_DIR, "gen/Parser.java")), collector.finish());
  }

  public void testMarkerOnStderrIsAlsoCollected() {
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    collector.append(ProcessOutputTypes.STDERR, "grammar-kit:generated gen/Parser.java\n");
    assertEquals(List.of(new File(WORK_DIR, "gen/Parser.java")), collector.finish());
  }

  public void testUnterminatedLinesOnBothStreamsAreFlushedOnFinish() {
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    collector.append(ProcessOutputTypes.STDOUT, "grammar-kit:generated gen/Parser.java");
    collector.append(ProcessOutputTypes.STDERR, "grammar-kit:generated gen/Psi.java");
    // The order the streams were first seen in, not whatever a hash map happens to produce.
    assertEquals(List.of(new File(WORK_DIR, "gen/Parser.java"), new File(WORK_DIR, "gen/Psi.java")),
                 collector.finish());
  }

  public void testFlushOrderFollowsTheOrderTheStreamsWereFirstSeenIn() {
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    collector.append(ProcessOutputTypes.STDERR, "grammar-kit:generated gen/Psi.java");
    collector.append(ProcessOutputTypes.STDOUT, "grammar-kit:generated gen/Parser.java");
    assertEquals(List.of(new File(WORK_DIR, "gen/Psi.java"), new File(WORK_DIR, "gen/Parser.java")),
                 collector.finish());
  }

  public void testCarriageReturnTerminatesALine() {
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    // A progress bar redrawing itself, then the marker, all without a single '\n'.
    collector.append("  0%\r 50%\r100%\rgrammar-kit:generated gen/Parser.java\r");
    assertEquals(List.of(new File(WORK_DIR, "gen/Parser.java")), collector.finish());
  }

  public void testUnterminatedOutputDoesNotGrowWithoutBound() {
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    for (int i = 0; i < 1000; i++) {
      collector.append("x".repeat(100));
    }
    collector.append("\ngrammar-kit:generated gen/Parser.java\n");
    assertEquals(List.of(new File(WORK_DIR, "gen/Parser.java")), collector.finish());
  }

  public void testPrefixWithoutSeparatorIsNotAMarker() {
    GeneratedFilesCollector collector = new GeneratedFilesCollector(WORK_DIR);
    collector.append("grammar-kit:generatedFiles=3\ngrammar-kit:generated\n");
    assertTrue(collector.finish().isEmpty());
  }
}
