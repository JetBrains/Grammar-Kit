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
        return new BlockOfImpl(node);
      }
      else if (type == EXPR) {
        return new ExprImpl(node);
      }
      else if (type == GRAMMAR_ELEMENT) {
        return new GrammarElementImpl(node);
      }
      else if (type == IDENTIFIER) {
        return new IdentifierImpl(node);
      }
      else if (type == ID_EXPR) {
        return new ExternalType2Impl(node);
      }
      else if (type == LITERAL) {
        return new LiteralImpl(node);
      }
      else if (type == MISSING_EXTERNAL_TYPE) {
        return new ExternalTypeImpl(node);
      }
      else if (type == MUL_EXPR) {
        return new MulExprImpl(node);
      }
      else if (type == PLUS_EXPR) {
        return new PlusExprImpl(node);
      }
      else if (type == REF_EXPR) {
        return new RefExprImpl(node);
      }
      else if (type == ROOT_B) {
        return new RootBImpl(node);
      }
      else if (type == ROOT_C) {
        return new RootCImpl(node);
      }
      else if (type == ROOT_D) {
        return new RootDImpl(node);
      }
      else if (type == SPECIALREF) {
        return new SpecialRefImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- BlockOf.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import generated.CompositeElement;

public interface BlockOf extends CompositeElement {

}
// ---- Expr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import generated.CompositeElement;

public interface Expr extends CompositeElement {

  @NotNull
  List<Expr> getExprList();

}
// ---- ExternalType.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ExternalType extends Expr {

  @NotNull
  PsiElement getNumber();

}
// ---- ExternalType2.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ExternalType2 extends Expr {

  @NotNull
  PsiElement getId();

}
// ---- GrammarElement.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import generated.CompositeElement;

public interface GrammarElement extends CompositeElement {

  @NotNull
  Expr getExpr();

}
// ---- Identifier.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import generated.CompositeElement;

public interface Identifier extends CompositeElement {

  @NotNull
  PsiElement getId();

}
// ---- Literal.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Literal extends Expr {

  @NotNull
  PsiElement getNumber();

}
// ---- MulExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface MulExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- PlusExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PlusExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- RefExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RefExpr extends Expr {

  @NotNull
  Identifier getIdentifier();

}
// ---- RootB.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RootB extends Root {

}
// ---- RootC.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RootC extends Root {

  @NotNull
  BlockOf getBlockOf();

}
// ---- RootD.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RootD extends Root {

  @NotNull
  List<GrammarElement> getGrammarElementList();

}
// ---- SpecialRef.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SpecialRef extends RefExpr {

  @NotNull
  Identifier getIdentifier();

  @NotNull
  RefExpr getRefExpr();

}
// ---- BlockOfImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.CompositeElementImpl;
import generated.psi.*;

public class BlockOfImpl extends CompositeElementImpl implements BlockOf {

  public BlockOfImpl(ASTNode node) {
    super(node);
  }

}
// ---- ExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.CompositeElementImpl;
import generated.psi.*;

public class ExprImpl extends CompositeElementImpl implements Expr {

  public ExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- ExternalTypeImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class ExternalTypeImpl extends ExprImpl implements ExternalType {

  public ExternalTypeImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getNumber() {
    return findNotNullChildByType(NUMBER);
  }

}
// ---- ExternalType2Impl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class ExternalType2Impl extends ExprImpl implements ExternalType2 {

  public ExternalType2Impl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

}
// ---- GrammarElementImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.CompositeElementImpl;
import generated.psi.*;

public class GrammarElementImpl extends CompositeElementImpl implements GrammarElement {

  public GrammarElementImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public Expr getExpr() {
    return findNotNullChildByClass(Expr.class);
  }

}
// ---- IdentifierImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.CompositeElementImpl;
import generated.psi.*;

public class IdentifierImpl extends CompositeElementImpl implements Identifier {

  public IdentifierImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

}
// ---- LiteralImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class LiteralImpl extends ExprImpl implements Literal {

  public LiteralImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getNumber() {
    return findNotNullChildByType(NUMBER);
  }

}
// ---- MulExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class MulExprImpl extends ExprImpl implements MulExpr {

  public MulExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- PlusExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class PlusExprImpl extends ExprImpl implements PlusExpr {

  public PlusExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- RefExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class RefExprImpl extends MyRefImpl implements RefExpr {

  public RefExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public Identifier getIdentifier() {
    return findNotNullChildByClass(Identifier.class);
  }

}
// ---- RootBImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class RootBImpl extends RootImpl implements RootB {

  public RootBImpl(ASTNode node) {
    super(node);
  }

}
// ---- RootCImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class RootCImpl extends RootImpl implements RootC {

  public RootCImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public BlockOf getBlockOf() {
    return findNotNullChildByClass(BlockOf.class);
  }

}
// ---- RootDImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class RootDImpl extends RootImpl implements RootD {

  public RootDImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<GrammarElement> getGrammarElementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GrammarElement.class);
  }

}
// ---- SpecialRefImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class SpecialRefImpl extends RefExprImpl implements SpecialRef {

  public SpecialRefImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public Identifier getIdentifier() {
    return findNotNullChildByClass(Identifier.class);
  }

  @Override
  @NotNull
  public RefExpr getRefExpr() {
    return findNotNullChildByClass(RefExpr.class);
  }

}