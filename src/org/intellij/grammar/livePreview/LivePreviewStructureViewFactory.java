/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.livePreview;

import com.intellij.ide.structureView.*;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.BnfIcons;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getPsiClassFormat;
import static org.intellij.grammar.generator.ParserGeneratorUtil.getRulePsiClassName;

/**
 * @author gregsh
 */
public class LivePreviewStructureViewFactory implements PsiStructureViewFactory {

  public @Nullable StructureViewBuilder getStructureViewBuilder(@NotNull PsiFile psiFile) {
    if (!(psiFile.getLanguage() instanceof LivePreviewLanguage)) return null;
    return new TreeBasedStructureViewBuilder() {
      @Override
      public @NotNull StructureViewModel createStructureViewModel(@Nullable Editor editor) {
        return new MyModel(psiFile);
      }

      @Override
      public boolean isRootNodeShown() {
        return false;
      }
    };
  }

  private static class MyModel extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider{
    protected MyModel(@NotNull PsiFile psiFile) {
      super(psiFile, new MyElement(psiFile));
      withSuitableClasses(PsiElement.class);
    }

    @Override
    public boolean shouldEnterElement(Object element) {
      return true;
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
      return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
      return element.getValue() instanceof LeafPsiElement;
    }

  }

  private static class MyElement extends PsiTreeElementBase<PsiElement> implements SortableTreeElement, ColoredItemPresentation {

    MyElement(PsiElement element) {
      super(element);
    }

    public @NotNull Collection<StructureViewTreeElement> getChildrenBase() {
      PsiElement element = getElement();
      if (element == null || element instanceof LeafPsiElement) return Collections.emptyList();
      ArrayList<StructureViewTreeElement> result = new ArrayList<>();
      for (PsiElement e = element.getFirstChild(); e != null; e = e.getNextSibling()) {
        if (e instanceof PsiWhiteSpace) continue;
        result.add(new MyElement(e));
      }
      return result;
    }

    @Override
    public @NotNull String getAlphaSortKey() {
      return getPresentableText();
    }

    @Override
    public @NotNull String getPresentableText() {
      PsiElement element = getElement();
      ASTNode node = element != null ? element.getNode() : null;
      IElementType elementType = node != null ? node.getElementType() : null;
      if (element instanceof LeafPsiElement) {
        return elementType + ": '" + element.getText() + "'";
      }
      else if (element instanceof PsiErrorElement) {
        return "PsiErrorElement: '" + ((PsiErrorElement)element).getErrorDescription() + "'";
      }
      else if (elementType instanceof LivePreviewElementType.RuleType) {
        BnfRule rule = ((LivePreviewElementType.RuleType)elementType).getRule(element.getProject());
        if (rule != null) {
          BnfFile file = (BnfFile)rule.getContainingFile();
          String className = getRulePsiClassName(rule, getPsiClassFormat(file));
          return className + ": '" + StringUtil.first(element.getText(), 30, true) +"'";
        }
      }
      return elementType + "";
    }

    @Override
    public @Nullable String getLocationString() {
      return null;
    }

    @Override
    public @Nullable Icon getIcon(boolean unused) {
      PsiElement element = getElement();
      if (element instanceof PsiErrorElement) {
        return null; //AllIcons.General.Error;
      }
      else if (element instanceof LeafPsiElement) {
        return null;
      }
      ASTNode node = element != null ? element.getNode() : null;
      IElementType elementType = node != null ? node.getElementType() : null;
      if (elementType instanceof LivePreviewElementType.RuleType) {
        return BnfIcons.RULE;
      }
      return null;
    }

    @Override
    public @Nullable TextAttributesKey getTextAttributesKey() {
      return getElement() instanceof PsiErrorElement? CodeInsightColors.ERRORS_ATTRIBUTES : null;
    }
  }
}
