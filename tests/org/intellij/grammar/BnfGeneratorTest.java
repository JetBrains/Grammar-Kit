package org.intellij.grammar;

import com.intellij.concurrency.JobLauncher;
import com.intellij.concurrency.JobLauncherImpl;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.DumbServiceImpl;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiReferenceService;
import com.intellij.psi.PsiReferenceServiceImpl;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.search.CachesBasedRefSearcher;
import com.intellij.psi.impl.search.PsiSearchHelperImpl;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageManagerImpl;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.rt.execution.junit.FileComparisonFailure;
import com.intellij.testFramework.ParsingTestCase;
import com.thaiopensource.xml.dtd.test.CompareFailException;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.psi.impl.BnfFileImpl;
import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gregsh
 */
public class BnfGeneratorTest extends ParsingTestCase {
  public BnfGeneratorTest() {
    super("generator", "bnf", new BnfParserDefinition());
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
    Extensions.getArea(getProject()).registerExtensionPoint("com.intellij.multiHostInjector", "com.intellij.lang.injection.MultiHostInjector");
    Extensions.getRootArea().getExtensionPoint("com.intellij.referencesSearch").registerExtension(new CachesBasedRefSearcher());
    registerApplicationService(PsiReferenceService.class, new PsiReferenceServiceImpl());
    registerApplicationService(JobLauncher.class, new JobLauncherImpl());
    getProject().registerService(PsiSearchHelper.class, new PsiSearchHelperImpl(getPsiManager()));
    getProject().registerService(DumbService.class, new DumbServiceImpl(getProject(), getProject().getMessageBus()));
    getProject().registerService(PsiFileFactory.class, new PsiFileFactoryImpl(getPsiManager()));
    getProject().registerService(ResolveCache.class, new ResolveCache(getProject().getMessageBus()));
    InjectedLanguageManagerImpl languageManager = new InjectedLanguageManagerImpl(getProject(), DumbService.getInstance(getProject()));
    Disposer.register(getProject(), languageManager);
    getProject().registerService(InjectedLanguageManager.class, languageManager);
    ProgressManager.getInstance();
  }

  public void testBnfGrammar() throws Exception { doGenTest(true); }
  public void testSelf() throws Exception { doGenTest(true); }
  public void testSmall() throws Exception { doGenTest(false); }
  public void testAutopin() throws Exception { doGenTest(false); }
  public void testExternalRules() throws Exception { doGenTest(false); }
  public void testLeftAssociative() throws Exception { doGenTest(false); }
  public void testPsiGen() throws Exception { doGenTest(true); }
  public void testPsiAccessors() throws Exception { doGenTest(true); }
  public void testPsiStart() throws Exception { doGenTest(true); }
  public void testExprParser() throws Exception { doGenTest(false); }

  public void testEmpty() throws Exception {
    myFile = createPsiFile("empty.bnf", "{ }");
    ParserGenerator parserGenerator = new ParserGenerator((BnfFileImpl)myFile, "", myFullDataPath);
    parserGenerator.setUnitTestMode(true);
    parserGenerator.generate();
  }

  @Override
  protected String loadFile(@NonNls String name) throws IOException {
    if (name.equals("BnfGrammar.bnf")) return super.loadFile("../../grammars/Grammar.bnf");
    return super.loadFile(name);
  }

  public void doGenTest(final boolean generatePsi) throws Exception {
    final String name = getTestName(false);
    String text = loadFile(name + "." + myFileExt);
    myFile = createPsiFile(name, text.replaceAll("generatePsi=\"[^\"]*\"", "\0 generatePsi=" + generatePsi));
    List<File> filesToCheck = new ArrayList<File>();
    filesToCheck.add(new File(myFullDataPath, name + ".java"));
    if (generatePsi) {
      filesToCheck.add(new File(myFullDataPath, name + ".PSI.java"));
    }
    for (File file : filesToCheck) {
      if (file.exists()) {
        assertTrue(file.delete());
      }
    }

    ParserGenerator parserGenerator = new ParserGenerator((BnfFileImpl)myFile, "", myFullDataPath);
    parserGenerator.setUnitTestMode(true);
    if (generatePsi) parserGenerator.generate();
    else parserGenerator.generateParser();

    List<String> messages = new ArrayList<String>();
    try {
      for (File file : filesToCheck) {
        assertTrue("Generated file not found: "+file, file.exists());
        final String expectedName = FileUtil.getNameWithoutExtension(file) + ".expected.java";
        String result = loadFile(file.getName());
        try {
          if (OVERWRITE_TESTDATA) throw new FileNotFoundException();
          String expectedText = loadFile(expectedName);
          if (!Comparing.equal(expectedText, result)) {
            throw new FileComparisonFailure(expectedName, expectedText, result, new File(myFullDataPath + File.separator + expectedName).getAbsolutePath());
          }
        }
        catch (FileNotFoundException e) {
          FileWriter writer = new FileWriter(new File(myFullDataPath, expectedName));
          try {
            writer.write(result);
          }
          finally {
            writer.close();
          }
          messages.add("No output text found. File " + expectedName + " created.");
        }
      }
    }
    finally {
      for (File file : filesToCheck) {
        file.delete();
      }
    }
    for (String message : messages) {
      System.err.println(message);
    }
    assertTrue(OVERWRITE_TESTDATA || messages.isEmpty());
  }
}
