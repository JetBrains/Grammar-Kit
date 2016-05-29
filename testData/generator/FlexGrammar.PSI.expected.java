// ---- JFlexTypes.java -----------------
// license.txt
package org.intellij.jflex.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.intellij.jflex.psi.impl.*;

public interface JFlexTypes {

  IElementType FLEX_CHOICE_EXPRESSION = new JFlexCompositeElementType("FLEX_CHOICE_EXPRESSION");
  IElementType FLEX_CLASS_EXPRESSION = new JFlexCompositeElementType("FLEX_CLASS_EXPRESSION");
  IElementType FLEX_DECLARATIONS_SECTION = new JFlexCompositeElementType("FLEX_DECLARATIONS_SECTION");
  IElementType FLEX_EXPRESSION = new JFlexCompositeElementType("FLEX_EXPRESSION");
  IElementType FLEX_JAVA_CODE = new JFlexCompositeElementType("FLEX_JAVA_CODE");
  IElementType FLEX_JAVA_TYPE = new JFlexCompositeElementType("FLEX_JAVA_TYPE");
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
  IElementType FLEX_BSLASH = new JFlexTokenType("\\");
  IElementType FLEX_CHAR = new JFlexTokenType("char");
  IElementType FLEX_CHAR_CLASS = new JFlexTokenType("char_class");
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
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == FLEX_CHOICE_EXPRESSION) {
        return new JFlexChoiceExpressionImpl(node);
      }
      else if (type == FLEX_CLASS_EXPRESSION) {
        return new JFlexClassExpressionImpl(node);
      }
      else if (type == FLEX_DECLARATIONS_SECTION) {
        return new JFlexDeclarationsSectionImpl(node);
      }
      else if (type == FLEX_EXPRESSION) {
        return new JFlexExpressionImpl(node);
      }
      else if (type == FLEX_JAVA_CODE) {
        return new JFlexJavaCodeImpl(node);
      }
      else if (type == FLEX_JAVA_TYPE) {
        return new JFlexJavaTypeImpl(node);
      }
      else if (type == FLEX_LEXICAL_RULES_SECTION) {
        return new JFlexLexicalRulesSectionImpl(node);
      }
      else if (type == FLEX_LITERAL_EXPRESSION) {
        return new JFlexLiteralExpressionImpl(node);
      }
      else if (type == FLEX_LOOK_AHEAD) {
        return new JFlexLookAheadImpl(node);
      }
      else if (type == FLEX_MACRO_DEFINITION) {
        return new JFlexMacroDefinitionImpl(node);
      }
      else if (type == FLEX_MACRO_REFERENCE) {
        return new JFlexMacroReferenceImpl(node);
      }
      else if (type == FLEX_MACRO_REF_EXPRESSION) {
        return new JFlexMacroRefExpressionImpl(node);
      }
      else if (type == FLEX_NOT_EXPRESSION) {
        return new JFlexNotExpressionImpl(node);
      }
      else if (type == FLEX_OPTION) {
        return new JFlexOptionImpl(node);
      }
      else if (type == FLEX_PAREN_EXPRESSION) {
        return new JFlexParenExpressionImpl(node);
      }
      else if (type == FLEX_PREDEFINED_CLASS_EXPRESSION) {
        return new JFlexPredefinedClassExpressionImpl(node);
      }
      else if (type == FLEX_QUANTIFIER_EXPRESSION) {
        return new JFlexQuantifierExpressionImpl(node);
      }
      else if (type == FLEX_RULE) {
        return new JFlexRuleImpl(node);
      }
      else if (type == FLEX_SEQUENCE_EXPRESSION) {
        return new JFlexSequenceExpressionImpl(node);
      }
      else if (type == FLEX_STATE_DECLARATION) {
        return new JFlexStateDeclarationImpl(node);
      }
      else if (type == FLEX_STATE_DEFINITION) {
        return new JFlexStateDefinitionImpl(node);
      }
      else if (type == FLEX_STATE_LIST) {
        return new JFlexStateListImpl(node);
      }
      else if (type == FLEX_STATE_REFERENCE) {
        return new JFlexStateReferenceImpl(node);
      }
      else if (type == FLEX_USER_CODE_SECTION) {
        return new JFlexUserCodeSectionImpl(node);
      }
      else if (type == FLEX_USER_VALUE) {
        return new JFlexUserValueImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
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
  List<JFlexClassExpression> getClassExpressionList();

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

public interface JFlexExpression extends JFlexCompositeElement {

}
// ---- JFlexFileSection.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexFileSection extends JFlexCompositeElement {

}
// ---- JFlexJavaCode.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface JFlexJavaCode extends JFlexCompositeElement {

  PsiReference[] getReferences();

}
// ---- JFlexJavaType.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface JFlexJavaType extends JFlexCompositeElement {

  @NotNull
  PsiReference[] getReferences();

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

public interface JFlexLookAhead extends JFlexCompositeElement {

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

public interface JFlexMacroDefinition extends PsiNameIdentifierOwner {

  @Nullable
  JFlexExpression getExpression();

  @NotNull
  PsiElement getId();

  @NotNull
  String getName();

  @NotNull
  PsiNameIdentifierOwner setName(String p1);

  @NotNull
  PsiElement getNameIdentifier();

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

public interface JFlexMacroReference extends JFlexCompositeElement {

  @NotNull
  PsiElement getId();

  @NotNull
  PsiReference getReference();

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

public interface JFlexOption extends JFlexCompositeElement {

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

public interface JFlexRule extends JFlexCompositeElement {

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

public interface JFlexStateDeclaration extends JFlexCompositeElement {

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

public interface JFlexStateDefinition extends PsiNameIdentifierOwner {

  @NotNull
  PsiElement getId();

  @NotNull
  String getName();

  @NotNull
  PsiNameIdentifierOwner setName(String p1);

  @NotNull
  PsiElement getNameIdentifier();

}
// ---- JFlexStateList.java -----------------
// license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexStateList extends JFlexCompositeElement {

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

public interface JFlexStateReference extends JFlexCompositeElement {

  @NotNull
  PsiElement getId();

  @NotNull
  PsiReference getReference();

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

public interface JFlexUserValue extends JFlexCompositeElement {

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

public class JFlexChoiceExpressionImpl extends JFlexExpressionImpl implements JFlexChoiceExpression {

  public JFlexChoiceExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitChoiceExpression(this);
  }

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

public class JFlexClassExpressionImpl extends JFlexExpressionImpl implements JFlexClassExpression {

  public JFlexClassExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitClassExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JFlexClassExpression> getClassExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexClassExpression.class);
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

public class JFlexDeclarationsSectionImpl extends JFlexFileSectionImpl implements JFlexDeclarationsSection {

  public JFlexDeclarationsSectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitDeclarationsSection(this);
  }

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

public class JFlexExpressionImpl extends JFlexCompositeElementImpl implements JFlexExpression {

  public JFlexExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitExpression(this);
  }

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

public class JFlexFileSectionImpl extends JFlexCompositeElementImpl implements JFlexFileSection {

  public JFlexFileSectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitFileSection(this);
  }

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

public class JFlexJavaCodeImpl extends JFlexCompositeElementImpl implements JFlexJavaCode {

  public JFlexJavaCodeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitJavaCode(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  public PsiReference[] getReferences() {
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
import com.intellij.psi.PsiReference;

public class JFlexJavaTypeImpl extends JFlexCompositeElementImpl implements JFlexJavaType {

  public JFlexJavaTypeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitJavaType(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @NotNull
  public PsiReference[] getReferences() {
    return JFlexPsiImplUtil.getReferences(this);
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

public class JFlexLexicalRulesSectionImpl extends JFlexFileSectionImpl implements JFlexLexicalRulesSection {

  public JFlexLexicalRulesSectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitLexicalRulesSection(this);
  }

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

public class JFlexLiteralExpressionImpl extends JFlexExpressionImpl implements JFlexLiteralExpression {

  public JFlexLiteralExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitLiteralExpression(this);
  }

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

public class JFlexLookAheadImpl extends JFlexCompositeElementImpl implements JFlexLookAhead {

  public JFlexLookAheadImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitLookAhead(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JFlexExpression getExpression() {
    return findChildByClass(JFlexExpression.class);
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

public class JFlexMacroDefinitionImpl extends JFlexCompositeElementImpl implements JFlexMacroDefinition {

  public JFlexMacroDefinitionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitMacroDefinition(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JFlexExpression getExpression() {
    return findChildByClass(JFlexExpression.class);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(FLEX_ID);
  }

  @NotNull
  public String getName() {
    return JFlexPsiImplUtil.getName(this);
  }

  @NotNull
  public PsiNameIdentifierOwner setName(String p1) {
    return JFlexPsiImplUtil.setName(this, p1);
  }

  @NotNull
  public PsiElement getNameIdentifier() {
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

public class JFlexMacroRefExpressionImpl extends JFlexExpressionImpl implements JFlexMacroRefExpression {

  public JFlexMacroRefExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitMacroRefExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public JFlexMacroReference getMacroReference() {
    return findNotNullChildByClass(JFlexMacroReference.class);
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

public class JFlexMacroReferenceImpl extends JFlexCompositeElementImpl implements JFlexMacroReference {

  public JFlexMacroReferenceImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitMacroReference(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(FLEX_ID);
  }

  @NotNull
  public PsiReference getReference() {
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

public class JFlexNotExpressionImpl extends JFlexExpressionImpl implements JFlexNotExpression {

  public JFlexNotExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitNotExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JFlexExpression getExpression() {
    return findChildByClass(JFlexExpression.class);
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

public class JFlexOptionImpl extends JFlexCompositeElementImpl implements JFlexOption {

  public JFlexOptionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitOption(this);
  }

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

public class JFlexParenExpressionImpl extends JFlexExpressionImpl implements JFlexParenExpression {

  public JFlexParenExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitParenExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JFlexExpression getExpression() {
    return findChildByClass(JFlexExpression.class);
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

public class JFlexPredefinedClassExpressionImpl extends JFlexExpressionImpl implements JFlexPredefinedClassExpression {

  public JFlexPredefinedClassExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitPredefinedClassExpression(this);
  }

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

public class JFlexQuantifierExpressionImpl extends JFlexExpressionImpl implements JFlexQuantifierExpression {

  public JFlexQuantifierExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitQuantifierExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public JFlexExpression getExpression() {
    return findNotNullChildByClass(JFlexExpression.class);
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

public class JFlexRuleImpl extends JFlexCompositeElementImpl implements JFlexRule {

  public JFlexRuleImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitRule(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JFlexExpression getExpression() {
    return findChildByClass(JFlexExpression.class);
  }

  @Override
  @Nullable
  public JFlexJavaCode getJavaCode() {
    return findChildByClass(JFlexJavaCode.class);
  }

  @Override
  @Nullable
  public JFlexLookAhead getLookAhead() {
    return findChildByClass(JFlexLookAhead.class);
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
    return findChildByClass(JFlexStateList.class);
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

public class JFlexSequenceExpressionImpl extends JFlexExpressionImpl implements JFlexSequenceExpression {

  public JFlexSequenceExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitSequenceExpression(this);
  }

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

public class JFlexStateDeclarationImpl extends JFlexCompositeElementImpl implements JFlexStateDeclaration {

  public JFlexStateDeclarationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitStateDeclaration(this);
  }

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

public class JFlexStateDefinitionImpl extends JFlexCompositeElementImpl implements JFlexStateDefinition {

  public JFlexStateDefinitionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitStateDefinition(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(FLEX_ID);
  }

  @NotNull
  public String getName() {
    return JFlexPsiImplUtil.getName(this);
  }

  @NotNull
  public PsiNameIdentifierOwner setName(String p1) {
    return JFlexPsiImplUtil.setName(this, p1);
  }

  @NotNull
  public PsiElement getNameIdentifier() {
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

public class JFlexStateListImpl extends JFlexCompositeElementImpl implements JFlexStateList {

  public JFlexStateListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitStateList(this);
  }

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

public class JFlexStateReferenceImpl extends JFlexCompositeElementImpl implements JFlexStateReference {

  public JFlexStateReferenceImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitStateReference(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(FLEX_ID);
  }

  @NotNull
  public PsiReference getReference() {
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

public class JFlexUserCodeSectionImpl extends JFlexFileSectionImpl implements JFlexUserCodeSection {

  public JFlexUserCodeSectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitUserCodeSection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JFlexJavaCode getJavaCode() {
    return findChildByClass(JFlexJavaCode.class);
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

public class JFlexUserValueImpl extends JFlexCompositeElementImpl implements JFlexUserValue {

  public JFlexUserValueImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitUserValue(this);
  }

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
import com.intellij.psi.PsiNameIdentifierOwner;

public class JFlexVisitor extends PsiElementVisitor {

  public void visitChoiceExpression(@NotNull JFlexChoiceExpression o) {
    visitExpression(o);
  }

  public void visitClassExpression(@NotNull JFlexClassExpression o) {
    visitExpression(o);
  }

  public void visitDeclarationsSection(@NotNull JFlexDeclarationsSection o) {
    visitFileSection(o);
  }

  public void visitExpression(@NotNull JFlexExpression o) {
    visitCompositeElement(o);
  }

  public void visitFileSection(@NotNull JFlexFileSection o) {
    visitCompositeElement(o);
  }

  public void visitJavaCode(@NotNull JFlexJavaCode o) {
    visitCompositeElement(o);
  }

  public void visitJavaType(@NotNull JFlexJavaType o) {
    visitCompositeElement(o);
  }

  public void visitLexicalRulesSection(@NotNull JFlexLexicalRulesSection o) {
    visitFileSection(o);
  }

  public void visitLiteralExpression(@NotNull JFlexLiteralExpression o) {
    visitExpression(o);
  }

  public void visitLookAhead(@NotNull JFlexLookAhead o) {
    visitCompositeElement(o);
  }

  public void visitMacroDefinition(@NotNull JFlexMacroDefinition o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitMacroRefExpression(@NotNull JFlexMacroRefExpression o) {
    visitExpression(o);
  }

  public void visitMacroReference(@NotNull JFlexMacroReference o) {
    visitCompositeElement(o);
  }

  public void visitNotExpression(@NotNull JFlexNotExpression o) {
    visitExpression(o);
  }

  public void visitOption(@NotNull JFlexOption o) {
    visitCompositeElement(o);
  }

  public void visitParenExpression(@NotNull JFlexParenExpression o) {
    visitExpression(o);
  }

  public void visitPredefinedClassExpression(@NotNull JFlexPredefinedClassExpression o) {
    visitExpression(o);
  }

  public void visitQuantifierExpression(@NotNull JFlexQuantifierExpression o) {
    visitExpression(o);
  }

  public void visitRule(@NotNull JFlexRule o) {
    visitCompositeElement(o);
  }

  public void visitSequenceExpression(@NotNull JFlexSequenceExpression o) {
    visitExpression(o);
  }

  public void visitStateDeclaration(@NotNull JFlexStateDeclaration o) {
    visitCompositeElement(o);
  }

  public void visitStateDefinition(@NotNull JFlexStateDefinition o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitStateList(@NotNull JFlexStateList o) {
    visitCompositeElement(o);
  }

  public void visitStateReference(@NotNull JFlexStateReference o) {
    visitCompositeElement(o);
  }

  public void visitUserCodeSection(@NotNull JFlexUserCodeSection o) {
    visitFileSection(o);
  }

  public void visitUserValue(@NotNull JFlexUserValue o) {
    visitCompositeElement(o);
  }

  public void visitPsiNameIdentifierOwner(@NotNull PsiNameIdentifierOwner o) {
    visitElement(o);
  }

  public void visitCompositeElement(@NotNull JFlexCompositeElement o) {
    visitElement(o);
  }

}