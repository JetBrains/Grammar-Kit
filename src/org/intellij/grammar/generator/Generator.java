/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.containers.MultiMap;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.generator.NodeCalls.*;
import org.intellij.grammar.generator.Renderer.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.String.format;
import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.RuleGraphHelper.*;

public sealed abstract class Generator permits JavaParserGenerator, KotlinParserGenerator {
  private static final Logger LOG = Logger.getInstance(Generator.class);

  /**
   * The input BNF file to generate the parser from.
   */
  protected final @NotNull BnfFile myFile;
  protected final @NotNull String myOutputPath;

  /**
   * The package prefix to use for the generated parser.
   */
  protected final @NotNull String myPackagePrefix;
  protected final @NotNull String mySourcePath;
  protected final @Nullable String myGrammarRoot;
  protected final @Nullable String myGrammarRootParser;

  protected final @NotNull GenOptions G;
  protected final @NotNull Renderer R;
  public final Names N;

  private final @NotNull String myOutputFileExtension;
  protected final @NotNull OutputOpener myOpener;
  protected NameShortener myShortener;
  private FilePrinter myPrinter;
  protected final RuleGraphHelper myGraphHelper;
  protected final BnfFirstNextAnalyzer myFirstNextAnalyzer;

  final @NotNull Map<String, RuleInfo> myRuleInfos = new TreeMap<>();

  /**
   * Collection of token sets corresponding to each of the token choice
   * expressions in the grammar.
   */
  protected final Map<String, Collection<String>> myChoiceTokenSets = new TreeMap<>();

  /**
   * Contains information regarding all the tokens in the grammar.
   * The entries are divided into two categories:
   * 1. Token entries based on the `tokens` attribute in the grammar.
   * These entries are *reversed* and map the token text to the token name.
   * 2.
   */
  protected final Map<String, String> mySimpleTokens;

  /**
   * Maps field names to their corresponding parser function bodies.
   * These functions are generated as lambda fields in the parser implementation.
   */
  protected final @NotNull Map<String, String> myParserLambdas = new HashMap<>();
  /**
   * Maps field names to their corresponding parser class fully qualified names (FQNs).
   */
  protected final @NotNull Map<String, String> myRenderedLambdas = new HashMap<>();
  protected final @NotNull Set<String> myInlinedChildNodes = new HashSet<>();
  /**
   * Some meta-method calls use only static parsers as arguments,
   * i.e. they don't reference meta-method parameters,
   * we want to cache them in static fields.
   * <p/>
   * Mapping: <code> meta field name -> meta method call </code>
   */
  protected final Map<String, String> myMetaMethodFields = new HashMap<>();


  protected Generator(@NotNull BnfFile psiFile,
                      @NotNull String sourcePath,
                      @NotNull String outputPath,
                      @NotNull String packagePrefix,
                      @NotNull String outputFileExtension,
                      @NotNull OutputOpener outputOpener,
                      @NotNull Renderer renderer) {
    myFile = psiFile;

    G = new GenOptions(psiFile);
    N = G.names;
    R = renderer;
    mySourcePath = sourcePath;
    myOutputPath = outputPath;
    myPackagePrefix = packagePrefix;
    myOutputFileExtension = outputFileExtension;
    myOpener = outputOpener;

    List<BnfRule> rules = psiFile.getRules();
    BnfRule rootRule = rules.isEmpty() ? null : rules.get(0);
    myGrammarRoot = rootRule == null ? null : rootRule.getName();
    myGrammarRootParser = rootRule == null ? null : getRootAttribute(rootRule, KnownAttribute.PARSER_CLASS);
    mySimpleTokens = new LinkedHashMap<>(getTokenTextToNameMap(myFile));
    myGraphHelper = RuleGraphHelper.getCached(psiFile);
    myFirstNextAnalyzer = BnfFirstNextAnalyzer.createAnalyzer(true);
  }

  public abstract void generate() throws IOException;

  public abstract void generateParser() throws IOException;

  protected final void generateFileHeader(String className) {
    String header = getRootAttribute(myFile, KnownAttribute.CLASS_HEADER, className);
    String text = StringUtil.isEmpty(header) ? "" : getStringOrFile(header);
    if (StringUtil.isNotEmpty(text)) {
      out(text);
    }
    resetOffset();
  }

  /**
   * If the classHeader is a file path, loads the file content and returns it.
   * If it's a string representing a comment, returns it as is.
   * Otherwise, wraps the string in a comment.
   */
  private String getStringOrFile(String classHeader) {
    try {
      File file = new File(mySourcePath, classHeader);
      if (file.exists()) return FileUtil.loadFile(file);
    }
    catch (IOException ex) {
      LOG.error(ex);
    }
    return classHeader.startsWith("//") || classHeader.startsWith("/*")
           ? classHeader
           : StringUtil.countNewLines(classHeader) > 0
             ? "/*\n" + classHeader + "\n*/"
             :
             "// " + classHeader;
  }

  protected void openOutput(String className) throws IOException {
    String classNameAdjusted = myPackagePrefix.isEmpty() ? className : StringUtil.trimStart(className, myPackagePrefix + ".");
    File file = new File(myOutputPath, classNameAdjusted.replace('.', File.separatorChar) + "." + myOutputFileExtension);
    myPrinter = new FilePrinter(myOpener.openOutput(className, file, myFile));
  }

  protected void closeOutput() {
    myPrinter.close();
  }

  public void out(String s, Object... args) {
    myPrinter.out(s, args);
  }

  public void out(String s) {
    myPrinter.out(s);
  }

  public void newLine() {
    out("");
  }

  public @NotNull String shorten(@NotNull String s) {
    return myShortener.shorten(s);
  }

  protected void resetOffset() {
    myPrinter.resetOffset();
  }

  protected enum TypeKind {CLASS, INTERFACE, ABSTRACT_CLASS}

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

  @NotNull String getParserLambdaRef(@NotNull NodeCall nodeCall, @NotNull String nextName) {
    String constantName = Renderer.CommonRendererUtils.getWrapperParserConstantName(nextName);
    String targetClass = myRenderedLambdas.get(constantName);
    if (targetClass != null) {
      return StringUtil.getShortName(targetClass) + "." + constantName;
    }
    else if (!myParserLambdas.containsKey(constantName)) {
      String call = nodeCall.render(R);
      myParserLambdas.put(constantName, call);
      if (!call.startsWith(nextName + "(")) {
        myInlinedChildNodes.add(nextName);
      }
    }
    return constantName;
  }

  @NotNull RuleInfo ruleInfo(@NotNull BnfRule rule) {
    return Objects.requireNonNull(myRuleInfos.get(rule.getName()));
  }

  protected void calcAbstractRules() {
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
        ruleInfo(rule).isAbstract = true;
      }
    }
  }

  /**
   * Generates a method corresponding to a given rule.
   */
  abstract void generateNode(BnfRule rule, BnfExpression initialNode, String funcName, Set<BnfExpression> visited);

  @NotNull NodeCall generateNodeCall(@NotNull BnfRule rule, @Nullable BnfExpression node, @NotNull String nextName) {
    return generateNodeCall(rule, node, nextName, null);
  }

  abstract NodeCall generateNodeCall(@NotNull BnfRule rule,
                                     @Nullable BnfExpression node,
                                     @NotNull String nextName,
                                     @Nullable ConsumeType forcedConsumeType);

  @Nullable NodeCall generateTokenChoiceCall(@NotNull List<BnfExpression> children,
                                             @NotNull ConsumeType consumeType,
                                             @NotNull String funcName) {
    Collection<String> tokenNames = getTokenNames(myFile, children, 2);
    if (tokenNames == null) return null;

    Set<String> tokens = new TreeSet<>();
    for (String tokenName : tokenNames) {
      if (!mySimpleTokens.containsKey(tokenName) && !mySimpleTokens.containsValue(tokenName)) {
        mySimpleTokens.put(tokenName, null);
      }
      tokens.add(getElementType(tokenName));
    }

    String tokenSetName = CommonRendererUtils.getTokenSetConstantName(funcName);
    myChoiceTokenSets.put(tokenSetName, tokens);
    return instantiateTokenChoiceCall(consumeType, tokenSetName);
  }

  abstract @NotNull NodeCall generateTokenSequenceCall(@NotNull List<BnfExpression> children,
                                                       int startIndex,
                                                       PinMatcher pinMatcher,
                                                       boolean pinApplied,
                                                       Ref<Integer> skip,
                                                       @NotNull NodeCall nodeCall,
                                                       boolean rollbackOnFail,
                                                       ConsumeType consumeType);

  abstract @NotNull NodeCall instantiateTokenChoiceCall(@NotNull ConsumeType consumeType, @NotNull String tokenSetName);

  protected abstract @Nullable String generateFirstCheck(@NotNull BnfRule rule, @Nullable String frameName, boolean skipIfOne);

  protected @Nullable String firstToElementType(String first) {
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

  protected @Nullable String getTokenName(String value) {
    return mySimpleTokens.get(value);
  }

  void generateNodeChildren(BnfRule rule, String funcName, List<BnfExpression> children, Set<BnfExpression> visited) {
    for (int i = 0, len = children.size(); i < len; i++) {
      generateNodeChild(rule, children.get(i), funcName, i, visited);
    }
  }

  void generateNodeChild(@NotNull BnfRule rule,
                         @NotNull BnfExpression child,
                         @NotNull String funcName,
                         int index,
                         @NotNull Set<BnfExpression> visited) {
    if (child instanceof BnfExternalExpression externalExpression) {
      // generate parameters
      List<BnfExpression> expressions = externalExpression.getExpressionList();
      for (int j = 1, size = expressions.size(); j < size; j++) {
        BnfExpression expression = expressions.get(j);
        if (GrammarUtil.isAtomicExpression(expression)) continue;
        if (expression instanceof BnfExternalExpression) {
          generateNodeChild(rule, expression, R.getNextName(funcName, index), j - 1, visited);
        }
        else {
          String nextName = R.getNextName(R.getNextName(funcName, index), j - 1);
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
    // else do not generate
  }

  @NotNull NodeCall generateExternalCall(@NotNull BnfRule rule,
                                         @NotNull List<BnfExpression> expressions,
                                         @NotNull String nextName,
                                         @NotNull String stateHolder) {
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
        String parserClass = ruleInfo(targetRule).parserClass;
        if (!parserClass.equals(ruleInfo(rule).parserClass)) {
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
            arguments.add(new TextArgument(StringUtil.unquoteString(argument, '\'')));
          }
        }
        else if (nested instanceof BnfExternalExpression expression) {
          List<BnfExpression> expressionList = expression.getExpressionList();
          if (expressionList.size() == 1 && Rule.isMeta(rule)) {
            arguments.add(new MetaParameterArgument(formatMetaParamName(expressionList.get(0).getText())));
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
    return Rule.isMeta(targetRule) ? new MetaMethodCall(targetClassName, method, stateHolder, N.level, arguments)
                                   : new MethodCallWithArguments(method, stateHolder, N.level, arguments);
  }

  @NotNull String formatMetaParamName(@NotNull String s) {
    String argName = s.trim();
    return N.metaParamPrefix + (N.metaParamPrefix.isEmpty() || "_".equals(N.metaParamPrefix) ? argName : StringUtil.capitalize(argName));
  }

  @NotNull
  NodeArgument generateWrappedNodeCall(@NotNull BnfRule rule, @Nullable BnfExpression nested, @NotNull String nextName) {
    NodeCall nodeCall = generateNodeCall(rule, nested, nextName);
    if (nodeCall instanceof MetaMethodCall metaCall) {
      MetaMethodCallArgument argument = new MetaMethodCallArgument(metaCall);
      if (metaCall.referencesMetaParameter()) {
        return argument;
      }
      else {
        return renderer -> getMetaMethodFieldRef(argument.render(renderer), nextName);
      }
    }
    else if (useMethodCall(nodeCall)) {
      return renderer -> format("%s::%s", ((MethodCall)nodeCall).className(), ((MethodCall)nodeCall).methodName());
    }
    else {
      return renderer -> getParserLambdaRef(nodeCall, nextName);
    }
  }

  private @NotNull String getMetaMethodFieldRef(@NotNull String call, @NotNull String nextName) {
    String fieldName = CommonRendererUtils.getWrapperParserConstantName(nextName);
    myMetaMethodFields.putIfAbsent(fieldName, call);
    return fieldName;
  }

  boolean useMethodCall(NodeCall nodeCall) {
    return nodeCall instanceof MethodCall;
  }

  private boolean shallGenerateNodeChild(String funcName) {
    return !myInlinedChildNodes.contains(funcName);
  }

  /**
   * @noinspection StringEquality
   */
  protected String generateAutoRecoverCall(BnfRule rule) {
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

    StringBuilder sb = generateAutoRecoveryCall(tokenTypes);

    String constantName = rule.getName() + "_auto_recover_";
    myParserLambdas.put(constantName, sb.toString());
    return constantName;
  }

  abstract StringBuilder generateAutoRecoveryCall(List<String> tokenTypes);

  protected final @NotNull String getElementType(String token) {
    return getTokenType(myFile, token, G.generateTokenCase);
  }

  protected final @NotNull String getElementType(BnfRule rule) {
    return CommonRendererUtils.getElementType(rule, G.generateElementCase);
  }

  protected @NotNull List<Set<String>> buildExtendsSet(@NotNull MultiMap<BnfRule, BnfRule> map) {
    if (map.isEmpty()) return Collections.emptyList();
    List<Set<String>> result = new ArrayList<>();
    for (Map.Entry<BnfRule, Collection<BnfRule>> entry : map.entrySet()) {
      Set<String> set = null;
      for (BnfRule rule : entry.getValue()) {
        RuleInfo ruleInfo = this.ruleInfo(rule);
        if (!hasElementType(rule)) continue;
        String elementType = ruleInfo.isFake && !ruleInfo.isInElementType ||
                             getSynonymTargetOrSelf(rule) != rule ? null : ruleInfo.elementType;
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
}
