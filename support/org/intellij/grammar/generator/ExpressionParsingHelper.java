/*
 * Copyright 2011-2012 Gregory Shrago
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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.Function;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.containers.MultiMapBasedOnSet;
import gnu.trove.THashSet;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.actions.GenerateAction;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfElementFactory;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;

/**
 * @author greg
 */
public class ExpressionParsingHelper {
  public static final Function<BnfExpression, String> TO_STRING = new Function<BnfExpression, String>() {
    @Override
    public String fun(BnfExpression expression) {
      return expression.getText();
    }
  };
  private final BnfFile myFile;
  private final RuleGraphHelper myRuleGraph;

  private final Map<BnfRule, ExpressionInfo> myExpressionMap = new HashMap<BnfRule, ExpressionInfo>();
  private final Map<BnfRule, BnfRule> myRootRulesMap = new HashMap<BnfRule, BnfRule>();

  public ExpressionParsingHelper(BnfFile file, RuleGraphHelper ruleGraph) {
    myFile = file;
    myRuleGraph = ruleGraph;
    buildExpressionRules();
  }

  public ExpressionInfo getExpressionInfo(BnfRule rule) {
    BnfRule root = myRootRulesMap.get(rule);
    ExpressionInfo info = root == null ? null : myExpressionMap.get(root);
    if (info == null) return null;
    if (info.rootRule == rule || Rule.isPrivate(rule)) return info;
    return info.priorityMap.containsKey(rule) ? info : null;
  }

  private void buildExpressionRules() {
    BnfFirstNextAnalyzer analyzer = new BnfFirstNextAnalyzer();
    for (BnfRule rule : myFile.getRules()) {
      if (Rule.isPrivate(rule) || Rule.isFake(rule)) continue;
      Map<PsiElement, RuleGraphHelper.Cardinality> contentRules = myRuleGraph.getFor(rule);
      if (!contentRules.isEmpty()) continue;
      if (!analyzer.asStrings(analyzer.calcFirst(rule)).contains(rule.getName())) continue;

      ExpressionInfo expressionInfo = new ExpressionInfo(rule);
      addToPriorityMap(rule, myRuleGraph.getExtendsRules(rule), expressionInfo);
      myRootRulesMap.put(rule, rule);
      myExpressionMap.put(rule, expressionInfo);
    }
  }

  private void addToPriorityMap(BnfRule rule, Collection<BnfRule> rulesCluster, ExpressionInfo info) {
    Collection<BnfRule> subRules = myRuleGraph.getSubRules(rule);
    Map<BnfRule, Integer> priorityMap = info.priorityMap;
    int priority = rulesCluster.contains(rule) ? -1 : info.nextPriority++;

    for (BnfRule subRule : subRules) {
      if (priorityMap.containsKey(subRule)) {
        addWarning(subRule + " has duplicate appearance!");
        continue;
      }
      BnfRule prev = myRootRulesMap.put(subRule, info.rootRule);
      if (prev != null) {
        addWarning(subRule + " must not be in several expression hierarchies: " + prev.getName() + " and " + info.rootRule.getName());
      }

      if (rulesCluster.contains(subRule)) {
        if (buildExpressionContentMap(subRule, info.rootRule, info.operatorMap)) {
          priorityMap.put(subRule, priority == -1 ? info.nextPriority++ : priority);
        }
      }
      else {
        assert ParserGeneratorUtil.Rule.isPrivate(subRule) : " priority group should be private!";
        addToPriorityMap(subRule, rulesCluster, info);
      }
    }
  }

  private boolean buildExpressionContentMap(BnfRule rule, BnfRule rootRule, Map<BnfRule, OperatorInfo> expressionContentMap) {
    Map<PsiElement, RuleGraphHelper.Cardinality> ruleContent = myRuleGraph.getFor(rule);
    if (ruleContent.isEmpty()) return false;
    RuleGraphHelper.Cardinality cardinality = ruleContent.get(rootRule);
    String rootRuleName = rootRule.getName();
    List<BnfExpression> childExpressions = getChildExpressions(rule.getExpression());
    OperatorInfo info;
    if (cardinality == null) {
      // atom
      info = new OperatorInfo(rule, OperatorType.ATOM, rule.getExpression(), null);
    }
    else if (childExpressions.size() < 2) {
      addWarning("invalid expression definition for " + rule + ": 2 or more arguments expected");
      info = new OperatorInfo(rule, OperatorType.ATOM, rule.getExpression(), null);
    }
    else if (cardinality == RuleGraphHelper.Cardinality.REQUIRED) {
      // postfix or prefix unary expression
      Ref<BnfRule> causeRef = Ref.create(null);
      int index = indexOf(rootRule, 0, childExpressions, causeRef);
      if (index == 0) {
        info = new OperatorInfo(rule, OperatorType.UNARY_POSTFIX, combine(childExpressions.subList(1, childExpressions.size())), null);
      }
      else if (index == -1) {
        addWarning("invalid expression definition for " + rule + ": " + rootRuleName + " reference expected");
        info = new OperatorInfo(rule, OperatorType.ATOM, rule.getExpression(), null);
      }
      else {
        info = new OperatorInfo(rule, OperatorType.UNARY, combine(childExpressions.subList(0, index)),
                                combine(childExpressions.subList(index + 1, childExpressions.size())), causeRef.get());
      }
    }
    else if (cardinality == RuleGraphHelper.Cardinality.AT_LEAST_ONE) {
      // binary or n-ary expression
      int index = indexOf(rootRule, 0, childExpressions, null);
      if (index != 0) {
        addWarning("binary or n-ary expression must not have prefix: " + rule);
        info = new OperatorInfo(rule, OperatorType.ATOM, rule.getExpression(), null);
      }
      else {
        int index2 = indexOf(rootRule, 1, childExpressions, null);
        if (index2 == -1) {
          BnfExpression lastExpression = childExpressions.get(1);
          boolean badNAry = childExpressions.size() != 2 || !(lastExpression instanceof BnfQuantified) ||
                            !(((BnfQuantified)lastExpression).getQuantifier().getText().equals("+")) ||
                            !(((BnfQuantified)lastExpression).getExpression() instanceof BnfParenExpression);
          List<BnfExpression> childExpressions2 = badNAry ? Collections.<BnfExpression>emptyList() :
                                                  getChildExpressions(
                                                    ((BnfParenExpression)((BnfQuantified)lastExpression).getExpression()).getExpression());
          int index3 = indexOf(rootRule, 0, childExpressions2, null);
          if (badNAry || index3 == -1) {
            addWarning(rule + ": invalid n-ary expressions definition. '" + rootRuleName + " ( <op> " + rootRuleName + ") +' expected");
            info = new OperatorInfo(rule, OperatorType.UNARY_POSTFIX, combine(childExpressions.subList(1, childExpressions.size())), null);
          }
          else {
            info = new OperatorInfo(rule, OperatorType.N_ARY, combine(childExpressions2.subList(0, index3)),
                                    combine(childExpressions2.subList(index3 + 1, childExpressions2.size())));
          }
        }
        else {
          info = new OperatorInfo(rule, OperatorType.BINARY, combine(childExpressions.subList(index + 1, index2)),
                                  combine(childExpressions.subList(index2 + 1, childExpressions.size())));
        }
      }
    }
    else {
      addWarning("unexpected cardinality " + cardinality + " of " + rootRule + " in " + rule);
      return false;
    }
    expressionContentMap.put(rule, info);
    return true;
  }

  private static BnfExpression combine(List<BnfExpression> list) {
    if (list.isEmpty()) return null;
    if (list.size() == 1) return list.get(0);
    Project project = list.get(0).getProject();
    String text = StringUtil.join(list, TO_STRING, " ");
    return BnfElementFactory.createExpressionFromText(project, text);
  }

  private int indexOf(BnfRule rootRule, int startIndex, List<BnfExpression> childExpressions, @Nullable Ref<BnfRule> causeRef) {
    assert childExpressions.size() > 1;
    for (int i = startIndex, childExpressionsSize = childExpressions.size(); i < childExpressionsSize; i++) {
      BnfRule rule = myFile.getRule(childExpressions.get(i).getText());
      if (rootRule == rule) {
        return i;
      }
      else if (causeRef != null && i > 0 && myRootRulesMap.get(rule) == rootRule) {
        causeRef.set(rule);
        return i;
      }
    }
    return -1;
  }

  public static class ExpressionInfo {
    public final BnfRule rootRule;
    public final Map<BnfRule, Integer> priorityMap = new LinkedHashMap<BnfRule, Integer>();
    public final Map<BnfRule, OperatorInfo> operatorMap = new HashMap<BnfRule, OperatorInfo>();
    public int nextPriority;

    public ExpressionInfo(BnfRule rootRule) {
      this.rootRule = rootRule;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("Expression root: " + rootRule.getName());
      sb.append("\nOperator precedence:\n");
      for (int i = 0; i < nextPriority; i++) {
        sb.append(i).append(":");
        for (BnfRule rule : priorityMap.keySet()) {
          if (priorityMap.get(rule) == i) {
            OperatorInfo operatorInfo = operatorMap.get(rule);
            sb.append(" ").append(operatorInfo);
          }
        }
        sb.append("\n");
      }
      return sb.toString();
    }

    public int getPriority(BnfRule subRule) {
      if (subRule == rootRule) return -1;
      Integer integer = priorityMap.get(subRule);
      return integer == null ? -1 : integer;
    }
  }

  public static enum OperatorType {ATOM, UNARY, UNARY_POSTFIX, BINARY, N_ARY}

  public static class OperatorInfo {
    public final BnfRule rule;
    public final OperatorType type;
    public final BnfExpression operator;
    public final BnfExpression tail; // null for postfix
    public final BnfRule substitutor;

    public OperatorInfo(BnfRule rule, OperatorType type, BnfExpression operator, BnfExpression tail) {
      this(rule, type, operator, tail, null);
    }

    public OperatorInfo(BnfRule rule, OperatorType type, BnfExpression operator, BnfExpression tail, @Nullable BnfRule substitutor) {
      assert operator != null : rule + ": operator must not be null";
      this.rule = rule;
      this.type = type;
      this.operator = operator;
      this.tail = tail;
      this.substitutor = substitutor;
    }

    @Override
    public String toString() {
      return type + "(" + operator.getText() + (tail == null ? "" : " tail: " + tail.getText()) + ")";
    }
  }

  public void generateExpressionRoot(ExpressionInfo info, ParserGenerator g) {
    MultiMap<String, OperatorInfo> opCalls = new MultiMapBasedOnSet<String, OperatorInfo>();
    for (OperatorInfo operator : info.operatorMap.values()) {
      String nodeCall = g.generateNodeCall(info.rootRule, operator.operator, getNextName(operator.rule.getName(), 0));
      opCalls.putValue(nodeCall, operator);
    }
    Set<String> sortedOpCalls = new TreeSet<String>(opCalls.keySet());

    for (String s : info.toString().split("\n")) {
      g.out("// " + s);
    }

    // main entry
    String methodName = info.rootRule.getName();
    String kernelMethodName = getNextName(methodName, 0);
    String frameName = quote(ParserGeneratorUtil.getRuleDisplayName(info.rootRule, true));
    g.out("public static boolean " + methodName + "(PsiBuilder builder_, int level_, int precedence_) {");
    g.out("Marker marker_ = builder_.mark();");
    g.out("boolean result_ = false;");
    g.out("boolean pinned_ = false;");
    g.out("enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, " + frameName + ");");

    boolean first = true;
    for (String opCall : sortedOpCalls) {
      OperatorInfo operator = ContainerUtil.getFirstItem(findOperators(opCalls.get(opCall), OperatorType.ATOM, OperatorType.UNARY));
      if (operator == null) continue;
      g.out((first ? "" : "else ") + "if (" + opCall + ") {");
      first = false;
      g.out("pinned_ = true;");
      String elementType = ParserGeneratorUtil.getElementType(operator.rule);
      String tailCall =
        operator.tail == null ? null : g.generateNodeCall(operator.rule, operator.tail, getNextName(operator.rule.getName(), 1));
      if (operator.type == OperatorType.ATOM) {
        g.out("result_ = true;");
        g.out("marker_.done(" + elementType + ");");
      }
      else if (operator.type == OperatorType.UNARY) {
        Integer substitutorPriority = operator.substitutor == null ? null : info.getPriority(operator.substitutor);
        int rulePriority = info.getPriority(operator.rule);
        int priority = substitutorPriority == null ? (rulePriority == info.nextPriority - 1 ? -1 : rulePriority) : substitutorPriority;
        g.out("result_ = " + methodName + "(builder_, level_, " + priority + ");");
        if (tailCall != null) {
          g.out("result_ = report_error_(builder_, " + tailCall + ") && result_;");
        }
        g.out("marker_.done(" + elementType + ");");
      }
      g.out("}");
    }
    g.out("result_ = pinned_ && " + kernelMethodName + "(builder_, level_, precedence_) && result_;");
    g.out("if (!result_ && !pinned_) {");
    g.out("marker_.rollbackTo();");
    g.out("}");
    g.out("result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);");
    g.out("return result_ || pinned_;");
    g.out("}");
    g.newLine();

    // kernel
    g.out("public static boolean " + kernelMethodName + "(PsiBuilder builder_, int level_, int precedence_) {");
    g.out("boolean result_ = true;");
    g.out("while (true) {");
    g.out("boolean pinned_ = false;");
    g.out("Marker left_marker_ = (Marker) builder_.getLatestDoneMarker();");
    g.out("if (!invalid_left_marker_guard_(builder_, left_marker_, \"" + kernelMethodName + "\")) return false;");
    g.out("Marker marker_ = builder_.mark();");

    first = true;
    for (String opCall : sortedOpCalls) {
      OperatorInfo operator =
        ContainerUtil.getFirstItem(findOperators(opCalls.get(opCall), OperatorType.BINARY, OperatorType.N_ARY, OperatorType.UNARY_POSTFIX));
      if (operator == null) continue;
      int priority = info.getPriority(operator.rule);
      g.out((first ? "" : "else ") + "if (precedence_ < " + priority + " && " + opCall + ") {");
      first = false;
      String elementType = ParserGeneratorUtil.getElementType(operator.rule);
      boolean rightAssociative =
        ParserGeneratorUtil.getAttribute(operator.rule, KnownAttribute.create(Boolean.class, "rightAssociative", false));
      String tailCall =
        operator.tail == null ? null : g.generateNodeCall(operator.rule, operator.tail, getNextName(operator.rule.getName(), 1));
      g.out("pinned_ = true;");
      if (operator.type == OperatorType.BINARY) {
        g.out(
          "result_ = report_error_(builder_, " + methodName + "(builder_, level_, " + (rightAssociative ? priority - 1 : priority) + "));");
        if (tailCall != null) g.out("result_ = " + tailCall + " && result_;");
      }
      else if (operator.type == OperatorType.N_ARY) {
        g.out("while (true) {");
        g.out("result_ = report_error_(builder_, " + methodName + "(builder_, level_, " + priority + "));");
        if (tailCall != null) g.out("result_ = " + tailCall + " && result_;");
        g.out("if (!" + opCall + ") break;");
        g.out("}");
      }
      else if (operator.type == OperatorType.UNARY_POSTFIX) {
        g.out("result_ = true;");
      }
      g.out("left_marker_.precede().done(" + elementType + ");");
      g.out("}");
    }
    g.out("if (pinned_) {");
    g.out("marker_.drop();");
    g.out("}");
    g.out("else {");
    g.out("marker_.rollbackTo();");
    g.out("break;");
    g.out("}");
    g.out("}");
    g.out("return result_;");
    g.out("}");

    // operators and tails
    THashSet<BnfExpression> visited = new THashSet<BnfExpression>();
    for (String opCall : sortedOpCalls) {
      for (OperatorInfo operator : opCalls.get(opCall)) {
        g.generateNodeChild(operator.rule, operator.operator, operator.rule.getName(), 0, visited);
        if (operator.tail != null) {
          g.generateNodeChild(operator.rule, operator.tail, operator.rule.getName(), 1, visited);
        }
      }
    }
  }

  private List<OperatorInfo> findOperators(Collection<OperatorInfo> list, OperatorType... types) {
    SmartList<OperatorInfo> result = new SmartList<OperatorInfo>();
    List<OperatorType> typeList = Arrays.asList(types);
    for (OperatorInfo o : list) {
      if (ContainerUtil.find(typeList, o.type) != null) {
        result.add(o);
      }
    }
    if (result.size() > 1) {
      addWarning("only first " + typeList + " definition will be used for: " + list.iterator().next().operator.getText());
    }
    return result;
  }

  public void addWarning(String text) {
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      //noinspection UseOfSystemOutOrSystemErr
      System.out.println(text);
    }
    else {
      GenerateAction.LOG_GROUP.createNotification(text, MessageType.WARNING).notify(myFile.getProject());
    }
  }
}
