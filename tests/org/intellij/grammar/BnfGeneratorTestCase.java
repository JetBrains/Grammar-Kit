/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.concurrency.JobLauncher;
import com.intellij.concurrency.JobLauncherImpl;
import com.intellij.core.CoreInjectedLanguageManager;
import com.intellij.lang.LanguageASTFactory;
import com.intellij.lang.LanguageBraceMatching;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.mock.MockDumbService;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiReferenceService;
import com.intellij.psi.PsiReferenceServiceImpl;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class BnfGeneratorTestCase extends AbstractParsingTestCase {
  protected static final @NotNull String BNF_FILES_DIR = "bnfFiles";
  protected final @NotNull String myBnfFilesDir;

  public BnfGeneratorTestCase(String testDataName) {
    super(testDataName, "bnf", new BnfParserDefinition());
    myBnfFilesDir = this.getTestDataPath() + "/" + BNF_FILES_DIR;
  }

  @SuppressWarnings("UnstableApiUsage")
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    getApplication().registerService(JobLauncher.class, JobLauncherImpl.class);
    getApplication().registerService(PsiReferenceService.class, PsiReferenceServiceImpl.class);
    getProject().registerService(DumbService.class, new MockDumbService(getProject()));
    getProject().registerService(InjectedLanguageManager.class, CoreInjectedLanguageManager.class);
    getProject().registerService(PsiFileFactory.class, PsiFileFactoryImpl.class);
    getProject().registerService(ResolveCache.class, ResolveCache.class);
    LightPsi.Init.initExtensions(getApplication(), getProject());
    addExplicitExtension(LanguageASTFactory.INSTANCE, myLanguage, new BnfASTFactory());
    addExplicitExtension(LanguageBraceMatching.INSTANCE, myLanguage, new BnfBraceMatcher());
  }
}
