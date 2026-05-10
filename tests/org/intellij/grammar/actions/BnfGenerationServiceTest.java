/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.grammar.BnfGeneratorTestCase;
import org.intellij.grammar.generator.Generator;
import org.intellij.grammar.generator.JavaParserGenerator;
import org.intellij.grammar.generator.KotlinParserGenerator;
import org.intellij.grammar.generator.batch.*;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class BnfGenerationServiceTest extends BnfGeneratorTestCase {

  public BnfGenerationServiceTest() {
    super("java");
  }

  // ---- BatchGenerationResult record tests ----

  public void testEmptyBatchResult() {
    VirtualFile vf = createPsiFile("t.bnf", "{ }").getVirtualFile();
    List<VirtualFile> bnfFiles = List.of(vf);

    var result = BatchGenerationResult.empty(getProject(), bnfFiles);

    assertSame(bnfFiles, result.bnfFiles());
    assertSame(getProject(), result.project());
    assertEquals(0, result.filesProcessed());
    assertEquals(0L, result.totalWritten());
    assertTrue(result.files().isEmpty());
    assertTrue(result.targets().isEmpty());
  }

  public void testBatchResultAppendAccumulates() {
    VirtualFile bnfVf = createPsiFile("t.bnf", "{ }").getVirtualFile();
    VirtualFile targetVf = createPsiFile("t2.bnf", "{ }").getVirtualFile();
    File file = new File("Test.java");

    var initial = BatchGenerationResult.empty(getProject(), List.of(bnfVf));
    var report = new SingleGrammarGenerationReport(
      false, List.of(targetVf), List.of(file), 42L, null, new File(".")
    );

    var result = initial.append(report);

    assertEquals(1, result.filesProcessed());
    assertEquals(42L, result.totalWritten());
    assertEquals(List.of(file), result.files());
    assertTrue(result.targets().contains(targetVf));
  }

  public void testBatchResultAppendTwice() {
    VirtualFile bnfVf = createPsiFile("t.bnf", "{ }").getVirtualFile();
    VirtualFile targetVf1 = createPsiFile("t2.bnf", "{ }").getVirtualFile();
    VirtualFile targetVf2 = createPsiFile("t3.bnf", "{ }").getVirtualFile();
    File file1 = new File("A.java");
    File file2 = new File("B.java");

    var initial = BatchGenerationResult.empty(getProject(), List.of(bnfVf));
    var report1 = new SingleGrammarGenerationReport(
      false, List.of(targetVf1), List.of(file1), 10L, null, new File(".")
    );
    var report2 = new SingleGrammarGenerationReport(
      false, List.of(targetVf2), List.of(file2), 20L, null, new File(".")
    );

    var result = initial.append(report1).append(report2);

    assertEquals(2, result.filesProcessed());
    assertEquals(30L, result.totalWritten());
    assertEquals(2, result.files().size());
    assertTrue(result.targets().contains(targetVf1));
    assertTrue(result.targets().contains(targetVf2));
  }

  public void testNotFoundReport() {
    var report = SingleGrammarGenerationReport.notFound();

    assertTrue(report.targetNotFound());
    assertTrue(report.targets().isEmpty());
    assertTrue(report.files().isEmpty());
    assertEquals(0L, report.bytesWritten());
    assertNull(report.duration());
    assertNull(report.genDir());
  }

  // ---- generateInBatch control flow tests ----

  public void testGenerateInBatchEmptyList() {
    var ctx = new BatchGenerationContext(
      getProject(), List.of(),
      new LinkedHashMap<>(), new LinkedHashMap<>(),
      new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>(),
      new LinkedHashMap<>()
    );

    List<VirtualFile> started = new ArrayList<>();
    GenerationListener listener = new GenerationListener() {
      @Override
      public void onGrammarStarted(@NotNull VirtualFile f, int i, int t) { started.add(f); }
    };

    var result = BnfGenerationService.generateInBatch(ctx, listener);

    assertTrue(started.isEmpty());
    assertEquals(0, result.filesProcessed());
  }

  public void testGenerateInBatchStopsOnTargetNotFound() {
    VirtualFile vf1 = createPsiFile("a.bnf", "{ }").getVirtualFile();
    VirtualFile vf2 = createPsiFile("b.bnf", "{ }").getVirtualFile();
    // Empty rootMap: get() returns null → targetNotFound, batch stops after first file
    var ctx = new BatchGenerationContext(
      getProject(), List.of(vf1, vf2),
      new LinkedHashMap<>(), new LinkedHashMap<>(),
      new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>(),
      new LinkedHashMap<>()
    );

    List<VirtualFile> startedFiles = new ArrayList<>();
    List<Integer> startedIndices = new ArrayList<>();
    GenerationListener listener = new GenerationListener() {
      @Override
      public void onGrammarStarted(@NotNull VirtualFile f, int i, int t) {
        startedFiles.add(f);
        startedIndices.add(i);
      }
    };

    var result = BnfGenerationService.generateInBatch(ctx, listener);

    assertEquals(List.of(vf1), startedFiles);
    assertEquals(List.of(0), startedIndices);
    assertEquals(0, result.filesProcessed());
  }

  public void testGenerateInBatchNoGeneratedCallbackForTargetNotFound() {
    VirtualFile vf = createPsiFile("a.bnf", "{ }").getVirtualFile();
    var ctx = new BatchGenerationContext(
      getProject(), List.of(vf),
      new LinkedHashMap<>(), new LinkedHashMap<>(),
      new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>(),
      new LinkedHashMap<>()
    );

    List<VirtualFile> generated = new ArrayList<>();
    List<VirtualFile> failed = new ArrayList<>();
    GenerationListener listener = new GenerationListener() {
      @Override
      public void onGrammarGenerated(@NotNull VirtualFile f, @NotNull SingleGrammarGenerationReport r) { generated.add(f); }
      @Override
      public void onGenerationFailed(@NotNull VirtualFile f, @NotNull Exception ex) { failed.add(f); }
    };

    BnfGenerationService.generateInBatch(ctx, listener);

    assertTrue(generated.isEmpty());
    assertTrue(failed.isEmpty());
  }

  // ---- createGenerator factory tests ----

  public void testCreateGeneratorJava() {
    BnfFile bnfFile = (BnfFile)createPsiFile("Test.bnf",
                                              "{ parser-class=\"com.example.TestParser\" }\nroot ::= 'x'");

    Generator gen = BnfGenerationService.createGenerator(
      bnfFile, "", "", new ArrayList<>()
    );

    assertInstanceOf(gen, JavaParserGenerator.class);
  }

  public void testCreateGeneratorKotlin() {
    BnfFile bnfFile = (BnfFile)createPsiFile("Test.bnf",
                                              "{ generate=[parser-api=\"syntax\"] parser-class=\"com.example.TestParser\" }\nroot ::= 'x'");

    Generator gen = BnfGenerationService.createGenerator(
      bnfFile, "", "", new ArrayList<>()
    );

    assertInstanceOf(gen, KotlinParserGenerator.class);
  }
}
