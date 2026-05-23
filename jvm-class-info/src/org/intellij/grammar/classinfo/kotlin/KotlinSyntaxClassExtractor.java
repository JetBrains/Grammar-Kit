/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.kotlin;

import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import fleet.org.jetbrains.kotlin.kmp.lexer.KtTokens;
import fleet.org.jetbrains.kotlin.kmp.parser.KtNodeTypes;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmTypeRefs;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.ParameterSymbol;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.intellij.grammar.classinfo.TargetType;
import org.intellij.grammar.classinfo.java.JavaSyntaxClassExtractor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.intellij.grammar.classinfo.SyntaxTreeUtil.childrenOfType;
import static org.intellij.grammar.classinfo.SyntaxTreeUtil.firstChildOfType;
import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.extractModifiers;
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
 * {@link KotlinSyntaxTreeManager#parseText(String)}) and produces one {@link ClassSymbol.Builder} per
 * declared class, object, companion, or typealias, plus a synthesised {@code <FileStem>Kt} class
 * collecting all top-level callables. Returns builders so that the provider can merge sibling
 * multifile-facade files before sealing each class into an immutable record.
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
  private final Map<Fqn, ClassSymbol.Builder> result = new LinkedHashMap<>();

  private record FileJvmAnnotations(@Nullable String jvmName, boolean multifileFacade) { }

  /** Walks {@code fileRoot} once and returns one {@link ClassSymbol.Builder} per declared (or synthesised) class. */
  static @NotNull Map<Fqn, ClassSymbol.Builder> extractFrom(@NotNull SyntaxNode fileRoot,
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

  private @NotNull Map<Fqn, ClassSymbol.Builder> extract() {
    FileJvmAnnotations fileAnnotations = readFileJvmAnnotations();
    ClassSymbol.Builder fileClass = null;

    for (SyntaxNode child = fileRoot.firstChild(); child != null; child = child.nextSibling()) {
      SyntaxElementType t = child.getType();
      if (t == KtNodeTypes.INSTANCE.getCLASS()) {
        walkClass(child, Fqn.ROOT, Set.of());
      }
      else if (t == KtNodeTypes.INSTANCE.getOBJECT_DECLARATION()) {
        walkObject(child, Fqn.ROOT, null, Set.of());
      }
      else if (t == KtNodeTypes.INSTANCE.getTYPEALIAS()) {
        walkTypeAlias(child, Fqn.ROOT);
      }
      else if (t == KtNodeTypes.INSTANCE.getFUN() || t == KtNodeTypes.INSTANCE.getPROPERTY()) {
        if (fileClass == null) fileClass = createFileClass(fileAnnotations);
        appendFileClassMember(fileClass, child);
      }
    }

    if (fileClass != null) result.put(fileClass.name, fileClass);
    return result;
  }

  private @NotNull ClassSymbol.Builder createFileClass(@NotNull FileJvmAnnotations fileAnnotations) {
    ClassSymbol.Builder info = new ClassSymbol.Builder();
    String simpleName = fileAnnotations.jvmName() != null ? fileAnnotations.jvmName() : fileStem + "Kt";
    info.name = Fqn.of(imports.packageName()).child(simpleName);
    info.modifiers = Modifier.PUBLIC | Modifier.FINAL;
    info.superClass = Fqn.JAVA_LANG_OBJECT;
    info.multifileFacade = fileAnnotations.multifileFacade();
    return info;
  }

  private @NotNull FileJvmAnnotations readFileJvmAnnotations() {
    SyntaxNode list = firstChildOfType(fileRoot, KtNodeTypes.INSTANCE.getFILE_ANNOTATION_LIST());
    if (list == null) return new FileJvmAnnotations(null, false);
    String jvmName = null;
    boolean multifile = false;
    // {@code kotlin.jvm.*} is auto-imported, so the simple-name match here is the established pattern
    // (see {@link KotlinSyntaxNodes#hasJvmStatic}). Going through {@code resolveSimpleName} would
    // misresolve {@code JvmName} to {@code <package>.JvmName} via the same-package fallback.
    for (SyntaxNode entry = list.firstChild(); entry != null; entry = entry.nextSibling()) {
      if (entry.getType() != KtNodeTypes.INSTANCE.getANNOTATION_ENTRY()) continue;
      String name = KotlinSyntaxNodes.rightmostIdentifier(entry);
      if ("JvmName".equals(name)) {
        String value = KotlinSyntaxNodes.firstStringArgument(entry);
        if (value != null && !value.isEmpty()) {
          jvmName = value;
        }
      }
      else if ("JvmMultifileClass".equals(name)) {
        multifile = true;
      }
    }
    return new FileJvmAnnotations(jvmName, multifile);
  }

  private void appendFileClassMember(@NotNull ClassSymbol.Builder fileClass, @NotNull SyntaxNode member) {
    SyntaxNode modifierList = firstChildOfType(member, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    if (hasModifier(modifierList, KtTokens.INSTANCE.getPRIVATE_MODIFIER())) return;
    if (member.getType() == KtNodeTypes.INSTANCE.getFUN()) {
      MethodSymbol.Builder m = methodExtractor.extractFunction(member, fileClass.name, Set.of(), MethodType.STATIC);
      if (m != null) {
        m.modifiers |= Modifier.STATIC;
        m.methodType = MethodType.STATIC;
        fileClass.methods.add(m);
      }
    }
    else if (member.getType() == KtNodeTypes.INSTANCE.getPROPERTY()) {
      if (isConstProperty(member)) return;
      MethodSymbol.Builder getter = methodExtractor.synthesizeGetter(member, fileClass.name, Set.of(), true);
      if (getter != null) fileClass.methods.add(getter);
      if (isVarProperty(member)) {
        MethodSymbol.Builder setter = methodExtractor.synthesizeSetter(member, fileClass.name, Set.of(), true);
        if (setter != null) fileClass.methods.add(setter);
      }
    }
  }

  private void walkClass(@NotNull SyntaxNode classNode,
                         @NotNull Fqn enclosingFqn,
                         @NotNull Set<String> outerTypeVars) {
    SyntaxNode nameId = nameIdentifier(classNode);
    if (nameId == null) return;
    String simple = nameId.getText().toString();
    Fqn fqn = imports.qualify(enclosingFqn, simple);

    ClassSymbol.Builder info = new ClassSymbol.Builder();
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
      info.annotationTargets.addAll(parseAnnotationTargetSet(modifierList));
    }
    if (!enclosingFqn.isEmpty() && !isInnerNested(classNode)) {
      info.modifiers |= Modifier.STATIC;
    }

    SyntaxNode body = firstChildOfType(classNode, KtNodeTypes.INSTANCE.getCLASS_BODY());
    Map<String, Fqn> previousScope = typeFormatter.getNestedScope();
    typeFormatter.setNestedScope(collectNestedScope(previousScope, fqn, body));
    try {
      SyntaxNode superTypeList = firstChildOfType(classNode, KtNodeTypes.INSTANCE.getSUPER_TYPE_LIST());
      populateSuperTypes(info, classNode, superTypeList, classTypeVars);

      result.put(fqn, info);

      // Primary constructor + its val/var properties.
      SyntaxNode primaryCtor = firstChildOfType(classNode, KtNodeTypes.INSTANCE.getPRIMARY_CONSTRUCTOR());
      if (primaryCtor != null) {
        MethodSymbol.Builder ctorMethod = methodExtractor.extractConstructor(primaryCtor, fqn, classTypeVars);
        if (ctorMethod != null) info.methods.add(ctorMethod);
        collectPrimaryCtorPropertyAccessors(primaryCtor, info, classTypeVars);
      }

      if (body != null) walkClassBody(body, info, classTypeVars);
    }
    finally {
      typeFormatter.setNestedScope(previousScope);
    }
  }

  /**
   * Collect simple-name → FQN entries for direct nested classes / objects / typealiases declared in
   * {@code body}, layered on top of {@code previousScope}. The result is what
   * {@link KotlinSyntaxTypeFormatter#setNestedScope} should hold while walking the enclosing class's
   * members so that unqualified or partially-qualified references to siblings resolve to the
   * enclosing-class-qualified FQN.
   */
  private static @NotNull Map<String, Fqn> collectNestedScope(@NotNull Map<String, Fqn> previousScope,
                                                              @NotNull Fqn enclosingFqn,
                                                              @Nullable SyntaxNode body) {
    if (body == null) return previousScope;
    Map<String, Fqn> scope = new HashMap<>(previousScope);
    for (SyntaxNode m = body.firstChild(); m != null; m = m.nextSibling()) {
      SyntaxElementType mt = m.getType();
      boolean isClass = mt == KtNodeTypes.INSTANCE.getCLASS();
      boolean isObject = mt == KtNodeTypes.INSTANCE.getOBJECT_DECLARATION();
      boolean isTypeAlias = mt == KtNodeTypes.INSTANCE.getTYPEALIAS();
      if (!isClass && !isObject && !isTypeAlias) continue;
      SyntaxNode nameNode = nameIdentifier(m);
      String name = nameNode != null
                    ? nameNode.getText().toString()
                    : (isObject && isCompanion(m) ? "Companion" : null);
      if (name != null) scope.put(name, enclosingFqn.child(name));
    }
    return scope;
  }

  private void walkObject(@NotNull SyntaxNode objectNode,
                          @NotNull Fqn enclosingFqn,
                          @Nullable ClassSymbol.Builder enclosingClass,
                          @NotNull Set<String> outerTypeVars) {
    SyntaxNode nameId = nameIdentifier(objectNode);
    boolean companion = isCompanion(objectNode);
    String simple = nameId != null ? nameId.getText().toString() : (companion ? "Companion" : null);
    if (simple == null) return;
    Fqn fqn = imports.qualify(enclosingFqn, simple);

    ClassSymbol.Builder info = new ClassSymbol.Builder();
    info.name = fqn;
    SyntaxNode modifierList = firstChildOfType(objectNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    info.modifiers = extractModifiers(modifierList) | Modifier.FINAL;
    if (!enclosingFqn.isEmpty()) info.modifiers |= Modifier.STATIC;
    info.annotations.addAll(typeFormatter.extractAnnotationFqns(modifierList, outerTypeVars));
    info.superClass = Fqn.JAVA_LANG_OBJECT;

    SyntaxNode body = firstChildOfType(objectNode, KtNodeTypes.INSTANCE.getCLASS_BODY());
    Map<String, Fqn> previousScope = typeFormatter.getNestedScope();
    typeFormatter.setNestedScope(collectNestedScope(previousScope, fqn, body));
    try {
      SyntaxNode superTypeList = firstChildOfType(objectNode, KtNodeTypes.INSTANCE.getSUPER_TYPE_LIST());
      populateSuperTypes(info, objectNode, superTypeList, outerTypeVars);

      result.put(fqn, info);

      if (body != null) walkClassBody(body, info, outerTypeVars);
    }
    finally {
      typeFormatter.setNestedScope(previousScope);
    }

    // Lift @JvmStatic-annotated members onto the enclosing class.
    if (companion && enclosingClass != null) {
      for (MethodSymbol.Builder m : info.methods) {
        SyntaxNode origin = null; // We don't track origin nodes; presence-of-@JvmStatic check happens during body walk.
        if (m.modifiers != 0 && Modifier.isStatic(m.modifiers)) {
          // Already a static member by virtue of file-level synthesis (not the case for companion bodies);
          // skip to avoid duplicate insertion.
          continue;
        }
        // Tagged-and-copied path: marker carried via name-prefix collision is awkward; do the lift in walkClassBody instead.
        // (Intentionally a no-op here; see walkClassBody.)
        if (origin != null) {
          MethodSymbol.Builder lifted = copyAsStatic(m, enclosingClass.name);
          enclosingClass.methods.add(lifted);
        }
      }
    }
  }

  private void walkClassBody(@NotNull SyntaxNode body,
                             @NotNull ClassSymbol.Builder enclosing,
                             @NotNull Set<String> typeVars) {
    for (SyntaxNode member = body.firstChild(); member != null; member = member.nextSibling()) {
      SyntaxElementType t = member.getType();
      if (t == KtNodeTypes.INSTANCE.getFUN()) {
        MethodSymbol.Builder m = methodExtractor.extractFunction(member, enclosing.name, typeVars, MethodType.INSTANCE);
        if (m == null) continue;
        // Static methods (extension fns on member objects) keep their type as set by the extractor.
        enclosing.methods.add(m);
        // Companion-object members annotated @JvmStatic surface as static methods on the *outer* class.
        // Handled below in walkObject for the lift; we tag the original by inspecting modifierList here.
      }
      else if (t == KtNodeTypes.INSTANCE.getPROPERTY()) {
        if (isConstProperty(member)) continue;
        MethodSymbol.Builder getter = methodExtractor.synthesizeGetter(member, enclosing.name, typeVars, false);
        if (getter != null) enclosing.methods.add(getter);
        if (isVarProperty(member)) {
          MethodSymbol.Builder setter = methodExtractor.synthesizeSetter(member, enclosing.name, typeVars, false);
          if (setter != null) enclosing.methods.add(setter);
        }
      }
      else if (t == KtNodeTypes.INSTANCE.getSECONDARY_CONSTRUCTOR()) {
        MethodSymbol.Builder ctorMethod = methodExtractor.extractConstructor(member, enclosing.name, typeVars);
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
                                     @NotNull ClassSymbol.Builder enclosing,
                                     @NotNull Set<String> outerTypeVars) {
    SyntaxNode nameId = nameIdentifier(objectNode);
    boolean companion = isCompanion(objectNode);
    String simple = nameId != null ? nameId.getText().toString() : (companion ? "Companion" : null);
    if (simple == null) return;
    Fqn fqn = enclosing.name.child(simple);

    ClassSymbol.Builder info = new ClassSymbol.Builder();
    info.name = fqn;
    SyntaxNode modifierList = firstChildOfType(objectNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    info.modifiers = extractModifiers(modifierList) | Modifier.FINAL | Modifier.STATIC;
    info.annotations.addAll(typeFormatter.extractAnnotationFqns(modifierList, outerTypeVars));
    info.superClass = Fqn.JAVA_LANG_OBJECT;

    SyntaxNode body = firstChildOfType(objectNode, KtNodeTypes.INSTANCE.getCLASS_BODY());
    Map<String, Fqn> previousScope = typeFormatter.getNestedScope();
    typeFormatter.setNestedScope(collectNestedScope(previousScope, fqn, body));
    try {
      SyntaxNode superTypeList = firstChildOfType(objectNode, KtNodeTypes.INSTANCE.getSUPER_TYPE_LIST());
      populateSuperTypes(info, objectNode, superTypeList, outerTypeVars);

      result.put(fqn, info);

      if (body == null) return;
      walkObjectInsideClassBody(body, info, enclosing, fqn, companion, outerTypeVars);
    }
    finally {
      typeFormatter.setNestedScope(previousScope);
    }
  }

  private void walkObjectInsideClassBody(@NotNull SyntaxNode body,
                                         @NotNull ClassSymbol.Builder info,
                                         @NotNull ClassSymbol.Builder enclosing,
                                         @NotNull Fqn fqn,
                                         boolean companion,
                                         @NotNull Set<String> outerTypeVars) {
    for (SyntaxNode member = body.firstChild(); member != null; member = member.nextSibling()) {
      SyntaxElementType t = member.getType();
      if (t == KtNodeTypes.INSTANCE.getFUN()) {
        MethodSymbol.Builder m = methodExtractor.extractFunction(member, fqn, outerTypeVars, MethodType.INSTANCE);
        if (m == null) continue;
        info.methods.add(m);
        if (companion && hasJvmStatic(firstChildOfType(member, KtNodeTypes.INSTANCE.getMODIFIER_LIST()))) {
          enclosing.methods.add(copyAsStatic(m, enclosing.name));
        }
      }
      else if (t == KtNodeTypes.INSTANCE.getPROPERTY()) {
        if (isConstProperty(member)) continue;
        MethodSymbol.Builder getter = methodExtractor.synthesizeGetter(member, fqn, outerTypeVars, false);
        if (getter != null) info.methods.add(getter);
        if (isVarProperty(member)) {
          MethodSymbol.Builder setter = methodExtractor.synthesizeSetter(member, fqn, outerTypeVars, false);
          if (setter != null) info.methods.add(setter);
        }
        if (companion && hasJvmStatic(firstChildOfType(member, KtNodeTypes.INSTANCE.getMODIFIER_LIST()))) {
          if (getter != null) enclosing.methods.add(copyAsStatic(getter, enclosing.name));
          if (isVarProperty(member)) {
            MethodSymbol.Builder setter = methodExtractor.synthesizeSetter(member, enclosing.name, outerTypeVars, true);
            if (setter != null) enclosing.methods.add(setter);
          }
        }
      }
      else if (t == KtNodeTypes.INSTANCE.getCLASS()) {
        walkClass(member, fqn, outerTypeVars);
      }
    }
  }

  private void walkTypeAlias(@NotNull SyntaxNode aliasNode, @NotNull Fqn enclosingFqn) {
    SyntaxNode nameId = nameIdentifier(aliasNode);
    if (nameId == null) return;
    String simple = nameId.getText().toString();
    Fqn fqn = imports.qualify(enclosingFqn, simple);

    ClassSymbol.Builder info = new ClassSymbol.Builder();
    info.name = fqn;
    SyntaxNode modifierList = firstChildOfType(aliasNode, KtNodeTypes.INSTANCE.getMODIFIER_LIST());
    info.modifiers = extractModifiers(modifierList) | Modifier.FINAL;
    info.annotations.addAll(typeFormatter.extractAnnotationFqns(modifierList, Set.of()));
    SyntaxNode rhs = firstChildOfType(aliasNode, KtNodeTypes.INSTANCE.getTYPE_REFERENCE());
    // For typealias rhs we want JVM-mapped names (e.g. `String` → `java.lang.String`) so the
    // alias' superClass mirrors what JVM-reflective callers expect — use the raw Fqn of the parsed
    // type, which goes through the same JVM_BUILTINS / KOTLIN_TO_JAVA_ALIASES mapping.
    info.superClass = rhs == null
                      ? Fqn.JAVA_LANG_OBJECT
                      : JvmTypeRefs.rawFqn(typeFormatter.parseType(rhs, Set.of()));
    result.put(fqn, info);
  }

  private void populateSuperTypes(@NotNull ClassSymbol.Builder info,
                                  @NotNull SyntaxNode classOrObjectNode,
                                  @Nullable SyntaxNode superTypeList,
                                  @NotNull Set<String> typeVars) {
    List<Fqn> supers = typeFormatter.formatSuperTypes(superTypeList, typeVars);
    if (isInterface(classOrObjectNode) || KotlinSyntaxNodes.isAnnotationClass(classOrObjectNode)) {
      info.superClass = null;
      info.interfaces.addAll(supers);
      return;
    }
    if (supers.isEmpty()) {
      info.superClass = info.superClass == null ? Fqn.JAVA_LANG_OBJECT : info.superClass;
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
                                                   @NotNull ClassSymbol.Builder enclosing,
                                                   @NotNull Set<String> typeVars) {
    SyntaxNode paramList = firstChildOfType(primaryCtor, KtNodeTypes.INSTANCE.getVALUE_PARAMETER_LIST());
    if (paramList == null) return;
    for (SyntaxNode p = paramList.firstChild(); p != null; p = p.nextSibling()) {
      if (p.getType() != KtNodeTypes.INSTANCE.getVALUE_PARAMETER()) continue;
      boolean isVal = firstChildOfType(p, KtTokens.INSTANCE.getVAL_KEYWORD()) != null;
      boolean isVar = firstChildOfType(p, KtTokens.INSTANCE.getVAR_KEYWORD()) != null;
      if (!isVal && !isVar) continue;
      MethodSymbol.Builder getter = methodExtractor.synthesizeGetter(p, enclosing.name, typeVars, false);
      if (getter != null) enclosing.methods.add(getter);
      if (isVar) {
        MethodSymbol.Builder setter = methodExtractor.synthesizeSetter(p, enclosing.name, typeVars, false);
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

  /**
   * Translation map from Kotlin's {@code kotlin.annotation.AnnotationTarget} simple names to
   * {@link TargetType}. {@code AnnotationTarget.TYPE} → {@link TargetType#TYPE_USE} is the load-
   * bearing entry — that's the equivalence the JVM uses when the Kotlin compiler emits a
   * {@code @Target} on a Kotlin annotation class destined for Java callers. Targets without a JVM
   * analog (EXPRESSION, FILE, TYPEALIAS, PROPERTY) intentionally have no mapping.
   */
  private static final Map<String, TargetType> KOTLIN_ANNOTATION_TARGETS = Map.ofEntries(
    Map.entry("CLASS", TargetType.TYPE),
    Map.entry("ANNOTATION_CLASS", TargetType.ANNOTATION_TYPE),
    Map.entry("TYPE_PARAMETER", TargetType.TYPE_PARAMETER),
    Map.entry("FIELD", TargetType.FIELD),
    Map.entry("LOCAL_VARIABLE", TargetType.LOCAL_VARIABLE),
    Map.entry("VALUE_PARAMETER", TargetType.PARAMETER),
    Map.entry("CONSTRUCTOR", TargetType.CONSTRUCTOR),
    Map.entry("FUNCTION", TargetType.METHOD),
    Map.entry("PROPERTY_GETTER", TargetType.METHOD),
    Map.entry("PROPERTY_SETTER", TargetType.METHOD),
    Map.entry("TYPE", TargetType.TYPE_USE)
  );

  /**
   * Walk the annotation class's modifier list for a {@code @Target(AnnotationTarget.*, ...)} entry
   * and decode it into a {@link TargetType} set. Recognises the annotation by simple name
   * ({@code "Target"}); arguments are matched via their rightmost identifier, accepting both
   * {@code AnnotationTarget.TYPE} and statically-imported bare {@code TYPE}.
   */
  private static @NotNull Set<TargetType> parseAnnotationTargetSet(@Nullable SyntaxNode modifierList) {
    if (modifierList == null) return Set.of();
    for (SyntaxNode entry : childrenOfType(modifierList, KtNodeTypes.INSTANCE.getANNOTATION_ENTRY())) {
      if (!"Target".equals(KotlinSyntaxNodes.rightmostIdentifier(entry))) continue;
      SyntaxNode argList = firstChildOfType(entry, KtNodeTypes.INSTANCE.getVALUE_ARGUMENT_LIST());
      if (argList == null) return Set.of();
      EnumSet<TargetType> out = EnumSet.noneOf(TargetType.class);
      for (SyntaxNode arg = argList.firstChild(); arg != null; arg = arg.nextSibling()) {
        if (arg.getType() != KtNodeTypes.INSTANCE.getVALUE_ARGUMENT()) continue;
        String simple = KotlinSyntaxNodes.rightmostIdentifier(arg);
        if (simple == null) continue;
        TargetType mapped = KOTLIN_ANNOTATION_TARGETS.get(simple);
        if (mapped != null) out.add(mapped);
      }
      return out.isEmpty() ? Set.of() : out;
    }
    return Set.of();
  }

  private static @NotNull MethodSymbol.Builder copyAsStatic(@NotNull MethodSymbol.Builder src, @NotNull Fqn newDeclaring) {
    MethodSymbol.Builder m = new MethodSymbol.Builder();
    m.name = src.name;
    m.declaringClass = newDeclaring;
    m.modifiers = src.modifiers | Modifier.STATIC;
    m.methodType = MethodType.STATIC;
    m.returnType = src.returnType;
    m.annotations.addAll(src.annotations);
    m.exceptions.addAll(src.exceptions);
    m.generics.addAll(src.generics);
    for (ParameterSymbol.Builder sp : src.parameters) {
      ParameterSymbol.Builder cp = new ParameterSymbol.Builder();
      cp.name = sp.name;
      cp.type = sp.type;
      cp.annotations.addAll(sp.annotations);
      m.parameters.add(cp);
    }
    return m;
  }

}
