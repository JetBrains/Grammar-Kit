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
package org.intellij.grammar;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory;
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import gnu.trove.THashSet;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author gregsh
 */
public class BnfUnusedRulePassFactory extends AbstractProjectComponent implements TextEditorHighlightingPassFactory {
  public static final Key<CachedValue<Set<PsiElement>>> USED_RULES_KEY = Key.create("USED_RULES_KEY");

  public BnfUnusedRulePassFactory(Project project, TextEditorHighlightingPassRegistrar highlightingPassRegistrar) {
    super(project);
    highlightingPassRegistrar.registerTextEditorHighlightingPass(this, new int[]{Pass.UPDATE_ALL,}, null, true, -1);
  }

  @NonNls
  @NotNull
  public String getComponentName() {
    return "BnfUnusedRulePassFactory";
  }

  @Nullable
  public TextEditorHighlightingPass createHighlightingPass(@NotNull PsiFile file, @NotNull final Editor editor) {
    return file instanceof BnfFile? new MyPass(myProject, (BnfFile)file, editor.getDocument()) : null;
  }

  private static Set<PsiElement> getUsedElements(final PsiFile file) {
    CachedValue<Set<PsiElement>> value = file.getUserData(USED_RULES_KEY);
    if (value == null) {
      file.putUserData(USED_RULES_KEY, value = CachedValuesManager.getManager(file.getProject()).createCachedValue(new CachedValueProvider<Set<PsiElement>>() {
        @Override
        public Result<Set<PsiElement>> compute() {
          final THashSet<PsiElement> psiElements = new THashSet<PsiElement>();
          file.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(PsiElement element) {
              if (element instanceof BnfReferenceOrToken || element instanceof BnfStringLiteralExpression) {
                PsiReference reference = element.getReference();
                PsiElement target = reference != null? reference.resolve() : null;
                if (target instanceof BnfNamedElement) {
                  psiElements.add(target);
                }
              }
              super.visitElement(element);
            }
          });
          return new Result<Set<PsiElement>>(psiElements, PsiModificationTracker.MODIFICATION_COUNT);
        }
      }, false));
    }
    return value.getValue();
  }


  static class MyPass extends TextEditorHighlightingPass {

    private final BnfFile myFile;
    private final List<HighlightInfo> myHighlights = new ArrayList<HighlightInfo>();

    MyPass(Project myProject, BnfFile file, Document document) {
      super(myProject, document, true);
      myFile = file;
    }

    @Override
    public void doCollectInformation(@NotNull ProgressIndicator progress) {
      final Set<PsiElement> usedElements = getUsedElements(myFile);
      boolean first = true;
      for (BnfRule o : myFile.getRules()) {
        if (first) {
          first = false;
          continue;
        }
        if (!usedElements.contains(o)) {
          myHighlights.add(HighlightInfo.newHighlightInfo(HighlightInfoType.WARNING).
            range(o.getId()).descriptionAndTooltip("Unused rule").create());
        }
        else {
          visitAttrs(o.getAttrs(), usedElements);
        }
      }
      for (BnfAttrs o : myFile.getAttributes()) {
        visitAttrs(o, usedElements);
      }
    }

    private void visitAttrs(BnfAttrs attrs, Set<PsiElement> usedElements) {
      if (attrs == null) return;
      for (PsiElement child = attrs.getFirstChild(); child != null; child = child.getNextSibling()) {
        if (!(child instanceof BnfAttr)) continue;
        final String name = ((BnfAttr)child).getName();
        if (!usedElements.contains(child) && !name.toUpperCase().equals(name) && KnownAttribute.getAttribute(name) == null) {
          PsiElement anchor = ((BnfAttr)child).getId();
          myHighlights.add(HighlightInfo.newHighlightInfo(HighlightInfoType.WARNING).
            range(anchor).descriptionAndTooltip("Unused attribute").create());
        }
      }
    }

    @Override
    public void doApplyInformationToEditor() {
      UpdateHighlightersUtil.setHighlightersToEditor(myProject, myDocument, 0, myFile.getTextLength(), myHighlights, getColorsScheme(),
                                                     getId());
    }
  }
}
