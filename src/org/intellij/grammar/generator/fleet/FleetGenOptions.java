/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.fleet;

import org.intellij.grammar.generator.GenOptions;
import org.intellij.grammar.psi.BnfFile;


public class FleetGenOptions extends GenOptions {
  public FleetGenOptions(BnfFile myFile) {
    super(myFile);
  }

  @Override
  public boolean getGeneratePsi(){ return false; }
  @Override
  public boolean getGeneratePsiFactory(){ return false; }
  @Override
  public boolean getGeneratePsiClassesMap(){ return false; }
}
