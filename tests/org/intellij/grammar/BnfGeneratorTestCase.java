package org.intellij.grammar;

import com.intellij.concurrency.JobLauncher;
import com.intellij.concurrency.JobLauncherImpl;
import com.intellij.lang.LanguageASTFactory;
import com.intellij.lang.LanguageBraceMatching;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.DumbServiceImpl;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiReferenceService;
import com.intellij.psi.PsiReferenceServiceImpl;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageManagerImpl;

/**
 * @author gregsh
 */
public class BnfGeneratorTestCase extends AbstractParsingTestCase {
  public BnfGeneratorTestCase(String testDataName) {
    super(testDataName, "bnf", new BnfParserDefinition());
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    getApplication().registerService(JobLauncher.class, JobLauncherImpl.class);
    getApplication().registerService(PsiReferenceService.class, PsiReferenceServiceImpl.class);
    getProject().registerService(DumbService.class, DumbServiceImpl.class);
    getProject().registerService(InjectedLanguageManager.class, InjectedLanguageManagerImpl.class);
    getProject().registerService(PsiFileFactory.class, PsiFileFactoryImpl.class);
    getProject().registerService(ResolveCache.class, ResolveCache.class);
    LightPsi.Init.initExtensions(getApplication(), getProject());
    addExplicitExtension(LanguageASTFactory.INSTANCE, myLanguage, new BnfASTFactory());
    addExplicitExtension(LanguageBraceMatching.INSTANCE, myLanguage, new BnfBraceMatcher());
  }

}
