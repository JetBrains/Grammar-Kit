// ---- GeneratedTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public interface GeneratedTypes {

  IElementType ANOTHER_THREE_TOKENS = new IElementType("ANOTHER_THREE_TOKENS", null);
  IElementType FAST_CHOICE = new IElementType("FAST_CHOICE", null);
  IElementType FIVE_TOKENS_CHOICE = new IElementType("FIVE_TOKENS_CHOICE", null);
  IElementType FOUR_TOKENS_CHOICE = new IElementType("FOUR_TOKENS_CHOICE", null);
  IElementType PARENTHESIZED_CHOICE = new IElementType("PARENTHESIZED_CHOICE", null);
  IElementType REPEATING_TOKENS_CHOICE = new IElementType("REPEATING_TOKENS_CHOICE", null);
  IElementType SMART_CHOICE = new IElementType("SMART_CHOICE", null);
  IElementType SOME = new IElementType("SOME", null);
  IElementType TEN_TOKENS_CHOICE = new IElementType("TEN_TOKENS_CHOICE", null);
  IElementType THREE_TOKENS_CHOICE = new IElementType("THREE_TOKENS_CHOICE", null);
  IElementType THREE_TOKENS_IN_ANOTHER_ORDER = new IElementType("THREE_TOKENS_IN_ANOTHER_ORDER", null);

  IElementType A = new IElementType("A", null);
  IElementType B = new IElementType("B", null);
  IElementType C = new IElementType("C", null);
  IElementType D = new IElementType("D", null);
  IElementType E = new IElementType("E", null);
  IElementType F = new IElementType("F", null);
  IElementType G = new IElementType("G", null);
  IElementType H = new IElementType("H", null);
  IElementType I = new IElementType("I", null);
  IElementType J = new IElementType("J", null);
  IElementType P0 = new IElementType("P0", null);
  IElementType P1 = new IElementType("P1", null);
  IElementType P2 = new IElementType("P2", null);
  IElementType P3 = new IElementType("P3", null);
  IElementType S = new IElementType("S", null);

  interface TokenSets {
    TokenSet ANOTHER_THREE_TOKENS_TOKENS = TokenSet.create(A, B, D);
    TokenSet FAST_CHOICE_TOKENS = TokenSet.create(A, B, F);
    TokenSet FIVE_TOKENS_CHOICE_TOKENS = TokenSet.create(
        A, B, C, D, 
        E
    );
    TokenSet FOUR_TOKENS_CHOICE_TOKENS = TokenSet.create(A, B, C, D);
    TokenSet PARENTHESIZED_CHOICE_TOKENS = TokenSet.create(A, B, C);
    TokenSet PRIVATE_CHOICE_TOKENS = TokenSet.create(P0, P1, P2, P3);
    TokenSet REPEATING_TOKENS_CHOICE_TOKENS = FOUR_TOKENS_CHOICE_TOKENS;
    TokenSet SMART_CHOICE_TOKENS = TokenSet.create(A, B, S);
    TokenSet TEN_TOKENS_CHOICE_TOKENS = TokenSet.create(
        A, B, C, D, 
        E, F, G, H, 
        I, J
    );
    TokenSet THREE_TOKENS_CHOICE_TOKENS = PARENTHESIZED_CHOICE_TOKENS;
    TokenSet THREE_TOKENS_IN_ANOTHER_ORDER_TOKENS = PARENTHESIZED_CHOICE_TOKENS;
  }
}
// ---- GeneratedSyntaxElementTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.platform.syntax.SyntaxElementType;
import java.util.Set;

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
    Set<SyntaxElementType> ANOTHER_THREE_TOKENS_TOKENS = Set.of(A, B, D);
    Set<SyntaxElementType> FAST_CHOICE_TOKENS = Set.of(A, B, F);
    Set<SyntaxElementType> FIVE_TOKENS_CHOICE_TOKENS = Set.of(
        A, B, C, D, 
        E
    );
    Set<SyntaxElementType> FOUR_TOKENS_CHOICE_TOKENS = Set.of(A, B, C, D);
    Set<SyntaxElementType> PARENTHESIZED_CHOICE_TOKENS = Set.of(A, B, C);
    Set<SyntaxElementType> PRIVATE_CHOICE_TOKENS = Set.of(P0, P1, P2, P3);
    Set<SyntaxElementType> REPEATING_TOKENS_CHOICE_TOKENS = FOUR_TOKENS_CHOICE_TOKENS;
    Set<SyntaxElementType> SMART_CHOICE_TOKENS = Set.of(A, B, S);
    Set<SyntaxElementType> TEN_TOKENS_CHOICE_TOKENS = Set.of(
        A, B, C, D, 
        E, F, G, H, 
        I, J
    );
    Set<SyntaxElementType> THREE_TOKENS_CHOICE_TOKENS = PARENTHESIZED_CHOICE_TOKENS;
    Set<SyntaxElementType> THREE_TOKENS_IN_ANOTHER_ORDER_TOKENS = PARENTHESIZED_CHOICE_TOKENS;
  }
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
    map.put(GeneratedSyntaxElementTypes.ANOTHER_THREE_TOKENS, GeneratedTypes.ANOTHER_THREE_TOKENS);
    map.put(GeneratedSyntaxElementTypes.FAST_CHOICE, GeneratedTypes.FAST_CHOICE);
    map.put(GeneratedSyntaxElementTypes.FIVE_TOKENS_CHOICE, GeneratedTypes.FIVE_TOKENS_CHOICE);
    map.put(GeneratedSyntaxElementTypes.FOUR_TOKENS_CHOICE, GeneratedTypes.FOUR_TOKENS_CHOICE);
    map.put(GeneratedSyntaxElementTypes.PARENTHESIZED_CHOICE, GeneratedTypes.PARENTHESIZED_CHOICE);
    map.put(GeneratedSyntaxElementTypes.REPEATING_TOKENS_CHOICE, GeneratedTypes.REPEATING_TOKENS_CHOICE);
    map.put(GeneratedSyntaxElementTypes.SMART_CHOICE, GeneratedTypes.SMART_CHOICE);
    map.put(GeneratedSyntaxElementTypes.SOME, GeneratedTypes.SOME);
    map.put(GeneratedSyntaxElementTypes.TEN_TOKENS_CHOICE, GeneratedTypes.TEN_TOKENS_CHOICE);
    map.put(GeneratedSyntaxElementTypes.THREE_TOKENS_CHOICE, GeneratedTypes.THREE_TOKENS_CHOICE);
    map.put(GeneratedSyntaxElementTypes.THREE_TOKENS_IN_ANOTHER_ORDER, GeneratedTypes.THREE_TOKENS_IN_ANOTHER_ORDER);

    map.put(GeneratedSyntaxElementTypes.D, GeneratedTypes.D);
    map.put(GeneratedSyntaxElementTypes.A, GeneratedTypes.A);
    map.put(GeneratedSyntaxElementTypes.B, GeneratedTypes.B);
    map.put(GeneratedSyntaxElementTypes.F, GeneratedTypes.F);
    map.put(GeneratedSyntaxElementTypes.C, GeneratedTypes.C);
    map.put(GeneratedSyntaxElementTypes.E, GeneratedTypes.E);
    map.put(GeneratedSyntaxElementTypes.P2, GeneratedTypes.P2);
    map.put(GeneratedSyntaxElementTypes.P3, GeneratedTypes.P3);
    map.put(GeneratedSyntaxElementTypes.P0, GeneratedTypes.P0);
    map.put(GeneratedSyntaxElementTypes.P1, GeneratedTypes.P1);
    map.put(GeneratedSyntaxElementTypes.S, GeneratedTypes.S);
    map.put(GeneratedSyntaxElementTypes.G, GeneratedTypes.G);
    map.put(GeneratedSyntaxElementTypes.H, GeneratedTypes.H);
    map.put(GeneratedSyntaxElementTypes.I, GeneratedTypes.I);
    map.put(GeneratedSyntaxElementTypes.J, GeneratedTypes.J);
    return map;
  }
}