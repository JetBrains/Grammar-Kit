/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.psi.impl;

import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.UserDataHolderEx;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.RenameableFakePsiElement;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.ArrayUtil;
import com.intellij.util.CommonProcessors;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.jflex.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

import static org.intellij.jflex.psi.impl.JFlexPsiImplUtil.computeDefinitions;

/**
 * @author gregsh
 */
class StateRef extends PsiReferenceBase<PsiElement> {
  StateRef(JFlexStateReference o) {
    super(o, TextRange.from(0, o.getTextRange().getLength()));
  }

  StateRef(JFlexJavaCode o, TextRange range) {
    super(o, range);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    final String name = getRangeInElement().substring(getElement().getText());
    if (JFlexPsiImplUtil.isYYINITIAL(name)) {
      return resolveYYINITIAL(getElement());
    }
    CommonProcessors.FindFirstProcessor<JFlexStateDefinition> processor =
      new CommonProcessors.FindFirstProcessor<JFlexStateDefinition>() {
        @Override
        protected boolean accept(JFlexStateDefinition o) {
          return Comparing.equal(o.getName(), name);
        }
      };
    processStateVariants(getElement(), processor);
    return processor.getFoundValue();
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    CommonProcessors.CollectProcessor<PsiElement> processor =
      new CommonProcessors.CollectProcessor<>();
    processor.process(resolveYYINITIAL(getElement()));
    processStateVariants(getElement(), processor);
    return ArrayUtil.toObjectArray(processor.getResults());
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    PsiElement e = getElement();
    if (e instanceof JFlexStateReference) {
      return ((JFlexStateReference)e).getId()
        .replace(JFlexPsiElementFactory.createIdFromText(e.getProject(), newElementName));
    }
    else if (e instanceof JFlexJavaCode) {
      String text = StringUtil.replaceSubstring(e.getText(), getRangeInElement(), newElementName);
      return e.replace(JFlexPsiElementFactory.createJavaCodeFromText(e.getProject(), text));
    }
    throw new UnsupportedOperationException(e.toString());
  }

  private static boolean processStateVariants(PsiElement context, Processor<? super JFlexStateDefinition> processor) {
    final PsiFile containingFile = context.getContainingFile();
    List<JFlexStateDefinition> macros = CachedValuesManager.getCachedValue(
      containingFile,
      () -> CachedValueProvider.Result.create(computeDefinitions(containingFile, JFlexStateDefinition.class), containingFile));
    return ContainerUtil.process(macros, processor);
  }

  private static final Key<YYINITIALElement> YYINITIAL_ELEMENT = Key.create("YYINITIAL_ELEMENT");

  private static YYINITIALElement resolveYYINITIAL(PsiElement element) {
    PsiFile containingFile = element.getContainingFile();
    return ((UserDataHolderEx)containingFile)
      .putUserDataIfAbsent(YYINITIAL_ELEMENT, new YYINITIALElement(containingFile));
  }

  private static class YYINITIALElement extends RenameableFakePsiElement
    implements JFlexComposite, PsiNameIdentifierOwner {

    YYINITIALElement(PsiFile containingFile) {
      super(containingFile);
    }

    @Override
    public String getName() {
      return "YYINITIAL";
    }

    @Override
    public void navigate(boolean requestFocus) {
    }

    @Override
    public String getTypeName() {
      return "Initial State";
    }

    @Nullable
    @Override
    public Icon getIcon() {
      return null;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
      return null;
    }
  }
}
