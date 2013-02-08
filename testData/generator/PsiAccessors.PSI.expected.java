// ---- ParserTypes.java -----------------
//header.txt
package generated;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import generated.psi.impl.*;

public interface ParserTypes {

  IElementType BINARY = new IElementType("BINARY", null);
  IElementType EXPRESSION = new IElementType("EXPRESSION", null);
  IElementType OPERATOR = new IElementType("OPERATOR", null);
  IElementType RE = new IElementType("RE", null);
  IElementType VALUE = new IElementType("VALUE", null);

  IElementType ID = new IElementType("id", null);

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == BINARY) {
        return new XBinaryImpl(node);
      }
      else if (type == EXPRESSION) {
        return new XExpressionImpl(node);
      }
      else if (type == OPERATOR) {
        return new XOperatorImpl(node);
      }
      else if (type == RE) {
        return new XReImpl(node);
      }
      else if (type == VALUE) {
        return new XValueImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- XBinary.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XBinary extends XComposite {

  @NotNull
  List<XExpression> getExpressionList();

  @NotNull
  XOperator getOperator();

  @Nullable
  List<XExpression> getAlias();

  @NotNull
  XExpression getLeft();

  @Nullable
  XExpression getRight();

  @NotNull
  XOperator getOp();

  @NotNull
  XValue getLeftLeft();

  @Nullable
  XValue getRightRight();

  @Nullable
  XExpression getLast();

  @NotNull
  XExpression getFirst();

  @Nullable
  XValue getRightLeft();

  @Nullable
  XValue getLeftRight();

  @Nullable
  XValue getBadIndex();

}
// ---- XExpression.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XExpression extends XComposite {

  @NotNull
  List<XValue> getValueList();

}
// ---- XOperator.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XOperator extends XComposite {

}
// ---- XRe.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XRe extends XComposite {

  @NotNull
  PsiElement getId();

}
// ---- XValue.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XValue extends XComposite {

  @NotNull
  PsiElement getId();

}
// ---- XBinaryImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XBinaryImpl extends ASTWrapperPsiElement implements XBinary {

  public XBinaryImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<XExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, XExpression.class);
  }

  @Override
  @NotNull
  public XOperator getOperator() {
    return findNotNullChildByClass(XOperator.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitBinary(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public List<XExpression> getAlias() {
    return getExpressionList();
  }

  @Override
  @NotNull
  public XExpression getLeft() {
    List<XExpression> p1 = getExpressionList();
    return p1.get(0);
  }

  @Override
  @Nullable
  public XExpression getRight() {
    List<XExpression> p1 = getExpressionList();
    return p1.size() < 2 ? null : p1.get(1);
  }

  @Override
  @NotNull
  public XOperator getOp() {
    return getOperator();
  }

  @Override
  @NotNull
  public XValue getLeftLeft() {
    List<XExpression> p1 = getExpressionList();
    XExpression p2 = p1.get(0);
    List<XValue> p3 = p2.getValueList();
    return p3.get(0);
  }

  @Override
  @Nullable
  public XValue getRightRight() {
    List<XExpression> p1 = getExpressionList();
    XExpression p2 = p1.size() < 2 ? null : p1.get(1);
    if (p2 == null) return null;
    List<XValue> p3 = p2.getValueList();
    return p3.size() < 2 ? null : p3.get(1);
  }

  @Override
  @Nullable
  public XExpression getLast() {
    List<XExpression> p1 = getExpressionList();
    return p1.isEmpty()? null : p1.get(p1.size() - 1);
  }

  @Override
  @NotNull
  public XExpression getFirst() {
    List<XExpression> p1 = getExpressionList();
    return p1.get(0);
  }

  @Override
  @Nullable
  public XValue getRightLeft() {
    List<XExpression> p1 = getExpressionList();
    XExpression p2 = p1.size() < 2 ? null : p1.get(1);
    if (p2 == null) return null;
    List<XValue> p3 = p2.getValueList();
    return p3.get(0);
  }

  @Override
  @Nullable
  public XValue getLeftRight() {
    List<XExpression> p1 = getExpressionList();
    XExpression p2 = p1.get(0);
    List<XValue> p3 = p2.getValueList();
    return p3.size() < 2 ? null : p3.get(1);
  }

  @Override
  @Nullable
  public XValue getBadIndex() {
    List<XExpression> p1 = getExpressionList();
    XExpression p2 = p1.size() - 1 < bad_index ? null : p1.get(bad_index);
    if (p2 == null) return null;
    List<XValue> p3 = p2.getValueList();
    return p3.size() - 1 < wrong_turn ? null : p3.get(wrong_turn);
  }

}
// ---- XExpressionImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XExpressionImpl extends ASTWrapperPsiElement implements XExpression {

  public XExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<XValue> getValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, XValue.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitExpression(this);
    else super.accept(visitor);
  }

}
// ---- XOperatorImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XOperatorImpl extends ASTWrapperPsiElement implements XOperator {

  public XOperatorImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitOperator(this);
    else super.accept(visitor);
  }

}
// ---- XReImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XReImpl extends ASTWrapperPsiElement implements XRe {

  public XReImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitRe(this);
    else super.accept(visitor);
  }

}
// ---- XValueImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XValueImpl extends ASTWrapperPsiElement implements XValue {

  public XValueImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XVisitor) ((XVisitor)visitor).visitValue(this);
    else super.accept(visitor);
  }

}
// ---- XVisitor.java -----------------
//header.txt
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class XVisitor extends PsiElementVisitor {

  public void visitBinary(@NotNull XBinary o) {
    visitComposite(o);
  }

  public void visitExpression(@NotNull XExpression o) {
    visitComposite(o);
  }

  public void visitOperator(@NotNull XOperator o) {
    visitComposite(o);
  }

  public void visitRe(@NotNull XRe o) {
    visitComposite(o);
  }

  public void visitValue(@NotNull XValue o) {
    visitComposite(o);
  }

  public void visitComposite(@NotNull XComposite o) {
    visitElement(o);
  }

}