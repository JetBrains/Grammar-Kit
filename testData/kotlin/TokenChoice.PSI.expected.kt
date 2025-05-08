// ---- generated/GeneratedSyntaxElementTypes.kt -----------------
// This is a generated file. Not intended for manual editing.
package generated

import com.intellij.platform.syntax.SyntaxElementType

object GeneratedSyntaxElementTypes {
  val ANOTHER_THREE_TOKENS = SyntaxElementType("ANOTHER_THREE_TOKENS")
  val FAST_CHOICE = SyntaxElementType("FAST_CHOICE")
  val FIVE_TOKENS_CHOICE = SyntaxElementType("FIVE_TOKENS_CHOICE")
  val FOUR_TOKENS_CHOICE = SyntaxElementType("FOUR_TOKENS_CHOICE")
  val PARENTHESIZED_CHOICE = SyntaxElementType("PARENTHESIZED_CHOICE")
  val REPEATING_TOKENS_CHOICE = SyntaxElementType("REPEATING_TOKENS_CHOICE")
  val SMART_CHOICE = SyntaxElementType("SMART_CHOICE")
  val SOME = SyntaxElementType("SOME")
  val TEN_TOKENS_CHOICE = SyntaxElementType("TEN_TOKENS_CHOICE")
  val THREE_TOKENS_CHOICE = SyntaxElementType("THREE_TOKENS_CHOICE")
  val THREE_TOKENS_IN_ANOTHER_ORDER = SyntaxElementType("THREE_TOKENS_IN_ANOTHER_ORDER")

  val A = SyntaxElementType("A")
  val B = SyntaxElementType("B")
  val C = SyntaxElementType("C")
  val D = SyntaxElementType("D")
  val E = SyntaxElementType("E")
  val F = SyntaxElementType("F")
  val G = SyntaxElementType("G")
  val H = SyntaxElementType("H")
  val I = SyntaxElementType("I")
  val J = SyntaxElementType("J")
  val P0 = SyntaxElementType("P0")
  val P1 = SyntaxElementType("P1")
  val P2 = SyntaxElementType("P2")
  val P3 = SyntaxElementType("P3")
  val S = SyntaxElementType("S")

  val ANOTHER_THREE_TOKENS_TOKENS: Set<SyntaxElementType> = setOf(A, B, D)
  val FAST_CHOICE_TOKENS: Set<SyntaxElementType> = setOf(A, B, F)
  val FIVE_TOKENS_CHOICE_TOKENS: Set<SyntaxElementType> = setOf(
      A, B, C, D, 
      E
  )
  val FOUR_TOKENS_CHOICE_TOKENS: Set<SyntaxElementType> = setOf(A, B, C, D)
  val PARENTHESIZED_CHOICE_TOKENS: Set<SyntaxElementType> = setOf(A, B, C)
  val PRIVATE_CHOICE_TOKENS: Set<SyntaxElementType> = setOf(P0, P1, P2, P3)
  val REPEATING_TOKENS_CHOICE_TOKENS: Set<SyntaxElementType> = FOUR_TOKENS_CHOICE_TOKENS
  val SMART_CHOICE_TOKENS: Set<SyntaxElementType> = setOf(A, B, S)
  val TEN_TOKENS_CHOICE_TOKENS: Set<SyntaxElementType> = setOf(
      A, B, C, D, 
      E, F, G, H, 
      I, J
  )
  val THREE_TOKENS_CHOICE_TOKENS: Set<SyntaxElementType> = PARENTHESIZED_CHOICE_TOKENS
  val THREE_TOKENS_IN_ANOTHER_ORDER_TOKENS: Set<SyntaxElementType> = PARENTHESIZED_CHOICE_TOKENS
}