/*
 * Copyright 2011-2014 Gregory Shrago
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

import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PairProcessor;
import com.intellij.util.Processor;
import com.intellij.util.SmartList;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.intellij.psi.SyntaxTraverser.psiTraverser;
import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.psi.BnfTypes.BNF_SEQUENCE;

/**
 * @author gregsh
 */
public class GrammarUtil {

  public final static Comparator<BnfNamedElement> NAME_COMPARATOR = new Comparator<BnfNamedElement>() {
    @Override
    public int compare(BnfNamedElement o1, BnfNamedElement o2) {
      return Comparing.compare(o1.getName(), o2.getName());
    }
  };

  public static PsiElement getDummyAwarePrevSibling(PsiElement child) {
    PsiElement prevSibling = child.getPrevSibling();
    while (prevSibling instanceof GeneratedParserUtilBase.DummyBlock) {
      prevSibling = prevSibling.getLastChild();
    }
    if (prevSibling != null) return prevSibling;
    PsiElement parent = child.getParent();
    while (parent instanceof GeneratedParserUtilBase.DummyBlock && parent.getPrevSibling() == null) {
      parent = parent.getParent();
    }
    return parent == null? null : parent.getPrevSibling();
  }

  public static boolean equalsElement(BnfExpression e1, BnfExpression e2) {
    if (e1 == null) return e2 == null;
    if (e2 == null) return false;
    if (ParserGeneratorUtil.getEffectiveType(e1) != ParserGeneratorUtil.getEffectiveType(e2)) return false;
    if (isOneTokenExpression(e1)) {
      return e1.getText().equals(e2.getText());
    }
    else {
      for (PsiElement c1 = e1.getFirstChild(), c2 = e2.getFirstChild(); ;) {
        boolean f1 = c1 == null || c1 instanceof BnfExpression;
        boolean f2 = c2 == null || c2 instanceof BnfExpression;
        if (f1 && f2 && !equalsElement((BnfExpression)c1, (BnfExpression)c2)) return false;
        if (f1 && f2 || !f1) c1 = c1 == null? null : c1.getNextSibling();
        if (f1 && f2 || !f2) c2 = c2 == null? null : c2.getNextSibling();
        if (c1 == null && c2 == null) return true;
      }
    }
  }

  public static boolean isInAttributesReference(@Nullable PsiElement element) {
    return PsiTreeUtil.getParentOfType(element, BnfRule.class, BnfAttrs.class) instanceof BnfAttrs;
  }

  public static boolean isOneTokenExpression(@Nullable BnfExpression e1) {
    return e1 instanceof BnfLiteralExpression || e1 instanceof BnfReferenceOrToken;
  }

  public static boolean isExternalReference(@Nullable PsiElement psiElement) {
    PsiElement parent = psiElement == null? null : psiElement.getParent();
    if (parent instanceof BnfExternalExpression && ((BnfExternalExpression)parent).getExpressionList().get(0) == psiElement) return true;
    if (parent instanceof BnfSequence && parent.getFirstChild() == psiElement) parent = parent.getParent();
    return parent instanceof BnfRule && ParserGeneratorUtil.Rule.isExternal((BnfRule)parent);
  }

  public static List<BnfExpression> getExternalRuleExpressions(@NotNull BnfRule subRule) {
    BnfExpression expression = subRule.getExpression();
    return expression instanceof BnfSequence ? ((BnfSequence)expression).getExpressionList() : Collections.singletonList(expression);
  }

  public static List<String> collectExtraArguments(BnfRule rule, BnfExpression expression) {
    if (!ParserGeneratorUtil.Rule.isMeta(rule) && !ParserGeneratorUtil.Rule.isExternal(rule)) return Collections.emptyList();
    SmartList<String> result = new SmartList<String>();
    for (BnfExternalExpression o : bnfTraverserNoAttrs(expression).filter(BnfExternalExpression.class)) {
      List<BnfExpression> list = o.getExpressionList();
      if (list.size() == 1) {
        String text = "<<"+list.get(0).getText() +">>";
        if (!result.contains(text)) {
          result.add(text);
        }
      }
    }
    return result;
  }

  public static boolean processExpressionNames(BnfRule rule, String funcName, BnfExpression expression, PairProcessor<String, BnfExpression> processor) {
    if (isAtomicExpression(expression)) return true;
    BnfExpression nonTrivialExpression = expression;
    for (BnfExpression e = expression, n = getTrivialNodeChild(e); n != null; e = n, n = getTrivialNodeChild(e)) {
      if (!processor.process(funcName, e)) return false;
      nonTrivialExpression = n;
    }
    final List<BnfExpression> children = getChildExpressions(nonTrivialExpression);
    for (int i = 0, childExpressionsSize = children.size(); i < childExpressionsSize; i++) {
      BnfExpression child = children.get(i);
      if (isAtomicExpression(child)) continue;
      String nextName = ParserGeneratorUtil.isTokenSequence(rule, child)? funcName : getNextName(funcName, i);
      if (!processExpressionNames(rule, nextName, child, processor)) return false;
    }
    return processor.process(funcName, nonTrivialExpression);
  }

  public static boolean processPinnedExpressions(final BnfRule rule, final Processor<BnfExpression> processor) {
    return processPinnedExpressions(rule, new PairProcessor<BnfExpression, PinMatcher>() {
      @Override
      public boolean process(BnfExpression bnfExpression, PinMatcher pinMatcher) {
        return processor.process(bnfExpression);
      }
    });
  }

  public static boolean processPinnedExpressions(final BnfRule rule, final PairProcessor<BnfExpression, PinMatcher> processor) {
    return processExpressionNames(rule, rule.getName(), rule.getExpression(), new PairProcessor<String, BnfExpression>() {
      @Override
      public boolean process(String funcName, BnfExpression expression) {
        if (!(expression instanceof BnfSequence)) return true;
        List<BnfExpression> children = getChildExpressions(expression);
        if (children.size() < 2) return true;
        PinMatcher pinMatcher = new PinMatcher(rule, BNF_SEQUENCE, funcName);
        boolean pinApplied = false;
        for (int i = 0, childExpressionsSize = children.size(); i < childExpressionsSize; i++) {
          BnfExpression child = children.get(i);
          if (!pinApplied && pinMatcher.matches(i, child)) {
            pinApplied = true;
            if (!processor.process(child, pinMatcher)) return false;
          }
        }
        return true;
      }
    });
  }

  public static boolean isAtomicExpression(BnfExpression tree) {
    return tree instanceof BnfReferenceOrToken ||
           tree instanceof BnfLiteralExpression ||
           tree instanceof BnfExternalExpression;
  }

  public static SyntaxTraverser<PsiElement> bnfTraverser(PsiElement root) {
    return psiTraverser().withRoot(root).
      forceDisregardTypes(Conditions.equalTo(GeneratedParserUtilBase.DUMMY_BLOCK)).
      filter(Conditions.instanceOf(BnfCompositeElement.class));
  }

  public static SyntaxTraverser<PsiElement> bnfTraverserNoAttrs(PsiElement root) {
    return bnfTraverser(root).forceIgnore(Conditions.instanceOf(BnfAttrs.class));
  }

  public static String getMethodName(BnfRule rule, PsiElement element) {
    final BnfExpression target = PsiTreeUtil.getParentOfType(element, BnfExpression.class, false);
    String ruleName = rule.getName();
    if (target == null) return ruleName;
    final Ref<String> ref = Ref.create(null);
    processExpressionNames(rule, ruleName, rule.getExpression(), new PairProcessor<String, BnfExpression>() {
      @Override
      public boolean process(String funcName, BnfExpression expression) {
        if (target == expression) {
          ref.set(funcName);
          return false;
        }
        return true;
      }
    });
    return ref.get();
  }

  public static String getIdText(@Nullable PsiElement id) {
    String text = id == null ? "" : id.getText();
    return isIdQuoted(text) ? text.substring(1, text.length() - 1) : text;
  }

  public static boolean isIdQuoted(@Nullable String text) {
    return text != null && text.startsWith("<") && text.endsWith(">");
  }
}
