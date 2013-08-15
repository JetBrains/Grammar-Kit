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

package org.intellij.jflex.psi.impl;

import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.ArrayUtil;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.jflex.psi.JFlexLexicalRulesSection;
import org.intellij.jflex.psi.JFlexMacroDefinition;
import org.intellij.jflex.psi.JFlexMacroReference;
import org.intellij.jflex.psi.JFlexUserCodeSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gregsh
 */
public class JFlexPsiImplUtil {
  @NotNull
  public static String getName(JFlexMacroDefinition o) {
    return o.getNameIdentifier().getText();
  }

  @NotNull
  public static JFlexMacroDefinition setName(JFlexMacroDefinition o, String newName) {
    // TBD
    return o;
  }

  @NotNull
  public static PsiElement getNameIdentifier(JFlexMacroDefinition o) {
    return o.getId();
  }

  @NotNull
  public static PsiReference getReference(JFlexMacroReference o) {
    return new PsiReferenceBase<JFlexMacroReference>(o, TextRange.from(0, o.getTextRange().getLength())) {
      @Nullable
      @Override
      public PsiElement resolve() {
        final String name = getElement().getId().getText();
        CommonProcessors.FindFirstProcessor<JFlexMacroDefinition> processor =
          new CommonProcessors.FindFirstProcessor<JFlexMacroDefinition>() {
            @Override
            protected boolean accept(JFlexMacroDefinition o) {
              return Comparing.equal(o.getName(), name);
            }
          };
        processMacroVariants(getElement(), processor);
        return processor.getFoundValue();
      }

      @NotNull
      @Override
      public Object[] getVariants() {
        CommonProcessors.CollectProcessor<JFlexMacroDefinition> processor =
          new CommonProcessors.CollectProcessor<JFlexMacroDefinition>();
        processMacroVariants(getElement(), processor);
        return ArrayUtil.toObjectArray(processor.getResults());
      }

    };
  }

  private static boolean processMacroVariants(PsiElement context, Processor<JFlexMacroDefinition> processor) {
    final PsiFile containingFile = context.getContainingFile();
    List<JFlexMacroDefinition> macros = CachedValuesManager.getManager(containingFile.getProject())
      .getCachedValue(containingFile, new CachedValueProvider<List<JFlexMacroDefinition>>() {
        @Nullable
        @Override
        public Result<List<JFlexMacroDefinition>> compute() {
          return Result.create(computeMacroDefinitions(containingFile), containingFile);
        }
      });
    return ContainerUtil.process(macros, processor);
  }

  private static List<JFlexMacroDefinition> computeMacroDefinitions(PsiFile psiFile) {
    final List<JFlexMacroDefinition> result = ContainerUtil.newArrayList();
    psiFile.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        if (element instanceof JFlexMacroDefinition) {
          result.add((JFlexMacroDefinition)element);
        }
        else if (!(element instanceof JFlexLexicalRulesSection) &&
                 !(element instanceof JFlexUserCodeSection)) {
          super.visitElement(element);
        }
      }
    });
    return result;
  }
}
