/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;

public abstract class Generator {
  private static final Logger LOG = Logger.getInstance(Generator.class);

  /**
   * The input BNF file to generate the parser from.
   */
  protected final @NotNull BnfFile myFile;
  protected final @NotNull String myOutputPath;

  /**
   * The package prefix to use for the generated parser.
   */
  protected final @NotNull String myPackagePrefix;
  protected final @NotNull String mySourcePath;
  protected final @Nullable String myGrammarRoot;
  protected final @Nullable String myGrammarRootParser;
  protected final @NotNull GenOptions G;
  private final @NotNull String myOutputFileExtension;
  private final @NotNull OutputOpener myOpener;
  protected NameShortener myShortener;
  private FilePrinter myPrinter;

  protected Generator(@NotNull BnfFile psiFile,
                      @NotNull String sourcePath,
                      @NotNull String outputPath,
                      @NotNull String packagePrefix,
                      @NotNull String outputFileExtension,
                      @NotNull OutputOpener outputOpener) {
    myFile = psiFile;

    G = new GenOptions(psiFile);
    mySourcePath = sourcePath;
    myOutputPath = outputPath;
    myPackagePrefix = packagePrefix;
    myOutputFileExtension = outputFileExtension;
    myOpener = outputOpener;

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
  private String getStringOrFile(String classHeader) {
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

  protected void closeOutput() {
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
