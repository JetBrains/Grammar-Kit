// ---- ParserTypes.java -----------------
//header.txt
package generated;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IElementType;
import generated.psi.impl.*;

public interface ParserTypes {

  IElementType BLOCKOF = new IElementType("BLOCKOF");
  IElementType EXPR = new IElementType("EXPR");
  IElementType GRAMMAR_ELEMENT = new IElementType("GRAMMAR_ELEMENT");
  IElementType IDENTIFIER = new IElementType("IDENTIFIER");
  IElementType ID_EXPR = new IElementType("ID_EXPR");
  IElementType LITERAL = new IElementType("LITERAL");
  IElementType MISSING_EXTERNAL_TYPE = new IElementType("MISSING_EXTERNAL_TYPE");
  IElementType MUL_EXPR = new IElementType("MUL_EXPR");
  IElementType PLUS_EXPR = new IElementType("PLUS_EXPR");
  IElementType REF_EXPR = new IElementType("REF_EXPR");
  IElementType ROOT_B = new IElementType("ROOT_B");
  IElementType ROOT_C = new IElementType("ROOT_C");
  IElementType ROOT_D = new IElementType("ROOT_D");
  IElementType SPECIALREF = new IElementType("SPECIALREF");

  IElementType ID = new IElementType("id");
  IElementType NUMBER = new IElementType("number");
  IElementType OF = new IElementType("OF");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == BLOCKOF) {
        return new XBlockOfImpl(node);
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
      else if (type == SPECIALREF) {
        return new XSpecialRefImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- XBlockOf.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XBlockOf extends XComposite {

}
// ---- XExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XExpr extends XComposite {

  @NotNull
  List<XExpr> getExprList();

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

  @NotNull
  List<XExpr> getExprList();

}
// ---- XPlusExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XPlusExpr extends XExpr {

  @NotNull
  List<XExpr> getExprList();

}
// ---- XRefExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XRefExpr extends XExpr {

  @NotNull
  XIdentifier getIdentifier();

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
// ---- XSpecialRef.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XSpecialRef extends XRefExpr {

  @NotNull
  XIdentifier getIdentifier();

  @NotNull
  XRefExpr getRefExpr();

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
import static generated.ParserTypes.*;
import generated.CompositeElementImpl;
import generated.psi.*;

public class XBlockOfImpl extends CompositeElementImpl implements XBlockOf {

  public XBlockOfImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitBlockOf(this);
    else super.accept(visitor);
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
import static generated.ParserTypes.*;
import generated.CompositeElementImpl;
import generated.psi.*;

public class XExprImpl extends CompositeElementImpl implements XExpr {

  public XExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<XExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, XExpr.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitExpr(this);
    else super.accept(visitor);
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
import static generated.ParserTypes.*;
import generated.psi.*;

public class XExternalTypeImpl extends XExprImpl implements XExternalType {

  public XExternalTypeImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getNumber() {
    return findNotNullChildByType(NUMBER);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitExternalType(this);
    else super.accept(visitor);
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
import static generated.ParserTypes.*;
import generated.psi.*;

public class XExternalType2Impl extends XExprImpl implements XExternalType2 {

  public XExternalType2Impl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitExternalType2(this);
    else super.accept(visitor);
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
import static generated.ParserTypes.*;
import generated.CompositeElementImpl;
import generated.psi.*;

public class XGrammarElementImpl extends CompositeElementImpl implements XGrammarElement {

  public XGrammarElementImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public XExpr getExpr() {
    return findNotNullChildByClass(XExpr.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitGrammarElement(this);
    else super.accept(visitor);
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
import static generated.ParserTypes.*;
import generated.CompositeElementImpl;
import generated.psi.*;

public class XIdentifierImpl extends CompositeElementImpl implements XIdentifier {

  public XIdentifierImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitIdentifier(this);
    else super.accept(visitor);
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
import static generated.ParserTypes.*;
import generated.psi.*;

public class XLiteralImpl extends XExprImpl implements XLiteral {

  public XLiteralImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getNumber() {
    return findNotNullChildByType(NUMBER);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitLiteral(this);
    else super.accept(visitor);
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
import static generated.ParserTypes.*;
import generated.psi.*;

public class XMulExprImpl extends XExprImpl implements XMulExpr {

  public XMulExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<XExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, XExpr.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitMulExpr(this);
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
import static generated.ParserTypes.*;
import generated.psi.*;

public class XPlusExprImpl extends XExprImpl implements XPlusExpr {

  public XPlusExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<XExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, XExpr.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitPlusExpr(this);
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
import static generated.ParserTypes.*;
import generated.psi.*;

public class XRefExprImpl extends MyRefImpl implements XRefExpr {

  public XRefExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public XIdentifier getIdentifier() {
    return findNotNullChildByClass(XIdentifier.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitRefExpr(this);
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
import static generated.ParserTypes.*;
import generated.psi.*;

public class XRootBImpl extends XRootImpl implements XRootB {

  public XRootBImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitRootB(this);
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
import static generated.ParserTypes.*;
import generated.psi.*;

public class XRootCImpl extends XRootImpl implements XRootC {

  public XRootCImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public XBlockOf getBlockOf() {
    return findNotNullChildByClass(XBlockOf.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitRootC(this);
    else super.accept(visitor);
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
import static generated.ParserTypes.*;
import generated.psi.*;

public class XRootDImpl extends XRootImpl implements XRootD {

  public XRootDImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<XGrammarElement> getGrammarElementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, XGrammarElement.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitRootD(this);
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
import static generated.ParserTypes.*;
import generated.psi.*;

public class XSpecialRefImpl extends XRefExprImpl implements XSpecialRef {

  public XSpecialRefImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public XIdentifier getIdentifier() {
    return findNotNullChildByClass(XIdentifier.class);
  }

  @Override
  @NotNull
  public XRefExpr getRefExpr() {
    return findNotNullChildByClass(XRefExpr.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitSpecialRef(this);
    else super.accept(visitor);
  }

}
// ---- XVisitor.java -----------------
//header.txt
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class XVisitor extends PsiElementVisitor {

  public void visitBlockOf(@NotNull XBlockOf o) {
    visitComposite(o);
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

  public void visitLiteral(@NotNull XLiteral o) {
    visitExpr(o);
  }

  public void visitMulExpr(@NotNull XMulExpr o) {
    visitExpr(o);
  }

  public void visitPlusExpr(@NotNull XPlusExpr o) {
    visitExpr(o);
  }

  public void visitRefExpr(@NotNull XRefExpr o) {
    visitExpr(o);
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

  public void visitSpecialRef(@NotNull XSpecialRef o) {
    visitRefExpr(o);
  }

  public void visitRoot(XRoot o) {
    visitElement(o);
  }

  public void visitComposite(XComposite o) {
    visitElement(o);
  }

}