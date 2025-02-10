/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ObjectUtils;
import com.intellij.util.SmartList;
import com.intellij.util.containers.*;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.generator.NodeCalls.*;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.grammar.parser.GeneratedParserUtilBase.Parser;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.*;

import static com.intellij.util.containers.ContainerUtil.map;
import static java.lang.String.format;
import static java.util.stream.Collectors.*;
import static org.intellij.grammar.analysis.BnfFirstNextAnalyzer.BNF_MATCHES_ANY;
import static org.intellij.grammar.analysis.BnfFirstNextAnalyzer.BNF_MATCHES_EOF;
import static org.intellij.grammar.generator.BnfConstants.*;
import static org.intellij.grammar.generator.NameShortener.getRawClassName;
import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.RuleGraphHelper.*;
import static org.intellij.grammar.psi.BnfTypes.*;


/**
 * @author gregory
 * Date 16.07.11 10:41
 */
public class KotlinParserGenerator extends GeneratorBase {
  public static final Logger LOG = Logger.getInstance(KotlinParserGenerator.class);

  @NotNull
  RuleInfo ruleInfo(BnfRule rule) {
    return Objects.requireNonNull(myRuleInfos.get(rule.getName()));
  }

  private final Map<String, RuleInfo> myRuleInfos = new TreeMap<>();
  private final Map<String, String> myParserLambdas = new HashMap<>();       // field name -> body
  private final Map<String, String> myRenderedLambdas = new HashMap<>();     // field name -> parser class FQN
  private final Set<String> myInlinedChildNodes = new HashSet<>();

  /**
   * Some meta-method calls use only static parsers as arguments,
   * i.e. they don't reference meta-method parameters,
   * we want to cache them in static fields.
   * <p/>
   * Mapping: <code> meta field name -> meta method call </code>
   */
  private final Map<String, String> myMetaMethodFields = new HashMap<>();

  private final Map<String, String> mySimpleTokens;

  /**
   * Name of the class containing runtime parser utilities
   * (default: {@link GeneratedParserUtilBase}).
   */
  protected final String myParserUtilClass;
  /**
   * An optional class for miscellaneous utility methods
   * to use in the parser.
   */
  protected final String myPsiImplUtilClass;

  /**
   * Name of the visitor class
   */
  protected final String myVisitorClassName;
  protected final String myTypeHolderClass;

  private final RuleGraphHelper myGraphHelper;
  private final ExpressionHelper myExpressionHelper;
  private final BnfFirstNextAnalyzer myFirstNextAnalyzer;
  private final JavaHelper myJavaHelper;

  final Names N;
  protected final IntelliJPlatformConstants C;

  public KotlinParserGenerator(@NotNull BnfFile psiFile,
                               @NotNull String sourcePath,
                               @NotNull String outputPath,
                               @NotNull String packagePrefix) {
    super(psiFile, sourcePath, outputPath, packagePrefix, "kt");

    //super ctor body:
    //myFile = psiFile;
    //myGenerateForFleet = psiFile instanceof FleetBnfFileWrapper;
    //
    //G = new GenOptions(psiFile);
    //mySourcePath = sourcePath;
    //myOutputPath = outputPath;
    //myPackagePrefix = packagePrefix;
    //myIntfClassFormat = getPsiClassFormat(myFile);
    //myImplClassFormat = getPsiImplClassFormat(myFile);
    //
    //List<BnfRule> rules = psiFile.getRules();
    //BnfRule rootRule = rules.isEmpty() ? null : rules.get(0);
    //myGrammarRoot = rootRule == null ? null : rootRule.getName();
    //myGrammarRootParser = rootRule == null ? null : getRootAttribute(rootRule, KnownAttribute.PARSER_CLASS);

    N = G.names;

    myParserUtilClass = getRootAttribute(myFile, KnownAttribute.PARSER_UTIL_CLASS);
    myPsiImplUtilClass = getRootAttribute(myFile, KnownAttribute.PSI_IMPL_UTIL_CLASS);
    String tmpVisitorClass = getRootAttribute(myFile, KnownAttribute.PSI_VISITOR_NAME);
    tmpVisitorClass = !G.generateVisitor || StringUtil.isEmpty(tmpVisitorClass) ? null :
                      !tmpVisitorClass.equals(myIntfClassFormat.strip(tmpVisitorClass)) ? tmpVisitorClass :
                      myIntfClassFormat.apply("") + tmpVisitorClass;
    myVisitorClassName = tmpVisitorClass == null || !tmpVisitorClass.equals(StringUtil.getShortName(tmpVisitorClass)) ?
                         tmpVisitorClass : getRootAttribute(myFile, KnownAttribute.PSI_PACKAGE) + "." + tmpVisitorClass;
    myTypeHolderClass = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);

    C = IntelliJPlatformConstants.getConstantSetForBnf(myFile);

    mySimpleTokens = new LinkedHashMap<>(getTokenTextToNameMap(myFile));
    myGraphHelper = getCached(myFile);
    myExpressionHelper = new ExpressionHelper(myFile, myGraphHelper, this::addWarning);
    new RuleMethodsHelper(myGraphHelper, myExpressionHelper, mySimpleTokens, G);
    myFirstNextAnalyzer = BnfFirstNextAnalyzer.createAnalyzer(true);
    myJavaHelper = JavaHelper.getJavaHelper(myFile);

    List<BnfRule> rules = psiFile.getRules();
    for (BnfRule r : rules) {
      String ruleName = r.getName();
      boolean noPsi = !hasPsiClass(r);
      myRuleInfos.put(ruleName, new RuleInfo(
        ruleName, Rule.isFake(r),
        getElementType(r), getAttribute(r, KnownAttribute.PARSER_CLASS),
        noPsi ? null : getAttribute(r, KnownAttribute.PSI_PACKAGE),
        noPsi ? null : getAttribute(r, KnownAttribute.PSI_IMPL_PACKAGE),
        noPsi ? null : getRulePsiClassName(r, myIntfClassFormat), noPsi ? null : getRulePsiClassName(r, myImplClassFormat),
        noPsi ? null : getAttribute(r, KnownAttribute.MIXIN), noPsi ? null : getAttribute(r, KnownAttribute.STUB_CLASS)));
    }
    JBIterable.from(myRuleInfos.values()).find(o -> o.stub != null);

    calcFakeRulesWithType();
    calcRulesStubNames();
    calcAbstractRules();
  }

  private void calcAbstractRules() {
    Set<String> reusedRules = new HashSet<>();
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
        ruleInfo(rule).isAbstract = true;
      }
    }
  }

  private void calcFakeRulesWithType() {
    for (BnfRule rule : myFile.getRules()) {
      BnfRule r = myFile.getRule(getAttribute(rule, KnownAttribute.ELEMENT_TYPE));
      if (r == null) continue;
      ruleInfo(r).isInElementType = true;
    }
  }

  private void calcRulesStubNames() {
    for (BnfRule rule : myFile.getRules()) {
      RuleInfo info = ruleInfo(rule);
      String stubClass = info.stub;
      if (stubClass == null) {
        BnfRule topSuper = getEffectiveSuperRule(myFile, rule);
        stubClass = topSuper == null ? null : ruleInfo(topSuper).stub;
      }
      BnfRule topSuper = getEffectiveSuperRule(myFile, rule);
      String superRuleClass = topSuper == null ? getRootAttribute(myFile, KnownAttribute.EXTENDS) :
                              topSuper == rule ? getAttribute(rule, KnownAttribute.EXTENDS) :
                              ruleInfo(topSuper).intfClass;
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

  public void addWarning(String text) {
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

  @Override
  public void generate() throws IOException {
    generateParser();
  }

  private void generateParser() throws IOException {
    Map<String, Set<RuleInfo>> classified = ContainerUtil.classify(myRuleInfos.values().iterator(), o -> o.parserClass);
    for (String className : ContainerUtil.sorted(classified.keySet())) {
      openOutput(className);
      try {
        generateParser(className, map(classified.get(className), it -> it.name));
      }
      finally {
        closeOutput();
      }
    }
  }

  /**
   * Outputs the following elements:
   * <ul>
   *   <li>file header</li>
   *   <li>package declaration</li>
   *   <li>imports</li>
   *   <li>annotations</li>
   *   <li>class header (from the `class` keyword to the opening brace,
   *       inclusive)</li>
   * </ul>
   */
  @Override
  protected void generateClassHeader(String className, Set<String> imports, String annos, Java javaType, String... supers) {
    // file header
    generateFileHeader(className);

    // package declaration
    String packageName = generatePackageName(className);
    if (StringUtil.isNotEmpty(packageName)) {
      out("package %s", packageName);
      newLine();
    }
    String shortClassName = StringUtil.getShortName(className);
    NameShortener shortener = new NameShortener(packageName, !G.generateFQN);

    // imports
    Set<String> includedClasses = collectClasses(imports, packageName);
    shortener.addImports(imports, includedClasses);
    for (String s : shortener.getImports()) {
      // remove the static part
      final var withoutStatic = StringUtil.replace(s, "static ", "");
      out("import %s", withoutStatic);
    }
    if (G.generateFQN && imports.contains("#forced")) {
      for (String s : JBIterable.from(imports).filter(o -> !"#forced".equals(o))) {
        out("import %s", s);
      }
    }
    newLine();


    // class definition annotations
    if (StringUtil.isNotEmpty(annos)) {
      out(shortener.shorten(annos));
    }

    // superclass / interfaces
    StringBuilder sb = new StringBuilder();
    var isColonPresent = false;
    for (String aSuper : supers) {
      if (StringUtil.isEmpty(aSuper)) continue;
      if (imports.contains(aSuper)) {
        aSuper = StringUtil.getShortName(aSuper);
      }
      // TODO: primary constructor n stuff
      if (!isColonPresent) {
        sb.append(": ").append(shortener.shorten(aSuper));
        isColonPresent = true;
      }
      else {
        sb.append(", ").append(shortener.shorten(aSuper));
      }
    }

    // type header itself
    out("%s %s%s {", Case.LOWER.apply(javaType.name()).replace('_', ' '), shortClassName, sb.toString());
    newLine();

    myShortener = shortener;
  }

  /**
   * Creates and returns a set of all imports to be included in the parser.
   */
  private @NotNull Set<@NotNull String> generateImportsSet(boolean isRootParser, @NotNull Collection<String> ownRuleNames) {
    final var parserImports = getRootAttribute(myFile, KnownAttribute.PARSER_IMPORTS).asStrings();
    final var imports = new LinkedHashSet<String>();
    if (!G.generateFQN) {
      imports.add(C.PsiBuilderClass);
      imports.add(C.PsiBuilderClass + ".Marker");
    }
    else {
      imports.add("#forced");
    }
    imports.add(starImport(myTypeHolderClass));
    if (G.generateTokenSets && hasAtLeastOneTokenChoice(myFile, ownRuleNames)) {
      imports.add(starImport(myTypeHolderClass + "." + TOKEN_SET_HOLDER_NAME));
    }
    if (StringUtil.isNotEmpty(myParserUtilClass)) {
      imports.add(starImport(myParserUtilClass));
    }
    if (!isRootParser) {
      imports.add(starImport(myGrammarRootParser));
    }
    else if (!G.generateFQN) {
      imports.addAll(Arrays.asList(C.IElementTypeClass,
                                   C.AstNodeClass,
                                   C.TokenSetClass,
                                   C.PsiParserClass,
                                   C.LightPsiParserClass));
    }
    imports.addAll(parserImports);
    return imports;
  }

  private static final @NotNull String HORIZONTAL_SEPARATOR = "/* ********************************************************** */";

  private void generateParser(String parserClass, Collection<String> ownRuleNames) {
    final var isRootParser = parserClass.equals(myGrammarRootParser);
    final var imports = generateImportsSet(isRootParser, ownRuleNames);

    generateClassHeader(parserClass,
                        imports,
                        "",
                        Java.CLASS, "",
                        isRootParser ? C.PsiParserClass : "",
                        isRootParser ? C.LightPsiParserClass : "");

    if (isRootParser) {
      generateRootParserNonStatics();
      out("companion object {");
      generateRootParserStatics();
    } else {
      out("companion object {");
    }

    for (String ruleName : ownRuleNames) {
      BnfRule rule = Objects.requireNonNull(myFile.getRule(ruleName));
      if (Rule.isExternal(rule) || Rule.isFake(rule)) continue;
      if (myExpressionHelper.getExpressionInfo(rule) != null) continue;
      out(HORIZONTAL_SEPARATOR);
      generateNode(rule, rule.getExpression(), getFuncName(rule), new HashSet<>());
      newLine();
    }
    for (String ruleName : ownRuleNames) {
      BnfRule rule = myFile.getRule(ruleName);
      ExpressionHelper.ExpressionInfo info = myExpressionHelper.getExpressionInfo(rule);
      if (info != null && info.rootRule == rule) {
        out(HORIZONTAL_SEPARATOR);
        ExpressionGeneratorHelper.generateExpressionRoot(info, this);
        newLine();
      }
    }
    final var addNewLine = !myParserLambdas.isEmpty() && !myMetaMethodFields.isEmpty();
    generateParserLambdas(parserClass);
    if (addNewLine) newLine();
    generateMetaMethodFields();

    out("}"); // closes the companion object
    out("}"); // and the class itself
  }

  /**
   * These *must* be in a companion object.
   */
  private void generateParserLambdas(@NotNull String parserClass) {
    Map<String, String> reversedLambdas = new HashMap<>();
    take(myParserLambdas).forEach((name, body) -> {
      String call = reversedLambdas.get(body);
      if (call == null) {
        call = generateParserInstance(body);
        reversedLambdas.put(body, name);
      }
      out("internal val %s: Parser = %s", name, call);
      myRenderedLambdas.put(name, parserClass);
    });
  }

  private @NotNull String generateParserInstance(@NotNull String body) {
    return "Parser { %s, %s -> %s }".formatted(N.builder, N.level, body);
  }

  /**
   * These *must* be surrounded in a companion object.
   */
  private void generateMetaMethodFields() {
    take(myMetaMethodFields).forEach(
      (field, call) -> out("private val %s: Parser = %s", field, call)
    );
  }
  
  private void generateRootParserNonStatics() {
    BnfRule rootRule = myFile.getRule(myGrammarRoot);
    List<BnfRule> extraRoots = new ArrayList<>();
    for (String ruleName : myRuleInfos.keySet()) {
      BnfRule rule = Objects.requireNonNull(myFile.getRule(ruleName));
      if (getAttribute(rule, KnownAttribute.ELEMENT_TYPE) != null) continue;
      if (!hasElementType(rule)) continue;
      if (Rule.isFake(rule) || Rule.isMeta(rule)) continue;
      ExpressionHelper.ExpressionInfo info = myExpressionHelper.getExpressionInfo(rule);
      if (info != null && info.rootRule != rule) continue;
      if (!Boolean.TRUE.equals(getAttribute(rule, KnownAttribute.EXTRA_ROOT))) continue;
      extraRoots.add(rule);
    }

    String shortElementType = shorten(C.IElementTypeClass);
    String shortASTNode = shorten(C.AstNodeClass);
    String shortPsiBuilder = shorten(C.PsiBuilderClass);
    shorten(C.TokenSetClass);
    String shortMarker = !G.generateFQN ? "Marker" : C.PsiBuilderClass + ".Marker";
    List<Set<String>> extendsSet = buildExtendsSet(myGraphHelper.getRuleExtendsMap());
    boolean generateExtendsSets = !extendsSet.isEmpty();

    // parse() method
    out("override fun parse(%s: %s, %s: %s): %s {", N.root, shortElementType, N.builder, shortPsiBuilder, shortASTNode);
    out("parseLight(%s, %s)", N.root, N.builder);
    out("return %s.getTreeBuilt()", N.builder);
    out("}");
    newLine();

    // parseLight() method
    out("override fun parseLight(%s: %s, %s: %s) {", N.root, shortElementType, N.builder, shortPsiBuilder);
    out("var %s: Boolean", N.result);
    out("val %s = adapt_builder_(%s, %s, this, %s)", N.builder, N.root, N.builder, generateExtendsSets ? "EXTENDS_SETS_" : null);
    out("val %s: %s = enter_section_(%s, 0, _COLLAPSE_, null)", N.marker, shortMarker, N.builder);
    out("%s = parse_root_(%s, %s)", N.result, N.root, N.builder);
    out("exit_section_(%s, 0, %s, %s, %s, true, TRUE_CONDITION)", N.builder, N.marker, N.root, N.result);
    out("}");
    newLine();

    // main parse_root_() method
    out("protected fun parse_root_(%s: %s, %s: %s): Boolean {", N.root, shortElementType, N.builder, shortPsiBuilder);
    out("return parse_root_(%s, %s, 0)", N.root, N.builder);
    out("}");
    newLine();
  }

  private void generateRootParserStatics() {
    BnfRule rootRule = myFile.getRule(myGrammarRoot);
    List<BnfRule> extraRoots = new ArrayList<>();
    for (String ruleName : myRuleInfos.keySet()) {
      BnfRule rule = Objects.requireNonNull(myFile.getRule(ruleName));
      if (getAttribute(rule, KnownAttribute.ELEMENT_TYPE) != null) continue;
      if (!hasElementType(rule)) continue;
      if (Rule.isFake(rule) || Rule.isMeta(rule)) continue;
      ExpressionHelper.ExpressionInfo info = myExpressionHelper.getExpressionInfo(rule);
      if (info != null && info.rootRule != rule) continue;
      if (!Boolean.TRUE.equals(getAttribute(rule, KnownAttribute.EXTRA_ROOT))) continue;
      extraRoots.add(rule);
    }

    String shortElementType = shorten(C.IElementTypeClass);
    String shortPsiBuilder = shorten(C.PsiBuilderClass);
    shorten(C.TokenSetClass);
    List<Set<String>> extendsSet = buildExtendsSet(myGraphHelper.getRuleExtendsMap());
    boolean generateExtendsSets = !extendsSet.isEmpty();

    out("internal fun parse_root_(%s: %s, %s: %s, %s: Int): Boolean {", N.root, shortElementType, N.builder, shortPsiBuilder, N.level);
    if (extraRoots.isEmpty()) {
      out("return %s", rootRule == null ? "false" : generateNodeCall(rootRule, null, myGrammarRoot).render(N));
    }
    else {
      boolean first = true;
      out("boolean %s", N.result);
      for (BnfRule rule : extraRoots) {
        String elementType = getElementType(rule);
        out("%sif (%s == %s) {", first ? "" : "else ", N.root, elementType);
        String nodeCall = generateNodeCall(ObjectUtils.notNull(rootRule, rule), null, rule.getName()).render(N);
        out("%s = %s", N.result, nodeCall);
        out("}");
        if (first) first = false;
      }
      out("else {");
      out("%s = %s", N.result, rootRule == null ? "false" : generateNodeCall(rootRule, null, myGrammarRoot).render(N));
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
   * Generates the {@code EXTENDS_SETS_} array in the parser.
   */
  private void generateExtendsSet(@NotNull List<Set<String>> extendsSet) {
    String shortTokenSet = shorten(C.TokenSetClass);

    out("val EXTENDS_SETS_: Array<%s> = arrayOf(", shortTokenSet);
    final var builder = new StringBuilder();
    for (Set<String> elementTypes : extendsSet) {
      int i = 0;
      for (String elementType : elementTypes) {
        if (i > 0) builder.append(i % 4 == 0 ? ",\n" : ", ");
        builder.append(elementType);
        i++;
      }
      out("create_token_set_(%s),", builder);
      builder.setLength(0);
    }
    out(")");
  }

  private @NotNull List<Set<String>> buildExtendsSet(@NotNull MultiMap<BnfRule, BnfRule> map) {
    if (map.isEmpty()) return Collections.emptyList();
    List<Set<String>> result = new ArrayList<>();
    for (Map.Entry<BnfRule, Collection<BnfRule>> entry : map.entrySet()) {
      Set<String> set = null;
      for (BnfRule rule : entry.getValue()) {
        RuleInfo info = ruleInfo(rule);
        if (!hasElementType(rule)) continue;
        String elementType = info.isFake && !info.isInElementType ||
                             getSynonymTargetOrSelf(rule) != rule ? null : info.elementType;
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

  @Override
  protected @NotNull Set<String> collectClasses(Set<String> imports, String packageName) {
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

  @NotNull
  protected String generatePackageName(String className) {
    return StringUtil.getPackageName(className);
  }

  /**
   * Generates a method corresponding to a given rule.
   * This method *must* be enclosed in a companion object.
   */
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
    boolean firstNonTrivial = node == Rule.firstNotTrivial(rule);
    boolean isPrivate = !(isRule || firstNonTrivial) || Rule.isPrivate(rule) || myGrammarRoot.equals(rule.getName());
    boolean isLeft = firstNonTrivial && Rule.isLeft(rule);
    boolean isLeftInner = isLeft && (isPrivate || Rule.isInner(rule));
    boolean isUpper = !isPrivate && Rule.isUpper(rule);
    String recoverWhile = !firstNonTrivial ? null : getAttribute(rule, KnownAttribute.RECOVER_WHILE);
    Map<String, String> hooks = firstNonTrivial ? getAttribute(rule, KnownAttribute.HOOKS).asMap() : Collections.emptyMap();

    boolean canCollapse = !isPrivate && (!isLeft || isLeftInner) && firstNonTrivial && myGraphHelper.canCollapse(rule);

    String elementType = getElementType(rule);
    String elementTypeRef = !isPrivate && StringUtil.isNotEmpty(elementType) ? elementType : null;

    boolean isSingleNode =
      node instanceof BnfReferenceOrToken || node instanceof BnfLiteralExpression || node instanceof BnfExternalExpression;

    List<BnfExpression> children = isSingleNode ? Collections.singletonList(node) : getChildExpressions(node);
    String frameName = !children.isEmpty() && firstNonTrivial && !Rule.isMeta(rule) ? quote(getRuleDisplayName(rule, !isPrivate)) : null;

    String extraParameters = metaParameters.stream().map(it -> ", Parser " + it).collect(joining());

    // the actual method header
    out("%sfun %s(%s: %s, %s: Int%s): Boolean {",
        !isRule ? "private " : isPrivate ? "internal " : "",
        funcName,
        N.builder,
        shorten(C.PsiBuilderClass),
        N.level,
        extraParameters
    );
    if (isSingleNode) {
      if (isPrivate && !isLeftInner && recoverWhile == null && frameName == null) {
        String nodeCall = generateNodeCall(rule, node, getNextName(funcName, 0)).render(N);
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
      out("if (!recursion_guard_(%s, %s, \"%s\")) return false", N.builder, N.level, funcName);
    }

    if (recoverWhile == null && (isRule || firstNonTrivial)) {
      frameName = generateFirstCheck(rule, frameName, getAttribute(rule, KnownAttribute.NAME) == null);
    }

    PinMatcher pinMatcher = new PinMatcher(rule, type, firstNonTrivial ? rule.getName() : funcName);
    boolean pinApplied = false;
    boolean alwaysTrue = children.isEmpty() || type == BNF_OP_OPT || type == BNF_OP_ZEROMORE;
    boolean pinned = pinMatcher.active() && pinMatcher.shouldGenerate(children);
    if (!alwaysTrue) {
      out("var %s: Boolean%s", N.result, children.isEmpty() ? " = true" : "");
      if (pinned) {
        out("var %s: Boolean", N.pinned);
      }
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
    String shortMarker = !G.generateFQN ? "Marker" : C.PsiBuilderClass + ".Marker";
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

    int[] skip = {0};
    for (int i = 0, p = 0, childrenSize = children.size(); i < childrenSize; i++) {
      BnfExpression child = children.get(i);

      NodeCall nodeCall = generateNodeCall(rule, child, getNextName(funcName, i));
      if (type == BNF_CHOICE) {
        if (isRule && i == 0 && G.generateTokenSets) {
          ConsumeType consumeType = getEffectiveConsumeType(rule, node, null);
          NodeCall tokenChoice = generateTokenChoiceCall(children, consumeType, funcName);
          if (tokenChoice != null) {
            out("%s = %s", N.result, tokenChoice.render(N));
            break;
          }
        }
        out("%s%s = %s", i > 0 ? format("if (!%s) ", N.result) : "", N.result, nodeCall.render(N));
      }
      else if (type == BNF_SEQUENCE) {
        if (skip[0] == 0) {
          ConsumeType consumeType = getEffectiveConsumeType(rule, node, null);
          nodeCall = generateTokenSequenceCall(children, i, pinMatcher, pinApplied, skip, nodeCall, false, consumeType);
          if (i == 0) {
            out("%s = %s", N.result, nodeCall.render(N));
          }
          else {
            if (pinApplied && G.generateExtendedPin) {
              if (i == childrenSize - 1) {
                // do not report error for last child
                if (i == p + 1) {
                  out("%s = %s && %s", N.result, N.result, nodeCall.render(N));
                }
                else {
                  out("%s = %s && %s && %s", N.result, N.pinned, nodeCall.render(N), N.result);
                }
              }
              else if (i == p + 1) {
                out("%s = %s && report_error_(%s, %s)", N.result, N.result, N.builder, nodeCall.render(N));
              }
              else {
                out("%s = %s && report_error_(%s, %s) && %s", N.result, N.pinned, N.builder, nodeCall.render(N),
                    N.result);
              }
            }
            else {
              out("%s = %s && %s", N.result, N.result, nodeCall.render(N));
            }
          }
        }
        else {
          skip[0]--; // we are inside already generated token sequence
          if (pinApplied && i == p + 1) p++; // shift pinned index as we skip
        }
        if (pinned && !pinApplied && pinMatcher.matches(i, child)) {
          pinApplied = true;
          p = i;
          out("%s = %s // pin = %s", N.pinned, N.result, pinMatcher.pinValue);
        }
      }
      else if (type == BNF_OP_OPT) {
        out(nodeCall.render(N));
      }
      else if (type == BNF_OP_ONEMORE || type == BNF_OP_ZEROMORE) {
        if (type == BNF_OP_ONEMORE) {
          out("%s = %s", N.result, nodeCall.render(N));
        }
        out("while (%s) {", alwaysTrue ? "true" : N.result);
        out("val %s: Int = current_position_(%s)", N.pos, N.builder);
        out("if (!%s) break", nodeCall.render(N));
        out("if (!empty_element_parsed_guard_(%s, \"%s\", %s)) break", N.builder, funcName, N.pos);
        out("}");
      }
      else if (type == BNF_OP_AND) {
        out("%s = %s", N.result, nodeCall.render(N));
      }
      else if (type == BNF_OP_NOT) {
        out("%s = !%s", N.result, nodeCall.render(N));
      }
      else {
        addWarning("unexpected: " + type);
      }
    }

    if (sectionRequired) {
      String resultRef = alwaysTrue ? "true" : N.result;
      if (!hooks.isEmpty()) {
        for (Map.Entry<String, String> entry : hooks.entrySet()) {
          String hookName = toIdentifier(entry.getKey(), null, Case.UPPER);
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
            recoverCall = generateAutoRecoverCall(rule);
          }
          else if (Rule.isMeta(rule) && GrammarUtil.isDoubleAngles(recoverWhile)) {
            recoverCall = formatMetaParamName(recoverWhile.substring(2, recoverWhile.length() - 2));
          }
          else {
            recoverCall = predicateRule == null ? null : generateWrappedNodeCall(rule, null, predicateRule.getName()).render();
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
   * Meta-methods are methods that take several {@link Parser Parser} instances as parameters and return another {@link Parser instance}.
   * These *must* be enclosed in a companion object.
   *
   * @param isRule whether meta-method may be used from another parser classes, and therefore should be accessible,
   *               e.g. it is {@code true} for {@code meta rule ::= <<p>>},
   *               and it is {@code false} for nested in-place method generated
   *               for {@code <<p>> | some} in {@code meta rule ::= (<<p>> | some)* }.
   */
  private void generateMetaMethod(@NotNull String methodName, @NotNull List<String> parameterNames, boolean isRule) {
    String parameterList = parameterNames.stream().map(it -> "Parser " + it).collect(joining(", "));
    String argumentList = String.join(", ", parameterNames);
    String metaParserMethodName = getWrapperParserMetaMethodName(methodName);
    String call = format("%s(%s, %s + 1, %s)", methodName, N.builder, N.level, argumentList);
    // @formatter:off
    out("%sstatic Parser %s(%s) {", isRule ? "" : "private ", metaParserMethodName, parameterList);
    out("return %s", generateParserInstance(call));
    out("}");
    // @formatter:on
  }

  /**
   * @noinspection StringEquality
   */
  private String generateAutoRecoverCall(BnfRule rule) {
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

    appendTokenTypes(sb, tokenTypes);
    sb.append(")");

    String constantName = rule.getName() + "_auto_recover_";
    myParserLambdas.put(constantName, sb.toString());
    return constantName;
  }

  public String generateFirstCheck(BnfRule rule, String frameName, boolean skipIfOne) {
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
      Map<ConsumeType, List<String>> grouped = firstElementTypes.entrySet().stream().collect(groupingBy(
        Map.Entry::getValue,
        () -> new EnumMap<>(ConsumeType.class),
        mapping(Map.Entry::getKey, toList())
      ));
      String condition = grouped.entrySet().stream().map(entry -> {
        ConsumeType consumeType = entry.getKey();
        List<String> tokenTypes = entry.getValue();
        StringBuilder sb = new StringBuilder("!");
        sb.append("nextTokenIs").append(consumeType.getMethodSuffix()).append("(").append(N.builder).append(", ");
        if (!dropFrameName && consumeType == ConsumeType.DEFAULT) sb.append(StringUtil.notNullize(frameName, "\"\"")).append(", ");
        appendTokenTypes(sb, tokenTypes);
        sb.append(")");
        return sb;
      }).collect(joining(" &&\n  ", "if (", ") return false"));
      out(condition);
    }

    return dropFrameName && StringUtil.isEmpty(getAttribute(rule, KnownAttribute.NAME)) ? null : frameName;
  }

  private @NotNull ConsumeType getRuleConsumeType(@NotNull BnfRule rule, @Nullable BnfRule contextRule) {
    ConsumeType forcedConsumeType = ExpressionGeneratorHelper.fixForcedConsumeType(myExpressionHelper, rule, null, null);
    if (forcedConsumeType != null && contextRule != null && myExpressionHelper.getExpressionInfo(contextRule) == null) {
      // do not force child expr consume-type in a non-expr context
      forcedConsumeType = null;
    }
    return ObjectUtils.chooseNotNull(forcedConsumeType, ConsumeType.forRule(rule));
  }

  private void generateNodeChildren(BnfRule rule, String funcName, List<BnfExpression> children, Set<BnfExpression> visited) {
    for (int i = 0, len = children.size(); i < len; i++) {
      generateNodeChild(rule, children.get(i), funcName, i, visited);
    }
  }

  void generateNodeChild(BnfRule rule, BnfExpression child, String funcName, int index, Set<BnfExpression> visited) {
    if (child instanceof BnfExternalExpression) {
      // generate parameters
      List<BnfExpression> expressions = ((BnfExternalExpression)child).getExpressionList();
      for (int j = 1, size = expressions.size(); j < size; j++) {
        BnfExpression expression = expressions.get(j);
        if (GrammarUtil.isAtomicExpression(expression)) continue;
        if (expression instanceof BnfExternalExpression) {
          generateNodeChild(rule, expression, getNextName(funcName, index), j - 1, visited);
        }
        else {
          String nextName = getNextName(getNextName(funcName, index), j - 1);
          if (shallGenerateNodeChild(nextName)) {
            newLine();
            generateNode(rule, expression, nextName, visited);
          }
        }
      }
    }
    else if (GrammarUtil.isAtomicExpression(child) || isTokenSequence(rule, child)) {
      // do not generate
    }
    else {
      String nextName = getNextName(funcName, index);
      if (shallGenerateNodeChild(nextName)) {
        newLine();
        generateNode(rule, child, nextName, visited);
      }
    }
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

  private @Nullable String firstToElementType(String first) {
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

  private @NotNull NodeCall generateTokenSequenceCall(List<BnfExpression> children,
                                                      int startIndex,
                                                      PinMatcher pinMatcher,
                                                      boolean pinApplied,
                                                      int[] skip,
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
    skip[0] = list.size() - 1;
    String consumeMethodName = (rollbackOnFail ? "parseTokens" : "consumeTokens") +
                               (consumeType == ConsumeType.SMART ? consumeType.getMethodSuffix() : "");
    return new ConsumeTokensCall(consumeMethodName, pin, list);
  }

  private @Nullable NodeCall generateTokenChoiceCall(@NotNull List<BnfExpression> children,
                                                     @NotNull ConsumeType consumeType,
                                                     @NotNull String funcName) {
    Collection<String> tokenNames = getTokenNames(myFile, children, 2);
    if (tokenNames == null) return null;

    for (String tokenName : tokenNames) {
      if (!mySimpleTokens.containsKey(tokenName) && !mySimpleTokens.containsValue(tokenName)) {
        mySimpleTokens.put(tokenName, null);
      }
    }

    String tokenSetName = getTokenSetConstantName(funcName);
    return new ConsumeTokenChoiceCall(consumeType, tokenSetName);
  }

  @NotNull
  NodeCall generateNodeCall(@NotNull BnfRule rule, @Nullable BnfExpression node, @NotNull String nextName) {
    return generateNodeCall(rule, node, nextName, null);
  }

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
      return generateConsumeTextToken(consumeType, text.startsWith("\"") ? value : StringUtil.escapeStringCharacters(value));
    }
    else if (type == BNF_NUMBER) {
      ConsumeType consumeType = getEffectiveConsumeType(rule, node, forcedConsumeType);
      return generateConsumeTextToken(consumeType, text);
    }
    else if (type == BNF_REFERENCE_OR_TOKEN) {
      String value = GrammarUtil.stripQuotesAroundId(text);
      BnfRule subRule = myFile.getRule(value);
      if (subRule != null) {
        if (Rule.isExternal(subRule)) {
          return generateExternalCall(rule, GrammarUtil.getExternalRuleExpressions(subRule), nextName);
        }
        else {
          ExpressionHelper.ExpressionInfo info = ExpressionGeneratorHelper.getInfoForExpressionParsing(myExpressionHelper, subRule);
          BnfRule rr = info != null ? info.rootRule : subRule;
          String method = getFuncName(rr);
          String parserClass = ruleInfo(rr).parserClass;
          String parserClassName = StringUtil.getShortName(parserClass);
          boolean renderClass = !parserClass.equals(myGrammarRootParser) && !parserClass.equals(ruleInfo(rule).parserClass);
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
      ConsumeType consumeType = getEffectiveConsumeType(rule, node, forcedConsumeType);
      return generateConsumeToken(consumeType, text);
    }
    else if (isTokenSequence(rule, node)) {
      ConsumeType consumeType = getEffectiveConsumeType(rule, node, forcedConsumeType);
      PinMatcher pinMatcher = new PinMatcher(rule, type, nextName);
      List<BnfExpression> childExpressions = getChildExpressions(node);
      BnfExpression firstElement = ContainerUtil.getFirstItem(childExpressions);
      NodeCall nodeCall = generateNodeCall(rule, firstElement, getNextName(nextName, 0), consumeType);
      for (PsiElement e : childExpressions) {
        String t = e instanceof BnfStringLiteralExpression ? GrammarUtil.unquote(e.getText()) : e.getText();
        if (!mySimpleTokens.containsKey(t) && !mySimpleTokens.containsValue(t)) {
          mySimpleTokens.put(t, null);
        }
      }
      return generateTokenSequenceCall(childExpressions, 0, pinMatcher, false, new int[]{0}, nodeCall, true, consumeType);
    }
    else if (type == BNF_EXTERNAL_EXPRESSION) {
      List<BnfExpression> expressions = ((BnfExternalExpression)node).getExpressionList();
      if (expressions.size() == 1 && Rule.isMeta(rule)) {
        return new MetaParameterCall(formatMetaParamName(expressions.getFirst().getText()));
      }
      else {
        return generateExternalCall(rule, expressions, nextName);
      }
    }
    else {
      List<String> extraArguments = collectMetaParametersFormatted(rule, node);
      if (extraArguments.isEmpty()) {
        return new MethodCall(false, StringUtil.getShortName(ruleInfo(rule).parserClass), nextName);
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

  private @NotNull NodeCall generateExternalCall(@NotNull BnfRule rule,
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
        method = callParameters.getFirst().getText();
        if (metaParameterNames.size() < expressions.size() - 1) {
          callParameters = ContainerUtil.concat(callParameters, expressions.subList(metaParameterNames.size() + 1, expressions.size()));
        }
      }
      else {
        String parserClass = ruleInfo(targetRule).parserClass;
        if (!parserClass.equals(myGrammarRootParser) && !parserClass.equals(ruleInfo(rule).parserClass)) {
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
          argNextName = getNextName(nextName, metaIdx);
        }
        else {
          argNextName = getNextName(nextName, i - 1);
        }
        if (nested instanceof BnfReferenceOrToken) {
          if (myFile.getRule(argument) != null) {
            arguments.add(generateWrappedNodeCall(rule, nested, argument));
          }
          else {
            String tokenType = getElementType(argument);
            arguments.add(generateWrappedNodeCall(rule, nested, tokenType));
          }
        }
        else if (nested instanceof BnfLiteralExpression) {
          String attributeName = getTokenName(GrammarUtil.unquote(argument));
          if (attributeName != null) {
            arguments.add(generateWrappedNodeCall(rule, nested, attributeName));
          }
          else {
            arguments.add(new TextArgument(argument.startsWith("'") ? GrammarUtil.unquote(argument) : argument));
          }
        }
        else if (nested instanceof BnfExternalExpression expression) {
          List<BnfExpression> expressionList = expression.getExpressionList();
          if (expressionList.size() == 1 && Rule.isMeta(rule)) {
            arguments.add(new MetaParameterArgument(formatMetaParamName(expressionList.getFirst().getText())));
          }
          else {
            arguments.add(generateWrappedNodeCall(rule, nested, argNextName));
          }
        }
        else {
          arguments.add(generateWrappedNodeCall(rule, nested, argNextName));
        }
      }
    }
    return Rule.isMeta(targetRule) ? new MetaMethodCall(targetClassName, method, arguments)
                                   : new MethodCallWithArguments(method, arguments);
  }

  private @NotNull NodeArgument generateWrappedNodeCall(@NotNull BnfRule rule, @Nullable BnfExpression nested, @NotNull String nextName) {
    NodeCall nodeCall = generateNodeCall(rule, nested, nextName);
    if (nodeCall instanceof MetaMethodCall metaCall) {
      MetaMethodCallArgument argument = new MetaMethodCallArgument(metaCall);
      if (metaCall.referencesMetaParameter()) {
        return argument;
      }
      else {
        return () -> getMetaMethodFieldRef(argument.render(), nextName);
      }
    }
    else if (nodeCall instanceof MethodCall methodCall && G.javaVersion > 6) {
      return () -> format("%s::%s", methodCall.getClassName(), methodCall.getMethodName());
    }
    else {
      return () -> getParserLambdaRef(nodeCall, nextName);
    }
  }

  private String getMetaMethodFieldRef(@NotNull String call, @NotNull String nextName) {
    String fieldName = getWrapperParserConstantName(nextName);
    myMetaMethodFields.putIfAbsent(fieldName, call);
    return fieldName;
  }

  private @NotNull String getParserLambdaRef(@NotNull NodeCall nodeCall, @NotNull String nextName) {
    String constantName = getWrapperParserConstantName(nextName);
    String targetClass = myRenderedLambdas.get(constantName);
    if (targetClass != null) {
      return StringUtil.getShortName(targetClass) + "." + constantName;
    }
    else if (!myParserLambdas.containsKey(constantName)) {
      String call = nodeCall.render(N);
      myParserLambdas.put(constantName, call);
      if (!call.startsWith(nextName + "(")) {
        myInlinedChildNodes.add(nextName);
      }
    }
    return constantName;
  }

  private boolean shallGenerateNodeChild(String funcName) {
    return !myInlinedChildNodes.contains(funcName);
  }

  private @NotNull NodeCall generateConsumeToken(@NotNull ConsumeType consumeType, @NotNull String tokenName) {
    return new ConsumeTokenCall(consumeType, getElementType(tokenName));
  }

  private static @NotNull NodeCall generateConsumeTextToken(@NotNull ConsumeType consumeType, @NotNull String tokenText) {
    return new ConsumeTokenCall(consumeType, "\"" + tokenText + "\"");
  }

  private String getElementType(String token) {
    return getTokenType(myFile, token, G.generateTokenCase);
  }

  String getElementType(BnfRule r) {
    return ParserGeneratorUtil.getElementType(r, G.generateElementCase);
  }

  /*ElementTypes******************************************************************/
}
