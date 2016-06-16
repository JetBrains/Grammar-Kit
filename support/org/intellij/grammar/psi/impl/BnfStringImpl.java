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

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.*;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author gregsh
 */
public abstract class BnfStringImpl extends BnfExpressionImpl implements BnfStringLiteralExpression, PsiLanguageInjectionHost {

  private static final Key<PsiReference> REF_KEY = Key.create("BNF_REF_KEY");
  private static final Map<ElementPattern<? extends PsiElement>, PsiReferenceProvider> ourProviders;

  static {
    ourProviders = ContainerUtil.newLinkedHashMap();
    new BnfStringRefContributor().registerReferenceProviders(new PsiReferenceRegistrar() {
      @Override
      public <T extends PsiElement> void registerReferenceProvider(@NotNull ElementPattern<T> pattern,
                                                                   @NotNull PsiReferenceProvider provider,
                                                                   double priority) {
        ourProviders.put(pattern, provider);
      }
    });
  }

  @NotNull
  public static PsiReference createPatternReference(@NotNull BnfStringImpl e) {
    PsiReference ref = e.getUserData(REF_KEY);
    if (ref == null) {
      e.putUserData(REF_KEY, ref = new MyPatternReference(e));
    }
    return ref;
  }

  @NotNull
  public static PsiReference createRuleReference(@NotNull BnfStringImpl e) {
    PsiReference ref = e.getUserData(REF_KEY);
    if (ref == null) {
      e.putUserData(REF_KEY, ref = new MyRuleReference(e));
    }
    return ref;
  }

  public BnfStringImpl(ASTNode node) {
    super(node);
  }

  @Override
  public PsiElement getNumber() {
    return null;
  }

  @NotNull
  public PsiReference[] getReferences() {
    // performance: do not run injectors
    // return PsiReferenceService.getService().getContributedReferences(this);
    List<PsiReference> result = ContainerUtil.newSmartList();
    for (Map.Entry<ElementPattern<? extends PsiElement>, PsiReferenceProvider> e : ourProviders.entrySet()) {
      ProcessingContext context = new ProcessingContext();
      if (e.getKey().accepts(this, context)) {
        result.addAll(Arrays.asList(e.getValue().getReferencesByElement(this, context)));
      }
    }
    return result.isEmpty() ? PsiReference.EMPTY_ARRAY : ContainerUtil.toArray(result, new PsiReference[result.size()]);
  }

  @Override
  public PsiReference getReference() {
    return ArrayUtil.getFirstElement(getReferences());
  }

  @Override
  public boolean isValidHost() {
    return true;
  }

  @Override
  public BnfStringImpl updateText(@NotNull final String text) {
    final BnfExpression expression = BnfElementFactory.createExpressionFromText(getProject(), text);
    assert expression instanceof BnfStringImpl : text + "-->" + expression;
    return (BnfStringImpl)this.replace(expression);
  }

  @NotNull
  @Override
  public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
    return new BnfStringLiteralEscaper(this);
  }

  @Nullable
  private static Pattern getPattern(BnfLiteralExpression expression) {
    return ParserGeneratorUtil.compilePattern(StringUtil.stripQuotesAroundValue(expression.getText()));
  }

  private static class MyRuleReference extends BnfReferenceImpl<BnfStringImpl> {
    MyRuleReference(BnfStringImpl element) {
      super(element, null);
    }

    @Override
    public TextRange getRangeInElement() {
      return BnfStringManipulator.getStringTokenRange(getElement());
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
      BnfStringImpl element = getElement();
      PsiElement string = element.getString();
      char quote = string.getText().charAt(0);
      return string.replace(BnfElementFactory.createLeafFromText(element.getProject(), quote + newElementName + quote));
    }
  }

  private static class MyPatternReference extends PsiPolyVariantReferenceBase<BnfStringImpl> {
    private static final ResolveCache.PolyVariantResolver<MyPatternReference> RESOLVER =
      new ResolveCache.PolyVariantResolver<MyPatternReference>() {
        @NotNull
        @Override
        public ResolveResult[] resolve(@NotNull MyPatternReference reference, boolean b) {
          return reference.multiResolveInner();
        }
      };

    MyPatternReference(BnfStringImpl element) {
      super(element);
    }

    @Override
    public TextRange getRangeInElement() {
      return BnfStringManipulator.getStringTokenRange(getElement());
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
      return matchesElement(getElement(), element) && super.isReferenceTo(element);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
      return ResolveCache.getInstance(getElement().getProject()).resolveWithCaching(this, RESOLVER, false, b);
    }

    @NotNull
    public ResolveResult[] multiResolveInner() {
      final Pattern pattern = getPattern(getElement());
      if (pattern == null) return ResolveResult.EMPTY_ARRAY;
      final List<PsiElement> result = ContainerUtil.newArrayList();

      BnfAttr thisAttr = ObjectUtils.assertNotNull(PsiTreeUtil.getParentOfType(getElement(), BnfAttr.class));
      BnfAttrs thisAttrs = ObjectUtils.assertNotNull(PsiTreeUtil.getParentOfType(thisAttr, BnfAttrs.class));
      BnfRule thisRule = PsiTreeUtil.getParentOfType(thisAttrs, BnfRule.class);

      String thisAttrName = thisAttr.getName();
      // collect priority patterns
      List<Pattern> otherPatterns = new SmartList<Pattern>();
      for (BnfAttr attr : thisAttrs.getAttrList()) {
        if (attr == thisAttr) break;
        if (thisAttrName.equals(attr.getName())) {
          BnfAttrPattern attrPattern = attr.getAttrPattern();
          BnfLiteralExpression expression = attrPattern != null ? attrPattern.getLiteralExpression() : null;
          if (expression != null) {
            ContainerUtil.addIfNotNull(getPattern(expression), otherPatterns);
          }
        }
      }

      BnfFile file = (BnfFile)thisAttrs.getContainingFile();
      int thisOffset = (thisRule != null ? thisRule : thisAttrs).getTextRange().getStartOffset();
      List<BnfRule> rules = thisRule != null ? Collections.singletonList(thisRule) : file.getRules();
      main:
      for (BnfRule rule : rules) {
        if (rule.getTextRange().getStartOffset() < thisOffset) continue;
        String ruleName = rule.getName();
        if (pattern.matcher(ruleName).matches()) {
          for (Pattern otherPattern : otherPatterns) {
            if (otherPattern.matcher(ruleName).matches()) continue main;
          }
          result.add(rule);
        }
      }
      if (KnownAttribute.getAttribute(thisAttrName) == KnownAttribute.PIN) {
        PairProcessor<String, BnfExpression> processor = new PairProcessor<String, BnfExpression>() {
          @Override
          public boolean process(String funcName, BnfExpression expression) {
            if (!(expression instanceof BnfSequence)) return true;
            PsiElement firstNotTrivial = ParserGeneratorUtil.Rule.firstNotTrivial(ParserGeneratorUtil.Rule.of(expression));
            if (firstNotTrivial == expression) return true;
            if (pattern.matcher(funcName).matches()) {
              result.add(new MyFakePsiElement(funcName, expression));
            }
            return true;
          }
        };
        for (Object e : result.toArray()) {
          BnfRule rule = (BnfRule)e;
          GrammarUtil.processExpressionNames(rule, ParserGeneratorUtil.getFuncName(rule), rule.getExpression(), processor);
        }
      }
      return PsiElementResolveResult.createResults(result);
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
      // do not rename pattern
      return myElement;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
      return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }
  }

  public static boolean matchesElement(@Nullable BnfLiteralExpression e1, @NotNull PsiElement e2) {
    if (e1 == null) return false;
    if (e2 instanceof PsiNamedElement) {
      String name = ((PsiNamedElement)e2).getName();
      Pattern pattern = getPattern(e1);
      if (name == null || pattern == null || !pattern.matcher(name).matches()) {
        return false;
      }
    }
    return true;
  }

  private static class MyFakePsiElement extends FakePsiElement implements BnfCompositeElement {
    private final String myFuncName;
    private final BnfExpression myExpression;

    MyFakePsiElement(String funcName, BnfExpression expression) {
      myFuncName = funcName;
      myExpression = expression;
    }

    @Override
    public String getName() {
      return myFuncName;
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
      return myExpression;
    }

    @Override
    public TextRange getTextRange() {
      return myExpression.getTextRange();
    }

    @Override
    public PsiElement getParent() {
      return myExpression.getParent();
    }

    @Override
    public <R> R accept(@NotNull BnfVisitor<R> visitor) {
      return null;
    }
  }
}
