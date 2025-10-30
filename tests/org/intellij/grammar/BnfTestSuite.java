/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.intellij.grammar.expression.ExpressionParserTest;
import org.intellij.grammar.generator.JavaBnfGeneratorTest;
import org.intellij.grammar.generator.KotlinBnfGeneratorTest;
import org.intellij.grammar.inspection.BnfHighlightingTest;
import org.intellij.grammar.intention.BnfConvertOptExpressionIntentionTest;
import org.intellij.grammar.intention.BnfFlipChoiceIntentionTest;
import org.intellij.jflex.JFlexCompletionTest;
import org.intellij.jflex.JFlexGenerationTest;
import org.intellij.jflex.parser.JFlexParserTest;

/**
 * @author gregsh
 */
public class BnfTestSuite extends TestCase {

  public static Test suite() {
    TestSuite testSuite = new TestSuite();
    testSuite.addTest(BnfTestSuiteFast.suite());
    testSuite.addTestSuite(BnfFirstNextTest.class);
    testSuite.addTestSuite(BnfRuleGraphTest.class);
    testSuite.addTestSuite(BnfCompletionTest.class);
    testSuite.addTestSuite(BnfHighlightingTest.class);
    testSuite.addTestSuite(BnfInlineRuleTest.class);
    testSuite.addTestSuite(BnfIntroduceRuleTest.class);
    testSuite.addTestSuite(BnfFlipChoiceIntentionTest.class);
    testSuite.addTestSuite(BnfMoveLeftRightTest.class);
    testSuite.addTestSuite(BnfConvertOptExpressionIntentionTest.class);

    testSuite.addTestSuite(JFlexCompletionTest.class);
    return testSuite;
  }

  public static class BnfTestSuiteFast extends TestCase {
    public static Test suite() {
      TestSuite testSuite = new TestSuite("Fast");
      testSuite.addTestSuite(BnfUtilTest.class);
      testSuite.addTestSuite(JFlexGenerationTest.class);
      testSuite.addTestSuite(JFlexParserTest.class);
      testSuite.addTestSuite(BnfParserTest.class);
      testSuite.addTestSuite(JavaBnfGeneratorTest.class);
      testSuite.addTestSuite(ExpressionParserTest.class);
      testSuite.addTestSuite(BnfLivePreviewParserTest.class);
      testSuite.addTestSuite(KotlinBnfGeneratorTest.class);
      return testSuite;
    }
  }
}
