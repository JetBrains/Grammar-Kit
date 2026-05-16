/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ObjectUtils;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import org.intellij.grammar.BnfPathsResolution;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.generator.NodeCalls.*;
import org.intellij.grammar.generator.java.JavaBnfConstants;
import org.intellij.grammar.generator.java.JavaNameRenderer;
import org.intellij.grammar.generator.java.JavaNameShortener;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.intellij.grammar.util.Case;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

import static com.intellij.util.containers.ContainerUtil.map;
import static java.lang.String.format;
import static java.util.stream.Collectors.*;
import static org.intellij.grammar.analysis.BnfFirstNextAnalyzer.BNF_MATCHES_ANY;
import static org.intellij.grammar.analysis.BnfFirstNextAnalyzer.BNF_MATCHES_EOF;
import static org.intellij.grammar.generator.CommonBnfConstants.RECOVER_AUTO;
import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.RuleGraphHelper.hasElementType;
import static org.intellij.grammar.generator.RuleGraphHelper.hasPsiClass;
import static org.intellij.grammar.psi.BnfAst.*;
import static org.intellij.grammar.psi.BnfAttributes.getAttribute;
import static org.intellij.grammar.psi.BnfAttributes.getRootAttribute;
import static org.intellij.grammar.psi.BnfTypes.*;


/**
 * {@link ParserGenerator} implementation that emits the Java parser source(s) for a BNF file.
 * One file is produced per distinct {@link KnownAttribute#PARSER_CLASS} value: file header,
 * imports, class declaration, root-parser scaffolding (only for the grammar root parser), per-rule
 * parse methods, expression-rule roots, and trailing parser-lambda / meta-method static fields.
 * <p>
 * {@link #generate()} runs the parser pipeline and then delegates PSI emission to a fresh
 * {@link JavaPsiGenerator} constructed against this generator's accumulated state. The same
 * delegation is used by {@link KotlinParserGenerator}, so PSI emission for Java targets has a
 * single owner.
 */
public final class JavaParserGenerator extends ParserGenerator {
  public static final Logger LOG = Logger.getInstance(JavaParserGenerator.class);

  private final String myGrammarRoot;
  private final String myGrammarRootParser;
  private final String myParserUtilClass;

  private final String myParserTypeHolderClass;

  private final ExpressionHelper myExpressionHelper;

  public JavaParserGenerator(@NotNull BnfFile psiFile,
                             @NotNull String sourcePath,
                             @NotNull String packagePrefix,
                             @NotNull OutputOpener outputOpener,
                             @NotNull BnfPathsResolution paths) {
    super(psiFile, sourcePath, packagePrefix, "java", outputOpener, new JavaNameRenderer(), paths);

    NameFormat psiInterfaceFormat = NameFormat.forPsiClass(myFile);
    NameFormat implClassFormat = NameFormat.forPsiImplClass(myFile);
    myParserUtilClass = getRootAttribute(myFile, KnownAttribute.PARSER_UTIL_CLASS);
    myParserTypeHolderClass = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);

    myExpressionHelper = new ExpressionHelper(myFile, myGraphHelper, this::addWarning);

    List<BnfRule> rules = psiFile.getRules();
    BnfRule rootRule = rules.isEmpty() ? null : rules.get(0);
    myGrammarRoot = rootRule == null ? null : rootRule.getName();
    for (BnfRule r : rules) {
      String ruleName = r.getName();
      boolean noPsi = !hasPsiClass(r);
      myRuleInfos.put(ruleName, new RuleInfo(
        ruleName, BnfRules.isFake(r),
        getElementType(r), getAttribute(r, KnownAttribute.PARSER_CLASS),
        noPsi ? null : getAttribute(r, KnownAttribute.PSI_PACKAGE),
        noPsi ? null : getAttribute(r, KnownAttribute.PSI_IMPL_PACKAGE),
        noPsi ? null : CommonRendererUtils.getRulePsiClassName(r, psiInterfaceFormat),
        noPsi ? null : CommonRendererUtils.getRulePsiClassName(r, implClassFormat),
        noPsi ? null : getAttribute(r, KnownAttribute.MIXIN),
        noPsi ? null : getAttribute(r, KnownAttribute.STUB_CLASS)));
    }
    myGrammarRootParser = rootRule == null ? null : ruleInfo(rootRule).parserClass;

    calcFakeRulesWithType();
    calcAbstractRules();
  }

  /** Marks rules that are reused as another rule's {@code elementType}, so they keep an element-type entry even when {@code fake}. */
  private void calcFakeRulesWithType() {
    for (BnfRule rule : myFile.getRules()) {
      BnfRule r = myFile.getRule(getAttribute(rule, KnownAttribute.ELEMENT_TYPE));
      if (r == null) continue;
      ruleInfo(r).isInElementType = true;
    }
  }

  @Override
  public void generate() throws IOException {
    generateParser();
    new JavaPsiGenerator(this).generate();
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

  @Override
  public void generateParser() throws IOException {
    Map<String, Set<RuleInfo>> classified = ContainerUtil.classify(myRuleInfos.values().iterator(), o -> o.parserClass);
    for (String className : ContainerUtil.sorted(classified.keySet())) {
      openOutput(className, myPaths.pathString(KnownAttribute.PARSER_OUTPUT_PATH));
      try {
        generateParser(className, map(classified.get(className), it -> it.name));
      }
      finally {
        closeOutput();
      }
    }
  }

  /**
   * Emits one parser source file: imports, class header, the root-parser scaffolding (only for
   * the grammar root parser), the per-rule parse methods, expression-rule roots, and finally the
   * shared {@code Parser} lambda fields and meta-method fields collected during emission.
   */
  public void generateParser(String parserClass, Collection<String> ownRuleNames) {
    List<String> parserImports = getRootAttribute(myFile, KnownAttribute.PARSER_IMPORTS).asStrings();
    boolean rootParser = parserClass.equals(myGrammarRootParser);
    Set<String> imports = new LinkedHashSet<>();
    if (!G.generateFQN) {
      imports.add(JavaBnfConstants.PSI_BUILDER_CLASS);
      imports.add(JavaBnfConstants.PSI_BUILDER_CLASS + ".Marker");
    }
    else {
      imports.add("#forced");
    }
    imports.add(staticStarImport(myParserTypeHolderClass));
    if (G.generateTokenSets && hasAtLeastOneTokenChoice(myFile, ownRuleNames)) {
      imports.add(staticStarImport(myParserTypeHolderClass + "." + JavaBnfConstants.TOKEN_SET_HOLDER_NAME));
    }
    if (StringUtil.isNotEmpty(myParserUtilClass) &&
        (G.parserApi == GenOptions.ParserApi.Classic || !myParserUtilClass.equals(JavaBnfConstants.GPUB_CLASS))) {
      imports.add(staticStarImport(myParserUtilClass));
    }
    if (!rootParser) {
      imports.add(staticStarImport(myGrammarRootParser));
    }
    else if (!G.generateFQN) {
      imports.addAll(Arrays.asList(JavaBnfConstants.IELEMENTTYPE_CLASS,
                                   JavaBnfConstants.AST_NODE_CLASS,
                                   JavaBnfConstants.TOKEN_SET_CLASS));
                                   imports.addAll(List.of(JavaBnfConstants.PSI_PARSER_CLASS,
                                   JavaBnfConstants.LIGHT_PSI_PARSER_CLASS));
    }
    imports.addAll(parserImports);

    generateClassHeader(parserClass, imports,
                        JavaBnfConstants.SUPPRESS_WARNINGS_ANNO + "({\"SimplifiableIfStatement\", \"UnusedAssignment\"})",
                        TypeKind.CLASS, "",
                        rootParser ? JavaBnfConstants.PSI_PARSER_CLASS : "",
                        rootParser ? JavaBnfConstants.LIGHT_PSI_PARSER_CLASS : "");

    if (rootParser) {
      generateRootParserContent();
    }
    for (String ruleName : ownRuleNames) {
      BnfRule rule = Objects.requireNonNull(myFile.getRule(ruleName));
      if (BnfRules.isExternal(rule) || BnfRules.isFake(rule)) continue;
      if (myExpressionHelper.getExpressionInfo(rule) != null) continue;
      out("/* ********************************************************** */");
      generateNode(rule, rule.getExpression(), R.getFuncName(rule), new HashSet<>());
      newLine();
    }
    for (String ruleName : ownRuleNames) {
      BnfRule rule = myFile.getRule(ruleName);
      ExpressionInfo info = myExpressionHelper.getExpressionInfo(rule);
      if (info != null && info.rootRule == rule) {
        out("/* ********************************************************** */");
        ExpressionGeneratorHelper.generateExpressionRoot(info, this, R);
        newLine();
      }
    }
    boolean addNewLine = !myParserLambdas.isEmpty() && !myMetaMethodFields.isEmpty();
    generateParserLambdas(parserClass);
    if (addNewLine) newLine();
    generateMetaMethodFields();
    out("}");
  }

  /**
   * Emits the {@code static final Parser} fields collected in {@link #myParserLambdas},
   * de-duplicating identical bodies so equal lambdas share a single field reference.
   */
  private void generateParserLambdas(@NotNull String parserClass) {
    Map<String, String> reversedLambdas = new HashMap<>();
    take(myParserLambdas).forEach((name, body) -> {
      String call = reversedLambdas.get(body);
      if (call == null) {
        call = generateParserInstance(body);
        reversedLambdas.put(body, name);
      }
      String parserClassShortName = "Parser";
      out("static final %s %s = %s;", parserClassShortName, name, call);
      myRenderedLambdas.put(name, parserClass);
    });
  }

  /** Renders {@code body} as a {@code Parser} instance — a Java 7+ lambda or, for {@code javaVersion <= 6}, an anonymous class. */
  private @NotNull String generateParserInstance(@NotNull String body) {
    return G.javaVersion > 6
           ? format("(%s, %s) -> %s", N.builder, N.level, body)
           : format("new Parser() {\npublic boolean parse(%s %s, int %s) {\nreturn %s;\n}\n}",
                    shorten(JavaBnfConstants.PSI_BUILDER_CLASS), N.builder, N.level, body);
  }

  /** Emits cached static {@code Parser} fields for parameter-free meta-method calls collected in {@link #myMetaMethodFields}. */
  private void generateMetaMethodFields() {
    String parserClassShortName = "Parser";
    take(myMetaMethodFields).forEach((field, call) -> out(String.format("private static final %s %s = %s;", parserClassShortName, field, call)));
  }

  /**
   * Emits the root parser body: {@code parse}/{@code parseLight} entry points, the
   * {@code parse_root_} dispatcher (with extra-root branches for rules marked
   * {@code extraRoot=true}), and the static {@code EXTENDS_SETS_} array.
   */
  private void generateRootParserContent() {
    BnfRule rootRule = myFile.getRule(myGrammarRoot);
    List<BnfRule> extraRoots = new ArrayList<>();
    for (String ruleName : myRuleInfos.keySet()) {
      BnfRule rule = Objects.requireNonNull(myFile.getRule(ruleName));
      if (getAttribute(rule, KnownAttribute.ELEMENT_TYPE) != null) continue;
      if (!hasElementType(rule)) continue;
      if (BnfRules.isFake(rule) || BnfRules.isMeta(rule)) continue;
      ExpressionInfo info = myExpressionHelper.getExpressionInfo(rule);
      if (info != null && info.rootRule != rule) continue;
      if (!Boolean.TRUE.equals(getAttribute(rule, KnownAttribute.EXTRA_ROOT))) continue;
      extraRoots.add(rule);
    }

    List<Set<String>> extendsSet = buildExtendsSet(myGraphHelper.getRuleExtendsMap());
    boolean generateExtendsSets = !extendsSet.isEmpty();
    String shortET = shorten(JavaBnfConstants.IELEMENTTYPE_CLASS);
    String shortAN = shorten(JavaBnfConstants.AST_NODE_CLASS);
    String shortPB = shorten(JavaBnfConstants.PSI_BUILDER_CLASS);
    String shortTS = shorten(JavaBnfConstants.TOKEN_SET_CLASS);
    String shortMarker = G.generateFQN ? JavaBnfConstants.PSI_BUILDER_CLASS + ".Marker" : "Marker";
    out("public %s parse(%s %s, %s %s) {", shortAN, shortET, N.root, shortPB, N.builder);
    out("parseLight(%s, %s);", N.root, N.builder);
    out("return %s.getTreeBuilt();", N.builder);
    out("}");
    newLine();
    out("public void parseLight(%s %s, %s %s) {", shortET, N.root, shortPB, N.builder);
    out("boolean %s;", N.result);
    out("%s = adapt_builder_(%s, %s, %s, %s);", N.builder, N.root, N.builder, "this", generateExtendsSets ? "EXTENDS_SETS_" : null);
    out("%s %s = enter_section_(%s, 0, _COLLAPSE_, null);", shortMarker, N.marker, N.builder);
    out("%s = parse_root_(%s, %s);", N.result, N.root, N.builder);
    out("exit_section_(%s, 0, %s, %s, %s, true, TRUE_CONDITION);", N.builder, N.marker, N.root, N.result);
    out("}");
    newLine();
    out("protected boolean parse_root_(%s %s, %s %s) {", shortET, N.root, shortPB, N.builder);
    out("return parse_root_(%s, %s, 0);", N.root, N.builder);
    out("}");
    newLine();
    out("static boolean parse_root_(%s %s, %s %s, int %s) {", shortET, N.root, shortPB, N.builder, N.level);
    if (extraRoots.isEmpty()) {
      out("return %s;", rootRule == null ? "false" : generateNodeCall(rootRule, null, myGrammarRoot).render(R));
    }
    else {
      boolean first = true;
      out("boolean %s;", N.result);
      for (BnfRule rule : extraRoots) {
        String elementType = getElementType(rule);
        out("%sif (%s == %s) {", first ? "" : "else ", N.root, elementType);
        String nodeCall = generateNodeCall(ObjectUtils.notNull(rootRule, rule), null, rule.getName()).render(R);
        out("%s = %s;", N.result, nodeCall);
        out("}");
        if (first) first = false;
      }
      out("else {");
      out("%s = %s;", N.result, rootRule == null ? "false" : generateNodeCall(rootRule, null, myGrammarRoot).render(R));
      out("}");
      out("return %s;", N.result);
    }
    out("}");
    newLine();
    if (generateExtendsSets) {
      out("public static final %s[] EXTENDS_SETS_ = new %s[] {", shortTS, shortTS);
      StringBuilder sb = new StringBuilder();
      for (Set<String> elementTypes : extendsSet) {
        int i = 0;
        for (String elementType : elementTypes) {
          if (i > 0) sb.append(i % 4 == 0 ? ",\n" : ", ");
          sb.append(elementType);
          i++;
        }
        out("create_token_set_(%s),", sb);
        sb.setLength(0);
      }
      out("};");
      newLine();
    }
  }

  /**
   * Meta-methods are methods that take several {@link org.intellij.grammar.parser.GeneratedParserUtilBase.Parser Parser} instances as parameters and return another {@code Parser} instance.
   *
   * @param isRule whether meta-method may be used from another parser classes, and therefore should be accessible,
   *               e.g. it is {@code true} for {@code meta rule ::= <<p>>},
   *               and it is {@code false} for nested in-place method generated
   *               for {@code <<p>> | some} in {@code meta rule ::= (<<p>> | some)* }.
   */
  private void generateMetaMethod(@NotNull String methodName, @NotNull List<String> parameterNames, boolean isRule) {
    String parserClassShortName = "Parser";
    String parameterList = parameterNames.stream().map(it -> parserClassShortName + " " + it).collect(joining(", "));
    String argumentList = String.join(", ", parameterNames);
    String metaParserMethodName = R.getWrapperParserMetaMethodName(methodName);
    String call = format("%s(%s, %s + 1, %s)", methodName, N.builder, N.level, argumentList);
    // @formatter:off
    out("%sstatic %s %s(%s) {", isRule ? "" : "private ", parserClassShortName, metaParserMethodName, parameterList);
      out("return %s;", generateParserInstance(call));
    out("}");
    // @formatter:on
  }

  /**
   * Emits the parser method body for {@code rule}'s expression. Handles all the generation
   * concerns: meta-method wrapping, single-token shortcut returns, FIRST-set guards, section
   * enter/exit with the right modifier flags ({@code _LEFT_}, {@code _COLLAPSE_}, {@code _AND_},
   * {@code _NOT_}, {@code _UPPER_}), pin-on-success tracking for sequences, choice/sequence/
   * optional/zero-or-more/one-or-more/and-not dispatch, hooks, and {@code recoverWhile} (with
   * automatic, meta-parameterized, and explicit predicate variants). Recursively generates child
   * helper methods after the main body.
   */
  @SuppressWarnings("DuplicatedCode")
  @Override
  void generateNode(BnfRule rule, BnfExpression initialNode, String funcName, Set<BnfExpression> visited) {
    boolean isRule = initialNode.getParent() == rule;
    BnfExpression node = getNonTrivialNode(initialNode);

    List<String> metaParameters = collectMetaParametersFormatted(rule, node);
    if (!metaParameters.isEmpty()) {
      if (isRule && isUsedAsArgument(rule) || !isRule && isArgument(initialNode)) {
        generateMetaMethod(funcName, metaParameters, isRule);
        newLine();
      }
    }

    IElementType type = getEffectiveType(node);

    for (String s : StringUtil.split((StringUtil.isEmpty(node.getText()) ? initialNode : node).getText(), "\n")) {
      out("// " + s);
    }
    boolean firstNonTrivial = node == BnfRules.firstNotTrivial(rule);
    boolean isPrivate = !(isRule || firstNonTrivial) || BnfRules.isPrivate(rule) || myGrammarRoot.equals(rule.getName());
    boolean isLeft = firstNonTrivial && BnfRules.isLeft(rule);
    boolean isLeftInner = isLeft && (isPrivate || BnfRules.isInner(rule));
    boolean isUpper = !isPrivate && BnfRules.isUpper(rule);
    String recoverWhile = !firstNonTrivial ? null : getAttribute(rule, KnownAttribute.RECOVER_WHILE);
    Map<String, String> hooks = firstNonTrivial ? getAttribute(rule, KnownAttribute.HOOKS).asMap() : Collections.emptyMap();

    boolean canCollapse = !isPrivate && (!isLeft || isLeftInner) && firstNonTrivial && myGraphHelper.canCollapse(rule);

    String elementType = getElementType(rule);
    String elementTypeRef = !isPrivate && StringUtil.isNotEmpty(elementType) ? elementType : null;

    boolean isSingleNode =
      node instanceof BnfReferenceOrToken || node instanceof BnfLiteralExpression || node instanceof BnfExternalExpression;

    List<BnfExpression> children = isSingleNode ? Collections.singletonList(node) : getChildExpressions(node);
    String frameName = !children.isEmpty() && firstNonTrivial && !BnfRules.isMeta(rule) ? quote(R.getRuleDisplayName(rule, !isPrivate)) : null;

    String extraParameters = metaParameters.stream().map(it -> ", Parser " + it).collect(joining());
    out("%sstatic boolean %s(%s %s, int %s%s) {", !isRule ? "private " : isPrivate ? "" : "public ",
        funcName, shorten(JavaBnfConstants.PSI_BUILDER_CLASS), N.builder, N.level, extraParameters);
    if (isSingleNode) {
      if (isPrivate && !isLeftInner && recoverWhile == null && frameName == null) {
        String nodeCall = generateNodeCall(rule, node, R.getNextName(funcName, 0)).render(R);
        out("return %s;", nodeCall);
        out("}");
        if (node instanceof BnfExternalExpression && ((BnfExternalExpression)node).getExpressionList().size() > 1) {
          generateNodeChildren(rule, funcName, children, visited);
        }
        return;
      }
      else {
        type = BNF_SEQUENCE;
      }
    }

    if (!children.isEmpty()) {
      out("if (!recursion_guard_(%s, %s, \"%s\")) return false;", N.builder, N.level, funcName);
    }

    if (recoverWhile == null && (isRule || firstNonTrivial)) {
      frameName = generateFirstCheck(rule, frameName, getAttribute(rule, KnownAttribute.NAME) == null);
    }

    PinMatcher pinMatcher = new PinMatcher(rule, type, firstNonTrivial ? rule.getName() : funcName);
    boolean pinApplied = false;
    boolean alwaysTrue = children.isEmpty() || type == BNF_OP_OPT || type == BNF_OP_ZEROMORE;
    boolean pinned = pinMatcher.active() && pinMatcher.shouldGenerate(children);
    if (!alwaysTrue) {
      out("boolean %s%s%s;", N.result, children.isEmpty() ? " = true" : "", pinned ? format(", %s", N.pinned) : "");
    }

    List<String> modifierList = new SmartList<>();
    if (canCollapse) modifierList.add("_COLLAPSE_");
    if (isLeftInner) {
      modifierList.add("_LEFT_INNER_");
    }
    else if (isLeft) modifierList.add("_LEFT_");
    if (type == BNF_OP_AND) {
      modifierList.add("_AND_");
    }
    else if (type == BNF_OP_NOT) modifierList.add("_NOT_");
    if (isUpper) modifierList.add("_UPPER_");
    if (modifierList.isEmpty() && (pinned || frameName != null)) modifierList.add("_NONE_");

    boolean sectionRequired = !alwaysTrue || !isPrivate || isLeft || recoverWhile != null;
    boolean sectionRequiredSimple = sectionRequired && modifierList.isEmpty() && recoverWhile == null && frameName == null;
    boolean sectionMaybeDropped = sectionRequiredSimple && type == BNF_CHOICE && elementTypeRef == null &&
                                  !ContainerUtil.exists(children, o -> isRollbackRequired(o, myFile));
    String modifiers = modifierList.isEmpty() ? "_NONE_" : StringUtil.join(modifierList, " | ");
    String shortMarker = G.generateFQN ? JavaBnfConstants.PSI_BUILDER_CLASS + ".Marker" : "Marker";
    if (sectionRequiredSimple) {
      if (!sectionMaybeDropped) {
        out("%s %s = enter_section_(%s);", shortMarker, N.marker, N.builder);
      }
    }
    else if (sectionRequired) {
      boolean shortVersion = frameName == null && elementTypeRef == null;
      if (shortVersion) {
        out("%s %s = enter_section_(%s, %s, %s);", shortMarker, N.marker, N.builder, N.level, modifiers);
      }
      else {
        out("%s %s = enter_section_(%s, %s, %s, %s, %s);", shortMarker, N.marker, N.builder, N.level, modifiers,
            elementTypeRef, frameName);
      }
    }

    final var skip = Ref.create(0);
    for (int i = 0, p = 0, childrenSize = children.size(); i < childrenSize; i++) {
      BnfExpression child = children.get(i);

      NodeCall nodeCall = generateNodeCall(rule, child, R.getNextName(funcName, i));
      if (type == BNF_CHOICE) {
        if (isRule && i == 0 && G.generateTokenSets) {
          ConsumeType consumeType = getEffectiveConsumeType(rule, node, null);
          NodeCall tokenChoice = generateTokenChoiceCall(children, consumeType, funcName);
          if (tokenChoice != null) {
            out("%s = %s;", N.result, tokenChoice.render(R));
            break;
          }
        }
        out("%s%s = %s;", i > 0 ? format("if (!%s) ", N.result) : "", N.result, nodeCall.render(R));
      }
      else if (type == BNF_SEQUENCE) {
        if (skip.get() == 0) {
          ConsumeType consumeType = getEffectiveConsumeType(rule, node, null);
          nodeCall = generateTokenSequenceCall(children, i, pinMatcher, pinApplied, skip, nodeCall, false, consumeType);
          if (i == 0) {
            out("%s = %s;", N.result, nodeCall.render(R));
          }
          else {
            if (pinApplied && G.generateExtendedPin) {
              if (i == childrenSize - 1) {
                // do not report error for last child
                if (i == p + 1) {
                  out("%s = %s && %s;", N.result, N.result, nodeCall.render(R));
                }
                else {
                  out("%s = %s && %s && %s;", N.result, N.pinned, nodeCall.render(R), N.result);
                }
              }
              else if (i == p + 1) {
                out("%s = %s && report_error_(%s, %s);", N.result, N.result, N.builder, nodeCall.render(R));
              }
              else {
                out("%s = %s && report_error_(%s, %s) && %s;", N.result, N.pinned, N.builder, nodeCall.render(R), N.result);
              }
            }
            else {
              out("%s = %s && %s;", N.result, N.result, nodeCall.render(R));
            }
          }
        }
        else {
          skip.set(skip.get() - 1); // we are inside already generated token sequence
          if (pinApplied && i == p + 1) p++; // shift pinned index as we skip
        }
        if (pinned && !pinApplied && pinMatcher.matches(i, child)) {
          pinApplied = true;
          p = i;
          out("%s = %s; // pin = %s", N.pinned, N.result, pinMatcher.pinValue);
        }
      }
      else if (type == BNF_OP_OPT) {
        out(nodeCall.render(R) + ";");
      }
      else if (type == BNF_OP_ONEMORE || type == BNF_OP_ZEROMORE) {
        if (type == BNF_OP_ONEMORE) {
          out("%s = %s;", N.result, nodeCall.render(R));
        }
        out("while (%s) {", alwaysTrue ? "true" : N.result);
        out("int %s = current_position_(%s);", N.pos, N.builder);
        out("if (!%s) break;", nodeCall.render(R));
        out("if (!empty_element_parsed_guard_(%s, \"%s\", %s)) break;", N.builder, funcName, N.pos);
        out("}");
      }
      else if (type == BNF_OP_AND) {
        out("%s = %s;", N.result, nodeCall.render(R));
      }
      else if (type == BNF_OP_NOT) {
        out("%s = !%s;", N.result, nodeCall.render(R));
      }
      else {
        addWarning("unexpected: " + type);
      }
    }

    if (sectionRequired) {
      String resultRef = alwaysTrue ? "true" : N.result;
      if (!hooks.isEmpty()) {
        for (Map.Entry<String, String> entry : hooks.entrySet()) {
          String hookName = CommonRendererUtils.toIdentifier(entry.getKey(), null, Case.UPPER);
          out("register_hook_(%s, %s, %s);", N.builder, hookName, entry.getValue());
        }
      }
      if (sectionRequiredSimple) {
        if (!sectionMaybeDropped) {
          out("exit_section_(%s, %s, %s, %s);", N.builder, N.marker, elementTypeRef, resultRef);
        }
      }
      else {
        String pinnedRef = pinned ? N.pinned : "false";
        String recoverCall;
        if (recoverWhile != null) {
          BnfRule predicateRule = myFile.getRule(recoverWhile);
          if (RECOVER_AUTO.equals(recoverWhile)) {
            recoverCall = generateAutoRecoverCall(rule);
          }
          else if (BnfRules.isMeta(rule) && GrammarUtil.isDoubleAngles(recoverWhile)) {
            recoverCall = formatMetaParamName(recoverWhile.substring(2, recoverWhile.length() - 2));
          }
          else {
            recoverCall = predicateRule == null ? null : generateWrappedNodeCall(rule, null, predicateRule.getName()).render(R);
          }
        }
        else {
          recoverCall = null;
        }
        out("exit_section_(%s, %s, %s, %s, %s, %s);", N.builder, N.level, N.marker, resultRef, pinnedRef,
            recoverCall);
      }
    }

    out("return %s;", alwaysTrue ? "true" : N.result + (pinned ? format(" || %s", N.pinned) : ""));
    out("}");
    generateNodeChildren(rule, funcName, children, visited);
  }

  /** Java rendering of {@code !nextTokenIsFast(builder, t1, t2, ...)} for the auto-recovery predicate. */
  @Override
  public StringBuilder generateAutoRecoveryCall(List<String> tokenTypes){
    StringBuilder sb = new StringBuilder(format("!nextTokenIsFast(%s, ", N.builder));
    appendTokenTypes(sb, tokenTypes);
    sb.append(")");
    return sb;
  }

  /**
   * Emits the inline FIRST-set check, grouped by {@link ConsumeType} so each group becomes one
   * {@code nextTokenIs*(...)} call AND-ed together. Skipped when the FIRST set contains
   * "matches any/eof", a sub-rule, or anything that doesn't reduce to a token element type, or
   * when the set is larger than {@link GenOptions#generateFirstCheck}.
   */
  @SuppressWarnings("DuplicatedCode")
  @Override
  public String generateFirstCheck(@NotNull BnfRule rule, String frameName, boolean skipIfOne) {
    if (G.generateFirstCheck <= 0) return frameName;
    Set<BnfExpression> firstSet = myFirstNextAnalyzer.calcFirst(rule);
    ConsumeType ruleConsumeType = getRuleConsumeType(rule, null);
    Map<String, ConsumeType> firstElementTypes = new TreeMap<>();
    for (BnfExpression expression : firstSet) {
      if (expression == BNF_MATCHES_EOF || expression == BNF_MATCHES_ANY) return frameName;

      String expressionString = BnfFirstNextAnalyzer.asString(expression);
      if (myFile.getRule(expressionString) != null) continue;

      String t = firstToElementType(expressionString);
      if (t == null) return frameName;

      ConsumeType childConsumeType = getRuleConsumeType(Objects.requireNonNull(BnfRules.of(expression)), rule);
      ConsumeType consumeType = ConsumeType.min(ruleConsumeType, childConsumeType);
      firstElementTypes.compute(t, (k, existing) -> ConsumeType.max(existing, consumeType));
    }
    if (firstElementTypes.isEmpty()) return frameName;

    int allTokensCount = firstElementTypes.size();
    // do not include frameName if FIRST is known and its size is 1
    boolean dropFrameName = skipIfOne && allTokensCount == 1;
    if (allTokensCount <= G.generateFirstCheck) {
      Map<ConsumeType, List<String>> grouped = firstElementTypes.entrySet().stream().collect(groupingBy(
        Map.Entry::getValue,
        () -> new EnumMap<>(ConsumeType.class),
        mapping(Map.Entry::getKey, toList())
      ));
      String condition = grouped.entrySet().stream().map(entry -> {
        ConsumeType consumeType = entry.getKey();
        List<String> tokenTypes = entry.getValue();
        StringBuilder sb = new StringBuilder("!nextTokenIs")
          .append(consumeType.getMethodSuffix()).append("(").append(N.builder).append(", ");
        if (!dropFrameName && consumeType == ConsumeType.DEFAULT) sb.append(StringUtil.notNullize(frameName, "\"\"")).append(", ");
        appendTokenTypes(sb, tokenTypes);
        sb.append(")");
        return sb;
      }).collect(joining(" &&\n  ", "if (", ") return false;"));
      out(condition);
    }

    return dropFrameName && StringUtil.isEmpty(getAttribute(rule, KnownAttribute.NAME)) ? null : frameName;
  }

  /**
   * Returns the consume type that should be used when calling {@code rule}. A consume-type
   * forced by an enclosing expression rule is honored only when {@code contextRule} is itself
   * an expression rule; otherwise the rule's own default is used.
   */
  private @NotNull ConsumeType getRuleConsumeType(@NotNull BnfRule rule, @Nullable BnfRule contextRule) {
    ConsumeType forcedConsumeType = ExpressionGeneratorHelper.fixForcedConsumeType(myExpressionHelper, rule, null, null);
    if (forcedConsumeType != null && contextRule != null && myExpressionHelper.getExpressionInfo(contextRule) == null) {
      // do not force child expr consume-type in a non-expr context
      forcedConsumeType = null;
    }
    return ObjectUtils.chooseNotNull(forcedConsumeType, ConsumeType.forRule(rule));
  }

  /** Collects meta-parameter placeholders ({@code <<x>>}) used in {@code expression} and returns their generated parameter names. */
  private @NotNull List<String> collectMetaParametersFormatted(@NotNull BnfRule rule, @Nullable BnfExpression expression) {
    if (expression == null) return Collections.emptyList();
    return map(GrammarUtil.collectMetaParameters(rule, expression),
               it -> formatMetaParamName(it.substring(2, it.length() - 2)));
  }

  @Override
  @NotNull NodeCall generateTokenSequenceCall(List<BnfExpression> children,
                                                      int startIndex,
                                                      PinMatcher pinMatcher,
                                                      boolean pinApplied,
                                                      Ref<Integer> skip,
                                                      @NotNull NodeCall nodeCall,
                                                      boolean rollbackOnFail,
                                                      ConsumeType consumeType) {
    if (startIndex == children.size() - 1 || !(nodeCall instanceof ConsumeTokenCall)) return nodeCall;
    List<String> list = new ArrayList<>();
    int pin = pinApplied ? -1 : 0;
    for (int i = startIndex, len = children.size(); i < len; i++) {
      BnfExpression child = children.get(i);
      String text = child.getText();
      String tokenName = child instanceof BnfStringLiteralExpression ? getTokenName(GrammarUtil.unquote(text)) :
                         child instanceof BnfReferenceOrToken && myFile.getRule(text) == null ? text : null;
      if (tokenName == null) break;
      list.add(getElementType(tokenName));
      if (!pinApplied && pinMatcher.matches(i, child)) {
        pin = i - startIndex + 1;
        pinApplied = true;
      }
    }
    if (list.size() < 2) return nodeCall;
    skip.set(list.size() - 1);
    String consumeMethodName = (rollbackOnFail ? ("parseTokens") : ("consumeTokens")) +
                               (consumeType == ConsumeType.SMART ? consumeType.getMethodSuffix() : "");
    return new ConsumeTokensCall(consumeMethodName, N.builder, pin, list);
  }

  @Override
  @NotNull NodeCall instantiateTokenChoiceCall(@NotNull ConsumeType consumeType, @NotNull String tokenSetName){
    return new ConsumeTokenChoiceCall(consumeType, tokenSetName, N.builder);
  }

  /**
   * Java implementation of {@link ParserGenerator#generateNodeCall}: tokens become
   * {@code consumeToken*}, sub-rules become method calls (cross-class qualified when needed),
   * expression rules become {@code ExpressionMethodCall} with the priority offset,
   * external rules go through {@link #generateExternalCall}, and meta-parameter references
   * become {@code MetaParameterCall}. Falls through to a plain method call to {@code nextName}
   * for "everything else".
   */
  @Override
  @NotNull
  NodeCall generateNodeCall(@NotNull BnfRule rule,
                            @Nullable BnfExpression node,
                            @NotNull String nextName,
                            @Nullable ConsumeType forcedConsumeType) {
    IElementType type = node == null ? BNF_REFERENCE_OR_TOKEN : getEffectiveType(node);
    String text = node == null ? nextName : node.getText();

    if (type == BNF_STRING) {
      String value = GrammarUtil.unquote(text);
      String attributeName = getTokenName(value);
      ConsumeType consumeType = getEffectiveConsumeType(rule, node, forcedConsumeType);
      if (attributeName != null) {
        return generateConsumeToken(consumeType, attributeName);
      }
      return generateConsumeTextToken(consumeType,
                                      text.startsWith("\"") ? value : StringUtil.escapeStringCharacters(value),
                                      N.builder);
    }
    else if (type == BNF_NUMBER) {
      ConsumeType consumeType = getEffectiveConsumeType(rule, node, forcedConsumeType);
      return generateConsumeTextToken(consumeType, text, N.builder);
    }
    else if (type == BNF_REFERENCE_OR_TOKEN) {
      String value = GrammarUtil.stripQuotesAroundId(text);
      BnfRule subRule = myFile.getRule(value);
      if (subRule != null) {
        if (BnfRules.isExternal(subRule)) {
          return generateExternalCall(rule, GrammarUtil.getExternalRuleExpressions(subRule), nextName);
        }
        else {
          ExpressionInfo info = ExpressionGeneratorHelper.getInfoForExpressionParsing(myExpressionHelper, subRule);
          BnfRule rr = info != null ? info.rootRule : subRule;
          String method = R.getFuncName(rr);
          String parserClass = ruleInfo(rr).parserClass;
          String parserClassName = StringUtil.getShortName(parserClass);
          boolean renderClass = !parserClass.equals(myGrammarRootParser) && !parserClass.equals(ruleInfo(rule).parserClass);
          if (info == null) {
            return new MethodCall(renderClass, parserClassName, method, N.builder, N.level);
          }
          else {
            if (renderClass) {
              method = StringUtil.getQualifiedName(parserClassName, method);
            }
            return new ExpressionMethodCall(method, N.builder, N.level, info.getPriority(subRule) - 1);
          }
        }
      }
      // allow token usage by registered token name instead of token text
      if (!mySimpleTokens.containsKey(text) && !mySimpleTokens.containsValue(text)) {
        mySimpleTokens.put(text, null);
      }
      ConsumeType consumeType = getEffectiveConsumeType(rule, node, forcedConsumeType);
      return generateConsumeToken(consumeType, text);
    }
    else if (isTokenSequence(rule, node)) {
      ConsumeType consumeType = getEffectiveConsumeType(rule, node, forcedConsumeType);
      PinMatcher pinMatcher = new PinMatcher(rule, type, nextName);
      List<BnfExpression> childExpressions = getChildExpressions(node);
      BnfExpression firstElement = ContainerUtil.getFirstItem(childExpressions);
      NodeCall nodeCall = generateNodeCall(rule, firstElement, R.getNextName(nextName, 0), consumeType);
      for (PsiElement e : childExpressions) {
        String t = e instanceof BnfStringLiteralExpression ? GrammarUtil.unquote(e.getText()) : e.getText();
        if (!mySimpleTokens.containsKey(t) && !mySimpleTokens.containsValue(t)) {
          mySimpleTokens.put(t, null);
        }
      }
      return generateTokenSequenceCall(childExpressions, 0, pinMatcher, false, Ref.create(0), nodeCall, true, consumeType);
    }
    else if (type == BNF_EXTERNAL_EXPRESSION) {
      List<BnfExpression> expressions = ((BnfExternalExpression)node).getExpressionList();
      if (expressions.size() == 1 && BnfRules.isMeta(rule)) {
        return new MetaParameterCall(formatMetaParamName(expressions.get(0).getText()), N.builder, N.level);
      }
      else {
        return generateExternalCall(rule, expressions, nextName);
      }
    }
    else {
      List<String> extraArguments = collectMetaParametersFormatted(rule, node);
      if (extraArguments.isEmpty()) {
        return new MethodCall(false, StringUtil.getShortName(ruleInfo(rule).parserClass), nextName, N.builder, N.level);
      }
      else {
        return new MetaMethodCall(null, nextName, N.builder, N.level, map(extraArguments, MetaParameterArgument::new));
      }
    }
  }

  /**
   * Resolves the consume type to use when emitting a call for {@code node} inside {@code rule}.
   * A non-leading sequence child whose preceding alternatives can also start at the same token
   * is forced back to {@link ConsumeType#DEFAULT} to avoid swallowing tokens belonging to a
   * sibling alternative.
   */
  private @NotNull ConsumeType getEffectiveConsumeType(@NotNull BnfRule rule,
                                                       @Nullable BnfExpression node,
                                                       @Nullable ConsumeType forcedConsumeType) {
    if (forcedConsumeType == ConsumeType.DEFAULT) return ConsumeType.DEFAULT;
    PsiElement parent = node == null ? null : node.getParent();

    if (forcedConsumeType == null && parent instanceof BnfSequence &&
        ContainerUtil.getFirstItem(((BnfSequence)parent).getExpressionList()) != node) {
      Set<BnfExpression> expressions = BnfFirstNextAnalyzer.createAnalyzer(false, false, o -> o != parent)
        .calcFirst((BnfExpression)parent);
      if (expressions.size() != 1 || expressions.iterator().next() != node) {
        return ConsumeType.DEFAULT;
      }
    }
    ConsumeType fixed = ExpressionGeneratorHelper.fixForcedConsumeType(myExpressionHelper, rule, node, forcedConsumeType);
    return fixed != null ? fixed : ConsumeType.forRule(rule);
  }

  @NotNull NodeCall generateExternalCall(@NotNull BnfRule rule,
                                         @NotNull List<BnfExpression> expressions,
                                         @NotNull String nextName) {
    return generateExternalCall(rule, expressions, nextName, N.builder);
  }

  @Override
  protected boolean useTargetClassName(@NotNull BnfRule rule, String parserClass) {
    return !parserClass.equals(myGrammarRootParser) && !parserClass.equals(ruleInfo(rule).parserClass);
  }

  @Override
  boolean useMethodCall(NodeCall nodeCall) {
    return nodeCall instanceof MethodCall && G.javaVersion > 6;
  }


  /** Builds a {@code consumeToken(builder, ELEMENT_TYPE)} call and records {@code tokenName} as used in the grammar. */
  private @NotNull NodeCall generateConsumeToken(@NotNull ConsumeType consumeType, @NotNull String tokenName) {
    myTokensUsedInGrammar.add(tokenName);
    return new ConsumeTokenCall(consumeType, getElementType(tokenName), N.builder);
  }

  /** Builds a {@code consumeToken(builder, "literal")} call for tokens given by their literal text rather than name. */
  private static @NotNull NodeCall generateConsumeTextToken(@NotNull ConsumeType consumeType,
                                                            @NotNull String tokenText,
                                                            @NotNull String stateHolder) {
    return new ConsumeTokenCall(consumeType, "\"" + tokenText + "\"", stateHolder);
  }
}