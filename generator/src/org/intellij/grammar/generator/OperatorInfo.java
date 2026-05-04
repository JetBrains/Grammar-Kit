/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;

/**
 * @param tail null for postfix
 */
public record OperatorInfo(BnfRule rule, OperatorType type, BnfExpression operator, BnfExpression tail, BnfRule arg1, BnfRule arg2) {
  public OperatorInfo(BnfRule rule, OperatorType type, BnfExpression operator, BnfExpression tail) {
    this(rule, type, operator, tail, null, null);
  }

  @Override
  public @NotNull String toString() {
    return type + "(" + rule.getName() + ")";
  }
}
