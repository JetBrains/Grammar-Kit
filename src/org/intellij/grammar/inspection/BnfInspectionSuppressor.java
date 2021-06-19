/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.inspection;

import com.intellij.codeInsight.daemon.impl.actions.SuppressByCommentFix;
import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.codeInspection.SuppressionUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.JBIterable;
import org.intellij.grammar.BnfLanguage;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

import static com.intellij.codeInspection.SuppressionUtilCore.SUPPRESS_INSPECTIONS_TAG_NAME;

/**
 * @author gregsh
 */
public class BnfInspectionSuppressor implements InspectionSuppressor {
  @Override
  public SuppressQuickFix @NotNull [] getSuppressActions(@Nullable PsiElement element, @NotNull String toolId) {
    return new SuppressQuickFix[]{
      new MyFix(toolId, BnfRule.class),
      new MyFix(toolId, BnfAttr.class),
      new MyFix(toolId, BnfFile.class)
    };
  }

  @Override
  public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
    if (!(element instanceof BnfComposite)) return false;
    PsiFile file = element.getContainingFile();
    if (isSuppressedInComment(file, toolId + "ForFile")) return true;
    JBIterable<PsiElement> parents = SyntaxTraverser.psiApi().parents(element);
    for (PsiElement parent : parents) {
      if (parent == file) break;
      if (isSuppressedInComment(parent, toolId)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isSuppressedInComment(@Nullable PsiElement root, @NotNull String toolId) {
    JBIterable<PsiElement> leaves =
      root instanceof PsiFile ?
      JBIterable.generate(PsiTreeUtil.getDeepestFirst(root), PsiTreeUtil::nextLeaf) :
      JBIterable.generate(root, PsiTreeUtil::prevLeaf).skip(1);
    JBIterable<PsiComment> comments = leaves
      .takeWhile(e -> e instanceof PsiWhiteSpace || e instanceof PsiComment ||
                      e instanceof BnfSequence && e.getTextLength() == 0)
      .filter(PsiComment.class);
    for (PsiComment comment : comments) {
      Matcher matcher = SuppressionUtil.SUPPRESS_IN_LINE_COMMENT_PATTERN.matcher(comment.getText());
      if (matcher.matches()) {
        if (SuppressionUtil.isInspectionToolIdMentioned(matcher.group(1), toolId)) {
          return true;
        }
      }
    }
    return false;
  }

  private static class MyFix extends SuppressByCommentFix {

    MyFix(@NotNull String toolId, @NotNull Class<? extends PsiElement> clazz) {
      super(BnfFile.class.isAssignableFrom(clazz) ? toolId + "ForFile": toolId, clazz);
    }

    @Override
    public @NotNull String getName() {
      Class<? extends PsiElement> clazz = mySuppressionHolderClass;
      String target = BnfRule.class.isAssignableFrom(clazz) ? "rule" :
                      BnfAttr.class.isAssignableFrom(clazz) ? "attribute" :
                      BnfFile.class.isAssignableFrom(clazz) ? "file" : null;
      if (target == null) throw new AssertionError(clazz);
      return "Suppress for " + target;
    }

    @Override
    protected void createSuppression(@NotNull Project project, @NotNull PsiElement element, @NotNull PsiElement container)
      throws IncorrectOperationException {
      if (container instanceof PsiFile) {
        String text = SUPPRESS_INSPECTIONS_TAG_NAME + " " + myID;
        PsiComment comment = SuppressionUtil.createComment(project, text, BnfLanguage.INSTANCE);
        container.addAfter(comment, null);
      }
      else {
        super.createSuppression(project, element, container);
      }
    }
  }
}
