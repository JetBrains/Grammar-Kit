/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import org.intellij.grammar.BnfPathsResolution;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.generator.java.JavaNames;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.JavaHelperFactory;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.BnfRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import static org.intellij.grammar.generator.RuleGraphHelper.hasPsiClass;
import static org.intellij.grammar.psi.BnfAst.getTokenTextToNameMap;
import static org.intellij.grammar.psi.BnfAttributes.getAttribute;
import static org.intellij.grammar.psi.BnfAttributes.getRootAttribute;
import static org.intellij.grammar.psi.BnfRules.getEffectiveSuperRule;

/**
 * Immutable, grammar-derived metadata shared across the parser and PSI generators.
 * Computed once per {@link BnfFile} via {@link #build}: rule analysis (element type,
 * parser class, PSI class names, mixin/stub resolution, abstract flag) and helper
 * objects (graph, FIRST/NEXT analyzer, expression helper, simple-token map) are all
 * produced in a single sweep.
 * <p>
 * {@link JavaParserGenerator} and {@link KotlinParserGenerator} each build a fresh
 * {@code GrammarInfo} for their {@link BnfFile}. {@link JavaPsiGenerator} reuses the
 * same instance from its source {@link ParserGenerator}, so the grammar is analyzed
 * exactly once across the parser + PSI pipeline.
 */
public record GrammarInfo(
  @NotNull BnfFile file,
  @NotNull GenOptions options,
  @Nullable String grammarRoot,
  @Nullable String grammarRootParser,
  @NotNull Map<String, RuleInfo> ruleInfos,
  @NotNull Map<String, String> initialSimpleTokens,
  @NotNull RuleGraphHelper graphHelper,
  @NotNull BnfFirstNextAnalyzer firstNextAnalyzer,
  @NotNull ExpressionHelper expressionHelper
) {

  public static @NotNull GrammarInfo build(@NotNull BnfFile file, @NotNull BnfPathsResolution paths) {
    return build(file, JavaHelperFactory.getInstance(file.getProject()).scoped(paths), Generator.defaultWarningSink(file));
  }

  public static @NotNull GrammarInfo build(@NotNull BnfFile file,
                                           @NotNull JavaHelperFactory.ScopedHelpers scopedHelpers,
                                           @NotNull Consumer<String> warningSink) {
    GenOptions options = new GenOptions(file);
    List<BnfRule> rules = file.getRules();
    BnfRule rootRule = rules.isEmpty() ? null : rules.get(0);
    String grammarRoot = rootRule == null ? null : rootRule.getName();

    Map<String, String> simpleTokens = Collections.unmodifiableMap(new LinkedHashMap<>(getTokenTextToNameMap(file)));
    RuleGraphHelper graphHelper = RuleGraphHelper.getCached(file);
    BnfFirstNextAnalyzer firstNextAnalyzer = BnfFirstNextAnalyzer.createAnalyzer(true);
    ExpressionHelper expressionHelper = new ExpressionHelper(file, graphHelper, warningSink);

    NameFormat intfFormat = NameFormat.forPsiClass(file);
    NameFormat implFormat = NameFormat.forPsiImplClass(file);

    Map<String, Builder> builders = new TreeMap<>();
    for (BnfRule rule : rules) {
      Builder b = new Builder();
      b.name = rule.getName();
      b.isFake = BnfRules.isFake(rule);
      b.elementType = CommonRendererUtils.getElementType(rule, options.generateElementCase);
      b.parserClass = getAttribute(rule, KnownAttribute.PARSER_CLASS);
      boolean noPsi = !hasPsiClass(rule);
      String intfPackage = noPsi ? null : getAttribute(rule, KnownAttribute.PSI_PACKAGE);
      String implPackage = noPsi ? null : getAttribute(rule, KnownAttribute.PSI_IMPL_PACKAGE);
      String intfShortName = noPsi ? null : CommonRendererUtils.getRulePsiClassName(rule, intfFormat);
      String implShortName = noPsi ? null : CommonRendererUtils.getRulePsiClassName(rule, implFormat);
      b.intfPackage = intfPackage;
      b.implPackage = implPackage;
      // The legacy RuleInfo ctor unconditionally concatenated `pkg + "." + className`, so noPsi
      // rules ended up with the literal string "null.null" rather than a null reference. Some
      // callers (e.g. JavaPsiGenerator.getAccessorType) are @NotNull-typed and depend on that.
      b.intfClass = intfPackage + "." + intfShortName;
      b.implClass = implPackage + "." + implShortName;
      b.mixin = noPsi ? null : getAttribute(rule, KnownAttribute.MIXIN);
      b.stub = noPsi ? null : getAttribute(rule, KnownAttribute.STUB_CLASS);
      builders.put(b.name, b);
    }

    calcFakeRulesWithType(file, builders);
    calcRulesStubNames(file, builders, scopedHelpers);
    calcAbstractRules(file, grammarRoot, graphHelper, builders);

    Map<String, RuleInfo> ruleInfos = new TreeMap<>();
    for (Map.Entry<String, Builder> e : builders.entrySet()) {
      ruleInfos.put(e.getKey(), e.getValue().toRuleInfo());
    }
    Map<String, RuleInfo> frozenRuleInfos = Collections.unmodifiableMap(ruleInfos);

    String grammarRootParser = grammarRoot == null ? null : frozenRuleInfos.get(grammarRoot).parserClass();

    return new GrammarInfo(file, options, grammarRoot, grammarRootParser, frozenRuleInfos,
                           simpleTokens, graphHelper, firstNextAnalyzer, expressionHelper);
  }

  /**
   * Marks rules reused as another rule's {@code elementType}, so they keep an element-type entry even when {@code fake}.
   */
  private static void calcFakeRulesWithType(@NotNull BnfFile file, @NotNull Map<String, Builder> builders) {
    for (BnfRule rule : file.getRules()) {
      String elementType = getAttribute(rule, KnownAttribute.ELEMENT_TYPE);
      BnfRule referenced = elementType == null ? null : file.getRule(elementType);
      if (referenced == null) continue;
      Builder b = builders.get(referenced.getName());
      if (b != null) b.isInElementType = true;
    }
  }

  /**
   * Resolves each rule's effective stub class — explicit {@code stubClass}, inherited
   * from the effective super-rule, or extracted as the type argument of a stub-aware
   * base class on the {@code mixin}/{@code extends} chain.
   */
  private static void calcRulesStubNames(@NotNull BnfFile file,
                                         @NotNull Map<String, Builder> builders,
                                         @NotNull JavaHelperFactory.ScopedHelpers scopedHelpers) {
    for (BnfRule rule : file.getRules()) {
      Builder b = builders.get(rule.getName());
      if (b == null) continue;
      String stubClass = b.stub;
      if (stubClass == null) {
        BnfRule topSuper = getEffectiveSuperRule(file, rule);
        Builder topB = topSuper == null ? null : builders.get(topSuper.getName());
        stubClass = topB == null ? null : topB.stub;
      }
      BnfRule topSuper = getEffectiveSuperRule(file, rule);
      Builder topB = topSuper == null ? null : builders.get(topSuper.getName());
      String superRuleClass = topSuper == null ? getRootAttribute(file, KnownAttribute.EXTENDS) :
                              topSuper == rule ? getAttribute(rule, KnownAttribute.EXTENDS) :
                              (topB == null ? null : topB.intfClass);
      String implSuper = StringUtil.notNullize(b.mixin, superRuleClass);
      String implSuperRaw = JavaNames.getRawClassName(implSuper);
      JavaHelper hierarchyHelper = scopedHelpers.get(KnownAttribute.MIXIN);
      String stubName =
        StringUtil.isNotEmpty(stubClass) ? stubClass :
        implSuper.indexOf("<") < implSuper.indexOf(">") &&
        !hierarchyHelper.findClassMethods(implSuperRaw, MethodType.INSTANCE, "getParentByStub", false, 0).isEmpty() ?
        implSuper.substring(implSuper.indexOf("<") + 1, implSuper.indexOf(">")) : null;
      if (StringUtil.isNotEmpty(stubName)) {
        b.realStubClass = stubClass;
      }
    }
  }

  /**
   * Marks rules whose generated PSI impl should be {@code abstract}: rules without
   * modifiers, recovery, or hooks, that aren't reused as another rule's element type,
   * aren't the grammar root, and the rule graph reports as collapsible with no incoming
   * references.
   */
  private static void calcAbstractRules(@NotNull BnfFile file,
                                        @Nullable String grammarRoot,
                                        @NotNull RuleGraphHelper graphHelper,
                                        @NotNull Map<String, Builder> builders) {
    Set<String> reusedRules = new HashSet<>();
    for (BnfRule rule : file.getRules()) {
      String elementType = getAttribute(rule, KnownAttribute.ELEMENT_TYPE);
      BnfRule referenced = elementType != null ? file.getRule(elementType) : null;
      if (referenced != null && referenced != rule) reusedRules.add(referenced.getName());
    }
    for (BnfRule rule : file.getRules()) {
      if (reusedRules.contains(rule.getName())) continue;
      if (grammarRoot != null && grammarRoot.equals(rule.getName())) continue;
      if (!rule.getModifierList().isEmpty()) continue;
      if (getAttribute(rule, KnownAttribute.RECOVER_WHILE) != null) continue;
      if (!getAttribute(rule, KnownAttribute.HOOKS).isEmpty()) continue;

      if (graphHelper.canCollapse(rule) && graphHelper.getFor(rule).isEmpty()) {
        Builder b = builders.get(rule.getName());
        if (b != null) b.isAbstract = true;
      }
    }
  }

  /**
   * Mutable working copy used inside {@link #build}; converted to a {@link RuleInfo} record at the end.
   */
  @SuppressWarnings("NotNullFieldNotInitialized")
  private static final class Builder {
    @NotNull String name;
    boolean isFake;
    @NotNull String elementType;
    @Nullable String parserClass;
    @Nullable String intfPackage;
    @Nullable String implPackage;
    @NotNull String intfClass;
    @NotNull String implClass;
    @Nullable String mixin;
    @Nullable String stub;
    @Nullable String realStubClass;
    boolean isAbstract;
    boolean isInElementType;

    @NotNull RuleInfo toRuleInfo() {
      return new RuleInfo(name, isFake, elementType, parserClass,
                          intfPackage, implPackage, intfClass, implClass,
                          mixin, stub, realStubClass, isAbstract, isInElementType);
    }
  }
}
