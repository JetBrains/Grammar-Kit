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
  IElementType LITERAL = new IElementType("LITERAL");
  IElementType MUL_EXPR = new IElementType("MUL_EXPR");
  IElementType PLUS_EXPR = new IElementType("PLUS_EXPR");
  IElementType QREF_EXPR = new IElementType("QREF_EXPR");
  IElementType REF_EXPR = new IElementType("REF_EXPR");
  IElementType REF_EXPRESSION = new IElementType("REF_EXPRESSION");
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
      else if (type == LITERAL) {
        return new LiteralImpl(node);
      }
      else if (type == MUL_EXPR) {
        return new MulExprImpl(node);
      }
      else if (type == PLUS_EXPR) {
        return new PlusExprImpl(node);
      }
      else if (type == QREF_EXPR) {
        return new QrefExprImpl(node);
      }
      else if (type == REF_EXPR) {
        return new RefExprImpl(node);
      }
      else if (type == REF_EXPRESSION) {
        return new RefExpressionImpl(node);
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

public interface Expr extends Expr {

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
  public Expr getExpr();

}
// ---- Literal.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Literal extends Expr {

  @NotNull
  public PsiElement getNumber();

}
// ---- MulExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface MulExpr extends Expr {

  @NotNull
  public List<Expr> getExprList();

}
// ---- PlusExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PlusExpr extends Expr {

  @NotNull
  public List<Expr> getExprList();

}
// ---- QrefExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface QrefExpr extends RefExpr {

  @NotNull
  public Expr getExpr();

  @NotNull
  public PsiElement getId();

}
// ---- RefExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RefExpr extends Expr {

  @NotNull
  public PsiElement getId();

}
// ---- RefExpression.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RefExpression extends Expr {

}
// ---- Root.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import generated.CompositeElement;

public interface Root extends CompositeElement {

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
  public BlockOf getBlockOf();

}
// ---- RootD.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RootD extends Root {

  @NotNull
  public List<GrammarElement> getGrammarElementList();

}
// ---- SpecialRef.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SpecialRef extends QrefExpr {

  @NotNull
  public Expr getExpr();

  @NotNull
  public PsiElement getId();

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
import generated.psi.*;

public class ExprImpl extends ExprImpl implements Expr {

  public ExprImpl(ASTNode node) {
    super(node);
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
// ---- QrefExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class QrefExprImpl extends RefExprImpl implements QrefExpr {

  public QrefExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public Expr getExpr() {
    return findNotNullChildByClass(Expr.class);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
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

public class RefExprImpl extends ExprImpl implements RefExpr {

  public RefExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

}
// ---- RefExpressionImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class RefExpressionImpl extends ExprImpl implements RefExpression {

  public RefExpressionImpl(ASTNode node) {
    super(node);
  }

}
// ---- RootImpl.java -----------------
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

public class RootImpl extends CompositeElementImpl implements Root {

  public RootImpl(ASTNode node) {
    super(node);
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

public class SpecialRefImpl extends QrefExprImpl implements SpecialRef {

  public SpecialRefImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public Expr getExpr() {
    return findNotNullChildByClass(Expr.class);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

}