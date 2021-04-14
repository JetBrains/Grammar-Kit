/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @author gregory
 *         Date: 17.07.11 2:55
 */
public interface BnfIcons {
  Icon FILE = IconLoader.getIcon("/resources/bnf.svg", BnfIcons.class);

  Icon RULE = AllIcons.Nodes.Method;
  Icon EXTERNAL_RULE = AllIcons.Nodes.AbstractMethod;
  Icon ATTRIBUTE = AllIcons.Nodes.Field;

  Icon RELATED_METHOD = AllIcons.Gutter.ImplementedMethod;
}
