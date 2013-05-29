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
package org.intellij.grammar.generator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.SmartList;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.actions.GenerateAction;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
    return ((BnfFile)node.getContainingFile()).findAttributeValue(null, attribute, match);
  }

  public static <T> T getAttribute(BnfRule rule, KnownAttribute<T> attribute) {
    return getAttribute(rule, attribute, null);
  }

  public static <T> T getAttribute(BnfRule rule, KnownAttribute<T> attribute, String match) {
    return ((BnfFile)rule.getContainingFile()).findAttributeValue(rule, attribute, match);
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

  public static <T> T getLiteralValue(BnfLiteralExpression child) {
    if (child == null) return null;
    PsiElement literal = PsiTreeUtil.getDeepestFirst(child);
    String text = child.getText();
    IElementType elementType = literal.getNode().getElementType();
    if (elementType == BnfTypes.BNF_STRING) return (T)StringUtil.stripQuotesAroundValue(text);
    if (elementType == BnfTypes.BNF_NUMBER) return (T)Integer.valueOf(text);
    return null;
  }

  private static Object getTokenValue(BnfReferenceOrToken child) {
    String text = child.getText();
    if (text.equals("true") || text.equals("false")) return Boolean.parseBoolean(text);
    if (text.equals("null")) return NULL;
    return text;
  }

  public static boolean isTrivialNode(PsiElement element) {
    return getTrivialNodeChild(element) != null;
  }

  public static BnfExpression getNonTrivialNode(BnfExpression initialNode) {
    BnfExpression nonTrivialNode = initialNode;
    for (BnfExpression e = initialNode, n = getTrivialNodeChild(e); n != null; e = n, n = getTrivialNodeChild(e)) {
      nonTrivialNode = n;
    }
    return nonTrivialNode;
  }

  public static BnfExpression getTrivialNodeChild(PsiElement element) {
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
    else if (element.getFirstChild() == element.getLastChild() &&
        (element instanceof BnfChoice || element instanceof BnfSequence || element instanceof BnfExpression)) {
      child = element.getFirstChild();
    }
    return child instanceof BnfExpression && !(child instanceof BnfLiteralExpression || child instanceof BnfReferenceOrToken) ?
        (BnfExpression) child : null;
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

  public static List<BnfExpression> getChildExpressions(@Nullable BnfExpression node) {
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
    String displayName = toDisplayOrConstantName(elementType, true);
    return StringUtil.isEmpty(displayName)? "" : getAttribute(rule, KnownAttribute.ELEMENT_TYPE_PREFIX) + displayName;
  }

  public static String wrapCallWithParserInstance(String nodeCall) {
    return "new Parser() {\npublic boolean parse(PsiBuilder builder_, int level_) {\nreturn " + nodeCall + ";\n}\n}";
  }

  public static Collection<BnfRule> getSortedPublicRules(Set<PsiElement> accessors) {
    Map<String, BnfRule> result = new TreeMap<String, BnfRule>();
    for (PsiElement tree : accessors) {
      if (tree instanceof BnfRule) {
        BnfRule rule = (BnfRule)tree;
        if (!Rule.isPrivate(rule)) result.put(rule.getName(), rule);
      }
    }
    return result.values();
  }

  public static Collection<BnfExpression> getSortedTokens(Set<PsiElement> accessors) {
    TreeMap<String, BnfExpression> result = new TreeMap<String, BnfExpression>();
    for (PsiElement tree : accessors) {
      if (!(tree instanceof BnfExpression)) continue;
      result.put(tree.getText(), (BnfExpression)tree);
    }
    return result.values();
  }

  public static Collection<LeafPsiElement> getSortedExternalRules(Set<PsiElement> accessors) {
    TreeMap<String, LeafPsiElement> result = new TreeMap<String, LeafPsiElement>();
    for (PsiElement tree : accessors) {
      if (!(tree instanceof LeafPsiElement)) continue;
      result.put(tree.getText(), (LeafPsiElement) tree);
    }
    return result.values();
  }

  public static <T> List<T> topoSort(Collection<T> collection,
                                     Topology<T> topology) {
    List<T> rulesToSort = new ArrayList<T>(collection);
    if (rulesToSort.size() < 2) return new ArrayList<T>(rulesToSort);
    Collections.reverse(rulesToSort);
    List<T> sorted = new ArrayList<T>(rulesToSort.size());
    int iterationCount = 0;
    main: while (!rulesToSort.isEmpty()) {
      inner: for (T rule : rulesToSort) {
        for (T r : rulesToSort) {
          if ((iterationCount ++) % 100 == 0) {
            ProgressManager.checkCanceled();
          }
          if (rule == r) continue;
          if (topology.contains(rule, r)) continue inner;
        }
        sorted.add(rule);
        rulesToSort.remove(rule);
        continue main;
      }
      sorted.add(topology.forceChoose(rulesToSort));
    }
    return sorted;
  }

  public static void addWarning(Project project, String text) {
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      //noinspection UseOfSystemOutOrSystemErr
      System.out.println(text);
    }
    else {
      GenerateAction.LOG_GROUP.createNotification(text, MessageType.WARNING).notify(project);
    }
  }

  public static boolean isRegexpToken(String tokenText) {
    return tokenText.startsWith("regexp:");
  }

  public static String getRegexpTokenRegexp(String tokenText) {
    return tokenText.substring("regexp:".length());
  }

  public static boolean isTokenSequence(@NotNull BnfRule rule, @Nullable BnfExpression node) {
    if (node == null || useConsumeTokenFast(rule)) return false;
    if (getEffectiveType(node) != BNF_SEQUENCE) return false;
    BnfFile bnfFile = (BnfFile) rule.getContainingFile();
    for (PsiElement child : getChildExpressions(node)) {
      boolean isToken = child instanceof BnfReferenceOrToken && bnfFile.getRule(child.getText()) == null;
      if (!isToken) return false;
    }
    return true;
  }

  public static boolean useConsumeTokenFast(BnfRule rule) {
    return "consumeTokenFast".equals(getAttribute(rule, KnownAttribute.CONSUME_TOKEN_METHOD));
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

    public final BnfRule rule;
    public final String funcName;
    public final Object pinValue;
    private final int pinIndex;
    private final Pattern pinPattern;

    public PinMatcher(BnfRule rule, IElementType type, String funcName) {
      this.rule = rule;
      this.funcName = funcName;
      pinValue = type == BNF_SEQUENCE ? getAttribute(rule, KnownAttribute.PIN, funcName) : null;
      pinIndex = pinValue instanceof Integer? (Integer)pinValue : -1;
      pinPattern = pinValue instanceof String ? compilePattern((String) pinValue) : null;
    }

    public boolean active() { return pinIndex > -1 || pinPattern != null; }

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

  public static abstract class Topology<T> {
    public abstract boolean contains(T t1, T t2);
    public T forceChoose(Collection<T> col) {
      // choose the first from cycle
      return forceChooseInner(col, Condition.TRUE);
    }

    protected T forceChooseInner(Collection<T> col, Condition<T>... conditions) {
      //for (T rule : rulesToSort) {
      //  StringBuilder sb = new StringBuilder(rule.toString()).append(":");
      //  for (T r : rulesToSort) {
      //    if (rule == r) continue;
      //    if (condition.process(rule, r)) {
      //      sb.append(" ").append(r);
      //    }
      //  }
      //  System.out.println(sb);
      //}
      for (Condition<T> condition : conditions) {
        for (Iterator<T> it = col.iterator(); it.hasNext(); ) {
          T t = it.next();
          if (condition.value(t)) {
            it.remove();
            return t;
          }
        }
      }
      throw new AssertionError();
    }
  }
}
