// ---- GeneratedTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import generated.psi.impl.*;

public interface GeneratedTypes {

  IElementType SOME = new IElementType("SOME", null);
  IElementType SOME_SEQ = new IElementType("SOME_SEQ", null);
  IElementType WITH_RECURSIVE = new IElementType("WITH_RECURSIVE", null);

  IElementType A = new IElementType("A", null);

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == SOME) {
        return new SomeImpl(node);
      }
      else if (type == SOME_SEQ) {
        return new SomeSeqImpl(node);
      }
      else if (type == WITH_RECURSIVE) {
        return new WithRecursiveImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- Some.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Some extends PsiElement {

}
// ---- SomeSeq.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SomeSeq extends PsiElement {

  @NotNull
  List<Some> getSomeList();

}
// ---- WithRecursive.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface WithRecursive extends PsiElement {

  @NotNull
  List<Some> getSomeList();

}
// ---- SomeImpl.java -----------------
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

public class SomeImpl extends ASTWrapperPsiElement implements Some {

  public SomeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) ((Visitor)visitor).visitSome(this);
    else super.accept(visitor);
  }

}
// ---- SomeSeqImpl.java -----------------
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

public class SomeSeqImpl extends ASTWrapperPsiElement implements SomeSeq {

  public SomeSeqImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) ((Visitor)visitor).visitSomeSeq(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Some> getSomeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Some.class);
  }

}
// ---- WithRecursiveImpl.java -----------------
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

public class WithRecursiveImpl extends ASTWrapperPsiElement implements WithRecursive {

  public WithRecursiveImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) ((Visitor)visitor).visitWithRecursive(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Some> getSomeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Some.class);
  }

}
// ---- Visitor.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class Visitor extends PsiElementVisitor {

  public void visitSome(@NotNull Some o) {
    visitPsiElement(o);
  }

  public void visitSomeSeq(@NotNull SomeSeq o) {
    visitPsiElement(o);
  }

  public void visitWithRecursive(@NotNull WithRecursive o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}