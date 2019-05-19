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

package org.intellij.jflex.editor;

import com.intellij.ide.structureView.*;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.util.Function;
import com.intellij.util.containers.JBIterable;
import org.intellij.jflex.psi.*;
import org.intellij.jflex.psi.impl.JFlexFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static com.intellij.openapi.util.Conditions.instanceOf;
import static com.intellij.openapi.util.text.StringUtil.*;

/**
 * @author gregsh
 */
public class JFlexStructureViewFactory implements PsiStructureViewFactory {
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

  static class MyModel extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider {

    static final Class[] CLASSES = {JFlexOption.class, JFlexMacroDefinition.class, JFlexRule.class};

    protected MyModel(@NotNull PsiFile psiFile) {
      super(psiFile, new MyElement(psiFile));
      withSuitableClasses(CLASSES);
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
      return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
      Object o = element.getValue();
      return o instanceof JFlexOption || o instanceof JFlexMacroDefinition;
    }

    @Override
    public boolean shouldEnterElement(Object element) {
      return false;
    }

  }

  static final Function<PsiElement, StructureViewTreeElement> WRAPPER = MyElement::new;

  static class MyElement extends PsiTreeElementBase<PsiElement> implements SortableTreeElement {

    MyElement(PsiElement element) {
      super(element);
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
      return notNullize(getPresentableText());
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
      PsiElement o = getElement();
      if (o == null) return Collections.emptyList();
      if (o instanceof JFlexFile) {
        return SyntaxTraverser.psiTraverser(o)
          .expand(instanceOf(JFlexFile.class, JFlexFileSection.class))
          .traverse()
          .filter(instanceOf(MyModel.CLASSES))
          .map(MyElement::new)
          .addAllTo(new ArrayList<>());
      }
      else if (o instanceof JFlexRule) {
        return SyntaxTraverser.psiApi().children(o)
          .filter(instanceOf(MyModel.CLASSES))
          .map(MyElement::new)
          .addAllTo(new ArrayList<>());
      }
      return Collections.emptyList();
    }

    @Override
    public String getPresentableText() {
      PsiElement o = getElement();
      if (o == null) return null;
      if (o instanceof JFlexFile) {
        return ((JFlexFile)o).getName();
      }
      else if (o instanceof JFlexOption) {
        return trimEnd(o.getFirstChild().getText(), "{");
      }
      else if (o instanceof JFlexMacroDefinition) {
        return ((JFlexMacroDefinition)o).getName();
      }
      else if (o instanceof JFlexRule) {
        JFlexStateList states = ((JFlexRule)o).getStateList();
        JFlexExpression expr = ((JFlexRule)o).getExpression();
        StringBuilder sb = new StringBuilder();
        if (states != null) {
          sb.append("<");
          sb.append(join(JBIterable.from(states.getStateReferenceList()).map(PsiElement::getText), ", "));
          sb.append(">");
        }
        if (expr != null) {
          if (states != null) sb.append(" ");
          sb.append(firstLast(expr.getText(), 40));
        }
        return sb.toString();
      }
      return o.getClass().getSimpleName();
    }

    @Override
    public Icon getIcon(boolean open) {
      PsiElement o = getElement();
      if (o == null) return null;
      return o.getIcon(0);
    }
  }
}
