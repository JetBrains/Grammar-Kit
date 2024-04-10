/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.fleet;

import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

public class FleetParserGenerator extends ParserGenerator {

  private final String intellijNamespacePrefix = "com.intellij";

  public FleetParserGenerator(@NotNull BnfFile psiFile,
                              @NotNull String sourcePath,
                              @NotNull String outputPath,
                              @NotNull String packagePrefix) {
    super(psiFile, sourcePath, outputPath, packagePrefix);
    G = new FleetGenOptions(psiFile);
  }

  protected String adjustNameSpace(String original){
    if (original.startsWith(intellijNamespacePrefix))
      return BnfConstants.FLEET_NAMESPACE_PREFIX + original;

    return original;
  }

  @Override
  protected @NotNull String generatePackageName(String className){
    return BnfConstants.FLEET_NAMESPACE_PREFIX + super.generatePackageName(className);
  }

  @Override
  protected void generateParseMethod(String shortAN, String shortET, String shortPB){
    out("public %s parse(%s %s, %s %s) {", shortAN, shortET, N.root, shortPB, N.builder);
    out("throw new IllegalStateException(\"Use parseLight instead\");");
    out("}");
  }

  @Override
  protected @NotNull String generateImport(String s){
    return BnfConstants.FLEET_NAMESPACE_PREFIX + s;
  }

  @Override
  public @NotNull String shorten(@NotNull String s){
    return super.shorten(BnfConstants.FLEET_NAMESPACE_PREFIX + s);
  }
}
