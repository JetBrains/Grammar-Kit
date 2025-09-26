// ---- generated/GeneratedSyntaxElementTypes.kt -----------------
//header.txt
package generated

import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf

object GeneratedSyntaxElementTypes {
  val A_STATEMENT = SyntaxElementType("A_STATEMENT")
  val BLOCK_OF = SyntaxElementType("BLOCK_OF")
  val B_STATEMENT = SyntaxElementType("B_STATEMENT")
  val CAST_EXPR = SyntaxElementType("CAST_EXPR")
  val CHOICE_JOINED = SyntaxElementType("CHOICE_JOINED")
  val C_STATEMENT = SyntaxElementType("C_STATEMENT")
  val EXPR = SyntaxElementType("EXPR")
  val GRAMMAR_ELEMENT = SyntaxElementType("GRAMMAR_ELEMENT")
  val IDENTIFIER = SyntaxElementType("IDENTIFIER")
  val ID_EXPR = SyntaxElementType("ID_EXPR")
  val INCLUDE_SECTION = SyntaxElementType("INCLUDE_SECTION")
  val INCLUDE__SECTION__ALT = SyntaxElementType("INCLUDE__SECTION__ALT")
  val ITEM_EXPR = SyntaxElementType("ITEM_EXPR")
  val LEFT_SHADOW = SyntaxElementType("LEFT_SHADOW")
  val LEFT_SHADOW_TEST = SyntaxElementType("LEFT_SHADOW_TEST")
  val LITERAL = SyntaxElementType("LITERAL")
  val MISSING_EXTERNAL_TYPE = SyntaxElementType("MISSING_EXTERNAL_TYPE")
  val MUL_EXPR = SyntaxElementType("MUL_EXPR")
  val PLUS_EXPR = SyntaxElementType("PLUS_EXPR")
  val REF_EXPR = SyntaxElementType("REF_EXPR")
  val ROOT = SyntaxElementType("ROOT")
  val ROOT_B = SyntaxElementType("ROOT_B")
  val ROOT_C = SyntaxElementType("ROOT_C")
  val ROOT_D = SyntaxElementType("ROOT_D")
  val SOME_EXPR = SyntaxElementType("SOME_EXPR")
  val SPECIAL_REF = SyntaxElementType("SPECIAL_REF")
  val STATEMENT = SyntaxElementType("STATEMENT")

  val ID = SyntaxElementType("id")
  val NOTSPACE = SyntaxElementType("notspace")
  val NUMBER = SyntaxElementType("number")
  val OF = SyntaxElementType("OF")
  val OP_DIV = SyntaxElementType("/")
  val OP_MUL = SyntaxElementType("*")
  val SLASH = SyntaxElementType("\\")
}
// ---- ../myPsi/output/generated/GeneratedTypes.java -----------------
//header.txt
package generated;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import sample.MyTypeFactory;
import sample.MyRootType;
import generated.psi.impl.*;

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

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == A_STATEMENT) {
        return new XAStatementImpl(node);
      }
      else if (type == BLOCK_OF) {
        return new XBlockOfImpl(node);
      }
      else if (type == B_STATEMENT) {
        return new XBStatementImpl(node);
      }
      else if (type == CAST_EXPR) {
        return new XCastExprImpl(node);
      }
      else if (type == CHOICE_JOINED) {
        return new XChoiceJoinedImpl(node);
      }
      else if (type == C_STATEMENT) {
        return new XCStatementImpl(node);
      }
      else if (type == EXPR) {
        return new XExprImpl(node);
      }
      else if (type == GRAMMAR_ELEMENT) {
        return new XGrammarElementImpl(node);
      }
      else if (type == IDENTIFIER) {
        return new XIdentifierImpl(node);
      }
      else if (type == ID_EXPR) {
        return new XExternalType2Impl(node);
      }
      else if (type == INCLUDE_SECTION) {
        return new XIncludeSectionImpl(node);
      }
      else if (type == INCLUDE__SECTION__ALT) {
        return new XIncludeSectionAltImpl(node);
      }
      else if (type == ITEM_EXPR) {
        return new XItemExprImpl(node);
      }
      else if (type == LEFT_SHADOW) {
        return new XLeftShadowImpl(node);
      }
      else if (type == LEFT_SHADOW_TEST) {
        return new XLeftShadowTestImpl(node);
      }
      else if (type == LITERAL) {
        return new XLiteralImpl(node);
      }
      else if (type == MISSING_EXTERNAL_TYPE) {
        return new XExternalTypeImpl(node);
      }
      else if (type == MUL_EXPR) {
        return new XMulExprImpl(node);
      }
      else if (type == PLUS_EXPR) {
        return new XPlusExprImpl(node);
      }
      else if (type == REF_EXPR) {
        return new XRefExprImpl(node);
      }
      else if (type == ROOT_B) {
        return new XRootBImpl(node);
      }
      else if (type == ROOT_C) {
        return new XRootCImpl(node);
      }
      else if (type == ROOT_D) {
        return new XRootDImpl(node);
      }
      else if (type == SPECIAL_REF) {
        return new XSpecialRefImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- ../myPsi/output/generated/GeneratedSyntaxElementTypeConverterFactory.java -----------------
//header.txt
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
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.A_STATEMENT, GeneratedTypes.A_STATEMENT),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.BLOCK_OF, GeneratedTypes.BLOCK_OF),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.B_STATEMENT, GeneratedTypes.B_STATEMENT),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.CAST_EXPR, GeneratedTypes.CAST_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.CHOICE_JOINED, GeneratedTypes.CHOICE_JOINED),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.C_STATEMENT, GeneratedTypes.C_STATEMENT),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.EXPR, GeneratedTypes.EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.GRAMMAR_ELEMENT, GeneratedTypes.GRAMMAR_ELEMENT),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.IDENTIFIER, GeneratedTypes.IDENTIFIER),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ID_EXPR, GeneratedTypes.ID_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.INCLUDE_SECTION, GeneratedTypes.INCLUDE_SECTION),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.INCLUDE__SECTION__ALT, GeneratedTypes.INCLUDE__SECTION__ALT),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ITEM_EXPR, GeneratedTypes.ITEM_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.LEFT_SHADOW, GeneratedTypes.LEFT_SHADOW),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.LEFT_SHADOW_TEST, GeneratedTypes.LEFT_SHADOW_TEST),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.LITERAL, GeneratedTypes.LITERAL),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.MISSING_EXTERNAL_TYPE, GeneratedTypes.MISSING_EXTERNAL_TYPE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.MUL_EXPR, GeneratedTypes.MUL_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.PLUS_EXPR, GeneratedTypes.PLUS_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.REF_EXPR, GeneratedTypes.REF_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ROOT, GeneratedTypes.ROOT),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ROOT_B, GeneratedTypes.ROOT_B),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ROOT_C, GeneratedTypes.ROOT_C),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ROOT_D, GeneratedTypes.ROOT_D),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.SOME_EXPR, GeneratedTypes.SOME_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.SPECIAL_REF, GeneratedTypes.SPECIAL_REF),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.STATEMENT, GeneratedTypes.STATEMENT),

      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.OP_MUL, GeneratedTypes.OP_MUL),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.OP_DIV, GeneratedTypes.OP_DIV),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.SLASH, GeneratedTypes.SLASH),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ID, GeneratedTypes.ID),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.NUMBER, GeneratedTypes.NUMBER),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.NOTSPACE, GeneratedTypes.NOTSPACE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.OF, GeneratedTypes.OF)
    );
  }
}
// ---- ../myPsi/output/generated/psi/XLeftShadow.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XLeftShadow extends XComposite {

  @Nullable
  XLeftShadow getLeftShadow();

  @NotNull
  List<XIdentifier> getIdentifierList();

}
// ---- ../myPsi/output/generated/psi/XLeftShadowTest.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XLeftShadowTest extends XComposite {

  @Nullable
  XLeftShadow getLeftShadow();

  @Nullable
  XIdentifier getIdentifier();

}
// ---- ../myPsi/output/generated/psi/XAStatement.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XAStatement extends XStatement {

  @Nullable
  PsiElement getId();

  @Nullable
  PsiElement getNumber();

}
// ---- ../myPsi/output/generated/psi/XBStatement.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XBStatement extends XStatement {

  @Nullable
  PsiElement getId();

  @Nullable
  PsiElement getNumber();

}
// ---- ../myPsi/output/generated/psi/XBlockOf.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XBlockOf extends XComposite {

}
// ---- ../myPsi/output/generated/psi/XCStatement.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XCStatement extends XStatement {

  @Nullable
  PsiElement getId();

  @Nullable
  PsiElement getNumber();

}
// ---- ../myPsi/output/generated/psi/XCastExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XCastExpr extends XExpr {

  @NotNull
  XExpr getExpr();

  @NotNull
  PsiElement getId();

}
// ---- ../myPsi/output/generated/psi/XChoiceJoined.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XChoiceJoined extends XLiteral {

  @NotNull
  XLiteral getLiteral();

  @NotNull
  PsiElement getId();

}
// ---- ../myPsi/output/generated/psi/XExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XExpr extends XComposite {

  @NotNull
  List<XExpr> getKids();

  //WARNING: missing(...) is skipped
  //matching missing(XExpr, ...)
  //methods are not found in null

}
// ---- ../myPsi/output/generated/psi/XExternalType.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XExternalType extends XExpr {

  @NotNull
  PsiElement getNumber();

}
// ---- ../myPsi/output/generated/psi/XExternalType2.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XExternalType2 extends XExpr {

  @NotNull
  PsiElement getId();

}
// ---- ../myPsi/output/generated/psi/XGrammarElement.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XGrammarElement extends XComposite {

  @NotNull
  XExpr getExpr();

}
// ---- ../myPsi/output/generated/psi/XIdentifier.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XIdentifier extends XComposite {

  @NotNull
  PsiElement getId();

}
// ---- ../myPsi/output/generated/psi/XIncludeSectionAlt.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XIncludeSectionAlt extends XComposite {

  @NotNull
  PsiElement getId();

  @NotNull
  PsiElement getNumber();

}
// ---- ../myPsi/output/generated/psi/XIncludeSection.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XIncludeSection extends XComposite {

  @NotNull
  XIncludeSectionAlt getIncludeSectionAlt();

  @NotNull
  XIncludeSection getIncludeSection();

  @NotNull
  PsiElement getId();

  @NotNull
  PsiElement getNumber();

}
// ---- ../myPsi/output/generated/psi/XItemExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XItemExpr extends XExpr {

  @NotNull
  XExpr getExpr();

  @NotNull
  PsiElement getNumber();

}
// ---- ../myPsi/output/generated/psi/XLiteral.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XLiteral extends XExpr {

  @NotNull
  PsiElement getNumber();

}
// ---- ../myPsi/output/generated/psi/XMulExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XMulExpr extends XExpr {

}
// ---- ../myPsi/output/generated/psi/XNamedElement.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface XNamedElement extends PsiNameIdentifierOwner {

  @NotNull
  List<XIdentifier> getIdentifierList();

}
// ---- ../myPsi/output/generated/psi/XOtherExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XOtherExpr extends XExpr {

}
// ---- ../myPsi/output/generated/psi/XPlusExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XPlusExpr extends XExpr {

}
// ---- ../myPsi/output/generated/psi/XRefExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XRefExpr extends XExpr, MyRef {

  @NotNull
  XIdentifier getIdentifier();

}
// ---- ../myPsi/output/generated/psi/XRoot.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XRoot extends XComposite {

}
// ---- ../myPsi/output/generated/psi/XRootB.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XRootB extends XRoot {

}
// ---- ../myPsi/output/generated/psi/XRootC.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XRootC extends XRoot {

  @NotNull
  XBlockOf getBlockOf();

}
// ---- ../myPsi/output/generated/psi/XRootD.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XRootD extends XRoot {

  @NotNull
  List<XGrammarElement> getGrammarElementList();

}
// ---- ../myPsi/output/generated/psi/XSomeExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XSomeExpr extends XExpr {

}
// ---- ../myPsi/output/generated/psi/XSpecialRef.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XSpecialRef extends XRefExpr {

  @NotNull
  XRefExpr getRefExpr();

}
// ---- ../myPsi/output/generated/psi/XStatement.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XStatement extends XComposite {

}
// ---- ../myPsi/output/generated/psi/XWrappingStatement.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XWrappingStatement extends XStatement {

  @NotNull
  XStatement getStatement();

}
// ---- ../myPsi/output/generated/psi/impl/XLeftShadowImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XLeftShadowImpl extends ASTWrapperPsiElement implements XLeftShadow {

  public XLeftShadowImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull XVisitor visitor) {
    visitor.visitLeftShadow(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public XLeftShadow getLeftShadow() {
    return findChildByClass(XLeftShadow.class);
  }

  @Override
  @NotNull
  public List<XIdentifier> getIdentifierList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, XIdentifier.class);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XLeftShadowTestImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XLeftShadowTestImpl extends ASTWrapperPsiElement implements XLeftShadowTest {

  public XLeftShadowTestImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull XVisitor visitor) {
    visitor.visitLeftShadowTest(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public XLeftShadow getLeftShadow() {
    return findChildByClass(XLeftShadow.class);
  }

  @Override
  @Nullable
  public XIdentifier getIdentifier() {
    return findChildByClass(XIdentifier.class);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XAStatementImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XAStatementImpl extends XStatementImpl implements XAStatement {

  public XAStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitAStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(ID);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findChildByType(NUMBER);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XBStatementImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XBStatementImpl extends XStatementImpl implements XBStatement {

  public XBStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitBStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(ID);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findChildByType(NUMBER);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XBlockOfImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XBlockOfImpl extends ASTWrapperPsiElement implements XBlockOf {

  public XBlockOfImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull XVisitor visitor) {
    visitor.visitBlockOf(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XCStatementImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XCStatementImpl extends XStatementImpl implements XCStatement {

  public XCStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitCStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(ID);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findChildByType(NUMBER);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XCastExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XCastExprImpl extends XExprImpl implements XCastExpr {

  public XCastExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitCastExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public XExpr getExpr() {
    return findNotNullChildByClass(XExpr.class);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XChoiceJoinedImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XChoiceJoinedImpl extends XLiteralImpl implements XChoiceJoined {

  public XChoiceJoinedImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitChoiceJoined(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public XLiteral getLiteral() {
    return findNotNullChildByClass(XLiteral.class);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XExprImpl extends ASTWrapperPsiElement implements XExpr {

  public XExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull XVisitor visitor) {
    visitor.visitExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<XExpr> getKids() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, XExpr.class);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XExternalTypeImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XExternalTypeImpl extends XExprImpl implements XExternalType {

  public XExternalTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitExternalType(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getNumber() {
    return findNotNullChildByType(NUMBER);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XExternalType2Impl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XExternalType2Impl extends XExprImpl implements XExternalType2 {

  public XExternalType2Impl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitExternalType2(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XGrammarElementImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XGrammarElementImpl extends ASTWrapperPsiElement implements XGrammarElement {

  public XGrammarElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull XVisitor visitor) {
    visitor.visitGrammarElement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public XExpr getExpr() {
    return findNotNullChildByClass(XExpr.class);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XIdentifierImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XIdentifierImpl extends ASTWrapperPsiElement implements XIdentifier {

  public XIdentifierImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull XVisitor visitor) {
    visitor.visitIdentifier(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XIncludeSectionAltImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XIncludeSectionAltImpl extends ASTWrapperPsiElement implements XIncludeSectionAlt {

  public XIncludeSectionAltImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull XVisitor visitor) {
    visitor.visitIncludeSectionAlt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

  @Override
  @NotNull
  public PsiElement getNumber() {
    return findNotNullChildByType(NUMBER);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XIncludeSectionImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XIncludeSectionImpl extends ASTWrapperPsiElement implements XIncludeSection {

  public XIncludeSectionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull XVisitor visitor) {
    visitor.visitIncludeSection(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public XIncludeSectionAlt getIncludeSectionAlt() {
    return findNotNullChildByClass(XIncludeSectionAlt.class);
  }

  @Override
  @NotNull
  public XIncludeSection getIncludeSection() {
    return findNotNullChildByClass(XIncludeSection.class);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

  @Override
  @NotNull
  public PsiElement getNumber() {
    return findNotNullChildByType(NUMBER);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XItemExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XItemExprImpl extends XExprImpl implements XItemExpr {

  public XItemExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitItemExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public XExpr getExpr() {
    return findNotNullChildByClass(XExpr.class);
  }

  @Override
  @NotNull
  public PsiElement getNumber() {
    return findNotNullChildByType(NUMBER);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XLiteralImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XLiteralImpl extends XExprImpl implements XLiteral {

  public XLiteralImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitLiteral(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getNumber() {
    return findNotNullChildByType(NUMBER);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XMulExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XMulExprImpl extends XExprImpl implements XMulExpr {

  public XMulExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitMulExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XNamedElementImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XNamedElementImpl extends ASTWrapperPsiElement implements XNamedElement {

  public XNamedElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull XVisitor visitor) {
    visitor.visitNamedElement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<XIdentifier> getIdentifierList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, XIdentifier.class);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XOtherExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XOtherExprImpl extends XExprImpl implements XOtherExpr {

  public XOtherExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitOtherExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XPlusExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XPlusExprImpl extends XExprImpl implements XPlusExpr {

  public XPlusExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitPlusExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XRefExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XRefExprImpl extends MyRefImpl implements XRefExpr {

  public XRefExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull XVisitor visitor) {
    visitor.visitRefExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public XIdentifier getIdentifier() {
    return findNotNullChildByClass(XIdentifier.class);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XRootImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public abstract class XRootImpl extends ASTWrapperPsiElement implements XRoot {

  public XRootImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull XVisitor visitor) {
    visitor.visitRoot(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XRootBImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XRootBImpl extends XRootImpl implements XRootB {

  public XRootBImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitRootB(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XRootCImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XRootCImpl extends XRootImpl implements XRootC {

  public XRootCImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitRootC(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public XBlockOf getBlockOf() {
    return findNotNullChildByClass(XBlockOf.class);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XRootDImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XRootDImpl extends XRootImpl implements XRootD {

  public XRootDImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitRootD(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<XGrammarElement> getGrammarElementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, XGrammarElement.class);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XSomeExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public abstract class XSomeExprImpl extends XExprImpl implements XSomeExpr {

  public XSomeExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitSomeExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XSpecialRefImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XSpecialRefImpl extends XRefExprImpl implements XSpecialRef {

  public XSpecialRefImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitSpecialRef(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public XRefExpr getRefExpr() {
    return findNotNullChildByClass(XRefExpr.class);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XStatementImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public abstract class XStatementImpl extends ASTWrapperPsiElement implements XStatement {

  public XStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull XVisitor visitor) {
    visitor.visitStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

}
// ---- ../myPsi/output/generated/psi/impl/XWrappingStatementImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class XWrappingStatementImpl extends XStatementImpl implements XWrappingStatement {

  public XWrappingStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull XVisitor visitor) {
    visitor.visitWrappingStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) accept((XVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public XStatement getStatement() {
    return findNotNullChildByClass(XStatement.class);
  }

}
// ---- ../myPsi/output/generated/psi/XVisitor.java -----------------
//header.txt
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNameIdentifierOwner;

public class XVisitor extends PsiElementVisitor {

  public void visitLeftShadow(@NotNull XLeftShadow o) {
    visitComposite(o);
  }

  public void visitLeftShadowTest(@NotNull XLeftShadowTest o) {
    visitComposite(o);
  }

  public void visitAStatement(@NotNull XAStatement o) {
    visitStatement(o);
  }

  public void visitBStatement(@NotNull XBStatement o) {
    visitStatement(o);
  }

  public void visitBlockOf(@NotNull XBlockOf o) {
    visitComposite(o);
  }

  public void visitCStatement(@NotNull XCStatement o) {
    visitStatement(o);
  }

  public void visitCastExpr(@NotNull XCastExpr o) {
    visitExpr(o);
  }

  public void visitChoiceJoined(@NotNull XChoiceJoined o) {
    visitLiteral(o);
  }

  public void visitExpr(@NotNull XExpr o) {
    visitComposite(o);
  }

  public void visitExternalType(@NotNull XExternalType o) {
    visitExpr(o);
  }

  public void visitExternalType2(@NotNull XExternalType2 o) {
    visitExpr(o);
  }

  public void visitGrammarElement(@NotNull XGrammarElement o) {
    visitComposite(o);
  }

  public void visitIdentifier(@NotNull XIdentifier o) {
    visitComposite(o);
  }

  public void visitIncludeSectionAlt(@NotNull XIncludeSectionAlt o) {
    visitComposite(o);
  }

  public void visitIncludeSection(@NotNull XIncludeSection o) {
    visitComposite(o);
  }

  public void visitItemExpr(@NotNull XItemExpr o) {
    visitExpr(o);
  }

  public void visitLiteral(@NotNull XLiteral o) {
    visitExpr(o);
  }

  public void visitMulExpr(@NotNull XMulExpr o) {
    visitExpr(o);
  }

  public void visitNamedElement(@NotNull XNamedElement o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitOtherExpr(@NotNull XOtherExpr o) {
    visitExpr(o);
  }

  public void visitPlusExpr(@NotNull XPlusExpr o) {
    visitExpr(o);
  }

  public void visitRefExpr(@NotNull XRefExpr o) {
    visitExpr(o);
    // visitMyRef(o);
  }

  public void visitRoot(@NotNull XRoot o) {
    visitComposite(o);
  }

  public void visitRootB(@NotNull XRootB o) {
    visitRoot(o);
  }

  public void visitRootC(@NotNull XRootC o) {
    visitRoot(o);
  }

  public void visitRootD(@NotNull XRootD o) {
    visitRoot(o);
  }

  public void visitSomeExpr(@NotNull XSomeExpr o) {
    visitExpr(o);
  }

  public void visitSpecialRef(@NotNull XSpecialRef o) {
    visitRefExpr(o);
  }

  public void visitStatement(@NotNull XStatement o) {
    visitComposite(o);
  }

  public void visitWrappingStatement(@NotNull XWrappingStatement o) {
    visitStatement(o);
  }

  public void visitPsiNameIdentifierOwner(@NotNull PsiNameIdentifierOwner o) {
    visitElement(o);
  }

  public void visitComposite(@NotNull XComposite o) {
    visitElement(o);
  }

}