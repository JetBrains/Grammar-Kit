/*
 * Copyright 2011-2022 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.inspection;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.formatter.FormatterUtil;
import org.intellij.grammar.GrammarKitBundle;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.refactor.BnfIntroduceRuleHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class BnfSealedRuleIsNotChoiceConsistingOfReferencesInspection extends LocalInspectionTool {
  @Override
  public boolean runForWholeFile() {
    return false;
  }

  @Override
  public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder,
                                                 boolean isOnTheFly,
                                                 @NotNull LocalInspectionToolSession session) {
    return new BnfVisitor<Void>() {
      @Override
      public Void visitRule(@NotNull BnfRule rule) {
        var sealedModifier = findModifierByText(rule, "sealed");
        if (sealedModifier == null) return null;

        if (!(rule.getExpression() instanceof BnfChoice choice)) {
          holder.registerProblem(
            rule.getExpression(),
            GrammarKitBundle.message("inspection.message.sealed.rule.that.is.not.choice"),
            createQuickFixToRemove(sealedModifier, rule.getName())
          );
          return null;
        }
        for (BnfExpression subRule : choice.getExpressionList()) {
          if (subRule instanceof BnfReferenceOrToken) continue;
          PsiFile file = session.getFile();
          holder.registerProblem(
            subRule,
            GrammarKitBundle.message("inspection.message.sealed.rule.choice.that.is.not.a.reference"),
            createQuickFixToRemove(sealedModifier, rule.getName()),
            createQuickFixToExtractExpression(rule, subRule, file)
          );
        }
        return null;
      }
    };
  }

  static @Nullable BnfModifier findModifierByText(@NotNull BnfRule rule, String text) {
    for (BnfModifier modifier : rule.getModifierList()) if (modifier.getText().matches(text)) return modifier;
    return null;
  }

  static LocalQuickFix createQuickFixToRemove(@NotNull BnfModifier modifier, final String ruleName) {
    return new LocalQuickFix() {
      @Override
      public @IntentionName @NotNull String getName() {
        return GrammarKitBundle.message("action.grammar.Remove.modifier.from", modifier.getText(), ruleName);
      }

      @Override
      public @IntentionFamilyName @NotNull String getFamilyName() {
        return GrammarKitBundle.message("action.grammar.Remove.modifier");
      }

      @Override
      public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        deleteWithTrailingWhitespaceOrTryReformat(modifier);
      }
    };
  }

  static LocalQuickFix createQuickFixToUpgradeJavaVersion(@NotNull BnfStringLiteralExpression oldVersionString, int newVersion) {
    return new LocalQuickFix() {
      @Override
      public @IntentionName @NotNull String getName() {
        return GrammarKitBundle.message("action.grammar.Upgrade.java.version.to.first.stable.release");
      }

      @Override
      public @IntentionFamilyName @NotNull String getFamilyName() {
        return GrammarKitBundle.message("action.grammar.Upgrade.java");
      }

      @Override
      public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        var manipulator = ElementManipulators.getManipulator(oldVersionString);
        if (manipulator == null) return;
        manipulator.handleContentChange(oldVersionString, Integer.toString(newVersion));
      }
    };
  }

  private static void deleteWithTrailingWhitespaceOrTryReformat(@NotNull PsiElement element) {
    var nextElement = element.getNextSibling();
    element.delete();
    if (nextElement == null || !FormatterUtil.isWhitespaceOrEmpty(nextElement.getNode()) || !nextElement.isValid()) return;
    nextElement.delete();
  }

  static LocalQuickFix createQuickFixToRemoveExtendsAttributesReferencing(@NotNull String ruleName, @NotNull BnfFile containingFile) {
    return new LocalQuickFix() {
      @Override
      public @IntentionName @NotNull String getName() {
        return GrammarKitBundle.message("action.grammar.Remove.all.extends.attributes.referencing", ruleName);
      }

      @Override
      public @IntentionFamilyName @NotNull String getFamilyName() {
        return GrammarKitBundle.message("action.grammar.Remove.attributes");
      }

      @Override
      public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        for (BnfAttrs attrListElement : getAllAttrsIn(containingFile).toList()) {
          List<BnfAttr> attributes = attrListElement.getAttrList();
          for (BnfAttr attribute : attributes) {
            var expression = attribute.getExpression();
            if (expression == null || !expression.textMatches(ruleName)) continue;
            deleteWithTrailingWhitespaceOrTryReformat(attributes.size() == 1 ? attrListElement : attribute);
          }
        }
      }
    };
  }

  @NotNull
  private static Stream<BnfAttrs> getAllAttrsIn(@NotNull BnfFile containingFile) {
    return Stream.concat(
      containingFile.getRules().stream()
        .map(BnfRule::getAttrs)
        .filter(Objects::nonNull),
      containingFile.getAttributes().stream()
    );
  }

  private static @NotNull LocalQuickFix createQuickFixToExtractExpression(@NotNull BnfRule rule, BnfExpression sealedChoice, PsiFile file) {
    return new LocalQuickFix() {
      @Override
      public @IntentionName @NotNull String getName() {
        return GrammarKitBundle.message("action.grammars.IntroduceRule.description");
      }

      @Override
      public @IntentionFamilyName @NotNull String getFamilyName() {
        return GrammarKitBundle.message("action.grammars.IntroduceRule.description");
      }

      @Override
      public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        VirtualFile vFile = file.getVirtualFile();
        if (vFile == null) return;
        FileEditor fileEditor = FileEditorManager.getInstance(file.getProject()).getSelectedEditor(vFile);
        if (!(fileEditor instanceof TextEditor textEditor)) return;
        BnfIntroduceRuleHandler.invokeIntroduce(project, textEditor.getEditor(), file, rule, List.of(sealedChoice), false);
      }
    };
  }
}
