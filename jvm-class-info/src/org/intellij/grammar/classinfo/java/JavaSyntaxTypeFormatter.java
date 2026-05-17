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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.intellij.grammar.classinfo.SyntaxTreeUtil.firstChildOfType;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.buildDottedText;

/**
 * Parses Java {@link SyntaxNode} type expressions ({@code TYPE} nodes, {@code JAVA_CODE_REFERENCE}
 * nodes) into the canonical {@link JvmTypeRef} model that {@link ClassSymbol} / {@link MethodSymbol}
 * consumers expect.
 * <p>
 * In-source type-use annotations (JLS 9.7.4) are captured per position: annotations preceding the
 * component type land on the resulting {@link JvmTypeRef.UserType} / {@link JvmTypeRef.PrimitiveType}
 * (primitives ignore them); annotations preceding an {@code LBRACKET} land on that array dimension's
 * {@link JvmTypeRef.ArrayType}. So {@code PsiReference @NotNull []} parses to
 * {@code ArrayType([NotNull], UserType(PsiReference))} and {@code @A T @B []} to
 * {@code ArrayType([B], UserType(T, [A]))}.
 * <p>
 * Declaration-target annotations on parameters / methods / classes (the {@code @NotNull} that
 * appears before a parameter type, attached to the {@code PARAMETER}'s {@code MODIFIER_LIST}) still
 * flow through {@link #extractAnnotationFqns} into {@code param.annotations}.
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
  private @NotNull Map<String, Fqn> nestedScope = Map.of();

  JavaSyntaxTypeFormatter(@NotNull JavaSyntaxImportContext imports) {
    this.imports = imports;
  }

  /**
   * In-scope nested class simple-name → FQN map. The extractor sets this before walking a class's
   * members so that an unqualified reference to a sibling nested type (e.g. {@code E} inside
   * {@code class Util { enum E {} static E f(){...} }}) resolves to the enclosing-class-qualified
   * FQN rather than falling through to the same-package fallback.
   */
  @NotNull Map<String, Fqn> getNestedScope() {
    return nestedScope;
  }

  void setNestedScope(@NotNull Map<String, Fqn> scope) {
    this.nestedScope = scope;
  }

  /**
   * Parse a non-wildcard {@code TYPE} node — either a primitive keyword or a {@code JAVA_CODE_REFERENCE}
   * with optional {@code []} / {@code ...} array dimensions, with type-use annotations attached at
   * the right position. Wildcards ({@code ?}, {@code ? extends X}, {@code ? super X}) appear only in
   * {@code REFERENCE_PARAMETER_LIST} positions and are handled by {@link #parseProjection}, never
   * reaching this method.
   */
  @NotNull JvmTypeRef parseType(@NotNull SyntaxNode typeNode, @NotNull Set<String> typeVars) {
    JvmTypeRef base = null;
    List<Fqn> pending = new ArrayList<>();
    List<List<Fqn>> dimAnnotations = new ArrayList<>();
    for (SyntaxNode child = typeNode.firstChild(); child != null; child = child.nextSibling()) {
      SyntaxElementType t = child.getType();
      if (t == JavaSyntaxElementType.JAVA_CODE_REFERENCE) {
        JvmTypeRef ref = parseReference(child, typeVars);
        // Pending type-use annotations preceding the reference attach to the component itself.
        // Merge with any annotations the reference already collected (e.g. nested MODIFIER_LIST
        // inside the JAVA_CODE_REFERENCE) — pending comes first to preserve source order.
        if (!pending.isEmpty() && ref instanceof JvmTypeRef.UserType u) {
          List<Fqn> merged = new ArrayList<>(pending.size() + u.annotations().size());
          merged.addAll(pending);
          merged.addAll(u.annotations());
          ref = new JvmTypeRef.UserType(u.name(), merged, u.args());
        }
        // (Type variables don't carry annotations; primitives don't either — drop pending in those cases.)
        pending.clear();
        base = ref;
      }
      else if (PRIMITIVE_KEYWORDS.contains(t)) {
        base = new JvmTypeRef.PrimitiveType(child.getText().toString());
        pending.clear();
      }
      else if (t == JavaSyntaxElementType.TYPE) {
        // IntelliJ's Java parser nests the component as an inner TYPE child for annotated arrays
        // (e.g. `T @A []` parses as TYPE(TYPE(T), MODIFIER_LIST(@A), `[`, `]`)). Recurse to get the
        // component, then continue collecting array dims and their annotations from siblings.
        base = parseType(child, typeVars);
      }
      else if (t == JavaSyntaxTokenType.LBRACKET || t == JavaSyntaxTokenType.ELLIPSIS) {
        // Each `[`/`...` opens a new array dimension; pending annotations belong to THIS dim.
        dimAnnotations.add(List.copyOf(pending));
        pending.clear();
      }
      else if (t == JavaSyntaxElementType.MODIFIER_LIST) {
        pending.addAll(extractAnnotationFqns(child, typeVars));
      }
      else if (t == JavaSyntaxElementType.ANNOTATION) {
        Fqn fqn = annotationFqn(child, typeVars);
        if (fqn != null) pending.add(fqn);
      }
    }
    if (base == null) base = new JvmTypeRef.UserType(Fqn.of(""), List.of(), List.of());
    JvmTypeRef result = base;
    // dimAnnotations is in source order — innermost dim first; wrap inside-out so the outermost
    // ArrayType (last wrap) takes the last-recorded dim's annotations.
    for (List<Fqn> annos : dimAnnotations) {
      result = new JvmTypeRef.ArrayType(result, annos);
    }
    return result;
  }

  private @Nullable Fqn annotationFqn(@NotNull SyntaxNode annotation, @NotNull Set<String> typeVars) {
    SyntaxNode ref = firstChildOfType(annotation, JavaSyntaxElementType.JAVA_CODE_REFERENCE);
    return ref == null ? null : Fqn.of(resolveDotted(ref, typeVars));
  }

  /**
   * Parse a {@code JAVA_CODE_REFERENCE} as a {@link JvmTypeRef.UserType}, including any generic
   * type arguments (each TYPE child of the REFERENCE_PARAMETER_LIST becomes one {@link TypeProjection})
   * and any type-use annotations the parser nested inside the reference itself (e.g.
   * {@code T extends @A X} where {@code @A} ends up as a child of the X reference's MODIFIER_LIST).
   */
  @NotNull JvmTypeRef parseReference(@NotNull SyntaxNode refNode, @NotNull Set<String> typeVars) {
    String resolved = resolveDotted(refNode, typeVars);
    if (typeVars.contains(resolved)) {
      return new JvmTypeRef.TypeVariable(resolved);
    }
    SyntaxNode refParams = firstChildOfType(refNode, JavaSyntaxElementType.REFERENCE_PARAMETER_LIST);
    List<TypeProjection> args = refParams == null ? List.of() : parseProjections(refParams, typeVars);
    List<Fqn> annotations = collectTypeUseAnnotations(refNode, typeVars);
    return new JvmTypeRef.UserType(Fqn.of(resolved), annotations, args);
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
   * Type-use annotations that precede each reference ({@code extends @A X & @B Y}) attach to the
   * resulting {@link JvmTypeRef.UserType} for that bound.
   */
  @NotNull List<JvmTypeRef> parseRefs(@Nullable SyntaxNode wrapper, @NotNull Set<String> typeVars) {
    if (wrapper == null) return List.of();
    List<JvmTypeRef> refs = new SmartList<>();
    List<Fqn> pending = new ArrayList<>();
    for (SyntaxNode child = wrapper.firstChild(); child != null; child = child.nextSibling()) {
      SyntaxElementType t = child.getType();
      if (t == JavaSyntaxElementType.JAVA_CODE_REFERENCE) {
        JvmTypeRef ref = parseReference(child, typeVars);
        // Merge sibling-level pending annotations with the reference's own (collected from inside
        // the JAVA_CODE_REFERENCE). Pending first to preserve source order.
        if (!pending.isEmpty() && ref instanceof JvmTypeRef.UserType u) {
          List<Fqn> merged = new ArrayList<>(pending.size() + u.annotations().size());
          merged.addAll(pending);
          merged.addAll(u.annotations());
          ref = new JvmTypeRef.UserType(u.name(), merged, u.args());
        }
        pending.clear();
        refs.add(ref);
      }
      else if (t == JavaSyntaxElementType.MODIFIER_LIST) {
        pending.addAll(extractAnnotationFqns(child, typeVars));
      }
      else if (t == JavaSyntaxElementType.ANNOTATION) {
        Fqn fqn = annotationFqn(child, typeVars);
        if (fqn != null) pending.add(fqn);
      }
    }
    return refs;
  }

  /**
   * Collect annotation FQNs from the given parent's direct children — both bare {@code ANNOTATION}
   * elements and {@code MODIFIER_LIST}-wrapped ones. Used for type-parameter annotations
   * ({@code <@A T>}) where the parser may emit either shape as a direct {@code TYPE_PARAMETER} child.
   */
  @NotNull List<Fqn> collectTypeUseAnnotations(@NotNull SyntaxNode parent, @NotNull Set<String> typeVars) {
    List<Fqn> out = new SmartList<>();
    for (SyntaxNode child = parent.firstChild(); child != null; child = child.nextSibling()) {
      SyntaxElementType t = child.getType();
      if (t == JavaSyntaxElementType.MODIFIER_LIST) {
        out.addAll(extractAnnotationFqns(child, typeVars));
      }
      else if (t == JavaSyntaxElementType.ANNOTATION) {
        Fqn fqn = annotationFqn(child, typeVars);
        if (fqn != null) out.add(fqn);
      }
    }
    return out;
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
    int firstDot = dotted.indexOf('.');
    if (firstDot < 0) {
      Fqn nested = nestedScope.get(dotted);
      if (nested != null) return nested.value();           // sibling/outer nested class wins over same-package
      return imports.resolveSimpleName(dotted);
    }
    // Dotted reference: if the head segment names an in-scope nested class, qualify with its full FQN.
    String head = dotted.substring(0, firstDot);
    Fqn nestedHead = nestedScope.get(head);
    if (nestedHead != null) return nestedHead.value() + dotted.substring(firstDot);
    return dotted;                                         // already qualified, leave as-is
  }
}
