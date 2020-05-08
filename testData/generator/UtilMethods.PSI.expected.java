// ---- FooTypes.java -----------------
//header.txt
package test;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import test.stub.FooParserDefinition;
import test.psi.impl.*;

public interface FooTypes {

  IElementType ELEMENT_1 = FooParserDefinition.createType("ELEMENT_1");


  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ELEMENT_1) {
        return new Element1Impl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- Element1.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.intellij.grammar.test.UtilMethods.X;
import org.intellij.grammar.test.UtilMethods.Y;
import org.intellij.grammar.test.UtilMethods.Z;

public interface Element1 extends PsiElement {

  <T extends X & Y> void foo0(T p1) throws Z, RuntimeException;

  <T> void foo1(T p1);

  <T, K> void foo2(T p1, K p2);

  <T extends X, K extends Y> void foo3(T p1, K p2);

  <T extends X, K> void foo4(T p1, K p2);

  <T, K extends X> void foo5(T p1, K p2);

}
// ---- Element1Impl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import test.psi.MyPsiTreeUtil;
import static test.FooTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import test.psi.*;
import org.intellij.grammar.test.UtilMethods;
import org.intellij.grammar.test.UtilMethods.X;
import org.intellij.grammar.test.UtilMethods.Y;
import org.intellij.grammar.test.UtilMethods.Z;

public class Element1Impl extends ASTWrapperPsiElement implements Element1 {

  public Element1Impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitElement1(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public <T extends X & Y> void foo0(T p1) throws Z, RuntimeException {
    UtilMethods.foo0(this, p1);
  }

  @Override
  public <T> void foo1(T p1) {
    UtilMethods.foo1(this, p1);
  }

  @Override
  public <T, K> void foo2(T p1, K p2) {
    UtilMethods.foo2(this, p1, p2);
  }

  @Override
  public <T extends X, K extends Y> void foo3(T p1, K p2) {
    UtilMethods.foo3(this, p1, p2);
  }

  @Override
  public <T extends X, K> void foo4(T p1, K p2) {
    UtilMethods.foo4(this, p1, p2);
  }

  @Override
  public <T, K extends X> void foo5(T p1, K p2) {
    UtilMethods.foo5(this, p1, p2);
  }

}
// ---- Visitor.java -----------------
//header.txt
package test.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class Visitor extends PsiElementVisitor {

  public void visitElement1(@NotNull Element1 o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}