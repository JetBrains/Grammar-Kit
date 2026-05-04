/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Function;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.containers.JBTreeTraverser;
import com.intellij.util.containers.TreeTraversal;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.java.JavaBnfConstants;
import org.intellij.grammar.generator.java.JavaNames;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.TypeParameterInfo;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.intellij.grammar.util.Case;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.intellij.grammar.psi.BnfAst.compilePattern;
import static org.intellij.grammar.psi.BnfAttributes.getAttribute;
import static org.intellij.grammar.psi.BnfAttributes.getRootAttribute;

/**
 * @author gregory
 *         Date: 16.07.11 10:41
 */
public class ParserGeneratorUtil {

  public static @NotNull <T extends Enum<T>> T enumFromString(@Nullable String value, @NotNull T def) {
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



  public static boolean isRollbackRequired(BnfExpression o, BnfFile file) {
    if (o instanceof BnfStringLiteralExpression) return false;
    if (!(o instanceof BnfReferenceOrToken)) return true;
    String value = GrammarUtil.stripQuotesAroundId(o.getText());
    BnfRule subRule = file.getRule(value);
    if (subRule == null) return false;
    if (getAttribute(subRule, KnownAttribute.RECOVER_WHILE) != null) return true;
    if (!getAttribute(subRule, KnownAttribute.HOOKS).isEmpty()) return true;
    return BnfRules.isExternal(subRule);
  }


  public static String getTokenType(BnfFile file, String token, @NotNull Case cas) {
    final var format = NameFormat.from(getRootAttribute(file, KnownAttribute.ELEMENT_TYPE_PREFIX));
    String fixed = cas.apply(token.replaceAll("[^:\\p{javaJavaIdentifierPart}]", "_"));
    return format.apply(fixed);
  }

  public static @NotNull Collection<BnfRule> getSortedPublicRules(@NotNull Set<PsiElement> accessors) {
    Map<String, BnfRule> result = new TreeMap<>();
    for (PsiElement tree : accessors) {
      if (tree instanceof BnfRule rule) {
        if (!BnfRules.isPrivate(rule)) result.put(rule.getName(), rule);
      }
    }
    return result.values();
  }

  public static @NotNull Collection<BnfExpression> getSortedTokens(@NotNull Set<PsiElement> accessors) {
    Map<String, BnfExpression> result = new TreeMap<>();
    for (PsiElement tree : accessors) {
      if (!(tree instanceof BnfReferenceOrToken || tree instanceof BnfLiteralExpression)) continue;
      result.put(tree.getText(), (BnfExpression)tree);
    }
    return result.values();
  }

  public static @NotNull Collection<LeafPsiElement> getSortedExternalRules(@NotNull Set<PsiElement> accessors) {
    Map<String, LeafPsiElement> result = new TreeMap<>();
    for (PsiElement tree : accessors) {
      if (!(tree instanceof LeafPsiElement)) continue;
      result.put(tree.getText(), (LeafPsiElement)tree);
    }
    return result.values();
  }

  public static @NotNull List<BnfRule> topoSort(@NotNull Collection<BnfRule> rules, @NotNull RuleGraphHelper ruleGraph) {
    Set<BnfRule> rulesSet = new HashSet<>(rules);
    return new JBTreeTraverser<BnfRule>(
      rule -> JBIterable.from(ruleGraph.getSubRules(rule)).filter(rulesSet::contains))
      .withRoots(ContainerUtil.reverse(new ArrayList<>(rules)))
      .withTraversal(TreeTraversal.POST_ORDER_DFS)
      .unique()
      .toList();
  }

  public static boolean isRegexpToken(@NotNull String tokenText) {
    return tokenText.startsWith(CommonBnfConstants.REGEXP_PREFIX);
  }

  public static @NotNull String getRegexpTokenRegexp(@NotNull String tokenText) {
    return tokenText.substring(CommonBnfConstants.REGEXP_PREFIX.length());
  }

  private static boolean isTokenChoice(@NotNull BnfFile file, @NotNull BnfExpression choice) {
    return choice instanceof BnfChoice && BnfAst.getTokenNames(file, ((BnfChoice)choice).getExpressionList(), 2) != null;
  }

  static boolean hasAtLeastOneTokenChoice(@NotNull BnfFile file, @NotNull Collection<String> ownRuleNames) {
    for (String ruleName : ownRuleNames) {
      BnfRule rule = file.getRule(ruleName);
      if (rule == null) continue;
      BnfExpression expression = rule.getExpression();
      if (isTokenChoice(file, expression)) return true;
    }
    return false;
  }

  private static boolean appendTokenTypes(@NotNull StringBuilder builder, int count, int line, int size) {
    boolean newLine = line == 0 && count == 2 || line > 0 && (count - 2) % 6 == 0;
    newLine &= (size - count) > 2;
    if (count > 0) builder.append(",").append(newLine ? "\n" : " ");
    return newLine;
  }

  public static void appendTokenTypes(StringBuilder sb, List<String> tokenTypes) {
    for (int count = 0, line = 0, size = tokenTypes.size(); count < size; count++) {
      boolean newLine = appendTokenTypes(sb, count, line, size);
      sb.append(tokenTypes.get(count));
      if (newLine) line++;
    }
  }

  public static void appendTokenTypes(@NotNull StringBuilder builder,
                                      @NotNull List<@NotNull String> tokenTypes,
                                      @NotNull String elementTypesHolder) {
    for (int count = 0, line = 0, size = tokenTypes.size(); count < size; count++) {
      boolean newLine = appendTokenTypes(builder, count, line, size);
      builder.append(elementTypesHolder).append(".").append(tokenTypes.get(count));
      if (newLine) line++;
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

  public static Map<String, String> collectTokenPattern2Name(@NotNull BnfFile file,
                                                             boolean createTokenIfMissing,
                                                             @NotNull Map<String, String> map,
                                                             @Nullable Set<String> usedInGrammar) {
    Set<String> usedNames = usedInGrammar != null ? usedInGrammar : new LinkedHashSet<>();
    Map<String, String> origTokens = BnfAst.getTokenTextToNameMap(file);
    Pattern pattern = getAllTokenPattern(origTokens);
    int[] autoCount = {0};
    Set<String> origTokenNames = BnfAst.getTokenNameToTextMap(file).keySet();

    BnfVisitor<Void> visitor = new BnfVisitor<>() {

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

  public static @Nullable String quote(@Nullable String text) {
    return quote(text, "\"");
  }

  public static @Nullable String quote(@Nullable String text, @NotNull String quoteString) {
    if (text == null) return null;
    return quoteString + text + quoteString;
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

  public static @Nullable Pattern getAllTokenPattern(Map<String, String> tokens) {
    StringBuilder sb = new StringBuilder();
    for (String pattern : tokens.keySet()) {
      if (!isRegexpToken(pattern)) continue;
      if (!sb.isEmpty()) sb.append("|");
      sb.append(getRegexpTokenRegexp(pattern));
    }
    return compilePattern(sb.toString());
  }

  public static String getParametersString(List<String> paramsTypes,
                                           int offset,
                                           int mask,
                                           Function<? super String, String> substitutor,
                                           Function<? super Integer, ? extends List<String>> annoProvider,
                                           NameShortener shortener) {
    StringBuilder sb = new StringBuilder();
    for (int i = offset; i < paramsTypes.size(); i += 2) {
      if (i > offset) sb.append(", ");
      String type = substitutor.fun(paramsTypes.get(i));
      String name = paramsTypes.get(i + 1);
      String rawType = JavaNames.getRawClassName(type);
      if (rawType.endsWith(JavaBnfConstants.AST_NODE_CLASS)) name = "node";
      if (rawType.endsWith("ElementType")) name = "type";
      if (rawType.endsWith("Stub")) name = "stub";
      if ((mask & 1) == 1) {
        List<String> annos = annoProvider.fun(i);
        for (String s : annos) {
          if (s.startsWith("kotlin.")) continue;
          sb.append("@").append(shortener.shorten(s)).append(" ");
        }
        sb.append(shortener.shorten(type));
      }
      if ((mask & 3) == 3) sb.append(" ");
      if ((mask & 2) == 2) sb.append(name);
    }
    return sb.toString();
  }

  public static @NotNull String unwrapTypeArgumentForParamList(String type) {
    if (!type.endsWith(">")) return type;
    int idx = type.lastIndexOf('<');
    if (idx < 0 || idx > 0 && type.charAt(idx - 1) != ' ') return type;
    return type.substring(0, idx) + type.substring(idx + 1, type.length() - 1);
  }

  public static String getGenericClauseString(List<TypeParameterInfo> genericParameters, NameShortener shortener) {
    if (genericParameters.isEmpty()) return "";

    StringBuilder buffer = new StringBuilder();
    buffer.append('<');
    for (int i = 0; i < genericParameters.size(); i++) {
      if (i > 0) buffer.append(", ");

      TypeParameterInfo parameter = genericParameters.get(i);
      for (String annotation : parameter.getAnnotations()) {
        buffer.append("@").append(shortener.shorten(annotation)).append(" ");
      }
      buffer.append(parameter.getName());

      List<String> extendsList = parameter.getExtendsList();
      if (!extendsList.isEmpty()) {
        buffer.append(" extends ");
        for (int i1 = 0; i1 < extendsList.size(); i1++) {
          if (i1 > 0) buffer.append(" & ");
          String superType = extendsList.get(i1);
          String shortened = shortener.shorten(superType);
          buffer.append(shortened);
        }
      }
    }

    buffer.append("> ");
    return buffer.toString();
  }

  public static @NotNull String getThrowsString(List<String> exceptionList, NameShortener shortener) {
    if (exceptionList.isEmpty()) return "";

    List<String> shortened = ContainerUtil.map(exceptionList, shortener::shorten);

    StringBuilder buffer = new StringBuilder();
    buffer.append(" throws ");
    StringUtil.join(shortened, ", ", buffer);
    return buffer.toString();
  }

  static @NotNull String staticStarImport(@NotNull String fqn) {
    return "static " + fqn + ".*";
  }

  public static @NotNull String starImport(@NotNull String fqn) {
    return fqn + ".*";
  }

  static @NotNull <K extends Comparable<? super K>, V> Map<K, V> take(@NotNull Map<K, V> map) {
    Map<K, V> result = new TreeMap<>(map);
    map.clear();
    return result;
  }

  enum ConsumeType {
    FAST, SMART, DEFAULT;

    public static @NotNull ConsumeType forRule(@NotNull BnfRule rule) {
      String value = getAttribute(rule, KnownAttribute.CONSUME_TOKEN_METHOD);
      for (ConsumeType method : values()) {
        if (StringUtil.equalsIgnoreCase(value, method.name())) return method;
      }
      return ObjectUtils.chooseNotNull(forMethod(value), DEFAULT);
    }

    public static @Nullable ConsumeType forMethod(String value) {
      if ("consumeTokenFast".equals(value)) return FAST;
      if ("consumeTokenSmart".equals(value)) return SMART;
      if ("consumeToken".equals(value)) return DEFAULT;
      return null;
    }

    public static @Nullable ConsumeType min(@Nullable ConsumeType a, @Nullable ConsumeType b) {
      if (a == null || b == null) return null;
      return a.compareTo(b) < 0 ? a : b;
    }

    public static @Nullable ConsumeType max(@Nullable ConsumeType a, @Nullable ConsumeType b) {
      if (a == null) return b;
      if (b == null) return a;
      return a.compareTo(b) < 0 ? b : a;
    }

    public @NotNull String getMethodSuffix() {
      return this == DEFAULT ? "" : StringUtil.capitalize(name().toLowerCase());
    }

    public @NotNull String getMethodName() {
      return KnownAttribute.CONSUME_TOKEN_METHOD.getDefaultValue() + getMethodSuffix();
    }
  }

}
