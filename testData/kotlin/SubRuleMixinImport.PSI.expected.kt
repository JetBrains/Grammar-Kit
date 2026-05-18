// ---- test/FooSyntaxTypes.kt -----------------
//header.txt
package test

import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import test.FooParserDefinition

object FooSyntaxTypes {
  val PARENT = FooParserDefinition.createSyntaxType("PARENT")
  val SUB_A = FooParserDefinition.createSyntaxType("SUB_A")
  val SUB_B = FooParserDefinition.createSyntaxType("SUB_B")
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

  IElementType PARENT = FooParserDefinition.createType("PARENT");
  IElementType SUB_A = FooParserDefinition.createType("SUB_A");
  IElementType SUB_B = FooParserDefinition.createType("SUB_B");


  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == PARENT) {
        return new ParentImpl(node);
      }
      else if (type == SUB_A) {
        return new SubAImpl(node);
      }
      else if (type == SUB_B) {
        return new SubBImpl(node);
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
      new Pair<SyntaxElementType, IElementType>(FooSyntaxTypes.INSTANCE.getPARENT(), FooTypes.PARENT),
      new Pair<SyntaxElementType, IElementType>(FooSyntaxTypes.INSTANCE.getSUB_A(), FooTypes.SUB_A),
      new Pair<SyntaxElementType, IElementType>(FooSyntaxTypes.INSTANCE.getSUB_B(), FooTypes.SUB_B)
    );
  }
}
// ---- test/psi/Parent.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Parent extends PsiElement {

}
// ---- test/psi/SubA.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SubA extends Parent {

}
// ---- test/psi/SubB.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SubB extends Parent {

}
// ---- test/psi/impl/ParentImpl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static test.FooTypes.*;
import test.psi.ext.ParentImplMixin;
import test.psi.*;

public class ParentImpl extends ParentImplMixin implements Parent {

  public ParentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitParent(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- test/psi/impl/SubAImpl.java -----------------
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

public class SubAImpl extends ParentImpl implements SubA {

  public SubAImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitSubA(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- test/psi/impl/SubBImpl.java -----------------
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

public class SubBImpl extends ParentImpl implements SubB {

  public SubBImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitSubB(this);
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

  public void visitParent(@NotNull Parent o) {
    visitPsiElement(o);
  }

  public void visitSubA(@NotNull SubA o) {
    visitParent(o);
  }

  public void visitSubB(@NotNull SubB o) {
    visitParent(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
