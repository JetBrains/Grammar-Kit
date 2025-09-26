// ---- org/intellij/grammar/BnfSyntaxTypes.kt -----------------
// license.txt
package org.intellij.grammar

import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf

object BnfSyntaxTypes {
  val BNF_ATTR = SyntaxElementType("BNF_ATTR")
  val BNF_ATTRS = SyntaxElementType("BNF_ATTRS")
  val BNF_ATTR_PATTERN = SyntaxElementType("BNF_ATTR_PATTERN")
  val BNF_CHOICE = SyntaxElementType("BNF_CHOICE")
  val BNF_EXPRESSION = SyntaxElementType("BNF_EXPRESSION")
  val BNF_EXTERNAL_EXPRESSION = SyntaxElementType("BNF_EXTERNAL_EXPRESSION")
  val BNF_LIST_ENTRY = SyntaxElementType("BNF_LIST_ENTRY")
  val BNF_LITERAL_EXPRESSION = SyntaxElementType("BNF_LITERAL_EXPRESSION")
  val BNF_MODIFIER = SyntaxElementType("BNF_MODIFIER")
  val BNF_PAREN_EXPRESSION = SyntaxElementType("BNF_PAREN_EXPRESSION")
  val BNF_PAREN_OPT_EXPRESSION = SyntaxElementType("BNF_PAREN_OPT_EXPRESSION")
  val BNF_PREDICATE = SyntaxElementType("BNF_PREDICATE")
  val BNF_PREDICATE_SIGN = SyntaxElementType("BNF_PREDICATE_SIGN")
  val BNF_QUANTIFIED = SyntaxElementType("BNF_QUANTIFIED")
  val BNF_QUANTIFIER = SyntaxElementType("BNF_QUANTIFIER")
  val BNF_REFERENCE_OR_TOKEN = SyntaxElementType("BNF_REFERENCE_OR_TOKEN")
  val BNF_RULE = SyntaxElementType("BNF_RULE")
  val BNF_SEQUENCE = SyntaxElementType("BNF_SEQUENCE")
  val BNF_STRING_LITERAL_EXPRESSION = SyntaxElementType("BNF_STRING_LITERAL_EXPRESSION")
  val BNF_VALUE_LIST = SyntaxElementType("BNF_VALUE_LIST")

  val BNF_BLOCK_COMMENT = SyntaxElementType("block_comment")
  val BNF_EXTERNAL_END = SyntaxElementType(">>")
  val BNF_EXTERNAL_START = SyntaxElementType("<<")
  val BNF_ID = SyntaxElementType("id")
  val BNF_LEFT_BRACE = SyntaxElementType("{")
  val BNF_LEFT_BRACKET = SyntaxElementType("[")
  val BNF_LEFT_PAREN = SyntaxElementType("(")
  val BNF_LINE_COMMENT = SyntaxElementType("line_comment")
  val BNF_NUMBER = SyntaxElementType("number")
  val BNF_OP_AND = SyntaxElementType("&")
  val BNF_OP_EQ = SyntaxElementType("=")
  val BNF_OP_IS = SyntaxElementType("::=")
  val BNF_OP_NOT = SyntaxElementType("!")
  val BNF_OP_ONEMORE = SyntaxElementType("+")
  val BNF_OP_OPT = SyntaxElementType("?")
  val BNF_OP_OR = SyntaxElementType("|")
  val BNF_OP_ZEROMORE = SyntaxElementType("*")
  val BNF_RIGHT_BRACE = SyntaxElementType("}")
  val BNF_RIGHT_BRACKET = SyntaxElementType("]")
  val BNF_RIGHT_PAREN = SyntaxElementType(")")
  val BNF_SEMICOLON = SyntaxElementType(";")
  val BNF_STRING = SyntaxElementType("string")
}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfTypes.java -----------------
// license.txt
package org.intellij.grammar.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.intellij.grammar.psi.impl.*;
import com.intellij.psi.impl.source.tree.CompositePsiElement;

public interface BnfTypes {

  IElementType BNF_ATTR = new BnfCompositeElementType("BNF_ATTR");
  IElementType BNF_ATTRS = new BnfCompositeElementType("BNF_ATTRS");
  IElementType BNF_ATTR_PATTERN = new BnfCompositeElementType("BNF_ATTR_PATTERN");
  IElementType BNF_CHOICE = new BnfCompositeElementType("BNF_CHOICE");
  IElementType BNF_EXPRESSION = new BnfCompositeElementType("BNF_EXPRESSION");
  IElementType BNF_EXTERNAL_EXPRESSION = new BnfCompositeElementType("BNF_EXTERNAL_EXPRESSION");
  IElementType BNF_LIST_ENTRY = new BnfCompositeElementType("BNF_LIST_ENTRY");
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
  IElementType BNF_VALUE_LIST = new BnfCompositeElementType("BNF_VALUE_LIST");

  IElementType BNF_BLOCK_COMMENT = new BnfTokenType("block_comment");
  IElementType BNF_EXTERNAL_END = new BnfTokenType(">>");
  IElementType BNF_EXTERNAL_START = new BnfTokenType("<<");
  IElementType BNF_ID = new BnfTokenType("id");
  IElementType BNF_LEFT_BRACE = new BnfTokenType("{");
  IElementType BNF_LEFT_BRACKET = new BnfTokenType("[");
  IElementType BNF_LEFT_PAREN = new BnfTokenType("(");
  IElementType BNF_LINE_COMMENT = new BnfTokenType("line_comment");
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
    public static CompositePsiElement createElement(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/BnfSyntaxElementTypeConverterFactory.java -----------------
// license.txt
package org.intellij.grammar;

import org.intellij.grammar.psi.BnfTypes;
import com.intellij.psi.tree.IElementType;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.psi.ElementTypeConverterFactory;
import com.intellij.platform.syntax.psi.ElementTypeConverter;
import com.intellij.platform.syntax.psi.ElementTypeConverterKt;
import org.jetbrains.annotations.NotNull;
import kotlin.Pair;

public class BnfSyntaxElementTypeConverterFactory implements ElementTypeConverterFactory {

  @Override
  public @NotNull ElementTypeConverter getElementTypeConverter() {
    return ElementTypeConverterKt.elementTypeConverterOf(
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_ATTR, BnfTypes.BNF_ATTR),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_ATTRS, BnfTypes.BNF_ATTRS),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_ATTR_PATTERN, BnfTypes.BNF_ATTR_PATTERN),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_CHOICE, BnfTypes.BNF_CHOICE),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_EXPRESSION, BnfTypes.BNF_EXPRESSION),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_EXTERNAL_EXPRESSION, BnfTypes.BNF_EXTERNAL_EXPRESSION),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_LIST_ENTRY, BnfTypes.BNF_LIST_ENTRY),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_LITERAL_EXPRESSION, BnfTypes.BNF_LITERAL_EXPRESSION),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_MODIFIER, BnfTypes.BNF_MODIFIER),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_PAREN_EXPRESSION, BnfTypes.BNF_PAREN_EXPRESSION),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_PAREN_OPT_EXPRESSION, BnfTypes.BNF_PAREN_OPT_EXPRESSION),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_PREDICATE, BnfTypes.BNF_PREDICATE),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_PREDICATE_SIGN, BnfTypes.BNF_PREDICATE_SIGN),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_QUANTIFIED, BnfTypes.BNF_QUANTIFIED),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_QUANTIFIER, BnfTypes.BNF_QUANTIFIER),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_REFERENCE_OR_TOKEN, BnfTypes.BNF_REFERENCE_OR_TOKEN),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_RULE, BnfTypes.BNF_RULE),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_SEQUENCE, BnfTypes.BNF_SEQUENCE),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_STRING_LITERAL_EXPRESSION, BnfTypes.BNF_STRING_LITERAL_EXPRESSION),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_VALUE_LIST, BnfTypes.BNF_VALUE_LIST),

      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_OP_EQ, BnfTypes.BNF_OP_EQ),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_OP_IS, BnfTypes.BNF_OP_IS),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_OP_OR, BnfTypes.BNF_OP_OR),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_OP_OPT, BnfTypes.BNF_OP_OPT),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_OP_ONEMORE, BnfTypes.BNF_OP_ONEMORE),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_OP_ZEROMORE, BnfTypes.BNF_OP_ZEROMORE),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_OP_AND, BnfTypes.BNF_OP_AND),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_OP_NOT, BnfTypes.BNF_OP_NOT),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_SEMICOLON, BnfTypes.BNF_SEMICOLON),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_LEFT_BRACE, BnfTypes.BNF_LEFT_BRACE),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_RIGHT_BRACE, BnfTypes.BNF_RIGHT_BRACE),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_LEFT_BRACKET, BnfTypes.BNF_LEFT_BRACKET),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_RIGHT_BRACKET, BnfTypes.BNF_RIGHT_BRACKET),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_LEFT_PAREN, BnfTypes.BNF_LEFT_PAREN),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_RIGHT_PAREN, BnfTypes.BNF_RIGHT_PAREN),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_EXTERNAL_START, BnfTypes.BNF_EXTERNAL_START),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_EXTERNAL_END, BnfTypes.BNF_EXTERNAL_END),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_ID, BnfTypes.BNF_ID),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_STRING, BnfTypes.BNF_STRING),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_NUMBER, BnfTypes.BNF_NUMBER),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_LINE_COMMENT, BnfTypes.BNF_LINE_COMMENT),
      new Pair<SyntaxElementType, IElementType>(BnfSyntaxTypes.BNF_BLOCK_COMMENT, BnfTypes.BNF_BLOCK_COMMENT)
    );
  }
}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfAttr.java -----------------
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfAttrPattern.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfAttrPattern extends BnfComposite {

  @Nullable
  BnfStringLiteralExpression getLiteralExpression();

}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfAttrs.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfAttrs extends BnfComposite {

  @NotNull
  List<BnfAttr> getAttrList();

}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfChoice.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfChoice extends BnfExpression {

  @NotNull
  List<BnfExpression> getExpressionList();

}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfExpression.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfExpression extends BnfComposite {

}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfExternalExpression.java -----------------
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfListEntry.java -----------------
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfLiteralExpression.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfLiteralExpression extends BnfExpression {

  @Nullable
  PsiElement getNumber();

}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfModifier.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfModifier extends BnfComposite {

}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfParenExpression.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfParenExpression extends BnfParenthesized {

}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfParenOptExpression.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfParenOptExpression extends BnfParenthesized {

}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfParenthesized.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfParenthesized extends BnfExpression {

  @NotNull
  BnfExpression getExpression();

}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfPredicate.java -----------------
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfPredicateSign.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfPredicateSign extends BnfComposite {

}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfQuantified.java -----------------
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfQuantifier.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfQuantifier extends BnfComposite {

}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfReferenceOrToken.java -----------------
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfRule.java -----------------
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfSequence.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfSequence extends BnfExpression {

  @NotNull
  List<BnfExpression> getExpressionList();

}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfStringLiteralExpression.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfStringLiteralExpression extends BnfLiteralExpression {

  @NotNull
  PsiElement getString();

}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfValueList.java -----------------
// license.txt
package org.intellij.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BnfValueList extends BnfExpression {

  @NotNull
  List<BnfListEntry> getListEntryList();

}
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfAttrImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfAttrImpl extends BnfNamedImpl implements BnfAttr {

  public BnfAttrImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfAttrPatternImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfAttrPatternImpl extends BnfCompositeImpl implements BnfAttrPattern {

  public BnfAttrPatternImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfAttrsImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfAttrsImpl extends BnfCompositeImpl implements BnfAttrs {

  public BnfAttrsImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfChoiceImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfChoiceImpl extends BnfExpressionImpl implements BnfChoice {

  public BnfChoiceImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfExpressionImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public abstract class BnfExpressionImpl extends BnfCompositeImpl implements BnfExpression {

  public BnfExpressionImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfExternalExpressionImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfExternalExpressionImpl extends BnfExpressionImpl implements BnfExternalExpression {

  public BnfExternalExpressionImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfListEntryImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfListEntryImpl extends BnfCompositeImpl implements BnfListEntry {

  public BnfListEntryImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfLiteralExpressionImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfLiteralExpressionImpl extends BnfExpressionImpl implements BnfLiteralExpression {

  public BnfLiteralExpressionImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfModifierImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfModifierImpl extends BnfCompositeImpl implements BnfModifier {

  public BnfModifierImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfParenExpressionImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfParenExpressionImpl extends BnfParenthesizedImpl implements BnfParenExpression {

  public BnfParenExpressionImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfParenOptExpressionImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfParenOptExpressionImpl extends BnfParenthesizedImpl implements BnfParenOptExpression {

  public BnfParenOptExpressionImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfParenthesizedImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfParenthesizedImpl extends BnfExpressionImpl implements BnfParenthesized {

  public BnfParenthesizedImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfPredicateImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfPredicateImpl extends BnfExpressionImpl implements BnfPredicate {

  public BnfPredicateImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfPredicateSignImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfPredicateSignImpl extends BnfCompositeImpl implements BnfPredicateSign {

  public BnfPredicateSignImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfQuantifiedImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfQuantifiedImpl extends BnfExpressionImpl implements BnfQuantified {

  public BnfQuantifiedImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfQuantifierImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfQuantifierImpl extends BnfCompositeImpl implements BnfQuantifier {

  public BnfQuantifierImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfReferenceOrTokenImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfReferenceOrTokenImpl extends BnfRefOrTokenImpl implements BnfReferenceOrToken {

  public BnfReferenceOrTokenImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfRuleImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfRuleImpl extends BnfNamedImpl implements BnfRule {

  public BnfRuleImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfSequenceImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfSequenceImpl extends BnfExpressionImpl implements BnfSequence {

  public BnfSequenceImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfStringLiteralExpressionImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfStringLiteralExpressionImpl extends BnfStringImpl implements BnfStringLiteralExpression {

  public BnfStringLiteralExpressionImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/impl/BnfValueListImpl.java -----------------
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
import com.intellij.psi.tree.IElementType;

public class BnfValueListImpl extends BnfExpressionImpl implements BnfValueList {

  public BnfValueListImpl(IElementType type) {
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
// ---- ../grammar-kit/psi/org/intellij/grammar/psi/BnfVisitor.java -----------------
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