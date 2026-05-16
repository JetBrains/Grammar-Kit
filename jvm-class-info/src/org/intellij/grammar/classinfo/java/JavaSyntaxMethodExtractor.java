/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

import com.intellij.java.syntax.element.JavaSyntaxElementType;
import com.intellij.java.syntax.element.JavaSyntaxTokenType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.ParameterSymbol;
import org.intellij.grammar.classinfo.TypeParameterSymbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.HashSet;
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
    m.annotations.addAll(typeFormatter.extractAnnotationFqns(modifierList, typeVars));

    SyntaxNode nameId = firstNameIdentifier(methodNode);
    if (nameId == null) return null;
    m.name = nameId.getText().toString();

    SyntaxNode returnType = firstChildOfType(methodNode, JavaSyntaxElementType.TYPE);
    if (returnType == null) {
      m.methodType = MethodType.CONSTRUCTOR;
      m.returnType = declaringFqn.value();
    }
    else {
      m.methodType = Modifier.isStatic(m.modifiers) ? MethodType.STATIC : MethodType.INSTANCE;
      m.returnType = typeFormatter.formatType(returnType, typeVars);
    }

    collectParameters(methodNode, m, typeVars);

    m.annotatedReturnType = m.returnType;
    for (ParameterSymbol.Builder p : m.parameters) p.annotatedType = p.type;
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
      info.extendsList.addAll(typeFormatter.formatRefs(firstChildOfType(tp, JavaSyntaxElementType.EXTENDS_BOUND_LIST), typeVars));
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
      param.type = pType == null ? "" : typeFormatter.formatType(pType, typeVars);
      param.name = pName == null ? "p" + paramIdx : pName.getText().toString();
      param.annotations.addAll(typeFormatter.extractAnnotationFqns(firstChildOfType(p, JavaSyntaxElementType.MODIFIER_LIST), typeVars));
      m.parameters.add(param);
      paramIdx++;
    }
  }
}
