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
  IElementType INTERFACE_TYPE = FooParserDefinition.createType("INTERFACE_TYPE");
  IElementType STRUCT_TYPE = FooParserDefinition.createType("STRUCT_TYPE");
  IElementType TYPE = FooParserDefinition.createType("TYPE");


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
      else if (type == INTERFACE_TYPE) {
        return new InterfaceTypeImpl(node);
      }
      else if (type == STRUCT_TYPE) {
        return new StructTypeImpl(node);
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

  @NotNull
  Element5 getElement5();

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

  @NotNull
  List<Element4> getElement4List();

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
  Element4 getElement4();

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

  @Nullable
  Element2 getElement2();

}
// ---- Element5.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Element5 extends PsiElement {

}
// ---- InterfaceType.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface InterfaceType extends Type {

}
// ---- Missing.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import test.stub.MissingStub;

public interface Missing extends PsiElement, StubBasedPsiElement<MissingStub> {

}
// ---- Simple.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.intellij.grammar.test.StubTest.SimpleStub;

public interface Simple extends PsiElement, StubBasedPsiElement<SimpleStub> {

}
// ---- StructType.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface StructType extends Type {

}
// ---- Type.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import test.stub.TypeStub;

public interface Type extends PsiElement, StubBasedPsiElement<TypeStub> {

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
import org.intellij.grammar.test.StubTest.GenericBase;
import test.stub.Element1Stub;
import test.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class Element1Impl extends GenericBase<Element1Stub> implements Element1 {

  public Element1Impl(@NotNull Element1Stub stub, @NotNull IStubElementType type) {
    super(stub, type);
  }

  public Element1Impl(@NotNull ASTNode node) {
    super(node);
  }

  public Element1Impl(Element1Stub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
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
  public Element5 getElement5() {
    return notNullChild(MyPsiTreeUtil.getChildOfType(this, Element5.class));
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
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import test.stub.Element2Stub;
import test.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class Element2Impl extends StubBasedPsiElementBase<Element2Stub> implements Element2 {

  public Element2Impl(@NotNull Element2Stub stub, @NotNull IStubElementType type) {
    super(stub, type);
  }

  public Element2Impl(@NotNull ASTNode node) {
    super(node);
  }

  public Element2Impl(Element2Stub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
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
  public List<Element4> getElement4List() {
    return MyPsiTreeUtil.getStubChildrenOfTypeAsList(this, Element4.class);
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
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import test.stub.Element3Stub;
import test.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class Element3Impl extends StubBasedPsiElementBase<Element3Stub> implements Element3 {

  public Element3Impl(@NotNull Element3Stub stub, @NotNull IStubElementType type) {
    super(stub, type);
  }

  public Element3Impl(@NotNull ASTNode node) {
    super(node);
  }

  public Element3Impl(Element3Stub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
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
  public Element4 getElement4() {
    return notNullChild(MyPsiTreeUtil.getStubChildOfType(this, Element4.class));
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
import test.psi.MyPsiTreeUtil;
import static test.FooTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import test.stub.Element4Stub;
import test.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class Element4Impl extends StubBasedPsiElementBase<Element4Stub> implements Element4 {

  public Element4Impl(@NotNull Element4Stub stub, @NotNull IStubElementType type) {
    super(stub, type);
  }

  public Element4Impl(@NotNull ASTNode node) {
    super(node);
  }

  public Element4Impl(Element4Stub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitElement4(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public Element2 getElement2() {
    return MyPsiTreeUtil.getStubChildOfType(this, Element2.class);
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
import test.psi.MyPsiTreeUtil;
import static test.FooTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import test.psi.*;

public class Element5Impl extends ASTWrapperPsiElement implements Element5 {

  public Element5Impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitElement5(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- InterfaceTypeImpl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import test.psi.MyPsiTreeUtil;
import static test.FooTypes.*;
import test.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import test.stub.TypeStub;

public class InterfaceTypeImpl extends TypeImpl implements InterfaceType {

  public InterfaceTypeImpl(@NotNull TypeStub stub, @NotNull IStubElementType type) {
    super(stub, type);
  }

  public InterfaceTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public InterfaceTypeImpl(TypeStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitInterfaceType(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- MissingImpl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import test.psi.MyPsiTreeUtil;
import static test.FooTypes.*;
import test.stub.MissingBase;
import test.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import test.stub.MissingStub;

public class MissingImpl extends MissingBase implements Missing {

  public MissingImpl(ASTNode node) {
    super(node);
  }

  public MissingImpl(MissingStub stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitMissing(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- SimpleImpl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import test.psi.MyPsiTreeUtil;
import static test.FooTypes.*;
import org.intellij.grammar.test.StubTest.SimpleBase;
import test.psi.*;
import org.intellij.grammar.test.StubTest.SimpleStub;
import com.intellij.psi.stubs.IStubElementType;

public class SimpleImpl extends SimpleBase implements Simple {

  public SimpleImpl(@NotNull SimpleStub stub, @NotNull IStubElementType type) {
    super(stub, type);
  }

  public SimpleImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitSimple(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- StructTypeImpl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import test.psi.MyPsiTreeUtil;
import static test.FooTypes.*;
import test.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import test.stub.TypeStub;

public class StructTypeImpl extends TypeImpl implements StructType {

  public StructTypeImpl(@NotNull TypeStub stub, @NotNull IStubElementType type) {
    super(stub, type);
  }

  public StructTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public StructTypeImpl(TypeStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitStructType(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- TypeImpl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import test.psi.MyPsiTreeUtil;
import static test.FooTypes.*;
import org.intellij.grammar.test.StubTest.GenericBase;
import test.stub.TypeStub;
import test.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public abstract class TypeImpl extends GenericBase<TypeStub> implements Type {

  public TypeImpl(@NotNull TypeStub stub, @NotNull IStubElementType type) {
    super(stub, type);
  }

  public TypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public TypeImpl(TypeStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitType(this);
  }

  @Override
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

  public void visitInterfaceType(@NotNull InterfaceType o) {
    visitType(o);
  }

  public void visitMissing(@NotNull Missing o) {
    visitPsiElement(o);
  }

  public void visitSimple(@NotNull Simple o) {
    visitPsiElement(o);
  }

  public void visitStructType(@NotNull StructType o) {
    visitType(o);
  }

  public void visitType(@NotNull Type o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}