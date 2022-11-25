// ---- GeneratedTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import generated.psi.impl.*;

public interface GeneratedTypes {

  IElementType BARBAR = new IElementType("BARBAR", null);
  IElementType BARFOO = new IElementType("BARFOO", null);
  IElementType BAZ = new IElementType("BAZ", null);


  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == BARBAR) {
        return new BarbarImpl(node);
      }
      else if (type == BARFOO) {
        return new BarfooImpl(node);
      }
      else if (type == BAZ) {
        return new BazImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- Bar.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public sealed interface Bar extends Foo, PsiElement permits Barbar, Barfoo {

  <T> void foo1(T p1);

}
// ---- Barbar.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public non-sealed interface Barbar extends Bar, PsiElement {

}
// ---- Barfoo.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public non-sealed interface Barfoo extends Bar, PsiElement {

}
// ---- Baz.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public non-sealed interface Baz extends Foo, PsiElement {

}
// ---- Foo.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.intellij.grammar.test.UtilMethods.X;
import org.intellij.grammar.test.UtilMethods.Y;
import org.intellij.grammar.test.UtilMethods.Z;

public sealed interface Foo extends PsiElement permits Bar, Baz {

  <T extends X & Y> void foo0(T p1) throws Z, RuntimeException;

}
// ---- BarbarImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;
import org.intellij.grammar.test.UtilMethods;
import org.intellij.grammar.test.UtilMethods.X;
import org.intellij.grammar.test.UtilMethods.Y;
import org.intellij.grammar.test.UtilMethods.Z;

public class BarbarImpl extends ASTWrapperPsiElement implements Barbar {

  public BarbarImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitBarbar(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public <T> void foo1(T p1) {
    UtilMethods.foo1(this, p1);
  }

  @Override
  public <T extends X & Y> void foo0(T p1) throws Z, RuntimeException {
    UtilMethods.foo0(this, p1);
  }

}
// ---- BarfooImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;
import org.intellij.grammar.test.UtilMethods;
import org.intellij.grammar.test.UtilMethods.X;
import org.intellij.grammar.test.UtilMethods.Y;
import org.intellij.grammar.test.UtilMethods.Z;

public class BarfooImpl extends ASTWrapperPsiElement implements Barfoo {

  public BarfooImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitBarfoo(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public <T> void foo1(T p1) {
    UtilMethods.foo1(this, p1);
  }

  @Override
  public <T extends X & Y> void foo0(T p1) throws Z, RuntimeException {
    UtilMethods.foo0(this, p1);
  }

}
// ---- BazImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;
import org.intellij.grammar.test.UtilMethods;
import org.intellij.grammar.test.UtilMethods.X;
import org.intellij.grammar.test.UtilMethods.Y;
import org.intellij.grammar.test.UtilMethods.Z;

public class BazImpl extends ASTWrapperPsiElement implements Baz {

  public BazImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitBaz(this);
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

}
// ---- Visitor.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class Visitor extends PsiElementVisitor {

  public void visitBar(@NotNull Bar o) {
    visitFoo(o);
  }

  public void visitBarbar(@NotNull Barbar o) {
    visitBar(o);
  }

  public void visitBarfoo(@NotNull Barfoo o) {
    visitBar(o);
  }

  public void visitBaz(@NotNull Baz o) {
    visitFoo(o);
  }

  public void visitFoo(@NotNull Foo o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}