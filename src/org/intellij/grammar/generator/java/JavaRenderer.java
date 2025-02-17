/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.java;

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

public final class JavaRenderer implements Renderer {
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

  private JavaRenderer() {

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
  public @NotNull String getFuncName(@NotNull BnfRule rule) {
    String name = getBaseName(rule.getName());
    return JAVA_RESERVED.contains(name) ? name + RESERVED_SUFFIX : name;
  }

  @Override
  public @NotNull String unwrapFuncName(@NotNull String funcName) {
    return StringUtil.trimEnd(funcName, RESERVED_SUFFIX);
  }

  @Override
  public @NotNull String getWrapperParserMetaMethodName(@NotNull String nextName) {
    return getBaseName(nextName) + RESERVED_SUFFIX;
  }

  public @Nullable String getRuleDisplayName(
    @NotNull BnfRule rule,
    boolean force
  ) {
    final var s = getRuleDisplayNameRaw(rule, force);
    return StringUtil.isEmpty(s) ? null : "<" + s + ">";
  }

  private @Nullable String getRuleDisplayNameRaw(@NotNull BnfRule rule, boolean force) {
    String name = getAttribute(rule, KnownAttribute.NAME);
    BnfRule realRule = rule;
    if (name != null) {
      realRule = ((BnfFile)rule.getContainingFile()).getRule(name);
      if (realRule != null && realRule != rule) name = getAttribute(realRule, KnownAttribute.NAME);
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
