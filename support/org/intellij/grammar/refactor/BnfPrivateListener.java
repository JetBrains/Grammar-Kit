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

import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/22/11
 * Time: 5:40 PM
 *
 * @author Vadim Romansky
 */
public class BnfPrivateListener {
  private final Editor myEditor;

  public BnfPrivateListener(Editor editor) {
    myEditor = editor;
  }

  public void perform(final boolean generatePrivate, @NotNull final BnfRule rule) {
    final Document document = myEditor.getDocument();
    final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(rule.getProject());
    documentManager.commitDocument(myEditor.getDocument());

    final Runnable runnable = new Runnable() {
      public void run() {
        PsiElement id = rule.getFirstChild();
        assert id != null;
        int startOffset = rule.getTextRange().getStartOffset();
        Document document = myEditor.getDocument();

        if (generatePrivate) {
          final int offset = document.getLineStartOffset(document.getLineNumber(startOffset));
          document.insertString(offset, "private ");
        }
        else {
          document.replaceString(startOffset, startOffset + "private ".length(), "");
        }
      }
    };
    final LookupImpl lookup = (LookupImpl)LookupManager.getActiveLookup(myEditor);
    if (lookup != null) {
      lookup.performGuardedChange(runnable);
    }
    else {
      runnable.run();
    }
    documentManager.commitDocument(document);
  }
}
