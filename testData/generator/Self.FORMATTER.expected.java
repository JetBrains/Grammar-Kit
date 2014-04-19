// ---- BnfFormattingModelBuilder.java -----------------
package org.intellij.grammar;

import com.intellij.formatting.*;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.tree.IElementType;
import java.util.List;
import java.util.ArrayList;
import static org.intellij.grammar.psi.BnfTypes.*;

public class BnfFormattingModelBuilder implements FormattingModelBuilder {
  @NotNull
  @Override
  public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
    BnfFormattingBlock block = new BnfFormattingBlock(element.getNode(), null, null, settings);
    return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(), block, settings);
  }

  @Override
  public TextRange getRangeAffectingIndent(PsiFile psiFile, int i, ASTNode astNode) {
    return null;
  }

  public static class BnfFormattingBlock extends AbstractBlock {
    private final CodeStyleSettings mySettings;
    public BnfFormattingBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, CodeStyleSettings settings) {
      super(node, wrap, alignment);
      mySettings = settings;
    }

    @Override
    protected List<Block> buildChildren() {
      if (isLeaf()) { return EMPTY; }
      ArrayList<Block> children = new ArrayList<Block>();
      Alignment baseAlignment = Alignment.createAlignment();
      for (ASTNode childNode = getNode().getFirstChildNode(); childNode != null; childNode = childNode.getTreeNext()) {
        if (FormatterUtil.containsWhiteSpacesOnly(childNode)) continue;
        Block childBlock = new BnfFormattingBlock(childNode, createChildWrap(childNode), createChildAlignment(baseAlignment, childNode), mySettings);
        children.add(childBlock);
      }
      return children;
    }

    public Wrap createChildWrap(ASTNode child) {
      return null;
    }

    public Alignment createChildAlignment(Alignment baseAlignment, ASTNode child) {
      IElementType elementType = child.getElementType();
      ASTNode parent = child.getTreeParent();
      IElementType parentType = parent != null ? parent.getElementType() : null;
      if (parentType == BNF_ATTRS && elementType != BNF_RIGHT_BRACE && elementType != BNF_LEFT_BRACE) {
        return baseAlignment;
      }
      if (parentType == BNF_CHOICE) {
        return baseAlignment;
      }
      if (parentType == BNF_PAREN_EXPRESSION && elementType != BNF_RIGHT_BRACE && elementType != BNF_LEFT_BRACE) {
        return baseAlignment;
      }
      return null;
    }

    @Override
    public Indent getIndent() {
      ASTNode node = getNode();
      IElementType elementType = node.getElementType();
      ASTNode parent = node.getTreeParent();
      IElementType parentType = parent != null ? parent.getElementType() : null;
      if (parentType == BNF_ATTRS && elementType == BNF_ATTR) {
        return Indent.getNormalIndent();
      }
      if (parentType == BNF_CHOICE) {
        return Indent.getContinuationIndent();
      }
      if (parentType == BNF_PAREN_EXPRESSION) {
        return Indent.getContinuationIndent();
      }
      return Indent.getNoneIndent();
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
      SpacingBuilder spacingBuilder = new SpacingBuilder(mySettings)
      .around(BNF_OP_EQ).none()
      .before(BNF_SEMICOLON).none()
      .after(BNF_LEFT_PAREN).none()
      .before(BNF_RIGHT_PAREN).none()
      .before(BNF_RIGHT_BRACE).none()
      .after(BNF_LEFT_BRACE).none()
      .after(BNF_LEFT_BRACKET).none()
      .around(BNF_OP_OR).spaces(1)
      .before(BNF_OP_ZEROMORE).none()
      .before(BNF_OP_ONEMORE).none()
      .around(BNF_OP_IS).spaces(1)
      .around(BNF_RULE).none()
      .around(BNF_EXPRESSION).spaces(1)
      .after(BNF_MODIFIER).spaces(1)
      .before(BNF_ATTRS).spaces(1)
      .after(BNF_ATTR).spaces(1)
      .after(BNF_PREDICATE_SIGN).none()
      .after(BNF_QUANTIFIED).spaces(1)
      .before(BNF_QUANTIFIER).none()
      .around(BNF_REFERENCE_OR_TOKEN).spaces(1)
      .around(BNF_LITERAL_EXPRESSION).spaces(1)
      .around(BNF_STRING_LITERAL_EXPRESSION).spaces(1)
      .around(BNF_PAREN_EXPRESSION).spaces(1)
      ;
      return spacingBuilder.getSpacing(this, child1, child2);
    }

    @Override
    public boolean isLeaf() {
      return false;
    }
  }
}