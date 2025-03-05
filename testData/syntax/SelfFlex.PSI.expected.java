// ---- JFlexTypes.java -----------------
// license.txt
package org.intellij.jflex.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.intellij.jflex.psi.impl.*;
import com.intellij.psi.impl.source.tree.CompositePsiElement;

public interface JFlexTypes {

  IElementType FLEX_CHAR_RANGE = new JFlexCompositeElementType("FLEX_CHAR_RANGE");
  IElementType FLEX_CHOICE_EXPRESSION = new JFlexCompositeElementType("FLEX_CHOICE_EXPRESSION");
  IElementType FLEX_CLASS_EXPRESSION = new JFlexCompositeElementType("FLEX_CLASS_EXPRESSION");
  IElementType FLEX_DECLARATIONS_SECTION = new JFlexCompositeElementType("FLEX_DECLARATIONS_SECTION");
  IElementType FLEX_EXPRESSION = new JFlexCompositeElementType("FLEX_EXPRESSION");
  IElementType FLEX_JAVA_CODE = new JFlexCompositeElementType("FLEX_JAVA_CODE");
  IElementType FLEX_JAVA_NAME = new JFlexCompositeElementType("FLEX_JAVA_NAME");
  IElementType FLEX_JAVA_TYPE = new JFlexCompositeElementType("FLEX_JAVA_TYPE");
  IElementType FLEX_JAVA_TYPE_PARAMETERS = new JFlexCompositeElementType("FLEX_JAVA_TYPE_PARAMETERS");
  IElementType FLEX_LEXICAL_RULES_SECTION = new JFlexCompositeElementType("FLEX_LEXICAL_RULES_SECTION");
  IElementType FLEX_LITERAL_EXPRESSION = new JFlexCompositeElementType("FLEX_LITERAL_EXPRESSION");
  IElementType FLEX_LOOK_AHEAD = new JFlexCompositeElementType("FLEX_LOOK_AHEAD");
  IElementType FLEX_MACRO_DEFINITION = new JFlexCompositeElementType("FLEX_MACRO_DEFINITION");
  IElementType FLEX_MACRO_REFERENCE = new JFlexCompositeElementType("FLEX_MACRO_REFERENCE");
  IElementType FLEX_MACRO_REF_EXPRESSION = new JFlexCompositeElementType("FLEX_MACRO_REF_EXPRESSION");
  IElementType FLEX_NOT_EXPRESSION = new JFlexCompositeElementType("FLEX_NOT_EXPRESSION");
  IElementType FLEX_OPTION = new JFlexCompositeElementType("FLEX_OPTION");
  IElementType FLEX_PAREN_EXPRESSION = new JFlexCompositeElementType("FLEX_PAREN_EXPRESSION");
  IElementType FLEX_PREDEFINED_CLASS_EXPRESSION = new JFlexCompositeElementType("FLEX_PREDEFINED_CLASS_EXPRESSION");
  IElementType FLEX_QUANTIFIER_EXPRESSION = new JFlexCompositeElementType("FLEX_QUANTIFIER_EXPRESSION");
  IElementType FLEX_RULE = new JFlexCompositeElementType("FLEX_RULE");
  IElementType FLEX_SEQUENCE_EXPRESSION = new JFlexCompositeElementType("FLEX_SEQUENCE_EXPRESSION");
  IElementType FLEX_STATE_DECLARATION = new JFlexCompositeElementType("FLEX_STATE_DECLARATION");
  IElementType FLEX_STATE_DEFINITION = new JFlexCompositeElementType("FLEX_STATE_DEFINITION");
  IElementType FLEX_STATE_LIST = new JFlexCompositeElementType("FLEX_STATE_LIST");
  IElementType FLEX_STATE_REFERENCE = new JFlexCompositeElementType("FLEX_STATE_REFERENCE");
  IElementType FLEX_USER_CODE_SECTION = new JFlexCompositeElementType("FLEX_USER_CODE_SECTION");
  IElementType FLEX_USER_VALUE = new JFlexCompositeElementType("FLEX_USER_VALUE");

  IElementType FLEX_AMPAMP = new JFlexTokenType("&&");
  IElementType FLEX_ANGLE1 = new JFlexTokenType("<");
  IElementType FLEX_ANGLE2 = new JFlexTokenType(">");
  IElementType FLEX_BANG = new JFlexTokenType("!");
  IElementType FLEX_BAR = new JFlexTokenType("|");
  IElementType FLEX_BARBAR = new JFlexTokenType("||");
  IElementType FLEX_BLOCK_COMMENT = new JFlexTokenType("block_comment");
  IElementType FLEX_BRACE1 = new JFlexTokenType("{");
  IElementType FLEX_BRACE2 = new JFlexTokenType("}");
  IElementType FLEX_BRACK1 = new JFlexTokenType("[");
  IElementType FLEX_BRACK2 = new JFlexTokenType("]");
  IElementType FLEX_CHAR = new JFlexTokenType("char");
  IElementType FLEX_CHAR_CLASS = new JFlexTokenType("char_class");
  IElementType FLEX_CHAR_ESC = new JFlexTokenType("char_esc");
  IElementType FLEX_COMMA = new JFlexTokenType(",");
  IElementType FLEX_DASH = new JFlexTokenType("-");
  IElementType FLEX_DASHDASH = new JFlexTokenType("--");
  IElementType FLEX_DOLLAR = new JFlexTokenType("$");
  IElementType FLEX_DOT = new JFlexTokenType(".");
  IElementType FLEX_EOF = new JFlexTokenType("<<EOF>>");
  IElementType FLEX_EQ = new JFlexTokenType("=");
  IElementType FLEX_FSLASH = new JFlexTokenType("/");
  IElementType FLEX_HAT = new JFlexTokenType("^");
  IElementType FLEX_ID = new JFlexTokenType("id");
  IElementType FLEX_LINE_COMMENT = new JFlexTokenType("line_comment");
  IElementType FLEX_NUMBER = new JFlexTokenType("number");
  IElementType FLEX_OPT16BIT = new JFlexTokenType("%16bit");
  IElementType FLEX_OPT_7BIT = new JFlexTokenType("%7bit");
  IElementType FLEX_OPT_8BIT = new JFlexTokenType("%8bit");
  IElementType FLEX_OPT_ABSTRACT = new JFlexTokenType("%abstract");
  IElementType FLEX_OPT_APIPRIVATE = new JFlexTokenType("%apiprivate");
  IElementType FLEX_OPT_BUFFER = new JFlexTokenType("%buffer");
  IElementType FLEX_OPT_CASELESS = new JFlexTokenType("%caseless");
  IElementType FLEX_OPT_CHAR = new JFlexTokenType("%char");
  IElementType FLEX_OPT_CLASS = new JFlexTokenType("%class");
  IElementType FLEX_OPT_CODE1 = new JFlexTokenType("%{");
  IElementType FLEX_OPT_CODE2 = new JFlexTokenType("%}");
  IElementType FLEX_OPT_COLUMN = new JFlexTokenType("%column");
  IElementType FLEX_OPT_CTORARG = new JFlexTokenType("%ctorarg");
  IElementType FLEX_OPT_CUP = new JFlexTokenType("%cup");
  IElementType FLEX_OPT_CUPDEBUG = new JFlexTokenType("%cupdebug");
  IElementType FLEX_OPT_CUPSYM = new JFlexTokenType("%cupsym");
  IElementType FLEX_OPT_DEBUG = new JFlexTokenType("%debug");
  IElementType FLEX_OPT_EOF1 = new JFlexTokenType("%eof{");
  IElementType FLEX_OPT_EOF2 = new JFlexTokenType("%eof}");
  IElementType FLEX_OPT_EOFCLOSE = new JFlexTokenType("%eofclose");
  IElementType FLEX_OPT_EOFTHROW = new JFlexTokenType("%eofthrow");
  IElementType FLEX_OPT_EOFTHROW1 = new JFlexTokenType("%eofthrow{");
  IElementType FLEX_OPT_EOFTHROW2 = new JFlexTokenType("%eofthrow}");
  IElementType FLEX_OPT_EOFVAL1 = new JFlexTokenType("%eofval{");
  IElementType FLEX_OPT_EOFVAL2 = new JFlexTokenType("%eofval}");
  IElementType FLEX_OPT_EXTENDS = new JFlexTokenType("%extends");
  IElementType FLEX_OPT_FINAL = new JFlexTokenType("%final");
  IElementType FLEX_OPT_FULL = new JFlexTokenType("%full");
  IElementType FLEX_OPT_FUNCTION = new JFlexTokenType("%function");
  IElementType FLEX_OPT_IGNORECASE = new JFlexTokenType("%ignorecase");
  IElementType FLEX_OPT_IMPLEMENTS = new JFlexTokenType("%implements");
  IElementType FLEX_OPT_INCLUDE = new JFlexTokenType("%include");
  IElementType FLEX_OPT_INIT1 = new JFlexTokenType("%init{");
  IElementType FLEX_OPT_INIT2 = new JFlexTokenType("%init}");
  IElementType FLEX_OPT_INITTHROW = new JFlexTokenType("%initthrow");
  IElementType FLEX_OPT_INITTHROW1 = new JFlexTokenType("%initthrow{");
  IElementType FLEX_OPT_INITTHROW2 = new JFlexTokenType("%initthrow}");
  IElementType FLEX_OPT_INT = new JFlexTokenType("%int");
  IElementType FLEX_OPT_INTEGER = new JFlexTokenType("%integer");
  IElementType FLEX_OPT_INTWRAP = new JFlexTokenType("%intwrap");
  IElementType FLEX_OPT_LINE = new JFlexTokenType("%line");
  IElementType FLEX_OPT_NOTUNIX = new JFlexTokenType("%notunix");
  IElementType FLEX_OPT_PUBLIC = new JFlexTokenType("%public");
  IElementType FLEX_OPT_SCANERROR = new JFlexTokenType("%scanerror");
  IElementType FLEX_OPT_STANDALONE = new JFlexTokenType("%standalone");
  IElementType FLEX_OPT_STATE = new JFlexTokenType("%state");
  IElementType FLEX_OPT_TYPE = new JFlexTokenType("%type");
  IElementType FLEX_OPT_UNICODE = new JFlexTokenType("%unicode");
  IElementType FLEX_OPT_XSTATE = new JFlexTokenType("%xstate");
  IElementType FLEX_OPT_YYEOF = new JFlexTokenType("%yyeof");
  IElementType FLEX_OPT_YYLEXTHROW = new JFlexTokenType("%yylexthrow");
  IElementType FLEX_OPT_YYLEXTHROW1 = new JFlexTokenType("%yylexthrow{");
  IElementType FLEX_OPT_YYLEXTHROW2 = new JFlexTokenType("%yylexthrow}");
  IElementType FLEX_PAREN1 = new JFlexTokenType("(");
  IElementType FLEX_PAREN2 = new JFlexTokenType(")");
  IElementType FLEX_PLUS = new JFlexTokenType("+");
  IElementType FLEX_QUESTION = new JFlexTokenType("?");
  IElementType FLEX_RAW = new JFlexTokenType("code block");
  IElementType FLEX_STAR = new JFlexTokenType("*");
  IElementType FLEX_STRING = new JFlexTokenType("string");
  IElementType FLEX_TILDE = new JFlexTokenType("~");
  IElementType FLEX_TILDETILDE = new JFlexTokenType("~~");
  IElementType FLEX_TWO_PERCS = new JFlexTokenType("%%");
  IElementType FLEX_UNCLOSED = new JFlexTokenType("unclosed");
  IElementType FLEX_VERSION = new JFlexTokenType("version");

  class Factory {
    public static CompositePsiElement createElement(IElementType type) {
       if (type == FLEX_CHAR_RANGE) {
        return new JFlexCharRangeImpl(type);
      }
      else if (type == FLEX_CHOICE_EXPRESSION) {
        return new JFlexChoiceExpressionImpl(type);
      }
      else if (type == FLEX_CLASS_EXPRESSION) {
        return new JFlexClassExpressionImpl(type);
      }
      else if (type == FLEX_DECLARATIONS_SECTION) {
        return new JFlexDeclarationsSectionImpl(type);
      }
      else if (type == FLEX_JAVA_CODE) {
        return new JFlexJavaCodeImpl(type);
      }
      else if (type == FLEX_JAVA_NAME) {
        return new JFlexJavaNameImpl(type);
      }
      else if (type == FLEX_JAVA_TYPE) {
        return new JFlexJavaTypeImpl(type);
      }
      else if (type == FLEX_JAVA_TYPE_PARAMETERS) {
        return new JFlexJavaTypeParametersImpl(type);
      }
      else if (type == FLEX_LEXICAL_RULES_SECTION) {
        return new JFlexLexicalRulesSectionImpl(type);
      }
      else if (type == FLEX_LITERAL_EXPRESSION) {
        return new JFlexLiteralExpressionImpl(type);
      }
      else if (type == FLEX_LOOK_AHEAD) {
        return new JFlexLookAheadImpl(type);
      }
      else if (type == FLEX_MACRO_DEFINITION) {
        return new JFlexMacroDefinitionImpl(type);
      }
      else if (type == FLEX_MACRO_REFERENCE) {
        return new JFlexMacroReferenceImpl(type);
      }
      else if (type == FLEX_MACRO_REF_EXPRESSION) {
        return new JFlexMacroRefExpressionImpl(type);
      }
      else if (type == FLEX_NOT_EXPRESSION) {
        return new JFlexNotExpressionImpl(type);
      }
      else if (type == FLEX_OPTION) {
        return new JFlexOptionImpl(type);
      }
      else if (type == FLEX_PAREN_EXPRESSION) {
        return new JFlexParenExpressionImpl(type);
      }
      else if (type == FLEX_PREDEFINED_CLASS_EXPRESSION) {
        return new JFlexPredefinedClassExpressionImpl(type);
      }
      else if (type == FLEX_QUANTIFIER_EXPRESSION) {
        return new JFlexQuantifierExpressionImpl(type);
      }
      else if (type == FLEX_RULE) {
        return new JFlexRuleImpl(type);
      }
      else if (type == FLEX_SEQUENCE_EXPRESSION) {
        return new JFlexSequenceExpressionImpl(type);
      }
      else if (type == FLEX_STATE_DECLARATION) {
        return new JFlexStateDeclarationImpl(type);
      }
      else if (type == FLEX_STATE_DEFINITION) {
        return new JFlexStateDefinitionImpl(type);
      }
      else if (type == FLEX_STATE_LIST) {
        return new JFlexStateListImpl(type);
      }
      else if (type == FLEX_STATE_REFERENCE) {
        return new JFlexStateReferenceImpl(type);
      }
      else if (type == FLEX_USER_CODE_SECTION) {
        return new JFlexUserCodeSectionImpl(type);
      }
      else if (type == FLEX_USER_VALUE) {
        return new JFlexUserValueImpl(type);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- GeneratedSyntaxElementTypes.java -----------------
// license.txt
package generated;

import com.intellij.platform.syntax.SyntaxElementType;

public interface GeneratedSyntaxElementTypes {

  SyntaxElementType FLEX_CHAR_RANGE = new SyntaxElementType("FLEX_CHAR_RANGE");
  SyntaxElementType FLEX_CHOICE_EXPRESSION = new SyntaxElementType("FLEX_CHOICE_EXPRESSION");
  SyntaxElementType FLEX_CLASS_EXPRESSION = new SyntaxElementType("FLEX_CLASS_EXPRESSION");
  SyntaxElementType FLEX_DECLARATIONS_SECTION = new SyntaxElementType("FLEX_DECLARATIONS_SECTION");
  SyntaxElementType FLEX_EXPRESSION = new SyntaxElementType("FLEX_EXPRESSION");
  SyntaxElementType FLEX_JAVA_CODE = new SyntaxElementType("FLEX_JAVA_CODE");
  SyntaxElementType FLEX_JAVA_NAME = new SyntaxElementType("FLEX_JAVA_NAME");
  SyntaxElementType FLEX_JAVA_TYPE = new SyntaxElementType("FLEX_JAVA_TYPE");
  SyntaxElementType FLEX_JAVA_TYPE_PARAMETERS = new SyntaxElementType("FLEX_JAVA_TYPE_PARAMETERS");
  SyntaxElementType FLEX_LEXICAL_RULES_SECTION = new SyntaxElementType("FLEX_LEXICAL_RULES_SECTION");
  SyntaxElementType FLEX_LITERAL_EXPRESSION = new SyntaxElementType("FLEX_LITERAL_EXPRESSION");
  SyntaxElementType FLEX_LOOK_AHEAD = new SyntaxElementType("FLEX_LOOK_AHEAD");
  SyntaxElementType FLEX_MACRO_DEFINITION = new SyntaxElementType("FLEX_MACRO_DEFINITION");
  SyntaxElementType FLEX_MACRO_REFERENCE = new SyntaxElementType("FLEX_MACRO_REFERENCE");
  SyntaxElementType FLEX_MACRO_REF_EXPRESSION = new SyntaxElementType("FLEX_MACRO_REF_EXPRESSION");
  SyntaxElementType FLEX_NOT_EXPRESSION = new SyntaxElementType("FLEX_NOT_EXPRESSION");
  SyntaxElementType FLEX_OPTION = new SyntaxElementType("FLEX_OPTION");
  SyntaxElementType FLEX_PAREN_EXPRESSION = new SyntaxElementType("FLEX_PAREN_EXPRESSION");
  SyntaxElementType FLEX_PREDEFINED_CLASS_EXPRESSION = new SyntaxElementType("FLEX_PREDEFINED_CLASS_EXPRESSION");
  SyntaxElementType FLEX_QUANTIFIER_EXPRESSION = new SyntaxElementType("FLEX_QUANTIFIER_EXPRESSION");
  SyntaxElementType FLEX_RULE = new SyntaxElementType("FLEX_RULE");
  SyntaxElementType FLEX_SEQUENCE_EXPRESSION = new SyntaxElementType("FLEX_SEQUENCE_EXPRESSION");
  SyntaxElementType FLEX_STATE_DECLARATION = new SyntaxElementType("FLEX_STATE_DECLARATION");
  SyntaxElementType FLEX_STATE_DEFINITION = new SyntaxElementType("FLEX_STATE_DEFINITION");
  SyntaxElementType FLEX_STATE_LIST = new SyntaxElementType("FLEX_STATE_LIST");
  SyntaxElementType FLEX_STATE_REFERENCE = new SyntaxElementType("FLEX_STATE_REFERENCE");
  SyntaxElementType FLEX_USER_CODE_SECTION = new SyntaxElementType("FLEX_USER_CODE_SECTION");
  SyntaxElementType FLEX_USER_VALUE = new SyntaxElementType("FLEX_USER_VALUE");

  SyntaxElementType FLEX_AMPAMP = new SyntaxElementType("&&");
  SyntaxElementType FLEX_ANGLE1 = new SyntaxElementType("<");
  SyntaxElementType FLEX_ANGLE2 = new SyntaxElementType(">");
  SyntaxElementType FLEX_BANG = new SyntaxElementType("!");
  SyntaxElementType FLEX_BAR = new SyntaxElementType("|");
  SyntaxElementType FLEX_BARBAR = new SyntaxElementType("||");
  SyntaxElementType FLEX_BLOCK_COMMENT = new SyntaxElementType("block_comment");
  SyntaxElementType FLEX_BRACE1 = new SyntaxElementType("{");
  SyntaxElementType FLEX_BRACE2 = new SyntaxElementType("}");
  SyntaxElementType FLEX_BRACK1 = new SyntaxElementType("[");
  SyntaxElementType FLEX_BRACK2 = new SyntaxElementType("]");
  SyntaxElementType FLEX_CHAR = new SyntaxElementType("char");
  SyntaxElementType FLEX_CHAR_CLASS = new SyntaxElementType("char_class");
  SyntaxElementType FLEX_CHAR_ESC = new SyntaxElementType("char_esc");
  SyntaxElementType FLEX_COMMA = new SyntaxElementType(",");
  SyntaxElementType FLEX_DASH = new SyntaxElementType("-");
  SyntaxElementType FLEX_DASHDASH = new SyntaxElementType("--");
  SyntaxElementType FLEX_DOLLAR = new SyntaxElementType("$");
  SyntaxElementType FLEX_DOT = new SyntaxElementType(".");
  SyntaxElementType FLEX_EOF = new SyntaxElementType("<<EOF>>");
  SyntaxElementType FLEX_EQ = new SyntaxElementType("=");
  SyntaxElementType FLEX_FSLASH = new SyntaxElementType("/");
  SyntaxElementType FLEX_HAT = new SyntaxElementType("^");
  SyntaxElementType FLEX_ID = new SyntaxElementType("id");
  SyntaxElementType FLEX_LINE_COMMENT = new SyntaxElementType("line_comment");
  SyntaxElementType FLEX_NUMBER = new SyntaxElementType("number");
  SyntaxElementType FLEX_OPT16BIT = new SyntaxElementType("%16bit");
  SyntaxElementType FLEX_OPT_7BIT = new SyntaxElementType("%7bit");
  SyntaxElementType FLEX_OPT_8BIT = new SyntaxElementType("%8bit");
  SyntaxElementType FLEX_OPT_ABSTRACT = new SyntaxElementType("%abstract");
  SyntaxElementType FLEX_OPT_APIPRIVATE = new SyntaxElementType("%apiprivate");
  SyntaxElementType FLEX_OPT_BUFFER = new SyntaxElementType("%buffer");
  SyntaxElementType FLEX_OPT_CASELESS = new SyntaxElementType("%caseless");
  SyntaxElementType FLEX_OPT_CHAR = new SyntaxElementType("%char");
  SyntaxElementType FLEX_OPT_CLASS = new SyntaxElementType("%class");
  SyntaxElementType FLEX_OPT_CODE1 = new SyntaxElementType("%{");
  SyntaxElementType FLEX_OPT_CODE2 = new SyntaxElementType("%}");
  SyntaxElementType FLEX_OPT_COLUMN = new SyntaxElementType("%column");
  SyntaxElementType FLEX_OPT_CTORARG = new SyntaxElementType("%ctorarg");
  SyntaxElementType FLEX_OPT_CUP = new SyntaxElementType("%cup");
  SyntaxElementType FLEX_OPT_CUPDEBUG = new SyntaxElementType("%cupdebug");
  SyntaxElementType FLEX_OPT_CUPSYM = new SyntaxElementType("%cupsym");
  SyntaxElementType FLEX_OPT_DEBUG = new SyntaxElementType("%debug");
  SyntaxElementType FLEX_OPT_EOF1 = new SyntaxElementType("%eof{");
  SyntaxElementType FLEX_OPT_EOF2 = new SyntaxElementType("%eof}");
  SyntaxElementType FLEX_OPT_EOFCLOSE = new SyntaxElementType("%eofclose");
  SyntaxElementType FLEX_OPT_EOFTHROW = new SyntaxElementType("%eofthrow");
  SyntaxElementType FLEX_OPT_EOFTHROW1 = new SyntaxElementType("%eofthrow{");
  SyntaxElementType FLEX_OPT_EOFTHROW2 = new SyntaxElementType("%eofthrow}");
  SyntaxElementType FLEX_OPT_EOFVAL1 = new SyntaxElementType("%eofval{");
  SyntaxElementType FLEX_OPT_EOFVAL2 = new SyntaxElementType("%eofval}");
  SyntaxElementType FLEX_OPT_EXTENDS = new SyntaxElementType("%extends");
  SyntaxElementType FLEX_OPT_FINAL = new SyntaxElementType("%final");
  SyntaxElementType FLEX_OPT_FULL = new SyntaxElementType("%full");
  SyntaxElementType FLEX_OPT_FUNCTION = new SyntaxElementType("%function");
  SyntaxElementType FLEX_OPT_IGNORECASE = new SyntaxElementType("%ignorecase");
  SyntaxElementType FLEX_OPT_IMPLEMENTS = new SyntaxElementType("%implements");
  SyntaxElementType FLEX_OPT_INCLUDE = new SyntaxElementType("%include");
  SyntaxElementType FLEX_OPT_INIT1 = new SyntaxElementType("%init{");
  SyntaxElementType FLEX_OPT_INIT2 = new SyntaxElementType("%init}");
  SyntaxElementType FLEX_OPT_INITTHROW = new SyntaxElementType("%initthrow");
  SyntaxElementType FLEX_OPT_INITTHROW1 = new SyntaxElementType("%initthrow{");
  SyntaxElementType FLEX_OPT_INITTHROW2 = new SyntaxElementType("%initthrow}");
  SyntaxElementType FLEX_OPT_INT = new SyntaxElementType("%int");
  SyntaxElementType FLEX_OPT_INTEGER = new SyntaxElementType("%integer");
  SyntaxElementType FLEX_OPT_INTWRAP = new SyntaxElementType("%intwrap");
  SyntaxElementType FLEX_OPT_LINE = new SyntaxElementType("%line");
  SyntaxElementType FLEX_OPT_NOTUNIX = new SyntaxElementType("%notunix");
  SyntaxElementType FLEX_OPT_PUBLIC = new SyntaxElementType("%public");
  SyntaxElementType FLEX_OPT_SCANERROR = new SyntaxElementType("%scanerror");
  SyntaxElementType FLEX_OPT_STANDALONE = new SyntaxElementType("%standalone");
  SyntaxElementType FLEX_OPT_STATE = new SyntaxElementType("%state");
  SyntaxElementType FLEX_OPT_TYPE = new SyntaxElementType("%type");
  SyntaxElementType FLEX_OPT_UNICODE = new SyntaxElementType("%unicode");
  SyntaxElementType FLEX_OPT_XSTATE = new SyntaxElementType("%xstate");
  SyntaxElementType FLEX_OPT_YYEOF = new SyntaxElementType("%yyeof");
  SyntaxElementType FLEX_OPT_YYLEXTHROW = new SyntaxElementType("%yylexthrow");
  SyntaxElementType FLEX_OPT_YYLEXTHROW1 = new SyntaxElementType("%yylexthrow{");
  SyntaxElementType FLEX_OPT_YYLEXTHROW2 = new SyntaxElementType("%yylexthrow}");
  SyntaxElementType FLEX_PAREN1 = new SyntaxElementType("(");
  SyntaxElementType FLEX_PAREN2 = new SyntaxElementType(")");
  SyntaxElementType FLEX_PLUS = new SyntaxElementType("+");
  SyntaxElementType FLEX_QUESTION = new SyntaxElementType("?");
  SyntaxElementType FLEX_RAW = new SyntaxElementType("code block");
  SyntaxElementType FLEX_STAR = new SyntaxElementType("*");
  SyntaxElementType FLEX_STRING = new SyntaxElementType("string");
  SyntaxElementType FLEX_TILDE = new SyntaxElementType("~");
  SyntaxElementType FLEX_TILDETILDE = new SyntaxElementType("~~");
  SyntaxElementType FLEX_TWO_PERCS = new SyntaxElementType("%%");
  SyntaxElementType FLEX_UNCLOSED = new SyntaxElementType("unclosed");
  SyntaxElementType FLEX_VERSION = new SyntaxElementType("version");
}
// ---- GeneratedSyntaxElementTypeConverter.java -----------------
// license.txt
package generated;

import org.intellij.jflex.psi.JFlexTypes;
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
    map.put(GeneratedSyntaxElementTypes.FLEX_CHAR_RANGE, JFlexTypes.FLEX_CHAR_RANGE);
    map.put(GeneratedSyntaxElementTypes.FLEX_CHOICE_EXPRESSION, JFlexTypes.FLEX_CHOICE_EXPRESSION);
    map.put(GeneratedSyntaxElementTypes.FLEX_CLASS_EXPRESSION, JFlexTypes.FLEX_CLASS_EXPRESSION);
    map.put(GeneratedSyntaxElementTypes.FLEX_DECLARATIONS_SECTION, JFlexTypes.FLEX_DECLARATIONS_SECTION);
    map.put(GeneratedSyntaxElementTypes.FLEX_EXPRESSION, JFlexTypes.FLEX_EXPRESSION);
    map.put(GeneratedSyntaxElementTypes.FLEX_JAVA_CODE, JFlexTypes.FLEX_JAVA_CODE);
    map.put(GeneratedSyntaxElementTypes.FLEX_JAVA_NAME, JFlexTypes.FLEX_JAVA_NAME);
    map.put(GeneratedSyntaxElementTypes.FLEX_JAVA_TYPE, JFlexTypes.FLEX_JAVA_TYPE);
    map.put(GeneratedSyntaxElementTypes.FLEX_JAVA_TYPE_PARAMETERS, JFlexTypes.FLEX_JAVA_TYPE_PARAMETERS);
    map.put(GeneratedSyntaxElementTypes.FLEX_LEXICAL_RULES_SECTION, JFlexTypes.FLEX_LEXICAL_RULES_SECTION);
    map.put(GeneratedSyntaxElementTypes.FLEX_LITERAL_EXPRESSION, JFlexTypes.FLEX_LITERAL_EXPRESSION);
    map.put(GeneratedSyntaxElementTypes.FLEX_LOOK_AHEAD, JFlexTypes.FLEX_LOOK_AHEAD);
    map.put(GeneratedSyntaxElementTypes.FLEX_MACRO_DEFINITION, JFlexTypes.FLEX_MACRO_DEFINITION);
    map.put(GeneratedSyntaxElementTypes.FLEX_MACRO_REFERENCE, JFlexTypes.FLEX_MACRO_REFERENCE);
    map.put(GeneratedSyntaxElementTypes.FLEX_MACRO_REF_EXPRESSION, JFlexTypes.FLEX_MACRO_REF_EXPRESSION);
    map.put(GeneratedSyntaxElementTypes.FLEX_NOT_EXPRESSION, JFlexTypes.FLEX_NOT_EXPRESSION);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPTION, JFlexTypes.FLEX_OPTION);
    map.put(GeneratedSyntaxElementTypes.FLEX_PAREN_EXPRESSION, JFlexTypes.FLEX_PAREN_EXPRESSION);
    map.put(GeneratedSyntaxElementTypes.FLEX_PREDEFINED_CLASS_EXPRESSION, JFlexTypes.FLEX_PREDEFINED_CLASS_EXPRESSION);
    map.put(GeneratedSyntaxElementTypes.FLEX_QUANTIFIER_EXPRESSION, JFlexTypes.FLEX_QUANTIFIER_EXPRESSION);
    map.put(GeneratedSyntaxElementTypes.FLEX_RULE, JFlexTypes.FLEX_RULE);
    map.put(GeneratedSyntaxElementTypes.FLEX_SEQUENCE_EXPRESSION, JFlexTypes.FLEX_SEQUENCE_EXPRESSION);
    map.put(GeneratedSyntaxElementTypes.FLEX_STATE_DECLARATION, JFlexTypes.FLEX_STATE_DECLARATION);
    map.put(GeneratedSyntaxElementTypes.FLEX_STATE_DEFINITION, JFlexTypes.FLEX_STATE_DEFINITION);
    map.put(GeneratedSyntaxElementTypes.FLEX_STATE_LIST, JFlexTypes.FLEX_STATE_LIST);
    map.put(GeneratedSyntaxElementTypes.FLEX_STATE_REFERENCE, JFlexTypes.FLEX_STATE_REFERENCE);
    map.put(GeneratedSyntaxElementTypes.FLEX_USER_CODE_SECTION, JFlexTypes.FLEX_USER_CODE_SECTION);
    map.put(GeneratedSyntaxElementTypes.FLEX_USER_VALUE, JFlexTypes.FLEX_USER_VALUE);

    map.put(GeneratedSyntaxElementTypes.FLEX_ID, JFlexTypes.FLEX_ID);
    map.put(GeneratedSyntaxElementTypes.FLEX_LINE_COMMENT, JFlexTypes.FLEX_LINE_COMMENT);
    map.put(GeneratedSyntaxElementTypes.FLEX_BLOCK_COMMENT, JFlexTypes.FLEX_BLOCK_COMMENT);
    map.put(GeneratedSyntaxElementTypes.FLEX_RAW, JFlexTypes.FLEX_RAW);
    map.put(GeneratedSyntaxElementTypes.FLEX_UNCLOSED, JFlexTypes.FLEX_UNCLOSED);
    map.put(GeneratedSyntaxElementTypes.FLEX_TWO_PERCS, JFlexTypes.FLEX_TWO_PERCS);
    map.put(GeneratedSyntaxElementTypes.FLEX_STAR, JFlexTypes.FLEX_STAR);
    map.put(GeneratedSyntaxElementTypes.FLEX_PAREN1, JFlexTypes.FLEX_PAREN1);
    map.put(GeneratedSyntaxElementTypes.FLEX_PAREN2, JFlexTypes.FLEX_PAREN2);
    map.put(GeneratedSyntaxElementTypes.FLEX_BRACK1, JFlexTypes.FLEX_BRACK1);
    map.put(GeneratedSyntaxElementTypes.FLEX_BRACK2, JFlexTypes.FLEX_BRACK2);
    map.put(GeneratedSyntaxElementTypes.FLEX_BRACE1, JFlexTypes.FLEX_BRACE1);
    map.put(GeneratedSyntaxElementTypes.FLEX_BRACE2, JFlexTypes.FLEX_BRACE2);
    map.put(GeneratedSyntaxElementTypes.FLEX_QUESTION, JFlexTypes.FLEX_QUESTION);
    map.put(GeneratedSyntaxElementTypes.FLEX_DASH, JFlexTypes.FLEX_DASH);
    map.put(GeneratedSyntaxElementTypes.FLEX_PLUS, JFlexTypes.FLEX_PLUS);
    map.put(GeneratedSyntaxElementTypes.FLEX_HAT, JFlexTypes.FLEX_HAT);
    map.put(GeneratedSyntaxElementTypes.FLEX_FSLASH, JFlexTypes.FLEX_FSLASH);
    map.put(GeneratedSyntaxElementTypes.FLEX_DASHDASH, JFlexTypes.FLEX_DASHDASH);
    map.put(GeneratedSyntaxElementTypes.FLEX_AMPAMP, JFlexTypes.FLEX_AMPAMP);
    map.put(GeneratedSyntaxElementTypes.FLEX_BARBAR, JFlexTypes.FLEX_BARBAR);
    map.put(GeneratedSyntaxElementTypes.FLEX_TILDETILDE, JFlexTypes.FLEX_TILDETILDE);
    map.put(GeneratedSyntaxElementTypes.FLEX_DOT, JFlexTypes.FLEX_DOT);
    map.put(GeneratedSyntaxElementTypes.FLEX_COMMA, JFlexTypes.FLEX_COMMA);
    map.put(GeneratedSyntaxElementTypes.FLEX_ANGLE1, JFlexTypes.FLEX_ANGLE1);
    map.put(GeneratedSyntaxElementTypes.FLEX_ANGLE2, JFlexTypes.FLEX_ANGLE2);
    map.put(GeneratedSyntaxElementTypes.FLEX_BAR, JFlexTypes.FLEX_BAR);
    map.put(GeneratedSyntaxElementTypes.FLEX_DOLLAR, JFlexTypes.FLEX_DOLLAR);
    map.put(GeneratedSyntaxElementTypes.FLEX_EQ, JFlexTypes.FLEX_EQ);
    map.put(GeneratedSyntaxElementTypes.FLEX_BANG, JFlexTypes.FLEX_BANG);
    map.put(GeneratedSyntaxElementTypes.FLEX_TILDE, JFlexTypes.FLEX_TILDE);
    map.put(GeneratedSyntaxElementTypes.FLEX_EOF, JFlexTypes.FLEX_EOF);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_CLASS, JFlexTypes.FLEX_OPT_CLASS);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_IMPLEMENTS, JFlexTypes.FLEX_OPT_IMPLEMENTS);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_EXTENDS, JFlexTypes.FLEX_OPT_EXTENDS);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_PUBLIC, JFlexTypes.FLEX_OPT_PUBLIC);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_FINAL, JFlexTypes.FLEX_OPT_FINAL);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_ABSTRACT, JFlexTypes.FLEX_OPT_ABSTRACT);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_APIPRIVATE, JFlexTypes.FLEX_OPT_APIPRIVATE);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_CODE1, JFlexTypes.FLEX_OPT_CODE1);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_CODE2, JFlexTypes.FLEX_OPT_CODE2);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_INIT1, JFlexTypes.FLEX_OPT_INIT1);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_INIT2, JFlexTypes.FLEX_OPT_INIT2);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_INITTHROW, JFlexTypes.FLEX_OPT_INITTHROW);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_INITTHROW1, JFlexTypes.FLEX_OPT_INITTHROW1);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_INITTHROW2, JFlexTypes.FLEX_OPT_INITTHROW2);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_CTORARG, JFlexTypes.FLEX_OPT_CTORARG);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_SCANERROR, JFlexTypes.FLEX_OPT_SCANERROR);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_BUFFER, JFlexTypes.FLEX_OPT_BUFFER);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_INCLUDE, JFlexTypes.FLEX_OPT_INCLUDE);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_FUNCTION, JFlexTypes.FLEX_OPT_FUNCTION);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_INTEGER, JFlexTypes.FLEX_OPT_INTEGER);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_INT, JFlexTypes.FLEX_OPT_INT);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_INTWRAP, JFlexTypes.FLEX_OPT_INTWRAP);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_TYPE, JFlexTypes.FLEX_OPT_TYPE);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_YYLEXTHROW, JFlexTypes.FLEX_OPT_YYLEXTHROW);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_YYLEXTHROW1, JFlexTypes.FLEX_OPT_YYLEXTHROW1);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_YYLEXTHROW2, JFlexTypes.FLEX_OPT_YYLEXTHROW2);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_EOFVAL1, JFlexTypes.FLEX_OPT_EOFVAL1);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_EOFVAL2, JFlexTypes.FLEX_OPT_EOFVAL2);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_EOF1, JFlexTypes.FLEX_OPT_EOF1);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_EOF2, JFlexTypes.FLEX_OPT_EOF2);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_EOFTHROW, JFlexTypes.FLEX_OPT_EOFTHROW);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_EOFTHROW1, JFlexTypes.FLEX_OPT_EOFTHROW1);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_EOFTHROW2, JFlexTypes.FLEX_OPT_EOFTHROW2);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_EOFCLOSE, JFlexTypes.FLEX_OPT_EOFCLOSE);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_DEBUG, JFlexTypes.FLEX_OPT_DEBUG);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_STANDALONE, JFlexTypes.FLEX_OPT_STANDALONE);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_CUP, JFlexTypes.FLEX_OPT_CUP);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_CUPSYM, JFlexTypes.FLEX_OPT_CUPSYM);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_CUPDEBUG, JFlexTypes.FLEX_OPT_CUPDEBUG);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_7BIT, JFlexTypes.FLEX_OPT_7BIT);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_FULL, JFlexTypes.FLEX_OPT_FULL);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_8BIT, JFlexTypes.FLEX_OPT_8BIT);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_UNICODE, JFlexTypes.FLEX_OPT_UNICODE);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT16BIT, JFlexTypes.FLEX_OPT16BIT);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_CASELESS, JFlexTypes.FLEX_OPT_CASELESS);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_IGNORECASE, JFlexTypes.FLEX_OPT_IGNORECASE);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_CHAR, JFlexTypes.FLEX_OPT_CHAR);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_LINE, JFlexTypes.FLEX_OPT_LINE);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_COLUMN, JFlexTypes.FLEX_OPT_COLUMN);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_NOTUNIX, JFlexTypes.FLEX_OPT_NOTUNIX);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_YYEOF, JFlexTypes.FLEX_OPT_YYEOF);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_STATE, JFlexTypes.FLEX_OPT_STATE);
    map.put(GeneratedSyntaxElementTypes.FLEX_OPT_XSTATE, JFlexTypes.FLEX_OPT_XSTATE);
    map.put(GeneratedSyntaxElementTypes.FLEX_STRING, JFlexTypes.FLEX_STRING);
    map.put(GeneratedSyntaxElementTypes.FLEX_CHAR_CLASS, JFlexTypes.FLEX_CHAR_CLASS);
    map.put(GeneratedSyntaxElementTypes.FLEX_CHAR, JFlexTypes.FLEX_CHAR);
    map.put(GeneratedSyntaxElementTypes.FLEX_CHAR_ESC, JFlexTypes.FLEX_CHAR_ESC);
    map.put(GeneratedSyntaxElementTypes.FLEX_NUMBER, JFlexTypes.FLEX_NUMBER);
    map.put(GeneratedSyntaxElementTypes.FLEX_VERSION, JFlexTypes.FLEX_VERSION);
    return map;
  }
}
// ---- JFlexCharRange.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexCharRange extends JFlexClassExpression {

}
// ---- JFlexChoiceExpression.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexChoiceExpression extends JFlexExpression {

  @NotNull
  List<JFlexExpression> getExpressionList();

}
// ---- JFlexClassExpression.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexClassExpression extends JFlexExpression {

  @NotNull
  List<JFlexExpression> getExpressionList();

}
// ---- JFlexDeclarationsSection.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexDeclarationsSection extends JFlexFileSection {

  @NotNull
  List<JFlexMacroDefinition> getMacroDefinitionList();

  @NotNull
  List<JFlexOption> getOptionList();

  @NotNull
  List<JFlexStateDeclaration> getStateDeclarationList();

}
// ---- JFlexExpression.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexExpression extends JFlexComposite {

}
// ---- JFlexFileSection.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexFileSection extends JFlexComposite {

}
// ---- JFlexJavaCode.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface JFlexJavaCode extends JFlexComposite {

  PsiReference[] getReferences();

}
// ---- JFlexJavaName.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface JFlexJavaName extends JFlexComposite {

  PsiReference @NotNull [] getReferences();

}
// ---- JFlexJavaType.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexJavaType extends JFlexComposite {

  @NotNull
  JFlexJavaName getJavaName();

  @Nullable
  JFlexJavaTypeParameters getJavaTypeParameters();

}
// ---- JFlexJavaTypeParameters.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexJavaTypeParameters extends JFlexComposite {

  @NotNull
  List<JFlexJavaType> getJavaTypeList();

}
// ---- JFlexLexicalRulesSection.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexLexicalRulesSection extends JFlexFileSection {

  @NotNull
  List<JFlexOption> getOptionList();

  @NotNull
  List<JFlexRule> getRuleList();

}
// ---- JFlexLiteralExpression.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexLiteralExpression extends JFlexExpression {

}
// ---- JFlexLookAhead.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexLookAhead extends JFlexComposite {

  @Nullable
  JFlexExpression getExpression();

}
// ---- JFlexMacroDefinition.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface JFlexMacroDefinition extends JFlexNamedElement {

  @Nullable
  JFlexExpression getExpression();

  @NotNull
  PsiElement getId();

  @NotNull String getName();

  @NotNull PsiNameIdentifierOwner setName(String p1);

  @NotNull PsiElement getNameIdentifier();

}
// ---- JFlexMacroRefExpression.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexMacroRefExpression extends JFlexExpression {

  @NotNull
  JFlexMacroReference getMacroReference();

}
// ---- JFlexMacroReference.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface JFlexMacroReference extends JFlexComposite {

  @NotNull
  PsiElement getId();

  @NotNull PsiReference getReference();

}
// ---- JFlexNotExpression.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexNotExpression extends JFlexExpression {

  @Nullable
  JFlexExpression getExpression();

}
// ---- JFlexOption.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexOption extends JFlexComposite {

}
// ---- JFlexParenExpression.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexParenExpression extends JFlexExpression {

  @Nullable
  JFlexExpression getExpression();

}
// ---- JFlexPredefinedClassExpression.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexPredefinedClassExpression extends JFlexExpression {

}
// ---- JFlexQuantifierExpression.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexQuantifierExpression extends JFlexExpression {

  @NotNull
  JFlexExpression getExpression();

}
// ---- JFlexRule.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexRule extends JFlexComposite {

  @Nullable
  JFlexExpression getExpression();

  @Nullable
  JFlexJavaCode getJavaCode();

  @Nullable
  JFlexLookAhead getLookAhead();

  @NotNull
  List<JFlexOption> getOptionList();

  @NotNull
  List<JFlexRule> getRuleList();

  @Nullable
  JFlexStateList getStateList();

}
// ---- JFlexSequenceExpression.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexSequenceExpression extends JFlexExpression {

  @NotNull
  List<JFlexExpression> getExpressionList();

}
// ---- JFlexStateDeclaration.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexStateDeclaration extends JFlexComposite {

  @NotNull
  List<JFlexStateDefinition> getStateDefinitionList();

}
// ---- JFlexStateDefinition.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface JFlexStateDefinition extends JFlexNamedElement {

  @NotNull
  PsiElement getId();

  @NotNull String getName();

  @NotNull PsiNameIdentifierOwner setName(String p1);

  @NotNull PsiElement getNameIdentifier();

}
// ---- JFlexStateList.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexStateList extends JFlexComposite {

  @NotNull
  List<JFlexStateReference> getStateReferenceList();

}
// ---- JFlexStateReference.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface JFlexStateReference extends JFlexComposite {

  @NotNull
  PsiElement getId();

  @NotNull PsiReference getReference();

}
// ---- JFlexUserCodeSection.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexUserCodeSection extends JFlexFileSection {

  @Nullable
  JFlexJavaCode getJavaCode();

}
// ---- JFlexUserValue.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexUserValue extends JFlexComposite {

}
// ---- JFlexCharRangeImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexCharRangeImpl extends JFlexClassExpressionImpl implements JFlexCharRange {

  public JFlexCharRangeImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitCharRange(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- JFlexChoiceExpressionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexChoiceExpressionImpl extends JFlexExpressionImpl implements JFlexChoiceExpression {

  public JFlexChoiceExpressionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitChoiceExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JFlexExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexExpression.class);
  }

}
// ---- JFlexClassExpressionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexClassExpressionImpl extends JFlexExpressionImpl implements JFlexClassExpression {

  public JFlexClassExpressionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitClassExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JFlexExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexExpression.class);
  }

}
// ---- JFlexDeclarationsSectionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexDeclarationsSectionImpl extends JFlexFileSectionImpl implements JFlexDeclarationsSection {

  public JFlexDeclarationsSectionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitDeclarationsSection(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JFlexMacroDefinition> getMacroDefinitionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexMacroDefinition.class);
  }

  @Override
  @NotNull
  public List<JFlexOption> getOptionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexOption.class);
  }

  @Override
  @NotNull
  public List<JFlexStateDeclaration> getStateDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexStateDeclaration.class);
  }

}
// ---- JFlexExpressionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public abstract class JFlexExpressionImpl extends JFlexCompositeImpl implements JFlexExpression {

  public JFlexExpressionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- JFlexFileSectionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexFileSectionImpl extends JFlexCompositeImpl implements JFlexFileSection {

  public JFlexFileSectionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitFileSection(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- JFlexJavaCodeImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;

public class JFlexJavaCodeImpl extends JFlexJavaCodeInjectionHostImpl implements JFlexJavaCode {

  public JFlexJavaCodeImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitJavaCode(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public PsiReference[] getReferences() {
    return JFlexPsiImplUtil.getReferences(this);
  }

}
// ---- JFlexJavaNameImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;

public class JFlexJavaNameImpl extends JFlexCompositeImpl implements JFlexJavaName {

  public JFlexJavaNameImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitJavaName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public PsiReference @NotNull [] getReferences() {
    return JFlexPsiImplUtil.getReferences(this);
  }

}
// ---- JFlexJavaTypeImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexJavaTypeImpl extends JFlexCompositeImpl implements JFlexJavaType {

  public JFlexJavaTypeImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitJavaType(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public JFlexJavaName getJavaName() {
    return PsiTreeUtil.getChildOfType(this, JFlexJavaName.class);
  }

  @Override
  @Nullable
  public JFlexJavaTypeParameters getJavaTypeParameters() {
    return PsiTreeUtil.getChildOfType(this, JFlexJavaTypeParameters.class);
  }

}
// ---- JFlexJavaTypeParametersImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexJavaTypeParametersImpl extends JFlexCompositeImpl implements JFlexJavaTypeParameters {

  public JFlexJavaTypeParametersImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitJavaTypeParameters(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JFlexJavaType> getJavaTypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexJavaType.class);
  }

}
// ---- JFlexLexicalRulesSectionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexLexicalRulesSectionImpl extends JFlexFileSectionImpl implements JFlexLexicalRulesSection {

  public JFlexLexicalRulesSectionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitLexicalRulesSection(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JFlexOption> getOptionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexOption.class);
  }

  @Override
  @NotNull
  public List<JFlexRule> getRuleList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexRule.class);
  }

}
// ---- JFlexLiteralExpressionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexLiteralExpressionImpl extends JFlexExpressionImpl implements JFlexLiteralExpression {

  public JFlexLiteralExpressionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitLiteralExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- JFlexLookAheadImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexLookAheadImpl extends JFlexCompositeImpl implements JFlexLookAhead {

  public JFlexLookAheadImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitLookAhead(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JFlexExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, JFlexExpression.class);
  }

}
// ---- JFlexMacroDefinitionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.tree.IElementType;

public class JFlexMacroDefinitionImpl extends JFlexCompositeImpl implements JFlexMacroDefinition {

  public JFlexMacroDefinitionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitMacroDefinition(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JFlexExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, JFlexExpression.class);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findPsiChildByType(FLEX_ID);
  }

  @Override
  public @NotNull String getName() {
    return JFlexPsiImplUtil.getName(this);
  }

  @Override
  public @NotNull PsiNameIdentifierOwner setName(String p1) {
    return JFlexPsiImplUtil.setName(this, p1);
  }

  @Override
  public @NotNull PsiElement getNameIdentifier() {
    return JFlexPsiImplUtil.getNameIdentifier(this);
  }

}
// ---- JFlexMacroRefExpressionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexMacroRefExpressionImpl extends JFlexExpressionImpl implements JFlexMacroRefExpression {

  public JFlexMacroRefExpressionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitMacroRefExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public JFlexMacroReference getMacroReference() {
    return PsiTreeUtil.getChildOfType(this, JFlexMacroReference.class);
  }

}
// ---- JFlexMacroReferenceImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;

public class JFlexMacroReferenceImpl extends JFlexCompositeImpl implements JFlexMacroReference {

  public JFlexMacroReferenceImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitMacroReference(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findPsiChildByType(FLEX_ID);
  }

  @Override
  public @NotNull PsiReference getReference() {
    return JFlexPsiImplUtil.getReference(this);
  }

}
// ---- JFlexNotExpressionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexNotExpressionImpl extends JFlexExpressionImpl implements JFlexNotExpression {

  public JFlexNotExpressionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitNotExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JFlexExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, JFlexExpression.class);
  }

}
// ---- JFlexOptionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexOptionImpl extends JFlexCompositeImpl implements JFlexOption {

  public JFlexOptionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitOption(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- JFlexParenExpressionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexParenExpressionImpl extends JFlexExpressionImpl implements JFlexParenExpression {

  public JFlexParenExpressionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitParenExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JFlexExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, JFlexExpression.class);
  }

}
// ---- JFlexPredefinedClassExpressionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexPredefinedClassExpressionImpl extends JFlexExpressionImpl implements JFlexPredefinedClassExpression {

  public JFlexPredefinedClassExpressionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitPredefinedClassExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- JFlexQuantifierExpressionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexQuantifierExpressionImpl extends JFlexExpressionImpl implements JFlexQuantifierExpression {

  public JFlexQuantifierExpressionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitQuantifierExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public JFlexExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, JFlexExpression.class);
  }

}
// ---- JFlexRuleImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexRuleImpl extends JFlexCompositeImpl implements JFlexRule {

  public JFlexRuleImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitRule(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JFlexExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, JFlexExpression.class);
  }

  @Override
  @Nullable
  public JFlexJavaCode getJavaCode() {
    return PsiTreeUtil.getChildOfType(this, JFlexJavaCode.class);
  }

  @Override
  @Nullable
  public JFlexLookAhead getLookAhead() {
    return PsiTreeUtil.getChildOfType(this, JFlexLookAhead.class);
  }

  @Override
  @NotNull
  public List<JFlexOption> getOptionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexOption.class);
  }

  @Override
  @NotNull
  public List<JFlexRule> getRuleList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexRule.class);
  }

  @Override
  @Nullable
  public JFlexStateList getStateList() {
    return PsiTreeUtil.getChildOfType(this, JFlexStateList.class);
  }

}
// ---- JFlexSequenceExpressionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexSequenceExpressionImpl extends JFlexExpressionImpl implements JFlexSequenceExpression {

  public JFlexSequenceExpressionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitSequenceExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JFlexExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexExpression.class);
  }

}
// ---- JFlexStateDeclarationImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexStateDeclarationImpl extends JFlexCompositeImpl implements JFlexStateDeclaration {

  public JFlexStateDeclarationImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitStateDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JFlexStateDefinition> getStateDefinitionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexStateDefinition.class);
  }

}
// ---- JFlexStateDefinitionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.tree.IElementType;

public class JFlexStateDefinitionImpl extends JFlexCompositeImpl implements JFlexStateDefinition {

  public JFlexStateDefinitionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitStateDefinition(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findPsiChildByType(FLEX_ID);
  }

  @Override
  public @NotNull String getName() {
    return JFlexPsiImplUtil.getName(this);
  }

  @Override
  public @NotNull PsiNameIdentifierOwner setName(String p1) {
    return JFlexPsiImplUtil.setName(this, p1);
  }

  @Override
  public @NotNull PsiElement getNameIdentifier() {
    return JFlexPsiImplUtil.getNameIdentifier(this);
  }

}
// ---- JFlexStateListImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexStateListImpl extends JFlexCompositeImpl implements JFlexStateList {

  public JFlexStateListImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitStateList(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JFlexStateReference> getStateReferenceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexStateReference.class);
  }

}
// ---- JFlexStateReferenceImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;

public class JFlexStateReferenceImpl extends JFlexCompositeImpl implements JFlexStateReference {

  public JFlexStateReferenceImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitStateReference(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findPsiChildByType(FLEX_ID);
  }

  @Override
  public @NotNull PsiReference getReference() {
    return JFlexPsiImplUtil.getReference(this);
  }

}
// ---- JFlexUserCodeSectionImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexUserCodeSectionImpl extends JFlexFileSectionImpl implements JFlexUserCodeSection {

  public JFlexUserCodeSectionImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitUserCodeSection(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JFlexJavaCode getJavaCode() {
    return PsiTreeUtil.getChildOfType(this, JFlexJavaCode.class);
  }

}
// ---- JFlexUserValueImpl.java -----------------
// license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.tree.IElementType;

public class JFlexUserValueImpl extends JFlexCompositeImpl implements JFlexUserValue {

  public JFlexUserValueImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitUserValue(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- JFlexVisitor.java -----------------
// license.txt
package org.intellij.jflex.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class JFlexVisitor<R> extends PsiElementVisitor {

  public R visitCharRange(@NotNull JFlexCharRange o) {
    return visitClassExpression(o);
  }

  public R visitChoiceExpression(@NotNull JFlexChoiceExpression o) {
    return visitExpression(o);
  }

  public R visitClassExpression(@NotNull JFlexClassExpression o) {
    return visitExpression(o);
  }

  public R visitDeclarationsSection(@NotNull JFlexDeclarationsSection o) {
    return visitFileSection(o);
  }

  public R visitExpression(@NotNull JFlexExpression o) {
    return visitComposite(o);
  }

  public R visitFileSection(@NotNull JFlexFileSection o) {
    return visitComposite(o);
  }

  public R visitJavaCode(@NotNull JFlexJavaCode o) {
    return visitComposite(o);
  }

  public R visitJavaName(@NotNull JFlexJavaName o) {
    return visitComposite(o);
  }

  public R visitJavaType(@NotNull JFlexJavaType o) {
    return visitComposite(o);
  }

  public R visitJavaTypeParameters(@NotNull JFlexJavaTypeParameters o) {
    return visitComposite(o);
  }

  public R visitLexicalRulesSection(@NotNull JFlexLexicalRulesSection o) {
    return visitFileSection(o);
  }

  public R visitLiteralExpression(@NotNull JFlexLiteralExpression o) {
    return visitExpression(o);
  }

  public R visitLookAhead(@NotNull JFlexLookAhead o) {
    return visitComposite(o);
  }

  public R visitMacroDefinition(@NotNull JFlexMacroDefinition o) {
    return visitNamedElement(o);
  }

  public R visitMacroRefExpression(@NotNull JFlexMacroRefExpression o) {
    return visitExpression(o);
  }

  public R visitMacroReference(@NotNull JFlexMacroReference o) {
    return visitComposite(o);
  }

  public R visitNotExpression(@NotNull JFlexNotExpression o) {
    return visitExpression(o);
  }

  public R visitOption(@NotNull JFlexOption o) {
    return visitComposite(o);
  }

  public R visitParenExpression(@NotNull JFlexParenExpression o) {
    return visitExpression(o);
  }

  public R visitPredefinedClassExpression(@NotNull JFlexPredefinedClassExpression o) {
    return visitExpression(o);
  }

  public R visitQuantifierExpression(@NotNull JFlexQuantifierExpression o) {
    return visitExpression(o);
  }

  public R visitRule(@NotNull JFlexRule o) {
    return visitComposite(o);
  }

  public R visitSequenceExpression(@NotNull JFlexSequenceExpression o) {
    return visitExpression(o);
  }

  public R visitStateDeclaration(@NotNull JFlexStateDeclaration o) {
    return visitComposite(o);
  }

  public R visitStateDefinition(@NotNull JFlexStateDefinition o) {
    return visitNamedElement(o);
  }

  public R visitStateList(@NotNull JFlexStateList o) {
    return visitComposite(o);
  }

  public R visitStateReference(@NotNull JFlexStateReference o) {
    return visitComposite(o);
  }

  public R visitUserCodeSection(@NotNull JFlexUserCodeSection o) {
    return visitFileSection(o);
  }

  public R visitUserValue(@NotNull JFlexUserValue o) {
    return visitComposite(o);
  }

  public R visitNamedElement(@NotNull JFlexNamedElement o) {
    return visitComposite(o);
  }

  public R visitComposite(@NotNull JFlexComposite o) {
    visitElement(o);
    return null;
  }

}