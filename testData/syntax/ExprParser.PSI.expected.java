// ---- generated/GeneratedSyntaxElementTypes.java -----------------
//header.txt
package generated;

import com.intellij.platform.syntax.SyntaxElementType;

public interface GeneratedSyntaxElementTypes {

  SyntaxElementType ARG_LIST = new SyntaxElementType("ARG_LIST");
  SyntaxElementType ASSIGN_EXPR = new SyntaxElementType("ASSIGN_EXPR");
  SyntaxElementType BETWEEN_EXPR = new SyntaxElementType("BETWEEN_EXPR");
  SyntaxElementType CALL_EXPR = new SyntaxElementType("CALL_EXPR");
  SyntaxElementType CONDITIONAL_EXPR = new SyntaxElementType("CONDITIONAL_EXPR");
  SyntaxElementType DIV_EXPR = new SyntaxElementType("DIV_EXPR");
  SyntaxElementType ELVIS_EXPR = new SyntaxElementType("ELVIS_EXPR");
  SyntaxElementType EXPR = new SyntaxElementType("EXPR");
  SyntaxElementType EXP_EXPR = new SyntaxElementType("EXP_EXPR");
  SyntaxElementType FACTORIAL_EXPR = new SyntaxElementType("FACTORIAL_EXPR");
  SyntaxElementType IDENTIFIER = new SyntaxElementType("IDENTIFIER");
  SyntaxElementType IS_NOT_EXPR = new SyntaxElementType("IS_NOT_EXPR");
  SyntaxElementType LITERAL_EXPR = new SyntaxElementType("LITERAL_EXPR");
  SyntaxElementType MINUS_EXPR = new SyntaxElementType("MINUS_EXPR");
  SyntaxElementType MUL_EXPR = new SyntaxElementType("MUL_EXPR");
  SyntaxElementType PAREN_EXPR = new SyntaxElementType("PAREN_EXPR");
  SyntaxElementType PLUS_EXPR = new SyntaxElementType("PLUS_EXPR");
  SyntaxElementType REF_EXPR = new SyntaxElementType("REF_EXPR");
  SyntaxElementType SPECIAL_EXPR = new SyntaxElementType("SPECIAL_EXPR");
  SyntaxElementType UNARY_MIN_EXPR = new SyntaxElementType("UNARY_MIN_EXPR");
  SyntaxElementType UNARY_NOT_EXPR = new SyntaxElementType("UNARY_NOT_EXPR");
  SyntaxElementType UNARY_PLUS_EXPR = new SyntaxElementType("UNARY_PLUS_EXPR");
  SyntaxElementType XOR_EXPR = new SyntaxElementType("XOR_EXPR");

  SyntaxElementType AND = new SyntaxElementType("AND");
  SyntaxElementType BETWEEN = new SyntaxElementType("BETWEEN");
  SyntaxElementType COMMENT = new SyntaxElementType("comment");
  SyntaxElementType ID = new SyntaxElementType("id");
  SyntaxElementType IS = new SyntaxElementType("IS");
  SyntaxElementType NOT = new SyntaxElementType("NOT");
  SyntaxElementType NUMBER = new SyntaxElementType("number");
  SyntaxElementType STRING = new SyntaxElementType("string");
  SyntaxElementType SYNTAX = new SyntaxElementType("syntax");
}
// ---- generated/GeneratedSyntaxElementTypeConverterFactory.java -----------------
//header.txt
package generated;

import org.intellij.grammar.expression.ExpressionTypes;
import com.intellij.psi.tree.IElementType;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.psi.ElementTypeConverterFactory;
import com.intellij.platform.syntax.psi.ElementTypeConverter;
import com.intellij.platform.syntax.psi.ElementTypeConverterKt;
import org.jetbrains.annotations.NotNull;
import kotlin.Pair;

public class GeneratedSyntaxElementTypeConverterFactory implements ElementTypeConverterFactory {

  @Override
  public @NotNull ElementTypeConverter getElementTypeConverter() {
    return ElementTypeConverterKt.elementTypeConverterOf(
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ARG_LIST, ExpressionTypes.ARG_LIST),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ASSIGN_EXPR, ExpressionTypes.ASSIGN_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.BETWEEN_EXPR, ExpressionTypes.BETWEEN_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.CALL_EXPR, ExpressionTypes.CALL_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.CONDITIONAL_EXPR, ExpressionTypes.CONDITIONAL_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.DIV_EXPR, ExpressionTypes.DIV_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ELVIS_EXPR, ExpressionTypes.ELVIS_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.EXPR, ExpressionTypes.EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.EXP_EXPR, ExpressionTypes.EXP_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.FACTORIAL_EXPR, ExpressionTypes.FACTORIAL_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.IDENTIFIER, ExpressionTypes.IDENTIFIER),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.IS_NOT_EXPR, ExpressionTypes.IS_NOT_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.LITERAL_EXPR, ExpressionTypes.LITERAL_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.MINUS_EXPR, ExpressionTypes.MINUS_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.MUL_EXPR, ExpressionTypes.MUL_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.PAREN_EXPR, ExpressionTypes.PAREN_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.PLUS_EXPR, ExpressionTypes.PLUS_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.REF_EXPR, ExpressionTypes.REF_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.SPECIAL_EXPR, ExpressionTypes.SPECIAL_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.UNARY_MIN_EXPR, ExpressionTypes.UNARY_MIN_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.UNARY_NOT_EXPR, ExpressionTypes.UNARY_NOT_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.UNARY_PLUS_EXPR, ExpressionTypes.UNARY_PLUS_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.XOR_EXPR, ExpressionTypes.XOR_EXPR),

      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.COMMENT, ExpressionTypes.COMMENT),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.NUMBER, ExpressionTypes.NUMBER),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ID, ExpressionTypes.ID),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.STRING, ExpressionTypes.STRING),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.SYNTAX, ExpressionTypes.SYNTAX),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.BETWEEN, ExpressionTypes.BETWEEN),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.IS, ExpressionTypes.IS),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.NOT, ExpressionTypes.NOT),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.AND, ExpressionTypes.AND)
    );
  }
}