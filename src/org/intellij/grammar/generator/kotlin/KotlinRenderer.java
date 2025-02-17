/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.kotlin;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.codeStyle.NameUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.Case;
import org.intellij.grammar.generator.NameFormat;
import org.intellij.grammar.generator.Renderer;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getAttribute;
import static org.intellij.grammar.generator.ParserGeneratorUtil.quote;
import static org.intellij.grammar.generator.java.JavaRenderer.JAVA_RESERVED;

public final class KotlinRenderer implements Renderer {
  public static final @NotNull KotlinRenderer INSTANCE = new KotlinRenderer();
  private static final @NotNull Set<@NotNull String> KOTLIN_RESERVED = Set.of(
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

  private KotlinRenderer() {

  }

  @Override
  public @NotNull String toIdentifier(@NotNull String text, @Nullable NameFormat format, @NotNull Case textCase) {
    if (text.isEmpty()) return "";
    String fixed = text.replaceAll("[^:\\p{javaJavaIdentifierPart}]", "_");
    boolean allCaps = Case.UPPER.apply(fixed).equals(fixed);
    StringBuilder sb = new StringBuilder();
    if (!Character.isJavaIdentifierStart(fixed.charAt(0)) && sb.isEmpty()) sb.append("_");
    String[] strings = NameUtil.nameToWords(fixed);
    for (int i = 0, len = strings.length; i < len; i++) {
      String s = strings[i];
      if (textCase == Case.CAMEL && s.startsWith("_") && !(i == 0 || i == len - 1)) continue;
      if (textCase == Case.UPPER && !s.startsWith("_") && !(i == 0 || StringUtil.endsWith(sb, "_"))) sb.append("_");
      if (textCase == Case.CAMEL && !allCaps && Case.UPPER.apply(s).equals(s)) {
        sb.append(s);
      }
      else {
        sb.append(textCase.apply(s));
      }
    }
    return format == null ? sb.toString() : format.apply(sb.toString());
  }

  @Override
  public @NotNull String getNextName(@NotNull String funcName, int i) {
    return unwrapFuncName(funcName) + "_" + i;
  }

  @Override
  public @NotNull String unwrapFuncName(@NotNull String funcName) {
    final var unquoted = StringUtil.unquoteString(funcName, '`');
    return StringUtil.trimEnd(unquoted, RESERVED_SUFFIX);
  }

  @Override
  public @NotNull String getFuncName(@NotNull BnfRule rule) {
    final var name = getBaseName(rule.getName());
    // to ensure Java compatibility, we also check for any Java
    // reserved identifiers in addition to kotlin ones
    return (KOTLIN_RESERVED.contains(name) || JAVA_RESERVED.contains(name))
           ? "`%s%s`".formatted(name, RESERVED_SUFFIX)
           : name;
  }

  @Override
  public @NotNull String getWrapperParserMetaMethodName(@NotNull String nextName) {
    return quote(getBaseName(nextName) + RESERVED_SUFFIX, "`");
  }

  public @Nullable String getRuleDisplayName(
    @NotNull BnfRule rule,
    boolean force
  ) {
    final var s = getRuleDisplayNameRaw(rule, force);
    return StringUtil.isEmpty(s) ? null : "<" + s + ">";
  }

  private @Nullable String getRuleDisplayNameRaw(@NotNull BnfRule rule, boolean force) {
    var name = getAttribute(rule, KnownAttribute.NAME);
    var realRule = rule;
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
}
