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