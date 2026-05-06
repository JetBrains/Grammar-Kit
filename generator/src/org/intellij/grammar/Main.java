/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.lang.LanguageASTFactory;
import com.intellij.lang.LanguageBraceMatching;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import org.intellij.grammar.generator.JavaParserGenerator;
import org.intellij.grammar.generator.OutputOpener;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Command-line interface to parser generator.
 * Required community jars on classpath:
 * app-client.jar, lib-client.jar, opentelemetry.jar, util.jar, util-8.jar, util_rt.jar
 *
 * @author gregsh
 * @noinspection UseOfSystemOutOrSystemErr
 */
public class Main {
  private final @NotNull BnfParserDefinition parserDefinition;
  private final @NotNull File output;

  Main(@NotNull BnfParserDefinition parserDefinition, @NotNull File output) {
    this.parserDefinition = parserDefinition;
    this.output = output;
  }

  public static void main(String[] args) {
    int exitCode = run(args);
    System.exit(exitCode);
  }

  @SuppressWarnings("CallToPrintStackTrace")
  static int run(String[] args) {
    try {
      if (args.length < 2) {
        System.out.println("Usage: Main <output-dir> <grammars or patterns>");
        return 0;
      }
      String outputPath = args[0];
      File output = new File(outputPath);
      if (!output.exists() && !output.mkdirs() || output.isFile()) {
        System.out.println("Output directory not found: " + output.getAbsolutePath());
        return 0;
      }

      LightPsi.init();
      LightPsi.Init.addKeyedExtension(LanguageASTFactory.INSTANCE, BnfLanguage.INSTANCE, new BnfASTFactory(), null);
      LightPsi.Init.addKeyedExtension(LanguageBraceMatching.INSTANCE, BnfLanguage.INSTANCE, new BnfBraceMatcher(), null);

      BnfParserDefinition parserDefinition = new BnfParserDefinition();

      var main = new Main(parserDefinition, output);
      for (int i = 1; i < args.length; i++) {
        if (!main.processGrammarFile(args[i])) {
          return 1;
        }
      }
      return 0;
    }
    catch (Throwable throwable) {
      throwable.printStackTrace();
      return 1;
    }
  }

  private boolean processGrammarFile(@NotNull String grammar) throws IOException, ClassNotFoundException {
    var grammarPattern = GrammarPattern.of(grammar);
    if (!grammarPattern.isValid()) {
      System.err.println("Grammar directory not found: " + grammarPattern.grammarDir.getAbsolutePath());
      return false;
    }

    var grammarFiles = grammarPattern.collectGrammarFiles();

    int count = 0;
    for (File grammarFile : grammarFiles) {
      if (generateGrammar(grammarFile, grammarPattern.grammarDir)) {
        System.out.println(grammarFile.getName() + " parser generated to " + output.getCanonicalPath());
        count++;
      }
    }

    if (count == 0) {
      System.out.println("No grammars matching '" + grammarPattern.wildCard + "' found in: " + grammarPattern.grammarDir);
    }

    return true;
  }

  private boolean generateGrammar(@NotNull File grammarFile, @NotNull File grammarDir) throws IOException, ClassNotFoundException {
    PsiFile bnfFile = LightPsi.parseFile(grammarFile, parserDefinition);
    if (!(bnfFile instanceof BnfFile)) return false;

    // for light-psi-all building:
    if (output.getAbsolutePath().contains("lightpsi")) {
      Class.forName("org.jetbrains.annotations.NotNull");
      Class.forName("org.jetbrains.annotations.Nullable");
      Class.forName("org.intellij.lang.annotations.Pattern");
      Class.forName("org.intellij.lang.annotations.RegExp");
      DebugUtil.psiToString(bnfFile, false);
    }

    JavaParserGenerator generator = new JavaParserGenerator((BnfFile)bnfFile,
                                                            grammarDir.getAbsolutePath(),
                                                            output.getAbsolutePath(),
                                                            "",
                                                            OutputOpener.DEFAULT);
    generator.generate();
    return true;
  }

  private record GrammarPattern(
    @NotNull File grammarDir,
    @NotNull Pattern grammarPattern,
    @NotNull String wildCard
  ) {

    @NotNull List<File> collectGrammarFiles() {
      File[] files = grammarDir.listFiles();
      if (files == null) return List.of();

      return Stream.of(files)
        .filter(f -> !f.isDirectory() && grammarPattern.matcher(f.getName()).matches())
        .toList();
    }

    boolean isValid() {
      return grammarDir.exists() && grammarDir.isDirectory();
    }

    static @NotNull Main.GrammarPattern of(@NotNull String grammar) {
      int idx = grammar.lastIndexOf(File.separator);
      File grammarDir = new File(idx >= 0 ? grammar.substring(0, idx) : ".");
      String wildCard = idx >= 0 ? grammar.substring(idx + 1) : grammar;
      Pattern grammarPattern = Pattern.compile(convertToJavaPattern(wildCard));
      return new GrammarPattern(grammarDir, grammarPattern, wildCard);
    }

    private static @NotNull String convertToJavaPattern(@NotNull String wildcardPattern) {
      wildcardPattern = StringUtil.replace(wildcardPattern, ".", "\\.");
      wildcardPattern = StringUtil.replace(wildcardPattern, "*?", ".+");
      wildcardPattern = StringUtil.replace(wildcardPattern, "?*", ".+");
      wildcardPattern = StringUtil.replace(wildcardPattern, "*", ".*");
      wildcardPattern = StringUtil.replace(wildcardPattern, "?", ".");
      return wildcardPattern;
    }
  }
}
