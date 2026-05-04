/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.codeStyle.NameUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.util.Case;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.intellij.grammar.psi.BnfAttributes.getAttribute;

public abstract class NameRendererBase implements NameRenderer {
  protected @Nullable String getRuleDisplayNameRaw(@NotNull BnfRule rule, boolean force) {
    String name = getAttribute(rule, KnownAttribute.NAME);
    BnfRule realRule = rule;
    if (name != null) {
      realRule = ((BnfFile)rule.getContainingFile()).getRule(name);
      if (realRule != null && realRule != rule) {
        name = getAttribute(realRule, KnownAttribute.NAME);
      }
    }
    if (name != null || (!force && realRule == rule)) {
      return name;
    }
    else {
      final var unwrapped = unwrapFuncName(getFuncName(realRule));
      final var parts = NameUtil.splitNameIntoWords(unwrapped);
      return Case.LOWER.apply(StringUtil.join(parts, " "));
    }
  }

  /**
   * Undoes the effect of the {@link NameRenderer#getFuncName} method.
   */
  protected abstract @NotNull String unwrapFuncName(@NotNull String funcName);
}
