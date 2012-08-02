/*
 * Copyright 2011-2011 Gregory Shrago
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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringHash;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.Function;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
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

  private final Map<String, String> myRuleParserClasses = new TreeMap<String, String>();
  private final Map<String, String> myParserLambdas = new TreeMap<String, String>();
  private final Map<String, String> mySimpleTokens = new THashMap<String, String>();
  private final MultiMap<BnfRule, BnfRule> myRuleExtendsMap;
  private final BnfFile myFile;
  private final String mySourcePath;
  private final String myOutputPath;
  private final String myGrammarRoot;
  private final String myGrammarRootParser;
  private final String myRuleClassPrefix;
  private final boolean generateMemoizationCode;
  private final boolean generateExtendedPin;
  private final String visitorClassName;
  private final int generateFirstCheck;

  private int myOffset;
  private PrintWriter myOut;

  private boolean myUnitTestMode;

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
    generateMemoizationCode = getRootAttribute(myFile, KnownAttribute.GENERATE_MEMOIZATION);
    generateExtendedPin = getRootAttribute(myFile, KnownAttribute.EXTENDED_PIN);
    generateFirstCheck = getRootAttribute(myFile, KnownAttribute.GENERATE_FIRST_CHECK);
    myRuleClassPrefix = getPsiClassPrefix(myFile);
    String tmpVisitorClass = getRootAttribute(myFile, KnownAttribute.PSI_VISITOR_NAME);
    visitorClassName = StringUtil.isEmpty(tmpVisitorClass) ?
                       null : tmpVisitorClass.startsWith(myRuleClassPrefix) ?
                              tmpVisitorClass : myRuleClassPrefix + tmpVisitorClass;
    for (Pair<String, String> pair : getRootAttribute(myFile, KnownAttribute.TOKENS)) {
      mySimpleTokens.put(pair.second, pair.first); // string value to constant name
    }
    myRuleExtendsMap = RuleGraphHelper.computeInheritance(myFile);
  }

  public void setUnitTestMode(boolean unitTestMode) {
    myUnitTestMode = unitTestMode;
  }

  private void openOutput(File file) throws FileNotFoundException {
    String grammarName = FileUtil.getNameWithoutExtension(myFile.getName());
    String fileName = FileUtil.getNameWithoutExtension(file);
    if (myUnitTestMode) {
      String name = grammarName + (fileName.startsWith(grammarName) || fileName.endsWith("Parser") ? "" : ".PSI") + ".java";
      myOut = new PrintWriter(new FileOutputStream(new File(myOutputPath, name), true));
      out("// ---- " + file.getName() + " -----------------");
    }
    else {
      //noinspection ResultOfMethodCallIgnored
      file.getParentFile().mkdirs();
      myOut = new PrintWriter(new FileOutputStream(file));
    }
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
    boolean prevSemi = false;
    for (int start = 0, end; start < length; start = end + 1) {
      end = StringUtil.indexOf(s, '\n', start, length);
      if (end == -1) end = length;
      String substring = s.substring(start, end);
      if (!isComment && substring.startsWith("}")) myOffset--;
      if (myOffset > 0) {
        myOut.print(StringUtil.repeat("  ", start == 0 || prevSemi && !substring.equals("}") ? myOffset : myOffset + 1));
      }
      if (!isComment && substring.endsWith("{")) myOffset++;
      myOut.println(substring);
      prevSemi = substring.endsWith(";");
    }
  }

  private void newLine() {
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
      if (sortedCompositeTypes.containsKey(elementType)) continue;
      if (!Rule.isFake(rule)) {
        sortedCompositeTypes.put(elementType, rule);
      }
      sortedPsiRules.put(rule.getName(), rule);
    }
    if (myGrammarRoot != null) {
      String className = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);
      File parserFile = new File(myOutputPath, className.replace('.', File.separatorChar) + ".java");
      openOutput(parserFile);
      try {
        generateElementTypesHolder(className, sortedCompositeTypes, generatePsi);
      }
      finally {
        closeOutput();
      }
    }
    if (generatePsi) {
      Map<String, String> infClasses = new HashMap<String, String>();
      RuleGraphHelper graphHelper = new RuleGraphHelper(myFile, myRuleExtendsMap);
      String psiPackage = getPsiPackage(myFile);
      for (String ruleName : sortedPsiRules.keySet()) {
        BnfRule rule = myFile.getRule(ruleName);
        String psiClass = psiPackage + "." + getRulePsiClassName(rule, myRuleClassPrefix);
        infClasses.put(ruleName, psiClass);
        File psiFile = new File(myOutputPath, psiClass.replace('.', File.separatorChar) + ".java");
        openOutput(psiFile);
        try {
          generatePsiIntf(graphHelper, rule, psiClass, getSuperInterfaceNames(rule, psiPackage));
        }
        finally {
          closeOutput();
        }
      }
      String psiImplPackage = getPsiImplPackage(myFile);
      String suffix = getPsiImplSuffix(myFile);
      for (String ruleName : sortedPsiRules.keySet()) {
        BnfRule rule = myFile.getRule(ruleName);
        String psiClass = psiImplPackage + "." + getRulePsiClassName(rule, myRuleClassPrefix) + suffix;
        File psiFile = new File(myOutputPath, psiClass.replace('.', File.separatorChar) + ".java");
        openOutput(psiFile);
        try {
          generatePsiImpl(graphHelper, rule, psiClass, infClasses.get(ruleName), getSuperClassName(rule, psiImplPackage, suffix));
        }
        finally {
          closeOutput();
        }
      }
      if (visitorClassName != null && myGrammarRoot != null) {
        String psiClass = psiPackage + "." + visitorClassName;
        File psiFile = new File(myOutputPath, psiClass.replace('.', File.separatorChar) + ".java");
        openOutput(psiFile);
        try {
          generateVisitor(psiClass, sortedPsiRules);
        }
        finally {
          closeOutput();
        }
      }
    }
    boolean generateUtil = getRootAttribute(myFile, KnownAttribute.GENERATE_PARSER_UTIL);
    if (generateUtil) {
      generateParserUtil();
    }
  }

  private void generateVisitor(String psiClass, Map<String, BnfRule> sortedRules) {
    generateClassHeader(psiClass, Arrays.asList("org.jetbrains.annotations.*", "com.intellij.psi.PsiElementVisitor"),
                        "", false, "PsiElementVisitor");
    String firstImplements = ContainerUtil.getFirstItem(getRootAttribute(myFile, KnownAttribute.IMPLEMENTS));
    String superIntf = firstImplements == null ? "" : StringUtil.getShortName(firstImplements);
    Set<String> visited = new HashSet<String>();
    Set<String> all = new TreeSet<String>();
    for (String ruleName : sortedRules.keySet()) {
      BnfRule rule = sortedRules.get(ruleName);
      String methodName = getRulePsiClassName(rule, "");
      visited.add(methodName);
      out("public void visit" + methodName + "(@NotNull " + getRulePsiClassName(rule, myRuleClassPrefix) + " o) {");
      boolean first = true;
      for (String top : getSuperInterfaceNames(rule, "")) {
        all.add(top);
        if (!first && top.equals(superIntf)) continue;
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
    all.remove(superIntf);
    for (String top : ContainerUtil.concat(all, Arrays.asList(superIntf))) {
      String methodName = top.startsWith(myRuleClassPrefix) ? top.substring(myRuleClassPrefix.length()) : top;
      if (visited.contains(methodName)) continue;
      out("public void visit" + methodName + "(@NotNull " + top + " o) {");
      if (!methodName.equals(top) && !top.equals(superIntf)) {
        out("visit" + (superIntf.startsWith(myRuleClassPrefix) ? superIntf.substring(myRuleClassPrefix.length()) : superIntf) + "(o);");
      }
      else {
        out("visitElement(o);");
      }
      out("}");
      newLine();
    }

    out("}");
  }

  private void generateParserUtil() throws IOException {
    final String stubParser = getRootAttribute(myFile, KnownAttribute.PARSER_UTIL_CLASS);
    final String stubPackage = StringUtil.getPackageName(stubParser);
    String baseClassName = GeneratedParserUtilBase.class.getSimpleName();
    String baseFileName = baseClassName + ".java";
    InputStream baseUtilText = GeneratedParserUtilBase.class.getResourceAsStream(baseFileName);
    if (baseUtilText == null) return;

    Project project = myFile.getProject();
    if (JavaHelper.getJavaHelper(project).findClass(stubParser) != null) return;
    File baseFile = new File(myOutputPath, stubPackage.replace('.', File.separatorChar) + "/" + baseFileName);
    openOutput(baseFile);
    try {
      String text = FileUtil.loadTextAndClose(baseUtilText);
      text =
        text.replace("package " + StringUtil.getPackageName(GeneratedParserUtilBase.class.getName()) + ";", "package " + stubPackage + ";");
      generateFileHeader(baseClassName);
      myOut.write(text);
    }
    finally {
      closeOutput();
    }
    File utilFile = new File(myOutputPath, stubParser.replace('.', File.separatorChar) + ".java");
    openOutput(utilFile);
    try {
      generateClassHeader(stubParser, Collections.<String>emptyList(), "", false, baseClassName);
      newLine();
      out("}");
    }
    finally {
      closeOutput();
    }
  }


  public void generateParser() throws FileNotFoundException {
    for (String className : new TreeSet<String>(myRuleParserClasses.values())) {
      Map<String, BnfRule> map = new TreeMap<String, BnfRule>();
      for (String ruleName : myRuleParserClasses.keySet()) {
        if (className.equals(myRuleParserClasses.get(ruleName))) {
          map.put(ruleName, myFile.getRule(ruleName));
        }
      }
      File parserFile = new File(myOutputPath + File.separatorChar + className.replace('.', File.separatorChar) + ".java");
      openOutput(parserFile);
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
    BnfRule cur = rule;
    for (BnfRule next = rule; next != null; cur = !visited.add(next) ? null : next) {
      if (cur == null) break;
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
  private String[] getSuperInterfaceNames(BnfRule rule, String psiPackage) {
    ArrayList<String> strings = new ArrayList<String>();
    List<String> topRuleImplements = Collections.emptyList();
    String topRuleClass = null;
    BnfRule topSuper = getTopSuperRule(rule);
    boolean simpleMode = psiPackage.isEmpty();
    if (topSuper != null && topSuper != rule) {
      topRuleImplements = getAttribute(topSuper, KnownAttribute.IMPLEMENTS);
      topRuleClass = StringUtil.nullize((simpleMode ? "" : psiPackage + ".") + getRulePsiClassName(topSuper, myRuleClassPrefix));
      if (!StringUtil.isEmpty(topRuleClass)) strings.add(topRuleClass);
    }
    List<String> rootImplements = getRootAttribute(myFile, KnownAttribute.IMPLEMENTS);
    List<String> ruleImplements = getAttribute(rule, KnownAttribute.IMPLEMENTS);
    for (String className : ruleImplements) {
      BnfRule superIntfRule = myFile.getRule(className);
      if (superIntfRule != null) {
        strings.add((simpleMode ? "" : psiPackage + ".") + getRulePsiClassName(superIntfRule, myRuleClassPrefix));
      }
      else if (!topRuleImplements.contains(className) && (topRuleClass == null || !rootImplements.contains(className))) {
        String name = simpleMode ? StringUtil.getShortName(className) : className;
        if (className != null && strings.size() == 1) {
          strings.add(0, name);
        }
        else {
          strings.add(name);
        }
      }
    }
    return strings.toArray(new String[strings.size()]);
  }

  public void generateParser(String parserClass, final Set<String> ownRuleNames) {
    String elementTypeHolderClass = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);
    String stubParser = getRootAttribute(myFile, KnownAttribute.PARSER_UTIL_CLASS);
    List<String> parserImports = getRootAttribute(myFile, KnownAttribute.PARSER_IMPORTS);
    boolean rootParser = parserClass.equals(myGrammarRootParser);
    Set<String> imports = new LinkedHashSet<String>();
    imports.addAll(Arrays.asList("org.jetbrains.annotations.*",
                                 "com.intellij.lang.LighterASTNode",
                                 "com.intellij.lang.PsiBuilder",
                                 "com.intellij.lang.PsiBuilder.Marker",
                                 "com.intellij.openapi.diagnostic.Logger",
                                 "static " + elementTypeHolderClass + ".*",
                                 "static " + stubParser + ".*"));
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

    out("public static Logger LOG_ = Logger.getInstance(\"" + parserClass + "\");");
    newLine();

    if (rootParser) {
      generateRootParserContent(ownRuleNames);
    }
    for (String ruleName : ownRuleNames) {
      BnfRule rule = myFile.getRule(ruleName);
      if (Rule.isExternal(rule) || Rule.isFake(rule)) continue;
      out("/* ********************************************************** */");
      generateNode(rule, rule.getExpression(), ruleName, new THashSet<BnfExpression>());
      newLine();
    }
    Map<String, String> reversedLambdas = new THashMap<String, String>();
    for (Map.Entry<String, String> e : myParserLambdas.entrySet()) {
      String body = e.getValue();
      if (body.startsWith("#")) {
        String name = e.getKey();
        String value = reversedLambdas.get(body);
        if (value == null) {
          value = wrapCall(body.substring(1));
          reversedLambdas.put(body, name);
        }
        out("final static Parser " + name + " = " + value + ";");
        e.setValue(StringUtil.getShortName(parserClass) + "." + name);
      }
    }
    out("}");
  }

  private void generateRootParserContent(Set<String> ownRuleNames) {
    out("@NotNull");
    out("public ASTNode parse(IElementType root_, PsiBuilder builder_) {");
    out("int level_ = 0;");
    out("boolean result_;");
    out("builder_ = adapt_builder_(root_, builder_, this);");
    boolean first = true;
    for (String ruleName : ownRuleNames) {
      BnfRule rule = myFile.getRule(ruleName);
      if (!RuleGraphHelper.shouldGeneratePsi(rule, false) || Rule.isMeta(rule)) continue;
      if (Rule.isFake(rule)) continue;
      String elementType = getElementType(rule);
      out((first ? "" : "else ") + "if (root_ == " + elementType + ") {");
      String nodeCall = generateNodeCall(rule, null, ruleName);
      out("result_ = " + nodeCall + ";");
      out("}");
      if (first) first = false;
    }
    {
      if (!first) out("else {");
      out("Marker marker_ = builder_.mark();");
      out("result_ = parse_root_(root_, builder_, level_);");
      out("while (builder_.getTokenType() != null) {");
      out("builder_.advanceLexer();");
      out("}");
      out("marker_.done(root_);");
      if (!first) out("}");
    }
    out("return builder_.getTreeBuilt();");
    out("}");
    newLine();
    {
      BnfRule rootRule = myFile.getRule(myGrammarRoot);
      String nodeCall = generateNodeCall(rootRule, null, rootRule.getName());
      out("protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {");
      out("return " + nodeCall + ";");
      out("}");
      newLine();
    }
    if (!myRuleExtendsMap.isEmpty()) {
      out("private static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {");
      StringBuilder sb = new StringBuilder();
      Set<String> elementTypes = new TreeSet<String>();
      for (String ruleName : myRuleParserClasses.keySet()) {
        BnfRule baseRule = myFile.getRule(ruleName);
        if (Rule.isFake(baseRule)) continue;
        Collection<BnfRule> rules = myRuleExtendsMap.get(baseRule);
        if (rules.isEmpty()) continue;
        for (BnfRule rule : rules) {
          if (Rule.isFake(rule)) continue;
          elementTypes.add(getElementType(rule));
        }
        int i = 0;
        for (String elementType : elementTypes) {
          if (i > 0) sb.append(i % 4 == 0 ? ",\n" : ", ");
          sb.append(elementType);
          i++;
        }
        out("TokenSet.create(" + sb.toString() + "),");
        sb.setLength(0);
        elementTypes.clear();
      }
      out("};");
      out("public static boolean type_extends_(IElementType child_, IElementType parent_) {");
      out("for (TokenSet set : EXTENDS_SETS_) {");
      out("if (set.contains(child_) && set.contains(parent_)) return true;");
      out("}");
      out("return false;");
      out("}");
      newLine();
    }
  }

  private Function<String, String> generateClassHeader(String className,
                                                       Collection<String> imports,
                                                       String annos,
                                                       boolean intf,
                                                       String... supers) {
    generateFileHeader(className);
    final String packageName = StringUtil.getPackageName(className);
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
      for (String s : StringUtil.tokenize(item, TYPE_TEXT_SEPARATORS)) {
        if (!s.contains(".") || !s.equals(shortener.fun(s))) continue;
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
    out("public " + (intf ? "interface " : "class ") + StringUtil.getShortName(className) + sb.toString() + " {");
    newLine();
    return shortener;
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

  private void generateNode(final BnfRule rule, BnfExpression node, String funcName, Set<BnfExpression> visited) {
    IElementType type = getEffectiveType(node);

    for (String s : StringUtil.split(node.getText(), "\n")) {
      out("// " + s);
    }
    boolean isRule = node.getParent() == rule;
    boolean firstNonTrivial = node == Rule.firstNotTrivial(rule);
    boolean isPrivate = !(isRule || firstNonTrivial) || Rule.isPrivate(rule) || myGrammarRoot.equals(rule.getName());
    boolean isLeft = firstNonTrivial && Rule.isLeft(rule);
    boolean isLeftInner = isLeft && (isPrivate || Rule.isInner(rule));
    final String recoverRoot = firstNonTrivial ? Rule.attribute(rule, KnownAttribute.RECOVER_UNTIL) : null;
    final boolean canCollapse = (!isLeft || isLeftInner) && firstNonTrivial && myRuleExtendsMap.containsScalarValue(rule);

    final List<BnfExpression> children;
    out((!isRule ? "private " : isPrivate ? "" : "public ") + "static boolean " + funcName + "(PsiBuilder builder_, int level_"
        + collectExtraArguments(rule, node, true) + ") {");
    if (node instanceof BnfReferenceOrToken || node instanceof BnfLiteralExpression || node instanceof BnfExternalExpression) {
      children = Collections.singletonList(node);
      if (isPrivate && !isLeftInner && recoverRoot == null) {
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
      if (children.isEmpty()) {
        out("return true;");
        out("}");
        return;
      }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    String debugFuncName = funcName; // + ":" + node.toStringTree();
    out("if (!recursion_guard_(builder_, level_, \"" + debugFuncName + "\")) return false;");

    if (isTrivialNode(node)) {
      BnfExpression child = children.get(0);
      out("return " + generateNodeCall(rule, child, getNextName(funcName, 0)) + ";");
      out("}");
      generateNodeChildren(rule, funcName, children, visited);
      return;
    }


    String frameName = firstNonTrivial && !Rule.isMeta(rule)? quote(getRuleDisplayName(rule, !isPrivate)) : null;
    if (generateFirstCheck > 0 && recoverRoot == null && (isRule || firstNonTrivial)) {
      BnfFirstNextAnalyzer analyzer = new BnfFirstNextAnalyzer();
      Set<String> firstSet = analyzer.asStrings(analyzer.calcFirstInner(node, new THashSet<BnfExpression>(), new THashSet<BnfRule>()));
      List<String> firstElementTypes = new ArrayList<String>(firstSet.size());
      for (String s : firstSet) {
        @SuppressWarnings("StringEquality")
        boolean unknown = s == BnfFirstNextAnalyzer.MATCHES_EOF || s == BnfFirstNextAnalyzer.MATCHES_ANY;
        String elementType = unknown? null : firstToElementType(s);
        if (elementType != null) {
          firstElementTypes.add(elementType);
        }
        else {
          firstElementTypes.clear();
          break;
        }
      }
      // do not include frameName if FIRST is known and its size is 1
      if (!firstElementTypes.isEmpty() && firstElementTypes.size() == 1) frameName = null;
      if (!firstElementTypes.isEmpty() && firstElementTypes.size() <= generateFirstCheck) {
        StringBuilder sb = new StringBuilder("if (");
        for (int count = 0, elementTypesSize = firstElementTypes.size(); count < elementTypesSize; count++) {
          if (count > 0) sb.append(count % 2 == 0 ? "\n  && " : " && ");
          sb.append("!nextTokenIs(builder_, ").append(firstElementTypes.get(count)).append(")");
        }
        if (frameName != null) {
          sb.append(firstElementTypes.size() % 2 == 0 ? "\n  && " : " && ");
          sb.append("replaceVariants(builder_, ").append(firstElementTypes.size()).append(", ").append(frameName).append(")");
        }
        sb.append(") return false;");
        out(sb.toString());
      }
    }

    final long funcId = StringHash.calc(funcName);
    if (generateMemoizationCode) {
      out("if (memoizedFalseBranch(builder_, " + funcId + "L) return false;");
    }
    PinMatcher pinMatcher = new PinMatcher(rule, type, firstNonTrivial ? rule.getName() : funcName);
    boolean pinApplied = false;
    final boolean alwaysTrue = type == BNF_OP_OPT || type == BNF_OP_ZEROMORE;
    if (!alwaysTrue) {
      out("boolean result_ = " + (type == BNF_OP_ZEROMORE || type == BNF_OP_OPT) + ";");
    }
    boolean pinned = pinMatcher.active();
    if (pinned) {
      out("boolean pinned_ = false;");
    }
    if (!isPrivate && canCollapse) {
      out("int start_ = builder_.getCurrentOffset();");
    }
    if (isLeft) {
      out("Marker left_marker_ = (Marker)builder_.getLatestDoneMarker();");
      if (generateExtendedPin) {
        out("if (!invalid_left_marker_guard_(builder_, left_marker_, \"" + debugFuncName + "\")) return false;");
      }
    }
    if (!alwaysTrue || !isPrivate) {
      out("Marker marker_ = builder_.mark();");
    }

    String sectionType = recoverRoot != null ? "_SECTION_RECOVER_" :
                         type == BNF_OP_AND ? "_SECTION_AND_" :
                         type == BNF_OP_NOT ? "_SECTION_NOT_" :
                         pinned || frameName != null ? "_SECTION_GENERAL_" : null;
    if (sectionType != null) {
      out("enterErrorRecordingSection(builder_, level_, " + sectionType + ", " + frameName + ");");
    }

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
          nodeCall = generateTokenSequenceCall(children, i, pinMatcher, pinApplied, skip, nodeCall);
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
        if (!pinApplied && pinMatcher.matches(i, child)) {
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
        out("int offset_ = builder_.getCurrentOffset();");
        out("while (" + (alwaysTrue ? "true" : "result_") + ") {");
        out("if (!" + nodeCall + ") break;");
        out("int next_offset_ = builder_.getCurrentOffset();");
        out("if (offset_ == next_offset_) {");
        out("empty_element_parsed_guard_(builder_, offset_, \"" + debugFuncName + "\");");
        out("break;");
        out("}");
        out("offset_ = next_offset_;");
        out("}");
      }
      else if (type == BNF_OP_AND) {
        out("result_ = " + nodeCall + ";");
      }
      else if (type == BNF_OP_NOT) {
        out("result_ = !" + nodeCall + ";");
      }
      else {
        throw new AssertionError("unexpected: " + type);
      }
    }

    if (type == BNF_OP_AND || type == BNF_OP_NOT) {
      out("marker_.rollbackTo();");
    }
    else if (!isPrivate) {
      String elementType = getElementType(rule);
      if (canCollapse) {
        out(
          "LighterASTNode last_ = " + (alwaysTrue ? "builder_.getLatestDoneMarker();" : "result_? builder_.getLatestDoneMarker() : null;"));
        out("if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), " + elementType + ")) {");
        out("marker_.drop();");
        out("}");
      }
      if (!alwaysTrue || canCollapse) {
        out((canCollapse ? "else " : "") + (alwaysTrue ? "{" : "if (result_" + (pinned ? " || pinned_" : "") + ") {"));
      }
      if (isLeftInner) {
        out("marker_.done(" + elementType + ");");
        out("left_marker_.precede().done(((LighterASTNode)left_marker_).getTokenType());");
        out("left_marker_.drop();");
      }
      else if (isLeft) {
        out("marker_.drop();");
        out("left_marker_.precede().done(" + elementType + ");");
      }
      else {
        out("marker_.done(" + elementType + ");");
      }
      if (!alwaysTrue || canCollapse) {
        out("}");
      }
      if (!alwaysTrue) {
        out("else {");
        out("marker_.rollbackTo();");
        out("}");
      }
    }
    else {
      if (!alwaysTrue) {
        out("if (!result_" + (pinned ? " && !pinned_" : "") + ") {");
        out("marker_.rollbackTo();");
        out("}");
        out("else {");
        out("marker_.drop();");
      }
      if (isLeftInner) {
        out("left_marker_.precede().done(((LighterASTNode)left_marker_).getTokenType());");
        out("left_marker_.drop();");
      }
      if (!alwaysTrue) {
        out("}");
      }
    }
    if (sectionType != null) {
      final String untilCall;
      if (recoverRoot != null) {
        BnfRule untilRule = myFile.getRule(recoverRoot);
        untilCall = untilRule == null ? null : generateWrappedNodeCall(rule, null, untilRule.getName());
      }
      else {
        untilCall = null;
      }
      final String resultEq = alwaysTrue ? "" : "result_ = ";
      final String resultRef = alwaysTrue ? "true" : "result_";
      out(resultEq + "exitErrorRecordingSection(builder_, " + "level_, " + resultRef + ", " +
          (pinned ? "pinned_" : "false") + ", " + sectionType + ", " + untilCall + ");");
    }

    if (!alwaysTrue && generateMemoizationCode) {
      out("if (!result_" + (pinned ? " && !pinned_" : "") + ") memoizeFalseBranch(builder_, " + funcId + ")");
    }
    out("return " + (alwaysTrue ? "true" : "result_" + (pinned ? " || pinned_" : "")) + ";");
    out("}");
    generateNodeChildren(rule, funcName, children, visited);
  }

  @Nullable
  private static String getRuleDisplayName(BnfRule rule, boolean force) {
    String name = getAttribute(rule, KnownAttribute.NAME);
    BnfRule realRule = rule;
    if (name != null) {
      realRule = ((BnfFile)rule.getContainingFile()).getRule(name);
      if (realRule != null) name = getAttribute(realRule, KnownAttribute.NAME);
    }
    if (name != null || (!force && realRule == rule)) {
      return StringUtil.isEmpty(name)? null : "<" + name + ">";
    }
    return "<" + toDisplayOrConstantName(realRule.getName(), false) + ">";
  }

  private static String toDisplayOrConstantName(String name, boolean constant) {
    String[] strings = NameUtil.splitNameIntoWords(name);
    for (int i = 0; i < strings.length; i++) strings[i] = constant? strings[i].toUpperCase() : strings[i].toLowerCase();
    return StringUtil.join(strings, constant? "_" : " ");
  }

  private void generateNodeChildren(BnfRule rule, String funcName, List<BnfExpression> children, Set<BnfExpression> visited) {
    for (int i = 0, len = children.size(); i < len; i++) {
      BnfExpression child = children.get(i);
      if (child instanceof BnfLiteralExpression || child instanceof BnfReferenceOrToken) {
        // do not generate
      }
      else if (child instanceof BnfExternalExpression) {
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
            generateNode(rule, expression, getNextName(getNextName(funcName, i), j - 1), visited);
          }
        }
      }
      else {
        newLine();
        generateNode(rule, child, getNextName(funcName, i), visited);
      }
    }
  }

  private static String collectExtraArguments(BnfRule rule, BnfExpression expression, final boolean declaration) {
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
        return getElementType(attributeName);
      }
      return null;
    }
    return getElementType(first);
  }

  private String getTokenName(String value) {
    String existing = mySimpleTokens.get(value);
    if (existing == null) {
      String attributeName = getAttributeName(ContainerUtil.getFirstItem(myFile.getAttributes()), value);
      if (attributeName != null) {
        mySimpleTokens.put(value, attributeName);
        return attributeName;
      }
    }
    return existing;
  }

  private String generateTokenSequenceCall(List<BnfExpression> children,
                                           int startIndex,
                                           PinMatcher pinMatcher,
                                           boolean pinApplied,
                                           int[] skip,
                                           String nodeCall) {
    if (startIndex == children.size() - 1 || !nodeCall.startsWith("consumeToken(builder_, ")) return nodeCall;
    ArrayList<String> list = new ArrayList<String>();
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
      list.add(getElementType(tokenName));
      if (!pinApplied && pinMatcher.matches(i, child)) {
        pin = i - startIndex + 1;
      }
    }
    if (list.size() < 2) return nodeCall;
    skip[0] = list.size() - 1;
    return "consumeTokens(builder_, " + pin + ", " + StringUtil.join(list, ", ") + ")";
  }

  private String generateNodeCall(BnfRule rule, @Nullable BnfExpression node, String nextName) {
    IElementType type = node == null ? BNF_REFERENCE_OR_TOKEN : getEffectiveType(node);
    String text = node == null ? nextName : node.getText();
    if (type == BNF_STRING) {
      String value = StringUtil.stripQuotesAroundValue(text);
      String attributeName = getTokenName(value);
      if (attributeName != null) {
        return generateConsumeToken(attributeName);
      }
      return generateConsumeTextToken(value);
    }
    else if (type == BNF_NUMBER) {
      return generateConsumeTextToken(text);
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
          method = subRule.getName();
          String parserClass = myRuleParserClasses.get(method);
          if (!parserClass.equals(myGrammarRootParser) && !parserClass.equals(myRuleParserClasses.get(rule.getName()))) {
            method = StringUtil.getShortName(parserClass) + "." + method;
          }
          return method + "(builder_, level_ + 1)";
        }
      }
      mySimpleTokens.put(text, null);
      return generateConsumeToken(text);
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
    if (targetRule != null && Rule.isExternal(targetRule)) {
      metaParameterNames = GrammarUtil.collectExtraArguments(targetRule, targetRule.getExpression());
      callParameters = GrammarUtil.getExternalRuleExpressions(targetRule);
      metaParameters = expressions;
      method = callParameters.get(0).getText();
      if (metaParameterNames.size() < expressions.size() - 1) {
        callParameters = ContainerUtil.concat(callParameters, expressions.subList(metaParameterNames.size() + 1, expressions.size()));
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
          if (argument.startsWith("\'")) {
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
      return wrapCall(generateNodeCall(rule, nested, nextName));
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

  private static String wrapCall(String nodeCall) {
    return "new Parser() {\npublic boolean parse(PsiBuilder builder_, int level_) {\nreturn " + nodeCall + ";\n}\n}";
  }

  private static String generateConsumeTextToken(String tokenText) {
    return "consumeToken(builder_, \"" + tokenText + "\")";
  }

  private String generateConsumeToken(String tokenName) {
    return "consumeToken(builder_, " + getElementType(tokenName) + ")";
  }

  private static String getElementType(BnfRule rule) {
    String elementType = StringUtil.notNullize(getAttribute(rule, KnownAttribute.ELEMENT_TYPE), rule.getName());
    return getAttribute(rule, KnownAttribute.ELEMENT_TYPE_PREFIX) + toDisplayOrConstantName(elementType, true);
  }

  private String getElementType(String token) {
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
      imports.add("com.intellij.lang.ASTNode");
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
    Function<String, String> shortener = generateClassHeader(className, imports, "", true);
    for (String elementType : sortedCompositeTypes.keySet()) {
      Pair<String, String> pair = compositeToClassAndFactoryMap.get(elementType);
      String elementCreateCall;
      if (pair.second == null) {
        elementCreateCall = "new " + StringUtil.getShortName(pair.first);
      } else {
        elementCreateCall = shortener.fun(StringUtil.getPackageName(pair.second)) + "." + StringUtil.getShortName(pair.second);
      }
      out("IElementType " + elementType + " = " + elementCreateCall + "(\"" + elementType + "\");");
    }
    if (generateTokens) {
      newLine();
      Map<String, String> sortedTokens = new TreeMap<String, String>();
      String tokenCreateCall;
      if (tokenTypeFactory == null) {
        tokenCreateCall = "new " + StringUtil.getShortName(tokenTypeClass);
      }
      else {
        tokenCreateCall = shortener.fun(StringUtil.getPackageName(tokenTypeFactory)) + "." + StringUtil.getShortName(tokenTypeFactory);
      }
      for (String tokenText : mySimpleTokens.keySet()) {
        String tokenName = ObjectUtils.chooseNotNull(mySimpleTokens.get(tokenText), tokenText);
        sortedTokens.put(getElementType(tokenName), tokenText);
      }
      for (String tokenType : sortedTokens.keySet()) {
        out("IElementType " + tokenType + " = " + tokenCreateCall + "(\"" + sortedTokens.get(tokenType) + "\");");
      }
    }
    if (generatePsi) {
      newLine();
      out("class Factory {");
      out("public static PsiElement createElement(ASTNode node) {");
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
  private void generatePsiIntf(RuleGraphHelper helper, BnfRule rule, String psiClass, String... psiSupers) {
    String psiImplUtilClass = getRootAttribute(myFile, KnownAttribute.PSI_IMPL_UTIL_CLASS);

    Map<PsiElement, RuleGraphHelper.Cardinality> accessors = helper.getFor(rule);
    Collection<BnfRule> sortedPublicRules = getSortedPublicRules(accessors.keySet());

    Set<String> imports = new LinkedHashSet<String>();
    imports.addAll(Arrays.asList("java.util.List",
                                 "org.jetbrains.annotations.*",
                                 "com.intellij.psi.PsiElement"));
    imports.addAll(Arrays.asList(psiSupers));
    imports.addAll(getRuleAccessorClasses(rule, sortedPublicRules));
    JavaHelper javaHelper = JavaHelper.getJavaHelper(myFile.getProject());
    List<Pair<String, String>> methods = getAttribute(rule, KnownAttribute.METHODS);
    for (Pair<String, String> pair : methods) {
      if (pair.second == null) addUtilMethodTypes(imports, pair.first, psiImplUtilClass, javaHelper);
    }

    Function<String, String> shortener = generateClassHeader(psiClass, imports, "", true, psiSupers);
    for (PsiElement tree : sortedPublicRules) {
      generatePsiAccessor(rule, tree, accessors.get(tree), true, shortener);
    }
    for (BnfReferenceOrToken tree : getSortedSimpleTokens(accessors.keySet())) {
      generatePsiAccessor(rule, tree, accessors.get(tree), true, shortener);
    }
    for (Pair<String, String> entry : methods) {
      if (entry.second == null) {
        generateUtilMethod(entry.first, psiImplUtilClass, true, javaHelper, shortener);
      }
      else {
        generateUserPsiAccessors(rule, helper, entry.first, entry.second, true, shortener);
      }
    }
    out("}");
  }

  private void generatePsiImpl(RuleGraphHelper helper,
                               BnfRule rule,
                               String psiClass,
                               String superInterface,
                               String superRuleClass) {
    String typeHolderClass = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);
    String psiImplUtilClass = getRootAttribute(myFile, KnownAttribute.PSI_IMPL_UTIL_CLASS);
    // mixin attribute overrides "extends":
    String implSuper = StringUtil.notNullize(getAttribute(rule, KnownAttribute.MIXIN), superRuleClass);
    Map<PsiElement, RuleGraphHelper.Cardinality> accessors = helper.getFor(rule);
    Collection<BnfRule> sortedPublicRules = getSortedPublicRules(accessors.keySet());

    Set<String> imports = new LinkedHashSet<String>();
    imports.addAll(Arrays.asList("java.util.List",
                                 "org.jetbrains.annotations.*",
                                 "com.intellij.lang.ASTNode",
                                 "com.intellij.psi.PsiElement"));
    if (visitorClassName != null) imports.add("com.intellij.psi.PsiElementVisitor");
    imports.add("com.intellij.psi.util.PsiTreeUtil");
    imports.add("static " + typeHolderClass + ".*");
    if (StringUtil.isNotEmpty(implSuper)) imports.add(implSuper);
    imports.add(StringUtil.getPackageName(superInterface) + ".*");
    if (StringUtil.isNotEmpty(psiImplUtilClass)) imports.add(psiImplUtilClass);
    imports.addAll(getRuleAccessorClasses(rule, sortedPublicRules));
    JavaHelper javaHelper = JavaHelper.getJavaHelper(myFile.getProject());
    List<Pair<String, String>> methods = getAttribute(rule, KnownAttribute.METHODS);
    for (Pair<String, String> pair : methods) {
      if (pair.second == null) addUtilMethodTypes(imports, pair.first, psiImplUtilClass, javaHelper);
    }

    Function<String, String> shortener = generateClassHeader(psiClass, imports, "", false, StringUtil.getShortName(implSuper),
                                                             StringUtil.getShortName(superInterface));
    String shortName = StringUtil.getShortName(psiClass);
    out("public " + shortName + "(ASTNode node) {");
    out("super(node);");
    out("}");
    newLine();
    for (BnfRule tree : sortedPublicRules) {
      generatePsiAccessor(rule, tree, accessors.get(tree), false, shortener);
    }
    for (BnfReferenceOrToken tree : getSortedSimpleTokens(accessors.keySet())) {
      generatePsiAccessor(rule, tree, accessors.get(tree), false, shortener);
    }
    if (visitorClassName != null) {
      out("public void accept(@NotNull PsiElementVisitor visitor) {");
      out("if (visitor instanceof " +
          visitorClassName +
          ") ((" +
          visitorClassName +
          ")visitor).visit" +
          getRulePsiClassName(rule, "") +
          "(this);");
      out("else super.accept(visitor);");
      out("}");
      newLine();
    }
    for (Pair<String, String> entry : methods) {
      if (entry.second == null) {
        generateUtilMethod(entry.first, psiImplUtilClass, false, javaHelper, shortener);
      }
      else {
        generateUserPsiAccessors(rule, helper, entry.first, entry.second, false, shortener);
      }
    }
    out("}");
  }

  private static void addUtilMethodTypes(Collection<String> result, String methodName, String psiImplUtilClass, JavaHelper javaHelper) {
    NavigatablePsiElement method = psiImplUtilClass == null ? null : javaHelper.findClassMethod(psiImplUtilClass, methodName, -1);
    if (method != null) {
      for (String s : javaHelper.getMethodTypes(method)) {
        if (s.contains(".")) result.add(s);
      }
      for (String s : javaHelper.getAnnotations(method)) {
        result.add(s);
      }
    }
  }

  private void generatePsiAccessor(BnfRule rule,
                                   PsiElement tree,
                                   RuleGraphHelper.Cardinality type,
                                   boolean intf,
                                   Function<String, String> shortener) {
    BnfRule treeRule = tree instanceof BnfRule ? (BnfRule)tree : null;

    boolean many = type == AT_LEAST_ONE || type == ANY_NUMBER;

    String ruleName;
    if (treeRule == null) {
      IElementType effectiveType = getEffectiveType(tree);
      if (effectiveType == BNF_STRING) {
        //String value = StringUtil.stripQuotesAroundValue(tree.getText());
        //ruleName = getAttributeName(rule, value);
        return; // do not generate
      }
      else if (effectiveType == BNF_REFERENCE_OR_TOKEN) {
        ruleName = tree.getText();
        // generate lowercase ident, id, string, etc
        if (!ruleName.toLowerCase().equals(ruleName)) return;
        if (many) return;
      }
      else {
        ruleName = null; // do not bother generate numbers & simple literals
      }
    }
    else {
      ruleName = treeRule.getName();
    }
    if (ruleName == null) return;
    String defaultGetterName = "get" + toIdentifier(ruleName, "");
    String getterNameBody = StringUtil.notNullize(getAttribute(rule, KnownAttribute.METHOD_RENAMES, defaultGetterName), defaultGetterName);
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
    String className = shortener.fun(getAccessorType(treeRule != null ? treeRule : rule, tree));
    String tail = intf ? "();" : "() {";
    out((intf ? "" : "public ") + (many ? "List<" : "") + className + (many ? "> " : " ") + getterName + tail);
    if (!intf) {
      if (treeRule == null) {
        out("return " + (type == REQUIRED ? "findNotNullChildByType" : "findChildByType") + "(" + getElementType(ruleName) + ");");
      }
      else {
        if (many) {
          out("return PsiTreeUtil.getChildrenOfTypeAsList(this, " + className + ".class);");
        }
        else {
          out("return " + (type == REQUIRED ? "findNotNullChildByClass" : "findChildByClass") + "(" + className + ".class);");
        }
      }
      out("}");
    }
    newLine();
  }

  private String getAccessorType(BnfRule rule, PsiElement tree) {
    BnfRule treeRule = tree instanceof BnfRule ? (BnfRule)tree : null;
    if (treeRule == null) {
      return BnfConstants.PSI_ELEMENT_CLASS;
    }
    else if (Rule.isExternal(treeRule)) {
      String attribute = ContainerUtil.getFirstItem(getAttribute(treeRule, KnownAttribute.IMPLEMENTS));
      //noinspection StringEquality
      return attribute == KnownAttribute.IMPLEMENTS.getDefaultValue().get(0) ? BnfConstants.PSI_ELEMENT_CLASS : attribute;
    }
    else {
      return getRulePsiClassName(rule, myRuleClassPrefix);
    }
  }

  private static Collection<BnfRule> getSortedPublicRules(Set<PsiElement> accessors) {
    Map<String, BnfRule> result = new TreeMap<String, BnfRule>();
    for (PsiElement tree : accessors) {
      if (tree instanceof BnfRule) {
        BnfRule rule = (BnfRule)tree;
        if (!Rule.isPrivate(rule)) result.put(rule.getName(), rule);
      }
    }
    return result.values();
  }

  private Collection<BnfReferenceOrToken> getSortedSimpleTokens(Set<PsiElement> accessors) {
    TreeMap<String, BnfReferenceOrToken> result = new TreeMap<String, BnfReferenceOrToken>();
    for (PsiElement tree : accessors) {
      if (!(tree instanceof BnfReferenceOrToken)) continue;
      if (mySimpleTokens.containsKey(tree.getText()) /*|| type == STRING || type == NUMBER*/) {
        result.put(tree.getText(), (BnfReferenceOrToken)tree);
      }
    }
    return result.values();
  }

  private Collection<String> getRuleAccessorClasses(BnfRule rule, Collection<BnfRule> bnfRules) {
    final TreeSet<String> result = new TreeSet<String>();
    for (BnfRule r : bnfRules) {
      result.add(getAccessorType(rule, r));
    }
    return result;
  }

  private void generateUserPsiAccessors(BnfRule startRule,
                                        RuleGraphHelper helper,
                                        String methodName, String pathString,
                                        boolean intf, Function<String, String> shortener) {
    if (pathString == null) return;

    StringBuilder sb = new StringBuilder();
    BnfRule targetRule = startRule;
    RuleGraphHelper.Cardinality cardinality = REQUIRED;
    String context = "";
    String[] splittedPath = pathString.split("/");
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
      BnfRule rule = myFile.getRule(item);
      if (rule == null) return;  // wrong rule
      RuleGraphHelper.Cardinality card = helper.getFor(targetRule).get(rule);
      if (card == null) return; // not in list
      if (index != null && !card.many()) return; // list expected

      boolean many = card.many();
      String className = shortener.fun(getAccessorType(rule, startRule));
      String donorMethodName = "get" + toIdentifier(item, "") + (card.many() ? "List" : "") + "()";
      String type = (many ? "List<" : "") + className + (many ? "> " : " ");
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
      sb.append(context).append(donorMethodName).append(";\n");

      context = curId;

      targetRule = rule; // next accessors
      cardinality = card;
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
        if (card != AT_LEAST_ONE || !index.equals("0")) { // range check
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
        cardinality = card == AT_LEAST_ONE && index.equals("0") ? REQUIRED : OPTIONAL;
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
    String className = shortener.fun(getAccessorType(targetRule, startRule));
    final String getterName = "get" + toIdentifier(methodName, "");
    String tail = intf ? "();" : "() {";
    out((intf ? "" : "public ") + (many ? "List<" : "") + className + (many ? "> " : " ") + getterName + tail);

    if (!intf) {
      out(sb.toString());
      out("}");
    }
    newLine();
  }

  private void generateUtilMethod(String methodName,
                                  String psiImplUtilClass,
                                  boolean intf,
                                  final JavaHelper javaHelper,
                                  Function<String, String> shortener) {
    NavigatablePsiElement method = psiImplUtilClass == null ? null : javaHelper.findClassMethod(psiImplUtilClass, methodName, -1);
    List<String> methodTypes = method == null ? Arrays.asList("void") : javaHelper.getMethodTypes(method);
    String returnType = shortener.fun(methodTypes.get(0));
    List<String> paramsTypes = methodTypes.subList(1, methodTypes.size());
    StringBuilder sb = new StringBuilder();
    int count = -1;
    for (String s : paramsTypes) {
      if (++count < 2) continue;
      if (count > 2 && count % 2 == 0) {
        sb.append(", ");
      }
      else if (count > 2) sb.append(" ");
      sb.append(shortener.fun(s));
    }

    for (String s : javaHelper.getAnnotations(method)) {
      out("@" + shortener.fun(s));
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
          shortener.fun(StringUtil.notNullize(psiImplUtilClass, KnownAttribute.PSI_IMPL_UTIL_CLASS.getName())) + "." + methodName
          + "(this" + sb.toString() + ");");
      out("}");
    }
    newLine();
  }
}
