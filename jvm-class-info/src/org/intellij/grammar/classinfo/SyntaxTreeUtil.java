/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Language-agnostic helpers for walking {@link SyntaxNode} trees. */
@SuppressWarnings("UnstableApiUsage")
public final class SyntaxTreeUtil {

  private SyntaxTreeUtil() { }

  public static @Nullable SyntaxNode firstChildOfType(@NotNull SyntaxNode node, @NotNull SyntaxElementType type) {
    for (SyntaxNode child = node.firstChild(); child != null; child = child.nextSibling()) {
      if (child.getType() == type) return child;
    }
    return null;
  }

  public static @NotNull List<SyntaxNode> childrenOfType(@NotNull SyntaxNode node, @NotNull SyntaxElementType type) {
    List<SyntaxNode> result = new SmartList<>();
    for (SyntaxNode child = node.firstChild(); child != null; child = child.nextSibling()) {
      if (child.getType() == type) result.add(child);
    }
    return result;
  }
}
