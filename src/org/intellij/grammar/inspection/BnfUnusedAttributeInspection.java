/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfVisitor;
import org.jetbrains.annotations.NotNull;

import static org.intellij.grammar.KnownAttribute.getAttribute;
import static org.intellij.grammar.KnownAttribute.getCompatibleAttribute;

/**
 * @author gregsh
 */
public class BnfUnusedAttributeInspection extends LocalInspectionTool {

  @Override
  public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
    return new BnfVisitor<Void>() {
      @Override
      public Void visitAttr(@NotNull BnfAttr o) {
        String name = o.getName();
        if (!name.toUpperCase().equals(name) && getAttribute(name) == null) {
          KnownAttribute<?> newAttr = getCompatibleAttribute(name);
          String text = newAttr == null ? "Unused attribute" : "Deprecated attribute, use '" + newAttr.getName() + "' instead";
          holder.registerProblem(o.getId(), text);
        }
        return null;
      }
    };
  }
}
