/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.Nullable;

public class OperatorInfo {
  public final BnfRule rule;
  public final OperatorType type;
  public final BnfExpression operator;
  public final BnfExpression tail; // null for postfix
  public final BnfRule arg1;
  public final BnfRule arg2;

  public OperatorInfo(BnfRule rule, OperatorType type, BnfExpression operator, BnfExpression tail) {
    this(rule, type, operator, tail, null, null);
  }

  public OperatorInfo(BnfRule rule, OperatorType type, BnfExpression operator, BnfExpression tail,
                      @Nullable BnfRule arg1,
                      @Nullable BnfRule arg2) {
    if (operator == null) {
      throw new AssertionError(rule + ": operator must not be null");
    }
    this.rule = rule;
    this.type = type;
    this.operator = operator;
    this.tail = tail;
    this.arg1 = arg1;
    this.arg2 = arg2;
  }

  @Override
  public String toString() {
    return type + "(" + rule.getName() + ")";
  }
}
