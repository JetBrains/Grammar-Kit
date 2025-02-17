/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.lang.LanguageASTFactory;
import com.intellij.lang.LanguageBraceMatching;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import org.intellij.grammar.fleet.FleetBnfFileWrapper;
import org.intellij.grammar.fleet.FleetFileTypeGenerator;
import org.intellij.grammar.generator.Generator;
import org.intellij.grammar.generator.OutputOpener;
import org.intellij.grammar.psi.BnfFile;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Command-line interface to parser generator.
 * Required community jars on classpath:
 * app-client.jar, lib-client.jar, opentelemetry.jar, util.jar, util-8.jar, util_rt.jar
 *
 * @author gregsh
 * @noinspection UseOfSystemOutOrSystemErr
 */
public class Main {
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println(
        "Usage: Main <output-dir> <grammar-or-pattern 1> [--fleet] [--generateFileTypeElement --className=<fqn> --debugName=<debugName> --languageClass=<fqn>] [ ... <grammar-or-pattern n> [--fleet] [--generateFileTypeElement...]]");
      return;
    }
    File output = new File(args[0]);
    if (!output.exists() && !output.mkdirs() || output.isFile()) {
      System.out.println("Output directory not found: " + output.getAbsolutePath());
      return;
    }
    LightPsi.init();
    LightPsi.Init.addKeyedExtension(LanguageASTFactory.INSTANCE, BnfLanguage.INSTANCE, new BnfASTFactory(), null);
    LightPsi.Init.addKeyedExtension(LanguageBraceMatching.INSTANCE, BnfLanguage.INSTANCE, new BnfBraceMatcher(), null);

    try {
      BnfParserDefinition parserDefinition = new BnfParserDefinition();

      for (int i = 1; i < args.length; i++) {
        boolean generateForFleet = false;
        boolean generateFileTypeElement = false;
        String className = "";
        String languageClass = "";
        String debugName = "FILE";

        String grammar = args[i];
        int idx = grammar.lastIndexOf(File.separator);
        File grammarDir = new File(idx >= 0 ? grammar.substring(0, idx) : ".");
        String wildCard = idx >= 0 ? grammar.substring(idx + 1) : grammar;
        Pattern grammarPattern = Pattern.compile(convertToJavaPattern(wildCard));
        if (!grammarDir.exists() || !grammarDir.isDirectory()) {
          System.out.println("Grammar directory not found: " + grammarDir.getAbsolutePath());
          return;
        }

        while (i + 1 < args.length && (args[i + 1].startsWith("--fleet") || args[i + 1].startsWith("--generateFileTypeElement"))) {
          i++;
          var arg = args[i];
          if (arg.equals("--fleet")) {
            generateForFleet = true;
          }
          if (arg.startsWith("--generateFileTypeElement")) {
            var hasClassName = false;
            var hasLanguageClass = false;
            while (i + 1 < args.length &&
                   (args[i + 1].startsWith("--className") ||
                    args[i + 1].startsWith("--debugName") ||
                    args[i + 1].startsWith("--languageClass"))) {
              i++;
              var argInner = args[i];
              if (argInner.startsWith("--className")) {
                String[] keyValuePair = argInner.split("=");
                if (keyValuePair.length == 2) {
                  className = keyValuePair[1];
                  hasClassName = true;
                }
                else {
                  System.out.println("Error parsing parameters: " + argInner);
                  return;
                }
              }
              if (argInner.startsWith("--languageClass")) {
                String[] keyValuePair = argInner.split("=");
                if (keyValuePair.length == 2) {
                  languageClass = keyValuePair[1];
                  hasLanguageClass = true;
                }
                else {
                  System.out.println("Error parsing parameters: " + argInner);
                  return;
                }
              }
              if (argInner.startsWith("--debugName")) {
                String[] keyValuePair = argInner.split("=");
                if (keyValuePair.length == 2) {
                  debugName = keyValuePair[1];
                }
                else {
                  System.out.println("Error parsing parameters: " + argInner);
                  return;
                }
              }
            }

            if (!hasClassName) {
              System.out.println("Error parsing parameters: --className missing");
              return;
            }
            if (!hasLanguageClass) {
              System.out.println("Error parsing parameters: --languageClass missing");
              return;
            }
            generateFileTypeElement = true;
          }
        }
        File[] files = grammarDir.listFiles();
        int count = 0;
        if (files != null) {
          for (File file : files) {
            if (file.isDirectory() || !grammarPattern.matcher(file.getName()).matches()) continue;
            PsiFile psiFile = LightPsi.parseFile(file, parserDefinition);
            if (!(psiFile instanceof BnfFile)) continue;

            // for light-psi-all building:
            if (args[0].contains("lightpsi")) {
              Class.forName("org.jetbrains.annotations.NotNull");
              Class.forName("org.jetbrains.annotations.Nullable");
              Class.forName("org.intellij.lang.annotations.Pattern");
              Class.forName("org.intellij.lang.annotations.RegExp");
              DebugUtil.psiToString(psiFile, false);
            }
            count++;

            BnfFile bnfFile = (generateForFleet) ? FleetBnfFileWrapper.wrapBnfFile((BnfFile)psiFile) : (BnfFile)psiFile;
            Generator.JAVA_GENERATOR.generate(bnfFile, grammarDir.getAbsolutePath(), output.getAbsolutePath(), "", OutputOpener.DEFAULT);
            if (generateFileTypeElement) {
              new FleetFileTypeGenerator((BnfFile)psiFile,
                                         grammarDir.getAbsolutePath(),
                                         output.getAbsolutePath(),
                                         "",
                                         className, debugName, languageClass, OutputOpener.DEFAULT).generate();
            }

            System.out.println(file.getName() + " parser generated to " + output.getCanonicalPath());
          }
        }
        if (count == 0) {
          System.out.println("No grammars matching '" + wildCard + "' found in: " + grammarDir);
        }
      }
    }
    catch (Throwable throwable) {
      throwable.printStackTrace();
    }
    finally {
      System.exit(0);
    }
  }

  private static String convertToJavaPattern(String wildcardPattern) {
    wildcardPattern = StringUtil.replace(wildcardPattern, ".", "\\.");
    wildcardPattern = StringUtil.replace(wildcardPattern, "*?", ".+");
    wildcardPattern = StringUtil.replace(wildcardPattern, "?*", ".+");
    wildcardPattern = StringUtil.replace(wildcardPattern, "*", ".*");
    wildcardPattern = StringUtil.replace(wildcardPattern, "?", ".");
    return wildcardPattern;
  }
}
