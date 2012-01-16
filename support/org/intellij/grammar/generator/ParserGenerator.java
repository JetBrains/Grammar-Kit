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
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringHash;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.MultiMap;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality.*;
import static org.intellij.grammar.psi.BnfTypes.*;


/**
 * @author gregory
 *         Date: 16.07.11 10:41
 *
 * todo improve error reporting: investigate { atttr= attr=value } case *
 */
public class ParserGenerator {
  public static final Logger LOG = Logger.getInstance("ParserGenerator");

  public static final String IELEMENTTYPE_CLASS = "com.intellij.psi.tree.IElementType";
  public static final String PSI_ELEMENT_CLASS = "com.intellij.psi.PsiElement";
  private static final String DEFAULT_FILE_HEADER = "// This is a generated file. Not intended for manual editing.";

  private final Map<String, BnfRule> ruleMap = new TreeMap<String, BnfRule>();
  private final Map<String, String> ruleParserClasses = new TreeMap<String, String>();
  private final Map<String, String> parserLambdas = new TreeMap<String, String>();
  private final Set<String> simpleTokens = new TreeSet<String>();
  private final Set<BnfRule> rulesWithIheritance = new HashSet<BnfRule>();
  private final MultiMap<String, String> rulesExtendsMap = new MultiMap<String, String>() {
    @Override
    protected Collection<String> createCollection() {
      return new LinkedHashSet<String>();
    }
  };
  private final BnfFile treeRoot;
  private String rootPath;
  private final String grammarRoot;
  private final boolean generateMemoizationCode;
  private final boolean generateExtendedPin;

  private int offset;
  private PrintWriter out;

  public ParserGenerator(BnfFile tree, String path) {
    treeRoot = tree;
    rootPath = path;
    final List<BnfRule> rules = tree.getRules();
    grammarRoot = rules.isEmpty() ? null : rules.get(0).getName();
    for (BnfRule r : rules) {
      ruleMap.put(r.getName(), r);
      ruleParserClasses.put(r.getName(), getAttribute(r, "parserClass", "generated.Parser"));
    }
    generateMemoizationCode = getRootAttribute(treeRoot, "memoization", false);
    generateExtendedPin = getRootAttribute(treeRoot, "extendedPin", true);
    computeInheritance();
  }

  public void out(String s) {
    int length = s.length();
    if (length == 0) {
      out.println();
      return;
    }
    boolean isComment = s.startsWith("//");
    for (int start = 0, end; start < length; start = end + 1) {
      end = StringUtil.indexOf(s, '\n', start, length);
      if (end == -1) end = length;
      String substring = s.substring(start, end);
      if (!isComment && substring.startsWith("}")) offset--;
      if (offset > 0) {
        out.print(StringUtil.repeat("  ", start == 0 ? offset : offset + 1));
      }
      if (!isComment && substring.endsWith("{")) offset++;
      out.println(substring);
    }
  }

  private void newLine() {
    out("");
  }

  public void generate() throws IOException {
    {
      generateParser();
    }
    boolean generatePsi = getRootAttribute(treeRoot, "generatePsi", true);
    {
      String className = getRootAttribute(treeRoot, "elementTypeHolderClass", "generated.ParserTypes");
      File parserFile = new File(rootPath + File.separatorChar + className.replace('.', File.separatorChar) + ".java");
      parserFile.getParentFile().mkdirs();
      out = new PrintWriter(new FileOutputStream(parserFile));
      try {
        generateElementTypesHolder(className, generatePsi);
      }
      finally {
        out.close();
      }
    }
    if (generatePsi) {
      Map<String, String> infClasses = new HashMap<String, String>();
      RuleGraphHelper graphHelper = new RuleGraphHelper(ruleMap);
      for (String ruleName : ruleMap.keySet()) {
        BnfRule rule = ruleMap.get(ruleName);
        if (Rule.isPrivate(rule) || Rule.isExternal(rule)) continue;
        String psiPackage = getRootAttribute(treeRoot, "psiPackage", "generated.psi");
        String psiClass = psiPackage + "." + getRulePsiClassName(rule, ruleName, true);
        infClasses.put(ruleName, psiClass);
        File psiFile = new File(rootPath + File.separatorChar + psiClass.replace('.', File.separatorChar) + ".java");
        psiFile.getParentFile().mkdirs();
        out = new PrintWriter(new FileOutputStream(psiFile));
        try {
          generatePsiIntf(graphHelper, rule, psiClass, getSuperInterfaceNames(rule, psiPackage));
        }
        finally {
          out.close();
        }
      }
      for (String ruleName : ruleMap.keySet()) {
        BnfRule rule = ruleMap.get(ruleName);
        if (Rule.isPrivate(rule) || Rule.isExternal(rule)) continue;
        String psiPackage = getRootAttribute(treeRoot, "psiImplPackage", "generated.psi.impl");
        String suffix = getRootAttribute(treeRoot, "psiImplClassSuffix", "Impl");
        String psiClass = psiPackage + "." + getRulePsiClassName(rule, ruleName, true) + suffix;
        File psiFile = new File(rootPath + File.separatorChar + psiClass.replace('.', File.separatorChar) + ".java");
        psiFile.getParentFile().mkdirs();
        out = new PrintWriter(new FileOutputStream(psiFile));
        try {
          generatePsiImpl(graphHelper, rule, psiClass, infClasses.get(ruleName), getSuperClassName(rule, psiPackage, suffix));
        }
        finally {
          out.close();
        }
      }
    }
    boolean generateUtil = getRootAttribute(treeRoot, "generateStubParser", true);
    if (generateUtil) {
      generateParserUtil();
    }
    
  }

  private void generateParserUtil() throws IOException {
    final String stubParser = getRootAttribute(treeRoot, "stubParserClass", "generated.ParserUtil");
    final String stubPackage = StringUtil.getPackageName(stubParser);
    String baseClassName = GeneratedParserUtilBase.class.getSimpleName();
    String baseFileName = baseClassName + ".java";
    InputStream baseUtilText = GeneratedParserUtilBase.class.getResourceAsStream(baseFileName);
    if (baseUtilText == null) return;

    Project project = treeRoot.getProject();
    if (JavaPsiFacade.getInstance(project).findClass(stubParser, GlobalSearchScope.allScope(project)) != null) return;
    File baseFile = new File(rootPath + File.separatorChar + stubPackage.replace('.', File.separatorChar) + "/" + baseFileName);
    out = new PrintWriter(new FileOutputStream(baseFile));
    try {
      String text = FileUtil.loadTextAndClose(baseUtilText);
      text = text.replace("package " + StringUtil.getPackageName(GeneratedParserUtilBase.class.getName())+";", "package "+stubPackage+";");
      generateFileHeader(baseClassName);
      out.write(text);
    }
    finally {
      out.close();
    }
    File utilFile = new File(rootPath + File.separatorChar + stubParser.replace('.', File.separatorChar) + ".java");
    out = new PrintWriter(new FileOutputStream(utilFile));
    try {
      generateClassHeader(stubParser, "", "", false, baseClassName);
      newLine();
      out("}");
    }
    finally {
      out.close();
    }
  }


  public void generateParser() throws FileNotFoundException {
    for (String className : new TreeSet<String>(ruleParserClasses.values())) {
      Map<String, BnfRule> map = new TreeMap<String, BnfRule>();
      for (String ruleName : ruleMap.keySet()) {
        if (className.equals(ruleParserClasses.get(ruleName))) {
          map.put(ruleName, ruleMap.get(ruleName));
        }
      }
      File parserFile = new File(rootPath + File.separatorChar + className.replace('.', File.separatorChar) + ".java");
      parserFile.getParentFile().mkdirs();
      out = new PrintWriter(new FileOutputStream(parserFile));
      try {
        generateParser(className, map);
      }
      finally {
        out.close();
      }
    }
  }

  @NotNull
  private String getSuperClassName(BnfRule rule, String psiPackage, String suffix) {
    String superRuleName = getAttribute(rule, "extends", "generated.CompositeElementImpl");
    BnfRule superRule = superRuleName == null ? null : ruleMap.get(superRuleName);
    if (superRule == null) return superRuleName;
    return psiPackage + "." + getRulePsiClassName(superRule, superRule.getName(), true) + suffix;
  }

  @NotNull
  private String[] getSuperInterfaceNames(BnfRule rule, String psiPackage) {
    ArrayList<String> strings = new ArrayList<String>();
    String superRuleImplements = "";
    {
      String superRuleName = getAttribute(rule, "extends", null);
      BnfRule superRule = superRuleName == null ? null : ruleMap.get(superRuleName);
      if (superRule != null) {
        superRuleImplements = getAttribute(superRule, "implements", "generated.CompositeElement");
        strings.add(psiPackage + "." + getRulePsiClassName(superRule, superRule.getName(), true));
      }
    }
    String[] superIntfNames = getAttribute(rule, "implements", "generated.CompositeElement").split(",");
    for (String superIntfName : superIntfNames) {
      BnfRule superIntfRule = ruleMap.get(superIntfName);
      if (superIntfRule != null) {
        strings.add(psiPackage + "." + getRulePsiClassName(superIntfRule, superIntfRule.getName(), true));
      }
      else if (!superRuleImplements.contains(superIntfName)) {
        strings.add(superIntfName);
      }
    }
    return strings.toArray(new String[strings.size()]);
  }

  private static String getRulePsiClassName(BnfRule rule, String ruleName, boolean withPrefix) {
    StringBuilder sb = new StringBuilder();
    if (withPrefix) {
      sb.append(getAttribute(rule, "psiClassPrefix", ""));
    }
    for (String s : ruleName.split("_")) {
      if (s.length() == 0) continue;
      sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1).toLowerCase());
    }
    return sb.toString();
  }

  public void generateParser(String parserClass, Map<String, BnfRule> ruleMap) {
    final String elementTypeHolderClass = getRootAttribute(treeRoot, "elementTypeHolderClass", "generated.ParserTypes");
    final String stubParser = getRootAttribute(treeRoot, "stubParserClass", "generated.ParserUtil");
    final String parserImports = getRootAttribute(treeRoot, "parserImports", "");
    String rootParserClass = ruleParserClasses.get(grammarRoot);
    boolean rootParser = parserClass.equals(rootParserClass);
    generateClassHeader(parserClass,
                        "org.jetbrains.annotations.*;" +
                        "com.intellij.lang.LighterASTNode;" +
                        "com.intellij.lang.PsiBuilder;" +
                        "com.intellij.lang.PsiBuilder.Marker;" +
                        "com.intellij.openapi.diagnostic.Logger;" +
                        "static " + elementTypeHolderClass + ".*;" +
                        "static " + stubParser + ".*;" +
                        (!rootParser ? "static " + rootParserClass + ".*;" :
                         IELEMENTTYPE_CLASS + ";" +
                         "com.intellij.lang.ASTNode;" +
                         "com.intellij.psi.tree.TokenSet;" +
                         "com.intellij.lang.PsiParser;") +
                        parserImports
      , "@SuppressWarnings({\"SimplifiableIfStatement\", \"UnusedAssignment\"})",
                        false, "", rootParser ? "PsiParser" : "");

    out("public static Logger LOG_ = Logger.getInstance(\"" + parserClass + "\");");
    newLine();

    if (rootParser) {
      generateRootParserContent(ruleMap.keySet());
    }
    for (String ruleName : ruleMap.keySet()) {
      BnfRule rule = ruleMap.get(ruleName);
      if (Rule.isExternal(rule)) continue;
      out("/* ********************************************************** */");
      generateNode(rule, rule.getExpression(), ruleName, new HashSet<BnfExpression>());
      newLine();
    }
    Map<String, String> reversedLambdas = new THashMap<String, String>(); 
    for (Map.Entry<String, String> e : parserLambdas.entrySet()) {
      String body = e.getValue();
      if (body.startsWith("#")) {
        String name = e.getKey();
        String value = reversedLambdas.get(body);
        if (value == null) {
          value = wrapCall(body.substring(1));
          reversedLambdas.put(body, name);
        }
        out("final static Parser " + name + " = " + value + ";");
        e.setValue(StringUtil.getShortName(parserClass)+"."+ name);
      }
    }
    out("}");
  }

  private void generateRootParserContent(Set<String> ownRuleNames) {
    out("@NotNull");
    out("public ASTNode parse(final IElementType root_, final PsiBuilder builder_) {");
    out("int level_ = 0;");
    out("boolean result_;");
    boolean first = true;
    for (String ruleName : ownRuleNames) {
      BnfRule rule = ruleMap.get(ruleName);
      if (Rule.isPrivate(rule) || Rule.isExternal(rule) || Rule.isMeta(rule) || grammarRoot.equals(ruleName)) continue;
      String elementType = getElementType(rule);
      out((first ? "" : "else ") + "if (root_ == " + elementType + ") {");
      String nodeCall = generateNodeCall(rule, null, ruleName);
      out("result_ = " + nodeCall + ";");
      out("}");
      if (first) first = false;
    }
    {
      BnfRule rootRule = ruleMap.get(grammarRoot);
      String nodeCall = generateNodeCall(rootRule, null, rootRule.getName());
      if (!first) out("else {");
      out("Marker marker_ = builder_.mark();");
      //out("try {");
      out("result_ = " + nodeCall + ";");
      out("while (builder_.getTokenType() != null) {");
      out("builder_.advanceLexer();");
      out("}");
      //out("}");
      //out("finally {");
      out("marker_.done(root_);");
      //out("}");
      if (!first) out("}");
    }
    out("return builder_.getTreeBuilt();");
    out("}");
    newLine();
    if (!rulesExtendsMap.isEmpty()) {
      out("private static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {");
      for (String ruleName : rulesExtendsMap.keySet()) {
        Collection<String> strings = rulesExtendsMap.get(ruleName);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String string : strings) {
          if (i > 0 && i % 4 == 0) {
            sb.append(",\n");
          }
          else if (i > 0) sb.append(", ");
          sb.append(string);
          i++;
        }
        out("TokenSet.create(" + sb.toString() + "),");
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

  private void computeInheritance() {
    final Set<String> publicRules = new THashSet<String>();
    for (String ruleName : ruleMap.keySet()) {
      BnfRule rule = ruleMap.get(ruleName);
      if (Rule.isPrivate(rule) || Rule.isExternal(rule)) continue;
      BnfRule superRule = ruleMap.get(getAttribute(rule, "extends", ""));
      String elementType = getElementType(rule);
      publicRules.add(elementType);
      if (superRule == null) continue;
      rulesExtendsMap.putValue(getElementType(superRule), elementType);

      rulesWithIheritance.add(rule);
      rulesWithIheritance.add(superRule);
    }

    for (int i = 0, len = rulesExtendsMap.size(); i < len; i++) {
      boolean changed = false;
      for (String parent : rulesExtendsMap.keySet()) {
        final Collection<String> strings = rulesExtendsMap.get(parent);
        for (String child : new ArrayList<String>(strings)) {
          changed = strings.addAll(rulesExtendsMap.get(child));
        }
      }
      if (!changed) break;
    }
    for (String ruleName : rulesExtendsMap.keySet()) {
      if (!publicRules.contains(ruleName)) continue;
      rulesExtendsMap.putValue(ruleName, ruleName); // add super to itself
    }
  }

  private void generateClassHeader(String className, String imports, String annos, boolean intf, String... supers) {
    generateFileHeader(className);
    final String packageName = StringUtil.getPackageName(className);
    out("package " + packageName + ";");
    newLine();
    for (String s : imports.split(";")) {
      if (s.startsWith(packageName + ".") && s.indexOf(".", packageName.length() + 1) == -1) {
        continue;
      }
      if (!s.contains(".")) continue;
      out("import " + s + ";");
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
        sb.append(" extends ").append(aSuper);
      }
      else if (!intf && i == 1) {
        sb.append(" implements ").append(aSuper);
      }
      else {
        sb.append(", ").append(aSuper);
      }
    }
    if (StringUtil.isNotEmpty(annos)) {
      out(annos);
    }
    out("public " + (intf ? "interface " : "class ") + StringUtil.getShortName(className) + sb.toString() + " {");
    newLine();
  }

  private void generateFileHeader(String className) {
    final String classHeader = getStringOrFile(getRootAttribute(treeRoot, "classHeader", DEFAULT_FILE_HEADER, className));
    out(classHeader);
    offset = 0;
  }

  private String getStringOrFile(String classHeader) {
    final File file = new File(VfsUtil.virtualToIoFile(treeRoot.getVirtualFile()).getParentFile(), classHeader);
    try {
      return file.exists()? FileUtil.loadFile(file) : classHeader;
    }
    catch (IOException ex) {
      LOG.error(ex);
    }
    return classHeader;
  }

  private void generateNode(BnfRule rule, BnfExpression node, String funcName, Set<BnfExpression> visited) {
    IElementType type = getEffectiveType(node);

    for (String s : StringUtil.split(node.getText(), "\n")) {
      out("// " + s);
    }
    boolean isRule = node.getParent() == rule;
    boolean firstNonTrivial = node == Rule.firstNotTrivial(rule);
    boolean isPrivate = !(isRule || firstNonTrivial) || Rule.isPrivate(rule) || grammarRoot.equals(rule.getName());
    boolean isLeft = firstNonTrivial && Rule.isLeft(rule);
    boolean isLeftInner = isLeft && (isPrivate || Rule.isInner(rule));
    final String recoverRoot = firstNonTrivial ? Rule.attribute(rule, "recoverUntil", (String)null) : null;
    final boolean canCollapse = (!isLeft || isLeftInner) && firstNonTrivial && rulesWithIheritance.contains(rule);

    final List<BnfExpression> children;
    out((!isRule ? "private " : isPrivate ? "" : "public ") + "static boolean " + funcName + "(PsiBuilder builder_, int level_"
        + collectExtraArguments(rule, node, true) + ") {");
    if (node instanceof BnfReferenceOrToken || node instanceof BnfLiteralExpression || node instanceof BnfExternalExpression) {
      children = Collections.singletonList(node);
      if (isPrivate && !isLeftInner) {
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

    String debugFuncName = funcName; // + ":" + node.toStringTree();
    out("if (!recursion_guard_(builder_, level_, \"" + debugFuncName + "\")) return false;");

    if (isTrivialNode(node)) {
      BnfExpression child = children.get(0);
      out("return " + generateNodeCall(rule, child, getNextName(funcName, 0)) + ";");
      out("}");
      generateNodeChildren(rule, funcName, children, visited);
      return;
    }
    final long funcId = StringHash.calc(funcName);
    if (generateMemoizationCode) {
      out("if (memoizedFalseBranch(builder_, " + funcId + "L) return false;");
    }

    final Object pinValue = type == BNF_SEQUENCE ? getAttribute(rule, "pin", null, firstNonTrivial? rule.getName() : funcName) : null;
    final int pinIndex = pinValue instanceof Integer ? (Integer)pinValue : -1;
    final Pattern pinPattern = pinValue instanceof String ? Pattern.compile(StringUtil.unescapeStringCharacters((String)pinValue)) : null;
    boolean pinApplied = false;
    final boolean alwaysTrue = type == BNF_OP_OPT || type == BNF_OP_ZEROMORE;
    if (!alwaysTrue) {
      out("boolean result_ = " + (type == BNF_OP_ZEROMORE || type == BNF_OP_OPT) + ";");
    }
    boolean pinned = pinIndex > -1 || pinPattern != null;
    if (pinned) {
      out("boolean pinned_ = false;");
    }
    if (!isPrivate && canCollapse) {
      out("final int start_ = builder_.getCurrentOffset();");
    }
    if (isLeft) {
      out("final Marker left_marker_ = (Marker)builder_.getLatestDoneMarker();");
      if (generateExtendedPin) {
        out("if (!invalid_left_marker_guard_(builder_, left_marker_, \"" + debugFuncName + "\")) return false;");
      }
    }
    if (!alwaysTrue || !isPrivate) {
      out("final Marker marker_ = builder_.mark();");
    }

    String sectionType = recoverRoot != null ? "_SECTION_RECOVER_" :
                         type == BNF_OP_AND ? "_SECTION_AND_" :
                         type == BNF_OP_NOT ? "_SECTION_NOT_" :
                         pinned ? "_SECTION_GENERAL_" : null;
    if (sectionType != null) {
      out("enterErrorRecordingSection(builder_, level_, " + sectionType + ");");
    }

    boolean predicateEncountered = false;
    for (int i = 0, p = 0, childrenSize = children.size(); i < childrenSize; i++) {
      BnfExpression child = children.get(i);

      String nodeCall = generateNodeCall(rule, child, getNextName(funcName, i));
      if (type == BNF_CHOICE) {
        out((i > 0 ? "if (!result_) " : "") + "result_ = " + nodeCall + ";");
      }
      else if (type == BNF_SEQUENCE) {
        predicateEncountered |= pinApplied && ParserGeneratorUtil.getEffectiveExpression(child, ruleMap) instanceof BnfPredicate;
        if (i > 0) {
          if (pinApplied && generateExtendedPin && !predicateEncountered) {
            if (i == childrenSize - 1) {
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
        else {
          out("result_ = " + nodeCall + ";");
        }
        if (!pinApplied && (i == pinIndex - 1 || pinPattern != null && pinPattern.matcher(child.getText()).matches())) {
          pinApplied = true;
          p = i;
          out("pinned_ = result_; // pin = " + pinValue);
        }
      }
      else if (type == BNF_OP_OPT) {
        out(nodeCall + ";");
      }
      else if (type == BNF_OP_ONEMORE || type == BNF_OP_ZEROMORE) {
        if (type == BNF_OP_ONEMORE) {
          out("result_ = " + nodeCall + ";");
          nodeCall = generateNodeCall(rule, child, getNextName(funcName, i));
        }
        out("int offset_ = builder_.getCurrentOffset();");
        out("while ("+ (alwaysTrue? "true" : "result_") +") {");
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
        out("LighterASTNode last_ = "+(alwaysTrue? "builder_.getLatestDoneMarker();" : "result_? builder_.getLatestDoneMarker() : null;"));
        out("if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), " + elementType + ")) {");
        out("marker_.drop();");
        out("}");
      }
      if (!alwaysTrue || canCollapse) {
        out((canCollapse? "else " : "") + (alwaysTrue? "{" : "if (result_" + (pinned ? " || pinned_" : "") + ") {"));
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
    else if (!alwaysTrue) {
      out("if (!result_" + (pinned ? " && !pinned_" : "") + ") {");
      out("marker_.rollbackTo();");
      out("}");
      out("else {");
      out("marker_.drop();");
      if (isLeftInner) {
        out("left_marker_.precede().done(((LighterASTNode)left_marker_).getTokenType());");
        out("left_marker_.drop();");
      }
      out("}");
    }
    if (sectionType != null) {
      final String untilCall;
      if (recoverRoot != null) {
        BnfRule untilRule = ruleMap.get(recoverRoot);
        untilCall = untilRule == null ? null : generateWrappedNodeCall(rule, null, untilRule.getName());
      }
      else {
        untilCall = null;
      }
      final String resultEq = alwaysTrue? "" : "result_ = ";
      final String resultRef = alwaysTrue? "true" : "result_";
      out(resultEq + "exitErrorRecordingSection(builder_, "+resultRef+", level_, " +
          (pinned ? "pinned_" : "false") + ", " + sectionType + ", " + untilCall + ");");
    }

    if (!alwaysTrue && generateMemoizationCode) {
      out("if (!result_" + (pinned ? " && !pinned_" : "") + ") memoizeFalseBranch(builder_, " + funcId + ")");
    }
    out("return " + (alwaysTrue? "true": "result_" + (pinned ? " || pinned_" : "")) + ";");
    out("}");
    generateNodeChildren(rule, funcName, children, visited);
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
          if (expression instanceof BnfParenthesized) {
            newLine();
            generateNode(rule, expression, getNextName(getNextName(funcName, i), j - 1), visited);
          }
          else if (expression instanceof BnfExternalExpression) {
            generateNodeChildren(rule, getNextName(funcName, j - 1), Collections.singletonList(expression), visited);
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
    List<BnfExternalExpression> params = GrammarUtil.collectExtraArguments(rule, expression);
    if (params.isEmpty()) return "";
    final StringBuilder sb = new StringBuilder();
    for (BnfExternalExpression param : params) {
      sb.append(", " + (declaration ? "final Parser " : "") + param.getExpressionList().get(0).getText());
    }
    return sb.toString();
  }

  private String generateNodeCall(BnfRule rule, @Nullable BnfExpression node, String nextName) {
    IElementType type = node == null ? BNF_REFERENCE_OR_TOKEN : getEffectiveType(node);
    String text = node == null ? nextName : node.getText();
    if (type == BNF_STRING) {
      String value = StringUtil.stripQuotesAroundValue(text);
      String attributeName = getAttributeName(rule, value);
      if (attributeName != null && text.charAt(0) != '\"') {
        return generateConsumeToken(attributeName);
      }
      return generateConsumeTextToken(value);
    }
    else if (type == BNF_NUMBER) {
      return generateConsumeTextToken(text);
    }
    else if (type == BNF_REFERENCE_OR_TOKEN) {
      BnfRule subRule = ruleMap.get(text);
      if (subRule != null) {
        String method;
        if (Rule.isExternal(subRule)) {
          StringBuilder clause = new StringBuilder();
          BnfExpression expression = subRule.getExpression();
          List<BnfExpression> expressions =
            expression instanceof BnfSequence ? ((BnfSequence)expression).getExpressionList() : Collections.singletonList(expression);
          method = generateExternalCall(rule, clause, expressions, nextName);
          return method + "(builder_, level_ + 1" + clause.toString() + ")";
        }
        else {
          method = subRule.getName();
          String parserClass = ruleParserClasses.get(method);
          if (!parserClass.equals(ruleParserClasses.get(rule.getName()))) {
            method = StringUtil.getShortName(parserClass) + "." + method;
          }
          return method + "(builder_, level_ + 1)";
        }
      }
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
      return nextName + "(builder_, level_ + 1"+ collectExtraArguments(rule, node, false)+")";
    }
  }

  private String generateExternalCall(BnfRule rule, StringBuilder clause, List<BnfExpression> expressions, String nextName) {
    String method;
    method = expressions.size() > 0 ? expressions.get(0).getText() : null;
    if (expressions.size() > 1) {
      for (int i = 1, len = expressions.size(); i < len; i++) {
        clause.append(", ");
        BnfExpression nested = expressions.get(i);
        String argument = nested.getText();
        if (nested instanceof BnfReferenceOrToken) {
          if (ruleMap.containsKey(argument)) {
            clause.append(generateWrappedNodeCall(rule, nested, argument));
          }
          else {
            //clause.append(getElementType(argument));
            clause.append(argument);
          }
        }
        else if (nested instanceof BnfParenthesized) {
          clause.append(generateWrappedNodeCall(rule, nested, getNextName(nextName, i - 1)));
        }
        else if (nested instanceof BnfStringLiteralExpression && argument.startsWith("\'")) {
          clause.append(StringUtil.unquoteString(argument));
        }
        else if (nested instanceof BnfExternalExpression) {
          List<BnfExpression> expressionList = ((BnfExternalExpression)nested).getExpressionList();
          boolean metaRule = Rule.isMeta(rule);
          if (metaRule && expressionList.size() == 1) {
            // parameter
            clause.append(expressionList.get(0).getText());
          }
          else {
            clause.append(generateWrappedNodeCall(rule, nested, getNextName(nextName, i - 1)));
          }
        }
          
        else {
          clause.append(argument);
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
    String current = parserLambdas.get(constantName);
    if (current == null) {
      parserLambdas.put(constantName, "#"+generateNodeCall(rule, nested, nextName));
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
    simpleTokens.add(tokenName);
    return "consumeToken(builder_, " + getElementType(tokenName) + ")";
  }

  private static String getElementType(BnfRule rule) {
    String elementType = getAttribute(rule, "elementType", rule.getName());
    return getAttribute(rule, "elementTypePrefix", "") + elementType.toUpperCase();
  }

  private String getElementType(String token) {
    return getRootAttribute(treeRoot, "elementTypePrefix", "") + token.toUpperCase();
  }

  /*ElementTypes******************************************************************/

  private void generateElementTypesHolder(String className, boolean generatePsi) {
    String implPackage = getRootAttribute(treeRoot, "psiImplPackage", "generated.psi.impl");
    final String elementTypeClass = getRootAttribute(treeRoot, "elementTypeClass", IELEMENTTYPE_CLASS);
    final boolean generateTokens = getRootAttribute(treeRoot, "generateTokens", true);
    final String elementTypeFactory = getRootAttribute(treeRoot, "elementTypeFactory", null);
    final String tokenTypeClass = getRootAttribute(treeRoot, "tokenTypeClass", IELEMENTTYPE_CLASS);
    final String tokenTypeFactory = getRootAttribute(treeRoot, "tokenTypeFactory", null);
    generateClassHeader(className,
                        IELEMENTTYPE_CLASS + ";" +
                        "com.intellij.psi.PsiElement;" +
                        "com.intellij.lang.ASTNode;" +
                        elementTypeClass + ";" +
                        (elementTypeFactory == null ? "" : "static " + elementTypeFactory + ";") +
                        tokenTypeClass + ";" +
                        (tokenTypeFactory == null ? "" : "static " + tokenTypeFactory + ";") +
                        (generatePsi ? implPackage + ".*;" : ""), "", true);
    final Set<String> visited = new THashSet<String>();
    String elementCreateCall =
      elementTypeFactory == null ? "new " + StringUtil.getShortName(elementTypeClass) : StringUtil.getShortName(elementTypeFactory);
    for (String ruleName : ruleMap.keySet()) {
      final BnfRule rule = ruleMap.get(ruleName);
      if (Rule.isPrivate(rule) || Rule.isExternal(rule) || grammarRoot.equals(ruleName)) continue;
      final String elementType = getElementType(rule);
      if (!visited.add(elementType)) continue;
      out("IElementType " + elementType + " = "
          + elementCreateCall + "(\"" + elementType + "\");");
    }
    if (generateTokens) {
      newLine();
      String tokenCreateCall =
        tokenTypeFactory == null ? "new " + StringUtil.getShortName(tokenTypeClass) : StringUtil.getShortName(tokenTypeFactory);
      for (String token : simpleTokens) {
        String name = getRootAttribute(treeRoot, token, token);
        out("IElementType " + getElementType(token) + " = "
            + tokenCreateCall + "(\"" + name + "\");");
      }
    }
    if (generatePsi) {
      newLine();
      out("class Factory {");
      out("public static PsiElement createElement(ASTNode node) {");
      out("IElementType type = node.getElementType();");
      String suffix = getRootAttribute(treeRoot, "psiImplClassSuffix", "Impl");
      visited.clear();
      boolean first = true;
      for (String ruleName : ruleMap.keySet()) {
        final BnfRule rule = ruleMap.get(ruleName);
        if (Rule.isPrivate(rule) || Rule.isExternal(rule) || grammarRoot.equals(ruleName)) continue;
        String psiClass = getRulePsiClassName(rule, ruleName, true) + suffix;
        String elementType = getElementType(rule);
        if (!visited.add(elementType)) continue;
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
    Map<PsiElement, RuleGraphHelper.Cardinality> accessors = helper.getFor(rule);
    final Collection<BnfRule> sortedPublicRules = getSortedPublicRules(accessors.keySet());

    generateClassHeader(psiClass, "java.util.List;" +
                                  "org.jetbrains.annotations.*;" +
                                  "com.intellij.psi.PsiElement;" +
                                  StringUtil.join(psiSupers, ";") + ";" +
                                  StringUtil.join(getRuleAccessorClasses(rule, sortedPublicRules), ";"),
                        "", true, psiSupers);
    for (PsiElement tree : sortedPublicRules) {
      generatePsiAccessor(rule, tree, accessors.get(tree), true);
    }
    for (BnfReferenceOrToken tree : getSortedSimpleTokens(accessors.keySet())) {
      generatePsiAccessor(rule, tree, accessors.get(tree), true);
    }
    out("}");
  }

  private void generatePsiImpl(RuleGraphHelper helper,
                               BnfRule rule,
                               String psiClass,
                               String superInterface,
                               String superRuleClass) {
    String typeHolderClass = getRootAttribute(treeRoot, "elementTypeHolderClass", "generated.ParserTypes");
    // mixin attribute overrides "extends":
    String implSuper = getAttribute(rule, "mixin", superRuleClass);
    Map<PsiElement, RuleGraphHelper.Cardinality> accessors = helper.getFor(rule);
    final Collection<BnfRule> sortedPublicRules = getSortedPublicRules(accessors.keySet());
    generateClassHeader(psiClass, "java.util.List;" +
                                  "org.jetbrains.annotations.*;" +
                                  "com.intellij.lang.ASTNode;" +
                                  "com.intellij.psi.PsiElement;" +
                                  "com.intellij.psi.util.PsiTreeUtil;" +
                                  "static " + typeHolderClass + ".*;" +
                                  (StringUtil.isNotEmpty(implSuper) ? implSuper + ";" : "") +
                                  StringUtil.getPackageName(superInterface) + ".*;" +
                                  StringUtil.join(getRuleAccessorClasses(rule, sortedPublicRules), ";"),
                        "", false, StringUtil.getShortName(implSuper),
                        StringUtil.getShortName(superInterface));
    out("public " + StringUtil.getShortName(psiClass) + "(ASTNode node) {");
    out("super(node);");
    out("}");
    newLine();
    for (BnfRule tree : sortedPublicRules) {
      generatePsiAccessor(rule, tree, accessors.get(tree), false);
    }
    for (BnfReferenceOrToken tree : getSortedSimpleTokens(accessors.keySet())) {
      generatePsiAccessor(rule, tree, accessors.get(tree), false);
    }
    out("}");
  }

  private void generatePsiAccessor(BnfRule rule, PsiElement tree, RuleGraphHelper.Cardinality type, boolean intf) {
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
    String getterNameBody = getAttribute(rule, "methodRenames", "get" + getRulePsiClassName(rule, ruleName, false), ruleName);
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
    String className = StringUtil.getShortName(getAccessorType(rule, tree));
    String tail = intf ? "();" : "() {";
    out("public " + (many ? "List<" : "") + className + (many ? "> " : " ") + getterName + tail);
    if (!intf) {
      if (treeRule == null) {
        out("ASTNode child = getNode().findChildByType(" + getElementType(ruleName) + ");");
        out("return child == null? null : child.getPsi();");
      }
      else {
        if (many) {
          out("return PsiTreeUtil.getChildrenOfTypeAsList(this, " + className + ".class);");
        }
        else {
          out("return PsiTreeUtil.getChildOfType(this, " + className + ".class);");
        }
      }
      out("}");
    }
    newLine();
  }

  private static String getAccessorType(BnfRule rule, PsiElement tree) {
    BnfRule treeRule = tree instanceof BnfRule ? (BnfRule)tree : null;
    return treeRule == null ? PSI_ELEMENT_CLASS :
           Rule.isExternal(treeRule) ? getAttribute(treeRule, "implements", PSI_ELEMENT_CLASS) :
           getRulePsiClassName(rule, treeRule.getName(), true);
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
      if (simpleTokens.contains(tree.getText()) /*|| type == STRING || type == NUMBER*/) {
        result.put(tree.getText(), (BnfReferenceOrToken)tree);
      }
    }
    return result.values();
  }

  private static Collection<String> getRuleAccessorClasses(BnfRule rule, Collection<BnfRule> bnfRules) {
    final TreeSet<String> result = new TreeSet<String>();
    for (BnfRule r : bnfRules) {
      result.add(getAccessorType(rule, r));
    }
    return result;
  }
}
