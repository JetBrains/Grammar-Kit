/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import junit.framework.TestCase;

import java.io.StringWriter;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Generated sources are committed artifacts, so {@link FilePrinter} must produce the same bytes
 * on every host OS: always {@code \n}, never the platform line separator (GitHub #468).
 */
public class FilePrinterTest extends TestCase {

  public void testLineSeparatorIsAlwaysLf() {
    assertEquals("// comment\n", print(p -> p.out("// comment")));
  }

  public void testEmptyOutputPrintsSingleLf() {
    assertEquals("\n", print(p -> p.out("")));
  }

  public void testMultiLineOutputIsSplitOnLf() {
    assertEquals("// a\n// b\n", print(p -> p.out("// a\n// b")));
  }

  public void testIndentedBlockUsesLf() {
    assertEquals("void f() {\n  return;\n}\n", print(p -> {
      p.out("void f() {");
      p.out("return;");
      p.out("}");
    }));
  }

  public void testFormattedNumbersAreLocaleIndependent() {
    Locale previous = Locale.getDefault();
    try {
      // a locale whose default numbering system is Devanagari, not Latin
      Locale.setDefault(new Locale.Builder().setLanguage("hi").setRegion("IN").setUnicodeLocaleKeyword("nu", "deva").build());
      assertEquals("f(b, 42, X);\n", print(p -> p.out("%s(%s, %d, %s);", "f", "b", 42, "X")));
    }
    finally {
      Locale.setDefault(previous);
    }
  }

  private static String print(Consumer<FilePrinter> consumer) {
    StringWriter writer = new StringWriter();
    try (FilePrinter printer = new FilePrinter(writer)) {
      consumer.accept(printer);
    }
    return writer.toString();
  }
}
