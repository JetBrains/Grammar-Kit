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

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringActionHandler;
import gnu.trove.THashSet;
import org.intellij.grammar.psi.BnfAttrs;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfReferenceOrToken;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.impl.BnfElementFactory;
import org.intellij.grammar.psi.impl.BnfFileImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/16/11
 * Time: 5:25 PM
 *
 * @author Vadim Romansky
 * @author gregsh
 */
public class BnfIntroduceRuleHandler implements RefactoringActionHandler {
  public static final String REFACTORING_NAME = "Extract Rule";

  @Override
  public void invoke(@NotNull final Project project, final Editor editor, PsiFile file, @Nullable DataContext dataContext) {
    if (!(file instanceof BnfFileImpl)) return;

    final BnfFileImpl bnfFile = (BnfFileImpl)file;
    final SelectionModel selectionModel = editor.getSelectionModel();
    int startOffset = selectionModel.getSelectionStart();
    int endOffset = selectionModel.getSelectionEnd();
    final BnfRule currentRule = PsiTreeUtil.getParentOfType(file.findElementAt(startOffset), BnfRule.class);
    if (currentRule == null) return;
    final BnfExpression parentExpression = findParentExpression(bnfFile, startOffset, endOffset - 1);
    final TextRange fixedRange = calcFixedRange(parentExpression, startOffset, endOffset);
    if (fixedRange == null) return;
    final BnfRule ruleFromText = BnfElementFactory.createRuleFromText(file.getProject(), "a ::= " + fixedRange.substring(file.getText()));
    BnfExpressionOptimizer.optimize(ruleFromText.getExpression());

    new WriteCommandAction.Simple(project, REFACTORING_NAME, file) {
      @Override
      public void run() {
        rewriteFile(project, editor, currentRule, ruleFromText.getExpression(), fixedRange);
      }
    }.execute();
  }

  @Override
  public void invoke(final @NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
    // do not support this case
  }


  private void rewriteFile(final Project project,
                           Editor editor,
                           @NotNull BnfRule currentRule,
                           BnfExpression expr,
                           TextRange fixedRange) {
    final PsiFile containingFile = currentRule.getContainingFile();
    String newRuleName = choseRuleName(containingFile);
    String newRuleText = "private  " + newRuleName + " ::= " + expr.getText();

    PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
    Document document = editor.getDocument();
    psiDocumentManager.doPostponedOperationsAndUnblockDocument(document);
    int newRuleOffset = document.getLineEndOffset(document.getLineNumber(currentRule.getTextRange().getEndOffset()));
    document.insertString(newRuleOffset, "\n" + newRuleText);
    document.replaceString(fixedRange.getStartOffset(), fixedRange.getEndOffset(), newRuleName);
    newRuleOffset += newRuleName.length() - fixedRange.getLength();
    psiDocumentManager.commitDocument(document);

    BnfRule addedRule = PsiTreeUtil.getParentOfType(containingFile.findElementAt(newRuleOffset + 2), BnfRule.class, false);
    BnfExpression expression = PsiTreeUtil.getParentOfType(containingFile.findElementAt(fixedRange.getStartOffset()), BnfExpression.class, false);

    final BnfExpression parentExpression =
      PsiTreeUtil.getParentOfType(containingFile.findElementAt(fixedRange.getStartOffset()), BnfExpression.class);
    BnfExpressionOptimizer.optimize(parentExpression);

    psiDocumentManager.doPostponedOperationsAndUnblockDocument(document);
    final BnfIntroduceRulePopup popup = new BnfIntroduceRulePopup(project, editor, addedRule, expression);

    editor.getCaretModel().moveToOffset(addedRule.getTextOffset());
    popup.performInplaceRename();
  }

  private String choseRuleName(PsiFile containingFile) {
    final Set<String> existingNames = new THashSet<String>();
    containingFile.accept(new PsiRecursiveElementWalkingVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        if (element instanceof BnfAttrs) return; 
        if (element instanceof BnfReferenceOrToken) {
          existingNames.add(((BnfReferenceOrToken)element).getId().getText());
        }
        else if (element instanceof BnfRule) {
          existingNames.add(((BnfRule)element).getId().getText());
        }
        super.visitElement(element);
      }
    });
    String name = "newRule";
    for (int i = 1; existingNames.contains(name); i++) {
      name = "newRule" + i;
    }
    return name;
  }

  @Nullable
  private static TextRange calcFixedRange(BnfExpression expression, int startOffset, int endOffset) {
    if (expression == null) return null;
    boolean expressionFound = false;
    for (PsiElement child = expression.getFirstChild(); child != null; child = child.getNextSibling()) {
      final TextRange textRange = child.getTextRange();
      if (!expressionFound && (child instanceof BnfExpression)) expressionFound = true;
      if (textRange.containsOffset(startOffset)) {
        startOffset = textRange.getStartOffset();
      }
      if (textRange.containsOffset(endOffset)) {
        endOffset = textRange.getEndOffset();
        break;
      }
    }
    if (!expressionFound) return expression.getTextRange();
    return new TextRange(startOffset, endOffset);
  }

  @Nullable
  private static BnfExpression findParentExpression(BnfFileImpl file, int startOffset, int endOffset) {
    final PsiElement startElement = file.findElementAt(startOffset);
    final PsiElement endElement = file.findElementAt(endOffset);
    if (startElement == null || endElement == null) return null;
    final PsiElement commonParent = PsiTreeUtil.findCommonParent(startElement, endElement);
    return PsiTreeUtil.getParentOfType(commonParent, BnfExpression.class, false);
  }
}
