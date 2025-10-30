/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.Trinity;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import com.intellij.util.ObjectUtils;
import com.intellij.util.SmartList;
import com.intellij.util.containers.*;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.generator.NodeCalls.*;
import org.intellij.grammar.generator.Renderer.*;
import org.intellij.grammar.generator.java.JavaBnfConstants;
import org.intellij.grammar.generator.java.JavaNameShortener;
import org.intellij.grammar.generator.java.JavaRenderer;
import org.intellij.grammar.generator.kotlin.KotlinBnfConstants;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.parser.GeneratedParserUtilBase.Parser;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.HashMap;
import java.util.HashSet;

import static com.intellij.util.containers.ContainerUtil.emptyList;
import static com.intellij.util.containers.ContainerUtil.map;
import static java.lang.String.format;
import static java.util.stream.Collectors.*;
import static org.intellij.grammar.analysis.BnfFirstNextAnalyzer.BNF_MATCHES_ANY;
import static org.intellij.grammar.analysis.BnfFirstNextAnalyzer.BNF_MATCHES_EOF;
import static org.intellij.grammar.generator.CommonBnfConstants.RECOVER_AUTO;
import static org.intellij.grammar.generator.NameShortener.addTypeToImports;
import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.RuleGraphHelper.*;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality.*;
import static org.intellij.grammar.generator.java.JavaNameShortener.getRawClassName;
import static org.intellij.grammar.psi.BnfTypes.*;


/**
 * @author gregory
 * Date 16.07.11 10:41
 */
public final class JavaParserGenerator extends Generator {
  public static final Logger LOG = Logger.getInstance(JavaParserGenerator.class);
  
  private final Set<String> myTokensUsedInGrammar = new LinkedHashSet<>();
  private final boolean myNoStubs;

  private final String myGrammarRoot;
  private final String myGrammarRootParser;
  private final String myParserUtilClass;
  private final String myPsiImplUtilClass;
  private final String myPsiTreeUtilClass;

  private final NameFormat myPsiInterfaceFormat;
  private final NameFormat myImplClassFormat;

  private final String myVisitorClassName;
  private final String myParserTypeHolderClass;
  private final String myPsiElementTypeHolderClass;

  private final ExpressionHelper myExpressionHelper;
  private final RuleMethodsHelper myRulesMethodsHelper;
  private final JavaHelper myJavaHelper;

  public JavaParserGenerator(@NotNull BnfFile psiFile,
                             @NotNull String sourcePath,
                             @NotNull String outputPath,
                             @NotNull String packagePrefix,
                             @NotNull OutputOpener outputOpener) {
    super(psiFile, sourcePath, outputPath, packagePrefix, "java", outputOpener, new JavaRenderer());

    myPsiInterfaceFormat = getPsiClassFormat(myFile);
    myImplClassFormat = getPsiImplClassFormat(myFile);
    myParserUtilClass = getRootAttribute(myFile, KnownAttribute.PARSER_UTIL_CLASS);
    myPsiImplUtilClass = getRootAttribute(myFile, KnownAttribute.PSI_IMPL_UTIL_CLASS);
    myPsiTreeUtilClass = getRootAttribute(myFile, KnownAttribute.PSI_TREE_UTIL_CLASS);
    String tmpVisitorClass = getRootAttribute(myFile, KnownAttribute.PSI_VISITOR_NAME);
    tmpVisitorClass = !G.generateVisitor || StringUtil.isEmpty(tmpVisitorClass) ? null :
                      !tmpVisitorClass.equals(myPsiInterfaceFormat.strip(tmpVisitorClass)) ? tmpVisitorClass :
                      myPsiInterfaceFormat.apply("") + tmpVisitorClass;
    myVisitorClassName = tmpVisitorClass == null || !tmpVisitorClass.equals(StringUtil.getShortName(tmpVisitorClass)) ?
                         tmpVisitorClass : getRootAttribute(myFile, KnownAttribute.PSI_PACKAGE) + "." + tmpVisitorClass;
    myParserTypeHolderClass = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);
    myPsiElementTypeHolderClass = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);

    myExpressionHelper = new ExpressionHelper(myFile, myGraphHelper, this::addWarning);
    myRulesMethodsHelper = new RuleMethodsHelper(myGraphHelper, myExpressionHelper, mySimpleTokens, G);
    myJavaHelper = JavaHelper.getJavaHelper(myFile);

    List<BnfRule> rules = psiFile.getRules();
    BnfRule rootRule = rules.isEmpty() ? null : rules.get(0);
    myGrammarRoot = rootRule == null ? null : rootRule.getName();
    for (BnfRule r : rules) {
      String ruleName = r.getName();
      boolean noPsi = !hasPsiClass(r);
      myRuleInfos.put(ruleName, new RuleInfo(
        ruleName, Rule.isFake(r),
        getElementType(r), getAttribute(r, KnownAttribute.PARSER_CLASS),
        noPsi ? null : getAttribute(r, KnownAttribute.PSI_PACKAGE),
        noPsi ? null : getAttribute(r, KnownAttribute.PSI_IMPL_PACKAGE),
        noPsi ? null : CommonRendererUtils.getRulePsiClassName(r, myPsiInterfaceFormat), noPsi ? null : CommonRendererUtils.getRulePsiClassName(r, myImplClassFormat),
        noPsi ? null : getAttribute(r, KnownAttribute.MIXIN), noPsi ? null : getAttribute(r, KnownAttribute.STUB_CLASS)));
    }
    myGrammarRootParser = rootRule == null ? null : ruleInfo(rootRule).parserClass;
    myNoStubs = JBIterable.from(myRuleInfos.values()).find(o -> o.stub != null) == null;

    calcFakeRulesWithType();
    calcRulesStubNames();
    calcAbstractRules();
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
        implSuper.indexOf("<") < implSuper.indexOf(">") && !myJavaHelper.findClassMethods(implSuperRaw, JavaHelper.MethodType.INSTANCE, "getParentByStub", 0).isEmpty() ?
        implSuper.substring(implSuper.indexOf("<") + 1, implSuper.indexOf(">")) : null;
      if (StringUtil.isNotEmpty(stubName)) {
        info.realStubClass = stubClass;
      }
    }
  }

  private void calcRealSuperClasses(Map<String, BnfRule> sortedPsiRules) {
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
      BnfRule topSuper = supers.get(rule);
      RuleInfo topInfo = topSuper == null || topSuper == rule ? null : ruleInfo(topSuper);
      String superRuleClass = topSuper == null ? getRootAttribute(myFile, KnownAttribute.EXTENDS) :
                              topSuper == rule ? getAttribute(rule, KnownAttribute.EXTENDS) :
                              topInfo.implClass;
      String stubName = info.realStubClass;
      String adjustedSuperRuleClass =
        StringUtil.isEmpty(stubName) ? superRuleClass :
        JavaBnfConstants.AST_WRAPPER_PSI_ELEMENT_CLASS.equals(superRuleClass) ? JavaBnfConstants.STUB_BASED_PSI_ELEMENT_BASE + "<" + stubName + ">" :
        superRuleClass.contains("?") ? superRuleClass.replaceAll("\\?", stubName) : superRuleClass;
      // mixin attribute overrides "extends":
      info.realSuperClass = StringUtil.notNullize(info.mixin, adjustedSuperRuleClass);
      info.mixedAST = topInfo != null ? topInfo.mixedAST : JBIterable.of(superRuleClass, info.realSuperClass)
        .map(JavaNameShortener::getRawClassName)
        .flatMap(s -> JBTreeTraverser.<String>from(o -> JBIterable.of(myJavaHelper.getSuperClassName(o))).withRoot(s).unique())
        .find(JavaBnfConstants.COMPOSITE_PSI_ELEMENT_CLASS::equals) != null;
    }
  }

  @Override
  public void generate() throws IOException {
    {
      generateParser();
    }
    generatePsiOnly();
  }
  
  //Because some of the simple tokens are added during parser generation, 
  // we use this function to pass missing tokens from KotlinParserGenerator to JavaParserGenerator
  public void replaceSimpleTokes(Map<String, String> simpleTypes) {
    mySimpleTokens.clear();
    mySimpleTokens.putAll(simpleTypes);
  }
  
  public void generatePsiOnly() throws IOException {
    Map<String, BnfRule> sortedCompositeTypes = new TreeMap<>();
    Map<String, BnfRule> sortedPsiRules = new TreeMap<>();

    for (BnfRule rule : myFile.getRules()) {
      RuleInfo info = ruleInfo(rule);
      if (info.intfPackage == null) continue;
      String elementType = info.elementType;
      if (StringUtil.isEmpty(elementType)) continue;
      if (sortedCompositeTypes.containsKey(elementType)) continue;
      if (!info.isFake || info.isInElementType) {
        sortedCompositeTypes.put(elementType, rule);
      }
      sortedPsiRules.put(rule.getName(), rule);
      info.superInterfaces = new LinkedHashSet<>(getSuperInterfaceNames(myFile, rule, myPsiInterfaceFormat));
    }
    if (G.generatePsi) {
      calcRealSuperClasses(sortedPsiRules);
    }
    if (myGrammarRoot != null && (G.generateTokenTypes || G.generateElementTypes || G.generatePsi && G.generatePsiFactory)) {
      openOutput(myPsiElementTypeHolderClass);
      try {
        generateElementTypesHolder(myPsiElementTypeHolderClass,
                                   sortedCompositeTypes,
                                   getRootAttribute(myFile, KnownAttribute.TOKEN_TYPE_FACTORY),
                                   G.generatePsi);
      }
      finally {
        closeOutput();
      }
    }
    if (G.parserApi == GenOptions.ParserApi.Syntax) {
      var converterClass = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_CLASS);
      openOutput(converterClass);
      try {
        generateElementTypesConverter(converterClass,
                                      myParserTypeHolderClass,
                                      getRootAttribute(myFile, KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_CLASS),
                                      sortedCompositeTypes);
      }
      finally {
        closeOutput();
      }
    }
    if (G.generatePsi) {
      generatePsi(sortedPsiRules);
    }
  }

  private void checkClassAvailability(@Nullable String className) {
    if (StringUtil.isEmpty(className)) return;
    if (myJavaHelper.findClass(className) == null) {
      String tail = StringUtil.isEmpty("PSI method signatures will not be detected") ? "" : " (PSI method signatures will not be detected)";
      addWarning(className + " class not found" + tail);
    }
  }

  private void generateVisitor(String psiClass, Map<String, BnfRule> sortedRules) {
    String superIntf = ObjectUtils.notNull(ContainerUtil.getFirstItem(getRootAttribute(myFile, KnownAttribute.IMPLEMENTS)),
                                           KnownAttribute.IMPLEMENTS.getDefaultValue().get(0)).second;
    Set<String> imports = new LinkedHashSet<>(Arrays.asList("org.jetbrains.annotations.*", JavaBnfConstants.PSI_ELEMENT_VISITOR_CLASS, superIntf));
    MultiMap<String, String> supers = new MultiMap<>();
    for (BnfRule rule : sortedRules.values()) {
      supers.putValues(rule.getName(), getSuperInterfaceNames(myFile, rule, myPsiInterfaceFormat));
    }
    {
      // ensure only public supers are exposed, replace non-public with default super-intf for simplicity
      Map<String, String> replacements = new HashMap<>();
      Set<String> visited = new HashSet<>();
      for (String s : supers.values()) {
        if (!visited.add(s)) continue;
        NavigatablePsiElement aClass = myJavaHelper.findClass(s);
        if (aClass != null && !myJavaHelper.isPublic(aClass)) {
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
    }
    imports.addAll(ContainerUtil.sorted(
      JBIterable.from(sortedRules.values()).map(this::ruleInfo).map(o -> o.intfPackage + ".*").toSet()));
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

  public void generateParser() throws IOException {
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
      if (Rule.isExternal(rule) || Rule.isFake(rule)) continue;
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

  private @NotNull String generateParserInstance(@NotNull String body) {
    return G.javaVersion > 6
           ? format("(%s, %s) -> %s", N.builder, N.level, body)
           : format("new Parser() {\npublic boolean parse(%s %s, int %s) {\nreturn %s;\n}\n}",
                    shorten(JavaBnfConstants.PSI_BUILDER_CLASS), N.builder, N.level, body);
  }

  private void generateMetaMethodFields() {
    String parserClassShortName = "Parser";
    take(myMetaMethodFields).forEach((field, call) -> out(String.format("private static final %s %s = %s;", parserClassShortName, field, call)));
  }

  private void generateRootParserContent() {
    BnfRule rootRule = myFile.getRule(myGrammarRoot);
    List<BnfRule> extraRoots = new ArrayList<>();
    for (String ruleName : myRuleInfos.keySet()) {
      BnfRule rule = Objects.requireNonNull(myFile.getRule(ruleName));
      if (getAttribute(rule, KnownAttribute.ELEMENT_TYPE) != null) continue;
      if (!hasElementType(rule)) continue;
      if (Rule.isFake(rule) || Rule.isMeta(rule)) continue;
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
   * Meta-methods are methods that take several {@link Parser Parser} instances as parameters and return another {@link Parser instance}.
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
    String frameName = !children.isEmpty() && firstNonTrivial && !Rule.isMeta(rule) ? quote(R.getRuleDisplayName(rule, !isPrivate)) : null;

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
          else if (Rule.isMeta(rule) && GrammarUtil.isDoubleAngles(recoverWhile)) {
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

  @Override
  public StringBuilder generateAutoRecoveryCall(List<String> tokenTypes){
    StringBuilder sb = new StringBuilder(format("!nextTokenIsFast(%s, ", N.builder));
    appendTokenTypes(sb, tokenTypes);
    sb.append(")");
    return sb;
  }

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

  @NotNull NodeCall instantiateTokenChoiceCall(@NotNull ConsumeType consumeType, @NotNull String tokenSetName){
    return new ConsumeTokenChoiceCall(consumeType, tokenSetName, N.builder);
  }

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
        if (Rule.isExternal(subRule)) {
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
      if (expressions.size() == 1 && Rule.isMeta(rule)) {
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


  private @NotNull NodeCall generateConsumeToken(@NotNull ConsumeType consumeType, @NotNull String tokenName) {
    myTokensUsedInGrammar.add(tokenName);
    return new ConsumeTokenCall(consumeType, getElementType(tokenName), N.builder);
  }

  private static @NotNull NodeCall generateConsumeTextToken(@NotNull ConsumeType consumeType,
                                                            @NotNull String tokenText,
                                                            @NotNull String stateHolder) {
    return new ConsumeTokenCall(consumeType, "\"" + tokenText + "\"", stateHolder);
  }

  /*ElementTypes******************************************************************/

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
        JBIterable.from(sortedCompositeTypes.values()).map(this::ruleInfo).map(o -> o.implPackage + ".*").toSet()));
      if (G.generatePsiClassesMap) {
        imports.add(CommonClassNames.JAVA_UTIL_COLLECTIONS);
        imports.add(CommonClassNames.JAVA_UTIL_SET);
        imports.add("java.util.LinkedHashMap");
      }
      if (G.generatePsiFactory) {
        if (JBIterable.from(myRuleInfos.values()).find(o -> o.mixedAST) != null) {
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
        if (info.isAbstract) continue;
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
        if (info.isAbstract) continue;
        if (info.mixedAST) continue;
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
        if (info.isAbstract) continue;
        if (!info.mixedAST) continue;
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

  private boolean isIgnoredWhitespaceToken(@NotNull String tokenName, @NotNull String tokenText) {
    return isRegexpToken(tokenText) &&
           !myTokensUsedInGrammar.contains(tokenName) &&
           matchesAny(getRegexpTokenRegexp(tokenText), " ", "\n") &&
           !matchesAny(getRegexpTokenRegexp(tokenText), "a", "1", "_", ".");
  }

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
  private void generatePsi(Map<String, BnfRule> sortedPsiRules) throws IOException {
    checkClassAvailability(myPsiImplUtilClass);
    myRulesMethodsHelper.buildMaps(sortedPsiRules.values());
    for (BnfRule rule : sortedPsiRules.values()) {
      RuleInfo info = ruleInfo(rule);
      openOutput(info.intfClass);
      try {
        generatePsiIntf(rule, info);
      }
      finally {
        closeOutput();
      }
    }
    for (BnfRule rule : sortedPsiRules.values()) {
      RuleInfo info = ruleInfo(rule);
      openOutput(info.implClass);
      try {
        generatePsiImpl(rule, info);
      }
      finally {
        closeOutput();
      }
    }
    if (myVisitorClassName != null && myGrammarRoot != null) {
      openOutput(myVisitorClassName);
      try {
        generateVisitor(myVisitorClassName, sortedPsiRules);
      }
      finally {
        closeOutput();
      }
    }
  }


  private void generatePsiIntf(BnfRule rule, RuleInfo info) {
    String psiClass = info.intfClass;
    String stubClass = info.stub;
    Collection<String> psiSupers = info.superInterfaces;
    if (StringUtil.isNotEmpty(stubClass)) {
      psiSupers = new LinkedHashSet<>(psiSupers);
      psiSupers.add(JavaBnfConstants.STUB_BASED_PSI_ELEMENT + "<" + stubClass + ">");
    }

    Set<String> imports = new LinkedHashSet<>();
    imports.addAll(Arrays.asList("java.util.List",
                                 "org.jetbrains.annotations.*",
                                 JavaBnfConstants.PSI_ELEMENT_CLASS));
    imports.addAll(psiSupers);
    imports.addAll(getRuleMethodTypesToImport(rule));

    generateClassHeader(psiClass, imports, "", TypeKind.INTERFACE, ArrayUtil.toStringArray(psiSupers));
    generatePsiClassMethods(rule, info, true);
    out("}");
  }

  private void generatePsiImpl(@NotNull BnfRule rule, @NotNull RuleInfo info) {
    String psiClass = info.implClass;
    String superInterface = info.intfClass;
    String stubName = info.realStubClass;
    String implSuper = info.realSuperClass;

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
    for (BnfRule next = rule; next != null && next != topSuperRule; ) {
      if (!visited.add(next)) {
        addWarning(rule.getName() + " employs cyclic inheritance");
        break;
      }
      topSuperRule = next;
      String superClass = ruleInfo(next).realSuperClass;
      if (superClass == null) continue;
      next = getEffectiveSuperRule(myFile, next);
      if (next != null && next != topSuperRule && getAttribute(topSuperRule, KnownAttribute.MIXIN) == null) continue;
      topSuperClass = getRawClassName(superClass);
      constructors = myJavaHelper.findClassMethods(topSuperClass, JavaHelper.MethodType.CONSTRUCTOR, "*", -1);
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
      for (RuleMethodsHelper.MethodInfo methodInfo : myRulesMethodsHelper.getFor(rule)) {
        if (methodInfo.rule == null && !StringUtil.isEmpty(methodInfo.name)) {
          for (String s : getRootAttribute(myFile, KnownAttribute.PARSER_IMPORTS).asStrings()) {
            if (s.startsWith("static ")) imports.add(s);
          }
          break;
        }
      }
    }

    TypeKind typeKind = info.isAbstract ? TypeKind.ABSTRACT_CLASS : TypeKind.CLASS;
    generateClassHeader(psiClass, imports, "", typeKind, implSuper, superInterface);
    String shortName = StringUtil.getShortName(psiClass);
    if (constructors.isEmpty()) {
      out("public " + shortName + "(" + shorten(JavaBnfConstants.AST_NODE_CLASS) + " node) {");
      out("super(node);");
      out("}");
      newLine();
      if (stubName != null) {
        out("public " + shortName + "(" +
            shorten(stubName) + " stub, " +
            shorten(G.fallbackStubElementType) + " stubType) {");
        out("super(stub, stubType);");
        out("}");
        newLine();
      }
    }
    else {
      for (NavigatablePsiElement m : constructors) {
        List<String> types = myJavaHelper.getMethodTypes(m);
        Function<Integer, List<String>> annoProvider = i -> myJavaHelper.getParameterAnnotations(m, (i - 1) / 2);
        out("public " + shortName + "(" + getParametersString(types, 1, 3, substitutor, annoProvider, myShortener) + ") {");
        out("super(" + getParametersString(types, 1, 2, substitutor, annoProvider, myShortener) + ");");
        out("}");
        newLine();
      }
    }
    if (myVisitorClassName != null) {
      String shortened = shorten(myVisitorClassName);
      String r = G.visitorValue != null ? "<" + G.visitorValue + ">" : "";
      String t = G.visitorValue != null ? " " + G.visitorValue : "void";
      String ret = G.visitorValue != null ? "return " : "";
      boolean addOverride = topSuperRule != rule && info.mixin == null;
      if (!addOverride && topSuperClass != null) {
        main:
        for (String curClass = topSuperClass; curClass != null; curClass = myJavaHelper.getSuperClassName(curClass)) {
          for (NavigatablePsiElement m : myJavaHelper.findClassMethods(
            curClass, JavaHelper.MethodType.INSTANCE, "accept", 1, myVisitorClassName)) {
            String paramType = myJavaHelper.getMethodTypes(m).get(1);
            if (getRawClassName(paramType).endsWith(StringUtil.getShortName(myVisitorClassName))) {
              addOverride = true;
              break main;
            }
          }
        }
      }
      if (addOverride) {
        out(shorten(JavaBnfConstants.OVERRIDE_ANNO));
      }
      out("public " + r + t + " accept(" + shorten(JavaBnfConstants.NOTNULL_ANNO) + " " + shortened + r + " visitor) {");
      out(ret + "visitor.visit" + CommonRendererUtils.getRulePsiClassName(rule, null) + "(this);");
      out("}");
      newLine();
      out(shorten(JavaBnfConstants.OVERRIDE_ANNO));
      out("public void accept(" +
          shorten(JavaBnfConstants.NOTNULL_ANNO) +
          " " +
          shorten(JavaBnfConstants.PSI_ELEMENT_VISITOR_CLASS) +
          " visitor) {");
      out("if (visitor instanceof " + shortened + ") accept((" + shortened + ")visitor);");
      out("else super.accept(visitor);");
      out("}");
      newLine();
    }
    generatePsiClassMethods(rule, info, false);
    out("}");
  }

  private void generatePsiClassMethods(BnfRule rule, RuleInfo info, boolean intf) {
    Set<String> visited = new TreeSet<>();
    boolean mixedAST = info.mixedAST;
    for (RuleMethodsHelper.MethodInfo methodInfo : myRulesMethodsHelper.getFor(rule)) {
      if (StringUtil.isEmpty(methodInfo.name)) continue;
      switch (methodInfo.type) {
        case RULE, TOKEN -> generatePsiAccessor(rule, methodInfo, intf, mixedAST);
        case USER -> generateUserPsiAccessors(rule, methodInfo, intf, mixedAST);
        case MIXIN -> {
          boolean found = false;
          if (intf) {
            String mixinClass = getAttribute(rule, KnownAttribute.MIXIN);
            List<NavigatablePsiElement> methods =
              myJavaHelper.findClassMethods(mixinClass, JavaHelper.MethodType.INSTANCE, methodInfo.name, -1);
            for (NavigatablePsiElement method : methods) {
              generateUtilMethod(methodInfo.name, method, true, false, visited);
              found = true;
            }
          }
          List<NavigatablePsiElement> methods = findRuleImplMethods(myJavaHelper, myPsiImplUtilClass, methodInfo.name, rule);
          for (NavigatablePsiElement method : methods) {
            generateUtilMethod(methodInfo.name, method, intf, true, visited);
            found = true;
          }
          if (intf && !found) {
            String ruleClassName = shorten(info.intfClass);
            String implClassName = StringUtil.getShortName(String.valueOf(myPsiImplUtilClass));
            out("""
                  //WARNING: %s(...) is skipped
                  //matching %s(%s, ...)
                  //methods are not found in %s""",
                methodInfo.name, methodInfo.name, ruleClassName, implClassName);
            newLine();
            addWarning(format("%s.%s(%s, ...) method not found", implClassName, methodInfo.name, ruleClassName));
          }
        }
        default -> throw new AssertionError(methodInfo.toString());
      }
    }
  }

  public Collection<String> getRuleMethodTypesToImport(BnfRule rule) {
    Set<String> result = new TreeSet<>();

    Collection<RuleMethodsHelper.MethodInfo> methods = myRulesMethodsHelper.getFor(rule);
    for (RuleMethodsHelper.MethodInfo methodInfo : methods) {
      if (methodInfo.rule != null) {
        result.add(getAccessorType(methodInfo.rule));
      }
      else if (methodInfo.type == RuleMethodsHelper.MethodType.USER) {
        RuleMethodsHelper.MethodInfo targetInfo = null;
        for (Object m : resolveUserPsiPathMethods(rule, methodInfo.path.split("/"))) {
          if (m == null) break;
          if (m instanceof String) continue;
          targetInfo = (RuleMethodsHelper.MethodInfo)m;
        }
        if (targetInfo != null && targetInfo.rule != null) {
          result.add(getAccessorType(targetInfo.rule));
        }
      }
    }

    String mixinClass = getAttribute(rule, KnownAttribute.MIXIN);

    for (RuleMethodsHelper.MethodInfo methodInfo : methods) {
      if (methodInfo.type != RuleMethodsHelper.MethodType.MIXIN) continue;

      List<NavigatablePsiElement> mixinMethods =
        myJavaHelper.findClassMethods(mixinClass, JavaHelper.MethodType.INSTANCE, methodInfo.name, -1);
      List<NavigatablePsiElement> implMethods = findRuleImplMethods(myJavaHelper, myPsiImplUtilClass, methodInfo.name, rule);

      collectMethodTypesToImport(mixinMethods, false, result);
      collectMethodTypesToImport(implMethods, true, result);
    }
    return result;
  }

  private void collectMethodTypesToImport(@NotNull List<NavigatablePsiElement> methods, boolean isInPsiUtil, @NotNull Set<String> result) {
    for (NavigatablePsiElement method : methods) {
      List<String> types = myJavaHelper.getMethodTypes(method);
      String returnType = ContainerUtil.getFirstItem(types);
      addTypeToImports(returnType, myJavaHelper.getAnnotations(method), result);

      for (JavaHelper.TypeParameterInfo generic : myJavaHelper.getGenericParameters(method)) {
        for (String type : generic.getExtendsList()) {
          addTypeToImports(type, emptyList(), result);
        }
        for (String type : generic.getAnnotations()) {
          addTypeToImports(type, emptyList(), result);
        }
      }

      for (int i = isInPsiUtil ? 3 : 1, count = types.size(); i < count; i += 2) {
        String type = types.get(i);
        addTypeToImports(type, myJavaHelper.getParameterAnnotations(method, (i - 1) / 2), result);
      }

      for (String exception : myJavaHelper.getExceptionList(method)) {
        addTypeToImports(exception, emptyList(), result);
      }
    }
  }


  private void generatePsiAccessor(BnfRule rule, RuleMethodsHelper.MethodInfo methodInfo, boolean intf, boolean mixedAST) {
    Cardinality type = methodInfo.cardinality;
    boolean isToken = methodInfo.rule == null;

    boolean many = type.many();

    String getterName = methodInfo.generateGetterName();
    if (!intf) out(shorten(JavaBnfConstants.OVERRIDE_ANNO));
    if (type == REQUIRED) {
      out(shorten(JavaBnfConstants.NOTNULL_ANNO));
    }
    else if (type == OPTIONAL) {
      out(shorten(JavaBnfConstants.NULLABLE_ANNO));
    }
    else {
      out(shorten(JavaBnfConstants.NOTNULL_ANNO));
    }
    String s = isToken ? JavaBnfConstants.PSI_ELEMENT_CLASS : getAccessorType(methodInfo.rule);
    String className = shorten(s);
    String tail = intf ? "();" : "() {";
    out((intf ? "" : "public ") +
        (many ? shorten(CommonClassNames.JAVA_UTIL_LIST) + "<" : "") +
        className +
        (many ? "> " : " ") +
        getterName +
        tail);
    if (!intf) {
      out("return " + generatePsiAccessorImplCall(rule, methodInfo, mixedAST) + ";");
      out("}");
    }
    newLine();
  }

  private String generatePsiAccessorImplCall(@NotNull BnfRule rule, @NotNull RuleMethodsHelper.MethodInfo methodInfo, boolean mixedAST) {
    boolean isToken = methodInfo.rule == null;

    Cardinality type = methodInfo.cardinality;
    boolean many = type.many();
    boolean required = type == REQUIRED && !many;
    boolean stubbed = !isToken &&
                      ruleInfo(rule).realStubClass != null &&
                      ruleInfo(methodInfo.rule).realStubClass != null;
    String result;
    // todo REMOVEME. Keep old generation logic for a while.
    if (!mixedAST && myNoStubs) {
      if (isToken) {
        return (type == REQUIRED ? "findNotNullChildByType" : "findChildByType") +
               "(" + getElementType(methodInfo.path) + ")";
      }
      else {
        String className = shorten(getAccessorType(methodInfo.rule));
        return many ? format("%s.getChildrenOfTypeAsList(this, %s.class)", shorten(myPsiTreeUtilClass), className) :
               (type == REQUIRED ? "findNotNullChildByClass" : "findChildByClass") + "(" + className + ".class)";
      }
    }
    // new logic
    if (isToken) {
      String getterName = mixedAST ? "findPsiChildByType" : "findChildByType";
      result = getterName + "(" + getElementType(methodInfo.path) + ")";
    }
    else {
      String className = shorten(getAccessorType(methodInfo.rule));
      String getterName = stubbed && many ? "getStubChildrenOfTypeAsList" :
                          stubbed ? "getStubChildOfType" :
                          many ? "getChildrenOfTypeAsList" : "getChildOfType";
      result = format("%s.%s(this, %s.class)", shorten(myPsiTreeUtilClass), getterName, className);
    }
    return required && !mixedAST ? "notNullChild(" + result + ")" : result;
  }

  private @NotNull String getAccessorType(@NotNull BnfRule rule) {
    if (Rule.isExternal(rule)) {
      Pair<String, String> first = ContainerUtil.getFirstItem(getAttribute(rule, KnownAttribute.IMPLEMENTS));
      return Objects.requireNonNull(first).second;
    }
    else {
      return ruleInfo(rule).intfClass;
    }
  }

  private JBIterable<?> resolveUserPsiPathMethods(BnfRule startRule,
                                                  String[] splitPath) {
    BnfRule[] targetRule = {startRule};
    return JBIterable.generate(0, i -> i + 1).take(splitPath.length).map(i -> {
      String pathElement = splitPath[i];
      int indexStart = pathElement.indexOf('[');
      String item = indexStart > -1 ? pathElement.substring(0, indexStart).trim() : pathElement.trim();

      if (item.isEmpty()) return item;
      if (targetRule[0] == null) return null;
      RuleMethodsHelper.MethodInfo targetInfo = myRulesMethodsHelper.getMethodInfo(targetRule[0], item);
      targetRule[0] = targetInfo == null ? null : targetInfo.rule;
      return targetInfo;
    });
  }

  private void generateUserPsiAccessors(BnfRule startRule, RuleMethodsHelper.MethodInfo methodInfo, boolean intf, boolean mixedAST) {
    StringBuilder sb = new StringBuilder();
    BnfRule targetRule = startRule;
    Cardinality cardinality = REQUIRED;
    String context = "";
    String[] splitPath = methodInfo.path.split("/");

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


      RuleMethodsHelper.MethodInfo targetInfo = (RuleMethodsHelper.MethodInfo)m;
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
      else if (index != null && !targetInfo.cardinality.many()) {
        error = "'[" + index + "]' unexpected after '" + base + getCardinalityText(targetInfo.cardinality) + "'";
      }
      else if (!last && index == null && targetInfo.cardinality.many()) {
        error = "'[<index>]' required after '" + base + getCardinalityText(targetInfo.cardinality) + "'";
      }
      else if (i > 0 && StringUtil.isEmpty(targetInfo.name)) {
        error = "'" + base + "' accessor suppressed in '" + splitPath[i - 1] + "'";
      }
      else {
        error = null;
      }
      if (error != null) {
        if (intf) { // warn only once
          addWarning(format("%s#%s(\"%s\"): %s", startRule.getName(), methodInfo.name, methodInfo.path, error));
        }
        return;
      }

      boolean many = targetInfo.cardinality.many();
      String className = shorten(targetInfo.rule == null ? JavaBnfConstants.PSI_ELEMENT_CLASS : getAccessorType(targetInfo.rule));

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
      if (StringUtil.isNotEmpty(targetInfo.name)) {
        targetCall = targetInfo.generateGetterName() + "()";
      }
      else {
        targetCall = generatePsiAccessorImplCall(startRule, targetInfo, mixedAST);
      }
      sb.append(context).append(targetCall).append(";\n");

      context = curId;

      targetRule = targetInfo.rule; // next accessors
      cardinality = targetInfo.cardinality;
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

    if (!intf) out(shorten(JavaBnfConstants.OVERRIDE_ANNO));

    if (!cardinality.many() && cardinality == REQUIRED && !totalNullable) {
      out(shorten(JavaBnfConstants.NOTNULL_ANNO));
    }
    else {
      out(shorten(JavaBnfConstants.NULLABLE_ANNO));
    }

    boolean many = cardinality.many();
    String s = targetRule == null ? JavaBnfConstants.PSI_ELEMENT_CLASS : getAccessorType(targetRule);
    String className = shorten(s);
    String getterName = CommonRendererUtils.getGetterName(methodInfo.name);
    String tail = intf ? "();" : "() {";
    out((intf ? "" : "public ") +
        (many ? shorten(CommonClassNames.JAVA_UTIL_LIST) + "<" : "") +
        className +
        (many ? "> " : " ") +
        getterName +
        tail);

    if (!intf) {
      out(sb.toString());
      out("}");
    }
    newLine();
  }

  private void generateUtilMethod(String methodName,
                                  NavigatablePsiElement method,
                                  boolean intf,
                                  boolean isInPsiUtil,
                                  Set<String> visited) {
    List<String> methodTypes = method == null ? Collections.emptyList() : myJavaHelper.getMethodTypes(method);
    String returnType = methodTypes.isEmpty() ? "void" : shorten(methodTypes.get(0));
    int offset = methodTypes.isEmpty() || isInPsiUtil && methodTypes.size() < 3 ? 0 :
                 isInPsiUtil ? 3 : 1;
    if (!visited.add(methodName + methodTypes.subList(offset, methodTypes.size()))) return;
    if (intf && methodTypes.size() == offset && "toString".equals(methodName)) return;

    List<JavaHelper.TypeParameterInfo> genericParameters = myJavaHelper.getGenericParameters(method);
    List<String> exceptionList = myJavaHelper.getExceptionList(method);

    if (!intf /*|| hasMethodInInfos*/) out(shorten(JavaBnfConstants.OVERRIDE_ANNO));
    for (String s : myJavaHelper.getAnnotations(method)) {
      if ("java.lang.Override".equals(s)) continue;
      if (s.startsWith("kotlin.")) continue;
      out("@" + shorten(s));
    }
    Function<Integer, List<String>> annoProvider = i -> myJavaHelper.getParameterAnnotations(method, (i - 1) / 2);
    Function<String, String> substitutor = ParserGeneratorUtil::unwrapTypeArgumentForParamList;
    out("%s%s%s %s(%s)%s%s",
        intf ? "" : "public ",
        getGenericClauseString(genericParameters, myShortener),
        returnType,
        methodName,
        getParametersString(methodTypes, offset, 3, substitutor, annoProvider, myShortener),
        getThrowsString(exceptionList, myShortener),
        intf ? ";" : " {");
    if (!intf) {
      String implUtilRef = shorten(StringUtil.notNullize(myPsiImplUtilClass, KnownAttribute.PSI_IMPL_UTIL_CLASS.getName()));
      String string = getParametersString(methodTypes, offset, 2, substitutor, annoProvider, myShortener);
      out("%s%s.%s(this%s);", "void".equals(returnType) ? "" : "return ", implUtilRef, methodName,
          string.isEmpty() ? "" : ", " + string);
      out("}");
    }
    newLine();
  }

  /*Syntax******************************************************************/
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