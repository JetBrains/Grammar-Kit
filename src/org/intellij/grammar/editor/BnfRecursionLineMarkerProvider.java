/*
 * Copyright 2011-present Greg Shrago
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

package org.intellij.grammar.editor;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import com.intellij.util.FunctionUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author gregsh
 */
public class BnfRecursionLineMarkerProvider implements LineMarkerProvider {
  @Nullable
  @Override
  public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
    return null;
  }

  @Override
  public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
    for (PsiElement element : elements) {
      if (!(element instanceof BnfRule)) continue;
      BnfRule rule = (BnfRule)element;

      ProgressManager.checkCanceled();

      RuleGraphHelper helper = RuleGraphHelper.getCached((BnfFile)rule.getContainingFile());
      Map<PsiElement, RuleGraphHelper.Cardinality> map = helper.getFor(rule);
      if (map.containsKey(rule)) {
        result.add(new MyMarkerInfo(rule.getId()));
      }
    }
  }

  private static class MyMarkerInfo extends LineMarkerInfo<PsiElement> {
    private MyMarkerInfo(@NotNull PsiElement id) {
      super(id,
            id.getTextRange(),
            AllIcons.Gutter.RecursiveMethod,
            FunctionUtil.constant("Recursive rule"),
            null,
            GutterIconRenderer.Alignment.RIGHT
      );
    }

    @Override
    public GutterIconRenderer createGutterRenderer() {
      if (myIcon == null) return null;
      return new LineMarkerGutterIconRenderer<PsiElement>(this) {
        @Override
        public AnAction getClickAction() {
          return null;
        }
      };
    }
  }
}
