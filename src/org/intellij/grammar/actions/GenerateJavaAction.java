/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import org.intellij.grammar.generator.JavaParserGenerator;

/**
 * @author gregory
 * Date: 15.07.11 17:12
 */
public class GenerateJavaAction extends GenerateActionBase {
  public GenerateJavaAction() {
    super(JavaParserGenerator::new);
  }
}
