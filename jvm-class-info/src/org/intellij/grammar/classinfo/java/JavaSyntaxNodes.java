/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

import com.intellij.java.syntax.element.JavaSyntaxElementType;
import com.intellij.java.syntax.element.JavaSyntaxTokenType;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.intellij.grammar.classinfo.SyntaxTreeUtil.firstChildOfType;

/**
 * Static helpers for navigating Java {@link SyntaxNode} trees: finding children by type,
 * locating the name identifier of a class or method, extracting modifier bits, and rendering
 * a {@code JAVA_CODE_REFERENCE} as a dotted string.
 */
@SuppressWarnings("UnstableApiUsage")
final class JavaSyntaxNodes {

  private static final Map<SyntaxElementType, Integer> MODIFIER_BITS = Map.ofEntries(
    new HashMap.SimpleEntry<>(JavaSyntaxTokenType.PUBLIC_KEYWORD, Modifier.PUBLIC),
    new HashMap.SimpleEntry<>(JavaSyntaxTokenType.PRIVATE_KEYWORD, Modifier.PRIVATE),
    new HashMap.SimpleEntry<>(JavaSyntaxTokenType.PROTECTED_KEYWORD, Modifier.PROTECTED),
    new HashMap.SimpleEntry<>(JavaSyntaxTokenType.STATIC_KEYWORD, Modifier.STATIC),
    new HashMap.SimpleEntry<>(JavaSyntaxTokenType.FINAL_KEYWORD, Modifier.FINAL),
    new HashMap.SimpleEntry<>(JavaSyntaxTokenType.SYNCHRONIZED_KEYWORD, Modifier.SYNCHRONIZED),
    new HashMap.SimpleEntry<>(JavaSyntaxTokenType.VOLATILE_KEYWORD, Modifier.VOLATILE),
    new HashMap.SimpleEntry<>(JavaSyntaxTokenType.TRANSIENT_KEYWORD, Modifier.TRANSIENT),
    new HashMap.SimpleEntry<>(JavaSyntaxTokenType.NATIVE_KEYWORD, Modifier.NATIVE),
    new HashMap.SimpleEntry<>(JavaSyntaxTokenType.ABSTRACT_KEYWORD, Modifier.ABSTRACT),
    new HashMap.SimpleEntry<>(JavaSyntaxTokenType.STRICTFP_KEYWORD, Modifier.STRICT)
  );

  private JavaSyntaxNodes() { }

  static @NotNull List<String> typeParameterNames(@Nullable SyntaxNode typeParameterList) {
    if (typeParameterList == null) return List.of();
    List<String> names = new SmartList<>();
    for (SyntaxNode tp = typeParameterList.firstChild(); tp != null; tp = tp.nextSibling()) {
      if (tp.getType() != JavaSyntaxElementType.TYPE_PARAMETER) continue;
      SyntaxNode id = firstChildOfType(tp, JavaSyntaxTokenType.IDENTIFIER);
      if (id != null) names.add(id.getText().toString());
    }
    return names;
  }

  /** Identifier that immediately follows a {@code class}/{@code interface}/{@code enum}/{@code record} keyword. */
  static @Nullable SyntaxNode findClassName(@NotNull SyntaxNode classNode) {
    boolean afterKeyword = false;
    for (SyntaxNode child = classNode.firstChild(); child != null; child = child.nextSibling()) {
      SyntaxElementType t = child.getType();
      if (t == JavaSyntaxTokenType.CLASS_KEYWORD ||
          t == JavaSyntaxTokenType.INTERFACE_KEYWORD ||
          t == JavaSyntaxTokenType.ENUM_KEYWORD ||
          t == JavaSyntaxTokenType.RECORD_KEYWORD) {
        afterKeyword = true;
        continue;
      }
      if (afterKeyword && t == JavaSyntaxTokenType.IDENTIFIER) return child;
    }
    return null;
  }

  static boolean isInterface(@NotNull SyntaxNode classNode) {
    return firstChildOfType(classNode, JavaSyntaxTokenType.INTERFACE_KEYWORD) != null;
  }

  static boolean isAnnotationType(@NotNull SyntaxNode classNode) {
    return firstChildOfType(classNode, JavaSyntaxTokenType.AT) != null
           && firstChildOfType(classNode, JavaSyntaxTokenType.INTERFACE_KEYWORD) != null;
  }

  /**
   * Method name = the {@link JavaSyntaxTokenType#IDENTIFIER IDENTIFIER} that follows the (optional)
   * return type. For constructors there is no return type, so it's the first identifier after the
   * modifier list / type-parameter list.
   */
  static @Nullable SyntaxNode firstNameIdentifier(@NotNull SyntaxNode methodNode) {
    boolean afterTypeOrMods = false;
    for (SyntaxNode child = methodNode.firstChild(); child != null; child = child.nextSibling()) {
      SyntaxElementType t = child.getType();
      if (t == JavaSyntaxElementType.MODIFIER_LIST ||
          t == JavaSyntaxElementType.TYPE_PARAMETER_LIST ||
          t == JavaSyntaxElementType.TYPE) {
        afterTypeOrMods = true;
        continue;
      }
      if (afterTypeOrMods && t == JavaSyntaxTokenType.IDENTIFIER) return child;
    }
    return null;
  }

  static int extractModifiers(@Nullable SyntaxNode modifierList) {
    if (modifierList == null) return 0;
    int bits = 0;
    for (SyntaxNode child = modifierList.firstChild(); child != null; child = child.nextSibling()) {
      Integer bit = MODIFIER_BITS.get(child.getType());
      if (bit != null) bits |= bit;
    }
    return bits;
  }

  static @NotNull String buildDottedText(@NotNull SyntaxNode refNode) {
    StringBuilder sb = new StringBuilder();
    appendDotted(refNode, sb);
    return sb.toString();
  }

  private static void appendDotted(@NotNull SyntaxNode refNode, @NotNull StringBuilder sb) {
    for (SyntaxNode child = refNode.firstChild(); child != null; child = child.nextSibling()) {
      SyntaxElementType t = child.getType();
      if (t == JavaSyntaxElementType.JAVA_CODE_REFERENCE) {
        appendDotted(child, sb);
      }
      else if (t == JavaSyntaxTokenType.DOT) {
        sb.append('.');
      }
      else if (t == JavaSyntaxTokenType.IDENTIFIER) {
        sb.append(child.getText());
      }
      // skip REFERENCE_PARAMETER_LIST: caller handles generics
    }
  }
}
