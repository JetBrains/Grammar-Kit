/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;

public final class FilePrinter implements Closeable {
  private final @NotNull PrintWriter myOut;
  private int myOffset;
  
  public FilePrinter(@NotNull PrintWriter output) {
    myOut = output;
    myOffset = 0;
  }

  /**
   * Formats the string given the format and args and then
   * prints it to the output.
   */
  public void out(@NotNull String format, Object... args) {
    this.out(format.formatted(args));
  }

  /**
   * Prints the given string to the output.
   * Additionally, it manages the indent level appropriately.
   * @param output the string to print
   */
  public void out(@NotNull String output) {
    int length = output.length();
    if (length == 0) {
      myOut.println();
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
      myOut.println(substring);
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

  /** Sets the current offset level of this object to 0. */
  public void resetOffset() {
    myOffset = 0;
  }
  
  @Override
  public void close() throws IOException {
    myOut.close();
  }
}
