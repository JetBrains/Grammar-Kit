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
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.containers.JBTreeTraverser;
import com.intellij.util.containers.TreeTraversal;
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

import static java.lang.String.format;
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
  private static final String RESERVED_SUFFIX = "_$";
  private static final Set<String> JAVA_RESERVED = ContainerUtil.newTroveSet(
    "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
    "const", "default", "do", "double", "else", "enum", "extends", "false", "final", "finally",
    "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long",
    "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static",
    "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true",
    "try", "void", "volatile", "while", "continue");

  @NotNull
  public static String getRawClassName(@NotNull String name) {
    return name.indexOf("<") < name.indexOf(">") ? name.substring(0, name.indexOf("<")) : name;
  }

  enum ConsumeType {
    FAST, SMART, DEFAULT;

    @NotNull
    public String getMethodSuffix() {
      return this == DEFAULT ? "" : StringUtil.capitalize(name().toLowerCase());
    }

    @NotNull
    public String getMethodName() {
      return KnownAttribute.CONSUME_TOKEN_METHOD.getDefaultValue() + getMethodSuffix();
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

    @Nullable
    public static ConsumeType min(@Nullable ConsumeType a, @Nullable ConsumeType b) {
      if (a == null || b == null) return null;
      return a.compareTo(b) < 0 ? a : b;
    }

    @Nullable
    public static ConsumeType max(@Nullable ConsumeType a, @Nullable ConsumeType b) {
      if (a == null) return b;
      if (b == null) return a;
      return a.compareTo(b) < 0 ? b : a;
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

  public static <T> T getGenerateOption(@NotNull PsiElement node, @NotNull KnownAttribute<T> attribute,
                                        @NotNull Map<String, String> genOptions, String... genOptionKeys) {
    String currentValue = JBIterable.of(genOptionKeys).map(genOptions::get).filter(Objects::nonNull).first();
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
      String unquoted = GrammarUtil.unquote(text);
      // in double-quoted strings: un-escape quotes only leaving the rest \ manageable
      String result = text.charAt(0) == '"' ? unquoted.replaceAll("\\\\([\"'])", "$1") : unquoted;
      return (T) result;
    }
    return null;
  }

  private static Object getTokenValue(BnfReferenceOrToken child) {
    String text = child.getText();
    if (text.equals("true") || text.equals("false")) return Boolean.parseBoolean(text);
    if (text.equals("null")) return NULL;
    return GrammarUtil.getIdText(child);
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
    else if (element.getFirstChild() == element.getLastChild() && element instanceof BnfExpression) {
      child = element.getFirstChild();
    }
    return child instanceof BnfExpression && !(child instanceof BnfLiteralExpression || child instanceof BnfReferenceOrToken) ?
        (BnfExpression) child : null;
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

  @NotNull
  private static String getBaseName(@NotNull String name) {
    return toIdentifier(name, null, Case.AS_IS);
  }

  public static String getFuncName(@NotNull BnfRule r) {
    String name = getBaseName(r.getName());
    return JAVA_RESERVED.contains(name) ? name + RESERVED_SUFFIX : name;
  }

  @NotNull
  static String getWrapperParserConstantName(@NotNull String nextName) {
    return getBaseName(nextName) + "_parser_";
  }

  @NotNull
  static String getWrapperParserMetaMethodName(@NotNull String nextName) {
    return getBaseName(nextName) + RESERVED_SUFFIX;
  }

  public static String getNextName(@NotNull String funcName, int i) {
    return StringUtil.trimEnd(funcName, RESERVED_SUFFIX) + "_" + i;
  }

  @NotNull
  public static String getGetterName(@NotNull String text) {
    return toIdentifier(text, NameFormat.from("get"), Case.CAMEL);
  }

  @NotNull
  static String getTokenSetConstantName(@NotNull String nextName) {
    return toIdentifier(nextName, null, Case.UPPER) + "_TOKENS";
  }

  public static boolean isRollbackRequired(BnfExpression o, BnfFile file) {
    if (!(o instanceof BnfReferenceOrToken)) return true;
    String value = GrammarUtil.stripQuotesAroundId(o.getText());
    BnfRule subRule = file.getRule(value);
    if (subRule == null) return false;
    if (getAttribute(subRule, KnownAttribute.RECOVER_WHILE) != null) return true;
    if (!getAttribute(subRule, KnownAttribute.HOOKS).isEmpty()) return true;
    if (Rule.isExternal(subRule)) return true;
    return false;
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

  public static Couple<String> getQualifiedRuleClassName(BnfRule rule) {
    BnfFile file = (BnfFile)rule.getContainingFile();
    String psiPackage = getAttribute(rule, KnownAttribute.PSI_PACKAGE);
    String psiImplPackage = getAttribute(rule, KnownAttribute.PSI_IMPL_PACKAGE);
    NameFormat psiFormat = getPsiClassFormat(file);
    NameFormat psiImplFormat = getPsiImplClassFormat(file);
    return Couple.of(psiPackage + "." + getRulePsiClassName(rule, psiFormat),
                     psiImplPackage + "." + getRulePsiClassName(rule, psiImplFormat));
  }

  @NotNull
  public static List<NavigatablePsiElement> findRuleImplMethods(@NotNull JavaHelper helper,
                                                                @Nullable String psiImplUtilClass,
                                                                @Nullable String methodName,
                                                                @Nullable BnfRule rule) {
    if (rule == null) return Collections.emptyList();
    List<NavigatablePsiElement> methods = Collections.emptyList();
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
    Set<String> result = ContainerUtil.newLinkedHashSet();
    BnfFile file = (BnfFile)rule.getContainingFile();
    BnfRule topSuper = getEffectiveSuperRule(file, rule);
    String superClassName = topSuper == null ? getRootAttribute(file, KnownAttribute.EXTENDS) :
                            topSuper == rule ? getAttribute(rule, KnownAttribute.EXTENDS) :
                            getAttribute(topSuper, KnownAttribute.PSI_PACKAGE) + "." +
                            getRulePsiClassName(topSuper, getPsiClassFormat(file));
    String implSuper = StringUtil.notNullize(getAttribute(rule, KnownAttribute.MIXIN), superClassName);
    Couple<String> names = getQualifiedRuleClassName(rule);
    result.add(names.first);
    result.add(names.second);
    result.add(superClassName);
    result.add(implSuper);
    result.addAll(getSuperInterfaceNames(file, rule, getPsiClassFormat(file)));
    return result;
  }

  @NotNull
  static JBIterable<BnfRule> getSuperRules(@NotNull BnfFile file, @Nullable BnfRule rule) {
    abstract class Fun<S, T> extends JBIterable.Stateful<Fun> implements Function<S, T> { }
    JBIterable<Object> result = JBIterable.generate(rule, new Fun<Object, Object>() {
      final Set<BnfRule> visited = ContainerUtil.newHashSet();

      @Override
      public Object fun(Object o) {
        if (o == ObjectUtils.NULL) return null;
        BnfRule cur = (BnfRule)o;
        if (!visited.add(cur)) return ObjectUtils.NULL;
        BnfRule next = getSynonymTargetOrSelf(cur);
        if (next != cur) return next;
        if (cur != rule) return null; // do not search for elementType any further
        String attr = getAttribute(cur, KnownAttribute.EXTENDS);
        //noinspection StringEquality
        BnfRule ext = attr != KnownAttribute.EXTENDS.getDefaultValue() ? file.getRule(attr) : null;
        return ext == null && attr != null ? null : ext;
      }
    }).map(o -> o == ObjectUtils.NULL ? null : o);
    return (JBIterable<BnfRule>)(JBIterable)result;
  }

  @Nullable
  static BnfRule getEffectiveSuperRule(@NotNull BnfFile file, @Nullable BnfRule rule) {
    return getSuperRules(file, rule).last();
  }

  @NotNull
  static List<String> getSuperInterfaceNames(BnfFile file, BnfRule rule, NameFormat format) {
    List<String> strings = ContainerUtil.newArrayList();
    List<String> topRuleImplements = Collections.emptyList();
    String topRuleClass = null;
    BnfRule topSuper = getEffectiveSuperRule(file, rule);
    if (topSuper != null && topSuper != rule) {
      topRuleImplements = getAttribute(topSuper, KnownAttribute.IMPLEMENTS).asStrings();
      topRuleClass = getAttribute(topSuper, KnownAttribute.PSI_PACKAGE) + "." + getRulePsiClassName(topSuper, format);
      if (!StringUtil.isEmpty(topRuleClass)) strings.add(topRuleClass);
    }
    List<String> rootImplements = getRootAttribute(file, KnownAttribute.IMPLEMENTS).asStrings();
    List<String> ruleImplements = getAttribute(rule, KnownAttribute.IMPLEMENTS).asStrings();
    for (String className : ruleImplements) {
      if (className == null) continue;
      BnfRule superIntfRule = file.getRule(className);
      if (superIntfRule != null) {
        strings.add(getAttribute(superIntfRule, KnownAttribute.PSI_PACKAGE) + "." + getRulePsiClassName(superIntfRule, format));
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
      if (realRule != null && realRule != rule) name = getAttribute(realRule, KnownAttribute.NAME);
    }
    if (name != null || (!force && realRule == rule)) {
      return name;
    }
    else {
      String[] parts = NameUtil.splitNameIntoWords(getFuncName(realRule));
      return Case.LOWER.apply(StringUtil.join(parts, " "));
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

  public static List<BnfRule> topoSort(@NotNull Collection<BnfRule> rules, @NotNull RuleGraphHelper ruleGraph) {
    Set<BnfRule> rulesSet = ContainerUtil.newHashSet(rules);
    return new JBTreeTraverser<BnfRule>(
      rule -> JBIterable.from(ruleGraph.getSubRules(rule)).filter(rulesSet::contains))
      .withRoots(ContainerUtil.reverse(ContainerUtil.newArrayList(rules)))
      .withTraversal(TreeTraversal.POST_ORDER_DFS)
      .unique()
      .toList();
  }

  public static void addWarning(Project project, String s, Object... args) {
    addWarning(project, format(s, args));
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
      addWarning(file.getProject(), className + " class not found" + tail);
    }
  }

  public static boolean isRegexpToken(@NotNull String tokenText) {
    return tokenText.startsWith(BnfConstants.REGEXP_PREFIX);
  }

  public static String getRegexpTokenRegexp(@NotNull String tokenText) {
    return tokenText.substring(BnfConstants.REGEXP_PREFIX.length());
  }

  @Nullable
  static Collection<String> getTokenNames(@NotNull BnfFile file, @NotNull List<BnfExpression> expressions) {
    return getTokenNames(file, expressions, -1);
  }

  @Nullable("when some expression is not a token or total tokens count is less than or equals threshold")
  static Collection<String> getTokenNames(@NotNull BnfFile file, @NotNull List<BnfExpression> expressions, int threshold) {
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

  private static String getTokenName(@NotNull BnfFile file, @NotNull BnfExpression expression) {
    String text = expression.getText();
    if (expression instanceof BnfStringLiteralExpression) {
      return RuleGraphHelper.getTokenTextToNameMap(file).get(GrammarUtil.unquote(text));
    }
    else if (expression instanceof BnfReferenceOrToken) {
      return file.getRule(text) == null ? text : null;
    }
    else {
      return null;
    }
  }

  public static boolean isTokenSequence(@NotNull BnfRule rule, @Nullable BnfExpression node) {
    if (node == null || ConsumeType.forRule(rule) != ConsumeType.DEFAULT) return false;
    if (getEffectiveType(node) != BNF_SEQUENCE) return false;
    BnfFile file = (BnfFile)rule.getContainingFile();
    return getTokenNames(file, getChildExpressions(node)) != null;
  }

  private static boolean isTokenChoice(@NotNull BnfFile file, @NotNull BnfExpression choice) {
    return choice instanceof BnfChoice && getTokenNames(file, ((BnfChoice)choice).getExpressionList(), 2) != null;
  }

  static boolean hasAtLeastOneTokenChoice(@NotNull BnfFile file, @NotNull Collection<String> ownRuleNames) {
    for (String ruleName : ownRuleNames) {
      final BnfRule rule = file.getRule(ruleName);
      if (rule == null) continue;
      final BnfExpression expression = rule.getExpression();
      if (isTokenChoice(file, expression)) return true;
    }
    return false;
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

  private static Collection<String> addNewLines(Collection<String> strings) {
    if (strings.size() < 5) return strings;
    List<String> result = new ArrayList<>();
    int counter = 0;
    for (String string : strings) {
      if (counter > 0 && counter % 4 == 0) {
        result.add("\n" + string);
      }
      else {
        result.add(string);
      }
      counter++;
    }
    return result;
  }

  static String tokenSetString(Collection<String> tokens) {
    String string = String.join(", ", addNewLines(tokens));
    if (tokens.size() < 5) {
      return string;
    }
    else {
      return "\n" + string + "\n";
    }
  }

  public static Map<String, String> collectTokenPattern2Name(@NotNull final BnfFile file,
                                                             final boolean createTokenIfMissing,
                                                             @NotNull final Map<String, String> map,
                                                             @Nullable Set<String> usedInGrammar) {
    final Set<String> usedNames = usedInGrammar != null ? usedInGrammar : ContainerUtil.newLinkedHashSet();
    final Map<String, String> origTokens = RuleGraphHelper.getTokenTextToNameMap(file);
    final Pattern pattern = getAllTokenPattern(origTokens);
    final int[] autoCount = {0};
    final Set<String> origTokenNames = getTokenNameToTextMap(file).keySet();

    BnfVisitor<Void> visitor = new BnfVisitor<Void>() {

      @Override
      public Void visitStringLiteralExpression(@NotNull BnfStringLiteralExpression o) {
        String text = o.getText();
        String tokenText = GrammarUtil.unquote(text);
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

  static boolean isUsedAsArgument(@NotNull BnfRule rule) {
    return !ReferencesSearch.search(rule, rule.getUseScope()).forEach(ref -> !isUsedAsArgument(ref));
  }

  private static boolean isUsedAsArgument(@NotNull PsiReference ref) {
    PsiElement element = ref.getElement();
    if (!(element instanceof BnfExpression)) {
      return false;
    }
    PsiElement parent = element.getParent();
    if (!(parent instanceof BnfExternalExpression) || ((BnfExternalExpression)parent).getRefElement() != element) {
      return false;
    }
    return isArgument((BnfExpression)parent);
  }

  static boolean isArgument(@NotNull BnfExpression expr) {
    PsiElement parent = expr.getParent();
    return parent instanceof BnfExternalExpression && ((BnfExternalExpression)parent).getArguments().contains(expr);
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
    StringBuilder sb = new StringBuilder();
    for (String pattern : tokens.keySet()) {
      if (!isRegexpToken(pattern)) continue;
      if (sb.length() > 0) sb.append("|");
      sb.append(getRegexpTokenRegexp(pattern));
    }
    return compilePattern(sb.toString());
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
                                           Function<Integer, List<String>> annoProvider,
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
      if (type.endsWith("ElementType")) name = "type";
      if (type.endsWith("Stub")) name = "stub";
      if ((mask & 1) == 1) {
        List<String> annos = annoProvider.fun(i);
        for (String s : annos) {
          if (s.startsWith("kotlin.")) continue;
          sb.append("@").append(shortener.fun(s)).append(" ");
        }
        sb.append(shortener.fun(type));
      }
      if ((mask & 3) == 3) sb.append(" ");
      if ((mask & 2) == 2) sb.append(name);
    }
    return sb.toString();
  }

  public static String getGenericClauseString(List<JavaHelper.TypeParameterInfo> genericParameters, Function<String, String> shortener) {
    if (genericParameters.isEmpty()) return "";

    StringBuilder buffer = new StringBuilder();
    buffer.append('<');
    for (int i = 0; i < genericParameters.size(); i++) {
      if (i > 0) buffer.append(", ");

      JavaHelper.TypeParameterInfo parameter = genericParameters.get(i);
      buffer.append(parameter.getName());

      List<String> extendsList = parameter.getExtendsList();
      if (!extendsList.isEmpty()) {
        buffer.append(" extends ");
        for (int i1 = 0; i1 < extendsList.size(); i1++) {
          if (i1 > 0) buffer.append(" & ");
          String superType = extendsList.get(i1);
          String shortened = shortener.fun(superType);
          buffer.append(shortened);
        }
      }
    }

    buffer.append("> ");
    return buffer.toString();
  }

  @NotNull
  public static String getThrowsString(List<String> exceptionList, Function<String, String> shortener) {
    if (exceptionList.isEmpty()) return "";

    List<String> shortened = ContainerUtil.map(exceptionList, shortener);

    StringBuilder buffer = new StringBuilder();
    buffer.append(" throws ");
    StringUtil.join(shortened, ", ", buffer);
    return buffer.toString();
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

  @NotNull
  static String staticStarImport(@NotNull String fqn) {
    return "static " + fqn + ".*";
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

  @NotNull
  static <K extends Comparable<? super K>, V> Map<K, V> take(@NotNull Map<K, V> map) {
    Map<K, V> result = ContainerUtil.newTreeMap(map);
    map.clear();
    return result;
  }
}
