/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.codeStyle.NameUtil;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static org.intellij.grammar.generator.ParserGeneratorUtil.RESERVED_SUFFIX;
import static org.intellij.grammar.generator.ParserGeneratorUtil.quote;

public class KotlinParserGeneratorUtil {
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

  public static @NotNull String toIdentifierKt(@NotNull String text, @Nullable ParserGeneratorUtil.NameFormat format, @NotNull Case cas) {
    if (text.isEmpty()) return "";
    String fixed = text.replaceAll("[^:\\p{javaJavaIdentifierPart}]", "_");
    boolean allCaps = Case.UPPER.apply(fixed).equals(fixed);
    StringBuilder sb = new StringBuilder();
    if (!Character.isJavaIdentifierStart(fixed.charAt(0)) && sb.isEmpty()) sb.append("_");
    String[] strings = NameUtil.nameToWords(fixed);
    for (int i = 0, len = strings.length; i < len; i++) {
      String s = strings[i];
      if (cas == Case.CAMEL && s.startsWith("_") && !(i == 0 || i == len - 1)) continue;
      if (cas == Case.UPPER && !s.startsWith("_") && !(i == 0 || StringUtil.endsWith(sb, "_"))) sb.append("_");
      if (cas == Case.CAMEL && !allCaps && Case.UPPER.apply(s).equals(s)) {
        sb.append(s);
      }
      else {
        sb.append(cas.apply(s));
      }
    }
    return format == null ? sb.toString() : format.apply(sb.toString());
  }

  public static @NotNull String getBaseNameKt(@NotNull String name) {
    return toIdentifierKt(name, null, Case.AS_IS);
  }

  public static String getNextNameKt(@NotNull String funcName, int i) {
    final var unquoted = StringUtil.unquoteString(funcName, '`');
    final var trimmed = StringUtil.trimEnd(unquoted, RESERVED_SUFFIX);
    return trimmed + "_" + i;
  }

  public static @NotNull String getKtFuncName(@NotNull BnfRule rule) {
    final var name = getBaseNameKt(rule.getName());
    return KOTLIN_RESERVED.contains(name) ? "`%s%s`".formatted(name, RESERVED_SUFFIX) : name;
  }

  static @NotNull String getWrapperParserMetaMethodNameKt(@NotNull String nextName) {
    return quote(getBaseNameKt(nextName) + RESERVED_SUFFIX, "`");
  }
}
