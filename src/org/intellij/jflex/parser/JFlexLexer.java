/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.parser;

import com.intellij.lexer.FlexAdapter;

/**
 * @author gregsh
 */
public class JFlexLexer extends FlexAdapter {
  public JFlexLexer() {
    super(new _JFlexLexer());
  }
}
