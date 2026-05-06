/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.intellij.grammar.generator.batch.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Integration test verifying that {@link BnfGenerationService#prepareGenerationContext} and
 * {@link BnfGenerationService#generateInBatch} work end-to-end.
 *
 * <p>Uses {@link JavaCodeInsightFixtureTestCase} (JUnit4, heavy fixture) so the project is backed
 * by a real local filesystem, which is required by the generator when writing output files.
 */
public class BnfGenerationServiceIntegrationPlatformTest extends JavaCodeInsightFixtureTestCase {
  private static final String BNF_CONTENT =
    """
      {
        parserClass="com.example.TestParser"
        psiPackage=""
        psiImplPackage=""
      }
      root ::= 'x'
      """;

  @Override
  protected boolean runInDispatchThread() {
    return false;
  }

  public void testPrepareContextAndGenerateInBatch() throws Exception {
    VirtualFile bnfFile = myFixture.configureByText("Grammar.bnf", BNF_CONTENT).getVirtualFile();

    BatchGenerationContext context = WriteAction.computeAndWait(
      () -> BnfGenerationService.prepareGenerationContext(getProject(), List.of(bnfFile))
    );

    assertNotNull("prepareGenerationContext must resolve a target directory for the BNF file",
                  context.rootMap().get(bnfFile));

    List<VirtualFile> generatedFiles = new ArrayList<>();
    List<VirtualFile> failedFiles = new ArrayList<>();

    BatchGenerationResult result = BnfGenerationService.generateInBatch(
      context,
      new GenerationListener() {
        @Override
        public void onGrammarGenerated(@NotNull VirtualFile f, @NotNull SingleGrammarGenerationReport r) {
          generatedFiles.add(f);
        }

        @Override
        public void onGenerationFailed(@NotNull VirtualFile f, @NotNull Exception ex) {
          failedFiles.add(f);
        }
      }
    );

    assertTrue("Generation should not fail, but got: " + failedFiles, failedFiles.isEmpty());
    assertEquals(List.of(bnfFile), generatedFiles);
    assertEquals(1, result.filesProcessed());
    assertFalse("Generator must write at least one file", result.files().isEmpty());
    assertTrue("Generator must write some bytes", result.totalWritten() > 0);
  }
}
