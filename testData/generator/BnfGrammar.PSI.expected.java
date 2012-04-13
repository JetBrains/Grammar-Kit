// ---- BnfTypes.java -----------------
header.txt
package org.intellij.grammar.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.intellij.grammar.psi.impl.*;

public interface BnfTypes {

  IElementType BNF_ATTR = new BnfCompositeElementType("BNF_ATTR");
  IElementType BNF_ATTRS = new BnfCompositeElementType("BNF_ATTRS");
  IElementType BNF_ATTR_PATTERN = new BnfCompositeElementType("BNF_ATTR_PATTERN");
  IElementType BNF_ATTR_VALUE = new BnfCompositeElementType("BNF_ATTR_VALUE");
  IElementType BNF_CHOICE = new BnfCompositeElementType("BNF_CHOICE");
  IElementType BNF_EXPRESSION = new BnfCompositeElementType("BNF_EXPRESSION");
  IElementType BNF_EXTERNAL_EXPRESSION = new BnfCompositeElementType("BNF_EXTERNAL_EXPRESSION");
  IElementType BNF_LITERAL_EXPRESSION = new BnfCompositeElementType("BNF_LITERAL_EXPRESSION");
  IElementType BNF_MAP = new BnfCompositeElementType("BNF_MAP");
  IElementType BNF_MAP_ENTRY = new BnfCompositeElementType("BNF_MAP_ENTRY");
  IElementType BNF_MODIFIER = new BnfCompositeElementType("BNF_MODIFIER");
  IElementType BNF_PAREN_EXPRESSION = new BnfCompositeElementType("BNF_PAREN_EXPRESSION");
  IElementType BNF_PAREN_OPT_EXPRESSION = new BnfCompositeElementType("BNF_PAREN_OPT_EXPRESSION");
  IElementType BNF_PREDICATE = new BnfCompositeElementType("BNF_PREDICATE");
  IElementType BNF_PREDICATE_SIGN = new BnfCompositeElementType("BNF_PREDICATE_SIGN");
  IElementType BNF_QUANTIFIED = new BnfCompositeElementType("BNF_QUANTIFIED");
  IElementType BNF_QUANTIFIER = new BnfCompositeElementType("BNF_QUANTIFIER");
  IElementType BNF_REFERENCE_OR_TOKEN = new BnfCompositeElementType("BNF_REFERENCE_OR_TOKEN");
  IElementType BNF_RULE = new BnfCompositeElementType("BNF_RULE");
  IElementType BNF_SEQUENCE = new BnfCompositeElementType("BNF_SEQUENCE");
  IElementType BNF_STRING_LITERAL_EXPRESSION = new BnfCompositeElementType("BNF_STRING_LITERAL_EXPRESSION");

  IElementType BNF_EXTERNAL_END = new BnfTokenType(">>");
  IElementType BNF_EXTERNAL_START = new BnfTokenType("<<");
  IElementType BNF_ID = new BnfTokenType("id");
  IElementType BNF_LEFT_BRACE = new BnfTokenType("{");
  IElementType BNF_LEFT_BRACKET = new BnfTokenType("[");
  IElementType BNF_LEFT_PAREN = new BnfTokenType("(");
  IElementType BNF_NUMBER = new BnfTokenType("number");
  IElementType BNF_OP_AND = new BnfTokenType("&");
  IElementType BNF_OP_EQ = new BnfTokenType("=");
  IElementType BNF_OP_IS = new BnfTokenType("::=");
  IElementType BNF_OP_NOT = new BnfTokenType("!");
  IElementType BNF_OP_ONEMORE = new BnfTokenType("+");
  IElementType BNF_OP_OPT = new BnfTokenType("?");
  IElementType BNF_OP_OR = new BnfTokenType("|");
  IElementType BNF_OP_ZEROMORE = new BnfTokenType("*");
  IElementType BNF_RIGHT_BRACE = new BnfTokenType("}");
  IElementType BNF_RIGHT_BRACKET = new BnfTokenType("]");
  IElementType BNF_RIGHT_PAREN = new BnfTokenType(")");
  IElementType BNF_SEMICOLON = new BnfTokenType(";");
  IElementType BNF_STRING = new BnfTokenType("string");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == BNF_ATTR) {
        return new BnfAttrImpl(node);
      }
      else if (type == BNF_ATTRS) {
        return new BnfAttrsImpl(node);
      }
      else if (type == BNF_ATTR_PATTERN) {
        return new BnfAttrPatternImpl(node);
      }
      else if (type == BNF_ATTR_VALUE) {
        return new BnfAttrValueImpl(node);
      }
      else if (type == BNF_CHOICE) {
        return new BnfChoiceImpl(node);
      }
      else if (type == BNF_EXPRESSION) {
        return new BnfExpressionImpl(node);
      }
      else if (type == BNF_EXTERNAL_EXPRESSION) {
        return new BnfExternalExpressionImpl(node);
      }
      else if (type == BNF_LITERAL_EXPRESSION) {
        return new BnfLiteralExpressionImpl(node);
      }
      else if (type == BNF_MAP) {
        return new BnfMapImpl(node);
      }
      else if (type == BNF_MAP_ENTRY) {
        return new BnfMapEntryImpl(node);
      }
      else if (type == BNF_MODIFIER) {
        return new BnfModifierImpl(node);
      }
      else if (type == BNF_PAREN_EXPRESSION) {
        return new BnfParenExpressionImpl(node);
      }
      else if (type == BNF_PAREN_OPT_EXPRESSION) {
        return new BnfParenOptExpressionImpl(node);
      }
      else if (type == BNF_PREDICATE) {
        return new BnfPredicateImpl(node);
      }
      else if (type == BNF_PREDICATE_SIGN) {
        return new BnfPredicateSignImpl(node);
      }
      else if (type == BNF_QUANTIFIED) {
        return new BnfQuantifiedImpl(node);
      }
      else if (type == BNF_QUANTIFIER) {
        return new BnfQuantifierImpl(node);
      }
      else if (type == BNF_REFERENCE_OR_TOKEN) {
        return new BnfReferenceOrTokenImpl(node);
      }
      else if (type == BNF_RULE) {
        return new BnfRuleImpl(node);
      }
      else if (type == BNF_SEQUENCE) {
        return new BnfSequenceImpl(node);
      }
      else if (type == BNF_STRING_LITERAL_EXPRESSION) {
        return new BnfStringLiteralExpressionImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- BnfAttr.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfAttr extends BnfNamedElement {

  @Nullable
  BnfAttrPattern getAttrPattern();

  @Nullable
  BnfAttrValue getAttrValue();

  @NotNull
  PsiElement getId();

}
// ---- BnfAttrPattern.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfAttrPattern extends BnfCompositeElement {

  @Nullable
  BnfLiteralExpression getLiteralExpression();

}
// ---- BnfAttrValue.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfAttrValue extends BnfCompositeElement {

  @NotNull
  BnfExpression getExpression();

}
// ---- BnfAttrs.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfAttrs extends BnfCompositeElement {

  @NotNull
  List<BnfAttr> getAttrList();

}
// ---- BnfChoice.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfChoice extends BnfExpression {

  @NotNull
  List<BnfExpression> getExpressionList();

}
// ---- BnfExpression.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfExpression extends BnfCompositeElement {

}
// ---- BnfExternalExpression.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfExternalExpression extends BnfExpression {

  @NotNull
  List<BnfExpression> getExpressionList();

}
// ---- BnfLiteralExpression.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfLiteralExpression extends BnfExpression {

  @Nullable
  PsiElement getNumber();

}
// ---- BnfMap.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfMap extends BnfExpression {

  @NotNull
  List<BnfMapEntry> getMapEntryList();

}
// ---- BnfMapEntry.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfMapEntry extends BnfCompositeElement {

  @Nullable
  BnfLiteralExpression getLiteralExpression();

  @NotNull
  PsiElement getId();

}
// ---- BnfModifier.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfModifier extends BnfCompositeElement {

}
// ---- BnfParenExpression.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfParenExpression extends BnfParenthesized {

  @Nullable
  BnfExpression getExpression();

}
// ---- BnfParenOptExpression.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfParenOptExpression extends BnfParenthesized {

  @Nullable
  BnfExpression getExpression();

}
// ---- BnfParenthesized.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfParenthesized extends BnfExpression {

  @Nullable
  BnfExpression getExpression();

}
// ---- BnfPredicate.java -----------------
header.txt
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
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfPredicateSign extends BnfCompositeElement {

}
// ---- BnfQuantified.java -----------------
header.txt
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
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfQuantifier extends BnfCompositeElement {

}
// ---- BnfReferenceOrToken.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfReferenceOrToken extends BnfExpression {

  @NotNull
  PsiElement getId();

}
// ---- BnfRule.java -----------------
header.txt
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
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfSequence extends BnfExpression {

  @NotNull
  List<BnfExpression> getExpressionList();

}
// ---- BnfStringLiteralExpression.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfStringLiteralExpression extends BnfLiteralExpression {

  @NotNull
  PsiElement getString();

}
// ---- BnfAttrImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfAttrImpl extends BnfNamedElementImpl implements BnfAttr {

  public BnfAttrImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public BnfAttrPattern getAttrPattern() {
    return findChildByClass(BnfAttrPattern.class);
  }

  @Override
  @Nullable
  public BnfAttrValue getAttrValue() {
    return findChildByClass(BnfAttrValue.class);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(BNF_ID);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitAttr(this);
    else super.accept(visitor);
  }

}
// ---- BnfAttrPatternImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfAttrPatternImpl extends BnfCompositeElementImpl implements BnfAttrPattern {

  public BnfAttrPatternImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public BnfLiteralExpression getLiteralExpression() {
    return findChildByClass(BnfLiteralExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitAttrPattern(this);
    else super.accept(visitor);
  }

}
// ---- BnfAttrValueImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfAttrValueImpl extends BnfCompositeElementImpl implements BnfAttrValue {

  public BnfAttrValueImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public BnfExpression getExpression() {
    return findNotNullChildByClass(BnfExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitAttrValue(this);
    else super.accept(visitor);
  }

}
// ---- BnfAttrsImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfAttrsImpl extends BnfCompositeElementImpl implements BnfAttrs {

  public BnfAttrsImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<BnfAttr> getAttrList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BnfAttr.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitAttrs(this);
    else super.accept(visitor);
  }

}
// ---- BnfChoiceImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfChoiceImpl extends BnfExpressionImpl implements BnfChoice {

  public BnfChoiceImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<BnfExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BnfExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitChoice(this);
    else super.accept(visitor);
  }

}
// ---- BnfExpressionImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfExpressionImpl extends BnfCompositeElementImpl implements BnfExpression {

  public BnfExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitExpression(this);
    else super.accept(visitor);
  }

}
// ---- BnfExternalExpressionImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfExternalExpressionImpl extends BnfExpressionImpl implements BnfExternalExpression {

  public BnfExternalExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<BnfExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BnfExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitExternalExpression(this);
    else super.accept(visitor);
  }

}
// ---- BnfLiteralExpressionImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfLiteralExpressionImpl extends BnfExpressionImpl implements BnfLiteralExpression {

  public BnfLiteralExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findChildByType(BNF_NUMBER);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitLiteralExpression(this);
    else super.accept(visitor);
  }

}
// ---- BnfMapImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfMapImpl extends BnfExpressionImpl implements BnfMap {

  public BnfMapImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<BnfMapEntry> getMapEntryList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BnfMapEntry.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitMap(this);
    else super.accept(visitor);
  }

}
// ---- BnfMapEntryImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfMapEntryImpl extends BnfCompositeElementImpl implements BnfMapEntry {

  public BnfMapEntryImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public BnfLiteralExpression getLiteralExpression() {
    return findChildByClass(BnfLiteralExpression.class);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(BNF_ID);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitMapEntry(this);
    else super.accept(visitor);
  }

}
// ---- BnfModifierImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfModifierImpl extends BnfCompositeElementImpl implements BnfModifier {

  public BnfModifierImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitModifier(this);
    else super.accept(visitor);
  }

}
// ---- BnfParenExpressionImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfParenExpressionImpl extends BnfParenthesizedImpl implements BnfParenExpression {

  public BnfParenExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public BnfExpression getExpression() {
    return findChildByClass(BnfExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitParenExpression(this);
    else super.accept(visitor);
  }

}
// ---- BnfParenOptExpressionImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfParenOptExpressionImpl extends BnfParenthesizedImpl implements BnfParenOptExpression {

  public BnfParenOptExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public BnfExpression getExpression() {
    return findChildByClass(BnfExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitParenOptExpression(this);
    else super.accept(visitor);
  }

}
// ---- BnfParenthesizedImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfParenthesizedImpl extends BnfExpressionImpl implements BnfParenthesized {

  public BnfParenthesizedImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public BnfExpression getExpression() {
    return findChildByClass(BnfExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitParenthesized(this);
    else super.accept(visitor);
  }

}
// ---- BnfPredicateImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfPredicateImpl extends BnfExpressionImpl implements BnfPredicate {

  public BnfPredicateImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public BnfExpression getExpression() {
    return findNotNullChildByClass(BnfExpression.class);
  }

  @Override
  @NotNull
  public BnfPredicateSign getPredicateSign() {
    return findNotNullChildByClass(BnfPredicateSign.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitPredicate(this);
    else super.accept(visitor);
  }

}
// ---- BnfPredicateSignImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfPredicateSignImpl extends BnfCompositeElementImpl implements BnfPredicateSign {

  public BnfPredicateSignImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitPredicateSign(this);
    else super.accept(visitor);
  }

}
// ---- BnfQuantifiedImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfQuantifiedImpl extends BnfExpressionImpl implements BnfQuantified {

  public BnfQuantifiedImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public BnfExpression getExpression() {
    return findNotNullChildByClass(BnfExpression.class);
  }

  @Override
  @NotNull
  public BnfQuantifier getQuantifier() {
    return findNotNullChildByClass(BnfQuantifier.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitQuantified(this);
    else super.accept(visitor);
  }

}
// ---- BnfQuantifierImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfQuantifierImpl extends BnfCompositeElementImpl implements BnfQuantifier {

  public BnfQuantifierImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitQuantifier(this);
    else super.accept(visitor);
  }

}
// ---- BnfReferenceOrTokenImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfReferenceOrTokenImpl extends BnfRefOrTokenImpl implements BnfReferenceOrToken {

  public BnfReferenceOrTokenImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(BNF_ID);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitReferenceOrToken(this);
    else super.accept(visitor);
  }

}
// ---- BnfRuleImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfRuleImpl extends BnfNamedElementImpl implements BnfRule {

  public BnfRuleImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public BnfAttrs getAttrs() {
    return findChildByClass(BnfAttrs.class);
  }

  @Override
  @NotNull
  public BnfExpression getExpression() {
    return findNotNullChildByClass(BnfExpression.class);
  }

  @Override
  @NotNull
  public List<BnfModifier> getModifierList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BnfModifier.class);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(BNF_ID);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitRule(this);
    else super.accept(visitor);
  }

}
// ---- BnfSequenceImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfSequenceImpl extends BnfExpressionImpl implements BnfSequence {

  public BnfSequenceImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<BnfExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BnfExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitSequence(this);
    else super.accept(visitor);
  }

}
// ---- BnfStringLiteralExpressionImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfStringLiteralExpressionImpl extends BnfStringImpl implements BnfStringLiteralExpression {

  public BnfStringLiteralExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getString() {
    return findNotNullChildByType(BNF_STRING);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) ((BnfVisitor)visitor).visitStringLiteralExpression(this);
    else super.accept(visitor);
  }

}
// ---- BnfVisitor.java -----------------
header.txt
package org.intellij.grammar.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class BnfVisitor extends PsiElementVisitor {

  public void visitAttr(@NotNull BnfAttr o) {
    visitNamedElement(o);
  }

  public void visitAttrPattern(@NotNull BnfAttrPattern o) {
    visitCompositeElement(o);
  }

  public void visitAttrValue(@NotNull BnfAttrValue o) {
    visitCompositeElement(o);
  }

  public void visitAttrs(@NotNull BnfAttrs o) {
    visitCompositeElement(o);
  }

  public void visitChoice(@NotNull BnfChoice o) {
    visitExpression(o);
  }

  public void visitExpression(@NotNull BnfExpression o) {
    visitCompositeElement(o);
  }

  public void visitExternalExpression(@NotNull BnfExternalExpression o) {
    visitExpression(o);
  }

  public void visitLiteralExpression(@NotNull BnfLiteralExpression o) {
    visitExpression(o);
  }

  public void visitMap(@NotNull BnfMap o) {
    visitExpression(o);
  }

  public void visitMapEntry(@NotNull BnfMapEntry o) {
    visitCompositeElement(o);
  }

  public void visitModifier(@NotNull BnfModifier o) {
    visitCompositeElement(o);
  }

  public void visitParenExpression(@NotNull BnfParenExpression o) {
    visitParenthesized(o);
  }

  public void visitParenOptExpression(@NotNull BnfParenOptExpression o) {
    visitParenthesized(o);
  }

  public void visitParenthesized(@NotNull BnfParenthesized o) {
    visitExpression(o);
  }

  public void visitPredicate(@NotNull BnfPredicate o) {
    visitExpression(o);
  }

  public void visitPredicateSign(@NotNull BnfPredicateSign o) {
    visitCompositeElement(o);
  }

  public void visitQuantified(@NotNull BnfQuantified o) {
    visitExpression(o);
  }

  public void visitQuantifier(@NotNull BnfQuantifier o) {
    visitCompositeElement(o);
  }

  public void visitReferenceOrToken(@NotNull BnfReferenceOrToken o) {
    visitExpression(o);
  }

  public void visitRule(@NotNull BnfRule o) {
    visitNamedElement(o);
  }

  public void visitSequence(@NotNull BnfSequence o) {
    visitExpression(o);
  }

  public void visitStringLiteralExpression(@NotNull BnfStringLiteralExpression o) {
    visitLiteralExpression(o);
  }

  public void visitNamedElement(@NotNull BnfNamedElement o) {
    visitCompositeElement(o);
  }

  public void visitCompositeElement(@NotNull BnfCompositeElement o) {
    visitElement(o);
  }

}