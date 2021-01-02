/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.usageView.UsageViewNodeTextLocation;
import com.intellij.usageView.UsageViewShortNameLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import org.apache.xmlbeans.impl.common.NameUtil;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfComposite;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregory
 *         Date: 17.07.11 18:46
 */
public class BnfDescriptionProvider implements ElementDescriptionProvider {
  @Override
  public String getElementDescription(@NotNull PsiElement psiElement, @NotNull ElementDescriptionLocation location) {
    if (location == UsageViewNodeTextLocation.INSTANCE && psiElement instanceof BnfComposite) {
      return getElementDescription(psiElement, UsageViewTypeLocation.INSTANCE) + " " +
             "'" + getElementDescription(psiElement, UsageViewShortNameLocation.INSTANCE) + "'";
    }
    if (psiElement instanceof BnfRule) {
      if (location == UsageViewTypeLocation.INSTANCE) {
        return "Grammar Rule";
      }
      return ((BnfRule)psiElement).getName();
    }
    else if (psiElement instanceof BnfAttr) {
      if (location == UsageViewTypeLocation.INSTANCE) {
        BnfRule rule = PsiTreeUtil.getParentOfType(psiElement, BnfRule.class);
        return (rule == null ? "Grammar" : "Rule") + " Attribute";
      }
      return ((BnfAttr)psiElement).getName();
    }
    else if (psiElement instanceof BnfComposite) {
      if (location == UsageViewTypeLocation.INSTANCE) {
        IElementType elementType = PsiUtilCore.getElementType(psiElement);
        return elementType == null ? null : StringUtil.join(NameUtil.splitWords(elementType.toString(), false), " ");
      }
      return psiElement instanceof PsiNamedElement? ((PsiNamedElement) psiElement).getName() : psiElement.getClass().getSimpleName();
    }
    return null;
  }
}
