// ---- BnfTypes.java -----------------
// license.txt
package org.intellij.grammar.psi;

import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.intellij.grammar.psi.impl.*;
import com.intellij.psi.impl.source.tree.CompositePsiElement;

public interface BnfTypes {

  SyntaxElementType BNF_ATTR = new BnfCompositeElementType("BNF_ATTR");
  SyntaxElementType BNF_ATTRS = new BnfCompositeElementType("BNF_ATTRS");
  SyntaxElementType BNF_ATTR_PATTERN = new BnfCompositeElementType("BNF_ATTR_PATTERN");
  SyntaxElementType BNF_CHOICE = new BnfCompositeElementType("BNF_CHOICE");
  SyntaxElementType BNF_EXPRESSION = new BnfCompositeElementType("BNF_EXPRESSION");
  SyntaxElementType BNF_EXTERNAL_EXPRESSION = new BnfCompositeElementType("BNF_EXTERNAL_EXPRESSION");
  SyntaxElementType BNF_LIST_ENTRY = new BnfCompositeElementType("BNF_LIST_ENTRY");
  SyntaxElementType BNF_LITERAL_EXPRESSION = new BnfCompositeElementType("BNF_LITERAL_EXPRESSION");
  SyntaxElementType BNF_MODIFIER = new BnfCompositeElementType("BNF_MODIFIER");
  SyntaxElementType BNF_PAREN_EXPRESSION = new BnfCompositeElementType("BNF_PAREN_EXPRESSION");
  SyntaxElementType BNF_PAREN_OPT_EXPRESSION = new BnfCompositeElementType("BNF_PAREN_OPT_EXPRESSION");
  SyntaxElementType BNF_PREDICATE = new BnfCompositeElementType("BNF_PREDICATE");
  SyntaxElementType BNF_PREDICATE_SIGN = new BnfCompositeElementType("BNF_PREDICATE_SIGN");
  SyntaxElementType BNF_QUANTIFIED = new BnfCompositeElementType("BNF_QUANTIFIED");
  SyntaxElementType BNF_QUANTIFIER = new BnfCompositeElementType("BNF_QUANTIFIER");
  SyntaxElementType BNF_REFERENCE_OR_TOKEN = new BnfCompositeElementType("BNF_REFERENCE_OR_TOKEN");
  SyntaxElementType BNF_RULE = new BnfCompositeElementType("BNF_RULE");
  SyntaxElementType BNF_SEQUENCE = new BnfCompositeElementType("BNF_SEQUENCE");
  SyntaxElementType BNF_STRING_LITERAL_EXPRESSION = new BnfCompositeElementType("BNF_STRING_LITERAL_EXPRESSION");
  SyntaxElementType BNF_VALUE_LIST = new BnfCompositeElementType("BNF_VALUE_LIST");

  SyntaxElementType BNF_BLOCK_COMMENT = new BnfTokenType("block_comment");
  SyntaxElementType BNF_EXTERNAL_END = new BnfTokenType(">>");
  SyntaxElementType BNF_EXTERNAL_START = new BnfTokenType("<<");
  SyntaxElementType BNF_ID = new BnfTokenType("id");
  SyntaxElementType BNF_LEFT_BRACE = new BnfTokenType("{");
  SyntaxElementType BNF_LEFT_BRACKET = new BnfTokenType("[");
  SyntaxElementType BNF_LEFT_PAREN = new BnfTokenType("(");
  SyntaxElementType BNF_LINE_COMMENT = new BnfTokenType("line_comment");
  SyntaxElementType BNF_NUMBER = new BnfTokenType("number");
  SyntaxElementType BNF_OP_AND = new BnfTokenType("&");
  SyntaxElementType BNF_OP_EQ = new BnfTokenType("=");
  SyntaxElementType BNF_OP_IS = new BnfTokenType("::=");
  SyntaxElementType BNF_OP_NOT = new BnfTokenType("!");
  SyntaxElementType BNF_OP_ONEMORE = new BnfTokenType("+");
  SyntaxElementType BNF_OP_OPT = new BnfTokenType("?");
  SyntaxElementType BNF_OP_OR = new BnfTokenType("|");
  SyntaxElementType BNF_OP_ZEROMORE = new BnfTokenType("*");
  SyntaxElementType BNF_RIGHT_BRACE = new BnfTokenType("}");
  SyntaxElementType BNF_RIGHT_BRACKET = new BnfTokenType("]");
  SyntaxElementType BNF_RIGHT_PAREN = new BnfTokenType(")");
  SyntaxElementType BNF_SEMICOLON = new BnfTokenType(";");
  SyntaxElementType BNF_STRING = new BnfTokenType("string");

  class Factory {
    public static CompositePsiElement createElement(SyntaxElementType type) {
       if (type == BNF_ATTR) {
        return new BnfAttrImpl(type);
      }
      else if (type == BNF_ATTRS) {
        return new BnfAttrsImpl(type);
      }
      else if (type == BNF_ATTR_PATTERN) {
        return new BnfAttrPatternImpl(type);
      }
      else if (type == BNF_CHOICE) {
        return new BnfChoiceImpl(type);
      }
      else if (type == BNF_EXTERNAL_EXPRESSION) {
        return new BnfExternalExpressionImpl(type);
      }
      else if (type == BNF_LIST_ENTRY) {
        return new BnfListEntryImpl(type);
      }
      else if (type == BNF_LITERAL_EXPRESSION) {
        return new BnfLiteralExpressionImpl(type);
      }
      else if (type == BNF_MODIFIER) {
        return new BnfModifierImpl(type);
      }
      else if (type == BNF_PAREN_EXPRESSION) {
        return new BnfParenExpressionImpl(type);
      }
      else if (type == BNF_PAREN_OPT_EXPRESSION) {
        return new BnfParenOptExpressionImpl(type);
      }
      else if (type == BNF_PREDICATE) {
        return new BnfPredicateImpl(type);
      }
      else if (type == BNF_PREDICATE_SIGN) {
        return new BnfPredicateSignImpl(type);
      }
      else if (type == BNF_QUANTIFIED) {
        return new BnfQuantifiedImpl(type);
      }
      else if (type == BNF_QUANTIFIER) {
        return new BnfQuantifierImpl(type);
      }
      else if (type == BNF_REFERENCE_OR_TOKEN) {
        return new BnfReferenceOrTokenImpl(type);
      }
      else if (type == BNF_RULE) {
        return new BnfRuleImpl(type);
      }
      else if (type == BNF_SEQUENCE) {
        return new BnfSequenceImpl(type);
      }
      else if (type == BNF_STRING_LITERAL_EXPRESSION) {
        return new BnfStringLiteralExpressionImpl(type);
      }
      else if (type == BNF_VALUE_LIST) {
        return new BnfValueListImpl(type);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- BnfAttr.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfAttr extends BnfNamedElement {

  @Nullable
  BnfAttrPattern getAttrPattern();

  @Nullable
  BnfExpression getExpression();

  @NotNull
  PsiElement getId();

}
// ---- BnfAttrPattern.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfAttrPattern extends BnfComposite {

  @Nullable
  BnfStringLiteralExpression getLiteralExpression();

}
// ---- BnfAttrs.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfAttrs extends BnfComposite {

  @NotNull
  List<BnfAttr> getAttrList();

}
// ---- BnfChoice.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfChoice extends BnfExpression {

  @NotNull
  List<BnfExpression> getExpressionList();

}
// ---- BnfExpression.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfExpression extends BnfComposite {

}
// ---- BnfExternalExpression.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfExternalExpression extends BnfExpression {

  @NotNull
  List<BnfExpression> getExpressionList();

  @NotNull
  BnfExpression getRefElement();

  @NotNull List<BnfExpression> getArguments();

}
// ---- BnfListEntry.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface BnfListEntry extends BnfComposite {

  @Nullable
  PsiElement getId();

  PsiReference @NotNull [] getReferences();

  @Nullable
  BnfStringLiteralExpression getLiteralExpression();

}
// ---- BnfLiteralExpression.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfLiteralExpression extends BnfExpression {

  @Nullable
  PsiElement getNumber();

}
// ---- BnfModifier.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfModifier extends BnfComposite {

}
// ---- BnfParenExpression.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfParenExpression extends BnfParenthesized {

}
// ---- BnfParenOptExpression.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfParenOptExpression extends BnfParenthesized {

}
// ---- BnfParenthesized.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfParenthesized extends BnfExpression {

  @NotNull
  BnfExpression getExpression();

}
// ---- BnfPredicate.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfPredicate extends BnfExpression {

  @NotNull
  BnfExpression getExpression();

  @NotNull
  BnfPredicateSign getPredicateSign();

}
// ---- BnfPredicateSign.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfPredicateSign extends BnfComposite {

}
// ---- BnfQuantified.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfQuantified extends BnfExpression {

  @NotNull
  BnfExpression getExpression();

  @NotNull
  BnfQuantifier getQuantifier();

}
// ---- BnfQuantifier.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfQuantifier extends BnfComposite {

}
// ---- BnfReferenceOrToken.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfReferenceOrToken extends BnfExpression {

  @NotNull
  PsiElement getId();

  @Nullable BnfRule resolveRule();

}
// ---- BnfRule.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfRule extends BnfNamedElement {

  @Nullable
  BnfAttrs getAttrs();

  @NotNull
  BnfExpression getExpression();

  @NotNull
  List<BnfModifier> getModifierList();

  @NotNull
  PsiElement getId();

}
// ---- BnfSequence.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfSequence extends BnfExpression {

  @NotNull
  List<BnfExpression> getExpressionList();

}
// ---- BnfStringLiteralExpression.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfStringLiteralExpression extends BnfLiteralExpression {

  @NotNull
  PsiElement getString();

}
// ---- BnfValueList.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfValueList extends BnfExpression {

  @NotNull
  List<BnfListEntry> getListEntryList();

}
// ---- BnfAttrImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfAttrImpl extends BnfNamedImpl implements BnfAttr {

  public BnfAttrImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitAttr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BnfAttrPattern getAttrPattern() {
    return PsiTreeUtil.getChildOfType(this, BnfAttrPattern.class);
  }

  @Override
  @Nullable
  public BnfExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, BnfExpression.class);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findPsiChildByType(BNF_ID);
  }

}
// ---- BnfAttrPatternImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfAttrPatternImpl extends BnfCompositeImpl implements BnfAttrPattern {

  public BnfAttrPatternImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitAttrPattern(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BnfStringLiteralExpression getLiteralExpression() {
    return PsiTreeUtil.getChildOfType(this, BnfStringLiteralExpression.class);
  }

}
// ---- BnfAttrsImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfAttrsImpl extends BnfCompositeImpl implements BnfAttrs {

  public BnfAttrsImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitAttrs(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<BnfAttr> getAttrList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BnfAttr.class);
  }

}
// ---- BnfChoiceImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfChoiceImpl extends BnfExpressionImpl implements BnfChoice {

  public BnfChoiceImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitChoice(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<BnfExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BnfExpression.class);
  }

}
// ---- BnfExpressionImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public abstract class BnfExpressionImpl extends BnfCompositeImpl implements BnfExpression {

  public BnfExpressionImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- BnfExternalExpressionImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfExternalExpressionImpl extends BnfExpressionImpl implements BnfExternalExpression {

  public BnfExternalExpressionImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitExternalExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<BnfExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BnfExpression.class);
  }

  @Override
  @NotNull
  public BnfExpression getRefElement() {
    List<BnfExpression> p1 = getExpressionList();
    return p1.get(0);
  }

  @Override
  public @NotNull List<BnfExpression> getArguments() {
    return GrammarPsiImplUtil.getArguments(this);
  }

}
// ---- BnfListEntryImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.psi.PsiReference;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfListEntryImpl extends BnfCompositeImpl implements BnfListEntry {

  public BnfListEntryImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitListEntry(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findPsiChildByType(BNF_ID);
  }

  @Override
  public PsiReference @NotNull [] getReferences() {
    return GrammarPsiImplUtil.getReferences(this);
  }

  @Override
  @Nullable
  public BnfStringLiteralExpression getLiteralExpression() {
    return PsiTreeUtil.getChildOfType(this, BnfStringLiteralExpression.class);
  }

}
// ---- BnfLiteralExpressionImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfLiteralExpressionImpl extends BnfExpressionImpl implements BnfLiteralExpression {

  public BnfLiteralExpressionImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitLiteralExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findPsiChildByType(BNF_NUMBER);
  }

}
// ---- BnfModifierImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfModifierImpl extends BnfCompositeImpl implements BnfModifier {

  public BnfModifierImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitModifier(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- BnfParenExpressionImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfParenExpressionImpl extends BnfParenthesizedImpl implements BnfParenExpression {

  public BnfParenExpressionImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitParenExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- BnfParenOptExpressionImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfParenOptExpressionImpl extends BnfParenthesizedImpl implements BnfParenOptExpression {

  public BnfParenOptExpressionImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitParenOptExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- BnfParenthesizedImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfParenthesizedImpl extends BnfExpressionImpl implements BnfParenthesized {

  public BnfParenthesizedImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitParenthesized(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public BnfExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, BnfExpression.class);
  }

}
// ---- BnfPredicateImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfPredicateImpl extends BnfExpressionImpl implements BnfPredicate {

  public BnfPredicateImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitPredicate(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public BnfExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, BnfExpression.class);
  }

  @Override
  @NotNull
  public BnfPredicateSign getPredicateSign() {
    return PsiTreeUtil.getChildOfType(this, BnfPredicateSign.class);
  }

}
// ---- BnfPredicateSignImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfPredicateSignImpl extends BnfCompositeImpl implements BnfPredicateSign {

  public BnfPredicateSignImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitPredicateSign(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- BnfQuantifiedImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfQuantifiedImpl extends BnfExpressionImpl implements BnfQuantified {

  public BnfQuantifiedImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitQuantified(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public BnfExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, BnfExpression.class);
  }

  @Override
  @NotNull
  public BnfQuantifier getQuantifier() {
    return PsiTreeUtil.getChildOfType(this, BnfQuantifier.class);
  }

}
// ---- BnfQuantifierImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfQuantifierImpl extends BnfCompositeImpl implements BnfQuantifier {

  public BnfQuantifierImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitQuantifier(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- BnfReferenceOrTokenImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfReferenceOrTokenImpl extends BnfRefOrTokenImpl implements BnfReferenceOrToken {

  public BnfReferenceOrTokenImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitReferenceOrToken(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findPsiChildByType(BNF_ID);
  }

}
// ---- BnfRuleImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfRuleImpl extends BnfNamedImpl implements BnfRule {

  public BnfRuleImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitRule(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BnfAttrs getAttrs() {
    return PsiTreeUtil.getChildOfType(this, BnfAttrs.class);
  }

  @Override
  @NotNull
  public BnfExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, BnfExpression.class);
  }

  @Override
  @NotNull
  public List<BnfModifier> getModifierList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BnfModifier.class);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findPsiChildByType(BNF_ID);
  }

}
// ---- BnfSequenceImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfSequenceImpl extends BnfExpressionImpl implements BnfSequence {

  public BnfSequenceImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitSequence(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<BnfExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BnfExpression.class);
  }

}
// ---- BnfStringLiteralExpressionImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfStringLiteralExpressionImpl extends BnfStringImpl implements BnfStringLiteralExpression {

  public BnfStringLiteralExpressionImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitStringLiteralExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getString() {
    return findPsiChildByType(BNF_STRING);
  }

}
// ---- BnfValueListImpl.java -----------------
// license.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.platform.syntax.SyntaxElementType;

public class BnfValueListImpl extends BnfExpressionImpl implements BnfValueList {

  public BnfValueListImpl(SyntaxElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitValueList(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<BnfListEntry> getListEntryList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BnfListEntry.class);
  }

}
// ---- BnfVisitor.java -----------------
// license.txt
package org.intellij.grammar.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class BnfVisitor<R> extends PsiElementVisitor {

  public R visitAttr(@NotNull BnfAttr o) {
    return visitNamedElement(o);
  }

  public R visitAttrPattern(@NotNull BnfAttrPattern o) {
    return visitComposite(o);
  }

  public R visitAttrs(@NotNull BnfAttrs o) {
    return visitComposite(o);
  }

  public R visitChoice(@NotNull BnfChoice o) {
    return visitExpression(o);
  }

  public R visitExpression(@NotNull BnfExpression o) {
    return visitComposite(o);
  }

  public R visitExternalExpression(@NotNull BnfExternalExpression o) {
    return visitExpression(o);
  }

  public R visitListEntry(@NotNull BnfListEntry o) {
    return visitComposite(o);
  }

  public R visitLiteralExpression(@NotNull BnfLiteralExpression o) {
    return visitExpression(o);
  }

  public R visitModifier(@NotNull BnfModifier o) {
    return visitComposite(o);
  }

  public R visitParenExpression(@NotNull BnfParenExpression o) {
    return visitParenthesized(o);
  }

  public R visitParenOptExpression(@NotNull BnfParenOptExpression o) {
    return visitParenthesized(o);
  }

  public R visitParenthesized(@NotNull BnfParenthesized o) {
    return visitExpression(o);
  }

  public R visitPredicate(@NotNull BnfPredicate o) {
    return visitExpression(o);
  }

  public R visitPredicateSign(@NotNull BnfPredicateSign o) {
    return visitComposite(o);
  }

  public R visitQuantified(@NotNull BnfQuantified o) {
    return visitExpression(o);
  }

  public R visitQuantifier(@NotNull BnfQuantifier o) {
    return visitComposite(o);
  }

  public R visitReferenceOrToken(@NotNull BnfReferenceOrToken o) {
    return visitExpression(o);
  }

  public R visitRule(@NotNull BnfRule o) {
    return visitNamedElement(o);
  }

  public R visitSequence(@NotNull BnfSequence o) {
    return visitExpression(o);
  }

  public R visitStringLiteralExpression(@NotNull BnfStringLiteralExpression o) {
    return visitLiteralExpression(o);
  }

  public R visitValueList(@NotNull BnfValueList o) {
    return visitExpression(o);
  }

  public R visitNamedElement(@NotNull BnfNamedElement o) {
    return visitComposite(o);
  }

  public R visitComposite(@NotNull BnfComposite o) {
    visitElement(o);
    return null;
  }

}