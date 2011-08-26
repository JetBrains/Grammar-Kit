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

package org.intellij.grammar.refactor;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.refactoring.introduce.inplace.InplaceVariableIntroducer;
import com.intellij.ui.NonFocusableCheckBox;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfRule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Vadim Romansky
 * @author gregsh
 */
public class BnfIntroduceRulePopup extends InplaceVariableIntroducer<BnfExpression> {

  private final JPanel myPanel = new JPanel(new GridBagLayout());
  private final JCheckBox myCheckBox = new NonFocusableCheckBox("Declare private");


  public BnfIntroduceRulePopup(Project project, Editor editor, PsiNamedElement elementToRename, BnfExpression expr) {
    super(elementToRename, editor, project, "Introduce Rule", new BnfExpression[0], expr);

    myCheckBox.setSelected(true);
    myCheckBox.setMnemonic('p');

    myPanel.setBorder(null);
    myPanel.add(myCheckBox, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

    myPanel.add(Box.createVerticalBox(), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
  }

  @Override
  protected BnfRule getVariable() {
    return (BnfRule)super.getVariable();
  }

  @Override
  protected void moveOffsetAfter(boolean success) {
    RangeMarker exprMarker = getExprMarker();
    if (success) {
      if (exprMarker != null && exprMarker.isValid()) {
        myEditor.getCaretModel().moveToOffset(exprMarker.getEndOffset());
        myEditor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
        exprMarker.dispose();
      }
      BnfRule rule = getVariable();
      if (rule == null) return;
      Document document = myEditor.getDocument();
      final TextRange textRange = rule.getId().getTextRange();
      document.deleteString(textRange.getStartOffset() - 1, textRange.getStartOffset());
      PsiDocumentManager.getInstance(myProject).commitDocument(document);
    }
    else {
      if (exprMarker != null && exprMarker.isValid()) {
        myEditor.getCaretModel().moveToOffset(exprMarker.getStartOffset());
        myEditor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
        exprMarker.dispose();
      }
      // todo restore original expression
    }
  }

  @Override
  protected JComponent getComponent() {
    myCheckBox.addActionListener(new ActionListener() {
      final BnfPrivateListener privateListener = new BnfPrivateListener(myEditor);

      @Override
      public void actionPerformed(ActionEvent e) {
        new WriteCommandAction(myProject, BnfIntroduceRuleHandler.REFACTORING_NAME, BnfIntroduceRuleHandler.REFACTORING_NAME) {
          @Override
          protected void run(Result result) throws Throwable {
            final BnfRule variable = getVariable();
            assert variable != null;
            privateListener.perform(myCheckBox.isSelected(), variable);
          }
        }.execute();
      }
    });
    return myPanel;
  }
}
