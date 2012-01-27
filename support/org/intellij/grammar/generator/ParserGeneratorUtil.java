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
package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.intellij.grammar.psi.BnfTypes.BNF_SEQUENCE;

/**
 * @author gregory
 *         Date: 16.07.11 10:41
 */
public class ParserGeneratorUtil {
  private static final Object NULL = new Object();
  private static final PsiElement NULL_ATTR = new LeafPsiElement(TokenType.ERROR_ELEMENT, "");

  public static <T> T getRootAttribute(BnfFile treeRoot, String attrName, @Nullable T def) {
    return getRootAttribute(treeRoot, attrName, def, null);
  }

  public static <T> T getRootAttribute(BnfFile treeRoot, String attrName, @Nullable T def, @Nullable String match) {
    return getAttributeInner(ContainerUtil.getFirstItem(treeRoot.getAttributes()), attrName, def, match);
  }

  public static <T> T getAttribute(BnfRule rule, String attrName, @Nullable T def) {
    return getAttribute(rule, attrName, def, rule.getName());
  }

  public static <T> T getAttribute(BnfRule rule, String attrName, @Nullable T def, String match) {
    return getAttributeInner(rule, attrName, def, match);
  }

  private static <T> T getAttributeInner(PsiElement node, String attrName, @Nullable T def, @Nullable String match) {
    BnfCompositeElement parent = PsiTreeUtil.getNonStrictParentOfType(node, BnfRule.class, BnfAttrs.class);
    PsiElement attrValue =
      findAttributeValueNode(parent instanceof BnfRule ? ((BnfRule)parent).getAttrs() : (BnfAttrs)parent, attrName, match);

    Object attrVal = getLiteralValue(attrValue);
    if (attrVal != null) return attrVal == NULL ? def : (T)attrVal;
    if (parent == null) return def;
    if (parent instanceof BnfAttrs && parent.getParent() instanceof BnfRule) parent = (BnfRule)parent.getParent();
    for (PsiElement child = GrammarUtil.getDummyAwarePrevSibling(parent);
         child != null;
         child = GrammarUtil.getDummyAwarePrevSibling(child)) {
      if (!(child instanceof BnfAttrs)) continue;
      attrValue = findAttributeValueNode((BnfAttrs)child, attrName, match);

      attrVal = getLiteralValue(attrValue);
      if (attrVal != null) return attrVal == NULL ? def : (T)attrVal;
    }
    return def;
  }

  public static String getAttributeName(PsiElement node, String value) {
    BnfCompositeElement parent = PsiTreeUtil.getNonStrictParentOfType(node, BnfRule.class, BnfAttrs.class);
    BnfAttr attr = findAttributeByValue(parent instanceof BnfRule ? ((BnfRule)parent).getAttrs() : (BnfAttrs)parent, value);
    if (attr != null) return attr.getId().getText();
    if (parent == null) return null;
    if (parent instanceof BnfAttrs && parent.getParent() instanceof BnfRule) parent = (BnfRule)parent.getParent();
    for (PsiElement child = GrammarUtil.getDummyAwarePrevSibling(parent);
         child != null;
         child = GrammarUtil.getDummyAwarePrevSibling(child)) {
      if (!(child instanceof BnfAttrs)) continue;
      attr = findAttributeByValue(child, value);
      if (attr != null) return attr.getId().getText();
    }
    return null;
  }

  @Nullable
  public static PsiElement findAttributeValueNode(BnfAttrs attrs, String attrName, String ruleName) {
    if (attrs == null) return null;
    BnfAttrValue noPattern = null;
    for (BnfAttr tree : attrs.getAttrList()) {
      if (attrName.equals(tree.getId().getText())) {
        BnfAttrPattern attrPattern = tree.getAttrPattern();
        BnfAttrValue attrValue = tree.getAttrValue();
        if (attrPattern == null) {
          noPattern = attrValue;
          if (ruleName == null) break;
        }
        else if (ruleName != null) {
          BnfLiteralExpression pattern = attrPattern.getLiteralExpression();
          try {
            if (pattern != null && Pattern.matches(StringUtil.stripQuotesAroundValue(pattern.getText()), ruleName)) {
              return attrValue;
            }
          }
          catch (PatternSyntaxException e) {
            // do nothing
          }
        }
      }
    }
    // do not pin nested sequences
    return ruleName != null && Pattern.matches(".*(_\\d+)+", ruleName)? noPattern != null ? NULL_ATTR : null : noPattern;
  }

  @Nullable
  public static BnfAttr findAttributeByValue(PsiElement attrs, String value) {
    if (!(attrs instanceof BnfAttrs)) return null;
    for (BnfAttr tree : ((BnfAttrs)attrs).getAttrList()) {
      if (value.equals(getLiteralValue(tree.getAttrValue()))) {
        return tree;
      }
    }
    return null;
  }

  public static Object getLiteralValue(PsiElement child) {
    if (child == null) return null;
    if (child == NULL_ATTR) return NULL;
    PsiElement literal = PsiTreeUtil.getDeepestFirst(child);
    String text = child.getText();
    IElementType elementType = literal.getNode().getElementType();
    if (elementType == BnfTypes.BNF_STRING) return StringUtil.stripQuotesAroundValue(text);
    if (elementType == BnfTypes.BNF_NUMBER) return Integer.parseInt(text);
    if (elementType == BnfTypes.BNF_ID) {
      if (text.equals("true") || text.equals("false")) return Boolean.parseBoolean(text);
      if (text.equals("null")) return NULL;
      Object attribute = getAttributeInner(child.getParent(), text, null, null);
      if (attribute == null) {
        // todo look for rule
      }
      return attribute;
    }
    return null;
  }

  public static boolean isTrivialNode(PsiElement node) {
    PsiElement child = null;
    if (node instanceof BnfParenExpression) {
      child = ((BnfParenExpression)node).getExpression();
    }
    if (node.getFirstChild() == node.getLastChild() &&
        (node instanceof BnfChoice || node instanceof BnfSequence || node instanceof BnfExpression)) {
      child = node.getFirstChild();
    }
    return child instanceof BnfExpression && !(child instanceof BnfLiteralExpression || child instanceof BnfReferenceOrToken);
  }

  public static BnfExpression getEffectiveExpression(BnfExpression tree, Map<String, BnfRule> ruleMap) {
    if (tree instanceof BnfReferenceOrToken) {
      BnfRule rule = ruleMap.get(tree.getText());
      if (rule != null) return rule.getExpression();
    }
    return tree;
  }
  
  public static IElementType getEffectiveType(PsiElement tree) {
    if (tree instanceof BnfParenOptExpression) {
      return BnfTypes.BNF_OP_OPT;
    }
    else if (tree instanceof BnfQuantified) {
      final BnfQuantifier quantifier = ((BnfQuantified)tree).getQuantifier();
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
      return BnfTypes.BNF_SEQUENCE;
    }
    else {
      return tree.getNode().getElementType();
    }
  }

  public static List<BnfExpression> getChildExpressions(BnfExpression node) {
    return PsiTreeUtil.getChildrenOfTypeAsList(node, BnfExpression.class);
  }

  public static String getNextName(String funcName, int i) {
    return funcName + "_" + i;
  }


  public static class Rule {

    public static boolean isPrivate(BnfRule node) {
      return hasModifier(node, "private");
    }

    public static boolean isExternal(BnfRule node) {
      return hasModifier(node, "external");
    }

    public static boolean isMeta(BnfRule node) {
      return hasModifier(node, "meta");
    }

    public static boolean isLeft(BnfRule node) {
      return hasModifier(node, "left");
    }

    public static boolean isInner(BnfRule node) {
      return hasModifier(node, "inner");
    }

    private static boolean hasModifier(BnfRule node, String s) {
      for (BnfModifier modifier : node.getModifierList()) {
        if (s.equals(modifier.getText())) return true;
      }
      return false;
    }

    public static PsiElement firstNotTrivial(BnfRule rule) {
      for (PsiElement tree = rule.getExpression(); tree != null; tree = PsiTreeUtil.getChildOfType(tree, BnfExpression.class)) {
        if (!isTrivialNode(tree)) return tree;
      }
      return null;
    }

    public static <T> T attribute(BnfRule rule, String attrName, @Nullable T def) {
      PsiElement attr = findAttributeValueNode(rule.getAttrs(), attrName, null);
      Object attrVal = getLiteralValue(attr);
      if (attrVal != null) return attr == NULL ? def : (T)attrVal;
      return def;
    }
    
    public static BnfRule of(BnfExpression expr) {
      return PsiTreeUtil.getParentOfType(expr, BnfRule.class);
    }
  }
  
  public static class PinMatcher {

    public final Object pinValue;
    private final int pinIndex;
    private final Pattern pinPattern;

    public PinMatcher(BnfRule rule, IElementType type, String funcName) {
      pinValue = type == BNF_SEQUENCE ? getAttribute(rule, "pin", null, funcName) : null;
      pinIndex = pinValue instanceof Integer ? (Integer)pinValue : -1;
      pinPattern = pinValue instanceof String? Pattern.compile(StringUtil.unescapeStringCharacters((String)pinValue)) : null;
    }

    boolean active() { return pinIndex > -1 || pinPattern != null; }

    public boolean matches(int i, BnfExpression child) {
      return  i == pinIndex - 1 || pinPattern != null && pinPattern.matcher(child.getText()).matches();
    }
  }
}
