package org.intellij.grammar;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.grammar.psi.BnfAttrs;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @author gregsh
 */
public class BnfFoldingBuilder extends FoldingBuilderEx implements DumbAware {
  @NotNull
  @Override
  public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
    if (!(root instanceof BnfFile)) return FoldingDescriptor.EMPTY;
    BnfFile file = (BnfFile)root;

    final ArrayList<FoldingDescriptor> result = new ArrayList<FoldingDescriptor>();
    for (BnfAttrs attrs : file.getAttributes()) {
      result.add(new FoldingDescriptor(attrs, attrs.getTextRange()));
    }
    for (BnfRule rule : file.getRules()) {
      //result.add(new FoldingDescriptor(rule, rule.getTextRange()));
      BnfAttrs attrs = rule.getAttrs();
      if (attrs != null) {
        result.add(new FoldingDescriptor(attrs, attrs.getTextRange()));
      }
    }
    if (!quick) {
      PsiTreeUtil.processElements(file, new PsiElementProcessor() {
        @Override
        public boolean execute(@NotNull PsiElement element) {
          if (element.getNode().getElementType() == BnfParserDefinition.BNF_BLOCK_COMMENT) {
            result.add(new FoldingDescriptor(element, element.getTextRange()));
          }
          return true;
        }
      });
    }

    return result.toArray(new FoldingDescriptor[result.size()]);
  }

  @Nullable
  @Override
  public String getPlaceholderText(@NotNull ASTNode node) {
    PsiElement psi = node.getPsi();
    if (psi instanceof BnfAttrs) return "{..}";
    if (psi instanceof BnfRule) return ((BnfRule)psi).getName() + " ::= ...";
    if (node.getElementType() == BnfParserDefinition.BNF_BLOCK_COMMENT) return "/*..*/";
    return null;
  }

  @Override
  public boolean isCollapsedByDefault(@NotNull ASTNode node) {
    return false;
  }
}
