/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.fleet.FleetBnfFileWrapper;
import org.intellij.grammar.generator.fleet.FleetConstants;
import org.jetbrains.annotations.Nullable;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;

public class GenerateFleetAction extends GenerateAction {

  @Override
  protected String getParserClass(PsiFile bnfFile) {
    var original = super.getParserClass(bnfFile);
    if (adjustPackages(bnfFile) && !original.startsWith(FleetConstants.FLEET_NAMESPACE_PREFIX)) {
      original = FleetConstants.FLEET_NAMESPACE_PREFIX + original;
    }
    return original;
  }

  private static boolean adjustPackages(PsiFile file) {
    return getRootAttribute(file, KnownAttribute.GENERATE).stream()
      .noneMatch(pair -> pair.first.equals("adjustPackagesForFleet") && pair.second.equals("no"));
  }

  @Override
  protected @Nullable PsiFile getBnfFile(VirtualFile file, PsiManager psiManager) {
    var psiFile = super.getBnfFile(file, psiManager);
    if (psiFile == null) return null;
    var viewProvider = psiFile.getViewProvider();
    return new FleetBnfFileWrapper(viewProvider);
  }
}
