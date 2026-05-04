/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.KnownAttribute;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

import static org.intellij.grammar.psi.BnfAst.compilePattern;
import static org.intellij.grammar.psi.BnfAttributes.getAttribute;
import static org.intellij.grammar.psi.BnfTypes.BNF_SEQUENCE;

public class PinMatcher {

  public final BnfRule rule;
  public final String funcName;
  public final Object pinValue;
  private final int pinIndex;
  private final Pattern pinPattern;

  public PinMatcher(@NotNull BnfRule rule, @NotNull IElementType type, @NotNull String funcName) {
    this.rule = rule;
    this.funcName = funcName;
    pinValue = type == BNF_SEQUENCE ? getAttribute(rule, KnownAttribute.PIN, funcName) : null;
    pinIndex = pinValue instanceof Integer ? (Integer)pinValue : -1;
    pinPattern = pinValue instanceof String ? compilePattern((String)pinValue) : null;
  }

  public boolean active() {
    return pinIndex > -1 || pinPattern != null;
  }

  public boolean matches(int i, @NotNull BnfExpression child) {
    return i == pinIndex - 1 || pinPattern != null && pinPattern.matcher(child.getText()).matches();
  }

  public boolean shouldGenerate(List<BnfExpression> children) {
    // do not check last expression, last item pin is trivial
    for (int i = 0, size = children.size(); i < size - 1; i++) {
      if (matches(i, children.get(i))) return true;
    }
    return false;
  }
}
