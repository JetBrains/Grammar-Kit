// ---- GeneratedSyntaxElementTypes.java -----------------
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
// ---- GeneratedSyntaxElementTypeConverter.java -----------------
//header.txt
package generated;

import org.intellij.grammar.expression.ExpressionTypes;
import com.intellij.psi.tree.IElementType;
import com.intellij.platform.syntax.SyntaxElementType;
import java.util.Map;
import java.util.HashMap;
import com.intellij.platform.syntax.psi.ElementTypeConverterBase;

public class GeneratedSyntaxElementTypeConverter extends ElementTypeConverterBase {

  public GeneratedSyntaxElementTypeConverter() {
    super(makeElementMap());
  }

  private static Map<SyntaxElementType, IElementType> makeElementMap() {
    Map<SyntaxElementType, IElementType> map = new HashMap<>();
    map.put(GeneratedSyntaxElementTypes.ARG_LIST, ExpressionTypes.ARG_LIST);
    map.put(GeneratedSyntaxElementTypes.ASSIGN_EXPR, ExpressionTypes.ASSIGN_EXPR);
    map.put(GeneratedSyntaxElementTypes.BETWEEN_EXPR, ExpressionTypes.BETWEEN_EXPR);
    map.put(GeneratedSyntaxElementTypes.CALL_EXPR, ExpressionTypes.CALL_EXPR);
    map.put(GeneratedSyntaxElementTypes.CONDITIONAL_EXPR, ExpressionTypes.CONDITIONAL_EXPR);
    map.put(GeneratedSyntaxElementTypes.DIV_EXPR, ExpressionTypes.DIV_EXPR);
    map.put(GeneratedSyntaxElementTypes.ELVIS_EXPR, ExpressionTypes.ELVIS_EXPR);
    map.put(GeneratedSyntaxElementTypes.EXPR, ExpressionTypes.EXPR);
    map.put(GeneratedSyntaxElementTypes.EXP_EXPR, ExpressionTypes.EXP_EXPR);
    map.put(GeneratedSyntaxElementTypes.FACTORIAL_EXPR, ExpressionTypes.FACTORIAL_EXPR);
    map.put(GeneratedSyntaxElementTypes.IDENTIFIER, ExpressionTypes.IDENTIFIER);
    map.put(GeneratedSyntaxElementTypes.IS_NOT_EXPR, ExpressionTypes.IS_NOT_EXPR);
    map.put(GeneratedSyntaxElementTypes.LITERAL_EXPR, ExpressionTypes.LITERAL_EXPR);
    map.put(GeneratedSyntaxElementTypes.MINUS_EXPR, ExpressionTypes.MINUS_EXPR);
    map.put(GeneratedSyntaxElementTypes.MUL_EXPR, ExpressionTypes.MUL_EXPR);
    map.put(GeneratedSyntaxElementTypes.PAREN_EXPR, ExpressionTypes.PAREN_EXPR);
    map.put(GeneratedSyntaxElementTypes.PLUS_EXPR, ExpressionTypes.PLUS_EXPR);
    map.put(GeneratedSyntaxElementTypes.REF_EXPR, ExpressionTypes.REF_EXPR);
    map.put(GeneratedSyntaxElementTypes.SPECIAL_EXPR, ExpressionTypes.SPECIAL_EXPR);
    map.put(GeneratedSyntaxElementTypes.UNARY_MIN_EXPR, ExpressionTypes.UNARY_MIN_EXPR);
    map.put(GeneratedSyntaxElementTypes.UNARY_NOT_EXPR, ExpressionTypes.UNARY_NOT_EXPR);
    map.put(GeneratedSyntaxElementTypes.UNARY_PLUS_EXPR, ExpressionTypes.UNARY_PLUS_EXPR);
    map.put(GeneratedSyntaxElementTypes.XOR_EXPR, ExpressionTypes.XOR_EXPR);

    map.put(GeneratedSyntaxElementTypes.COMMENT, ExpressionTypes.COMMENT);
    map.put(GeneratedSyntaxElementTypes.NUMBER, ExpressionTypes.NUMBER);
    map.put(GeneratedSyntaxElementTypes.ID, ExpressionTypes.ID);
    map.put(GeneratedSyntaxElementTypes.STRING, ExpressionTypes.STRING);
    map.put(GeneratedSyntaxElementTypes.SYNTAX, ExpressionTypes.SYNTAX);
    map.put(GeneratedSyntaxElementTypes.BETWEEN, ExpressionTypes.BETWEEN);
    map.put(GeneratedSyntaxElementTypes.IS, ExpressionTypes.IS);
    map.put(GeneratedSyntaxElementTypes.NOT, ExpressionTypes.NOT);
    map.put(GeneratedSyntaxElementTypes.AND, ExpressionTypes.AND);
    return map;
  }
}