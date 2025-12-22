/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.java;

import com.intellij.openapi.util.text.StringUtil;
import org.intellij.grammar.generator.Renderer;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class JavaRenderer extends Renderer {
  public static final @NotNull JavaRenderer INSTANCE = new JavaRenderer();
  // shared with kotlin renderer
  public static final @NotNull Set<@NotNull String> JAVA_RESERVED = Set.of(
    // reserved keywords
    "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
    "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if",
    "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private",
    "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
    "throw", "throws", "transient", "try", "void", "volatile", "while",
    // contextual keywords
    "exports", "module", "non-sealed", "open", "opens", "permits", "provides", "record", "requires", "sealed",
    "to", "transitive", "uses", "var", "when", "with", "yield"
  );


  @Override
  public @NotNull String getNextName(@NotNull String funcName, int i) {
    return unwrapFuncName(funcName) + "_" + i;
  }

  @Override
  public @NotNull String getFuncName(@NotNull BnfRule rule) {
    String name = CommonRendererUtils.getBaseName(rule.getName());
    return JAVA_RESERVED.contains(name) ? name + RESERVED_SUFFIX : name;
  }

  @Override
  public @NotNull String unwrapFuncName(@NotNull String funcName) {
    return StringUtil.trimEnd(funcName, RESERVED_SUFFIX);
  }

  @Override
  public @NotNull String getWrapperParserMetaMethodName(@NotNull String nextName) {
    return CommonRendererUtils.getBaseName(nextName) + RESERVED_SUFFIX;
  }

  public @Nullable String getRuleDisplayName(@NotNull BnfRule rule, boolean force) {
    final var s = getRuleDisplayNameRaw(rule, force);
    return StringUtil.isEmpty(s) ? null : "<" + s + ">";
  }
}
