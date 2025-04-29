// ---- generated/GeneratedTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import generated.psi.impl.*;

public interface GeneratedTypes {

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
// ---- generated/GeneratedSyntaxElementTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.platform.syntax.SyntaxElementType;

public interface GeneratedSyntaxElementTypes {

  SyntaxElementType ELEMENT = new SyntaxElementType("ELEMENT");
  SyntaxElementType ENTRY = new SyntaxElementType("ENTRY");
  SyntaxElementType LIST = new SyntaxElementType("LIST");
  SyntaxElementType MAP = new SyntaxElementType("MAP");

}
// ---- generated/GeneratedSyntaxElementTypeConverterFactory.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

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
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ELEMENT, GeneratedTypes.ELEMENT),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ENTRY, GeneratedTypes.ENTRY),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.LIST, GeneratedTypes.LIST),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.MAP, GeneratedTypes.MAP)
    );
  }
}
// ---- generated/psi/Element.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Element extends PsiElement {

}
// ---- generated/psi/Entry.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Entry extends PsiElement {

  @NotNull
  Element getElement();

}
// ---- generated/psi/List.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface List extends PsiElement {

  @NotNull
  java.util.List<Element> getElementList();

}
// ---- generated/psi/Map.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Map extends PsiElement {

  @NotNull
  java.util.List<Entry> getEntryList();

}
// ---- generated/psi/impl/ElementImpl.java -----------------
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

public class ElementImpl extends ASTWrapperPsiElement implements Element {

  public ElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitElement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- generated/psi/impl/EntryImpl.java -----------------
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

public class EntryImpl extends ASTWrapperPsiElement implements Entry {

  public EntryImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitEntry(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public Element getElement() {
    return findNotNullChildByClass(Element.class);
  }

}
// ---- generated/psi/impl/ListImpl.java -----------------
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
  public java.util.List<Element> getElementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Element.class);
  }

}
// ---- generated/psi/impl/MapImpl.java -----------------
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

public class MapImpl extends ASTWrapperPsiElement implements Map {

  public MapImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitMap(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public java.util.List<Entry> getEntryList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Entry.class);
  }

}
// ---- generated/psi/Visitor.java -----------------
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