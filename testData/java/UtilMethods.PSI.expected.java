// ---- test/FooTypes.java -----------------
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
// ---- test/psi/Element1.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.intellij.grammar.test.UtilMethods.E;
import org.intellij.grammar.test.UtilMethods.Wrap.Deep;
import org.intellij.grammar.test.UtilMethods.X;
import org.intellij.grammar.test.UtilMethods.Y;
import org.intellij.grammar.test.UtilMethods.Z;

public interface Element1 extends PsiElement {

  <T extends X & Y> void foo0(T param) throws Z, RuntimeException;

  <T> void foo1(T param);

  <T, K> void foo2(T param, K k);

  <T extends X, K extends Y> void foo3(T param, K k);

  <@Nls @NonNls T extends @Nls @NonNls X & @NonNls @Nls Y, @Nls @NonNls K> @Nls String foo4(@Nls @NonNls T param, @Nls @NonNls K k);

  <@Nls @NonNls T, @Nls @NonNls K extends @Nls @NonNls X & @NonNls @Nls Y> @Nls String foo5(@Nls @NonNls T param, @Nls @NonNls K k);

  @NotNull
  @Nls
  List<? super @NotNull @Nls String> @Nullable @Unmodifiable [][] foo6(@NotNull @Nls List<? super @NotNull @Nls String> @Nullable @Unmodifiable [][] args);

  @NotNull
  E foo7();

  @NotNull
  Deep foo8();

}
// ---- test/psi/impl/Element1Impl.java -----------------
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
import org.intellij.grammar.test.UtilMethods.E;
import org.intellij.grammar.test.UtilMethods.Wrap.Deep;
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
  public <T extends X & Y> void foo0(T param) throws Z, RuntimeException {
    UtilMethods.foo0(this, param);
  }

  @Override
  public <T> void foo1(T param) {
    UtilMethods.foo1(this, param);
  }

  @Override
  public <T, K> void foo2(T param, K k) {
    UtilMethods.foo2(this, param, k);
  }

  @Override
  public <T extends X, K extends Y> void foo3(T param, K k) {
    UtilMethods.foo3(this, param, k);
  }

  @Override
  public <@Nls @NonNls T extends @Nls @NonNls X & @NonNls @Nls Y, @Nls @NonNls K> @Nls String foo4(@Nls @NonNls T param, @Nls @NonNls K k) {
    return UtilMethods.foo4(this, param, k);
  }

  @Override
  public <@Nls @NonNls T, @Nls @NonNls K extends @Nls @NonNls X & @NonNls @Nls Y> @Nls String foo5(@Nls @NonNls T param, @Nls @NonNls K k) {
    return UtilMethods.foo5(this, param, k);
  }

  @Override
  @NotNull
  @Nls
  public List<? super @NotNull @Nls String> @Nullable @Unmodifiable [][] foo6(@NotNull @Nls List<? super @NotNull @Nls String> @Nullable @Unmodifiable [][] args) {
    return UtilMethods.foo6(this, args);
  }

  @Override
  @NotNull
  public E foo7() {
    return UtilMethods.foo7(this);
  }

  @Override
  @NotNull
  public Deep foo8() {
    return UtilMethods.foo8(this);
  }

}
// ---- test/psi/Visitor.java -----------------
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