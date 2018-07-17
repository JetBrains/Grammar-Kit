/*
 * Copyright 2011-present Greg Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
