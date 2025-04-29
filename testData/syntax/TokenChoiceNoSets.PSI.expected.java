// ---- generated/GeneratedSyntaxElementTypes.java -----------------
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
// ---- generated/GeneratedSyntaxElementTypeConverterFactory.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

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
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.INNER_CHOICE, GeneratedTypes.INNER_CHOICE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.INNER_PARENTHESIZED_CHOICE, GeneratedTypes.INNER_PARENTHESIZED_CHOICE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.TEXT_TOKEN_CHOICE, GeneratedTypes.TEXT_TOKEN_CHOICE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.TWO_TOKENS_CHOICE, GeneratedTypes.TWO_TOKENS_CHOICE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.TWO_TOKENS_REPEATING_CHOICE, GeneratedTypes.TWO_TOKENS_REPEATING_CHOICE),

      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.D, GeneratedTypes.D),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.A, GeneratedTypes.A),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.B, GeneratedTypes.B),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.C, GeneratedTypes.C)
    );
  }
}