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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Trinity;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import com.intellij.util.Functions;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.*;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.parser.GeneratedParserUtilBase.Parser;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.util.containers.ContainerUtil.emptyList;
import static com.intellij.util.containers.ContainerUtil.map;
import static java.lang.String.format;
import static java.util.stream.Collectors.*;
import static org.intellij.grammar.analysis.BnfFirstNextAnalyzer.BNF_MATCHES_ANY;
import static org.intellij.grammar.analysis.BnfFirstNextAnalyzer.BNF_MATCHES_EOF;
import static org.intellij.grammar.generator.BnfConstants.*;
import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality.*;
import static org.intellij.grammar.generator.RuleGraphHelper.getSynonymTargetOrSelf;
import static org.intellij.grammar.generator.RuleGraphHelper.hasPsiClass;
import static org.intellij.grammar.psi.BnfTypes.*;


/**
 * @author gregory
 *         Date 16.07.11 10:41
 */
public class ParserGenerator {
  public static final Logger LOG = Logger.getInstance("ParserGenerator");

  private static final String TYPE_TEXT_SEPARATORS = "<>,[]";

  private static final Function<String, String> genericUnwrapper = type -> {
    if (type.startsWith("<") && type.endsWith(">")) {
      return type.substring(1, type.length() - 1);
    }
    return type;
  };

  static class RuleInfo {
    final String name;
    final boolean isFake;
    final String elementType;
    final String parserClass;
    final String intfPackage;
    final String implPackage;
    final String intfClass;
    final String implClass;
    final String mixin;
    final String stub;
    String realStubClass;
    Set<String> superInterfaces;
    boolean mixedAST;
    String realSuperClass;
    boolean isAbstract;
    boolean isInElementType;

    RuleInfo(String name, boolean isFake,
             String elementType, String parserClass,
             String intfPackage, String implPackage,
             String intfClass, String implClass, String mixin, String stub) {
      this.name = name;
      this.isFake = isFake;
      this.elementType = elementType;
      this.parserClass = parserClass;
      this.intfPackage = intfPackage;
      this.implPackage = implPackage;
      this.stub = stub;
      this.intfClass = intfPackage + "." + intfClass;
      this.implClass = implPackage + "." + implClass;
      this.mixin = mixin;
      //this.stubName = stubName;
      //this.classes = classes;
      //this.mixedAST = mixedAST;
    }
  }

  @NotNull
  RuleInfo ruleInfo(BnfRule rule) {
    return ObjectUtils.notNull(myRuleInfos.get(rule.getName()));
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

  private final Map<String, Collection<String>> myTokenSets = new TreeMap<>();
  private final Map<String, String> mySimpleTokens;
  private final Set<String> myTokensUsedInGrammar = new LinkedHashSet<>();
  private final boolean myNoStubs;

  private final BnfFile myFile;
  private final String mySourcePath;
  private final String myOutputPath;
  private final String myPackagePrefix;
  private final String myGrammarRoot;
  private final String myGrammarRootParser;
  private final String myParserUtilClass;
  private final String myPsiImplUtilClass;
  private final String myPsiTreeUtilClass;

  private final NameFormat myIntfClassFormat;
  private final NameFormat myImplClassFormat;

  private final String myVisitorClassName;
  private final String myTypeHolderClass;


  private int myOffset;
  private PrintWriter myOut;
  private Function<String, String> myShortener;

  private final RuleGraphHelper myGraphHelper;
  private final ExpressionHelper myExpressionHelper;
  private final RuleMethodsHelper myRulesMethodsHelper;
  private final JavaHelper myJavaHelper;

  final Names N;
  final GenOptions G;

  public ParserGenerator(@NotNull BnfFile psiFile,
                         @NotNull String sourcePath,
                         @NotNull String outputPath,
                         @NotNull String packagePrefix) {
    myFile = psiFile;
    mySourcePath = sourcePath;
    myOutputPath = outputPath;
    myPackagePrefix = packagePrefix;

    G = new GenOptions(myFile);
    N = G.names;

    myIntfClassFormat = getPsiClassFormat(myFile);
    myImplClassFormat = getPsiImplClassFormat(myFile);
    myParserUtilClass = getRootAttribute(myFile, KnownAttribute.PARSER_UTIL_CLASS);
    myPsiImplUtilClass = getRootAttribute(myFile, KnownAttribute.PSI_IMPL_UTIL_CLASS);
    myPsiTreeUtilClass = getRootAttribute(myFile, KnownAttribute.PSI_TREE_UTIL_CLASS);
    String tmpVisitorClass = getRootAttribute(myFile, KnownAttribute.PSI_VISITOR_NAME);
    tmpVisitorClass = !G.generateVisitor || StringUtil.isEmpty(tmpVisitorClass) ? null :
                      !tmpVisitorClass.equals(myIntfClassFormat.strip(tmpVisitorClass)) ? tmpVisitorClass :
                      myIntfClassFormat.apply("") + tmpVisitorClass;
    myVisitorClassName = tmpVisitorClass == null || !tmpVisitorClass.equals(StringUtil.getShortName(tmpVisitorClass)) ?
                         tmpVisitorClass : getRootAttribute(myFile, KnownAttribute.PSI_PACKAGE) + "." + tmpVisitorClass;
    myTypeHolderClass = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);

    mySimpleTokens = new LinkedHashMap<>(RuleGraphHelper.getTokenTextToNameMap(myFile));
    myGraphHelper = RuleGraphHelper.getCached(myFile);
    myExpressionHelper = new ExpressionHelper(myFile, myGraphHelper, true);
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
        noPsi ? null : getRulePsiClassName(r, myIntfClassFormat), noPsi ? null : getRulePsiClassName(r, myImplClassFormat),
        noPsi ? null : getAttribute(r, KnownAttribute.MIXIN), noPsi ? null : getAttribute(r, KnownAttribute.STUB_CLASS)));
    }
    myGrammarRootParser = rootRule == null ? null : ruleInfo(rootRule).parserClass;
    myNoStubs = JBIterable.from(myRuleInfos.values()).find(o -> o.stub != null) == null;

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
        AST_WRAPPER_PSI_ELEMENT_CLASS.equals(superRuleClass) ? STUB_BASED_PSI_ELEMENT_BASE + "<" + stubName + ">" :
        superRuleClass.contains("?") ? superRuleClass.replaceAll("\\?", stubName) : superRuleClass;
      // mixin attribute overrides "extends":
      info.realSuperClass = StringUtil.notNullize(info.mixin, adjustedSuperRuleClass);
      info.mixedAST = topInfo != null ? topInfo.mixedAST :
                      JBIterable.of(superRuleClass, info.realSuperClass)
                        .flatMap(s -> JBIterable.generate(s, myJavaHelper::getSuperClassName))
                        .find(BnfConstants.COMPOSITE_PSI_ELEMENT_CLASS::equals) != null;
    }
  }

  private void openOutput(String className) throws IOException {
    String classNameAdjusted = myPackagePrefix.isEmpty() ? className : StringUtil.trimStart(className, myPackagePrefix + ".");
    File file = new File(myOutputPath, classNameAdjusted.replace('.', File.separatorChar) + ".java");
    myOut = openOutputInner(file);
  }

  protected PrintWriter openOutputInner(File file) throws IOException {
    //noinspection ResultOfMethodCallIgnored
    file.getParentFile().mkdirs();
    return new PrintWriter(new FileOutputStream(file));
  }

  private void closeOutput() {
    myOut.close();
  }

  public void out(String s, Object... args) {
    out(format(s, args));
  }

  public void out(String s) {
    int length = s.length();
    if (length == 0) {
      myOut.println();
      return;
    }
    boolean newStatement = true;
    for (int start = 0, end; start < length; start = end + 1) {
      boolean isComment = s.startsWith("//", start);
      end = StringUtil.indexOf(s, '\n', start, length);
      if (end == -1) end = length;
      String substring = s.substring(start, end);
      if (!isComment && (substring.startsWith("}") || substring.startsWith(")"))) {
        myOffset--;
        newStatement = true;
      }
      if (myOffset > 0) {
        myOut.print(StringUtil.repeat("  ", newStatement ? myOffset : myOffset + 1));
      }
      myOut.println(substring);
      if (isComment) {
        newStatement = true;
      }
      else if (substring.endsWith("{")) {
        myOffset++;
        newStatement = true;
      }
      else if (substring.endsWith("(")) {
        myOffset++;
        newStatement = false;
      }
      else {
        newStatement = substring.endsWith(";") || substring.endsWith("}");
      }
    }
  }

  public void newLine() {
    out("");
  }

  public void generate() throws IOException {
    {
      generateParser();
    }
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
      info.superInterfaces = ContainerUtil.newLinkedHashSet(getSuperInterfaceNames(myFile, rule, myIntfClassFormat));
    }
    if (G.generatePsi) {
      calcRealSuperClasses(sortedPsiRules);
    }
    if (myGrammarRoot != null && (G.generateTokenTypes || G.generateElementTypes || G.generatePsi && G.generatePsiFactory)) {
      openOutput(myTypeHolderClass);
      try {
        generateElementTypesHolder(myTypeHolderClass, sortedCompositeTypes);
      }
      finally {
        closeOutput();
      }
    }
    if (G.generatePsi) {
      checkClassAvailability(myFile, myPsiImplUtilClass, "PSI method signatures will not be detected");
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
  }

  private void generateVisitor(String psiClass, Map<String, BnfRule> sortedRules) {
    String superIntf = ObjectUtils.notNull(ContainerUtil.getFirstItem(getRootAttribute(myFile, KnownAttribute.IMPLEMENTS)),
                                           KnownAttribute.IMPLEMENTS.getDefaultValue().get(0)).second;
    String shortSuperIntf = StringUtil.getShortName(superIntf);
    List<String> imports = ContainerUtil.newArrayList("org.jetbrains.annotations.*", PSI_ELEMENT_VISITOR_CLASS, superIntf);
    MultiMap<String, String> supers = MultiMap.createSmart();
    for (BnfRule rule : sortedRules.values()) {
      supers.putValues(rule.getName(), getSuperInterfaceNames(myFile, rule, myIntfClassFormat));
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
    generateClassHeader(psiClass + r, imports, "", Java.CLASS, PSI_ELEMENT_VISITOR_CLASS);
    Set<String> visited = new HashSet<>();
    Set<String> all = new TreeSet<>();
    for (BnfRule rule : sortedRules.values()) {
      String methodName = getRulePsiClassName(rule, null);
      visited.add(methodName);
      out("public " + t + " visit" + methodName + "(@NotNull " + getRulePsiClassName(rule, myIntfClassFormat) + " o) {");
      boolean first = true;
      for (String top : supers.get(rule.getName())) {
        if (!first && top.equals(superIntf)) continue;
        int trimIdx = StringUtil.indexOfAny(top, TYPE_TEXT_SEPARATORS); // trim generics
        top = myShortener.fun(trimIdx > 0 ? top.substring(0, trimIdx) : top);
        if (first) all.add(top);
        top = myIntfClassFormat.strip(top);
        String text = "visit" + top + "(o);";
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
    all.remove(shortSuperIntf);
    for (String top : JBIterable.from(all).append(shortSuperIntf)) {
      String methodName = myIntfClassFormat.strip(top);
      if (visited.contains(methodName)) continue;
      out("public " + t + " visit" + methodName + "(@NotNull " + myShortener.fun(top) + " o) {");
      if (!methodName.equals(top) && !top.equals(shortSuperIntf)) {
        out(ret + "visit" + myIntfClassFormat.strip(shortSuperIntf) + "(o);");
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
    imports.addAll(Arrays.asList(PSI_BUILDER_CLASS,
                                 PSI_BUILDER_CLASS + ".Marker",
                                 staticStarImport(myTypeHolderClass)));
    if (G.generateTokenSets && hasAtLeastOneTokenChoice(myFile, ownRuleNames)) {
      imports.add(staticStarImport(myTypeHolderClass + "." + TOKEN_SET_HOLDER_NAME));
    }
    if (StringUtil.isNotEmpty(myParserUtilClass)) {
      imports.add(staticStarImport(myParserUtilClass));
    }
    if (!rootParser) {
      imports.add(staticStarImport(myGrammarRootParser));
    }
    else {
      imports.addAll(Arrays.asList(IELEMENTTYPE_CLASS,
                                   IFILEELEMENTTYPE_CLASS,
                                   AST_NODE_CLASS,
                                   TOKEN_SET_CLASS,
                                   PSI_PARSER_CLASS,
                                   LIGHT_PSI_PARSER_CLASS));
    }
    imports.addAll(parserImports);

    generateClassHeader(parserClass, imports,
                        "@SuppressWarnings({\"SimplifiableIfStatement\", \"UnusedAssignment\"})",
                        Java.CLASS, "",
                        rootParser ? PSI_PARSER_CLASS : "",
                        rootParser ? LIGHT_PSI_PARSER_CLASS : "");

    if (rootParser) {
      generateRootParserContent();
    }
    for (String ruleName : ownRuleNames) {
      BnfRule rule = ObjectUtils.assertNotNull(myFile.getRule(ruleName));
      if (Rule.isExternal(rule) || Rule.isFake(rule)) continue;
      if (myExpressionHelper.getExpressionInfo(rule) != null) continue;
      out("/* ********************************************************** */");
      generateNode(rule, rule.getExpression(), getFuncName(rule), new THashSet<>());
      newLine();
    }
    for (String ruleName : ownRuleNames) {
      BnfRule rule = myFile.getRule(ruleName);
      ExpressionHelper.ExpressionInfo info = myExpressionHelper.getExpressionInfo(rule);
      if (info != null && info.rootRule == rule) {
        out("/* ********************************************************** */");
        ExpressionGeneratorHelper.generateExpressionRoot(info, this);
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
    Map<String, String> reversedLambdas = new THashMap<>();
    take(myParserLambdas).forEach((name, body) -> {
      String call = reversedLambdas.get(body);
      if (call == null) {
        call = generateParserInstance(body);
        reversedLambdas.put(body, name);
      }
      out("static final Parser " + name + " = " + call + ";");
      myRenderedLambdas.put(name, parserClass);
    });
  }

  @NotNull
  private String generateParserInstance(@NotNull String body) {
    return G.javaVersion == JavaVersion.JAVA_8
           ? format("(%s, %s) -> %s", N.builder, N.level, body)
           : format("new Parser() {\npublic boolean parse(PsiBuilder %s, int %s) {\nreturn %s;\n}\n}", N.builder, N.level, body);
  }

  private void generateMetaMethodFields() {
    take(myMetaMethodFields).forEach((field, call) -> out("private static final Parser " + field + " = " + call + ";"));
  }

  private void generateRootParserContent() {
    BnfRule rootRule = myFile.getRule(myGrammarRoot);
    List<BnfRule> extraRoots = new ArrayList<>();
    for (String ruleName : myRuleInfos.keySet()) {
      BnfRule rule = ObjectUtils.assertNotNull(myFile.getRule(ruleName));
      if (getAttribute(rule, KnownAttribute.ELEMENT_TYPE) != null) continue;
      if (!RuleGraphHelper.hasElementType(rule)) continue;
      if (Rule.isFake(rule) || Rule.isMeta(rule)) continue;
      ExpressionHelper.ExpressionInfo info = myExpressionHelper.getExpressionInfo(rule);
      if (info != null && info.rootRule != rule) continue;
      if (!Boolean.TRUE.equals(getAttribute(rule, KnownAttribute.EXTRA_ROOT))) continue;
      extraRoots.add(rule);
    }

    List<Set<String>> extendsSet = buildExtendsSet(myGraphHelper.getRuleExtendsMap());
    boolean generateExtendsSets = !extendsSet.isEmpty();
    out("public ASTNode parse(IElementType %s, PsiBuilder %s) {", N.root, N.builder);
    out("parseLight(%s, %s);", N.root, N.builder);
    out("return %s.getTreeBuilt();", N.builder);
    out("}");
    newLine();
    out("public void parseLight(IElementType %s, PsiBuilder %s) {", N.root, N.builder);
    out("boolean %s;", N.result);
    out("%s = adapt_builder_(%s, %s, this, %s);", N.builder, N.root, N.builder, generateExtendsSets ? "EXTENDS_SETS_" : null);
    out("Marker %s = enter_section_(%s, 0, _COLLAPSE_, null);", N.marker, N.builder);
    out("if (%s instanceof IFileElementType) {", N.root);
    out("%s = parse_root_(%s, %s, 0);", N.result, N.root, N.builder);
    out("}");
    out("else {");
    if (extraRoots.isEmpty()) {
      out("%s = false;", N.result);
    }
    else {
      out("%s = parse_extra_roots_(%s, %s, 0);", N.result, N.root, N.builder);
    }
    out("}");
    out("exit_section_(%s, 0, %s, %s, %s, true, TRUE_CONDITION);", N.builder, N.marker, N.root, N.result);
    out("}");
    if (!extraRoots.isEmpty()) {
      newLine();
      out("static boolean parse_extra_roots_(IElementType %s, PsiBuilder %s, int %s) {", N.root, N.builder, N.level);
      boolean first = true;
      for (BnfRule rule : extraRoots) {
        if (first) out("boolean %s;", N.result);
        String elementType = getElementType(rule);
        out("%sif (%s == %s) {", first ? "" : "else ", N.root, elementType);
        String nodeCall = generateNodeCall(ObjectUtils.notNull(rootRule, rule), null, rule.getName()).render(N);
        out("%s = %s;", N.result, nodeCall);
        out("}");
        if (first) first = false;
      }
      if (first) {
        out("return false;");
      }
      else {
        out("else {");
        out("%s = false;", N.result);
        out("}");
        out("return %s;", N.result);
      }
      out("}");
    }
    newLine();
    {
      String nodeCall = rootRule == null ? "false" : generateNodeCall(rootRule, null, myGrammarRoot).render(N);
      out("protected boolean parse_root_(IElementType %s, PsiBuilder %s, int %s) {", N.root, N.builder, N.level);
      out("return %s;", nodeCall);
      out("}");
      newLine();
    }
    if (generateExtendsSets) {
      out("public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {");
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

  @NotNull
  private List<Set<String>> buildExtendsSet(@NotNull MultiMap<BnfRule, BnfRule> map) {
    if (map.isEmpty()) return Collections.emptyList();
    List<Set<String>> result = new ArrayList<>();
    for (Map.Entry<BnfRule, Collection<BnfRule>> entry : map.entrySet()) {
      Set<String> set = null;
      for (BnfRule rule : entry.getValue()) {
        RuleInfo info = ruleInfo(rule);
        if (!RuleGraphHelper.hasElementType(rule)) continue;
        String elementType = info.isFake && !info.isInElementType ||
                             getSynonymTargetOrSelf(rule) != rule ? null : info.elementType;
        if (StringUtil.isEmpty(elementType)) continue;
        if (set == null) set = ContainerUtil.newTreeSet();
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

  private enum Java { CLASS, INTERFACE, ABSTRACT_CLASS }
  private void generateClassHeader(String className,
                                   Collection<String> imports,
                                   String annos,
                                   Java javaType,
                                   String... supers) {
    generateFileHeader(className);
    String packageName = StringUtil.getPackageName(className);
    String shortClassName = StringUtil.getShortName(className);
    out("package %s;", packageName);
    newLine();
    Set<String> includedPackages = JBIterable.from(imports)
      .filter(o -> !o.startsWith("static") && o.endsWith(".*"))
      .map(o -> StringUtil.trimEnd(o, ".*"))
      .append(packageName).toSet();
    Set<String> includedClasses = new THashSet<>();
    for (RuleInfo info : myRuleInfos.values()) {
      if (includedPackages.contains(info.intfPackage)) includedClasses.add(StringUtil.getShortName(info.intfClass));
      if (includedPackages.contains(info.implPackage)) includedClasses.add(StringUtil.getShortName(info.implClass));
    }
    Set<String> realImports = ContainerUtil.newLinkedHashSet(packageName + ".*");
    Function<String, String> shortener = newClassNameShortener(realImports);
    for (String item : imports) {
      for (String s : StringUtil.tokenize(item.replaceAll("\\s+", " "), TYPE_TEXT_SEPARATORS)) {
        s = StringUtil.trimStart(StringUtil.trimStart(s, "? super "), "? extends ");
        if (!s.contains(".") || !s.equals(shortener.fun(s))) continue;
        if (packageName.equals(StringUtil.getPackageName(s))) continue;
        if (includedClasses.contains(StringUtil.getShortName(s))) continue;
        realImports.add(s);
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
        sb.append(" extends ").append(shortener.fun(aSuper));
      }
      else if (javaType != Java.INTERFACE && i == 1) {
        sb.append(" implements ").append(shortener.fun(aSuper));
      }
      else {
        sb.append(", ").append(shortener.fun(aSuper));
      }
    }
    if (StringUtil.isNotEmpty(annos)) {
      out(annos);
    }
    out("public %s %s%s {", Case.LOWER.apply(javaType.name()).replace('_', ' '), shortClassName, sb.toString());
    newLine();
    myShortener = shortener;
  }

  @NotNull
  private static Function<String, String> newClassNameShortener(final Set<String> realImports) {
    return s -> {
      boolean changed = false;
      StringBuilder sb = new StringBuilder();
      boolean vararg = s.endsWith("...");
      for (String part : StringUtil.tokenize(new StringTokenizer(StringUtil.trimEnd(s, "..."), TYPE_TEXT_SEPARATORS, true))) {
        String pkg;
        String wildcard = part.startsWith("? super ") ? "? super " : part.startsWith("? extends ") ? "? extends " : "";
        part = StringUtil.trimStart(part, wildcard);
        if (TYPE_TEXT_SEPARATORS.contains(part)) {
          sb.append(part).append(part.equals(",") ? " " : "");
        }
        else if (realImports.contains(part) ||
                 "java.lang".equals(pkg = StringUtil.getPackageName(part)) ||
                 realImports.contains(pkg + ".*")) {
          sb.append(wildcard).append(StringUtil.getShortName(part));
          changed = true;
        }
        else {
          sb.append(wildcard).append(part);
        }
      }
      return changed ? sb.append(vararg? "..." : "").toString() : s;
    };
  }

  private void generateFileHeader(String className) {
    String header = getRootAttribute(myFile, KnownAttribute.CLASS_HEADER, className);
    String text = StringUtil.isEmpty(header) ? "" : getStringOrFile(header);
    if (StringUtil.isNotEmpty(text)) {
      out(text);
    }
    myOffset = 0;
  }

  private String getStringOrFile(String classHeader) {
    try {
      File file = new File(mySourcePath, classHeader);
      if (file.exists()) return FileUtil.loadFile(file);
    }
    catch (IOException ex) {
      LOG.error(ex);
    }
    return classHeader.startsWith("//") || classHeader.startsWith("/*")? classHeader :
           StringUtil.countNewLines(classHeader) > 0 ? "/*\n" + classHeader + "\n*/" :
           "// " + classHeader;
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
    String parameterList = parameterNames.stream().map(it -> "Parser " + it).collect(joining(", "));
    String argumentList = String.join(", ", parameterNames);
    String metaParserMethodName = getWrapperParserMetaMethodName(methodName);
    String call = String.format("%s(%s, %s + 1, %s)", methodName, N.builder, N.level, argumentList);
    // @formatter:off
    out("%sstatic Parser %s(%s) {", isRule ? "" : "private ", metaParserMethodName, parameterList);
      out("return %s;", generateParserInstance(call));
    out("}");
    // @formatter:on
  }

  void generateNode(BnfRule rule, BnfExpression initialNode, String funcName, Set<BnfExpression> visited) {
    boolean isRule = initialNode.getParent() == rule;
    BnfExpression node = getNonTrivialNode(initialNode);

    final List<String> metaParameters = collectExtraArguments(rule, node);
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

    final boolean canCollapse = !isPrivate && (!isLeft || isLeftInner) && firstNonTrivial && myGraphHelper.canCollapse(rule);

    String elementType = getElementType(rule);
    String elementTypeRef = !isPrivate && StringUtil.isNotEmpty(elementType) ? elementType : null;

    boolean isSingleNode = node instanceof BnfReferenceOrToken || node instanceof BnfLiteralExpression || node instanceof BnfExternalExpression;

    List<BnfExpression> children = isSingleNode ? Collections.singletonList(node) : getChildExpressions(node);
    String frameName = !children.isEmpty() && firstNonTrivial && !Rule.isMeta(rule) ? quote(getRuleDisplayName(rule, !isPrivate)) : null;

    String extraParameters = metaParameters.stream().map(it -> ", Parser " + it).collect(Collectors.joining());
    out("%sstatic boolean %s(PsiBuilder %s, int %s%s) {", !isRule ? "private " : isPrivate ? "" : "public ", funcName, N.builder, N.level, extraParameters);
    if (isSingleNode) {
      if (isPrivate && !isLeftInner && recoverWhile == null && frameName == null) {
        String nodeCall = generateNodeCall(rule, node, getNextName(funcName, 0)).render(N);
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
    final boolean alwaysTrue = children.isEmpty() || type == BNF_OP_OPT || type == BNF_OP_ZEROMORE;
    boolean pinned = pinMatcher.active() && pinMatcher.shouldGenerate(children);
    if (!alwaysTrue) {
      out("boolean %s%s%s;", N.result, children.isEmpty() ? " = true" : "", pinned ? format(", %s", N.pinned) : "");
    }

    List<String> modifierList = ContainerUtil.newSmartList();
    if (canCollapse) modifierList.add("_COLLAPSE_");
    if (isLeftInner) modifierList.add("_LEFT_INNER_");
    else if (isLeft) modifierList.add("_LEFT_");
    if (type == BNF_OP_AND) modifierList.add("_AND_");
    else if (type == BNF_OP_NOT) modifierList.add("_NOT_");
    if (isUpper) modifierList.add("_UPPER_");
    if (modifierList.isEmpty() && (pinned || frameName != null)) modifierList.add("_NONE_");

    boolean sectionRequired = !alwaysTrue || !isPrivate || isLeft || recoverWhile != null;
    boolean sectionRequiredSimple = sectionRequired && modifierList.isEmpty() && recoverWhile == null && frameName == null;
    boolean sectionMaybeDropped = sectionRequiredSimple && type == BNF_CHOICE && elementTypeRef == null &&
                                  children.stream().noneMatch(o -> ParserGeneratorUtil.isRollbackRequired(o, myFile));
    String modifiers = modifierList.isEmpty()? "_NONE_" : StringUtil.join(modifierList, " | ");
    if (sectionRequiredSimple) {
      if (!sectionMaybeDropped) {
        out("Marker %s = enter_section_(%s);", N.marker, N.builder);
      }
    }
    else if (sectionRequired) {
      boolean shortVersion = frameName == null && elementTypeRef == null;
      if (shortVersion) {
        out("Marker %s = enter_section_(%s, %s, %s);", N.marker, N.builder, N.level, modifiers);
      }
      else {
        out("Marker %s = enter_section_(%s, %s, %s, %s, %s);", N.marker, N.builder, N.level, modifiers, elementTypeRef, frameName);
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
            out("%s = %s;", N.result, tokenChoice.render(N));
            break;
          }
        }
        out("%s%s = %s;", i > 0 ? format("if (!%s) ", N.result) : "", N.result, nodeCall.render(N));
      }
      else if (type == BNF_SEQUENCE) {
        if (skip[0] == 0) {
          ConsumeType consumeType = getEffectiveConsumeType(rule, node, null);
          nodeCall = generateTokenSequenceCall(children, i, pinMatcher, pinApplied, skip, nodeCall, false, consumeType);
          if (i == 0) {
            out("%s = %s;", N.result, nodeCall.render(N));
          }
          else {
            if (pinApplied && G.generateExtendedPin) {
              if (i == childrenSize - 1) {
                // do not report error for last child
                if (i == p + 1) {
                  out("%s = %s && %s;", N.result, N.result, nodeCall.render(N));
                }
                else {
                  out("%s = %s && %s && %s;", N.result, N.pinned, nodeCall.render(N), N.result);
                }
              }
              else if (i == p + 1) {
                out("%s = %s && report_error_(%s, %s);", N.result, N.result, N.builder, nodeCall.render(N));
              }
              else {
                out("%s = %s && report_error_(%s, %s) && %s;", N.result, N.pinned, N.builder, nodeCall.render(N), N.result);
              }
            }
            else {
              out("%s = %s && %s;", N.result, N.result, nodeCall.render(N));
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
          out("%s = %s; // pin = %s", N.pinned, N.result, pinMatcher.pinValue);
        }
      }
      else if (type == BNF_OP_OPT) {
        out(nodeCall.render(N) + ";");
      }
      else if (type == BNF_OP_ONEMORE || type == BNF_OP_ZEROMORE) {
        if (type == BNF_OP_ONEMORE) {
          out("%s = %s;", N.result, nodeCall.render(N));
        }
        out("while (%s) {", alwaysTrue ? "true" : N.result);
        out("int %s = current_position_(%s);", N.pos, N.builder);
        out("if (!%s) break;", nodeCall.render(N));
        out("if (!empty_element_parsed_guard_(%s, \"%s\", %s)) break;", N.builder, funcName, N.pos);
        out("}");
      }
      else if (type == BNF_OP_AND) {
        out("%s = %s;", N.result, nodeCall.render(N));
      }
      else if (type == BNF_OP_NOT) {
        out("%s = !%s;", N.result, nodeCall.render(N));
      }
      else {
        addWarning(myFile.getProject(), "unexpected: " + type);
      }
    }

    if (sectionRequired) {
      String resultRef = alwaysTrue ? "true" : N.result;
      if (!hooks.isEmpty()) {
        for (Map.Entry<String, String> entry : hooks.entrySet()) {
          String hookName = ParserGeneratorUtil.toIdentifier(entry.getKey(), null, Case.UPPER);
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
            recoverCall = formatArgName(recoverWhile.substring(2, recoverWhile.length() - 2));
          }
          else {
            recoverCall = predicateRule == null ? null : generateWrappedNodeCall(rule, null, predicateRule.getName()).render();
          }
        }
        else {
          recoverCall = null;
        }
        out("exit_section_(%s, %s, %s, %s, %s, %s);", N.builder, N.level, N.marker, resultRef, pinnedRef, recoverCall);
      }
    }

    out("return %s;", alwaysTrue ? "true" : N.result + (pinned ? format(" || %s", N.pinned) : ""));
    out("}");
    generateNodeChildren(rule, funcName, children, visited);
  }

  /** @noinspection StringEquality*/
  private String generateAutoRecoverCall(BnfRule rule) {
    BnfFirstNextAnalyzer analyzer = new BnfFirstNextAnalyzer().setPredicateLookAhead(true);
    Set<BnfExpression> nextExprSet = analyzer.calcNext(rule).keySet();
    Set<String> nextSet = analyzer.asStrings(nextExprSet);
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
        addWarning(myFile.getProject(), rule.getName() + " #auto recovery generation failed: " + s);
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
    BnfFirstNextAnalyzer analyzer = new BnfFirstNextAnalyzer().setPredicateLookAhead(true);
    Set<BnfExpression> firstSet = analyzer.calcFirst(rule);
    ConsumeType ruleConsumeType = getRuleConsumeType(rule);
    Map<String, ConsumeType> firstElementTypes = new TreeMap<>();
    for (BnfExpression expression : firstSet) {
      if (expression == BNF_MATCHES_EOF || expression == BNF_MATCHES_ANY) return frameName;

      String expressionString = BnfFirstNextAnalyzer.asString(expression);
      if (myFile.getRule(expressionString) != null) continue;

      String t = firstToElementType(expressionString);
      if (t == null) return frameName;

      ConsumeType childConsumeType = getRuleConsumeType(ObjectUtils.notNull(Rule.of(expression)));
      ConsumeType consumeType = ConsumeType.min(ruleConsumeType, childConsumeType);
      ConsumeType existing = firstElementTypes.get(t);
      firstElementTypes.put(t, ConsumeType.max(existing, consumeType));
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
      }).collect(Collectors.joining(" &&\n  ", "if (", ") return false;"));
      out(condition);
    }

    return dropFrameName && StringUtil.isEmpty(getAttribute(rule, KnownAttribute.NAME)) ? null : frameName;
  }

  @NotNull
  private ConsumeType getRuleConsumeType(@NotNull BnfRule rule) {
    ConsumeType forcedConsumeType = ExpressionGeneratorHelper.fixForcedConsumeType(myExpressionHelper, rule, null, null);
    return ObjectUtils.chooseNotNull(forcedConsumeType, ConsumeType.forRule(rule));
  }

  void generateNodeChildren(BnfRule rule, String funcName, List<BnfExpression> children, Set<BnfExpression> visited) {
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

  @NotNull
  private List<String> collectExtraArguments(@NotNull BnfRule rule, @Nullable BnfExpression expression) {
    if (expression == null) return Collections.emptyList();
    return GrammarUtil.collectExtraArguments(rule, expression)
      .stream()
      .map(it -> formatArgName(it.substring(2, it.length() - 2)))
      .collect(toList());
  }

  private String formatArgName(String s) {
    String argName = s.trim();
    return N.argPrefix + (N.argPrefix.isEmpty() || "_".equals(N.argPrefix) ? argName : StringUtil.capitalize(argName));
  }

  @Nullable
  private String firstToElementType(String first) {
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

  @Nullable
  private String getTokenName(String value) {
    return mySimpleTokens.get(value);
  }

  @NotNull
  private NodeCall generateTokenSequenceCall(List<BnfExpression> children,
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

  @Nullable
  private NodeCall generateTokenChoiceCall(@NotNull List<BnfExpression> children,
                                           @NotNull ConsumeType consumeType,
                                           @NotNull String funcName) {
    final Collection<String> tokenNames = getTokenNames(myFile, children, 2);
    if (tokenNames == null) return null;

    final Set<String> tokens = ContainerUtil.newTreeSet();
    for (String tokenName : tokenNames) {
      if (!mySimpleTokens.containsKey(tokenName) && !mySimpleTokens.containsValue(tokenName)) {
        mySimpleTokens.put(tokenName, null);
      }
      tokens.add(getElementType(tokenName));
    }

    final String tokenSetName = getTokenSetConstantName(funcName);
    myTokenSets.put(tokenSetName, tokens);
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
        return new MetaParameterCall(formatArgName(expressions.get(0).getText()));
      }
      else {
        return generateExternalCall(rule, expressions, nextName);
      }
    }
    else {
      List<String> extraArguments = collectExtraArguments(rule, node);
      if (extraArguments.isEmpty()) {
        return new MethodCall(false, StringUtil.getShortName(ruleInfo(rule).parserClass), nextName);
      }
      else {
        return new MetaMethodCall(null, nextName, map(extraArguments, MetaParameterArgument::new));
      }
    }
  }

  @NotNull
  private ConsumeType getEffectiveConsumeType(@NotNull BnfRule rule, @Nullable BnfExpression node, @Nullable ConsumeType forcedConsumeType) {
    if (forcedConsumeType == ConsumeType.DEFAULT) return ConsumeType.DEFAULT;
    PsiElement parent = node == null ? null : node.getParent();

    if (forcedConsumeType == null && parent instanceof BnfSequence &&
        ContainerUtil.getFirstItem(((BnfSequence)parent).getExpressionList()) != node) {
      Set<BnfExpression> expressions = new BnfFirstNextAnalyzer()
        .setParentFilter(o -> o != parent)
        .calcFirst((BnfExpression)parent);
      if (expressions.size() != 1 || expressions.iterator().next() != node) {
        return ConsumeType.DEFAULT;
      }
    }
    ConsumeType fixed = ExpressionGeneratorHelper.fixForcedConsumeType(myExpressionHelper, rule, node, forcedConsumeType);
    return fixed != null ? fixed : ConsumeType.forRule(rule);
  }

  @NotNull
  private NodeCall generateExternalCall(@NotNull BnfRule rule, @NotNull List<BnfExpression> expressions, @NotNull String nextName) {
    List<BnfExpression> callParameters = expressions;
    List<BnfExpression> metaParameters = Collections.emptyList();
    List<String> metaParameterNames = Collections.emptyList();
    String method = expressions.size() > 0 ? expressions.get(0).getText() : null;
    String targetClassName = null;
    BnfRule targetRule = method == null ? null : myFile.getRule(method);
    // handle external rule call: substitute and merge arguments from external expression and rule definition
    if (targetRule != null) {
      if (Rule.isExternal(targetRule)) {
        metaParameterNames = GrammarUtil.collectExtraArguments(targetRule, targetRule.getExpression());
        callParameters = GrammarUtil.getExternalRuleExpressions(targetRule);
        metaParameters = expressions;
        method = callParameters.get(0).getText();
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
          nested = metaParameters.get(metaIdx + 1);
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
            arguments.add(new TextArgument(argument.startsWith("\'") ? GrammarUtil.unquote(argument) : argument));
          }
        }
        else if (nested instanceof BnfExternalExpression) {
          List<BnfExpression> expressionList = ((BnfExternalExpression)nested).getExpressionList();
          if (expressionList.size() == 1 && Rule.isMeta(rule)) {
            arguments.add(new MetaParameterArgument(formatArgName(expressionList.get(0).getText())));
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

  @NotNull
  private NodeArgument generateWrappedNodeCall(@NotNull BnfRule rule, @Nullable BnfExpression nested, @NotNull String nextName) {
    NodeCall nodeCall = generateNodeCall(rule, nested, nextName);
    if (nodeCall instanceof MetaMethodCall) {
      MetaMethodCall metaCall = (MetaMethodCall)nodeCall;
      MetaMethodCallArgument argument = new MetaMethodCallArgument(metaCall);
      if (metaCall.referencesMetaParameter()) {
        return argument;
      }
      else {
        return () -> getMetaMethodFieldRef(argument.render(), nextName);
      }
    }
    else if (nodeCall instanceof MethodCall && G.javaVersion == JavaVersion.JAVA_8) {
      MethodCall methodCall = (MethodCall)nodeCall;
      return () -> String.format("%s::%s", methodCall.getClassName(), methodCall.getMethodName());
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

  @NotNull
  private String getParserLambdaRef(@NotNull NodeCall nodeCall, @NotNull String nextName) {
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

  @NotNull
  private NodeCall generateConsumeToken(@NotNull ConsumeType consumeType, @NotNull String tokenName) {
    myTokensUsedInGrammar.add(tokenName);
    return new ConsumeTokenCall(consumeType, getElementType(tokenName));
  }

  @NotNull
  public static NodeCall generateConsumeTextToken(@NotNull ConsumeType consumeType, @NotNull String tokenText) {
    return new ConsumeTokenCall(consumeType, "\"" + tokenText + "\"");
  }

  private String getElementType(String token) {
    return ParserGeneratorUtil.getTokenType(myFile, token, G.generateTokenCase);
  }

  String getElementType(BnfRule r) {
    return ParserGeneratorUtil.getElementType(r, G.generateElementCase);
  }

  /*ElementTypes******************************************************************/

  private void generateElementTypesHolder(String className, Map<String, BnfRule> sortedCompositeTypes) {
    String tokenTypeClass = getRootAttribute(myFile, KnownAttribute.TOKEN_TYPE_CLASS);
    String tokenTypeFactory = getRootAttribute(myFile, KnownAttribute.TOKEN_TYPE_FACTORY);
    Set<String> imports = new LinkedHashSet<>();
    imports.add(IELEMENTTYPE_CLASS);
    if (G.generatePsi) {
      imports.add(PSI_ELEMENT_CLASS);
      imports.add(AST_NODE_CLASS);
    }
    if (G.generateTokenSets && !myTokenSets.isEmpty()) {
      imports.add(TOKEN_SET_CLASS);
    }
    boolean useExactElements = "all".equals(G.generateExactTypes) || G.generateExactTypes.contains("elements");
    boolean useExactTokens = "all".equals(G.generateExactTypes) || G.generateExactTypes.contains("tokens");

    Map<String, Trinity<String, String, RuleInfo>> compositeToClassAndFactoryMap = new THashMap<>();
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
    if (G.generatePsi) {
      imports.addAll(ContainerUtil.sorted(
        JBIterable.from(sortedCompositeTypes.values()).map(this::ruleInfo).map(o -> o.implPackage + ".*").toSet()));
      if (G.generatePsiClassesMap) {
        imports.add(CommonClassNames.JAVA_UTIL_COLLECTIONS);
        imports.add(CommonClassNames.JAVA_UTIL_SET);
        imports.add("java.util.LinkedHashMap");
      }
      if (G.generatePsiFactory) {
        if (JBIterable.from(myRuleInfos.values()).find(o -> o.mixedAST) != null) {
          imports.add(COMPOSITE_PSI_ELEMENT_CLASS);
        }
      }
    }
    generateClassHeader(className, imports, "", Java.INTERFACE);
    if (G.generateElementTypes) {
      for (String elementType : sortedCompositeTypes.keySet()) {
        String exactType = null;
        Trinity<String, String, RuleInfo> info = compositeToClassAndFactoryMap.get(elementType);
        String elementCreateCall;
        if (info.second == null) {
          elementCreateCall = "new " + (exactType = myShortener.fun(info.first));
        }
        else {
          elementCreateCall = myShortener.fun(StringUtil.getPackageName(info.second)) + "." + StringUtil.getShortName(info.second);
        }
        String fieldType = ObjectUtils.notNull(useExactElements ? exactType : "IElementType");
        String callFix = elementCreateCall.equals("new IElementType") ? ", null" : "";
        out(fieldType + " " + elementType + " = " + elementCreateCall + "(\"" + elementType + "\"" + callFix + ");");
      }
    }
    if (G.generateTokenTypes) {
      newLine();
      String exactType = null;
      Map<String, String> sortedTokens = new TreeMap<>();
      String tokenCreateCall;
      if (tokenTypeFactory == null) {
        tokenCreateCall = "new " + (exactType = myShortener.fun(tokenTypeClass));
      }
      else {
        tokenCreateCall = myShortener.fun(StringUtil.getPackageName(tokenTypeFactory)) + "." + StringUtil.getShortName(tokenTypeFactory);
      }
      String fieldType = ObjectUtils.notNull(useExactTokens ? exactType : null, "IElementType");
      for (String tokenText : mySimpleTokens.keySet()) {
        String tokenName = ObjectUtils.chooseNotNull(mySimpleTokens.get(tokenText), tokenText);
        if (isIgnoredWhitespaceToken(tokenName, tokenText)) continue;
        sortedTokens.put(getElementType(tokenName), isRegexpToken(tokenText) ? tokenName : tokenText);
      }
      for (String tokenType : sortedTokens.keySet()) {
        String callFix = tokenCreateCall.equals("new IElementType") ? ", null" : "";
        String tokenString = sortedTokens.get(tokenType);
        out(fieldType + " " + tokenType + " = " + tokenCreateCall + "(\"" + StringUtil.escapeStringCharacters(tokenString) + "\""+callFix+");");
      }
      generateTokenSets();
    }
    if (G.generatePsi && G.generatePsiClassesMap) {
      newLine();
      out("class Classes {");
      newLine();
      out("public static Class<?> findClass(IElementType elementType) {");
      out("return ourMap.get(elementType);");
      out("}");
      newLine();
      out("public static %s<IElementType> elementTypes() {", myShortener.fun(CommonClassNames.JAVA_UTIL_SET));
      out("return %s.unmodifiableSet(ourMap.keySet());", myShortener.fun(CommonClassNames.JAVA_UTIL_COLLECTIONS));
      out("}");
      newLine();
      String type = myShortener.fun("java.util.LinkedHashMap") + "<IElementType, Class<?>>";
      out("private static final %s ourMap = new %1$s();", type);
      newLine();
      out("static {");
      for (String elementType : sortedCompositeTypes.keySet()) {
        BnfRule rule = sortedCompositeTypes.get(elementType);
        RuleInfo info = ruleInfo(rule);
        if (info.isAbstract) continue;
        String psiClass = getRulePsiClassName(rule, myImplClassFormat);
        out("ourMap.put(" + elementType + ", " + psiClass + ".class);");
      }
      out("}");
      out("}");
    }
    if (G.generatePsi && G.generatePsiFactory) {
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
          out("public static %s createElement(%s node) {", myShortener.fun(PSI_ELEMENT_CLASS), myShortener.fun(AST_NODE_CLASS));
          out("IElementType type = node.getElementType();");
        }
        String psiClass = getRulePsiClassName(rule, myImplClassFormat);
        out((!first1 ? "else " : "") + "if (type == " + elementType + ") {");
        out("return new " + psiClass + "(node);");
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
          out("public static %s createElement(%s type) {", myShortener.fun(COMPOSITE_PSI_ELEMENT_CLASS), myShortener.fun(IELEMENTTYPE_CLASS));
        }
        String psiClass = getRulePsiClassName(rule, myImplClassFormat);
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
    if (myTokenSets.isEmpty()) {
      return;
    }
    newLine();
    out("interface %s {", TOKEN_SET_HOLDER_NAME);
    Map<String, String> reverseMap = new HashMap<>();
    myTokenSets.forEach((name, tokens) -> {
      String value = String.format("TokenSet.create(%s)", tokenSetString(tokens));
      String alreadyRendered = reverseMap.putIfAbsent(value, name);
      out("TokenSet %s = %s;", name, ObjectUtils.chooseNotNull(alreadyRendered, value));
    });
    out("}");
  }

  /*PSI******************************************************************/
  private void generatePsiIntf(BnfRule rule, RuleInfo info) {
    String psiClass = info.intfClass;
    String stubClass = info.stub;
    Collection<String> psiSupers = info.superInterfaces;
    if (StringUtil.isNotEmpty(stubClass)) {
      psiSupers = new LinkedHashSet<>(psiSupers);
      psiSupers.add(STUB_BASED_PSI_ELEMENT + "<" + stubClass + ">");
    }

    Set<String> imports = new LinkedHashSet<>();
    imports.addAll(Arrays.asList("java.util.List",
                                 "org.jetbrains.annotations.*",
                                 PSI_ELEMENT_CLASS));
    imports.addAll(psiSupers);
    imports.addAll(getRuleMethodTypesToImport(rule));

    generateClassHeader(psiClass, imports, "", Java.INTERFACE, ArrayUtil.toStringArray(psiSupers));
    generatePsiClassMethods(rule, info, true);
    out("}");
  }

  private void generatePsiImpl(BnfRule rule, RuleInfo info) {
    String psiClass = info.implClass;
    String superInterface = info.intfClass;
    String stubName = info.realStubClass;
    String implSuper = info.realSuperClass;

    Set<String> imports = new LinkedHashSet<>();
    imports.addAll(Arrays.asList(CommonClassNames.JAVA_UTIL_LIST,
                                 "org.jetbrains.annotations.*",
                                 AST_NODE_CLASS,
                                 PSI_ELEMENT_CLASS));
    if (myVisitorClassName != null) imports.add(PSI_ELEMENT_VISITOR_CLASS);
    imports.add(myPsiTreeUtilClass);
    imports.add(staticStarImport(myTypeHolderClass));
    if (StringUtil.isNotEmpty(implSuper)) imports.add(implSuper);
    imports.add(StringUtil.getPackageName(superInterface) + ".*");
    imports.add(StringUtil.notNullize(myVisitorClassName));
    imports.add(StringUtil.notNullize(myPsiImplUtilClass));
    imports.addAll(getRuleMethodTypesToImport(rule));

    Function<String, String> substitutor = stubName != null ? Functions.constant(stubName) : genericUnwrapper;

    List<NavigatablePsiElement> constructors = Collections.emptyList();
    BnfRule topSuperRule = null;
    for (BnfRule next = rule; next != null && next != topSuperRule; ) {
      topSuperRule = next;
      String superClass = ruleInfo(next).realSuperClass;
      if (superClass == null) continue;
      next = getEffectiveSuperRule(myFile, next);
      if (next != null && next != topSuperRule && getAttribute(topSuperRule, KnownAttribute.MIXIN) == null) continue;
      constructors = myJavaHelper.findClassMethods(getRawClassName(superClass), JavaHelper.MethodType.CONSTRUCTOR, "*", -1);
      if (!constructors.isEmpty()) break;
    }
    for (NavigatablePsiElement m : constructors) {
      collectMethodTypesToImport(Collections.singletonList(m), false, imports);
    }
    if (stubName != null && constructors.isEmpty()) imports.add(ISTUBELEMENTTYPE_CLASS);
    if (stubName != null) imports.add(stubName);

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

    Java javaType = info.isAbstract ? Java.ABSTRACT_CLASS : Java.CLASS;
    generateClassHeader(psiClass, imports, "", javaType, implSuper, superInterface);
    String shortName = StringUtil.getShortName(psiClass);
    if (constructors.isEmpty()) {
      out("public " + shortName + "(" + myShortener.fun(AST_NODE_CLASS) + " node) {");
      out("super(node);");
      out("}");
      newLine();
      if (stubName != null) {
        out("public " + shortName + "(" + myShortener.fun(stubName) + " stub, " + myShortener.fun(ISTUBELEMENTTYPE_CLASS) + " stubType) {");
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
      String shortened = myShortener.fun(myVisitorClassName);
      String r = G.visitorValue != null ? "<" + G.visitorValue + ">" : "";
      String t = G.visitorValue != null ? " " + G.visitorValue : "void";
      String ret = G.visitorValue != null ? "return " : "";
      out("public " + r + t + " accept(@NotNull " + shortened + r + " visitor) {");
      out(ret + "visitor.visit" + getRulePsiClassName(rule, null) + "(this);");
      out("}");
      newLine();
      //if (topSuperRule == rule) {
        out("public void accept(@NotNull " + myShortener.fun(PSI_ELEMENT_VISITOR_CLASS) + " visitor) {");
        out("if (visitor instanceof " + shortened + ") accept((" + shortened + ")visitor);");
        out("else super.accept(visitor);");
        out("}");
        newLine();
      //}
    }
    generatePsiClassMethods(rule, info, false);
    out("}");
  }

  private void generatePsiClassMethods(BnfRule rule, RuleInfo info, boolean intf) {
    Set<String> visited = ContainerUtil.newTreeSet();
    boolean mixedAST = info.mixedAST;
    for (RuleMethodsHelper.MethodInfo methodInfo : myRulesMethodsHelper.getFor(rule)) {
      if (StringUtil.isEmpty(methodInfo.name)) continue;
      switch (methodInfo.type) {
        case RULE:
        case TOKEN:
          generatePsiAccessor(rule, methodInfo, intf, mixedAST);
          break;
        case USER:
          generateUserPsiAccessors(rule, methodInfo, intf, mixedAST);
          break;
        case MIXIN:
          boolean found = false;
          if (intf) {
            String mixinClass = getAttribute(rule, KnownAttribute.MIXIN);
            List<NavigatablePsiElement> methods = myJavaHelper.findClassMethods(mixinClass, JavaHelper.MethodType.INSTANCE, methodInfo.name, -1);
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
            String ruleClassName = myShortener.fun(info.intfClass);
            String implClassName = StringUtil.getShortName(String.valueOf(myPsiImplUtilClass));
            out("" +
                "//WARNING: %s(...) is skipped\n" +
                "//matching %s(%s, ...)\n" +
                "//methods are not found in %s",
                methodInfo.name, methodInfo.name, ruleClassName, implClassName);
            newLine();
            addWarning(myFile.getProject(), "%s.%s(%s, ...) method not found", implClassName, methodInfo.name, ruleClassName);
          }
          break;
        default: throw new AssertionError(methodInfo.toString());
      }
    }
  }

  public Collection<String> getRuleMethodTypesToImport(BnfRule rule) {
    Set<String> result = ContainerUtil.newTreeSet();

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

      List<NavigatablePsiElement> mixinMethods = myJavaHelper.findClassMethods(mixinClass, JavaHelper.MethodType.INSTANCE, methodInfo.name, -1);
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
        for (String superType : generic.getExtendsList()) {
          addTypeToImports(superType, emptyList(), result);
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

  private static void addTypeToImports(@Nullable String type,
                                       @NotNull List<String> typeAnnotations,
                                       @NotNull Collection<String> result) {
    if (type != null && type.contains(".")) {
      result.add(type);
    }
    for (String anno : typeAnnotations) {
      if (!anno.startsWith("kotlin.")) {
        result.add(anno);
      }
    }
  }


  private void generatePsiAccessor(BnfRule rule, RuleMethodsHelper.MethodInfo methodInfo, boolean intf, boolean mixedAST) {
    RuleGraphHelper.Cardinality type = methodInfo.cardinality;
    boolean isToken = methodInfo.rule == null;

    boolean many = type.many();

    String getterName = methodInfo.generateGetterName();
    if (!intf) out("@Override");
    if (type == REQUIRED) {
      out("@NotNull");
    }
    else if (type == OPTIONAL) {
      out("@Nullable");
    }
    else {
      out("@NotNull");
    }
    String className = myShortener.fun(isToken ? PSI_ELEMENT_CLASS : getAccessorType(methodInfo.rule));
    String tail = intf ? "();" : "() {";
    out((intf ? "" : "public ") + (many ? myShortener.fun(CommonClassNames.JAVA_UTIL_LIST) + "<" : "") + className + (many ? "> " : " ") + getterName + tail);
    if (!intf) {
      out("return " + generatePsiAccessorImplCall(rule, methodInfo, mixedAST) + ";");
      out("}");
    }
    newLine();
  }

  private String generatePsiAccessorImplCall(@NotNull BnfRule rule, @NotNull RuleMethodsHelper.MethodInfo methodInfo, boolean mixedAST) {
    boolean isToken = methodInfo.rule == null;

    RuleGraphHelper.Cardinality type = methodInfo.cardinality;
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
        String className = myShortener.fun(getAccessorType(methodInfo.rule));
        return many ? String.format("%s.getChildrenOfTypeAsList(this, %s.class)", myShortener.fun(myPsiTreeUtilClass), className) :
               (type == REQUIRED ? "findNotNullChildByClass" : "findChildByClass") + "(" + className + ".class)";
      }
    }
    // new logic
    if (isToken) {
      String getterName = mixedAST ? "findPsiChildByType" : "findChildByType";
      result = getterName + "(" + getElementType(methodInfo.path) + ")";
    }
    else {
      String className = myShortener.fun(getAccessorType(methodInfo.rule));
      String getterName = stubbed && many ? "getStubChildrenOfTypeAsList" :
                          stubbed ? "getStubChildOfType" :
                          many ? "getChildrenOfTypeAsList" : "getChildOfType";
      result = String.format("%s.%s(this, %s.class)", myShortener.fun(myPsiTreeUtilClass), getterName, className);
    }
    return required && !mixedAST ? "notNullChild(" + result + ")" : result;
  }

  @NotNull
  private String getAccessorType(@NotNull BnfRule rule) {
    if (Rule.isExternal(rule)) {
      Pair<String, String> first = ContainerUtil.getFirstItem(getAttribute(rule, KnownAttribute.IMPLEMENTS));
      return ObjectUtils.assertNotNull(first).second;
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
      RuleMethodsHelper.MethodInfo targetInfo = myRulesMethodsHelper.getMethodInfo(targetRule[0], item);
      targetRule[0] = targetInfo == null ? null : targetInfo.rule;
      return targetInfo;
    });
  }

  private void generateUserPsiAccessors(BnfRule startRule, RuleMethodsHelper.MethodInfo methodInfo, boolean intf, boolean mixedAST) {
    StringBuilder sb = new StringBuilder();
    BnfRule targetRule = startRule;
    RuleGraphHelper.Cardinality cardinality = REQUIRED;
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
      String index = indexEnd > -1 ? pathElement.substring(indexStart + 1, indexEnd).trim() : null;
      if ("first".equals(index)) index = "0";

      RuleMethodsHelper.MethodInfo targetInfo = (RuleMethodsHelper.MethodInfo)m;
      if (targetInfo == null ||
          index != null && !targetInfo.cardinality.many() ||
          i > 0 && StringUtil.isEmpty(targetInfo.name) && targetInfo.rule == null) {
        if (intf) { // warn only once
          addWarning(startRule.getProject(), "incorrect item '" + pathElement + "' in '" + startRule.getName() + "' method " +
                                             methodInfo.name + "=\"" + methodInfo.path + "\"");
        }
        return; // missing rule, unknown or wrong cardinality
      }

      boolean many = targetInfo.cardinality.many();
      String className = myShortener.fun(targetInfo.rule == null ? PSI_ELEMENT_CLASS : getAccessorType(targetInfo.rule));

      String type = (many ? myShortener.fun(CommonClassNames.JAVA_UTIL_LIST) + "<" : "") + className + (many ? "> " : " ");
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

    if (!intf) out("@Override");

    if (!cardinality.many() && cardinality == REQUIRED && !totalNullable) {
      out("@NotNull");
    }
    else {
      out("@Nullable");
    }

    boolean many = cardinality.many();
    String className = myShortener.fun(targetRule == null ? PSI_ELEMENT_CLASS : getAccessorType(targetRule));
    String getterName = getGetterName(methodInfo.name);
    String tail = intf ? "();" : "() {";
    out((intf ? "" : "public ") + (many ? myShortener.fun(CommonClassNames.JAVA_UTIL_LIST) + "<" : "") + className + (many ? "> " : " ") + getterName + tail);

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
    String returnType = methodTypes.isEmpty()? "void" : myShortener.fun(methodTypes.get(0));
    int offset = methodTypes.isEmpty() || isInPsiUtil && methodTypes.size() < 3 ? 0 :
                 isInPsiUtil ? 3 : 1;
    if (!visited.add(methodName + methodTypes.subList(offset, methodTypes.size()))) return;
    if (intf && methodTypes.size() == offset && "toString".equals(methodName)) return;

    List<JavaHelper.TypeParameterInfo> genericParameters = myJavaHelper.getGenericParameters(method);
    List<String> exceptionList = myJavaHelper.getExceptionList(method);

    if (!intf) out("@" + myShortener.fun("java.lang.Override"));
    for (String s : myJavaHelper.getAnnotations(method)) {
      if ("java.lang.Override".equals(s)) continue;
      if (s.startsWith("kotlin.")) continue;
      out("@" + myShortener.fun(s));
    }
    Function<Integer, List<String>> annoProvider = i -> myJavaHelper.getParameterAnnotations(method, (i - 1) / 2);
    out("%s%s%s %s(%s)%s%s",
        intf ? "" : "public ",
        getGenericClauseString(genericParameters, myShortener),
        returnType,
        methodName,
        getParametersString(methodTypes, offset, 3, genericUnwrapper, annoProvider, myShortener),
        getThrowsString(exceptionList, myShortener),
        intf ? ";" : " {");
    if (!intf) {
      String implUtilRef = myShortener.fun(StringUtil.notNullize(myPsiImplUtilClass, KnownAttribute.PSI_IMPL_UTIL_CLASS.getName()));
      String string = getParametersString(methodTypes, offset, 2, genericUnwrapper, annoProvider, myShortener);
      out("%s%s.%s(this%s);", "void".equals(returnType) ? "" : "return ", implUtilRef, methodName,
          string.isEmpty() ? "" : ", " + string);
      out("}");
    }
    newLine();
  }
}
