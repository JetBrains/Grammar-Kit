/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.kotlin;

import com.intellij.platform.syntax.tree.SyntaxNode;
import fleet.org.jetbrains.kotlin.kmp.lexer.KtTokens;
import fleet.org.jetbrains.kotlin.kmp.parser.KtNodeTypes;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmTypeRef;
import org.intellij.grammar.classinfo.JvmTypeRefs;
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
  static final JvmTypeRef IMPLICIT_TYPE =
    new JvmTypeRef.UserType(Fqn.of("<implicit-type>"), List.of(), List.of());

  /** {@code void} return / receiver / setter component — JVM primitive, never annotated. */
  private static final JvmTypeRef VOID_TYPE = new JvmTypeRef.PrimitiveType("void");

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
      m.returnType = typeFormatter.parseType(returnType, typeVars);
    }
    else if (hasExpressionBody(funNode)) {
      // `fun foo() = expr` — return type is inferred from the expression; we can't recover it.
      m.returnType = IMPLICIT_TYPE;
    }
    else {
      m.returnType = VOID_TYPE;
    }

    if (receiverType != null) {
      ParameterSymbol.Builder receiver = new ParameterSymbol.Builder();
      receiver.type = typeFormatter.parseType(receiverType, typeVars);
      receiver.name = "receiver";
      m.parameters.add(receiver);
    }

    collectValueParameters(paramList, m, typeVars);

    m.methodType = defaultMethodType;
    if (receiverType != null) m.methodType = MethodType.STATIC;
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
    m.returnType = VOID_TYPE;

    Set<String> typeVars = new HashSet<>(classTypeVars);
    SyntaxNode modifierList = firstChildOfType(ctorNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    m.modifiers = extractModifiers(modifierList);
    KotlinSyntaxTypeFormatter.MethodAnnotations ctorAnnos = typeFormatter.extractMethodAnnotations(modifierList, typeVars);
    m.annotations.addAll(ctorAnnos.annotations());
    m.exceptions.addAll(ctorAnnos.exceptions());

    SyntaxNode paramList = firstChildOfType(ctorNode, KtNodeTypes.INSTANCE.getVALUE_PARAMETER_LIST());
    collectValueParameters(paramList, m, typeVars);

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
    // @JvmField exposes the property as a direct field — kotlinc emits no getter, neither do we.
    if (KotlinSyntaxNodes.hasJvmField(modifierList)) return null;
    int mods = extractModifiers(modifierList);
    if (Modifier.isPrivate(mods)) return null;

    SyntaxNode typeRef = firstChildOfType(propertyOrParamNode, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
    JvmTypeRef parsed = typeRef == null ? IMPLICIT_TYPE : typeFormatter.parseType(typeRef, classTypeVars);

    MethodSymbol.Builder m = new MethodSymbol.Builder();
    m.declaringClass = declaringFqn;
    m.name = "get" + capitalize(nameId.getText().toString());
    m.modifiers = mods | (staticAccessor ? Modifier.STATIC : 0);
    m.methodType = staticAccessor ? MethodType.STATIC : MethodType.INSTANCE;
    m.returnType = parsed;
    KotlinSyntaxTypeFormatter.MethodAnnotations getterAnnos = typeFormatter.extractMethodAnnotations(modifierList, classTypeVars);
    m.annotations.addAll(getterAnnos.annotations());
    m.exceptions.addAll(getterAnnos.exceptions());
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
    // @JvmField exposes the property as a direct field — kotlinc emits no setter, neither do we.
    if (KotlinSyntaxNodes.hasJvmField(modifierList)) return null;
    int mods = extractModifiers(modifierList);
    if (Modifier.isPrivate(mods)) return null;

    SyntaxNode typeRef = firstChildOfType(propertyOrParamNode, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
    JvmTypeRef parsed = typeRef == null ? IMPLICIT_TYPE : typeFormatter.parseType(typeRef, classTypeVars);

    MethodSymbol.Builder m = new MethodSymbol.Builder();
    m.declaringClass = declaringFqn;
    m.name = "set" + capitalize(nameId.getText().toString());
    m.modifiers = mods | (staticAccessor ? Modifier.STATIC : 0);
    m.methodType = staticAccessor ? MethodType.STATIC : MethodType.INSTANCE;
    m.returnType = VOID_TYPE;
    ParameterSymbol.Builder value = new ParameterSymbol.Builder();
    value.type = parsed;
    value.name = "value";
    m.parameters.add(value);
    KotlinSyntaxTypeFormatter.MethodAnnotations setterAnnos = typeFormatter.extractMethodAnnotations(modifierList, classTypeVars);
    m.annotations.addAll(setterAnnos.annotations());
    m.exceptions.addAll(setterAnnos.exceptions());
    return m;
  }

  /**
   * Synthesise the methods kotlinc auto-generates for a {@code data class}: {@code componentN()}
   * for each primary-ctor val/var property, {@code copy(...)} taking the same parameters,
   * {@code equals(Object)}, {@code hashCode()}, {@code toString()}. Mirrors what the ASM-side
   * extractor sees once the {@code data} modifier triggers kotlinc's synthesis. (Audit #11.)
   */
  void synthesizeDataClassMembers(@NotNull SyntaxNode primaryCtor,
                                  @NotNull Fqn classFqn,
                                  @NotNull java.util.List<String> classTypeParameters,
                                  @NotNull Set<String> classTypeVars,
                                  @NotNull java.util.List<MethodSymbol.Builder> out) {
    SyntaxNode paramList = firstChildOfType(primaryCtor, KtNodeTypes.INSTANCE.getVALUE_PARAMETER_LIST());
    if (paramList == null) return;
    List<ParameterSymbol.Builder> componentParams = new java.util.ArrayList<>();
    int idx = 0;
    for (SyntaxNode p = paramList.firstChild(); p != null; p = p.nextSibling()) {
      if (p.getType() != KtNodeTypes.INSTANCE.getVALUE_PARAMETER()) continue;
      boolean isProperty = firstChildOfType(p, KtTokens.INSTANCE.getVAL_KEYWORD()) != null
                           || firstChildOfType(p, KtTokens.INSTANCE.getVAR_KEYWORD()) != null;
      if (!isProperty) continue;
      SyntaxNode nameId = firstChildOfType(p, KtTokens.INSTANCE.getIDENTIFIER());
      SyntaxNode typeRef = firstChildOfType(p, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
      if (nameId == null) continue;
      JvmTypeRef parsed = typeRef == null ? IMPLICIT_TYPE : typeFormatter.parseType(typeRef, classTypeVars);
      idx++;

      // componentN(): T
      MethodSymbol.Builder componentN = new MethodSymbol.Builder();
      componentN.declaringClass = classFqn;
      componentN.name = "component" + idx;
      componentN.modifiers = Modifier.PUBLIC | Modifier.FINAL;
      componentN.methodType = MethodType.INSTANCE;
      componentN.returnType = parsed;
      out.add(componentN);

      // Remember the parameter for copy(...)
      ParameterSymbol.Builder cp = new ParameterSymbol.Builder();
      cp.name = nameId.getText().toString();
      cp.type = parsed;
      componentParams.add(cp);
    }

    // copy(...): ClassFqn<...> — same parameter shape as the primary constructor's val/var props,
    // returning the data class itself (with its type parameters).
    MethodSymbol.Builder copy = new MethodSymbol.Builder();
    copy.declaringClass = classFqn;
    copy.name = "copy";
    copy.modifiers = Modifier.PUBLIC | Modifier.FINAL;
    copy.methodType = MethodType.INSTANCE;
    copy.returnType = classSelfType(classFqn, classTypeParameters);
    copy.parameters.addAll(componentParams);
    out.add(copy);

    // equals(Object): boolean
    MethodSymbol.Builder equals = new MethodSymbol.Builder();
    equals.declaringClass = classFqn;
    equals.name = "equals";
    equals.modifiers = Modifier.PUBLIC;
    equals.methodType = MethodType.INSTANCE;
    equals.returnType = new JvmTypeRef.PrimitiveType("boolean");
    ParameterSymbol.Builder other = new ParameterSymbol.Builder();
    other.name = "other";
    other.type = new JvmTypeRef.UserType(Fqn.of("java.lang.Object"),
                                         List.of(KotlinSyntaxTypeFormatter.NULLABLE), List.of());
    equals.parameters.add(other);
    out.add(equals);

    // hashCode(): int
    MethodSymbol.Builder hash = new MethodSymbol.Builder();
    hash.declaringClass = classFqn;
    hash.name = "hashCode";
    hash.modifiers = Modifier.PUBLIC;
    hash.methodType = MethodType.INSTANCE;
    hash.returnType = new JvmTypeRef.PrimitiveType("int");
    out.add(hash);

    // toString(): @NotNull String
    MethodSymbol.Builder toStr = new MethodSymbol.Builder();
    toStr.declaringClass = classFqn;
    toStr.name = "toString";
    toStr.modifiers = Modifier.PUBLIC;
    toStr.methodType = MethodType.INSTANCE;
    toStr.returnType = new JvmTypeRef.UserType(Fqn.of("java.lang.String"),
                                               List.of(KotlinSyntaxTypeFormatter.NOT_NULL), List.of());
    out.add(toStr);
  }

  /** {@code @NotNull ClassFqn<T1, T2, ...>} — used as the return type for {@code data class}'s
   * synthesised {@code copy(...)}. */
  private static @NotNull JvmTypeRef classSelfType(@NotNull Fqn classFqn,
                                                   @NotNull java.util.List<String> typeParameters) {
    List<org.intellij.grammar.classinfo.TypeProjection> args = new java.util.ArrayList<>(typeParameters.size());
    for (String tp : typeParameters) {
      args.add(new org.intellij.grammar.classinfo.TypeProjection.WithVariance(
        org.intellij.grammar.classinfo.TypeProjection.Variance.INVARIANT,
        new JvmTypeRef.TypeVariable(tp)));
    }
    return new JvmTypeRef.UserType(classFqn, List.of(KotlinSyntaxTypeFormatter.NOT_NULL), args);
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
      if (boundType != null) info.extendsList.add(typeFormatter.parseType(boundType, typeVars));
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
      JvmTypeRef parsed = pType != null
                          ? typeFormatter.parseType(pType, typeVars)
                          : JvmTypeRefs.missingType("Kotlin VALUE_PARAMETER with no TYPE_REFERENCE at offset "
                                                    + p.getStartOffset() + ".." + p.getEndOffset());
      SyntaxNode mods = firstChildOfType(p, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
      if (KotlinSyntaxNodes.hasModifier(mods, KtTokens.INSTANCE.getVARARG_MODIFIER())) {
        // vararg X → X[] with the outer @NotNull on the array itself; kotlinc emits the array
        // reference as non-null and the element annotation stays on the component (already inside `parsed`).
        parsed = new JvmTypeRef.ArrayType(parsed, List.of(KotlinSyntaxTypeFormatter.NOT_NULL));
      }
      ParameterSymbol.Builder param = new ParameterSymbol.Builder();
      param.type = parsed;
      param.name = pName == null ? "p" + paramIdx : pName.getText().toString();
      param.annotations.addAll(typeFormatter.extractAnnotationFqns(mods, typeVars));
      m.parameters.add(param);
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
    return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }
}
