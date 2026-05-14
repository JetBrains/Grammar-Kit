/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.kotlin;

import com.intellij.platform.syntax.tree.SyntaxNode;
import fleet.org.jetbrains.kotlin.kmp.lexer.KtTokens;
import fleet.org.jetbrains.kotlin.kmp.parser.KtNodeTypes;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.MethodInfo;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.TypeParameterInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.extractModifiers;
import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.firstChildOfType;

/**
 * Translates Kotlin {@code FUN} / {@code PROPERTY} / {@code PRIMARY_CONSTRUCTOR} /
 * {@code SECONDARY_CONSTRUCTOR} nodes into {@link MethodInfo} records.
 * <p>
 * Properties surface as JVM-style {@code getX()} / {@code setX(value)} methods.
 * Extension functions surface as static methods whose first parameter is the receiver type.
 */
@SuppressWarnings("UnstableApiUsage")
final class KotlinSyntaxMethodExtractor {

  private final KotlinSyntaxTypeFormatter typeFormatter;

  KotlinSyntaxMethodExtractor(@NotNull KotlinSyntaxTypeFormatter typeFormatter) {
    this.typeFormatter = typeFormatter;
  }

  /**
   * Build a {@link MethodInfo} for a {@code FUN} node. {@code defaultMethodType} selects
   * {@code STATIC} for top-level / extension functions, {@code INSTANCE} for member functions.
   */
  @Nullable MethodInfo extractFunction(@NotNull SyntaxNode funNode,
                                       @NotNull Fqn declaringFqn,
                                       @NotNull Set<String> classTypeVars,
                                       @NotNull MethodType defaultMethodType) {
    SyntaxNode nameId = nameAfterFunKeyword(funNode);
    if (nameId == null) return null;

    MethodInfo m = new MethodInfo();
    m.declaringClass = declaringFqn;
    m.name = nameId.getText().toString();

    Set<String> typeVars = new HashSet<>(classTypeVars);
    SyntaxNode modifierList = firstChildOfType(funNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    m.modifiers = extractModifiers(modifierList);
    m.annotations.get(0).addAll(typeFormatter.extractAnnotationFqns(modifierList, typeVars));

    collectFunctionTypeParameters(funNode, m, typeVars);

    // Receiver type: TYPE_REFERENCE sibling that appears *before* the identifier.
    SyntaxNode receiverType = receiverTypeBeforeName(funNode, nameId);

    // Return type: TYPE_REFERENCE sibling that appears *after* the VALUE_PARAMETER_LIST.
    SyntaxNode paramList = firstChildOfType(funNode, KtNodeTypes.INSTANCE.getVALUE_PARAMETER_LIST());
    SyntaxNode returnType = returnTypeAfterParams(funNode, paramList);
    String returnStr = returnType == null ? "void" : typeFormatter.formatType(returnType, typeVars);
    m.types.add(returnStr);

    if (receiverType != null) {
      m.types.add(typeFormatter.formatType(receiverType, typeVars));
      m.types.add("receiver");
    }

    collectValueParameters(paramList, m, typeVars);

    m.methodType = defaultMethodType;
    if (receiverType != null) m.methodType = MethodType.STATIC;
    m.annotatedTypes.addAll(m.types);
    return m;
  }

  /** Build a {@link MethodInfo} for a primary or secondary constructor. */
  @Nullable MethodInfo extractConstructor(@NotNull SyntaxNode ctorNode,
                                          @NotNull Fqn declaringFqn,
                                          @NotNull Set<String> classTypeVars) {
    MethodInfo m = new MethodInfo();
    m.declaringClass = declaringFqn;
    m.name = "<init>";
    m.methodType = MethodType.CONSTRUCTOR;
    m.types.add(declaringFqn.value());

    Set<String> typeVars = new HashSet<>(classTypeVars);
    SyntaxNode modifierList = firstChildOfType(ctorNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    m.modifiers = extractModifiers(modifierList);
    m.annotations.get(0).addAll(typeFormatter.extractAnnotationFqns(modifierList, typeVars));

    SyntaxNode paramList = firstChildOfType(ctorNode, KtNodeTypes.INSTANCE.getVALUE_PARAMETER_LIST());
    collectValueParameters(paramList, m, typeVars);

    m.annotatedTypes.addAll(m.types);
    return m;
  }

  /** Synthesises {@code getX()} for a {@code PROPERTY} / primary-ctor {@code val}/{@code var} param. */
  @Nullable MethodInfo synthesizeGetter(@NotNull SyntaxNode propertyOrParamNode,
                                        @NotNull Fqn declaringFqn,
                                        @NotNull Set<String> classTypeVars,
                                        boolean staticAccessor) {
    SyntaxNode nameId = firstChildOfType(propertyOrParamNode, KtTokens.INSTANCE.getIDENTIFIER());
    if (nameId == null) return null;

    SyntaxNode modifierList = firstChildOfType(propertyOrParamNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    int mods = extractModifiers(modifierList);
    if (Modifier.isPrivate(mods)) return null;

    SyntaxNode typeRef = firstChildOfType(propertyOrParamNode, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
    String typeStr = typeRef == null ? "void" : typeFormatter.formatType(typeRef, classTypeVars);

    MethodInfo m = new MethodInfo();
    m.declaringClass = declaringFqn;
    m.name = "get" + capitalize(nameId.getText().toString());
    m.modifiers = mods | (staticAccessor ? Modifier.STATIC : 0);
    m.methodType = staticAccessor ? MethodType.STATIC : MethodType.INSTANCE;
    m.types.add(typeStr);
    m.annotations.get(0).addAll(typeFormatter.extractAnnotationFqns(modifierList, classTypeVars));
    m.annotatedTypes.addAll(m.types);
    return m;
  }

  /** Synthesises {@code setX(value)} for a {@code var} property / primary-ctor {@code var} param. */
  @Nullable MethodInfo synthesizeSetter(@NotNull SyntaxNode propertyOrParamNode,
                                        @NotNull Fqn declaringFqn,
                                        @NotNull Set<String> classTypeVars,
                                        boolean staticAccessor) {
    SyntaxNode nameId = firstChildOfType(propertyOrParamNode, KtTokens.INSTANCE.getIDENTIFIER());
    if (nameId == null) return null;

    SyntaxNode modifierList = firstChildOfType(propertyOrParamNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    int mods = extractModifiers(modifierList);
    if (Modifier.isPrivate(mods)) return null;

    SyntaxNode typeRef = firstChildOfType(propertyOrParamNode, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
    String typeStr = typeRef == null ? "java.lang.Object" : typeFormatter.formatType(typeRef, classTypeVars);

    MethodInfo m = new MethodInfo();
    m.declaringClass = declaringFqn;
    m.name = "set" + capitalize(nameId.getText().toString());
    m.modifiers = mods | (staticAccessor ? Modifier.STATIC : 0);
    m.methodType = staticAccessor ? MethodType.STATIC : MethodType.INSTANCE;
    m.types.add("void");
    m.types.add(typeStr);
    m.types.add("value");
    m.annotations.get(0).addAll(typeFormatter.extractAnnotationFqns(modifierList, classTypeVars));
    m.annotatedTypes.addAll(m.types);
    return m;
  }

  private void collectFunctionTypeParameters(@NotNull SyntaxNode funNode,
                                             @NotNull MethodInfo m,
                                             @NotNull Set<String> typeVars) {
    SyntaxNode tparams = firstChildOfType(funNode, KtNodeTypes.INSTANCE.getTYPE_PARAMETER_LIST());
    if (tparams == null) return;
    for (SyntaxNode tp = tparams.firstChild(); tp != null; tp = tp.nextSibling()) {
      if (tp.getType() != KtNodeTypes.INSTANCE.getTYPE_PARAMETER()) continue;
      SyntaxNode tpId = firstChildOfType(tp, KtTokens.INSTANCE.getIDENTIFIER());
      if (tpId == null) continue;
      String tvName = tpId.getText().toString();
      typeVars.add(tvName);
      TypeParameterInfo info = new TypeParameterInfo(tvName);
      SyntaxNode boundType = firstChildOfType(tp, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
      if (boundType != null) info.getExtendsList().add(typeFormatter.formatType(boundType, typeVars));
      m.generics.add(info);
    }
  }

  private void collectValueParameters(@Nullable SyntaxNode paramList,
                                      @NotNull MethodInfo m,
                                      @NotNull Set<String> typeVars) {
    if (paramList == null) return;
    int paramIdx = m.types.size() == 1 ? 0 : (m.types.size() - 1) / 2; // account for already-added receiver
    for (SyntaxNode p = paramList.firstChild(); p != null; p = p.nextSibling()) {
      if (p.getType() != KtNodeTypes.INSTANCE.getVALUE_PARAMETER()) continue;
      SyntaxNode pType = firstChildOfType(p, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
      SyntaxNode pName = firstChildOfType(p, KtTokens.INSTANCE.getIDENTIFIER());
      String typeStr = pType == null ? "" : typeFormatter.formatType(pType, typeVars);
      SyntaxNode mods = firstChildOfType(p, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
      if (KotlinSyntaxNodes.hasModifier(mods, KtTokens.INSTANCE.getVARARG_MODIFIER())) {
        typeStr = typeStr + "[]";
      }
      m.types.add(typeStr);
      m.types.add(pName == null ? "p" + paramIdx : pName.getText().toString());
      List<Fqn> annos = typeFormatter.extractAnnotationFqns(mods, typeVars);
      if (!annos.isEmpty()) m.annotations.get(paramIdx + 1).addAll(annos);
      paramIdx++;
    }
  }

  /**
   * Function name = the first direct {@code IDENTIFIER} child of a {@code FUN} node. Parameter and
   * return-type identifiers live inside {@code VALUE_PARAMETER} / {@code TYPE_REFERENCE} subtrees,
   * so they don't collide.
   */
  private static @Nullable SyntaxNode nameAfterFunKeyword(@NotNull SyntaxNode funNode) {
    return firstChildOfType(funNode, KtTokens.INSTANCE.getIDENTIFIER());
  }

  /**
   * Receiver TYPE_REFERENCE for an extension function: the only TYPE_REFERENCE sibling that appears
   * before the function's name identifier. {@code KmpSyntaxNode} returns fresh data-class copies
   * from {@code firstChild}/{@code nextSibling}, so siblings are compared by source offset.
   */
  private static @Nullable SyntaxNode receiverTypeBeforeName(@NotNull SyntaxNode funNode,
                                                             @NotNull SyntaxNode nameId) {
    int nameStart = nameId.getStartOffset();
    SyntaxNode receiver = null;
    for (SyntaxNode c = funNode.firstChild(); c != null; c = c.nextSibling()) {
      if (c.getStartOffset() >= nameStart) break;
      if (c.getType() == KtNodeTypes.INSTANCE.getTYPE_REFERENCE()) receiver = c;
    }
    return receiver;
  }

  /** Return-type TYPE_REFERENCE: the TYPE_REFERENCE sibling that appears after the parameter list. */
  private static @Nullable SyntaxNode returnTypeAfterParams(@NotNull SyntaxNode funNode,
                                                            @Nullable SyntaxNode paramList) {
    if (paramList == null) return null;
    int paramListEnd = paramList.getEndOffset();
    for (SyntaxNode c = funNode.firstChild(); c != null; c = c.nextSibling()) {
      if (c.getStartOffset() < paramListEnd) continue;
      if (c.getType() == KtNodeTypes.INSTANCE.getTYPE_REFERENCE()) return c;
    }
    return null;
  }

  private static @NotNull String capitalize(@NotNull String s) {
    if (s.isEmpty()) return s;
    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }
}
