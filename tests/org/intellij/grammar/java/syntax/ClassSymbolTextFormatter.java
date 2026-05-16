/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.ParameterSymbol;
import org.intellij.grammar.classinfo.TypeParameterSymbol;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Serialises a {@code Map<Fqn, ClassSymbol>} produced by the syntax-generation pipeline into a
 * stable, human-readable text block used by the golden-file tests in this package. The format is
 * pinned by {@code ClassInfoTextFormatterTest} — change it there if you change it here.
 */
public final class ClassSymbolTextFormatter {

  private ClassSymbolTextFormatter() { }

  public static @NotNull String format(@NotNull Map<Fqn, ClassSymbol> classes) {
    List<Map.Entry<Fqn, ClassSymbol>> sorted = new ArrayList<>(classes.entrySet());
    sorted.sort(Comparator.comparing(e -> e.getKey().value()));
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < sorted.size(); i++) {
      if (i > 0) sb.append('\n');
      appendClass(sb, sorted.get(i).getValue());
    }
    // Trim the trailing newline so callers get a clean block; golden files keep their own EOL.
    if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') sb.setLength(sb.length() - 1);
    return sb.toString();
  }

  private static void appendClass(@NotNull StringBuilder sb, @NotNull ClassSymbol info) {
    sb.append("class ").append(info.name);
    if (info.superClass != null) sb.append(" extends ").append(info.superClass);
    if (!info.interfaces.isEmpty()) {
      sb.append(" implements ");
      appendJoined(sb, info.interfaces);
    }
    sb.append('\n');

    if (info.modifiers != 0) {
      sb.append("  modifiers: ").append(modifiersToString(info.modifiers)).append('\n');
    }
    if (!info.annotations.isEmpty()) {
      sb.append("  annotations: ");
      appendAnnotations(sb, info.annotations);
      sb.append('\n');
    }
    if (!info.typeParameters.isEmpty()) {
      sb.append("  typeParameters: ");
      for (int i = 0; i < info.typeParameters.size(); i++) {
        if (i > 0) sb.append(", ");
        sb.append(info.typeParameters.get(i));
      }
      sb.append('\n');
    }
    if (info.multifileFacade) {
      sb.append("  multifileFacade\n");
    }

    if (info.methods.isEmpty()) {
      sb.append("  methods: (none)\n");
    }
    else {
      sb.append("  methods:\n");
      for (MethodSymbol m : info.methods) {
        appendMethod(sb, m);
      }
    }
  }

  private static void appendMethod(@NotNull StringBuilder sb, @NotNull MethodSymbol m) {
    sb.append("    ").append(m.methodType).append(' ');
    String mods = modifiersToString(m.modifiers);
    if (!mods.isEmpty()) sb.append(mods).append(' ');

    if (m.methodType != MethodType.CONSTRUCTOR) {
      String returnType = m.returnType == null ? "?" : m.returnType;
      sb.append(returnType).append(' ');
    }
    sb.append(m.name).append('(');
    appendParams(sb, m);
    sb.append(")\n");

    if (!m.generics.isEmpty()) {
      sb.append("      typeParameters: ");
      for (int i = 0; i < m.generics.size(); i++) {
        if (i > 0) sb.append(", ");
        appendTypeParameter(sb, m.generics.get(i));
      }
      sb.append('\n');
    }
    if (!m.annotations.isEmpty()) {
      sb.append("      annotations: ");
      appendAnnotations(sb, m.annotations);
      sb.append('\n');
    }
    for (int p = 0; p < m.parameters.size(); p++) {
      List<Fqn> paramAnnotations = m.parameters.get(p).annotations;
      if (!paramAnnotations.isEmpty()) {
        sb.append("      param[").append(p).append("] annotations: ");
        appendAnnotations(sb, paramAnnotations);
        sb.append('\n');
      }
    }
    if (!m.exceptions.isEmpty()) {
      sb.append("      throws: ");
      appendJoined(sb, m.exceptions);
      sb.append('\n');
    }
    if (hasAnnotatedTypeDifferences(m)) {
      sb.append("      annotatedTypes: [").append(m.annotatedReturnType);
      for (ParameterSymbol p : m.parameters) {
        sb.append(", ").append(p.annotatedType).append(", ").append(p.name);
      }
      sb.append("]\n");
    }
  }

  private static boolean hasAnnotatedTypeDifferences(@NotNull MethodSymbol m) {
    if (m.annotatedReturnType == null) return false;
    if (!Objects.equals(m.annotatedReturnType, m.returnType)) return true;
    for (ParameterSymbol p : m.parameters) {
      if (!Objects.equals(p.annotatedType, p.type)) return true;
    }
    return false;
  }

  private static void appendParams(@NotNull StringBuilder sb, @NotNull MethodSymbol m) {
    boolean first = true;
    for (ParameterSymbol p : m.parameters) {
      if (!first) sb.append(", ");
      sb.append(p.type).append(' ').append(p.name);
      first = false;
    }
  }

  private static void appendTypeParameter(@NotNull StringBuilder sb, @NotNull TypeParameterSymbol tp) {
    for (Fqn a : tp.getAnnotations()) {
      sb.append('@').append(a).append(' ');
    }
    sb.append(tp.getName() == null ? "?" : tp.getName());
    List<String> bounds = tp.getExtendsList();
    if (!bounds.isEmpty()) {
      sb.append(" extends ");
      for (int i = 0; i < bounds.size(); i++) {
        if (i > 0) sb.append(" & ");
        sb.append(bounds.get(i));
      }
    }
  }

  private static void appendAnnotations(@NotNull StringBuilder sb, @NotNull List<Fqn> annotations) {
    for (int i = 0; i < annotations.size(); i++) {
      if (i > 0) sb.append(", ");
      sb.append('@').append(annotations.get(i));
    }
  }

  private static void appendJoined(@NotNull StringBuilder sb, @NotNull List<Fqn> values) {
    for (int i = 0; i < values.size(); i++) {
      if (i > 0) sb.append(", ");
      sb.append(values.get(i));
    }
  }

  public static @NotNull String modifiersToString(int m) {
    if (m == 0) return "";
    StringBuilder sb = new StringBuilder();
    appendModifier(sb, m, Modifier.PUBLIC,       "public");
    appendModifier(sb, m, Modifier.PROTECTED,    "protected");
    appendModifier(sb, m, Modifier.PRIVATE,      "private");
    appendModifier(sb, m, Modifier.ABSTRACT,     "abstract");
    appendModifier(sb, m, Modifier.STATIC,       "static");
    appendModifier(sb, m, Modifier.FINAL,        "final");
    appendModifier(sb, m, Modifier.NATIVE,       "native");
    appendModifier(sb, m, Modifier.SYNCHRONIZED, "synchronized");
    appendModifier(sb, m, Modifier.TRANSIENT,    "transient");
    appendModifier(sb, m, Modifier.VOLATILE,     "volatile");
    appendModifier(sb, m, Modifier.STRICT,       "strictfp");
    appendModifier(sb, m, Modifier.INTERFACE,    "interface");
    return sb.toString();
  }

  private static void appendModifier(@NotNull StringBuilder sb, int actual, int mask, @NotNull String keyword) {
    if ((actual & mask) != 0) {
      if (sb.length() > 0) sb.append(' ');
      sb.append(keyword);
    }
  }
}
