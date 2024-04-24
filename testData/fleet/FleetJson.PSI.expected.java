// ---- JsonElementTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package fleet.com.intellij.json;

import fleet.com.intellij.psi.tree.IElementType;

public interface JsonElementTypes {

  IElementType ARRAY = new JsonElementType("ARRAY");
  IElementType BOOLEAN_LITERAL = new JsonElementType("BOOLEAN_LITERAL");
  IElementType LITERAL = new JsonElementType("LITERAL");
  IElementType NULL_LITERAL = new JsonElementType("NULL_LITERAL");
  IElementType NUMBER_LITERAL = new JsonElementType("NUMBER_LITERAL");
  IElementType OBJECT = new JsonElementType("OBJECT");
  IElementType PROPERTY = new JsonElementType("PROPERTY");
  IElementType REFERENCE_EXPRESSION = new JsonElementType("REFERENCE_EXPRESSION");
  IElementType STRING_LITERAL = new JsonElementType("STRING_LITERAL");
  IElementType VALUE = new JsonElementType("VALUE");

  IElementType BLOCK_COMMENT = new JsonTokenType("BLOCK_COMMENT");
  IElementType COLON = new JsonTokenType(":");
  IElementType COMMA = new JsonTokenType(",");
  IElementType DOUBLE_QUOTED_STRING = new JsonTokenType("DOUBLE_QUOTED_STRING");
  IElementType FALSE = new JsonTokenType("false");
  IElementType IDENTIFIER = new JsonTokenType("IDENTIFIER");
  IElementType LINE_COMMENT = new JsonTokenType("LINE_COMMENT");
  IElementType L_BRACKET = new JsonTokenType("[");
  IElementType L_CURLY = new JsonTokenType("{");
  IElementType NULL = new JsonTokenType("null");
  IElementType NUMBER = new JsonTokenType("NUMBER");
  IElementType R_BRACKET = new JsonTokenType("]");
  IElementType R_CURLY = new JsonTokenType("}");
  IElementType SINGLE_QUOTED_STRING = new JsonTokenType("SINGLE_QUOTED_STRING");
  IElementType TRUE = new JsonTokenType("true");
}
// ---- JsonFileType.java -----------------
// This is a generated file. Not intended for manual editing.
package fleet.com.intellij.json.psi;

import fleet.com.intellij.json.JsonLanguage;
import fleet.com.intellij.json.JsonParser;
import fleet.com.intellij.psi.tree.IFileElementType;
import org.jetbrains.annotations.NotNull;
import fleet.com.intellij.psi.builder.FleetPsiBuilder;

public class JsonFileType extends IFileElementType {

  public static final JsonFileType INSTANCE = new JsonFileType();

  public JsonFileType() {
    super("JSON", JsonLanguage.INSTANCE)
  }

  @Override
  public void parse(@NotNull FleetPsiBuilder<?> builder) {
    new JsonParser().parseLight(this, builder);
  }
}