/*
 * Copyright 2011-2012 Gregory Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.intellij.grammar;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.psi.BnfFile;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Command-line interface to parser generator.
 * Required community jars on classpath:
 * jdom.jar, trove4j.jar, extensions.jar, picocontainer.jar, junit.jar, idea.jar, openapi.jar, util.jar.
 * @author gregsh
 */
public class Main {
  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.out.println("Usage: Main <output-dir> <grammars or patterns>");
      return;
    }
    File output = new File(args[0]);
    if (!output.exists() && !output.mkdirs() || output.isFile()) {
      System.out.println("Output directory not found: " + output.getAbsolutePath());
      return;
    }

    try {
      BnfParserDefinition parserDefinition = new BnfParserDefinition();

      for (int i = 1; i < args.length; i++) {
        String grammar = args[i];
        int idx = grammar.lastIndexOf(File.separator);
        File grammarDir = new File(idx >= 0 ? grammar.substring(0, idx) : ".");
        String wildCard = idx >= 0 ? grammar.substring(idx + 1) : grammar;
        Pattern grammarPattern = Pattern.compile(convertToJavaPattern(wildCard));
        if (!grammarDir.exists() || !grammarDir.isDirectory()) {
          System.out.println("Grammar directory not found: " + grammarDir.getAbsolutePath());
          return;
        }

        File[] files = grammarDir.listFiles();
        int count = 0;
        if (files != null) {
          for (File file : files) {
            if (file.isDirectory() || !grammarPattern.matcher(file.getName()).matches()) continue;
            PsiFile bnfFile = LightPsi.parseFile(file, parserDefinition);
            if (!(bnfFile instanceof BnfFile)) continue;

// for light-psi-all building:
//            Class.forName("org.jetbrains.annotations.NotNull");
//            Class.forName("org.jetbrains.annotations.Nullable");
//            Class.forName("org.intellij.lang.annotations.Pattern");
//            Class.forName("org.intellij.lang.annotations.RegExp");
//            com.intellij.psi.impl.DebugUtil.psiToString(bnfFile, false);

            count ++;
            new ParserGenerator((BnfFile) bnfFile, grammarDir.getAbsolutePath(), output.getAbsolutePath()).generate();
            System.out.println(file.getName() + " parser generated to " + output.getCanonicalPath());
          }
        }
        if (count == 0) {
          System.out.println("No grammars matching '"+wildCard+"' found in: "+ grammarDir);
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
