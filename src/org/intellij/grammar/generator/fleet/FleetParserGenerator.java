/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.fleet;

import com.intellij.openapi.util.text.StringUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;
import static org.intellij.grammar.generator.fleet.FleetConstants.FLEET_NAMESPACE;
import static org.intellij.grammar.generator.fleet.FleetConstants.FLEET_NAMESPACE_PREFIX;

public class FleetParserGenerator extends ParserGenerator {

  private final boolean myAdjustGeneratedNamespaces;
  private final Collection<String> myPossibleImports;

  public FleetParserGenerator(@NotNull BnfFile psiFile,
                              @NotNull String sourcePath,
                              @NotNull String outputPath,
                              @NotNull String packagePrefix) {
    super(psiFile, sourcePath, outputPath, packagePrefix);
    G = new FleetGenOptions(psiFile);
    myPossibleImports = new LinkedList<>();
    var importList = psiFile.getPossibleAttributeValues(KnownAttribute.ELEMENT_TYPE_CLASS);
    if (importList != null) myPossibleImports.addAll(psiFile.getPossibleAttributeValues(KnownAttribute.ELEMENT_TYPE_CLASS));
    importList = psiFile.getPossibleAttributeValues(KnownAttribute.ELEMENT_TYPE_FACTORY);
    if (importList != null) myPossibleImports.addAll(importList.stream().map(StringUtil::getPackageName).collect(Collectors.toSet()));
    importList = psiFile.getPossibleAttributeValues(KnownAttribute.TOKEN_TYPE_CLASS);
    if (importList != null) myPossibleImports.addAll(importList);
    importList = psiFile.getPossibleAttributeValues(KnownAttribute.TOKEN_TYPE_FACTORY);
    if (importList != null) myPossibleImports.addAll(importList.stream().map(StringUtil::getPackageName).collect(Collectors.toSet()));
    //importList = psiFile.getPossibleAttributeValues(KnownAttribute.PARSER_UTIL_CLASS);
    //if (importList != null) myPossibleImports.addAll(importList);
    //importList = psiFile.getPossibleAttributeValues(KnownAttribute.PSI_IMPL_UTIL_CLASS);
    //if (importList != null) myPossibleImports.addAll(importList);
    //importList = psiFile.getPossibleAttributeValues(KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);
    //if (importList != null) myPossibleImports.addAll(importList);
    importList = psiFile.getPossibleAttributeValues(KnownAttribute.PARSER_CLASS);
    if (importList != null) myPossibleImports.addAll(importList);

    var rootImport = getRootAttribute(psiFile, KnownAttribute.PARSER_UTIL_CLASS);
    if (rootImport != null) myPossibleImports.add(rootImport);
    rootImport = getRootAttribute(psiFile, KnownAttribute.PSI_IMPL_UTIL_CLASS);
    if (rootImport != null) myPossibleImports.add(rootImport);
    rootImport = getRootAttribute(psiFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);
    if (rootImport != null) myPossibleImports.add(rootImport);

    myAdjustGeneratedNamespaces = getRootAttribute(psiFile, KnownAttribute.ADJUST_FLEET_PACKAGE);
  }

  @Override
  protected @NotNull String generatePackageName(String className){
    var packageName = super.generatePackageName(className);
    if (myAdjustGeneratedNamespaces) {
      if (packageName.isEmpty())
        return FLEET_NAMESPACE;
      return FLEET_NAMESPACE_PREFIX + packageName;
    }

    return packageName;
  }

  @Override
  protected void generateParseMethod(String shortAN, String shortET, String shortPB){
    out("public %s parse(%s %s, %s %s) {", shortAN, shortET, N.root, shortPB, N.builder);
    out("throw new IllegalStateException(\"Use parseLight instead\");");
    out("}");
  }

  @Override
  protected @NotNull String adjustName(String original){
    switch (original){
      case BnfConstants.GPUB_CLASS: return FleetConstants.GPUB_CLASS;
      case BnfConstants.PSI_BUILDER_CLASS : return FleetConstants.PSI_BUILDER_CLASS;
      case BnfConstants.PSI_PARSER_CLASS : return FleetConstants.PSI_PARSER_CLASS;
      case BnfConstants.LIGHT_PSI_PARSER_CLASS : return FleetConstants.LIGHT_PSI_PARSER_CLASS;
      case BnfConstants.TOKEN_SET_CLASS : return FleetConstants.TOKEN_SET_CLASS;

      case BnfConstants.IELEMENTTYPE_CLASS : return FleetConstants.IELEMENTTYPE_CLASS;
      case BnfConstants.PSI_ELEMENT_CLASS : return FleetConstants.PSI_ELEMENT_CLASS;
      case BnfConstants.AST_NODE_CLASS : return FleetConstants.AST_NODE_CLASS;
      default:
        break;
    }

    if (myAdjustGeneratedNamespaces && (original.equals(myVisitorClassName) || myPossibleImports.contains(original)))
    {
      return FLEET_NAMESPACE_PREFIX + original;
    }

    return original;
  }

  @Override
  public @NotNull String shorten(@NotNull String s){
    return super.shorten(adjustName(s));
  }
}
