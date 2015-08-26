// ---- ExpressionTypes.java -----------------
//header.txt
package org.intellij.grammar.expression;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import generated.psi.impl.*;

public interface ExpressionTypes {

  IElementType ARG_LIST = ExpressionParserDefinition.createType("ARG_LIST");
  IElementType ASSIGN_EXPR = ExpressionParserDefinition.createType("ASSIGN_EXPR");
  IElementType BETWEEN_EXPR = ExpressionParserDefinition.createType("BETWEEN_EXPR");
  IElementType CALL_EXPR = ExpressionParserDefinition.createType("CALL_EXPR");
  IElementType CONDITIONAL_EXPR = ExpressionParserDefinition.createType("CONDITIONAL_EXPR");
  IElementType DIV_EXPR = ExpressionParserDefinition.createType("DIV_EXPR");
  IElementType ELVIS_EXPR = ExpressionParserDefinition.createType("ELVIS_EXPR");
  IElementType EXPR = ExpressionParserDefinition.createType("EXPR");
  IElementType EXP_EXPR = ExpressionParserDefinition.createType("EXP_EXPR");
  IElementType FACTORIAL_EXPR = ExpressionParserDefinition.createType("FACTORIAL_EXPR");
  IElementType IDENTIFIER = ExpressionParserDefinition.createType("IDENTIFIER");
  IElementType IS_NOT_EXPR = ExpressionParserDefinition.createType("IS_NOT_EXPR");
  IElementType LITERAL_EXPR = ExpressionParserDefinition.createType("LITERAL_EXPR");
  IElementType MINUS_EXPR = ExpressionParserDefinition.createType("MINUS_EXPR");
  IElementType MUL_EXPR = ExpressionParserDefinition.createType("MUL_EXPR");
  IElementType PAREN_EXPR = ExpressionParserDefinition.createType("PAREN_EXPR");
  IElementType PLUS_EXPR = ExpressionParserDefinition.createType("PLUS_EXPR");
  IElementType REF_EXPR = ExpressionParserDefinition.createType("REF_EXPR");
  IElementType SPECIAL_EXPR = ExpressionParserDefinition.createType("SPECIAL_EXPR");
  IElementType UNARY_MIN_EXPR = ExpressionParserDefinition.createType("UNARY_MIN_EXPR");
  IElementType UNARY_NOT_EXPR = ExpressionParserDefinition.createType("UNARY_NOT_EXPR");
  IElementType UNARY_PLUS_EXPR = ExpressionParserDefinition.createType("UNARY_PLUS_EXPR");
  IElementType XOR_EXPR = ExpressionParserDefinition.createType("XOR_EXPR");

  IElementType AND = ExpressionParserDefinition.createTokenType("AND");
  IElementType BETWEEN = ExpressionParserDefinition.createTokenType("BETWEEN");
  IElementType COMMENT = ExpressionParserDefinition.createTokenType("comment");
  IElementType ID = ExpressionParserDefinition.createTokenType("id");
  IElementType IS = ExpressionParserDefinition.createTokenType("IS");
  IElementType NOT = ExpressionParserDefinition.createTokenType("NOT");
  IElementType NUMBER = ExpressionParserDefinition.createTokenType("number");
  IElementType STRING = ExpressionParserDefinition.createTokenType("string");
  IElementType SYNTAX = ExpressionParserDefinition.createTokenType("syntax");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ARG_LIST) {
        return new ArgListImpl(node);
      }
      else if (type == ASSIGN_EXPR) {
        return new AssignExprImpl(node);
      }
      else if (type == BETWEEN_EXPR) {
        return new BetweenExprImpl(node);
      }
      else if (type == CALL_EXPR) {
        return new CallExprImpl(node);
      }
      else if (type == CONDITIONAL_EXPR) {
        return new ConditionalExprImpl(node);
      }
      else if (type == DIV_EXPR) {
        return new DivExprImpl(node);
      }
      else if (type == ELVIS_EXPR) {
        return new ElvisExprImpl(node);
      }
      else if (type == EXPR) {
        return new ExprImpl(node);
      }
      else if (type == EXP_EXPR) {
        return new ExpExprImpl(node);
      }
      else if (type == FACTORIAL_EXPR) {
        return new FactorialExprImpl(node);
      }
      else if (type == IDENTIFIER) {
        return new IdentifierImpl(node);
      }
      else if (type == IS_NOT_EXPR) {
        return new IsNotExprImpl(node);
      }
      else if (type == LITERAL_EXPR) {
        return new LiteralExprImpl(node);
      }
      else if (type == MINUS_EXPR) {
        return new MinusExprImpl(node);
      }
      else if (type == MUL_EXPR) {
        return new MulExprImpl(node);
      }
      else if (type == PAREN_EXPR) {
        return new ParenExprImpl(node);
      }
      else if (type == PLUS_EXPR) {
        return new PlusExprImpl(node);
      }
      else if (type == REF_EXPR) {
        return new RefExprImpl(node);
      }
      else if (type == SPECIAL_EXPR) {
        return new SpecialExprImpl(node);
      }
      else if (type == UNARY_MIN_EXPR) {
        return new UnaryMinExprImpl(node);
      }
      else if (type == UNARY_NOT_EXPR) {
        return new UnaryNotExprImpl(node);
      }
      else if (type == UNARY_PLUS_EXPR) {
        return new UnaryPlusExprImpl(node);
      }
      else if (type == XOR_EXPR) {
        return new XorExprImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- ArgList.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ArgList extends PsiElement {

  @NotNull
  List<Expr> getExprList();

}
// ---- AssignExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AssignExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- BetweenExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BetweenExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

  @NotNull
  Expr getTestExpr();

}
// ---- CallExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CallExpr extends Expr {

  @NotNull
  ArgList getArgList();

  @NotNull
  RefExpr getRefExpr();

}
// ---- ConditionalExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ConditionalExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- DivExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface DivExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- ElvisExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElvisExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- ExpExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ExpExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- Expr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Expr extends PsiElement {

}
// ---- FactorialExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface FactorialExpr extends Expr {

  @NotNull
  Expr getExpr();

}
// ---- Identifier.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Identifier extends PsiElement {

  @NotNull
  PsiElement getId();

}
// ---- IsNotExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IsNotExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- LiteralExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface LiteralExpr extends Expr {

  @NotNull
  PsiElement getNumber();

}
// ---- MinusExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface MinusExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

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
// ---- ParenExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ParenExpr extends Expr {

  @Nullable
  Expr getExpr();

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
  Expr getExpr();

  @NotNull
  Identifier getIdentifier();

}
// ---- SpecialExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SpecialExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- UnaryMinExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UnaryMinExpr extends Expr {

  @Nullable
  Expr getExpr();

}
// ---- UnaryNotExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UnaryNotExpr extends Expr {

  @Nullable
  Expr getExpr();

}
// ---- UnaryPlusExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UnaryPlusExpr extends Expr {

  @Nullable
  Expr getExpr();

}
// ---- XorExpr.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XorExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- ArgListImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class ArgListImpl extends ASTWrapperPsiElement implements ArgList {

  public ArgListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitArgList(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- AssignExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class AssignExprImpl extends ExprImpl implements AssignExpr {

  public AssignExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitAssignExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- BetweenExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class BetweenExprImpl extends ExprImpl implements BetweenExpr {

  public BetweenExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitBetweenExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

  @Override
  @NotNull
  public Expr getTestExpr() {
    List<Expr> p1 = getExprList();
    return p1.get(0);
  }

}
// ---- CallExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class CallExprImpl extends ExprImpl implements CallExpr {

  public CallExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitCallExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ArgList getArgList() {
    return findNotNullChildByClass(ArgList.class);
  }

  @Override
  @NotNull
  public RefExpr getRefExpr() {
    return findNotNullChildByClass(RefExpr.class);
  }

}
// ---- ConditionalExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class ConditionalExprImpl extends ExprImpl implements ConditionalExpr {

  public ConditionalExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitConditionalExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- DivExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class DivExprImpl extends ExprImpl implements DivExpr {

  public DivExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitDivExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- ElvisExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class ElvisExprImpl extends ExprImpl implements ElvisExpr {

  public ElvisExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitElvisExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- ExpExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class ExpExprImpl extends ExprImpl implements ExpExpr {

  public ExpExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitExpExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- ExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class ExprImpl extends ASTWrapperPsiElement implements Expr {

  public ExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- FactorialExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class FactorialExprImpl extends ExprImpl implements FactorialExpr {

  public FactorialExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitFactorialExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
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
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class IdentifierImpl extends ASTWrapperPsiElement implements Identifier {

  public IdentifierImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitIdentifier(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

}
// ---- IsNotExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class IsNotExprImpl extends ExprImpl implements IsNotExpr {

  public IsNotExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitIsNotExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- LiteralExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class LiteralExprImpl extends ExprImpl implements LiteralExpr {

  public LiteralExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitLiteralExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getNumber() {
    return findNotNullChildByType(NUMBER);
  }

}
// ---- MinusExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class MinusExprImpl extends ExprImpl implements MinusExpr {

  public MinusExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitMinusExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- MulExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class MulExprImpl extends ExprImpl implements MulExpr {

  public MulExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitMulExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- ParenExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class ParenExprImpl extends ExprImpl implements ParenExpr {

  public ParenExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitParenExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public Expr getExpr() {
    return findChildByClass(Expr.class);
  }

}
// ---- PlusExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class PlusExprImpl extends ExprImpl implements PlusExpr {

  public PlusExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitPlusExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
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
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class RefExprImpl extends ExprImpl implements RefExpr {

  public RefExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitRefExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public Expr getExpr() {
    return findNotNullChildByClass(Expr.class);
  }

  @Override
  @NotNull
  public Identifier getIdentifier() {
    return findNotNullChildByClass(Identifier.class);
  }

}
// ---- SpecialExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class SpecialExprImpl extends ExprImpl implements SpecialExpr {

  public SpecialExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitSpecialExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- UnaryMinExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class UnaryMinExprImpl extends ExprImpl implements UnaryMinExpr {

  public UnaryMinExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitUnaryMinExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public Expr getExpr() {
    return findChildByClass(Expr.class);
  }

}
// ---- UnaryNotExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class UnaryNotExprImpl extends ExprImpl implements UnaryNotExpr {

  public UnaryNotExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitUnaryNotExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public Expr getExpr() {
    return findChildByClass(Expr.class);
  }

}
// ---- UnaryPlusExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class UnaryPlusExprImpl extends ExprImpl implements UnaryPlusExpr {

  public UnaryPlusExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitUnaryPlusExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public Expr getExpr() {
    return findChildByClass(Expr.class);
  }

}
// ---- XorExprImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import generated.psi.*;

public class XorExprImpl extends ExprImpl implements XorExpr {

  public XorExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitXorExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- Visitor.java -----------------
//header.txt
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class Visitor extends PsiElementVisitor {

  public void visitArgList(@NotNull ArgList o) {
    visitPsiElement(o);
  }

  public void visitAssignExpr(@NotNull AssignExpr o) {
    visitExpr(o);
  }

  public void visitBetweenExpr(@NotNull BetweenExpr o) {
    visitExpr(o);
  }

  public void visitCallExpr(@NotNull CallExpr o) {
    visitExpr(o);
  }

  public void visitConditionalExpr(@NotNull ConditionalExpr o) {
    visitExpr(o);
  }

  public void visitDivExpr(@NotNull DivExpr o) {
    visitExpr(o);
  }

  public void visitElvisExpr(@NotNull ElvisExpr o) {
    visitExpr(o);
  }

  public void visitExpExpr(@NotNull ExpExpr o) {
    visitExpr(o);
  }

  public void visitExpr(@NotNull Expr o) {
    visitPsiElement(o);
  }

  public void visitFactorialExpr(@NotNull FactorialExpr o) {
    visitExpr(o);
  }

  public void visitIdentifier(@NotNull Identifier o) {
    visitPsiElement(o);
  }

  public void visitIsNotExpr(@NotNull IsNotExpr o) {
    visitExpr(o);
  }

  public void visitLiteralExpr(@NotNull LiteralExpr o) {
    visitExpr(o);
  }

  public void visitMinusExpr(@NotNull MinusExpr o) {
    visitExpr(o);
  }

  public void visitMulExpr(@NotNull MulExpr o) {
    visitExpr(o);
  }

  public void visitParenExpr(@NotNull ParenExpr o) {
    visitExpr(o);
  }

  public void visitPlusExpr(@NotNull PlusExpr o) {
    visitExpr(o);
  }

  public void visitRefExpr(@NotNull RefExpr o) {
    visitExpr(o);
  }

  public void visitSpecialExpr(@NotNull SpecialExpr o) {
    visitExpr(o);
  }

  public void visitUnaryMinExpr(@NotNull UnaryMinExpr o) {
    visitExpr(o);
  }

  public void visitUnaryNotExpr(@NotNull UnaryNotExpr o) {
    visitExpr(o);
  }

  public void visitUnaryPlusExpr(@NotNull UnaryPlusExpr o) {
    visitExpr(o);
  }

  public void visitXorExpr(@NotNull XorExpr o) {
    visitExpr(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}