/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.parser;

import com.intellij.lexer.FlexAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: gregory
 * Date: 13.07.11
 * Time: 22:50
 */
public class BnfLexer extends FlexAdapter {

  public BnfLexer() {
    super(new _BnfLexer());
  }

}
