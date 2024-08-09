// ---- FooTypes.java -----------------
//header.txt
package test;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import test.psi.impl.*;
import com.intellij.psi.impl.source.tree.CompositePsiElement;

public interface FooTypes {

  IElementType ELEMENT_1 = FooParserDefinition.createType("ELEMENT_1");
  IElementType ELEMENT_2 = FooParserDefinition.createType("ELEMENT_2");
  IElementType ELEMENT_3 = FooParserDefinition.createType("ELEMENT_3");

  IElementType AA = FooParserDefinition.createTokenType("aa");
  IElementType BB = FooParserDefinition.createTokenType("bb");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ELEMENT_2) {
        return new Element2Impl(node);
      }
      else if (type == ELEMENT_3) {
        return new Element3Impl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }

    public static CompositePsiElement createElement(IElementType type) {
       if (type == ELEMENT_1) {
        return new Element1Impl(type);
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

public interface Element1 extends PsiElement {

  @NotNull
  PsiElement getAa();

}
// ---- Element2.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Element2 extends PsiElement {

  @NotNull
  PsiElement getBb();

}
// ---- Element3.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import test.stub.Element3Stub;

public interface Element3 extends PsiElement, StubBasedPsiElement<Element3Stub> {

  @NotNull
  PsiElement getBb();

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
import test.CompositePsiElementImpl;
import test.psi.*;
import com.intellij.psi.tree.IElementType;

public class Element1Impl extends CompositePsiElementImpl implements Element1 {

  public Element1Impl(IElementType type) {
    super(type);
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
  @NotNull
  public PsiElement getAa() {
    return findPsiChildByType(AA);
  }

}
// ---- Element2Impl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import test.psi.MyPsiTreeUtil;
import static test.FooTypes.*;
import test.AstDelegatedPsiElementImpl;
import test.psi.*;

public class Element2Impl extends AstDelegatedPsiElementImpl implements Element2 {

  public Element2Impl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitElement2(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getBb() {
    return notNullChild(findChildByType(BB));
  }

}
// ---- Element3Impl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import test.psi.MyPsiTreeUtil;
import static test.FooTypes.*;
import test.StubBasedPsiElementImpl;
import test.stub.Element3Stub;
import test.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class Element3Impl extends StubBasedPsiElementImpl<Element3Stub> implements Element3 {

  public Element3Impl(ASTNode node) {
    super(node);
  }

  public Element3Impl(Element3Stub stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitElement3(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getBb() {
    return notNullChild(findChildByType(BB));
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

  public void visitElement2(@NotNull Element2 o) {
    visitPsiElement(o);
  }

  public void visitElement3(@NotNull Element3 o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}