/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

import com.intellij.java.syntax.element.JavaSyntaxElementType;
import com.intellij.java.syntax.element.JavaSyntaxTokenType;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import com.intellij.util.SmartList;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmTypeRef;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.classinfo.TypeProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static org.intellij.grammar.classinfo.SyntaxTreeUtil.firstChildOfType;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.buildDottedText;

/**
 * Parses Java {@link SyntaxNode} type expressions ({@code TYPE} nodes, {@code JAVA_CODE_REFERENCE}
 * nodes) into the canonical {@link JvmTypeRef} model that {@link ClassSymbol} / {@link MethodSymbol}
 * consumers expect.
 * <p>
 * Java types carry no built-in nullability semantics, so {@link JvmTypeRef#annotations()} is always
 * empty at parse time. Explicit declaration-target annotations on parameters / methods / classes
 * still flow through {@link #extractAnnotationFqns}; in-source type-use annotations on individual
 * type positions are not modelled.
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

  /**
   * Parse a non-wildcard {@code TYPE} node — either a primitive keyword or a {@code JAVA_CODE_REFERENCE}
   * with optional {@code []} / {@code ...} array dimensions. Wildcards ({@code ?}, {@code ? extends X},
   * {@code ? super X}) appear only in {@code REFERENCE_PARAMETER_LIST} positions and are handled by
   * {@link #parseProjection}, never reaching this method.
   */
  @NotNull JvmTypeRef parseType(@NotNull SyntaxNode typeNode, @NotNull Set<String> typeVars) {
    JvmTypeRef base = null;
    int arrayDims = 0;
    for (SyntaxNode child = typeNode.firstChild(); child != null; child = child.nextSibling()) {
      SyntaxElementType t = child.getType();
      if (t == JavaSyntaxElementType.JAVA_CODE_REFERENCE) {
        base = parseReference(child, typeVars);
      }
      else if (PRIMITIVE_KEYWORDS.contains(t)) {
        base = new JvmTypeRef.PrimitiveType(child.getText().toString());
      }
      else if (t == JavaSyntaxTokenType.LBRACKET) {
        // Java types pair LBRACKET + RBRACKET for each `[]`; count only the opening bracket.
        arrayDims++;
      }
      else if (t == JavaSyntaxTokenType.ELLIPSIS) {
        arrayDims++;
      }
    }
    if (base == null) base = new JvmTypeRef.UserType(Fqn.of(""), List.of(), List.of());
    JvmTypeRef result = base;
    for (int i = 0; i < arrayDims; i++) {
      result = new JvmTypeRef.ArrayType(result, List.of());
    }
    return result;
  }

  /**
   * Parse a {@code JAVA_CODE_REFERENCE} as a {@link JvmTypeRef.UserType}, including any generic
   * type arguments (each TYPE child of the REFERENCE_PARAMETER_LIST becomes one {@link TypeProjection}).
   */
  @NotNull JvmTypeRef parseReference(@NotNull SyntaxNode refNode, @NotNull Set<String> typeVars) {
    String resolved = resolveDotted(refNode, typeVars);
    if (typeVars.contains(resolved)) {
      return new JvmTypeRef.TypeVariable(resolved);
    }
    SyntaxNode refParams = firstChildOfType(refNode, JavaSyntaxElementType.REFERENCE_PARAMETER_LIST);
    List<TypeProjection> args = refParams == null ? List.of() : parseProjections(refParams, typeVars);
    return new JvmTypeRef.UserType(Fqn.of(resolved), List.of(), args);
  }

  private @NotNull List<TypeProjection> parseProjections(@NotNull SyntaxNode refParamList,
                                                         @NotNull Set<String> typeVars) {
    List<TypeProjection> out = new SmartList<>();
    for (SyntaxNode arg = refParamList.firstChild(); arg != null; arg = arg.nextSibling()) {
      if (arg.getType() != JavaSyntaxElementType.TYPE) continue;
      out.add(parseProjection(arg, typeVars));
    }
    return out;
  }

  private @NotNull TypeProjection parseProjection(@NotNull SyntaxNode typeArg, @NotNull Set<String> typeVars) {
    boolean isWildcard = false;
    TypeProjection.Variance variance = TypeProjection.Variance.INVARIANT;
    SyntaxNode wildcardBound = null;
    for (SyntaxNode child = typeArg.firstChild(); child != null; child = child.nextSibling()) {
      SyntaxElementType t = child.getType();
      if (t == JavaSyntaxTokenType.QUEST) {
        isWildcard = true;
      }
      else if (t == JavaSyntaxTokenType.EXTENDS_KEYWORD) {
        variance = TypeProjection.Variance.OUT;
      }
      else if (t == JavaSyntaxTokenType.SUPER_KEYWORD) {
        variance = TypeProjection.Variance.IN;
      }
      else if (t == JavaSyntaxElementType.TYPE) {
        // Wildcard bound, e.g. the inner TYPE of `? extends String`.
        wildcardBound = child;
      }
    }
    if (isWildcard && wildcardBound == null) return new TypeProjection.Star();
    if (isWildcard) {
      return new TypeProjection.WithVariance(variance, parseType(wildcardBound, typeVars));
    }
    // Non-wildcard projection (`List<String>`): the typeArg TYPE itself describes the argument.
    return new TypeProjection.WithVariance(TypeProjection.Variance.INVARIANT, parseType(typeArg, typeVars));
  }

  /**
   * Parse every {@code JAVA_CODE_REFERENCE} child of the given wrapper element as a full type
   * expression. Used for type-parameter bounds (e.g. {@code <T extends List<String> & Serializable>}).
   */
  @NotNull List<JvmTypeRef> parseRefs(@Nullable SyntaxNode wrapper, @NotNull Set<String> typeVars) {
    if (wrapper == null) return List.of();
    List<JvmTypeRef> refs = new SmartList<>();
    for (SyntaxNode ref = wrapper.firstChild(); ref != null; ref = ref.nextSibling()) {
      if (ref.getType() == JavaSyntaxElementType.JAVA_CODE_REFERENCE) {
        refs.add(parseReference(ref, typeVars));
      }
    }
    return refs;
  }

  /**
   * Like {@link #parseRefs} but strips generic parameters and returns raw {@link Fqn}s.
   */
  @NotNull List<Fqn> extractRefFqns(@Nullable SyntaxNode wrapper, @NotNull Set<String> typeVars) {
    if (wrapper == null) return List.of();
    List<Fqn> refs = new SmartList<>();
    for (SyntaxNode ref = wrapper.firstChild(); ref != null; ref = ref.nextSibling()) {
      if (ref.getType() == JavaSyntaxElementType.JAVA_CODE_REFERENCE) {
        refs.add(Fqn.of(resolveDotted(ref, typeVars)));
      }
    }
    return refs;
  }

  @NotNull List<Fqn> extractAnnotationFqns(@Nullable SyntaxNode modifierList, @NotNull Set<String> typeVars) {
    if (modifierList == null) return List.of();
    List<Fqn> annos = new SmartList<>();
    for (SyntaxNode child = modifierList.firstChild(); child != null; child = child.nextSibling()) {
      if (child.getType() != JavaSyntaxElementType.ANNOTATION) continue;
      SyntaxNode ref = firstChildOfType(child, JavaSyntaxElementType.JAVA_CODE_REFERENCE);
      if (ref != null) annos.add(Fqn.of(resolveDotted(ref, typeVars)));
    }
    return annos;
  }

  private @NotNull String resolveDotted(@NotNull SyntaxNode refNode, @NotNull Set<String> typeVars) {
    String dotted = buildDottedText(refNode);
    if (typeVars.contains(dotted)) return dotted;          // in-scope type variable, do not qualify
    if (dotted.contains(".")) return dotted;               // already qualified, leave as-is
    return imports.resolveSimpleName(dotted);
  }
}
