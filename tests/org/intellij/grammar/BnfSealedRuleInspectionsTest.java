/*
 * Copyright 2011-2022 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInspection.ProblemDescriptorBase;
import com.intellij.codeInspection.ex.LocalInspectionToolWrapper;
import com.intellij.codeInspection.ui.InspectionToolPresentation;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.InspectionTestUtil;
import com.intellij.testFramework.InspectionsKt;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.intellij.grammar.inspection.BnfSealedRuleChoiceIsNotPublicAndNotExtendedInspection;
import org.intellij.grammar.inspection.BnfSealedRuleIsNotChoiceConsistingOfReferencesInspection;
import org.intellij.lang.annotations.Language;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@SuppressWarnings("SameParameterValue")
public class BnfSealedRuleInspectionsTest extends BasePlatformTestCase {
  public void testInspectionIsShownForSealedRulesThatAreNotAChoice() {
    assertTriggersInspectionAtSelectionIn(
      "sealed foo ::= <selection>a</selection>",
      "inspection.message.sealed.rule.that.is.not.choice"
    );
    assertTriggersInspectionAtSelectionIn(
      "sealed foo ::= <selection>a (b | c)</selection>",
      "inspection.message.sealed.rule.that.is.not.choice"
    );
  }

  public void testThatTheFirstQuickFixRemovesTheSealedModifierOnARuleThatIsNotAChoice() {
    assertQuickFixChangesCodeAtSelectionIn(
      "sealed foo ::= <selection>a</selection>",
      0,
      "foo ::= a"
    );
  }

  public void testInspectionResultIsShownForSealedRuleChoicesThatAreNotAReference() {
    assertTriggersInspectionAtSelectionIn(
      "sealed foo ::= a | b | <selection>'c'</selection>",
      "inspection.message.sealed.rule.choice.that.is.not.a.reference"
    );
  }

  public void testThatTheFirstQuickFixRemovesTheSealedModifierOnAChoiceOptionThatIsNotAReference() {
    assertQuickFixChangesCodeAtSelectionIn(
      "sealed foo ::= a | b | <selection>'c'</selection>",
      0,
      "foo ::= a | b | 'c'"
    );
  }

  public void testThatTheSecondQuickFixExtractsTheExpressionThatShouldHaveBeenAReference() {
    assertQuickFixChangesCodeAtSelectionIn(
      "sealed foo ::= a | b | <selection>'c'</selection>",
      1,
      """
      sealed foo ::= a | b | rule
      rule ::= 'c'"""
    );
  }

  public void testNoInspectionIsShownForCorrectSealedRule() {
    assertNoInspectionsIn(
      """
      sealed foo ::= a | b
      a ::= 'a'
      b ::= 'b'
      """
    );
  }

  public void testSealedRuleMustNotBeBeforeJava15() {
    assertTriggersInspectionAtSelectionIn(
      """
      {
        generate=[java="8"]
      }
      <selection>sealed</selection> foo ::= a | b
      a ::= 'a'
      b ::= 'b'
      """,
      "inspection.message.sealed.rule.requires.java.15"
    );
  }

  public void testFirstQuickFixWhenJavaVersionIsTooLowRemovesSealedModifier() {
    assertQuickFixChangesCodeAtSelectionIn(
      """
      {
        generate=[java="8"]
      }
      <selection>sealed</selection> foo ::= a | b
      a ::= 'a'
      b ::= 'b'
      """,
      0,
      """
      {
        generate=[java="8"]
      }
      foo ::= a | b
      a ::= 'a'
      b ::= 'b'
      """
    );
  }

  public void testSecondQuickFixWhenJavaVersionIsTooLowUpgradesToJava17() {
    assertQuickFixChangesCodeAtSelectionIn(
      """
      {
        generate=[java="8"]
      }
      <selection>sealed</selection> foo ::= a | b
      a ::= 'a'
      b ::= 'b'
      """,
      1,
      """
      {
        generate=[java="17"]
      }
      sealed foo ::= a | b
      a ::= 'a'
      b ::= 'b'
      """
    );
  }

  public void testSealedRulesMayNotBeExtendedTo() {
    assertTriggersInspectionAtSelectionIn(
      """
      sealed <selection>foo</selection> ::= a | b
      a ::= 'a'
      b ::= 'b'
      c ::= 'c' {extends=foo}
      """,
      "inspection.message.sealed.rule.may.not.be.extended.to"
    );
  }

  public void testFirstQuickFixOnSealedTypeThatIsExtendedToRemovesSealedModifier() {
    assertQuickFixChangesCodeAtSelectionIn(
      """
      sealed <selection>foo</selection> ::= a | b
      a ::= 'a'
      b ::= 'b'
      c ::= 'c' {extends=foo}
      """,
      0,
      """
      foo ::= a | b
      a ::= 'a'
      b ::= 'b'
      c ::= 'c' {extends=foo}
      """
    );
  }

  public void testSecondQuickFixOnSealedTypeThatIsExtendedToRemovesAllReferencingAttributes() {
    assertQuickFixChangesCodeAtSelectionIn(
      """
      {
        extends("c")=foo
      }
      sealed <selection>foo</selection> ::= a | b
      a ::= 'a'
      b ::= 'b'
      c ::= 'c'
      d ::= 'd' {extends=foo}
      """,
      1,
      """
      sealed foo ::= a | b
      a ::= 'a'
      b ::= 'b'
      c ::= 'c'
      d ::= 'd'
      """
    );
  }

  public void testSecondQuickFixOnSealedTypeThatIsExtendedToDoesntRemoveTheAttrListIfItHasAttrsLeft() {
    assertQuickFixChangesCodeAtSelectionIn(
      """
      {
        extends("c")=foo
        tokens = []
      }
      sealed <selection>foo</selection> ::= a | b
      a ::= 'a'
      b ::= 'b'
      c ::= 'c'
      d ::= 'd' {extends=foo pin=1}
      """,
      1,
      """
      {
        tokens = []
      }
      sealed foo ::= a | b
      a ::= 'a'
      b ::= 'b'
      c ::= 'c'
      d ::= 'd' { pin=1}
      """ // Yeah there's a trailing whitespace that isn't removed, but it was too much effort to fix for little reward
    );
  }

  public void testSealedSubRulesMayNotBePrivate() {
    assertTriggersInspectionAtSelectionIn(
      """
      sealed foo ::= a | <selection>b</selection>
      a ::= 'a'
      private b ::= 'b'
      """,
      "inspection.message.sealed.rule.choice.that.is.not.public"
    );
  }

  public void testFirstQuickFixForIntentionRemovesSealedModifierWhenSubRuleIsNotPublic() {
    assertQuickFixChangesCodeAtSelectionIn(
      """
      sealed foo ::= a | <selection>b</selection>
      a ::= 'a'
      private b ::= 'b'
      """,
      0,
      """
      foo ::= a | b
      a ::= 'a'
      private b ::= 'b'
      """
    );
  }

  public void testSecondQuickFixForIntentionRemovesPrivateModifierWhenSubRuleIsNotPublic() {
    assertQuickFixChangesCodeAtSelectionIn(
      """
      sealed foo ::= a | <selection>b</selection>
      a ::= 'a'
      private b ::= 'b'
      """,
      1,
      """
      sealed foo ::= a | b
      a ::= 'a'
      b ::= 'b'
      """
    );
  }

  public void testSealedSubRulesMayNotBeImplicitlyPrivate() {
    assertTriggersInspectionAtSelectionIn(
      """
      implicitlyPrivateRule ::= 'yeet'
      sealed foo ::= a | <selection>implicitlyPrivateRule</selection>
      a ::= 'a'
      """,
      "inspection.message.sealed.rule.choice.that.is.not.public"
    );
  }

  public void testSealedSubRulesMayBeExternal() {
    assertNoInspectionsIn("""
      sealed foo ::= a | b | c | d | e | f
      external a ::= 'a'
      meta b ::= 'b'
      inner c ::= 'c'
      left d ::= 'd'
      upper e ::= 'e'
      fake f ::= 'f'
    """);
  }

  private void assertTriggersInspectionAtSelectionIn(@Language("bnf") String bnf, String key) {
    assertEquals(GrammarKitBundle.message(key), getProblemMatchingSelectionIn(bnf).getDescriptionTemplate());
  }

  private void assertNoInspectionsIn(@Language("bnf") String bnf) {
    assertEmpty(getProblemsIn(bnf));
  }

  private void assertQuickFixChangesCodeAtSelectionIn(@Language("bnf") String bnf, int quickFixIndex, @Language("bnf") String changedBnf) {
    var problem = getProblemMatchingSelectionIn(bnf);
    var fix = Objects.requireNonNull(problem.getFixes())[quickFixIndex];

    WriteCommandAction.runWriteCommandAction(getProject(), () -> fix.applyFix(getProject(), problem));

    assertEquals(changedBnf, myFixture.getFile().getText());
  }

  private ProblemDescriptorBase getProblemMatchingSelectionIn(@Language("bnf") String bnf) {
    var problems = getProblemsIn(bnf);
    assert problems.size() != 0 : "No problems found in code:\n" + bnf;
    TextRange expectedRange = myFixture.getEditor().getCaretModel().getPrimaryCaret().getSelectionRange();
    var problemsMatchingSelection = problems.stream()
      .filter(it -> expectedRange.equals(it.getTextRange()))
      .toList();
    assert problemsMatchingSelection.size() == 1 : "Expected one inspection to be at the caret, but got: " + problemsMatchingSelection;
    return problemsMatchingSelection.get(0);
  }

  private List<ProblemDescriptorBase> getProblemsIn(@Language("bnf") String bnf) {
    PsiFile file = myFixture.configureByText("foo.bnf", bnf);
    return Stream.concat(
      getProblemsIn(file, new LocalInspectionToolWrapper(new BnfSealedRuleIsNotChoiceConsistingOfReferencesInspection())),
      getProblemsIn(file, new LocalInspectionToolWrapper(new BnfSealedRuleChoiceIsNotPublicAndNotExtendedInspection()))
    ).toList();
  }

  private Stream<ProblemDescriptorBase> getProblemsIn(PsiFile file, LocalInspectionToolWrapper inspectionTool) {
    var scope = new AnalysisScope(file);
    scope.invalidate();

    var context = InspectionsKt.createGlobalContextForTool(scope, getProject(), List.of(inspectionTool));

    InspectionTestUtil.runTool(inspectionTool, scope, context);
    InspectionToolPresentation presentation = context.getPresentation(inspectionTool);
    presentation.updateContent();
    return presentation.getProblemElements().getValues()
      .stream()
      .map(it -> (ProblemDescriptorBase)it);
  }
}
