/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.util.PairConsumer;
import org.intellij.grammar.psi.BnfRule;

import java.util.*;

public class ExpressionInfo {
  public final BnfRule rootRule;
  public final Map<BnfRule, Integer> priorityMap = new LinkedHashMap<>();
  public final Map<BnfRule, OperatorInfo> operatorMap = new LinkedHashMap<>();
  public final Map<BnfRule, Integer> privateGroups = new HashMap<>();
  public int nextPriority;
  public final Set<OperatorInfo> checkEmpty = new HashSet<>();

  public ExpressionInfo(BnfRule rootRule) {
    this.rootRule = rootRule;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Expression root: " + rootRule.getName());
    sb.append("\nOperator priority table:\n");
    return dumpPriorityTable(sb).toString();
  }

  public StringBuilder dumpPriorityTable(StringBuilder sb) {
    return dumpPriorityTable(sb, StringBuilder::append);
  }

  public StringBuilder dumpPriorityTable(StringBuilder sb, PairConsumer<? super StringBuilder, ? super OperatorInfo> printer) {
    for (int i = 0; i < nextPriority; i++) {
      sb.append(i).append(":");
      int count = 0;
      for (BnfRule rule : priorityMap.keySet()) {
        if (priorityMap.get(rule) == i) {
          if ((count++ % 4) == 0 && count > 1) sb.append("\n  ");
          sb.append(" ");
          printer.consume(sb, operatorMap.get(rule));
        }
      }
      sb.append("\n");
    }
    return sb;
  }

  public int getPriority(BnfRule subRule) {
    if (subRule == rootRule) return 0;
    Integer op = priorityMap.get(subRule);
    if (op != null) return op;
    Integer group = privateGroups.get(subRule);
    return group == null ? -1 : group;
  }
}
