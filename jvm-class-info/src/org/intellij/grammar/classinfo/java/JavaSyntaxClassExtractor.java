/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

import com.intellij.java.syntax.element.JavaSyntaxElementType;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.MethodInfo;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.extractModifiers;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.findClassName;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.firstChildOfType;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.isAnnotationType;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.isInterface;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.typeParameterNames;

/**
 * Walks a parsed Java source file (the root {@link SyntaxNode} from
 * {@link JavaSyntaxTreeManager#parseFile}) and produces one {@link ClassInfo} record per class it
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
 *   <li>{@link JavaSyntaxMethodExtractor} — builds {@link MethodInfo} records for method nodes.</li>
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
  private final Map<String, ClassInfo> result = new LinkedHashMap<>();

  /** Walks {@code fileRoot} once and returns one {@link ClassInfo} per declared class, keyed by FQN. */
  public static @NotNull Map<String, ClassInfo> extractFrom(@NotNull SyntaxNode fileRoot) {
    return new JavaSyntaxClassExtractor(fileRoot).extract();
  }

  private JavaSyntaxClassExtractor(@NotNull SyntaxNode fileRoot) {
    this.fileRoot = fileRoot;
    this.imports = JavaSyntaxImportContext.extractFrom(fileRoot);
    this.typeFormatter = new JavaSyntaxTypeFormatter(imports);
    this.methodExtractor = new JavaSyntaxMethodExtractor(typeFormatter);
  }

  private @NotNull Map<String, ClassInfo> extract() {
    for (SyntaxNode child = fileRoot.firstChild(); child != null; child = child.nextSibling()) {
      if (child.getType() == JavaSyntaxElementType.CLASS) {
        walkClass(child, "", Set.of());
      }
    }
    return result;
  }

  private void walkClass(@NotNull SyntaxNode classNode,
                         @NotNull String enclosingFqn,
                         @NotNull Set<String> outerTypeVars) {
    SyntaxNode nameId = findClassName(classNode);
    if (nameId == null) return;
    String simpleName = nameId.getText().toString();
    String fqn = computeFqn(enclosingFqn, simpleName);

    ClassInfo info = new ClassInfo();
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
    if (isAnnotationType(classNode)) info.modifiers |= Modifier.INTERFACE | Modifier.ABSTRACT;

    List<String> extendsRefs = typeFormatter.formatRefs(firstChildOfType(classNode, JavaSyntaxElementType.EXTENDS_LIST),
                                                        classTypeVars);
    if (isInterface(classNode)) {
      info.superClass = null;
      info.interfaces.addAll(extendsRefs); // interfaces' EXTENDS_LIST holds super-interfaces
    }
    else {
      info.superClass = extendsRefs.isEmpty() ? "java.lang.Object" : extendsRefs.get(0);
    }
    info.interfaces.addAll(typeFormatter.formatRefs(firstChildOfType(classNode, JavaSyntaxElementType.IMPLEMENTS_LIST),
                                                    classTypeVars));

    result.put(fqn, info);

    for (SyntaxNode member = classNode.firstChild(); member != null; member = member.nextSibling()) {
      SyntaxElementType t = member.getType();
      if (t == JavaSyntaxElementType.METHOD || t == JavaSyntaxElementType.ANNOTATION_METHOD) {
        MethodInfo m = methodExtractor.extract(member, fqn, classTypeVars);
        if (m != null) info.methods.add(m);
      }
      else if (t == JavaSyntaxElementType.CLASS) {
        walkClass(member, fqn, classTypeVars);
      }
    }
  }

  private @NotNull String computeFqn(@NotNull String enclosingFqn, @NotNull String simpleName) {
    if (!enclosingFqn.isEmpty()) return enclosingFqn + "." + simpleName;
    String pkg = imports.packageName();
    return pkg.isEmpty() ? simpleName : pkg + "." + simpleName;
  }
}
