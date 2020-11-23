// ---- GeneratedTypes.java -----------------
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
// ---- XLeftShadow.java -----------------
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
// ---- XLeftShadowTest.java -----------------
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
// ---- XAStatement.java -----------------
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
// ---- XBStatement.java -----------------
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
// ---- XBlockOf.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XBlockOf extends XComposite {

}
// ---- XCStatement.java -----------------
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
// ---- XCastExpr.java -----------------
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
// ---- XChoiceJoined.java -----------------
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
// ---- XExpr.java -----------------
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
// ---- XExternalType.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XExternalType extends XExpr {

  @NotNull
  PsiElement getNumber();

}
// ---- XExternalType2.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XExternalType2 extends XExpr {

  @NotNull
  PsiElement getId();

}
// ---- XGrammarElement.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XGrammarElement extends XComposite {

  @NotNull
  XExpr getExpr();

}
// ---- XIdentifier.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XIdentifier extends XComposite {

  @NotNull
  PsiElement getId();

}
// ---- XIncludeSectionAlt.java -----------------
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
// ---- XIncludeSection.java -----------------
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
// ---- XItemExpr.java -----------------
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
// ---- XLiteral.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XLiteral extends XExpr {

  @NotNull
  PsiElement getNumber();

}
// ---- XMulExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XMulExpr extends XExpr {

}
// ---- XNamedElement.java -----------------
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
// ---- XOtherExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XOtherExpr extends XExpr {

}
// ---- XPlusExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XPlusExpr extends XExpr {

}
// ---- XRefExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XRefExpr extends XExpr, MyRef {

  @NotNull
  XIdentifier getIdentifier();

}
// ---- XRoot.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XRoot extends XComposite {

}
// ---- XRootB.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XRootB extends XRoot {

}
// ---- XRootC.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XRootC extends XRoot {

  @NotNull
  XBlockOf getBlockOf();

}
// ---- XRootD.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XRootD extends XRoot {

  @NotNull
  List<XGrammarElement> getGrammarElementList();

}
// ---- XSomeExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XSomeExpr extends XExpr {

}
// ---- XSpecialRef.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XSpecialRef extends XRefExpr {

  @NotNull
  XRefExpr getRefExpr();

}
// ---- XStatement.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XStatement extends XComposite {

}
// ---- XWrappingStatement.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XWrappingStatement extends XStatement {

  @NotNull
  XStatement getStatement();

}
// ---- XLeftShadowImpl.java -----------------
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
// ---- XLeftShadowTestImpl.java -----------------
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
// ---- XAStatementImpl.java -----------------
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
// ---- XBStatementImpl.java -----------------
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
// ---- XBlockOfImpl.java -----------------
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
// ---- XCStatementImpl.java -----------------
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
// ---- XCastExprImpl.java -----------------
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
// ---- XChoiceJoinedImpl.java -----------------
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
// ---- XExprImpl.java -----------------
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
// ---- XExternalTypeImpl.java -----------------
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
// ---- XExternalType2Impl.java -----------------
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
// ---- XGrammarElementImpl.java -----------------
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
// ---- XIdentifierImpl.java -----------------
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
// ---- XIncludeSectionAltImpl.java -----------------
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
// ---- XIncludeSectionImpl.java -----------------
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
// ---- XItemExprImpl.java -----------------
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
// ---- XLiteralImpl.java -----------------
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
// ---- XMulExprImpl.java -----------------
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
// ---- XNamedElementImpl.java -----------------
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
// ---- XOtherExprImpl.java -----------------
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
// ---- XPlusExprImpl.java -----------------
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
// ---- XRefExprImpl.java -----------------
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
// ---- XRootImpl.java -----------------
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
// ---- XRootBImpl.java -----------------
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
// ---- XRootCImpl.java -----------------
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
// ---- XRootDImpl.java -----------------
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
// ---- XSomeExprImpl.java -----------------
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
// ---- XSpecialRefImpl.java -----------------
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
// ---- XStatementImpl.java -----------------
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
// ---- XWrappingStatementImpl.java -----------------
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
// ---- XVisitor.java -----------------
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