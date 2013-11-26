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
import com.intellij.ide.startup.impl.StartupManagerImpl;
import com.intellij.lang.*;
import com.intellij.lang.impl.PsiBuilderFactoryImpl;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.mock.*;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.extensions.ExtensionPoint;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.extensions.ExtensionsArea;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.options.SchemesManagerFactory;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.DumbServiceImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Getter;
import com.intellij.openapi.util.Trinity;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.encoding.EncodingManager;
import com.intellij.openapi.vfs.encoding.EncodingManagerImpl;
import com.intellij.openapi.vfs.encoding.EncodingRegistry;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiCachedValuesFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.search.CachesBasedRefSearcher;
import com.intellij.psi.impl.search.PsiSearchHelperImpl;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistryImpl;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageManagerImpl;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.testFramework.MockSchemesManagerFactory;
import com.intellij.util.CachedValuesManagerImpl;
import com.intellij.util.Function;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusFactory;
import org.intellij.grammar.java.JavaHelper;
import org.jetbrains.annotations.NonNls;
import org.picocontainer.*;
import org.picocontainer.defaults.AbstractComponentAdapter;

import java.io.*;
import java.lang.reflect.Modifier;
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

  private static class MyParsing implements Disposable {

    private final Trinity<MockProjectEx,MockPsiManager,PsiFileFactoryImpl> myModel;

    MyParsing() throws Exception {
      myModel = Init.initPsiFileFactory(this);
      Init.initExtensions(myModel.first, myModel.second);
    }

    protected PsiFile createFile(@NonNls String name, String text, ParserDefinition definition) {
      Language language = definition.getFileNodeType().getLanguage();
      Init.addExplicitExtension(myModel.first, LanguageParserDefinitions.INSTANCE, language, definition);
      return myModel.third.trySetupPsiForFile(new LightVirtualFile(name, language, text), language, true, false);
    }

    @Override
    public void dispose() {
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
    
    public static Trinity<MockProjectEx, MockPsiManager, PsiFileFactoryImpl> initPsiFileFactory(Disposable rootDisposable) {
      final MockApplicationEx application = initApplication(rootDisposable);
      ComponentAdapter component = application.getPicoContainer().getComponentAdapter(ProgressManager.class.getName());
      if (component == null) {
        application.getPicoContainer().registerComponent(new AbstractComponentAdapter(ProgressManager.class.getName(), Object.class) {
          @Override
          public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException {
            return new ProgressManagerImpl(application);
          }

          @Override
          public void verify(PicoContainer container) throws PicoIntrospectionException {
          }
        });
      }
      Extensions.registerAreaClass("IDEA_PROJECT", null);
      MockProjectEx project = new MockProjectEx(rootDisposable);
      MockPsiManager psiManager = new MockPsiManager(project);
      PsiFileFactoryImpl psiFileFactory = new PsiFileFactoryImpl(psiManager);
      MutablePicoContainer appContainer = application.getPicoContainer();
      registerComponentInstance(appContainer, MessageBus.class, MessageBusFactory.newMessageBus(application));
      registerComponentInstance(appContainer, SchemesManagerFactory.class, new MockSchemesManagerFactory());
      final MockEditorFactory editorFactory = new MockEditorFactory();
      registerComponentInstance(appContainer, EditorFactory.class, editorFactory);
      registerComponentInstance(
        appContainer, FileDocumentManager.class,
        new MockFileDocumentManagerImpl(new Function<CharSequence, Document>() {
          @Override
          public Document fun(CharSequence charSequence) {
            return editorFactory.createDocument(charSequence);
          }
        }, FileDocumentManagerImpl.DOCUMENT_KEY)
      );
      registerComponentInstance(appContainer, PsiDocumentManager.class, new MockPsiDocumentManager());
      registerComponentInstance(appContainer, FileTypeManager.class, new MockFileTypeManager(new MockLanguageFileType(PlainTextLanguage.INSTANCE, "txt")));
      registerApplicationService(project, PsiBuilderFactory.class, new PsiBuilderFactoryImpl());
      registerApplicationService(project, DefaultASTFactory.class, new DefaultASTFactoryImpl());
      registerApplicationService(project, ReferenceProvidersRegistry.class, new ReferenceProvidersRegistryImpl());
      project.registerService(CachedValuesManager.class, new CachedValuesManagerImpl(project, new PsiCachedValuesFactory(psiManager)));
      project.registerService(PsiManager.class, psiManager);
      project.registerService(StartupManager.class, new StartupManagerImpl(project));
      registerExtensionPoint(FileTypeFactory.FILE_TYPE_FACTORY_EP, FileTypeFactory.class);
      return Trinity.create(project, psiManager, psiFileFactory);
    }

    public static MockApplicationEx initApplication(Disposable rootDisposable) {
      MockApplicationEx instance = new MockApplicationEx(rootDisposable);
      ApplicationManager.setApplication(
        instance,
        new Getter<FileTypeRegistry>() {
          @Override
          public FileTypeRegistry get() {
            return FileTypeManager.getInstance();
          }
        },
        new Getter<EncodingRegistry>() {
          @Override
          public EncodingRegistry get() {
            return EncodingManager.getInstance();
          }
        },
        rootDisposable
      );
      instance.registerService(EncodingManager.class, EncodingManagerImpl.class);
      return instance;
    }

    public static <T> void registerExtensionPoint(ExtensionPointName<T> extensionPointName, final Class<T> aClass) {
      registerExtensionPoint(Extensions.getRootArea(), extensionPointName, aClass);
    }

    public static <T> void registerExtensionPoint(ExtensionsArea area, ExtensionPointName<T> extensionPointName, Class<? extends T> aClass) {
      final String name = extensionPointName.getName();
      if (!area.hasExtensionPoint(name)) {
        ExtensionPoint.Kind kind = aClass.isInterface() || (aClass.getModifiers() & Modifier.ABSTRACT) != 0
                                   ? ExtensionPoint.Kind.INTERFACE
                                   : ExtensionPoint.Kind.BEAN_CLASS;
        area.registerExtensionPoint(name, aClass.getName(), kind);
      }
    }

    public static <T> T registerComponentInstance(MutablePicoContainer container, Class<T> key, T implementation) {
      Object old = container.getComponentInstance(key);
      container.unregisterComponent(key);
      container.registerComponentInstance(key, implementation);
      return (T)old;
    }

    public static <T> void addExplicitExtension(Project project, final LanguageExtension<T> instance, final Language language, final T object) {
      instance.addExplicitExtension(language, object);
      Disposer.register(project, new Disposable() {
        @Override
        public void dispose() {
          instance.removeExplicitExtension(language, object);
        }
      });
    }
  }
}
