/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.kotlin;

import com.intellij.platform.syntax.tree.SyntaxNode;
import fleet.org.jetbrains.kotlin.kmp.lexer.KtTokens;
import fleet.org.jetbrains.kotlin.kmp.parser.KtNodeTypes;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.ParameterSymbol;
import org.intellij.grammar.classinfo.TypeParameterSymbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.intellij.grammar.classinfo.SyntaxTreeUtil.firstChildOfType;
import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.extractModifiers;

/**
 * Translates Kotlin {@code FUN} / {@code PROPERTY} / {@code PRIMARY_CONSTRUCTOR} /
 * {@code SECONDARY_CONSTRUCTOR} nodes into {@link MethodSymbol} builders.
 * <p>
 * Properties surface as JVM-style {@code getX()} / {@code setX(value)} methods.
 * Extension functions surface as static methods whose first parameter is the receiver type.
 */
@SuppressWarnings("UnstableApiUsage")
final class KotlinSyntaxMethodExtractor {

  /** Sentinel used when a property/function has no explicit type and the real type is only
   * recoverable by inferring from an expression body — which is not derivable from syntax alone. */
  static final String IMPLICIT_TYPE = "<implicit-type>";

  private final KotlinSyntaxTypeFormatter typeFormatter;

  KotlinSyntaxMethodExtractor(@NotNull KotlinSyntaxTypeFormatter typeFormatter) {
    this.typeFormatter = typeFormatter;
  }

  /**
   * Build a {@link MethodSymbol.Builder} for a {@code FUN} node. {@code defaultMethodType} selects
   * {@code STATIC} for top-level / extension functions, {@code INSTANCE} for member functions.
   */
  @Nullable MethodSymbol.Builder extractFunction(@NotNull SyntaxNode funNode,
                                                 @NotNull Fqn declaringFqn,
                                                 @NotNull Set<String> classTypeVars,
                                                 @NotNull MethodType defaultMethodType) {
    SyntaxNode nameId = nameAfterFunKeyword(funNode);
    if (nameId == null) return null;

    MethodSymbol.Builder m = new MethodSymbol.Builder();
    m.declaringClass = declaringFqn;
    m.name = nameId.getText().toString();

    Set<String> typeVars = new HashSet<>(classTypeVars);
    SyntaxNode modifierList = firstChildOfType(funNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    m.modifiers = extractModifiers(modifierList);
    KotlinSyntaxTypeFormatter.MethodAnnotations funAnnos = typeFormatter.extractMethodAnnotations(modifierList, typeVars);
    m.annotations.addAll(funAnnos.annotations());
    m.exceptions.addAll(funAnnos.exceptions());

    collectFunctionTypeParameters(funNode, m, typeVars);

    // Receiver type: TYPE_REFERENCE sibling that appears *before* the identifier.
    SyntaxNode receiverType = receiverTypeBeforeName(funNode, nameId);

    // Return type: TYPE_REFERENCE sibling that appears *after* the VALUE_PARAMETER_LIST.
    SyntaxNode paramList = firstChildOfType(funNode, KtNodeTypes.INSTANCE.getVALUE_PARAMETER_LIST());
    SyntaxNode returnType = returnTypeAfterParams(funNode, paramList);
    if (returnType != null) {
      m.returnType = typeFormatter.formatType(returnType, typeVars);
      m.annotatedReturnType = annotatedWithNullability(returnType, m.returnType, typeVars);
    }
    else if (hasExpressionBody(funNode)) {
      // `fun foo() = expr` — return type is inferred from the expression; we can't recover it.
      m.returnType = IMPLICIT_TYPE;
    }
    else {
      m.returnType = "void";
    }

    if (receiverType != null) {
      ParameterSymbol.Builder receiver = new ParameterSymbol.Builder();
      receiver.type = typeFormatter.formatType(receiverType, typeVars);
      receiver.name = "receiver";
      addNullabilityAnnotation(receiver.annotations, receiverType, receiver.type, typeVars);
      m.parameters.add(receiver);
    }

    collectValueParameters(paramList, m, typeVars);

    m.methodType = defaultMethodType;
    if (receiverType != null) m.methodType = MethodType.STATIC;
    copyTypesAsAnnotated(m);
    return m;
  }

  /** Build a {@link MethodSymbol.Builder} for a primary or secondary constructor. */
  @Nullable MethodSymbol.Builder extractConstructor(@NotNull SyntaxNode ctorNode,
                                                    @NotNull Fqn declaringFqn,
                                                    @NotNull Set<String> classTypeVars) {
    MethodSymbol.Builder m = new MethodSymbol.Builder();
    m.declaringClass = declaringFqn;
    m.name = "<init>";
    m.methodType = MethodType.CONSTRUCTOR;
    m.returnType = declaringFqn.value();

    Set<String> typeVars = new HashSet<>(classTypeVars);
    SyntaxNode modifierList = firstChildOfType(ctorNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    m.modifiers = extractModifiers(modifierList);
    KotlinSyntaxTypeFormatter.MethodAnnotations ctorAnnos = typeFormatter.extractMethodAnnotations(modifierList, typeVars);
    m.annotations.addAll(ctorAnnos.annotations());
    m.exceptions.addAll(ctorAnnos.exceptions());

    SyntaxNode paramList = firstChildOfType(ctorNode, KtNodeTypes.INSTANCE.getVALUE_PARAMETER_LIST());
    collectValueParameters(paramList, m, typeVars);

    copyTypesAsAnnotated(m);
    return m;
  }

  /** Synthesises {@code getX()} for a {@code PROPERTY} / primary-ctor {@code val}/{@code var} param. */
  @Nullable MethodSymbol.Builder synthesizeGetter(@NotNull SyntaxNode propertyOrParamNode,
                                                  @NotNull Fqn declaringFqn,
                                                  @NotNull Set<String> classTypeVars,
                                                  boolean staticAccessor) {
    SyntaxNode nameId = firstChildOfType(propertyOrParamNode, KtTokens.INSTANCE.getIDENTIFIER());
    if (nameId == null) return null;

    SyntaxNode modifierList = firstChildOfType(propertyOrParamNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    int mods = extractModifiers(modifierList);
    if (Modifier.isPrivate(mods)) return null;

    SyntaxNode typeRef = firstChildOfType(propertyOrParamNode, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
    String typeStr = typeRef == null ? IMPLICIT_TYPE : typeFormatter.formatType(typeRef, classTypeVars);

    MethodSymbol.Builder m = new MethodSymbol.Builder();
    m.declaringClass = declaringFqn;
    m.name = "get" + capitalize(nameId.getText().toString());
    m.modifiers = mods | (staticAccessor ? Modifier.STATIC : 0);
    m.methodType = staticAccessor ? MethodType.STATIC : MethodType.INSTANCE;
    m.returnType = typeStr;
    KotlinSyntaxTypeFormatter.MethodAnnotations getterAnnos = typeFormatter.extractMethodAnnotations(modifierList, classTypeVars);
    m.annotations.addAll(getterAnnos.annotations());
    m.exceptions.addAll(getterAnnos.exceptions());
    if (typeRef != null) m.annotatedReturnType = annotatedWithNullability(typeRef, typeStr, classTypeVars);
    copyTypesAsAnnotated(m);
    return m;
  }

  /** Synthesises {@code setX(value)} for a {@code var} property / primary-ctor {@code var} param. */
  @Nullable MethodSymbol.Builder synthesizeSetter(@NotNull SyntaxNode propertyOrParamNode,
                                                  @NotNull Fqn declaringFqn,
                                                  @NotNull Set<String> classTypeVars,
                                                  boolean staticAccessor) {
    SyntaxNode nameId = firstChildOfType(propertyOrParamNode, KtTokens.INSTANCE.getIDENTIFIER());
    if (nameId == null) return null;

    SyntaxNode modifierList = firstChildOfType(propertyOrParamNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    int mods = extractModifiers(modifierList);
    if (Modifier.isPrivate(mods)) return null;

    SyntaxNode typeRef = firstChildOfType(propertyOrParamNode, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
    String typeStr = typeRef == null ? IMPLICIT_TYPE : typeFormatter.formatType(typeRef, classTypeVars);

    MethodSymbol.Builder m = new MethodSymbol.Builder();
    m.declaringClass = declaringFqn;
    m.name = "set" + capitalize(nameId.getText().toString());
    m.modifiers = mods | (staticAccessor ? Modifier.STATIC : 0);
    m.methodType = staticAccessor ? MethodType.STATIC : MethodType.INSTANCE;
    m.returnType = "void";
    ParameterSymbol.Builder value = new ParameterSymbol.Builder();
    value.type = typeStr;
    value.name = "value";
    if (typeRef != null) addNullabilityAnnotation(value.annotations, typeRef, typeStr, classTypeVars);
    m.parameters.add(value);
    KotlinSyntaxTypeFormatter.MethodAnnotations setterAnnos = typeFormatter.extractMethodAnnotations(modifierList, classTypeVars);
    m.annotations.addAll(setterAnnos.annotations());
    m.exceptions.addAll(setterAnnos.exceptions());
    copyTypesAsAnnotated(m);
    return m;
  }

  private void collectFunctionTypeParameters(@NotNull SyntaxNode funNode,
                                             @NotNull MethodSymbol.Builder m,
                                             @NotNull Set<String> typeVars) {
    SyntaxNode tparams = firstChildOfType(funNode, KtNodeTypes.INSTANCE.getTYPE_PARAMETER_LIST());
    if (tparams == null) return;
    for (SyntaxNode tp = tparams.firstChild(); tp != null; tp = tp.nextSibling()) {
      if (tp.getType() != KtNodeTypes.INSTANCE.getTYPE_PARAMETER()) continue;
      SyntaxNode tpId = firstChildOfType(tp, KtTokens.INSTANCE.getIDENTIFIER());
      if (tpId == null) continue;
      String tvName = tpId.getText().toString();
      typeVars.add(tvName);
      TypeParameterSymbol.Builder info = new TypeParameterSymbol.Builder(tvName);
      SyntaxNode boundType = firstChildOfType(tp, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
      if (boundType != null) info.extendsList.add(typeFormatter.formatType(boundType, typeVars));
      m.generics.add(info);
    }
  }

  private void collectValueParameters(@Nullable SyntaxNode paramList,
                                      @NotNull MethodSymbol.Builder m,
                                      @NotNull Set<String> typeVars) {
    if (paramList == null) return;
    int paramIdx = m.parameters.size(); // account for already-added receiver
    for (SyntaxNode p = paramList.firstChild(); p != null; p = p.nextSibling()) {
      if (p.getType() != KtNodeTypes.INSTANCE.getVALUE_PARAMETER()) continue;
      SyntaxNode pType = firstChildOfType(p, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
      SyntaxNode pName = firstChildOfType(p, KtTokens.INSTANCE.getIDENTIFIER());
      String typeStr = pType == null ? "" : typeFormatter.formatType(pType, typeVars);
      SyntaxNode mods = firstChildOfType(p, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
      if (KotlinSyntaxNodes.hasModifier(mods, KtTokens.INSTANCE.getVARARG_MODIFIER())) {
        typeStr = typeStr + "[]";
      }
      ParameterSymbol.Builder param = new ParameterSymbol.Builder();
      param.type = typeStr;
      param.name = pName == null ? "p" + paramIdx : pName.getText().toString();
      param.annotations.addAll(typeFormatter.extractAnnotationFqns(mods, typeVars));
      addNullabilityAnnotation(param.annotations, pType, typeStr, typeVars);
      m.parameters.add(param);
      paramIdx++;
    }
  }

  private static void copyTypesAsAnnotated(@NotNull MethodSymbol.Builder m) {
    if (m.annotatedReturnType == null) m.annotatedReturnType = m.returnType;
    for (ParameterSymbol.Builder p : m.parameters) {
      if (p.annotatedType == null) p.annotatedType = p.type;
    }
  }

  /**
   * Synthesizes the {@code @NotNull} / {@code @Nullable} declaration-target annotation that kotlinc
   * would emit for the given Kotlin {@code typeRef}, and appends it to {@code target} unless the
   * same FQN is already there (which it would be if the source explicitly wrote the annotation).
   */
  private void addNullabilityAnnotation(@NotNull List<Fqn> target,
                                        @Nullable SyntaxNode typeRef,
                                        @NotNull String formattedType,
                                        @NotNull Set<String> typeVars) {
    Fqn anno = typeFormatter.classifyNullability(typeRef, formattedType, typeVars);
    if (anno != null && !target.contains(anno)) target.add(anno);
  }

  /**
   * Returns {@code formattedType} with the synthesized nullability annotation inlined at the
   * start (e.g. {@code "@org.jetbrains.annotations.NotNull java.lang.String"}), matching the
   * format used by {@code AsmClassSymbolProvider} for bytecode type annotations. Returns the
   * input unchanged when no annotation applies (primitive, type variable, or null typeRef).
   */
  private @NotNull String annotatedWithNullability(@Nullable SyntaxNode typeRef,
                                                   @NotNull String formattedType,
                                                   @NotNull Set<String> typeVars) {
    Fqn anno = typeFormatter.classifyNullability(typeRef, formattedType, typeVars);
    return anno == null ? formattedType : "@" + anno + " " + formattedType;
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

  /** True when {@code funNode} uses an expression body (`fun foo() = expr`) rather than a block body
   * or no body. The {@code EQ} token is a direct child of {@code FUN} only for expression bodies. */
  private static boolean hasExpressionBody(@NotNull SyntaxNode funNode) {
    return firstChildOfType(funNode, KtTokens.INSTANCE.getEQ()) != null;
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
