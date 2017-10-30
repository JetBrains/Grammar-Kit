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

package org.intellij.grammar.inspection;

import com.intellij.codeInspection.*;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

import static com.intellij.openapi.util.Condition.NOT_NULL;
import static org.intellij.grammar.KnownAttribute.RECOVER_WHILE;
import static org.intellij.grammar.KnownAttribute.getCompatibleAttribute;
import static org.intellij.grammar.generator.ParserGeneratorUtil.findAttribute;
import static org.intellij.grammar.psi.impl.GrammarUtil.bnfTraverser;
import static org.intellij.grammar.psi.impl.GrammarUtil.bnfTraverserNoAttrs;

/**
 * @author gregsh
 */
public class BnfUnusedRuleInspection extends LocalInspectionTool {

  private static final Function<PsiElement, BnfRule> RESOLVER = o -> {
    if (!(o instanceof BnfReferenceOrToken) && !(o instanceof BnfStringLiteralExpression)) return null;
    PsiReference reference = o.getReference();
    PsiElement target = reference != null ? reference.resolve() : null;
    return target instanceof BnfRule ? (BnfRule)target : null;
  };

  @Override
  public boolean runForWholeFile() {
    return true;
  }

  @Nullable
  @Override
  public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
    if (!(file instanceof BnfFile)) return null;
    if (SuppressionUtil.inspectionResultSuppressed(file, this)) return null;
    BnfFile myFile = (BnfFile)file;
    ProblemsHolder holder = new ProblemsHolder(manager, file, isOnTheFly);

    //noinspection LimitedScopeInnerClass,EmptyClass
    abstract class Cond<T> extends JBIterable.Stateful<Cond> implements Condition<T> { }

    Set<BnfRule> inExpr = ContainerUtil.newTroveSet();
    final Set<BnfRule> inParsing = ContainerUtil.newTroveSet();
    Map<BnfRule, String> inAttrs = ContainerUtil.newTroveMap();

    bnfTraverserNoAttrs(myFile).traverse().transform(RESOLVER).filter(NOT_NULL).addAllTo(inExpr);

    for (int size = 0, prev = -1; size != prev; prev = size, size = inParsing.size()) {
      bnfTraverserNoAttrs(myFile).expand(new Cond<PsiElement>() {
        @Override
        public boolean value(PsiElement element) {
          if (element instanceof BnfRule) {
            BnfRule rule = (BnfRule)element;
            if (inParsing.isEmpty()) inParsing.add(rule);
            // add recovery rules to calculation
            BnfAttr recoverAttr = findAttribute(rule, KnownAttribute.RECOVER_WHILE);
            value(recoverAttr == null ? null : recoverAttr.getExpression());
            return inParsing.contains(rule);
          }
          else if (element instanceof BnfReferenceOrToken) {
            ContainerUtil.addIfNotNull(inParsing, ((BnfReferenceOrToken)element).resolveRule());
            return false;
          }
          return true;
        }
      }).traverse().size();
    }

    for (BnfAttr attr : bnfTraverser(myFile).filter(BnfAttr.class)) {
      BnfRule target = RESOLVER.fun(attr.getExpression());
      if (target != null) inAttrs.put(target, attr.getName());
    }

    boolean first = true;
    for (BnfRule r : myFile.getRules()) {
      if (first) {
        first = false;
        continue;
      }
      String message = null;
      boolean fake = ParserGeneratorUtil.Rule.isFake(r);
      if (fake) {
        if (!inAttrs.containsKey(r)) message = "Unused fake rule";
      }
      else {
        if (getCompatibleAttribute(inAttrs.get(r)) == RECOVER_WHILE) {
          if (!ParserGeneratorUtil.Rule.isPrivate(r)) {
            message = "Non-private recovery rule";
          }
        }
        else if (!inExpr.contains(r)) {
          message = "Unused rule";
        }
        else if (!inParsing.contains(r)) {
          message = "Unreachable rule";
        }
      }
      if (message != null && !SuppressionUtil.inspectionResultSuppressed(r, this)) {
        holder.registerProblem(r.getId(), message);
      }
    }
    return holder.getResultsArray();
  }

}
