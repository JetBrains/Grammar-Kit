package org.intellij.grammar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gregsh
 */
public class BnfTestSuite extends TestCase {
  public static Test suite() {
    final TestSuite testSuite = new TestSuite();
    testSuite.addTestSuite(BnfParserTest.class);
    testSuite.addTestSuite(BnfGeneratorTest.class);
    testSuite.addTestSuite(BnfInlineRuleTest.class);
    testSuite.addTestSuite(BnfExtractRuleTest.class);
    return testSuite;
  }
}
