/*
 * Copyright 2011-present Greg Shrago
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
import com.intellij.ide.startup.impl.StartupManagerImpl;
import com.intellij.lang.*;
import com.intellij.lang.impl.PsiBuilderFactoryImpl;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lexer.Lexer;
import com.intellij.mock.*;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.extensions.ExtensionPoint;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.extensions.ExtensionsArea;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.DumbServiceImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.KeyedExtensionCollector;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.encoding.EncodingManager;
import com.intellij.openapi.vfs.encoding.EncodingManagerImpl;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiCachedValuesFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.search.CachesBasedRefSearcher;
import com.intellij.psi.impl.search.PsiSearchHelperImpl;
import com.intellij.psi.impl.source.CharTableImpl;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistryImpl;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageManagerImpl;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.CachedValuesManagerImpl;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusFactory;
import org.intellij.grammar.java.JavaHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.picocontainer.*;
import org.picocontainer.defaults.AbstractComponentAdapter;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.openapi.extensions.ExtensionPoint.Kind.BEAN_CLASS;
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

  @Nullable
  public static PsiFile parseFile(@NotNull File file, @NotNull ParserDefinition parserDefinition) throws IOException {
    String name = file.getName();
    String text = FileUtil.loadFile(file);
    return parseFile(name, text, parserDefinition);
  }

  @Nullable
  public static PsiFile parseFile(@NotNull String name, @NotNull String text, @NotNull ParserDefinition parserDefinition) {
    return ourParsing.createFile(name, text, parserDefinition);
  }

  @NotNull
  public static ASTNode parseText(@NotNull String text, @NotNull ParserDefinition parserDefinition) {
    return ourParsing.createAST(text, parserDefinition);
  }

  @NotNull
  public static SyntaxTraverser<LighterASTNode> parseLight(@NotNull String text, @NotNull ParserDefinition parserDefinition) {
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
    Pattern pattern = Pattern.compile("\\[Loaded (.*) from (?:file:)?(.*)]");

    JarOutputStream jar = new JarOutputStream(new FileOutputStream(outJarFile));
    int count = 0;
    String s;
    addJarEntry(jar, "misc/registry.properties");
    while ((s = reader.readLine()) != null) {
      Matcher matcher = pattern.matcher(s);
      if (!matcher.matches()) continue;
      String className = matcher.group(1);
      String path = matcher.group(2);
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

    private final MockProject myProject;

    MyParsing() {
      myProject = Init.initAppAndProject(this);
      Init.initExtensions(myProject);
    }

    @Nullable
    protected PsiFile createFile(@NotNull String name, @NotNull String text, @NotNull ParserDefinition definition) {
      Language language = definition.getFileNodeType().getLanguage();
      Init.addKeyedExtension(LanguageParserDefinitions.INSTANCE, language, definition, getProject());
      return ((PsiFileFactoryImpl)PsiFileFactory.getInstance(myProject)).trySetupPsiForFile(new LightVirtualFile(name, language, text), language, true, false);
    }

    @NotNull
    protected ASTNode createAST(@NotNull String text, @NotNull ParserDefinition definition) {
      PsiParser parser = definition.createParser(getProject());
      Lexer lexer = definition.createLexer(getProject());
      PsiBuilderImpl psiBuilder = new PsiBuilderImpl(getProject(), null, definition, lexer, new CharTableImpl(), text, null, null);
      return parser.parse(definition.getFileNodeType(), psiBuilder);
    }

    @NotNull
    protected SyntaxTraverser<LighterASTNode> parseLight(@NotNull String text, @NotNull ParserDefinition definition) {
      LightPsiParser parser = (LightPsiParser)definition.createParser(getProject());
      Lexer lexer = definition.createLexer(getProject());
      PsiBuilderImpl psiBuilder = new PsiBuilderImpl(getProject(), null, definition, lexer, new CharTableImpl(), text, null, null);
      parser.parseLight(definition.getFileNodeType(), psiBuilder);
      return SyntaxTraverser.lightTraverser(psiBuilder);
    }

    private MockProject getProject() {
      return myProject;
    }

    @Override
    public void dispose() {
    }
  }

  public static class Init {

    public static void initExtensions(@NotNull MockProject project) {
      ExtensionsArea rootArea = Extensions.getRootArea();
      rootArea.registerExtensionPoint("com.intellij.referencesSearch", "com.intellij.util.QueryExecutor", INTERFACE);
      rootArea.registerExtensionPoint("com.intellij.useScopeEnlarger", "com.intellij.psi.search.UseScopeEnlarger", INTERFACE);
      rootArea.registerExtensionPoint("com.intellij.useScopeOptimizer", "com.intellij.psi.search.UseScopeOptimizer", INTERFACE);
      rootArea.registerExtensionPoint("com.intellij.languageInjector", "com.intellij.psi.LanguageInjector", INTERFACE);
      rootArea.registerExtensionPoint("com.intellij.codeInsight.containerProvider", "com.intellij.codeInsight.ContainerProvider", INTERFACE);
      rootArea.getExtensionPoint("com.intellij.referencesSearch").registerExtension(new CachesBasedRefSearcher(), project);
      ExtensionsArea projectArea = Extensions.getArea(project);
      projectArea.registerExtensionPoint("com.intellij.multiHostInjector", "com.intellij.lang.injection.MultiHostInjector", INTERFACE);
      registerApplicationService(project, PsiReferenceService.class, PsiReferenceServiceImpl.class);
      registerApplicationService(project, JobLauncher.class, JobLauncherImpl.class);
      registerApplicationService(project, AsyncFutureFactory.class, AsyncFutureFactoryImpl.class);
      project.registerService(PsiSearchHelper.class, PsiSearchHelperImpl.class);
      project.registerService(DumbService.class, DumbServiceImpl.class);
      project.registerService(ResolveCache.class, ResolveCache.class);
      project.registerService(PsiFileFactory.class, PsiFileFactoryImpl.class);
      try {
        project.registerService(JavaHelper.class, new JavaHelper.AsmHelper());
      }
      catch (LinkageError e) {
        System.out.println("ASM not available, using reflection helper: " + e);
        project.registerService(JavaHelper.class, new JavaHelper.ReflectionHelper());
      }

      project.registerService(InjectedLanguageManager.class, InjectedLanguageManagerImpl.class);
      ProgressManager.getInstance();
    }

    private static <T, S extends T> void registerApplicationService(Project project, Class<T> intfClass, Class<S> implClass) {
      MockApplication application = (MockApplication)ApplicationManager.getApplication();
      application.registerService(intfClass, implClass);
      Disposer.register(project, () -> application.getPicoContainer().unregisterComponent(intfClass.getName()));
    }

    public static MockProject initAppAndProject(Disposable rootDisposable) {
      final MockApplicationEx application = initApplication(rootDisposable);
      ComponentAdapter component = application.getPicoContainer().getComponentAdapter(ProgressManager.class.getName());
      if (component == null) {
        application.getPicoContainer().registerComponent(new AbstractComponentAdapter(ProgressManager.class.getName(), Object.class) {
          @Override
          public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException {
            return new ProgressManagerImpl();
          }

          @Override
          public void verify(PicoContainer container) throws PicoIntrospectionException {
          }
        });
      }
      Extensions.registerAreaClass("IDEA_PROJECT", null);
      MockProjectEx project = new MockProjectEx(rootDisposable);
      MutablePicoContainer appContainer = application.getPicoContainer();
      registerComponentInstance(appContainer, MessageBus.class, MessageBusFactory.newMessageBus(application));
      final MockEditorFactory editorFactory = new MockEditorFactory();
      registerComponentInstance(appContainer, EditorFactory.class, editorFactory);
      registerComponentInstance(
        appContainer, FileDocumentManager.class,
        new MockFileDocumentManagerImpl(editorFactory::createDocument, FileDocumentManagerImpl.HARD_REF_TO_DOCUMENT_KEY)
      );
      registerComponentInstance(appContainer, PsiDocumentManager.class, new MockPsiDocumentManager());
      registerComponentInstance(appContainer, FileTypeManager.class, new MockFileTypeManager(new MockLanguageFileType(PlainTextLanguage.INSTANCE, "txt")));
      registerApplicationService(project, PsiBuilderFactory.class, PsiBuilderFactoryImpl.class);
      registerApplicationService(project, DefaultASTFactory.class, DefaultASTFactoryImpl.class);
      registerApplicationService(project, ReferenceProvidersRegistry.class, ReferenceProvidersRegistryImpl.class);
      project.registerService(PsiManager.class, MockPsiManager.class);
      project.registerService(PsiFileFactory.class, PsiFileFactoryImpl.class);
      project.registerService(StartupManager.class, StartupManagerImpl.class);
      project.registerService(CachedValuesManager.class, new CachedValuesManagerImpl(project, new PsiCachedValuesFactory(PsiManager.getInstance(project))));
      registerExtensionPoint(FileTypeFactory.FILE_TYPE_FACTORY_EP, FileTypeFactory.class);
      registerExtensionPoint(MetaLanguage.EP_NAME, MetaLanguage.class);
      return project;
    }

    public static MockApplicationEx initApplication(Disposable rootDisposable) {
      MockApplicationEx instance = new MockApplicationEx(rootDisposable);
      ApplicationManager.setApplication(instance, FileTypeManager::getInstance, rootDisposable);
      instance.registerService(EncodingManager.class, EncodingManagerImpl.class);
      return instance;
    }

    public static <T> void registerExtensionPoint(ExtensionPointName<T> extensionPointName, final Class<T> aClass) {
      registerExtensionPoint(Extensions.getRootArea(), extensionPointName, aClass);
    }

    public static <T> void registerExtensionPoint(ExtensionsArea area, ExtensionPointName<T> extensionPointName, Class<? extends T> aClass) {
      final String name = extensionPointName.getName();
      if (!area.hasExtensionPoint(name)) {
        ExtensionPoint.Kind kind = aClass.isInterface() || (aClass.getModifiers() & Modifier.ABSTRACT) != 0 ? INTERFACE : BEAN_CLASS;
        area.registerExtensionPoint(name, aClass.getName(), kind);
      }
    }

    public static <T> void registerComponentInstance(MutablePicoContainer container, Class<T> key, T implementation) {
      container.unregisterComponent(key);
      container.registerComponentInstance(key, implementation);
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
}
