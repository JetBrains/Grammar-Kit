/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.parser;

import com.intellij.lang.PsiBuilder;

/**
 * @author gregsh
 */
public class GrammarParserUtil extends GeneratedParserUtilBase {
  public static boolean parseGrammar(PsiBuilder builder_, int level, Parser parser) {
    ErrorState state = ErrorState.get(builder_);
    return parseAsTree(state, builder_, level, DUMMY_BLOCK, true, parser, TRUE_CONDITION);
  }
}
