/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
import org.intellij.grammar.java.AsmHelper;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.ReflectionHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.openapi.extensions.ExtensionPoint.Kind.INTERFACE;

/**
 * Minimal headless harness for running an IntelliJ Platform {@link ParserDefinition}
 * outside the IDE — no real {@code Application}, no project model, no plugin loader.
 * <p>
 * On first class load a singleton {@link MyParsing} builds a {@link CoreApplicationEnvironment}
 * + {@link CoreProjectEnvironment} pair and registers the minimum set of extension points
 * and services needed for {@link PsiFileFactoryImpl#trySetupPsiForFile} to succeed
 * (see {@link Init#initExtensions}). Callers that need extra language services
 * (an {@code ASTFactory}, a {@code BraceMatcher}, etc.) register them through
 * {@link Init#addKeyedExtension}.
 * <p>
 * Three parse entry points are exposed, in decreasing weight:
 * <ul>
 *   <li>{@link #parseFile} — full PSI; required by anything that walks {@code PsiElement}.</li>
 *   <li>{@link #parseText} — raw {@link ASTNode} only; lex + parse with no PSI.</li>
 *   <li>{@link #parseLight} — {@link LighterASTNode} traverser over the in-memory
 *       builder buffer; matches {@link LightPsiParser#parseLight}.</li>
 * </ul>
 * <p>
 * The unrelated {@link #main} builds {@code light-psi-all.jar} from a
 * {@code -verbose:class} log; see that method for details.
 * <p>
 * Consumers in this repo:
 * <ul>
 *   <li>{@code generator/Main} — the headless {@code java -jar grammar-kit.jar} CLI.</li>
 *   <li>{@code tests/BnfGeneratorTestCase} — borrows {@link Init#initExtensions}
 *       to keep test fixtures aligned with the headless generator.</li>
 *   <li>{@code tests/expression/Main} — the expression-console sample.</li>
 * </ul>
 * <p>
 * <b>Caveats:</b> the static singleton means exactly one {@code MockApplication} per JVM —
 * tests that need isolation should own their own application/project and only call
 * {@link Init#initExtensions} on it. ASM is preferred but optional: when missing,
 * {@link Init#initExtensions} silently falls back to {@link ReflectionHelper} for
 * {@link JavaHelper} target-class lookups.
 *
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

  /**
   * No-op trigger that forces the static initializer to run, building the singleton
   * {@link MyParsing} (and therefore the {@link MockApplication} + {@link MockProject}).
   * Call this before {@link Init#addKeyedExtension} when registering language services
   * up front; otherwise the first {@code parseXxx} call will trigger initialization.
   */
  public static void init() {
  }

  /**
   * Reads {@code file} from disk and produces a full PSI tree. The file's name is
   * preserved as the {@link LightVirtualFile} name so error reporting and references
   * see the original path.
   *
   * @return the parsed {@link PsiFile}, or {@code null} if the platform cannot wire
   *         a PSI tree for this language (e.g. the parser definition produced no parser).
   */
  public static @Nullable PsiFile parseFile(@NotNull File file, @NotNull ParserDefinition parserDefinition) throws IOException {
    String name = file.getName();
    String text = FileUtil.loadFile(file);
    return parseFile(name, text, parserDefinition);
  }

  /**
   * Builds a {@link LightVirtualFile} of the given name and runs
   * {@link PsiFileFactoryImpl#trySetupPsiForFile}. On first use of each language a
   * matching {@link MockLanguageFileType} is registered with the file-type manager
   * for the file's extension.
   */
  public static @Nullable PsiFile parseFile(@NotNull String name, @NotNull String text, @NotNull ParserDefinition parserDefinition) {
    return ourParsing.createFile(name, text, parserDefinition);
  }

  /**
   * Cheaper path: lex + parse only, returning the raw {@link ASTNode}. Use this
   * when the caller does not need PSI (tree dumps, lexer/parser smoke tests).
   */
  public static @NotNull ASTNode parseText(@NotNull String text, @NotNull ParserDefinition parserDefinition) {
    return ourParsing.createAST(text, parserDefinition);
  }

  /**
   * Cheapest path: drives {@link LightPsiParser#parseLight} and returns a traverser
   * over the {@link LighterASTNode} buffer. No PSI, no AST nodes — appropriate when
   * the tree is read once and discarded. Requires the parser to implement
   * {@link LightPsiParser}.
   */
  public static @NotNull SyntaxTraverser<LighterASTNode> parseLight(@NotNull String text, @NotNull ParserDefinition parserDefinition) {
    return ourParsing.parseLight(text, parserDefinition);
  }

  /**
   * Build-time entry point for {@code light-psi-all.jar} — unrelated to parsing.
   * Reads a JVM {@code -verbose:class} log and packs every loaded IntelliJ Platform
   * class into a single jar, so the standalone {@code grammar-kit.jar} distribution
   * can run without the full platform on the classpath. Triggered by the grammar-kit
   * run configuration; not a stable API.
   *
   * @param args {@code <output-dir> <classes.log.txt>}
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
    BufferedReader reader = new BufferedReader(new FileReader(classesFile, StandardCharsets.UTF_8));
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
           path.contains("util-8.jar") ||
           path.contains("util_rt.jar") ||
           path.contains("app-client.jar") ||
           path.contains("lib-client.jar") ||
           path.contains("opentelemetry.jar") ||
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

  /**
   * Reusable bootstrap helpers. Kept as a nested type so callers that already own a
   * {@link MockApplication} / {@link MockProject} (notably the test base) can run the
   * extension-point setup without paying for the full {@link MyParsing} singleton.
   */
  public static class Init {

    /**
     * Registers the minimum extension points and project services {@code LightPsi}
     * needs: references search, scope enlargers/optimizers, container providers,
     * language injectors, multi-host injectors, {@link PsiSearchHelper}, and a
     * {@link JavaHelper} implementation. Prefers ASM-backed {@link AsmHelper};
     * falls back to {@link ReflectionHelper} when ASM is missing from the classpath.
     */
    @SuppressWarnings("UnstableApiUsage")
    public static void initExtensions(MockApplication application, @NotNull MockProject project) {
      ExtensionsAreaImpl ra = application.getExtensionArea();
      ra.registerExtensionPoint("com.intellij.referencesSearch", "com.intellij.util.QueryExecutor", INTERFACE, false);
      ra.getExtensionPoint("com.intellij.referencesSearch").registerExtension(new CachesBasedRefSearcher(), project);
      ra.registerExtensionPoint("com.intellij.useScopeEnlarger", "com.intellij.psi.search.UseScopeEnlarger", INTERFACE, false);
      ra.registerExtensionPoint("com.intellij.useScopeOptimizer", "com.intellij.psi.search.ScopeOptimizer", INTERFACE, false);
      ra.registerExtensionPoint("com.intellij.codeInsight.containerProvider", "com.intellij.codeInsight.ContainerProvider", INTERFACE, false);
      ra.registerExtensionPoint("com.intellij.languageInjector", "com.intellij.psi.LanguageInjector", INTERFACE, false);
      project.registerService(PsiSearchHelper.class, PsiSearchHelperImpl.class);
      project.getExtensionArea().registerExtensionPoint("com.intellij.multiHostInjector", "com.intellij.lang.injection.MultiHostInjector", INTERFACE, false);
      try {
        project.registerService(JavaHelper.class, new AsmHelper());
      }
      catch (LinkageError e) {
        System.out.println("ASM not available, using reflection helper: " + e);
        project.registerService(JavaHelper.class, new ReflectionHelper());
      }
    }

    /**
     * Registers a per-key entry on a {@link KeyedExtensionCollector} (e.g. a language
     * extension on {@code LanguageASTFactory.INSTANCE}). When a {@code disposable} is
     * supplied the entry is automatically removed on disposal, so callers do not leak
     * registrations across runs.
     */
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

  /**
   * Minimal {@link LanguageFileType} whose only job is to advertise a language and
   * its file extension so {@code FileTypeManager} can route lookups. {@link #equals}
   * is keyed on the language so wrapper identity does not matter — two mocks for
   * the same language compare equal.
   */
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
