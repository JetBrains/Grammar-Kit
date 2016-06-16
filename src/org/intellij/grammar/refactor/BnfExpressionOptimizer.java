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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfElementFactory;
import org.intellij.grammar.psi.impl.GrammarUtil;

import java.util.LinkedList;
import java.util.List;

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
        mergeChildrenTo(parent, cur, list);
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
      else if (isOptMany(cur) && isOptMany(PsiTreeUtil.getChildOfType(cur, BnfExpression.class))) {
        BnfExpression child = PsiTreeUtil.getChildOfType(cur, BnfExpression.class);
        IElementType type1 = ParserGeneratorUtil.getEffectiveType(cur);
        IElementType type2 = ParserGeneratorUtil.getEffectiveType(child);
        if (type1 == type2) {
          list.add(cur.replace(child));
        }
        else if (type1 == BnfTypes.BNF_OP_OPT && type2 == BnfTypes.BNF_OP_ONEMORE ||
                 type2 == BnfTypes.BNF_OP_OPT && type1 == BnfTypes.BNF_OP_ONEMORE ||
                 type1 == BnfTypes.BNF_OP_ZEROMORE || type2 == BnfTypes.BNF_OP_ZEROMORE
          ) {
          BnfExpression childOfChild = PsiTreeUtil.getChildOfType(child, BnfExpression.class);
          String childText = childOfChild == null? "" : childOfChild.getText();
          String replacement = (child instanceof BnfParenthesized? "(" + childText + ")" : childText) + "*";
          cur.replace(BnfElementFactory.createExpressionFromText(element.getProject(), replacement));
        }
      }
    }
  }

  private static boolean isOptMany(PsiElement cur) {
    return cur instanceof BnfQuantified || cur instanceof BnfParenOptExpression;
  }

  private static boolean canBeMergedInto(PsiElement cur, PsiElement parent) {
    if (cur instanceof BnfSequence) {
      if (parent instanceof BnfChoice) return true;
      if (parent instanceof BnfSequence) {
        List<BnfExpression> list = ((BnfSequence)parent).getExpressionList();
        return list.isEmpty() || !GrammarUtil.isExternalReference(list.get(0));
      }
    }
    if (cur instanceof BnfChoice && parent instanceof BnfChoice) return true;
    return false;
  }

  private static void mergeChildrenTo(PsiElement parent, PsiElement cur, LinkedList<PsiElement> list) {
    boolean skipParens = cur instanceof BnfParenthesized;
    PsiElement last = cur.getLastChild();
    PsiElement first = cur.getFirstChild();
    if (skipParens) {
      last = last.getPrevSibling();
      first = first.getNextSibling();
    }
    cur = unwrap(parent, first, last, cur);
    while (cur != null) {
      if (cur instanceof BnfExpression) list.add(cur);
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
    PsiElement parent = element.getParent();
    if (element instanceof BnfParenthesized && parent instanceof BnfRule && !(isOptMany(element) || element instanceof BnfChoice)) {
      return true;
    }
    else if (element instanceof BnfParenExpression &&
             (canBeMergedInto(((BnfParenExpression)element).getExpression(), parent) ||
              parent instanceof BnfParenthesized ||
              isTrivialOrSingular(((BnfParenExpression)element).getExpression()))) {
      return true;
    }
    else if (element instanceof BnfSequence && (((BnfSequence)element).getExpressionList().size() == 1 || parent instanceof BnfSequence)) {
      return true;
    }
    else if (element instanceof BnfChoice && (((BnfChoice)element).getExpressionList().size() == 1 || parent instanceof BnfChoice)) {
      return true;
    }
    return false;
  }

  private static boolean isTrivialOrSingular(PsiElement element) {
    return element instanceof BnfReferenceOrToken || element instanceof BnfLiteralExpression ||
           element instanceof BnfParenthesized || element instanceof BnfQuantified ||
           isTrivial(element)
      ;
  }
}
