// ---- GeneratedTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import generated.psi.impl.*;

public interface GeneratedTypes {

  IElementType ITEM = new IElementType("ITEM", null);
  IElementType LIST = new IElementType("LIST", null);

  IElementType COMMA = new IElementType(",", null);
  IElementType NUMBER = new IElementType("number", null);
  IElementType PAREN1 = new IElementType("(", null);
  IElementType PAREN2 = new IElementType(")", null);
  IElementType SEMI = new IElementType(";", null);

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ITEM) {
        return new ItemImpl(node);
      }
      else if (type == LIST) {
        return new ListImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- Item.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Item extends PsiElement {

  @NotNull
  PsiElement getNumber();

}
// ---- List.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface List extends PsiElement {

  @NotNull
  java.util.List<Item> getItemList();

}
// ---- ItemImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class ItemImpl extends ASTWrapperPsiElement implements Item {

  public ItemImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitItem(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getNumber() {
    return findNotNullChildByType(NUMBER);
  }

}
// ---- ListImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class ListImpl extends ASTWrapperPsiElement implements List {

  public ListImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitList(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public java.util.List<Item> getItemList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Item.class);
  }

}
// ---- Visitor.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class Visitor extends PsiElementVisitor {

  public void visitItem(@NotNull Item o) {
    visitPsiElement(o);
  }

  public void visitList(@NotNull List o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}