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
import org.intellij.grammar.classinfo.SymbolResolver;
import org.intellij.grammar.classinfo.TargetType;
import org.intellij.grammar.classinfo.TypeProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
  private final SymbolResolver resolver;
  private @NotNull Map<String, Fqn> nestedScope = Map.of();
  private @NotNull List<Fqn> enclosingChain = List.of();

  JavaSyntaxTypeFormatter(@NotNull JavaSyntaxImportContext imports, @NotNull SymbolResolver resolver) {
    this.imports = imports;
    this.resolver = resolver;
  }

  /**
   * Predicate for {@link org.intellij.grammar.classinfo.TypeUseAnnotationLifter}: an annotation
   * type counts as type-use iff its resolved {@link ClassSymbol#annotationTargets} contains
   * {@link TargetType#TYPE_USE}. Returns {@code false} when the FQN does not resolve (annotation
   * lives in an unreachable jar, or cycle protection short-circuits the lookup) — matches IntelliJ's
   * {@code AddAnnotationPsiFix} behaviour where unresolved annotations stay on the declaration list.
   */
  boolean isTypeUseAnnotation(@NotNull Fqn annotationFqn) {
    ClassSymbol c = resolver.findClass(annotationFqn);
    return c != null && c.annotationTargets().contains(TargetType.TYPE_USE);
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
   * Innermost-first list of enclosing class FQNs. Used to resolve an unqualified simple name to an
   * inherited nested type — for {@code class Sub extends Parent { void f(Marker m) {} }} where
   * {@code Parent} declares {@code Marker}, the simple-name path walks Sub's supertypes after the
   * {@link #nestedScope} miss and finds {@code Parent.Marker} (JLS 6.4.1).
   */
  @NotNull List<Fqn> getEnclosingChain() {
    return enclosingChain;
  }

  void setEnclosingChain(@NotNull List<Fqn> chain) {
    this.enclosingChain = chain;
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

  /**
   * Collect annotation FQNs from a {@code MODIFIER_LIST} (or {@code TYPE_PARAMETER}, etc.). Skips
   * annotations that carry arguments — matches the IDE-side {@code ClassSymbolUtil.annotationsFromPsi}
   * behaviour (which drops {@code annotation.getParameterList().getAttributes().length > 0}). Arg-
   * bearing annotations like {@code @Target(...)} or {@code @SuppressWarnings("x")} carry semantic
   * payload that the FQN-only model cannot represent, so dropping them prevents misleading
   * round-trips.
   */
  @NotNull List<Fqn> extractAnnotationFqns(@Nullable SyntaxNode modifierList, @NotNull Set<String> typeVars) {
    if (modifierList == null) return List.of();
    List<Fqn> annos = new SmartList<>();
    for (SyntaxNode child = modifierList.firstChild(); child != null; child = child.nextSibling()) {
      if (child.getType() != JavaSyntaxElementType.ANNOTATION) continue;
      if (hasAnnotationArguments(child)) continue;
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
      // Probe inherited nested types: walk each enclosing class's super-chain for a nested type
      // matching the simple name. Own nested types already won via nestedScope above (JLS 6.4.1).
      for (Fqn enclosing : enclosingChain) {
        String inherited = NestedTypeResolver.findDeclaringClass(enclosing, dotted, resolver);
        if (inherited != null && !inherited.equals(enclosing.child(dotted).value())) {
          return inherited;
        }
      }
      return imports.resolveSimpleName(dotted);
    }
    // Dotted reference. If the head segment names an in-scope nested class, qualify with that FQN.
    String head = dotted.substring(0, firstDot);
    String tail = dotted.substring(firstDot);
    Fqn nestedHead = nestedScope.get(head);
    if (nestedHead != null) return canonicalize(nestedHead.value() + tail);
    // Resolve the head through file-level imports. JLS doesn't restrict class names to start with
    // uppercase, so we cannot use a casing heuristic; instead we accept the resolved head whenever
    // it comes from an explicit import / builtin / wildcard-with-resolver-confirmation, and for the
    // last-resort same-package fallback only when the resolver actually knows the class. This
    // preserves package-qualified `java.util.Map.Entry` (head `java` would otherwise be qualified to
    // `<pkg>.java` by the same-package fallback) while still resolving `legacyType.Inner` /
    // `_Inner.Leaf` after their imports.
    if (!head.isEmpty()) {
      String resolvedHead = imports.resolveSimpleName(head);
      if (!resolvedHead.equals(head) && isConfirmedClass(head, resolvedHead)) {
        return canonicalize(resolvedHead + tail);
      }
    }
    return canonicalize(dotted);                           // already qualified — still canonicalize tail
  }

  /**
   * The head of a dotted reference is ambiguous: it can be a class simple name (resolvable through
   * imports / builtins / same-package) or the leading segment of a package-qualified FQN like
   * {@code java} in {@code java.util.Map.Entry}. We accept the resolved name when it came from an
   * import / builtin / wildcard-with-resolver-hit (those don't false-positive on package segments)
   * or when the resolver confirms the same-package candidate is a real class.
   */
  private boolean isConfirmedClass(@NotNull String head, @NotNull String resolvedHead) {
    if (imports.isSingleTypeImport(head) || imports.isKnownBuiltin(head)) return true;
    return resolver.findClass(Fqn.of(resolvedHead)) != null;
  }

  /**
   * Canonicalize each segment of a dotted nested-type reference (JLS 7.5.1). For
   * {@code Sub.Inner.Leaf} where {@code Inner} is inherited from {@code Sub}'s supertype, the
   * intermediate {@code Inner} hop must rewrite to the declaring class — otherwise we'd produce
   * {@code Sub.Inner.Leaf} where ASM produces {@code Parent.Inner.Leaf}. Walks segment-by-segment,
   * asking {@link NestedTypeResolver#findDeclaringClass} at each hop. When the resolver doesn't
   * know the current enclosing (pure package prefixes like {@code java.util}), append the segment
   * verbatim and continue — only segments whose enclosing actually resolves get rewritten.
   */
  private @NotNull String canonicalize(@NotNull String dotted) {
    int firstDot = dotted.indexOf('.');
    if (firstDot < 0) return dotted;
    String enclosing = dotted.substring(0, firstDot);
    int idx = firstDot + 1;
    while (true) {
      int nextDot = dotted.indexOf('.', idx);
      String segment = dotted.substring(idx, nextDot < 0 ? dotted.length() : nextDot);
      String declaring = NestedTypeResolver.findDeclaringClass(Fqn.of(enclosing), segment, resolver);
      enclosing = declaring != null ? declaring : enclosing + "." + segment;
      if (nextDot < 0) break;
      idx = nextDot + 1;
    }
    return enclosing;
  }

  private static final Fqn JAVA_LANG_ANNOTATION_TARGET = Fqn.of("java.lang.annotation.Target");

  static boolean hasAnnotationArguments(@NotNull SyntaxNode annotation) {
    SyntaxNode paramList = firstChildOfType(annotation, JavaSyntaxElementType.ANNOTATION_PARAMETER_LIST);
    if (paramList == null) return false;
    for (SyntaxNode child = paramList.firstChild(); child != null; child = child.nextSibling()) {
      if (child.getType() == JavaSyntaxElementType.NAME_VALUE_PAIR) return true;
    }
    return false;
  }

  /**
   * Scan the given annotation-type's modifier list for {@code @java.lang.annotation.Target(...)} and
   * decode its {@code ElementType} argument(s) into a {@link TargetType} set. Returns an empty set
   * when {@code @Target} is absent or unresolved — callers treat empty as "applies to declaration
   * positions only" for the lift decision, which is the safe default.
   * <p>
   * Handles both single-value and array shapes:
   * {@code @Target(ElementType.TYPE_USE)} and {@code @Target({ElementType.METHOD, ElementType.PARAMETER})}.
   * Also accepts statically-imported bare names ({@code @Target(TYPE_USE)}).
   */
  @NotNull Set<TargetType> parseAnnotationTargetSet(@Nullable SyntaxNode modifierList,
                                                    @NotNull Set<String> typeVars) {
    if (modifierList == null) return Set.of();
    for (SyntaxNode child = modifierList.firstChild(); child != null; child = child.nextSibling()) {
      if (child.getType() != JavaSyntaxElementType.ANNOTATION) continue;
      SyntaxNode ref = firstChildOfType(child, JavaSyntaxElementType.JAVA_CODE_REFERENCE);
      if (ref == null) continue;
      if (!JAVA_LANG_ANNOTATION_TARGET.value().equals(resolveDotted(ref, typeVars))) continue;
      EnumSet<TargetType> out = EnumSet.noneOf(TargetType.class);
      SyntaxNode paramList = firstChildOfType(child, JavaSyntaxElementType.ANNOTATION_PARAMETER_LIST);
      if (paramList != null) collectTargetReferences(paramList, out);
      return out.isEmpty() ? Set.of() : out;
    }
    return Set.of();
  }

  private static void collectTargetReferences(@NotNull SyntaxNode container,
                                              @NotNull EnumSet<TargetType> out) {
    for (SyntaxNode child = container.firstChild(); child != null; child = child.nextSibling()) {
      SyntaxElementType t = child.getType();
      if (t == JavaSyntaxElementType.NAME_VALUE_PAIR
          || t == JavaSyntaxElementType.ANNOTATION_ARRAY_INITIALIZER) {
        collectTargetReferences(child, out);
      }
      else if (t == JavaSyntaxElementType.REFERENCE_EXPRESSION) {
        String simple = lastIdentifier(child);
        if (simple == null) continue;
        try {
          out.add(TargetType.valueOf(simple));
        }
        catch (IllegalArgumentException ignored) {
          // Unknown ElementType constant (e.g. a JDK 9+ value missing from our enum) — skip silently.
        }
      }
    }
  }

  private static @Nullable String lastIdentifier(@NotNull SyntaxNode refExpression) {
    String last = null;
    for (SyntaxNode c = refExpression.firstChild(); c != null; c = c.nextSibling()) {
      if (c.getType() == JavaSyntaxTokenType.IDENTIFIER) last = c.getText().toString();
    }
    return last;
  }

}
