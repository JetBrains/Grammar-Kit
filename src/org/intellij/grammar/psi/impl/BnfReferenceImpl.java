/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.psi.impl;

import com.intellij.codeInsight.daemon.EmptyResolveMessageProvider;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author gregsh
 */
public class BnfReferenceImpl<T extends BnfComposite> extends PsiReferenceBase<T> implements EmptyResolveMessageProvider {

  public BnfReferenceImpl(@NotNull T element, TextRange range) {
    super(element, range);
  }

  @Override
  public PsiElement resolve() {
    PsiFile containingFile = myElement.getContainingFile();
    String referenceName = getRangeInElement().substring(myElement.getText());
    boolean isExternal = GrammarUtil.isExternalReference(myElement);
    PsiElement result = containingFile instanceof BnfFile? ((BnfFile)containingFile).getRule(referenceName) : null;
    if (result != null || !isExternal) return result;

    return resolveMethod();
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }

  @Nullable
  private PsiElement resolveMethod() {
    String referenceName = getRangeInElement().substring(myElement.getText());
    PsiElement parent = myElement.getParent();
    int paramCount = parent instanceof BnfSequence ? ((BnfSequence)parent).getExpressionList().size() - 1 :
                     parent instanceof BnfExternalExpression ? ((BnfExternalExpression)parent).getArguments().size() : 0;
    BnfRule rule = ObjectUtils.notNull(PsiTreeUtil.getParentOfType(myElement, BnfRule.class));
    String parserClass = ParserGeneratorUtil.getAttribute(rule, KnownAttribute.PARSER_UTIL_CLASS);
    // paramCount + 2 (builder and level)
    JavaHelper helper = JavaHelper.getJavaHelper(myElement);
    for (String className = parserClass; className != null; className = helper.getSuperClassName(className)) {
      List<NavigatablePsiElement> methods = helper.findClassMethods(className, JavaHelper.MethodType.STATIC, referenceName, paramCount + 2);
      PsiElement first = ContainerUtil.getFirstItem(methods);
      if (first != null) return first;
    }
    return null;
  }

  @NotNull
  @Override
  public String getUnresolvedMessagePattern() {
    return GrammarUtil.isExternalReference(myElement) ?
           "Unresolved meta rule or method ''{0}''" :
           "Unresolved rule ''{0}''";
  }
}
