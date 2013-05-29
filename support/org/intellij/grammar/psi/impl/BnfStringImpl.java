/*
 * Copyright 2011-2013 Gregory Shrago
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
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.*;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author gregsh
 */
public abstract class BnfStringImpl extends BnfExpressionImpl implements BnfStringLiteralExpression, PsiLanguageInjectionHost {
  public BnfStringImpl(ASTNode node) {
    super(node);
  }

  @Override
  public PsiElement getNumber() {
    return null;
  }

  @Override
  public PsiReference getReference() {
    PsiElement parent = getParent();
    if (parent instanceof BnfAttrPattern) {
      KnownAttribute attribute = KnownAttribute.getAttribute(((BnfAttr)parent.getParent()).getName());
      return attribute != KnownAttribute.METHOD_RENAMES ? getAttrPatternReference() : null;
    }
    else if (parent instanceof BnfAttr) {
      PsiReference[] refs = getAttrValueReference();
      return refs.length < 2? ArrayUtil.getFirstElement(refs) : new MyMultiReference(refs, this);
    }
    else return null;
  }

  @NotNull
  public PsiReference[] getReferences() {
    PsiElement parent = getParent();
    if (parent instanceof BnfAttrPattern) {
      KnownAttribute attribute = KnownAttribute.getAttribute(((BnfAttr)parent.getParent()).getName());
      if (attribute != KnownAttribute.METHOD_RENAMES) {
        return new PsiReference[] { getAttrPatternReference() };
      }
      else {
        return PsiReference.EMPTY_ARRAY;
      }
    }
    else if (parent instanceof BnfAttr) {
      return getAttrValueReference();
    }
    return PsiReference.EMPTY_ARRAY;
  }

  @NotNull
  private PsiReference[] getAttrValueReference() {
    KnownAttribute attribute = KnownAttribute.getAttribute(((BnfAttr)getParent()).getName());
    if (attribute == null) return PsiReference.EMPTY_ARRAY;
    boolean addJavaRefs = attribute.getName().endsWith("Class") || attribute.getName().endsWith("Package") ||
                       (attribute == KnownAttribute.EXTENDS || attribute == KnownAttribute.IMPLEMENTS ||
                        attribute == KnownAttribute.MIXIN);
    boolean addBnfRef = attribute == KnownAttribute.EXTENDS || attribute == KnownAttribute.IMPLEMENTS ||
                        attribute == KnownAttribute.RECOVER_UNTIL || attribute == KnownAttribute.NAME;

    BnfReferenceImpl<BnfStringLiteralExpression> bnfReference = null;
    if (addBnfRef) {
      TextRange range = BnfStringManipulator.getStringTokenRange(this);
      bnfReference = new BnfReferenceImpl<BnfStringLiteralExpression>(this, range) {
        @Override
        public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
          PsiElement string = getString();
          char quote = string.getText().charAt(0);
          return string.replace(BnfElementFactory.createLeafFromText(getProject(), quote + newElementName + quote));
        }
      };
    }
    if (addJavaRefs) {
      PsiReferenceProvider provider = JavaHelper.getJavaHelper(getProject()).getClassReferenceProvider();
      PsiReference[] javaRefs = provider == null ? PsiReference.EMPTY_ARRAY : provider.getReferencesByElement(this, new ProcessingContext());
      return addBnfRef? ArrayUtil.mergeArrays(new PsiReference[]{bnfReference}, javaRefs, PsiReference.ARRAY_FACTORY) : javaRefs;
    }
    else if (addBnfRef) {
      return new PsiReference[] { bnfReference };
    }
    return PsiReference.EMPTY_ARRAY;
  }

  private PsiReference getAttrPatternReference() {
    return new MyPatternReference(this);
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

  @Override
  public String toString() {
    return super.toString() + ": " + getText();
  }

  private static class MyMultiReference extends PsiMultiReference {
    MyMultiReference(PsiReference[] psiReferences, BnfStringLiteralExpression element) {
      super(psiReferences, element);
    }

    @Override
    public TextRange getRangeInElement() {
      PsiReference[] references = getReferences();
      TextRange result = references[0].getRangeInElement();
      for (PsiReference reference : references) {
        result = result.union(reference.getRangeInElement());
      }
      return result;
    }
  }

  private static class MyPatternReference extends PsiPolyVariantReferenceBase<BnfStringImpl> {
    private static final ResolveCache.PolyVariantResolver<MyPatternReference> RESOLVER = new ResolveCache.PolyVariantResolver<MyPatternReference>() {
      @NotNull
      @Override
      public ResolveResult[] resolve(@NotNull MyPatternReference reference, boolean b) {
        return reference.multiResolveInner();
      }
    };

    MyPatternReference(BnfStringImpl element) {
      super(element, BnfStringManipulator.getStringTokenRange(element));
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
      return ResolveCache.getInstance(getElement().getProject()).resolveWithCaching(this, RESOLVER, false, b);
    }

    @NotNull
    public ResolveResult[] multiResolveInner() {
      final Pattern pattern = ParserGeneratorUtil.compilePattern(getCanonicalText());
      if (pattern == null) return ResolveResult.EMPTY_ARRAY;
      final ArrayList<PsiElement> result = new ArrayList<PsiElement>();

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
            ContainerUtil.addIfNotNull(ParserGeneratorUtil.compilePattern(StringUtil.stripQuotesAroundValue(expression.getText())), otherPatterns);
          }
        }
      }

      BnfFile file = (BnfFile) thisAttrs.getContainingFile();
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
          BnfRule rule = (BnfRule) e;
          GrammarUtil.processExpressionNames(rule, rule.getName(), rule.getExpression(), processor);
        }
      }
      return PsiElementResolveResult.createResults(result);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
      return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }

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
  }
}
