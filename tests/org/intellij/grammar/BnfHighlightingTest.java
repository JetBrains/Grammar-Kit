package org.intellij.grammar;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.intellij.grammar.inspection.*;

import java.io.File;
import java.io.IOException;

/**
 * @author gregsh
 */
public class BnfHighlightingTest extends LightPlatformCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "testData/highlighting";
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    VfsRootAccess.allowRootAccess(new File(getTestDataPath()).getAbsolutePath());
  }

  public void testSelfBnf() { doFileTest(); }
  public void testSelfFlex() { doFileTest(); }
  public void testEmpty() { doFileTest(); }
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
  public void testUnusedRule1() { doTest("r ::= <warning>A</warning> ::= 1 A"); }
  public void testUnusedRule1a() { doTest("r ::= A ::= 1 B {extraRoot=true} B ::="); }
  public void testUnusedRule2() { doTest("r ::= B fake A ::= B ::= {extends=A}"); }
  public void testUnusedRule3() { doTest("r ::= B fake <warning>A</warning> ::= B ::= "); }
  public void testUnusedRule4() { doTest("r ::= {recoverWhile=A} private A ::= "); }
  public void testUnusedRule5() { doTest("r ::= {recoverWhile=A} <warning>A</warning> ::= "); }
  public void testMissingRecover() { doTest("r ::= m m ::= {recoverWhile=\"<warning>missing</warning>\"}"); }
  public void testAutoRecover() { doTest("r ::= m m ::= {recoverWhile=\"#auto\"}"); }
  public void testMetaRecover() { doTest("r ::= <<m recover>> meta m ::= {recoverWhile=\"<<param>>\"} recover ::= "); }

  public void testSuppressUnused() { doTest("r ::= \n//noinspection BnfUnusedRule\nA ::= B C B::= C::="); }

  private void doFileTest() {
    String name = getTestName(false) + ".bnf";
    String adjusted;
    if ("SelfBnf.bnf".equals(name)) adjusted = "../../grammars/Grammar.bnf";
    else if ("SelfFlex.bnf".equals(name)) adjusted = "../../grammars/JFlex.bnf";
    else adjusted = name;

    boolean toggleSrc = !adjusted.equals(name);
    try {
      if (toggleSrc) toggleGrammarKitSrc(myModule, getTestDataPath());
      VirtualFile file = myFixture.copyFileToProject(adjusted, new File(adjusted).getName());
      myFixture.configureFromExistingVirtualFile(file);
      doTest();
    }
    finally {
      if (toggleSrc) toggleGrammarKitSrc(myModule, getTestDataPath());
    }
  }

  private void doTest(String text) {
    myFixture.configureByText("a.bnf", text);
    doTest();
  }

  private void doTest() {
    myFixture.enableInspections(BnfResolveInspection.class,
                                BnfUnusedRuleInspection.class,
                                BnfUnusedAttributeInspection.class,
                                BnfSuspiciousTokenInspection.class,
                                BnfDuplicateRuleInspection.class,
                                BnfIdenticalChoiceBranchesInspection.class,
                                BnfLeftRecursionInspection.class,
                                BnfUnreachableChoiceBranchInspection.class);
    if ("JFlex.bnf".equals(myFixture.getFile().getName())) {
      // todo remove when suppression is implemented
      myFixture.disableInspections(new BnfSuspiciousTokenInspection());
    }
    myFixture.checkHighlighting(true, false, false);
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  private static void toggleGrammarKitSrc(Module module, String testDataPath) {
    String absolutePath = new File("").getAbsolutePath();
    JavaPsiFacade facade = JavaPsiFacade.getInstance(module.getProject());
    boolean add = facade.findPackage("org.intellij.grammar.psi") == null;
    if (add) {
      VfsRootAccess.allowRootAccess(absolutePath);
    }
    else {
      VfsRootAccess.disallowRootAccess(absolutePath);
    }
    WriteAction.run(() -> {
      ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
      String supportUrl = getUrl(testDataPath + "/../../src");
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
    });
    assertTrue("GrammarKit src problem", add == (null != facade.findPackage("org.intellij.grammar.psi")));
  }

  private static String getUrl(String path) {
    try {
      return VfsUtil.pathToUrl(FileUtil.toSystemIndependentName(new File(path).getCanonicalPath()));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
