/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.kotlin;

import com.intellij.openapi.util.text.StringUtil;
import org.intellij.grammar.generator.Renderer;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static org.intellij.grammar.generator.ParserGeneratorUtil.quote;
import static org.intellij.grammar.generator.java.JavaRenderer.JAVA_RESERVED;

public final class KotlinRenderer extends Renderer {
  private final static String RESERVED_KOTLIN_SUFFIX = "__";
  
  private final @NotNull Set<@NotNull String> KOTLIN_RESERVED = Set.of(
    // hard keywords
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun",
    "if", "in", "interface", "is", "null", "object", "package", "return",
    "super", "this", "throw", "true", "try", "typealias", "typeof", "val",
    "var", "when", "while",
    // soft keywords
    "by", "catch", "constructor", "delegate", "dynamic", "field", "file",
    "finally", "get", "import", "init", "param", "property", "receiver",
    "set", "setparam", "value", "where",
    // modifiers
    "abstract", "actual", "annotation", "companion", "const", "crossinline",
    "data", "enum", "expect", "external", "final", "infix", "inline", "inner",
    "internal", "lateinit", "noinline", "open", "operator", "out", "override",
    "private", "protected", "public", "reified", "sealed", "suspend", "tailrec",
    "vararg",
    // special identifiers (just in case)
    /*"field", */"it"
  );

  @Override
  public @NotNull String getNextName(@NotNull String funcName, int i) {
    return unwrapFuncName(funcName) + "_" + i;
  }

  @Override
  public @NotNull String unwrapFuncName(@NotNull String funcName) {
    return StringUtil.trimEnd(funcName, RESERVED_KOTLIN_SUFFIX);
  }

  @Override
  public @NotNull String getFuncName(@NotNull BnfRule rule) {
    final var name = CommonRendererUtils.getBaseName(rule.getName());
    // to ensure Java compatibility, we also check for any Java
    // reserved identifiers in addition to kotlin ones
    return (KOTLIN_RESERVED.contains(name) || JAVA_RESERVED.contains(name))
           ? "%s%s".formatted(name, RESERVED_KOTLIN_SUFFIX)
           : name;
  }

  @Override
  public @NotNull String getWrapperParserMetaMethodName(@NotNull String nextName) {
    return CommonRendererUtils.getBaseName(nextName) + RESERVED_KOTLIN_SUFFIX;
  }

  @Override
  public @Nullable String getRuleDisplayName(
    @NotNull BnfRule rule,
    boolean force
  ) {
    final var s = getRuleDisplayNameRaw(rule, force);
    return StringUtil.isEmpty(s) ? null : "<" + s + ">";
  }
}
