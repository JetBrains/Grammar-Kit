/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import junit.framework.TestCase;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.Fqn;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Base class for golden-file tests that snapshot the output of the syntax-generation pipeline.
 * Each test produces a {@code Map<Fqn, ClassInfo>}, hands it to
 * {@link #assertClassInfoMatchesGolden}, and the rendered text is compared against
 * {@code <goldenDir>/<getName()>.txt}.
 * <p>
 * Set {@code -Dgrammar.kit.override.test.data=true} (or env var
 * {@code GRAMMAR_KIT_OVERRIDE_TEST_DATA=true}) to rewrite the golden in place rather than fail.
 * Used when the change is intended.
 */
public abstract class GoldenClassInfoTestCase extends TestCase {

  private static final String OVERRIDE_PROPERTY = "grammar.kit.override.test.data";
  private static final String OVERRIDE_ENV = "GRAMMAR_KIT_OVERRIDE_TEST_DATA";

  /** Path of the directory under {@code testData/} holding the goldens for this test class. */
  protected abstract @NotNull String goldenDir();

  protected final void assertClassInfoMatchesGolden(@NotNull Map<Fqn, ClassInfo> classes) {
    String actual = ClassInfoTextFormatter.format(classes);
    Path golden = Path.of("testData", goldenDir(), getName() + ".txt");
    compareToGolden(actual, golden);
  }

  private static void compareToGolden(@NotNull String actual, @NotNull Path golden) {
    if (isOverride()) {
      writeGolden(golden, actual);
      return;
    }
    if (!Files.exists(golden)) {
      writeGolden(golden, actual);
      fail("Golden file not found, created with actual output:\n  " + golden.toAbsolutePath()
           + "\nRe-run the test to verify, then commit the file.");
    }
    String expected;
    try {
      expected = Files.readString(golden, StandardCharsets.UTF_8);
    }
    catch (IOException e) {
      throw new AssertionError("Failed to read golden file: " + golden, e);
    }
    String expectedTrimmed = trimTrailingNewline(expected);
    if (!expectedTrimmed.equals(actual)) {
      throw new junit.framework.ComparisonFailure(
        "Golden file does not match: " + golden + "\nSet -D" + OVERRIDE_PROPERTY
        + "=true to rewrite.", expectedTrimmed, actual);
    }
  }

  private static void writeGolden(@NotNull Path golden, @NotNull String actual) {
    try {
      Files.createDirectories(golden.getParent());
      Files.writeString(golden, actual + "\n", StandardCharsets.UTF_8);
    }
    catch (IOException e) {
      throw new AssertionError("Failed to write golden file: " + golden, e);
    }
  }

  private static @NotNull String trimTrailingNewline(@NotNull String s) {
    int end = s.length();
    while (end > 0 && (s.charAt(end - 1) == '\n' || s.charAt(end - 1) == '\r')) end--;
    return s.substring(0, end);
  }

  private static boolean isOverride() {
    return Boolean.parseBoolean(System.getProperty(OVERRIDE_PROPERTY))
           || Boolean.parseBoolean(System.getenv(OVERRIDE_ENV));
  }
}
