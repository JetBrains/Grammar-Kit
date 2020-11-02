/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.refactor;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import org.intellij.grammar.parser.BnfLexer;
import org.intellij.grammar.psi.BnfTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class BnfNamesValidator implements NamesValidator {
  @Override
  public boolean isKeyword(@NotNull String s, Project project) {
    return false;
  }

  @Override
  public boolean isIdentifier(@NotNull String s, Project project) {
    BnfLexer lexer = new BnfLexer();
    lexer.start(s);
    return lexer.getTokenEnd() == s.length() && lexer.getTokenType() == BnfTypes.BNF_ID;
  }
}
