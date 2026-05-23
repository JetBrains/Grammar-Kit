/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import junit.framework.TestCase;
import org.intellij.grammar.classinfo.asm.AsmClassSymbolProvider;

import java.lang.reflect.Modifier;

/**
 * Unit coverage for {@link AsmClassSymbolProvider#unmangleInternalKotlinName} (audit task #16).
 * <p>
 * Source-level method-name lookup expects the un-mangled Kotlin name (the name the user wrote),
 * but {@code internal fun foo()} compiles to {@code public foo$<module-name>} at the bytecode
 * level. The ASM provider strips the suffix so its output converges with the source extractor.
 * <p>
 * This is a unit test (not a convergence test) because the convergence harness is Java-only
 * today — exercising the real Kotlin compilation pipeline against {@code internal} declarations
 * needs the deferred kotlin-compiler-embeddable dependency. The unmangler is a pure string
 * function, easy to cover directly.
 */
public class AsmInternalNameUnmanglingTest extends TestCase {

  // --- positive: typical Kotlin internal-mangled names get the suffix stripped ----------------

  public void testInternalManglingStrippedForLowercaseModule() {
    assertEquals("foo", AsmClassSymbolProvider.unmangleInternalKotlinName("foo$grammar_kit", Modifier.PUBLIC));
  }

  public void testInternalManglingStrippedForHyphenatedModule() {
    // Gradle-style module names use hyphens; kotlinc keeps them in the mangle suffix.
    assertEquals("doIt", AsmClassSymbolProvider.unmangleInternalKotlinName("doIt$my-module", Modifier.PUBLIC));
  }

  public void testInternalManglingStrippedForCamelCaseModule() {
    assertEquals("compute", AsmClassSymbolProvider.unmangleInternalKotlinName("compute$MainKt", Modifier.PUBLIC));
  }

  // --- negative: things we must NOT touch ------------------------------------------------------

  public void testPlainNameUnchanged() {
    assertEquals("foo", AsmClassSymbolProvider.unmangleInternalKotlinName("foo", Modifier.PUBLIC));
  }

  public void testConstructorUnchanged() {
    assertEquals("<init>", AsmClassSymbolProvider.unmangleInternalKotlinName("<init>", Modifier.PUBLIC));
  }

  public void testNonPublicMethodUnchanged() {
    // internal mangling only happens on public; private/protected methods with $ in the name
    // (rare but possible) shouldn't be touched.
    assertEquals("foo$private_helper", AsmClassSymbolProvider.unmangleInternalKotlinName("foo$private_helper", Modifier.PRIVATE));
  }

  public void testTrailingDollarUnchanged() {
    // Empty suffix after $ — not a Kotlin module name, leave it alone.
    assertEquals("foo$", AsmClassSymbolProvider.unmangleInternalKotlinName("foo$", Modifier.PUBLIC));
  }

  public void testSuffixWithInvalidCharsUnchanged() {
    // Module names don't have dots — anything that isn't [A-Za-z_][A-Za-z0-9_-]* is left alone.
    assertEquals("foo$bar.baz", AsmClassSymbolProvider.unmangleInternalKotlinName("foo$bar.baz", Modifier.PUBLIC));
  }

  public void testDigitOnlyLeadingCharUnchanged() {
    // Module identifier must start with a letter or underscore — "$2lambda" is some compiler
    // artifact, leave it alone.
    assertEquals("foo$2lambda", AsmClassSymbolProvider.unmangleInternalKotlinName("foo$2lambda", Modifier.PUBLIC));
  }

  public void testLeadingDollarOnNameUnchanged() {
    // Name starts with $ — synthetic-style, last-dollar would strip the whole thing. Leave alone.
    assertEquals("$module", AsmClassSymbolProvider.unmangleInternalKotlinName("$module", Modifier.PUBLIC));
  }

  // --- multi-dollar: only the trailing module suffix is stripped -------------------------------

  public void testOnlyLastDollarSuffixIsStripped() {
    // foo$lambda$grammar_kit — the lambda-name machinery already strips this via ACC_SYNTHETIC,
    // so we won't see it in practice. But the unmangler should still only act on the last $.
    assertEquals("foo$lambda", AsmClassSymbolProvider.unmangleInternalKotlinName("foo$lambda$grammar_kit", Modifier.PUBLIC));
  }
}
