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

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory;
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.intellij.openapi.util.Condition.NOT_NULL;
import static org.intellij.grammar.KnownAttribute.*;
import static org.intellij.grammar.generator.ParserGeneratorUtil.findAttribute;
import static org.intellij.grammar.psi.impl.GrammarUtil.bnfTraverser;
import static org.intellij.grammar.psi.impl.GrammarUtil.bnfTraverserNoAttrs;
/**
 * @author gregsh
 */
public class BnfUnusedRulePassFactory extends AbstractProjectComponent implements TextEditorHighlightingPassFactory {

  public BnfUnusedRulePassFactory(Project project, TextEditorHighlightingPassRegistrar highlightingPassRegistrar) {
    super(project);
    highlightingPassRegistrar.registerTextEditorHighlightingPass(this, new int[]{Pass.UPDATE_ALL,}, null, true, -1);
  }

  @NonNls
  @NotNull
  public String getComponentName() {
    return "BnfUnusedRulePassFactory";
  }

  @Nullable
  public TextEditorHighlightingPass createHighlightingPass(@NotNull PsiFile file, @NotNull final Editor editor) {
    return file instanceof BnfFile? new MyPass(myProject, (BnfFile)file, editor.getDocument()) : null;
  }

  private static final Function<PsiElement, BnfRule> RESOLVER = new Function<PsiElement, BnfRule>() {
    @Override
    public BnfRule fun(PsiElement o) {
      if (!(o instanceof BnfReferenceOrToken) && !(o instanceof BnfStringLiteralExpression)) return null;
      PsiReference reference = o.getReference();
      PsiElement target = reference != null ? reference.resolve() : null;
      return target instanceof BnfRule ? (BnfRule)target : null;
    }
  };

  static class MyPass extends TextEditorHighlightingPass {

    private final BnfFile myFile;
    private final List<HighlightInfo> myHighlights = new ArrayList<HighlightInfo>();

    MyPass(Project myProject, BnfFile file, Document document) {
      super(myProject, document, true);
      myFile = file;
    }

    @Override
    public void doCollectInformation(@NotNull ProgressIndicator progress) {
      Set<BnfRule> inExpr = ContainerUtil.newTroveSet();
      final Set<BnfRule> inParsing = ContainerUtil.newTroveSet();
      Map<BnfRule, String> inAttrs = ContainerUtil.newTroveMap();

      bnfTraverserNoAttrs(myFile).traverse().transform(RESOLVER).filter(NOT_NULL).addAllTo(inExpr);

      for (int size = 0, prev = -1; size != prev; prev = size, size = inParsing.size()) {
        bnfTraverserNoAttrs(myFile).expand(new JBIterable.StatefulFilter<PsiElement>() {
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
        if (message != null) {
          myHighlights.add(HighlightInfo.newHighlightInfo(HighlightInfoType.WARNING).
            range(r.getId()).descriptionAndTooltip(message).create());
        }
        visitAttrs(r.getAttrs());
      }
      for (BnfAttrs o : myFile.getAttributes()) {
        visitAttrs(o);
      }
    }

    private void visitAttrs(BnfAttrs attrs) {
      if (attrs == null) return;
      for (PsiElement child = attrs.getFirstChild(); child != null; child = child.getNextSibling()) {
        if (!(child instanceof BnfAttr)) continue;
        final String name = ((BnfAttr)child).getName();
        if (!name.toUpperCase().equals(name) && getAttribute(name) == null) {
          KnownAttribute newAttr = getCompatibleAttribute(name);
          PsiElement anchor = ((BnfAttr)child).getId();
          HighlightInfo.Builder builder = HighlightInfo.newHighlightInfo(HighlightInfoType.WARNING).range(anchor);
          builder.descriptionAndTooltip(newAttr == null ? "Unused attribute" : "Deprecated attribute, use '" + newAttr.getName() + "' instead");
          myHighlights.add(builder.create());
        }
      }
    }

    @Override
    public void doApplyInformationToEditor() {
      UpdateHighlightersUtil.setHighlightersToEditor(
        myProject, myDocument, 0, myFile.getTextLength(), myHighlights, getColorsScheme(), getId());
    }
  }
}
