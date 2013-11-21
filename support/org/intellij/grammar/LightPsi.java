/*
 * Copyright 2011-2013 Gregory Shrago
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

import com.intellij.concurrency.AsyncFutureFactory;
import com.intellij.concurrency.AsyncFutureFactoryImpl;
import com.intellij.concurrency.JobLauncher;
import com.intellij.concurrency.JobLauncherImpl;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.mock.MockApplicationEx;
import com.intellij.mock.MockProjectEx;
import com.intellij.mock.MockPsiManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.DumbServiceImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.search.CachesBasedRefSearcher;
import com.intellij.psi.impl.search.PsiSearchHelperImpl;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageManagerImpl;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.testFramework.ParsingTestCase;
import org.intellij.grammar.java.JavaHelper;
import org.jetbrains.annotations.NonNls;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author greg
 */
public class LightPsi {

  private static final MyParsing ourParsing;

  static {
    try {
      ourParsing = new MyParsing();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static PsiFile parseFile(File file, ParserDefinition parserDefinition) throws IOException {
    String name = file.getName();
    String text = FileUtil.loadFile(file);
    return parseFile(name, text, parserDefinition);
  }

  public static PsiFile parseFile(String name, String text, ParserDefinition parserDefinition) {
    return ourParsing.createFile(name, text, parserDefinition);
  }

  /*
   * Builds light-psi-all.jar from JVM class loader log (-verbose:class option)
   */
  public static void main(String[] args) throws Throwable {
    if (args.length < 2) {
      System.out.println("Usage: Main <output-dir> <classes.log.txt>");
      return;
    }

    File dir = new File(args[0]);
    BufferedReader reader = new BufferedReader(new FileReader(new File(args[1])));
    String s;
    Pattern pattern = Pattern.compile("\\[Loaded (.*) from (?:file:)?(.*)\\]");

    JarOutputStream jarFile = new JarOutputStream(new FileOutputStream(new File(dir, "light-psi-all.jar")));
//    JarOutputStream jarFile = new JarOutputStream(new FileOutputStream(new File(dir, "light-psi-min.jar")));
    while ((s = reader.readLine()) != null) {
      Matcher matcher = pattern.matcher(s);
      if (!matcher.matches()) continue;
      String className = matcher.group(1);
      String path = matcher.group(2);
      if (!path.startsWith("/Applications")) continue;
//      if (!path.contains("light-psi-all.jar")) continue;
      String entryName = className.replace(".", "/") + ".class";

      jarFile.putNextEntry(new JarEntry(entryName));
      FileUtil.copy(LightPsi.class.getClassLoader().getResourceAsStream(entryName), jarFile);
      jarFile.closeEntry();
    }
    jarFile.close();
  }

  private static class MyParsing extends ParsingTestCase {
    MyParsing() throws Exception {
      super("", "", new BnfParserDefinition());
      super.setUp();
      Init.initExtensions(getProject(), getPsiManager());
    }

    @Override
    protected String getTestDataPath() {
      return "";
    }

    protected PsiFile createFile(@NonNls String name, String text, ParserDefinition definition) {
      addExplicitExtension(LanguageParserDefinitions.INSTANCE, definition.getFileNodeType().getLanguage(), definition);
      myLanguage = definition.getFileNodeType().getLanguage();
      return super.createFile(name, text);
    }
  }

  public static class Init {

    public static void initExtensions(MockProjectEx project, MockPsiManager psiManager) {
      Extensions.getRootArea().registerExtensionPoint("com.intellij.referencesSearch", "com.intellij.util.QueryExecutor");
      Extensions.getRootArea().registerExtensionPoint("com.intellij.useScopeEnlarger", "com.intellij.psi.search.UseScopeEnlarger");
      Extensions.getRootArea().registerExtensionPoint("com.intellij.languageInjector", "com.intellij.psi.LanguageInjector");
      Extensions.getArea(project).registerExtensionPoint("com.intellij.multiHostInjector", "com.intellij.lang.injection.MultiHostInjector");
      Extensions.getRootArea().registerExtensionPoint("com.intellij.codeInsight.containerProvider",
                                                      "com.intellij.codeInsight.ContainerProvider");
      Extensions.getRootArea().getExtensionPoint("com.intellij.referencesSearch").registerExtension(new CachesBasedRefSearcher());
      registerApplicationService(project, PsiReferenceService.class, new PsiReferenceServiceImpl());
      registerApplicationService(project, JobLauncher.class, new JobLauncherImpl());
      registerApplicationService(project, AsyncFutureFactory.class, new AsyncFutureFactoryImpl());
      project.registerService(PsiSearchHelper.class, new PsiSearchHelperImpl(psiManager));
      project.registerService(DumbService.class, new DumbServiceImpl(project, project.getMessageBus()));
      project.registerService(ResolveCache.class, new ResolveCache(project.getMessageBus()));
      project.registerService(PsiFileFactory.class, new PsiFileFactoryImpl(psiManager));
      try {
        project.registerService(JavaHelper.class, new JavaHelper.AsmHelper());
      }
      catch (LinkageError e) {
        System.out.println("ASM not available, using reflection helper: " + e);
        project.registerService(JavaHelper.class, new JavaHelper.ReflectionHelper());
      }

      InjectedLanguageManagerImpl languageManager = new InjectedLanguageManagerImpl(project, DumbService.getInstance(project));
      Disposer.register(project, languageManager);
      project.registerService(InjectedLanguageManager.class, languageManager);
      ProgressManager.getInstance();
    }

    private static <T> void registerApplicationService(Project project, final Class<T> aClass, T object) {
      final MockApplicationEx application = (MockApplicationEx)ApplicationManager.getApplication();
      application.registerService(aClass, object);
      Disposer.register(project, new Disposable() {
        @Override
        public void dispose() {
          application.getPicoContainer().unregisterComponent(aClass.getName());
        }
      });
    }
  }
}
