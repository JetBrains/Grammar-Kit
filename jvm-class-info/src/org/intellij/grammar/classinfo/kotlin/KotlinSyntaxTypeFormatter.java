/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.kotlin;

import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import com.intellij.util.SmartList;
import fleet.org.jetbrains.kotlin.kmp.lexer.KtTokens;
import fleet.org.jetbrains.kotlin.kmp.parser.KtNodeTypes;
import org.intellij.grammar.classinfo.ClassInfo;
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
 * {@link ClassInfo} / {@link MethodSymbol} consumers expect.
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
      return formatType(inner, typeVars); // drop the '?'
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

    if (targs == null) return resolved;
    List<String> renderedArgs = new SmartList<>();
    for (SyntaxNode arg = targs.firstChild(); arg != null; arg = arg.nextSibling()) {
      if (arg.getType() != KtNodeTypes.INSTANCE.getTYPE_PROJECTION()) continue;
      SyntaxNode innerType = firstChildOfType(arg, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
      renderedArgs.add(formatType(innerType, typeVars));
    }
    if (renderedArgs.isEmpty()) return resolved;
    return resolved + "<" + String.join(", ", renderedArgs) + ">";
  }

  private @Nullable String firstTypeArg(@NotNull SyntaxNode typeArgList, @NotNull Set<String> typeVars) {
    for (SyntaxNode arg = typeArgList.firstChild(); arg != null; arg = arg.nextSibling()) {
      if (arg.getType() != KtNodeTypes.INSTANCE.getTYPE_PROJECTION()) continue;
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
   * {@link ClassInfo} field type demands an {@link Fqn} rather than a free-form type expression.
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
      if (typeVars.contains(dotted) || dotted.contains(".")) return Fqn.of(dotted);
      return Fqn.of(imports.resolveSimpleName(dotted));
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
      if (typeRef != null) out.add(formatTypeFqn(typeRef, typeVars));
    }
    return out;
  }
}
