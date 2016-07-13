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

package org.intellij.grammar.refactor;

import com.intellij.BundleBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.ElementDescriptionUtil;
import com.intellij.psi.PsiReference;
import com.intellij.refactoring.inline.InlineOptionsDialog;
import com.intellij.usageView.UsageViewNodeTextLocation;
import org.intellij.grammar.psi.BnfRule;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/11/11
 * Time: 4:04 PM
 *
 * @author Vadim Romansky
 */
public class InlineRuleDialog extends InlineOptionsDialog {
  private final PsiReference myReference;

  private final BnfRule myRule;

  public InlineRuleDialog(Project project, BnfRule rule, PsiReference ref) {
    super(project, true, rule);
    myRule = rule;
    myReference = ref;
    myInvokedOnReference = myReference != null;

    setTitle("Inline Rule");

    init();
  }

  protected String getNameLabelText() {
    return ElementDescriptionUtil.getElementDescription(myElement, UsageViewNodeTextLocation.INSTANCE);
  }

  protected String getBorderTitle() {
    return "Inline";
  }

  protected String getInlineThisText() {
    return BundleBase.replaceMnemonicAmpersand("&This reference only and keep the rule");
  }

  protected String getInlineAllText() {
    return BundleBase.replaceMnemonicAmpersand("&All references and remove the rule");
  }

  protected boolean isInlineThis() {
    return false;
  }

  protected void doAction() {
    invokeRefactoring(new BnfInlineRuleProcessor(myRule, getProject(), myReference, isInlineThisOnly()));
  }

  protected void doHelpAction() {
  }
}
