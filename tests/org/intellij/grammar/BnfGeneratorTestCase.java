package org.intellij.grammar;

import com.intellij.concurrency.AsyncFutureFactory;
import com.intellij.concurrency.AsyncFutureFactoryImpl;
import com.intellij.concurrency.JobLauncher;
import com.intellij.concurrency.JobLauncherImpl;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.DumbServiceImpl;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiReferenceService;
import com.intellij.psi.PsiReferenceServiceImpl;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.search.CachesBasedRefSearcher;
import com.intellij.psi.impl.search.PsiSearchHelperImpl;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageManagerImpl;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.testFramework.ParsingTestCase;

/**
 * @author gregsh
 */
public class BnfGeneratorTestCase extends ParsingTestCase {
  public BnfGeneratorTestCase(String testDataName) {
    super(testDataName, "bnf", new BnfParserDefinition());
  }

  @Override
  protected String getTestDataPath() {
    return "testData";
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Extensions.getRootArea().registerExtensionPoint("com.intellij.referencesSearch", "com.intellij.util.QueryExecutor");
    Extensions.getRootArea().registerExtensionPoint("com.intellij.useScopeEnlarger", "com.intellij.psi.search.UseScopeEnlarger");
    Extensions.getRootArea().registerExtensionPoint("com.intellij.languageInjector", "com.intellij.psi.LanguageInjector");
    Extensions.getRootArea().registerExtensionPoint("com.intellij.codeInsight.containerProvider", "com.intellij.codeInsight.ContainerProvider");
    Extensions.getArea(getProject()).registerExtensionPoint("com.intellij.multiHostInjector", "com.intellij.lang.injection.MultiHostInjector");
    Extensions.getRootArea().getExtensionPoint("com.intellij.referencesSearch").registerExtension(new CachesBasedRefSearcher());
    registerApplicationService(PsiReferenceService.class, new PsiReferenceServiceImpl());
    registerApplicationService(JobLauncher.class, new JobLauncherImpl());
    registerApplicationService(AsyncFutureFactory.class, new AsyncFutureFactoryImpl());
    getProject().registerService(PsiSearchHelper.class, new PsiSearchHelperImpl(getPsiManager()));
    getProject().registerService(DumbService.class, new DumbServiceImpl(getProject(), getProject().getMessageBus()));
    getProject().registerService(PsiFileFactory.class, new PsiFileFactoryImpl(getPsiManager()));
    getProject().registerService(ResolveCache.class, new ResolveCache(getProject().getMessageBus()));
    InjectedLanguageManagerImpl languageManager = new InjectedLanguageManagerImpl(getProject(), DumbService.getInstance(getProject()));
    Disposer.register(getProject(), languageManager);
    getProject().registerService(InjectedLanguageManager.class, languageManager);
    ProgressManager.getInstance();
  }

}
