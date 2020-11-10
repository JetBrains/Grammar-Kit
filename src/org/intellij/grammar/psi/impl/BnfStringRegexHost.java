/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.psi.impl;

import com.intellij.psi.impl.JavaRegExpHost;

/**
 * @author gregsh
 */
public class BnfStringRegexHost extends JavaRegExpHost {
  @Override
  public boolean characterNeedsEscaping(char c) {
    return c == '\"' || super.characterNeedsEscaping(c);
  }
}
