/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.kotlin;

import com.intellij.openapi.util.text.StringUtil;
import org.intellij.grammar.generator.NameShortener;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class KotlinNameShortener implements NameShortener {
  private static final @NotNull Set<@NotNull String> KOTLIN_AUTOIMPORTS = Set.of(
    "kotlin", "kotlin.annotation", "kotlin.collections", "kotlin.comparisons",
    "kotlin.io", "kotlin.ranges", "kotlin.sequences", "kotlin.text", "java.lang",
    "kotlin.jvm", "kotlin.js"
  );
  private final String myPackage;
  private final boolean myEnabled;
  private final Set<String> myImports = new LinkedHashSet<>();

  public KotlinNameShortener(@NotNull String packageName, boolean enabled) {
    myPackage = packageName;
    myEnabled = enabled;
  }

  public static @NotNull String getRawClassName(@NotNull String name) {
    return name.indexOf("<") < name.indexOf(">") ? name.substring(0, name.indexOf("<")) : name;
  }

  public @NotNull Set<String> getImports() {
    return Collections.unmodifiableSet(myImports);
  }

  public void addImports(@NotNull Collection<String> initialImports, @NotNull Collection<String> includedClasses) {
    if (!myEnabled) return;
    for (final var item : initialImports) {
      for (String s : StringUtil.tokenize(item.replaceAll("\\s+", " "), TYPE_TEXT_SEPARATORS)) {
        s = StringUtil.trimStart(StringUtil.trimStart(s, "out "), "in ");
        if (!s.contains(".") || !s.equals(shorten(s))) continue;
        if (myPackage.equals(StringUtil.getPackageName(s))) continue;
        if (includedClasses.contains(StringUtil.getShortName(s))) continue;
        myImports.add(s);
      }
    }
  }

  public @NotNull String shorten(@NotNull String s) {
    if (!myEnabled) return s;
    boolean changed = false;
    StringBuilder sb = new StringBuilder();
    boolean quoted = false;
    int offset = 0, len = s.length();
    boolean vararg = s.endsWith("...");
    for (String part : StringUtil.tokenize(new StringTokenizer(StringUtil.trimEnd(s, "..."), TYPE_TEXT_SEPARATORS, true))) {
      if (TYPE_TEXT_SEPARATORS.contains(part) || "?".equals(part) || "extends".equals(part) || "super".equals(part)) {
        if ("\"".equals(part) && offset > 0 && s.indexOf(offset - 1) != '\\') quoted = !quoted;
        sb.append(part);
        if (",".equals(part) && offset < len && !Character.isWhitespace(s.charAt(offset + 1))) {
          sb.append(" "); // Map<K,V> psi types skip space after comma
        }
      }
      else {
        final var packageName = StringUtil.getPackageName(part);
        if (!quoted && (myImports.contains(part) ||
                        KOTLIN_AUTOIMPORTS.contains(packageName) ||
                        myPackage.contains(packageName) ||
                        myImports.contains(packageName + ".*") ||
                        part.endsWith(".") && myImports.contains(NameShortener.getAnnotatedFQNAt(s, offset)))) {
          sb.append(StringUtil.getShortName(part));
          changed = true;
        }
        else {
          sb.append(part);
        }
      }
      offset += part.length();
    }
    return changed ? sb.append(vararg ? "..." : "").toString() : s;
  }
}
