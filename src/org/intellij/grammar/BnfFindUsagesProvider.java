/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.ElementDescriptionUtil;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewNodeTextLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class BnfFindUsagesProvider implements FindUsagesProvider {
  @Override
  public WordsScanner getWordsScanner() {
    return null;
  }

  @Override
  public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
    return psiElement instanceof BnfRule || psiElement instanceof BnfAttr;
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
}
