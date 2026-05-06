/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.batch;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * Callback interface for generation events. All methods have no-op defaults.
 */
public interface GenerationListener {
  /**
   * Called before each grammar is processed; use to update a progress indicator.
   */
  default void onGrammarStarted(@NotNull VirtualFile bnfFile, int index, int total) {
  }

  default void onGrammarGenerated(@NotNull VirtualFile bnfFile, @NotNull SingleGrammarGenerationReport report) {
  }

  default void onGenerationFailed(@NotNull VirtualFile bnfFile, @NotNull Exception ex) {
  }
}
