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
package org.intellij.grammar.generator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ThreeState;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import gnu.trove.TObjectHashingStrategy;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.actions.GenerateAction;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.intellij.grammar.generator.RuleGraphHelper.getSynonymTargetOrSelf;
import static org.intellij.grammar.generator.RuleGraphHelper.getTokenNameToTextMap;
import static org.intellij.grammar.psi.BnfTypes.BNF_SEQUENCE;

/**
 * @author gregory
 *         Date: 16.07.11 10:41
 */
public class ParserGeneratorUtil {
  private static final Object NULL = new Object();
  private static final BnfExpression NULL_ATTR = new FakeBnfExpression("NULL");

  enum ConsumeType {
    DEFAULT, FAST, SMART;

    public String getMethodSuffix() {
      return this == DEFAULT ? "" : StringUtil.capitalize(name().toLowerCase());
    }

    @NotNull
    public static ConsumeType forRule(@NotNull BnfRule rule) {
      String value = getAttribute(rule, KnownAttribute.CONSUME_TOKEN_METHOD);
      for (ConsumeType method : values()) {
        if (StringUtil.equalsIgnoreCase(value, method.name())) return method;
      }
      return ObjectUtils.chooseNotNull(forMethod(value), DEFAULT);
    }

    @Nullable
    public static ConsumeType forMethod(String value) {
      if ("consumeTokenFast".equals(value)) return FAST;
      if ("consumeTokenSmart".equals(value)) return SMART;
      if ("consumeToken".equals(value)) return DEFAULT;
      return null;
    }
  }

  @NotNull
  public static <T extends Enum<T>> T enumFromString(@Nullable String value, @NotNull T def) {
    try {
      return value == null ? def : Enum.valueOf(def.getDeclaringClass(), Case.UPPER.apply(value).replace('-', '_'));
    }
    catch (Exception e) {
      return def;
    }
  }

  public static <T> T getGenerateOption(@NotNull PsiElement node, @NotNull KnownAttribute<T> attribute, @Nullable String currentValue) {
    if (attribute.getDefaultValue() instanceof Boolean) {
      if ("yes".equals(currentValue)) return (T)Boolean.TRUE;
      if ("no".equals(currentValue)) return (T)Boolean.FALSE;
    }
    else if (attribute.getDefaultValue() instanceof Number) {
      int value = StringUtil.parseInt(currentValue, -1);
      if (value != -1) return (T)Integer.valueOf(value);
    }
    return getRootAttribute(node, attribute, null);
  }

  public static <T> T getRootAttribute(@NotNull PsiElement node, @NotNull KnownAttribute<T> attribute) {
    return getRootAttribute(node, attribute, null);
  }

  public static <T> T getRootAttribute(@NotNull PsiElement node, @NotNull KnownAttribute<T> attribute, @Nullable String match) {
    return ((BnfFile)node.getContainingFile()).findAttributeValue(null, attribute, match);
  }

  public static <T> T getAttribute(@NotNull BnfRule rule, @NotNull KnownAttribute<T> attribute) {
    return getAttribute(rule, attribute, null);
  }

  @Nullable
  public static <T> BnfAttr findAttribute(@NotNull BnfRule rule, @NotNull KnownAttribute<T> attribute) {
    return ((BnfFile)rule.getContainingFile()).findAttribute(rule, attribute, null);
  }

  public static <T> T getAttribute(@NotNull BnfRule rule, @NotNull KnownAttribute<T> attribute, @Nullable String match) {
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
      KnownAttribute.ListValue pairs = new KnownAttribute.ListValue();
      for (BnfListEntry o : ((BnfValueList)value).getListEntryList()) {
        PsiElement id = o.getId();
        pairs.add(Pair.create(id == null? null : id.getText(), getLiteralValue(o.getLiteralExpression())));
      }
      return pairs;
    }
    return null;
  }

  @Nullable
  public static String getLiteralValue(BnfStringLiteralExpression child) {
    return getLiteralValue((BnfLiteralExpression)child);
  }

  @Nullable
  public static <T> T getLiteralValue(BnfLiteralExpression child) {
    if (child == null) return null;
    PsiElement literal = PsiTreeUtil.getDeepestFirst(child);
    String text = child.getText();
    IElementType elementType = literal.getNode().getElementType();
    if (elementType == BnfTypes.BNF_NUMBER) return (T)Integer.valueOf(text);
    if (elementType == BnfTypes.BNF_STRING) {
      String unquoted = StringUtil.stripQuotesAroundValue(text);
      // in double-quoted strings: un-escape quotes only leaving the rest \ manageable
      String result = text.charAt(0) == '"' ? unquoted.replaceAll("\\\\(\"|')", "$1") : unquoted;
      return (T) result;
    }
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

  public static String getFuncName(@NotNull BnfRule r) {
    return toIdentifier(r.getName(), null, Case.AS_IS);
  }

  public static String getNextName(@NotNull String funcName, int i) {
    return funcName + "_" + i;
  }

  @NotNull
  public static String getGetterName(@NotNull String text) {
    return toIdentifier(text, NameFormat.from("get"), Case.CAMEL);
  }

  @TestOnly
  @NotNull
  public static String toIdentifier(@NotNull String text, @Nullable NameFormat format, @NotNull Case cas) {
    if (text.isEmpty()) return "";
    String fixed = text.replaceAll("[^:\\p{javaJavaIdentifierPart}]", "_");
    boolean allCaps = Case.UPPER.apply(fixed).equals(fixed);
    StringBuilder sb = new StringBuilder();
    if (!Character.isJavaIdentifierStart(fixed.charAt(0)) && sb.length() == 0) sb.append("_");
    String[] strings = NameUtil.nameToWords(fixed);
    for (int i = 0, len = strings.length; i < len; i++) {
      String s = strings[i];
      if (cas == Case.CAMEL && s.startsWith("_") && !(i == 0 || i == len - 1)) continue;
      if (cas == Case.UPPER && !s.startsWith("_") && !(i == 0 || StringUtil.endsWith(sb, "_"))) sb.append("_");
      if (cas == Case.CAMEL && !allCaps && Case.UPPER.apply(s).equals(s)) sb.append(s);
      else sb.append(cas.apply(s));
    }
    return format == null ? sb.toString() : format.apply(sb.toString());
  }

  public static String getPsiPackage(BnfFile file) {
    return getRootAttribute(file, KnownAttribute.PSI_PACKAGE);
  }

  public static String getPsiImplPackage(BnfFile file) {
    return getRootAttribute(file, KnownAttribute.PSI_IMPL_PACKAGE);
  }

  @NotNull
  public static NameFormat getPsiClassFormat(BnfFile file) {
    return NameFormat.from(getRootAttribute(file, KnownAttribute.PSI_CLASS_PREFIX));
  }

  @NotNull
  public static NameFormat getPsiImplClassFormat(BnfFile file) {
    String prefix = getRootAttribute(file, KnownAttribute.PSI_CLASS_PREFIX);
    String suffix = getRootAttribute(file, KnownAttribute.PSI_IMPL_CLASS_SUFFIX);
    return NameFormat.from(prefix + "/" + StringUtil.notNullize(suffix));
  }

  @NotNull
  public static String getRulePsiClassName(@NotNull BnfRule rule, @Nullable NameFormat format) {
    return toIdentifier(rule.getName(), format, Case.CAMEL);
  }

  public static String getQualifiedRuleClassName(BnfRule rule, boolean impl) {
    BnfFile file = (BnfFile)rule.getContainingFile();
    String packageName = impl ? getPsiImplPackage(file) : getPsiPackage(file);
    NameFormat format = impl ? getPsiImplClassFormat(file) : getPsiClassFormat(file);
    return packageName + "." + getRulePsiClassName(rule, format);
  }

  @NotNull
  public static List<NavigatablePsiElement> findRuleImplMethods(@NotNull JavaHelper helper,
                                                                @Nullable String psiImplUtilClass,
                                                                @Nullable String methodName,
                                                                @Nullable BnfRule rule) {
    List<NavigatablePsiElement> methods = Collections.emptyList();
    if (rule == null) return methods;
    String selectedSuperClass = null;
    main: for (String ruleClass : getRuleClasses(rule)) {
      for (String utilClass = psiImplUtilClass; utilClass != null; utilClass = helper.getSuperClassName(utilClass)) {
        methods = helper.findClassMethods(utilClass, JavaHelper.MethodType.STATIC, methodName, -1, ruleClass);
        selectedSuperClass = ruleClass;
        if (!methods.isEmpty()) break main;
      }
    }
    return filterOutShadowedRuleImplMethods(selectedSuperClass, methods, helper);
  }

  @NotNull
  private static List<NavigatablePsiElement> filterOutShadowedRuleImplMethods(String selectedClass,
                                                                              List<NavigatablePsiElement> methods,
                                                                              @NotNull JavaHelper helper) {
    if (methods.size() <= 1) return methods;

    // filter out less specific methods
    // todo move to JavaHelper
    List<NavigatablePsiElement> result = ContainerUtil.newArrayList(methods);
    Map<String, NavigatablePsiElement> prototypes = ContainerUtil.newLinkedHashMap();
    for (NavigatablePsiElement m2 : methods) {
      List<String> types = helper.getMethodTypes(m2);
      String proto = m2.getName() + types.subList(3, types.size());
      NavigatablePsiElement m1 = prototypes.get(proto);
      if (m1 == null) {
        prototypes.put(proto, m2);
        continue;
      }
      String type1 = helper.getMethodTypes(m1).get(1);
      String type2 = types.get(1);
      if (Comparing.equal(type1, type2)) continue;
      for (String s = selectedClass; s != null; s = helper.getSuperClassName(s)) {
        if (Comparing.equal(type1, s)) {
          result.remove(m2);
        }
        else if (Comparing.equal(type2, s)) {
          result.remove(m1);
        }
        else continue;
        break;
      }
    }
    return result;
  }

  @NotNull
  public static Set<String> getRuleClasses(@NotNull BnfRule rule) {
    BnfFile file = (BnfFile)rule.getContainingFile();
    Set<String> result = ContainerUtil.newLinkedHashSet();
    String superClassName = getSuperClassName(file, rule, getPsiImplPackage(file), getPsiImplClassFormat(file));
    String implSuper = StringUtil.notNullize(getAttribute(rule, KnownAttribute.MIXIN), superClassName);
    String aPackage = getPsiPackage(file);
    result.add(getQualifiedRuleClassName(rule, false));
    result.add(getQualifiedRuleClassName(rule, true));
    result.add(superClassName);
    result.add(implSuper);
    result.addAll(getSuperInterfaceNames(file, rule, aPackage, getPsiClassFormat(file)));
    return result;
  }

  @Nullable
  static BnfRule getTopSuperRule(@NotNull BnfFile file, BnfRule rule) {
    Set<BnfRule> visited = ContainerUtil.newHashSet();
    BnfRule cur = rule, next = rule;
    for (; next != null && cur != null; cur = !visited.add(next) ? null : next) {
      next = getSynonymTargetOrSelf(cur);
      if (next != cur) continue;
      if (cur != rule) break; // do not search for elementType any further
      String attr = getAttribute(cur, KnownAttribute.EXTENDS);
      //noinspection StringEquality
      next = attr != KnownAttribute.EXTENDS.getDefaultValue() ? file.getRule(attr) : null;
      if (next == null && attr != null) break;
    }
    return cur;
  }

  @NotNull
  static List<String> getSuperInterfaceNames(BnfFile file, BnfRule rule, String psiPackage, NameFormat classPrefix) {
    List<String> strings = ContainerUtil.newArrayList();
    List<String> topRuleImplements = Collections.emptyList();
    String topRuleClass = null;
    BnfRule topSuper = getTopSuperRule(file, rule);
    boolean withPackage = psiPackage.isEmpty();
    if (topSuper != null && topSuper != rule) {
      topRuleImplements = getAttribute(topSuper, KnownAttribute.IMPLEMENTS).asStrings();
      topRuleClass =
        StringUtil.nullize((withPackage ? "" : psiPackage + ".") + getRulePsiClassName(topSuper, classPrefix));
      if (!StringUtil.isEmpty(topRuleClass)) strings.add(topRuleClass);
    }
    List<String> rootImplements = getRootAttribute(file, KnownAttribute.IMPLEMENTS).asStrings();
    List<String> ruleImplements = getAttribute(rule, KnownAttribute.IMPLEMENTS).asStrings();
    for (String className : ruleImplements) {
      if (className == null) continue;
      BnfRule superIntfRule = file.getRule(className);
      if (superIntfRule != null) {
        strings.add((withPackage ? "" : psiPackage + ".") + getRulePsiClassName(superIntfRule, classPrefix));
      }
      else if (!topRuleImplements.contains(className) &&
               (topRuleClass == null || !rootImplements.contains(className))) {
        if (strings.size() == 1 && topSuper == null) {
          strings.add(0, className);
        }
        else {
          strings.add(className);
        }
      }
    }
    return strings;
  }

  @NotNull
  static String getSuperClassName(BnfFile file, BnfRule rule, String psiPackage, NameFormat format) {
    BnfRule topSuper = getTopSuperRule(file, rule);
    return topSuper == null ? getRootAttribute(file, KnownAttribute.EXTENDS) :
           topSuper == rule ? getAttribute(rule, KnownAttribute.EXTENDS) :
           psiPackage + "." + getRulePsiClassName(topSuper, format);
  }

  @Nullable
  public static String getRuleDisplayName(BnfRule rule, boolean force) {
    String s = getRuleDisplayNameRaw(rule, force);
    return StringUtil.isEmpty(s) ? null : "<" + s + ">";
  }

  @Nullable
  private static String getRuleDisplayNameRaw(BnfRule rule, boolean force) {
    String name = getAttribute(rule, KnownAttribute.NAME);
    BnfRule realRule = rule;
    if (name != null) {
      realRule = ((BnfFile)rule.getContainingFile()).getRule(name);
      if (realRule != null) name = getAttribute(realRule, KnownAttribute.NAME);
    }
    if (name != null || (!force && realRule == rule)) {
      return name;
    }
    else {
      return Case.LOWER.apply(StringUtil.join(NameUtil.splitNameIntoWords(realRule.getName()), " "));
    }
  }

  public static String getElementType(BnfRule rule, @NotNull Case cas) {
    String elementType = getAttribute(rule, KnownAttribute.ELEMENT_TYPE);
    if ("".equals(elementType)) return "";
    NameFormat prefix = NameFormat.from(getAttribute(rule, KnownAttribute.ELEMENT_TYPE_PREFIX));
    return toIdentifier(elementType != null ? elementType : rule.getName(), prefix, cas);
  }

  public static String getTokenType(BnfFile file, String token, @NotNull Case cas) {
    NameFormat format = NameFormat.from(getRootAttribute(file, KnownAttribute.ELEMENT_TYPE_PREFIX));
    String fixed = cas.apply(token.replaceAll("[^:\\p{javaJavaIdentifierPart}]", "_"));
    return format == null ? fixed : format.apply(fixed);
  }

  public static Collection<BnfRule> getSortedPublicRules(Set<PsiElement> accessors) {
    Map<String, BnfRule> result = ContainerUtil.newTreeMap();
    for (PsiElement tree : accessors) {
      if (tree instanceof BnfRule) {
        BnfRule rule = (BnfRule)tree;
        if (!Rule.isPrivate(rule)) result.put(rule.getName(), rule);
      }
    }
    return result.values();
  }

  public static Collection<BnfExpression> getSortedTokens(Set<PsiElement> accessors) {
    Map<String, BnfExpression> result = ContainerUtil.newTreeMap();
    for (PsiElement tree : accessors) {
      if (!(tree instanceof BnfReferenceOrToken || tree instanceof BnfLiteralExpression)) continue;
      result.put(tree.getText(), (BnfExpression)tree);
    }
    return result.values();
  }

  public static Collection<LeafPsiElement> getSortedExternalRules(Set<PsiElement> accessors) {
    Map<String, LeafPsiElement> result = ContainerUtil.newTreeMap();
    for (PsiElement tree : accessors) {
      if (!(tree instanceof LeafPsiElement)) continue;
      result.put(tree.getText(), (LeafPsiElement) tree);
    }
    return result.values();
  }

  public static List<BnfRule> topoSort(Collection<BnfRule> rules, RuleGraphHelper ruleGraph) {
    Map<BnfRule, ThreeState> colors = ContainerUtil.newTroveMap();
    for (BnfRule rule : rules) {
      colors.put(rule, ThreeState.UNSURE);
    }
    Iterator<BnfRule> iterator = rules.iterator();
    LinkedList<BnfRule> result = ContainerUtil.newLinkedList();
    LinkedList<BnfRule> stack = ContainerUtil.newLinkedList();
    Collection<? extends BnfRule> inheritors = ruleGraph.getRuleExtendsMap().values();
    while (true) {
      if (stack.isEmpty()) {
        if (iterator.hasNext()) stack.addFirst(iterator.next());
        else break;
      }
      BnfRule rule = stack.pollFirst();
      ThreeState color = colors.get(rule);
      if (color == ThreeState.UNSURE) {
        stack.addFirst(rule);
        colors.put(rule, ThreeState.YES);
        for (BnfRule child : ruleGraph.getSubRules(rule)) {
          if (child == rule) continue;
          ThreeState childColor = colors.get(child);
          if (childColor == null || childColor == ThreeState.NO) continue;

          if (childColor == ThreeState.YES &&
              !ContainerUtil.intersects(ruleGraph.getSubRules(child), inheritors)) {
            continue;
          }
          stack.addFirst(child);
        }
      }
      else if (color == ThreeState.YES) {
        colors.put(rule, ThreeState.NO);
        result.addLast(rule);
      }
    }
    return result;
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

  public static void checkClassAvailability(@NotNull BnfFile file, @Nullable String className, @Nullable String description) {
    if (StringUtil.isEmpty(className)) return;

    JavaHelper javaHelper = JavaHelper.getJavaHelper(file);
    if (javaHelper.findClass(className) == null) {
      String tail = StringUtil.isEmpty(description) ? "" : " (" + description + ")";
      addWarning(file.getProject(), "class not found: " + className + tail);
    }
  }

  public static boolean isRegexpToken(@NotNull String tokenText) {
    return tokenText.startsWith("regexp:");
  }

  public static String getRegexpTokenRegexp(@NotNull String tokenText) {
    return tokenText.substring("regexp:".length());
  }

  public static boolean isTokenSequence(@NotNull BnfRule rule, @Nullable BnfExpression node) {
    if (node == null || ConsumeType.forRule(rule) != ConsumeType.DEFAULT) return false;
    if (getEffectiveType(node) != BNF_SEQUENCE) return false;
    BnfFile bnfFile = (BnfFile) rule.getContainingFile();
    for (PsiElement child : getChildExpressions(node)) {
      boolean isToken = child instanceof BnfReferenceOrToken && bnfFile.getRule(child.getText()) == null;
      if (!isToken) return false;
    }
    return true;
  }

  public static void appendTokenTypes(StringBuilder sb, List<String> tokenTypes) {
    for (int count = 0, line = 0, size = tokenTypes.size(); count < size; count++) {
      boolean newLine = line == 0 && count == 2 || line > 0 && (count - 2) % 6 == 0;
      newLine &= (size - count) > 2;
      if (count > 0) sb.append(",").append(newLine ? "\n" : " ");
      sb.append(tokenTypes.get(count));
      if (newLine) line ++;
    }
  }

  @NotNull
  public static KnownAttribute.ListValue collectUnknownAttributes(@NotNull BnfFile file) {
    KnownAttribute.ListValue result = new KnownAttribute.ListValue();
    BnfAttrs attrs = ContainerUtil.getFirstItem(file.getAttributes());
    if (attrs == null) return result;

    for (BnfAttr attr : attrs.getAttrList()) {
      if (KnownAttribute.getAttribute(attr.getName()) != null) continue;
      BnfExpression expression = attr.getExpression();
      if (!(expression instanceof BnfStringLiteralExpression)) continue;
      result.add(Pair.create(attr.getName(), getLiteralValue((BnfStringLiteralExpression)expression)));
    }
    return result;
  }

  public static Map<String, String> collectTokenPattern2Name(@NotNull final BnfFile file,
                                                             final boolean createTokenIfMissing,
                                                             @NotNull final Map<String, String> map,
                                                             @Nullable Set<String> usedInGrammar) {
    final Set<String> usedNames = usedInGrammar != null ? usedInGrammar : ContainerUtil.<String>newLinkedHashSet();
    final Map<String, String> origTokens = RuleGraphHelper.getTokenTextToNameMap(file);
    final Pattern pattern = getAllTokenPattern(origTokens);
    final int[] autoCount = {0};
    final Set<String> origTokenNames = getTokenNameToTextMap(file).keySet();

    BnfVisitor<Void> visitor = new BnfVisitor<Void>() {

      @Override
      public Void visitStringLiteralExpression(@NotNull BnfStringLiteralExpression o) {
        String text = o.getText();
        String tokenText = StringUtil.stripQuotesAroundValue(text);
        // add auto-XXX token for all unmatched strings to avoid BAD_CHARACTER's
        if (createTokenIfMissing &&
            !usedNames.contains(tokenText) &&
            !StringUtil.isJavaIdentifier(tokenText) &&
            (pattern == null || !pattern.matcher(tokenText).matches())) {
          String tokenName = "_AUTO_" + (autoCount[0]++);
          usedNames.add(text);
          map.put(tokenText, tokenName);
        }
        else {
          ContainerUtil.addIfNotNull(usedNames, origTokens.get(tokenText));
        }
        return null;
      }

      @Override
      public Void visitReferenceOrToken(@NotNull BnfReferenceOrToken o) {
        if (GrammarUtil.isExternalReference(o)) return null;
        BnfRule rule = o.resolveRule();
        if (rule != null) return null;
        String tokenName = o.getText();
        if (usedNames.add(tokenName) && !origTokenNames.contains(tokenName)) {
          map.put(tokenName, tokenName);
        }
        return null;
      }
    };
    for (BnfExpression o : GrammarUtil.bnfTraverserNoAttrs(file).filter(BnfExpression.class)) {
      o.accept(visitor);
    }
    // fix ordering: origTokens go _after_ to handle keywords correctly
    for (String tokenText : origTokens.keySet()) {
      String tokenName = origTokens.get(tokenText);
      map.remove(tokenText);
      map.put(tokenText, tokenName != null || !createTokenIfMissing ? tokenName : "_AUTO_" + (autoCount[0]++));
    }
    return map;
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

    public static boolean isUpper(BnfRule node) {
      return hasModifier(node, "upper");
    }

    private static boolean hasModifier(@Nullable BnfRule rule, @NotNull String s) {
      if (rule == null) return false;
      for (BnfModifier modifier : rule.getModifierList()) {
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
    }
    catch (PatternSyntaxException e) {
      return null;
    }
  }

  public static boolean matchesAny(String regexp, String... text) {
    try {
      Pattern p = Pattern.compile(regexp);
      for (String s : text) {
        if (p.matcher(s).matches()) return true;
      }
    }
    catch (PatternSyntaxException ignored) {
    }
    return false;
  }

  @Nullable
  public static Pattern getAllTokenPattern(Map<String, String> tokens) {
    String allRegexp = "";
    for (String pattern : tokens.keySet()) {
      if (!isRegexpToken(pattern)) continue;
      if (allRegexp.length() > 0) allRegexp += "|";
      allRegexp += getRegexpTokenRegexp(pattern);
    }
    return compilePattern(allRegexp);
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

    public boolean shouldGenerate(List<BnfExpression> children) {
      // do not check last expression, last item pin is trivial
      for (int i = 0, size = children.size(); i < size - 1; i++) {
        if (matches(i, children.get(i))) return true;
      }
      return false;
    }
  }

  public static String getParametersString(List<String> paramsTypes,
                                           int offset,
                                           int mask,
                                           Function<String, String> substitutor,
                                           Function<String, String> shortener) {
    StringBuilder sb = new StringBuilder();
    for (int i = offset; i < paramsTypes.size(); i += 2) {
      if (i > offset) sb.append(", ");
      String type = paramsTypes.get(i);
      String name = paramsTypes.get(i + 1);
      if (type.startsWith("<") && type.endsWith(">")) {
        type = substitutor.fun(type);
      }
      if (BnfConstants.AST_NODE_CLASS.equals(type)) name = "node";
      if ((mask & 1) == 1) sb.append(shortener.fun(type));
      if ((mask & 3) == 3) sb.append(" ");
      if ((mask & 2) == 2) sb.append(name);
    }
    return sb.toString();
  }

  public static class NameFormat {
    final static NameFormat EMPTY = new NameFormat("");

    final String prefix;
    final String suffix;

    public static NameFormat from(@Nullable String format) {
      return StringUtil.isEmpty(format) ? EMPTY : new NameFormat(format);
    }

    private NameFormat(@Nullable String format) {
      JBIterable<String> parts = JBIterable.of(format == null ? null : format.split("/"));
      prefix = parts.get(0);
      suffix = StringUtil.join(parts.skip(1), "");
    }

    public String apply(String s) {
      if (prefix != null) s = prefix + s;
      if (suffix != null) s += suffix;
      return s;
    }

    public String strip(String s) {
      if (prefix != null && s.startsWith(prefix)) s = s.substring(prefix.length());
      if (suffix != null && s.endsWith(suffix)) s = s.substring(0, s.length() - suffix.length());
      return s;
    }

  }

  private static final TObjectHashingStrategy<PsiElement> TEXT_STRATEGY = new TObjectHashingStrategy<PsiElement>() {
    @Override
    public int computeHashCode(PsiElement e) {
      return e.getText().hashCode();
    }

    @Override
    public boolean equals(PsiElement e1, PsiElement e2) {
      return Comparing.equal(e1.getText(), e2.getText());
    }
  };

  public static <T extends PsiElement> TObjectHashingStrategy<T> textStrategy() {
    return (TObjectHashingStrategy<T>)TEXT_STRATEGY;
  }

}
