/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ObjectUtils;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.containers.MultiMap;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.generator.NodeCalls.*;
import org.intellij.grammar.generator.kotlin.KotlinBnfConstants;
import org.intellij.grammar.generator.kotlin.KotlinNameShortener;
import org.intellij.grammar.generator.kotlin.KotlinPlatformConstants;
import org.intellij.grammar.generator.kotlin.KotlinRenderer;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.grammar.parser.GeneratedParserUtilBase.Parser;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.util.containers.ContainerUtil.map;
import static java.lang.String.format;
import static org.intellij.grammar.analysis.BnfFirstNextAnalyzer.BNF_MATCHES_ANY;
import static org.intellij.grammar.analysis.BnfFirstNextAnalyzer.BNF_MATCHES_EOF;
import static org.intellij.grammar.generator.CommonBnfConstants.RECOVER_AUTO;
import static org.intellij.grammar.generator.ExpressionGeneratorHelper.CONSUME_TYPE_OVERRIDE;
import static org.intellij.grammar.generator.ExpressionGeneratorHelper.findOperators;
import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.java.JavaNameShortener.getRawClassName;
import static org.intellij.grammar.psi.BnfTypes.*;


/// A Kotlin parser generator implementation.
///
/// Implementation notes:
/// 1. A method's name starts with `generate` if it either calls the
///   [Generator#out] method or calls another `generate*` method.
/// 2. `build*` methods are generally used for building strings that
///   are then going to be outputted via one of the `generate*`
///   methods to the file.
public final class KotlinParserGenerator extends Generator {
  public static final @NotNull Logger LOG = Logger.getInstance(KotlinParserGenerator.class);
  private static final @NotNull String HORIZONTAL_SEPARATOR = "/* ********************************************************** */";
  private final @NotNull KotlinPlatformConstants C;
  private final @NotNull Names N;
  /**
   * Name of the class containing runtime parser utilities
   * (default: {@link GeneratedParserUtilBase}).
   */
  private final @NotNull String myParserRuntimeName;
  /**
   * Name of the class containing all the generated element types.
   */
  private final @NotNull String myElementTypesHolderName;
  private final @NotNull Map<String, RuleInfo> myRuleInfos = new TreeMap<>();
  private final @NotNull Map<String, String> myParserLambdas = new HashMap<>();       // field name -> body
  private final @NotNull Map<String, String> myRenderedLambdas = new HashMap<>();     // field name -> parser class FQN
  private final @NotNull Set<String> myInlinedChildNodes = new HashSet<>();
  /**
   * All token elements that the generator has deemed necessary to make it
   * work.
   */
  private final @NotNull Set<String> myTokensUsedInGrammar = new LinkedHashSet<>();
  /**
   * Some meta-method calls use only static parsers as arguments,
   * i.e. they don't reference meta-method parameters,
   * we want to cache them in static fields.
   * <p/>
   * Mapping: <code> meta field name -> meta method call </code>
   */
  private final Map<String, String> myMetaMethodFields = new HashMap<>();
  private final Map<String, Collection<String>> myTokenSets = new TreeMap<>();
  private final Map<String, String> mySimpleTokens;
  private final RuleGraphHelper myGraphHelper;
  private final ExpressionHelper myExpressionHelper;
  private final BnfFirstNextAnalyzer myFirstNextAnalyzer;
  private final JavaHelper myJavaHelper;
  private final @NotNull KotlinRenderer R = KotlinRenderer.INSTANCE;

  public KotlinParserGenerator(@NotNull BnfFile psiFile,
                               @NotNull String sourcePath,
                               @NotNull String outputPath,
                               @NotNull String packagePrefix,
                               @NotNull OutputOpener outputOpener
  ) {
    super(psiFile, sourcePath, outputPath, packagePrefix, "kt", outputOpener);

    N = G.names;
    C = KotlinPlatformConstants.DEFAULT_CONSTANTS;

    // TODO: consider creating kotlin specific attributes for this
    myParserRuntimeName = KotlinBnfConstants.KT_PARSER_RUNTIME_CLASS;/*getRootAttribute(myFile, KnownAttribute.PARSER_UTIL_CLASS);*/
    myElementTypesHolderName = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);

    mySimpleTokens = new LinkedHashMap<>(RuleGraphHelper.getTokenTextToNameMap(myFile));
    myGraphHelper = RuleGraphHelper.getCached(myFile);
    myExpressionHelper = new ExpressionHelper(myFile, myGraphHelper, this::addWarning);
    myFirstNextAnalyzer = BnfFirstNextAnalyzer.createAnalyzer(true);
    myJavaHelper = JavaHelper.getJavaHelper(myFile);

    for (final var rule : psiFile.getRules()) {
      final var ruleName = rule.getName();
      final var isNoPsi = !RuleGraphHelper.hasPsiClass(rule);
      final var ruleInfo = new RuleInfo(
        ruleName,
        Rule.isFake(rule),
        getElementType(rule),
        getAttribute(rule, KnownAttribute.PARSER_CLASS),
        // TODO: potentially make a kotlin-specific attribute for this vvv
        isNoPsi ? null : getAttribute(rule, KnownAttribute.PSI_PACKAGE),
        // don't need any of the below
        null,
        null,
        null,
        null,
        null
      );
      myRuleInfos.put(ruleName, ruleInfo);
    }

    calcFakeRulesWithType();
    calcRulesStubNames();
    calcAbstractRules();
  }

  private static @NotNull NodeCall getConsumeTextToken(@NotNull ConsumeType consumeType, @NotNull String tokenText) {
    return new ConsumeTokenCall(consumeType, "\"" + tokenText + "\"");
  }

  private @NotNull String shortElementTypesHolderName() {
    return StringUtil.getShortName(myElementTypesHolderName);
  }

  private void calcFakeRulesWithType() {
    for (final var rule : myFile.getRules()) {
      BnfRule r = myFile.getRule(getAttribute(rule, KnownAttribute.ELEMENT_TYPE));
      if (r == null) continue;
      getRuleInfo(r).isInElementType = true;
    }
  }

  private void calcRulesStubNames() {
    for (BnfRule rule : myFile.getRules()) {
      RuleInfo info = getRuleInfo(rule);
      String stubClass = info.stub;
      if (stubClass == null) {
        BnfRule topSuper = getEffectiveSuperRule(myFile, rule);
        stubClass = topSuper == null ? null : getRuleInfo(topSuper).stub;
      }
      BnfRule topSuper = getEffectiveSuperRule(myFile, rule);
      String superRuleClass = topSuper == null ? getRootAttribute(myFile, KnownAttribute.EXTENDS) :
                              topSuper == rule ? getAttribute(rule, KnownAttribute.EXTENDS) :
                              getRuleInfo(topSuper).intfClass;
      String implSuper = StringUtil.notNullize(info.mixin, superRuleClass);
      String implSuperRaw = getRawClassName(implSuper);
      String stubName =
        StringUtil.isNotEmpty(stubClass) ? stubClass :
        implSuper.indexOf("<") < implSuper.indexOf(">") &&
        !myJavaHelper.findClassMethods(implSuperRaw, JavaHelper.MethodType.INSTANCE, "getParentByStub", 0).isEmpty() ?
        implSuper.substring(implSuper.indexOf("<") + 1, implSuper.indexOf(">")) : null;
      if (StringUtil.isNotEmpty(stubName)) {
        info.realStubClass = stubClass;
      }
    }
  }

  private void calcAbstractRules() {
    final var reusedRules = new HashSet<String>();
    for (BnfRule rule : myFile.getRules()) {
      String elementType = getAttribute(rule, KnownAttribute.ELEMENT_TYPE);
      BnfRule r = elementType != null ? myFile.getRule(elementType) : null;
      if (r != null && r != rule) reusedRules.add(r.getName());
    }
    for (BnfRule rule : myFile.getRules()) {
      if (reusedRules.contains(rule.getName())) continue;
      if (myGrammarRoot.equals(rule.getName())) continue;
      if (!rule.getModifierList().isEmpty()) continue;
      if (getAttribute(rule, KnownAttribute.RECOVER_WHILE) != null) continue;
      if (!getAttribute(rule, KnownAttribute.HOOKS).isEmpty()) continue;

      if (myGraphHelper.canCollapse(rule) && myGraphHelper.getFor(rule).isEmpty()) {
        getRuleInfo(rule).isAbstract = true;
      }
    }
  }

  // region generate* methods

  @Override
  public void generate() throws IOException {
    generateParser();
    if (myGrammarRoot != null && (G.generateTokenTypes || G.generateElementTypes)) {
      generateElementTypes();
    }
  }

  // region Parser generation

  @Override
  public void generateParser() throws IOException {
    Map<String, Set<RuleInfo>> classified = ContainerUtil.classify(myRuleInfos.values().iterator(), o -> o.parserClass);
    for (String className : ContainerUtil.sorted(classified.keySet())) {
      openOutput(className);
      try {
        generateParserImpl(className, map(classified.get(className), it -> it.name));
      }
      finally {
        closeOutput();
      }
    }
  }

  private void generateParserImpl(String parserClass, Collection<String> ownRuleNames) {
    // file header
    generateFileHeader(parserClass);

    // package declaration
    final var packageName = StringUtil.getPackageName(parserClass);
    if (StringUtil.isNotEmpty(packageName)) {
      out("package %s", packageName);
      newLine();
    }
    final var nameShortener = new KotlinNameShortener(packageName, !G.generateFQN);

    // imports
    final var isRootParser = parserClass.equals(myGrammarRootParser);
    final var imports = createImportsSet(isRootParser);

    Set<String> includedClasses = collectClasses(imports, packageName);
    nameShortener.addImports(imports, includedClasses);
    for (final var importName : nameShortener.getImports()) {
      out("import %s", importName);
    }
    if (G.generateFQN && imports.contains("#forced")) {
      for (String s : JBIterable.from(imports).filter(o -> !"#forced".equals(o))) {
        out("import %s", s);
      }
    }
    newLine();


    // class definition annotations
    out("%s(\"unused\", \"FunctionName\", \"JoinDeclarationAndAssignment\")", nameShortener.shorten(KotlinBnfConstants.KT_SUPPRESS_ANNO));

    // type header itself
    out("open class %s {", StringUtil.getShortName(parserClass));
    newLine();

    myShortener = nameShortener;

    if (isRootParser) {
      generateRootParserNonStatics();
      out("companion object {");
      generateRootParserStatics();
    }
    else {
      out("companion object {");
    }

    for (String ruleName : ownRuleNames) {
      final var rule = Objects.requireNonNull(myFile.getRule(ruleName));
      if (Rule.isExternal(rule) || Rule.isFake(rule)) continue;
      if (myExpressionHelper.getExpressionInfo(rule) != null) continue;
      out(HORIZONTAL_SEPARATOR);
      generateNode(rule, rule.getExpression(), R.getFuncName(rule), new HashSet<>());
      newLine();
    }
    for (String ruleName : ownRuleNames) {
      BnfRule rule = myFile.getRule(ruleName);
      final var expressionInfo = myExpressionHelper.getExpressionInfo(rule);
      if (expressionInfo != null && expressionInfo.rootRule == rule) {
        out(HORIZONTAL_SEPARATOR);
        generateExpressionRoot(expressionInfo);
        newLine();
      }
    }

    final var addNewLine = !myParserLambdas.isEmpty() && !myMetaMethodFields.isEmpty();
    generateParserLambdas(parserClass);
    if (addNewLine) newLine();
    generateMetaMethodFields();

    out("}"); // closes the companion object
    out("}"); // closes the class itself
  }

  /**
   * Generates all the root parser methods that aren't in the companion object.
   */
  private void generateRootParserNonStatics() {
    final var shortElementType = shorten(KotlinBnfConstants.KT_ELEMENT_TYPE_CLASS);
    final var shortBuilder = shorten(C.SyntaxTreeBuilderClass());
    final var shortMarker = !G.generateFQN ? "Marker" : C.SyntaxTreeBuilderClass() + ".Marker";
    List<Set<String>> extendsSet = createExtendsSet(myGraphHelper.getRuleExtendsMap());
    final var generateExtendsSets = !extendsSet.isEmpty();

    // TODO: change to the new utils API
    // parse() method
    out("fun parse(%s: %s, %s: %s) {", N.root, shortElementType, N.builder, shortBuilder);
    out("var %s: Boolean", N.result);
    out("val %s = adapt_builder_(%s, %s, this, %s)", N.builder, N.root, N.builder, generateExtendsSets ? "EXTENDS_SETS_" : null);
    out("val %s: %s = enter_section_(%s, 0, _COLLAPSE_, null)", N.marker, shortMarker, N.builder);
    out("%s = parse_root_(%s, %s)", N.result, N.root, N.builder);
    out("exit_section_(%s, 0, %s, %s, %s, true, TRUE_CONDITION)", N.builder, N.marker, N.root, N.result);
    out("}");
    newLine();

    // main parse_root_() method
    out("protected fun parse_root_(%s: %s, %s: %s): Boolean {", N.root, shortElementType, N.builder, shortBuilder);
    out("return parse_root_(%s, %s, 0)", N.root, N.builder);
    out("}");
    newLine();
  }

  /**
   * Generates all the root parser methods that are in the companion object.
   */
  private void generateRootParserStatics() {
    final var rootRule = myFile.getRule(myGrammarRoot);
    final var extraRoots = new ArrayList<BnfRule>();
    for (final var ruleName : myRuleInfos.keySet()) {
      final var rule = Objects.requireNonNull(myFile.getRule(ruleName));
      if (getAttribute(rule, KnownAttribute.ELEMENT_TYPE) != null) continue;
      if (!RuleGraphHelper.hasElementType(rule)) continue;
      if (Rule.isFake(rule) || Rule.isMeta(rule)) continue;
      final var expressionInfo = myExpressionHelper.getExpressionInfo(rule);
      if (expressionInfo != null && expressionInfo.rootRule != rule) continue;
      if (!Boolean.TRUE.equals(getAttribute(rule, KnownAttribute.EXTRA_ROOT))) continue;
      extraRoots.add(rule);
    }

    final var shortElementType = shorten(KotlinBnfConstants.KT_ELEMENT_TYPE_CLASS);
    final var shortBuilder = shorten(C.SyntaxTreeBuilderClass());
    List<Set<String>> extendsSet = createExtendsSet(myGraphHelper.getRuleExtendsMap());
    boolean generateExtendsSets = !extendsSet.isEmpty();

    out("internal fun parse_root_(%s: %s, %s: %s, %s: Int): Boolean {", N.root, shortElementType, N.builder, shortBuilder, N.level);
    if (extraRoots.isEmpty()) {
      out("return %s", rootRule == null ? "false" : createNodeCall(rootRule, null, myGrammarRoot).render(R, N));
    }
    else {
      boolean first = true;
      out("var %s: Boolean", N.result);
      for (BnfRule rule : extraRoots) {
        String elementType = shortElementTypesHolderName() + "." + getElementType(rule);
        out("%sif (%s == %s) {", first ? "" : "else ", N.root, elementType);
        String nodeCall = createNodeCall(ObjectUtils.notNull(rootRule, rule), null, rule.getName()).render(R, N);
        out("%s = %s", N.result, nodeCall);
        out("}");
        if (first) first = false;
      }
      out("else {");
      out("%s = %s", N.result, rootRule == null ? "false" : createNodeCall(rootRule, null, myGrammarRoot).render(R, N));
      out("}");
      out("return %s", N.result);
    }
    out("}");
    newLine();

    // let's assume we're in a companion object here already
    if (generateExtendsSets) {
      generateExtendsSet(extendsSet);
      newLine();
    }
  }

  /**
   * Generates a method corresponding to a given rule.
   * This method *must* be enclosed in a companion object.
   */
  private void generateNode(BnfRule rule, @NotNull BnfExpression initialNode, String funcName, Set<BnfExpression> visited) {
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
    final var isFirstNonTrivial = node == Rule.firstNotTrivial(rule);
    final var isPrivate = !(isRule || isFirstNonTrivial) || Rule.isPrivate(rule) || myGrammarRoot.equals(rule.getName());
    final var isLeft = isFirstNonTrivial && Rule.isLeft(rule);
    final var isLeftInner = isLeft && (isPrivate || Rule.isInner(rule));
    final var isUpper = !isPrivate && Rule.isUpper(rule);
    String recoverWhile = !isFirstNonTrivial ? null : getAttribute(rule, KnownAttribute.RECOVER_WHILE);
    Map<String, String> hooks = isFirstNonTrivial ? getAttribute(rule, KnownAttribute.HOOKS).asMap() : Collections.emptyMap();

    boolean canCollapse = !isPrivate && (!isLeft || isLeftInner) && isFirstNonTrivial && myGraphHelper.canCollapse(rule);

    String elementType = getElementType(rule);
    String elementTypeRef = !isPrivate && StringUtil.isNotEmpty(elementType) ? shortElementTypesHolderName() + "." + elementType : null;

    boolean isSingleNode =
      node instanceof BnfReferenceOrToken || node instanceof BnfLiteralExpression || node instanceof BnfExternalExpression;

    List<BnfExpression> children = isSingleNode ? Collections.singletonList(node) : getChildExpressions(node);
    String frameName =
      !children.isEmpty() && isFirstNonTrivial && !Rule.isMeta(rule) ? quote(R.getRuleDisplayName(rule, !isPrivate)) : null;

    String extraParameters = metaParameters.stream().map(", %s: Parser"::formatted).collect(Collectors.joining());

    // the actual method header
    final var access = !isRule ? "private " : isPrivate ? "internal " : "";
    out("%sfun %s(%s: %s, %s: Int%s): Boolean {", access, funcName, N.builder, shorten(C.SyntaxTreeBuilderClass()), N.level,
        extraParameters);
    if (isSingleNode) {
      if (isPrivate && !isLeftInner && recoverWhile == null && frameName == null) {
        final var nodeCallElement = createNodeCall(rule, node, R.getNextName(funcName, 0));
        String nodeCall = nodeCallElement.render(R, N);
        out("return %s", nodeCall);
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
      out("if (!recursion_guard_(%s, %s, \"%s\")) return false", N.builder, N.level, /*R.unwrapFuncName*/(funcName));
    }

    if (recoverWhile == null && (isRule || isFirstNonTrivial)) {
      frameName = generateFirstCheck(rule, frameName, getAttribute(rule, KnownAttribute.NAME) == null);
    }

    PinMatcher pinMatcher = new PinMatcher(rule, type, isFirstNonTrivial ? rule.getName() : funcName);
    boolean pinApplied = false;
    boolean alwaysTrue = children.isEmpty() || type == BNF_OP_OPT || type == BNF_OP_ZEROMORE;
    boolean pinned = pinMatcher.active() && pinMatcher.shouldGenerate(children);
    if (!alwaysTrue) {
      out("var %s: Boolean%s", N.result, children.isEmpty() ? " = true" : "");
      if (pinned) {
        out("var %s: Boolean", N.pinned);
      }
    }

    final var modifierList = new SmartList<String>();
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
    String modifiers = modifierList.isEmpty() ? "_NONE_" : StringUtil.join(modifierList, " or ");
    String shortMarker = !G.generateFQN ? "Marker" : C.SyntaxTreeBuilderClass() + ".Marker";
    if (sectionRequiredSimple) {
      if (!sectionMaybeDropped) {
        out("val %s: %s = enter_section_(%s)", N.marker, shortMarker, N.builder);
      }
    }
    else if (sectionRequired) {
      boolean shortVersion = frameName == null && elementTypeRef == null;
      if (shortVersion) {
        out("val %s: %s = enter_section_(%s, %s, %s)", N.marker, shortMarker, N.builder, N.level, modifiers);
      }
      else {
        out("val %s: %s = enter_section_(%s, %s, %s, %s, %s)", N.marker, shortMarker, N.builder, N.level, modifiers,
            elementTypeRef,
            frameName);
      }
    }

    final var skip = Ref.create(0);
    for (int i = 0, p = 0, childrenSize = children.size(); i < childrenSize; i++) {
      BnfExpression child = children.get(i);

      NodeCall nodeCall = createNodeCall(rule, child, R.getNextName(funcName, i));
      if (type == BNF_CHOICE) {
        if (isRule && i == 0 && G.generateTokenSets) {
          ConsumeType consumeType = getEffectiveConsumeType(rule, node, null);
          final var tokenChoice = createTokenChoiceCall(children, consumeType, funcName);
          if (tokenChoice != null) {
            out("%s = %s", N.result, tokenChoice.render(R, N));
            break;
          }
        }
        out("%s%s = %s", i > 0 ? format("if (!%s) ", N.result) : "", N.result, nodeCall.render(R, N));
      }
      else if (type == BNF_SEQUENCE) {
        if (skip.get() == 0) {
          ConsumeType consumeType = getEffectiveConsumeType(rule, node, null);
          nodeCall = createTokenSequenceCall(children, i, pinMatcher, pinApplied, skip, nodeCall, false, consumeType);
          if (i == 0) {
            out("%s = %s", N.result, nodeCall.render(R, N));
          }
          else {
            if (pinApplied && G.generateExtendedPin) {
              if (i == childrenSize - 1) {
                // do not report error for last child
                if (i == p + 1) {
                  out("%s = %s && %s", N.result, N.result, nodeCall.render(R, N));
                }
                else {
                  out("%s = %s && %s && %s", N.result, N.pinned, nodeCall.render(R, N), N.result);
                }
              }
              else if (i == p + 1) {
                out("%s = %s && report_error_(%s, %s)", N.result, N.result, N.builder, nodeCall.render(R, N));
              }
              else {
                out("%s = %s && report_error_(%s, %s) && %s", N.result, N.pinned, N.builder, nodeCall.render(R, N),
                    N.result);
              }
            }
            else {
              out("%s = %s && %s", N.result, N.result, nodeCall.render(R, N));
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
          out("%s = %s // pin = %s", N.pinned, N.result, pinMatcher.pinValue);
        }
      }
      else if (type == BNF_OP_OPT) {
        out(nodeCall.render(R, N));
      }
      else if (type == BNF_OP_ONEMORE || type == BNF_OP_ZEROMORE) {
        if (type == BNF_OP_ONEMORE) {
          out("%s = %s", N.result, nodeCall.render(R, N));
        }
        out("while (%s) {", alwaysTrue ? "true" : N.result);
        out("val %s: Int = current_position_(%s)", N.pos, N.builder);
        out("if (!%s) break", nodeCall.render(R, N));
        out("if (!empty_element_parsed_guard_(%s, \"%s\", %s)) break", N.builder, funcName, N.pos);
        out("}");
      }
      else if (type == BNF_OP_AND) {
        out("%s = %s", N.result, nodeCall.render(R, N));
      }
      else if (type == BNF_OP_NOT) {
        out("%s = !%s", N.result, nodeCall.render(R, N));
      }
      else {
        addWarning("unexpected: " + type);
      }
    }

    if (sectionRequired) {
      String resultRef = alwaysTrue ? "true" : N.result;
      if (!hooks.isEmpty()) {
        for (Map.Entry<String, String> entry : hooks.entrySet()) {
          String hookName = R.toIdentifier(entry.getKey(), null, Case.UPPER);
          out("register_hook_(%s, %s, %s)", N.builder, hookName, entry.getValue());
        }
      }
      if (sectionRequiredSimple) {
        if (!sectionMaybeDropped) {
          out("exit_section_(%s, %s, %s, %s)", N.builder, N.marker, elementTypeRef, resultRef);
        }
      }
      else {
        String pinnedRef = pinned ? N.pinned : "false";
        String recoverCall;
        if (recoverWhile != null) {
          BnfRule predicateRule = myFile.getRule(recoverWhile);
          if (RECOVER_AUTO.equals(recoverWhile)) {
            recoverCall = buildAutoRecoverCall(rule);
          }
          else if (Rule.isMeta(rule) && GrammarUtil.isDoubleAngles(recoverWhile)) {
            recoverCall = formatMetaParamName(recoverWhile.substring(2, recoverWhile.length() - 2));
          }
          else {
            recoverCall = predicateRule == null ? null : createWrappedNodeCall(rule, null, predicateRule.getName()).render(R);
          }
        }
        else {
          recoverCall = null;
        }
        out("exit_section_(%s, %s, %s, %s, %s, %s)", N.builder, N.level, N.marker, resultRef, pinnedRef, recoverCall);
      }
    }

    out("return %s", alwaysTrue ? "true" : N.result + (pinned ? format(" || %s", N.pinned) : ""));
    out("}");
    generateNodeChildren(rule, funcName, children, visited);
  }

  /**
   * *Must* be in a companion object.
   */
  private void generateExpressionRoot(@NotNull ExpressionInfo info) {
    final var opCalls = buildCallMap(info);
    final var sortedOpCalls = opCalls.keySet();

    for (final var s : info.toString().split("\n")) {
      out("// " + s);
    }

    // main entry
    String methodName = R.getFuncName(info.rootRule);
    String kernelMethodName = R.getNextName(methodName, 0);
    String frameName = quote(R.getRuleDisplayName(info.rootRule, true));
    String shortPB = shorten(C.SyntaxTreeBuilderClass());
    String shortMarker = !G.generateFQN ? "Marker" : C.SyntaxTreeBuilderClass() + ".Marker";
    out("fun %s(%s: %s, %s: Int, %s: Int): Boolean {", methodName, N.builder, shortPB, N.level, N.priority);
    out("if (!recursion_guard_(%s, %s, \"%s\")) return false", N.builder, N.level, methodName);

    if (frameName != null) {
      out("addVariant(%s, %s)", N.builder, frameName);
    }
    generateFirstCheck(info.rootRule, frameName, true);
    out("var %s: Boolean", N.result);
    out("var %s: Boolean", N.pinned);
    out("val %s: %s = enter_section_(%s, %s, _NONE_, %s)", N.marker, shortMarker, N.builder, N.level, frameName);

    boolean first = true;
    for (String opCall : sortedOpCalls) {
      List<OperatorInfo> operators = findOperators(opCalls.get(opCall), OperatorType.ATOM, OperatorType.PREFIX);
      if (operators.isEmpty()) continue;
      OperatorInfo operator = operators.get(0);
      if (operators.size() > 1) {
        addWarning("only first definition will be used for '" + operator.operator.getText() + "': " + operators);
      }
      String nodeCall = createNodeCall(operator.rule, null, operator.rule.getName()).render(R, N);
      out("%s%s = %s", first ? "" : format("if (!%s) ", N.result), N.result, nodeCall);
      first = false;
    }

    out("%s = %s", N.pinned, N.result);
    out("%s = %s && %s(%s, %s + 1, %s)", N.result, N.result, kernelMethodName, N.builder, N.level, N.priority);
    out("exit_section_(%s, %s, %s, null, %s, %s, null)", N.builder, N.level, N.marker, N.result, N.pinned);
    out("return %s || %s", N.result, N.pinned);
    out("}");
    newLine();

    // kernel
    out("fun %s(%s: %s, %s: Int, %s: Int): Boolean {", kernelMethodName, shortPB, N.builder, N.level, N.priority);
    out("if (!recursion_guard_(%s, %s, \"%s\")) return false", N.builder, N.level, kernelMethodName);
    out("var %s = true", N.result);
    out("while (true) {");
    out("val %s: %s = enter_section_(%s, %s, _LEFT_, null)", N.marker, shortMarker, N.builder, N.level);

    first = true;
    for (String opCall : sortedOpCalls) {
      List<OperatorInfo> operators = findOperators(opCalls.get(opCall), OperatorType.BINARY, OperatorType.N_ARY, OperatorType.POSTFIX);
      if (operators.isEmpty()) continue;
      OperatorInfo operator = operators.get(0);
      if (operators.size() > 1) {
        addWarning("only first definition will be used for '" + operator.operator.getText() + "': " + operators);
      }
      int priority = info.getPriority(operator.rule);
      int arg2Priority = operator.arg2 == null ? -1 : info.getPriority(operator.arg2);
      int argPriority = arg2Priority == -1 ? priority : arg2Priority - 1;

      String substCheck = "";
      if (operator.arg1 != null) {
        substCheck =
          format(" && leftMarkerIs(%s, %s)", N.builder, shortElementTypesHolderName() + "." + getElementType(operator.arg1));
      }
      out("%sif (%s < %d%s && %s) {", first ? "" : "else ", N.priority, priority, substCheck, opCall);
      first = false;
      String elementType = shortElementTypesHolderName() + "." + getElementType(operator.rule);
      boolean rightAssociative = getAttribute(operator.rule, KnownAttribute.RIGHT_ASSOCIATIVE);
      String tailCall = operator.tail == null ? null : createNodeCall(
        operator.rule, operator.tail, R.getNextName(R.getFuncName(operator.rule), 1), ConsumeType.DEFAULT
      ).render(R, N);
      if (operator.type == OperatorType.BINARY) {
        String argCall = format("%s(%s, %s, %d)", methodName, N.builder, N.level, rightAssociative ? argPriority - 1 : argPriority);
        out("%s = %s", N.result, tailCall == null ? argCall : format("report_error_(%s, %s)", N.builder, argCall));
        if (tailCall != null) out("%s = %s && %s", N.result, tailCall, N.result);
      }
      else if (operator.type == OperatorType.N_ARY) {
        boolean checkEmpty = info.checkEmpty.contains(operator);
        if (checkEmpty) {
          out("val %s: Int = current_position_(%s)", N.pos, N.builder);
        }
        out("while (true) {");
        out("%s = report_error_(%s, %s(%s, %s, %d))", N.result, N.builder, methodName, N.builder, N.level, argPriority);
        if (tailCall != null) out("%s = %s && %s", N.result, tailCall, N.result);
        out("if (!%s) break", opCall);
        if (checkEmpty) {
          out("if (!empty_element_parsed_guard_(%s, \"%s\", %s)) break", N.builder, operator.rule.getName(), N.pos);
          out("%s = current_position_(%s)", N.pos, N.builder);
        }
        out("}");
      }
      else if (operator.type == OperatorType.POSTFIX) {
        out("%s = true", N.result);
      }
      out("exit_section_(%s, %s, %s, %s, %s, true, null)", N.builder, N.level, N.marker, elementType, N.result);
      out("}");
    }
    if (first) {
      out("// no BINARY or POSTFIX operators present");
      out("break");
    }
    else {
      out("else {");
      out("exit_section_(%s, %s, %s, null, false, false, null)", N.builder, N.level, N.marker);
      out("break");
      out("}");
    }
    out("}");
    out("return %s", N.result);
    out("}");

    // operators and tails
    Set<BnfExpression> visited = new HashSet<>();
    for (String opCall : sortedOpCalls) {
      for (final var operator : opCalls.get(opCall)) {
        if (operator.type == OperatorType.ATOM) {
          if (Rule.isExternal(operator.rule)) continue;
          newLine();
          generateNode(operator.rule, operator.rule.getExpression(), R.getFuncName(operator.rule), visited);
          continue;
        }
        else if (operator.type == OperatorType.PREFIX) {
          newLine();
          String operatorFuncName = operator.rule.getName();
          out("fun %s(%s: %s, %s: Int): Boolean {", operatorFuncName, shortPB, N.builder, N.level);
          out("if (!recursion_guard_(%s, %s, \"%s\")) return false", N.builder, N.level, operatorFuncName);
          generateFirstCheck(operator.rule, frameName, false);
          out("var %s: Boolean", N.result);
          out("var %s: Boolean", N.pinned);
          out("val %s: %s = enter_section_(%s, %s, _NONE_, null)", N.marker, shortMarker, N.builder, N.level);

          String elementType = getElementType(operator.rule);
          String tailCall = operator.tail == null ? null : createNodeCall(
            operator.rule, operator.tail, R.getNextName(R.getFuncName(operator.rule), 1), ConsumeType.DEFAULT
          ).render(R, N);

          out("%s = %s", N.result, opCall);
          out("%s = %s", N.pinned, N.result);
          int priority = info.getPriority(operator.rule);
          int arg1Priority = operator.arg1 == null ? -1 : info.getPriority(operator.arg1);
          int argPriority = arg1Priority == -1 ? (priority == info.nextPriority - 1 ? -1 : priority) : arg1Priority - 1;
          out("%s = %s && %s(%s, %s, %d)", N.result, N.pinned, methodName, N.builder, N.level, argPriority);
          if (tailCall != null) {
            out("%s = %s && report_error_(%s, %s) && %s", N.result, N.pinned, N.builder, tailCall, N.result);
          }
          String elementTypeRef = StringUtil.isNotEmpty(elementType) ? shortElementTypesHolderName() + "." + elementType : "null";
          out("exit_section_(%s, %s, %s, %s, %s, %s, null)", N.builder, N.level, N.marker, elementTypeRef,
              N.result, N.pinned);
          out("return %s || %s", N.result, N.pinned);
          out("}");
        }
        generateNodeChild(operator.rule, operator.operator, R.getFuncName(operator.rule), 0, visited);
        if (operator.tail != null) {
          generateNodeChild(operator.rule, operator.tail, operator.rule.getName(), 1, visited);
        }
      }
    }
  }

  private @NotNull Map<String, List<OperatorInfo>> buildCallMap(@NotNull ExpressionInfo info) {
    final var result = new LinkedHashMap<String, List<OperatorInfo>>();
    for (final var bnfRule : info.priorityMap.keySet()) {
      final var operatorInfo = info.operatorMap.get(bnfRule);
      String opCall = createNodeCall(
        info.rootRule, operatorInfo.operator, R.getNextName(R.getFuncName(operatorInfo.rule), 0), CONSUME_TYPE_OVERRIDE
      ).render(R, N);
      result.computeIfAbsent(opCall, k -> new ArrayList<>(2)).add(operatorInfo);
    }
    return result;
  }

  /**
   * These *must* be in a companion object.
   */
  private void generateParserLambdas(@NotNull String parserClass) {
    Map<String, String> reversedLambdas = new HashMap<>();
    take(myParserLambdas).forEach((name, body) -> {
      String call = reversedLambdas.get(body);
      if (call == null) {
        call = buildParserInstance(body);
        reversedLambdas.put(body, name);
      }
      out("internal val %s: Parser = %s", name, call);
      myRenderedLambdas.put(name, parserClass);
    });
  }

  /**
   * Meta-methods are methods that take several {@link Parser Parser} instances as parameters and return another {@link Parser instance}.
   * These *must* be enclosed in a companion object.
   *
   * @param isRule whether meta-method may be used from another parser classes, and therefore should be accessible,
   *               e.g. it is {@code true} for {@code meta rule ::= <<p>>},
   *               and it is {@code false} for nested in-place method generated
   *               for {@code <<p>> | some} in {@code meta rule ::= (<<p>> | some)* }.
   */
  private void generateMetaMethod(@NotNull String methodName, @NotNull List<String> parameterNames, boolean isRule) {
    String parameterList = parameterNames.stream().map("%s: Parser"::formatted).collect(Collectors.joining(", "));
    String argumentList = String.join(", ", parameterNames);
    String metaParserMethodName = R.getWrapperParserMetaMethodName(methodName);
    String call = format("%s(%s, %s + 1, %s)", methodName, N.builder, N.level, argumentList);
    out("%s fun %s(%s): Parser {", isRule ? "internal" : "private", metaParserMethodName, parameterList);
    out("return %s", buildParserInstance(call));
    out("}");
  }

  /**
   * These *must* be surrounded in a companion object.
   */
  private void generateMetaMethodFields() {
    take(myMetaMethodFields).forEach(
      (field, call) -> out("private val %s: Parser = %s", field, call)
    );
  }

  /**
   * Generates the {@code EXTENDS_SETS_} array in the parser.
   */
  private void generateExtendsSet(@NotNull List<Set<String>> extendsSet) {
    final var shortSet = shorten(KotlinBnfConstants.KT_SET_CLASS);
    final var shortArray = shorten(KotlinBnfConstants.KT_ARRAY_CLASS);
    final var shortElementType = shorten(KotlinBnfConstants.KT_ELEMENT_TYPE_CLASS);
    final var shortArrayOf = shorten(KotlinBnfConstants.KT_ARRAY_OF_FUNCTION);
    final var shortElementTypesHolder = shortElementTypesHolderName();

    out("val EXTENDS_SETS_: %s<%s<%s>> = %s(", shortArray, shortSet, shortElementType, shortArrayOf);
    final var builder = new StringBuilder();
    for (Set<String> elementTypes : extendsSet) {
      int i = 0;
      for (String elementType : elementTypes) {
        if (i > 0) builder.append(i % 4 == 0 ? ",\n" : ", ");
        builder.append(shortElementTypesHolder).append(".").append(elementType);
        i++;
      }
      out("create_token_set_(%s),", builder);
      builder.setLength(0);
    }
    out(")");
  }

  private @Nullable String generateFirstCheck(@NotNull BnfRule rule, @Nullable String frameName, boolean skipIfOne) {
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

      ConsumeType childConsumeType = getRuleConsumeType(Objects.requireNonNull(Rule.of(expression)), rule);
      ConsumeType consumeType = ConsumeType.min(ruleConsumeType, childConsumeType);
      firstElementTypes.compute(t, (k, existing) -> ConsumeType.max(existing, consumeType));
    }
    if (firstElementTypes.isEmpty()) return frameName;

    int allTokensCount = firstElementTypes.size();
    // do not include frameName if FIRST is known and its size is 1
    boolean dropFrameName = skipIfOne && allTokensCount == 1;
    if (allTokensCount <= G.generateFirstCheck) {
      Map<ConsumeType, List<String>> grouped = firstElementTypes.entrySet().stream().collect(Collectors.groupingBy(
        Map.Entry::getValue,
        () -> new EnumMap<>(ConsumeType.class),
        Collectors.mapping(Map.Entry::getKey, Collectors.toList())
      ));
      String condition = grouped
        .entrySet()
        .stream()
        .map(
          entry -> {
            ConsumeType consumeType = entry.getKey();
            List<String> tokenTypes = entry.getValue();
            StringBuilder sb = new StringBuilder("!");
            sb.append("nextTokenIs").append(consumeType.getMethodSuffix()).append("(").append(N.builder).append(", ");
            if (!dropFrameName && consumeType == ConsumeType.DEFAULT) sb.append(StringUtil.notNullize(frameName, "\"\"")).append(", ");
            appendTokenTypes(sb, tokenTypes, shortElementTypesHolderName());
            sb.append(")");
            return sb;
          }
        ).collect(
          Collectors.joining(" &&\n  ", "if (", ") return false")
        );
      out(condition);
    }

    return dropFrameName && StringUtil.isEmpty(getAttribute(rule, KnownAttribute.NAME)) ? null : frameName;
  }

  private void generateNodeChildren(BnfRule rule, String funcName, @NotNull List<BnfExpression> children, Set<BnfExpression> visited) {
    for (int i = 0, len = children.size(); i < len; i++) {
      generateNodeChild(rule, children.get(i), funcName, i, visited);
    }
  }

  private void generateNodeChild(@NotNull BnfRule rule,
                                 @NotNull BnfExpression child,
                                 @NotNull String funcName,
                                 int index,
                                 @NotNull Set<BnfExpression> visited) {
    if (child instanceof BnfExternalExpression externalExpression) {
      // generate parameters
      final var expressions = externalExpression.getExpressionList();
      for (var i = 1; i < expressions.size(); i++) {
        BnfExpression expression = expressions.get(i);
        if (GrammarUtil.isAtomicExpression(expression)) continue;
        if (expression instanceof BnfExternalExpression) {
          generateNodeChild(rule, expression, R.getNextName(funcName, index), i - 1, visited);
        }
        else {
          String nextName = R.getNextName(R.getNextName(funcName, index), i - 1);
          if (shallGenerateNodeChild(nextName)) {
            newLine();
            generateNode(rule, expression, nextName, visited);
          }
        }
      }
    }
    else if (!GrammarUtil.isAtomicExpression(child) && !isTokenSequence(rule, child)) {
      String nextName = R.getNextName(funcName, index);
      if (shallGenerateNodeChild(nextName)) {
        newLine();
        generateNode(rule, child, nextName, visited);
      }
    }
    // otherwise do not generate
  }

  // endregion Parser generation

  // region Element Types Generation

  private void generateElementTypes() throws IOException {
    final var sortedCompositeTypes = new TreeSet<String>();
    for (final var rule : myFile.getRules()) {
      final var info = ruleInfo(rule);
      if (info.intfPackage == null) continue;
      final var elementType = info.elementType;
      if (StringUtil.isEmpty(elementType)) continue;
      if (sortedCompositeTypes.contains(elementType)) continue;
      if (!info.isFake || info.isInElementType) {
        sortedCompositeTypes.add(elementType);
      }
    }
    openOutput(myElementTypesHolderName);
    try {
      generateElementTypesImpl(myElementTypesHolderName, sortedCompositeTypes);
    }
    finally {
      closeOutput();
    }
  }

  private void generateElementTypesImpl(@NotNull String objectName, @NotNull Set<String> sortedCompositeTypes) {
    // file header
    generateFileHeader(objectName);

    // package declaration
    final var packageName = StringUtil.getPackageName(objectName);
    if (StringUtil.isNotEmpty(packageName)) {
      out("package %s", packageName);
      newLine();
    }

    final var imports = Set.of(KotlinBnfConstants.KT_ELEMENT_TYPE_CLASS);
    final var includedClasses = collectClasses(imports, packageName);
    final var nameShortener = new KotlinNameShortener(packageName, !G.generateFQN);
    nameShortener.addImports(imports, includedClasses);

    out("import %s", KotlinBnfConstants.KT_ELEMENT_TYPE_CLASS);
    newLine();

    final var shortElementType = nameShortener.shorten(KotlinBnfConstants.KT_ELEMENT_TYPE_CLASS);
    final var shortClassName = StringUtil.getShortName(objectName);

    // object definition
    out("object %s {", shortClassName);
    if (G.generateElementTypes) {
      for (final var elementType : sortedCompositeTypes) {
        out("val %s = %s(\"%s\")", elementType, shortElementType, elementType);
      }
    }
    if (G.generateTokenTypes) {
      generateTokenDefinitions(nameShortener);
      generateTokenSets(nameShortener);
    }
    out("}");
  }

  private void generateTokenDefinitions(@NotNull NameShortener nameShortener) {
    if (mySimpleTokens.isEmpty()) return;
    newLine();
    final var shortElementType = nameShortener.shorten(KotlinBnfConstants.KT_ELEMENT_TYPE_CLASS);
    final var sortedTokens = new TreeMap<String, String>();
    for (String tokenText : mySimpleTokens.keySet()) {
      String tokenName = ObjectUtils.chooseNotNull(mySimpleTokens.get(tokenText), tokenText);
      if (isIgnoredWhitespaceToken(tokenName, tokenText)) continue;
      sortedTokens.put(getElementType(tokenName), isRegexpToken(tokenText) ? tokenName : tokenText);
    }
    for (final var tokenType : sortedTokens.keySet()) {
      final var tokenString = sortedTokens.get(tokenType);
      out("val %s = %s(\"%s\")", tokenType, shortElementType, StringUtil.escapeStringCharacters(tokenString));
    }
  }

  private void generateTokenSets(@NotNull NameShortener shortener) {
    if (myTokenSets.isEmpty()) return;
    newLine();
    Map<String, String> reverseMap = new HashMap<>();
    final var shortSetOf = shortener.shorten(KotlinBnfConstants.KT_SET_OF_FUNCTION);
    final var shortSetClass = shortener.shorten(KotlinBnfConstants.KT_SET_CLASS);
    final var shortElementType = shortener.shorten(KotlinBnfConstants.KT_ELEMENT_TYPE_CLASS);
    myTokenSets.forEach((name, tokens) -> {
      final var call = "%s(%s)".formatted(shortSetOf, tokenSetString(tokens));
      String alreadyRendered = reverseMap.putIfAbsent(call, name);
      out("val %s: %s<%s> = %s", name, shortSetClass, shortElementType, ObjectUtils.chooseNotNull(alreadyRendered, call));
    });
  }

  // endregion Element Types Generation

  // endregion generate* methods 

  /**
   * Builds and returns a set of all imports to be included in the parser.
   */
  private @NotNull Set<@NotNull String> createImportsSet(boolean isRootParser) {
    final var parserImports = getRootAttribute(myFile, KnownAttribute.PARSER_IMPORTS).asStrings();
    final var imports = new LinkedHashSet<String>();
    if (!G.generateFQN) {
      imports.add(C.SyntaxTreeBuilderClass());
      imports.add(C.SyntaxTreeBuilderClass() + ".Marker");
    }
    else {
      imports.add("#forced");
    }
    imports.add(myElementTypesHolderName);
    if (StringUtil.isNotEmpty(myParserRuntimeName)) {
      imports.add(myParserRuntimeName);
    }
    if (!isRootParser) {
      imports.add(starImport(myGrammarRootParser));
    }
    else if (!G.generateFQN) {
      imports.add(KotlinBnfConstants.KT_ELEMENT_TYPE_CLASS);
    }
    imports.addAll(parserImports);
    return imports;
  }

  /**
   * Given a lambda function's body, returns a string representing
   * a Parser lambda instance.
   */
  private @NotNull String buildParserInstance(@NotNull String body) {
    return "Parser { %s, %s -> %s }".formatted(N.builder, N.level, body);
  }

  private @NotNull List<Set<String>> createExtendsSet(@NotNull MultiMap<BnfRule, BnfRule> map) {
    if (map.isEmpty()) return Collections.emptyList();
    final var result = new ArrayList<Set<String>>();
    for (final var entry : map.entrySet()) {
      Set<String> set = null;
      for (BnfRule rule : entry.getValue()) {
        final var ruleInfo = getRuleInfo(rule);
        if (!RuleGraphHelper.hasElementType(rule)) continue;
        String elementType = ruleInfo.isFake && !ruleInfo.isInElementType ||
                             RuleGraphHelper.getSynonymTargetOrSelf(rule) != rule ? null : ruleInfo.elementType;
        if (StringUtil.isEmpty(elementType)) continue;
        if (set == null) set = new TreeSet<>();
        set.add(elementType);
      }
      if (set != null && set.size() > 1) result.add(set);
    }
    result.sort(Comparator.comparingInt(Set::size));
    for (ListIterator<Set<String>> it = result.listIterator(); it.hasNext(); ) {
      Set<String> smaller = it.next();
      for (Set<String> bigger : result.subList(it.nextIndex(), result.size())) {
        if (bigger.containsAll(smaller)) {
          it.remove();
          break;
        }
      }
    }
    return result;
  }


  private @NotNull RuleInfo getRuleInfo(@NotNull BnfRule rule) {
    return Objects.requireNonNull(myRuleInfos.get(rule.getName()));
  }


  private @NotNull Set<String> collectClasses(Set<String> imports, String packageName) {
    Set<String> includedPackages = JBIterable.from(imports)
      .filter(o -> !o.startsWith("static") && o.endsWith(".*"))
      .map(o -> StringUtil.trimEnd(o, ".*"))
      .append(packageName).toSet();
    Set<String> includedClasses = new HashSet<>();
    for (RuleInfo info : myRuleInfos.values()) {
      if (includedPackages.contains(info.intfPackage)) includedClasses.add(StringUtil.getShortName(info.intfClass));
      if (includedPackages.contains(info.implPackage)) includedClasses.add(StringUtil.getShortName(info.implClass));
    }
    return includedClasses;
  }

  @SuppressWarnings("StringEquality")
  private @NotNull String buildAutoRecoverCall(BnfRule rule) {
    Set<BnfExpression> nextExprSet = myFirstNextAnalyzer.calcNext(rule).keySet();
    Set<String> nextSet = BnfFirstNextAnalyzer.asStrings(nextExprSet);
    List<String> tokenTypes = new ArrayList<>(nextSet.size());

    for (String s : nextSet) {
      if (myFile.getRule(s) != null) continue; // ignore left recursion
      if (s == BnfFirstNextAnalyzer.MATCHES_EOF || s == BnfFirstNextAnalyzer.MATCHES_NOTHING) continue;

      boolean unknown = s == BnfFirstNextAnalyzer.MATCHES_ANY;
      String t = unknown ? null : firstToElementType(s);
      if (t != null) {
        tokenTypes.add(t);
      }
      else {
        tokenTypes.clear();
        addWarning(rule.getName() + " #auto recovery generation failed: " + s);
        break;
      }
    }
    StringBuilder sb = new StringBuilder(format("!nextTokenIsFast(%s, ", N.builder));

    appendTokenTypes(sb, tokenTypes, shortElementTypesHolderName());
    sb.append(")");

    String constantName = rule.getName() + "_auto_recover_";
    myParserLambdas.put(constantName, sb.toString());
    return constantName;
  }

  private @NotNull ConsumeType getRuleConsumeType(@NotNull BnfRule rule, @Nullable BnfRule contextRule) {
    ConsumeType forcedConsumeType = ExpressionGeneratorHelper.fixForcedConsumeType(myExpressionHelper, rule, null, null);
    if (forcedConsumeType != null && contextRule != null && myExpressionHelper.getExpressionInfo(contextRule) == null) {
      // do not force child expr consume-type in a non-expr context
      forcedConsumeType = null;
    }
    return ObjectUtils.chooseNotNull(forcedConsumeType, ConsumeType.forRule(rule));
  }

  private @NotNull List<String> collectMetaParametersFormatted(@NotNull BnfRule rule, @Nullable BnfExpression expression) {
    if (expression == null) return Collections.emptyList();
    return map(GrammarUtil.collectMetaParameters(rule, expression),
               it -> formatMetaParamName(it.substring(2, it.length() - 2)));
  }

  private @NotNull String formatMetaParamName(@NotNull String s) {
    String argName = s.trim();
    return N.metaParamPrefix +
           (N.metaParamPrefix.isEmpty() || "_".equals(N.metaParamPrefix) ? argName : StringUtil.capitalize(argName));
  }

  private @Nullable String firstToElementType(@NotNull String first) {
    if (first.startsWith("#") || first.startsWith("-") || first.startsWith("<<")) return null;
    String value = GrammarUtil.unquote(first);
    //noinspection StringEquality
    if (first != value) {
      String attributeName = getTokenName(value);
      if (attributeName != null && !first.startsWith("\"")) {
        return getElementType(attributeName);
      }
      return null;
    }
    return getElementType(first);
  }

  private @Nullable String getTokenName(String value) {
    return mySimpleTokens.get(value);
  }

  private @NotNull NodeCall createTokenSequenceCall(@NotNull List<BnfExpression> children,
                                                    int startIndex,
                                                    PinMatcher pinMatcher,
                                                    boolean pinApplied,
                                                    Ref<Integer> skip,
                                                    @NotNull NodeCall nodeCall,
                                                    boolean rollbackOnFail,
                                                    ConsumeType consumeType) {
    if (startIndex == children.size() - 1 || !(nodeCall instanceof ConsumeTokenCall)) return nodeCall;
    final var list = new ArrayList<String>();
    int pin = pinApplied ? -1 : 0;
    for (int i = startIndex, len = children.size(); i < len; i++) {
      BnfExpression child = children.get(i);
      final var text = child.getText();
      final var tokenName = child instanceof BnfStringLiteralExpression
                            ? getTokenName(GrammarUtil.unquote(text))
                            : child instanceof BnfReferenceOrToken && myFile.getRule(text) == null
                              ? text
                              : null;
      if (tokenName == null) break;
      list.add(shortElementTypesHolderName() + "." + getElementType(tokenName));
      if (!pinApplied && pinMatcher.matches(i, child)) {
        pin = i - startIndex + 1;
        pinApplied = true;
      }
    }
    if (list.size() < 2) return nodeCall;
    skip.set(list.size() - 1);
    String consumeMethodName = (rollbackOnFail ? "parseTokens" : "consumeTokens") +
                               (consumeType == ConsumeType.SMART ? consumeType.getMethodSuffix() : "");
    return new ConsumeTokensCall(consumeMethodName, pin, list);
  }

  private @Nullable NodeCall createTokenChoiceCall(@NotNull List<BnfExpression> children,
                                                   @NotNull ConsumeType consumeType,
                                                   @NotNull String funcName) {
    Collection<String> tokenNames = getTokenNames(myFile, children, 2);
    if (tokenNames == null) return null;

    final var tokens = new TreeSet<String>();
    for (String tokenName : tokenNames) {
      if (!mySimpleTokens.containsKey(tokenName) && !mySimpleTokens.containsValue(tokenName)) {
        mySimpleTokens.put(tokenName, null);
      }
      tokens.add(getElementType(tokenName));
    }

    String tokenSetName = R.getTokenSetConstantName(funcName);
    myTokenSets.put(tokenSetName, tokens);
    return new ConsumeTokenChoiceCall(consumeType, shortElementTypesHolderName() + "." + tokenSetName);
  }

  private @NotNull NodeCall createNodeCall(@NotNull BnfRule rule, @Nullable BnfExpression node, @NotNull String nextName) {
    return createNodeCall(rule, node, nextName, null);
  }

  private @NotNull NodeCall createNodeCall(@NotNull BnfRule rule,
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
        return createConsumeToken(consumeType, attributeName);
      }
      return getConsumeTextToken(consumeType, text.startsWith("\"") ? value : StringUtil.escapeStringCharacters(value));
    }
    else if (type == BNF_NUMBER) {
      ConsumeType consumeType = getEffectiveConsumeType(rule, node, forcedConsumeType);
      return getConsumeTextToken(consumeType, text);
    }
    else if (type == BNF_REFERENCE_OR_TOKEN) {
      String value = GrammarUtil.stripQuotesAroundId(text);
      BnfRule subRule = myFile.getRule(value);
      if (subRule != null) {
        if (Rule.isExternal(subRule)) {
          return createExternalCall(rule, GrammarUtil.getExternalRuleExpressions(subRule), nextName);
        }
        else {
          ExpressionInfo info = ExpressionGeneratorHelper.getInfoForExpressionParsing(myExpressionHelper, subRule);
          BnfRule rr = info != null ? info.rootRule : subRule;
          String method = R.getFuncName(rr);
          String parserClass = getRuleInfo(rr).parserClass;
          String parserClassName = StringUtil.getShortName(parserClass);
          boolean renderClass = !parserClass.equals(myGrammarRootParser) && !parserClass.equals(getRuleInfo(rule).parserClass);
          if (info == null) {
            return new MethodCall(renderClass, parserClassName, method);
          }
          else {
            if (renderClass) {
              method = StringUtil.getQualifiedName(parserClassName, method);
            }
            return new ExpressionMethodCall(method, info.getPriority(subRule) - 1);
          }
        }
      }
      // allow token usage by registered token name instead of token text
      if (!mySimpleTokens.containsKey(text) && !mySimpleTokens.containsValue(text)) {
        mySimpleTokens.put(text, null);
      }
      final var consumeType = getEffectiveConsumeType(rule, node, forcedConsumeType);
      return createConsumeToken(consumeType, text);
    }
    else if (isTokenSequence(rule, node)) {
      final var consumeType = getEffectiveConsumeType(rule, node, forcedConsumeType);
      final var pinMatcher = new PinMatcher(rule, type, nextName);
      List<BnfExpression> childExpressions = getChildExpressions(node);
      final var firstElement = ContainerUtil.getFirstItem(childExpressions);
      final var nodeCall = createNodeCall(rule, firstElement, R.getNextName(nextName, 0), consumeType);
      for (PsiElement childExpression : childExpressions) {
        final var childText = childExpression instanceof BnfStringLiteralExpression
                              ? GrammarUtil.unquote(childExpression.getText())
                              : childExpression.getText();
        if (!mySimpleTokens.containsKey(childText) && !mySimpleTokens.containsValue(childText)) {
          mySimpleTokens.put(childText, null);
        }
      }
      return createTokenSequenceCall(childExpressions, 0, pinMatcher, false, Ref.create(0), nodeCall, true, consumeType);
    }
    else if (type == BNF_EXTERNAL_EXPRESSION) {
      List<BnfExpression> expressions = ((BnfExternalExpression)node).getExpressionList();
      if (expressions.size() == 1 && Rule.isMeta(rule)) {
        return new MetaParameterCall(formatMetaParamName(expressions.get(0).getText()));
      }
      else {
        return createExternalCall(rule, expressions, nextName);
      }
    }
    else {
      List<String> extraArguments = collectMetaParametersFormatted(rule, node);
      if (extraArguments.isEmpty()) {
        return new MethodCall(false, StringUtil.getShortName(getRuleInfo(rule).parserClass), nextName);
      }
      else {
        return new MetaMethodCall(null, nextName, map(extraArguments, MetaParameterArgument::new));
      }
    }
  }

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

  private @NotNull NodeCall createExternalCall(@NotNull BnfRule rule,
                                               @NotNull List<BnfExpression> expressions,
                                               @NotNull String nextName) {
    List<BnfExpression> callParameters = expressions;
    List<String> metaParameterNames = Collections.emptyList();
    String method = !expressions.isEmpty() ? expressions.get(0).getText() : null;
    String targetClassName = null;
    BnfRule targetRule = method == null ? null : myFile.getRule(method);
    // handle external rule call: substitute and merge arguments from external expression and rule definition
    if (targetRule != null) {
      if (Rule.isExternal(targetRule)) {
        metaParameterNames = GrammarUtil.collectMetaParameters(targetRule, targetRule.getExpression());
        callParameters = GrammarUtil.getExternalRuleExpressions(targetRule);
        method = callParameters.get(0).getText();
        if (metaParameterNames.size() < expressions.size() - 1) {
          callParameters = ContainerUtil.concat(callParameters, expressions.subList(metaParameterNames.size() + 1, expressions.size()));
        }
      }
      else {
        String parserClass = getRuleInfo(targetRule).parserClass;
        if (!parserClass.equals(myGrammarRootParser) && !parserClass.equals(getRuleInfo(rule).parserClass)) {
          targetClassName = StringUtil.getShortName(parserClass);
        }
      }
    }
    method = String.valueOf(method);
    List<NodeArgument> arguments = new ArrayList<>();
    if (callParameters.size() > 1) {
      for (int i = 1, len = callParameters.size(); i < len; i++) {
        BnfExpression nested = callParameters.get(i);
        String argument = nested.getText();
        String argNextName;
        int metaIdx;
        if (argument.startsWith("<<") && (metaIdx = metaParameterNames.indexOf(argument)) > -1) {
          nested = expressions.get(metaIdx + 1);
          argument = nested.getText();
          argNextName = R.getNextName(nextName, metaIdx);
        }
        else {
          argNextName = R.getNextName(nextName, i - 1);
        }
        if (nested instanceof BnfReferenceOrToken) {
          if (myFile.getRule(argument) != null) {
            arguments.add(createWrappedNodeCall(rule, nested, argument));
          }
          else {
            String tokenType = getElementType(argument);
            arguments.add(createWrappedNodeCall(rule, nested, tokenType));
          }
        }
        else if (nested instanceof BnfLiteralExpression) {
          String attributeName = getTokenName(GrammarUtil.unquote(argument));
          if (attributeName != null) {
            arguments.add(createWrappedNodeCall(rule, nested, attributeName));
          }
          else {
            arguments.add(new TextArgument(argument.startsWith("'") ? GrammarUtil.unquote(argument) : argument));
          }
        }
        else if (nested instanceof BnfExternalExpression expression) {
          List<BnfExpression> expressionList = expression.getExpressionList();
          if (expressionList.size() == 1 && Rule.isMeta(rule)) {
            arguments.add(new MetaParameterArgument(formatMetaParamName(expressionList.get(0).getText())));
          }
          else {
            arguments.add(createWrappedNodeCall(rule, nested, argNextName));
          }
        }
        else {
          arguments.add(createWrappedNodeCall(rule, nested, argNextName));
        }
      }
    }
    return Rule.isMeta(targetRule) ? new MetaMethodCall(targetClassName, method, arguments)
                                   : new MethodCallWithArguments(method, arguments);
  }

  private @NotNull NodeArgument createWrappedNodeCall(@NotNull BnfRule rule, @Nullable BnfExpression nested, @NotNull String nextName) {
    NodeCall nodeCall = createNodeCall(rule, nested, nextName);
    if (nodeCall instanceof MetaMethodCall metaCall) {
      final var callArgument = new MetaMethodCallArgument(metaCall);
      if (metaCall.referencesMetaParameter()) {
        return callArgument;
      }
      else {
        return r -> getMetaMethodFieldRef(callArgument.render(r), nextName);
      }
    }
    else if (nodeCall instanceof MethodCall methodCall && G.javaVersion > 6) {
      return r -> format("%s::%s", methodCall.className(), methodCall.methodName());
    }
    else {
      return r -> getParserLambdaRef(nodeCall, nextName);
    }
  }

  private @NotNull String getMetaMethodFieldRef(@NotNull String call, @NotNull String nextName) {
    String fieldName = R.getWrapperParserConstantName(nextName);
    myMetaMethodFields.putIfAbsent(fieldName, call);
    return fieldName;
  }

  private @NotNull String getParserLambdaRef(@NotNull NodeCall nodeCall, @NotNull String nextName) {
    String constantName = R.getWrapperParserConstantName(nextName);
    String targetClass = myRenderedLambdas.get(constantName);
    if (targetClass != null) {
      return StringUtil.getShortName(targetClass) + "." + constantName;
    }
    else if (!myParserLambdas.containsKey(constantName)) {
      String call = nodeCall.render(R, N);
      myParserLambdas.put(constantName, call);
      if (!call.startsWith(nextName + "(")) {
        myInlinedChildNodes.add(nextName);
      }
    }
    return constantName;
  }

  private @NotNull NodeCall createConsumeToken(@NotNull ConsumeType consumeType, @NotNull String tokenName) {
    myTokensUsedInGrammar.add(tokenName);
    return new ConsumeTokenCall(consumeType, shortElementTypesHolderName() + "." + getElementType(tokenName));
  }

  // region Utils
  private @NotNull String getElementType(String token) {
    return getTokenType(myFile, token, G.generateTokenCase);
  }

  private @NotNull String getElementType(BnfRule rule) {
    return R.getElementType(rule, G.generateElementCase);
  }

  private boolean shallGenerateNodeChild(String funcName) {
    return !myInlinedChildNodes.contains(funcName);
  }

  private void addWarning(String text) {
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      //noinspection UseOfSystemOutOrSystemErr
      System.out.println(text);
    }
    else {
      NotificationGroupManager.getInstance()
        .getNotificationGroup("grammarkit.parser.generator.log")
        .createNotification(text, MessageType.WARNING).notify(myFile.getProject());
    }
  }

  private boolean isIgnoredWhitespaceToken(@NotNull String tokenName, @NotNull String tokenText) {
    return isRegexpToken(tokenText) &&
           !myTokensUsedInGrammar.contains(tokenName) &&
           matchesAny(getRegexpTokenRegexp(tokenText), " ", "\n") &&
           !matchesAny(getRegexpTokenRegexp(tokenText), "a", "1", "_", ".");
  }

  private @NotNull RuleInfo ruleInfo(@NotNull BnfRule rule) {
    return Objects.requireNonNull(myRuleInfos.get(rule.getName()));
  }

  // endregion Utils
}
