/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.fleet;

import com.intellij.openapi.util.text.StringUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.generator.GenOptions;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;
import static org.intellij.grammar.generator.fleet.FleetConstants.FLEET_NAMESPACE;
import static org.intellij.grammar.generator.fleet.FleetConstants.FLEET_NAMESPACE_PREFIX;

public class FleetParserGenerator extends ParserGenerator {

  private final Collection<String> myPossibleImports;

  private final boolean myGenerateIFileType;
  private final String myFileTypeClassName;
  private final String myFileTypeDebugName;
  private final String myLanguageClass;

  public FleetParserGenerator(@NotNull BnfFile psiFile,
                              @NotNull String sourcePath,
                              @NotNull String outputPath,
                              @NotNull String packagePrefix) {
    super(psiFile, sourcePath, outputPath, packagePrefix);
    myPossibleImports = new LinkedList<>();
    var importList = psiFile.getAllPossibleAttributeValues(KnownAttribute.ELEMENT_TYPE_CLASS);
    if (importList != null) myPossibleImports.addAll(psiFile.getAllPossibleAttributeValues(KnownAttribute.ELEMENT_TYPE_CLASS));
    importList = psiFile.getAllPossibleAttributeValues(KnownAttribute.ELEMENT_TYPE_FACTORY);
    if (importList != null) myPossibleImports.addAll(importList.stream().map(StringUtil::getPackageName).collect(Collectors.toSet()));
    importList = psiFile.getAllPossibleAttributeValues(KnownAttribute.TOKEN_TYPE_CLASS);
    if (importList != null) myPossibleImports.addAll(importList);
    importList = psiFile.getAllPossibleAttributeValues(KnownAttribute.PARSER_CLASS);
    if (importList != null) myPossibleImports.addAll(importList);

    var rootImport = getRootAttribute(psiFile, KnownAttribute.PARSER_UTIL_CLASS);
    if (rootImport != null) myPossibleImports.add(rootImport);
    rootImport = getRootAttribute(psiFile, KnownAttribute.PSI_IMPL_UTIL_CLASS);
    if (rootImport != null) myPossibleImports.add(rootImport);
    rootImport = getRootAttribute(psiFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);
    if (rootImport != null) myPossibleImports.add(rootImport);

    var iFiletypeGenerationOptions = getRootAttribute(psiFile, KnownAttribute.FLEET_FILETYPE_GENERATION).asMap();
    myGenerateIFileType = !iFiletypeGenerationOptions.isEmpty();
    myFileTypeClassName = iFiletypeGenerationOptions.getOrDefault("fileTypeClass", "");
    myFileTypeDebugName = iFiletypeGenerationOptions.getOrDefault("debugName", "FILE");
    myLanguageClass = iFiletypeGenerationOptions.getOrDefault("languageClass", "");

    myPossibleImports.add(myFileTypeClassName);
    myPossibleImports.add(myLanguageClass);
    myPossibleImports.add(myGrammarRootParser);
  }

  @Override
  protected @NotNull GenOptions createGenerator(BnfFile file) {
    return new GenOptions(file, true);
  }

  @Override
  protected @NotNull String generatePackageName(String className) {
    var packageName = super.generatePackageName(className);
    if (G.adjustPackagesForFleet && !className.startsWith(FLEET_NAMESPACE)) {
      if (packageName.isEmpty()) {
        return FLEET_NAMESPACE;
      }
      return FLEET_NAMESPACE_PREFIX + packageName;
    }

    return packageName;
  }

  @Override
  protected void generateParseMethod(String shortAN, String shortET, String root, String shortPB, String builder) {
    out("public %s parse(%s %s, %s %s) {", shortAN, shortET, root, shortPB, builder);
    out("throw new IllegalStateException(\"Use parseLight instead\");");
    out("}");
  }

  @Override
  protected @NotNull String adjustName(String original) {
    switch (original) {
      case BnfConstants.GPUB_CLASS:
        return FleetConstants.GPUB_CLASS;
      case BnfConstants.PSI_BUILDER_CLASS:
        return FleetConstants.PSI_BUILDER_CLASS;
      case BnfConstants.PSI_PARSER_CLASS:
        return FleetConstants.PSI_PARSER_CLASS;
      case BnfConstants.LIGHT_PSI_PARSER_CLASS:
        return FleetConstants.LIGHT_PSI_PARSER_CLASS;
      case BnfConstants.TOKEN_SET_CLASS:
        return FleetConstants.TOKEN_SET_CLASS;
      case BnfConstants.IELEMENTTYPE_CLASS:
        return FleetConstants.IELEMENTTYPE_CLASS;
      case BnfConstants.PSI_ELEMENT_CLASS:
        return FleetConstants.PSI_ELEMENT_CLASS;
      case BnfConstants.AST_NODE_CLASS:
        return FleetConstants.AST_NODE_CLASS;
      default:
        break;
    }

    if (G.adjustPackagesForFleet &&
        (original.equals(myVisitorClassName) || myPossibleImports.contains(original)) &&
        !original.startsWith(FLEET_NAMESPACE)) {
      return FLEET_NAMESPACE_PREFIX + original;
    }

    return original;
  }

  @Override
  protected boolean useFactory(String factory) {
    return false;
  }

  @Override
  public @NotNull String shorten(@NotNull String s) {
    return super.shorten(adjustName(s));
  }

  @Override
  protected void generateAdditionalFiles() throws IOException {
    if (myGenerateIFileType) {
      openOutput(adjustName(myFileTypeClassName));
      try {
        generateFileTypeClass();
      }
      finally {
        closeOutput();
      }
    }
  }

  private void generateFileTypeClass() {
    var imports = new HashSet<String>();
    imports.add(FleetConstants.FLEET_FILE_ELEMENT_TYPE_CLASS);
    imports.add(FleetConstants.FLEET_PSI_BUILDER_CLASS);
    imports.add(adjustName(myLanguageClass));
    imports.add(adjustName(myGrammarRootParser));
    imports.add(BnfConstants.NOTNULL_ANNO);

    generateClassHeader(adjustName(myFileTypeClassName), imports, "", Java.CLASS, FleetConstants.FLEET_FILE_ELEMENT_TYPE_CLASS);

    out("public static final %s INSTANCE = new %s();", shorten(adjustName(myFileTypeClassName)), shorten(adjustName(myFileTypeClassName)));
    newLine();
    out("public %s() {", shorten(adjustName(myFileTypeClassName)));
    out("super(\"%s\", %s.INSTANCE);", shorten(myFileTypeDebugName), shorten(adjustName(myLanguageClass)));
    out("}");
    newLine();
    out(shorten(BnfConstants.OVERRIDE_ANNO));
    out("public void parse(%s %s<?> builder) {", shorten(BnfConstants.NOTNULL_ANNO), shorten(FleetConstants.FLEET_PSI_BUILDER_CLASS));
    out("new %s().parseLight(this, builder);", shorten(adjustName(myGrammarRootParser)));
    out("}");
    out("}");
  }
}
