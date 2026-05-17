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
   * Builds {@link ClassSymbol} stubs for every PSI artifact this generator is about to emit — PSI
   * interfaces and impls (with method signatures), the visitor (with its {@code visit*} methods),
   * and the element-type holder (class header only). Stubs mirror the hierarchy and surface of the
   * to-be-generated class so the second pass can answer {@link JavaHelper#findClassMethods} queries
   * over rule super-chains, which drives accurate {@code @Override} emission. Must be called after
   * {@link #inferSuperInterfaces} and {@link #calcRealSuperClasses}, which populate
   * {@link PsiRuleInfo}; also calls {@link RuleMethodsHelper#buildMaps} so populators can iterate
   * rule methods.
   */
  private @NotNull List<ClassSymbol> buildPsiStubs(@NotNull Map<String, BnfRule> sortedPsiRules) {
    myRulesMethodsHelper.buildMaps(sortedPsiRules.values());
    List<ClassSymbol> stubs = new ArrayList<>();
    for (BnfRule rule : sortedPsiRules.values()) {
      RuleInfo info = ruleInfo(rule);
      ClassSymbol.Builder intf = stubInterfaceBuilder(info.intfClass(), resolvePsiIntfSupers(rule));
      populateIntfMethods(intf, rule, info);
      stubs.add(intf.build());
      ClassSymbol.Builder impl = stubImplBuilder(info.implClass(), resolvePsiImplSuperClass(rule), resolvePsiImplSuperInterface(rule));
      populateImplMethods(impl, rule, info);
      stubs.add(impl.build());
    }
    ClassSymbol.Builder visitor = stubVisitor(sortedPsiRules);
    if (visitor != null) stubs.add(visitor.build());
    ClassSymbol.Builder holder = stubElementTypeHolder();
    if (holder != null) stubs.add(holder.build());
    return stubs;
  }

  private static @NotNull ClassSymbol.Builder stubInterfaceBuilder(@NotNull String fqn,
                                                                   @NotNull Collection<String> superInterfaces) {
    ClassSymbol.Builder b = new ClassSymbol.Builder();
    b.name = Fqn.of(fqn);
    b.modifiers = Modifier.PUBLIC | Modifier.INTERFACE;
    for (String s : superInterfaces) {
      b.interfaces.add(Fqn.of(getRawClassName(s)));
    }
    return b;
  }

  private static @NotNull ClassSymbol.Builder stubImplBuilder(@NotNull String fqn,
                                                              @Nullable String superClass,
                                                              @NotNull String intfClass) {
    ClassSymbol.Builder b = new ClassSymbol.Builder();
    b.name = Fqn.of(fqn);
    b.modifiers = Modifier.PUBLIC;
    if (StringUtil.isNotEmpty(superClass)) {
      b.superClass = Fqn.of(getRawClassName(superClass));
    }
    b.interfaces.add(Fqn.of(intfClass));
    return b;
  }

  /**
   * Stub for the to-be-generated PSI visitor, with one {@code visit*} {@link MethodSymbol} per rule
   * plus one per super-interface (matches {@link #generateVisitor} emission). Returns {@code null}
   * when no visitor will be emitted (mirrors the gate at {@link #generatePsi}).
   */
  private @Nullable ClassSymbol.Builder stubVisitor(@NotNull Map<String, BnfRule> sortedPsiRules) {
    if (myVisitorClassName == null || myGrammarRoot == null) return null;
    ClassSymbol.Builder b = new ClassSymbol.Builder();
    b.name = Fqn.of(myVisitorClassName);
    b.modifiers = Modifier.PUBLIC;
    b.superClass = Fqn.of(JavaBnfConstants.PSI_ELEMENT_VISITOR_CLASS);
    if (G.visitorValue != null) b.typeParameters.add(G.visitorValue);
    for (MethodSymbol.Builder m : visitorMethodSignatures(sortedPsiRules)) {
      m.declaringClass = b.name;
      b.methods.add(m);
    }
    return b;
  }

  /**
   * Stub for the to-be-generated element-type holder interface. Class header only — no methods or
   * fields are modelled here (callers don't currently introspect those). Returns {@code null}
   * when no holder will be emitted (mirrors the gate at {@link #generateElementTypeHolder}).
   */
  private @Nullable ClassSymbol.Builder stubElementTypeHolder() {
    boolean needToGenerate = myGrammarRoot != null &&
                             (G.generateTokenTypes || G.generateElementTypes || G.generatePsi && G.generatePsiFactory);
    if (!needToGenerate) return null;
    ClassSymbol.Builder b = new ClassSymbol.Builder();
    b.name = Fqn.of(myPsiElementTypeHolderClass);
    b.modifiers = Modifier.PUBLIC | Modifier.INTERFACE;
    return b;
  }

  private void populateIntfMethods(@NotNull ClassSymbol.Builder intf, @NotNull BnfRule rule, @NotNull RuleInfo info) {
    for (MethodSymbol.Builder m : intfMethodSignatures(rule, info)) {
      m.declaringClass = intf.name;
      intf.methods.add(m);
    }
  }

  private void populateImplMethods(@NotNull ClassSymbol.Builder impl, @NotNull BnfRule rule, @NotNull RuleInfo info) {
    for (MethodSymbol.Builder m : implMethodSignatures(rule, info)) {
      m.declaringClass = impl.name;
      impl.methods.add(m);
    }
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

  private @NotNull List<MethodSymbol.Builder> intfMethodSignatures(@NotNull BnfRule rule, @NotNull RuleInfo info) {
    List<MethodSymbol.Builder> out = new ArrayList<>();
    Set<String> visited = new TreeSet<>();
    for (RuleMethodsHelper.RuleMethodInfo ruleMethodInfo : myRulesMethodsHelper.getFor(rule)) {
      if (StringUtil.isEmpty(ruleMethodInfo.name())) continue;
      addMethodSignatures(out, rule, info, ruleMethodInfo, /*intf*/ true, visited);
    }
    return out;
  }

  private @NotNull List<MethodSymbol.Builder> implMethodSignatures(@NotNull BnfRule rule, @NotNull RuleInfo info) {
    List<MethodSymbol.Builder> out = new ArrayList<>();
    out.addAll(constructorSignatures(rule, info));
    if (myVisitorClassName != null) {
      out.add(acceptSpecializedSignature(rule));
      out.add(acceptGenericSignature());
    }
    Set<String> visited = new TreeSet<>();
    for (RuleMethodsHelper.RuleMethodInfo ruleMethodInfo : myRulesMethodsHelper.getFor(rule)) {
      if (StringUtil.isEmpty(ruleMethodInfo.name())) continue;
      addMethodSignatures(out, rule, info, ruleMethodInfo, /*intf*/ false, visited);
    }
    return out;
  }

  private void addMethodSignatures(@NotNull List<MethodSymbol.Builder> out,
                                   @NotNull BnfRule rule,
                                   @NotNull RuleInfo info,
                                   @NotNull RuleMethodsHelper.RuleMethodInfo ruleMethodInfo,
                                   boolean intf,
                                   @NotNull Set<String> visited) {
    switch (ruleMethodInfo.type()) {
      case RULE, TOKEN -> out.add(accessorSignature(rule, ruleMethodInfo, intf));
      case USER -> {
        MethodSymbol.Builder b = userAccessorSignature(rule, ruleMethodInfo, intf);
        if (b != null) out.add(b);
      }
      case MIXIN -> mixinSignatures(out, rule, ruleMethodInfo, info, intf, visited);
    }
  }

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
    b.returnType = returnType;
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
    b.returnType = returnType;
    boolean nonNull = !cardinality.many() && cardinality == REQUIRED && !totalNullable;
    b.annotations.add(nonNull ? NOTNULL_FQ : NULLABLE_FQ);
    return b;
  }

  /**
   * Signatures for one MIXIN entry — one per resolved on-disk method (mirroring
   * {@link #generateMixinMethod}: for {@code intf}, mixin lookups; for both, psiImplUtil
   * lookups). Dedupes by name + parameter types via {@code visited}, same as
   * {@link #generateUtilMethod}.
   */
  private void mixinSignatures(@NotNull List<MethodSymbol.Builder> out,
                               @NotNull BnfRule rule,
                               @NotNull RuleMethodsHelper.RuleMethodInfo ruleMethodInfo,
                               @NotNull RuleInfo info,
                               boolean intf,
                               @NotNull Set<String> visited) {
    if (intf) {
      JavaHelper mixinHelper = helperFor(KnownAttribute.MIXIN);
      String mixinClass = getAttribute(rule, KnownAttribute.MIXIN);
      for (NavigatablePsiElement m : mixinHelper.findClassMethods(mixinClass, MethodType.INSTANCE, ruleMethodInfo.name(), false, -1)) {
        MethodSymbol.Builder b = mixinSignatureFromSource(ruleMethodInfo.name(), m, /*isInPsiUtil*/ false, intf, visited);
        if (b != null) out.add(b);
      }
    }
    JavaHelper psiImplUtilHelper = helperFor(KnownAttribute.PSI_IMPL_UTIL_CLASS);
    for (NavigatablePsiElement m : RuleImplUtil.findRuleImplMethods(psiImplUtilHelper, myPsiImplUtilClass, ruleMethodInfo.name(), rule)) {
      MethodSymbol.Builder b = mixinSignatureFromSource(ruleMethodInfo.name(), m, /*isInPsiUtil*/ true, intf, visited);
      if (b != null) out.add(b);
    }
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
    b.returnType = returnType;
    for (String s : JavaHelper.getAnnotations(method)) {
      if ("java.lang.Override".equals(s)) continue;
      if (s.startsWith("kotlin.")) continue;
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
      pb.type = type;
      pb.name = name;
      for (String s : JavaHelper.getParameterAnnotations(method, (i - 1) / 2)) {
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
    b.returnType = G.visitorValue != null ? G.visitorValue : "void";
    if (G.visitorValue != null) {
      b.generics.add(new TypeParameterSymbol.Builder(G.visitorValue));
    }
    ParameterSymbol.Builder p = new ParameterSymbol.Builder();
    p.name = "visitor";
    p.type = myVisitorClassName + (G.visitorValue != null ? "<" + G.visitorValue + ">" : "");
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
    b.returnType = "void";
    ParameterSymbol.Builder p = new ParameterSymbol.Builder();
    p.name = "visitor";
    p.type = JavaBnfConstants.PSI_ELEMENT_VISITOR_CLASS;
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
    b.returnType = "void";
    ParameterSymbol.Builder p = new ParameterSymbol.Builder();
    p.name = "node";
    p.type = JavaBnfConstants.AST_NODE_CLASS;
    b.parameters.add(p);
    return b;
  }

  private @NotNull MethodSymbol.Builder stubConstructorSignature(@NotNull String shortName, @NotNull String stubName) {
    MethodSymbol.Builder b = new MethodSymbol.Builder();
    b.name = shortName;
    b.methodType = MethodType.CONSTRUCTOR;
    b.modifiers = Modifier.PUBLIC;
    b.returnType = "void";
    ParameterSymbol.Builder pStub = new ParameterSymbol.Builder();
    pStub.name = "stub";
    pStub.type = stubName;
    b.parameters.add(pStub);
    ParameterSymbol.Builder pType = new ParameterSymbol.Builder();
    pType.name = "stubType";
    pType.type = G.fallbackStubElementType;
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
    b.returnType = "void";
    for (int i = 1, n = types.size(); i + 1 < n; i += 2) {
      ParameterSymbol.Builder pb = new ParameterSymbol.Builder();
      pb.type = substitutor.apply(types.get(i));
      String name = types.get(i + 1);
      // Name overrides used by the legacy {@link ParserGeneratorUtil#getParametersString} formatter
      // so generated identifiers don't depend on whether the source-method names were preserved.
      String rawType = getRawClassName(pb.type);
      if (rawType.endsWith(JavaBnfConstants.AST_NODE_CLASS)) name = "node";
      else if (rawType.endsWith("ElementType")) name = "type";
      else if (rawType.endsWith("Stub")) name = "stub";
      pb.name = name;
      for (String s : JavaHelper.getParameterAnnotations(source, (i - 1) / 2)) {
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
    b.returnType = returnType;
    ParameterSymbol.Builder p = new ParameterSymbol.Builder();
    p.name = "o";
    p.type = paramFqn;
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
                             : shorten(b.annotatedReturnType != null ? b.annotatedReturnType : b.returnType);
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
            line.append(shorten(t.extendsList.get(j)));
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
      String pt = p.annotatedType != null ? p.annotatedType : p.type;
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

    List<ClassSymbol> stubs = buildPsiStubs(psiGenerationTargets.sortedPsiRules());
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
    generatePsi(psiGenerationTargets.sortedPsiRules());
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
   * Emits the PSI visitor class. Each rule gets a {@code visit<Rule>} method that delegates to
   * its first declared super-interface; remaining supers are listed as commented-out alternatives.
   * Non-public super-interfaces are replaced with the configured base interface. When
   * {@link GenOptions#visitorValue} is set, methods carry that type parameter and {@code return}
   * the delegate result.
   */
  private void generateVisitor(String psiClass, Map<String, BnfRule> sortedRules) {
    VisitorSupers vs = resolveVisitorSupers(sortedRules);
    String superIntf = vs.superIntf;
    MultiMap<String, String> supers = vs.supers;
    Set<String> imports = new LinkedHashSet<>(Arrays.asList("org.jetbrains.annotations.*", JavaBnfConstants.PSI_ELEMENT_VISITOR_CLASS, superIntf));
    imports.addAll(ContainerUtil.sorted(
      JBIterable.from(sortedRules.values()).map(this::ruleInfo).map(o -> o.intfPackage() + ".*").toSet()));
    imports.addAll(supers.values());
    String r = G.visitorValue != null ? "<" + G.visitorValue + ">" : "";
    String t = G.visitorValue != null ? G.visitorValue : "void";
    String ret = G.visitorValue != null ? "return " : "";
    generateClassHeader(psiClass + r, imports, "", TypeKind.CLASS, JavaBnfConstants.PSI_ELEMENT_VISITOR_CLASS);
    Set<String> visited = new HashSet<>();
    Set<String> all = new TreeSet<>();
    for (BnfRule rule : sortedRules.values()) {
      String methodName = CommonRendererUtils.getRulePsiClassName(rule, null);
      visited.add(methodName);
      // Rule-side visit methods always emit the short PSI name as the parameter type, irrespective
      // of {@code G.generateFQN} — matches the historical {@link #generateVisitor} behavior.
      out("public %s visit%s(%s %s o) {", t, methodName, shorten(JavaBnfConstants.NOTNULL_ANNO),
          CommonRendererUtils.getRulePsiClassName(rule, myPsiInterfaceFormat));
      boolean first = true;
      for (String top : supers.get(rule.getName())) {
        if (!first && top.equals(superIntf)) continue;
        top = getRawClassName(top);
        if (first) all.add(top);
        String text = "visit" + myPsiInterfaceFormat.strip(StringUtil.getShortName(top)) + "(o);";
        if (first) {
          out(ret + text);
        }
        else {
          out("// " + text);
        }
        if (first) first = false;
      }
      out("}");
      newLine();
    }
    all.remove(superIntf);
    for (String top : JBIterable.from(all).append(superIntf)) {
      String methodName = myPsiInterfaceFormat.strip(StringUtil.getShortName(top));
      if (visited.contains(methodName)) continue;
      out("public %s visit%s(%s %s o) {", t, methodName, shorten(JavaBnfConstants.NOTNULL_ANNO), shorten(top));
      if (!methodName.equals(StringUtil.getShortName(top)) && !top.equals(superIntf)) {
        out(ret + "visit" + myPsiInterfaceFormat.strip(StringUtil.getShortName(superIntf)) + "(o);");
      }
      else {
        String superPrefix = methodName.equals("Element") ? "super." : "";
        out(superPrefix + "visitElement(o);");
        if (G.visitorValue != null) out(ret + "null;");
      }
      out("}");
      newLine();
    }

    out("}");
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
  /** Emits the PSI interface and impl for each rule, plus the visitor when one is configured. */
  private void generatePsi(@NotNull Map<String, BnfRule> sortedPsiRules) throws IOException {
    if (!G.generatePsi) return;

    checkClassAvailability(myPsiImplUtilClass, KnownAttribute.PSI_IMPL_UTIL_CLASS);

    myRulesMethodsHelper.buildMaps(sortedPsiRules.values());

    String psiOutput = myPaths.pathString(KnownAttribute.PSI_OUTPUT_PATH);

    for (BnfRule rule : sortedPsiRules.values()) {
      RuleInfo info = ruleInfo(rule);
      openOutput(info.intfClass(), psiOutput);
      try {
        generatePsiIntf(rule, info);
      }
      finally {
        closeOutput();
      }
    }

    for (BnfRule rule : sortedPsiRules.values()) {
      RuleInfo info = ruleInfo(rule);
      openOutput(info.implClass(), psiOutput);
      try {
        generatePsiImpl(rule, info);
      }
      finally {
        closeOutput();
      }
    }

    if (myVisitorClassName != null && myGrammarRoot != null) {
      openOutput(myVisitorClassName, psiOutput);
      try {
        generateVisitor(myVisitorClassName, sortedPsiRules);
      }
      finally {
        closeOutput();
      }
    }
  }


  /** Emits the public PSI interface for {@code rule}: super-interfaces (with stub-aware additions) and rule accessor signatures. */
  private void generatePsiIntf(@NotNull BnfRule rule, @NotNull RuleInfo info) {
    String psiClass = info.intfClass();
    Collection<String> psiSupers = resolvePsiIntfSupers(rule);

    Set<String> imports = new LinkedHashSet<>();
    imports.addAll(DEFAULT_PSI_IMPORTS);
    imports.addAll(psiSupers);
    imports.addAll(getRuleMethodTypesToImport(rule));

    generateClassHeader(psiClass, imports, "", TypeKind.INTERFACE, ArrayUtil.toStringArray(psiSupers));
    generatePsiClassMethods(rule, info, true);
    out("}");
  }

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

  /**
   * Emits the PSI impl class for {@code rule}: extends/implements clause from
   * {@link PsiRuleInfo#realSuperClass}, constructors that mirror those of the (eventual) base
   * class, optional {@code accept(Visitor)} dispatch, and rule accessor implementations.
   * Detects and warns on cyclic {@code mixin}/{@code extends} chains.
   */
  private void generatePsiImpl(@NotNull BnfRule rule, @NotNull RuleInfo info) {
    String psiClass = info.implClass();
    String superInterface = resolvePsiImplSuperInterface(rule);
    String stubName = info.realStubClass();
    String implSuper = resolvePsiImplSuperClass(rule);

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

    Function<String, String> substitutor = stubName == null ? ParserGeneratorUtil::unwrapTypeArgumentForParamList : o -> {
      String oo = unwrapTypeArgumentForParamList(o);
      if (oo.equals(o)) return o;
      int idx = oo.lastIndexOf(" ");
      return idx == -1 ? stubName : oo.substring(0, idx) + " " + stubName;
    };

    Set<BnfRule> visited = new HashSet<>();
    List<NavigatablePsiElement> constructors = Collections.emptyList();
    BnfRule topSuperRule = null;
    String topSuperClass = null;
    JavaHelper mixinHelper = helperFor(KnownAttribute.MIXIN);
    for (BnfRule next = rule; next != null && next != topSuperRule; ) {
      if (!visited.add(next)) {
        addWarning(rule.getName() + " employs cyclic inheritance");
        break;
      }
      topSuperRule = next;
      String superClass = psiInfo(next).realSuperClass;
      if (superClass == null) continue;
      next = getEffectiveSuperRule(myFile, next);
      if (next != null && next != topSuperRule && getAttribute(topSuperRule, KnownAttribute.MIXIN) == null) continue;
      topSuperClass = getRawClassName(superClass);
      constructors = mixinHelper.findClassMethods(topSuperClass, MethodType.CONSTRUCTOR, "*", false, -1);
      if (!constructors.isEmpty()) break;
    }
    if (!G.generateFQN) {
      for (NavigatablePsiElement m : constructors) {
        collectMethodTypesToImport(Collections.singletonList(m), false, imports);
      }
      if (stubName != null && constructors.isEmpty()) imports.add(G.fallbackStubElementType);
      if (stubName != null) imports.add(stubName);
    }

    if (!G.generateTokenTypes) {
      // add parser static imports hoping external token constants are there
      for (RuleMethodsHelper.RuleMethodInfo ruleMethodInfo : myRulesMethodsHelper.getFor(rule)) {
        if (ruleMethodInfo.rule() == null && !StringUtil.isEmpty(ruleMethodInfo.name())) {
          for (String s : getRootAttribute(myFile, KnownAttribute.PARSER_IMPORTS).asStrings()) {
            if (s.startsWith("static ")) imports.add(s);
          }
          break;
        }
      }
    }

    TypeKind typeKind = info.isAbstract() ? TypeKind.ABSTRACT_CLASS : TypeKind.CLASS;
    generateClassHeader(psiClass, imports, "", typeKind, implSuper, superInterface);
    for (MethodSymbol.Builder b : constructorSignatures(rule, info)) {
      renderMethodHeader(b, /*intf*/ false);
      StringBuilder args = new StringBuilder();
      for (ParameterSymbol.Builder p : b.parameters) {
        if (args.length() > 0) args.append(", ");
        args.append(p.name);
      }
      out("super(" + args + ");");
      out("}");
      newLine();
    }
    if (myVisitorClassName != null) {
      MethodSymbol.Builder bSpecialized = acceptSpecializedSignature(rule);
      if (superDeclares(implSuper, mixinHelper, "accept", 1, myVisitorClassName)) {
        out(shorten(JavaBnfConstants.OVERRIDE_ANNO));
      }
      renderMethodHeader(bSpecialized, /*intf*/ false);
      String ret = G.visitorValue != null ? "return " : "";
      out(ret + "visitor.visit" + CommonRendererUtils.getRulePsiClassName(rule, null) + "(this);");
      out("}");
      newLine();

      MethodSymbol.Builder bGeneric = acceptGenericSignature();
      // PsiElement always declares accept(PsiElementVisitor); the @Override stays unconditional —
      // walking the chain through JavaHelper would require interface-side lookup, which the
      // current JavaHelper API doesn't expose.
      out(shorten(JavaBnfConstants.OVERRIDE_ANNO));
      renderMethodHeader(bGeneric, /*intf*/ false);
      String shortened = shorten(myVisitorClassName);
      out("if (visitor instanceof " + shortened + ") accept((" + shortened + ")visitor);");
      out("else super.accept(visitor);");
      out("}");
      newLine();
    }
    generatePsiClassMethods(rule, info, false);
    out("}");
  }

  /**
   * Emits accessor and util methods for {@code rule}'s PSI interface ({@code intf=true}) or impl ({@code intf=false}).
   */
  private void generatePsiClassMethods(@NotNull BnfRule rule, @NotNull RuleInfo info, boolean intf) {
    Set<String> visited = new TreeSet<>();
    boolean mixedAST = psiInfo(rule).mixedAST;
    for (RuleMethodsHelper.RuleMethodInfo ruleMethodInfo : myRulesMethodsHelper.getFor(rule)) {
      if (StringUtil.isEmpty(ruleMethodInfo.name())) continue;
      switch (ruleMethodInfo.type()) {
        case RULE, TOKEN -> generatePsiAccessor(rule, ruleMethodInfo, intf, mixedAST);
        case USER -> generateUserPsiAccessors(rule, ruleMethodInfo, intf, mixedAST);
        case MIXIN -> generateMixinMethod(rule, ruleMethodInfo, intf, info, visited);
        default -> throw new AssertionError(ruleMethodInfo.toString());
      }
    }
  }

  private void generateMixinMethod(@NotNull BnfRule rule,
                                   @NotNull RuleMethodsHelper.RuleMethodInfo ruleMethodInfo,
                                   boolean intf,
                                   @NotNull RuleInfo info,
                                   @NotNull Set<String> visited) {
    boolean isGenerated = false;

    if (intf) {
      JavaHelper mixinHelper = helperFor(KnownAttribute.MIXIN);
      String mixinClass = getAttribute(rule, KnownAttribute.MIXIN);
      List<NavigatablePsiElement> methods = mixinHelper.findClassMethods(mixinClass, MethodType.INSTANCE, ruleMethodInfo.name(), false, -1);

      for (NavigatablePsiElement method : methods) {
        generateUtilMethod(ruleMethodInfo.name(), method, true, false, visited);
        isGenerated = true;
      }
    }

    JavaHelper psiImplUtilHelper = helperFor(KnownAttribute.PSI_IMPL_UTIL_CLASS);
    List<NavigatablePsiElement> methods = RuleImplUtil.findRuleImplMethods(psiImplUtilHelper, myPsiImplUtilClass, ruleMethodInfo.name(), rule);
    for (NavigatablePsiElement method : methods) {
      generateUtilMethod(ruleMethodInfo.name(), method, intf, true, visited);
      isGenerated = true;
    }

    if (intf && !isGenerated) {
      String ruleClassName = shorten(info.intfClass());
      String implClassName = StringUtil.getShortName(String.valueOf(myPsiImplUtilClass));
      out("""
            //WARNING: %s(...) is skipped
            //matching %s(%s, ...)
            //methods are not found in %s""",
          ruleMethodInfo.name(), ruleMethodInfo.name(), ruleClassName, implClassName);
      newLine();
      addWarning(format("%s.%s(%s, ...) method not found", implClassName, ruleMethodInfo.name(), ruleClassName));
    }
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
      addTypeToImports(returnType, JavaHelper.getAnnotations(method), result);

      for (TypeParameterSymbol generic : JavaHelper.getGenericParameters(method)) {
        for (String type : generic.extendsList()) {
          addTypeToImports(type, emptyList(), result);
        }
        for (Fqn type : generic.annotations()) {
          addTypeToImports(type.value(), emptyList(), result);
        }
      }

      for (int i = isInPsiUtil ? 3 : 1, count = types.size(); i < count; i += 2) {
        String type = types.get(i);
        addTypeToImports(type, JavaHelper.getParameterAnnotations(method, (i - 1) / 2), result);
      }

      for (String exception : JavaHelper.getExceptionList(method)) {
        addTypeToImports(exception, emptyList(), result);
      }
    }
  }


  /** Emits a single rule- or token-accessor method (e.g. {@code getFoo()}, {@code getFooList()}) for the PSI interface or impl. */
  private void generatePsiAccessor(BnfRule rule, RuleMethodsHelper.RuleMethodInfo ruleMethodInfo, boolean intf, boolean mixedAST) {
    MethodSymbol.Builder b = accessorSignature(rule, ruleMethodInfo, intf);
    if (!intf && superDeclares(ruleInfo(rule).intfClass(), helperFor(KnownAttribute.MIXIN), b.name, 0)) {
      out(shorten(JavaBnfConstants.OVERRIDE_ANNO));
    }
    renderMethodHeader(b, intf);
    if (!intf) {
      out("return " + generatePsiAccessorImplCall(rule, ruleMethodInfo, mixedAST) + ";");
      out("}");
    }
    newLine();
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

  /**
   * Emits a user-defined accessor whose body chains through a {@code methods} attribute path
   * (e.g. {@code "foo/bar[last]"}). Reports a warning per attribute on the first invalid path
   * step (unknown rule, index on a non-list, missing index on a list, suppressed accessor, …)
   * and aborts emission of that accessor.
   */
  private void generateUserPsiAccessors(BnfRule startRule, RuleMethodsHelper.RuleMethodInfo ruleMethodInfo, boolean intf, boolean mixedAST) {
    StringBuilder sb = new StringBuilder();
    BnfRule targetRule = startRule;
    Cardinality cardinality = REQUIRED;
    String context = "";
    String[] splitPath = ruleMethodInfo.path().split("/");

    int i = -1, count = 1;
    boolean totalNullable = false;
    for (Object m : resolveUserPsiPathMethods(startRule, splitPath)) {
      String pathElement = splitPath[++i];
      if (m instanceof String) continue;
      boolean last = i == splitPath.length - 1;
      int indexStart = pathElement.indexOf('[');
      int indexEnd = indexStart > 0 ? pathElement.lastIndexOf(']') : -1;
      String base = (indexStart > -1 ? pathElement.substring(0, indexStart) : pathElement).trim();
      String index = indexEnd > -1 ? pathElement.substring(indexStart + 1, indexEnd).trim() : null;


      RuleMethodsHelper.RuleMethodInfo targetInfo = (RuleMethodsHelper.RuleMethodInfo)m;
      String error;
      if (indexStart > 0 && (indexEnd == -1 || StringUtil.isNotEmpty(pathElement.substring(indexEnd + 1)))) {
        error = "'<name>[<index>]' expected, got '" + pathElement + "'";
      }
      else if (targetInfo == null) {
        Collection<String> available = targetRule == null ? null : myRulesMethodsHelper.getMethodNames(targetRule);
        if (targetRule == null) {
          error = "'" + base + "' not found in '" + splitPath[i - 1] + "' (not a rule)";
        }
        else if (available == null || available.isEmpty()) {
          error = "'" + base + "' not found in '" + targetRule.getName() + "' (available: nothing)";
        }
        else {
          error = "'" + base + "' not found in '" + targetRule.getName() + "' (available: " + String.join(", ", available) + ")";
        }
      }
      else if (index != null && !targetInfo.cardinality().many()) {
        error = "'[" + index + "]' unexpected after '" + base + getCardinalityText(targetInfo.cardinality()) + "'";
      }
      else if (!last && index == null && targetInfo.cardinality().many()) {
        error = "'[<index>]' required after '" + base + getCardinalityText(targetInfo.cardinality()) + "'";
      }
      else if (i > 0 && StringUtil.isEmpty(targetInfo.name())) {
        error = "'" + base + "' accessor suppressed in '" + splitPath[i - 1] + "'";
      }
      else {
        error = null;
      }
      if (error != null) {
        if (intf) { // warn only once
          addWarning(format("%s#%s(\"%s\"): %s", startRule.getName(), ruleMethodInfo.name(), ruleMethodInfo.path(), error));
        }
        return;
      }

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

      targetRule = targetInfo.rule(); // next accessors
      cardinality = targetInfo.cardinality();
      totalNullable |= cardinality.optional();

      // list item
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
        if (cardinality != AT_LEAST_ONE || !index.equals("0")) { // range check
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
        totalNullable |= cardinality.optional();
      }
    }

    MethodSymbol.Builder b = userAccessorSignature(startRule, ruleMethodInfo, intf);
    if (b == null) return; // path already validated above; defensive
    if (!intf && superDeclares(ruleInfo(startRule).intfClass(), helperFor(KnownAttribute.MIXIN), b.name, 0)) {
      out(shorten(JavaBnfConstants.OVERRIDE_ANNO));
    }
    renderMethodHeader(b, intf);

    if (!intf) {
      out(sb.toString());
      out("}");
    }
    newLine();
  }

  /**
   * Emits a method that delegates to a mixin or {@code psiImplUtil} static helper, mirroring its
   * signature, generics, annotations, and exceptions. {@code isInPsiUtil=true} drops the first
   * helper parameter (the {@code this} target) when forwarding the call.
   */
  private void generateUtilMethod(String methodName,
                                  NavigatablePsiElement method,
                                  boolean intf,
                                  boolean isInPsiUtil,
                                  Set<String> visited) {
    MethodSymbol.Builder b = mixinSignatureFromSource(methodName, method, isInPsiUtil, intf, visited);
    if (b == null) return;
    if (!intf) out(shorten(JavaBnfConstants.OVERRIDE_ANNO));
    renderMethodHeader(b, intf);
    if (!intf) {
      String implUtilRef = shorten(StringUtil.notNullize(myPsiImplUtilClass, KnownAttribute.PSI_IMPL_UTIL_CLASS.getName()));
      StringBuilder args = new StringBuilder();
      for (ParameterSymbol.Builder p : b.parameters) {
        args.append(", ").append(p.name);
      }
      out("%s%s.%s(this%s);", "void".equals(b.returnType) ? "" : "return ", implUtilRef, methodName, args);
      out("}");
    }
    newLine();
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
