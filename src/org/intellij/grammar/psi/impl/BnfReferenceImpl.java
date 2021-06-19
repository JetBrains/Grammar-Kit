/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.psi.impl;

import com.intellij.codeInsight.daemon.EmptyResolveMessageProvider;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.RenameableFakePsiElement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

import static org.intellij.grammar.psi.impl.GrammarUtil.bnfTraverserNoAttrs;

/**
 * @author gregsh
 */
public class BnfReferenceImpl<T extends BnfExpression> extends PsiReferenceBase<T> implements EmptyResolveMessageProvider {

  public BnfReferenceImpl(@NotNull T element, TextRange range) {
    super(element, range);
  }

  @Override
  public PsiElement resolve() {
    PsiFile containingFile = myElement.getContainingFile();
    String referenceName = getRangeInElement().substring(myElement.getText());
    boolean isExternal = GrammarUtil.isExternalReference(myElement);
    PsiElement targetRule = containingFile instanceof BnfFile? ((BnfFile)containingFile).getRule(referenceName) : null;
    if (!isExternal) return targetRule;

    BnfRule rule = ParserGeneratorUtil.Rule.of(myElement);

    if (!ParserGeneratorUtil.Rule.isMeta(rule) &&
        !ParserGeneratorUtil.Rule.isExternal(rule)) {
      return targetRule != null ? targetRule : resolveMethod();
    }

    BnfExternalExpression externalExpr = ObjectUtils.tryCast(myElement.getParent(), BnfExternalExpression.class);
    if (externalExpr != null && externalExpr.getArguments().isEmpty()) {
      return new MetaParameter(rule, myElement.getText());
    }

    return targetRule != null ? targetRule : resolveMethod();
  }

  private @Nullable PsiElement resolveMethod() {
    String referenceName = getRangeInElement().substring(myElement.getText());
    PsiElement parent = myElement.getParent();
    int paramCount = parent instanceof BnfSequence ? ((BnfSequence)parent).getExpressionList().size() - 1 :
                     parent instanceof BnfExternalExpression ? ((BnfExternalExpression)parent).getArguments().size() : 0;
    BnfRule rule = Objects.requireNonNull(PsiTreeUtil.getParentOfType(myElement, BnfRule.class));
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

  @Override
  public @NotNull String getUnresolvedMessagePattern() {
    return GrammarUtil.isExternalReference(myElement) ?
           "Unresolved meta rule or method ''{0}''" :
           "Unresolved rule ''{0}''";
  }

  public static class MetaParameter extends RenameableFakePsiElement implements BnfNamedElement {
    private final String myName;

    MetaParameter(BnfRule rule, String name) {
      super(rule);
      myName = name;
    }

    @Override
    public <R> R accept(@NotNull BnfVisitor<R> visitor) {
      return visitor.visitNamedElement(this);
    }

    @Override
    public @NotNull PsiElement getId() {
      return Objects.requireNonNull(getNameIdentifier());
    }

    @Override
    public @NotNull String getName() {
      return myName;
    }

    @Override
    public @Nullable @Nls String getTypeName() {
      return "Meta Rule Parameter";
    }

    @Override
    public @Nullable Icon getIcon() {
      return null;
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
      for (BnfExternalExpression o : bnfTraverserNoAttrs(getParent()).filter(BnfExternalExpression.class)) {
        if (o.getArguments().isEmpty() && Objects.equals(myName, o.getRefElement().getText())) {
          return o.getRefElement();
        }
      }
      return null;
    }

    @Override
    public @NotNull PsiElement getNavigationElement() {
      PsiElement identifier = getNameIdentifier();
      if (identifier != null) return identifier;
      throw new PsiInvalidElementAccessException(this);
    }

    @Override
    public boolean isEquivalentTo(PsiElement another) {
      return another instanceof BnfReferenceImpl.MetaParameter &&
             Objects.equals(myName, ((MetaParameter)another).myName) &&
             getManager().areElementsEquivalent(getParent(), another.getParent());
    }

    @Override
    public @NotNull SearchScope getUseScope() {
      return new LocalSearchScope(getContainingFile());
    }
  }
}
