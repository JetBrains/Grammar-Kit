/*
 * Copyright 2011-2022 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import org.intellij.grammar.GrammarKitBundle;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.intellij.grammar.inspection.BnfSealedRuleIsNotChoiceConsistingOfReferencesInspection.*;

public class BnfSealedRuleChoiceIsNotPublicAndNotExtendedInspection extends LocalInspectionTool {

  private static final int FIRST_STABLE_JAVA_VERSION_SUPPORTING_SEALED_TYPES = 17;
  private static final int FIRST_EXPERIMENTAL_JAVA_VERSION_SUPPORTING_SEALED_TYPES = 15;

  @Override
  public boolean runForWholeFile() {
    return true; // Because it needs to resolve references
  }

  @Override
  public ProblemDescriptor @Nullable [] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
    if (!(file instanceof BnfFile bnfFile)) return null;
    var incorrectJavaVersionElement = getJavaVersionConfigurationThatDoesNotSupportSealedTypes(bnfFile);

    var namesOfExtendedRules = RuleGraphHelper.findAllRuleNamesThatAreSubClassedByExtendsIn(bnfFile);
    var holder = new ProblemsHolder(manager, file, isOnTheFly);
    var rules = bnfFile.getRules();
    var rootRule = rules.get(0);
    for (BnfRule rule : rules) {
      var sealedModifier = findModifierByText(rule, "sealed");
      if (sealedModifier == null) continue;
      if (incorrectJavaVersionElement != null) {
        holder.registerProblem(
          sealedModifier,
          GrammarKitBundle.message("inspection.message.sealed.rule.requires.java.15"),
          createQuickFixToRemove(sealedModifier, rule.getName()),
          createQuickFixToUpgradeJavaVersion(incorrectJavaVersionElement, FIRST_STABLE_JAVA_VERSION_SUPPORTING_SEALED_TYPES)
        );
      }
      if (namesOfExtendedRules.contains(rule.getName())) {
        holder.registerProblem(
          rule.getId(),
          GrammarKitBundle.message("inspection.message.sealed.rule.may.not.be.extended.to"),
          createQuickFixToRemove(sealedModifier, rule.getName()),
          createQuickFixToRemoveExtendsAttributesReferencing(rule.getName(), bnfFile)
        );
      }
      if (!(rule.getExpression() instanceof BnfChoice choice)) continue;

      for (BnfExpression option : choice.getExpressionList()) {
        if (!(option instanceof BnfReferenceOrToken token)) continue;
        var subRule = token.resolveRule();
        if (subRule == null) continue;

        var subRuleIsImplicitlyPrivate = subRule.equals(rootRule);
        if (subRuleIsImplicitlyPrivate) {
          holder.registerProblem(
            option,
            GrammarKitBundle.message("inspection.message.sealed.rule.choice.that.is.not.public"),
            createQuickFixToRemove(sealedModifier, rule.getName())
          );
          continue;
        }

        var illegalSealedSubRuleModifier = findModifierThatMakesThisRuleNotPublic(subRule);
        if (illegalSealedSubRuleModifier == null) continue;

        holder.registerProblem(
          option,
          GrammarKitBundle.message("inspection.message.sealed.rule.choice.that.is.not.public"),
          createQuickFixToRemove(sealedModifier, rule.getName()),
          createQuickFixToRemove(illegalSealedSubRuleModifier, subRule.getName())
        );
      }
    }
    return holder.getResultsArray();
  }

  private static @Nullable BnfStringLiteralExpression getJavaVersionConfigurationThatDoesNotSupportSealedTypes(@NotNull BnfFile bnfFile) {
    var generateAttribute = bnfFile.findAttribute(null, KnownAttribute.GENERATE, null);
    if (generateAttribute == null) return null;
    if (!(generateAttribute.getExpression() instanceof BnfValueList generateValues)) return null;
    for (BnfListEntry entry : generateValues.getListEntryList()) {
      var entryId = entry.getId();
      if (entryId == null) continue;
      if (!entryId.textMatches("java")) continue;
      var versionExpression = entry.getLiteralExpression();
      if (versionExpression == null) return null;
      String versionStringWithoutQuotes = StringUtil.trim(versionExpression.getString().getText(), it -> it != '"');
      try {
        if (Integer.parseInt(versionStringWithoutQuotes) >= FIRST_EXPERIMENTAL_JAVA_VERSION_SUPPORTING_SEALED_TYPES) return null;
        return versionExpression;
      }
      catch (NumberFormatException ignored) {
        return null;
      }
    }
    return null;
  }

  @Nullable
  private static BnfModifier findModifierThatMakesThisRuleNotPublic(BnfRule subRule) {
    return findModifierByText(subRule, "private");
  }
}
