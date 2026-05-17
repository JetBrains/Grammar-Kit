/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Trinity;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.containers.JBTreeTraverser;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.containers.TreeTraversal;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmTypeRef;
import org.intellij.grammar.classinfo.JvmTypeRefs;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.ParameterSymbol;
import org.intellij.grammar.classinfo.TypeParameterSymbol;
import org.intellij.grammar.generator.java.JavaBnfConstants;
import org.intellij.grammar.generator.java.JavaNameRenderer;
import org.intellij.grammar.generator.java.JavaNameShortener;
import org.intellij.grammar.generator.java.JavaNames;
import org.intellij.grammar.generator.kotlin.KotlinBnfConstants;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.JavaHelperFactory;
import org.intellij.grammar.java.RuleImplUtil;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.util.Case;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.intellij.util.containers.ContainerUtil.emptyList;
import static java.lang.String.format;
import static org.intellij.grammar.generator.NameShortener.addTypeToImports;
import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality.AT_LEAST_ONE;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality.OPTIONAL;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality.REQUIRED;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality;
import static org.intellij.grammar.generator.RuleGraphHelper.getCardinalityText;
import static org.intellij.grammar.generator.java.JavaNames.getRawClassName;
import static org.intellij.grammar.psi.BnfAttributes.getAttribute;
import static org.intellij.grammar.psi.BnfAttributes.getRootAttribute;
import static org.intellij.grammar.psi.BnfRules.getEffectiveSuperRule;
import static org.intellij.grammar.psi.BnfRules.getSuperInterfaceNames;

/**
 * {@link Generator} implementation that emits the Java PSI artifacts for a BNF file: the
 * element-type holder interface, (when {@link GenOptions#parserApi} is {@code Syntax}) a Syntax-API
 * element-type converter, PSI interfaces and impls, and the optional visitor class.
 * <p>
 * Constructed from a {@link ParserGenerator} that has just finished {@link ParserGenerator#generateParser()}.
 * The parser's accumulated simple-token map, token-choice sets, and "tokens used in grammar" set
 * are copied in; PSI-side rule metadata (interface/impl class names, mixin/stub resolution, real
 * super-classes) is computed fresh from the BNF file so this generator's output is independent of
 * the parser's internal {@link RuleInfo} representation (Kotlin parser builds its {@code RuleInfo}
 * without PSI fields).
 */
public final class JavaPsiGenerator extends Generator {
  private static final List<String> DEFAULT_PSI_IMPORTS = Arrays.asList("java.util.List", "org.jetbrains.annotations.*", JavaBnfConstants.PSI_ELEMENT_CLASS);

  private final @NotNull Set<String> myTokensUsedInGrammar;

  private final boolean myNoStubs;
  private final NameFormat myPsiInterfaceFormat;
  private final NameFormat myImplClassFormat;
  private final String myPsiImplUtilClass;
  private final String myPsiTreeUtilClass;
  private final String myVisitorClassName;
  private final String myParserTypeHolderClass;
  private final String myPsiElementTypeHolderClass;
  private final RuleMethodsHelper myRulesMethodsHelper;

  /**
   * PSI-only mutable state per rule (populated by {@link #calcRealSuperClasses}). Kept
   * separate from the immutable {@link RuleInfo} held by {@link GrammarInfo}.
   */
  private final Map<String, PsiRuleInfo> myPsiRuleInfos = new HashMap<>();

  public JavaPsiGenerator(@NotNull Generator parserGen, @NotNull JavaHelperFactory.ScopedHelpers scopedHelpers) {
    super(parserGen.grammarInfo(),
          parserGen.mySourcePath,
          parserGen.myPackagePrefix,
          "java",
          parserGen.myOpener,
          new JavaNameRenderer(),
          parserGen.myPaths,
          scopedHelpers
    );

    // TODO I don't like that we copy state here
    // Copy parser-side state populated during parser emission.
    mySimpleTokens.clear();
    mySimpleTokens.putAll(parserGen.mySimpleTokens);
    myChoiceTokenSets.putAll(parserGen.myChoiceTokenSets);
    if (parserGen instanceof ParserGenerator pg) {
      myTokensUsedInGrammar = new LinkedHashSet<>(pg.myTokensUsedInGrammar);
    }
    else if (parserGen instanceof JavaPsiGenerator pg) {
      myTokensUsedInGrammar = new LinkedHashSet<>(pg.myTokensUsedInGrammar);
      //myPsiRuleInfos.putAll(pg.myPsiRuleInfos);
    }
    else {
      throw new IllegalArgumentException("Unsupported parser generator type: " + parserGen.getClass().getName());
    }

    myPsiInterfaceFormat = NameFormat.forPsiClass(myFile);
    myImplClassFormat = NameFormat.forPsiImplClass(myFile);
    myPsiImplUtilClass = getRootAttribute(myFile, KnownAttribute.PSI_IMPL_UTIL_CLASS);
    myPsiTreeUtilClass = getRootAttribute(myFile, KnownAttribute.PSI_TREE_UTIL_CLASS);
    myVisitorClassName = inferVisitorClassName(myFile, G.generateVisitor, myPsiInterfaceFormat);
    myParserTypeHolderClass = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);
    myPsiElementTypeHolderClass = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);

    myRulesMethodsHelper = new RuleMethodsHelper(myGraphHelper, grammarInfo().expressionHelper(), mySimpleTokens, G);

    myNoStubs = JBIterable.from(myRuleInfos.values()).find(o -> o.stub() != null) == null;
  }

  private @NotNull PsiRuleInfo psiInfo(@NotNull BnfRule rule) {
    return myPsiRuleInfos.computeIfAbsent(rule.getName(), n -> new PsiRuleInfo());
  }

  /**
   * Per-method bundle. The {@link MethodSymbol.Builder} is the symbol; {@link #body} writes the
   * method's body inside the {@code { … }} (omitted for interface methods). {@link #customHeader},
   * when non-null, replaces the default {@link #renderMethodHeader} call — used by the visitor's
   * rule-side {@code visit*} methods which emit the parameter type as a short PSI name irrespective
   * of {@link GenOptions#generateFQN}. {@link #signature} is null only for "raw text"
   * entries (warning comments interleaved between methods); those entries are skipped during
   * symbol extraction in pass 1 and render only their {@link #customHeader}.
   */
  private record MethodPlan(@Nullable MethodSymbol.Builder signature,
                            @Nullable Runnable body,
                            @Nullable Runnable customHeader) {
    static @NotNull MethodPlan of(@NotNull MethodSymbol.Builder signature) {
      return new MethodPlan(signature, null, null);
    }

    static @NotNull MethodPlan ofImpl(@NotNull MethodSymbol.Builder signature, @NotNull Runnable body) {
      return new MethodPlan(signature, body, null);
    }

    static @NotNull MethodPlan ofImpl(@NotNull MethodSymbol.Builder signature,
                                      @NotNull Runnable body,
                                      @Nullable Runnable customHeader) {
      return new MethodPlan(signature, body, customHeader);
    }

    static @NotNull MethodPlan ofRaw(@NotNull Runnable emit) {
      return new MethodPlan(null, null, emit);
    }
  }

  /**
   * Per-class bundle. {@link #symbol} carries everything that goes into the on-disk stub
   * (hierarchy, modifiers, type parameters); its {@code methods} list is deliberately left empty
   * until pass 1 extracts the immutable {@link ClassSymbol} — see {@link #generate()}. The other
   * fields are emission-only: file location, the import set, and the ordered method plans the
   * renderer walks. {@link #preMethods} fires before the method loop; we don't currently use
   * {@link #postMethods} but keep it as a placeholder.
   *
   * @param supersForHeader Original super-class/interface strings with generic args preserved — needed for the {@code extends}/{@code implements} clause.
   *                        The symbol's {@code superClass}/{@code interfaces} carry the raw FQNs (for {@link JavaHelper} lookups), so this array isn't redundant.
   */
  private record ClassPlan(
    @NotNull ClassSymbol.Builder symbol,
    @NotNull String outputPath,
    @NotNull Set<String> imports,
    @NotNull String[] supersForHeader,
    @NotNull List<MethodPlan> methods,
    @Nullable Runnable preMethods,
    @Nullable Runnable postMethods
  ) {
  }

  /**
   * Builds {@link ClassPlan}s for every PSI artifact this generator is about to emit — PSI
   * interfaces and impls (with method signatures and body emitters), and the visitor (with its
   * {@code visit*} methods). The element-type holder is stubbed separately by
   * {@link #buildElementTypeHolderStub} since its emission flow (fields, not methods) is handled
   * by {@link #generateElementTypeHolder}. Must be called after {@link #inferSuperInterfaces} and
   * {@link #calcRealSuperClasses}; also calls {@link RuleMethodsHelper#buildMaps} so the method
   * iteration sees the rule's accessor set.
   */
  private @NotNull List<ClassPlan> buildPsiClassPlans(@NotNull Map<String, BnfRule> sortedPsiRules) {
    myRulesMethodsHelper.buildMaps(sortedPsiRules.values());
    List<ClassPlan> plans = new ArrayList<>();
    // Matches the historical emission order: all PSI interfaces, then all impls, then the visitor.
    for (BnfRule rule : sortedPsiRules.values()) {
      plans.add(buildIntfPlan(rule, ruleInfo(rule)));
    }
    for (BnfRule rule : sortedPsiRules.values()) {
      plans.add(buildImplPlan(rule, ruleInfo(rule)));
    }
    ClassPlan visitor = buildVisitorPlan(sortedPsiRules);
    if (visitor != null) plans.add(visitor);
    return plans;
  }

  /**
   * Element-type holder stub for {@link JavaHelperFactory.ExtraClasses} — class header only. The
   * holder's actual emission stays in {@link #generateElementTypeHolder} because it writes static
   * fields rather than methods, so it doesn't fit the {@link #renderClass} method-loop model.
   */
  private @Nullable ClassSymbol buildElementTypeHolderStub() {
    boolean needToGenerate = myGrammarRoot != null &&
                             (G.generateTokenTypes || G.generateElementTypes || G.generatePsi && G.generatePsiFactory);
    if (!needToGenerate) return null;
    ClassSymbol.Builder b = new ClassSymbol.Builder();
    b.name = Fqn.of(myPsiElementTypeHolderClass);
    b.modifiers = Modifier.PUBLIC | Modifier.INTERFACE;
    return b.build();
  }

  /** PSI interface plan: hierarchy + accessor signatures (no bodies — interface methods are abstract). */
  private @NotNull ClassPlan buildIntfPlan(@NotNull BnfRule rule, @NotNull RuleInfo info) {
    String psiClass = info.intfClass();
    Collection<String> psiSupers = resolvePsiIntfSupers(rule);

    Set<String> imports = new LinkedHashSet<>();
    imports.addAll(DEFAULT_PSI_IMPORTS);
    imports.addAll(psiSupers);
    imports.addAll(getRuleMethodTypesToImport(rule));

    ClassSymbol.Builder sym = new ClassSymbol.Builder();
    sym.name = Fqn.of(psiClass);
    sym.modifiers = Modifier.PUBLIC | Modifier.INTERFACE;
    for (String s : psiSupers) {
      sym.interfaces.add(Fqn.of(getRawClassName(s)));
    }

    List<MethodPlan> methods = new ArrayList<>();
    Set<String> visited = new TreeSet<>();
    for (RuleMethodsHelper.RuleMethodInfo rmi : myRulesMethodsHelper.getFor(rule)) {
      if (StringUtil.isEmpty(rmi.name())) continue;
      addIntfMethodPlans(methods, rule, info, rmi, visited);
    }
    String[] headerSupers = ArrayUtil.toStringArray(psiSupers);
    return new ClassPlan(sym, myPaths.pathString(KnownAttribute.PSI_OUTPUT_PATH), imports, headerSupers, methods, null, null);
  }

  /** PSI impl plan: hierarchy + constructors + (optional) accept variants + accessor / util methods, each with a body emitter. */
  private @NotNull ClassPlan buildImplPlan(@NotNull BnfRule rule, @NotNull RuleInfo info) {
    String psiClass = info.implClass();
    String superInterface = resolvePsiImplSuperInterface(rule);
    String stubName = info.realStubClass();
    String implSuper = resolvePsiImplSuperClass(rule);
    boolean mixedAST = psiInfo(rule).mixedAST;

    Set<String> imports = new LinkedHashSet<>();
    if (!G.generateFQN) {
      imports.addAll(Arrays.asList(CommonClassNames.JAVA_UTIL_LIST,
                                   "org.jetbrains.annotations.*",
                                   JavaBnfConstants.AST_NODE_CLASS,
                                   JavaBnfConstants.PSI_ELEMENT_CLASS));
      if (myVisitorClassName != null) imports.add(JavaBnfConstants.PSI_ELEMENT_VISITOR_CLASS);
      imports.add(myPsiTreeUtilClass);
    }
    else {
      imports.add("#forced");
    }
    imports.add(staticStarImport(myPsiElementTypeHolderClass));
    if (!G.generateFQN) {
      if (StringUtil.isNotEmpty(implSuper)) imports.add(implSuper);
      imports.add(StringUtil.getPackageName(superInterface) + ".*");
      imports.add(StringUtil.notNullize(myVisitorClassName));
      imports.add(StringUtil.notNullize(myPsiImplUtilClass));
      imports.addAll(getRuleMethodTypesToImport(rule));
    }

    JavaHelper mixinHelper = helperFor(KnownAttribute.MIXIN);
    List<NavigatablePsiElement> inheritedCtors = inheritedConstructors(rule, mixinHelper);

    if (!G.generateFQN) {
      for (NavigatablePsiElement m : inheritedCtors) {
        collectMethodTypesToImport(Collections.singletonList(m), false, imports);
      }
      if (stubName != null && inheritedCtors.isEmpty()) imports.add(G.fallbackStubElementType);
      if (stubName != null) imports.add(stubName);
    }
    if (!G.generateTokenTypes) {
      for (RuleMethodsHelper.RuleMethodInfo rmi : myRulesMethodsHelper.getFor(rule)) {
        if (rmi.rule() == null && !StringUtil.isEmpty(rmi.name())) {
          for (String s : getRootAttribute(myFile, KnownAttribute.PARSER_IMPORTS).asStrings()) {
            if (s.startsWith("static ")) imports.add(s);
          }
          break;
        }
      }
    }

    ClassSymbol.Builder sym = new ClassSymbol.Builder();
    sym.name = Fqn.of(psiClass);
    sym.modifiers = info.isAbstract()
                    ? Modifier.PUBLIC | Modifier.ABSTRACT
                    : Modifier.PUBLIC;
    if (StringUtil.isNotEmpty(implSuper)) {
      sym.superClass = Fqn.of(getRawClassName(implSuper));
    }
    sym.interfaces.add(Fqn.of(superInterface));

    List<MethodPlan> methods = new ArrayList<>();
    for (MethodSymbol.Builder ctor : constructorSignatures(rule, info)) {
      String args = paramNamesCsv(ctor);
      methods.add(MethodPlan.ofImpl(ctor, () -> out("super(" + args + ");")));
    }
    if (myVisitorClassName != null) {
      MethodSymbol.Builder spec = acceptSpecializedSignature(rule);
      if (superDeclares(implSuper, mixinHelper, "accept", 1, myVisitorClassName)) {
        spec.annotations.add(0, OVERRIDE_FQ);
      }
      boolean returns = G.visitorValue != null;
      String visitCall = "visitor.visit" + CommonRendererUtils.getRulePsiClassName(rule, null) + "(this);";
      methods.add(MethodPlan.ofImpl(spec, () -> out((returns ? "return " : "") + visitCall)));

      MethodSymbol.Builder gen = acceptGenericSignature();
      // PsiElement always declares accept(PsiElementVisitor); @Override stays unconditional —
      // walking the chain through JavaHelper would require interface-side lookup the JavaHelper
      // API doesn't expose.
      gen.annotations.add(0, OVERRIDE_FQ);
      methods.add(MethodPlan.ofImpl(gen, () -> {
        String shortened = shorten(myVisitorClassName);
        out("if (visitor instanceof " + shortened + ") accept((" + shortened + ")visitor);");
        out("else super.accept(visitor);");
      }));
    }

    Set<String> visited = new TreeSet<>();
    for (RuleMethodsHelper.RuleMethodInfo rmi : myRulesMethodsHelper.getFor(rule)) {
      if (StringUtil.isEmpty(rmi.name())) continue;
      addImplMethodPlans(methods, rule, info, rmi, mixedAST, visited);
    }
    String[] headerSupers = new String[]{StringUtil.notNullize(implSuper), superInterface};
    return new ClassPlan(sym, myPaths.pathString(KnownAttribute.PSI_OUTPUT_PATH), imports, headerSupers, methods, null, null);
  }

  /** Visitor plan: one rule-side {@code visit*} per rule (with a custom header), plus a super-loop pass. */
  private @Nullable ClassPlan buildVisitorPlan(@NotNull Map<String, BnfRule> sortedPsiRules) {
    if (myVisitorClassName == null || myGrammarRoot == null) return null;
    VisitorSupers vs = resolveVisitorSupers(sortedPsiRules);
    String superIntf = vs.superIntf;
    MultiMap<String, String> supers = vs.supers;

    Set<String> imports = new LinkedHashSet<>(Arrays.asList(
      "org.jetbrains.annotations.*", JavaBnfConstants.PSI_ELEMENT_VISITOR_CLASS, superIntf));
    imports.addAll(ContainerUtil.sorted(
      JBIterable.from(sortedPsiRules.values()).map(this::ruleInfo).map(o -> o.intfPackage() + ".*").toSet()));
    imports.addAll(supers.values());

    String t = G.visitorValue != null ? G.visitorValue : "void";
    String ret = G.visitorValue != null ? "return " : "";

    ClassSymbol.Builder sym = new ClassSymbol.Builder();
    sym.name = Fqn.of(myVisitorClassName);
    sym.modifiers = Modifier.PUBLIC;
    sym.superClass = Fqn.of(JavaBnfConstants.PSI_ELEMENT_VISITOR_CLASS);
    if (G.visitorValue != null) sym.typeParameters.add(G.visitorValue);

    List<MethodPlan> methods = new ArrayList<>();
    Set<String> visited = new HashSet<>();
    Set<String> all = new TreeSet<>();
    for (BnfRule rule : sortedPsiRules.values()) {
      String methodName = CommonRendererUtils.getRulePsiClassName(rule, null);
      visited.add(methodName);
      MethodSymbol.Builder sig = buildVisitMethod("visit" + methodName, t, ruleInfo(rule).intfClass());
      // collect "all" with the same logic generateVisitor used at emission time
      List<String> rawSupers = new ArrayList<>();
      boolean first = true;
      for (String top : supers.get(rule.getName())) {
        if (!first && top.equals(superIntf)) continue;
        String raw = getRawClassName(top);
        rawSupers.add(raw);
        if (first) {
          all.add(raw);
          first = false;
        }
      }
      // Rule-side visit methods always emit the short PSI name as the parameter type,
      // irrespective of G.generateFQN — matches the historical generateVisitor behavior.
      String shortPsiName = CommonRendererUtils.getRulePsiClassName(rule, myPsiInterfaceFormat);
      Runnable customHeader = () ->
        out("public %s visit%s(%s %s o) {", t, methodName, shorten(JavaBnfConstants.NOTNULL_ANNO), shortPsiName);
      Runnable body = () -> {
        boolean firstBody = true;
        for (String raw : rawSupers) {
          String text = "visit" + myPsiInterfaceFormat.strip(StringUtil.getShortName(raw)) + "(o);";
          if (firstBody) {
            out(ret + text);
            firstBody = false;
          }
          else {
            out("// " + text);
          }
        }
      };
      methods.add(MethodPlan.ofImpl(sig, body, customHeader));
    }
    all.remove(superIntf);
    for (String top : JBIterable.from(all).append(superIntf)) {
      String methodName = myPsiInterfaceFormat.strip(StringUtil.getShortName(top));
      if (visited.contains(methodName)) continue;
      MethodSymbol.Builder sig = buildVisitMethod("visit" + methodName, t, top);
      boolean fallback = methodName.equals(StringUtil.getShortName(top)) || top.equals(superIntf);
      Runnable body = () -> {
        if (!fallback) {
          out(ret + "visit" + myPsiInterfaceFormat.strip(StringUtil.getShortName(superIntf)) + "(o);");
        }
        else {
          String superPrefix = methodName.equals("Element") ? "super." : "";
          out(superPrefix + "visitElement(o);");
          if (G.visitorValue != null) out(ret + "null;");
        }
      };
      methods.add(MethodPlan.ofImpl(sig, body));
    }
    String[] headerSupers = new String[]{JavaBnfConstants.PSI_ELEMENT_VISITOR_CLASS};
    return new ClassPlan(sym, myPaths.pathString(KnownAttribute.PSI_OUTPUT_PATH), imports, headerSupers, methods, null, null);
  }

  private static final Fqn OVERRIDE_FQ = Fqn.of("java.lang.Override");

  /**
   * Annotations that are dropped when copying a signature from a referenced source method
   * into generated PSI code. {@code @SuppressWarnings} targets the original source and is
   * meaningless on a generated wrapper; {@code @Contract} describes the implementing
   * method's behavior, which a generated accessor or forwarder does not necessarily honor.
   */
  private static final Set<String> IGNORED_ANNOTATIONS_IN_PSI = Set.of(
      "java.lang.SuppressWarnings",
      "org.jetbrains.annotations.Contract"
  );

  /** Comma-joined parameter names of {@code b} — used to build the {@code super(p0, p1, …)} body of an inherited constructor. */
  private static @NotNull String paramNamesCsv(@NotNull MethodSymbol.Builder b) {
    StringBuilder sb = new StringBuilder();
    for (ParameterSymbol.Builder p : b.parameters) {
      if (!sb.isEmpty()) sb.append(", ");
      sb.append(p.name);
    }
    return sb.toString();
  }

  private void addIntfMethodPlans(@NotNull List<MethodPlan> out,
                                  @NotNull BnfRule rule,
                                  @NotNull RuleInfo info,
                                  @NotNull RuleMethodsHelper.RuleMethodInfo rmi,
                                  @NotNull Set<String> visited) {
    switch (rmi.type()) {
      case RULE, TOKEN -> out.add(MethodPlan.of(accessorSignature(rule, rmi, /*intf*/ true)));
      case USER -> {
        MethodSymbol.Builder sig = userAccessorSignature(rule, rmi, /*intf*/ true);
        if (sig != null) out.add(MethodPlan.of(sig));
      }
      case MIXIN -> addMixinMethodPlans(out, rule, info, rmi, /*intf*/ true, /*mixedAST*/ false, visited);
    }
  }

  private void addImplMethodPlans(@NotNull List<MethodPlan> out,
                                  @NotNull BnfRule rule,
                                  @NotNull RuleInfo info,
                                  @NotNull RuleMethodsHelper.RuleMethodInfo rmi,
                                  boolean mixedAST,
                                  @NotNull Set<String> visited) {
    JavaHelper mixinHelper = helperFor(KnownAttribute.MIXIN);
    switch (rmi.type()) {
      case RULE, TOKEN -> {
        MethodSymbol.Builder sig = accessorSignature(rule, rmi, /*intf*/ false);
        if (superDeclares(ruleInfo(rule).intfClass(), mixinHelper, sig.name, 0)) {
          sig.annotations.add(0, OVERRIDE_FQ);
        }
        out.add(MethodPlan.ofImpl(sig, () ->
          out("return " + generatePsiAccessorImplCall(rule, rmi, mixedAST) + ";")));
      }
      case USER -> {
        MethodSymbol.Builder sig = userAccessorSignature(rule, rmi, /*intf*/ false);
        if (sig == null) return;
        if (superDeclares(ruleInfo(rule).intfClass(), mixinHelper, sig.name, 0)) {
          sig.annotations.add(0, OVERRIDE_FQ);
        }
        out.add(MethodPlan.ofImpl(sig, () -> emitUserAccessorBody(rule, rmi, mixedAST)));
      }
      case MIXIN -> addMixinMethodPlans(out, rule, info, rmi, /*intf*/ false, mixedAST, visited);
    }
  }

  /**
   * Mixin entries — mirrors the legacy {@code generateMixinMethod}:
   * <ul>
   *   <li>{@code intf=true}: emit the mixin-class methods (intf-side) plus the {@code psiImplUtil}
   *       methods (intf-side). If neither produced anything, emit a "method not found" warning
   *       comment block inside the class body and register the same warning with
   *       {@link Generator#addWarning}.</li>
   *   <li>{@code intf=false}: emit only {@code psiImplUtil} methods (impl-side). Mixin-class
   *       methods aren't re-emitted on the impl because the impl extends the mixin and inherits
   *       them.</li>
   * </ul>
   */
  private void addMixinMethodPlans(@NotNull List<MethodPlan> out,
                                   @NotNull BnfRule rule,
                                   @NotNull RuleInfo info,
                                   @NotNull RuleMethodsHelper.RuleMethodInfo rmi,
                                   boolean intf,
                                   boolean mixedAST,
                                   @NotNull Set<String> visited) {
    JavaHelper mixinHelper = helperFor(KnownAttribute.MIXIN);
    JavaHelper psiImplUtilHelper = helperFor(KnownAttribute.PSI_IMPL_UTIL_CLASS);
    String mixinClass = getAttribute(rule, KnownAttribute.MIXIN);
    int added = 0;

    if (intf) {
      for (NavigatablePsiElement m : mixinHelper.findClassMethods(mixinClass, MethodType.INSTANCE, rmi.name(), false, -1)) {
        MethodSymbol.Builder sig = mixinSignatureFromSource(rmi.name(), m, /*isInPsiUtil*/ false, /*intf*/ true, visited);
        if (sig != null) {
          out.add(MethodPlan.of(sig));
          added++;
        }
      }
    }
    for (NavigatablePsiElement m : RuleImplUtil.findRuleImplMethods(psiImplUtilHelper, myPsiImplUtilClass, rmi.name(), rule)) {
      MethodSymbol.Builder sig = mixinSignatureFromSource(rmi.name(), m, /*isInPsiUtil*/ true, intf, visited);
      if (sig == null) continue;
      if (intf) {
        out.add(MethodPlan.of(sig));
      }
      else {
        sig.annotations.add(0, OVERRIDE_FQ);
        out.add(MethodPlan.ofImpl(sig, () -> emitUtilMethodBody(rmi.name(), m, /*isInPsiUtil*/ true)));
      }
      added++;
    }

    if (intf && added == 0) {
      String ruleClassFqn = info.intfClass();
      String implClassName = StringUtil.getShortName(String.valueOf(myPsiImplUtilClass));
      addWarning(format("%s.%s(%s, ...) method not found", implClassName, rmi.name(), StringUtil.getShortName(ruleClassFqn)));
      out.add(MethodPlan.ofRaw(() -> {
        String ruleClassName = shorten(ruleClassFqn);
        out("""
              //WARNING: %s(...) is skipped
              //matching %s(%s, ...)
              //methods are not found in %s""",
            rmi.name(), rmi.name(), ruleClassName, implClassName);
      }));
    }
  }

  /*Body emitters****************************************************************/
  // Body-only counterparts to the legacy generate* emission functions. The headers are produced
  // by renderMethodHeader; these only write the inside of the {@code { … }} block.

  /**
   * Impl-side body of a user (path-based) accessor: walks the path, accumulates the StringBuilder
   * exactly as the legacy {@code generateUserPsiAccessors} did, then emits it. The path's
   * validity is already established by {@link #userAccessorSignature}; this routine returns
   * silently on the same conditions in case the helper state changed.
   */
  private void emitUserAccessorBody(@NotNull BnfRule startRule,
                                    @NotNull RuleMethodsHelper.RuleMethodInfo ruleMethodInfo,
                                    boolean mixedAST) {
    StringBuilder sb = new StringBuilder();
    Cardinality cardinality = REQUIRED;
    String context = "";
    String[] splitPath = ruleMethodInfo.path().split("/");
    int i = -1, count = 1;
    for (Object m : resolveUserPsiPathMethods(startRule, splitPath)) {
      String pathElement = splitPath[++i];
      if (m instanceof String) continue;
      boolean last = i == splitPath.length - 1;
      int indexStart = pathElement.indexOf('[');
      int indexEnd = indexStart > 0 ? pathElement.lastIndexOf(']') : -1;
      String index = indexEnd > -1 ? pathElement.substring(indexStart + 1, indexEnd).trim() : null;

      RuleMethodsHelper.RuleMethodInfo targetInfo = (RuleMethodsHelper.RuleMethodInfo)m;
      if (targetInfo == null) return;
      if (indexStart > 0 && (indexEnd == -1 || StringUtil.isNotEmpty(pathElement.substring(indexEnd + 1)))) return;
      if (index != null && !targetInfo.cardinality().many()) return;
      if (!last && index == null && targetInfo.cardinality().many()) return;
      if (i > 0 && StringUtil.isEmpty(targetInfo.name())) return;

      boolean many = targetInfo.cardinality().many();
      String className = shorten(targetInfo.rule() == null ? JavaBnfConstants.PSI_ELEMENT_CLASS : getAccessorType(targetInfo.rule()));
      String type = (many ? shorten(CommonClassNames.JAVA_UTIL_LIST) + "<" : "") + className + (many ? "> " : " ");
      String curId = N.psiLocal + (count++);
      if (!context.isEmpty()) {
        if (cardinality.optional()) {
          sb.append("if (").append(context).append(" == null) return null;\n");
        }
        context += ".";
      }
      if (last && index == null) {
        sb.append("return ");
      }
      else {
        sb.append(type).append(curId).append(" = ");
      }
      String targetCall;
      if (StringUtil.isNotEmpty(targetInfo.name())) {
        targetCall = targetInfo.generateGetterName() + "()";
      }
      else {
        targetCall = generatePsiAccessorImplCall(startRule, targetInfo, mixedAST);
      }
      sb.append(context).append(targetCall).append(";\n");
      context = curId;
      cardinality = targetInfo.cardinality();
      if (index != null) {
        if ("first".equals(index)) index = "0";
        context += ".";
        boolean isLast = index.equals("last");
        if (isLast) index = context + "size() - 1";
        curId = N.psiLocal + (count++);
        if (last) {
          sb.append("return ");
        }
        else {
          sb.append(className).append(" ").append(curId).append(" = ");
        }
        if (cardinality != AT_LEAST_ONE || !index.equals("0")) {
          if (isLast) {
            sb.append(context).append("isEmpty()? null : ");
          }
          else {
            int val = StringUtil.parseInt(index, Integer.MAX_VALUE);
            sb.append(context).append("size()").append(val == Integer.MAX_VALUE ? " - 1 < " + index : " < " + (val + 1))
              .append(" ? null : ");
          }
        }
        sb.append(context).append("get(").append(index).append(");\n");
        context = curId;
        cardinality = cardinality == AT_LEAST_ONE && index.equals("0") ? REQUIRED : OPTIONAL;
      }
    }
    out(sb.toString());
  }

  /** Impl-side body of a mixin / {@code psiImplUtil} util method: forwards to the static helper. */
  private void emitUtilMethodBody(@NotNull String methodName,
                                  @NotNull NavigatablePsiElement source,
                                  boolean isInPsiUtil) {
    List<String> methodTypes = JavaHelper.getMethodTypes(source);
    String rawReturn = methodTypes.isEmpty() ? "void" : methodTypes.get(0);
    int offset = methodTypes.isEmpty() || isInPsiUtil && methodTypes.size() < 3 ? 0 :
                 isInPsiUtil ? 3 : 1;
    String implUtilRef = shorten(StringUtil.notNullize(myPsiImplUtilClass, KnownAttribute.PSI_IMPL_UTIL_CLASS.getName()));
    Function<Integer, List<String>> annoProvider = i -> JavaHelper.getParameterAnnotations(source, (i - 1) / 2);
    Function<String, String> substitutor = ParserGeneratorUtil::unwrapTypeArgumentForParamList;
    String args = getParametersString(methodTypes, offset, 2, substitutor, annoProvider, myShortener);
    out("%s%s.%s(this%s);",
        "void".equals(rawReturn) ? "" : "return ",
        implUtilRef, methodName,
        args.isEmpty() ? "" : ", " + args);
  }

  /*Signatures******************************************************************/
  // Per-method signature functions: pure producers of {@link MethodSymbol.Builder}. The same
  // function is consumed by stub population (above) and by emission (below) so the two never
  // drift. Annotations stored here are "stable" — they appear in both the generated source and
  // the symbol — so {@code @Override} (a generation-time decision) is never recorded here; it
  // is decided at the emission site.

  private static final String NOTNULL_FQN = StringUtil.trimStart(JavaBnfConstants.NOTNULL_ANNO, "@");
  private static final String NULLABLE_FQN = StringUtil.trimStart(JavaBnfConstants.NULLABLE_ANNO, "@");
  private static final Fqn NOTNULL_FQ = Fqn.of(NOTNULL_FQN);
  private static final Fqn NULLABLE_FQ = Fqn.of(NULLABLE_FQN);

  /** Signature of a basic rule/token accessor (no parameters; nullability follows cardinality). */
  private @NotNull MethodSymbol.Builder accessorSignature(@NotNull BnfRule rule,
                                                          @NotNull RuleMethodsHelper.RuleMethodInfo info,
                                                          boolean intf) {
    Cardinality c = info.cardinality();
    boolean many = c.many();
    boolean isToken = info.rule() == null;
    String elementType = isToken ? JavaBnfConstants.PSI_ELEMENT_CLASS : getAccessorType(info.rule());
    String returnType = many ? CommonClassNames.JAVA_UTIL_LIST + "<" + elementType + ">" : elementType;

    MethodSymbol.Builder b = new MethodSymbol.Builder();
    b.name = info.generateGetterName();
    b.methodType = MethodType.INSTANCE;
    b.modifiers = intf ? Modifier.PUBLIC | Modifier.ABSTRACT : Modifier.PUBLIC;
    b.returnType = JvmTypeRefs.raw(returnType);
    b.annotations.add(c == OPTIONAL ? NULLABLE_FQ : NOTNULL_FQ);
    return b;
  }

  /**
   * Signature of a user-defined path-based accessor. Walks the path to determine the final
   * cardinality and target rule, then derives a return-type / nullability that matches
   * {@link #generateUserPsiAccessors}. Returns {@code null} when the path is invalid — the
   * emission path reports a warning in that case; the stub silently skips it.
   */
  private @Nullable MethodSymbol.Builder userAccessorSignature(@NotNull BnfRule startRule,
                                                               @NotNull RuleMethodsHelper.RuleMethodInfo ruleMethodInfo,
                                                               boolean intf) {
    String[] splitPath = ruleMethodInfo.path().split("/");
    BnfRule targetRule = startRule;
    Cardinality cardinality = REQUIRED;
    boolean totalNullable = false;
    int i = -1;
    for (Object m : resolveUserPsiPathMethods(startRule, splitPath)) {
      String pathElement = splitPath[++i];
      if (m instanceof String) continue;
      boolean last = i == splitPath.length - 1;
      int indexStart = pathElement.indexOf('[');
      int indexEnd = indexStart > 0 ? pathElement.lastIndexOf(']') : -1;
      String index = indexEnd > -1 ? pathElement.substring(indexStart + 1, indexEnd).trim() : null;

      RuleMethodsHelper.RuleMethodInfo targetInfo = (RuleMethodsHelper.RuleMethodInfo)m;
      if (targetInfo == null) return null;
      if (indexStart > 0 && (indexEnd == -1 || StringUtil.isNotEmpty(pathElement.substring(indexEnd + 1)))) return null;
      if (index != null && !targetInfo.cardinality().many()) return null;
      if (!last && index == null && targetInfo.cardinality().many()) return null;
      if (i > 0 && StringUtil.isEmpty(targetInfo.name())) return null;

      targetRule = targetInfo.rule();
      cardinality = targetInfo.cardinality();
      totalNullable |= cardinality.optional();
      if (index != null) {
        // matches the index rewrite in {@link #generateUserPsiAccessors}: a {@code [first]}
        // selector is normalised to {@code [0]} before the cardinality narrowing fires.
        if ("first".equals(index)) index = "0";
        cardinality = cardinality == AT_LEAST_ONE && "0".equals(index) ? REQUIRED : OPTIONAL;
        totalNullable |= cardinality.optional();
      }
    }
    boolean many = cardinality.many();
    String elementType = targetRule == null ? JavaBnfConstants.PSI_ELEMENT_CLASS : getAccessorType(targetRule);
    String returnType = many ? CommonClassNames.JAVA_UTIL_LIST + "<" + elementType + ">" : elementType;

    MethodSymbol.Builder b = new MethodSymbol.Builder();
    b.name = CommonRendererUtils.getGetterName(ruleMethodInfo.name());
    b.methodType = MethodType.INSTANCE;
    b.modifiers = intf ? Modifier.PUBLIC | Modifier.ABSTRACT : Modifier.PUBLIC;
    b.returnType = JvmTypeRefs.raw(returnType);
    boolean nonNull = !cardinality.many() && cardinality == REQUIRED && !totalNullable;
    b.annotations.add(nonNull ? NOTNULL_FQ : NULLABLE_FQ);
    return b;
  }

  /**
   * Derives a {@link MethodSymbol.Builder} from an on-disk source method, mirroring
   * {@link #generateUtilMethod}'s view of it (return type, parameter list with annotations,
   * generics, exceptions). Returns {@code null} when the entry should be skipped (dedup hit, or
   * the {@code toString} guard).
   */
  private @Nullable MethodSymbol.Builder mixinSignatureFromSource(@NotNull String methodName,
                                                                  @Nullable NavigatablePsiElement method,
                                                                  boolean isInPsiUtil,
                                                                  boolean intf,
                                                                  @NotNull Set<String> visited) {
    List<String> methodTypes = method == null ? Collections.emptyList() : JavaHelper.getMethodTypes(method);
    String returnType = methodTypes.isEmpty() ? "void" : methodTypes.get(0);
    int offset = methodTypes.isEmpty() || isInPsiUtil && methodTypes.size() < 3 ? 0 :
                 isInPsiUtil ? 3 : 1;
    if (!visited.add(methodName + methodTypes.subList(offset, methodTypes.size()))) return null;
    if (intf && methodTypes.size() == offset && "toString".equals(methodName)) return null;

    MethodSymbol.Builder b = new MethodSymbol.Builder();
    b.name = methodName;
    b.methodType = MethodType.INSTANCE;
    b.modifiers = intf ? Modifier.PUBLIC | Modifier.ABSTRACT : Modifier.PUBLIC;
    b.returnType = JvmTypeRefs.raw(returnType);
    for (String s : JavaHelper.getAnnotations(method)) {
      if ("java.lang.Override".equals(s)) continue;
      if (s.startsWith("kotlin.")) continue;
      if (IGNORED_ANNOTATIONS_IN_PSI.contains(s)) continue;
      b.annotations.add(Fqn.of(s));
    }
    for (TypeParameterSymbol gp : JavaHelper.getGenericParameters(method)) {
      TypeParameterSymbol.Builder tpb = new TypeParameterSymbol.Builder(StringUtil.notNullize(gp.name()));
      tpb.extendsList.addAll(gp.extendsList());
      tpb.annotations.addAll(gp.annotations());
      b.generics.add(tpb);
    }
    for (int i = offset, n = methodTypes.size(); i < n; i += 2) {
      String type = ParserGeneratorUtil.unwrapTypeArgumentForParamList(methodTypes.get(i));
      String name = i + 1 < n ? methodTypes.get(i + 1) : "p" + ((i - offset) / 2);
      ParameterSymbol.Builder pb = new ParameterSymbol.Builder();
      pb.type = JvmTypeRefs.raw(type);
      pb.name = name;
      for (String s : JavaHelper.getParameterAnnotations(method, (i - 1) / 2)) {
        if (IGNORED_ANNOTATIONS_IN_PSI.contains(s)) continue;
        pb.annotations.add(Fqn.of(s));
      }
      b.parameters.add(pb);
    }
    for (String e : JavaHelper.getExceptionList(method)) {
      b.exceptions.add(Fqn.of(e));
    }
    return b;
  }

  /** Signature of the specialized {@code accept(MyVisitor)} method on a PSI impl. */
  private @NotNull MethodSymbol.Builder acceptSpecializedSignature(@NotNull BnfRule rule) {
    MethodSymbol.Builder b = new MethodSymbol.Builder();
    b.name = "accept";
    b.methodType = MethodType.INSTANCE;
    b.modifiers = Modifier.PUBLIC;
    b.returnType = JvmTypeRefs.raw(G.visitorValue != null ? G.visitorValue : "void");
    if (G.visitorValue != null) {
      b.generics.add(new TypeParameterSymbol.Builder(G.visitorValue));
    }
    ParameterSymbol.Builder p = new ParameterSymbol.Builder();
    p.name = "visitor";
    p.type = JvmTypeRefs.raw(myVisitorClassName + (G.visitorValue != null ? "<" + G.visitorValue + ">" : ""));
    p.annotations.add(NOTNULL_FQ);
    b.parameters.add(p);
    return b;
  }

  /** Signature of the generic {@code accept(PsiElementVisitor)} dispatch method on a PSI impl. */
  private @NotNull MethodSymbol.Builder acceptGenericSignature() {
    MethodSymbol.Builder b = new MethodSymbol.Builder();
    b.name = "accept";
    b.methodType = MethodType.INSTANCE;
    b.modifiers = Modifier.PUBLIC;
    b.returnType = JvmTypeRefs.raw("void");
    ParameterSymbol.Builder p = new ParameterSymbol.Builder();
    p.name = "visitor";
    p.type = JvmTypeRefs.raw(JavaBnfConstants.PSI_ELEMENT_VISITOR_CLASS);
    p.annotations.add(NOTNULL_FQ);
    b.parameters.add(p);
    return b;
  }

  /**
   * Constructor signatures emitted on a PSI impl — either the default {@code (ASTNode)} (and stub
   * variant) when no parent constructor list is borrowed, or one per inherited constructor from
   * the resolved top super (matches {@link #generatePsiImpl}'s constructor block).
   */
  private @NotNull List<MethodSymbol.Builder> constructorSignatures(@NotNull BnfRule rule, @NotNull RuleInfo info) {
    String implFqn = info.implClass();
    String stubName = info.realStubClass();
    String shortName = StringUtil.getShortName(implFqn);
    JavaHelper mixinHelper = helperFor(KnownAttribute.MIXIN);
    List<NavigatablePsiElement> constructors = inheritedConstructors(rule, mixinHelper);

    List<MethodSymbol.Builder> out = new ArrayList<>();
    if (constructors.isEmpty()) {
      out.add(astNodeConstructorSignature(shortName));
      if (stubName != null) {
        out.add(stubConstructorSignature(shortName, stubName));
      }
    }
    else {
      Function<String, String> substitutor = stubName == null ? ParserGeneratorUtil::unwrapTypeArgumentForParamList : o -> {
        String oo = unwrapTypeArgumentForParamList(o);
        if (oo.equals(o)) return o;
        int idx = oo.lastIndexOf(" ");
        return idx == -1 ? stubName : oo.substring(0, idx) + " " + stubName;
      };
      for (NavigatablePsiElement m : constructors) {
        out.add(inheritedConstructorSignature(shortName, m, substitutor));
      }
    }
    return out;
  }

  /** Find the inherited-constructor source method that {@link #generatePsiImpl} would borrow from. */
  private @NotNull List<NavigatablePsiElement> inheritedConstructors(@NotNull BnfRule rule, @NotNull JavaHelper mixinHelper) {
    Set<BnfRule> visited = new HashSet<>();
    List<NavigatablePsiElement> constructors = Collections.emptyList();
    BnfRule topSuperRule = null;
    for (BnfRule next = rule; next != null && next != topSuperRule; ) {
      if (!visited.add(next)) break;
      topSuperRule = next;
      String superClass = psiInfo(next).realSuperClass;
      if (superClass == null) continue;
      next = getEffectiveSuperRule(myFile, next);
      if (next != null && next != topSuperRule && getAttribute(topSuperRule, KnownAttribute.MIXIN) == null) continue;
      constructors = mixinHelper.findClassMethods(getRawClassName(superClass), MethodType.CONSTRUCTOR, "*", false, -1);
      if (!constructors.isEmpty()) break;
    }
    return constructors;
  }

  private @NotNull MethodSymbol.Builder astNodeConstructorSignature(@NotNull String shortName) {
    MethodSymbol.Builder b = new MethodSymbol.Builder();
    b.name = shortName;
    b.methodType = MethodType.CONSTRUCTOR;
    b.modifiers = Modifier.PUBLIC;
    b.returnType = JvmTypeRefs.raw("void");
    ParameterSymbol.Builder p = new ParameterSymbol.Builder();
    p.name = "node";
    p.type = JvmTypeRefs.raw(JavaBnfConstants.AST_NODE_CLASS);
    b.parameters.add(p);
    return b;
  }

  private @NotNull MethodSymbol.Builder stubConstructorSignature(@NotNull String shortName, @NotNull String stubName) {
    MethodSymbol.Builder b = new MethodSymbol.Builder();
    b.name = shortName;
    b.methodType = MethodType.CONSTRUCTOR;
    b.modifiers = Modifier.PUBLIC;
    b.returnType = JvmTypeRefs.raw("void");
    ParameterSymbol.Builder pStub = new ParameterSymbol.Builder();
    pStub.name = "stub";
    pStub.type = JvmTypeRefs.raw(stubName);
    b.parameters.add(pStub);
    ParameterSymbol.Builder pType = new ParameterSymbol.Builder();
    pType.name = "stubType";
    pType.type = JvmTypeRefs.raw(G.fallbackStubElementType);
    b.parameters.add(pType);
    return b;
  }

  private @NotNull MethodSymbol.Builder inheritedConstructorSignature(@NotNull String shortName,
                                                                     @NotNull NavigatablePsiElement source,
                                                                     @NotNull Function<String, String> substitutor) {
    List<String> types = JavaHelper.getMethodTypes(source);
    MethodSymbol.Builder b = new MethodSymbol.Builder();
    b.name = shortName;
    b.methodType = MethodType.CONSTRUCTOR;
    b.modifiers = Modifier.PUBLIC;
    b.returnType = JvmTypeRefs.raw("void");
    for (int i = 1, n = types.size(); i + 1 < n; i += 2) {
      ParameterSymbol.Builder pb = new ParameterSymbol.Builder();
      pb.type = JvmTypeRefs.raw(substitutor.apply(types.get(i)));
      String name = types.get(i + 1);
      // Name overrides used by the legacy {@link ParserGeneratorUtil#getParametersString} formatter
      // so generated identifiers don't depend on whether the source-method names were preserved.
      String rawType = getRawClassName(JvmTypeRefs.renderPlain(pb.type));
      if (rawType.endsWith(JavaBnfConstants.AST_NODE_CLASS)) name = "node";
      else if (rawType.endsWith("ElementType")) name = "type";
      else if (rawType.endsWith("Stub")) name = "stub";
      pb.name = name;
      for (String s : JavaHelper.getParameterAnnotations(source, (i - 1) / 2)) {
        if (IGNORED_ANNOTATIONS_IN_PSI.contains(s)) continue;
        pb.annotations.add(Fqn.of(s));
      }
      b.parameters.add(pb);
    }
    return b;
  }

  /**
   * Filters {@code sortedPsiRules}' super-interfaces to public-only (replacing non-public ones
   * with {@code superIntf}), mirroring the loop at the top of {@link #generateVisitor}. Both the
   * visitor stub and the visitor emission consume this result.
   */
  private @NotNull VisitorSupers resolveVisitorSupers(@NotNull Map<String, BnfRule> sortedRules) {
    String superIntf = ObjectUtils.notNull(ContainerUtil.getFirstItem(getRootAttribute(myFile, KnownAttribute.IMPLEMENTS)),
                                           KnownAttribute.IMPLEMENTS.getDefaultValue().get(0)).second;
    MultiMap<String, String> supers = new MultiMap<>();
    for (BnfRule rule : sortedRules.values()) {
      supers.putValues(rule.getName(), getSuperInterfaceNames(myFile, rule, myPsiInterfaceFormat));
    }
    Map<String, String> replacements = new HashMap<>();
    Set<String> seen = new HashSet<>();
    JavaHelper implementsHelper = helperFor(KnownAttribute.IMPLEMENTS);
    for (String s : supers.values()) {
      if (!seen.add(s)) continue;
      NavigatablePsiElement aClass = implementsHelper.findClass(s);
      if (aClass != null && !JavaHelper.isPublic(aClass)) {
        replacements.put(s, superIntf);
      }
    }
    for (String key : supers.keySet()) {
      for (ListIterator<String> it = ((List<String>)supers.get(key)).listIterator(); it.hasNext(); ) {
        String s = replacements.get(it.next());
        if (s != null) {
          if (s.isEmpty()) it.remove();
          else it.set(s);
        }
      }
    }
    return new VisitorSupers(superIntf, supers);
  }

  private record VisitorSupers(@NotNull String superIntf, @NotNull MultiMap<String, String> supers) {}

  /**
   * Visitor visit-method signatures: one per rule (parameter type = rule's PSI interface) plus one
   * per super-interface gathered across all rules (excluding rule-name collisions). Order matches
   * {@link #generateVisitor}'s emission order. Each builder's {@code declaringClass} is left
   * unset; callers (the stub or the emitter) bind it as needed.
   */
  private @NotNull List<MethodSymbol.Builder> visitorMethodSignatures(@NotNull Map<String, BnfRule> sortedRules) {
    VisitorSupers vs = resolveVisitorSupers(sortedRules);
    List<MethodSymbol.Builder> out = new ArrayList<>();
    Set<String> visited = new HashSet<>();
    Set<String> all = new TreeSet<>();
    String t = G.visitorValue != null ? G.visitorValue : "void";
    for (BnfRule rule : sortedRules.values()) {
      String methodName = CommonRendererUtils.getRulePsiClassName(rule, null);
      visited.add(methodName);
      out.add(buildVisitMethod("visit" + methodName, t, ruleInfo(rule).intfClass()));
      boolean first = true;
      for (String top : vs.supers.get(rule.getName())) {
        if (first) {
          all.add(getRawClassName(top));
          first = false;
        }
      }
    }
    all.remove(vs.superIntf);
    for (String top : JBIterable.from(all).append(vs.superIntf)) {
      String methodName = myPsiInterfaceFormat.strip(StringUtil.getShortName(top));
      if (visited.contains(methodName)) continue;
      out.add(buildVisitMethod("visit" + methodName, t, top));
    }
    return out;
  }

  private @NotNull MethodSymbol.Builder buildVisitMethod(@NotNull String name,
                                                         @NotNull String returnType,
                                                         @NotNull String paramFqn) {
    MethodSymbol.Builder b = new MethodSymbol.Builder();
    b.name = name;
    b.methodType = MethodType.INSTANCE;
    b.modifiers = Modifier.PUBLIC;
    b.returnType = JvmTypeRefs.raw(returnType);
    ParameterSymbol.Builder p = new ParameterSymbol.Builder();
    p.name = "o";
    p.type = JvmTypeRefs.raw(paramFqn);
    p.annotations.add(NOTNULL_FQ);
    b.parameters.add(p);
    return b;
  }

  /**
   * Renders a method header (annotations, modifier, generics, return type, name, parameters,
   * throws clause) from a {@link MethodSymbol.Builder}, matching the existing emission format
   * exactly. For interfaces emits a {@code ;} terminator; for impls emits a {@code  {} opener so
   * the caller can write the body and {@code "}"} to close.
   */
  private void renderMethodHeader(@NotNull MethodSymbol.Builder b, boolean intf) {
    String shortReturnType = b.methodType == MethodType.CONSTRUCTOR
                             ? ""
                             : shorten(JvmTypeRefs.renderAnnotated(b.returnType));
    // IDEA-384557: a method-level annotation already embedded in the return type text (e.g.
    // {@code java.lang.@NotNull String}) must not be emitted again on its own line.
    String topLevelType = shortReturnType;
    int angleIdx = NameShortener.indexOfUnquotedAngleBracket(topLevelType);
    if (angleIdx >= 0) topLevelType = topLevelType.substring(0, angleIdx);
    for (Fqn anno : b.annotations) {
      String shortAnno = shorten(anno.value());
      if (b.methodType != MethodType.CONSTRUCTOR && topLevelType.contains("@" + shortAnno + " ")) continue;
      out("@" + shortAnno);
    }
    StringBuilder line = new StringBuilder();
    if (!intf) line.append("public ");
    if (!b.generics.isEmpty()) {
      line.append('<');
      for (int i = 0; i < b.generics.size(); i++) {
        if (i > 0) line.append(", ");
        TypeParameterSymbol.Builder t = b.generics.get(i);
        for (Fqn anno : t.annotations) {
          line.append('@').append(shorten(anno.value())).append(' ');
        }
        line.append(t.name);
        if (!t.extendsList.isEmpty()) {
          line.append(" extends ");
          for (int j = 0; j < t.extendsList.size(); j++) {
            if (j > 0) line.append(" & ");
            line.append(shorten(JvmTypeRefs.renderAnnotated(t.extendsList.get(j))));
          }
        }
      }
      line.append("> ");
    }
    if (b.methodType != MethodType.CONSTRUCTOR) {
      line.append(shortReturnType).append(' ');
    }
    line.append(b.name).append('(');
    for (int i = 0; i < b.parameters.size(); i++) {
      if (i > 0) line.append(", ");
      ParameterSymbol.Builder p = b.parameters.get(i);
      for (Fqn anno : p.annotations) {
        line.append('@').append(shorten(anno.value())).append(' ');
      }
      String pt = JvmTypeRefs.renderAnnotated(p.type);
      line.append(shorten(pt));
      if (StringUtil.isNotEmpty(p.name)) line.append(' ').append(p.name);
    }
    line.append(')');
    if (!b.exceptions.isEmpty()) {
      line.append(" throws ");
      for (int i = 0; i < b.exceptions.size(); i++) {
        if (i > 0) line.append(", ");
        line.append(shorten(b.exceptions.get(i).value()));
      }
    }
    line.append(intf ? ";" : " {");
    out(line.toString());
  }

  /**
   * Walks {@code startClass} up the super-class chain (via {@link JavaHelper#getSuperClassName})
   * looking for a method matching {@code methodName}/{@code paramCount}/{@code paramTypes}. Used
   * to decide {@code @Override} emission once stubs carry method signatures for generated PSI
   * classes. Filters {@link JavaHelper#findClassMethods} results down to entries whose actual
   * parameter raw class names share the short name of the corresponding {@code paramTypes} entry —
   * {@code findClassMethods} would otherwise match supertype parameters (e.g.
   * {@code accept(PsiElementVisitor)} when probing for {@code accept(MyVisitor)}), which is the
   * wrong contract for {@code @Override}.
   */
  private static boolean superDeclares(@Nullable String startClass,
                                       @NotNull JavaHelper helper,
                                       @NotNull String methodName,
                                       int paramCount,
                                       @NotNull String... paramTypes) {
    for (String cur = startClass; cur != null; cur = helper.getSuperClassName(cur)) {
      for (NavigatablePsiElement m : helper.findClassMethods(cur, MethodType.INSTANCE, methodName, true, paramCount, paramTypes)) {
        List<String> types = JavaHelper.getMethodTypes(m);
        boolean exact = true;
        for (int i = 0; i < paramTypes.length; i++) {
          String actualRaw = getRawClassName(types.get(1 + 2 * i));
          if (!StringUtil.getShortName(actualRaw).equals(StringUtil.getShortName(paramTypes[i]))) {
            exact = false;
            break;
          }
        }
        if (exact) return true;
      }
    }
    return false;
  }

  private static @Nullable String inferVisitorClassName(@NotNull BnfFile file, boolean generateVisitor, @NotNull NameFormat format) {
    if (!generateVisitor) {
      return null;
    }

    String specifiedName = getRootAttribute(file, KnownAttribute.PSI_VISITOR_NAME);
    if (StringUtil.isEmpty(specifiedName)) {
      return null;
    }

    // TODO this seems to be incorrect when FQN is specified
    String nameWithPrefix = specifiedName.equals(format.strip(specifiedName))
                            ? format.apply("") + specifiedName
                            : specifiedName;

    if (nameWithPrefix.equals(StringUtil.getShortName(nameWithPrefix))) {
      return getRootAttribute(file, KnownAttribute.PSI_PACKAGE) + "." + nameWithPrefix;
    }
    else {
      return nameWithPrefix;
    }
  }

  /**
   * Computes each PSI rule's {@code realSuperClass} (used for the {@code extends} clause of the
   * generated impl) and {@code mixedAST} flag, walking the rule super-chain in post-order so a
   * super's resolved values are visible to its descendants. Honors {@code mixin} overrides and
   * substitutes the resolved stub type into stub-parameterized base classes.
   */
  private void calcRealSuperClasses(@NotNull Map<String, BnfRule> sortedPsiRules) {
    if (!G.generatePsi) return;
    Map<BnfRule, BnfRule> supers = new HashMap<>();
    for (BnfRule rule : sortedPsiRules.values()) {
      supers.put(rule, getEffectiveSuperRule(myFile, rule));
    }
    JBTreeTraverser<BnfRule> ordered = new JBTreeTraverser<BnfRule>(key -> JBIterable.of(supers.get(key)))
      .withRoots(sortedPsiRules.values())
      .withTraversal(TreeTraversal.POST_ORDER_DFS)
      .unique();
    for (BnfRule rule : ordered) {
      RuleInfo info = ruleInfo(rule);
      PsiRuleInfo psi = psiInfo(rule);
      BnfRule topSuper = supers.get(rule);
      RuleInfo topInfo = topSuper == null || topSuper == rule ? null : ruleInfo(topSuper);
      PsiRuleInfo topPsi = topSuper == null || topSuper == rule ? null : psiInfo(topSuper);
      String superRuleClass = topSuper == null ? getRootAttribute(myFile, KnownAttribute.EXTENDS) :
                              topSuper == rule ? getAttribute(rule, KnownAttribute.EXTENDS) :
                              topInfo.implClass();
      String stubName = info.realStubClass();
      String adjustedSuperRuleClass =
        StringUtil.isEmpty(stubName) ? superRuleClass :
        JavaBnfConstants.AST_WRAPPER_PSI_ELEMENT_CLASS.equals(superRuleClass) ? JavaBnfConstants.STUB_BASED_PSI_ELEMENT_BASE + "<" + stubName + ">" :
        superRuleClass.contains("?") ? superRuleClass.replaceAll("\\?", stubName) : superRuleClass;
      // mixin attribute overrides "extends":
      psi.realSuperClass = StringUtil.notNullize(info.mixin(), adjustedSuperRuleClass);
      JavaHelper hierarchyHelper = helperFor(KnownAttribute.MIXIN);
      psi.mixedAST = topPsi != null ? topPsi.mixedAST : JBIterable.of(superRuleClass, psi.realSuperClass)
        .map(JavaNames::getRawClassName)
        .flatMap(s -> JBTreeTraverser.<String>from(o -> JBIterable.of(hierarchyHelper.getSuperClassName(o))).withRoot(s).unique())
        .find(JavaBnfConstants.COMPOSITE_PSI_ELEMENT_CLASS::equals) != null;
    }
  }

  /**
   * Emits everything PSI-side: the element-type holder, an optional Syntax-API element type
   * converter, and (when {@link GenOptions#generatePsi}) PSI interfaces, impls, and visitor.
   */
  @Override
  public void generate() throws IOException {
    PsiGenerationTargets psiGenerationTargets = prepareGeneration();

    // Pass 1: build class plans, materialise symbols inline, surface to ExtraClasses, discard
    // plans. Pass-1 plans live only for the duration of this method — `secondRun` rebuilds them.
    List<ClassSymbol> stubs = new ArrayList<>();
    if (G.generatePsi) {
      for (ClassPlan plan : buildPsiClassPlans(psiGenerationTargets.sortedPsiRules())) {
        Fqn declaring = plan.symbol.name;
        for (MethodPlan m : plan.methods) {
          if (m.signature == null) continue;
          m.signature.declaringClass = declaring;
          plan.symbol.methods.add(m.signature);
        }
        stubs.add(plan.symbol.build());
      }
    }
    ClassSymbol holder = buildElementTypeHolderStub();
    if (holder != null) stubs.add(holder);
    JavaHelperFactory.ExtraClasses extras = new JavaHelperFactory.ExtraClasses(stubs);

    var secondRun = new JavaPsiGenerator(this, JavaHelperFactory.getInstance(myFile.getProject()).scoped(myPaths, extras));
    secondRun.secondRun();
  }

  private @NotNull PsiGenerationTargets prepareGeneration() {
    PsiGenerationTargets psiGenerationTargets = computePsiGenerationTargets();
    inferSuperInterfaces(psiGenerationTargets.sortedPsiRules());
    calcRealSuperClasses(psiGenerationTargets.sortedPsiRules());
    return psiGenerationTargets;
  }

  private void secondRun() throws IOException {
    PsiGenerationTargets psiGenerationTargets = prepareGeneration();

    generateElementTypeHolder(psiGenerationTargets.sortedCompositeTypes());
    generateElementTypeConverter(psiGenerationTargets.sortedCompositeTypes());
    if (G.generatePsi) {
      checkClassAvailability(myPsiImplUtilClass, KnownAttribute.PSI_IMPL_UTIL_CLASS);
      for (ClassPlan plan : buildPsiClassPlans(psiGenerationTargets.sortedPsiRules())) {
        renderClass(plan);
      }
    }
  }

  /**
   * Generic renderer: open output, emit class header, run optional pre-method block, walk method
   * plans (each producing its own header via {@link #renderMethodHeader} or its custom emitter
   * and body), run optional post-method block, close the class.
   */
  private void renderClass(@NotNull ClassPlan plan) throws IOException {
    ClassSymbol.Builder sym = plan.symbol;
    boolean isInterface = (sym.modifiers & Modifier.INTERFACE) != 0;
    TypeKind kind = isInterface ? TypeKind.INTERFACE
                  : (sym.modifiers & Modifier.ABSTRACT) != 0 ? TypeKind.ABSTRACT_CLASS
                  : TypeKind.CLASS;
    String classNameWithGenerics = sym.name.value() +
                                   (sym.typeParameters.isEmpty()
                                    ? ""
                                    : "<" + String.join(", ", sym.typeParameters) + ">");
    openOutput(sym.name.value(), plan.outputPath);
    try {
      generateClassHeader(classNameWithGenerics, plan.imports, "", kind, plan.supersForHeader);
      if (plan.preMethods != null) plan.preMethods.run();
      for (MethodPlan m : plan.methods) {
        if (m.signature == null) {
          if (m.customHeader != null) m.customHeader.run();
          newLine();
          continue;
        }
        if (m.customHeader != null) m.customHeader.run();
        else renderMethodHeader(m.signature, isInterface);
        if (m.body != null) {
          m.body.run();
          out("}");
        }
        newLine();
      }
      if (plan.postMethods != null) plan.postMethods.run();
      out("}");
    }
    finally {
      closeOutput();
    }
  }

  private void generateElementTypeConverter(@NotNull Map<String, BnfRule> compositeTypes) throws IOException {
    if (G.parserApi != GenOptions.ParserApi.Syntax) return;

    var converterClass = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_CLASS);
    openOutput(converterClass, myPaths.pathString(KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH));
    try {
      generateElementTypesConverter(converterClass,
                                    myParserTypeHolderClass,
                                    getRootAttribute(myFile, KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_CLASS),
                                    compositeTypes);
    }
    finally {
      closeOutput();
    }
  }

  private void generateElementTypeHolder(@NotNull Map<String, BnfRule> compositeTypes) throws IOException {
    boolean needToGenerate = myGrammarRoot != null &&
                             (G.generateTokenTypes || G.generateElementTypes || G.generatePsi && G.generatePsiFactory);

    if (!needToGenerate) {
      return;
    }

    openOutput(myPsiElementTypeHolderClass, myPaths.pathString(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH));
    try {
      generateElementTypesHolder(myPsiElementTypeHolderClass,
                                 compositeTypes,
                                 getRootAttribute(myFile, KnownAttribute.TOKEN_TYPE_FACTORY),
                                 G.generatePsi);
    }
    finally {
      closeOutput();
    }
  }

  private void inferSuperInterfaces(@NotNull Map<String, BnfRule> sortedPsiRules) {
    for (BnfRule rule : sortedPsiRules.values()) {
      psiInfo(rule).superInterfaces = new LinkedHashSet<>(getSuperInterfaceNames(myFile, rule, myPsiInterfaceFormat));
    }
  }

  private @NotNull PsiGenerationTargets computePsiGenerationTargets() {
    Map<String, BnfRule> sortedCompositeTypes = new TreeMap<>();
    Map<String, BnfRule> sortedPsiRules = new TreeMap<>();

    for (BnfRule rule : myFile.getRules()) {
      RuleInfo info = ruleInfo(rule);
      if (info.intfPackage() == null) continue;
      String elementType = info.elementType();
      if (StringUtil.isEmpty(elementType)) continue;
      if (sortedCompositeTypes.containsKey(elementType)) continue;
      if (!info.isFake() || info.isInElementType()) {
        sortedCompositeTypes.put(elementType, rule);
      }
      sortedPsiRules.put(rule.getName(), rule);
    }
    return new PsiGenerationTargets(sortedCompositeTypes, sortedPsiRules);
  }

  private record PsiGenerationTargets(
    Map<String, BnfRule> sortedCompositeTypes,
    Map<String, BnfRule> sortedPsiRules
  ) {
  }

  /** Warns when {@code className} is configured but not resolvable on the classpath. */
  private void checkClassAvailability(@Nullable String className, @NotNull KnownAttribute<?> attribute) {
    if (StringUtil.isEmpty(className)) return;
    if (helperFor(attribute).findClass(className) == null) {
      String tail = StringUtil.isEmpty("PSI method signatures will not be detected") ? "" : " (PSI method signatures will not be detected)";
      addWarning(className + " class not found" + tail);
    }
  }

  /**
   * Emits the file header, package declaration, imports, optional annotations and the class/
   * interface declaration line for {@code className}. Installs a fresh {@link JavaNameShortener}
   * so subsequent {@link #shorten(String)} calls produce import-aware short names.
   */
  private void generateClassHeader(String className, Set<String> imports, String annos, TypeKind typeKind, String... supers) {
    generateFileHeader(className);
    String packageName = StringUtil.getPackageName(className);
    String shortClassName = StringUtil.getShortName(className);
    out("package %s;", packageName);
    newLine();
    JavaNameShortener shortener = new JavaNameShortener(packageName, !G.generateFQN);
    Set<String> includedClasses = collectClasses(imports, packageName);
    shortener.addImports(imports, includedClasses);
    for (String s : shortener.getImports()) {
      out("import %s;", s);
    }
    if (G.generateFQN && imports.contains("#forced")) {
      for (String s : JBIterable.from(imports).filter(o -> !"#forced".equals(o))) {
        out("import %s;", s);
      }
    }
    newLine();
    StringBuilder sb = new StringBuilder();
    for (int i = 0, supersLength = supers.length; i < supersLength; i++) {
      String aSuper = supers[i];
      if (StringUtil.isEmpty(aSuper)) continue;
      if (imports.contains(aSuper + ";")) {
        aSuper = StringUtil.getShortName(aSuper);
      }
      if (i == 0) {
        sb.append(" extends ").append(shortener.shorten(aSuper));
      }
      else if (typeKind != TypeKind.INTERFACE && i == 1) {
        sb.append(" implements ").append(shortener.shorten(aSuper));
      }
      else {
        sb.append(", ").append(shortener.shorten(aSuper));
      }
    }
    if (StringUtil.isNotEmpty(annos)) {
      out(shortener.shorten(annos));
    }
    out("public %s %s%s {", Case.LOWER.apply(typeKind.name()).replace('_', ' '), shortClassName, sb.toString());
    newLine();
    myShortener = shortener;
  }

  /*ElementTypes******************************************************************/

  /**
   * Emits the element-type holder interface: composite element-type fields, token-type fields,
   * the {@link #generateTokenSets() token sets} block, and (when generating PSI) optional
   * {@code Classes} (element-type → impl class) and {@code Factory} (element-type → impl
   * instantiation) inner classes.
   */
  private void generateElementTypesHolder(String className,
                                          Map<String, BnfRule> sortedCompositeTypes,
                                          String tokenTypeFactory,
                                          boolean generatePsi) {
    String tokenTypeClass = getRootAttribute(myFile, KnownAttribute.TOKEN_TYPE_CLASS);
    Set<String> imports = new LinkedHashSet<>();
    imports.add(JavaBnfConstants.IELEMENTTYPE_CLASS);
    if (generatePsi) {
      imports.add(JavaBnfConstants.PSI_ELEMENT_CLASS);
      imports.add(JavaBnfConstants.AST_NODE_CLASS);
    }
    if (G.generateTokenSets && !myChoiceTokenSets.isEmpty()) {
      imports.add(JavaBnfConstants.TOKEN_SET_CLASS);
    }
    boolean useExactElements = "all".equals(G.generateExactTypes) || G.generateExactTypes.contains("elements");
    boolean useExactTokens = "all".equals(G.generateExactTypes) || G.generateExactTypes.contains("tokens");

    Map<String, Trinity<String, String, RuleInfo>> compositeToClassAndFactoryMap = new HashMap<>();
    for (String elementType : sortedCompositeTypes.keySet()) {
      BnfRule rule = sortedCompositeTypes.get(elementType);
      RuleInfo ruleInfo = ruleInfo(rule);
      String elementTypeClass = getAttribute(rule, KnownAttribute.ELEMENT_TYPE_CLASS);
      String elementTypeFactory = getAttribute(rule, KnownAttribute.ELEMENT_TYPE_FACTORY);
      compositeToClassAndFactoryMap.put(elementType, Trinity.create(elementTypeClass, elementTypeFactory, ruleInfo));
      if (elementTypeFactory != null) {
        imports.add(StringUtil.getPackageName(elementTypeFactory));
      }
      else {
        ContainerUtil.addIfNotNull(imports, elementTypeClass);
      }
    }
    if (tokenTypeFactory != null) {
      imports.add(StringUtil.getPackageName(tokenTypeFactory));
    }
    else {
      ContainerUtil.addIfNotNull(imports, tokenTypeClass);
    }
    if (generatePsi) {
      imports.addAll(ContainerUtil.sorted(
        JBIterable.from(sortedCompositeTypes.values()).map(this::ruleInfo).map(o -> o.implPackage() + ".*").toSet()));
      if (G.generatePsiClassesMap) {
        imports.add(CommonClassNames.JAVA_UTIL_COLLECTIONS);
        imports.add(CommonClassNames.JAVA_UTIL_SET);
        imports.add("java.util.LinkedHashMap");
      }
      if (G.generatePsiFactory) {
        if (JBIterable.from(myPsiRuleInfos.values()).find(p -> p.mixedAST) != null) {
          imports.add(JavaBnfConstants.COMPOSITE_PSI_ELEMENT_CLASS);
        }
      }
    }
    generateClassHeader(className, imports, "", TypeKind.INTERFACE);
    if (G.generateElementTypes) {
      for (String elementType : sortedCompositeTypes.keySet()) {
        Trinity<String, String, RuleInfo> info = compositeToClassAndFactoryMap.get(elementType);
        String elementCreateCall;
        if (info.second == null) {
          elementCreateCall = "new " + shorten(info.first);
        }
        else {
          elementCreateCall = shorten(StringUtil.getPackageName(info.second)) + "." + StringUtil.getShortName(info.second);
        }
        String fieldType = useExactElements && info.first != null ? info.first : JavaBnfConstants.IELEMENTTYPE_CLASS;
        String callFix = elementCreateCall.endsWith("IElementType") ? ", null" : "";
        out("%s %s = %s(\"%s\"%s);", shorten(fieldType), elementType, elementCreateCall, elementType, callFix);
      }
    }
    if (G.generateTokenTypes) {
      newLine();
      String exactType = null;
      Map<String, String> sortedTokens = new TreeMap<>();
      String tokenCreateCall;
      if (tokenTypeFactory == null) {
        exactType = tokenTypeClass;
        tokenCreateCall = "new " + shorten(exactType);
      }
      else {
        tokenCreateCall = shorten(StringUtil.getPackageName(tokenTypeFactory)) + "." + StringUtil.getShortName(tokenTypeFactory);
      }
      String fieldType = ObjectUtils.notNull(useExactTokens ? exactType : null, JavaBnfConstants.IELEMENTTYPE_CLASS);
      for (String tokenText : mySimpleTokens.keySet()) {
        String tokenName = ObjectUtils.chooseNotNull(mySimpleTokens.get(tokenText), tokenText);
        if (isIgnoredWhitespaceToken(tokenName, tokenText)) continue;
        sortedTokens.put(getElementType(tokenName), isRegexpToken(tokenText) ? tokenName : tokenText);
      }
      for (String tokenType : sortedTokens.keySet()) {
        String callFix = tokenCreateCall.endsWith("IElementType") ? ", null" : "";
        String tokenString = sortedTokens.get(tokenType);
        out("%s %s = %s(\"%s\"%s);", shorten(fieldType), tokenType, tokenCreateCall, StringUtil.escapeStringCharacters(tokenString),
            callFix);
      }
      generateTokenSets();
    }
    if (generatePsi && G.generatePsiClassesMap) {
      String shortJC = shorten(CommonClassNames.JAVA_LANG_CLASS);
      String shortET = shorten(JavaBnfConstants.IELEMENTTYPE_CLASS);
      newLine();
      out("class Classes {");
      newLine();
      out("public static %s<?> findClass(%s elementType) {", shortJC, shortET);
      out("return ourMap.get(elementType);");
      out("}");
      newLine();
      out("public static %s<%s> elementTypes() {", shorten(CommonClassNames.JAVA_UTIL_SET), shortET);
      out("return %s.unmodifiableSet(ourMap.keySet());", shorten(CommonClassNames.JAVA_UTIL_COLLECTIONS));
      out("}");
      newLine();
      String type = shorten("java.util.LinkedHashMap<" + JavaBnfConstants.IELEMENTTYPE_CLASS + ", java.lang.Class<?>>");
      out("private static final %s ourMap = new %1$s();", type);
      newLine();
      out("static {");
      for (String elementType : sortedCompositeTypes.keySet()) {
        BnfRule rule = sortedCompositeTypes.get(elementType);
        RuleInfo info = ruleInfo(rule);
        if (info.isAbstract()) continue;
        String psiClass = CommonRendererUtils.getRulePsiClassName(rule, myImplClassFormat);
        out("ourMap.put(" + elementType + ", " + psiClass + ".class);");
      }
      out("}");
      out("}");
    }
    if (generatePsi && G.generatePsiFactory) {
      newLine();
      boolean first1;
      boolean first2;
      out("class Factory {");
      first1 = true;
      for (String elementType : sortedCompositeTypes.keySet()) {
        BnfRule rule = sortedCompositeTypes.get(elementType);
        RuleInfo info = ruleInfo(rule);
        if (info.isAbstract()) continue;
        if (psiInfo(rule).mixedAST) continue;
        if (first1) {
          out("public static %s createElement(%s node) {", shorten(JavaBnfConstants.PSI_ELEMENT_CLASS),
              shorten(JavaBnfConstants.AST_NODE_CLASS));
          out("%s type = node.getElementType();", shorten(JavaBnfConstants.IELEMENTTYPE_CLASS));
        }
        String psiClass = getAttribute(rule, KnownAttribute.PSI_IMPL_PACKAGE) + "." + CommonRendererUtils.getRulePsiClassName(rule, myImplClassFormat);
        out((!first1 ? "else " : "") + "if (type == " + elementType + ") {");
        out("return new " + shorten(psiClass) + "(node);");
        first1 = false;
        out("}");
      }
      if (!first1) {
        out("throw new AssertionError(\"Unknown element type: \" + type);");
        out("}");
      }
      first2 = true;
      for (String elementType : sortedCompositeTypes.keySet()) {
        BnfRule rule = sortedCompositeTypes.get(elementType);
        RuleInfo info = ruleInfo(rule);
        if (info.isAbstract()) continue;
        if (!psiInfo(rule).mixedAST) continue;
        if (first2) {
          if (!first1) newLine();
          out("public static %s createElement(%s type) {", shorten(JavaBnfConstants.COMPOSITE_PSI_ELEMENT_CLASS),
              shorten(JavaBnfConstants.IELEMENTTYPE_CLASS));
        }
        String psiClass = CommonRendererUtils.getRulePsiClassName(rule, myImplClassFormat);
        out((!first2 ? "else" : "") + " if (type == " + elementType + ") {");
        out("return new " + psiClass + "(type);");
        first2 = false;
        out("}");
      }
      if (!first2) {
        out("throw new AssertionError(\"Unknown element type: \" + type);");
        out("}");
      }
      out("}");
    }
    out("}");
  }

  /** True if {@code tokenName} is a regexp token that matches whitespace, isn't referenced by the grammar, and isn't a generic identifier/number. */
  private boolean isIgnoredWhitespaceToken(@NotNull String tokenName, @NotNull String tokenText) {
    return isRegexpToken(tokenText) &&
           !myTokensUsedInGrammar.contains(tokenName) &&
           matchesAny(getRegexpTokenRegexp(tokenText), " ", "\n") &&
           !matchesAny(getRegexpTokenRegexp(tokenText), "a", "1", "_", ".");
  }

  /** Emits the {@code TokenSets} inner interface holding the {@code TokenSet} constants registered via the parser's token-choice helper. */
  private void generateTokenSets() {
    if (myChoiceTokenSets.isEmpty()) {
      return;
    }
    newLine();
    out("interface %s {", JavaBnfConstants.TOKEN_SET_HOLDER_NAME);
    Map<String, String> reverseMap = new HashMap<>();
    myChoiceTokenSets.forEach((name, tokens) -> {
      String value = format(shorten(JavaBnfConstants.TOKEN_SET_CLASS) + ".create(%s)", tokenSetString(tokens));
      String alreadyRendered = reverseMap.putIfAbsent(value, name);
      out("%s %s = %s;", shorten(JavaBnfConstants.TOKEN_SET_CLASS), name, ObjectUtils.chooseNotNull(alreadyRendered, value));
    });
    out("}");
  }

  /*PSI******************************************************************/
  /**
   * Resolved super-interface list emitted for {@code rule}'s PSI interface:
   * {@link PsiRuleInfo#superInterfaces} plus a {@code StubBasedPsiElement<stubClass>} entry when
   * the rule carries a stub. Callers must have run {@link #inferSuperInterfaces}.
   */
  private @NotNull Collection<String> resolvePsiIntfSupers(@NotNull BnfRule rule) {
    Collection<String> supers = psiInfo(rule).superInterfaces;
    String stubClass = ruleInfo(rule).stub();
    if (StringUtil.isNotEmpty(stubClass)) {
      Set<String> withStub = new LinkedHashSet<>(supers);
      withStub.add(JavaBnfConstants.STUB_BASED_PSI_ELEMENT + "<" + stubClass + ">");
      return withStub;
    }
    return supers;
  }

  /**
   * Resolved {@code extends} target emitted for {@code rule}'s PSI impl. May carry generics; callers
   * that need the raw class name must pass the result through
   * {@link org.intellij.grammar.generator.java.JavaNames#getRawClassName}. Callers must have run
   * {@link #calcRealSuperClasses}.
   */
  private @Nullable String resolvePsiImplSuperClass(@NotNull BnfRule rule) {
    return psiInfo(rule).realSuperClass;
  }

  /**
   * Resolved {@code implements} entry emitted for {@code rule}'s PSI impl — the rule's own PSI
   * interface. Additional interfaces (super-rule's intf, {@code implements} attrs,
   * {@code StubBasedPsiElement}) are reached transitively through that interface's super-interface
   * list.
   */
  private @NotNull String resolvePsiImplSuperInterface(@NotNull BnfRule rule) {
    return ruleInfo(rule).intfClass();
  }

  /** Collects every type name that {@code rule}'s generated PSI accessors and mixin/util methods reference, so callers can build the import list. */
  public Collection<String> getRuleMethodTypesToImport(BnfRule rule) {
    Set<String> result = new TreeSet<>();

    Collection<RuleMethodsHelper.RuleMethodInfo> methods = myRulesMethodsHelper.getFor(rule);
    for (RuleMethodsHelper.RuleMethodInfo ruleMethodInfo : methods) {
      if (ruleMethodInfo.rule() != null) {
        result.add(getAccessorType(ruleMethodInfo.rule()));
      }
      else if (ruleMethodInfo.type() == RuleMethodsHelper.MethodType.USER) {
        RuleMethodsHelper.RuleMethodInfo targetInfo = null;
        for (Object m : resolveUserPsiPathMethods(rule, ruleMethodInfo.path().split("/"))) {
          if (m == null) break;
          if (m instanceof String) continue;
          targetInfo = (RuleMethodsHelper.RuleMethodInfo)m;
        }
        if (targetInfo != null && targetInfo.rule() != null) {
          result.add(getAccessorType(targetInfo.rule()));
        }
      }
    }

    String mixinClass = getAttribute(rule, KnownAttribute.MIXIN);

    for (RuleMethodsHelper.RuleMethodInfo ruleMethodInfo : methods) {
      if (ruleMethodInfo.type() != RuleMethodsHelper.MethodType.MIXIN) continue;

      JavaHelper mixinHelper = helperFor(KnownAttribute.MIXIN);
      List<NavigatablePsiElement> mixinMethods =
        mixinHelper.findClassMethods(mixinClass, MethodType.INSTANCE, ruleMethodInfo.name(), false, -1);

      JavaHelper psiImplUtilHelper = helperFor(KnownAttribute.PSI_IMPL_UTIL_CLASS);
      List<NavigatablePsiElement> implMethods = RuleImplUtil.findRuleImplMethods(psiImplUtilHelper, myPsiImplUtilClass, ruleMethodInfo.name(), rule);

      collectMethodTypesToImport(mixinMethods, false, result);
      collectMethodTypesToImport(implMethods, true, result);
    }
    return result;
  }

  /**
   * Adds every reachable type name from {@code methods} (return type, generics, parameter types,
   * exceptions) to {@code result}. {@code isInPsiUtil} skips the first PSI-util parameter pair
   * since it represents the {@code this} target, not a real parameter.
   */
  private void collectMethodTypesToImport(@NotNull List<NavigatablePsiElement> methods, boolean isInPsiUtil, @NotNull Set<String> result) {
    for (NavigatablePsiElement method : methods) {
      List<String> types = JavaHelper.getMethodTypes(method);
      String returnType = ContainerUtil.getFirstItem(types);
      addTypeToImports(returnType, ContainerUtil.filter(JavaHelper.getAnnotations(method), s -> !IGNORED_ANNOTATIONS_IN_PSI.contains(s)), result);

      for (TypeParameterSymbol generic : JavaHelper.getGenericParameters(method)) {
        for (JvmTypeRef type : generic.extendsList()) {
          addTypeToImports(JvmTypeRefs.renderPlain(type), emptyList(), result);
        }
        for (Fqn type : generic.annotations()) {
          addTypeToImports(type.value(), emptyList(), result);
        }
      }

      for (int i = isInPsiUtil ? 3 : 1, count = types.size(); i < count; i += 2) {
        String type = types.get(i);
        addTypeToImports(type, ContainerUtil.filter(JavaHelper.getParameterAnnotations(method, (i - 1) / 2), s -> !IGNORED_ANNOTATIONS_IN_PSI.contains(s)), result);
      }

      for (String exception : JavaHelper.getExceptionList(method)) {
        addTypeToImports(exception, emptyList(), result);
      }
    }
  }


  /**
   * Renders the body of a PSI accessor — a {@code findChildByType}, {@code findChildByClass},
   * {@code PsiTreeUtil.getChildOfType} or stub-aware variant — depending on cardinality, whether
   * the rule has stubs, whether the AST is mixed, and the legacy/no-stubs fallback.
   */
  private String generatePsiAccessorImplCall(@NotNull BnfRule rule, @NotNull RuleMethodsHelper.RuleMethodInfo ruleMethodInfo, boolean mixedAST) {
    boolean isToken = ruleMethodInfo.rule() == null;

    Cardinality type = ruleMethodInfo.cardinality();
    boolean many = type.many();
    boolean required = type == REQUIRED && !many;
    boolean stubbed = !isToken &&
                      ruleInfo(rule).realStubClass() != null &&
                      ruleInfo(ruleMethodInfo.rule()).realStubClass() != null;
    String result;
    // todo REMOVEME. Keep old generation logic for a while.
    if (!mixedAST && myNoStubs) {
      if (isToken) {
        return (type == REQUIRED ? "findNotNullChildByType" : "findChildByType") +
               "(" + getElementType(ruleMethodInfo.path()) + ")";
      }
      else {
        String className = shorten(getAccessorType(ruleMethodInfo.rule()));
        return many ? format("%s.getChildrenOfTypeAsList(this, %s.class)", shorten(myPsiTreeUtilClass), className) :
               (type == REQUIRED ? "findNotNullChildByClass" : "findChildByClass") + "(" + className + ".class)";
      }
    }
    // new logic
    if (isToken) {
      String getterName = mixedAST ? "findPsiChildByType" : "findChildByType";
      result = getterName + "(" + getElementType(ruleMethodInfo.path()) + ")";
    }
    else {
      String className = shorten(getAccessorType(ruleMethodInfo.rule()));
      String getterName = stubbed && many ? "getStubChildrenOfTypeAsList" :
                          stubbed ? "getStubChildOfType" :
                          many ? "getChildrenOfTypeAsList" : "getChildOfType";
      result = format("%s.%s(this, %s.class)", shorten(myPsiTreeUtilClass), getterName, className);
    }
    return required && !mixedAST ? "notNullChild(" + result + ")" : result;
  }

  /** Return type of an accessor for {@code rule} — its first {@code implements} entry for external rules, its PSI interface otherwise. */
  private @NotNull String getAccessorType(@NotNull BnfRule rule) {
    if (BnfRules.isExternal(rule)) {
      Pair<String, String> first = ContainerUtil.getFirstItem(getAttribute(rule, KnownAttribute.IMPLEMENTS));
      return Objects.requireNonNull(first).second;
    }
    else {
      return ruleInfo(rule).intfClass();
    }
  }

  /**
   * Walks a slash-separated path of accessor names ({@code "foo/bar[0]/baz"}) starting at
   * {@code startRule}, yielding the resolved {@link RuleMethodsHelper.RuleMethodInfo} for each step
   * (or the path element string for empty/index segments, or {@code null} when resolution fails).
   */
  private JBIterable<?> resolveUserPsiPathMethods(BnfRule startRule,
                                                  String[] splitPath) {
    BnfRule[] targetRule = {startRule};
    return JBIterable.generate(0, i -> i + 1).take(splitPath.length).map(i -> {
      String pathElement = splitPath[i];
      int indexStart = pathElement.indexOf('[');
      String item = indexStart > -1 ? pathElement.substring(0, indexStart).trim() : pathElement.trim();

      if (item.isEmpty()) return item;
      if (targetRule[0] == null) return null;
      RuleMethodsHelper.RuleMethodInfo targetInfo = myRulesMethodsHelper.getMethodInfo(targetRule[0], item);
      targetRule[0] = targetInfo == null ? null : targetInfo.rule();
      return targetInfo;
    });
  }

  /*Syntax******************************************************************/
  /**
   * Emits the Syntax-API element-type converter class — a registry mapping each generated
   * Syntax {@code KtElementType} (and, when token types are generated, each token element type)
   * to its legacy {@code IElementType} counterpart. Used only when {@link GenOptions.ParserApi} is
   * {@code Syntax}.
   */
  private void generateElementTypesConverter(String elementTypesConverter,
                                             String typeHolderClass,
                                             String syntaxElementTypeHolderClass,
                                             Map<String, BnfRule> sortedCompositeTypes) {
    var converterInterface = KotlinBnfConstants.KT_ELEMENT_TYPE_CONVERTER_FACTORY_CLASS;
    Set<String> imports = new LinkedHashSet<>();
    imports.add(typeHolderClass);
    imports.add(syntaxElementTypeHolderClass);
    imports.add(JavaBnfConstants.IELEMENTTYPE_CLASS);
    imports.add(KotlinBnfConstants.KT_ELEMENT_TYPE_CLASS);
    imports.add(converterInterface);
    imports.add(KotlinBnfConstants.KT_ELEMENT_TYPE_CONVERTER_CLASS);
    imports.add(KotlinBnfConstants.KT_ELEMENT_TYPE_CONVERTER_FILE);
    imports.add(JavaBnfConstants.NOTNULL_ANNO);
    imports.add(KotlinBnfConstants.KT_PAIR_CLASS);
    imports.add(JavaBnfConstants.OVERRIDE_ANNO);

    generateClassHeader(elementTypesConverter, imports, "", TypeKind.CLASS, "", converterInterface);

    out(shorten(JavaBnfConstants.OVERRIDE_ANNO));
    out("public %s %s getElementTypeConverter() {", shorten(JavaBnfConstants.NOTNULL_ANNO),
        shorten(KotlinBnfConstants.KT_ELEMENT_TYPE_CONVERTER_CLASS));
    out("return %s.elementTypeConverterOf(", shorten(KotlinBnfConstants.KT_ELEMENT_TYPE_CONVERTER_FILE));
    var sortedCompositeTypesArr = sortedCompositeTypes.keySet().toArray(new String[0]);
    var generateTokenTypeConversions = G.generateTokenTypes && !mySimpleTokens.isEmpty();
    for (int i = 0; i < sortedCompositeTypesArr.length; i++) {
      String elementType = sortedCompositeTypesArr[i];
      String elementTypeAccessor = "INSTANCE.get" + elementType.substring(0, 1).toUpperCase() + elementType.substring(1) + "()";
      out("new %s<%s, %s>(%s.%s, %s.%s)" + (i != sortedCompositeTypesArr.length - 1 || generateTokenTypeConversions ? "," : ""),
          shorten(KotlinBnfConstants.KT_PAIR_CLASS),
          shorten(KotlinBnfConstants.KT_ELEMENT_TYPE_CLASS), shorten(JavaBnfConstants.IELEMENTTYPE_CLASS),
          shorten(syntaxElementTypeHolderClass), elementTypeAccessor,
          shorten(typeHolderClass), elementType);
    }
    if (G.generateTokenTypes && !mySimpleTokens.isEmpty()) {
      newLine();
      var mySimpleTokensArr = mySimpleTokens.keySet().toArray(new String[0]);
      for (int i = 0; i < mySimpleTokensArr.length; i++) {
        var tokenText = mySimpleTokensArr[i];
        String tokenName = ObjectUtils.chooseNotNull(mySimpleTokens.get(tokenText), tokenText);
        if (isIgnoredWhitespaceToken(tokenName, tokenText)) continue;
        var elementType = getElementType(tokenName);
        String elementTypeAccessor = "INSTANCE.get" + elementType.substring(0, 1).toUpperCase() + elementType.substring(1) + "()";
        out("new %s<%s, %s>(%s.%s, %s.%s)" + (i != mySimpleTokensArr.length - 1 ? "," : ""),
            shorten(KotlinBnfConstants.KT_PAIR_CLASS),
            shorten(KotlinBnfConstants.KT_ELEMENT_TYPE_CLASS), shorten(JavaBnfConstants.IELEMENTTYPE_CLASS),
            shorten(syntaxElementTypeHolderClass), elementTypeAccessor,
            shorten(typeHolderClass), elementType);
      }
    }
    out(");");
    out("}");
    out("}");
  }
}
