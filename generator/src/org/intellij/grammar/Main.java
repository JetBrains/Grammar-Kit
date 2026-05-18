/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.lang.LanguageASTFactory;
import com.intellij.lang.LanguageBraceMatching;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import org.intellij.grammar.generator.JavaParserGenerator;
import org.intellij.grammar.generator.OutputOpener;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Command-line interface to parser generator.
 * Required community jars on classpath:
 * app-client.jar, lib-client.jar, opentelemetry.jar, util.jar, util-8.jar, util_rt.jar
 *
 * <p>New form: {@code generate <grammar-file> [options]}.<br>
 * Legacy form: {@code generate <output-dir> <grammars or patterns>} (deprecated).
 *
 * @author gregsh
 * @noinspection UseOfSystemOutOrSystemErr
 */
public class Main {
  static final Map<String, KnownAttribute<?>> FLAG_TO_ATTR = buildFlagMap();

  /** Flags whose attribute is multi-valued — repeating the flag accumulates entries instead of
   * overwriting. Single-value flags retain last-wins semantics. */
  static final Set<KnownAttribute<?>> MULTI_VALUE_ATTRS = Set.of(KnownAttribute.PSI_INPUT_PATH);

  private static Map<String, KnownAttribute<?>> buildFlagMap() {
    Map<String, KnownAttribute<?>> map = new LinkedHashMap<>();
    map.put("--parser-output",                          KnownAttribute.PARSER_OUTPUT_PATH);
    map.put("--psi-output",                             KnownAttribute.PSI_OUTPUT_PATH);
    map.put("--element-type-holder-output",             KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH);
    map.put("--syntax-element-type-holder-output",      KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH);
    map.put("--element-type-converter-factory-output",  KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH);
    map.put("--input-path",                             KnownAttribute.INPUT_PATH);
    map.put("--psi-input",                              KnownAttribute.PSI_INPUT_PATH);
    return Map.copyOf(map);
  }

  private final @NotNull BnfParserDefinition parserDefinition;
  private final @NotNull CliArgs cliArgs;

  Main(@NotNull BnfParserDefinition parserDefinition, @NotNull CliArgs cliArgs) {
    this.parserDefinition = parserDefinition;
    this.cliArgs = cliArgs;
  }

  public static void main(String[] args) {
    int exitCode = run(args);
    System.exit(exitCode);
  }

  @SuppressWarnings("CallToPrintStackTrace")
  static int run(String[] args) {
    CliArgs cliArgs;
    try {
      cliArgs = CliArgs.parse(args, System.out, System.err);
    }
    catch (UsageException e) {
      if (e.getMessage() != null) System.err.println(e.getMessage());
      printUsage(System.out);
      return e.exitCode;
    }

    Path parserOutput = cliArgs.firstPath(KnownAttribute.PARSER_OUTPUT_PATH);
    if (parserOutput != null) {
      File parserOutputFile = parserOutput.toFile();
      if (parserOutputFile.exists() && parserOutputFile.isFile()) {
        System.out.println("Output directory not found: " + parserOutputFile.getAbsolutePath());
        return 0;
      }
      if (!parserOutputFile.exists() && !parserOutputFile.mkdirs()) {
        System.err.println("Could not create output directory: " + parserOutputFile.getAbsolutePath());
        return 1;
      }
    }

    try {
      LightPsi.init();
      LightPsi.Init.addKeyedExtension(LanguageASTFactory.INSTANCE, BnfLanguage.INSTANCE, new BnfASTFactory(), null);
      LightPsi.Init.addKeyedExtension(LanguageBraceMatching.INSTANCE, BnfLanguage.INSTANCE, new BnfBraceMatcher(), null);

      var main = new Main(new BnfParserDefinition(), cliArgs);
      for (String pattern : cliArgs.grammarPatterns()) {
        if (!main.processGrammarFile(pattern)) {
          return 1;
        }
      }
      return 0;
    }
    catch (PathConflicts.ConflictException e) {
      // already printed; just exit
      return 1;
    }
    catch (Throwable throwable) {
      throwable.printStackTrace();
      return 1;
    }
  }

  private static void printUsage(@NotNull PrintStream out) {
    out.println("Usage: Main <grammar-file> [options]");
    out.println("Options:");
    for (Map.Entry<String, KnownAttribute<?>> e : FLAG_TO_ATTR.entrySet()) {
      String suffix = MULTI_VALUE_ATTRS.contains(e.getValue()) ? " (repeatable)" : "";
      out.println("  " + e.getKey() + " <path>   sets " + e.getValue().getName() + suffix);
    }
    out.println("  --strict-paths            fail on CLI/grammar path conflicts");
    out.println("  --source-psi              resolve Java references from .java sources at inputPath/psiInputPath");
    out.println();
    out.println("Legacy form (deprecated): Main <output-dir> <grammars or patterns>");
  }

  private boolean processGrammarFile(@NotNull String grammar) throws IOException, ClassNotFoundException {
    var grammarPattern = GrammarPattern.of(grammar);
    if (!grammarPattern.isValid()) {
      System.err.println("Grammar directory not found: " + grammarPattern.grammarDir().getAbsolutePath());
      return false;
    }

    var grammarFiles = grammarPattern.collectGrammarFiles();

    int count = 0;
    for (File grammarFile : grammarFiles) {
      if (generateGrammar(grammarFile, grammarPattern.grammarDir())) {
        Path parserOut = cliArgs.firstPath(KnownAttribute.PARSER_OUTPUT_PATH);
        String outDesc = parserOut != null ? parserOut.toString() : "(grammar-defined paths)";
        System.out.println(grammarFile.getName() + " parser generated to " + outDesc);
        count++;
      }
    }

    if (count == 0) {
      System.out.println("No grammars matching '" + grammarPattern.wildCard() + "' found in: " + grammarPattern.grammarDir());
    }

    return true;
  }

  private boolean generateGrammar(@NotNull File grammarFile, @NotNull File grammarDir) throws IOException, ClassNotFoundException {
    PsiFile bnfFile = LightPsi.parseFile(grammarFile, parserDefinition);
    if (!(bnfFile instanceof BnfFile)) return false;

    Path parserOutput = cliArgs.firstPath(KnownAttribute.PARSER_OUTPUT_PATH);
    if (parserOutput != null && parserOutput.toString().contains("lightpsi")) {
      Class.forName("org.jetbrains.annotations.NotNull");
      Class.forName("org.jetbrains.annotations.Nullable");
      Class.forName("org.intellij.lang.annotations.Pattern");
      Class.forName("org.intellij.lang.annotations.RegExp");
      DebugUtil.psiToString(bnfFile, false);
    }

    Path bnfParent = grammarFile.getParentFile().toPath();
    Map<KnownAttribute<?>, List<Path>> grammarMap = BnfPaths.collectExplicitPaths((BnfFile)bnfFile, bnfParent);
    Map<KnownAttribute<?>, List<Path>> merged = PathConflicts.merge(cliArgs.paths(), grammarMap, cliArgs.strictPaths(), System.err);
    BnfPathsResolution paths = BnfPaths.resolveExplicit(merged, bnfParent);

    JavaParserGenerator generator = new JavaParserGenerator((BnfFile)bnfFile,
                                                            grammarDir.getAbsolutePath(),
                                                            "",
                                                            OutputOpener.DEFAULT,
                                                            paths);
    generator.generate();
    return true;
  }
}
