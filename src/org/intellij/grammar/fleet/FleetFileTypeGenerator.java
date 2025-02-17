/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.fleet;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.JBIterable;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.generator.Case;
import org.intellij.grammar.generator.GeneratorBase;
import org.intellij.grammar.generator.OutputOpener;
import org.intellij.grammar.generator.java.JavaNameShortener;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.intellij.grammar.fleet.FleetConstants.FLEET_NAMESPACE;
import static org.intellij.grammar.fleet.FleetConstants.FLEET_NAMESPACE_PREFIX;

public class FleetFileTypeGenerator extends GeneratorBase {

  private final String myFileTypeClassName;
  private final String myFileTypeDebugName;
  private final String myLanguageClass;

  public FleetFileTypeGenerator(@NotNull BnfFile psiFile,
                                @NotNull String sourcePath,
                                @NotNull String outputPath,
                                @NotNull String packagePrefix,
                                @NotNull String classFileTypeName,
                                String debugFileTypeName,
                                @NotNull String languageClass,
                                @NotNull OutputOpener outputOpener
  ) {
    super(psiFile, sourcePath, outputPath, packagePrefix, "java", outputOpener);
    myFileTypeClassName = (!classFileTypeName.startsWith(FLEET_NAMESPACE_PREFIX)
                           ? FLEET_NAMESPACE_PREFIX
                           : "") + classFileTypeName;
    myLanguageClass = (!languageClass.startsWith(FLEET_NAMESPACE_PREFIX) ? FLEET_NAMESPACE_PREFIX : "") +
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

  protected void generateClassHeader(String className, Set<String> imports, String annos, TypeKind typeKind, String... supers) {
    generateFileHeader(className);
    String packageName = getPackageName(className);
    String shortClassName = StringUtil.getShortName(className);
    out("package %s;", packageName);
    newLine();
    JavaNameShortener shortener = new JavaNameShortener(packageName, !G.generateFQN);
    Set<String> includedClasses = collectClasses(imports, packageName);
    shortener.addImports(imports, includedClasses);
    for (String s : shortener.getImports()) {
      out("import %s;", s);
    }
    if (G.generateFQN && imports.contains("#forced")) {
      for (String s : JBIterable.from(imports).filter(o -> !"#forced".equals(o))) {
        out("import %s;", s);
      }
    }
    newLine();
    StringBuilder sb = new StringBuilder();
    for (int i = 0, supersLength = supers.length; i < supersLength; i++) {
      String aSuper = supers[i];
      if (StringUtil.isEmpty(aSuper)) continue;
      if (imports.contains(aSuper + ";")) {
        aSuper = StringUtil.getShortName(aSuper);
      }
      if (i == 0) {
        sb.append(" extends ").append(shortener.shorten(aSuper));
      }
      else if (typeKind != TypeKind.INTERFACE && i == 1) {
        sb.append(" implements ").append(shortener.shorten(aSuper));
      }
      else {
        sb.append(", ").append(shortener.shorten(aSuper));
      }
    }
    if (StringUtil.isNotEmpty(annos)) {
      out(shortener.shorten(annos));
    }
    out("public %s %s%s {", Case.LOWER.apply(typeKind.name()).replace('_', ' '), shortClassName, sb.toString());
    newLine();
    myShortener = shortener;
  }

  @NotNull
  protected String getPackageName(String className) {
    final var packageName = StringUtil.getPackageName(className);
    if (myGenerateForFleet && !className.startsWith(FLEET_NAMESPACE)) {
      if (packageName.isEmpty()) {
        return FLEET_NAMESPACE;
      }
      return FLEET_NAMESPACE_PREFIX + packageName;
    }
    return packageName;
  }

  protected @NotNull Set<String> collectClasses(Set<String> imports, String packageName) {
    return Set.of();
  }

  @Override
  public void generateParser() {
    generateFileTypeClass();
  }

  private void generateFileTypeClass() {
    var imports = new HashSet<String>();
    imports.add(myLanguageClass);
    imports.add(FleetConstants.FLEET_FILE_ELEMENT_TYPE_CLASS);
    imports.add(FleetConstants.PSI_BUILDER_CLASS);
    imports.add(myGrammarRootParser);
    imports.add(BnfConstants.NOTNULL_ANNO);

    generateClassHeader(myFileTypeClassName, imports, "", TypeKind.CLASS, FleetConstants.FLEET_FILE_ELEMENT_TYPE_CLASS);

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
