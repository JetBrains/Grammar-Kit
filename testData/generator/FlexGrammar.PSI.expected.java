// ---- JFlexTypes.java -----------------
license.txt
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
  IElementType FLEX_JAVA_FQN = new JFlexCompositeElementType("FLEX_JAVA_FQN");
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
  IElementType FLEX_STATE_REFERENCE = new JFlexCompositeElementType("FLEX_STATE_REFERENCE");
  IElementType FLEX_USER_CODE_SECTION = new JFlexCompositeElementType("FLEX_USER_CODE_SECTION");

  IElementType FLEX_ANGLE1 = new JFlexTokenType("<");
  IElementType FLEX_ANGLE2 = new JFlexTokenType(">");
  IElementType FLEX_BLOCK_COMMENT = new JFlexTokenType("block_comment");
  IElementType FLEX_BRACE1 = new JFlexTokenType("{");
  IElementType FLEX_BRACE2 = new JFlexTokenType("}");
  IElementType FLEX_BRACK1 = new JFlexTokenType("[");
  IElementType FLEX_BRACK2 = new JFlexTokenType("]");
  IElementType FLEX_CHAR = new JFlexTokenType("char");
  IElementType FLEX_CLASS1 = new JFlexTokenType("[:jletter:]");
  IElementType FLEX_CLASS2 = new JFlexTokenType("[:jletterdigit:]");
  IElementType FLEX_CLASS3 = new JFlexTokenType("[:letter:]");
  IElementType FLEX_CLASS4 = new JFlexTokenType("[:digit:]");
  IElementType FLEX_CLASS5 = new JFlexTokenType("[:uppercase:]");
  IElementType FLEX_CLASS6 = new JFlexTokenType("[:lowercase:]");
  IElementType FLEX_COMMA = new JFlexTokenType(",");
  IElementType FLEX_DASH = new JFlexTokenType("-");
  IElementType FLEX_DOLLAR = new JFlexTokenType("$");
  IElementType FLEX_DOT = new JFlexTokenType(".");
  IElementType FLEX_EOF = new JFlexTokenType("<<EOF>>");
  IElementType FLEX_EQ = new JFlexTokenType("=");
  IElementType FLEX_ESCAPED_CHAR = new JFlexTokenType("escaped_char");
  IElementType FLEX_ID = new JFlexTokenType("id");
  IElementType FLEX_JAVA = new JFlexTokenType("java");
  IElementType FLEX_LINE_COMMENT = new JFlexTokenType("line_comment");
  IElementType FLEX_NOT = new JFlexTokenType("!");
  IElementType FLEX_NOT2 = new JFlexTokenType("~");
  IElementType FLEX_NUMBER = new JFlexTokenType("number");
  IElementType FLEX_OR = new JFlexTokenType("|");
  IElementType FLEX_PAREN1 = new JFlexTokenType("(");
  IElementType FLEX_PAREN2 = new JFlexTokenType(")");
  IElementType FLEX_PERC2 = new JFlexTokenType("%%");
  IElementType FLEX_PERC_1 = new JFlexTokenType("%class");
  IElementType FLEX_PERC_10 = new JFlexTokenType("%init{");
  IElementType FLEX_PERC_11 = new JFlexTokenType("%init}");
  IElementType FLEX_PERC_12 = new JFlexTokenType("%initthrow");
  IElementType FLEX_PERC_13 = new JFlexTokenType("%initthrow{");
  IElementType FLEX_PERC_14 = new JFlexTokenType("%initthrow}");
  IElementType FLEX_PERC_15 = new JFlexTokenType("%ctorarg");
  IElementType FLEX_PERC_16 = new JFlexTokenType("%scanerror");
  IElementType FLEX_PERC_17 = new JFlexTokenType("%buffer");
  IElementType FLEX_PERC_18 = new JFlexTokenType("%include");
  IElementType FLEX_PERC_19 = new JFlexTokenType("%function");
  IElementType FLEX_PERC_2 = new JFlexTokenType("%implements");
  IElementType FLEX_PERC_20 = new JFlexTokenType("%integer");
  IElementType FLEX_PERC_21 = new JFlexTokenType("%int");
  IElementType FLEX_PERC_22 = new JFlexTokenType("%intwrap");
  IElementType FLEX_PERC_23 = new JFlexTokenType("%type");
  IElementType FLEX_PERC_24 = new JFlexTokenType("%yylexthrow");
  IElementType FLEX_PERC_25 = new JFlexTokenType("%yylexthrow{");
  IElementType FLEX_PERC_26 = new JFlexTokenType("%yylexthrow}");
  IElementType FLEX_PERC_27 = new JFlexTokenType("%eofval{");
  IElementType FLEX_PERC_28 = new JFlexTokenType("%eofval}");
  IElementType FLEX_PERC_29 = new JFlexTokenType("%eof{");
  IElementType FLEX_PERC_3 = new JFlexTokenType("%extends");
  IElementType FLEX_PERC_30 = new JFlexTokenType("%eof}");
  IElementType FLEX_PERC_31 = new JFlexTokenType("%eofthrow");
  IElementType FLEX_PERC_32 = new JFlexTokenType("%eofthrow{");
  IElementType FLEX_PERC_33 = new JFlexTokenType("%eofthrow}");
  IElementType FLEX_PERC_34 = new JFlexTokenType("%eofclose");
  IElementType FLEX_PERC_36 = new JFlexTokenType("%debug");
  IElementType FLEX_PERC_37 = new JFlexTokenType("%standalone");
  IElementType FLEX_PERC_38 = new JFlexTokenType("%cup");
  IElementType FLEX_PERC_39 = new JFlexTokenType("%cupsym");
  IElementType FLEX_PERC_4 = new JFlexTokenType("%public");
  IElementType FLEX_PERC_40 = new JFlexTokenType("%cupdebug");
  IElementType FLEX_PERC_41 = new JFlexTokenType("%byacc");
  IElementType FLEX_PERC_42 = new JFlexTokenType("%switch");
  IElementType FLEX_PERC_43 = new JFlexTokenType("%table");
  IElementType FLEX_PERC_44 = new JFlexTokenType("%pack");
  IElementType FLEX_PERC_45 = new JFlexTokenType("%7bit");
  IElementType FLEX_PERC_46 = new JFlexTokenType("%full");
  IElementType FLEX_PERC_47 = new JFlexTokenType("%8bit");
  IElementType FLEX_PERC_48 = new JFlexTokenType("%unicode");
  IElementType FLEX_PERC_49 = new JFlexTokenType("%16bit");
  IElementType FLEX_PERC_5 = new JFlexTokenType("%final");
  IElementType FLEX_PERC_50 = new JFlexTokenType("%caseless");
  IElementType FLEX_PERC_51 = new JFlexTokenType("%ignorecase");
  IElementType FLEX_PERC_52 = new JFlexTokenType("%char");
  IElementType FLEX_PERC_53 = new JFlexTokenType("%line");
  IElementType FLEX_PERC_54 = new JFlexTokenType("%column");
  IElementType FLEX_PERC_55 = new JFlexTokenType("%notunix");
  IElementType FLEX_PERC_56 = new JFlexTokenType("%yyeof");
  IElementType FLEX_PERC_57 = new JFlexTokenType("%state");
  IElementType FLEX_PERC_58 = new JFlexTokenType("%s");
  IElementType FLEX_PERC_59 = new JFlexTokenType("%xstate");
  IElementType FLEX_PERC_6 = new JFlexTokenType("%abstract");
  IElementType FLEX_PERC_69 = new JFlexTokenType("%x");
  IElementType FLEX_PERC_7 = new JFlexTokenType("%apiprivate");
  IElementType FLEX_PERC_8 = new JFlexTokenType("%{");
  IElementType FLEX_PERC_9 = new JFlexTokenType("%}");
  IElementType FLEX_PLUS = new JFlexTokenType("+");
  IElementType FLEX_QUESTION = new JFlexTokenType("?");
  IElementType FLEX_ROOF = new JFlexTokenType("^");
  IElementType FLEX_SLASH = new JFlexTokenType("\\");
  IElementType FLEX_SLASH2 = new JFlexTokenType("/");
  IElementType FLEX_STAR = new JFlexTokenType("*");
  IElementType FLEX_STRING = new JFlexTokenType("string");

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
      else if (type == FLEX_JAVA_FQN) {
        return new JFlexJavaFqnImpl(node);
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
      else if (type == FLEX_STATE_REFERENCE) {
        return new JFlexStateReferenceImpl(node);
      }
      else if (type == FLEX_USER_CODE_SECTION) {
        return new JFlexUserCodeSectionImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- JFlexChoiceExpression.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexChoiceExpression extends JFlexExpression {

  @NotNull
  List<JFlexExpression> getExpressionList();

}
// ---- JFlexClassExpression.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexClassExpression extends JFlexExpression {

}
// ---- JFlexDeclarationsSection.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexDeclarationsSection extends JFlexCompositeElement {

  @NotNull
  List<JFlexMacroDefinition> getMacroDefinitionList();

  @NotNull
  List<JFlexOption> getOptionList();

  @NotNull
  List<JFlexStateDeclaration> getStateDeclarationList();

}
// ---- JFlexExpression.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexExpression extends JFlexCompositeElement {

}
// ---- JFlexJavaCode.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexJavaCode extends JFlexCompositeElement {

}
// ---- JFlexJavaFqn.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexJavaFqn extends JFlexCompositeElement {

}
// ---- JFlexLexicalRulesSection.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexLexicalRulesSection extends JFlexCompositeElement {

  @NotNull
  List<JFlexRule> getRuleList();

}
// ---- JFlexLiteralExpression.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexLiteralExpression extends JFlexExpression {

  @Nullable
  PsiElement getChar();

  @Nullable
  PsiElement getEscapedChar();

  @Nullable
  PsiElement getId();

  @Nullable
  PsiElement getNumber();

  @Nullable
  PsiElement getString();

}
// ---- JFlexLookAhead.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexLookAhead extends JFlexCompositeElement {

  @Nullable
  JFlexExpression getExpression();

}
// ---- JFlexMacroDefinition.java -----------------
license.txt
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
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexMacroRefExpression extends JFlexExpression {

  @NotNull
  JFlexMacroReference getMacroReference();

}
// ---- JFlexMacroReference.java -----------------
license.txt
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
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexNotExpression extends JFlexExpression {

  @Nullable
  JFlexExpression getExpression();

}
// ---- JFlexOption.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexOption extends JFlexCompositeElement {

}
// ---- JFlexParenExpression.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexParenExpression extends JFlexExpression {

  @Nullable
  JFlexExpression getExpression();

}
// ---- JFlexPredefinedClassExpression.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexPredefinedClassExpression extends JFlexExpression {

}
// ---- JFlexQuantifierExpression.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexQuantifierExpression extends JFlexExpression {

  @NotNull
  JFlexExpression getExpression();

}
// ---- JFlexRule.java -----------------
license.txt
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
  List<JFlexRule> getRuleList();

  @NotNull
  List<JFlexStateReference> getStateReferenceList();

}
// ---- JFlexSequenceExpression.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexSequenceExpression extends JFlexExpression {

  @NotNull
  List<JFlexExpression> getExpressionList();

}
// ---- JFlexStateDeclaration.java -----------------
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexStateDeclaration extends JFlexCompositeElement {

  @NotNull
  List<JFlexStateDefinition> getStateDefinitionList();

}
// ---- JFlexStateDefinition.java -----------------
license.txt
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
// ---- JFlexStateReference.java -----------------
license.txt
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
license.txt
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JFlexUserCodeSection extends JFlexCompositeElement {

  @Nullable
  JFlexJavaCode getJavaCode();

}
// ---- JFlexChoiceExpressionImpl.java -----------------
license.txt
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
license.txt
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

}
// ---- JFlexDeclarationsSectionImpl.java -----------------
license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;

public class JFlexDeclarationsSectionImpl extends JFlexCompositeElementImpl implements JFlexDeclarationsSection {

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
license.txt
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
// ---- JFlexJavaCodeImpl.java -----------------
license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;

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

}
// ---- JFlexJavaFqnImpl.java -----------------
license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;

public class JFlexJavaFqnImpl extends JFlexCompositeElementImpl implements JFlexJavaFqn {

  public JFlexJavaFqnImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitJavaFqn(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- JFlexLexicalRulesSectionImpl.java -----------------
license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;

public class JFlexLexicalRulesSectionImpl extends JFlexCompositeElementImpl implements JFlexLexicalRulesSection {

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
  public List<JFlexRule> getRuleList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexRule.class);
  }

}
// ---- JFlexLiteralExpressionImpl.java -----------------
license.txt
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

  @Override
  @Nullable
  public PsiElement getChar() {
    return findChildByType(FLEX_CHAR);
  }

  @Override
  @Nullable
  public PsiElement getEscapedChar() {
    return findChildByType(FLEX_ESCAPED_CHAR);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(FLEX_ID);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findChildByType(FLEX_NUMBER);
  }

  @Override
  @Nullable
  public PsiElement getString() {
    return findChildByType(FLEX_STRING);
  }

}
// ---- JFlexLookAheadImpl.java -----------------
license.txt
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
license.txt
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
license.txt
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
license.txt
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
license.txt
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
license.txt
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
license.txt
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
license.txt
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
license.txt
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
license.txt
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
  public List<JFlexRule> getRuleList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexRule.class);
  }

  @Override
  @NotNull
  public List<JFlexStateReference> getStateReferenceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexStateReference.class);
  }

}
// ---- JFlexSequenceExpressionImpl.java -----------------
license.txt
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
license.txt
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
license.txt
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
// ---- JFlexStateReferenceImpl.java -----------------
license.txt
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
license.txt
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;

public class JFlexUserCodeSectionImpl extends JFlexCompositeElementImpl implements JFlexUserCodeSection {

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
// ---- JFlexVisitor.java -----------------
license.txt
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
    visitCompositeElement(o);
  }

  public void visitExpression(@NotNull JFlexExpression o) {
    visitCompositeElement(o);
  }

  public void visitJavaCode(@NotNull JFlexJavaCode o) {
    visitCompositeElement(o);
  }

  public void visitJavaFqn(@NotNull JFlexJavaFqn o) {
    visitCompositeElement(o);
  }

  public void visitLexicalRulesSection(@NotNull JFlexLexicalRulesSection o) {
    visitCompositeElement(o);
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

  public void visitStateReference(@NotNull JFlexStateReference o) {
    visitCompositeElement(o);
  }

  public void visitUserCodeSection(@NotNull JFlexUserCodeSection o) {
    visitCompositeElement(o);
  }

  public void visitPsiNameIdentifierOwner(@NotNull PsiNameIdentifierOwner o) {
    visitElement(o);
  }

  public void visitCompositeElement(@NotNull JFlexCompositeElement o) {
    visitElement(o);
  }

}