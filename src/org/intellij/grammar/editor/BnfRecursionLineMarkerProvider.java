/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
  public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
    return null;
  }

  @Override
  public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {
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
