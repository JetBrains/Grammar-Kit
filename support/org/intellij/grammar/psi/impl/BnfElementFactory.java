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
package org.intellij.grammar.psi.impl;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.grammar.BnfLanguage;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfAttrs;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfRule;

import java.util.List;

/**
 * @author gregsh
 */
public class BnfElementFactory {

  private static final Logger LOG = Logger.getInstance("org.intellij.grammar.psi.impl.BnfElementFactory");

  private BnfElementFactory() {
  }

  public static PsiElement createLeafFromText(Project project, String text) {
    PsiFile fileFromText = PsiFileFactory.getInstance(project).createFileFromText("a.bnf", BnfLanguage.INSTANCE, text);
    return PsiTreeUtil.getDeepestFirst(fileFromText);
  }

  public static BnfExpression createExpressionFromText(Project project, String text) {
    return createRuleFromText(project, "a ::= " + text).getExpression();
  }

  public static BnfRule createRuleFromText(Project project, String text) {
    PsiFile fromText = PsiFileFactory.getInstance(project).createFileFromText("a.bnf", BnfLanguage.INSTANCE, text);
    PsiElement firstChild = fromText.getFirstChild();
    LOG.assertTrue(firstChild instanceof BnfRule, text);
    //noinspection ConstantConditions
    return (BnfRule)firstChild;
  }

  public static BnfAttr createAttributeFromText(Project project, String text) {
    PsiFile fromText = PsiFileFactory.getInstance(project).createFileFromText("a.bnf", BnfLanguage.INSTANCE, "{\n  " + text + "\n}" );
    PsiElement firstChild = fromText.getFirstChild();
    LOG.assertTrue(firstChild instanceof BnfAttrs, text);
    //noinspection ConstantConditions
    List<BnfAttr> attrList = ((BnfAttrs) firstChild).getAttrList();
    LOG.assertTrue(attrList.size() == 1, text);
    return attrList.get(0);
  }

}
