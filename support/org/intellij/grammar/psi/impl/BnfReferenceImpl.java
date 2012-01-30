/*
 * Copyright 2011-2011 Gregory Shrago
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

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author gregsh
 */
public class BnfReferenceImpl<T extends BnfCompositeElement> extends PsiReferenceBase<T> {

  public BnfReferenceImpl(@NotNull T element, TextRange range) {
    super(element, range);
  }

  @Override
  public PsiElement resolve() {
    PsiFile containingFile = myElement.getContainingFile();
    String referenceName = getRangeInElement().substring(myElement.getText());
    PsiElement result = containingFile instanceof BnfFile? ((BnfFile)containingFile).getRule(referenceName) : null;
    if (result == null && GrammarUtil.isExternalReference(myElement)) {
      BnfRule rule = PsiTreeUtil.getParentOfType(myElement, BnfRule.class);
      String parserClass = ParserGeneratorUtil.getAttribute(rule, "stubParserClass", "");
      if (StringUtil.isNotEmpty(parserClass)) {
        result = JavaHelper.getJavaHelper(myElement.getProject()).findClassMethod(parserClass, myElement.getText());
      }
    }
    return result;
  }


  @NotNull
  @Override
  public Object[] getVariants() {
    final ArrayList<LookupElement> list = new ArrayList<LookupElement>();
    PsiFile containingFile = myElement.getContainingFile();
    List<BnfRule> rules = containingFile instanceof BnfFile ? ((BnfFile)containingFile).getRules() : Collections.<BnfRule>emptyList();
    for (BnfRule rule : rules) {
      list.add(LookupElementBuilder.create(rule).setBold());
    }
    if (GrammarUtil.isExternalReference(myElement)) {
      BnfRule rule = PsiTreeUtil.getParentOfType(myElement, BnfRule.class);
      String parserClass = ParserGeneratorUtil.getAttribute(rule, "stubParserClass", "");
      if (StringUtil.isNotEmpty(parserClass)) {
        for (NavigatablePsiElement element : JavaHelper.getJavaHelper(myElement.getProject()).getClassMethods(parserClass)) {
          list.add(LookupElementBuilder.create((PsiNamedElement)element).setIcon(element.getIcon(0)));
        }
      }
    }
    return list.toArray(new Object[list.size()]);
  }

}
