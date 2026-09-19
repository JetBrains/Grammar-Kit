/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

final class FilePrinter implements Closeable {
  private final @NotNull PrintWriter myOut;
  private int myOffset;

  public FilePrinter(@NotNull Writer output) {
    // PrintWriter for its swallow-the-IOException behaviour, buffered because the writer handed
    // over by an OutputOpener need not be. Line breaks come from newLine(), never from println().
    myOut = new PrintWriter(new BufferedWriter(output));
    myOffset = 0;
  }

  /**
   * Formats the string given the format and args and then
   * prints it to the output.
   * <p>
   * {@link Locale#ROOT}: the formatted text is source code, so {@code %d} must render Latin
   * digits whatever the default locale's numbering system is.
   */
  public void out(@NotNull String format, Object... args) {
    this.out(String.format(Locale.ROOT, format, args));
  }

  /**
   * Prints the given string to the output.
   * Additionally, it manages the indent level appropriately.
   * <p>
   * Lines are always terminated with {@code \n} rather than the platform separator so that
   * generated sources, which are typically committed, are byte-identical on every host OS.
   *
   * @param output the string to print
   */
  public void out(@NotNull String output) {
    int length = output.length();
    if (length == 0) {
      newLine();
      return;
    }
    boolean newStatement = true;
    for (int start = 0, end; start < length; start = end + 1) {
      boolean isComment = output.startsWith("//", start);
      end = StringUtil.indexOf(output, '\n', start, length);
      if (end == -1) end = length;
      String substring = output.substring(start, end);
      if (!isComment && (substring.startsWith("}") || substring.startsWith(")"))) {
        myOffset--;
        newStatement = true;
      }
      if (myOffset > 0) {
        myOut.print(StringUtil.repeat("  ", newStatement ? myOffset : myOffset + 1));
      }
      myOut.print(substring);
      newLine();
      if (isComment) {
        newStatement = true;
      }
      else if (substring.endsWith("{")) {
        myOffset++;
        newStatement = true;
      }
      else if (substring.endsWith("(")) {
        myOffset++;
        newStatement = false;
      }
      else {
        newStatement = substring.endsWith(";") || substring.endsWith("}");
      }
    }
  }

  private void newLine() {
    myOut.print('\n');
  }

  /**
   * Sets the current offset level of this object to 0.
   */
  public void resetOffset() {
    myOffset = 0;
  }

  @Override
  public void close() {
    myOut.close();
  }
}
