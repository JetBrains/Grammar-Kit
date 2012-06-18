package org.intellij.grammar;

import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.intellij.grammar.inspection.*;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/29/11
 * Time: 3:02 PM
 *
 * @author Vadim Romansky
 */
public class BnfInspectionTest extends LightPlatformCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "testData/inspection";
  }

  public void testBnfGrammar() throws Exception {
    VirtualFile virtualFile = myFixture.copyFileToProject("../../grammars/Grammar.bnf", "Grammar.bnf");
    PsiFile psiFile = myFixture.configureByFile("Grammar.bnf");
    toggleGrammarKitSrc(myModule, getTestDataPath());
    try {
      doTest();
    }
    finally {
      toggleGrammarKitSrc(myModule, getTestDataPath());
    }
  }

  private static void toggleGrammarKitSrc(Module module, String testDataPath) throws Exception {
    JavaPsiFacade facade = JavaPsiFacade.getInstance(module.getProject());
    boolean add = facade.findPackage("org.intellij.grammar.psi") == null;
    AccessToken accessToken = ApplicationManager.getApplication().acquireWriteActionLock(null);
    try {
      ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
      String supportUrl = getUrl(testDataPath + "/../../support");
      String genUrl = getUrl(testDataPath + "/../../gen");
      if (add) {
        model.addContentEntry(supportUrl).addSourceFolder(supportUrl, false);
        model.addContentEntry(genUrl).addSourceFolder(genUrl, false);
      }
      else {
        for (ContentEntry entry : model.getContentEntries()) {
          if (supportUrl.equals(entry.getUrl()) || genUrl.equals(entry.getUrl())) {
            model.removeContentEntry(entry);
          }
        }
      }
      model.commit();
    }
    finally {
      accessToken.finish();
    }
    assertTrue("GrammarKit src problem", add == (null != facade.findPackage("org.intellij.grammar.psi")));
  }

  private static String getUrl(final String path) throws Exception {
    return VfsUtil.pathToUrl(FileUtil.toSystemIndependentName(new File(path).getCanonicalPath()));
  }

  public void testSelf() { doFileTest(); }
  public void testDuplicateDefinition() { doTest("<warning>rule</warning>::= blablabla rule1" + "\n" + "<warning>rule</warning> ::=aaaaaaaaa"); }
  public void testSuspiciousToken() { doTest("rule ::= <warning>suspicious_token</warning>"); }
  public void testIdenticalBranchInChoice() { doTest("grammar ::= <warning>token</warning>|<warning>token</warning>"); }
  public void testComplexIdenticalBranchInChoice() { doTest("grammar ::= a b (c | <warning>(d e*)</warning>|<warning>(d /* */ e*)</warning>)"); }
  public void testLeftRecursion1() { doTest("<warning>grammar</warning> ::= grammar"); }
  public void testLeftRecursion2() { doTest("<warning>grammar</warning> ::= [r] [(r | grammar)]"); }
  public void testLeftRecursion3() { doTest("<warning>grammar</warning> ::= [r] [(r | rule)] <warning>rule</warning> ::= [r] ([r | grammar] r)"); }
  public void testLeftRecursion4() { doTest("meta m ::= (<<p1>> | <<p2>>) <warning>r</warning> ::= <<m x r>>"); }
  public void testUnreachableBranch1() { doTest("m ::= <warning>r</warning> | B | C r ::= A?"); }
  public void testUnreachableBranch2() { doTest("m ::= A<warning>||</warning> B | C"); }
  public void testUnreachableBranch3() { doTest("m ::=<warning>|</warning> B <warning>|</warning>"); }
  public void testUnreachableBranch4() { doTest("m ::=(A | (<warning>|</warning> B<warning>|</warning>))"); }
  public void testNeverMatchingBranch1() { doTest("m ::= <warning>! A r</warning> | B | C r ::= A"); }

  private void doFileTest() {
    myFixture.configureByFile(getTestName(false)+".bnf");
    doTest();
  }

  private void doTest(String text) {
    myFixture.configureByText("a.bnf", text);
    doTest();
  }

  private void doTest() {
    myFixture.enableInspections(BnfSuspiciousTokenInspection.class,
                                BnfDuplicateRuleInspection.class,
                                BnfIdenticalChoiceBranchesInspection.class,
                                BnfLeftRecursionInspection.class,
                                BnfUnreachableChoiceBranchInspection.class);
    myFixture.checkHighlighting(true, false, false);
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

}
