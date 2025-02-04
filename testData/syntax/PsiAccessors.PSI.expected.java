// ---- GeneratedTypes.java -----------------
//header.txt
package generated;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import generated.psi.impl.*;

public interface GeneratedTypes {

  IElementType BINARY = new IElementType("BINARY", null);
  IElementType EXPRESSION = new IElementType("EXPRESSION", null);
  IElementType OPERATOR = new IElementType("OPERATOR", null);
  IElementType VALUE = new IElementType("VALUE", null);

  IElementType ID = new IElementType("id", null);
  IElementType LOWCASEKWD1 = new IElementType("lowcasekwd1", null);
  IElementType MY_SOMETHING = new IElementType("something", null);
  IElementType UPCASEKWD1 = new IElementType("UPCASEKWD1", null);

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
// ---- XEmpty.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XEmpty extends XComposite {

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
// ---- XRenameList.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import generated.psi.child.XSomeChild;

public interface XRenameList extends XComposite {

  @NotNull
  List<XSomeChild> getSomeChildren();

  @Nullable
  XSomeChild getFirst();

  @Nullable
  PsiElement getFirstSmth();

  @Nullable
  XSomeChild getLast();

}
// ---- XReportSomeBad.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import generated.psi.child.XSomeChild;

public interface XReportSomeBad extends XComposite {

  @NotNull
  XReportSomeBad getReportSomeBad();

}
// ---- XSomeChild.java -----------------
//header.txt
package generated.psi.child;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import generated.psi.grand.XSomeGrandChild;

public interface XSomeChild extends XComposite {

  @NotNull
  XSomeGrandChild getSomeGrandChild();

  @NotNull
  PsiElement getSomething2();

  @NotNull
  PsiElement getSmth1();

  @NotNull
  PsiElement getSmth2();

}
// ---- XSomeGrandChild.java -----------------
//header.txt
package generated.psi.grand;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XSomeGrandChild extends XComposite {

  @NotNull
  PsiElement getMySomething();

  @Nullable
  PsiElement getSomething2();

}
// ---- XSomeRoot.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import generated.psi.child.XSomeChild;
import generated.psi.grand.XSomeGrandChild;

public interface XSomeRoot extends XComposite {

  @NotNull
  XSomeChild getSomeChild();

  @NotNull
  PsiElement getChildSomething();

  @NotNull
  PsiElement getChildSomething2();

  @NotNull
  XSomeGrandChild getGrandChild();

  @NotNull
  PsiElement getGrandChildSomethin();

  @Nullable
  PsiElement getGrandChildSomethin2();

}
// ---- XTokenDefaults.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface XTokenDefaults extends XComposite {

  @NotNull
  PsiElement getNodef();

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
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XBinaryImpl extends ASTWrapperPsiElement implements XBinary {

  public XBinaryImpl(@NotNull ASTNode node) {
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
// ---- XEmptyImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XEmptyImpl extends ASTWrapperPsiElement implements XEmpty {

  public XEmptyImpl(@NotNull ASTNode node) {
    super(node);
  }

}
// ---- XExpressionImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XExpressionImpl extends ASTWrapperPsiElement implements XExpression {

  public XExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<XValue> getValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, XValue.class);
  }

}
// ---- XOperatorImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XOperatorImpl extends ASTWrapperPsiElement implements XOperator {

  public XOperatorImpl(@NotNull ASTNode node) {
    super(node);
  }

}
// ---- XRenameListImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;
import generated.psi.child.XSomeChild;

public class XRenameListImpl extends ASTWrapperPsiElement implements XRenameList {

  public XRenameListImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<XSomeChild> getSomeChildren() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, XSomeChild.class);
  }

  @Override
  @Nullable
  public XSomeChild getFirst() {
    List<XSomeChild> p1 = getSomeChildren();
    return p1.size() < 1 ? null : p1.get(0);
  }

  @Override
  @Nullable
  public PsiElement getFirstSmth() {
    List<XSomeChild> p1 = getSomeChildren();
    XSomeChild p2 = p1.size() < 1 ? null : p1.get(0);
    if (p2 == null) return null;
    return p2.getSmth1();
  }

  @Override
  @Nullable
  public XSomeChild getLast() {
    List<XSomeChild> p1 = getSomeChildren();
    return p1.isEmpty()? null : p1.get(p1.size() - 1);
  }

}
// ---- XReportSomeBadImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;
import generated.psi.child.XSomeChild;

public class XReportSomeBadImpl extends ASTWrapperPsiElement implements XReportSomeBad {

  public XReportSomeBadImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public XReportSomeBad getReportSomeBad() {
    return findNotNullChildByClass(XReportSomeBad.class);
  }

}
// ---- XSomeChildImpl.java -----------------
//header.txt
package generated.psi.impl.child;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.child.*;
import generated.psi.grand.XSomeGrandChild;

public class XSomeChildImpl extends ASTWrapperPsiElement implements XSomeChild {

  public XSomeChildImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public XSomeGrandChild getSomeGrandChild() {
    return findNotNullChildByClass(XSomeGrandChild.class);
  }

  @Override
  @NotNull
  public PsiElement getSomething2() {
    return findNotNullChildByType(SOMETHING2);
  }

  @Override
  @NotNull
  public PsiElement getSmth1() {
    return findNotNullChildByType(MY_SOMETHING);
  }

  @Override
  @NotNull
  public PsiElement getSmth2() {
    return getSmth1();
  }

}
// ---- XSomeGrandChildImpl.java -----------------
//header.txt
package generated.psi.impl.grand;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.grand.*;

public class XSomeGrandChildImpl extends ASTWrapperPsiElement implements XSomeGrandChild {

  public XSomeGrandChildImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getMySomething() {
    return findNotNullChildByType(MY_SOMETHING);
  }

  @Override
  @Nullable
  public PsiElement getSomething2() {
    return findChildByType(SOMETHING2);
  }

}
// ---- XSomeRootImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;
import generated.psi.child.XSomeChild;
import generated.psi.grand.XSomeGrandChild;

public class XSomeRootImpl extends ASTWrapperPsiElement implements XSomeRoot {

  public XSomeRootImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public XSomeChild getSomeChild() {
    return findNotNullChildByClass(XSomeChild.class);
  }

  @Override
  @NotNull
  public PsiElement getChildSomething() {
    XSomeChild p1 = getSomeChild();
    return p1.getSmth1();
  }

  @Override
  @NotNull
  public PsiElement getChildSomething2() {
    XSomeChild p1 = getSomeChild();
    return p1.getSomething2();
  }

  @Override
  @NotNull
  public XSomeGrandChild getGrandChild() {
    XSomeChild p1 = getSomeChild();
    return p1.getSomeGrandChild();
  }

  @Override
  @NotNull
  public PsiElement getGrandChildSomethin() {
    XSomeChild p1 = getSomeChild();
    XSomeGrandChild p2 = p1.getSomeGrandChild();
    return p2.getMySomething();
  }

  @Override
  @Nullable
  public PsiElement getGrandChildSomethin2() {
    XSomeChild p1 = getSomeChild();
    XSomeGrandChild p2 = p1.getSomeGrandChild();
    return p2.getSomething2();
  }

}
// ---- XTokenDefaultsImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XTokenDefaultsImpl extends ASTWrapperPsiElement implements XTokenDefaults {

  public XTokenDefaultsImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getNodef() {
    return findNotNullChildByType(NODEF);
  }

}
// ---- XValueImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class XValueImpl extends ASTWrapperPsiElement implements XValue {

  public XValueImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

}