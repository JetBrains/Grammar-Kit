/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

import com.intellij.java.syntax.element.JavaSyntaxElementType;
import com.intellij.java.syntax.element.JavaSyntaxTokenType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.platform.syntax.tree.SyntaxNode;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmTypeRef;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.ParameterSymbol;
import org.intellij.grammar.classinfo.TypeParameterSymbol;
import org.intellij.grammar.classinfo.TypeUseAnnotationLifter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.intellij.grammar.classinfo.SyntaxTreeUtil.firstChildOfType;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.extractModifiers;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.firstNameIdentifier;

/**
 * Builds a {@link MethodSymbol} from a {@code METHOD} (or {@code ANNOTATION_METHOD})
 * {@link SyntaxNode}. Type-parameter scope inherited from the enclosing class is extended with
 * the method's own type parameters before any reference is rendered.
 */
@SuppressWarnings("UnstableApiUsage")
final class JavaSyntaxMethodExtractor {

  private static final Logger LOG = Logger.getInstance(JavaSyntaxMethodExtractor.class);

  private static final JvmTypeRef VOID_TYPE = new JvmTypeRef.PrimitiveType("void");

  private final JavaSyntaxTypeFormatter typeFormatter;

  JavaSyntaxMethodExtractor(@NotNull JavaSyntaxTypeFormatter typeFormatter) {
    this.typeFormatter = typeFormatter;
  }

  @Nullable MethodSymbol.Builder extract(@NotNull SyntaxNode methodNode,
                                         @NotNull Fqn declaringFqn,
                                         @NotNull Set<String> classTypeVars) {
    MethodSymbol.Builder m = new MethodSymbol.Builder();
    m.declaringClass = declaringFqn;

    Set<String> typeVars = new HashSet<>(classTypeVars);
    collectMethodTypeParameters(methodNode, m, typeVars);

    SyntaxNode modifierList = firstChildOfType(methodNode, JavaSyntaxElementType.MODIFIER_LIST);
    m.modifiers = extractModifiers(modifierList);
    List<Fqn> methodAnnotations = typeFormatter.extractAnnotationFqns(modifierList, typeVars);

    SyntaxNode nameId = firstNameIdentifier(methodNode);
    if (nameId == null) return null;
    m.name = nameId.getText().toString();

    SyntaxNode returnType = firstChildOfType(methodNode, JavaSyntaxElementType.TYPE);
    if (returnType == null) {
      m.methodType = MethodType.CONSTRUCTOR;
      m.returnType = VOID_TYPE;
      // Constructors have no user-written return type; keep all declaration-list annotations as-is.
      m.annotations.addAll(methodAnnotations);
    }
    else {
      m.methodType = Modifier.isStatic(m.modifiers) ? MethodType.STATIC : MethodType.INSTANCE;
      JvmTypeRef parsed = typeFormatter.parseType(returnType, typeVars);
      TypeUseAnnotationLifter.LiftResult lifted = TypeUseAnnotationLifter.lift(methodAnnotations, parsed, typeFormatter::isTypeUseAnnotation);
      m.annotations.addAll(lifted.declarationAnnotations());
      m.returnType = lifted.type();
    }

    collectParameters(methodNode, m, typeVars);

    m.exceptions.addAll(typeFormatter.extractRefFqns(firstChildOfType(methodNode, JavaSyntaxElementType.THROWS_LIST), typeVars));
    return m;
  }

  private void collectMethodTypeParameters(@NotNull SyntaxNode methodNode,
                                           @NotNull MethodSymbol.Builder m,
                                           @NotNull Set<String> typeVars) {
    SyntaxNode tparams = firstChildOfType(methodNode, JavaSyntaxElementType.TYPE_PARAMETER_LIST);
    if (tparams == null) return;
    for (SyntaxNode tp = tparams.firstChild(); tp != null; tp = tp.nextSibling()) {
      if (tp.getType() != JavaSyntaxElementType.TYPE_PARAMETER) continue;
      SyntaxNode tpId = firstChildOfType(tp, JavaSyntaxTokenType.IDENTIFIER);
      if (tpId == null) continue;
      String tvName = tpId.getText().toString();
      typeVars.add(tvName);
      TypeParameterSymbol.Builder info = new TypeParameterSymbol.Builder(tvName);
      // Type-use annotations on the type-parameter itself (`<@Nls T>`) live inside the TYPE_PARAMETER
      // node — either as direct ANNOTATION children or wrapped in a MODIFIER_LIST.
      info.annotations.addAll(typeFormatter.collectTypeUseAnnotations(tp, typeVars));
      info.extendsList.addAll(typeFormatter.parseRefs(firstChildOfType(tp, JavaSyntaxElementType.EXTENDS_BOUND_LIST), typeVars));
      m.generics.add(info);
    }
  }

  private void collectParameters(@NotNull SyntaxNode methodNode,
                                 @NotNull MethodSymbol.Builder m,
                                 @NotNull Set<String> typeVars) {
    SyntaxNode paramList = firstChildOfType(methodNode, JavaSyntaxElementType.PARAMETER_LIST);
    if (paramList == null) return;
    int paramIdx = 0;
    for (SyntaxNode p = paramList.firstChild(); p != null; p = p.nextSibling()) {
      if (p.getType() != JavaSyntaxElementType.PARAMETER) continue;
      SyntaxNode pType = firstChildOfType(p, JavaSyntaxElementType.TYPE);
      SyntaxNode pName = firstChildOfType(p, JavaSyntaxTokenType.IDENTIFIER);
      ParameterSymbol.Builder param = new ParameterSymbol.Builder();
      JvmTypeRef parsedType;
      if (pType == null) {
        // Defensive fallback: parser produced a PARAMETER with no TYPE child. Real-world java-syntax
        // output shouldn't reach here, but log loudly if it does — the empty-FQN that goes into the
        // ParameterSymbol will silently make this parameter unmatchable downstream.
        LOG.warn("Parameter without TYPE child; producing empty-FQN placeholder. Source range: "
                 + p.getStartOffset() + ".." + p.getEndOffset());
        parsedType = new JvmTypeRef.UserType(Fqn.of(""), List.of(), List.of());
      }
      else {
        parsedType = typeFormatter.parseType(pType, typeVars);
      }
      List<Fqn> paramAnnotations = typeFormatter.extractAnnotationFqns(firstChildOfType(p, JavaSyntaxElementType.MODIFIER_LIST), typeVars);
      TypeUseAnnotationLifter.LiftResult lifted = TypeUseAnnotationLifter.lift(paramAnnotations, parsedType, typeFormatter::isTypeUseAnnotation);
      param.type = lifted.type();
      param.annotations.addAll(lifted.declarationAnnotations());
      param.name = pName == null ? "p" + paramIdx : pName.getText().toString();
      m.parameters.add(param);
      paramIdx++;
    }
  }
}
