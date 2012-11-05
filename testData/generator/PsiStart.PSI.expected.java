// ---- ParserTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import generated.psi.impl.*;

public interface ParserTypes {

  IElementType ELEMENT = new IElementType("ELEMENT", null);
  IElementType ENTRY = new IElementType("ENTRY", null);
  IElementType LIST = new IElementType("LIST", null);
  IElementType MAP = new IElementType("MAP", null);


  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ELEMENT) {
        return new ElementImpl(node);
      }
      else if (type == ENTRY) {
        return new EntryImpl(node);
      }
      else if (type == LIST) {
        return new ListImpl(node);
      }
      else if (type == MAP) {
        return new MapImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- Element.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Element extends PsiElement {

}
// ---- Entry.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Entry extends PsiElement {

  @NotNull
  Element getElement();

}
// ---- List.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface List extends PsiElement {

  @NotNull
  java.util.List<Element> getElementList();

}
// ---- Map.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Map extends PsiElement {

  @NotNull
  java.util.List<Entry> getEntryList();

}
// ---- ElementImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class ElementImpl extends ASTWrapperPsiElement implements Element {

  public ElementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) ((Visitor)visitor).visitElement(this);
    else super.accept(visitor);
  }

}
// ---- EntryImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class EntryImpl extends ASTWrapperPsiElement implements Entry {

  public EntryImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public Element getElement() {
    return findNotNullChildByClass(Element.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) ((Visitor)visitor).visitEntry(this);
    else super.accept(visitor);
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
import static generated.ParserTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class ListImpl extends ASTWrapperPsiElement implements List {

  public ListImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public java.util.List<Element> getElementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Element.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) ((Visitor)visitor).visitList(this);
    else super.accept(visitor);
  }

}
// ---- MapImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.ParserTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class MapImpl extends ASTWrapperPsiElement implements Map {

  public MapImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public java.util.List<Entry> getEntryList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Entry.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) ((Visitor)visitor).visitMap(this);
    else super.accept(visitor);
  }

}
// ---- Visitor.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class Visitor extends PsiElementVisitor {

  public void visitElement(@NotNull Element o) {
    visitPsiElement(o);
  }

  public void visitEntry(@NotNull Entry o) {
    visitPsiElement(o);
  }

  public void visitList(@NotNull List o) {
    visitPsiElement(o);
  }

  public void visitMap(@NotNull Map o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}