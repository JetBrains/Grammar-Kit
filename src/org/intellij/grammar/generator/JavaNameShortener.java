/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author gregsh
 */
public class JavaNameShortener implements NameShortener {

  private final String myPackage;
  private final boolean myEnabled;
  private final Set<String> myImports = new LinkedHashSet<>();

  public JavaNameShortener(String packageName, boolean enabled) {
    myPackage = packageName;
    myEnabled = enabled;
  }

  public @NotNull Set<String> getImports() {
    return Collections.unmodifiableSet(myImports);
  }

  public void addImports(@NotNull Collection<String> initialImports, @NotNull Collection<String> includedClasses) {
    if (!myEnabled) return;
    for (String item : initialImports) {
      boolean isStatic = false;
      for (String s : StringUtil.tokenize(item.replaceAll("\\s+", " "), TYPE_TEXT_SEPARATORS)) {
        s = StringUtil.trimStart(StringUtil.trimStart(s, "? super "), "? extends ");
        boolean wasStatic = isStatic;
        isStatic = "static".equals(s);
        if (!s.contains(".") || !s.equals(shorten(s))) continue;
        if (myPackage.equals(StringUtil.getPackageName(s))) continue;
        if (includedClasses.contains(StringUtil.getShortName(s))) continue;
        if (wasStatic) s = "static " + s;
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
      String pkg;
      if (TYPE_TEXT_SEPARATORS.contains(part) ||
          "?".equals(part) || "extends".equals(part) || "super".equals(part)) {
        if ("\"".equals(part) && offset > 0 && s.indexOf(offset - 1) != '\\') quoted = !quoted;
        sb.append(part);
        if (",".equals(part) && offset < len && !Character.isWhitespace(s.charAt(offset + 1))) {
          sb.append(" "); // Map<K,V> psi types skip space after comma
        }
      }
      else if (!quoted && (myImports.contains(part) ||
                           "java.lang".equals(pkg = StringUtil.getPackageName(part)) ||
                           myPackage.equals(pkg) ||
                           myImports.contains(pkg + ".*") ||
                           part.endsWith(".") && myImports.contains(NameShortener.getAnnotatedFQNAt(s, offset)))) {
        sb.append(StringUtil.getShortName(part));
        changed = true;
      }
      else {
        sb.append(part);
      }
      offset += part.length();
    }
    return changed ? sb.append(vararg ? "..." : "").toString() : s;
  }

  public static @NotNull String getRawClassName(@NotNull String name) {
    return name.indexOf("<") < name.indexOf(">") ? name.substring(0, name.indexOf("<")) : name;
  }
}
