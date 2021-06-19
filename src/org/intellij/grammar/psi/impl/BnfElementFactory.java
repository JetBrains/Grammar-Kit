/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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

  private static PsiFile createFile(Project project, String text) {
    return PsiFileFactory.getInstance(project).createFileFromText("a.bnf", BnfLanguage.INSTANCE, text, false, false);
  }

  public static PsiElement createLeafFromText(Project project, String text) {
    return PsiTreeUtil.getDeepestFirst(createFile(project, text));
  }

  public static BnfExpression createExpressionFromText(Project project, String text) {
    return createRuleFromText(project, "a ::= " + text).getExpression();
  }

  public static BnfRule createRuleFromText(Project project, String text) {
    PsiElement firstChild = createFile(project, text).getFirstChild();
    LOG.assertTrue(firstChild instanceof BnfRule, text);
    return (BnfRule)firstChild;
  }

  public static BnfAttr createAttributeFromText(Project project, String text) {
    PsiElement firstChild = createFile(project, "{\n  " + text + "\n}").getFirstChild();
    LOG.assertTrue(firstChild instanceof BnfAttrs, text);
    List<BnfAttr> attrList = ((BnfAttrs) firstChild).getAttrList();
    LOG.assertTrue(attrList.size() == 1, text);
    return attrList.get(0);
  }

}
