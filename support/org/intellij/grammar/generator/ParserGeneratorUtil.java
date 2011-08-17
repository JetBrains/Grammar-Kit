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
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfDummyElementImpl;
import org.intellij.grammar.psi.impl.BnfFileImpl;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author gregory
 *         Date: 16.07.11 10:41
 */
public class ParserGeneratorUtil {
  private static final Object NULL = new Object();

  public static <T> T getRootAttribute(BnfFileImpl treeRoot, String attrName, @Nullable T def) {
    return getRootAttribute(treeRoot, attrName, def, null);
  }

  public static <T> T getRootAttribute(BnfFileImpl treeRoot, String attrName, @Nullable T def, @Nullable String match) {
    return getAttributeInner(GrammarUtil.findDummyAwareChildOfType(treeRoot, BnfAttrs.class), attrName, def, match);
  }

  public static <T> T getAttribute(BnfRule rule, String attrName, @Nullable T def) {
    return getAttribute(rule, attrName, def, Rule.name(rule));
  }

  public static <T> T getAttribute(BnfRule rule, String attrName, @Nullable T def, String match) {
    return getAttributeInner(rule, attrName, def, match);
  }

  private static <T> T getAttributeInner(PsiElement node, String attrName, @Nullable T def, @Nullable String match) {
    BnfCompositeElement parent = PsiTreeUtil.getNonStrictParentOfType(node, BnfRule.class, BnfAttrs.class);
    BnfAttrValue attrValue =
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
  public static BnfAttrValue findAttributeValueNode(BnfAttrs attrs, String attrName, String ruleName) {
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
          if (Pattern.matches(StringUtil.stripQuotesAroundValue(attrPattern.getString().getText()), ruleName)) {
            return attrValue;
          }
        }
      }
    }
    return noPattern;
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

  public static Object getLiteralValue(BnfAttrValue child) {
    if (child == null) return null;
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

  public static IElementType getEffectiveType(PsiElement tree) {
    if (tree instanceof BnfParenOptExpression) {
      return BnfTypes.BNF_OP_OPT;
    }
    else if (tree instanceof BnfQuantified) {
      final BnfQuantifier quantifier = ((BnfQuantified)tree).getQuantifier();
      final IElementType elementType = PsiTreeUtil.getDeepestFirst(quantifier == null ? tree : quantifier).getNode().getElementType();
      return elementType;
    }
    else if (tree instanceof BnfPredicate) {
      return ((BnfPredicate)tree).getPredicateSign().getFirstChild().getNode().getElementType();
    }
    else if (tree instanceof BnfLiteralExpression) {
      return tree.getFirstChild().getNode().getElementType();
    }
    else if (tree instanceof BnfParenExpression) {
      return BnfTypes.BNF_SEQUENCE;
    }
    else if (tree instanceof BnfStringLiteralExpression) {
      return BnfTypes.BNF_STRING;
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
    public static boolean is(PsiElement node) {
      return node instanceof BnfRule;
    }

    public static List<BnfRule> list(BnfFileImpl parent) {
      final ArrayList<BnfRule> rules = new ArrayList<BnfRule>();
      parent.acceptChildren(new PsiElementVisitor() {
        @Override
        public void visitElement(PsiElement element) {
          if (element instanceof BnfDummyElementImpl) {
            element.acceptChildren(this);
          }
          else if (element instanceof BnfRule) {
            rules.add((BnfRule)element);
          }
        }
      });
      return rules;
    }

    public static boolean isPrivate(BnfRule node) {
      return hasModifier(node, "private");
    }

    public static boolean isExternal(BnfRule node) {
      return hasModifier(node, "external");
    }

    public static boolean isWrapped(BnfRule node) {
      return hasModifier(node, "wrapped");
    }

    private static boolean hasModifier(BnfRule node, String s) {
      for (BnfModifier modifier : node.getModifierList()) {
        if (s.equals(modifier.getText())) return true;
      }
      return false;
    }

    public static String name(@NotNull BnfRule node) {
      return node.getId().getText();
    }

    public static BnfExpression body(BnfRule rule) {
      return rule.getExpression();
    }

    public static PsiElement firstNotTrivial(BnfRule rule) {
      for (PsiElement tree = rule.getExpression(); tree != null; tree = tree.getFirstChild()) {
        if (!isTrivialNode(tree)) return tree;
      }
      return null;
    }

    public static <T> T attribute(BnfRule rule, String attrName, @Nullable T def) {
      BnfAttrValue attr = findAttributeValueNode(rule.getAttrs(), attrName, null);
      Object attrVal = getLiteralValue(attr);
      if (attrVal != null) return attr == NULL ? def : (T)attrVal;
      return def;
    }
  }
}
