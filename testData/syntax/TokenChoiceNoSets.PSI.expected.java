// ---- GeneratedSyntaxElementTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.platform.syntax.SyntaxElementType;

public interface GeneratedSyntaxElementTypes {

  SyntaxElementType INNER_CHOICE = new SyntaxElementType("INNER_CHOICE");
  SyntaxElementType INNER_PARENTHESIZED_CHOICE = new SyntaxElementType("INNER_PARENTHESIZED_CHOICE");
  SyntaxElementType TEXT_TOKEN_CHOICE = new SyntaxElementType("TEXT_TOKEN_CHOICE");
  SyntaxElementType TWO_TOKENS_CHOICE = new SyntaxElementType("TWO_TOKENS_CHOICE");
  SyntaxElementType TWO_TOKENS_REPEATING_CHOICE = new SyntaxElementType("TWO_TOKENS_REPEATING_CHOICE");

  SyntaxElementType A = new SyntaxElementType("A");
  SyntaxElementType B = new SyntaxElementType("B");
  SyntaxElementType C = new SyntaxElementType("C");
  SyntaxElementType D = new SyntaxElementType("D");
}
// ---- GeneratedSyntaxElementTypeConverter.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

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
    map.put(GeneratedSyntaxElementTypes.INNER_CHOICE, GeneratedTypes.INNER_CHOICE);
    map.put(GeneratedSyntaxElementTypes.INNER_PARENTHESIZED_CHOICE, GeneratedTypes.INNER_PARENTHESIZED_CHOICE);
    map.put(GeneratedSyntaxElementTypes.TEXT_TOKEN_CHOICE, GeneratedTypes.TEXT_TOKEN_CHOICE);
    map.put(GeneratedSyntaxElementTypes.TWO_TOKENS_CHOICE, GeneratedTypes.TWO_TOKENS_CHOICE);
    map.put(GeneratedSyntaxElementTypes.TWO_TOKENS_REPEATING_CHOICE, GeneratedTypes.TWO_TOKENS_REPEATING_CHOICE);

    map.put(GeneratedSyntaxElementTypes.D, GeneratedTypes.D);
    map.put(GeneratedSyntaxElementTypes.A, GeneratedTypes.A);
    map.put(GeneratedSyntaxElementTypes.B, GeneratedTypes.B);
    map.put(GeneratedSyntaxElementTypes.C, GeneratedTypes.C);
    return map;
  }
}