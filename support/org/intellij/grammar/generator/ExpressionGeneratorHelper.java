/*
 * Copyright 2011-2013 Gregory Shrago
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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import gnu.trove.THashSet;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfRule;

import java.util.*;

import static org.intellij.grammar.generator.ExpressionHelper.OperatorInfo;
import static org.intellij.grammar.generator.ExpressionHelper.OperatorType;
import static org.intellij.grammar.generator.ParserGeneratorUtil.*;

/**
 * @author greg
 */
public class ExpressionGeneratorHelper {


  public static void generateExpressionRoot(ExpressionHelper.ExpressionInfo info, ParserGenerator g) {
    Map<String, List<OperatorInfo>> opCalls = new LinkedHashMap<String, List<OperatorInfo>>();
    for (BnfRule rule : info.priorityMap.keySet()) {
      OperatorInfo operator = info.operatorMap.get(rule);
      String opCall = g.generateNodeCall(info.rootRule, operator.operator, getNextName(operator.rule.getName(), 0), false);
      List<OperatorInfo> list = opCalls.get(opCall);
      if (list == null) opCalls.put(opCall, list = new ArrayList<OperatorInfo>(2));
      list.add(operator);
    }
    Set<String> sortedOpCalls = opCalls.keySet();

    for (String s : info.toString().split("\n")) {
      g.out("// " + s);
    }

    // main entry
    String methodName = info.rootRule.getName();
    String kernelMethodName = getNextName(methodName, 0);
    String frameName = quote(ParserGeneratorUtil.getRuleDisplayName(info.rootRule, true));
    g.out("public static boolean " + methodName + "(PsiBuilder builder_, int level_, int priority_) {");
    g.out("if (!recursion_guard_(builder_, level_, \"" + methodName + "\")) return false;");
    boolean fast = "consumeTokenFast".equals(getAttribute(info.rootRule, KnownAttribute.CONSUME_TOKEN_METHOD));
    if (fast && frameName != null) {
      g.out("addVariant(builder_, " + frameName + ");");
    }
    g.generateFirstCheck(info.rootRule, frameName, true);
    g.out("Marker marker_ = builder_.mark();");
    g.out("boolean result_ = false;");
    g.out("boolean pinned_ = false;");
    g.out("enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, " + frameName + ");");

    boolean first = true;
    for (String opCall : sortedOpCalls) {
      OperatorInfo operator = ContainerUtil.getFirstItem(findOperators(opCalls.get(opCall), OperatorType.ATOM, OperatorType.PREFIX));
      if (operator == null) continue;
      String nodeCall = g.generateNodeCall(operator.rule, null, operator.rule.getName());
      g.out((first ? "" : "if (!result_) ") + "result_ = " + nodeCall + ";");
      first = false;
    }

    g.out("pinned_ = result_;");
    g.out("result_ = result_ && " + kernelMethodName + "(builder_, level_ + 1, priority_);");
    g.out("if (!result_ && !pinned_) {");
    g.out("marker_.rollbackTo();");
    g.out("}");
    g.out("else {");
    g.out("marker_.drop();");
    g.out("}");
    g.out("result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);");
    g.out("return result_ || pinned_;");
    g.out("}");
    g.newLine();

    // kernel
    g.out("public static boolean " + kernelMethodName + "(PsiBuilder builder_, int level_, int priority_) {");
    g.out("if (!recursion_guard_(builder_, level_, \"" + kernelMethodName + "\")) return false;");
    g.out("boolean result_ = true;");
    g.out("while (true) {");
    g.out("Marker left_marker_ = (Marker) builder_.getLatestDoneMarker();");
    g.out("if (!invalid_left_marker_guard_(builder_, left_marker_, \"" + kernelMethodName + "\")) return false;");

    first = true;
    for (String opCall : sortedOpCalls) {
      OperatorInfo operator =
        ContainerUtil.getFirstItem(findOperators(opCalls.get(opCall), OperatorType.BINARY, OperatorType.N_ARY, OperatorType.POSTFIX));
      if (operator == null) continue;
      int priority = info.getPriority(operator.rule);
      String substCheck = "";
      if (operator.substitutor != null) {
        substCheck = " && ((LighterASTNode)left_marker_).getTokenType() == " + ParserGeneratorUtil.getElementType(operator.substitutor);
      }
      if (first) g.out("Marker marker_ = builder_.mark();");
      g.out((first ? "" : "else ") + "if (priority_ < " + priority  + substCheck + " && " + opCall + ") {");
      first = false;
      String elementType = ParserGeneratorUtil.getElementType(operator.rule);
      boolean rightAssociative = ParserGeneratorUtil.getAttribute(operator.rule, KnownAttribute.RIGHT_ASSOCIATIVE);
      String tailCall =
        operator.tail == null ? null : g.generateNodeCall(operator.rule, operator.tail, getNextName(operator.rule.getName(), 1), true);
      if (operator.type == OperatorType.BINARY) {
        g.out(
          "result_ = report_error_(builder_, " + methodName + "(builder_, level_, " + (rightAssociative ? priority - 1 : priority) + "));");
        if (tailCall != null) g.out("result_ = " + tailCall + " && result_;");
      }
      else if (operator.type == OperatorType.N_ARY) {
        g.out("while (true) {");
        g.out("result_ = report_error_(builder_, " + methodName + "(builder_, level_, " + priority + "));");
        if (tailCall != null) g.out("result_ = report_error_(builder_, " + tailCall + ") && result_;");
        g.out("if (!" + opCall + ") break;");
        g.out("}");
      }
      else if (operator.type == OperatorType.POSTFIX) {
        g.out("result_ = true;");
      }
      g.out("marker_.drop();");
      g.out("left_marker_.precede().done(" + elementType + ");");
      g.out("}");
    }
    if (first) {
      g.out("// no BINARY or POSTFIX operators present");
      g.out("break;");
    }
    else {
      g.out("else {");
      g.out("marker_.rollbackTo();");
      g.out("break;");
      g.out("}");
    }
    g.out("}");
    g.out("return result_;");
    g.out("}");

    // operators and tails
    THashSet<BnfExpression> visited = new THashSet<BnfExpression>();
    for (String opCall : sortedOpCalls) {
      for (OperatorInfo operator : opCalls.get(opCall)) {
        if (operator.type == OperatorType.ATOM) {
          g.newLine();
          g.generateNode(operator.rule, operator.rule.getExpression(), operator.rule.getName(), visited);
          continue;
        }
        else if (operator.type == OperatorType.PREFIX) {
          g.newLine();
          String operatorFuncName = operator.rule.getName();
          g.out("public static boolean " + operatorFuncName + "(PsiBuilder builder_, int level_) {");
          g.out("if (!recursion_guard_(builder_, level_, \"" + operatorFuncName + "\")) return false;");
          g.generateFirstCheck(operator.rule, frameName, false);
          g.out("boolean result_ = false;");
          g.out("boolean pinned_ = false;");
          g.out("Marker marker_ = builder_.mark();");
          g.out("enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);");

          String elementType = ParserGeneratorUtil.getElementType(operator.rule);
          String tailCall =
            operator.tail == null ? null : g.generateNodeCall(operator.rule, operator.tail, getNextName(operator.rule.getName(), 1), true);

          g.out("result_ = "+opCall+";");
          g.out("pinned_ = result_;");
          Integer substitutorPriority = operator.substitutor == null ? null : info.getPriority(operator.substitutor);
          int rulePriority = info.getPriority(operator.rule);
          int priority =
            substitutorPriority == null ? (rulePriority == info.nextPriority - 1 ? -1 : rulePriority) : substitutorPriority;
          g.out("result_ = pinned_ && " + methodName + "(builder_, level_, " + priority + ") && result_;");
          if (tailCall != null) {
            g.out("result_ = pinned_ && report_error_(builder_, " + tailCall + ") && result_;");
          }
          g.out("if (result_ || pinned_) {");
          if (StringUtil.isNotEmpty(elementType)) {
            g.out("marker_.done(" + elementType + ");");
          }
          else {
            g.out("marker_.drop();");
          }
          g.out("}");
          g.out("else {");
          g.out("marker_.rollbackTo();");
          g.out("}");
          g.out("result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);");
          g.out("return result_ || pinned_;");
          g.out("}");
        }
        g.generateNodeChild(operator.rule, operator.operator, operator.rule.getName(), 0, visited);
        if (operator.tail != null) {
          g.generateNodeChild(operator.rule, operator.tail, operator.rule.getName(), 1, visited);
        }
      }
    }
  }

  public static List<OperatorInfo> findOperators(Collection<OperatorInfo> list, OperatorType... types) {
    SmartList<OperatorInfo> result = new SmartList<OperatorInfo>();
    List<OperatorType> typeList = Arrays.asList(types);
    for (OperatorInfo o : list) {
      if (ContainerUtil.find(typeList, o.type) != null) {
        result.add(o);
      }
    }
    if (result.size() > 1) {
      OperatorInfo info = list.iterator().next();
      addWarning(info.rule.getProject(), "only first definition will be used for '" + info.operator.getText() + "': " + typeList);
    }
    return result;
  }

  public static ExpressionHelper.ExpressionInfo getInfoForExpressionParsing(ExpressionHelper expressionHelper, BnfRule rule) {
    ExpressionHelper.ExpressionInfo expressionInfo = expressionHelper.getExpressionInfo(rule);
    OperatorInfo operatorInfo = expressionInfo == null ? null : expressionInfo.operatorMap.get(rule);
    if (expressionInfo != null && (operatorInfo == null || operatorInfo.type != OperatorType.ATOM &&
                                                           operatorInfo.type != OperatorType.PREFIX)) {
      return expressionInfo;
    }
    return null;
  }
}
