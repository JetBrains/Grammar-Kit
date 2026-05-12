/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import com.intellij.java.syntax.element.JavaSyntaxElementType;
import com.intellij.java.syntax.element.JavaSyntaxTokenType;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static org.intellij.grammar.java.syntax.JavaSyntaxNodes.buildDottedText;
import static org.intellij.grammar.java.syntax.JavaSyntaxNodes.firstChildOfType;

/**
 * Renders Java {@link SyntaxNode} type expressions ({@code TYPE} nodes, {@code JAVA_CODE_REFERENCE}
 * nodes, annotations on modifier lists) into the dotted-FQN string form that
 * {@link org.intellij.grammar.java.ClassInfo} / {@link org.intellij.grammar.java.MethodInfo}
 * records expect.
 * <p>
 * Name resolution for unqualified references is delegated to {@link JavaSyntaxImportContext}.
 * In-scope type variables are passed in by the caller and are never qualified.
 */
@SuppressWarnings("UnstableApiUsage")
final class JavaSyntaxTypeFormatter {

  private static final Set<SyntaxElementType> PRIMITIVE_KEYWORDS = Set.of(
    JavaSyntaxTokenType.VOID_KEYWORD,
    JavaSyntaxTokenType.BOOLEAN_KEYWORD,
    JavaSyntaxTokenType.BYTE_KEYWORD,
    JavaSyntaxTokenType.SHORT_KEYWORD,
    JavaSyntaxTokenType.INT_KEYWORD,
    JavaSyntaxTokenType.LONG_KEYWORD,
    JavaSyntaxTokenType.CHAR_KEYWORD,
    JavaSyntaxTokenType.FLOAT_KEYWORD,
    JavaSyntaxTokenType.DOUBLE_KEYWORD
  );

  private final JavaSyntaxImportContext imports;

  JavaSyntaxTypeFormatter(@NotNull JavaSyntaxImportContext imports) {
    this.imports = imports;
  }

  @NotNull String formatType(@NotNull SyntaxNode typeNode, @NotNull Set<String> typeVars) {
    StringBuilder sb = new StringBuilder();
    for (SyntaxNode child = typeNode.firstChild(); child != null; child = child.nextSibling()) {
      SyntaxElementType t = child.getType();
      if (t == JavaSyntaxElementType.JAVA_CODE_REFERENCE) {
        sb.append(formatReference(child, typeVars));
      }
      else if (PRIMITIVE_KEYWORDS.contains(t)) {
        sb.append(child.getText());
      }
      else if (t == JavaSyntaxTokenType.LBRACKET || t == JavaSyntaxTokenType.RBRACKET) {
        sb.append(t == JavaSyntaxTokenType.LBRACKET ? '[' : ']');
      }
      else if (t == JavaSyntaxTokenType.ELLIPSIS) {
        sb.append("[]");
      }
      else if (t == JavaSyntaxTokenType.QUEST) {
        sb.append('?');
      }
      else if (t == JavaSyntaxElementType.TYPE) {
        sb.append(formatType(child, typeVars));
      }
      else if (t == JavaSyntaxTokenType.EXTENDS_KEYWORD) {
        sb.append(" extends ");
      }
      else if (t == JavaSyntaxTokenType.SUPER_KEYWORD) {
        sb.append(" super ");
      }
    }
    return sb.toString();
  }

  @NotNull String formatReference(@NotNull SyntaxNode refNode, @NotNull Set<String> typeVars) {
    String dotted = buildDottedText(refNode);
    String resolved;
    if (typeVars.contains(dotted)) {
      resolved = dotted; // in-scope type variable, do not qualify
    }
    else if (dotted.contains(".")) {
      resolved = dotted; // already qualified, leave as-is
    }
    else {
      resolved = imports.resolveSimpleName(dotted);
    }
    SyntaxNode refParams = firstChildOfType(refNode, JavaSyntaxElementType.REFERENCE_PARAMETER_LIST);
    if (refParams == null) return resolved;
    List<SyntaxNode> typeArgs = new SmartList<>();
    for (SyntaxNode arg = refParams.firstChild(); arg != null; arg = arg.nextSibling()) {
      if (arg.getType() == JavaSyntaxElementType.TYPE) typeArgs.add(arg);
    }
    if (typeArgs.isEmpty()) return resolved;
    StringBuilder sb = new StringBuilder(resolved).append('<');
    for (int i = 0; i < typeArgs.size(); i++) {
      if (i > 0) sb.append(", ");
      sb.append(formatType(typeArgs.get(i), typeVars));
    }
    sb.append('>');
    return sb.toString();
  }

  /** Resolves every {@code JAVA_CODE_REFERENCE} child of the given wrapper element to an FQN. */
  @NotNull List<String> formatRefs(@Nullable SyntaxNode wrapper, @NotNull Set<String> typeVars) {
    if (wrapper == null) return List.of();
    List<String> refs = new SmartList<>();
    for (SyntaxNode ref = wrapper.firstChild(); ref != null; ref = ref.nextSibling()) {
      if (ref.getType() == JavaSyntaxElementType.JAVA_CODE_REFERENCE) {
        refs.add(formatReference(ref, typeVars));
      }
    }
    return refs;
  }

  @NotNull List<String> extractAnnotationFqns(@Nullable SyntaxNode modifierList, @NotNull Set<String> typeVars) {
    if (modifierList == null) return List.of();
    List<String> annos = new SmartList<>();
    for (SyntaxNode child = modifierList.firstChild(); child != null; child = child.nextSibling()) {
      if (child.getType() != JavaSyntaxElementType.ANNOTATION) continue;
      SyntaxNode ref = firstChildOfType(child, JavaSyntaxElementType.JAVA_CODE_REFERENCE);
      if (ref != null) annos.add(formatReference(ref, typeVars));
    }
    return annos;
  }
}
