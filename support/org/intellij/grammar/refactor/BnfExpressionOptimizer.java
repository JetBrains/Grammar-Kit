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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfElementFactory;

import java.util.LinkedList;

/**
 * @author gregsh
 */
public class BnfExpressionOptimizer {
  public static void optimize(PsiElement element) {
    final LinkedList<PsiElement> list = new LinkedList<PsiElement>();
    list.add(element.getParent());
    list.add(element);
    while (!list.isEmpty()) {
      PsiElement cur = list.removeLast();
      PsiElement parent = cur.getParent();
      if (isTrivial(cur)) {
        list.add(cur.replace(PsiTreeUtil.getChildOfType(cur, BnfExpression.class)));
      }
      else if (cur instanceof BnfParenOptExpression && isTrivialOrSingular(((BnfParenOptExpression)cur).getExpression())) {
        // currently <expr> + ? expressions are not supported, thus:
        BnfExpression child = ((BnfParenOptExpression)cur).getExpression();
        IElementType type = ParserGeneratorUtil.getEffectiveType(child);
        if (type == BnfTypes.BNF_OP_OPT || type == BnfTypes.BNF_OP_ZEROMORE) {
          list.add(cur.replace(child));
        }
        else if (type == BnfTypes.BNF_OP_ONEMORE) {
          String replacement = ((BnfQuantified)child).getExpression().getText() + "*";
          list.add(cur.replace(BnfElementFactory.createExpressionFromText(element.getProject(), replacement)));
        }
        else {
          String replacement = child.getText() + "?";
          list.add(cur.replace(BnfElementFactory.createExpressionFromText(element.getProject(), replacement)));
        }
      }
      else if (cur instanceof BnfChoice &&
               !(parent instanceof BnfParenthesized) &&
               (parent instanceof BnfSequence || parent instanceof BnfQuantified)) {
        String replacement = "(" + cur.getText() + ")";
        cur.replace(BnfElementFactory.createExpressionFromText(element.getProject(), replacement));
      }
      else if (canBeMergedInto(cur, parent)) {
        mergeChildrenTo(parent, cur, list);
      }
      else if (cur instanceof BnfQuantified && ((BnfQuantified)cur).getExpression() instanceof BnfQuantified) {
        BnfQuantified child = (BnfQuantified)((BnfQuantified)cur).getExpression();
        IElementType type1 = ParserGeneratorUtil.getEffectiveType(cur);
        IElementType type2 = ParserGeneratorUtil.getEffectiveType(child);
        if (type1 == type2) {
          list.add(cur.replace(child));
        }
        else if (type1 == BnfTypes.BNF_OP_OPT && type2 == BnfTypes.BNF_OP_ONEMORE ||
                 type2 == BnfTypes.BNF_OP_OPT && type1 == BnfTypes.BNF_OP_ONEMORE ||
                 type1 == BnfTypes.BNF_OP_ZEROMORE || type2 == BnfTypes.BNF_OP_ZEROMORE
          ) {
          String childText = child.getExpression().getText();
          String replacement = (child instanceof BnfParenthesized? "(" + childText + ")" : childText) + "*";
          cur.replace(BnfElementFactory.createExpressionFromText(element.getProject(), replacement));
        }
      }
    }
  }

  private static boolean canBeMergedInto(PsiElement cur, PsiElement parent) {
    if (cur instanceof BnfSequence && (parent instanceof BnfSequence || parent instanceof BnfChoice)) return true;
    if (cur instanceof BnfChoice && parent instanceof BnfChoice) return true;
    return false;
  }

  private static void mergeChildrenTo(PsiElement parent, PsiElement cur, LinkedList<PsiElement> list) {
    PsiElement last = cur.getLastChild();
    cur = unwrap(parent, cur.getFirstChild(), last, cur);
    while (cur != null) {
      list.add(cur);
      if (cur == last) break;
      cur = cur.getNextSibling();
    }
  }

  private static PsiElement unwrap(PsiElement parent, PsiElement first, PsiElement last, PsiElement from) {
    while (first != last && first instanceof PsiWhiteSpace) {
      first = first.getNextSibling();
    }
    while (last != first && last instanceof PsiWhiteSpace) {
      last = last.getPrevSibling();
    }
    if (first == null || last == null || first == last && last instanceof PsiWhiteSpace) return null;

    PsiElement result = parent.addRangeBefore(first, last, from);
    from.delete();
    return result;
  }

  private static boolean isTrivial(PsiElement element) {
    return element instanceof BnfParenthesized && element.getParent() instanceof BnfRule && !(element instanceof BnfQuantified || element instanceof BnfChoice) ||
           element instanceof BnfParenExpression && (canBeMergedInto(((BnfParenExpression)element).getExpression(), element.getParent()) ||
                                                     element.getParent() instanceof BnfParenthesized || isTrivialOrSingular(((BnfParenExpression)element).getExpression())) ||
           element instanceof BnfSequence && ( ((BnfSequence)element).getExpressionList().size() == 1) ||
           element instanceof BnfChoice && ( ((BnfChoice)element).getExpressionList().size() == 1)
      ;
  }

  private static boolean isTrivialOrSingular(PsiElement element) {
    return element instanceof BnfReferenceOrToken || element instanceof BnfLiteralExpression ||
           element instanceof BnfParenthesized || element instanceof BnfQuantified ||
           isTrivial(element)
      ;
  }
}
