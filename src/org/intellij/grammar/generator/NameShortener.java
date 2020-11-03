/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author gregsh
 */
public class NameShortener {
  public static final String TYPE_TEXT_SEPARATORS = "<>,[]()@\" \n";

  private final String myPackage;
  private final Set<String> myImports = new LinkedHashSet<>();

  public NameShortener(String packageName) {
    myPackage = packageName;
  }

  public Set<String> getImports() {
    return Collections.unmodifiableSet(myImports);
  }

  public void addImports(Collection<String> initialImports, Collection<String> includedClasses) {
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

  @NotNull
  public String shorten(String s) {
    boolean changed = false;
    StringBuilder sb = new StringBuilder();
    boolean quoted = false;
    int offset = 0;
    boolean vararg = s.endsWith("...");
    for (String part : StringUtil.tokenize(new StringTokenizer(StringUtil.trimEnd(s, "..."), TYPE_TEXT_SEPARATORS, true))) {
      String pkg;
      if (TYPE_TEXT_SEPARATORS.contains(part) ||
          "?".equals(part) || "extends".equals(part) || "super".equals(part)) {
        if ("\"".equals(part) && offset > 0 && s.indexOf(offset - 1) != '\\') quoted = !quoted;
        sb.append(part);
      }
      else if (!quoted && (myImports.contains(part) ||
                           "java.lang".equals(pkg = StringUtil.getPackageName(part)) ||
                           myPackage.equals(pkg) ||
                           myImports.contains(pkg + ".*") ||
                           part.endsWith(".") && myImports.contains(getAnnotatedFQNAt(s, offset)))) {
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

  @Nullable
  private static String getAnnotatedFQNAt(@NotNull String s, int offset) {
    Ref<String> result = Ref.create();
    addTypeToImports(s.substring(offset), result::set, 0);
    return result.get();
  }

  public static void addTypeToImports(@Nullable String type,
                                      @NotNull List<String> typeAnnotations,
                                      @NotNull Collection<String> result) {
    addTypeToImports(type, result::add, -1);
    for (String anno : typeAnnotations) {
      if (anno.startsWith("kotlin.")) continue;
      addTypeToImports(anno, result::add, -1);
    }
  }

  private static void addTypeToImports(@Nullable String s, @NotNull Consumer<String> result, int forcedOffset) {
    if (s == null) return;
    boolean quoted = false;
    int offset = 0, parenCount = 0;
    Deque<int[]> packagePrefix = null;
    for (String part : StringUtil.tokenize(new StringTokenizer(StringUtil.trimEnd(s, "..."), TYPE_TEXT_SEPARATORS, true))) {
      if (TYPE_TEXT_SEPARATORS.contains(part) ||
          "?".equals(part) || "extends".equals(part) || "super".equals(part)) {
        if ("\"".equals(part) && offset > 0 && s.indexOf(offset - 1) != '\\') quoted = !quoted;
        if (!quoted && "(".equals(part)) parenCount ++;
        if (!quoted && ")".equals(part)) parenCount --;
      }
      else if (!quoted && part.endsWith(".")) {
        if (packagePrefix == null) packagePrefix = new ArrayDeque<>();
        int idx = s.indexOf('@', offset);
        if (idx != -1 && part.equals(s.substring(offset, idx).trim())) {
          packagePrefix.push(new int[] { parenCount, offset, offset + part.length(), idx });
        }
      }
      else if (!quoted && part.contains(".")) {
        if (forcedOffset == -1 || offset == forcedOffset) {
          result.accept(part);
          if (offset == forcedOffset) return;
        }
      }
      else if (!quoted && packagePrefix != null && !packagePrefix.isEmpty()) {
        int[] o = packagePrefix.peek();
        if (o[0] == parenCount && s.substring(o[3]+1, offset).trim().length() > 0) {
          packagePrefix.pop();
          if (forcedOffset == -1 || o[1] == forcedOffset) {
            result.accept(s.substring(o[1], o[2]) + part);
            if (o[1] == forcedOffset) return;
          }
        }
      }
      offset += part.length();
    }
  }

  @NotNull
  public static String getRawClassName(@NotNull String name) {
    return name.indexOf("<") < name.indexOf(">") ? name.substring(0, name.indexOf("<")) : name;
  }

}