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

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.intellij.grammar.psi.BnfTypes.BNF_SEQUENCE;

/**
 * @author gregory
 *         Date: 16.07.11 10:41
 */
public class ParserGeneratorUtil {
  private static final Object NULL = new Object();
  private static final BnfExpression NULL_ATTR = createFake("NULL");

  public static <T> T getRootAttribute(PsiElement node, KnownAttribute<T> attribute) {
    return getRootAttribute(node, attribute, null);
  }

  public static <T> T getRootAttribute(PsiElement node, KnownAttribute<T> attribute, @Nullable String match) {
    PsiFile file = node.getContainingFile();
    PsiElement firstItem = file instanceof BnfFile? ContainerUtil.getFirstItem(((BnfFile)file).getAttributes()) : null;
    return getAttributeInner(firstItem, attribute, match);
  }

  public static <T> T getAttribute(BnfRule rule, KnownAttribute<T> attribute) {
    return getAttribute(rule, attribute, rule.getName());
  }

  public static <T> T getAttribute(BnfRule rule, KnownAttribute<T> attribute, String match) {
    return getAttributeInner(rule, attribute, match);
  }

  private static <T> T getAttributeInner(PsiElement node, KnownAttribute<T> attribute, @Nullable String match) {
    BnfCompositeElement parent = PsiTreeUtil.getNonStrictParentOfType(node, BnfRule.class, BnfAttrs.class);
    BnfAttrs attrs = parent instanceof BnfRule ? ((BnfRule)parent).getAttrs() : (BnfAttrs)parent;
    BnfExpression attrValue = findAttributeValueNode(attrs, attribute.getName(), match);

    Object attrVal = getAttributeValue(attrValue);
    if (attrVal != null) return attrVal == NULL ? attribute.getDefaultValue() : attribute.ensureValue(attrVal);
    if (parent == null) return attribute.getDefaultValue();
    if (parent instanceof BnfAttrs && parent.getParent() instanceof BnfRule) parent = (BnfRule)parent.getParent();
    for (PsiElement child = GrammarUtil.getDummyAwarePrevSibling(parent);
         child != null;
         child = GrammarUtil.getDummyAwarePrevSibling(child)) {
      if (!(child instanceof BnfAttrs)) continue;
      attrValue = findAttributeValueNode((BnfAttrs)child, attribute.getName(), match);

      attrVal = getAttributeValue(attrValue);
      if (attrVal != null) return attrVal == NULL ? attribute.getDefaultValue() : attribute.ensureValue(attrVal);
    }
    return attribute.getDefaultValue();
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
  public static BnfExpression findAttributeValueNode(BnfAttrs attrs, String attrName, String ruleName) {
    if (attrs == null) return null;
    BnfExpression noPattern = null;
    for (BnfAttr tree : attrs.getAttrList()) {
      if (attrName.equals(tree.getId().getText())) {
        BnfAttrPattern attrPattern = tree.getAttrPattern();
        BnfExpression attrValue = tree.getExpression();
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
      if (value.equals(getAttributeValue(tree.getExpression()))) {
        return tree;
      }
    }
    return null;
  }

  public static Object getAttributeValue(BnfExpression value) {
    if (value == null) return null;
    if (value == NULL_ATTR) return NULL;
    if (value instanceof BnfReferenceOrToken) {
      return getTokenValue((BnfReferenceOrToken)value);
    }
    else if (value instanceof BnfLiteralExpression) {
      return getLiteralValue((BnfLiteralExpression)value);
    }
    else if (value instanceof BnfValueList) {
      List<Pair<String, String>> pairs = new SmartList<Pair<String, String>>();
      for (BnfListEntry o : ((BnfValueList)value).getListEntryList()) {
        PsiElement id = o.getId();
        pairs.add(Pair.create(id == null? null : id.getText(), (String)getLiteralValue(o.getLiteralExpression())));
      }
      return pairs;
    }
    return null;
  }

  private static Object getLiteralValue(BnfLiteralExpression child) {
    if (child == null) return null;
    PsiElement literal = PsiTreeUtil.getDeepestFirst(child);
    String text = child.getText();
    IElementType elementType = literal.getNode().getElementType();
    if (elementType == BnfTypes.BNF_STRING) return StringUtil.stripQuotesAroundValue(text);
    if (elementType == BnfTypes.BNF_NUMBER) return Integer.parseInt(text);
    return null;
  }

  private static Object getTokenValue(BnfReferenceOrToken child) {
    String text = child.getText();
    if (text.equals("true") || text.equals("false")) return Boolean.parseBoolean(text);
    if (text.equals("null")) return NULL;
    return text;
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

  public static BnfExpression getEffectiveExpression(BnfFile file, BnfExpression tree) {
    if (tree instanceof BnfReferenceOrToken) {
      BnfRule rule = file.getRule(tree.getText());
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

  public static String toIdentifier(String text, String prefix) {
    StringBuilder sb = new StringBuilder(prefix);
    for (String s : text.split("_")) {
      if (s.length() == 0) continue;
      sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1));
    }
    return sb.toString();
  }

  public static String getPsiPackage(final BnfFile file) {
    return getRootAttribute(file, KnownAttribute.PSI_PACKAGE);
  }

  public static String getPsiImplPackage(final BnfFile file) {
    return getRootAttribute(file, KnownAttribute.PSI_IMPL_PACKAGE);
  }

  public static String getPsiImplSuffix(final BnfFile file) {
    return getRootAttribute(file, KnownAttribute.PSI_IMPL_CLASS_SUFFIX);
  }

  @NotNull
  public static String getRulePsiClassName(BnfRule rule, final String prefix) {
    return toIdentifier(rule.getName(), prefix);
  }

  public static String getPsiClassPrefix(final BnfFile file) {
    return getRootAttribute(file, KnownAttribute.PSI_CLASS_PREFIX);
  }

  public static String getQualifiedRuleClassName(BnfRule rule, boolean impl) {
    BnfFile file = (BnfFile)rule.getContainingFile();
    String packageName = impl ? getPsiImplPackage(file) : getPsiPackage(file);
    return packageName + "." + getRulePsiClassName(rule, getPsiClassPrefix(file)) + (impl? getPsiImplSuffix(file): "");
  }

  public static BnfExpression createFake(final String text) {
    return new MyFakeExpression(text);
  }

  @Nullable
  public static String getRuleDisplayName(BnfRule rule, boolean force) {
    String name = getAttribute(rule, KnownAttribute.NAME);
    BnfRule realRule = rule;
    if (name != null) {
      realRule = ((BnfFile)rule.getContainingFile()).getRule(name);
      if (realRule != null) name = getAttribute(realRule, KnownAttribute.NAME);
    }
    if (name != null || (!force && realRule == rule)) {
      return StringUtil.isEmpty(name)? null : "<" + name + ">";
    }
    return "<" + toDisplayOrConstantName(realRule.getName(), false) + ">";
  }

  public static String toDisplayOrConstantName(String name, boolean constant) {
    String[] strings = NameUtil.splitNameIntoWords(name);
    for (int i = 0; i < strings.length; i++) strings[i] = constant? strings[i].toUpperCase() : strings[i].toLowerCase();
    return StringUtil.join(strings, constant? "_" : " ");
  }

  public static String getElementType(BnfRule rule) {
    String elementType = StringUtil.notNullize(getAttribute(rule, KnownAttribute.ELEMENT_TYPE), rule.getName());
    return getAttribute(rule, KnownAttribute.ELEMENT_TYPE_PREFIX) + toDisplayOrConstantName(elementType, true);
  }

  public static String wrapCallWithParserInstance(String nodeCall) {
    return "new Parser() {\npublic boolean parse(PsiBuilder builder_, int level_) {\nreturn " + nodeCall + ";\n}\n}";
  }

  public static String generateConsumeTextToken(String tokenText) {
    return "consumeToken(builder_, \"" + tokenText + "\")";
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

    public static boolean isFake(BnfRule node) {
      return hasModifier(node, "fake");
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

    public static <T> T attribute(BnfRule rule, KnownAttribute<T> attribute) {
      BnfExpression attr = findAttributeValueNode(rule.getAttrs(), attribute.getName(), null);
      Object attrVal = getAttributeValue(attr);
      if (attrVal != null) return attr == NULL ? attribute.getDefaultValue() : (T)attrVal;
      return attribute.getDefaultValue();
    }
    
    public static BnfRule of(BnfExpression expr) {
      return PsiTreeUtil.getParentOfType(expr, BnfRule.class);
    }
  }

  @Nullable
  public static String quote(@Nullable String text) {
    if (text == null) return null;
    return "\"" + text + "\"";
  }

  @Nullable
  public static Pattern compilePattern(String text) {
    try {
      return Pattern.compile(text);
    } catch (PatternSyntaxException e) {
      return null;
    }
  }
  
  public static class PinMatcher {

    public final Object pinValue;
    private final int pinIndex;
    private final Pattern pinPattern;

    public PinMatcher(BnfRule rule, IElementType type, String funcName) {
      pinValue = type == BNF_SEQUENCE ? getAttribute(rule, KnownAttribute.PIN, funcName) : null;
      pinIndex = pinValue instanceof Integer? (Integer)pinValue : -1;
      pinPattern = pinValue instanceof String ? compilePattern(StringUtil.unescapeStringCharacters((String) pinValue)) : null;
    }

    boolean active() { return pinIndex > -1 || pinPattern != null; }

    public boolean matches(int i, BnfExpression child) {
      return  i == pinIndex - 1 || pinPattern != null && pinPattern.matcher(child.getText()).matches();
    }
  }

  private static class MyFakeExpression extends FakePsiElement implements BnfExpression{
    private final String myText;

    MyFakeExpression(String text) {
      myText = text;
    }

    @Override
    public PsiElement getParent() {
      return null;
    }

    @Override
    public String getText() {
      return myText;
    }

    @Override
    public String toString() {
      return getText();
    }
  }
}
