// ---- test/FooSyntaxTypes.kt -----------------
//header.txt
package test

import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import test.FooParserDefinition

object FooSyntaxTypes {
  val UNRESOLVED = FooParserDefinition.createSyntaxType("UNRESOLVED")
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

  IElementType UNRESOLVED = FooParserDefinition.createType("UNRESOLVED");


  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == UNRESOLVED) {
        return new UnresolvedImpl(node);
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
      new Pair<SyntaxElementType, IElementType>(FooSyntaxTypes.INSTANCE.getUNRESOLVED(), FooTypes.UNRESOLVED)
    );
  }
}
// ---- test/psi/Unresolved.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import test.stub.UnresolvedStub;

public interface Unresolved extends PsiElement, StubBasedPsiElement<UnresolvedStub> {

}
// ---- test/psi/impl/UnresolvedImpl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static test.FooTypes.*;
import test.psi.ext.MissingMixin;
import test.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import test.stub.UnresolvedStub;

public class UnresolvedImpl extends MissingMixin implements Unresolved {

  public UnresolvedImpl(@NotNull ASTNode node) {
    super(node);
  }

  public UnresolvedImpl(@NotNull UnresolvedStub stub, @NotNull IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitUnresolved(this);
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

  public void visitUnresolved(@NotNull Unresolved o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
