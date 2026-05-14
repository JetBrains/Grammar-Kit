/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.kotlin;

import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import fleet.org.jetbrains.kotlin.kmp.lexer.KtTokens;
import fleet.org.jetbrains.kotlin.kmp.parser.KtNodeTypes;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.MethodInfo;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.intellij.grammar.classinfo.java.JavaSyntaxClassExtractor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.extractModifiers;
import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.firstChildOfType;
import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.hasJvmStatic;
import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.hasModifier;
import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.isCompanion;
import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.isConstProperty;
import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.isInnerNested;
import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.isInterface;
import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.isVarProperty;
import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.nameIdentifier;
import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.typeParameterNames;

/**
 * Walks a parsed Kotlin source file (the root {@link SyntaxNode} from
 * {@link KotlinSyntaxTreeManager#parseFile}) and produces one {@link ClassInfo} per declared class,
 * object, companion, or typealias, plus a synthesised {@code <FileStem>Kt} class collecting all
 * top-level callables.
 * <p>
 * Mirrors {@link JavaSyntaxClassExtractor}'s decomposition:
 * imports / type formatting / method extraction are pushed into dedicated helpers and this class
 * is just the orchestrator.
 */
@SuppressWarnings("UnstableApiUsage")
final class KotlinSyntaxClassExtractor {

  private final SyntaxNode fileRoot;
  private final String fileStem;
  private final KotlinSyntaxImportContext imports;
  private final KotlinSyntaxTypeFormatter typeFormatter;
  private final KotlinSyntaxMethodExtractor methodExtractor;
  private final Map<String, ClassInfo> result = new LinkedHashMap<>();

  /** Walks {@code fileRoot} once and returns one {@link ClassInfo} per declared (or synthesised) class. */
  static @NotNull Map<String, ClassInfo> extractFrom(@NotNull SyntaxNode fileRoot,
                                                     @NotNull String fileStem,
                                                     @NotNull SymbolResolver resolver) {
    return new KotlinSyntaxClassExtractor(fileRoot, fileStem, resolver).extract();
  }

  private KotlinSyntaxClassExtractor(@NotNull SyntaxNode fileRoot,
                                     @NotNull String fileStem,
                                     @NotNull SymbolResolver resolver) {
    this.fileRoot = fileRoot;
    this.fileStem = fileStem;
    this.imports = KotlinSyntaxImportContext.extractFrom(fileRoot, resolver);
    this.typeFormatter = new KotlinSyntaxTypeFormatter(imports);
    this.methodExtractor = new KotlinSyntaxMethodExtractor(typeFormatter);
  }

  private @NotNull Map<String, ClassInfo> extract() {
    ClassInfo fileClass = null;

    for (SyntaxNode child = fileRoot.firstChild(); child != null; child = child.nextSibling()) {
      SyntaxElementType t = child.getType();
      if (t == KtNodeTypes.INSTANCE.getCLASS()) {
        walkClass(child, "", Set.of());
      }
      else if (t == KtNodeTypes.INSTANCE.getOBJECT_DECLARATION()) {
        walkObject(child, "", null, Set.of());
      }
      else if (t == KtNodeTypes.INSTANCE.getTYPEALIAS()) {
        walkTypeAlias(child, "");
      }
      else if (t == KtNodeTypes.INSTANCE.getFUN() || t == KtNodeTypes.INSTANCE.getPROPERTY()) {
        if (fileClass == null) fileClass = createFileClass();
        appendFileClassMember(fileClass, child);
      }
    }

    if (fileClass != null) result.put(fileClass.name, fileClass);
    return result;
  }

  private @NotNull ClassInfo createFileClass() {
    ClassInfo info = new ClassInfo();
    String pkg = imports.packageName();
    String simple = fileStem + "Kt";
    info.name = pkg.isEmpty() ? simple : pkg + "." + simple;
    info.modifiers = Modifier.PUBLIC | Modifier.FINAL;
    info.superClass = "java.lang.Object";
    return info;
  }

  private void appendFileClassMember(@NotNull ClassInfo fileClass, @NotNull SyntaxNode member) {
    SyntaxNode modifierList = firstChildOfType(member, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    if (hasModifier(modifierList, KtTokens.INSTANCE.getPRIVATE_MODIFIER())) return;
    if (member.getType() == KtNodeTypes.INSTANCE.getFUN()) {
      MethodInfo m = methodExtractor.extractFunction(member, fileClass.name, Set.of(), MethodType.STATIC);
      if (m != null) {
        m.modifiers |= Modifier.STATIC;
        m.methodType = MethodType.STATIC;
        fileClass.methods.add(m);
      }
    }
    else if (member.getType() == KtNodeTypes.INSTANCE.getPROPERTY()) {
      if (isConstProperty(member)) return;
      MethodInfo getter = methodExtractor.synthesizeGetter(member, fileClass.name, Set.of(), true);
      if (getter != null) fileClass.methods.add(getter);
      if (isVarProperty(member)) {
        MethodInfo setter = methodExtractor.synthesizeSetter(member, fileClass.name, Set.of(), true);
        if (setter != null) fileClass.methods.add(setter);
      }
    }
  }

  private void walkClass(@NotNull SyntaxNode classNode,
                         @NotNull String enclosingFqn,
                         @NotNull Set<String> outerTypeVars) {
    SyntaxNode nameId = nameIdentifier(classNode);
    if (nameId == null) return;
    String simple = nameId.getText().toString();
    String fqn = computeFqn(enclosingFqn, simple);

    ClassInfo info = new ClassInfo();
    info.name = fqn;

    Set<String> classTypeVars = new HashSet<>(outerTypeVars);
    SyntaxNode tparams = firstChildOfType(classNode, KtNodeTypes.INSTANCE.getTYPE_PARAMETER_LIST());
    for (String name : typeParameterNames(tparams)) {
      info.typeParameters.add(name);
      classTypeVars.add(name);
    }

    SyntaxNode modifierList = firstChildOfType(classNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    int baseMods = extractModifiers(modifierList);
    info.modifiers = applyDefaultFinality(baseMods, modifierList, /*isInterface*/ isInterface(classNode));
    info.annotations.addAll(typeFormatter.extractAnnotationFqns(modifierList, classTypeVars));

    if (isInterface(classNode)) {
      info.modifiers |= Modifier.INTERFACE | Modifier.ABSTRACT;
      info.modifiers &= ~Modifier.FINAL;
    }
    if (KotlinSyntaxNodes.isAnnotationClass(classNode)) {
      info.modifiers |= Modifier.INTERFACE | Modifier.ABSTRACT | KotlinSyntaxNodes.ANNOTATION_MODIFIER_BIT;
      info.modifiers &= ~Modifier.FINAL;
    }
    if (!enclosingFqn.isEmpty() && !isInnerNested(classNode)) {
      info.modifiers |= Modifier.STATIC;
    }

    SyntaxNode superTypeList = firstChildOfType(classNode, KtNodeTypes.INSTANCE.getSUPER_TYPE_LIST());
    populateSuperTypes(info, classNode, superTypeList, classTypeVars);

    result.put(fqn, info);

    // Primary constructor + its val/var properties.
    SyntaxNode primaryCtor = firstChildOfType(classNode, KtNodeTypes.INSTANCE.getPRIMARY_CONSTRUCTOR());
    if (primaryCtor != null) {
      MethodInfo ctorMethod = methodExtractor.extractConstructor(primaryCtor, fqn, classTypeVars);
      if (ctorMethod != null) info.methods.add(ctorMethod);
      collectPrimaryCtorPropertyAccessors(primaryCtor, info, classTypeVars);
    }

    SyntaxNode body = firstChildOfType(classNode, KtNodeTypes.INSTANCE.getCLASS_BODY());
    if (body != null) walkClassBody(body, info, classTypeVars);
  }

  private void walkObject(@NotNull SyntaxNode objectNode,
                          @NotNull String enclosingFqn,
                          @Nullable ClassInfo enclosingClass,
                          @NotNull Set<String> outerTypeVars) {
    SyntaxNode nameId = nameIdentifier(objectNode);
    boolean companion = isCompanion(objectNode);
    String simple = nameId != null ? nameId.getText().toString() : (companion ? "Companion" : null);
    if (simple == null) return;
    String fqn = computeFqn(enclosingFqn, simple);

    ClassInfo info = new ClassInfo();
    info.name = fqn;
    SyntaxNode modifierList = firstChildOfType(objectNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    info.modifiers = extractModifiers(modifierList) | Modifier.FINAL;
    if (!enclosingFqn.isEmpty()) info.modifiers |= Modifier.STATIC;
    info.annotations.addAll(typeFormatter.extractAnnotationFqns(modifierList, outerTypeVars));
    info.superClass = "java.lang.Object";

    SyntaxNode superTypeList = firstChildOfType(objectNode, KtNodeTypes.INSTANCE.getSUPER_TYPE_LIST());
    populateSuperTypes(info, objectNode, superTypeList, outerTypeVars);

    result.put(fqn, info);

    SyntaxNode body = firstChildOfType(objectNode, KtNodeTypes.INSTANCE.getCLASS_BODY());
    if (body != null) walkClassBody(body, info, outerTypeVars);

    // Lift @JvmStatic-annotated members onto the enclosing class.
    if (companion && enclosingClass != null) {
      for (MethodInfo m : info.methods) {
        SyntaxNode origin = null; // We don't track origin nodes; presence-of-@JvmStatic check happens during body walk.
        if (m.modifiers != 0 && Modifier.isStatic(m.modifiers)) {
          // Already a static member by virtue of file-level synthesis (not the case for companion bodies);
          // skip to avoid duplicate insertion.
          continue;
        }
        // Tagged-and-copied path: marker carried via name-prefix collision is awkward; do the lift in walkClassBody instead.
        // (Intentionally a no-op here; see walkClassBody.)
        if (origin != null) {
          MethodInfo lifted = copyAsStatic(m, enclosingClass.name);
          enclosingClass.methods.add(lifted);
        }
      }
    }
  }

  private void walkClassBody(@NotNull SyntaxNode body,
                             @NotNull ClassInfo enclosing,
                             @NotNull Set<String> typeVars) {
    for (SyntaxNode member = body.firstChild(); member != null; member = member.nextSibling()) {
      SyntaxElementType t = member.getType();
      if (t == KtNodeTypes.INSTANCE.getFUN()) {
        MethodInfo m = methodExtractor.extractFunction(member, enclosing.name, typeVars, MethodType.INSTANCE);
        if (m == null) continue;
        // Static methods (extension fns on member objects) keep their type as set by the extractor.
        enclosing.methods.add(m);
        // Companion-object members annotated @JvmStatic surface as static methods on the *outer* class.
        // Handled below in walkObject for the lift; we tag the original by inspecting modifierList here.
      }
      else if (t == KtNodeTypes.INSTANCE.getPROPERTY()) {
        if (isConstProperty(member)) continue;
        MethodInfo getter = methodExtractor.synthesizeGetter(member, enclosing.name, typeVars, false);
        if (getter != null) enclosing.methods.add(getter);
        if (isVarProperty(member)) {
          MethodInfo setter = methodExtractor.synthesizeSetter(member, enclosing.name, typeVars, false);
          if (setter != null) enclosing.methods.add(setter);
        }
      }
      else if (t == KtNodeTypes.INSTANCE.getSECONDARY_CONSTRUCTOR()) {
        MethodInfo ctorMethod = methodExtractor.extractConstructor(member, enclosing.name, typeVars);
        if (ctorMethod != null) enclosing.methods.add(ctorMethod);
      }
      else if (t == KtNodeTypes.INSTANCE.getOBJECT_DECLARATION()) {
        // Companion or named nested object.
        walkObjectInsideClass(member, enclosing, typeVars);
      }
      else if (t == KtNodeTypes.INSTANCE.getCLASS()) {
        walkClass(member, enclosing.name, typeVars);
      }
    }
  }

  private void walkObjectInsideClass(@NotNull SyntaxNode objectNode,
                                     @NotNull ClassInfo enclosing,
                                     @NotNull Set<String> outerTypeVars) {
    SyntaxNode nameId = nameIdentifier(objectNode);
    boolean companion = isCompanion(objectNode);
    String simple = nameId != null ? nameId.getText().toString() : (companion ? "Companion" : null);
    if (simple == null) return;
    String fqn = enclosing.name + "." + simple;

    ClassInfo info = new ClassInfo();
    info.name = fqn;
    SyntaxNode modifierList = firstChildOfType(objectNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    info.modifiers = extractModifiers(modifierList) | Modifier.FINAL | Modifier.STATIC;
    info.annotations.addAll(typeFormatter.extractAnnotationFqns(modifierList, outerTypeVars));
    info.superClass = "java.lang.Object";

    SyntaxNode superTypeList = firstChildOfType(objectNode, KtNodeTypes.INSTANCE.getSUPER_TYPE_LIST());
    populateSuperTypes(info, objectNode, superTypeList, outerTypeVars);

    result.put(fqn, info);

    SyntaxNode body = firstChildOfType(objectNode, KtNodeTypes.INSTANCE.getCLASS_BODY());
    if (body == null) return;
    for (SyntaxNode member = body.firstChild(); member != null; member = member.nextSibling()) {
      SyntaxElementType t = member.getType();
      if (t == KtNodeTypes.INSTANCE.getFUN()) {
        MethodInfo m = methodExtractor.extractFunction(member, fqn, outerTypeVars, MethodType.INSTANCE);
        if (m == null) continue;
        info.methods.add(m);
        if (companion && hasJvmStatic(firstChildOfType(member, KtNodeTypes.INSTANCE.getMODIFIER_LIST()))) {
          enclosing.methods.add(copyAsStatic(m, enclosing.name));
        }
      }
      else if (t == KtNodeTypes.INSTANCE.getPROPERTY()) {
        if (isConstProperty(member)) continue;
        MethodInfo getter = methodExtractor.synthesizeGetter(member, fqn, outerTypeVars, false);
        if (getter != null) info.methods.add(getter);
        if (isVarProperty(member)) {
          MethodInfo setter = methodExtractor.synthesizeSetter(member, fqn, outerTypeVars, false);
          if (setter != null) info.methods.add(setter);
        }
        if (companion && hasJvmStatic(firstChildOfType(member, KtNodeTypes.INSTANCE.getMODIFIER_LIST()))) {
          if (getter != null) enclosing.methods.add(copyAsStatic(getter, enclosing.name));
          if (isVarProperty(member)) {
            MethodInfo setter = methodExtractor.synthesizeSetter(member, enclosing.name, outerTypeVars, true);
            if (setter != null) enclosing.methods.add(setter);
          }
        }
      }
      else if (t == KtNodeTypes.INSTANCE.getCLASS()) {
        walkClass(member, fqn, outerTypeVars);
      }
    }
  }

  private void walkTypeAlias(@NotNull SyntaxNode aliasNode, @NotNull String enclosingFqn) {
    SyntaxNode nameId = nameIdentifier(aliasNode);
    if (nameId == null) return;
    String simple = nameId.getText().toString();
    String fqn = computeFqn(enclosingFqn, simple);

    ClassInfo info = new ClassInfo();
    info.name = fqn;
    SyntaxNode modifierList = firstChildOfType(aliasNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    info.modifiers = extractModifiers(modifierList) | Modifier.FINAL;
    info.annotations.addAll(typeFormatter.extractAnnotationFqns(modifierList, Set.of()));
    SyntaxNode rhs = firstChildOfType(aliasNode, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
    info.superClass = rhs == null ? "java.lang.Object" : typeFormatter.formatType(rhs, Set.of());
    result.put(fqn, info);
  }

  private void populateSuperTypes(@NotNull ClassInfo info,
                                  @NotNull SyntaxNode classOrObjectNode,
                                  @Nullable SyntaxNode superTypeList,
                                  @NotNull Set<String> typeVars) {
    List<String> supers = typeFormatter.formatSuperTypes(superTypeList, typeVars);
    if (isInterface(classOrObjectNode) || KotlinSyntaxNodes.isAnnotationClass(classOrObjectNode)) {
      info.superClass = null;
      info.interfaces.addAll(supers);
      return;
    }
    if (supers.isEmpty()) {
      info.superClass = info.superClass == null ? "java.lang.Object" : info.superClass;
      return;
    }
    if (KotlinSyntaxTypeFormatter.hasCallEntry(superTypeList)) {
      // The first call entry is the superclass; rest are interfaces.
      info.superClass = supers.get(0);
      if (supers.size() > 1) info.interfaces.addAll(supers.subList(1, supers.size()));
    }
    else {
      // No constructor call → best-effort: first SUPER_TYPE_ENTRY is the superclass; rest interfaces.
      info.superClass = supers.get(0);
      if (supers.size() > 1) info.interfaces.addAll(supers.subList(1, supers.size()));
    }
  }

  private void collectPrimaryCtorPropertyAccessors(@NotNull SyntaxNode primaryCtor,
                                                   @NotNull ClassInfo enclosing,
                                                   @NotNull Set<String> typeVars) {
    SyntaxNode paramList = firstChildOfType(primaryCtor, KtNodeTypes.INSTANCE.getVALUE_PARAMETER_LIST());
    if (paramList == null) return;
    for (SyntaxNode p = paramList.firstChild(); p != null; p = p.nextSibling()) {
      if (p.getType() != KtNodeTypes.INSTANCE.getVALUE_PARAMETER()) continue;
      boolean isVal = firstChildOfType(p, KtTokens.INSTANCE.getVAL_KEYWORD()) != null;
      boolean isVar = firstChildOfType(p, KtTokens.INSTANCE.getVAR_KEYWORD()) != null;
      if (!isVal && !isVar) continue;
      MethodInfo getter = methodExtractor.synthesizeGetter(p, enclosing.name, typeVars, false);
      if (getter != null) enclosing.methods.add(getter);
      if (isVar) {
        MethodInfo setter = methodExtractor.synthesizeSetter(p, enclosing.name, typeVars, false);
        if (setter != null) enclosing.methods.add(setter);
      }
    }
  }

  private static int applyDefaultFinality(int modifiers, @Nullable SyntaxNode modifierList, boolean isInterface) {
    if (isInterface) return modifiers;
    if (Modifier.isAbstract(modifiers)) return modifiers;
    if (hasModifier(modifierList, KtTokens.INSTANCE.getOPEN_MODIFIER())) return modifiers;
    return modifiers | Modifier.FINAL;
  }

  private static @NotNull MethodInfo copyAsStatic(@NotNull MethodInfo src, @NotNull String newDeclaring) {
    MethodInfo m = new MethodInfo();
    m.name = src.name;
    m.declaringClass = newDeclaring;
    m.modifiers = src.modifiers | Modifier.STATIC;
    m.methodType = MethodType.STATIC;
    m.types.addAll(src.types);
    m.annotatedTypes.addAll(src.annotatedTypes);
    m.exceptions.addAll(src.exceptions);
    m.generics.addAll(src.generics);
    // Annotations are FactoryMap-backed; copy over what's present.
    for (Map.Entry<Integer, List<String>> e : src.annotations.entrySet()) {
      m.annotations.get(e.getKey()).addAll(e.getValue());
    }
    return m;
  }

  private @NotNull String computeFqn(@NotNull String enclosingFqn, @NotNull String simpleName) {
    if (!enclosingFqn.isEmpty()) return enclosingFqn + "." + simpleName;
    String pkg = imports.packageName();
    return pkg.isEmpty() ? simpleName : pkg + "." + simpleName;
  }
}
