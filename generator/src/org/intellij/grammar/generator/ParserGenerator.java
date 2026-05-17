/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import org.intellij.grammar.BnfPathsResolution;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.generator.NodeCalls.*;
import org.intellij.grammar.java.JavaHelperFactory;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.lang.String.format;
import static org.intellij.grammar.generator.ParserGeneratorUtil.ConsumeType;
import static org.intellij.grammar.psi.BnfAst.getTokenNames;
import static org.intellij.grammar.psi.BnfAst.isTokenSequence;

/**
 * Sealed intermediate base for the two parser-emitting subclasses ({@link JavaParserGenerator} and
 * {@link KotlinParserGenerator}). Splits off the parser-specific surface area from {@link Generator}
 * so that {@link JavaPsiGenerator} can extend {@code Generator} directly without inheriting parser
 * concerns.
 * <p>
 * Owns the parser-only abstract methods (target-language emission of nodes, node calls, token
 * sequence/choice calls, FIRST-set checks, auto-recovery), the parser-only mutable state
 * ({@link #myParserLambdas}, {@link #myRenderedLambdas}, {@link #myInlinedChildNodes},
 * {@link #myMetaMethodFields}, {@link #myTokensUsedInGrammar}), and the parser-only helper methods
 * built on top of them. PSI emission stays in {@link JavaPsiGenerator}; both layers share the
 * {@code BnfFile}, rule metadata, simple-token map, and choice-token-set map kept on
 * {@link Generator}.
 */
public sealed abstract class ParserGenerator extends Generator permits JavaParserGenerator, KotlinParserGenerator {

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

  /**
   * Names of tokens this generator emitted a {@code consumeToken(...)} call for during parser
   * emission. Read by {@link JavaPsiGenerator} when filtering whitespace regexp tokens from the
   * element-type holder — a token that is never referenced by the grammar and matches only
   * whitespace is treated as a non-emitted decorative token.
   */
  protected final @NotNull Set<String> myTokensUsedInGrammar = new LinkedHashSet<>();

  protected ParserGenerator(@NotNull GrammarInfo grammarInfo,
                            @NotNull String sourcePath,
                            @NotNull String packagePrefix,
                            @NotNull String outputFileExtension,
                            @NotNull OutputOpener outputOpener,
                            @NotNull NameRenderer nameRenderer,
                            @NotNull BnfPathsResolution paths) {
    super(grammarInfo, sourcePath, packagePrefix, outputFileExtension, outputOpener, nameRenderer, paths,
          JavaHelperFactory.getInstance(grammarInfo.file().getProject()).scoped(paths));
  }

  /**
   * Emits only the parser source(s). One file is produced per distinct
   * {@link org.intellij.grammar.KnownAttribute#PARSER_CLASS} value across the grammar's rules.
   */
  public abstract void generateParser() throws java.io.IOException;

  /**
   * Generates a method corresponding to a given rule.
   */
  abstract void generateNode(BnfRule rule, BnfExpression initialNode, String funcName, Set<BnfExpression> visited);

  @NotNull NodeCall generateNodeCall(@NotNull BnfRule rule, @Nullable BnfExpression node, @NotNull String nextName) {
    return generateNodeCall(rule, node, nextName, null);
  }

  /**
   * Builds the {@link NodeCall} that invokes the parser for {@code node} from inside {@code rule}.
   * For tokens this is a {@code consumeToken}; for sub-rules, a method call to the generated rule
   * function; for external/meta references, the appropriate external/meta call. {@code nextName}
   * is the function name to use when the call has to be inlined as a nested helper.
   */
  abstract NodeCall generateNodeCall(@NotNull BnfRule rule,
                                     @Nullable BnfExpression node,
                                     @NotNull String nextName,
                                     @Nullable ConsumeType forcedConsumeType);

  /**
   * If {@code children} is a homogeneous list of at least two tokens, registers a token-set
   * constant for them in {@link #myChoiceTokenSets} and returns a {@code consumeTokenFast(set)}
   * call. Returns {@code null} when no such optimization applies.
   */
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

  /**
   * Folds a run of consecutive token children starting at {@code startIndex} into a single
   * {@code consumeTokens}/{@code parseTokens} call when at least two tokens fit. {@code skip}
   * is set to the number of children to bypass in the caller's loop, and pin information is
   * propagated through {@code pinMatcher}.
   */
  abstract @NotNull NodeCall generateTokenSequenceCall(@NotNull List<BnfExpression> children,
                                                       int startIndex,
                                                       PinMatcher pinMatcher,
                                                       boolean pinApplied,
                                                       Ref<Integer> skip,
                                                       @NotNull NodeCall nodeCall,
                                                       boolean rollbackOnFail,
                                                       ConsumeType consumeType);

  /** Builds the target-specific {@code consumeToken(set)} call that backs {@link #generateTokenChoiceCall}. */
  abstract @NotNull NodeCall instantiateTokenChoiceCall(@NotNull ConsumeType consumeType, @NotNull String tokenSetName);

  /**
   * Emits an early-exit FIRST-set check for {@code rule}: when the next token can't possibly
   * begin {@code rule}, return {@code false} immediately. Returns the (possibly cleared)
   * {@code frameName} to use in the section header when the FIRST set was emitted inline.
   */
  protected abstract @Nullable String generateFirstCheck(@NotNull BnfRule rule, @Nullable String frameName, boolean skipIfOne);

  /**
   * Returns the constant name a {@code Parser} lambda for {@code nodeCall} should be referenced
   * by, registering its body in {@link #myParserLambdas} on first request. If the call doesn't
   * just delegate to {@code nextName(...)}, that {@code nextName} is added to
   * {@link #myInlinedChildNodes} so its child generation is skipped (the lambda inlines it).
   */
  @NotNull String getParserLambdaRef(@NotNull NodeCall nodeCall, @NotNull String nextName) {
    String constantName = CommonRendererUtils.getWrapperParserConstantName(nextName);
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

  void generateNodeChildren(BnfRule rule, String funcName, List<BnfExpression> children, Set<BnfExpression> visited) {
    for (int i = 0, len = children.size(); i < len; i++) {
      generateNodeChild(rule, children.get(i), funcName, i, visited);
    }
  }

  /**
   * Recursively emits helper parser methods for {@code child} (and, for external expressions,
   * for each non-atomic argument). Atomic and pure-token-sequence children are skipped because
   * their calls are inlined; children already marked as inlined via {@link #myInlinedChildNodes}
   * are also skipped.
   */
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

  /**
   * Emits a call to an external rule / meta rule. {@code expressions} holds the call form
   * {@code [methodName, arg1, arg2, ...]}; if it targets an external grammar rule, that rule's
   * own argument list is merged in. Each argument is rendered via {@link #generateWrappedNodeCall}.
   * Returns a {@link MetaMethodCall} when the target is a meta rule, otherwise a
   * {@link MethodCallWithArguments}.
   */
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
      if (BnfRules.isExternal(targetRule)) {
        metaParameterNames = GrammarUtil.collectMetaParameters(targetRule, targetRule.getExpression());
        callParameters = GrammarUtil.getExternalRuleExpressions(targetRule);
        method = callParameters.get(0).getText();
        if (metaParameterNames.size() < expressions.size() - 1) {
          callParameters = com.intellij.util.containers.ContainerUtil.concat(callParameters, expressions.subList(metaParameterNames.size() + 1, expressions.size()));
        }
      }
      else {
        String parserClass = ruleInfo(targetRule).parserClass();
        if (useTargetClassName(rule, parserClass)) {
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
          if (expressionList.size() == 1 && BnfRules.isMeta(rule)) {
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
    return BnfRules.isMeta(targetRule) ? new MetaMethodCall(targetClassName, method, stateHolder, N.level, arguments)
                                       : new MethodCallWithArguments(method, stateHolder, N.level, arguments);
  }

  protected boolean useTargetClassName(@NotNull BnfRule rule, String parserClass) {
    return !parserClass.equals(ruleInfo(rule).parserClass());
  }

  /** Formats a meta-rule parameter name (the {@code <<x>>} placeholder text) to its identifier in generated code. */
  @NotNull String formatMetaParamName(@NotNull String s) {
    String argName = s.trim();
    return N.metaParamPrefix + (N.metaParamPrefix.isEmpty() || "_".equals(N.metaParamPrefix) ? argName : StringUtil.capitalize(argName));
  }

  /**
   * Wraps the call for {@code nested} as a {@link NodeArgument} suitable for passing to another
   * generated parser function. Plain method calls become method references, meta-method calls
   * that don't reference meta-parameters are cached in static fields, and everything else is
   * rendered as a parser-lambda reference via {@link #getParserLambdaRef}.
   */
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

  /** Caches a parameter-free meta-method {@code call} as a static {@code Parser} field and returns its name. */
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
   * Builds the {@code #auto} recovery predicate for {@code rule}: a check that the current
   * token is none of the FIRST-of-NEXT element types. Registers it as a parser lambda and
   * returns its constant name. Emits a warning and produces an empty token list when the NEXT
   * set contains references that don't reduce to concrete element types.
   *
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

  /** Renders the body of the auto-recovery lambda — a {@code !nextTokenIsFast(...)} expression in the target language. */
  abstract StringBuilder generateAutoRecoveryCall(List<String> tokenTypes);
}
