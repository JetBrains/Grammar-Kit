/*
 * Copyright 2011-2013 Gregory Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.intellij.jflex.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.intellij.jflex.psi.JFlexMacroDefinition;
import org.intellij.jflex.psi.JFlexMacroReference;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class JFlexAnnotator implements Annotator, DumbAware {
  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element instanceof JFlexMacroDefinition) {
      holder.createInfoAnnotation(((JFlexMacroDefinition)element).getNameIdentifier(), null).setTextAttributes(JFlexSyntaxHighlighterFactory.MACRO);
    }
    else if (element instanceof JFlexMacroReference) {
      PsiReference reference = element.getReference();
      PsiElement resolve = reference == null ? null : reference.resolve();
      holder.createInfoAnnotation(element.getParent(), null).setTextAttributes(JFlexSyntaxHighlighterFactory.MACRO);
      if (resolve == null) {
        holder.createWarningAnnotation(element, "Unresolved macro reference");
      }
    }
  }
}
