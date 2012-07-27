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

import com.intellij.concurrency.JobLauncher;
import com.intellij.concurrency.JobLauncherImpl;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.DumbServiceImpl;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceService;
import com.intellij.psi.PsiReferenceServiceImpl;
import com.intellij.psi.impl.search.CachesBasedRefSearcher;
import com.intellij.psi.impl.search.PsiSearchHelperImpl;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageManagerImpl;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.testFramework.ParsingTestCase;
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
      System.out.println("Usage: Main <grammar> <output-dir>");
      return;
    }
    String grammar = args[0];
    int idx = grammar.lastIndexOf(File.separator);
    File grammarDir = new File(idx >= 0 ? grammar.substring(0, idx) : ".");
    Pattern grammarPattern = Pattern.compile(convertToJavaPattern(idx >= 0 ? grammar.substring(idx + 1) : grammar));
    if (!grammarDir.exists() || !grammarDir.isDirectory()) {
      System.out.println("Grammar directory not found: " + grammarDir.getAbsolutePath());
      return;
    }

    File output = new File(args[1]);
    if (!output.exists() && !output.mkdirs() || output.isFile()) {
      System.out.println("Output directory not found: " + output.getAbsolutePath());
      return;
    }

    File[] files = grammarDir.listFiles();
    if (files != null) {
      MyParsing parsingTestCase = new MyParsing();
      for (File file : files) {
        if (file.isDirectory() || !grammarPattern.matcher(file.getName()).matches()) continue;
        PsiFile bnfFile = parsingTestCase.parse(file);
        if (!(bnfFile instanceof BnfFile)) continue;
        new ParserGenerator((BnfFile)bnfFile, grammarDir.getAbsolutePath(), output.getAbsolutePath()).generate();
        System.out.println(file.getName() + " parser generated to " + output.getCanonicalPath());
      }
    }
    System.exit(0);
  }

  private static class MyParsing extends ParsingTestCase {
    MyParsing() throws Exception {
      super("", "", new BnfParserDefinition());
      super.setUp();
      Extensions.getRootArea().registerExtensionPoint("com.intellij.referencesSearch", "com.intellij.util.QueryExecutor");
      Extensions.getRootArea().registerExtensionPoint("com.intellij.useScopeEnlarger", "com.intellij.psi.search.UseScopeEnlarger");
      Extensions.getRootArea().registerExtensionPoint("com.intellij.languageInjector", "com.intellij.psi.LanguageInjector");
      Extensions.getArea(getProject()).registerExtensionPoint("com.intellij.multiHostInjector",
                                                              "com.intellij.lang.injection.MultiHostInjector");
      Extensions.getRootArea().getExtensionPoint("com.intellij.referencesSearch").registerExtension(new CachesBasedRefSearcher());
      registerApplicationService(PsiReferenceService.class, new PsiReferenceServiceImpl());
      registerApplicationService(JobLauncher.class, new JobLauncherImpl());
      getProject().registerService(PsiSearchHelper.class, new PsiSearchHelperImpl(getPsiManager()));
      getProject().registerService(DumbService.class, new DumbServiceImpl(getProject(), getProject().getMessageBus()));
      InjectedLanguageManagerImpl languageManager = new InjectedLanguageManagerImpl(getProject(), DumbService.getInstance(getProject()));
      Disposer.register(getProject(), languageManager);
      getProject().registerService(InjectedLanguageManager.class, languageManager);
      ProgressManager.getInstance();
    }

    @Override
    protected String getTestDataPath() {
      return "";
    }

    public PsiFile parse(File file) throws Exception {
      String name = file.getName();
      String text = FileUtil.loadFile(file);
      return createFile(name + "." + myFileExt, text);
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
