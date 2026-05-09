/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.SmartList;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.java.JavaBnfConstants;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.BnfRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.lang.String.format;
import static org.intellij.grammar.generator.ParserGeneratorUtil.ConsumeType;
import static org.intellij.grammar.generator.ParserGeneratorUtil.quote;
import static org.intellij.grammar.psi.BnfAttributes.getAttribute;

/**
 * Java-target emitter for operator-precedence (expression) rules.
 * <p>
 * BNF expression rules — those marked via {@link ExpressionInfo} — can't be parsed by the
 * straight recursive descent that {@link Generator#generateNode} produces; they need
 * precedence climbing. This helper renders the precedence-climbing parser for one such rule:
 * a recursive entry function that consumes atoms and prefix operators, a kernel loop that
 * left-folds binary/n-ary/postfix operators while respecting priority and right-associativity,
 * and per-operator helper functions. {@link KotlinParserGenerator#generateExpressionRoot} is
 * the Kotlin counterpart; the two share concept but not code.
 * <p>
 * Methods are static — instances are never created. {@link #fixForcedConsumeType} and
 * {@link #getInfoForExpressionParsing} also serve as cross-module hooks used by the regular
 * node emitters when they encounter rules participating in an expression.
 */
public final class ExpressionGeneratorHelper {

  /**
   * Consume type forced inside expression rules. {@code SMART} is used so that the runtime
   * uses the {@code consumeTokenSmart} family of methods, which is required to avoid spurious
   * "expected X" errors during precedence climbing where many alternatives are tried.
   */
  static final ConsumeType CONSUME_TYPE_OVERRIDE = ConsumeType.SMART;

  /**
   * Groups every operator of {@code info}'s expression rule by the rendered text of its
   * opening operator call. Operators that share an opening token end up in the same kernel-loop
   * branch, which is how the generator picks the highest-priority match among ambiguous starts.
   */
  private static @NotNull Map<String, List<OperatorInfo>> buildCallMap(@NotNull ExpressionInfo info,
                                                                       @NotNull JavaParserGenerator g,
                                                                       @NotNull NameRenderer R) {
    Map<String, List<OperatorInfo>> opCalls = new LinkedHashMap<>();
    for (BnfRule rule : info.priorityMap.keySet()) {
      OperatorInfo operator = info.operatorMap.get(rule);
      String opCall = g.generateNodeCall(info.rootRule,
                                         operator.operator(), 
                                         R.getNextName(R.getFuncName(operator.rule()), 0), 
                                         CONSUME_TYPE_OVERRIDE).render(R);
      opCalls.computeIfAbsent(opCall, k -> new ArrayList<>(2)).add(operator);
    }
    return opCalls;
  }

  /**
   * Emits the full Java parser for one expression rule:
   * <ol>
   *   <li>The entry function {@code <rule>(builder, level, priority)} — consumes atoms and
   *       prefix operators, then delegates to the kernel.</li>
   *   <li>The kernel function {@code <rule>_0(...)} — a {@code while(true)} loop over
   *       binary/n-ary/postfix operators, gated on {@code priority < operatorPriority} (with
   *       right-associativity decreasing the bound by one) and on {@code leftMarkerIs(...)}
   *       when {@code arg1} restricts the left-hand element type.</li>
   *   <li>Per-operator helpers — atom rule bodies via {@link Generator#generateNode}, and
   *       dedicated functions for prefix operators since they need their own section + recursion
   *       into the entry function with the right priority.</li>
   * </ol>
   * Warns when multiple operators share a starting opCall: only the first definition wins.
   */
  public static void generateExpressionRoot(@NotNull ExpressionInfo info,
                                            @NotNull JavaParserGenerator g,
                                            @NotNull NameRenderer R) {
    Map<String, List<OperatorInfo>> opCalls = buildCallMap(info, g, R);
    Set<String> sortedOpCalls = opCalls.keySet();

    for (String s : info.toString().split("\n")) {
      g.out("// " + s);
    }

    // main entry
    String methodName = R.getFuncName(info.rootRule);
    String kernelMethodName = R.getNextName(methodName, 0);
    String frameName = quote(R.getRuleDisplayName(info.rootRule, true));
    String shortPB = g.shorten(JavaBnfConstants.PSI_BUILDER_CLASS);
    String shortMarker = !g.G.generateFQN ? "Marker" : JavaBnfConstants.PSI_BUILDER_CLASS + ".Marker";
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
        g.addWarning("only first definition will be used for '" + operator.operator().getText() + "': " + operators);
      }
      String nodeCall = g.generateNodeCall(operator.rule(), null, operator.rule().getName()).render(R);
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
        g.addWarning("only first definition will be used for '" + operator.operator().getText() + "': " + operators);
      }
      int priority = info.getPriority(operator.rule());
      int arg2Priority = operator.arg2() == null ? -1 : info.getPriority(operator.arg2());
      int argPriority = arg2Priority == -1 ? priority : arg2Priority - 1;

      String substCheck = "";
      if (operator.arg1() != null) {
        substCheck = format(" && leftMarkerIs(%s, %s)", g.N.builder, g.getElementType(operator.arg1()));
      }
      g.out("%sif (%s < %d%s && %s) {", first ? "" : "else ", g.N.priority, priority, substCheck, opCall);
      first = false;
      String elementType = g.getElementType(operator.rule());
      boolean rightAssociative = getAttribute(operator.rule(), KnownAttribute.RIGHT_ASSOCIATIVE);
      String tailCall = operator.tail() == null ? null : g.generateNodeCall(operator.rule(), 
                                                                            operator.tail(), 
                                                                            R.getNextName(R.getFuncName(operator.rule()), 1), 
                                                                            ConsumeType.DEFAULT).render(R);
      if (operator.type() == OperatorType.BINARY) {
        String argCall = format("%s(%s, %s, %d)", methodName, g.N.builder, g.N.level, rightAssociative ? argPriority - 1 : argPriority);
        g.out("%s = %s;", g.N.result, tailCall == null ? argCall : format("report_error_(%s, %s)", g.N.builder, argCall));
        if (tailCall != null) g.out("%s = %s && %s;", g.N.result, tailCall, g.N.result);
      }
      else if (operator.type() == OperatorType.N_ARY) {
        boolean checkEmpty = info.checkEmpty.contains(operator);
        if (checkEmpty) {
          g.out("int %s = current_position_(%s);", g.N.pos, g.N.builder);
        }
        g.out("while (true) {");
        g.out("%s = report_error_(%s, %s(%s, %s, %d));", g.N.result, g.N.builder, methodName, g.N.builder, g.N.level, argPriority);
        if (tailCall != null) g.out("%s = %s && %s;", g.N.result, tailCall, g.N.result);
        g.out("if (!%s) break;", opCall);
        if (checkEmpty) {
          g.out("if (!empty_element_parsed_guard_(%s, \"%s\", %s)) break;", g.N.builder, operator.rule().getName(), g.N.pos);
          g.out("%s = current_position_(%s);", g.N.pos, g.N.builder);
        }
        g.out("}");
      }
      else if (operator.type() == OperatorType.POSTFIX) {
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
        if (operator.type() == OperatorType.ATOM) {
          if (BnfRules.isExternal(operator.rule())) continue;
          g.newLine();
          g.generateNode(operator.rule(), operator.rule().getExpression(), R.getFuncName(operator.rule()), visited);
          continue;
        }
        else if (operator.type() == OperatorType.PREFIX) {
          g.newLine();
          String operatorFuncName = operator.rule().getName();
          g.out("public static boolean %s(%s %s, int %s) {", operatorFuncName, shortPB, g.N.builder, g.N.level);
          g.out("if (!recursion_guard_(%s, %s, \"%s\")) return false;", g.N.builder, g.N.level, operatorFuncName);
          g.generateFirstCheck(operator.rule(), frameName, false);
          g.out("boolean %s, %s;", g.N.result, g.N.pinned);
          g.out("%s %s = enter_section_(%s, %s, _NONE_, null);", shortMarker, g.N.marker, g.N.builder, g.N.level);

          String elementType = g.getElementType(operator.rule());
          String tailCall = operator.tail() == null ? null : g.generateNodeCall(
            operator.rule(), operator.tail(), R.getNextName(R.getFuncName(operator.rule()), 1), ConsumeType.DEFAULT
          ).render(R);

          g.out("%s = %s;", g.N.result, opCall);
          g.out("%s = %s;", g.N.pinned, g.N.result);
          int priority = info.getPriority(operator.rule());
          int arg1Priority = operator.arg1() == null ? -1 : info.getPriority(operator.arg1());
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
        g.generateNodeChild(operator.rule(), operator.operator(), R.getFuncName(operator.rule()), 0, visited);
        if (operator.tail() != null) {
          g.generateNodeChild(operator.rule(), operator.tail(), operator.rule().getName(), 1, visited);
        }
      }
    }
  }

  /** Returns the subset of {@code operatorInfos} whose {@link OperatorInfo#type()} is one of {@code types}, preserving input order. */
  public static @NotNull List<OperatorInfo> findOperators(@NotNull Collection<OperatorInfo> operatorInfos, OperatorType... types) {
    final var result = new SmartList<OperatorInfo>();
    for (OperatorInfo o : operatorInfos) {
      if (ArrayUtil.contains(o.type(), types)) {
        result.add(o);
      }
    }
    return result;
  }

  /**
   * If a call to {@code rule} should go through the precedence-climbing entry instead of a
   * direct method call, returns the enclosing {@link ExpressionInfo}; otherwise {@code null}.
   * Atoms and prefix operators stay on the direct path because the kernel loop already calls
   * the entry recursively for their right-hand sides.
   */
  public static @Nullable ExpressionInfo getInfoForExpressionParsing(@NotNull ExpressionHelper expressionHelper, @Nullable BnfRule rule) {
    final var expressionInfo = expressionHelper.getExpressionInfo(rule);
    final var operatorInfo = expressionInfo == null ? null : expressionInfo.operatorMap.get(rule);
    if (expressionInfo != null && (operatorInfo == null || operatorInfo.type() != OperatorType.ATOM &&
                                                           operatorInfo.type() != OperatorType.PREFIX)) {
      return expressionInfo;
    }
    return null;
  }

  /**
   * Decides whether the consume type for {@code node} (or for the rule itself, when
   * {@code node} is {@code null}) should be forced to {@link #CONSUME_TYPE_OVERRIDE} because
   * it lives inside an expression-rule operator. {@code defValue} short-circuits the check
   * when a caller has already resolved the consume type.
   */
  static @Nullable ConsumeType fixForcedConsumeType(@NotNull ExpressionHelper expressionHelper,
                                                    @NotNull BnfRule rule,
                                                    @Nullable BnfExpression node,
                                                    @Nullable ConsumeType defValue) {
    if (defValue != null) return defValue;
    ExpressionInfo expressionInfo = expressionHelper.getExpressionInfo(rule);
    OperatorInfo operatorInfo = expressionInfo == null ? null : expressionInfo.operatorMap.get(rule);
    if (operatorInfo != null) {
      if (node == null) {
        return operatorInfo.type() == OperatorType.PREFIX || operatorInfo.type() == OperatorType.ATOM ?
               CONSUME_TYPE_OVERRIDE : null;
      }
      if (PsiTreeUtil.isAncestor(operatorInfo.operator(), node, false)) return CONSUME_TYPE_OVERRIDE;
      for (BnfExpression o : ExpressionHelper.getOriginalExpressions(operatorInfo.operator())) {
        if (PsiTreeUtil.isAncestor(o, node, false)) {
          return CONSUME_TYPE_OVERRIDE;
        }
      }
    }
    return null;
  }
}
