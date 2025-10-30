// ---- generated/GeneratedSyntaxElementTypes.kt -----------------
// This is a generated file. Not intended for manual editing.
package generated

import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf

object GeneratedSyntaxElementTypes {
  val ITEM = SyntaxElementType("ITEM")
  val LIST = SyntaxElementType("LIST")

  val COMMA = SyntaxElementType(",")
  val NUMBER = SyntaxElementType("number")
  val PAREN1 = SyntaxElementType("(")
  val PAREN2 = SyntaxElementType(")")
  val SEMI = SyntaxElementType(";")
}
// ---- generated/GeneratedTypes.java -----------------
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
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.INSTANCE.getITEM(), GeneratedTypes.ITEM),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.INSTANCE.getLIST(), GeneratedTypes.LIST),

      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.INSTANCE.getNUMBER(), GeneratedTypes.NUMBER),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.INSTANCE.getCOMMA(), GeneratedTypes.COMMA),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.INSTANCE.getPAREN1(), GeneratedTypes.PAREN1),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.INSTANCE.getPAREN2(), GeneratedTypes.PAREN2),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.INSTANCE.getSEMI(), GeneratedTypes.SEMI)
    );
  }
}
// ---- generated/psi/Item.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Item extends PsiElement {

  @NotNull
  PsiElement getNumber();

}
// ---- generated/psi/List.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface List extends PsiElement {

  @NotNull
  java.util.List<Item> getItemList();

}
// ---- generated/psi/impl/ItemImpl.java -----------------
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
  public java.util.List<Item> getItemList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Item.class);
  }

}
// ---- generated/psi/Visitor.java -----------------
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