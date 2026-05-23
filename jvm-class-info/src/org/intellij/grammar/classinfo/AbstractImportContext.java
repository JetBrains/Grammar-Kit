/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Shared base for file-level name-resolution scopes: package + single-type imports + wildcard imports.
 * <p>
 * Resolution of an unqualified simple name follows a fixed pipeline:
 * <ol>
 *   <li>explicit single-type imports (incl. aliases),</li>
 *   <li>language-specific predefined names ({@link #resolveBuiltin}: e.g. {@code java.lang} allow-list,
 *       Kotlin built-ins / {@code kotlin.collections} mappings),</li>
 *   <li>wildcard imports probed via the supplied {@link SymbolResolver} — this is the cross-language
 *       hop: a Java file's wildcard import can resolve to a Kotlin class and vice versa,</li>
 *   <li>same-package fallback,</li>
 *   <li>last resort: the simple name unchanged.</li>
 * </ol>
 * Qualified references are out of scope here; the caller decides not to consult resolution at all
 * for already-dotted names.
 */
public abstract class AbstractImportContext {

  private final String packageName;
  private final Map<String, String> imports;
  private final List<String> wildcardImports;
  private final SymbolResolver resolver;

  protected AbstractImportContext(@NotNull String packageName,
                                  @NotNull Map<String, String> imports,
                                  @NotNull List<String> wildcardImports,
                                  @NotNull SymbolResolver resolver) {
    this.packageName = packageName;
    this.imports = imports;
    this.wildcardImports = wildcardImports;
    this.resolver = resolver;
  }

  public final @NotNull String packageName() {
    return packageName;
  }

  public final @NotNull String resolveSimpleName(@NotNull String simple) {
    String byImport = imports.get(simple);
    if (byImport != null) return byImport;
    String byBuiltin = resolveBuiltin(simple);
    if (byBuiltin != null) return byBuiltin;
    for (String pkg : wildcardImports) {
      Fqn candidate = Fqn.of(pkg).child(simple);
      if (resolver.findClass(candidate) != null) return candidate.value();
    }
    if (!packageName.isEmpty()) return Fqn.of(packageName).child(simple).value();
    return simple;
  }

  /** Whether {@code simple} matches an explicit single-type-import entry — a definitive class hit. */
  public final boolean isSingleTypeImport(@NotNull String simple) {
    return imports.containsKey(simple);
  }

  /** Whether {@code simple} resolves as a language built-in (e.g. {@code java.lang} allow-list). */
  public final boolean isKnownBuiltin(@NotNull String simple) {
    return resolveBuiltin(simple) != null;
  }

  /** Qualify a simple name with the file's package when not nested inside an enclosing class. */
  public final @NotNull Fqn qualify(@NotNull Fqn enclosingFqn, @NotNull String simpleName) {
    if (!enclosingFqn.isEmpty()) return enclosingFqn.child(simpleName);
    return Fqn.of(packageName).child(simpleName);
  }

  /**
   * Language-specific predefined name resolution applied after explicit imports and before wildcard
   * probing. Return {@code null} to indicate "no built-in match — try the next step."
   */
  protected abstract @Nullable String resolveBuiltin(@NotNull String simple);
}
