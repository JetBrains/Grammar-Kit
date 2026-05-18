// ---- test/FooSyntaxTypes.kt -----------------
//header.txt
package test

import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import test.FooParserDefinition

object FooSyntaxTypes {
  val MY_ELEMENT = FooParserDefinition.createSyntaxType("MY_ELEMENT")
  val OTHER_ELEMENT = FooParserDefinition.createSyntaxType("OTHER_ELEMENT")
}
// ---- test/FooTypes.java -----------------
//header.txt
package test;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import test.stub.FooParserDefinition;
import test.psi.impl.*;

public interface FooTypes {

  IElementType MY_ELEMENT = FooParserDefinition.createType("MY_ELEMENT");
  IElementType OTHER_ELEMENT = FooParserDefinition.createType("OTHER_ELEMENT");


  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == MY_ELEMENT) {
        return new MyElementImpl(node);
      }
      else if (type == OTHER_ELEMENT) {
        return new OtherElementImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- generated/GeneratedSyntaxElementTypeConverterFactory.java -----------------
//header.txt
package generated;

import test.FooTypes;
import test.FooSyntaxTypes;
import com.intellij.psi.tree.IElementType;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.psi.ElementTypeConverterFactory;
import com.intellij.platform.syntax.psi.ElementTypeConverter;
import com.intellij.platform.syntax.psi.ElementTypeConverterKt;
import org.jetbrains.annotations.NotNull;
import kotlin.Pair;

public class GeneratedSyntaxElementTypeConverterFactory implements ElementTypeConverterFactory {

  @Override
  public @NotNull ElementTypeConverter getElementTypeConverter() {
    return ElementTypeConverterKt.elementTypeConverterOf(
      new Pair<SyntaxElementType, IElementType>(FooSyntaxTypes.INSTANCE.getMY_ELEMENT(), FooTypes.MY_ELEMENT),
      new Pair<SyntaxElementType, IElementType>(FooSyntaxTypes.INSTANCE.getOTHER_ELEMENT(), FooTypes.OTHER_ELEMENT)
    );
  }
}
// ---- test/psi/MyElement.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import test.stub.MyElementStub;

public interface MyElement extends PsiElement, StubBasedPsiElement<MyElementStub> {

}
// ---- test/psi/OtherElement.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import test.stub.OtherElementStub;

public interface OtherElement extends PsiElement, StubBasedPsiElement<OtherElementStub> {

}
// ---- test/psi/impl/MyElementImpl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import test.psi.MyPsiTreeUtil;
import static test.FooTypes.*;
import test.psi.ext.MyElementImplMixin;
import test.psi.*;
import test.stub.MyElementStub;
import com.intellij.psi.tree.IElementType;

public class MyElementImpl extends MyElementImplMixin implements MyElement {

  public MyElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public MyElementImpl(@NotNull MyElementStub stub, @NotNull IElementType type) {
    super(stub, type);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitMyElement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- test/psi/impl/OtherElementImpl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import test.psi.MyPsiTreeUtil;
import static test.FooTypes.*;
import test.psi.ext.OtherElementImplMixin;
import test.psi.*;
import test.stub.OtherElementStub;
import com.intellij.psi.tree.IElementType;

public class OtherElementImpl extends OtherElementImplMixin implements OtherElement {

  public OtherElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public OtherElementImpl(@NotNull OtherElementStub stub, @NotNull IElementType type) {
    super(stub, type);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitOtherElement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- test/psi/Visitor.java -----------------
//header.txt
package test.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class Visitor extends PsiElementVisitor {

  public void visitMyElement(@NotNull MyElement o) {
    visitPsiElement(o);
  }

  public void visitOtherElement(@NotNull OtherElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
