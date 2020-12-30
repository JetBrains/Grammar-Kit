/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.inspection;

import com.intellij.codeInspection.*;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfReferenceImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

import static org.intellij.grammar.KnownAttribute.RECOVER_WHILE;
import static org.intellij.grammar.KnownAttribute.getCompatibleAttribute;
import static org.intellij.grammar.generator.ParserGeneratorUtil.findAttribute;
import static org.intellij.grammar.generator.ParserGeneratorUtil.getAttribute;
import static org.intellij.grammar.psi.impl.GrammarUtil.bnfTraverser;
import static org.intellij.grammar.psi.impl.GrammarUtil.bnfTraverserNoAttrs;

/**
 * @author gregsh
 */
public class BnfUnusedRuleInspection extends LocalInspectionTool {

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
    JBIterable<BnfRule> rules = JBIterable.from(myFile.getRules());
    if (rules.isEmpty()) return null;
    
    ProblemsHolder holder = new ProblemsHolder(manager, file, isOnTheFly);

    Set<BnfRule> roots = new THashSet<>();
    Set<BnfRule> inExpr = new THashSet<>();
    Set<BnfRule> inParsing = new THashSet<>();
    Set<BnfRule> inSuppressed = new THashSet<>();
    Map<BnfRule, String> inAttrs = new THashMap<>();

    bnfTraverserNoAttrs(myFile).traverse()
      .filterMap(BnfUnusedRuleInspection::resolveRule)
      .addAllTo(inExpr);

    roots.add(rules.first());
    for (BnfRule rule : rules) {
      if (Boolean.TRUE.equals(getAttribute(rule, KnownAttribute.EXTRA_ROOT))) roots.add(rule);
      if (SuppressionUtil.inspectionResultSuppressed(rule, this)) inSuppressed.add(rule);
    }
    inParsing.addAll(roots);

    for (int size = 0, prev = -1; size != prev; prev = size, size = inParsing.size()) {
      bnfTraverserNoAttrs(myFile).expand(new Condition<>() {
        @Override
        public boolean value(PsiElement element) {
          if (element instanceof BnfRule) {
            BnfRule rule = (BnfRule)element;
            // add recovery rules to calculation
            BnfAttr recoverAttr = findAttribute(rule, KnownAttribute.RECOVER_WHILE);
            value(recoverAttr == null ? null : recoverAttr.getExpression());
            return inParsing.contains(rule) || inSuppressed.contains(rule);
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
      BnfRule target = resolveRule(attr.getExpression());
      if (target != null) inAttrs.put(target, attr.getName());
    }

    for (BnfRule r : rules.filter(o -> !roots.contains(o) && !inSuppressed.contains(o))) {
      String message = null;
      if (ParserGeneratorUtil.Rule.isFake(r)) {
        if (inExpr.contains(r)) {
          message = "Reachable fake rule";
        }
        else if (!inAttrs.containsKey(r)) {
          message = "Unused fake rule";
        }
      }
      else if (getCompatibleAttribute(inAttrs.get(r)) == RECOVER_WHILE) {
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
      if (message != null) {
        holder.registerProblem(r.getId(), message);
      }
    }
    return holder.getResultsArray();
  }

  @Nullable
  private static BnfRule resolveRule(@Nullable PsiElement o) {
    if (!(o instanceof BnfReferenceOrToken ||
          o instanceof BnfStringLiteralExpression)) return null;
    PsiReference reference = ContainerUtil.findInstance(o.getReferences(), BnfReferenceImpl.class);
    PsiElement target = reference != null ? reference.resolve() : null;
    return target instanceof BnfRule ? (BnfRule)target : null;
  }
}
