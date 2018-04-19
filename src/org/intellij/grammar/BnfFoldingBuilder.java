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

package org.intellij.grammar;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author gregsh
 */
public class BnfFoldingBuilder extends CustomFoldingBuilder {

  @Override
  protected void buildLanguageFoldRegions(@NotNull List<FoldingDescriptor> result,
                                          @NotNull PsiElement root,
                                          @NotNull Document document,
                                          boolean quick) {
    if (!(root instanceof BnfFile)) return;
    BnfFile file = (BnfFile)root;

    for (BnfAttrs attrs : file.getAttributes()) {
      TextRange textRange = attrs.getTextRange();
      if (textRange.getLength() <= 2) continue;
      result.add(new FoldingDescriptor(attrs, textRange));
      for (BnfAttr attr : attrs.getAttrList()) {
        BnfExpression attrValue = attr.getExpression();
        if (attrValue instanceof BnfValueList && attrValue.getTextLength() > 2) {
          result.add(new FoldingDescriptor(attrValue, attrValue.getTextRange()));
        }
      }
    }
    for (BnfRule rule : file.getRules()) {
      //result.add(new FoldingDescriptor(rule, rule.getTextRange()));
      BnfAttrs attrs = rule.getAttrs();
      if (attrs != null) {
        result.add(new FoldingDescriptor(attrs, attrs.getTextRange()));
      }
    }
    if (!quick) {
      PsiTreeUtil.processElements(file, element -> {
        if (element.getNode().getElementType() == BnfParserDefinition.BNF_BLOCK_COMMENT) {
          result.add(new FoldingDescriptor(element, element.getTextRange()));
        }
        return true;
      });
    }
  }

  @Override
  protected String getLanguagePlaceholderText(@NotNull ASTNode node, @NotNull TextRange range) {
    PsiElement psi = node.getPsi();
    if (psi instanceof BnfAttrs) return "{..}";
    if (psi instanceof BnfRule) return ((BnfRule)psi).getName() + " ::= ...";
    if (psi instanceof BnfValueList) return "[..]";
    if (node.getElementType() == BnfParserDefinition.BNF_BLOCK_COMMENT) return "/*..*/";
    return null;
  }

  @Override
  protected boolean isRegionCollapsedByDefault(@NotNull ASTNode node) {
    PsiElement psi = node.getPsi();
    return psi instanceof BnfValueList ||
           psi instanceof BnfAttrs && !(psi.getParent() instanceof BnfRule);
  }
}
