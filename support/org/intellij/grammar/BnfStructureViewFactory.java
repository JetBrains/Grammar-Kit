/*
 * Copyright 2011-2011 Gregory Shrago
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
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.navigation.ItemPresentation;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.PlatformIcons;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfFileImpl;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gregsh
 */
public class BnfStructureViewFactory implements PsiStructureViewFactory {
  public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
    return new TreeBasedStructureViewBuilder() {
      @NotNull
      public StructureViewModel createStructureViewModel() {
        return new Model(psiFile);
      }

      @Override
      public boolean isRootNodeShown() {
        return false;
      }
    };
  }

  public static class Model extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider {

    protected Model(@NotNull PsiFile psiFile) {
      super(psiFile, new Element(psiFile));
      withSuitableClasses(BnfRule.class, BnfAttrs.class, BnfAttr.class);
    }


    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
      return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
      final Object value = element.getValue();
      return !(value instanceof BnfRule);
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

  public static class Element implements StructureViewTreeElement, ItemPresentation {

    private final PsiElement myElement;

    public Element(PsiElement element) {
      this.myElement = element;
    }

    @Override
    public Object getValue() {
      return myElement;
    }

    @Override
    public void navigate(boolean requestFocus) {
      ((Navigatable)myElement).navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
      return ((Navigatable)myElement).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
      return ((Navigatable)myElement).canNavigateToSource();
    }

    @Override
    public ItemPresentation getPresentation() {
      return this;
    }

    @Override
    public TreeElement[] getChildren() {
      if (myElement instanceof BnfRule
          || myElement instanceof BnfAttr) {
        return EMPTY_ARRAY;
      }
      final ArrayList<TreeElement> result = new ArrayList<TreeElement>();
      if (myElement instanceof BnfFile) {
        for (BnfAttrs o : ((BnfFile)myElement).getAttributes()) {
          result.add(new Element(o));
        }
        for (BnfRule o : ((BnfFile)myElement).getRules()) {
          result.add(new Element(o));
        }
      }
      else if (myElement instanceof BnfAttrs) {
        for (BnfAttr o : ((BnfAttrs)myElement).getAttrList()) {
          result.add(new Element(o));
        }
      }
      return result.toArray(new TreeElement[result.size()]);
    }

    @Override
    public String getPresentableText() {
      if (myElement instanceof BnfRule) {
        return ((PsiNamedElement)myElement).getName();
      }
      else if (myElement instanceof BnfAttr) {
        return getAttrDisplayName((BnfAttr)myElement);
      }
      else if (myElement instanceof BnfAttrs) {
        List<BnfAttr> attrList = ((BnfAttrs)myElement).getAttrList();
        final BnfAttr firstAttr = ContainerUtil.getFirstItem(attrList);
        if (firstAttr == null) return "Attributes { <empty> }";
        String suffix = attrList.size() > 1? " & " + attrList.size()+" more..." : " ";
        return "Attributes { " + getAttrDisplayName(firstAttr) + suffix+ "}";
      }
      else if (myElement instanceof BnfFileImpl) {
        return ((BnfFileImpl)myElement).getName();
      }
      throw new AssertionError(myElement.getClass().getName());
    }

    private static String getAttrDisplayName(BnfAttr attr) {
      final BnfAttrPattern attrPattern = attr.getAttrPattern();
      final BnfExpression attrValue = attr.getExpression();
      String attrValueText = attrValue == null? "" : attrValue instanceof BnfValueList? "[ ... ]" : attrValue.getText();
      return attr.getName() + (attrPattern == null ? "" : attrPattern.getText()) + " = " + attrValueText;
    }

    @Override
    public String getLocationString() {
      return null;
    }

    @Override
    public Icon getIcon(boolean open) {
      return myElement instanceof BnfAttrs ? PlatformIcons.PACKAGE_ICON : myElement.getIcon(0);
    }
  }
}
