// ---- GeneratedTypes.java -----------------
//header.txt
package fleet.generated;

import fleet.com.intellij.psi.tree.IElementType;
import fleet.sample.MyTypeFactory;
import fleet.sample.MyRootType;

public interface GeneratedTypes {

  IElementType A_STATEMENT = new IElementType("A_STATEMENT", null);
  IElementType BLOCK_OF = new IElementType("BLOCK_OF", null);
  IElementType B_STATEMENT = new IElementType("B_STATEMENT", null);
  IElementType CAST_EXPR = MyTypeFactory.createExprType("CAST_EXPR");
  IElementType CHOICE_JOINED = new IElementType("CHOICE_JOINED", null);
  IElementType C_STATEMENT = new IElementType("C_STATEMENT", null);
  IElementType EXPR = new IElementType("EXPR", null);
  IElementType GRAMMAR_ELEMENT = new IElementType("GRAMMAR_ELEMENT", null);
  IElementType IDENTIFIER = new IElementType("IDENTIFIER", null);
  IElementType ID_EXPR = new IElementType("ID_EXPR", null);
  IElementType INCLUDE_SECTION = new IElementType("INCLUDE_SECTION", null);
  IElementType INCLUDE__SECTION__ALT = new IElementType("INCLUDE__SECTION__ALT", null);
  IElementType ITEM_EXPR = MyTypeFactory.createExprType("ITEM_EXPR");
  IElementType LEFT_SHADOW = new IElementType("LEFT_SHADOW", null);
  IElementType LEFT_SHADOW_TEST = new IElementType("LEFT_SHADOW_TEST", null);
  IElementType LITERAL = new IElementType("LITERAL", null);
  IElementType MISSING_EXTERNAL_TYPE = new IElementType("MISSING_EXTERNAL_TYPE", null);
  IElementType MUL_EXPR = MyTypeFactory.createExprType("MUL_EXPR");
  IElementType PLUS_EXPR = MyTypeFactory.createExprType("PLUS_EXPR");
  IElementType REF_EXPR = MyTypeFactory.createExprType("REF_EXPR");
  IElementType ROOT = new IElementType("ROOT", null);
  IElementType ROOT_B = new MyRootType("ROOT_B");
  IElementType ROOT_C = new MyRootType("ROOT_C");
  IElementType ROOT_D = new MyRootType("ROOT_D");
  IElementType SOME_EXPR = MyTypeFactory.createExprType("SOME_EXPR");
  IElementType SPECIAL_REF = new IElementType("SPECIAL_REF", null);
  IElementType STATEMENT = new IElementType("STATEMENT", null);

  IElementType ID = new IElementType("id", null);
  IElementType NOTSPACE = new IElementType("notspace", null);
  IElementType NUMBER = new IElementType("number", null);
  IElementType OF = new IElementType("OF", null);
  IElementType OP_DIV = new IElementType("/", null);
  IElementType OP_MUL = new IElementType("*", null);
  IElementType SLASH = new IElementType("\\", null);
}