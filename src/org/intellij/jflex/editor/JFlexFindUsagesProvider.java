/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewNodeTextLocation;
import com.intellij.usageView.UsageViewShortNameLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import org.apache.xmlbeans.impl.common.NameUtil;
import org.intellij.jflex.psi.JFlexComposite;
import org.intellij.jflex.psi.JFlexMacroDefinition;
import org.intellij.jflex.psi.JFlexStateDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author gregsh
 */
public class JFlexFindUsagesProvider implements FindUsagesProvider, ElementDescriptionProvider {
  @Override
  public WordsScanner getWordsScanner() {
    return null;
  }

  @Override
  public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
    return psiElement instanceof JFlexComposite && psiElement instanceof PsiNamedElement;
  }

  @Override
  public String getHelpId(@NotNull PsiElement psiElement) {
    return null;
  }

  @Override
  public @NotNull String getType(@NotNull PsiElement element) {
    return ElementDescriptionUtil.getElementDescription(element, UsageViewTypeLocation.INSTANCE);
  }

  @Override
  public @NotNull String getDescriptiveName(@NotNull PsiElement element) {
    return ElementDescriptionUtil.getElementDescription(element, UsageViewLongNameLocation.INSTANCE);
  }

  @Override
  public @NotNull String getNodeText(@NotNull PsiElement element, boolean useFullName) {
    return ElementDescriptionUtil.getElementDescription(element, UsageViewNodeTextLocation.INSTANCE);
  }

  @Override
  public @Nullable String getElementDescription(@NotNull PsiElement psiElement, @NotNull ElementDescriptionLocation location) {
    if (location == UsageViewNodeTextLocation.INSTANCE && psiElement instanceof JFlexComposite) {
      return getElementDescription(psiElement, UsageViewTypeLocation.INSTANCE) + " " +
             "'" + getElementDescription(psiElement, UsageViewShortNameLocation.INSTANCE) + "'";
    }
    if (psiElement instanceof JFlexMacroDefinition) {
      if (location == UsageViewTypeLocation.INSTANCE) {
        return "Macro Definition";
      }
      return ((JFlexMacroDefinition)psiElement).getName();
    }
    else if (psiElement instanceof JFlexStateDefinition) {
      if (location == UsageViewTypeLocation.INSTANCE) {
        return "State Definition";
      }
      return ((JFlexStateDefinition)psiElement).getName();
    }
    else if (psiElement instanceof JFlexComposite) {
      if (location == UsageViewTypeLocation.INSTANCE) {
        ASTNode node = psiElement.getNode();
        return node == null? "Initial State" : StringUtil.join(NameUtil.splitWords(node.getElementType().toString(), false), " ");
      }
      return psiElement instanceof PsiNamedElement ? ((PsiNamedElement)psiElement).getName() : psiElement.getClass().getSimpleName();
    }
    return null;
  }
}
