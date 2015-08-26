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
  IElementType ELEMENT_2 = FooParserDefinition.createType("ELEMENT_2");
  IElementType ELEMENT_3 = FooParserDefinition.createType("ELEMENT_3");
  IElementType ELEMENT_4 = FooParserDefinition.createType("ELEMENT_4");
  IElementType ELEMENT_5 = FooParserDefinition.createType("ELEMENT_5");


  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ELEMENT_1) {
        return new Element1Impl(node);
      }
      else if (type == ELEMENT_2) {
        return new Element2Impl(node);
      }
      else if (type == ELEMENT_3) {
        return new Element3Impl(node);
      }
      else if (type == ELEMENT_4) {
        return new Element4Impl(node);
      }
      else if (type == ELEMENT_5) {
        return new Element5Impl(node);
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
import com.intellij.psi.StubBasedPsiElement;
import test.stub.Element1Stub;

public interface Element1 extends PsiElement, StubBasedPsiElement<Element1Stub> {

}
// ---- Element2.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import test.stub.Element2Stub;

public interface Element2 extends PsiElement, StubBasedPsiElement<Element2Stub> {

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

}
// ---- Element4.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import test.stub.Element4Stub;

public interface Element4 extends PsiElement, StubBasedPsiElement<Element4Stub> {

}
// ---- Element5.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Element5 extends PsiElement {

}
// ---- Element1Impl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static test.FooTypes.*;
import test.stub.Element1Stub;
import test.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class Element1Impl extends MyStubbedElementBase<Element1Stub> implements Element1 {

  public Element1Impl(ASTNode node) {
    super(node);
  }

  public Element1Impl(Element1Stub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitElement1(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
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
import com.intellij.psi.util.PsiTreeUtil;
import static test.FooTypes.*;
import test.stub.Element2Stub;
import test.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class Element2Impl extends MyStubbedElementBase<Element2Stub> implements Element2 {

  public Element2Impl(ASTNode node) {
    super(node);
  }

  public Element2Impl(Element2Stub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitElement2(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
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
import com.intellij.psi.util.PsiTreeUtil;
import static test.FooTypes.*;
import test.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class Element3Impl extends MySubstituted implements Element3 {

  public Element3Impl(ASTNode node) {
    super(node);
  }

  public Element3Impl(test.stub.Element3Stub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitElement3(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- Element4Impl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static test.FooTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import test.stub.Element4Stub;
import test.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class Element4Impl extends StubBasedPsiElementBase<Element4Stub> implements Element4 {

  public Element4Impl(ASTNode node) {
    super(node);
  }

  public Element4Impl(Element4Stub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitElement4(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- Element5Impl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static test.FooTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import test.psi.*;

public class Element5Impl extends ASTWrapperPsiElement implements Element5 {

  public Element5Impl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitElement5(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
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

  public void visitElement4(@NotNull Element4 o) {
    visitPsiElement(o);
  }

  public void visitElement5(@NotNull Element5 o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}