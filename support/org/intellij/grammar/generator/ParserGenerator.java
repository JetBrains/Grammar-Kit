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
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.containers.MultiMap;
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

import static java.lang.String.format;
import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality.*;
import static org.intellij.grammar.generator.RuleGraphHelper.getSynonymTargetOrSelf;
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
  private final Set<String> myTokensUsedInGrammar = ContainerUtil.newLinkedHashSet();
  private final BnfFile myFile;
  private final String mySourcePath;
  private final String myOutputPath;
  private final String myGrammarRoot;
  private final String myGrammarRootParser;
  private final String myRuleClassPrefix;
  private final String myParserUtilClass;

  private final String visitorClassName;


  private int myOffset;
  private PrintWriter myOut;
  private Function<String, String> myShortener;

  private final RuleGraphHelper myGraphHelper;
  private final ExpressionHelper myExpressionHelper;
  private final RuleMethodsHelper myRulesMethodsHelper;
  private final KnownAttribute.ListValue myUnknownRootAttributes;

  final Names N;
  final GenOptions G;

  public ParserGenerator(BnfFile tree, String sourcePath, String outputPath) {
    myFile = tree;
    mySourcePath = sourcePath;
    myOutputPath = outputPath;

    G = new GenOptions(myFile);
    N = G.names;

    List<BnfRule> rules = tree.getRules();
    myGrammarRoot = rules.isEmpty() ? null : rules.get(0).getName();
    for (BnfRule r : rules) {
      myRuleParserClasses.put(r.getName(), getAttribute(r, KnownAttribute.PARSER_CLASS));
    }
    myGrammarRootParser = myGrammarRoot == null? null : myRuleParserClasses.get(myGrammarRoot);
    myRuleClassPrefix = getPsiClassPrefix(myFile);
    myParserUtilClass = ObjectUtils.chooseNotNull(getRootAttribute(myFile, KnownAttribute.PARSER_UTIL_CLASS.alias("stubParserClass")),
                                                  getRootAttribute(myFile, KnownAttribute.PARSER_UTIL_CLASS));
    String tmpVisitorClass = getRootAttribute(myFile, KnownAttribute.PSI_VISITOR_NAME);
    visitorClassName = !G.generateVisitor || StringUtil.isEmpty(tmpVisitorClass) ?
                       null : tmpVisitorClass.startsWith(myRuleClassPrefix) ?
                              tmpVisitorClass : myRuleClassPrefix + tmpVisitorClass;
    mySimpleTokens = ContainerUtil.newLinkedHashMap(RuleGraphHelper.getTokenMap(myFile));
    myUnknownRootAttributes = collectUnknownAttributes(myFile);
    myGraphHelper = RuleGraphHelper.getCached(myFile);
    myExpressionHelper = new ExpressionHelper(myFile, myGraphHelper, true);
    myRulesMethodsHelper = new RuleMethodsHelper(myGraphHelper, myExpressionHelper, mySimpleTokens, G);
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

  public void out(String s, Object... args) {
    out(format(s, args));
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
    if (myGrammarRoot != null && (G.generateTokenTypes || G.generateElementTypes || G.generatePsi && G.generatePsiFactory)) {
      String className = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);
      openOutput(className);
      try {
        generateElementTypesHolder(className, sortedCompositeTypes);
      }
      finally {
        closeOutput();
      }
    }
    if (G.generatePsi) {
      checkClassAvailability(myFile, getRootAttribute(myFile, KnownAttribute.PSI_IMPL_UTIL_CLASS),
                             "PSI method signatures will not be detected");

      myRulesMethodsHelper.buildMaps(sortedPsiRules.values());
      for (BnfRule r : sortedPsiRules.values()) {
        myPackageClasses.add(getRulePsiClassName(r, myRuleClassPrefix));
      }
      Map<String, String> infClasses = ContainerUtil.newTroveMap();
      String psiPackage = getPsiPackage(myFile);
      String suffix = getPsiImplSuffix(myFile);
      String psiImplPackage = getPsiImplPackage(myFile);

      for (String ruleName : sortedPsiRules.keySet()) {
        BnfRule rule = myFile.getRule(ruleName);
        String psiClass = psiPackage + "." + getRulePsiClassName(rule, myRuleClassPrefix);

        infClasses.put(ruleName, psiClass);
        openOutput(psiClass);
        try {
          generatePsiIntf(rule, psiClass, getSuperInterfaceNames(myFile, rule, psiPackage, myRuleClassPrefix));
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
          String superClassName = getSuperClassName(myFile, rule, psiImplPackage, myRuleClassPrefix, suffix);
          generatePsiImpl(rule, psiImplClass, infClasses.get(ruleName), superClassName);
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
      imports.addAll(getSuperInterfaceNames(myFile, rule, StringUtil.getPackageName(psiClass), myRuleClassPrefix));
    }
    String r = G.visitorValue != null ? "<" + G.visitorValue + ">" : "";
    String t = G.visitorValue != null ? G.visitorValue : "void";
    String ret = G.visitorValue != null ? "return " : "";
    generateClassHeader(psiClass + r, imports, "", false, BnfConstants.PSI_ELEMENT_VISITOR_CLASS);
    Set<String> visited = new HashSet<String>();
    Set<String> all = new TreeSet<String>();
    for (BnfRule rule : sortedRules.values()) {
      String methodName = getRulePsiClassName(rule, "");
      visited.add(methodName);
      out("public " + t + " visit" + methodName + "(@NotNull " + getRulePsiClassName(rule, myRuleClassPrefix) + " o) {");
      boolean first = true;
      for (String top : getSuperInterfaceNames(myFile, rule, "", myRuleClassPrefix)) {
        if (!first && top.equals(superIntf)) continue;
        int trimIdx = StringUtil.indexOfAny(top, TYPE_TEXT_SEPARATORS); // trim generics
        top = myShortener.fun(trimIdx > 0 ? top.substring(0, trimIdx) : top);
        if (first) all.add(top);
        if (top.startsWith(myRuleClassPrefix)) {
          top = top.substring(myRuleClassPrefix.length());
        }
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
      String methodName = top.startsWith(myRuleClassPrefix) ? top.substring(myRuleClassPrefix.length()) : top;
      if (visited.contains(methodName)) continue;
      out("public " + t + " visit" + methodName + "(@NotNull " + myShortener.fun(top) + " o) {");
      if (!methodName.equals(top) && !top.equals(shortSuperIntf)) {
        out(ret + "visit" + (shortSuperIntf.startsWith(myRuleClassPrefix) ? shortSuperIntf.substring(myRuleClassPrefix.length()) : shortSuperIntf) + "(o);");
      }
      else {
        out("visitElement(o);");
        if (G.visitorValue != null) out(ret + "null;");
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

  public void generateParser(String parserClass, final Set<String> ownRuleNames) {
    String elementTypeHolderClass = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS);
    List<String> parserImports = getRootAttribute(myFile, KnownAttribute.PARSER_IMPORTS).asStrings();
    boolean rootParser = parserClass.equals(myGrammarRootParser);
    Set<String> imports = new LinkedHashSet<String>();
    imports.addAll(Arrays.asList(BnfConstants.PSI_BUILDER_CLASS,
                                 BnfConstants.PSI_BUILDER_CLASS +".Marker",
                                 "static " + elementTypeHolderClass + ".*",
                                 "static " + myParserUtilClass + ".*"));
    if (!rootParser) {
      imports.add("static " + myGrammarRootParser + ".*");
    }
    else {
      imports.addAll(Arrays.asList(BnfConstants.IELEMENTTYPE_CLASS,
                                   BnfConstants.AST_NODE_CLASS,
                                   BnfConstants.TOKEN_SET_CLASS,
                                   BnfConstants.PSI_PARSER_CLASS,
                                   BnfConstants.LIGHT_PSI_PARSER_CLASS));
    }
    imports.addAll(parserImports);

    generateClassHeader(parserClass, imports,
                        "@SuppressWarnings({\"SimplifiableIfStatement\", \"UnusedAssignment\"})",
                        false, "",
                        rootParser ? BnfConstants.PSI_PARSER_CLASS : "",
                        rootParser ? BnfConstants.LIGHT_PSI_PARSER_CLASS : "");

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

  public String wrapCallWithParserInstance(String nodeCall) {
    return format("new Parser() {\npublic boolean parse(PsiBuilder %s, int %s) {\nreturn %s;\n}\n}", N.builder, N.level, nodeCall);
  }

  private void generateRootParserContent(Set<String> ownRuleNames) {
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
    boolean first = true;
    for (String ruleName : ownRuleNames) {
      BnfRule rule = myFile.getRule(ruleName);
      if (!RuleGraphHelper.shouldGeneratePsi(rule, false) || Rule.isMeta(rule)) continue;
      if (Rule.isFake(rule)) continue;
      if (G.generateRootRules != null && !G.generateRootRules.matcher(ruleName).matches()) continue;
      String elementType = getElementType(rule);
      out("%sif (%s == %s) {", first ? "" : "else ", N.root, elementType);
      String nodeCall = generateNodeCall(rule, null, ruleName);
      out("%s = %s;", N.result, nodeCall.replace(format("%s + 1", N.level), "0"));
      out("}");
      if (first) first = false;
    }
    {
      if (!first) out("else {");
      out("%s = parse_root_(%s, %s, 0);", N.result, N.root, N.builder);
      if (!first) out("}");
    }
    out("exit_section_(%s, 0, %s, %s, %s, true, TRUE_CONDITION);", N.builder, N.marker, N.root, N.result);
    out("}");
    newLine();
    {
      BnfRule rootRule = myFile.getRule(myGrammarRoot);
      String nodeCall = generateNodeCall(rootRule, null, myGrammarRoot);
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
    List<Set<String>> result = ContainerUtil.newArrayList();
    for (Map.Entry<BnfRule, Collection<BnfRule>> entry : map.entrySet()) {
      Set<String> set = null;
      for (BnfRule rule : entry.getValue()) {
        String elementType = Rule.isFake(rule) || getSynonymTargetOrSelf(rule) != rule ? null : getElementType(rule);
        if (StringUtil.isEmpty(elementType)) continue;
        if (set == null) set = ContainerUtil.newTreeSet();
        set.add(elementType);
      }
      if (set != null && set.size() > 1) result.add(set);
    }
    Collections.sort(result, new Comparator<Set<?>>() {
      @Override
      public int compare(Set<?> o1, Set<?> o2) {
        return o1.size() - o2.size();
      }
    });
    return result;
  }

  private void generateClassHeader(String className,
                                   Collection<String> imports,
                                   String annos,
                                   boolean intf,
                                   String... supers) {
    generateFileHeader(className);
    String packageName = StringUtil.getPackageName(className);
    String shortClassName = StringUtil.getShortName(className);
    out("package %s;", packageName);
    newLine();
    Set<String> realImports = ContainerUtil.newLinkedHashSet(packageName + ".*");
    Function<String, String> shortener = newClassNameShortener(realImports);
    for (String item : imports) {
      for (String s : StringUtil.tokenize(item.replaceAll("\\s+", " "), TYPE_TEXT_SEPARATORS)) {
        s = StringUtil.trimStart(StringUtil.trimStart(s, "? super "), "? extends ");
        if (!s.contains(".") || !s.equals(shortener.fun(s))) continue;
        if (myPackageClasses.contains(StringUtil.getShortName(s))) continue;
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
    out("public %s%s%s {", intf ? "interface " : "class ", shortClassName, sb.toString());
    newLine();
    myShortener = shortener;
  }

  @NotNull
  private static Function<String, String> newClassNameShortener(final Set<String> realImports) {
    return new Function<String, String>() {
      @Override
      public String fun(String s) {
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
      }
    };
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
    BnfExpression node = getNonTrivialNode(initialNode);

    IElementType type = getEffectiveType(node);

    for (String s : StringUtil.split((StringUtil.isEmpty(node.getText()) ? initialNode : node).getText(), "\n")) {
      out("// " + s);
    }
    boolean firstNonTrivial = node == Rule.firstNotTrivial(rule);
    boolean isPrivate = !(isRule || firstNonTrivial) || Rule.isPrivate(rule) || myGrammarRoot.equals(rule.getName());
    boolean isLeft = firstNonTrivial && Rule.isLeft(rule);
    boolean isLeftInner = isLeft && (isPrivate || Rule.isInner(rule));
    boolean isBranch = !isPrivate && Rule.isUpper(rule);
    final String recoverWhile = firstNonTrivial ? ObjectUtils.chooseNotNull(
      getAttribute(rule, KnownAttribute.RECOVER_WHILE.alias("recoverUntil")),
      getAttribute(rule, KnownAttribute.RECOVER_WHILE)) : null;

    final boolean canCollapse = !isPrivate && (!isLeft || isLeftInner) && firstNonTrivial && myGraphHelper.canCollapse(rule);

    String elementType = getElementType(rule);

    final List<BnfExpression> children;
    String extraArguments = collectExtraArguments(rule, node, true);
    out("%sstatic boolean %s(PsiBuilder %s, int %s%s) {", !isRule ? "private " : isPrivate ? "" : "public ", funcName, N.builder, N.level, extraArguments);
    if (node instanceof BnfReferenceOrToken || node instanceof BnfLiteralExpression || node instanceof BnfExternalExpression) {
      children = Collections.singletonList(node);
      if (isPrivate && !isLeftInner && recoverWhile == null) {
        String nodeCall = generateNodeCall(rule, node, getNextName(funcName, 0));
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
    else {
      children = getChildExpressions(node);
      //if (children.isEmpty() && recoverWhile == null) {
      //  if (!isPrivate && !StringUtil.isEmpty(elementType)) {
      //    if (isLeft || isLeftInner) {
      //      out("Marker %s = enter_section_(%s, %s, %s, %s);", N.marker, N.builder, N.level, isLeftInner ? "_LEFT_INNER_" : "_LEFT_", null);
      //      out("exit_section_(%s, %s, %s, %s, %s, %s, %s);", N.builder, N.level, N.marker, elementType, true, false, null);
      //    }
      //    else {
      //      out("Marker %s = enter_section_(%s);", N.marker, N.builder);
      //      out("exit_section_(%s, %s, %s, %s);", N.builder, N.marker, elementType, true);
      //    }
      //  }
      //  out("return true;");
      //  out("}");
      //  return;
      //}
    }
    if (!children.isEmpty()) {
      out("if (!recursion_guard_(%s, %s, \"%s\")) return false;", N.builder, N.level, funcName);
    }

    String frameName = !children.isEmpty() && firstNonTrivial && !Rule.isMeta(rule)? quote(getRuleDisplayName(rule, !isPrivate)) : null;
    if (recoverWhile == null && (isRule || firstNonTrivial)) {
      frameName = generateFirstCheck(rule, frameName, true);
    }

    PinMatcher pinMatcher = new PinMatcher(rule, type, firstNonTrivial ? rule.getName() : funcName);
    boolean pinApplied = false;
    final boolean alwaysTrue = children.isEmpty() || (type == BNF_OP_OPT || type == BNF_OP_ZEROMORE);
    boolean pinned = pinMatcher.active() && pinMatcher.shouldGenerate(children);
    if (!alwaysTrue) {
      boolean value = type == BNF_OP_ZEROMORE || type == BNF_OP_OPT || children.isEmpty();
      out("boolean %s%s%s;", N.result, children.isEmpty() || type == BNF_OP_ZEROMORE ? " = " + value : "",
          pinned ? format(", %s", N.pinned) : "");
    }

    List<String> modifierList = ContainerUtil.newSmartList();
    if (canCollapse) modifierList.add("_COLLAPSE_");
    if (isLeftInner) modifierList.add("_LEFT_INNER_");
    else if (isLeft) modifierList.add("_LEFT_");
    if (type == BNF_OP_AND) modifierList.add("_AND_");
    else if (type == BNF_OP_NOT) modifierList.add("_NOT_");
    if (isBranch) modifierList.add("_UPPER_");
    if (modifierList.isEmpty() && (pinned || frameName != null)) modifierList.add("_NONE_");
    boolean sectionRequired = !alwaysTrue || !isPrivate || isLeft || recoverWhile != null;
    boolean sectionRequiredSimple = sectionRequired && modifierList.isEmpty() && recoverWhile == null;
    String elementTypeRef = !isPrivate && StringUtil.isNotEmpty(elementType) ? elementType : null;
    String modifiers = modifierList.isEmpty()? "_NONE_" : StringUtil.join(modifierList, " | ");
    if (sectionRequiredSimple) {
      out("Marker %s = enter_section_(%s);", N.marker, N.builder);
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

    ConsumeType consumeType = ConsumeType.forRule(rule);
    boolean predicateEncountered = false;
    int[] skip = {0};
    for (int i = 0, p = 0, childrenSize = children.size(); i < childrenSize; i++) {
      BnfExpression child = children.get(i);

      String nodeCall = generateNodeCall(rule, child, getNextName(funcName, i));
      if (type == BNF_CHOICE) {
        out("%s%s = %s;", i > 0 ? format("if (!%s) ", N.result) : "", N.result, nodeCall);
      }
      else if (type == BNF_SEQUENCE) {
        predicateEncountered |= pinApplied && getEffectiveExpression(myFile, child) instanceof BnfPredicate;
        if (skip[0] == 0) {
          nodeCall = generateTokenSequenceCall(children, i, pinMatcher, pinApplied, skip, nodeCall, false, consumeType);
          if (i == 0) {
            out("%s = %s;", N.result, nodeCall);
          }
          else {
            if (pinApplied && G.generateExtendedPin && !predicateEncountered) {
              if (i == childrenSize - 1) {
                // do not report error for last child
                if (i == p + 1) {
                  out("%s = %s && %s;", N.result, N.result, nodeCall);
                }
                else {
                  out("%s = %s && %s && %s;", N.result, N.pinned, nodeCall, N.result);
                }
              }
              else if (i == p + 1) {
                out("%s = %s && report_error_(%s, %s);", N.result, N.result, N.builder, nodeCall);
              }
              else {
                out("%s = %s && report_error_(%s, %s) && %s;", N.result, N.pinned, N.builder, nodeCall, N.result);
              }
            }
            else {
              out("%s = %s && %s;", N.result, N.result, nodeCall);
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
        out(nodeCall + ";");
      }
      else if (type == BNF_OP_ONEMORE || type == BNF_OP_ZEROMORE) {
        if (type == BNF_OP_ONEMORE) {
          out("%s = %s;", N.result, nodeCall);
        }
        out("int %s = current_position_(%s);", N.pos, N.builder);
        out("while (%s) {", alwaysTrue ? "true" : N.result);
        out("if (!%s) break;", nodeCall);
        out("if (!empty_element_parsed_guard_(%s, \"%s\", %s)) break;", N.builder, funcName, N.pos);
        out("%s = current_position_(%s);", N.pos, N.builder);
        out("}");
      }
      else if (type == BNF_OP_AND) {
        out("%s = %s;", N.result, nodeCall);
      }
      else if (type == BNF_OP_NOT) {
        out("%s = !%s;", N.result, nodeCall);
      }
      else {
        addWarning(myFile.getProject(), "unexpected: " + type);
      }
    }

    if (sectionRequired) {
      String resultRef = alwaysTrue ? "true" : N.result;
      if (sectionRequiredSimple) {
        out("exit_section_(%s, %s, %s, %s);", N.builder, N.marker, elementTypeRef, resultRef);
      }
      else {
        String pinnedRef = pinned ? N.pinned : "false";
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
        out("exit_section_(%s, %s, %s, %s, %s, %s);", N.builder, N.level, N.marker, resultRef, pinnedRef, recoverCall);
      }
    }

    out("return %s;", alwaysTrue ? "true" : N.result + (pinned ? format(" || %s", N.pinned) : ""));
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
    StringBuilder sb = new StringBuilder(format("!nextTokenIsFast(%s, ", N.builder));

    appendTokenTypes(sb, tokenTypes);
    sb.append(")");

    String constantName = rule.getName() + "_auto_recover_";
    myParserLambdas.put(constantName, "#" + sb.toString());
    return constantName;
  }

  public String generateFirstCheck(BnfRule rule, String frameName, boolean skipIfOne) {
    if (G.generateFirstCheck <= 0) return frameName;
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
    boolean dropFrameName = skipIfOne && firstElementTypes.size() == 1;
    if (!firstElementTypes.isEmpty() && firstElementTypes.size() <= G.generateFirstCheck) {
      StringBuilder sb = new StringBuilder("if (!");
      sb.append(fast ? "nextTokenIs" + consumeType.getMethodSuffix() : "nextTokenIs").append("(").append(N.builder).append(", ");
      if (!fast && !dropFrameName) sb.append(frameName != null ? frameName : "\"\"").append(", ");

      appendTokenTypes(sb, firstElementTypes);
      sb.append(")) return false;");
      out(sb.toString());
    }
    return dropFrameName && StringUtil.isEmpty(getAttribute(rule, KnownAttribute.NAME))? null : frameName;
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
    else if (GrammarUtil.isAtomicExpression(child) || isTokenSequence(rule, child)) {
      // do not generate
    }
    else {
      newLine();
      generateNode(rule, child, getNextName(funcName, index), visited);
    }
  }

  private String collectExtraArguments(BnfRule rule, @Nullable BnfExpression expression, boolean declaration) {
    if (expression == null) return "";
    List<String> params = GrammarUtil.collectExtraArguments(rule, expression);
    if (params.isEmpty()) return "";
    final StringBuilder sb = new StringBuilder();
    for (String param : params) {
      sb.append(", ").append(declaration ? "final Parser " : "").
        append(formatArgName(param.substring(2, param.length() - 2)));
    }
    return sb.toString();
  }

  private String formatArgName(String s) {
    String argName = s.trim();
    return N.argPrefix + (N.argPrefix.isEmpty() || "_".equals(N.argPrefix) ? argName : StringUtil.capitalize(argName));
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
    String consumeMethodName = (rollbackOnFail ? "parseTokens" : "consumeTokens") + (consumeType == ConsumeType.SMART ? consumeType.getMethodSuffix() : "");
    return format("%s(%s, %d, %s)", consumeMethodName, N.builder, pin, StringUtil.join(list, ", "));
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
          return format("%s(%s, %s + 1%s)", method, N.builder, N.level, clause);
        }
        else {
          ExpressionHelper.ExpressionInfo info = ExpressionGeneratorHelper.getInfoForExpressionParsing(myExpressionHelper, subRule);
          method = info != null ? info.rootRule.getName() : subRule.getName();
          String parserClass = myRuleParserClasses.get(method);
          if (!parserClass.equals(myGrammarRootParser) && !parserClass.equals(myRuleParserClasses.get(rule.getName()))) {
            method = StringUtil.getShortName(parserClass) + "." + method;
          }
          if (info == null) {
            return format("%s(%s, %s + 1)", method, N.builder, N.level);
          }
          else {
            return format("%s(%s, %s + 1, %d)", method, N.builder, N.level, info.getPriority(subRule) - 1);
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
        return format("%s.parse(%s, %s)", formatArgName(expressions.get(0).getText()), N.builder, N.level);
      }
      else {
        StringBuilder clause = new StringBuilder();
        String method = generateExternalCall(rule, clause, expressions, nextName);
        return format("%s(%s, %s + 1%s)", method, N.builder, N.level, clause);
      }
    }
    else {
      String extraArguments = collectExtraArguments(rule, node, false);
      return format("%s(%s, %s + 1%s)", nextName, N.builder, N.level, extraArguments);
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
            clause.append(formatArgName(expressionList.get(0).getText()));
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
    myTokensUsedInGrammar.add(tokenName);
    return format("%s(%s, %s)", consumeMethodName, N.builder, getTokenElementType(tokenName));
  }

  public String generateConsumeTextToken(String tokenText, String consumeMethodName) {
    return format("%s(%s, \"%s\")", consumeMethodName, N.builder, tokenText);
  }

  private String getTokenElementType(String token) {
    return getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_PREFIX) + G.generateTokenCase.apply(token);
  }

  String getElementType(BnfRule r) {
    return ParserGeneratorUtil.getElementType(r,  G.generateElementCase);
  }

  /*ElementTypes******************************************************************/

  private void generateElementTypesHolder(String className, Map<String, BnfRule> sortedCompositeTypes) {
    String implPackage = getPsiImplPackage(myFile);
    String tokenTypeClass = getRootAttribute(myFile, KnownAttribute.TOKEN_TYPE_CLASS);
    String tokenTypeFactory = getRootAttribute(myFile, KnownAttribute.TOKEN_TYPE_FACTORY);
    Set<String> imports = new LinkedHashSet<String>();
    imports.add(BnfConstants.IELEMENTTYPE_CLASS);
    if (G.generatePsi) {
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
    if (G.generatePsi) imports.add(implPackage + ".*");
    generateClassHeader(className, imports, "", true);
    if (G.generateElementTypes) {
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
    }
    if (G.generateTokenTypes) {
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
        if (isIgnoredWhitespaceToken(tokenName, tokenText)) continue;
        sortedTokens.put(getTokenElementType(tokenName), isRegexpToken(tokenText) ? tokenName : tokenText);
      }
      for (String tokenType : sortedTokens.keySet()) {
        String callFix = tokenCreateCall.equals("new IElementType") ? ", null" : "";
        String tokenString = sortedTokens.get(tokenType);
        out("IElementType " + tokenType + " = " + tokenCreateCall + "(\"" + StringUtil.escapeStringCharacters(tokenString) + "\""+callFix+");");
      }
    }
    if (G.generatePsi && G.generatePsiFactory) {
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

  private boolean isIgnoredWhitespaceToken(@NotNull String tokenName, @NotNull String tokenText) {
    return isRegexpToken(tokenText) &&
           !myTokensUsedInGrammar.contains(tokenName) &&
           matchesAny(getRegexpTokenRegexp(tokenText), " ", "\n") &&
           !matchesAny(getRegexpTokenRegexp(tokenText), "a", "1", "_", ".");
  }


  /*PSI******************************************************************/
  private void generatePsiIntf(BnfRule rule,
                               String psiClass,
                               Collection<String> psiSupers) {
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
    JavaHelper javaHelper = JavaHelper.getJavaHelper(myFile);
    imports.addAll(getRuleMethodTypesToImport(rule, psiImplUtilClass, javaHelper));

    generateClassHeader(psiClass, imports, "", true, ArrayUtil.toStringArray(psiSupers));
    generatePsiClassMethods(rule, psiImplUtilClass, javaHelper, true);
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
    JavaHelper javaHelper = JavaHelper.getJavaHelper(myFile);
    imports.addAll(getRuleMethodTypesToImport(rule, psiImplUtilClass, javaHelper));

    String stubName = StringUtil.isNotEmpty(stubClass)? stubClass :
                      implSuper.indexOf("<") < implSuper.indexOf(">") &&
                      !javaHelper.findClassMethods(implSuper.substring(0, implSuper.indexOf("<")), false, "StubBasedPsiElementBase", 2).isEmpty()
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
      String r = G.visitorValue != null ? "<" + G.visitorValue + ">" : "";
      String t = G.visitorValue != null ? " " + G.visitorValue : "void";
      String ret = G.visitorValue != null ? "return " : "";
      out("public " + r + t + " accept(@NotNull " + visitorClassName + r + " visitor) {");
      out(ret + "visitor.visit" + getRulePsiClassName(rule, "") + "(this);");
      out("}");
      newLine();
      out("public void accept(@NotNull " + myShortener.fun(BnfConstants.PSI_ELEMENT_VISITOR_CLASS) + " visitor) {");
      out("if (visitor instanceof " + visitorClassName + ") accept((" + visitorClassName + ")visitor);");
      out("else super.accept(visitor);");
      out("}");
      newLine();
    }
    generatePsiClassMethods(rule, psiImplUtilClass, javaHelper, false);
    out("}");
  }

  private void generatePsiClassMethods(BnfRule rule,
                                       String psiImplUtilClass,
                                       JavaHelper javaHelper,
                                       boolean intf) {
    Set<String> visited = ContainerUtil.newTreeSet();
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
          if (intf) {
            String mixinClass = getAttribute(rule, KnownAttribute.MIXIN);
            List<NavigatablePsiElement> methods = javaHelper.findClassMethods(mixinClass, false, methodInfo.name, -1);
            for (NavigatablePsiElement method : methods) {
              generateUtilMethod(methodInfo.name, method, null, false, javaHelper, visited);
            }
          }
          List<NavigatablePsiElement> methods = findRuleImplMethods(javaHelper, psiImplUtilClass, methodInfo.name, rule);
          for (NavigatablePsiElement method : methods) {
            generateUtilMethod(methodInfo.name, method, intf ? null : psiImplUtilClass, true, javaHelper, visited);
          }
          break;
        default: throw new AssertionError(methodInfo.toString());
      }
    }
  }

  public Collection<String> getRuleMethodTypesToImport(BnfRule rule,
                                                       String psiImplUtilClass,
                                                       JavaHelper javaHelper) {
    Set<String> result = ContainerUtil.newTreeSet();

    Collection<RuleMethodsHelper.MethodInfo> methods = myRulesMethodsHelper.getFor(rule);
    for (RuleMethodsHelper.MethodInfo methodInfo : methods) {
      if (methodInfo.rule != null) {
        result.add(getAccessorType(methodInfo.rule));
      }
    }

    for (RuleMethodsHelper.MethodInfo methodInfo : methods) {
      if (methodInfo.type != 4) continue;

      String mixinClass = getAttribute(rule, KnownAttribute.MIXIN);
      List<NavigatablePsiElement> mixinMethods = javaHelper.findClassMethods(mixinClass, false, methodInfo.name, -1);
      List<NavigatablePsiElement> implMethods = findRuleImplMethods(javaHelper, psiImplUtilClass, methodInfo.name, rule);

      collectMethodTypesToImport(javaHelper, mixinMethods, false, result);
      collectMethodTypesToImport(javaHelper, implMethods, true, result);
    }
    return result;
  }

  private static void collectMethodTypesToImport(JavaHelper javaHelper,
                                                 List<NavigatablePsiElement> methods,
                                                 boolean isInPsiUtil,
                                                 Set<String> result) {
    for (NavigatablePsiElement method : methods) {
      int count = 0;
      List<String> types = javaHelper.getMethodTypes(method);
      for (String s : types) {
        if (count++ == 1 && isInPsiUtil) continue;
        if (s.contains(".")) result.add(s);
      }
      for (String s : javaHelper.getAnnotations(method)) {
        result.add(s);
      }
    }
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
    if (Rule.isExternal(rule)) {
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
                                  NavigatablePsiElement method,
                                  String psiImplUtilClass,
                                  boolean isInPsiUtil,
                                  JavaHelper javaHelper,
                                  Set<String> visited) {
    boolean intf = psiImplUtilClass == null;
    List<String> methodTypes = method == null ? Collections.<String>emptyList() : javaHelper.getMethodTypes(method);
    String returnType = methodTypes.isEmpty()? "void" : myShortener.fun(methodTypes.get(0));
    List<String> paramsTypes = methodTypes.isEmpty() || isInPsiUtil && methodTypes.size() < 3
                               ? Collections.<String>emptyList() : methodTypes.subList(isInPsiUtil ? 3 : 1, methodTypes.size());
    if (!visited.add(methodName + paramsTypes)) return;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < paramsTypes.size(); i += 2) {
      if (i > 0) sb.append(", ");
      sb.append(myShortener.fun(paramsTypes.get(i))).append(" ").append(paramsTypes.get(i + 1));
    }

    for (String s : javaHelper.getAnnotations(method)) {
      if ("java.lang.Override".equals(s)) continue;
      out("@" + myShortener.fun(s));
    }
    out("%s%s %s(%s)%s", intf ? "" : "public ", returnType, methodName, sb, intf ? ";" : " {");
    if (!intf) {
      sb.setLength(0);
      for (int i = 1; i < paramsTypes.size(); i += 2) {
        sb.append(", ").append(paramsTypes.get(i));
      }
      String implUtilRef = myShortener.fun(StringUtil.notNullize(psiImplUtilClass, KnownAttribute.PSI_IMPL_UTIL_CLASS.getName()));
      out("%s%s.%s(this%s);", "void".equals(returnType) ? "" : "return ", implUtilRef, methodName, sb);
      out("}");
    }
    newLine();
  }
}
