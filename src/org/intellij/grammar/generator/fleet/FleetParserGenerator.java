/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.fleet;

import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
  protected @NotNull Set<String> generateParserImports(Collection<String> ownRuleNames, boolean rootParser, List<String> parserImports){
    var result = super.generateParserImports(ownRuleNames, rootParser, parserImports);
    return result.stream().map(this::adjustNameSpace).collect(Collectors.toSet());
  }

  @Override
  protected @NotNull Set<String> generateElementTypeImports(Map<String, BnfRule> sortedCompositeTypes,
                                                            Map<String, FactoryCompositeRecord> compositeToClassAndFactoryMap,
                                                            String tokenTypeFactory, String tokenTypeClass) {
    var initialResult = super.generateElementTypeImports(sortedCompositeTypes, compositeToClassAndFactoryMap, tokenTypeFactory, tokenTypeClass);
    return initialResult.stream().map(this::adjustNameSpace).collect(Collectors.toSet());
  }
}
