/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ThreeState;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfReferenceImpl;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author gregsh
 */
public class BnfResolveInspection extends LocalInspectionTool {
  @Override
  public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
    return new BnfVisitor<Void>() {
      @Override
      public Void visitReferenceOrToken(@NotNull BnfReferenceOrToken o) {
        PsiElement parent = o.getParent();
        String text = o.getText();
        if (parent instanceof BnfAttr && "true".equals(text) || "false".equals(text)) {
          return null;
        }
        else if (parent instanceof BnfListEntry && ((BnfListEntry)parent).getId() == o) {
          PsiReference reference = parent.findReferenceAt(o.getStartOffsetInParent());
          PsiElement resolve = reference == null ? null : reference.resolve();
          if (resolve == null) {
            holder.registerProblem(o, "Unresolved method reference");
          }
        }
        else {
          PsiReference reference = o.getReference();
          Object resolve = reference == null ? null : reference.resolve();
          if (resolve == null) {
            if (parent instanceof BnfAttr) {
              holder.registerProblem(o, "Unresolved rule reference");
            }
            else if (GrammarUtil.isExternalReference(o)) {
              holder.registerProblem(o, "Unresolved rule or method reference");
            }
          }
        }

        return null;
      }

      @Override
      public Void visitStringLiteralExpression(@NotNull BnfStringLiteralExpression o) {
        PsiElement parent = o.getParent();
        if (parent instanceof BnfAttrPattern) {
          PsiReference reference = o.getReference();
          if (reference instanceof PsiPolyVariantReference && ((PsiPolyVariantReference)reference).multiResolve(false).length == 0) {
            holder.registerProblem(o, "Pattern doesn't match any rule");
          }
        }
        else if (parent instanceof BnfAttr || parent instanceof BnfListEntry) {
          String attrName = Objects.requireNonNull(PsiTreeUtil.getParentOfType(o, BnfAttr.class)).getName();
          KnownAttribute<?> attribute = KnownAttribute.getCompatibleAttribute(attrName);
          String value = StringUtil.unquoteString(o.getText());
          boolean checkReferences =
            attribute != null && attribute != KnownAttribute.NAME && !attribute.getName().endsWith("Factory") &&
            !(attribute == KnownAttribute.RECOVER_WHILE &&
              (BnfConstants.RECOVER_AUTO.equals(value) ||
               GrammarUtil.isDoubleAngles(value) && ParserGeneratorUtil.Rule.isMeta(ParserGeneratorUtil.Rule.of(o))));
          if (checkReferences) {
            TextRange valueRange = ElementManipulators.getValueTextRange(o);
            ThreeState reportAtEnd = ThreeState.UNSURE;
            PsiReference refAtEnd = null;
            for (PsiReference reference : o.getReferences()) {
              if (reference.isSoft()) continue;
              boolean atEnd = valueRange.getEndOffset() == reference.getRangeInElement().getEndOffset();
              PsiElement resolve = reference.resolve();
              if (resolve != null) {
                reportAtEnd = ThreeState.NO;
              }
              else if (!atEnd) {
                if (reference.getRangeInElement().getLength() == 1 && 
                    "?".equals(reference.getCanonicalText()) &&
                    !(reference instanceof BnfReferenceImpl)) continue;
                reportAtEnd = ThreeState.NO;
                holder.registerProblem(reference, 
                                       ProblemsHolder.unresolvedReferenceMessage(reference),
                                       ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
              }
              else {
                if (reportAtEnd != ThreeState.NO) reportAtEnd = ThreeState.YES; 
                if (refAtEnd == null ||
                    refAtEnd.getRangeInElement().getLength() > reference.getRangeInElement().getLength()) {
                  refAtEnd = reference;
                }
              }
            }
            if (reportAtEnd == ThreeState.YES) {
              holder.registerProblem(refAtEnd,
                                     ProblemsHolder.unresolvedReferenceMessage(refAtEnd),
                                     ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
            }
          }
        }
        return null;
      }
    };
  }
}
