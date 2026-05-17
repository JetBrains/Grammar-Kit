/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.kotlin;

import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import com.intellij.util.SmartList;
import fleet.org.jetbrains.kotlin.kmp.lexer.KtTokens;
import fleet.org.jetbrains.kotlin.kmp.parser.KtNodeTypes;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.intellij.grammar.classinfo.SyntaxTreeUtil.firstChildOfType;

/**
 * Renders Kotlin {@link SyntaxNode} type expressions ({@code TYPE_REFERENCE} / {@code USER_TYPE} /
 * {@code NULLABLE_TYPE} / {@code FUNCTION_TYPE}) into the dotted-FQN string form that
 * {@link ClassSymbol} / {@link MethodSymbol} consumers expect.
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

  /** Kotlin built-in array names → JVM primitive-array descriptors. */
  private static final Map<String, String> PRIMITIVE_ARRAYS = Map.ofEntries(
    Map.entry("IntArray", "int[]"),
    Map.entry("LongArray", "long[]"),
    Map.entry("ShortArray", "short[]"),
    Map.entry("ByteArray", "byte[]"),
    Map.entry("CharArray", "char[]"),
    Map.entry("BooleanArray", "boolean[]"),
    Map.entry("FloatArray", "float[]"),
    Map.entry("DoubleArray", "double[]")
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

  static final Fqn NOT_NULL = Fqn.of("org.jetbrains.annotations.NotNull");
  static final Fqn NULLABLE = Fqn.of("org.jetbrains.annotations.Nullable");

  private final KotlinSyntaxImportContext imports;

  KotlinSyntaxTypeFormatter(@NotNull KotlinSyntaxImportContext imports) {
    this.imports = imports;
  }

  /** Renders a {@code TYPE_REFERENCE} or one of the type-shape children directly. */
  @NotNull String formatType(@Nullable SyntaxNode typeNode, @NotNull Set<String> typeVars) {
    if (typeNode == null) return "";
    SyntaxElementType t = typeNode.getType();
    if (t == KtNodeTypes.INSTANCE.getTYPE_REFERENCE()) {
      SyntaxNode inner = firstNonModifierChild(typeNode);
      return formatType(inner, typeVars);
    }
    if (t == KtNodeTypes.INSTANCE.getNULLABLE_TYPE()) {
      SyntaxNode inner = firstNonModifierChild(typeNode);
      return boxIfPrimitive(formatType(inner, typeVars));
    }
    if (t == KtNodeTypes.INSTANCE.getUSER_TYPE()) {
      return formatUserType(typeNode, typeVars);
    }
    if (t == KtNodeTypes.INSTANCE.getFUNCTION_TYPE()) {
      return "kotlin.Function";
    }
    if (t == KtNodeTypes.INSTANCE.getDYNAMIC_TYPE()) {
      return "java.lang.Object";
    }
    return "";
  }

  /**
   * Returns the declaration-target nullability annotation FQN to attach to a parameter or return
   * derived from {@code typeRef}, or {@code null} when none should be emitted (primitives, void,
   * missing type, bare type-variable refs). Mirrors what kotlinc writes into the bytecode so the
   * source provider matches {@code AsmClassSymbolProvider}'s output for the same class.
   */
  @Nullable Fqn classifyNullability(@Nullable SyntaxNode typeRef,
                                    @NotNull String formattedType,
                                    @NotNull Set<String> typeVars) {
    if (typeRef == null) return null;
    if (isJvmPrimitive(formattedType)) return null;

    SyntaxNode inner = typeRef;
    while (inner != null && inner.getType() == KtNodeTypes.INSTANCE.getTYPE_REFERENCE()) {
      inner = firstNonModifierChild(inner);
    }
    if (inner == null) return null;
    if (inner.getType() == KtNodeTypes.INSTANCE.getNULLABLE_TYPE()) return NULLABLE;
    if (inner.getType() == KtNodeTypes.INSTANCE.getUSER_TYPE()) {
      String dotted = userTypeDotted(inner);
      if (typeVars.contains(dotted)) return null;
    }
    return NOT_NULL;
  }

  private static @NotNull String boxIfPrimitive(@NotNull String t) {
    return switch (t) {
      case "boolean" -> "java.lang.Boolean";
      case "byte" -> "java.lang.Byte";
      case "short" -> "java.lang.Short";
      case "int" -> "java.lang.Integer";
      case "long" -> "java.lang.Long";
      case "char" -> "java.lang.Character";
      case "float" -> "java.lang.Float";
      case "double" -> "java.lang.Double";
      case "void" -> "java.lang.Void";
      default -> t;
    };
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

  private @NotNull String formatUserType(@NotNull SyntaxNode userType, @NotNull Set<String> typeVars) {
    String dotted = userTypeDotted(userType);
    String resolved;
    if (typeVars.contains(dotted)) resolved = dotted;
    else if (dotted.contains(".")) resolved = dotted;
    else resolved = imports.resolveSimpleName(dotted);

    SyntaxNode targs = firstChildOfType(userType, KtNodeTypes.INSTANCE.getTYPE_ARGUMENT_LIST());

    // Special-case kotlin.Array<X> → X[]; built-in *Array typenames → primitive[].
    String prim = PRIMITIVE_ARRAYS.get(dotted);
    if (prim != null && targs == null) return prim;
    if (("Array".equals(dotted) || "kotlin.Array".equals(resolved)) && targs != null) {
      String elem = firstTypeArg(targs, typeVars);
      if (elem != null) return elem + "[]";
    }

    String mapped = JVM_BUILTINS.get(dotted);
    if (mapped != null && targs == null) return mapped;
    // Map kotlin.<Builtin> too (in case the user wrote it qualified).
    if (resolved.startsWith("kotlin.") && targs == null) {
      String tail = resolved.substring("kotlin.".length());
      String mappedQualified = JVM_BUILTINS.get(tail);
      if (mappedQualified != null) return mappedQualified;
    }

    String javaAlias = KOTLIN_TO_JAVA_ALIASES.get(resolved);
    if (javaAlias != null) resolved = javaAlias;

    if (targs == null) return resolved;
    List<String> renderedArgs = new SmartList<>();
    for (SyntaxNode arg = targs.firstChild(); arg != null; arg = arg.nextSibling()) {
      if (arg.getType() != KtNodeTypes.INSTANCE.getTYPE_PROJECTION()) continue;
      renderedArgs.add(formatProjection(arg, typeVars));
    }
    if (renderedArgs.isEmpty()) return resolved;
    return resolved + "<" + String.join(", ", renderedArgs) + ">";
  }

  /**
   * Render a single {@code TYPE_PROJECTION} as a generic argument string. Mirrors what kotlinc
   * writes into bytecode generic signatures so the source-derived string matches what
   * {@link org.intellij.grammar.classinfo.asm.AsmClassSymbolProvider} decodes from the same
   * class compiled.
   *
   * <ul>
   *   <li>{@code *}        → {@code ?}</li>
   *   <li>{@code out X}    → {@code ? extends X}</li>
   *   <li>{@code in X}     → {@code ? super X}</li>
   *   <li>{@code X}        → {@code X}</li>
   * </ul>
   */
  private @NotNull String formatProjection(@NotNull SyntaxNode projection, @NotNull Set<String> typeVars) {
    if (firstChildOfType(projection, KtTokens.INSTANCE.getMUL()) != null) return "?";
    SyntaxNode innerType = firstChildOfType(projection, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
    String rendered = formatType(innerType, typeVars);
    SyntaxNode modList = firstChildOfType(projection, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    if (KotlinSyntaxNodes.hasModifier(modList, KtTokens.INSTANCE.getOUT_MODIFIER())) return "? extends " + rendered;
    if (KotlinSyntaxNodes.hasModifier(modList, KtTokens.INSTANCE.getIN_MODIFIER())) return "? super " + rendered;
    return rendered;
  }

  /**
   * Single-arg helper for the {@code Array<X>} → {@code X[]} special case. JVM arrays don't
   * carry generic wildcards, so {@code in}/{@code out} variance is stripped here; a star
   * projection ({@code Array<*>}) maps to {@code java.lang.Object} — kotlinc's choice for the
   * element type of an unbounded array.
   */
  private @Nullable String firstTypeArg(@NotNull SyntaxNode typeArgList, @NotNull Set<String> typeVars) {
    for (SyntaxNode arg = typeArgList.firstChild(); arg != null; arg = arg.nextSibling()) {
      if (arg.getType() != KtNodeTypes.INSTANCE.getTYPE_PROJECTION()) continue;
      if (firstChildOfType(arg, KtTokens.INSTANCE.getMUL()) != null) return "java.lang.Object";
      SyntaxNode innerType = firstChildOfType(arg, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
      return formatType(innerType, typeVars);
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

  /** Format every {@code SUPER_TYPE_*} entry as a list of raw {@link Fqn}s (no generics, no primitive mapping). */
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
   * {@link ClassSymbol} field type demands an {@link Fqn} rather than a free-form type expression.
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
      String resolved;
      if (typeVars.contains(dotted) || dotted.contains(".")) resolved = dotted;
      else resolved = imports.resolveSimpleName(dotted);
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
        String resolved = dotted.contains(".") ? dotted : imports.resolveSimpleName(dotted);
        String alias = KOTLIN_TO_JAVA_ALIASES.get(resolved);
        return Fqn.of(alias != null ? alias : resolved);
      }
    }
    return null;
  }
}
