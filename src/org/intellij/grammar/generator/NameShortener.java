/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public interface NameShortener {
  /**
   * Returns all the imports this shortener contains.
   */
  @NotNull Set<@NotNull String> getImports();
  
  void addImports(@NotNull Collection<@NotNull String> initialImports, @NotNull Collection<@NotNull String> includedClasses);

  /**
   * Given a fully qualified name, returns a shortened version of it.
   * The shortening algorithm takes into consideration all the imported
   * packages stored in this object.
   */
  @NotNull String shorten(@NotNull String fqn);

  String TYPE_TEXT_SEPARATORS = "<>,[]()@\" \n";

  static @Nullable String getAnnotatedFQNAt(@NotNull String s, int offset) {
    Ref<String> result = Ref.create();
    addTypeToImports(s.substring(offset), result::set, 0);
    return result.get();
  }

  static void addTypeToImports(@Nullable String type,
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
    Deque<int[]> prefixStack = null;
    int[] prefix;
    for (String part : StringUtil.tokenize(new StringTokenizer(StringUtil.trimEnd(s, "..."), TYPE_TEXT_SEPARATORS, true))) {
      if (TYPE_TEXT_SEPARATORS.contains(part) ||
          "?".equals(part) || "extends".equals(part) || "super".equals(part)) {
        if ("\"".equals(part) && offset > 0 && s.indexOf(offset - 1) != '\\') quoted = !quoted;
        if (!quoted && "(".equals(part)) parenCount ++;
        if (!quoted && ")".equals(part)) parenCount --;
      }
      else if (!quoted && part.endsWith(".")) {
        if (prefixStack == null) prefixStack = new ArrayDeque<>();
        int idx = s.indexOf('@', offset);
        if (idx != -1 && part.equals(s.substring(offset, idx).trim())) {
          prefixStack.push(new int[] { parenCount, offset, offset + part.length(), idx });
        }
      }
      else if (!quoted && prefixStack != null && !prefixStack.isEmpty() && parenCount == (prefix = prefixStack.peek())[0] &&
               !s.substring(prefix[3] + 1, offset).trim().isEmpty()) {
        prefixStack.pop();
        if (forcedOffset == -1 || prefix[1] == forcedOffset) {
          int idx = part.indexOf('.');
          result.accept(s.substring(prefix[1], prefix[2]) + (idx == -1 ? part : part.substring(0, idx)));
          if (prefix[1] == forcedOffset) return;
        }
      }
      else if (!quoted && part.contains(".")) {
        if (forcedOffset == -1 || offset == forcedOffset) {
          result.accept(part);
          if (offset == forcedOffset) return;
        }
      }
      offset += part.length();
    }
  }
}
