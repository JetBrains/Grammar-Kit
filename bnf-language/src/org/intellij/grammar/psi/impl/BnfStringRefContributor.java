/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.psi.impl;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PatternCondition;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.ProcessingContext;
import org.intellij.grammar.BnfPaths;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.java.JavaHelperFactory;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfAttrPattern;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static com.intellij.patterns.PlatformPatterns.*;
import static org.intellij.grammar.KnownAttribute.*;

/**
 * @author gregsh
 */
class BnfStringRefContributor extends PsiReferenceContributor {

  private static final Set<KnownAttribute<?>> RULE_ATTRIBUTES = Set.of(EXTENDS, IMPLEMENTS, RECOVER_WHILE, NAME);

  private static final Set<KnownAttribute<?>> JAVA_REFERENCE_ATTRIBUTES = Set.of(
    EXTENDS, IMPLEMENTS, MIXIN,
    PARSER_CLASS, PARSER_UTIL_CLASS,
    PSI_IMPL_UTIL_CLASS, PSI_TREE_UTIL_CLASS,
    ELEMENT_TYPE_CLASS, ELEMENT_TYPE_HOLDER_CLASS,
    ELEMENT_TYPE_CONVERTER_FACTORY_CLASS,
    SYNTAX_ELEMENT_TYPE_HOLDER_CLASS,
    TOKEN_TYPE_CLASS, STUB_CLASS,
    PSI_PACKAGE, PSI_IMPL_PACKAGE,
    ELEMENT_TYPE_FACTORY, SYNTAX_ELEMENT_TYPE_FACTORY, TOKEN_TYPE_FACTORY);

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
        string().with(oneOf(JAVA_REFERENCE_ATTRIBUTES)))),
      new PsiReferenceProvider() {

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
          return JavaHelperFactory.getInstance(element.getProject()).getInstance(element).getClassReferences(element, context);
        }
      });

    registerPathReferenceProvider(registrar, Set.copyOf(BnfPaths.INPUTS), false);
    registerPathReferenceProvider(registrar, Set.copyOf(BnfPaths.OUTPUTS), true);
  }

  private static void registerPathReferenceProvider(@NotNull PsiReferenceRegistrar registrar,
                                                    @NotNull Set<KnownAttribute<?>> attributes,
                                                    boolean soft) {
    registrar.registerReferenceProvider(
      psiElement(BnfStringImpl.class).withAncestor(3, psiElement(BnfAttr.class).withName(
        string().with(oneOf(attributes)))),
      new PsiReferenceProvider() {

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
          TextRange valueRange = ElementManipulators.getValueTextRange(element);
          String text = valueRange.substring(element.getText());
          boolean caseSensitive = element.getContainingFile().getViewProvider().getVirtualFile().getFileSystem().isCaseSensitive();
          FileReferenceSet set = new FileReferenceSet(text, element, valueRange.getStartOffset(), null, caseSensitive, false) {
            @Override
            protected Condition<PsiFileSystemItem> getReferenceCompletionFilter() {
              return DIRECTORY_FILTER;
            }

            @Override
            protected boolean isSoft() {
              return soft;
            }
          };
          return set.getAllReferences();
        }
      });
  }

  private static PatternCondition<String> oneOf(Set<KnownAttribute<?>> attributes) {
    return new PatternCondition<>("oneOf") {
      @Override
      public boolean accepts(@NotNull String s, ProcessingContext context) {
        KnownAttribute<?> attribute = getCompatibleAttribute(s);
        return attribute != null && attributes.contains(attribute);
      }
    };
  }
}
