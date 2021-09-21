/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.core.CoreApplicationEnvironment;
import com.intellij.core.CoreProjectEnvironment;
import com.intellij.lang.*;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.lexer.Lexer;
import com.intellij.mock.MockApplication;
import com.intellij.mock.MockProject;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.extensions.impl.ExtensionsAreaImpl;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.KeyedExtensionCollector;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.search.CachesBasedRefSearcher;
import com.intellij.psi.impl.search.PsiSearchHelperImpl;
import com.intellij.psi.impl.source.CharTableImpl;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.testFramework.LightVirtualFile;
import org.intellij.grammar.java.JavaHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.openapi.extensions.ExtensionPoint.Kind.INTERFACE;

/**
 * @author greg
 * @noinspection UseOfSystemOutOrSystemErr
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

  public static void init() {
  }

  public static @Nullable PsiFile parseFile(@NotNull File file, @NotNull ParserDefinition parserDefinition) throws IOException {
    String name = file.getName();
    String text = FileUtil.loadFile(file);
    return parseFile(name, text, parserDefinition);
  }

  public static @Nullable PsiFile parseFile(@NotNull String name, @NotNull String text, @NotNull ParserDefinition parserDefinition) {
    return ourParsing.createFile(name, text, parserDefinition);
  }

  public static @NotNull ASTNode parseText(@NotNull String text, @NotNull ParserDefinition parserDefinition) {
    return ourParsing.createAST(text, parserDefinition);
  }

  public static @NotNull SyntaxTraverser<LighterASTNode> parseLight(@NotNull String text, @NotNull ParserDefinition parserDefinition) {
    return ourParsing.parseLight(text, parserDefinition);
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
    File file = new File(args[1]);
    File out = new File(dir, "light-psi-all.jar");
    int count = mainImpl(file, out);
    System.out.println(StringUtil.formatFileSize(out.length()) +
                       " and " + count + " classes written to " +
                       out.getName());
  }

  private static int mainImpl(File classesFile, File outJarFile) throws Throwable {
    BufferedReader reader = new BufferedReader(new FileReader(classesFile));
    Pattern pattern = Pattern.compile(".*\\[class,load] (?<c>.*) source: (?:file:)?(?<f>.*)");

    JarOutputStream jar = new JarOutputStream(new FileOutputStream(outJarFile));
    int count = 0;
    String s;
    addJarEntry(jar, "misc/registry.properties");
    while ((s = reader.readLine()) != null) {
      Matcher matcher = pattern.matcher(s);
      if (!matcher.matches()) continue;
      String className = matcher.group("c");
      String path = matcher.group("f");
      if (!shouldAddEntry(path)) continue;
      addJarEntry(jar, className.replace(".", "/") + ".class");
      count ++;
    }
    jar.close();
    return count;
  }

  private static boolean shouldAddEntry(String path) {
    if (!path.startsWith("/")) return false;
    if (path.contains("/grammar-kit/")) return false;

    return path.contains("/out/classes/production/") ||
           path.contains("idea.jar") ||
           path.contains("platform-api.jar") ||
           path.contains("platform-impl.jar") ||
           path.contains("util.jar") ||
           path.contains("extensions.jar");
  }

  private static void addJarEntry(JarOutputStream jarFile, String resourceName) throws IOException {
    InputStream stream = LightPsi.class.getClassLoader().getResourceAsStream(resourceName);
    if (stream == null) {
      System.err.println("Skipping missing " + resourceName);
    }
    else {
      jarFile.putNextEntry(new JarEntry(resourceName));
      FileUtil.copy(stream, jarFile);
      jarFile.closeEntry();
    }
  }

  private static class MyParsing implements Disposable {

    final CoreApplicationEnvironment app;
    final CoreProjectEnvironment proj;

    MyParsing() {
      app = new CoreApplicationEnvironment(this);
      proj = new CoreProjectEnvironment(this, app);
      Init.initExtensions(app.getApplication(), getProject());
    }

    protected @Nullable PsiFile createFile(@NotNull String name, @NotNull String text, @NotNull ParserDefinition definition) {
      Language language = definition.getFileNodeType().getLanguage();
      Init.addKeyedExtension(LanguageParserDefinitions.INSTANCE, language, definition, this);
      MockLanguageFileType fileType = new MockLanguageFileType(language, FileUtilRt.getExtension(name));
      app.registerFileType(fileType, fileType.getDefaultExtension());
      LightVirtualFile file = new LightVirtualFile(name, fileType, text);
      return ((PsiFileFactoryImpl)PsiFileFactory.getInstance(getProject())).trySetupPsiForFile(file, language, true, false);
    }

    protected @NotNull ASTNode createAST(@NotNull String text, @NotNull ParserDefinition definition) {
      PsiParser parser = definition.createParser(getProject());
      Lexer lexer = definition.createLexer(getProject());
      PsiBuilderImpl psiBuilder = new PsiBuilderImpl(getProject(), null, definition, lexer, new CharTableImpl(), text, null, null);
      return parser.parse(definition.getFileNodeType(), psiBuilder);
    }

    protected @NotNull SyntaxTraverser<LighterASTNode> parseLight(@NotNull String text, @NotNull ParserDefinition definition) {
      LightPsiParser parser = (LightPsiParser)definition.createParser(getProject());
      Lexer lexer = definition.createLexer(getProject());
      PsiBuilderImpl psiBuilder = new PsiBuilderImpl(getProject(), null, definition, lexer, new CharTableImpl(), text, null, null);
      parser.parseLight(definition.getFileNodeType(), psiBuilder);
      return SyntaxTraverser.lightTraverser(psiBuilder);
    }

    private MockProject getProject() {
      return proj.getProject();
    }

    @Override
    public void dispose() {
    }
  }

  public static class Init {

    public static void initExtensions(MockApplication application, @NotNull MockProject project) {
      ExtensionsAreaImpl ra = application.getExtensionArea();
      ra.registerExtensionPoint("com.intellij.referencesSearch", "com.intellij.util.QueryExecutor", INTERFACE);
      ra.getExtensionPoint("com.intellij.referencesSearch").registerExtension(new CachesBasedRefSearcher(), project);
      ra.registerExtensionPoint("com.intellij.useScopeEnlarger", "com.intellij.psi.search.UseScopeEnlarger", INTERFACE);
      ra.registerExtensionPoint("com.intellij.useScopeOptimizer", "com.intellij.psi.search.ScopeOptimizer", INTERFACE);
      ra.registerExtensionPoint("com.intellij.codeInsight.containerProvider", "com.intellij.codeInsight.ContainerProvider", INTERFACE);
      ra.registerExtensionPoint("com.intellij.languageInjector", "com.intellij.psi.LanguageInjector", INTERFACE);
      project.registerService(PsiSearchHelper.class, PsiSearchHelperImpl.class);
      project.getExtensionArea().registerExtensionPoint("com.intellij.multiHostInjector", "com.intellij.lang.injection.MultiHostInjector", INTERFACE);
      try {
        project.registerService(JavaHelper.class, new JavaHelper.AsmHelper());
      }
      catch (LinkageError e) {
        System.out.println("ASM not available, using reflection helper: " + e);
        project.registerService(JavaHelper.class, new JavaHelper.ReflectionHelper());
      }
    }

    public static <T, KeyT> void addKeyedExtension(@NotNull KeyedExtensionCollector<T, KeyT> instance,
                                                   @NotNull KeyT key,
                                                   @NotNull T object,
                                                   @Nullable Disposable disposable) {
      instance.addExplicitExtension(key, object);
      if (disposable != null) {
        Disposer.register(disposable, () -> instance.removeExplicitExtension(key, object));
      }
    }
  }

  public static class MockLanguageFileType extends LanguageFileType {

    private final String myExtension;

    public MockLanguageFileType(@NotNull Language language, String extension) {
      super(language);
      myExtension = extension;
    }

    @Override public @NotNull String getName() { return getLanguage().getID(); }
    @Override public @NotNull String getDescription() { return ""; }
    @Override public @NotNull String getDefaultExtension() { return myExtension; }
    @Override public Icon getIcon() { return null; }
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof LanguageFileType)) return false;
      return getLanguage().equals(((LanguageFileType)obj).getLanguage());
    }
  }}
