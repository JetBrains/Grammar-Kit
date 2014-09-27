/*
 * Copyright 2011-2014 Gregory Shrago
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
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality.*;
import static org.intellij.grammar.psi.BnfTypes.*;


/**
 * @author gregory
 *         Date 16.07.11 10:41
 */
public class ParserGenerator {
  public static final Logger LOG = Logger.getInstance("ParserGenerator");
  private static final String TYPE_TEXT_SEPARATORS = "<>,[]";

  private final Map<String, String> myRuleParserClasses = ContainerUtil.newTreeMap();
  private final Map<String, String> myParserLambdas = ContainerUtil.newTreeMap();
  private final Set<String> myPackageClasses = ContainerUtil.newTreeSet();
  private final Map<String, String> mySimpleTokens;
  private final BnfFile myFile;
  private final String mySourcePath;
  private final String myOutputPath;
  private final String myGrammarRoot;
  private final String myGrammarRootParser;
  private final String myRuleClassPrefix;
  private final String myParserUtilClass;
  private final boolean generateExtendedPin;
  private final String visitorClassName;
  private final int generateFirstCheck;

  private int myOffset;
  private PrintWriter myOut;
  private Function<String, String> myShortener;

  private final RuleGraphHelper myGraphHelper;
  private final ExpressionHelper myExpressionHelper;
  private final RuleMethodsHelper myRulesMethodsHelper;
  private final KnownAttribute.ListValue myUnknownRootAttributes;

  public ParserGenerator(BnfFile tree, String sourcePath, String outputPath) {
    myFile = tree;
    mySourcePath = sourcePath;
    myOutputPath = outputPath;
    final List<BnfRule> rules = tree.getRules();
    myGrammarRoot = rules.isEmpty() ? null : rules.get(0).getName();
    for (BnfRule r : rules) {
      myRuleParserClasses.put(r.getName(), getAttribute(r, KnownAttribute.PARSER_CLASS));
    }
    myGrammarRootParser = myGrammarRoot == null? null : myRuleParserClasses.get(myGrammarRoot);
    generateExtendedPin = getRootAttribute(myFile, KnownAttribute.EXTENDED_PIN);
    generateFirstCheck = getRootAttribute(myFile, KnownAttribute.GENERATE_FIRST_CHECK);
    myRuleClassPrefix = getPsiClassPrefix(myFile);
    myParserUtilClass = ObjectUtils.chooseNotNull(getRootAttribute(myFile, KnownAttribute.PARSER_UTIL_CLASS.alias("stubParserClass")),
                                                  getRootAttribute(myFile, KnownAttribute.PARSER_UTIL_CLASS));
    String tmpVisitorClass = getRootAttribute(myFile, KnownAttribute.PSI_VISITOR_NAME);
    visitorClassName = StringUtil.isEmpty(tmpVisitorClass) ?
                       null : tmpVisitorClass.startsWith(myRuleClassPrefix) ?
                              tmpVisitorClass : myRuleClassPrefix + tmpVisitorClass;
    mySimpleTokens = RuleGraphHelper.getTokenMap(myFile);
    myUnknownRootAttributes = ParserGeneratorUtil.collectUnknownAttributes(myFile);
    myGraphHelper = RuleGraphHelper.getCached(myFile);
    myExpressionHelper = new ExpressionHelper(myFile, myGraphHelper, true);
    myRulesMethodsHelper = new RuleMethodsHelper(myGraphHelper, myExpressionHelper, mySimpleTokens);
  }

  private void openOutput(String className) throws IOException {
    File file = new File(myOutputPath, className.replace('.', File.separatorChar) + ".java");
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

  public void out(String s) {
    int length = s.length();
    if (length == 0) {
      myOut.println();
      return;
    }
    boolean isComment = s.startsWith("//");
    boolean newStatement = true;
    for (int start = 0, end; start < length; start = end + 1) {
      end = StringUtil.indexOf(s, '\n', start, length);
      if (end == -1) end = length;
      String substring = s.substring(start, end);
      if (!isComment && substring.startsWith("}")) myOffset--;
      if (myOffset > 0) {
        myOut.print(StringUtil.repeat("  ", newStatement ? myOffset : myOffset + 1));
      }
      if (!isComment && substring.endsWith("{")) myOffset++;
      myOut.println(substring);
      newStatement = substring.endsWith(";") || substring.endsWith("{") || substring.endsWith("}");
    }
  }

  public void newLine() {
    out("");
  }

  public void generate() throws IOException {
    {
      generateParser();
    }
    boolean generatePsi = getRootAttribute(myFile, KnownAttribute.GENERATE_PSI);
    Map<String, BnfRule> sortedCompositeTypes = new TreeMap<String, BnfRule>();
    Map<String, BnfRule> sortedPsiRules = new TreeMap<String, BnfRule>();
    for (BnfRule rule : myFile.getRules()) {
      if (!RuleGraphHelper.shouldGeneratePsi(rule, true)) continue;
      String elementType = getElementType(rule);
      if (StringUtil.isEmpty(elementType)) continue;
      if (sortedCompositeTypes.containsKey(elementType)) continue;
      if (!Rule.isFake(rule)) {
        sortedCompositeTypes.put(elementType, rule);
      }
      sortedPsiRules.put(rule.getName(), rule);
    }
    if (myGrammarRoot != null) {
      String className = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);
      openOutput(className);
      try {
        generateElementTypesHolder(className, sortedCompositeTypes, generatePsi);
      }
      finally {
        closeOutput();
      }
    }
    if (generatePsi) {
      checkClassAvailability(myFile.getProject(), getRootAttribute(myFile, KnownAttribute.PSI_IMPL_UTIL_CLASS),
                             "PSI method signatures will not be detected");

      myRulesMethodsHelper.buildMaps(sortedPsiRules.values());
      for (BnfRule r : sortedPsiRules.values()) {
        myPackageClasses.add(getRulePsiClassName(r, myRuleClassPrefix));
      }
      Map<String, String> infClasses = new HashMap<String, String>();
      String psiPackage = getPsiPackage(myFile);
      String suffix = getPsiImplSuffix(myFile);     
      String psiImplPackage = getPsiImplPackage(myFile);
      
      for (String ruleName : sortedPsiRules.keySet()) {
        BnfRule rule = myFile.getRule(ruleName);
        String psiClass = psiPackage + "." + getRulePsiClassName(rule, myRuleClassPrefix);
        String psiImplClass = psiImplPackage + "." + getRulePsiClassName(rule, myRuleClassPrefix) + suffix;
        
        infClasses.put(ruleName, psiClass);
        openOutput(psiClass);
        try {
          generatePsiIntf(rule, psiClass, getSuperInterfaceNames(rule, psiPackage), psiImplClass);
        }
        finally {
          closeOutput();
        }
      }
      for (String ruleName : sortedPsiRules.keySet()) {
        BnfRule rule = myFile.getRule(ruleName);
        String psiImplClass = psiImplPackage + "." + getRulePsiClassName(rule, myRuleClassPrefix) + suffix;
        openOutput(psiImplClass);
        try {
          generatePsiImpl(rule, psiImplClass, infClasses.get(ruleName), getSuperClassName(rule, psiImplPackage, suffix));
        }
        finally {
          closeOutput();
        }
      }
      if (visitorClassName != null && myGrammarRoot != null) {
        String psiClass = psiPackage + "." + visitorClassName;
        openOutput(psiClass);
        try {
          generateVisitor(psiClass, sortedPsiRules);
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
    List<String> imports = ContainerUtil.newArrayList("org.jetbrains.annotations.*", BnfConstants.PSI_ELEMENT_VISITOR_CLASS, superIntf);
    for (BnfRule rule : sortedRules.values()) {
      imports.addAll(getSuperInterfaceNames(rule, StringUtil.getPackageName(psiClass)));
    }
    generateClassHeader(psiClass, imports, "", false, BnfConstants.PSI_ELEMENT_VISITOR_CLASS);
    Set<String> visited = new HashSet<String>();
    Set<String> all = new TreeSet<String>();
    for (BnfRule rule : sortedRules.values()) {
      String methodName = getRulePsiClassName(rule, "");
      visited.add(methodName);
      out("public void visit" + methodName + "(@NotNull " + getRulePsiClassName(rule, myRuleClassPrefix) + " o) {");
      boolean first = true;
      for (String top : getSuperInterfaceNames(rule, "")) {
        if (!first && top.equals(superIntf)) continue;
        else if (first) all.add(top);
        if (top.startsWith(myRuleClassPrefix)) {
          top = top.substring(myRuleClassPrefix.length());
        }
        String text = "visit" + top + "(o);";
        if (first) {
          out(text);
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
    for (String top : ContainerUtil.concat(all, Arrays.asList(shortSuperIntf))) {
      String methodName = top.startsWith(myRuleClassPrefix) ? top.substring(myRuleClassPrefix.length()) : top;
      if (visited.contains(methodName)) continue;
      out("public void visit" + methodName + "(@NotNull " + myShortener.fun(top) + " o) {");
      if (!methodName.equals(top) && !top.equals(shortSuperIntf)) {
        out("visit" + (shortSuperIntf.startsWith(myRuleClassPrefix) ? shortSuperIntf.substring(myRuleClassPrefix.length()) : shortSuperIntf) + "(o);");
      }
      else {
        out("visitElement(o);");
      }
      out("}");
      newLine();
    }

    out("}");
  }


  public void generateParser() throws IOException {
    for (String className : new TreeSet<String>(myRuleParserClasses.values())) {
      Map<String, BnfRule> map = new TreeMap<String, BnfRule>();
      for (String ruleName : myRuleParserClasses.keySet()) {
        if (className.equals(myRuleParserClasses.get(ruleName))) {
          map.put(ruleName, myFile.getRule(ruleName));
        }
      }
      openOutput(className);
      try {
        generateParser(className, map.keySet());
      }
      finally {
        closeOutput();
      }
    }
  }

  @NotNull
  private String getSuperClassName(BnfRule rule, String psiPackage, String suffix) {
    BnfRule topSuper = getTopSuperRule(rule);
    return topSuper == null ? getRootAttribute(myFile, KnownAttribute.EXTENDS) :
           topSuper == rule ? getAttribute(rule, KnownAttribute.EXTENDS) :
           psiPackage + "." + getRulePsiClassName(topSuper, myRuleClassPrefix) + suffix;
  }

  private BnfRule getTopSuperRule(BnfRule rule) {
    THashSet<BnfRule> visited = new THashSet<BnfRule>();
    BnfRule cur = rule, next = rule;
    for (; next != null && cur != null; cur = !visited.add(next) ? null : next) {
      next = RuleGraphHelper.getSynonymTargetOrSelf(cur);
      if (next != cur) continue;
      if (cur != rule) break; // do not search for elementType any further
      String attr = getAttribute(cur, KnownAttribute.EXTENDS);
      //noinspection StringEquality
      next = attr != KnownAttribute.EXTENDS.getDefaultValue() ? myFile.getRule(attr) : null;
      if (next == null && attr != null) break;
    }
    return cur;
  }

  @NotNull
  private List<String> getSuperInterfaceNames(BnfRule rule, String psiPackage) {
    List<String> strings = new ArrayList<String>();
    List<String> topRuleImplements = Collections.emptyList();
    String topRuleClass = null;
    BnfRule topSuper = getTopSuperRule(rule);
    boolean simpleMode = psiPackage.isEmpty();
    if (topSuper != null && topSuper != rule) {
      topRuleImplements = getAttribute(topSuper, KnownAttribute.IMPLEMENTS).asStrings();
      topRuleClass = StringUtil.nullize((simpleMode ? "" : psiPackage + ".") + getRulePsiClassName(topSuper, myRuleClassPrefix));
      if (!StringUtil.isEmpty(topRuleClass)) strings.add(topRuleClass);
    }
    List<String> rootImplements = getRootAttribute(myFile, KnownAttribute.IMPLEMENTS).asStrings();
    List<String> ruleImplements = getAttribute(rule, KnownAttribute.IMPLEMENTS).asStrings();
    for (String className : ruleImplements) {
      BnfRule superIntfRule = myFile.getRule(className);
      if (superIntfRule != null) {
        strings.add((simpleMode ? "" : psiPackage + ".") + getRulePsiClassName(superIntfRule, myRuleClassPrefix));
      }
      else if (!topRuleImplements.contains(className) && (topRuleClass == null || !rootImplements.contains(className))) {
        String name = simpleMode ? StringUtil.getShortName(className) : className;
        if (className != null && strings.size() == 1 && topSuper == null) {
          strings.add(0, name);
        }
        else {
          strings.add(name);
        }
      }
    }
    return strings;
  }

  public void generateParser(String parserClass, final Set<String> ownRuleNames) {
    String elementTypeHolderClass = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);
    List<String> parserImports = getRootAttribute(myFile, KnownAttribute.PARSER_IMPORTS).asStrings();
    boolean rootParser = parserClass.equals(myGrammarRootParser);
    Set<String> imports = new LinkedHashSet<String>();
    imports.addAll(Arrays.asList("com.intellij.lang.PsiBuilder",
                                 "com.intellij.lang.PsiBuilder.Marker",
                                 "static " + elementTypeHolderClass + ".*",
                                 "static " + myParserUtilClass + ".*"));
    if (!rootParser) {
      imports.add("static " + myGrammarRootParser + ".*");
    }
    else {
      imports.addAll(Arrays.asList(BnfConstants.IELEMENTTYPE_CLASS,
                                   "com.intellij.lang.ASTNode",
                                   "com.intellij.psi.tree.TokenSet",
                                   "com.intellij.lang.PsiParser"));
    }
    imports.addAll(parserImports);

    generateClassHeader(parserClass, imports,
                        "@SuppressWarnings({\"SimplifiableIfStatement\", \"UnusedAssignment\"})",
                        false, "", rootParser ? "PsiParser" : "");

    if (rootParser) {
      generateRootParserContent(ownRuleNames);
    }
    for (String ruleName : ownRuleNames) {
      BnfRule rule = myFile.getRule(ruleName);
      if (Rule.isExternal(rule) || Rule.isFake(rule)) continue;
      if (myExpressionHelper.getExpressionInfo(rule) != null) continue;
      out("/* ********************************************************** */");
      generateNode(rule, rule.getExpression(), ruleName, new THashSet<BnfExpression>());
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
    Map<String, String> reversedLambdas = new THashMap<String, String>();
    for (Map.Entry<String, String> e : myParserLambdas.entrySet()) {
      String body = e.getValue();
      if (body.startsWith("#")) {
        String name = e.getKey();
        String value = reversedLambdas.get(body);
        if (value == null) {
          value = wrapCallWithParserInstance(body.substring(1));
          reversedLambdas.put(body, name);
        }
        out("final static Parser " + name + " = " + value + ";");
        e.setValue(StringUtil.getShortName(parserClass) + "." + name);
      }
    }
    out("}");
  }

  private void generateRootParserContent(Set<String> ownRuleNames) {
    boolean generateExtendsSets = !myGraphHelper.getRuleExtendsMap().isEmpty();
    out("public ASTNode parse(IElementType root_, PsiBuilder builder_) {");
    out("parse_only_(root_, builder_);");
    out("return builder_.getTreeBuilt();");
    out("}");
    newLine();
    out("public void parse_only_(IElementType root_, PsiBuilder builder_) {");
    out("boolean result_;");
    out("builder_ = adapt_builder_(root_, builder_, this, " + (generateExtendsSets ? "EXTENDS_SETS_" : null) + ");");
    out("Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);");
    boolean first = true;
    for (String ruleName : ownRuleNames) {
      BnfRule rule = myFile.getRule(ruleName);
      if (!RuleGraphHelper.shouldGeneratePsi(rule, false) || Rule.isMeta(rule)) continue;
      if (Rule.isFake(rule)) continue;
      String elementType = getElementType(rule);
      out((first ? "" : "else ") + "if (root_ == " + elementType + ") {");
      String nodeCall = generateNodeCall(rule, null, ruleName);
      out("result_ = " + nodeCall.replace("level_ + 1", "0") + ";");
      out("}");
      if (first) first = false;
    }
    {
      if (!first) out("else {");
      out("result_ = parse_root_(root_, builder_, 0);");
      if (!first) out("}");
    }
    out("exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);");
    out("}");
    newLine();
    {
      BnfRule rootRule = myFile.getRule(myGrammarRoot);
      String nodeCall = generateNodeCall(rootRule, null, myGrammarRoot);
      out("protected boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {");
      out("return " + nodeCall + ";");
      out("}");
      newLine();
    }
    if (generateExtendsSets) {
      out("public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {");
      StringBuilder sb = new StringBuilder();
      Set<String> elementTypes = new TreeSet<String>();
      for (String ruleName : myRuleParserClasses.keySet()) {
        BnfRule baseRule = myFile.getRule(ruleName);
        if (Rule.isFake(baseRule)) continue;
        Collection<BnfRule> rules = myGraphHelper.getRuleExtendsMap().get(baseRule);
        if (rules.isEmpty()) continue;
        for (BnfRule rule : rules) {
          if (Rule.isFake(rule)) continue;
          elementTypes.add(getElementType(rule));
        }
        int i = 0;
        for (String elementType : elementTypes) {
          if (StringUtil.isEmpty(elementType)) continue;
          if (i > 0) sb.append(i % 4 == 0 ? ",\n" : ", ");
          sb.append(elementType);
          i++;
        }
        out("create_token_set_(" + sb.toString() + "),");
        sb.setLength(0);
        elementTypes.clear();
      }
      out("};");
      newLine();
    }
  }

  private void generateClassHeader(String className,
                                   Collection<String> imports,
                                   String annos,
                                   boolean intf,
                                   String... supers) {
    generateFileHeader(className);
    String packageName = StringUtil.getPackageName(className);
    String shortClassName = StringUtil.getShortName(className);
    out("package " + packageName + ";");
    newLine();
    final Set<String> realImports = new HashSet<String>();
    realImports.add(packageName + ".*");
    Function<String, String> shortener = new Function<String, String>() {
      @Override
      public String fun(String s) {
        boolean changed = false;
        StringBuilder sb = new StringBuilder();
        for (String part : StringUtil.tokenize(new StringTokenizer(s, TYPE_TEXT_SEPARATORS, true))) {
          String pkg;
          part = StringUtil.trimStart(StringUtil.trimStart(part, "? super "), "? extends ");
          if (TYPE_TEXT_SEPARATORS.contains(part)) {
            sb.append(part);
            if (part.equals(",")) sb.append(" ");
          }
          else if (realImports.contains(part) ||
                   "java.lang".equals(pkg = StringUtil.getPackageName(part)) ||
                   realImports.contains(pkg + ".*")) {
            sb.append(StringUtil.getShortName(part));
            changed = true;
          }
          else {
            sb.append(part);
          }
        }
        return changed ? sb.toString() : s;
      }
    };
    for (String item : imports) {
      for (String s : StringUtil.tokenize(item.replaceAll("\\s+", " "), TYPE_TEXT_SEPARATORS)) {
        s = StringUtil.trimStart(StringUtil.trimStart(s, "? super "), "? extends ");
        if (!s.contains(".") || !s.equals(shortener.fun(s))) continue;
        if (myPackageClasses.contains(StringUtil.getShortName(s))) continue;
        realImports.add(s);
        out("import " + s + ";");
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
      else if (!intf && i == 1) {
        sb.append(" implements ").append(shortener.fun(aSuper));
      }
      else {
        sb.append(", ").append(shortener.fun(aSuper));
      }
    }
    if (StringUtil.isNotEmpty(annos)) {
      out(annos);
    }
    out("public " + (intf ? "interface " : "class ") + shortClassName + sb.toString() + " {");
    newLine();
    myShortener = shortener;
  }

  private void generateFileHeader(String className) {
    final String classHeader = getStringOrFile(getRootAttribute(myFile, KnownAttribute.CLASS_HEADER, className));
    out(classHeader);
    myOffset = 0;
  }

  private String getStringOrFile(String classHeader) {
    File file = new File(mySourcePath, classHeader);
    try {
      return file.exists() ? FileUtil.loadFile(file) : classHeader;
    }
    catch (IOException ex) {
      LOG.error(ex);
    }
    return classHeader;
  }

  void generateNode(BnfRule rule, BnfExpression initialNode, String funcName, Set<BnfExpression> visited) {
    boolean isRule = initialNode.getParent() == rule;
    BnfExpression node = ParserGeneratorUtil.getNonTrivialNode(initialNode);

    IElementType type = getEffectiveType(node);

    for (String s : StringUtil.split((StringUtil.isEmpty(node.getText()) ? initialNode : node).getText(), "\n")) {
      out("// " + s);
    }
    boolean firstNonTrivial = node == Rule.firstNotTrivial(rule);
    boolean isPrivate = !(isRule || firstNonTrivial) || Rule.isPrivate(rule) || myGrammarRoot.equals(rule.getName());
    boolean isLeft = firstNonTrivial && Rule.isLeft(rule);
    boolean isLeftInner = isLeft && (isPrivate || Rule.isInner(rule));
    final String recoverWhile = firstNonTrivial ? ObjectUtils.chooseNotNull(
      getAttribute(rule, KnownAttribute.RECOVER_WHILE.alias("recoverUntil")),
      getAttribute(rule, KnownAttribute.RECOVER_WHILE)) : null;

    final boolean canCollapse = !isPrivate && (!isLeft || isLeftInner) && firstNonTrivial && canCollapse(rule);

    String elementType = getElementType(rule);

    final List<BnfExpression> children;
    out((!isRule ? "private " : isPrivate ? "" : "public ") + "static boolean " + funcName + "(PsiBuilder builder_, int level_"
        + collectExtraArguments(rule, node, true) + ") {");
    if (node instanceof BnfReferenceOrToken || node instanceof BnfLiteralExpression || node instanceof BnfExternalExpression) {
      children = Collections.singletonList(node);
      if (isPrivate && !isLeftInner && recoverWhile == null) {
        String nodeCall = generateNodeCall(rule, node, getNextName(funcName, 0));
        out("return " + nodeCall + ";");
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
    else {
      children = getChildExpressions(node);
      if (children.isEmpty() && recoverWhile == null) {
        if (isPrivate || StringUtil.isEmpty(elementType)) {
          out("return true;");
        }
        else {
          // todo handle left rules
          out("builder_.mark().done(" + elementType + ");");
          out("return true;");
        }
        out("}");
        return;
      }
    }
    out("if (!recursion_guard_(builder_, level_, \"" + funcName + "\")) return false;");

    String frameName = firstNonTrivial && !Rule.isMeta(rule)? quote(getRuleDisplayName(rule, !isPrivate)) : null;
    if (recoverWhile == null && (isRule || firstNonTrivial)) {
      frameName = generateFirstCheck(rule, frameName, true);
    }

    PinMatcher pinMatcher = new PinMatcher(rule, type, firstNonTrivial ? rule.getName() : funcName);
    boolean pinApplied = false;
    final boolean alwaysTrue = type == BNF_OP_OPT || type == BNF_OP_ZEROMORE;
    if (!alwaysTrue) {
      boolean value = type == BNF_OP_ZEROMORE || type == BNF_OP_OPT || children.isEmpty();
      out("boolean result_" + (children.isEmpty() || type == BNF_OP_ZEROMORE ? " = " + value : "") + ";");
    }
    boolean pinned = pinMatcher.active() && pinMatcher.shouldGenerate(children);
    if (pinned) {
      out("boolean pinned_;");
    }
    List<String> modifierList = ContainerUtil.newSmartList();
    if (canCollapse) modifierList.add("_COLLAPSE_");
    if (isLeftInner) modifierList.add("_LEFT_INNER_");
    else if (isLeft) modifierList.add("_LEFT_");
    if (type == BNF_OP_AND) modifierList.add("_AND_");
    else if (type == BNF_OP_NOT) modifierList.add("_NOT_");
    if (modifierList.isEmpty() && (pinned || frameName != null)) modifierList.add("_NONE_");
    boolean sectionRequired = !alwaysTrue || !isPrivate || isLeft || recoverWhile != null;
    boolean sectionRequiredSimple = sectionRequired && modifierList.isEmpty() && recoverWhile == null;
    String modifiers = modifierList.isEmpty()? "_NONE_" : StringUtil.join(modifierList, " | ");
    if (sectionRequiredSimple) {
      out("Marker marker_ = enter_section_(builder_);");
    }
    else if (sectionRequired) {
      out("Marker marker_ = enter_section_(builder_, level_, "+modifiers+", "+frameName+");");
    }

    ConsumeType consumeType = ConsumeType.forRule(rule);
    boolean predicateEncountered = false;
    int[] skip = {0};
    for (int i = 0, p = 0, childrenSize = children.size(); i < childrenSize; i++) {
      BnfExpression child = children.get(i);

      String nodeCall = generateNodeCall(rule, child, getNextName(funcName, i));
      if (type == BNF_CHOICE) {
        out((i > 0 ? "if (!result_) " : "") + "result_ = " + nodeCall + ";");
      }
      else if (type == BNF_SEQUENCE) {
        predicateEncountered |= pinApplied && ParserGeneratorUtil.getEffectiveExpression(myFile, child) instanceof BnfPredicate;
        if (skip[0] == 0) {
          nodeCall = generateTokenSequenceCall(children, i, pinMatcher, pinApplied, skip, nodeCall, false, consumeType);
          if (i == 0) {
            out("result_ = " + nodeCall + ";");
          }
          else {
            if (pinApplied && generateExtendedPin && !predicateEncountered) {
              if (i == childrenSize - 1) {
                // do not report error for last child
                if (i == p + 1) {
                  out("result_ = result_ && " + nodeCall + ";");
                }
                else {
                  out("result_ = pinned_ && " + nodeCall + " && result_;");
                }
              }
              else if (i == p + 1) {
                out("result_ = result_ && report_error_(builder_, " + nodeCall + ");");
              }
              else {
                out("result_ = pinned_ && report_error_(builder_, " + nodeCall + ") && result_;");
              }
            }
            else {
              out("result_ = result_ && " + nodeCall + ";");
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
          out("pinned_ = result_; // pin = " + pinMatcher.pinValue);
        }
      }
      else if (type == BNF_OP_OPT) {
        out(nodeCall + ";");
      }
      else if (type == BNF_OP_ONEMORE || type == BNF_OP_ZEROMORE) {
        if (type == BNF_OP_ONEMORE) {
          out("result_ = " + nodeCall + ";");
        }
        out("int pos_ = current_position_(builder_);");
        out("while (" + (alwaysTrue ? "true" : "result_") + ") {");
        out("if (!" + nodeCall + ") break;");
        out("if (!empty_element_parsed_guard_(builder_, \"" + funcName + "\", pos_)) break;");
        out("pos_ = current_position_(builder_);");
        out("}");
      }
      else if (type == BNF_OP_AND) {
        out("result_ = " + nodeCall + ";");
      }
      else if (type == BNF_OP_NOT) {
        out("result_ = !" + nodeCall + ";");
      }
      else {
        addWarning(myFile.getProject(), "unexpected: " + type);
      }
    }

    if (sectionRequired) {
      String elementTypeRef = !isPrivate && StringUtil.isNotEmpty(elementType)? elementType : "null";
      String resultRef = alwaysTrue ? "true" : "result_";
      if (sectionRequiredSimple) {
        out("exit_section_(builder_, marker_, " + elementTypeRef+", " + resultRef + ");");
      }
      else {
        String pinnedRef = pinned ? "pinned_" : "false";
        String recoverCall;
        if (recoverWhile != null) {
          BnfRule predicateRule = myFile.getRule(recoverWhile);
          if (BnfConstants.RECOVER_AUTO.equals(recoverWhile)) {
            recoverCall = generateAutoRecoverCall(rule);
          }
          else {
            recoverCall = predicateRule == null ? null : generateWrappedNodeCall(rule, null, predicateRule.getName());
          }
        }
        else {
          recoverCall = null;
        }
        out("exit_section_(builder_, level_, marker_, " + elementTypeRef +", " + resultRef + ", "+pinnedRef +", " + recoverCall + ");");
      }
    }

    out("return " + (alwaysTrue ? "true" : "result_" + (pinned ? " || pinned_" : "")) + ";");
    out("}");
    generateNodeChildren(rule, funcName, children, visited);
  }

  /** @noinspection StringEquality*/
  private String generateAutoRecoverCall(BnfRule rule) {
    BnfFirstNextAnalyzer analyzer = new BnfFirstNextAnalyzer();
    Set<BnfExpression> nextExprSet = analyzer.calcNext(rule).keySet();
    Set<String> nextSet = analyzer.asStrings(nextExprSet);
    List<String> tokenTypes = new ArrayList<String>(nextSet.size());

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
    StringBuilder sb = new StringBuilder("!nextTokenIsFast(builder_, ");

    appendTokenTypes(sb, tokenTypes);
    sb.append(")");

    String constantName = rule.getName() + "_auto_recover_";
    myParserLambdas.put(constantName, "#" + sb.toString());
    return constantName;
  }

  public String generateFirstCheck(BnfRule rule, String frameName, boolean skipIfOne) {
    if (generateFirstCheck <= 0) return frameName;
    BnfFirstNextAnalyzer analyzer = new BnfFirstNextAnalyzer();
    Set<String> firstSet = analyzer.asStrings(analyzer.calcFirst(rule));
    List<String> firstElementTypes = new ArrayList<String>(firstSet.size());
    for (String s : firstSet) {
      if (myFile.getRule(s) != null) continue; // ignore left recursion
      @SuppressWarnings("StringEquality")
      boolean unknown = s == BnfFirstNextAnalyzer.MATCHES_EOF || s == BnfFirstNextAnalyzer.MATCHES_ANY;
      String t = unknown? null : firstToElementType(s);
      if (t != null) {
        firstElementTypes.add(t);
      }
      else {
        firstElementTypes.clear();
        break;
      }
    }
    ConsumeType forcedConsumeType = ExpressionGeneratorHelper.fixForcedConsumeType(myExpressionHelper, rule, null, null);
    ConsumeType consumeType = ObjectUtils.chooseNotNull(forcedConsumeType, ConsumeType.forRule(rule));
    boolean fast = consumeType == ConsumeType.FAST || consumeType == ConsumeType.SMART;
    // do not include frameName if FIRST is known and its size is 1
    boolean dropFrameName = skipIfOne && !firstElementTypes.isEmpty() && firstElementTypes.size() == 1;
    if (!firstElementTypes.isEmpty() && firstElementTypes.size() <= generateFirstCheck) {
      StringBuilder sb = new StringBuilder("if (!");
      sb.append(fast ? "nextTokenIsFast" : "nextTokenIs").append("(builder_, ");
      if (!fast && !dropFrameName) sb.append(frameName != null ? frameName : "\"\"").append(", ");

      appendTokenTypes(sb, firstElementTypes);
      sb.append(")) return false;");
      out(sb.toString());
    }
    return dropFrameName? null : frameName;
  }

  private boolean canCollapse(BnfRule rule) {
    Map<PsiElement, RuleGraphHelper.Cardinality> map = myGraphHelper.getFor(rule);
    for (PsiElement element : map.keySet()) {
      if (element instanceof LeafPsiElement) continue;
      RuleGraphHelper.Cardinality c = map.get(element);
      if (c.optional()) continue;
      if (!(element instanceof BnfRule)) return false;
      if (!myGraphHelper.collapseEachOther(rule, (BnfRule)element)) return false;
    }
    return myGraphHelper.getRuleExtendsMap().containsScalarValue(rule);
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
      for (int j = 1, expressionsSize = expressions.size(); j < expressionsSize; j++) {
        BnfExpression expression = expressions.get(j);
        if (expression instanceof BnfLiteralExpression || expression instanceof BnfReferenceOrToken) continue;
        if (expression instanceof BnfExternalExpression) {
          generateNodeChildren(rule, getNextName(funcName, j - 1), Collections.singletonList(expression), visited);
        }
        else {
          newLine();
          generateNode(rule, expression, getNextName(getNextName(funcName, index), j - 1), visited);
        }
      }
    }
    else if (GrammarUtil.isAtomicExpression(child) || ParserGeneratorUtil.isTokenSequence(rule, child)) {
      // do not generate
    }
    else {
      newLine();
      generateNode(rule, child, getNextName(funcName, index), visited);
    }
  }

  private static String collectExtraArguments(BnfRule rule, @Nullable BnfExpression expression, final boolean declaration) {
    if (expression == null) return "";
    List<String> params = GrammarUtil.collectExtraArguments(rule, expression);
    if (params.isEmpty()) return "";
    final StringBuilder sb = new StringBuilder();
    for (String param : params) {
      sb.append(", ").append(declaration ? "final Parser " : "").append(param.substring(2, param.length() - 2));
    }
    return sb.toString();
  }

  @Nullable
  private String firstToElementType(String first) {
    if (first.startsWith("#") || first.startsWith("-") || first.startsWith("<<")) return null;
    String value = StringUtil.stripQuotesAroundValue(first);
    //noinspection StringEquality
    if (first != value) {
      String attributeName = getTokenName(value);
      if (attributeName != null && !first.startsWith("\"")) {
        return getTokenElementType(attributeName);
      }
      return null;
    }
    return getTokenElementType(first);
  }

  @Nullable
  private String getTokenName(String value) {
    String existing = mySimpleTokens.get(value);
    if (existing != null || !myUnknownRootAttributes.isEmpty()) {
      for (Pair<String, String> p : myUnknownRootAttributes) {
        if (Comparing.equal(value, p.second)) {
          String attrName = p.first;
          mySimpleTokens.put(value, attrName);
          return attrName;
        }
      }
    }
    return existing;
  }

  private String generateTokenSequenceCall(List<BnfExpression> children,
                                           int startIndex,
                                           PinMatcher pinMatcher,
                                           boolean pinApplied,
                                           int[] skip,
                                           String nodeCall,
                                           boolean rollbackOnFail,
                                           ConsumeType consumeType) {
    if (startIndex == children.size() - 1 || !isConsumeTokenCall(nodeCall)) return nodeCall;
    List<String> list = ContainerUtil.newArrayList();
    int pin = pinApplied ? -1 : 0;
    for (int i = startIndex, len = children.size(); i < len; i++) {
      BnfExpression child = children.get(i);
      IElementType type = child.getNode().getElementType();
      String text = child.getText();
      String tokenName;
      if (type == BNF_STRING && text.charAt(0) != '\"') {
        tokenName = getTokenName(StringUtil.stripQuotesAroundValue(text));
      }
      else if (type == BNF_REFERENCE_OR_TOKEN && myFile.getRule(text) == null) {
        tokenName = text;
      }
      else {
        break;
      }
      list.add(getTokenElementType(tokenName));
      if (!pinApplied && pinMatcher.matches(i, child)) {
        pin = i - startIndex + 1;
      }
    }
    if (list.size() < 2) return nodeCall;
    skip[0] = list.size() - 1;
    return ((rollbackOnFail ? "parseTokens" : "consumeTokens") + (consumeType == ConsumeType.SMART ? consumeType.getMethodSuffix() : "")) +
           "(builder_, " + pin + ", " + StringUtil.join(list, ", ") + ")";
  }

  private static boolean isConsumeTokenCall(String nodeCall) {
    int idx = nodeCall.indexOf('(');
    return idx > 0 && ConsumeType.forMethod(nodeCall.substring(0, idx)) != null;
  }

  String generateNodeCall(BnfRule rule, @Nullable BnfExpression node, String nextName) {
    return generateNodeCall(rule, node, nextName, null);
  }

  String generateNodeCall(BnfRule rule, @Nullable BnfExpression node, String nextName, @Nullable ConsumeType forcedConsumeType) {
    IElementType type = node == null ? BNF_REFERENCE_OR_TOKEN : getEffectiveType(node);
    String text = node == null ? nextName : node.getText();
    PsiElement parent = node == null? null : node.getParent();

    boolean forceDefaultConsumeType = forcedConsumeType == ConsumeType.DEFAULT ||
                                      forcedConsumeType == null && parent instanceof BnfSequence && parent.getFirstChild() != node;
    ConsumeType consumeType =
      forceDefaultConsumeType ? ConsumeType.DEFAULT :
      ObjectUtils.chooseNotNull(ExpressionGeneratorHelper.fixForcedConsumeType(myExpressionHelper, rule, node, forcedConsumeType), ConsumeType.forRule(rule));
    String consumeMethodName = KnownAttribute.CONSUME_TOKEN_METHOD.getDefaultValue() + consumeType.getMethodSuffix();

    if (type == BNF_STRING) {
      String value = StringUtil.stripQuotesAroundValue(text);
      String attributeName = getTokenName(value);
      if (attributeName != null) {
        return generateConsumeToken(attributeName, consumeMethodName);
      }
      return generateConsumeTextToken(text.startsWith("\"") ? value : StringUtil.escapeStringCharacters(value), consumeMethodName);
    }
    else if (type == BNF_NUMBER) {
      return generateConsumeTextToken(text, consumeMethodName);
    }
    else if (type == BNF_REFERENCE_OR_TOKEN) {
      BnfRule subRule = myFile.getRule(text);
      if (subRule != null) {
        String method;
        if (Rule.isExternal(subRule)) {
          StringBuilder clause = new StringBuilder();
          method = generateExternalCall(rule, clause, GrammarUtil.getExternalRuleExpressions(subRule), nextName);
          return method + "(builder_, level_ + 1" + clause.toString() + ")";
        }
        else {
          ExpressionHelper.ExpressionInfo info = ExpressionGeneratorHelper.getInfoForExpressionParsing(myExpressionHelper, subRule);
          method = info != null ? info.rootRule.getName() : subRule.getName();
          String parserClass = myRuleParserClasses.get(method);
          if (!parserClass.equals(myGrammarRootParser) && !parserClass.equals(myRuleParserClasses.get(rule.getName()))) {
            method = StringUtil.getShortName(parserClass) + "." + method;
          }
          if (info == null) {
            return method + "(builder_, level_ + 1)";
          }
          else {
            return method + "(builder_, level_ + 1, "+ (info.getPriority(subRule) - 1) + ")";
          }
        }
      }
      // allow token usage by registered token name instead of token text
      if (!mySimpleTokens.containsKey(text) && !mySimpleTokens.values().contains(text)) {
        mySimpleTokens.put(text, null);
      }
      return generateConsumeToken(text, consumeMethodName);
    }
    else if (isTokenSequence(rule, node)) {
      PinMatcher pinMatcher = new PinMatcher(rule, type, nextName);
      List<BnfExpression> childExpressions = getChildExpressions(node);
      PsiElement firstElement = ContainerUtil.getFirstItem(childExpressions);
      String nodeCall = generateNodeCall(rule, (BnfExpression) firstElement, getNextName(nextName, 0), consumeType);
      for (PsiElement psiElement : childExpressions) {
        String t = psiElement.getText();
        if (!mySimpleTokens.containsKey(t) && !mySimpleTokens.values().contains(t)) {
          mySimpleTokens.put(t, null);
        } 
      }
      return generateTokenSequenceCall(childExpressions, 0, pinMatcher, false, new int[]{0}, nodeCall, true, consumeType);
    }
    else if (type == BNF_EXTERNAL_EXPRESSION) {
      List<BnfExpression> expressions = ((BnfExternalExpression)node).getExpressionList();
      if (expressions.size() == 1 && Rule.isMeta(rule)) {
        return expressions.get(0).getText() + ".parse(builder_, level_)";
      }
      else {
        StringBuilder clause = new StringBuilder();
        String method = generateExternalCall(rule, clause, expressions, nextName);
        return method + "(builder_, level_ + 1" + clause.toString() + ")";
      }
    }
    else {
      return nextName + "(builder_, level_ + 1" + collectExtraArguments(rule, node, false) + ")";
    }
  }

  private String generateExternalCall(BnfRule rule, StringBuilder clause, List<BnfExpression> expressions, String nextName) {
    List<BnfExpression> callParameters = expressions;
    List<BnfExpression> metaParameters = Collections.emptyList();
    List<String> metaParameterNames = Collections.emptyList();
    String method = expressions.size() > 0 ? expressions.get(0).getText() : null;
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
        String parserClass = myRuleParserClasses.get(method);
        if (!parserClass.equals(myGrammarRootParser) && !parserClass.equals(myRuleParserClasses.get(rule.getName()))) {
          method = StringUtil.getShortName(parserClass) + "." + method;
        }
      }
    }
    if (callParameters.size() > 1) {
      for (int i = 1, len = callParameters.size(); i < len; i++) {
        clause.append(", ");
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
            clause.append(generateWrappedNodeCall(rule, nested, argument));
          }
          else {
            clause.append(argument);
          }
        }
        else if (nested instanceof BnfLiteralExpression) {
          String attributeName = getTokenName(StringUtil.unquoteString(argument));
          if (attributeName != null) {
            clause.append(generateWrappedNodeCall(rule, nested, attributeName));
          }
          else if (argument.startsWith("\'")) {
            clause.append(StringUtil.unquoteString(argument));
          }
          else {
            clause.append(argument);
          }
        }
        else if (nested instanceof BnfExternalExpression) {
          List<BnfExpression> expressionList = ((BnfExternalExpression)nested).getExpressionList();
          boolean metaRule = Rule.isMeta(rule);
          if (metaRule && expressionList.size() == 1) {
            // parameter
            clause.append(expressionList.get(0).getText());
          }
          else {
            clause.append(generateWrappedNodeCall(rule, nested, argNextName));
          }
        }
        else {
          clause.append(generateWrappedNodeCall(rule, nested, argNextName));
        }
      }
    }
    return method;
  }

  private String generateWrappedNodeCall(BnfRule rule, @Nullable BnfExpression nested, final String nextName) {
    if (!collectExtraArguments(rule, nested, false).isEmpty()) {
      return wrapCallWithParserInstance(generateNodeCall(rule, nested, nextName));
    }
    String constantName = nextName + "_parser_";
    String current = myParserLambdas.get(constantName);
    if (current == null) {
      myParserLambdas.put(constantName, "#" + generateNodeCall(rule, nested, nextName));
      return constantName;
    }
    else if (current.startsWith("#")) {
      return constantName;
    }
    else {
      return current;
    }
  }

  private String generateConsumeToken(String tokenName, String consumeMethodName) {
    return consumeMethodName + "(builder_, " + getTokenElementType(tokenName) + ")";
  }

  public static String generateConsumeTextToken(String tokenText, String consumeMethodName) {
    return consumeMethodName + "(builder_, \"" + tokenText + "\")";
  }

  private String getTokenElementType(String token) {
    return getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_PREFIX) + token.toUpperCase();
  }

  /*ElementTypes******************************************************************/

  private void generateElementTypesHolder(String className, Map<String, BnfRule> sortedCompositeTypes, boolean generatePsi) {
    String implPackage = getPsiImplPackage(myFile);
    boolean generateTokens = getRootAttribute(myFile, KnownAttribute.GENERATE_TOKENS);
    String tokenTypeClass = getRootAttribute(myFile, KnownAttribute.TOKEN_TYPE_CLASS);
    String tokenTypeFactory = getRootAttribute(myFile, KnownAttribute.TOKEN_TYPE_FACTORY);
    Set<String> imports = new LinkedHashSet<String>();
    imports.add(BnfConstants.IELEMENTTYPE_CLASS);
    if (generatePsi) {
      imports.add(BnfConstants.PSI_ELEMENT_CLASS);
      imports.add(BnfConstants.AST_NODE_CLASS);
    }
    Map<String, Pair<String, String>> compositeToClassAndFactoryMap = new THashMap<String, Pair<String, String>>();
    for (String elementType : sortedCompositeTypes.keySet()) {
      BnfRule rule = sortedCompositeTypes.get(elementType);
      String elementTypeClass = getAttribute(rule, KnownAttribute.ELEMENT_TYPE_CLASS);
      String elementTypeFactory = getAttribute(rule, KnownAttribute.ELEMENT_TYPE_FACTORY);
      compositeToClassAndFactoryMap.put(elementType, Pair.create(elementTypeClass, elementTypeFactory));
      if (elementTypeFactory != null) {
        imports.add(StringUtil.getPackageName(elementTypeFactory));
      }
      else {
        ContainerUtil.addIfNotNull(elementTypeClass, imports);
      }
    }
    if (tokenTypeFactory != null) {
      imports.add(StringUtil.getPackageName(tokenTypeFactory));
    }
    else {
      ContainerUtil.addIfNotNull(tokenTypeClass, imports);
    }
    if (generatePsi) imports.add(implPackage + ".*");
    generateClassHeader(className, imports, "", true);
    for (String elementType : sortedCompositeTypes.keySet()) {
      Pair<String, String> pair = compositeToClassAndFactoryMap.get(elementType);
      String elementCreateCall;
      if (pair.second == null) {
        elementCreateCall = "new " + StringUtil.getShortName(pair.first);
      } else {
        elementCreateCall = myShortener.fun(StringUtil.getPackageName(pair.second)) + "." + StringUtil.getShortName(pair.second);
      }
      String callFix = elementCreateCall.equals("new IElementType")? ", null" : "";
      out("IElementType " + elementType + " = " + elementCreateCall + "(\"" + elementType + "\""+callFix+");");
    }
    if (generateTokens) {
      newLine();
      Map<String, String> sortedTokens = ContainerUtil.newTreeMap();
      String tokenCreateCall;
      if (tokenTypeFactory == null) {
        tokenCreateCall = "new " + StringUtil.getShortName(tokenTypeClass);
      }
      else {
        tokenCreateCall = myShortener.fun(StringUtil.getPackageName(tokenTypeFactory)) + "." + StringUtil.getShortName(tokenTypeFactory);
      }
      for (String tokenText : mySimpleTokens.keySet()) {
        String tokenName = ObjectUtils.chooseNotNull(mySimpleTokens.get(tokenText), tokenText);
        sortedTokens.put(getTokenElementType(tokenName), ParserGeneratorUtil.isRegexpToken(tokenText) ? tokenName : tokenText);
      }
      for (String tokenType : sortedTokens.keySet()) {
        String callFix = tokenCreateCall.equals("new IElementType") ? ", null" : "";
        String tokenString = sortedTokens.get(tokenType);
        out("IElementType " + tokenType + " = " + tokenCreateCall + "(\"" + StringUtil.escapeStringCharacters(tokenString) + "\""+callFix+");");
      }
    }
    if (generatePsi) {
      newLine();
      out("class Factory {");
      out("public static " + myShortener.fun(BnfConstants.PSI_ELEMENT_CLASS) + " createElement(" + myShortener.fun(BnfConstants.AST_NODE_CLASS) + " node) {");
      out("IElementType type = node.getElementType();");
      String suffix = getPsiImplSuffix(myFile);
      boolean first = true;
      for (String elementType : sortedCompositeTypes.keySet()) {
        final BnfRule rule = sortedCompositeTypes.get(elementType);
        String psiClass = getRulePsiClassName(rule, myRuleClassPrefix) + suffix;
        out((!first ? "else" : "") + " if (type == " + elementType + ") {");
        out("return new " + psiClass + "(node);");
        first = false;
        out("}");
      }
      out("throw new AssertionError(\"Unknown element type: \" + type);");

      out("}");
      out("}");
    }
    out("}");
  }


  /*PSI******************************************************************/
  private void generatePsiIntf(BnfRule rule,
                               String psiClass,
                               Collection<String> psiSupers,
                               String psiImplClass) {
    String psiImplUtilClass = getRootAttribute(myFile, KnownAttribute.PSI_IMPL_UTIL_CLASS);
    String stubClass = getAttribute(rule, KnownAttribute.STUB_CLASS);
    if (StringUtil.isNotEmpty(stubClass)) {
      psiSupers = new LinkedHashSet<String>(psiSupers);
      psiSupers.add(BnfConstants.STUB_BASED_PSI_ELEMENT + "<" + stubClass + ">");
    }

    Set<String> imports = ContainerUtil.newLinkedHashSet();
    imports.addAll(Arrays.asList("java.util.List",
                                 "org.jetbrains.annotations.*",
                                 BnfConstants.PSI_ELEMENT_CLASS));
    imports.addAll(psiSupers);
    JavaHelper javaHelper = JavaHelper.getJavaHelper(myFile.getProject());
    imports.addAll(getRuleMethodTypesToImport(rule, psiImplUtilClass, javaHelper));

    generateClassHeader(psiClass, imports, "", true, ArrayUtil.toStringArray(psiSupers));
    generatePsiClassMethods(rule, psiImplClass, psiImplUtilClass, javaHelper, true);
    out("}");
  }

  private void generatePsiImpl(BnfRule rule,
                               String psiClass,
                               String superInterface,
                               String superRuleClass) {
    String typeHolderClass = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);
    String psiImplUtilClass = getRootAttribute(myFile, KnownAttribute.PSI_IMPL_UTIL_CLASS);
    String stubClass = getAttribute(rule, KnownAttribute.STUB_CLASS);
    if (StringUtil.isNotEmpty(stubClass)) {
      boolean extendsBasic = StringUtil.equals(superRuleClass, getRootAttribute(myFile, KnownAttribute.EXTENDS));
      String stubBasedPsiElement = BnfConstants.STUB_BASED_PSI_ELEMENT_BASE;
      String extendsClass = getAttribute(rule, KnownAttribute.EXTENDS);
      superRuleClass = StringUtil.isNotEmpty(extendsClass) && !extendsBasic
                       ? extendsClass.replaceAll("\\?", stubClass)
                       : stubBasedPsiElement + "<" + stubClass + ">";
    }
    // mixin attribute overrides "extends":
    String implSuper = StringUtil.notNullize(getAttribute(rule, KnownAttribute.MIXIN), superRuleClass);

    Set<String> imports = ContainerUtil.newLinkedHashSet();
    imports.addAll(Arrays.asList(CommonClassNames.JAVA_UTIL_LIST,
                                 "org.jetbrains.annotations.*",
                                 BnfConstants.AST_NODE_CLASS,
                                 BnfConstants.PSI_ELEMENT_CLASS));
    if (visitorClassName != null) imports.add(BnfConstants.PSI_ELEMENT_VISITOR_CLASS);
    imports.add(BnfConstants.PSI_TREE_UTIL_CLASS);
    imports.add("static " + typeHolderClass + ".*");
    if (StringUtil.isNotEmpty(implSuper)) imports.add(implSuper);
    imports.add(StringUtil.getPackageName(superInterface) + ".*");
    if (StringUtil.isNotEmpty(psiImplUtilClass)) imports.add(psiImplUtilClass);
    JavaHelper javaHelper = JavaHelper.getJavaHelper(myFile.getProject());
    imports.addAll(getRuleMethodTypesToImport(rule, psiImplUtilClass, javaHelper));

    String stubName = StringUtil.isNotEmpty(stubClass)? stubClass :
                      implSuper.indexOf("<") < implSuper.indexOf(">") &&
                      javaHelper.findClassMethod(implSuper.substring(0, implSuper.indexOf("<")), "StubBasedPsiElementBase", 2) != null
                      ? implSuper.substring(implSuper.indexOf("<") + 1, implSuper.indexOf(">")) : null;
    if (stubName != null) imports.add(BnfConstants.ISTUBELEMENTTYPE_CLASS);

    generateClassHeader(psiClass, imports, "", false, implSuper, superInterface);
    String shortName = StringUtil.getShortName(psiClass);
    out("public " + shortName + "(" + myShortener.fun(BnfConstants.AST_NODE_CLASS) + " node) {");
    out("super(node);");
    out("}");
    newLine();
    if (stubName != null) {
      out("public " + shortName + "(" + myShortener.fun(stubName) + " stub, " + myShortener.fun(BnfConstants.ISTUBELEMENTTYPE_CLASS) + " nodeType) {");
      out("super(stub, nodeType);");
      out("}");
      newLine();
    }
    if (visitorClassName != null) {
      out("public void accept(@NotNull " + myShortener.fun(BnfConstants.PSI_ELEMENT_VISITOR_CLASS) + " visitor) {");
      out("if (visitor instanceof " + visitorClassName + ") ((" + visitorClassName + ")visitor).visit" + getRulePsiClassName(rule, "") + "(this);");
      out("else super.accept(visitor);");
      out("}");
      newLine();
    }
    generatePsiClassMethods(rule, psiClass, psiImplUtilClass, javaHelper, false);
    out("}");
  }

  private void generatePsiClassMethods(BnfRule rule,
                                       String psiImplClass,
                                       String psiImplUtilClass,
                                       JavaHelper javaHelper,
                                       boolean intf) {
    for (RuleMethodsHelper.MethodInfo methodInfo : myRulesMethodsHelper.getFor(rule)) {
      if (StringUtil.isEmpty(methodInfo.name)) continue;
      switch (methodInfo.type) {
        case 1:
        case 2:
          generatePsiAccessor(methodInfo, intf);
          break;
        case 3:
          generateUserPsiAccessors(rule, methodInfo, intf);
          break;
        case 4:
          generateUtilMethod(methodInfo.name, psiImplUtilClass, intf, psiImplClass, javaHelper);
          break;
        default: throw new AssertionError(methodInfo.toString());
      }
    }
  }

  public Collection<String> getRuleMethodTypesToImport(BnfRule rule, String psiImplUtilClass, JavaHelper javaHelper) {
    Set<String> result = ContainerUtil.newTreeSet();

    Collection<RuleMethodsHelper.MethodInfo> methods = myRulesMethodsHelper.getFor(rule);
    for (RuleMethodsHelper.MethodInfo methodInfo : methods) {
      if (methodInfo.rule != null) {
        result.add(getAccessorType(methodInfo.rule));
      }
    }

    for (RuleMethodsHelper.MethodInfo methodInfo : methods) {
      if (methodInfo.type != 4) continue;

      NavigatablePsiElement method = psiImplUtilClass == null ? null : javaHelper.findClassMethod(psiImplUtilClass, methodInfo.name, -1);
      if (method != null) {
        for (String s : javaHelper.getMethodTypes(method)) {
          if (s.contains(".")) result.add(s);
        }
        for (String s : javaHelper.getAnnotations(method)) {
          result.add(s);
        }
      }
    }
    return result;
  }


  private void generatePsiAccessor(RuleMethodsHelper.MethodInfo methodInfo, boolean intf) {
    RuleGraphHelper.Cardinality type = methodInfo.cardinality;
    boolean isToken = methodInfo.rule == null;

    boolean many = type.many();

    String getterNameBody = "get" + toIdentifier(methodInfo.name, "");
    String getterName = getterNameBody + (many ? "List" : "");
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
    String className = myShortener.fun(isToken ? BnfConstants.PSI_ELEMENT_CLASS : getAccessorType(methodInfo.rule));
    String tail = intf ? "();" : "() {";
    out((intf ? "" : "public ") + (many ? myShortener.fun(CommonClassNames.JAVA_UTIL_LIST) + "<" : "") + className + (many ? "> " : " ") + getterName + tail);
    if (!intf) {
      out("return " + generatePsiAccessorImplCall(methodInfo) + ";");
      out("}");
    }
    newLine();
  }

  private String generatePsiAccessorImplCall(@NotNull RuleMethodsHelper.MethodInfo methodInfo) {
    boolean isToken = methodInfo.rule == null;

    RuleGraphHelper.Cardinality type = methodInfo.cardinality;
    boolean many = type.many();

    if (isToken) {
      return (type == REQUIRED ? "findNotNullChildByType" : "findChildByType") + "(" + getTokenElementType(methodInfo.path) + ")";
    }
    else {
      String className = myShortener.fun(getAccessorType(methodInfo.rule));
      if (many) {
        return "PsiTreeUtil.getChildrenOfTypeAsList(this, " + className + ".class)";
      }
      else {
        return (type == REQUIRED ? "findNotNullChildByClass" : "findChildByClass") + "(" + className + ".class)";
      }
    }
  }

  private String getAccessorType(@NotNull BnfRule rule) {
    if (ParserGeneratorUtil.Rule.isExternal(rule)) {
      Pair<String, String> first = ContainerUtil.getFirstItem(getAttribute(rule, KnownAttribute.IMPLEMENTS));
      return ObjectUtils.assertNotNull(first).second;
    }
    else {
      return getRulePsiClassName(rule, myRuleClassPrefix);
    }
  }

  private void generateUserPsiAccessors(BnfRule startRule,
                                        RuleMethodsHelper.MethodInfo methodInfo,
                                        boolean intf) {
    StringBuilder sb = new StringBuilder();
    BnfRule targetRule = startRule;
    RuleGraphHelper.Cardinality cardinality = REQUIRED;
    String context = "";
    String[] splittedPath = methodInfo.path.split("/");
    boolean totalNullable = false;
    for (int i = 0, count = 1; i < splittedPath.length; i++) {
      String pathElement = splittedPath[i];
      boolean last = i == splittedPath.length - 1;
      int indexStart = pathElement.indexOf('[');
      int indexEnd = indexStart > 0 ? pathElement.lastIndexOf(']') : -1;

      String item = indexEnd > -1 ? pathElement.substring(0, indexStart).trim() : pathElement.trim();
      String index = indexEnd > -1 ? pathElement.substring(indexStart + 1, indexEnd).trim() : null;
      if ("first".equals(index)) index = "0";

      if (item.isEmpty()) continue;
      RuleMethodsHelper.MethodInfo targetInfo = myRulesMethodsHelper.getMethodInfo(targetRule, item);
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
      String className = myShortener.fun(targetInfo.rule == null ? BnfConstants.PSI_ELEMENT_CLASS : getAccessorType(targetInfo.rule));

      String type = (many ? myShortener.fun(CommonClassNames.JAVA_UTIL_LIST) + "<" : "") + className + (many ? "> " : " ");
      String curId = "p" + (count++);
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
        targetCall = "get" + toIdentifier(item, "") + (targetInfo.cardinality.many() ? "List" : "") + "()";
      }
      else {
        targetCall = generatePsiAccessorImplCall(targetInfo);
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
        curId = "p" + (count++);
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
    String className = myShortener.fun(targetRule == null ? BnfConstants.PSI_ELEMENT_CLASS : getAccessorType(targetRule));
    final String getterName = "get" + toIdentifier(methodInfo.name, "");
    String tail = intf ? "();" : "() {";
    out((intf ? "" : "public ") + (many ? myShortener.fun(CommonClassNames.JAVA_UTIL_LIST) + "<" : "") + className + (many ? "> " : " ") + getterName + tail);

    if (!intf) {
      out(sb.toString());
      out("}");
    }
    newLine();
  }

  private void generateUtilMethod(String methodName,
                                  String psiImplUtilClass,
                                  boolean intf,
                                  String psiImplClass,
                                  JavaHelper javaHelper) {
    NavigatablePsiElement method = psiImplUtilClass == null ? null : javaHelper.findClassMethod(psiImplUtilClass, methodName, -1, psiImplClass);
    List<String> methodTypes = method == null ? Collections.<String>emptyList() : javaHelper.getMethodTypes(method);
    String returnType = methodTypes.isEmpty()? "void" : myShortener.fun(methodTypes.get(0));
    List<String> paramsTypes = methodTypes.isEmpty()? methodTypes : methodTypes.subList(1, methodTypes.size());
    StringBuilder sb = new StringBuilder();
    int count = -1;
    for (String s : paramsTypes) {
      if (++count < 2) continue;
      if (count > 2 && count % 2 == 0) {
        sb.append(", ");
      }
      else if (count > 2) sb.append(" ");
      sb.append(myShortener.fun(s));
    }

    for (String s : javaHelper.getAnnotations(method)) {
      out("@" + myShortener.fun(s));
    }
    out((intf ? "" : "public ") + returnType + " " + methodName + "(" + sb.toString() + ")" + (intf ? ";" : " {"));
    if (!intf) {
      sb.setLength(0);
      count = -1;
      for (String s : paramsTypes) {
        if (++count < 2) continue;
        if (count % 2 == 0) {
          sb.append(", ");
        }
        else {
          sb.append(s);
        }
      }

      out(("void".equals(returnType) ? "" : "return ") +
          myShortener.fun(StringUtil.notNullize(psiImplUtilClass, KnownAttribute.PSI_IMPL_UTIL_CLASS.getName())) + "." + methodName
          + "(this" + sb.toString() + ");");
      out("}");
    }
    newLine();
  }
}
