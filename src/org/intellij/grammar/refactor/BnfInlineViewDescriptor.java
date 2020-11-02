/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.refactor;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.usageView.UsageViewBundle;
import com.intellij.usageView.UsageViewDescriptor;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/11/11
 * Time: 8:50 PM
 *
 * @author Vadim Romansky
 */
public class BnfInlineViewDescriptor implements UsageViewDescriptor {
    private final BnfRule myElement;

    public BnfInlineViewDescriptor(BnfRule myElement) {
        this.myElement = myElement;
    }

    @NotNull
    public PsiElement[] getElements() {
      return new PsiElement[] {myElement};
    }

    public String getProcessedElementsHeader() {
      return "Rule";
    }

    public String getCodeReferencesText(int usagesCount, int filesCount) {
      return RefactoringBundle.message("invocations.to.be.inlined", UsageViewBundle.getReferencesString(usagesCount, filesCount));
    }

    public String getCommentReferencesText(int usagesCount, int filesCount) {
      return RefactoringBundle.message("comments.elements.header",
                                       UsageViewBundle.getOccurencesString(usagesCount, filesCount));
    }
}
