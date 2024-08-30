/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.fleet;

import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.generator.GeneratorBase;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class FleetFileTypeGenerator extends GeneratorBase {

  private final String myFileTypeClassName;
  private final String myFileTypeDebugName;
  private final String myLanguageClass;

  public FleetFileTypeGenerator(@NotNull BnfFile psiFile,
                                @NotNull String sourcePath,
                                @NotNull String outputPath,
                                @NotNull String packagePrefix,
                                String classFileTypeName,
                                String debugFileTypeName,
                                String languageClass) {
    super(psiFile, sourcePath, outputPath, packagePrefix);
    myFileTypeClassName = (G.adjustPackagesForFleet &&
                           !classFileTypeName.startsWith(FleetConstants.FLEET_NAMESPACE_PREFIX)
                           ? FleetConstants.FLEET_NAMESPACE_PREFIX
                           : "") + classFileTypeName;
    myLanguageClass = (G.adjustPackagesForFleet &&
                       !languageClass.startsWith(FleetConstants.FLEET_NAMESPACE_PREFIX) ? FleetConstants.FLEET_NAMESPACE_PREFIX : "") +
                      languageClass;
    myFileTypeDebugName = debugFileTypeName;
  }

  @Override
  public void generate() throws IOException {
    openOutput(myFileTypeClassName);
    try {
      generateFileTypeClass();
    }
    finally {
      closeOutput();
    }
  }

  @Override
  protected @NotNull Set<String> collectClasses(Set<String> imports, String packageName) {
    return Set.of();
  }

  private void generateFileTypeClass() {
    var imports = new HashSet<String>();
    imports.add(myLanguageClass);
    imports.add(FleetConstants.FLEET_FILE_ELEMENT_TYPE_CLASS);
    imports.add(FleetConstants.PSI_BUILDER_CLASS);
    imports.add(myGrammarRootParser);
    imports.add(BnfConstants.NOTNULL_ANNO);

    generateClassHeader(myFileTypeClassName, imports, "", Java.CLASS, FleetConstants.FLEET_FILE_ELEMENT_TYPE_CLASS);

    out("public static final %s INSTANCE = new %s();", shorten(myFileTypeClassName), shorten(myFileTypeClassName));
    newLine();
    out("public %s() {", shorten(myFileTypeClassName));
    out("super(\"%s\", %s.INSTANCE);", shorten(myFileTypeDebugName), shorten(myLanguageClass));
    out("}");
    newLine();
    out(shorten(BnfConstants.OVERRIDE_ANNO));
    out("public void parse(%s %s<?> builder) {", shorten(BnfConstants.NOTNULL_ANNO), shorten(FleetConstants.PSI_BUILDER_CLASS));
    out("new %s().parseLight(this, builder);", shorten(myGrammarRootParser));
    out("}");
    out("}");
  }
}
