/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.fleet.FleetBnfFileWrapper;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;

public abstract class GeneratorBase {
  private static final Logger LOG = Logger.getInstance(GeneratorBase.class);

  /**
   * The input BNF file to generate the parser from.
   */
  protected final BnfFile myFile;

  /**
   * Whether the generated parser should be generated for Fleet.
   */
  protected final Boolean myGenerateForFleet;

  protected final String myOutputPath;

  /**
   * The package prefix to use for the generated parser.
   */
  protected final String myPackagePrefix;
  protected final String mySourcePath;
  protected final String myGrammarRoot;
  protected final String myGrammarRootParser;
  /**
   * Name format used for the generated PSI interfaces.
   */
  protected final NameFormat myPsiInterfaceFormat;
  /**
   * Name format used for the generated implementation classes
   * of the PSI interfaces.
   */
  protected final NameFormat myImplClassFormat;
  protected final GenOptions G;
  private final @NotNull String myOutputFileExtension;
  private @NotNull final OutputOpener myOpener;
  protected NameShortener myShortener;
  private FilePrinter myPrinter;

  protected GeneratorBase(@NotNull BnfFile psiFile,
                          @NotNull String sourcePath,
                          @NotNull String outputPath,
                          @NotNull String packagePrefix,
                          @NotNull String outputFileExtension,
                          @NotNull OutputOpener outputOpener) {
    myFile = psiFile;
    myGenerateForFleet = psiFile instanceof FleetBnfFileWrapper;

    G = new GenOptions(psiFile);
    mySourcePath = sourcePath;
    myOutputPath = outputPath;
    myPackagePrefix = packagePrefix;
    myOutputFileExtension = outputFileExtension;
    myOpener = outputOpener;
    myPsiInterfaceFormat = getPsiClassFormat(myFile);
    myImplClassFormat = getPsiImplClassFormat(myFile);

    List<BnfRule> rules = psiFile.getRules();
    BnfRule rootRule = rules.isEmpty() ? null : rules.get(0);
    myGrammarRoot = rootRule == null ? null : rootRule.getName();
    myGrammarRootParser = rootRule == null ? null : getRootAttribute(rootRule, KnownAttribute.PARSER_CLASS);
  }

  public abstract void generate() throws IOException;

  public abstract void generateParser() throws IOException;

  protected final void generateFileHeader(String className) {
    String header = getRootAttribute(myFile, KnownAttribute.CLASS_HEADER, className);
    String text = StringUtil.isEmpty(header) ? "" : getStringOrFile(header);
    if (StringUtil.isNotEmpty(text)) {
      out(text);
    }
    resetOffset();
  }

  /**
   * If the classHeader is a file path, loads the file content and returns it.
   * If it's a string representing a comment, returns it as is.
   * Otherwise, wraps the string in a comment.
   */
  private final String getStringOrFile(String classHeader) {
    try {
      File file = new File(mySourcePath, classHeader);
      if (file.exists()) return FileUtil.loadFile(file);
    }
    catch (IOException ex) {
      LOG.error(ex);
    }
    return classHeader.startsWith("//") || classHeader.startsWith("/*")
           ? classHeader
           : StringUtil.countNewLines(classHeader) > 0
             ? "/*\n" + classHeader + "\n*/"
             :
             "// " + classHeader;
  }

  protected void openOutput(String className) throws IOException {
    String classNameAdjusted = myPackagePrefix.isEmpty() ? className : StringUtil.trimStart(className, myPackagePrefix + ".");
    File file = new File(myOutputPath, classNameAdjusted.replace('.', File.separatorChar) + "." + myOutputFileExtension);
    myPrinter = new FilePrinter(myOpener.openOutput(className, file, myFile));
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

  protected enum TypeKind {CLASS, INTERFACE, ABSTRACT_CLASS}
}
