/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.kotlin;

import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import com.intellij.util.SmartList;
import fleet.org.jetbrains.kotlin.kmp.lexer.KtTokens;
import fleet.org.jetbrains.kotlin.kmp.parser.KtNodeTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import static org.intellij.grammar.classinfo.SyntaxTreeUtil.childrenOfType;
import static org.intellij.grammar.classinfo.SyntaxTreeUtil.firstChildOfType;

/**
 * Static helpers for navigating Kotlin {@link SyntaxNode} trees. Mirrors
 * {@link org.intellij.grammar.classinfo.java.JavaSyntaxNodes} for the Kotlin AST shape.
 */
@SuppressWarnings("UnstableApiUsage")
final class KotlinSyntaxNodes {

  /** {@code java.lang.reflect.Modifier#ANNOTATION} — not exposed as a public constant on JDK 17. */
  static final int ANNOTATION_MODIFIER_BIT = 0x2000;

  /** Kotlin modifier token → JVM modifier bit. Non-{@code internal} visibility is set explicitly elsewhere. */
  private static final Map<SyntaxElementType, Integer> MODIFIER_BITS = Map.ofEntries(
    Map.entry(KtTokens.INSTANCE.getPRIVATE_MODIFIER(), Modifier.PRIVATE),
    Map.entry(KtTokens.INSTANCE.getPROTECTED_MODIFIER(), Modifier.PROTECTED),
    Map.entry(KtTokens.INSTANCE.getABSTRACT_MODIFIER(), Modifier.ABSTRACT),
    Map.entry(KtTokens.INSTANCE.getSEALED_MODIFIER(), Modifier.ABSTRACT),
    Map.entry(KtTokens.INSTANCE.getFINAL_MODIFIER(), Modifier.FINAL),
    Map.entry(KtTokens.INSTANCE.getENUM_MODIFIER(), Modifier.FINAL)
  );

  private KotlinSyntaxNodes() { }

  static boolean hasModifier(@Nullable SyntaxNode modifierList, @NotNull SyntaxElementType modifier) {
    return modifierList != null && firstChildOfType(modifierList, modifier) != null;
  }

  /** Reads visibility / structural modifier flags and produces the JVM modifier bitmask. */
  static int extractModifiers(@Nullable SyntaxNode modifierList) {
    int bits = 0;
    boolean hasVisibility = false;
    if (modifierList != null) {
      for (SyntaxNode child = modifierList.firstChild(); child != null; child = child.nextSibling()) {
        SyntaxElementType t = child.getType();
        Integer bit = MODIFIER_BITS.get(t);
        if (bit != null) bits |= bit;
        if (t == KtTokens.INSTANCE.getPUBLIC_MODIFIER() ||
            t == KtTokens.INSTANCE.getPRIVATE_MODIFIER() ||
            t == KtTokens.INSTANCE.getPROTECTED_MODIFIER() ||
            t == KtTokens.INSTANCE.getINTERNAL_MODIFIER()) {
          hasVisibility = true;
        }
      }
    }
    // Default visibility in Kotlin is public; `internal` maps to public on the JVM.
    if (!hasVisibility) bits |= Modifier.PUBLIC;
    else if ((bits & (Modifier.PRIVATE | Modifier.PROTECTED)) == 0) bits |= Modifier.PUBLIC;
    return bits;
  }

  /**
   * Name identifier of a declaration ({@code class}/{@code interface}/{@code object}/{@code fun}/
   * {@code val}/{@code var}/{@code typealias}). All of them carry the name as a direct
   * {@code IDENTIFIER} child — modifier-list / type-parameter / annotation identifiers are nested
   * deeper and do not collide.
   */
  static @Nullable SyntaxNode nameIdentifier(@NotNull SyntaxNode declarationNode) {
    return firstChildOfType(declarationNode, KtTokens.INSTANCE.getIDENTIFIER());
  }

  static boolean isInterface(@NotNull SyntaxNode classNode) {
    return firstChildOfType(classNode, KtTokens.INSTANCE.getINTERFACE_KEYWORD()) != null;
  }

  static boolean isAnnotationClass(@NotNull SyntaxNode classNode) {
    SyntaxNode mods = firstChildOfType(classNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    return hasModifier(mods, KtTokens.INSTANCE.getANNOTATION_MODIFIER());
  }

  static boolean isObjectKeyword(@NotNull SyntaxNode declarationNode) {
    return firstChildOfType(declarationNode, KtTokens.INSTANCE.getOBJECT_KEYWORD()) != null;
  }

  static boolean isCompanion(@NotNull SyntaxNode objectDeclaration) {
    SyntaxNode mods = firstChildOfType(objectDeclaration, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    return hasModifier(mods, KtTokens.INSTANCE.getCOMPANION_MODIFIER());
  }

  /** Kotlin nested classes are JVM-static by default; {@code inner} clears that. */
  static boolean isInnerNested(@NotNull SyntaxNode classNode) {
    SyntaxNode mods = firstChildOfType(classNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    return hasModifier(mods, KtTokens.INSTANCE.getINNER_MODIFIER());
  }

  /** {@code const val} → skip from getter/setter synthesis (compiles to a static field, not a method). */
  static boolean isConstProperty(@NotNull SyntaxNode propertyNode) {
    SyntaxNode mods = firstChildOfType(propertyNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    return hasModifier(mods, KtTokens.INSTANCE.getCONST_MODIFIER());
  }

  static boolean isVarProperty(@NotNull SyntaxNode propertyNode) {
    return firstChildOfType(propertyNode, KtTokens.INSTANCE.getVAR_KEYWORD()) != null;
  }

  /**
   * Reads the first positional string-literal argument of an annotation entry, e.g.
   * {@code @JvmName("Foo")} → {@code "Foo"}. Returns {@code null} when the argument is missing,
   * when there is no string literal, or when the literal contains interpolation — kotlinc forbids
   * non-constant arguments to {@code @JvmName} anyway, so the conservative refusal is correct.
   */
  static @Nullable String firstStringArgument(@NotNull SyntaxNode annotationEntry) {
    SyntaxNode argList = firstChildOfType(annotationEntry, KtNodeTypes.INSTANCE.getVALUE_ARGUMENT_LIST());
    if (argList == null) return null;
    SyntaxNode arg = firstChildOfType(argList, KtNodeTypes.INSTANCE.getVALUE_ARGUMENT());
    if (arg == null) return null;
    SyntaxNode template = firstChildOfType(arg, KtNodeTypes.INSTANCE.getSTRING_TEMPLATE());
    if (template == null) return null;
    StringBuilder sb = new StringBuilder();
    for (SyntaxNode c = template.firstChild(); c != null; c = c.nextSibling()) {
      SyntaxElementType ct = c.getType();
      if (ct == KtNodeTypes.INSTANCE.getLITERAL_STRING_TEMPLATE_ENTRY()) {
        sb.append(c.getText().toString());
      }
      else if (ct == KtNodeTypes.INSTANCE.getLONG_STRING_TEMPLATE_ENTRY() ||
               ct == KtNodeTypes.INSTANCE.getSHORT_STRING_TEMPLATE_ENTRY() ||
               ct == KtNodeTypes.INSTANCE.getESCAPE_STRING_TEMPLATE_ENTRY()) {
        return null;
      }
    }
    return sb.toString();
  }

  static boolean hasJvmStatic(@Nullable SyntaxNode modifierList) {
    if (modifierList == null) return false;
    for (SyntaxNode entry : childrenOfType(modifierList, KtNodeTypes.INSTANCE.getANNOTATION_ENTRY())) {
      String name = rightmostIdentifier(entry);
      if ("JvmStatic".equals(name)) return true;
    }
    return false;
  }

  /**
   * Returns the rightmost {@code IDENTIFIER} token text reachable by depth-first descent — used
   * for finding the simple name of an annotation reference like {@code @kotlin.jvm.JvmStatic},
   * which nests {@code CONSTRUCTOR_CALLEE → TYPE_REFERENCE → USER_TYPE → REFERENCE_EXPRESSION →
   * IDENTIFIER}.
   */
  static @Nullable String rightmostIdentifier(@NotNull SyntaxNode node) {
    String last = null;
    if (node.getType() == KtTokens.INSTANCE.getIDENTIFIER()) {
      return node.getText().toString();
    }
    for (SyntaxNode c = node.firstChild(); c != null; c = c.nextSibling()) {
      String inChild = rightmostIdentifier(c);
      if (inChild != null) last = inChild;
    }
    return last;
  }

  /**
   * Dotted text for a {@code DOT_QUALIFIED_EXPRESSION} (used by package + import directives) or a
   * left-recursive {@code USER_TYPE}.
   */
  static @NotNull String buildDottedText(@NotNull SyntaxNode refNode) {
    List<String> parts = new SmartList<>();
    collectDotted(refNode, parts);
    return String.join(".", parts);
  }

  private static void collectDotted(@NotNull SyntaxNode node, @NotNull List<String> out) {
    SyntaxElementType t = node.getType();
    if (t == KtTokens.INSTANCE.getIDENTIFIER()) {
      out.add(node.getText().toString());
      return;
    }
    if (t == KtNodeTypes.INSTANCE.getREFERENCE_EXPRESSION()) {
      SyntaxNode id = firstChildOfType(node, KtTokens.INSTANCE.getIDENTIFIER());
      if (id != null) out.add(id.getText().toString());
      return;
    }
    if (t == KtNodeTypes.INSTANCE.getDOT_QUALIFIED_EXPRESSION() ||
        t == KtNodeTypes.INSTANCE.getUSER_TYPE() ||
        t == KtNodeTypes.INSTANCE.getPACKAGE_DIRECTIVE()) {
      for (SyntaxNode c = node.firstChild(); c != null; c = c.nextSibling()) {
        SyntaxElementType ct = c.getType();
        if (ct == KtTokens.INSTANCE.getDOT()) continue;
        if (ct == KtTokens.INSTANCE.getPACKAGE_KEYWORD()) continue;
        if (ct == KtTokens.INSTANCE.getIDENTIFIER() ||
            ct == KtNodeTypes.INSTANCE.getREFERENCE_EXPRESSION() ||
            ct == KtNodeTypes.INSTANCE.getDOT_QUALIFIED_EXPRESSION() ||
            ct == KtNodeTypes.INSTANCE.getUSER_TYPE()) {
          collectDotted(c, out);
        }
      }
    }
  }

  /** Names of {@code TYPE_PARAMETER} children of a {@code TYPE_PARAMETER_LIST}. */
  static @NotNull List<String> typeParameterNames(@Nullable SyntaxNode typeParameterList) {
    if (typeParameterList == null) return List.of();
    List<String> names = new SmartList<>();
    for (SyntaxNode tp = typeParameterList.firstChild(); tp != null; tp = tp.nextSibling()) {
      if (tp.getType() != KtNodeTypes.INSTANCE.getTYPE_PARAMETER()) continue;
      SyntaxNode id = firstChildOfType(tp, KtTokens.INSTANCE.getIDENTIFIER());
      if (id != null) names.add(id.getText().toString());
    }
    return names;
  }
}
