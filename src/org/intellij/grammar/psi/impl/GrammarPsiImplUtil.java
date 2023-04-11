/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.psi.impl;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;

/**
 * @author gregsh
 */
public class GrammarPsiImplUtil {
  public static PsiReference @NotNull [] getReferences(BnfListEntry o) {
    BnfAttr attr = PsiTreeUtil.getParentOfType(o, BnfAttr.class);
    if (attr == null || !Objects.equals(KnownAttribute.METHODS.getName(), attr.getName())) return PsiReference.EMPTY_ARRAY;
    PsiElement id = o.getId();
    BnfLiteralExpression value = o.getLiteralExpression();
    if (id == null || value != null) return PsiReference.EMPTY_ARRAY;
    String psiImplUtilClass = getRootAttribute(attr, KnownAttribute.PSI_IMPL_UTIL_CLASS);
    JavaHelper javaHelper = JavaHelper.getJavaHelper(o);

    return new PsiReference[] {
      new PsiPolyVariantReferenceBase<>(o, TextRange.from(id.getStartOffsetInParent(), id.getTextLength())) {

        private List<NavigatablePsiElement> getTargetMethods(String methodName) {
          BnfRule rule = PsiTreeUtil.getParentOfType(getElement(), BnfRule.class);
          String mixinClass = rule == null ? null : getAttribute(rule, KnownAttribute.MIXIN);
          if (!(getElement().getContainingFile() instanceof BnfFile file)) throw new IllegalStateException("Element must be in bnf file");
          List<NavigatablePsiElement> implMethods = findRuleImplMethods(javaHelper, psiImplUtilClass, methodName, rule,
                                                                        RuleGraphHelper.getCached(file).getSealedRulesGraph());
          if (!implMethods.isEmpty()) return implMethods;
          List<NavigatablePsiElement> mixinMethods = javaHelper.findClassMethods(mixinClass, JavaHelper.MethodType.INSTANCE, methodName, -1);
          return ContainerUtil.concat(implMethods, mixinMethods);
        }

        @Override
        public ResolveResult @NotNull [] multiResolve(boolean b) {
          return PsiElementResolveResult.createResults(getTargetMethods(getElement().getText()));
        }

        @Override
        public Object @NotNull [] getVariants() {
          List<LookupElement> list = new ArrayList<>();
          for (NavigatablePsiElement element : getTargetMethods("*")) {
            list.add(LookupElementBuilder.createWithIcon((PsiNamedElement)element));
          }
          return ArrayUtil.toObjectArray(list);
        }

        @Override
        public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
          BnfListEntry element = getElement();
          PsiElement id = Objects.requireNonNull(element.getId());
          id.replace(BnfElementFactory.createLeafFromText(element.getProject(), newElementName));
          return element;
        }
      }
    };
  }

  public static @NotNull List<BnfExpression> getArguments(@NotNull BnfExternalExpression expr) {
    List<BnfExpression> expressions = expr.getExpressionList();
    return expressions.subList(1, expressions.size());
  }
}
