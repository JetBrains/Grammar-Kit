package org.intellij.grammar.livePreview;

import com.intellij.lang.*;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import gnu.trove.THashMap;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import static org.intellij.grammar.psi.BnfTypes.*;

/**
 * @author gregsh
 */
public class LivePreviewParser implements PsiParser {

  private final BnfFile myFile;
  private final LivePreviewLanguage myLanguage;
  private Map<String,String> mySimpleTokens;
  private BnfRule myGrammarRoot;
  private final Map<String, IElementType> myElementTypes = new THashMap<String, IElementType>();
  private RuleGraphHelper myGraphHelper;
  private MultiMap<BnfRule, BnfRule> myRuleExtendsMap;
  private boolean generateExtendedPin;
  private String myTokenTypeText;

  public LivePreviewParser(Project project, LivePreviewLanguage language) {
    myLanguage = language;
    myFile = language.getGrammar(project);
  }

  @NotNull
  @Override
  public ASTNode parse(IElementType root, PsiBuilder builder) {
    //ProgressManager.getInstance().getProgressIndicator().startNonCancelableSection(); // todo drop me
    PsiBuilder.Marker mark = builder.mark();
    init(builder);
    if (myGrammarRoot != null) {
      rule(adapt_builder_(root, builder, this), 1, myGrammarRoot);
    }

    while (!builder.eof()) {
      builder.advanceLexer();
    }
    mark.done(root);
    return builder.getTreeBuilt();
  }

  private void init(PsiBuilder builder) {
    myGrammarRoot = myFile == null? null : ContainerUtil.getFirstItem(myFile.getRules());
    if (myGrammarRoot == null) return;
    generateExtendedPin = getRootAttribute(myFile, KnownAttribute.EXTENDED_PIN);
    mySimpleTokens = LivePreviewLexer.collectTokenPattern2Name(myFile);
    myGraphHelper = RuleGraphHelper.getCached(myFile);
    myRuleExtendsMap = myGraphHelper.getRuleExtendsMap();

    myTokenTypeText = getRootAttribute(myFile, KnownAttribute.ELEMENT_TYPE_PREFIX);

    Lexer lexer = ((PsiBuilderImpl)builder).getLexer();
    if (lexer instanceof LivePreviewLexer) {
      for (LivePreviewLexer.Token type : ((LivePreviewLexer)lexer).getTokens()) {
        String tokenName = type.tokenType.toString();
        myElementTypes.put(tokenName, type.tokenType);
        String tokenAlias = mySimpleTokens.get(tokenName);
        if (tokenAlias != null && !tokenName.equals(tokenAlias)) {
          myElementTypes.put(myTokenTypeText + tokenAlias.toUpperCase(), type.tokenType);
        }
      }
    }
    for (BnfRule rule : myFile.getRules()) {
      String elementType = ParserGeneratorUtil.getElementType(rule);
      if (StringUtil.isEmpty(elementType)) continue;
      myElementTypes.put(elementType, new RuleElementType(elementType, rule, LivePreviewParser.this.myLanguage));
    }
  }

  private boolean rule(PsiBuilder builder, int level, BnfRule rule) {
    return expression(builder, level, rule, rule.getExpression(), rule.getName());
  }

  private boolean expression(PsiBuilder builder, int level, final BnfRule rule, BnfExpression initialNode, String funcName) {
    boolean isRule = initialNode.getParent() == rule;
    BnfExpression nonTrivialNode = initialNode;
    for (BnfExpression e = initialNode, n = getTrivialNodeChild(e); n != null; e = n, n = getTrivialNodeChild(e)) {
      nonTrivialNode = n;
    }
    BnfExpression node = nonTrivialNode;

    IElementType type = getEffectiveType(node);

    boolean firstNonTrivial = node == ParserGeneratorUtil.Rule.firstNotTrivial(rule);
    boolean isPrivate = !(isRule || firstNonTrivial) || ParserGeneratorUtil.Rule.isPrivate(rule) || myGrammarRoot == rule;
    boolean isLeft = firstNonTrivial && ParserGeneratorUtil.Rule.isLeft(rule);
    boolean isLeftInner = isLeft && (isPrivate || ParserGeneratorUtil.Rule.isInner(rule));
    String recoverRoot = firstNonTrivial ? getAttribute(rule, KnownAttribute.RECOVER_UNTIL) : null;
    boolean canCollapse = !isPrivate && (!isLeft || isLeftInner) && firstNonTrivial && canCollapse(rule);

    IElementType elementType = getElementType(rule);

    List<BnfExpression> children;
    if (node instanceof BnfReferenceOrToken || node instanceof BnfLiteralExpression || node instanceof BnfExternalExpression) {
      children = Collections.singletonList(node);
      if (isPrivate && !isLeftInner && recoverRoot == null) {
        return generateNodeCall(builder, level, rule, node, getNextName(funcName, 0));
      }
      else {
        type = BNF_SEQUENCE;
      }
    }
    else {
      children = getChildExpressions(node);
      if (children.isEmpty()) {
        if (isPrivate || elementType != null) {
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
    // todo
    //if (recoverRoot == null && (isRule || firstNonTrivial)) {
    //  frameName = generateFirstCheck(rule, frameName, true);
    //}

    PinMatcher pinMatcher = new PinMatcher(rule, type, firstNonTrivial ? rule.getName() : funcName);
    boolean pinApplied = false;
    boolean alwaysTrue = type == BNF_OP_OPT || type == BNF_OP_ZEROMORE;

    boolean result_ = (type == BNF_OP_ZEROMORE || type == BNF_OP_OPT);
    boolean pinned = pinMatcher.active();
    boolean pinned_ = false;

    int start_ = builder.getCurrentOffset();

    PsiBuilder.Marker left_marker_ = isLeft? (PsiBuilder.Marker)builder.getLatestDoneMarker() : null;
    if (isLeft && generateExtendedPin) {
      if (!invalid_left_marker_guard_(builder, left_marker_, funcName)) return false;
    }
    PsiBuilder.Marker marker_ = !alwaysTrue || !isPrivate? builder.mark() : null;

    String sectionType = recoverRoot != null ? "_SECTION_RECOVER_" :
                         type == BNF_OP_AND ? "_SECTION_AND_" :
                         type == BNF_OP_NOT ? "_SECTION_NOT_" :
                         pinned || frameName != null ? "_SECTION_GENERAL_" : null;
    if (sectionType != null) {
      enterErrorRecordingSection(builder, level, sectionType, frameName);
    }

    boolean predicateEncountered = false;
    int[] skip = {0};
    for (int i = 0, p = 0, childrenSize = children.size(); i < childrenSize; i++) {
      BnfExpression child = children.get(i);

      if (type == BNF_CHOICE) {
        if (i == 0) result_ = generateNodeCall(builder, level, rule, child, getNextName(funcName, i));
        else if (!result_) result_ = generateNodeCall(builder, level, rule, child, getNextName(funcName, i));
      }
      else if (type == BNF_SEQUENCE) {
        predicateEncountered |= pinApplied && ParserGeneratorUtil.getEffectiveExpression(myFile, child) instanceof BnfPredicate;
        if (skip[0] == 0) {
          if (i == 0) {
            result_ = generateTokenSequenceCall(builder, level, rule, children, funcName, i, pinMatcher, pinApplied, skip);
          }
          else {
            if (pinApplied && generateExtendedPin && !predicateEncountered) {
              if (i == childrenSize - 1) {
                // do not report error for last child
                if (i == p + 1) {
                  result_ = result_ && generateTokenSequenceCall(builder, level, rule, children, funcName, i, pinMatcher, pinApplied, skip);
                }
                else {
                  result_ = pinned_ && generateTokenSequenceCall(builder, level, rule, children, funcName, i, pinMatcher, pinApplied, skip) && result_;
                }
              }
              else if (i == p + 1) {
                result_ = result_ && report_error_(builder, generateTokenSequenceCall(builder, level, rule, children, funcName, i, pinMatcher, pinApplied, skip));
              }
              else {
                result_ = pinned_ && report_error_(builder, generateTokenSequenceCall(builder, level, rule, children, funcName, i, pinMatcher, pinApplied, skip)) && result_;
              }
            }
            else {
              result_ = result_ && generateTokenSequenceCall(builder, level, rule, children, funcName, i, pinMatcher, pinApplied, skip);
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
        generateNodeCall(builder, level, rule, child, getNextName(funcName, i));
      }
      else if (type == BNF_OP_ONEMORE || type == BNF_OP_ZEROMORE) {
        if (type == BNF_OP_ONEMORE) {
          result_ = generateNodeCall(builder, level, rule, child, getNextName(funcName, i));
        }
        int offset_ = builder.getCurrentOffset();
        //noinspection LoopConditionNotUpdatedInsideLoop
        while (alwaysTrue || result_) {
          if (!generateNodeCall(builder, level, rule, child, getNextName(funcName, i))) break;
          int next_offset_ = builder.getCurrentOffset();
          if (offset_ == next_offset_) {
            empty_element_parsed_guard_(builder, offset_, funcName);
            break;
          }
          offset_ = next_offset_;
        }
      }
      else if (type == BNF_OP_AND) {
        result_ = generateNodeCall(builder, level, rule, child, getNextName(funcName, i));
      }
      else if (type == BNF_OP_NOT) {
        result_ = !generateNodeCall(builder, level, rule, child, getNextName(funcName, i));
      }
      else {
        addWarning(myFile.getProject(), "unexpected: " + type);
      }
    }

    if (type == BNF_OP_AND || type == BNF_OP_NOT) {
      marker_.rollbackTo();
    }
    else if (!isPrivate && elementType != null) {
      LighterASTNode last_ = canCollapse && (alwaysTrue || result_) ? builder.getLatestDoneMarker() : null;
      if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), elementType)) {
        marker_.drop();
      }
      else if (result_ || pinned_) {
        if (isLeftInner) {
          marker_.done(elementType);
          left_marker_.precede().done(((LighterASTNode)left_marker_).getTokenType());
          left_marker_.drop();
        }
        else if (isLeft) {
          marker_.drop();
          left_marker_.precede().done(elementType);
        }
        else {
          marker_.done(elementType);
        }
      }
      else {
        marker_.rollbackTo();
      }
    }
    else if (!alwaysTrue) {
      if (!result_ && !pinned_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
      if (isLeftInner) {
        left_marker_.precede().done(((LighterASTNode)left_marker_).getTokenType());
        left_marker_.drop();
      }
    }
    if (sectionType != null) {
      final BnfRule untilRule = recoverRoot != null ? myFile.getRule(recoverRoot) : null;
      result_ = exitErrorRecordingSection(builder, level, alwaysTrue || result_, pinned_,
                                          sectionType, untilRule == null? null : new Parser() {
        @Override
        public boolean parse(PsiBuilder builder, int level) {
          return rule(builder, level, untilRule);
        }
      });
    }

    return alwaysTrue || result_ || pinned_;
  }

  private boolean type_extends_(IElementType elementType1, IElementType elementType2) {
    if (!(elementType1 instanceof RuleElementType)) return false;
    if (!(elementType2 instanceof RuleElementType)) return false;
    for (BnfRule baseRule : myRuleExtendsMap.keySet()) {
      Collection<BnfRule> ruleClass = myRuleExtendsMap.get(baseRule);
      if (ruleClass.contains(((RuleElementType)elementType1).rule) &&
          ruleClass.contains(((RuleElementType)elementType2).rule)) return true;
    }
    return false;
  }


  private boolean generateNodeCall(PsiBuilder builder, int level, BnfRule rule, @NotNull BnfExpression node) {
    return generateNodeCall(builder, level, rule, node, null);
  }

  private boolean generateNodeCall(PsiBuilder builder, int level, BnfRule rule, @Nullable BnfExpression node, String nextName) {
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
        String method;
        if (Rule.isExternal(subRule)) {
          return false;
          // todo external rules
          //method = generateExternalCall(rule, clause, GrammarUtil.getExternalRuleExpressions(subRule), nextName);
          //return method + "(builder_, level_ + 1" + clause.toString() + ")";
        }
        else {
          // todo expression parsing
          //ExpressionHelper.ExpressionInfo info = ExpressionGeneratorHelper.getInfoForExpressionParsing(myExpressionHelper, subRule);
          //method = info != null ? info.rootRule.getName() : subRule.getName();
          //if (info == null) {
            return rule(builder, level + 1, subRule);
          //}
          //else {
          //  return method + "(builder_, level_ + 1, " + info.getPriority(subRule) + ")";
          //}
        }
      }
      // allow token usage by registered token name instead of token text
      //if (!mySimpleTokens.containsKey(text) && !mySimpleTokens.values().contains(text)) {
      //  mySimpleTokens.put(text, null);
      //}
      return generateConsumeToken(builder, text);
    }
    else if (type == BNF_EXTERNAL_EXPRESSION) {
      return false;
      // todo
      //List<BnfExpression> expressions = ((BnfExternalExpression)node).getExpressionList();
      //if (expressions.size() == 1 && Rule.isMeta(rule)) {
      //  return expressions.get(0).getText() + ".parse(builder_, level_)";
      //}
      //else {
      //  StringBuilder clause = new StringBuilder();
      //  String method = generateExternalCall(rule, clause, expressions, nextName);
      //  return method + "(builder_, level_ + 1" + clause.toString() + ")";
      //}
    }
    else {
      return expression(builder, level, rule, node, nextName);
      /// todo
      //return nextName + "(builder_, level_ + 1" + collectExtraArguments(rule, node, false) + ")";
    }
  }

  private boolean generateTokenSequenceCall(PsiBuilder builder, int level,
                                            BnfRule rule,
                                            List<BnfExpression> children,
                                            String funcName, int startIndex,
                                            PinMatcher pinMatcher,
                                            boolean pinApplied,
                                            int[] skip) {
    return generateNodeCall(builder, level, rule, children.get(startIndex));

    // todo
    //if (startIndex == children.size() - 1 || !nodeCall.startsWith("consumeToken(builder_, ")) return nodeCall;
    //ArrayList<String> list = new ArrayList<String>();
    //int pin = pinApplied ? -1 : 0;
    //for (int i = startIndex, len = children.size(); i < len; i++) {
    //  BnfExpression child = children.get(i);
    //  IElementType type = child.getNode().getElementType();
    //  String text = child.getText();
    //  String tokenName;
    //  if (type == BNF_STRING && text.charAt(0) != '\"') {
    //    tokenName = getTokenName(StringUtil.stripQuotesAroundValue(text));
    //  }
    //  else if (type == BNF_REFERENCE_OR_TOKEN && myFile.getRule(text) == null) {
    //    tokenName = text;
    //  }
    //  else {
    //    break;
    //  }
    //  list.add(getTokenElementType(tokenName));
    //  if (!pinApplied && pinMatcher.matches(i, child)) {
    //    pin = i - startIndex + 1;
    //  }
    //}
    //if (list.size() < 2) return nodeCall;
    //skip[0] = list.size() - 1;
    //return "consumeTokens(builder_, " + pin + ", " + StringUtil.join(list, ", ") + ")";
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
    return myRuleExtendsMap.containsScalarValue(rule);
  }


  private String getTokenName(String value) {
    return mySimpleTokens.get(value);
  }

  @Nullable
  private IElementType getElementType(BnfRule rule) {
    String elementType = ParserGeneratorUtil.getElementType(rule);
    if (StringUtil.isEmpty(elementType)) return null;
    return getElementType(elementType);
  }

  private IElementType getElementType(String elementType) {
    return myElementTypes.get(elementType);
  }

  private boolean generateConsumeToken(PsiBuilder builder, String tokenName) {
    IElementType tokenType = getTokenElementType(tokenName);
    return tokenType != null && consumeToken(builder, tokenType);
  }

  private static boolean generateConsumeTextToken(PsiBuilder builder, String tokenText) {
    return consumeToken(builder, tokenText);
  }

  private IElementType getTokenElementType(String token) {
    return getElementType(myTokenTypeText + token.toUpperCase());
  }

  public static class RuleElementType extends IElementType {
    public final BnfRule rule;

    RuleElementType(String elementType, BnfRule rule, Language language) {
      super(elementType, language, false);
      this.rule = rule;
    }

  }
}
