/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.jflex.JFlexLanguage;

/**
 * @author gregsh
 */
public class JFlexTokenType extends IElementType {
  public JFlexTokenType(String debug) {
    super(debug, JFlexLanguage.INSTANCE);
  }
}
