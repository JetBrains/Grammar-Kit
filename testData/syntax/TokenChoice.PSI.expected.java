// ---- GeneratedTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.platform.syntax.SyntaxElementType;
import java.util.Set;

public interface GeneratedTypes {

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