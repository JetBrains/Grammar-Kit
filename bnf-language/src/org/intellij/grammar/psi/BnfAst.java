/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import it.unimi.dsi.fastutil.Hash;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.intellij.grammar.psi.BnfTypes.BNF_SEQUENCE;

public final class BnfAst {

  private BnfAst() {
  }

  private static final Hash.Strategy<PsiElement> TEXT_STRATEGY = new Hash.Strategy<>() {
    @Override
    public int hashCode(PsiElement e) {
      return e == null ? 0 : e.getText().hashCode();
    }

    @Override
    public boolean equals(PsiElement e1, PsiElement e2) {
      return e1 == null ? e2 == null : e2 != null && Objects.equals(e1.getText(), e2.getText());
    }
  };

  public static <T extends PsiElement> @NotNull Hash.Strategy<T> textStrategy() {
    return (Hash.Strategy<T>)TEXT_STRATEGY;
  }

  public static @NotNull BnfExpression getNonTrivialNode(@NotNull BnfExpression initialNode) {
    BnfExpression nonTrivialNode = initialNode;
    for (BnfExpression e = initialNode, n = getTrivialNodeChild(e); n != null; e = n, n = getTrivialNodeChild(e)) {
      nonTrivialNode = n;
    }
    return nonTrivialNode;
  }

  public static @Nullable BnfExpression getTrivialNodeChild(@NotNull PsiElement element) {
    PsiElement child = null;
    if (element instanceof BnfParenthesized) {
      BnfExpression e = ((BnfParenthesized)element).getExpression();
      if (element instanceof BnfParenExpression) {
        child = e;
      }
      else {
        BnfExpression c = e;
        while (c instanceof BnfParenthesized) {
          c = ((BnfParenthesized)c).getExpression();
        }
        if (c.getFirstChild() == null) {
          child = e;
        }
      }
    }
    else if (element.getFirstChild() == element.getLastChild() && element instanceof BnfExpression) {
      child = element.getFirstChild();
    }
    return child instanceof BnfExpression && !(child instanceof BnfLiteralExpression || child instanceof BnfReferenceOrToken) ?
           (BnfExpression)child : null;
  }

  public static @NotNull IElementType getEffectiveType(@NotNull PsiElement tree) {
    if (tree instanceof BnfParenOptExpression) {
      return BnfTypes.BNF_OP_OPT;
    }
    else if (tree instanceof BnfQuantified) {
      BnfQuantifier quantifier = ((BnfQuantified)tree).getQuantifier();
      return PsiTreeUtil.getDeepestFirst(quantifier).getNode().getElementType();
    }
    else if (tree instanceof BnfPredicate) {
      return ((BnfPredicate)tree).getPredicateSign().getFirstChild().getNode().getElementType();
    }
    else if (tree instanceof BnfStringLiteralExpression) {
      return BnfTypes.BNF_STRING;
    }
    else if (tree instanceof BnfLiteralExpression) {
      return tree.getFirstChild().getNode().getElementType();
    }
    else if (tree instanceof BnfParenExpression) {
      return BNF_SEQUENCE;
    }
    else {
      return tree.getNode().getElementType();
    }
  }

  public static @Nullable Pattern compilePattern(@NotNull String text) {
    try {
      return Pattern.compile(text);
    }
    catch (PatternSyntaxException e) {
      return null;
    }
  }

  public static @NotNull List<BnfExpression> getChildExpressions(@Nullable BnfExpression node) {
    return PsiTreeUtil.getChildrenOfTypeAsList(node, BnfExpression.class);
  }

  public static @NotNull Map<String, String> getTokenNameToTextMap(@NotNull BnfFile file) {
    return CachedValuesManager.getCachedValue(file, () -> new CachedValueProvider.Result<>(computeTokens(file).asMap(), file));
  }

  public static @NotNull Map<String, String> getTokenTextToNameMap(@NotNull BnfFile file) {
    return CachedValuesManager.getCachedValue(file, () -> new CachedValueProvider.Result<>(computeTokens(file).asInverseMap(), file));
  }

  public static @NotNull KnownAttribute.ListValue computeTokens(@NotNull BnfFile file) {
    return BnfAttributes.getRootAttribute(file, KnownAttribute.TOKENS);
  }

  public static @Nullable Collection<String> getTokenNames(@NotNull BnfFile file, @NotNull List<BnfExpression> expressions) {
    return getTokenNames(file, expressions, -1);
  }

  // null when some expression is not a token or total tokens count is less than or equals threshold
  public static @Nullable Collection<String> getTokenNames(@NotNull BnfFile file, @NotNull List<BnfExpression> expressions, int threshold) {
    Set<String> tokens = new LinkedHashSet<>();
    for (BnfExpression expression : expressions) {
      String token = getTokenName(file, expression);
      if (token == null) {
        return null;
      }
      else {
        tokens.add(token);
      }
    }
    return tokens.size() > threshold ? tokens : null;
  }

  private static @Nullable String getTokenName(@NotNull BnfFile file, @NotNull BnfExpression expression) {
    String text = expression.getText();
    if (expression instanceof BnfStringLiteralExpression) {
      return getTokenTextToNameMap(file).get(GrammarUtil.unquote(text));
    }
    else if (expression instanceof BnfReferenceOrToken) {
      return file.getRule(text) == null ? text : null;
    }
    else {
      return null;
    }
  }

  public static boolean isTokenSequence(@NotNull BnfRule rule, @Nullable BnfExpression node) {
    if (node == null || !isDefaultConsumeMethod(rule)) return false;
    if (getEffectiveType(node) != BNF_SEQUENCE) return false;
    BnfFile file = (BnfFile)rule.getContainingFile();
    return getTokenNames(file, getChildExpressions(node)) != null;
  }

  private static boolean isDefaultConsumeMethod(@NotNull BnfRule rule) {
    String value = BnfAttributes.getAttribute(rule, KnownAttribute.CONSUME_TOKEN_METHOD);
    return value == null || value.isEmpty()
           || "DEFAULT".equalsIgnoreCase(value)
           || "consumeToken".equals(value);
  }
}
