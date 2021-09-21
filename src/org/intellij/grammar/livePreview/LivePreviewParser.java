/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.livePreview;

import com.intellij.lang.*;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import gnu.trove.THashMap;
import gnu.trove.TObjectIntHashMap;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.generator.*;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import static org.intellij.grammar.psi.BnfTypes.*;

/**
 * @author gregsh
 */
public class LivePreviewParser implements PsiParser {
  private static final Logger LOG = Logger.getInstance(LivePreviewParser.class);

  private final BnfFile myFile;
  private final LivePreviewLanguage myLanguage;
  private final Map<String,String> mySimpleTokens = new LinkedHashMap<>();

  private final Map<String, IElementType> myRuleElementTypes = new THashMap<>();
  private final Map<String, IElementType> myTokenElementTypes = new THashMap<>();

  private GenOptions G;
  private BnfRule myGrammarRoot;
  private RuleGraphHelper myGraphHelper;
  private ExpressionHelper myExpressionHelper;
  private MultiMap<BnfRule, BnfRule> myRuleExtendsMap;
  private BnfFirstNextAnalyzer myFirstNextAnalyzer;
  private String myTokenTypeText;

  private final TObjectIntHashMap<BnfRule> myRuleNumbers = new TObjectIntHashMap<>();
  private BitSet[] myBitSets;

  public LivePreviewParser(Project project, LivePreviewLanguage language) {
    myLanguage = language;
    myFile = language.getGrammar(project);
  }

  @Override
  public @NotNull ASTNode parse(@NotNull IElementType type, @NotNull PsiBuilder originalBuilder) {
    init(originalBuilder);
    PsiBuilder builder = adapt_builder_(type, originalBuilder, this);
    ErrorState.get(builder).altExtendsChecker = this::type_extends_;
    ArrayList<BracePair> braces = new ArrayList<>();
    ContainerUtil.addIfNotNull(braces, tryMakeBracePair("{", "}", true));
    ContainerUtil.addIfNotNull(braces, tryMakeBracePair("(", ")", false));
    ContainerUtil.addIfNotNull(braces, tryMakeBracePair("[", "]", false));
    ContainerUtil.addIfNotNull(braces, tryMakeBracePair("<", ">", false));
    ErrorState.get(builder).braces = braces.isEmpty() ? null : braces.toArray(new BracePair[0]);
    boolean result = true;
    PsiBuilder.Marker marker = enter_section_(builder, 0, _COLLAPSE_, null);
    if (myGrammarRoot != null) {
      result = rule(builder, 1, myGrammarRoot, Collections.emptyMap());
    }
    exit_section_(builder, 0, marker, type, result, true, TRUE_CONDITION);
    return builder.getTreeBuilt();
  }

  private @Nullable BracePair tryMakeBracePair(String s1, String s2, boolean structural) {
    IElementType t1 = getTokenElementType(getTokenName(s1));
    IElementType t2 = getTokenElementType(getTokenName(s2));
    return t1 != null && t2 != null? new BracePair(t1, t2, structural) : null;
  }

  private void init(PsiBuilder builder) {
    if (myFile == null) return;
    myGrammarRoot = ContainerUtil.getFirstItem(myFile.getRules());
    G = new GenOptions(myFile);
    mySimpleTokens.putAll(LivePreviewLexer.collectTokenPattern2Name(myFile, null));
    myGraphHelper = RuleGraphHelper.getCached(myFile);
    myRuleExtendsMap = myGraphHelper.getRuleExtendsMap();
    myExpressionHelper = ExpressionHelper.getCached(myFile);
    myFirstNextAnalyzer = BnfFirstNextAnalyzer.createAnalyzer(true);

    myTokenTypeText = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_PREFIX);

    Lexer lexer = ((PsiBuilderImpl)builder).getLexer();
    if (lexer instanceof LivePreviewLexer) {
      for (LivePreviewLexer.Token type : ((LivePreviewLexer)lexer).getTokens()) {
        myTokenElementTypes.put(type.constantName, type.tokenType);
      }
    }
    for (BnfRule rule : myFile.getRules()) {
      String elementType = ParserGeneratorUtil.getElementType(rule, G.generateElementCase);
      if (StringUtil.isEmpty(elementType)) continue;
      if (myRuleElementTypes.containsKey(elementType)) continue;
      myRuleElementTypes.put(elementType, new LivePreviewElementType.RuleType(elementType, rule, myLanguage));
    }
    int count = 0;
    for (BnfRule rule : myFile.getRules()) {
      myRuleNumbers.put(rule, count ++);
    }
    myBitSets = new BitSet[builder.getOriginalText().length()+1];
    for (int i = 0; i < myBitSets.length; i++) {
      myBitSets[i] = new BitSet(count);
    }
  }

  private boolean rule(PsiBuilder builder, int level, BnfRule rule, Map<String, Parser> externalArguments) {
    BitSet bitSet = myBitSets[builder.getCurrentOffset()];
    int ruleNumber = myRuleNumbers.get(rule);
    if (bitSet.get(ruleNumber)) {
      builder.error("Endless recursion detected for '" + rule.getName() + "'");
      return false;
    }
    bitSet.set(ruleNumber);
    boolean result = expression(builder, level, rule, rule.getExpression(), rule.getName(), externalArguments);
    bitSet.clear(ruleNumber);
    return result;
  }

  protected boolean expression(PsiBuilder builder,
                               int level,
                               BnfRule rule,
                               BnfExpression initialNode,
                               String funcName,
                               Map<String, Parser> externalArguments) {
    boolean isRule = initialNode.getParent() == rule;
    BnfExpression node = getNonTrivialNode(initialNode);

    IElementType type = getEffectiveType(node);

    boolean firstNonTrivial = node == ParserGeneratorUtil.Rule.firstNotTrivial(rule);
    boolean isPrivate = !(isRule || firstNonTrivial) || ParserGeneratorUtil.Rule.isPrivate(rule) || myGrammarRoot == rule;
    boolean isLeft = firstNonTrivial && ParserGeneratorUtil.Rule.isLeft(rule);
    boolean isLeftInner = isLeft && (isPrivate || ParserGeneratorUtil.Rule.isInner(rule));
    boolean isBranch = !isPrivate && Rule.isUpper(rule);
    String recoverWhile = firstNonTrivial ? getAttribute(rule, KnownAttribute.RECOVER_WHILE) : null;
    Map<String, String> hooks = firstNonTrivial ? getAttribute(rule, KnownAttribute.HOOKS).asMap() : Collections.emptyMap();
    boolean canCollapse = !isPrivate && (!isLeft || isLeftInner) && firstNonTrivial && myGraphHelper.canCollapse(rule);

    IElementType elementType = !isPrivate ? getRuleElementType(rule) : null;

    boolean isSingleNode = node instanceof BnfReferenceOrToken || node instanceof BnfLiteralExpression || node instanceof BnfExternalExpression;

    List<BnfExpression> children = isSingleNode ? Collections.singletonList(node) : getChildExpressions(node);
    String frameName = !children.isEmpty() && firstNonTrivial && !Rule.isMeta(rule) ? getRuleDisplayName(rule, !isPrivate) : null;

    if (isSingleNode) {
      children = Collections.singletonList(node);
      if (isPrivate && !isLeftInner && recoverWhile == null && frameName == null) {
        return generateNodeCall(builder, level, rule, node, getNextName(funcName, 0), externalArguments);
      }
      else {
        type = BNF_SEQUENCE;
      }
    }
    if (!children.isEmpty()) {
      if (!recursion_guard_(builder, level, funcName)) return false;
    }

    //if (recoverRoot == null && (isRule || firstNonTrivial)) {
    //  frameName = generateFirstCheck(rule, frameName, true);
    //}

    PinMatcher pinMatcher = new PinMatcher(rule, type, firstNonTrivial ? rule.getName() : funcName);
    boolean pinApplied = false;
    boolean alwaysTrue = type == BNF_OP_OPT || type == BNF_OP_ZEROMORE;

    boolean result_ = type == BNF_OP_ZEROMORE || type == BNF_OP_OPT || children.isEmpty();
    boolean pinned = pinMatcher.active();
    boolean pinned_ = false;

    int modifiers = 0;
    if (canCollapse) modifiers |= _COLLAPSE_;
    if (isLeftInner) modifiers |= _LEFT_INNER_;
    else if (isLeft) modifiers |= _LEFT_;
    if (type == BNF_OP_AND) modifiers |= _AND_;
    else if (type == BNF_OP_NOT) modifiers |= _NOT_;
    if (isBranch) modifiers |= _UPPER_;

    PsiBuilder.Marker marker_ = null;
    boolean sectionRequired = !alwaysTrue || !isPrivate || isLeft || recoverWhile != null;
    boolean sectionRequiredSimple = sectionRequired && modifiers == _NONE_ && recoverWhile == null && !(pinned || frameName != null);
    boolean sectionMaybeDropped = sectionRequiredSimple && type == BNF_CHOICE && elementType == null &&
                                  children.stream().noneMatch(o -> ParserGeneratorUtil.isRollbackRequired(o, myFile));

    if (sectionRequiredSimple) {
      if (!sectionMaybeDropped) {
        marker_ = enter_section_(builder);
      }
    }
    else if (sectionRequired) {
      marker_ = enter_section_(builder, level, modifiers, elementType, frameName);
    }

    int[] skip = {0};
    for (int i = 0, p = 0, childrenSize = children.size(); i < childrenSize; i++) {
      BnfExpression child = children.get(i);

      if (type == BNF_CHOICE) {
        if (i == 0) result_ = generateNodeCall(builder, level, rule, child, getNextName(funcName, i), externalArguments);
        else if (!result_) result_ = generateNodeCall(builder, level, rule, child, getNextName(funcName, i), externalArguments);
      }
      else if (type == BNF_SEQUENCE) {
        if (skip[0] == 0) {
          if (i == 0) {
            result_ = generateTokenSequenceCall(builder, level, rule, children, funcName, i, pinMatcher, pinApplied, skip, externalArguments);
          }
          else {
            if (pinApplied && G.generateExtendedPin) {
              if (i == childrenSize - 1) {
                // do not report error for last child
                if (i == p + 1) {
                  result_ = result_ && generateTokenSequenceCall(builder, level, rule, children, funcName, i, pinMatcher, pinApplied, skip, externalArguments);
                }
                else {
                  result_ = pinned_ && generateTokenSequenceCall(builder, level, rule, children, funcName, i, pinMatcher, pinApplied, skip, externalArguments) && result_;
                }
              }
              else if (i == p + 1) {
                result_ = result_ && report_error_(builder, generateTokenSequenceCall(builder, level, rule, children, funcName, i, pinMatcher, pinApplied, skip, externalArguments));
              }
              else {
                result_ = pinned_ && report_error_(builder, generateTokenSequenceCall(builder, level, rule, children, funcName, i, pinMatcher, pinApplied, skip, externalArguments)) && result_;
              }
            }
            else {
              result_ = result_ && generateTokenSequenceCall(builder, level, rule, children, funcName, i, pinMatcher, pinApplied, skip, externalArguments);
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
          pinned_ = result_; // pin = pinMatcher.pinValue
        }
      }
      else if (type == BNF_OP_OPT) {
        generateNodeCall(builder, level, rule, child, getNextName(funcName, i), externalArguments);
      }
      else if (type == BNF_OP_ONEMORE || type == BNF_OP_ZEROMORE) {
        if (type == BNF_OP_ONEMORE) {
          result_ = generateNodeCall(builder, level, rule, child, getNextName(funcName, i), externalArguments);
        }
        int pos = current_position_(builder);
        //noinspection LoopConditionNotUpdatedInsideLoop
        while (alwaysTrue || result_) {
          if (!generateNodeCall(builder, level, rule, child, getNextName(funcName, i), externalArguments)) break;
          if (!empty_element_parsed_guard_(builder, funcName, pos)) break;
          pos = current_position_(builder);
        }
      }
      else if (type == BNF_OP_AND) {
        result_ = generateNodeCall(builder, level, rule, child, getNextName(funcName, i), externalArguments);
      }
      else if (type == BNF_OP_NOT) {
        result_ = !generateNodeCall(builder, level, rule, child, getNextName(funcName, i), externalArguments);
      }
      else {
        LOG.warn("unexpected: " + type);
      }
    }

    boolean success = alwaysTrue || result_ || pinned_;

    if (!hooks.isEmpty()) {
      for (Map.Entry<String, String> entry : hooks.entrySet()) {
        if (entry.getValue() == null) continue;
        String name = ParserGeneratorUtil.toIdentifier(entry.getKey(), null, Case.UPPER);
        LiveHooksHelper.registerHook(builder, name, entry.getValue());
      }
    }
    if (sectionRequiredSimple) {
      if (!sectionMaybeDropped) {
        exit_section_(builder, marker_, elementType, alwaysTrue || result_);
      }
    }
    else if (sectionRequired) {
      Parser recoverPredicate;
      BnfRule recoverRule = recoverWhile != null ? myFile.getRule(recoverWhile) : null;
      if (BnfConstants.RECOVER_AUTO.equals(recoverWhile)) {
        IElementType[] nextTokens = generateAutoRecoverCall(rule);
        recoverPredicate = (builder12, level12) -> !GeneratedParserUtilBase.nextTokenIsFast(builder12, nextTokens);
      }
      else if (Rule.isMeta(rule) && GrammarUtil.isDoubleAngles(recoverWhile)) {
        recoverPredicate = externalArguments.get(recoverWhile.substring(2, recoverWhile.length() - 2));
      }
      else {
        recoverPredicate = recoverRule == null ? null : (builder1, level1) -> rule(builder1, level1, recoverRule, Collections.emptyMap());
      }
      exit_section_(builder, level, marker_, alwaysTrue || result_, pinned_, recoverPredicate);
    }

    return success;
  }

  private boolean type_extends_(IElementType elementType1, IElementType elementType2) {
    if (elementType1 == elementType2) return true;
    if (!(elementType1 instanceof LivePreviewElementType.RuleType)) return false;
    if (!(elementType2 instanceof LivePreviewElementType.RuleType)) return false;
    for (BnfRule baseRule : myRuleExtendsMap.keySet()) {
      Collection<BnfRule> ruleClass = myRuleExtendsMap.get(baseRule);
      BnfRule r1 = myFile.getRule(((LivePreviewElementType.RuleType)elementType1).ruleName);
      BnfRule r2 = myFile.getRule(((LivePreviewElementType.RuleType)elementType2).ruleName);
      if (ruleClass.contains(r1) && ruleClass.contains(r2)) return true;
    }
    return false;
  }


  protected boolean generateNodeCall(PsiBuilder builder, int level, BnfRule rule, @Nullable BnfExpression node, String nextName, Map<String, Parser> externalArguments) {
    IElementType type = node == null ? BNF_REFERENCE_OR_TOKEN : getEffectiveType(node);
    String text = node == null ? nextName : node.getText();
    if (type == BNF_STRING) {
      String value = GrammarUtil.unquote(text);
      String attributeName = getTokenName(value);
      if (attributeName != null) {
        return generateConsumeToken(builder, attributeName);
      }
      return generateConsumeTextToken(builder, value);
    }
    else if (type == BNF_NUMBER) {
      return generateConsumeTextToken(builder, text);
    }
    else if (type == BNF_REFERENCE_OR_TOKEN) {
      BnfRule subRule = myFile.getRule(text);
      if (subRule != null) {
        //String method;
        if (Rule.isExternal(subRule)) {
          // not supported
          return false;
          //method = generateExternalCall(rule, clause, GrammarUtil.getExternalRuleExpressions(subRule), nextName);
          //return method + "(builder_, level_ + 1" + clause.toString() + ")";
        }
        else {
          ExpressionHelper.ExpressionInfo info = ExpressionGeneratorHelper.getInfoForExpressionParsing(myExpressionHelper, subRule);
          if (info == null) {
            return rule(builder, level + 1, subRule, externalArguments);
          }
          else {
            int priority = info.getPriority(rule);
            int arg1Priority = subRule == info.rootRule ? -1 : info.getPriority(subRule);
            int argPriority = arg1Priority == -1 ? (priority == info.nextPriority - 1 ? -1 : priority) : arg1Priority - 1;
            return generateExpressionRoot(builder, level, info, argPriority);
          }
        }
      }
      return generateConsumeToken(builder, text);
    }
    else if (type == BNF_EXTERNAL_EXPRESSION) {
      List<BnfExpression> expressions = ((BnfExternalExpression)node).getExpressionList();
      if (expressions.size() == 1 && Rule.isMeta(rule)) {
        Parser parser = externalArguments.get(node.getText());
        return parser != null && parser.parse(builder, level);
      }
      else {
        return generateExternalCall(builder, level, rule, expressions, nextName, externalArguments);
      }
    }
    else {
      return expression(builder, level, rule, node, nextName, externalArguments);
    }
  }

  private boolean generateTokenSequenceCall(PsiBuilder builder,
                                            int level,
                                            BnfRule rule,
                                            List<BnfExpression> children,
                                            String funcName,
                                            int startIndex,
                                            PinMatcher pinMatcher,
                                            boolean pinApplied,
                                            int[] skip,
                                            Map<String, Parser> externalArguments) {
    BnfExpression nextChild = children.get(startIndex);
    if (startIndex == children.size() - 1 || !isTokenExpression(nextChild)) {
      return generateNodeCall(builder, level, rule, nextChild, funcName, externalArguments);
    }
    ArrayList<IElementType> list = new ArrayList<>();
    int pin = pinApplied ? -1 : 0;
    for (int i = startIndex, len = children.size(); i < len; i++) {
      BnfExpression child = children.get(i);
      IElementType type = child.getNode().getElementType();
      String text = child.getText();
      String tokenName;
      if (type == BNF_STRING && text.charAt(0) != '\"') {
        tokenName = getTokenName(GrammarUtil.unquote(text));
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
    if (list.size() < 2) {
      return generateNodeCall(builder, level, rule, nextChild, funcName, externalArguments);
    }
    skip[0] = list.size() - 1;
    return consumeTokens(builder, pin, list.toArray(IElementType.EMPTY_ARRAY));
  }

  private boolean generateExternalCall(PsiBuilder builder,
                                       int level,
                                       BnfRule rule,
                                       List<BnfExpression> expressions,
                                       String nextName,
                                       Map<String, Parser> externalArguments) {
    List<BnfExpression> callParameters = expressions;
    List<String> metaParameterNames;
    String method = expressions.size() > 0 ? expressions.get(0).getText() : null;
    BnfRule targetRule = method == null ? null : myFile.getRule(method);
    // handle external rule call: substitute and merge arguments from external expression and rule definition
    if (targetRule != null) {
      metaParameterNames = GrammarUtil.collectMetaParameters(targetRule, targetRule.getExpression());
      if (Rule.isExternal(targetRule)) {
        callParameters = GrammarUtil.getExternalRuleExpressions(targetRule);
        method = callParameters.get(0).getText();
        if (metaParameterNames.size() < expressions.size() - 1) {
          callParameters = ContainerUtil.concat(callParameters, expressions.subList(metaParameterNames.size() + 1, expressions.size()));
        }
        if (myFile.getRule(method) == null) {
          // not supported
          return false;
        }
      }
    }
    else {
      // Hard-coded extensions:
      if ("eof".equals(method) && expressions.size() == 1) {
        return GeneratedParserUtilBase.eof(builder, level);
      }
      else if ("anything".equals(method) && expressions.size() == 2) {
        BnfExpression finalNested = expressions.get(1);
        parseAsTree(ErrorState.get(builder), builder, level + 1, DUMMY_BLOCK, true, TOKEN_ADVANCER,
                    (builder14, level14) -> generateNodeCall(builder14, level14, rule, finalNested, getNextName(nextName, 0), Collections.emptyMap()));
        return true;
      }
      // not supported
      return false;
    }
    if (callParameters.size() <= 1) {
      return rule(builder, level, targetRule, externalArguments);
    }
    Map<String, Parser> argumentMap = new HashMap<>();
    for (int i = 1, len = Math.min(callParameters.size(), metaParameterNames.size() + 1); i < len; i++) {
      BnfExpression nested = callParameters.get(i);
      String argument = nested.getText();
      String argName = metaParameterNames.get(i - 1);
      String argNextName;
      int metaIdx;
      if (argument.startsWith("<<") && (metaIdx = metaParameterNames.indexOf(argument)) > -1) {
        nested = expressions.get(metaIdx + 1);
        argNextName = getNextName(nextName, metaIdx);
      }
      else {
        argNextName = getNextName(nextName, i - 1);
      }
      BnfExpression finalNested = nested;
      if (nested instanceof BnfReferenceOrToken || nested instanceof BnfLiteralExpression) {
        BnfRule argRule = nested instanceof BnfReferenceOrToken? myFile.getRule(nested.getText()) : null;
        argumentMap.put(argName, (builder13, level13) -> {
          if (argRule != null) {
            return rule(builder13, level13, argRule, Collections.emptyMap());
          }
          else {
            return generateNodeCall(builder13, level13, rule, finalNested, nextName, Collections.emptyMap());
          }
        });
      }
      else if (nested instanceof BnfExternalExpression) {
        List<BnfExpression> expressionList = ((BnfExternalExpression)nested).getExpressionList();
        boolean metaRule = Rule.isMeta(rule) || Rule.isExternal(rule);
        if (metaRule && expressionList.size() == 1) {
          // parameter
          argumentMap.put(argName, externalArguments.get("<<" + expressionList.get(0).getText() + ">>"));
        }
        else {
          argumentMap.put(argName, (builder12, level12) -> generateNodeCall(builder12, level12, targetRule, finalNested, argNextName, externalArguments));
        }
      }
      else {
        argumentMap.put(argName,
                        (builder1, level1) -> generateNodeCall(builder1, level1, targetRule, finalNested, argNextName, externalArguments));
      }
    }
    if (Rule.isExternal(targetRule)) {
      return generateExternalCall(builder, level, targetRule, callParameters, targetRule.getName(), argumentMap);
    }
    return rule(builder, level, targetRule, argumentMap);
  }

  private String getTokenName(String value) {
    return mySimpleTokens.get(value);
  }

  private @Nullable IElementType getRuleElementType(BnfRule rule) {
    String elementType = ParserGeneratorUtil.getElementType(rule, G.generateElementCase);
    return StringUtil.isEmpty(elementType) ? null : myRuleElementTypes.get(elementType);
  }

  private IElementType getTokenElementType(String token) {
    return token == null ? null : myTokenElementTypes.get(myTokenTypeText + token.toUpperCase());
  }

  private boolean generateConsumeToken(PsiBuilder builder, String tokenName) {
    IElementType tokenType = getTokenElementType(tokenName);
    return tokenType != null && generateConsumeToken(builder, tokenType);
  }

  protected boolean generateConsumeToken(PsiBuilder builder, @NotNull IElementType tokenType) {
    return consumeToken(builder, tokenType);
  }

  protected boolean generateConsumeTextToken(PsiBuilder builder, @NotNull String tokenText) {
    return consumeToken(builder, tokenText);
  }

  protected boolean isTokenExpression(BnfExpression node) {
    return node instanceof BnfLiteralExpression || node instanceof BnfReferenceOrToken && myFile.getRule(node.getText()) == null;
  }

  // Expression Generator Helper part
  private boolean generateExpressionRoot(PsiBuilder builder, int level, ExpressionHelper.ExpressionInfo info, int priority_) {
    Map<String, List<ExpressionHelper.OperatorInfo>> opCalls = new LinkedHashMap<>();
    for (BnfRule rule : info.priorityMap.keySet()) {
      ExpressionHelper.OperatorInfo operator = info.operatorMap.get(rule);
      String opCall = getNextName(operator.rule.getName(), 0);
      List<ExpressionHelper.OperatorInfo> list = opCalls.get(opCall);
      if (list == null) opCalls.put(opCall, list = new ArrayList<>(2));
      list.add(operator);
    }
    // main entry
    String methodName = info.rootRule.getName();
    String kernelMethodName = getNextName(methodName, 0);
    String frameName = ParserGeneratorUtil.getRuleDisplayName(info.rootRule, true);
    if (!recursion_guard_(builder, level, methodName)) return false;
    if (frameName != null) addVariant(builder, frameName);
    //g.generateFirstCheck(info.rootRule, frameName, true);
    boolean result_ = false;
    boolean pinned_;
    PsiBuilder.Marker marker_ = enter_section_(builder, level, _NONE_, frameName);

    boolean first = true;
    for (ExpressionHelper.OperatorInfo operator : filter(opCalls, ExpressionHelper.OperatorType.ATOM, ExpressionHelper.OperatorType.PREFIX)) {
      if (first || !result_) {
        result_ = generateNodeCall(builder, level, operator.rule, null, operator.rule.getName(), Collections.emptyMap());
      }
      first = false;
    }

    pinned_ = result_;
    result_ = result_ && generateKernelMethod(builder, level + 1, kernelMethodName, info, opCalls, priority_);
    exit_section_(builder, level, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  private boolean generateKernelMethod(PsiBuilder builder,
                                      int level,
                                      String methodName,
                                      ExpressionHelper.ExpressionInfo info,
                                      Map<String, List<ExpressionHelper.OperatorInfo>> opCalls,
                                      int priority_) {
    if (!recursion_guard_(builder, level, methodName)) return false;
    PsiBuilder.Marker marker_ = null;
    boolean result_ = true;
    int pos = current_position_(builder);

    main: while (true) {
      PsiBuilder.Marker left_marker_ = (PsiBuilder.Marker)builder.getLatestDoneMarker();
      if (!invalid_left_marker_guard_(builder, left_marker_, methodName)) return false;

      for (ExpressionHelper.OperatorInfo operator : filter(opCalls, ExpressionHelper.OperatorType.BINARY, ExpressionHelper.OperatorType.N_ARY, ExpressionHelper.OperatorType.POSTFIX)) {
        int priority = info.getPriority(operator.rule);
        int arg2Priority = operator.arg2 == null ? -1 : info.getPriority(operator.arg2);
        int argPriority = arg2Priority == -1 ? priority : arg2Priority - 1;

        if (marker_ == null) marker_ = builder.mark();

        if (priority_ <  priority &&
            (operator.arg1 == null || ((LighterASTNode)left_marker_).getTokenType() == getRuleElementType(operator.arg1)) &&
            generateNodeCall(builder, level, info.rootRule, operator.operator, getNextName(operator.rule.getName(), 0), Collections.emptyMap())) {

          IElementType elementType = getRuleElementType(operator.rule);
          boolean rightAssociative = ParserGeneratorUtil.getAttribute(operator.rule, KnownAttribute.RIGHT_ASSOCIATIVE);
          if (operator.type == ExpressionHelper.OperatorType.BINARY) {
              result_ = report_error_(builder, generateExpressionRoot(builder, level, info, (rightAssociative ? argPriority - 1 : argPriority)));
            if (operator.tail != null) result_ = report_error_(builder, generateNodeCall(builder, level, operator.rule, operator.tail, getNextName(operator.rule.getName(), 1), Collections.emptyMap())) && result_;
          }
          else if (operator.type == ExpressionHelper.OperatorType.N_ARY) {
            int nary_pos = current_position_(builder);
            while (true) {
              result_ = report_error_(builder, generateExpressionRoot(builder, level, info, argPriority));
              if (operator.tail != null) result_ = report_error_(builder, generateNodeCall(builder, level, operator.rule, operator.tail, getNextName(operator.rule.getName(), 1), Collections.emptyMap())) && result_;
              if (!result_ || !generateNodeCall(builder, level, info.rootRule, operator.operator, getNextName(operator.rule.getName(), 0), Collections.emptyMap())) break;
              if (!empty_element_parsed_guard_(builder, operator.operator.getText(), nary_pos)) break;
              nary_pos = current_position_(builder);
            }
          }
          else if (operator.type == ExpressionHelper.OperatorType.POSTFIX) {
            result_ = true;
          }
          marker_.drop();
          left_marker_.precede().done(elementType);
          marker_ = null;
          if (!empty_element_parsed_guard_(builder, info.rootRule.getName(), pos)) break main;
          pos = current_position_(builder);
          continue main;
        }
      }
      break;
    }
    GeneratedParserUtilBase.exit_section_(builder, marker_, null, false);
    return result_;
  }

  private static Iterable<ExpressionHelper.OperatorInfo> filter(Map<String, List<ExpressionHelper.OperatorInfo>> opCalls,
                                                                ExpressionHelper.OperatorType... operatorTypes) {
    return ContainerUtil.mapNotNull(opCalls.keySet(), opCall ->
      ContainerUtil.getFirstItem(ExpressionGeneratorHelper.findOperators(opCalls.get(opCall), operatorTypes)));
  }

  /**
   * @noinspection StringEquality
   */
  private IElementType[] generateAutoRecoverCall(BnfRule rule) {
    Set<BnfExpression> nextExprSet = myFirstNextAnalyzer.calcNext(rule).keySet();
    Set<String> nextSet = BnfFirstNextAnalyzer.asStrings(nextExprSet);
    List<IElementType> tokenTypes = new ArrayList<>(nextSet.size());

    for (String s : nextSet) {
      if (myFile.getRule(s) != null) continue; // ignore left recursion
      if (s == BnfFirstNextAnalyzer.MATCHES_EOF || s == BnfFirstNextAnalyzer.MATCHES_NOTHING) continue;

      boolean unknown = s == BnfFirstNextAnalyzer.MATCHES_ANY;
      IElementType t = unknown ? null : getTokenElementType(getTokenName(GrammarUtil.unquote(s)));
      if (t != null) {
        tokenTypes.add(t);
      }
      else {
        tokenTypes.clear();
        break;
      }
    }
    return tokenTypes.toArray(IElementType.EMPTY_ARRAY);
  }
}
