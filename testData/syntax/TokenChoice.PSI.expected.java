// ---- generated/GeneratedSyntaxElementTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.SyntaxElementTypeSet;
import com.intellij.platform.syntax.SyntaxElementTypeSetKt;

public interface GeneratedSyntaxElementTypes {

  SyntaxElementType ANOTHER_THREE_TOKENS = new SyntaxElementType("ANOTHER_THREE_TOKENS");
  SyntaxElementType FAST_CHOICE = new SyntaxElementType("FAST_CHOICE");
  SyntaxElementType FIVE_TOKENS_CHOICE = new SyntaxElementType("FIVE_TOKENS_CHOICE");
  SyntaxElementType FOUR_TOKENS_CHOICE = new SyntaxElementType("FOUR_TOKENS_CHOICE");
  SyntaxElementType PARENTHESIZED_CHOICE = new SyntaxElementType("PARENTHESIZED_CHOICE");
  SyntaxElementType REPEATING_TOKENS_CHOICE = new SyntaxElementType("REPEATING_TOKENS_CHOICE");
  SyntaxElementType SMART_CHOICE = new SyntaxElementType("SMART_CHOICE");
  SyntaxElementType SOME = new SyntaxElementType("SOME");
  SyntaxElementType TEN_TOKENS_CHOICE = new SyntaxElementType("TEN_TOKENS_CHOICE");
  SyntaxElementType THREE_TOKENS_CHOICE = new SyntaxElementType("THREE_TOKENS_CHOICE");
  SyntaxElementType THREE_TOKENS_IN_ANOTHER_ORDER = new SyntaxElementType("THREE_TOKENS_IN_ANOTHER_ORDER");

  SyntaxElementType A = new SyntaxElementType("A");
  SyntaxElementType B = new SyntaxElementType("B");
  SyntaxElementType C = new SyntaxElementType("C");
  SyntaxElementType D = new SyntaxElementType("D");
  SyntaxElementType E = new SyntaxElementType("E");
  SyntaxElementType F = new SyntaxElementType("F");
  SyntaxElementType G = new SyntaxElementType("G");
  SyntaxElementType H = new SyntaxElementType("H");
  SyntaxElementType I = new SyntaxElementType("I");
  SyntaxElementType J = new SyntaxElementType("J");
  SyntaxElementType P0 = new SyntaxElementType("P0");
  SyntaxElementType P1 = new SyntaxElementType("P1");
  SyntaxElementType P2 = new SyntaxElementType("P2");
  SyntaxElementType P3 = new SyntaxElementType("P3");
  SyntaxElementType S = new SyntaxElementType("S");

  interface TokenSets {
    SyntaxElementTypeSet ANOTHER_THREE_TOKENS_TOKENS = SyntaxElementTypeSetKt.syntaxElementTypeSetOf(A, B, D);
    SyntaxElementTypeSet FAST_CHOICE_TOKENS = SyntaxElementTypeSetKt.syntaxElementTypeSetOf(A, B, F);
    SyntaxElementTypeSet FIVE_TOKENS_CHOICE_TOKENS = SyntaxElementTypeSetKt.syntaxElementTypeSetOf(
        A, B, C, D, 
        E
    );
    SyntaxElementTypeSet FOUR_TOKENS_CHOICE_TOKENS = SyntaxElementTypeSetKt.syntaxElementTypeSetOf(A, B, C, D);
    SyntaxElementTypeSet PARENTHESIZED_CHOICE_TOKENS = SyntaxElementTypeSetKt.syntaxElementTypeSetOf(A, B, C);
    SyntaxElementTypeSet PRIVATE_CHOICE_TOKENS = SyntaxElementTypeSetKt.syntaxElementTypeSetOf(P0, P1, P2, P3);
    SyntaxElementTypeSet REPEATING_TOKENS_CHOICE_TOKENS = FOUR_TOKENS_CHOICE_TOKENS;
    SyntaxElementTypeSet SMART_CHOICE_TOKENS = SyntaxElementTypeSetKt.syntaxElementTypeSetOf(A, B, S);
    SyntaxElementTypeSet TEN_TOKENS_CHOICE_TOKENS = SyntaxElementTypeSetKt.syntaxElementTypeSetOf(
        A, B, C, D, 
        E, F, G, H, 
        I, J
    );
    SyntaxElementTypeSet THREE_TOKENS_CHOICE_TOKENS = PARENTHESIZED_CHOICE_TOKENS;
    SyntaxElementTypeSet THREE_TOKENS_IN_ANOTHER_ORDER_TOKENS = PARENTHESIZED_CHOICE_TOKENS;
  }
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
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ANOTHER_THREE_TOKENS, GeneratedTypes.ANOTHER_THREE_TOKENS),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.FAST_CHOICE, GeneratedTypes.FAST_CHOICE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.FIVE_TOKENS_CHOICE, GeneratedTypes.FIVE_TOKENS_CHOICE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.FOUR_TOKENS_CHOICE, GeneratedTypes.FOUR_TOKENS_CHOICE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.PARENTHESIZED_CHOICE, GeneratedTypes.PARENTHESIZED_CHOICE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.REPEATING_TOKENS_CHOICE, GeneratedTypes.REPEATING_TOKENS_CHOICE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.SMART_CHOICE, GeneratedTypes.SMART_CHOICE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.SOME, GeneratedTypes.SOME),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.TEN_TOKENS_CHOICE, GeneratedTypes.TEN_TOKENS_CHOICE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.THREE_TOKENS_CHOICE, GeneratedTypes.THREE_TOKENS_CHOICE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.THREE_TOKENS_IN_ANOTHER_ORDER, GeneratedTypes.THREE_TOKENS_IN_ANOTHER_ORDER),

      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.D, GeneratedTypes.D),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.A, GeneratedTypes.A),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.B, GeneratedTypes.B),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.F, GeneratedTypes.F),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.C, GeneratedTypes.C),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.E, GeneratedTypes.E),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.P2, GeneratedTypes.P2),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.P3, GeneratedTypes.P3),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.P0, GeneratedTypes.P0),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.P1, GeneratedTypes.P1),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.S, GeneratedTypes.S),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.G, GeneratedTypes.G),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.H, GeneratedTypes.H),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.I, GeneratedTypes.I),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.J, GeneratedTypes.J)
    );
  }
}