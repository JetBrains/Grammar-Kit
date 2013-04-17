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

package org.intellij.grammar.refactor;

import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
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
  private static final String PRIVATE = "private ";

  private final JPanel myPanel = new JPanel(new GridBagLayout());
  private final JCheckBox myCheckBox = new NonFocusableCheckBox("Declare private");

  public BnfIntroduceRulePopup(Project project, Editor editor, BnfRule rule, BnfExpression expr) {
    super(rule, editor, project, "Introduce Rule", new BnfExpression[0], expr);

    myCheckBox.setSelected(true);
    myCheckBox.setMnemonic('p');

    myPanel.setBorder(null);
    myPanel.add(myCheckBox, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    myPanel.add(Box.createVerticalBox(), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
  }

  @Override
  protected void moveOffsetAfter(boolean success) {
    RangeMarker exprMarker = getExprMarker();
    final AccessToken accessToken = WriteAction.start();
    try {
      Document document = myEditor.getDocument();
      if (success) {
        int exprOffset = myExprMarker.getStartOffset();
        int lineOffset = getLineOffset(document, exprOffset);
        // todo greedy-to-left range markers workaround ??
        int idx = document.getText().indexOf(PRIVATE, lineOffset);
        idx = idx > -1 && idx < exprOffset? idx + PRIVATE.length() : document.getText().indexOf(" ", lineOffset);
        if (idx > -1 && idx < exprOffset) {
          document.deleteString(idx, idx + 1);
        }
      }
      else {
        // todo restore original expression
      }
      PsiDocumentManager.getInstance(myProject).commitDocument(document);
      if (exprMarker != null && exprMarker.isValid()) {
        myEditor.getCaretModel().moveToOffset(exprMarker.getStartOffset());
        myEditor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
        exprMarker.dispose();
      }
    }
    finally {
      accessToken.finish();
    }
  }

  @Override
  protected JComponent getComponent() {
    myCheckBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        new WriteCommandAction(myProject, BnfIntroduceRuleHandler.REFACTORING_NAME, BnfIntroduceRuleHandler.REFACTORING_NAME) {
          @Override
          protected void run(Result result) throws Throwable {
            perform(myCheckBox.isSelected());
          }
        }.execute();
      }
    });
    return myPanel;
  }

  public void perform(final boolean generatePrivate) {
    final Runnable runnable = new Runnable() {
      public void run() {
        final Document document = myEditor.getDocument();

        int exprOffset = myExprMarker.getStartOffset();
        final int lineOffset = getLineOffset(document, exprOffset);
        if (generatePrivate) {
          document.insertString(lineOffset, PRIVATE);
        }
        else {
          int idx = document.getText().indexOf(PRIVATE, lineOffset);
          if (idx > -1 && idx < exprOffset) {
            document.deleteString(idx, idx + PRIVATE.length());
          }
        }
        PsiDocumentManager.getInstance(myProject).commitDocument(document);
      }
    };
    final LookupImpl lookup = (LookupImpl)LookupManager.getActiveLookup(myEditor);
    if (lookup != null) {
      lookup.performGuardedChange(runnable);
    }
    else {
      runnable.run();
    }
  }

  private int getLineOffset(Document document, final int offset) {
    return document.getLineStartOffset(document.getLineNumber(offset));
  }
}
