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

  IElementType A_EXPR = new IElementType("A_EXPR");
  IElementType B_EXPR = new IElementType("B_EXPR");
  IElementType BLOCKOF = new IElementType("BLOCKOF");
  IElementType EXPR = new IElementType("EXPR");
  IElementType GRAMMAR_ELEMENT = new IElementType("GRAMMAR_ELEMENT");
  IElementType ID_EXPR = new IElementType("ID_EXPR");
  IElementType MUL_EXPR = new IElementType("MUL_EXPR");
  IElementType PLUS_EXPR = new IElementType("PLUS_EXPR");
  IElementType ROOT_B = new IElementType("ROOT_B");
  IElementType ROOT_C = new IElementType("ROOT_C");
  IElementType ROOT_D = new IElementType("ROOT_D");

  IElementType ID = new IElementType("id");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == A_EXPR) {
        return new AExprImpl(node);
      }
      else if (type == B_EXPR) {
        return new BExprImpl(node);
      }
      else if (type == BLOCKOF) {
        return new BlockOfImpl(node);
      }
      else if (type == EXPR) {
        return new ExprImpl(node);
      }
      else if (type == GRAMMAR_ELEMENT) {
        return new GrammarElementImpl(node);
      }
      else if (type == ID_EXPR) {
        return new IdExprImpl(node);
      }
      else if (type == MUL_EXPR) {
        return new MulExprImpl(node);
      }
      else if (type == PLUS_EXPR) {
        return new PlusExprImpl(node);
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
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- AExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AExpr extends Expr {

  @NotNull
  public List<Expr> getExprList();

}
// ---- BExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BExpr extends Expr {

  @NotNull
  public List<Expr> getExprList();

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
// ---- IdExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdExpr extends Expr {

  @NotNull
  public PsiElement getId();

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
// ---- AExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class AExprImpl extends ExprImpl implements AExpr {

  public AExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- BExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class BExprImpl extends ExprImpl implements BExpr {

  public BExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

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
// ---- IdExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import generated.psi.*;

public class IdExprImpl extends ExprImpl implements IdExpr {

  public IdExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
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