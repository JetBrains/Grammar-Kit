/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.kotlin;

import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import com.intellij.util.SmartList;
import fleet.org.jetbrains.kotlin.kmp.lexer.KtTokens;
import fleet.org.jetbrains.kotlin.kmp.parser.KtNodeTypes;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmTypeRef;
import org.intellij.grammar.classinfo.JvmTypeRefs;
import org.intellij.grammar.classinfo.TypeProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.intellij.grammar.classinfo.SyntaxTreeUtil.firstChildOfType;

/**
 * Parses Kotlin {@link SyntaxNode} type expressions ({@code TYPE_REFERENCE} / {@code USER_TYPE} /
 * {@code NULLABLE_TYPE} / {@code FUNCTION_TYPE}) into the structured {@link JvmTypeRef} model.
 * <p>
 * Each parsed node carries its own per-position nullability annotation list: reference types get
 * {@link #NOT_NULL} by default and {@link #NULLABLE} when wrapped in {@code NULLABLE_TYPE};
 * primitives and bare type-variable references carry no annotation. Generic type arguments and
 * array element types are parsed recursively, so the inline {@code @NotNull}/{@code @Nullable}
 * marker survives all the way through {@link JvmTypeRefs#renderAnnotated}.
 * <p>
 * Kotlin built-in primitives are mapped to JVM primitives so structural parameter-type matching
 * across Java and Kotlin sources agrees on the same canonical names.
 */
@SuppressWarnings("UnstableApiUsage")
final class KotlinSyntaxTypeFormatter {

  /** Kotlin built-in name → JVM primitive (or {@code void} / {@code java.lang.String}). */
  private static final Map<String, String> JVM_BUILTINS = Map.ofEntries(
    Map.entry("Int", "int"),
    Map.entry("Long", "long"),
    Map.entry("Short", "short"),
    Map.entry("Byte", "byte"),
    Map.entry("Char", "char"),
    Map.entry("Boolean", "boolean"),
    Map.entry("Float", "float"),
    Map.entry("Double", "double"),
    Map.entry("Unit", "void"),
    Map.entry("Nothing", "void"),
    Map.entry("String", "java.lang.String")
  );

  /** Kotlin built-in array names → JVM primitive (component type for the wrapping {@link JvmTypeRef.ArrayType}). */
  private static final Map<String, String> PRIMITIVE_ARRAYS = Map.ofEntries(
    Map.entry("IntArray", "int"),
    Map.entry("LongArray", "long"),
    Map.entry("ShortArray", "short"),
    Map.entry("ByteArray", "byte"),
    Map.entry("CharArray", "char"),
    Map.entry("BooleanArray", "boolean"),
    Map.entry("FloatArray", "float"),
    Map.entry("DoubleArray", "double")
  );

  /** Standard Kotlin classifier FQN → JVM Java alias, mirroring kotlinc's compile-time mapping. */
  private static final Map<String, String> KOTLIN_TO_JAVA_ALIASES = Map.ofEntries(
    Map.entry("kotlin.collections.Iterable",                "java.lang.Iterable"),
    Map.entry("kotlin.collections.MutableIterable",         "java.lang.Iterable"),
    Map.entry("kotlin.collections.Collection",              "java.util.Collection"),
    Map.entry("kotlin.collections.MutableCollection",       "java.util.Collection"),
    Map.entry("kotlin.collections.List",                    "java.util.List"),
    Map.entry("kotlin.collections.MutableList",             "java.util.List"),
    Map.entry("kotlin.collections.Set",                     "java.util.Set"),
    Map.entry("kotlin.collections.MutableSet",              "java.util.Set"),
    Map.entry("kotlin.collections.Map",                     "java.util.Map"),
    Map.entry("kotlin.collections.MutableMap",              "java.util.Map"),
    Map.entry("kotlin.collections.Map.Entry",               "java.util.Map.Entry"),
    Map.entry("kotlin.collections.MutableMap.MutableEntry", "java.util.Map.Entry"),
    Map.entry("kotlin.collections.Iterator",                "java.util.Iterator"),
    Map.entry("kotlin.collections.MutableIterator",         "java.util.Iterator"),
    Map.entry("kotlin.collections.ListIterator",            "java.util.ListIterator"),
    Map.entry("kotlin.collections.MutableListIterator",     "java.util.ListIterator"),
    Map.entry("kotlin.Any",          "java.lang.Object"),
    Map.entry("kotlin.CharSequence", "java.lang.CharSequence"),
    Map.entry("kotlin.Cloneable",    "java.lang.Cloneable"),
    Map.entry("kotlin.Comparable",   "java.lang.Comparable"),
    Map.entry("kotlin.Number",       "java.lang.Number"),
    Map.entry("kotlin.Enum",         "java.lang.Enum"),
    Map.entry("kotlin.Annotation",   "java.lang.annotation.Annotation"),
    Map.entry("kotlin.Throwable",    "java.lang.Throwable")
  );

  /** Kotlin primitive name → boxed JVM class FQN for the {@code Int?} → {@code java.lang.Integer} mapping. */
  private static final Map<String, String> PRIMITIVE_BOXES = Map.ofEntries(
    Map.entry("boolean", "java.lang.Boolean"),
    Map.entry("byte", "java.lang.Byte"),
    Map.entry("short", "java.lang.Short"),
    Map.entry("int", "java.lang.Integer"),
    Map.entry("long", "java.lang.Long"),
    Map.entry("char", "java.lang.Character"),
    Map.entry("float", "java.lang.Float"),
    Map.entry("double", "java.lang.Double"),
    Map.entry("void", "java.lang.Void")
  );

  static final Fqn NOT_NULL = Fqn.of("org.jetbrains.annotations.NotNull");
  static final Fqn NULLABLE = Fqn.of("org.jetbrains.annotations.Nullable");

  private final KotlinSyntaxImportContext imports;
  private @NotNull Map<String, Fqn> nestedScope = Map.of();

  KotlinSyntaxTypeFormatter(@NotNull KotlinSyntaxImportContext imports) {
    this.imports = imports;
  }

  /**
   * In-scope nested class/object/typealias simple-name → FQN map. The extractor sets this before
   * walking a class's members so that an unqualified reference to a sibling nested type (e.g.
   * {@code Direction} inside {@code class Util { enum class Direction; companion object { fun foo(): Direction } }})
   * resolves to the enclosing-class-qualified FQN rather than falling through to the same-package
   * fallback. Also drives qualification of partially-qualified dotted refs whose head segment is in
   * the map (e.g. {@code Wrap.Deep} from outside {@code Wrap} but inside its enclosing class).
   */
  @NotNull Map<String, Fqn> getNestedScope() {
    return nestedScope;
  }

  void setNestedScope(@NotNull Map<String, Fqn> scope) {
    this.nestedScope = scope;
  }

  /**
   * Apply the Java-FQN aliasing from {@link #KOTLIN_TO_JAVA_ALIASES} and return the rewritten
   * string. Used by every resolution site to keep the alias treatment consistent.
   */
  private @NotNull String resolveAliased(@NotNull String dotted, @NotNull Set<String> typeVars) {
    if (typeVars.contains(dotted)) return dotted;
    int firstDot = dotted.indexOf('.');
    String resolved;
    if (firstDot < 0) {
      Fqn nested = nestedScope.get(dotted);
      resolved = nested != null ? nested.value() : imports.resolveSimpleName(dotted);
    }
    else {
      String head = dotted.substring(0, firstDot);
      Fqn nestedHead = nestedScope.get(head);
      resolved = nestedHead != null ? nestedHead.value() + dotted.substring(firstDot) : dotted;
    }
    return resolved;
  }

  /**
   * Parse a Kotlin type expression into the canonical {@link JvmTypeRef} model. Reference-type
   * positions are annotated {@link #NOT_NULL} by default, swapped to {@link #NULLABLE} when wrapped
   * in {@code NULLABLE_TYPE}. Primitives, bare type-variable references, and the synthesised
   * {@code java.lang.Object} component of {@code Array<*>} carry no inline annotation.
   */
  @NotNull JvmTypeRef parseType(@Nullable SyntaxNode typeNode, @NotNull Set<String> typeVars) {
    return parseType(typeNode, typeVars, false);
  }

  /**
   * Like {@link #parseType} but skips the default outer {@code @NotNull} — used for the synthesised
   * {@code java.lang.Object} component of {@code Array<*>}, where kotlinc emits no inline annotation.
   */
  private @NotNull JvmTypeRef parseUnannotated(@Nullable SyntaxNode typeNode, @NotNull Set<String> typeVars) {
    return parseType(typeNode, typeVars, true);
  }

  private @NotNull JvmTypeRef parseType(@Nullable SyntaxNode typeNode,
                                        @NotNull Set<String> typeVars,
                                        boolean suppressOuter) {
    if (typeNode == null) return new JvmTypeRef.UserType(Fqn.of(""), List.of(), List.of());
    SyntaxElementType t = typeNode.getType();
    if (t == KtNodeTypes.INSTANCE.getTYPE_REFERENCE()) {
      return parseType(firstNonModifierChild(typeNode), typeVars, suppressOuter);
    }
    if (t == KtNodeTypes.INSTANCE.getNULLABLE_TYPE()) {
      return parseNullable(typeNode, typeVars);
    }
    if (t == KtNodeTypes.INSTANCE.getUSER_TYPE()) {
      return parseUserType(typeNode, typeVars, suppressOuter ? List.of() : List.of(NOT_NULL));
    }
    if (t == KtNodeTypes.INSTANCE.getFUNCTION_TYPE()) {
      return new JvmTypeRef.FunctionType(suppressOuter ? List.of() : List.of(NOT_NULL));
    }
    if (t == KtNodeTypes.INSTANCE.getDYNAMIC_TYPE()) {
      return new JvmTypeRef.DynamicType(suppressOuter ? List.of() : List.of(NOT_NULL));
    }
    return new JvmTypeRef.UserType(Fqn.of(""), List.of(), List.of());
  }

  /**
   * Parse {@code X?}. The inner type is rendered plain, then the outer annotation is replaced with
   * {@link #NULLABLE}. Kotlin nullable primitives box to their JVM wrapper class — {@code Int?} →
   * {@code java.lang.Integer} carrying {@code @Nullable}.
   */
  private @NotNull JvmTypeRef parseNullable(@NotNull SyntaxNode nullableType, @NotNull Set<String> typeVars) {
    SyntaxNode inner = firstNonModifierChild(nullableType);
    JvmTypeRef base = parseType(inner, typeVars, true);
    if (base instanceof JvmTypeRef.PrimitiveType p) {
      String boxed = PRIMITIVE_BOXES.get(p.name());
      return boxed == null ? p : new JvmTypeRef.UserType(Fqn.of(boxed), List.of(NULLABLE), List.of());
    }
    if (base instanceof JvmTypeRef.TypeVariable t) return t;
    if (base instanceof JvmTypeRef.UserType u) return new JvmTypeRef.UserType(u.name(), List.of(NULLABLE), u.args());
    if (base instanceof JvmTypeRef.ArrayType a) return new JvmTypeRef.ArrayType(a.component(), List.of(NULLABLE));
    if (base instanceof JvmTypeRef.FunctionType) return new JvmTypeRef.FunctionType(List.of(NULLABLE));
    if (base instanceof JvmTypeRef.DynamicType) return new JvmTypeRef.DynamicType(List.of(NULLABLE));
    return base;
  }

  /**
   * Parse a {@code USER_TYPE} into the appropriate {@link JvmTypeRef} record. Handles type
   * variables, JVM-primitive mappings, primitive arrays, the {@code Array<X>} → {@code X[]}
   * transformation (including the {@code Array<*>} → {@code java.lang.Object[]} special case where
   * the component carries no inline annotation), and the Kotlin → Java alias table.
   */
  private @NotNull JvmTypeRef parseUserType(@NotNull SyntaxNode userType,
                                            @NotNull Set<String> typeVars,
                                            @NotNull List<Fqn> outerAnnotations) {
    String dotted = userTypeDotted(userType);
    if (typeVars.contains(dotted)) return new JvmTypeRef.TypeVariable(dotted);
    String resolved = resolveAliased(dotted, typeVars);

    SyntaxNode targs = firstChildOfType(userType, KtNodeTypes.INSTANCE.getTYPE_ARGUMENT_LIST());

    // Primitive array (IntArray etc.) — JVM primitive arrays carry no inline nullability annotation
    // on either the array or the component, mirroring the kotlinc bytecode form.
    String prim = PRIMITIVE_ARRAYS.get(dotted);
    if (prim != null && targs == null) {
      return new JvmTypeRef.ArrayType(new JvmTypeRef.PrimitiveType(prim), List.of());
    }

    // Array<X> → X[] (variance stripped; star projection → bare java.lang.Object component).
    // The kotlinc convention is to emit ONE inline annotation per array, on the component position.
    // The outer ArrayType therefore carries no annotation here — the caller's outerAnnotations is
    // synthesised onto the component via parseArrayComponent (with the Array<*> exception of a
    // synthetic `java.lang.Object` component that takes its own NotNull).
    if (("Array".equals(dotted) || "kotlin.Array".equals(resolved)) && targs != null) {
      JvmTypeRef component = parseArrayComponent(targs, typeVars);
      if (component != null) return new JvmTypeRef.ArrayType(component, List.of());
    }

    // Plain primitive (Int, Long, Unit, ...) — no type arguments expected. JVM_BUILTINS maps
    // Kotlin "String" to "java.lang.String", which is a reference type — split that out below.
    String mapped = JVM_BUILTINS.get(dotted);
    if (mapped != null && targs == null) {
      if (isJvmPrimitive(mapped)) return new JvmTypeRef.PrimitiveType(mapped);
      return new JvmTypeRef.UserType(Fqn.of(mapped), outerAnnotations, List.of());
    }
    // Qualified form: `kotlin.Int`, `kotlin.String`, ...
    if (resolved.startsWith("kotlin.") && targs == null) {
      String mappedQualified = JVM_BUILTINS.get(resolved.substring("kotlin.".length()));
      if (mappedQualified != null) {
        if (isJvmPrimitive(mappedQualified)) return new JvmTypeRef.PrimitiveType(mappedQualified);
        return new JvmTypeRef.UserType(Fqn.of(mappedQualified), outerAnnotations, List.of());
      }
    }

    // Kotlin → Java alias rewrite.
    String javaAlias = KOTLIN_TO_JAVA_ALIASES.get(resolved);
    if (javaAlias != null) resolved = javaAlias;

    List<TypeProjection> args = targs == null ? List.of() : parseProjections(targs, typeVars);
    return new JvmTypeRef.UserType(Fqn.of(resolved), outerAnnotations, args);
  }

  /**
   * Special-case the single {@code Array<X>} type argument:
   * <ul>
   *   <li>{@code Array<*>}        → {@code java.lang.Object} component, no inline annotation</li>
   *   <li>{@code Array<X>} / {@code Array<out X>} / {@code Array<in X>} → component parsed annotated;
   *       variance is stripped (JVM arrays don't carry wildcards)</li>
   * </ul>
   */
  private @Nullable JvmTypeRef parseArrayComponent(@NotNull SyntaxNode typeArgList,
                                                   @NotNull Set<String> typeVars) {
    for (SyntaxNode arg = typeArgList.firstChild(); arg != null; arg = arg.nextSibling()) {
      if (arg.getType() != KtNodeTypes.INSTANCE.getTYPE_PROJECTION()) continue;
      if (firstChildOfType(arg, KtTokens.INSTANCE.getMUL()) != null) {
        // Array<*> → java.lang.Object[]. The synthetic Object component takes NotNull so the array
        // surfaces a single annotation in the rendered form — same convention as Array<String>.
        return new JvmTypeRef.UserType(Fqn.of("java.lang.Object"), List.of(NOT_NULL), List.of());
      }
      SyntaxNode innerType = firstChildOfType(arg, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
      return parseType(innerType, typeVars);
    }
    return null;
  }

  /** Parse every {@code TYPE_PROJECTION} child of {@code TYPE_ARGUMENT_LIST} into a {@link TypeProjection}. */
  private @NotNull List<TypeProjection> parseProjections(@NotNull SyntaxNode typeArgList,
                                                         @NotNull Set<String> typeVars) {
    List<TypeProjection> out = new SmartList<>();
    for (SyntaxNode arg = typeArgList.firstChild(); arg != null; arg = arg.nextSibling()) {
      if (arg.getType() != KtNodeTypes.INSTANCE.getTYPE_PROJECTION()) continue;
      out.add(parseProjection(arg, typeVars));
    }
    return out;
  }

  /**
   * Render a single {@code TYPE_PROJECTION} as a {@link TypeProjection}. Mirrors what kotlinc writes
   * into bytecode generic signatures so the source-derived form matches what {@code AsmClassSymbolProvider}
   * decodes from the same class compiled.
   *
   * <ul>
   *   <li>{@code *}        → {@link TypeProjection.Star}</li>
   *   <li>{@code out X}    → {@link TypeProjection.WithVariance} with {@link TypeProjection.Variance#OUT}</li>
   *   <li>{@code in X}     → {@link TypeProjection.WithVariance} with {@link TypeProjection.Variance#IN}</li>
   *   <li>{@code X}        → {@link TypeProjection.WithVariance} with {@link TypeProjection.Variance#INVARIANT}</li>
   * </ul>
   */
  private @NotNull TypeProjection parseProjection(@NotNull SyntaxNode projection, @NotNull Set<String> typeVars) {
    if (firstChildOfType(projection, KtTokens.INSTANCE.getMUL()) != null) return new TypeProjection.Star();
    SyntaxNode innerType = firstChildOfType(projection, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
    JvmTypeRef inner = parseType(innerType, typeVars);
    SyntaxNode modList = firstChildOfType(projection, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    TypeProjection.Variance variance;
    if (KotlinSyntaxNodes.hasModifier(modList, KtTokens.INSTANCE.getOUT_MODIFIER())) variance = TypeProjection.Variance.OUT;
    else if (KotlinSyntaxNodes.hasModifier(modList, KtTokens.INSTANCE.getIN_MODIFIER())) variance = TypeProjection.Variance.IN;
    else variance = TypeProjection.Variance.INVARIANT;
    return new TypeProjection.WithVariance(variance, inner);
  }

  private static boolean isJvmPrimitive(@NotNull String t) {
    return switch (t) {
      case "void", "boolean", "byte", "short", "int", "long", "char", "float", "double" -> true;
      default -> false;
    };
  }

  private static @Nullable SyntaxNode firstNonModifierChild(@NotNull SyntaxNode node) {
    for (SyntaxNode c = node.firstChild(); c != null; c = c.nextSibling()) {
      SyntaxElementType ct = c.getType();
      if (ct == KtNodeTypes.INSTANCE.getMODIFIER_LIST()) continue;
      if (ct == KtTokens.INSTANCE.getQUEST()) continue;
      if (ct == KtTokens.INSTANCE.getLPAR() || ct == KtTokens.INSTANCE.getRPAR()) continue;
      return c;
    }
    return null;
  }

  /**
   * Dotted text of a {@code USER_TYPE}. Kotlin nests user types left-recursively for qualified
   * names ({@code Outer.Inner} → {@code USER_TYPE(USER_TYPE(Outer), Inner)}); walk that chain.
   */
  private static @NotNull String userTypeDotted(@NotNull SyntaxNode userType) {
    List<String> segments = new SmartList<>();
    collectUserTypeSegments(userType, segments);
    return String.join(".", segments);
  }

  private static void collectUserTypeSegments(@NotNull SyntaxNode userType, @NotNull List<String> out) {
    for (SyntaxNode c = userType.firstChild(); c != null; c = c.nextSibling()) {
      SyntaxElementType ct = c.getType();
      if (ct == KtNodeTypes.INSTANCE.getUSER_TYPE()) {
        collectUserTypeSegments(c, out);
      }
      else if (ct == KtNodeTypes.INSTANCE.getREFERENCE_EXPRESSION()) {
        SyntaxNode id = firstChildOfType(c, KtTokens.INSTANCE.getIDENTIFIER());
        if (id != null) out.add(id.getText().toString());
      }
    }
  }

  /** Parse every {@code SUPER_TYPE_*} entry as a list of raw {@link Fqn}s (no generics, no primitive mapping). */
  @NotNull List<Fqn> formatSuperTypes(@Nullable SyntaxNode superTypeList, @NotNull Set<String> typeVars) {
    if (superTypeList == null) return List.of();
    List<Fqn> result = new SmartList<>();
    for (SyntaxNode entry = superTypeList.firstChild(); entry != null; entry = entry.nextSibling()) {
      SyntaxElementType t = entry.getType();
      if (t == KtNodeTypes.INSTANCE.getSUPER_TYPE_CALL_ENTRY()) {
        SyntaxNode callee = firstChildOfType(entry, KtNodeTypes.INSTANCE.getCONSTRUCTOR_CALLEE());
        if (callee != null) {
          SyntaxNode typeRef = firstChildOfType(callee, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
          result.add(formatTypeFqn(typeRef, typeVars));
        }
      }
      else if (t == KtNodeTypes.INSTANCE.getSUPER_TYPE_ENTRY() ||
               t == KtNodeTypes.INSTANCE.getDELEGATED_SUPER_TYPE_ENTRY()) {
        SyntaxNode typeRef = firstChildOfType(entry, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
        result.add(formatTypeFqn(typeRef, typeVars));
      }
    }
    return result;
  }

  /**
   * Resolves a type expression to its raw class FQN — no generics, no primitive mapping, no array
   * transform. Used for supertypes, annotation types, and typealias right-hand sides where the
   * {@link org.intellij.grammar.classinfo.ClassSymbol} field type demands an {@link Fqn} rather
   * than a structured type expression.
   */
  @NotNull Fqn formatTypeFqn(@Nullable SyntaxNode typeNode, @NotNull Set<String> typeVars) {
    if (typeNode == null) return Fqn.of("");
    SyntaxElementType t = typeNode.getType();
    if (t == KtNodeTypes.INSTANCE.getTYPE_REFERENCE()) {
      return formatTypeFqn(firstNonModifierChild(typeNode), typeVars);
    }
    if (t == KtNodeTypes.INSTANCE.getNULLABLE_TYPE()) {
      return formatTypeFqn(firstNonModifierChild(typeNode), typeVars);
    }
    if (t == KtNodeTypes.INSTANCE.getUSER_TYPE()) {
      String dotted = userTypeDotted(typeNode);
      String resolved = resolveAliased(dotted, typeVars);
      String javaAlias = KOTLIN_TO_JAVA_ALIASES.get(resolved);
      return Fqn.of(javaAlias != null ? javaAlias : resolved);
    }
    if (t == KtNodeTypes.INSTANCE.getFUNCTION_TYPE()) return Fqn.of("kotlin.Function");
    if (t == KtNodeTypes.INSTANCE.getDYNAMIC_TYPE()) return Fqn.of("java.lang.Object");
    return Fqn.of("");
  }

  /** Whether the first entry of a {@code SUPER_TYPE_LIST} is a {@code SUPER_TYPE_CALL_ENTRY}. */
  static boolean hasCallEntry(@Nullable SyntaxNode superTypeList) {
    if (superTypeList == null) return false;
    for (SyntaxNode entry = superTypeList.firstChild(); entry != null; entry = entry.nextSibling()) {
      if (entry.getType() == KtNodeTypes.INSTANCE.getSUPER_TYPE_CALL_ENTRY()) return true;
    }
    return false;
  }

  /** Collect annotation FQNs from a {@code MODIFIER_LIST}. */
  @NotNull List<Fqn> extractAnnotationFqns(@Nullable SyntaxNode modifierList, @NotNull Set<String> typeVars) {
    if (modifierList == null) return List.of();
    List<Fqn> out = new SmartList<>();
    for (SyntaxNode entry = modifierList.firstChild(); entry != null; entry = entry.nextSibling()) {
      if (entry.getType() != KtNodeTypes.INSTANCE.getANNOTATION_ENTRY()) continue;
      SyntaxNode callee = firstChildOfType(entry, KtNodeTypes.INSTANCE.getCONSTRUCTOR_CALLEE());
      SyntaxNode typeRef = callee == null ? firstChildOfType(entry, KtNodeTypes.INSTANCE.getTYPE_REFERENCE())
                                          : firstChildOfType(callee, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
      if (typeRef == null || isIgnoredAnnotation(typeRef)) continue;
      out.add(formatTypeFqn(typeRef, typeVars));
    }
    return out;
  }

  /**
   * Source-only Kotlin annotations that carry no JVM runtime meaning and must not surface in the
   * extracted symbols. {@code @kotlin.Suppress} is auto-imported, so the simple-name match covers
   * both {@code @Suppress} and {@code @kotlin.Suppress} (same convention as {@code @JvmStatic}/{@code @Throws}).
   */
  private static boolean isIgnoredAnnotation(@NotNull SyntaxNode typeRef) {
    return "Suppress".equals(KotlinSyntaxNodes.rightmostIdentifier(typeRef));
  }

  /**
   * Annotation extraction tailored to functions/constructors/property-accessors: separates
   * {@code @Throws(E::class, ...)} entries — whose semantic meaning on the JVM is a checked
   * exception declaration — from regular annotation entries. The {@code @Throws} type itself is
   * omitted from the returned annotations; its class-literal arguments populate the exceptions list.
   */
  record MethodAnnotations(@NotNull List<Fqn> annotations, @NotNull List<Fqn> exceptions) { }

  @NotNull MethodAnnotations extractMethodAnnotations(@Nullable SyntaxNode modifierList,
                                                      @NotNull Set<String> typeVars) {
    if (modifierList == null) return new MethodAnnotations(List.of(), List.of());
    List<Fqn> annotations = new SmartList<>();
    List<Fqn> exceptions = new SmartList<>();
    for (SyntaxNode entry = modifierList.firstChild(); entry != null; entry = entry.nextSibling()) {
      if (entry.getType() != KtNodeTypes.INSTANCE.getANNOTATION_ENTRY()) continue;
      SyntaxNode callee = firstChildOfType(entry, KtNodeTypes.INSTANCE.getCONSTRUCTOR_CALLEE());
      SyntaxNode typeRef = callee == null ? firstChildOfType(entry, KtNodeTypes.INSTANCE.getTYPE_REFERENCE())
                                          : firstChildOfType(callee, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
      if (typeRef == null) continue;
      // Check the simple name of the annotation type, not the entry's rightmost identifier — that
      // would walk into VALUE_ARGUMENT_LIST and pick up an identifier from `Foo::class` instead.
      if ("Throws".equals(KotlinSyntaxNodes.rightmostIdentifier(typeRef))) {
        collectThrowsArgumentFqns(entry, exceptions);
        continue;
      }
      if (isIgnoredAnnotation(typeRef)) continue;
      annotations.add(formatTypeFqn(typeRef, typeVars));
    }
    return new MethodAnnotations(annotations, exceptions);
  }

  private void collectThrowsArgumentFqns(@NotNull SyntaxNode annotationEntry, @NotNull List<Fqn> out) {
    SyntaxNode argList = firstChildOfType(annotationEntry, KtNodeTypes.INSTANCE.getVALUE_ARGUMENT_LIST());
    if (argList == null) return;
    for (SyntaxNode arg = argList.firstChild(); arg != null; arg = arg.nextSibling()) {
      if (arg.getType() != KtNodeTypes.INSTANCE.getVALUE_ARGUMENT()) continue;
      SyntaxNode classLit = firstChildOfType(arg, KtNodeTypes.INSTANCE.getCLASS_LITERAL_EXPRESSION());
      if (classLit == null) continue;
      Fqn fqn = resolveClassLiteralFqn(classLit);
      if (fqn != null) out.add(fqn);
    }
  }

  /**
   * Resolves the receiver of a {@code Foo::class} class-literal to a class FQN. The receiver is the
   * {@code REFERENCE_EXPRESSION} (simple name) or {@code DOT_QUALIFIED_EXPRESSION} (qualified) child
   * that precedes the {@code ::}; both shapes are handled by {@link KotlinSyntaxNodes#buildDottedText}.
   */
  private @Nullable Fqn resolveClassLiteralFqn(@NotNull SyntaxNode classLit) {
    for (SyntaxNode c = classLit.firstChild(); c != null; c = c.nextSibling()) {
      SyntaxElementType t = c.getType();
      if (t == KtNodeTypes.INSTANCE.getREFERENCE_EXPRESSION() ||
          t == KtNodeTypes.INSTANCE.getDOT_QUALIFIED_EXPRESSION()) {
        String dotted = KotlinSyntaxNodes.buildDottedText(c);
        if (dotted.isEmpty()) return null;
        String resolved = resolveAliased(dotted, Set.of());
        String alias = KOTLIN_TO_JAVA_ALIASES.get(resolved);
        return Fqn.of(alias != null ? alias : resolved);
      }
    }
    return null;
  }
}
