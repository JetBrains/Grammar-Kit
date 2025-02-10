/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.JBIterable;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.fleet.FleetBnfFileWrapper;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;

public abstract class GeneratorBase {
  private static final Logger LOG = Logger.getInstance(GeneratorBase.class);

  protected final BnfFile myFile;

  protected final Boolean myGenerateForFleet;

  protected final String myOutputPath;
  protected final String myPackagePrefix;
  private final @NotNull String myOutputFileExtension;
  protected final String mySourcePath;
  protected final String myGrammarRoot;
  protected final String myGrammarRootParser;

  protected final NameFormat myIntfClassFormat;
  protected final NameFormat myImplClassFormat;

  private FilePrinter myPrinter;
  protected NameShortener myShortener;

  protected final GenOptions G;

  protected NameShortener getShortener() {
    return myShortener;
  }

  protected enum Java {CLASS, INTERFACE, ABSTRACT_CLASS}

  protected GeneratorBase(@NotNull BnfFile psiFile,
                          @NotNull String sourcePath,
                          @NotNull String outputPath,
                          @NotNull String packagePrefix,
                          @NotNull String outputFileExtension) {
    myFile = psiFile;
    myGenerateForFleet = psiFile instanceof FleetBnfFileWrapper;

    G = new GenOptions(psiFile);
    mySourcePath = sourcePath;
    myOutputPath = outputPath;
    myPackagePrefix = packagePrefix;
    myOutputFileExtension = outputFileExtension;
    myIntfClassFormat = getPsiClassFormat(myFile);
    myImplClassFormat = getPsiImplClassFormat(myFile);

    List<BnfRule> rules = psiFile.getRules();
    BnfRule rootRule = rules.isEmpty() ? null : rules.get(0);
    myGrammarRoot = rootRule == null ? null : rootRule.getName();
    myGrammarRootParser = rootRule == null ? null : getRootAttribute(rootRule, KnownAttribute.PARSER_CLASS);
  }

  public abstract void generate() throws IOException;

  protected void generateClassHeader(String className,
                                     Set<String> imports,
                                     String annos,
                                     Java javaType,
                                     String... supers) {
    generateFileHeader(className);
    String packageName = generatePackageName(className);
    String shortClassName = StringUtil.getShortName(className);
    out("package %s;", packageName);
    newLine();
    NameShortener shortener = new NameShortener(packageName, !G.generateFQN);
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
      else if (javaType != ParserGenerator.Java.INTERFACE && i == 1) {
        sb.append(" implements ").append(shortener.shorten(aSuper));
      }
      else {
        sb.append(", ").append(shortener.shorten(aSuper));
      }
    }
    if (StringUtil.isNotEmpty(annos)) {
      out(shortener.shorten(annos));
    }
    out("public %s %s%s {", Case.LOWER.apply(javaType.name()).replace('_', ' '), shortClassName, sb.toString());
    newLine();
    myShortener = shortener;
  }

  protected abstract @NotNull Set<String> collectClasses(Set<String> imports, String packageName);

  protected void generateFileHeader(String className) {
    String header = getRootAttribute(myFile, KnownAttribute.CLASS_HEADER, className);
    String text = StringUtil.isEmpty(header) ? "" : getStringOrFile(header);
    if (StringUtil.isNotEmpty(text)) {
      out(text);
    }
    resetOffset();
  }
  
  
  @NotNull
  protected abstract String generatePackageName(String className);

  /**
   * If the classHeader is a file path, loads the file content and returns it.
   * If it's a string representing a comment, returns it as is.
   * Otherwise, wraps the string in a comment.
   */
  private String getStringOrFile(String classHeader) {
    try {
      File file = new File(mySourcePath, classHeader);
      if (file.exists()) return FileUtil.loadFile(file);
    }
    catch (IOException ex) {
      LOG.error(ex);
    }
    return classHeader.startsWith("//") || classHeader.startsWith("/*") ? classHeader :
           StringUtil.countNewLines(classHeader) > 0 ? "/*\n" + classHeader + "\n*/" :
           "// " + classHeader;
  }

  protected void openOutput(String className) throws IOException {
    String classNameAdjusted = myPackagePrefix.isEmpty() ? className : StringUtil.trimStart(className, myPackagePrefix + ".");
    File file = new File(myOutputPath, classNameAdjusted.replace('.', File.separatorChar) + "." + myOutputFileExtension);
    myPrinter = new FilePrinter(openOutputInner(className, file));
  }

  protected PrintWriter openOutputInner(String className, File file) throws IOException {
    //noinspection ResultOfMethodCallIgnored
    file.getParentFile().mkdirs();
    return new PrintWriter(new FileOutputStream(file), false, this.myFile.getVirtualFile().getCharset());
  }

  protected void closeOutput() throws IOException {
    myPrinter.close();
  }

  public void out(String s, Object... args) {
    myPrinter.out(s, args);
  }

  public void out(String s) {
    myPrinter.out(s);
  }

  public void newLine() {
    out("");
  }

  public @NotNull String shorten(@NotNull String s) {
    return myShortener.shorten(s);
  }

  protected void resetOffset() {
    myPrinter.resetOffset();
  }
}
