/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.psi.impl;

import com.intellij.patterns.PatternCondition;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfAttrPattern;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static com.intellij.patterns.PlatformPatterns.*;
import static org.intellij.grammar.KnownAttribute.*;

/**
 * @author gregsh
 */
public class BnfStringRefContributor extends PsiReferenceContributor {

  private static final Set<KnownAttribute<?>> RULE_ATTRIBUTES = ContainerUtil.set(EXTENDS, IMPLEMENTS, RECOVER_WHILE, NAME);

  private static final Set<KnownAttribute<?>> JAVA_CLASS_ATTRIBUTES = ContainerUtil.set(EXTENDS, IMPLEMENTS, MIXIN);

  @Override
  public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
    registrar.registerReferenceProvider(
      psiElement(BnfStringImpl.class).withParent(psiElement(BnfAttrPattern.class)), new PsiReferenceProvider() {

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
          return new PsiReference[]{BnfStringImpl.createPatternReference((BnfStringImpl)element)};
        }
      }
    );

    registrar.registerReferenceProvider(
      psiElement(BnfStringImpl.class).withParent(psiElement(BnfAttr.class).withName(string().with(oneOf(RULE_ATTRIBUTES)))),
      new PsiReferenceProvider() {

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
          return new PsiReference[]{BnfStringImpl.createRuleReference((BnfStringImpl)element)};
        }
      }
    );

    registrar.registerReferenceProvider(
      psiElement(BnfStringImpl.class).withAncestor(3, psiElement(BnfAttr.class).withName(
        or(string().endsWith("Class"), string().endsWith("Package"), string().endsWith("TypeFactory"),
           string().with(oneOf(JAVA_CLASS_ATTRIBUTES))))),
      new PsiReferenceProvider() {

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
          return JavaHelper.getJavaHelper(element).getClassReferences(element, context);
        }
      });
  }

  private static PatternCondition<String> oneOf(Set<KnownAttribute<?>> attributes) {
    return new PatternCondition<>("oneOf") {
      @Override
      public boolean accepts(@NotNull String s, ProcessingContext context) {
        return attributes.contains(getCompatibleAttribute(s));
      }
    };
  }
}
