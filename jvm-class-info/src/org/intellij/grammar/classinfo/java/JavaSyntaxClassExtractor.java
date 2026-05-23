/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

import com.intellij.java.syntax.element.JavaSyntaxElementType;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.intellij.grammar.classinfo.SyntaxTreeUtil.firstChildOfType;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.extractModifiers;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.findClassName;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.isAnnotationType;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.isInterface;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.typeParameterNames;

/**
 * Walks a parsed Java source file (the root {@link SyntaxNode} from
 * {@link JavaSyntaxTreeManager#parseText(String)}) and produces one {@link ClassSymbol} record per class it
 * declares (top-level + nested), keyed by FQN.
 * <p>
 * One instance per file. Stateless across calls; the {@link JavaClassManager} holds the cache.
 *
 * <h2>Decomposition</h2>
 * This class is the orchestrator. The actual work is split across:
 * <ul>
 *   <li>{@link JavaSyntaxImportContext} — file-level package + imports + name resolution.</li>
 *   <li>{@link JavaSyntaxTypeFormatter} — renders {@code TYPE}/{@code JAVA_CODE_REFERENCE} nodes
 *       and annotations into dotted-FQN strings.</li>
 *   <li>{@link JavaSyntaxMethodExtractor} — builds {@link MethodSymbol} records for method nodes.</li>
 *   <li>{@link JavaSyntaxNodes} — static tree-navigation helpers.</li>
 * </ul>
 *
 * <h2>FQN resolution</h2>
 * Reference resolution is best-effort:
 * <ul>
 *   <li>Single-identifier references resolve through explicit imports → a fixed
 *       {@code java.lang} allow-list → same-package fallback.</li>
 *   <li>Qualified references ({@code Outer.Inner}, {@code com.foo.Bar}) and wildcard imports are
 *       left untouched — without a classpath model we cannot tell them apart safely.</li>
 *   <li>In-scope class- and method-level type variables are recognised and never qualified.</li>
 *   <li>Type-use annotations on parameter / return / generic types are ignored.</li>
 * </ul>
 */
@SuppressWarnings("UnstableApiUsage")
public final class JavaSyntaxClassExtractor {

  private final SyntaxNode fileRoot;
  private final JavaSyntaxImportContext imports;
  private final JavaSyntaxTypeFormatter typeFormatter;
  private final JavaSyntaxMethodExtractor methodExtractor;
  private final Map<Fqn, ClassSymbol.Builder> result = new LinkedHashMap<>();

  /** Walks {@code fileRoot} once and returns one {@link ClassSymbol} per declared class, keyed by FQN. */
  public static @NotNull Map<Fqn, ClassSymbol> extractFrom(@NotNull SyntaxNode fileRoot, @NotNull SymbolResolver resolver) {
    Map<Fqn, ClassSymbol.Builder> builders = new JavaSyntaxClassExtractor(fileRoot, resolver).extract();
    Map<Fqn, ClassSymbol> built = new LinkedHashMap<>(builders.size());
    for (Map.Entry<Fqn, ClassSymbol.Builder> e : builders.entrySet()) built.put(e.getKey(), e.getValue().build());
    return built;
  }

  private JavaSyntaxClassExtractor(@NotNull SyntaxNode fileRoot, @NotNull SymbolResolver resolver) {
    this.fileRoot = fileRoot;
    this.imports = JavaSyntaxImportContext.extractFrom(fileRoot, resolver);
    this.typeFormatter = new JavaSyntaxTypeFormatter(imports, resolver);
    this.methodExtractor = new JavaSyntaxMethodExtractor(typeFormatter);
  }

  private @NotNull Map<Fqn, ClassSymbol.Builder> extract() {
    for (SyntaxNode child = fileRoot.firstChild(); child != null; child = child.nextSibling()) {
      if (child.getType() == JavaSyntaxElementType.CLASS) {
        walkClass(child, Fqn.ROOT, Set.of());
      }
    }
    return result;
  }

  private void walkClass(@NotNull SyntaxNode classNode,
                         @NotNull Fqn enclosingFqn,
                         @NotNull Set<String> outerTypeVars) {
    SyntaxNode nameId = findClassName(classNode);
    if (nameId == null) return;
    String simpleName = nameId.getText().toString();
    Fqn fqn = imports.qualify(enclosingFqn, simpleName);

    ClassSymbol.Builder info = new ClassSymbol.Builder();
    info.name = fqn;

    Set<String> classTypeVars = new HashSet<>(outerTypeVars);
    SyntaxNode tparams = firstChildOfType(classNode, JavaSyntaxElementType.TYPE_PARAMETER_LIST);
    for (String name : typeParameterNames(tparams)) {
      info.typeParameters.add(name);
      classTypeVars.add(name);
    }

    SyntaxNode modifierList = firstChildOfType(classNode, JavaSyntaxElementType.MODIFIER_LIST);
    info.modifiers = extractModifiers(modifierList);
    info.annotations.addAll(typeFormatter.extractAnnotationFqns(modifierList, classTypeVars));
    if (isInterface(classNode)) info.modifiers |= Modifier.INTERFACE | Modifier.ABSTRACT;
    if (isAnnotationType(classNode)) {
      info.modifiers |= Modifier.INTERFACE | Modifier.ABSTRACT;
      info.annotationTargets.addAll(typeFormatter.parseAnnotationTargetSet(modifierList, classTypeVars));
    }

    List<Fqn> extendsRefs = typeFormatter.extractRefFqns(firstChildOfType(classNode, JavaSyntaxElementType.EXTENDS_LIST),
                                                         classTypeVars);
    if (isInterface(classNode)) {
      info.superClass = null;
      info.interfaces.addAll(extendsRefs); // interfaces' EXTENDS_LIST holds super-interfaces
    }
    else {
      info.superClass = extendsRefs.isEmpty() ? Fqn.JAVA_LANG_OBJECT : extendsRefs.get(0);
    }
    info.interfaces.addAll(typeFormatter.extractRefFqns(firstChildOfType(classNode, JavaSyntaxElementType.IMPLEMENTS_LIST),
                                                        classTypeVars));

    result.put(fqn, info);

    Map<String, Fqn> previousScope = typeFormatter.getNestedScope();
    List<Fqn> previousChain = typeFormatter.getEnclosingChain();
    Map<String, Fqn> scope = new HashMap<>(previousScope);
    for (SyntaxNode member = classNode.firstChild(); member != null; member = member.nextSibling()) {
      if (member.getType() != JavaSyntaxElementType.CLASS) continue;
      SyntaxNode memberName = findClassName(member);
      if (memberName == null) continue;
      String simple = memberName.getText().toString();
      scope.put(simple, fqn.child(simple));
    }
    typeFormatter.setNestedScope(scope);
    // Innermost-first chain: current class then any outer enclosing classes. Lets the formatter
    // walk each enclosing class's supertypes when resolving an unqualified simple name to an
    // inherited nested type (JLS 6.4.1).
    List<Fqn> chain = new ArrayList<>(previousChain.size() + 1);
    chain.add(fqn);
    chain.addAll(previousChain);
    typeFormatter.setEnclosingChain(chain);
    try {
      for (SyntaxNode member = classNode.firstChild(); member != null; member = member.nextSibling()) {
        SyntaxElementType t = member.getType();
        if (t == JavaSyntaxElementType.METHOD || t == JavaSyntaxElementType.ANNOTATION_METHOD) {
          MethodSymbol.Builder m = methodExtractor.extract(member, fqn, classTypeVars);
          if (m != null) info.methods.add(m);
        }
        else if (t == JavaSyntaxElementType.CLASS) {
          walkClass(member, fqn, classTypeVars);
        }
      }
    }
    finally {
      typeFormatter.setNestedScope(previousScope);
      typeFormatter.setEnclosingChain(previousChain);
    }
  }
}
