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

package org.intellij.grammar;

import com.intellij.ide.structureView.*;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.PlatformIcons;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfFileImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author gregsh
 */
public class BnfStructureViewFactory implements PsiStructureViewFactory {
  public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
    return new TreeBasedStructureViewBuilder() {
      @NotNull
      public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
        return new MyModel(psiFile);
      }

      @Override
      public boolean isRootNodeShown() {
        return false;
      }
    };
  }

  public static class MyModel extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider {

    protected MyModel(@NotNull PsiFile psiFile) {
      super(psiFile, new MyElement(psiFile));
      withSuitableClasses(BnfFile.class, BnfRule.class, BnfAttrs.class, BnfAttr.class);
    }


    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
      return element.getValue() instanceof BnfAttrs;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
      final Object value = element.getValue();
      return value instanceof BnfRule || value instanceof BnfAttr;
    }

    @Override
    public boolean shouldEnterElement(Object element) {
      return false;
    }

    @Override
    protected boolean isSuitable(PsiElement element) {
      return element instanceof BnfAttrs || element instanceof BnfRule;
    }
  }

  public static class MyElement extends PsiTreeElementBase<PsiElement> implements SortableTreeElement {

    public MyElement(PsiElement element) {
      super(element);
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
      return getPresentableText();
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
      PsiElement element = getElement();
      if (element instanceof BnfRule
          || element instanceof BnfAttr) {
        return Collections.emptyList();
      }
      List<StructureViewTreeElement> result = new ArrayList<>();
      if (element instanceof BnfFile) {
        for (BnfAttrs o : ((BnfFile)element).getAttributes()) {
          result.add(new MyElement(o));
        }
        for (BnfRule o : ((BnfFile)element).getRules()) {
          result.add(new MyElement(o));
        }
      }
      else if (element instanceof BnfAttrs) {
        for (BnfAttr o : ((BnfAttrs)element).getAttrList()) {
          result.add(new MyElement(o));
        }
      }
      return result;
    }

    @NotNull
    @Override
    public String getPresentableText() {
      PsiElement element = getElement();
      if (element instanceof BnfRule) {
        return StringUtil.notNullize(((PsiNamedElement)element).getName());
      }
      else if (element instanceof BnfAttr) {
        return getAttrDisplayName((BnfAttr)element);
      }
      else if (element instanceof BnfAttrs) {
        List<BnfAttr> attrList = ((BnfAttrs)element).getAttrList();
        final BnfAttr firstAttr = ContainerUtil.getFirstItem(attrList);
        if (firstAttr == null) return "Attributes { <empty> }";
        String suffix = attrList.size() > 1? " & " + attrList.size()+" more..." : " ";
        return "Attributes { " + getAttrDisplayName(firstAttr) + suffix+ "}";
      }
      else if (element instanceof BnfFileImpl) {
        return ((BnfFileImpl)element).getName();
      }
      return "" + element;
    }

    private static String getAttrDisplayName(BnfAttr attr) {
      final BnfAttrPattern attrPattern = attr.getAttrPattern();
      final BnfExpression attrValue = attr.getExpression();
      String attrValueText = attrValue == null? "" : attrValue instanceof BnfValueList? "[ ... ]" : attrValue.getText();
      return attr.getName() + (attrPattern == null ? "" : attrPattern.getText()) + " = " + attrValueText;
    }

    @Override
    public Icon getIcon(boolean open) {
      PsiElement element = getElement();
      if (element == null) return null;
      return element instanceof BnfAttrs ? PlatformIcons.PACKAGE_ICON : element.getIcon(0);
    }
  }
}
