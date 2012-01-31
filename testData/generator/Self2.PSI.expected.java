// ---- BnfTypes.java -----------------
header.txt
package org.intellij.grammar.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.intellij.grammar.psi.impl.*;

public interface BnfTypes {

  IElementType BNF_ATTR = new BnfCompositeElementType("BNF_ATTR");
  IElementType BNF_ATTR_PATTERN = new BnfCompositeElementType("BNF_ATTR_PATTERN");
  IElementType BNF_ATTR_VALUE = new BnfCompositeElementType("BNF_ATTR_VALUE");
  IElementType BNF_ATTRS = new BnfCompositeElementType("BNF_ATTRS");
  IElementType BNF_CHOICE = new BnfCompositeElementType("BNF_CHOICE");
  IElementType BNF_EXPRESSION = new BnfCompositeElementType("BNF_EXPRESSION");
  IElementType BNF_EXTERNAL_EXPRESSION = new BnfCompositeElementType("BNF_EXTERNAL_EXPRESSION");
  IElementType BNF_LITERAL_EXPRESSION = new BnfCompositeElementType("BNF_LITERAL_EXPRESSION");
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
      else if (type == BNF_ATTR_PATTERN) {
        return new BnfAttrPatternImpl(node);
      }
      else if (type == BNF_ATTR_VALUE) {
        return new BnfAttrValueImpl(node);
      }
      else if (type == BNF_ATTRS) {
        return new BnfAttrsImpl(node);
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
  public BnfAttrPattern getAttrPattern();

  @Nullable
  public BnfAttrValue getAttrValue();

  @NotNull
  public PsiElement getId();

}
// ---- BnfAttrPattern.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfAttrPattern extends BnfCompositeElement {

  @Nullable
  public BnfLiteralExpression getLiteralExpression();

}
// ---- BnfAttrValue.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfAttrValue extends BnfCompositeElement {

  @NotNull
  public BnfExpression getExpression();

}
// ---- BnfAttrs.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfAttrs extends BnfCompositeElement {

  @NotNull
  public List<BnfAttr> getAttrList();

}
// ---- BnfChoice.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfChoice extends BnfExpression {

  @NotNull
  public List<BnfExpression> getExpressionList();

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
  public List<BnfExpression> getExpressionList();

}
// ---- BnfLiteralExpression.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfLiteralExpression extends BnfExpression {

  @Nullable
  public PsiElement getNumber();

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

public interface BnfParenExpression extends BnfExpression, BnfParenthesized {

  @Nullable
  public BnfExpression getExpression();

}
// ---- BnfParenOptExpression.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfParenOptExpression extends BnfExpression, BnfParenthesized {

  @Nullable
  public BnfExpression getExpression();

}
// ---- BnfPredicate.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfPredicate extends BnfExpression {

  @NotNull
  public BnfExpression getExpression();

  @NotNull
  public BnfPredicateSign getPredicateSign();

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
  public BnfExpression getExpression();

  @NotNull
  public BnfQuantifier getQuantifier();

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
  public PsiElement getId();

}
// ---- BnfRule.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfRule extends BnfNamedElement {

  @Nullable
  public BnfAttrs getAttrs();

  @NotNull
  public BnfExpression getExpression();

  @NotNull
  public List<BnfModifier> getModifierList();

  @NotNull
  public PsiElement getId();

}
// ---- BnfSequence.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfSequence extends BnfExpression {

  @NotNull
  public List<BnfExpression> getExpressionList();

}
// ---- BnfStringLiteralExpression.java -----------------
header.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfStringLiteralExpression extends BnfLiteralExpression {

  @NotNull
  public PsiElement getString();

}
// ---- BnfAttrImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
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

}
// ---- BnfAttrPatternImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
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

}
// ---- BnfAttrValueImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
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

}
// ---- BnfAttrsImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
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

}
// ---- BnfChoiceImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
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

}
// ---- BnfExpressionImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfExpressionImpl extends BnfCompositeElementImpl implements BnfExpression {

  public BnfExpressionImpl(ASTNode node) {
    super(node);
  }

}
// ---- BnfExternalExpressionImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
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

}
// ---- BnfLiteralExpressionImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
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

}
// ---- BnfModifierImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfModifierImpl extends BnfCompositeElementImpl implements BnfModifier {

  public BnfModifierImpl(ASTNode node) {
    super(node);
  }

}
// ---- BnfParenExpressionImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfParenExpressionImpl extends BnfExpressionImpl implements BnfParenExpression {

  public BnfParenExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public BnfExpression getExpression() {
    return findChildByClass(BnfExpression.class);
  }

}
// ---- BnfParenOptExpressionImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfParenOptExpressionImpl extends BnfExpressionImpl implements BnfParenOptExpression {

  public BnfParenOptExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public BnfExpression getExpression() {
    return findChildByClass(BnfExpression.class);
  }

}
// ---- BnfPredicateImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
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

}
// ---- BnfPredicateSignImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfPredicateSignImpl extends BnfCompositeElementImpl implements BnfPredicateSign {

  public BnfPredicateSignImpl(ASTNode node) {
    super(node);
  }

}
// ---- BnfQuantifiedImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
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

}
// ---- BnfQuantifierImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;

public class BnfQuantifierImpl extends BnfCompositeElementImpl implements BnfQuantifier {

  public BnfQuantifierImpl(ASTNode node) {
    super(node);
  }

}
// ---- BnfReferenceOrTokenImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
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

}
// ---- BnfRuleImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
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

}
// ---- BnfSequenceImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
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

}
// ---- BnfStringLiteralExpressionImpl.java -----------------
header.txt
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
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

}