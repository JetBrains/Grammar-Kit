/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.psi.impl;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ProcessingContext;
import com.intellij.util.SmartList;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author gregsh
 */
public abstract class BnfStringImpl extends BnfExpressionImpl implements BnfStringLiteralExpression, PsiLanguageInjectionHost {

  private static final Key<PsiReference> REF_KEY = Key.create("BNF_REF_KEY");
  private static final Map<ElementPattern<? extends PsiElement>, PsiReferenceProvider> ourProviders;

  static {
    ourProviders = new LinkedHashMap<>();
    new BnfStringRefContributor().registerReferenceProviders(new PsiReferenceRegistrar() {
      @Override
      public <T extends PsiElement> void registerReferenceProvider(@NotNull ElementPattern<T> pattern,
                                                                   @NotNull PsiReferenceProvider provider,
                                                                   double priority) {
        ourProviders.put(pattern, provider);
      }
    });
  }

  public static @NotNull PsiReference createPatternReference(@NotNull BnfStringImpl e) {
    PsiReference ref = e.getUserData(REF_KEY);
    if (ref == null) {
      e.putUserData(REF_KEY, ref = new MyPatternReference(e));
    }
    return ref;
  }

  public static @NotNull PsiReference createRuleReference(@NotNull BnfStringImpl e) {
    PsiReference ref = e.getUserData(REF_KEY);
    if (ref == null) {
      e.putUserData(REF_KEY, ref = new MyRuleReference(e));
    }
    return ref;
  }

  public BnfStringImpl(IElementType type) {
    super(type);
  }

  @Override
  public PsiElement getNumber() {
    return null;
  }

  public PsiReference @NotNull [] getReferences() {
    // performance: do not run injectors
    // return PsiReferenceService.getService().getContributedReferences(this);
    List<PsiReference> result = new SmartList<>();
    for (Map.Entry<ElementPattern<? extends PsiElement>, PsiReferenceProvider> e : ourProviders.entrySet()) {
      ProcessingContext context = new ProcessingContext();
      if (e.getKey().accepts(this, context)) {
        result.addAll(Arrays.asList(e.getValue().getReferencesByElement(this, context)));
      }
    }
    return result.isEmpty() ? PsiReference.EMPTY_ARRAY : result.toArray(PsiReference.EMPTY_ARRAY);
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
  public BnfStringImpl updateText(@NotNull String text) {
    BnfExpression expression = BnfElementFactory.createExpressionFromText(getProject(), text);
    assert expression instanceof BnfStringImpl : text + "-->" + expression;
    return (BnfStringImpl)this.replace(expression);
  }

  @Override
  public @NotNull LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
    return new BnfStringLiteralEscaper(this);
  }

  private static @Nullable Pattern getPattern(BnfLiteralExpression expression) {
    return ParserGeneratorUtil.compilePattern(GrammarUtil.unquote(expression.getText()));
  }

  private static class MyRuleReference extends BnfReferenceImpl<BnfStringImpl> {
    MyRuleReference(BnfStringImpl element) {
      super(element, null);
    }

    @Override
    public @NotNull TextRange getRangeInElement() {
      return BnfStringManipulator.getStringTokenRange(getElement());
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
      BnfStringImpl element = getElement();
      PsiElement string = element.getString();
      char quote = string.getText().charAt(0);
      return string.replace(BnfElementFactory.createLeafFromText(element.getProject(), quote + newElementName + quote));
    }
  }

  private static class MyPatternReference extends PsiPolyVariantReferenceBase<BnfStringImpl> {
    private static final ResolveCache.PolyVariantResolver<MyPatternReference> RESOLVER =
      (reference, b) -> reference.multiResolveInner();

    MyPatternReference(BnfStringImpl element) {
      super(element);
    }

    @Override
    public @NotNull TextRange getRangeInElement() {
      return BnfStringManipulator.getStringTokenRange(getElement());
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
      return matchesElement(getElement(), element) && super.isReferenceTo(element);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean b) {
      return ResolveCache.getInstance(getElement().getProject()).resolveWithCaching(this, RESOLVER, false, b);
    }

    public ResolveResult @NotNull [] multiResolveInner() {
      Pattern pattern = getPattern(getElement());
      if (pattern == null) return ResolveResult.EMPTY_ARRAY;
      List<PsiElement> result = new ArrayList<>();

      BnfAttr thisAttr = Objects.requireNonNull(PsiTreeUtil.getParentOfType(getElement(), BnfAttr.class));
      BnfAttrs thisAttrs = Objects.requireNonNull(PsiTreeUtil.getParentOfType(thisAttr, BnfAttrs.class));
      BnfRule thisRule = PsiTreeUtil.getParentOfType(thisAttrs, BnfRule.class);

      String thisAttrName = thisAttr.getName();
      KnownAttribute<?> knownAttribute = KnownAttribute.getAttribute(thisAttrName);

      // collect priority patterns
      List<Pattern> otherPatterns = new SmartList<>();
      if (knownAttribute != null && !(knownAttribute.getDefaultValue() instanceof KnownAttribute.ListValue)) {
        for (BnfAttr attr : thisAttrs.getAttrList()) {
          if (attr == thisAttr) break;
          if (thisAttrName.equals(attr.getName())) {
            BnfAttrPattern attrPattern = attr.getAttrPattern();
            BnfLiteralExpression expression = attrPattern != null ? attrPattern.getLiteralExpression() : null;
            Pattern p = expression == null ? null : getPattern(expression);
            if (p != null) otherPatterns.add(p);
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
      if (knownAttribute == KnownAttribute.PIN) {
        Set<String> visited = new HashSet<>();
        for (Object o : thisRule != null ? rules : new ArrayList<>(result)) {
          BnfRule rule = (BnfRule)o;
          GrammarUtil.processExpressionNames(rule, ParserGeneratorUtil.getFuncName(rule), rule.getExpression(), (funcName, expression) -> {
            if (!(expression instanceof BnfSequence)) return true;
            if (!visited.add(funcName)) return true;
            PsiElement firstNotTrivial = ParserGeneratorUtil.Rule.firstNotTrivial(ParserGeneratorUtil.Rule.of(expression));
            if (firstNotTrivial == expression) return true;
            if (pattern.matcher(funcName).matches()) {
              result.add(new MyFakePsiElement(funcName, expression));
            }
            return true;
          });
        }
      }
      return PsiElementResolveResult.createResults(result);
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
      // do not rename pattern
      return myElement;
    }

    @Override
    public Object @NotNull [] getVariants() {
      return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }
  }

  public static boolean matchesElement(@Nullable BnfLiteralExpression e1, @NotNull PsiElement e2) {
    if (e1 == null) return false;
    if (e2 instanceof PsiNamedElement) {
      String name = ((PsiNamedElement)e2).getName();
      Pattern pattern = getPattern(e1);
      return name != null && pattern != null && pattern.matcher(name).matches();
    }
    return true;
  }

  private static class MyFakePsiElement extends FakePsiElement implements BnfComposite {
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

    @Override
    public @NotNull PsiElement getNavigationElement() {
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

    @Override
    public boolean isEquivalentTo(PsiElement another) {
      return another instanceof MyFakePsiElement &&
             Objects.equals(myFuncName, ((MyFakePsiElement)another).myFuncName) &&
             myExpression.getManager().areElementsEquivalent(myExpression, ((MyFakePsiElement)another).myExpression);
    }
  }
}
