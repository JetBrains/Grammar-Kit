/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import org.intellij.grammar.generator.KotlinParserGenerator;

public final class GenerateKotlinAction extends GenerateActionBase {
  public GenerateKotlinAction() {
    super(KotlinParserGenerator::new);
  }
}
