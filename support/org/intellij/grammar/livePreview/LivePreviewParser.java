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

package org.intellij.grammar.livePreview;

import com.intellij.lang.*;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.NullableFunction;
import com.intellij.util.PairProcessor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.HashMap;
import com.intellij.util.containers.MultiMap;
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

  private final BnfFile myFile;
  private final LivePreviewLanguage myLanguage;
  private final Map<String,String> mySimpleTokens = ContainerUtil.newLinkedHashMap();
  private final Map<String, IElementType> myElementTypes = ContainerUtil.newTroveMap();

  private GenOptions G;
  private BnfRule myGrammarRoot;
  private RuleGraphHelper myGraphHelper;
  private ExpressionHelper myExpressionHelper;
  private MultiMap<BnfRule, BnfRule> myRuleExtendsMap;
  private String myTokenTypeText;

  private final TObjectIntHashMap<BnfRule> myRuleNumbers = new TObjectIntHashMap<BnfRule>();
  private BitSet[] myBitSets;

  public LivePreviewParser(Project project, LivePreviewLanguage language) {
    myLanguage = language;
    myFile = language.getGrammar(project);
  }

  @NotNull
  @Override
  public ASTNode parse(IElementType root, PsiBuilder originalBuilder) {
    //com.intellij.openapi.progress.ProgressIndicator indicator = com.intellij.openapi.progress.ProgressManager.getInstance().getProgressIndicator();
    //if (indicator != null ) indicator.startNonCancelableSection();
    //originalBuilder.setDebugMode(true);
    init(originalBuilder);
    PsiBuilder builder = adapt_builder_(root, originalBuilder, this);
    ErrorState.get(builder).altExtendsChecker = new PairProcessor<IElementType, IElementType>() {
      @Override
      public boolean process(IElementType elementType, IElementType elementType2) {
        return type_extends_(elementType, elementType2);
      }
    };
    ArrayList<BracePair> braces = new ArrayList<BracePair>();
    ContainerUtil.addIfNotNull(braces, tryMakeBracePair("{", "}", true));
    ContainerUtil.addIfNotNull(braces, tryMakeBracePair("(", ")", false));
    ContainerUtil.addIfNotNull(braces, tryMakeBracePair("[", "]", false));
    ContainerUtil.addIfNotNull(braces, tryMakeBracePair("<", ">", false));
    ErrorState.get(builder).braces = braces.isEmpty()? null : braces.toArray(new BracePair[braces.size()]);
    int level = 0;
    PsiBuilder.Marker mark = enter_section_(builder, level, _NONE_, null);
    boolean result = myGrammarRoot != null && rule(builder, 1, myGrammarRoot, Collections.<String, Parser>emptyMap());
    exit_section_(builder, level, mark, root, result, true, TRUE_CONDITION);
    return builder.getTreeBuilt();
  }

  @Nullable
  private BracePair tryMakeBracePair(String s1, String s2, boolean structural) {
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

    myTokenTypeText = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_PREFIX);

    Lexer lexer = ((PsiBuilderImpl)builder).getLexer();
    if (lexer instanceof LivePreviewLexer) {
      for (LivePreviewLexer.Token type : ((LivePreviewLexer)lexer).getTokens()) {
        myElementTypes.put(type.constantName, type.tokenType);
      }
    }
    for (BnfRule rule : myFile.getRules()) {
      String elementType = ParserGeneratorUtil.getElementType(rule, G.generateElementCase);
      if (StringUtil.isEmpty(elementType)) continue;
      if (myElementTypes.containsKey(elementType)) continue;
      myElementTypes.put(elementType, new LivePreviewElementType.RuleType(elementType, rule, myLanguage));
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
                               final BnfRule rule,
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
    boolean canCollapse = !isPrivate && (!isLeft || isLeftInner) && firstNonTrivial && myGraphHelper.canCollapse(rule);

    IElementType elementType = getElementType(rule);

    List<BnfExpression> children;
    if (node instanceof BnfReferenceOrToken || node instanceof BnfLiteralExpression || node instanceof BnfExternalExpression) {
      children = Collections.singletonList(node);
      if (isPrivate && !isLeftInner && recoverWhile == null) {
        return generateNodeCall(builder, level, rule, node, getNextName(funcName, 0), externalArguments);
      }
      else {
        type = BNF_SEQUENCE;
      }
    }
    else {
      children = getChildExpressions(node);
      if (children.isEmpty() && recoverWhile == null) {
        if (isPrivate || elementType == null) {
          return true;
        }
        else {
          builder.mark().done(elementType);
          return true;
        }
      }
    }
    if (!recursion_guard_(builder, level, funcName)) return false;

    String frameName = firstNonTrivial && !Rule.isMeta(rule)? getRuleDisplayName(rule, !isPrivate) : null;
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
    boolean sectionRequiredSimple = sectionRequired && modifiers == _NONE_ && recoverWhile == null && !(modifiers == 0 && (pinned || frameName != null));
    if (sectionRequiredSimple) {
      marker_ = enter_section_(builder);
    }
    else if (sectionRequired) {
      marker_ = enter_section_(builder, level, modifiers, isPrivate ? null : elementType, frameName);
    }

    boolean predicateEncountered = false;
    int[] skip = {0};
    for (int i = 0, p = 0, childrenSize = children.size(); i < childrenSize; i++) {
      BnfExpression child = children.get(i);

      if (type == BNF_CHOICE) {
        if (i == 0) result_ = generateNodeCall(builder, level, rule, child, getNextName(funcName, i), externalArguments);
        else if (!result_) result_ = generateNodeCall(builder, level, rule, child, getNextName(funcName, i), externalArguments);
      }
      else if (type == BNF_SEQUENCE) {
        predicateEncountered |= pinApplied && ParserGeneratorUtil.getEffectiveExpression(myFile, child) instanceof BnfPredicate;
        if (skip[0] == 0) {
          if (i == 0) {
            result_ = generateTokenSequenceCall(builder, level, rule, children, funcName, i, pinMatcher, pinApplied, skip, externalArguments);
          }
          else {
            if (pinApplied && G.generateExtendedPin && !predicateEncountered) {
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
        addWarning(myFile.getProject(), "unexpected: " + type);
      }
    }

    if (sectionRequiredSimple) {
      exit_section_(builder, marker_, isPrivate? null : elementType, alwaysTrue || result_);
    }
    else if (sectionRequired) {
      Parser recoverPredicate;
      final BnfRule recoverRule = recoverWhile != null ? myFile.getRule(recoverWhile) : null;
      if (BnfConstants.RECOVER_AUTO.equals(recoverWhile)) {
        final IElementType[] nextTokens = generateAutoRecoverCall(rule);
        recoverPredicate = new Parser() {
          @Override
          public boolean parse(PsiBuilder builder, int level) {
            return !GeneratedParserUtilBase.nextTokenIsFast(builder, nextTokens);
          }
        };
      }
      else {
        recoverPredicate = recoverRule == null ? null : new Parser() {
          @Override
          public boolean parse(PsiBuilder builder, int level) {
            return rule(builder, level, recoverRule, Collections.<String, Parser>emptyMap());
          }
        };
      }
      exit_section_(builder, level, marker_, alwaysTrue || result_, pinned_, recoverPredicate);
    }

    return alwaysTrue || result_ || pinned_;
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
      String value = StringUtil.stripQuotesAroundValue(text);
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
    ArrayList<IElementType> list = new ArrayList<IElementType>();
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
    if (list.size() < 2) {
      return generateNodeCall(builder, level, rule, nextChild, funcName, externalArguments);
    }
    skip[0] = list.size() - 1;
    return consumeTokens(builder, pin, list.toArray(new IElementType[list.size()]));
  }

  private boolean generateExternalCall(PsiBuilder builder,
                                       int level,
                                       final BnfRule rule,
                                       List<BnfExpression> expressions,
                                       final String nextName,
                                       final Map<String, Parser> externalArguments) {
    List<BnfExpression> callParameters = expressions;
    List<BnfExpression> metaParameters = Collections.emptyList();
    List<String> metaParameterNames;
    String method = expressions.size() > 0 ? expressions.get(0).getText() : null;
    final BnfRule targetRule = method == null ? null : myFile.getRule(method);
    // handle external rule call: substitute and merge arguments from external expression and rule definition
    if (targetRule != null) {
      metaParameterNames = GrammarUtil.collectExtraArguments(targetRule, targetRule.getExpression());
      if (Rule.isExternal(targetRule)) {
        // not supported
        return false;
        //callParameters = GrammarUtil.getExternalRuleExpressions(targetRule);
        //metaParameters = expressions;
        //method = callParameters.get(0).getText();
        //if (metaParameterNames.size() < expressions.size() - 1) {
        //  callParameters = ContainerUtil.concat(callParameters, expressions.subList(metaParameterNames.size() + 1, expressions.size()));
        //}
      }
    }
    else {
      // Hard-coded extensions:
      if ("eof".equals(method) && expressions.size() == 1) {
        return GeneratedParserUtilBase.eof(builder, level);
      }
      else if ("anything".equals(method) && expressions.size() == 2) {
        final BnfExpression finalNested = expressions.get(1);
        parseAsTree(ErrorState.get(builder), builder, level + 1, DUMMY_BLOCK, true, TOKEN_ADVANCER, new Parser() {
              @Override
              public boolean parse(PsiBuilder builder, int level) {
                return generateNodeCall(builder, level, rule, finalNested, getNextName(nextName, 0), Collections.<String, Parser>emptyMap());
              }
            });
        return true;
      }
      // not supported
      return false;
    }
    if (callParameters.size() <= 1) {
      return rule(builder, level, targetRule, externalArguments);
    }
    Map<String, Parser> argumentMap = new HashMap<String, Parser>();
    for (int i = 1, len = Math.min(callParameters.size(), metaParameterNames.size() + 1); i < len; i++) {
      BnfExpression nested = callParameters.get(i);
      String argument = nested.getText();
      final String argNextName;
      final String argName;
      int metaIdx;
      if (argument.startsWith("<<") && (metaIdx = metaParameterNames.indexOf(argument)) > -1) {
        nested = metaParameters.get(metaIdx + 1);
        argument = nested.getText();
        argNextName = getNextName(nextName, metaIdx);
        argName = argument;
      }
      else {
        argNextName = getNextName(nextName, i - 1);
        argName = metaParameterNames.get(i - 1);
      }
      final BnfExpression finalNested = nested;
      if (nested instanceof BnfReferenceOrToken || nested instanceof BnfLiteralExpression) {
        final BnfRule argRule = nested instanceof BnfReferenceOrToken? myFile.getRule(argument) : null;
        argumentMap.put(argName, new Parser() {
          @Override
          public boolean parse(PsiBuilder builder, int level) {
            if (argRule != null) {
              return rule(builder, level, argRule, Collections.<String, Parser>emptyMap());
            }
            else {
              return generateNodeCall(builder, level, rule, finalNested, nextName, Collections.<String, Parser>emptyMap());
            }
          }
        });
      }
      else if (nested instanceof BnfExternalExpression) {
        List<BnfExpression> expressionList = ((BnfExternalExpression)nested).getExpressionList();
        boolean metaRule = Rule.isMeta(rule);
        if (metaRule && expressionList.size() == 1) {
          // parameter
          argumentMap.put(argName, externalArguments.get(expressionList.get(0).getText()));
        }
        else {
          argumentMap.put(argName, new Parser() {
            @Override
            public boolean parse(PsiBuilder builder, int level) {
              return generateNodeCall(builder, level, targetRule, finalNested, argNextName, externalArguments);
            }
          });
        }
      }
      else {
        argumentMap.put(argName, new Parser() {
          @Override
          public boolean parse(PsiBuilder builder, int level) {
            return generateNodeCall(builder, level, targetRule, finalNested, argNextName, externalArguments);
          }
        });
      }
    }
    return rule(builder, level, targetRule, argumentMap);
  }

  private String getTokenName(String value) {
    return mySimpleTokens.get(value);
  }

  @Nullable
  private IElementType getElementType(BnfRule rule) {
    String elementType = ParserGeneratorUtil.getElementType(rule, G.generateElementCase);
    if (StringUtil.isEmpty(elementType)) return null;
    return getElementType(elementType);
  }

  private IElementType getElementType(String elementType) {
    return myElementTypes.get(elementType);
  }

  private boolean generateConsumeToken(PsiBuilder builder, String tokenName) {
    IElementType tokenType = getTokenElementType(tokenName);
    return tokenType != null && generateConsumeToken(builder, tokenType);
  }

  protected boolean generateConsumeToken(PsiBuilder builder, IElementType tokenType) {
    return consumeToken(builder, tokenType);
  }

  protected boolean generateConsumeTextToken(PsiBuilder builder, String tokenText) {
    return consumeToken(builder, tokenText);
  }

  private IElementType getTokenElementType(String token) {
    return token == null? null : getElementType(myTokenTypeText + token.toUpperCase());
  }

  protected boolean isTokenExpression(BnfExpression node) {
    return node instanceof BnfLiteralExpression || node instanceof BnfReferenceOrToken && myFile.getRule(node.getText()) == null;
  }

  // Expression Generator Helper part
  private boolean generateExpressionRoot(PsiBuilder builder, int level, ExpressionHelper.ExpressionInfo info, int priority_) {
    Map<String, List<ExpressionHelper.OperatorInfo>> opCalls = new LinkedHashMap<String, List<ExpressionHelper.OperatorInfo>>();
    for (BnfRule rule : info.priorityMap.keySet()) {
      ExpressionHelper.OperatorInfo operator = info.operatorMap.get(rule);
      String opCall = getNextName(operator.rule.getName(), 0);
      List<ExpressionHelper.OperatorInfo> list = opCalls.get(opCall);
      if (list == null) opCalls.put(opCall, list = new ArrayList<ExpressionHelper.OperatorInfo>(2));
      list.add(operator);
    }
    // main entry
    String methodName = info.rootRule.getName();
    String kernelMethodName = getNextName(methodName, 0);
    String frameName = quote(ParserGeneratorUtil.getRuleDisplayName(info.rootRule, true));
    if (!recursion_guard_(builder, level, methodName)) return false;
    //g.generateFirstCheck(info.rootRule, frameName, true);
    boolean result_ = false;
    boolean pinned_;
    PsiBuilder.Marker marker_ = enter_section_(builder, level, _NONE_, frameName);

    boolean first = true;
    for (ExpressionHelper.OperatorInfo operator : filter(opCalls, ExpressionHelper.OperatorType.ATOM, ExpressionHelper.OperatorType.PREFIX)) {
      if (first || !result_) {
        result_ = generateNodeCall(builder, level, operator.rule, null, operator.rule.getName(), Collections.<String, Parser>emptyMap());
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
            (operator.arg1 == null || ((LighterASTNode)left_marker_).getTokenType() == getElementType(operator.arg1)) &&
            generateNodeCall(builder, level, info.rootRule, operator.operator, getNextName(operator.rule.getName(), 0), Collections.<String, Parser>emptyMap())) {

          IElementType elementType = getElementType(operator.rule);
          boolean rightAssociative = ParserGeneratorUtil.getAttribute(operator.rule, KnownAttribute.RIGHT_ASSOCIATIVE);
          if (operator.type == ExpressionHelper.OperatorType.BINARY) {
              result_ = report_error_(builder, generateExpressionRoot(builder, level, info, (rightAssociative ? argPriority - 1 : argPriority)));
            if (operator.tail != null) result_ = report_error_(builder, generateNodeCall(builder, level, operator.rule, operator.tail, getNextName(operator.rule.getName(), 1), Collections.<String, Parser>emptyMap())) && result_;
          }
          else if (operator.type == ExpressionHelper.OperatorType.N_ARY) {
            while (true) {
              result_ = report_error_(builder, generateExpressionRoot(builder, level, info, argPriority));
              if (operator.tail != null) result_ = report_error_(builder, generateNodeCall(builder, level, operator.rule, operator.tail, getNextName(operator.rule.getName(), 1), Collections.<String, Parser>emptyMap())) && result_;
              if (!result_ || !generateNodeCall(builder, level, info.rootRule, operator.operator, getNextName(operator.rule.getName(), 0), Collections.<String, Parser>emptyMap())) break;
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

  private static Iterable<ExpressionHelper.OperatorInfo> filter(final Map<String, List<ExpressionHelper.OperatorInfo>> opCalls,
                                                                final ExpressionHelper.OperatorType... operatorTypes) {
    return ContainerUtil.mapNotNull(opCalls.keySet(), new NullableFunction<String, ExpressionHelper.OperatorInfo>() {
      @Nullable
      @Override
      public ExpressionHelper.OperatorInfo fun(String opCall) {
        return ContainerUtil.getFirstItem(ExpressionGeneratorHelper.findOperators(opCalls.get(opCall), operatorTypes));
      }
    });
  }

  /**
   * @noinspection StringEquality
   */
  private IElementType[] generateAutoRecoverCall(BnfRule rule) {
    BnfFirstNextAnalyzer analyzer = new BnfFirstNextAnalyzer();
    Set<BnfExpression> nextExprSet = analyzer.calcNext(rule).keySet();
    Set<String> nextSet = analyzer.asStrings(nextExprSet);
    List<IElementType> tokenTypes = new ArrayList<IElementType>(nextSet.size());

    for (String s : nextSet) {
      if (myFile.getRule(s) != null) continue; // ignore left recursion
      if (s == BnfFirstNextAnalyzer.MATCHES_EOF || s == BnfFirstNextAnalyzer.MATCHES_NOTHING) continue;

      boolean unknown = s == BnfFirstNextAnalyzer.MATCHES_ANY;
      IElementType t = unknown ? null : getTokenElementType(getTokenName(StringUtil.stripQuotesAroundValue(s)));
      if (t != null) {
        tokenTypes.add(t);
      }
      else {
        tokenTypes.clear();
        break;
      }
    }
    return tokenTypes.toArray(new IElementType[tokenTypes.size()]);
  }
}
