/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.test;

import com.intellij.lang.PsiBuilder;
import org.intellij.grammar.parser.GeneratedParserUtilBase;

/**
 * @author gregsh
 */
public class ParserUtil extends GeneratedParserUtilBase {
  public static boolean listOf(int level, PsiBuilder builder, Parser... p) {
    return true;
  }

  public static boolean uniqueListOf(int level, PsiBuilder builder, Parser... p) {
    return true;
  }

  public static boolean parseRef(int level, PsiBuilder builder) {
    return true;
  }
}
