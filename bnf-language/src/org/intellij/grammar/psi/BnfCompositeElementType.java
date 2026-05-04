/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.BnfLanguage;

/**
 * Created by IntelliJ IDEA.
 * User: gregory
 * Date: 13.07.11
 * Time: 19:11
 */
public class BnfCompositeElementType extends IElementType {
  public BnfCompositeElementType(String debug) {
    super(debug, BnfLanguage.INSTANCE);
  }
}
