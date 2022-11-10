/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.SmartList;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.lang.String.format;
import static org.intellij.grammar.generator.BnfConstants.PSI_BUILDER_CLASS;
import static org.intellij.grammar.generator.ExpressionHelper.OperatorInfo;
import static org.intellij.grammar.generator.ExpressionHelper.OperatorType;
import static org.intellij.grammar.generator.ParserGeneratorUtil.*;

/**
 * @author greg
 */
public class ExpressionGeneratorHelper {

  private static final ConsumeType CONSUME_TYPE_OVERRIDE = ConsumeType.SMART;

  private static @NotNull Map<String, List<OperatorInfo>> buildCallMap(ExpressionHelper.ExpressionInfo info, ParserGenerator g) {
    Map<String, List<OperatorInfo>> opCalls = new LinkedHashMap<>();
    for (BnfRule rule : info.priorityMap.keySet()) {
      OperatorInfo operator = info.operatorMap.get(rule);
      String opCall = g.generateNodeCall(
        info.rootRule, operator.operator, getNextName(getFuncName(operator.rule), 0), CONSUME_TYPE_OVERRIDE
      ).render(g.N);
      opCalls.computeIfAbsent(opCall, k -> new ArrayList<>(2)).add(operator);
    }
    return opCalls;
  }

  public static void generateExpressionRoot(ExpressionHelper.ExpressionInfo info, ParserGenerator g) {
    Map<String, List<OperatorInfo>> opCalls = buildCallMap(info, g);
    Set<String> sortedOpCalls = opCalls.keySet();

    for (String s : info.toString().split("\n")) {
      g.out("// " + s);
    }

    // main entry
    String methodName = getFuncName(info.rootRule);
    String kernelMethodName = getNextName(methodName, 0);
    String frameName = quote(getRuleDisplayName(info.rootRule, true));
    String shortPB = g.shorten(PSI_BUILDER_CLASS);
    String shortMarker = !g.G.generateFQN ? "Marker" : PSI_BUILDER_CLASS + ".Marker";
    g.out("public static boolean %s(%s %s, int %s, int %s) {", methodName, shortPB, g.N.builder, g.N.level, g.N.priority);
    g.out("if (!recursion_guard_(%s, %s, \"%s\")) return false;", g.N.builder, g.N.level, methodName);

    if (frameName != null) {
      g.out("addVariant(%s, %s);", g.N.builder, frameName);
    }
    g.generateFirstCheck(info.rootRule, frameName, true);
    g.out("boolean %s, %s;", g.N.result, g.N.pinned);
    g.out("%s %s = enter_section_(%s, %s, _NONE_, %s);", shortMarker, g.N.marker, g.N.builder, g.N.level, frameName);

    boolean first = true;
    for (String opCall : sortedOpCalls) {
      List<OperatorInfo> operators = findOperators(opCalls.get(opCall), OperatorType.ATOM, OperatorType.PREFIX);
      if (operators.isEmpty()) continue;
      OperatorInfo operator = operators.get(0);
      if (operators.size() > 1) {
        g.addWarning("only first definition will be used for '" + operator.operator.getText() + "': " + operators);
      }
      String nodeCall = g.generateNodeCall(operator.rule, null, operator.rule.getName()).render(g.N);
      g.out("%s%s = %s;", first ? "" : format("if (!%s) ", g.N.result), g.N.result, nodeCall);
      first = false;
    }

    g.out("%s = %s;", g.N.pinned, g.N.result);
    g.out("%s = %s && %s(%s, %s + 1, %s);", g.N.result, g.N.result, kernelMethodName, g.N.builder, g.N.level, g.N.priority);
    g.out("exit_section_(%s, %s, %s, null, %s, %s, null);", g.N.builder, g.N.level, g.N.marker, g.N.result, g.N.pinned);
    g.out("return %s || %s;", g.N.result, g.N.pinned);
    g.out("}");
    g.newLine();

    // kernel
    g.out("public static boolean %s(%s %s, int %s, int %s) {", kernelMethodName, shortPB, g.N.builder, g.N.level, g.N.priority);
    g.out("if (!recursion_guard_(%s, %s, \"%s\")) return false;", g.N.builder, g.N.level, kernelMethodName);
    g.out("boolean %s = true;", g.N.result);
    g.out("while (true) {");
    g.out("%s %s = enter_section_(%s, %s, _LEFT_, null);", shortMarker, g.N.marker, g.N.builder, g.N.level);

    first = true;
    for (String opCall : sortedOpCalls) {
      List<OperatorInfo> operators = findOperators(opCalls.get(opCall), OperatorType.BINARY, OperatorType.N_ARY, OperatorType.POSTFIX);
      if (operators.isEmpty()) continue;
      OperatorInfo operator = operators.get(0);
      if (operators.size() > 1) {
        g.addWarning("only first definition will be used for '" + operator.operator.getText() + "': " + operators);
      }
      int priority = info.getPriority(operator.rule);
      int arg2Priority = operator.arg2 == null ? -1 : info.getPriority(operator.arg2);
      int argPriority = arg2Priority == -1 ? priority : arg2Priority - 1;

      String substCheck = "";
      if (operator.arg1 != null) {
        substCheck = format(" && leftMarkerIs(%s, %s)", g.N.builder, g.getElementType(operator.arg1));
      }
      g.out("%sif (%s < %d%s && %s) {", first ? "" : "else ", g.N.priority, priority, substCheck, opCall);
      first = false;
      String elementType = g.getElementType(operator.rule);
      boolean rightAssociative = getAttribute(operator.rule, KnownAttribute.RIGHT_ASSOCIATIVE);
      String tailCall = operator.tail == null ? null : g.generateNodeCall(
        operator.rule, operator.tail, getNextName(getFuncName(operator.rule), 1), ConsumeType.DEFAULT
      ).render(g.N);
      if (operator.type == OperatorType.BINARY) {
        String argCall = format("%s(%s, %s, %d)", methodName, g.N.builder, g.N.level, rightAssociative ? argPriority - 1 : argPriority);
        g.out("%s = %s;", g.N.result, tailCall == null ? argCall : format("report_error_(%s, %s)", g.N.builder, argCall));
        if (tailCall != null) g.out("%s = %s && %s;", g.N.result, tailCall, g.N.result);
      }
      else if (operator.type == OperatorType.N_ARY) {
        boolean checkEmpty = info.checkEmpty.contains(operator);
        if (checkEmpty) {
          g.out("int %s = current_position_(%s);", g.N.pos, g.N.builder);
        }
        g.out("while (true) {");
        g.out("%s = report_error_(%s, %s(%s, %s, %d));", g.N.result, g.N.builder, methodName, g.N.builder, g.N.level, argPriority);
        if (tailCall != null) g.out("%s = %s && %s;", g.N.result, tailCall, g.N.result);
        g.out("if (!%s) break;", opCall);
        if (checkEmpty) {
          g.out("if (!empty_element_parsed_guard_(%s, \"%s\", %s)) break;", g.N.builder, operator.rule.getName(), g.N.pos);
          g.out("%s = current_position_(%s);", g.N.pos, g.N.builder);
        }
        g.out("}");
      }
      else if (operator.type == OperatorType.POSTFIX) {
        g.out("%s = true;", g.N.result);
      }
      g.out("exit_section_(%s, %s, %s, %s, %s, true, null);", g.N.builder, g.N.level, g.N.marker, elementType, g.N.result);
      g.out("}");
    }
    if (first) {
      g.out("// no BINARY or POSTFIX operators present");
      g.out("break;");
    }
    else {
      g.out("else {");
      g.out("exit_section_(%s, %s, %s, null, false, false, null);", g.N.builder, g.N.level, g.N.marker);
      g.out("break;");
      g.out("}");
    }
    g.out("}");
    g.out("return %s;", g.N.result);
    g.out("}");

    // operators and tails
    Set<BnfExpression> visited = new HashSet<>();
    for (String opCall : sortedOpCalls) {
      for (OperatorInfo operator : opCalls.get(opCall)) {
        if (operator.type == OperatorType.ATOM) {
          if (Rule.isExternal(operator.rule)) continue;
          g.newLine();
          g.generateNode(operator.rule, operator.rule.getExpression(), getFuncName(operator.rule), visited);
          continue;
        }
        else if (operator.type == OperatorType.PREFIX) {
          g.newLine();
          String operatorFuncName = operator.rule.getName();
          g.out("public static boolean %s(%s %s, int %s) {", operatorFuncName, shortPB, g.N.builder, g.N.level);
          g.out("if (!recursion_guard_(%s, %s, \"%s\")) return false;", g.N.builder, g.N.level, operatorFuncName);
          g.generateFirstCheck(operator.rule, frameName, false);
          g.out("boolean %s, %s;", g.N.result, g.N.pinned);
          g.out("%s %s = enter_section_(%s, %s, _NONE_, null);", shortMarker, g.N.marker, g.N.builder, g.N.level);

          String elementType = g.getElementType(operator.rule);
          String tailCall = operator.tail == null ? null : g.generateNodeCall(
            operator.rule, operator.tail, getNextName(getFuncName(operator.rule), 1), ConsumeType.DEFAULT
          ).render(g.N);

          g.out("%s = %s;", g.N.result, opCall);
          g.out("%s = %s;", g.N.pinned, g.N.result);
          int priority = info.getPriority(operator.rule);
          int arg1Priority = operator.arg1 == null ? -1 : info.getPriority(operator.arg1);
          int argPriority = arg1Priority == -1 ? (priority == info.nextPriority - 1 ? -1 : priority) : arg1Priority - 1;
          g.out("%s = %s && %s(%s, %s, %d);", g.N.result, g.N.pinned, methodName, g.N.builder, g.N.level, argPriority);
          if (tailCall != null) {
            g.out("%s = %s && report_error_(%s, %s) && %s;", g.N.result, g.N.pinned, g.N.builder, tailCall, g.N.result);
          }
          String elementTypeRef = StringUtil.isNotEmpty(elementType) ? elementType : "null";
          g.out("exit_section_(%s, %s, %s, %s, %s, %s, null);", g.N.builder, g.N.level, g.N.marker, elementTypeRef,
                g.N.result, g.N.pinned);
          g.out("return %s || %s;", g.N.result, g.N.pinned);
          g.out("}");
        }
        g.generateNodeChild(operator.rule, operator.operator, getFuncName(operator.rule), 0, visited);
        if (operator.tail != null) {
          g.generateNodeChild(operator.rule, operator.tail, operator.rule.getName(), 1, visited);
        }
      }
    }
  }

  public static @NotNull List<OperatorInfo> findOperators(Collection<OperatorInfo> list, OperatorType... types) {
    List<OperatorInfo> result = new SmartList<>();
    for (OperatorInfo o : list) {
      if (ArrayUtil.contains(o.type, types)) {
        result.add(o);
      }
    }
    return result;
  }

  public static @Nullable ExpressionHelper.ExpressionInfo getInfoForExpressionParsing(ExpressionHelper expressionHelper, BnfRule rule) {
    ExpressionHelper.ExpressionInfo expressionInfo = expressionHelper.getExpressionInfo(rule);
    OperatorInfo operatorInfo = expressionInfo == null ? null : expressionInfo.operatorMap.get(rule);
    if (expressionInfo != null && (operatorInfo == null || operatorInfo.type != OperatorType.ATOM &&
                                                           operatorInfo.type != OperatorType.PREFIX)) {
      return expressionInfo;
    }
    return null;
  }

  public static @Nullable ConsumeType fixForcedConsumeType(@NotNull ExpressionHelper expressionHelper,
                                                           @NotNull BnfRule rule,
                                                           @Nullable BnfExpression node,
                                                           @Nullable ConsumeType defValue) {
    if (defValue != null) return defValue;
    ExpressionHelper.ExpressionInfo expressionInfo = expressionHelper.getExpressionInfo(rule);
    OperatorInfo operatorInfo = expressionInfo == null ? null : expressionInfo.operatorMap.get(rule);
    if (operatorInfo != null) {
      if (node == null) {
        return operatorInfo.type == OperatorType.PREFIX || operatorInfo.type == OperatorType.ATOM ?
               CONSUME_TYPE_OVERRIDE : null;
      }
      if (PsiTreeUtil.isAncestor(operatorInfo.operator, node, false)) return CONSUME_TYPE_OVERRIDE;
      for (BnfExpression o : ExpressionHelper.getOriginalExpressions(operatorInfo.operator)) {
        if (PsiTreeUtil.isAncestor(o, node, false)) {
          return CONSUME_TYPE_OVERRIDE;
        }
      }
    }
    return null;
  }
}
