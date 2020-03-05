// ---- GeneratedTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import generated.psi.impl.*;

public interface GeneratedTypes {

  IElementType A_EXPR = new IElementType("A_EXPR", null);
  IElementType B_EXPR = new IElementType("B_EXPR", null);
  IElementType ERL_LIST = new IElementType("ERL_LIST", null);
  IElementType EXPR = new IElementType("EXPR", null);
  IElementType FOO__ﾉ__Ω__ﾉ_ﾐ_____INNER = new IElementType("FOO__ﾉ__Ω__ﾉ_ﾐ_____INNER", null);
  IElementType IMPORT = new IElementType("IMPORT", null);
  IElementType LEFT_EXPR = new IElementType("LEFT_EXPR", null);
  IElementType NOT_OPTIMIZED_CHOICE = new IElementType("NOT_OPTIMIZED_CHOICE", null);
  IElementType SOME = new IElementType("SOME", null);
  IElementType SOME_EXPR = new IElementType("SOME_EXPR", null);
  IElementType SOME_SEQ = new IElementType("SOME_SEQ", null);
  IElementType THING_ITEM = new IElementType("THING_ITEM", null);
  IElementType TWO_USAGES_LEFT = new IElementType("TWO_USAGES_LEFT", null);
  IElementType WITH_RECURSIVE = new IElementType("WITH_RECURSIVE", null);
  IElementType ZOME = new IElementType("ZOME", null);

  IElementType A = new IElementType("A", null);
  IElementType B = new IElementType("B", null);
  IElementType TOKEN_ONE = new IElementType("token-one", null);
  IElementType TOKEN_THREE = new IElementType("#", null);
  IElementType TOKEN_TWO = new IElementType("token-two", null);
}
// ---- FooΩInner.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface FooΩInner extends PsiElement {

}
// ---- AExpr.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- BExpr.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- ErlList.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ErlList extends PsiElement {

  @NotNull
  List<Expr> getExprList();

  @NotNull
  List<Some> getSomeList();

  @Nullable
  Zome getZome();

}
// ---- Expr.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Expr extends PsiElement {

}
// ---- Import.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Import extends PsiElement {

  @NotNull
  Some getSome();

}
// ---- LeftExpr.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface LeftExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- NotOptimizedChoice.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface NotOptimizedChoice extends PsiElement {

}
// ---- Some.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Some extends PsiElement {

}
// ---- SomeExpr.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SomeExpr extends Expr {

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
// ---- Thing.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Thing extends PsiElement {

  @NotNull
  ThingItem getThingItem();

}
// ---- ThingItem.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ThingItem extends PsiElement {

}
// ---- TwoUsagesLeft.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TwoUsagesLeft extends PsiElement {

  @NotNull
  Expr getExpr();

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
// ---- Zome.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Zome extends PsiElement {

  @Nullable
  PsiElement getTokenOne();

  @Nullable
  PsiElement getTokenTwo();

}
// ---- FooΩInnerImpl.java -----------------
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

public class FooΩInnerImpl extends ASTWrapperPsiElement implements FooΩInner {

  public FooΩInnerImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitFooΩInner(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- AExprImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class AExprImpl extends ExprImpl implements AExpr {

  public AExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitAExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- BExprImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class BExprImpl extends ExprImpl implements BExpr {

  public BExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitBExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- ErlListImpl.java -----------------
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

public class ErlListImpl extends ASTWrapperPsiElement implements ErlList {

  public ErlListImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitErlList(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

  @Override
  @NotNull
  public List<Some> getSomeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Some.class);
  }

  @Override
  @Nullable
  public Zome getZome() {
    return findChildByClass(Zome.class);
  }

}
// ---- ExprImpl.java -----------------
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

public abstract class ExprImpl extends ASTWrapperPsiElement implements Expr {

  public ExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- ImportImpl.java -----------------
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

public class ImportImpl extends ASTWrapperPsiElement implements Import {

  public ImportImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitImport(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public Some getSome() {
    return findNotNullChildByClass(Some.class);
  }

}
// ---- LeftExprImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class LeftExprImpl extends ExprImpl implements LeftExpr {

  public LeftExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitLeftExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr.class);
  }

}
// ---- NotOptimizedChoiceImpl.java -----------------
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

public class NotOptimizedChoiceImpl extends ASTWrapperPsiElement implements NotOptimizedChoice {

  public NotOptimizedChoiceImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitNotOptimizedChoice(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

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

  public SomeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitSome(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- SomeExprImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public abstract class SomeExprImpl extends ExprImpl implements SomeExpr {

  public SomeExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitSomeExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
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

  public SomeSeqImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitSomeSeq(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Some> getSomeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Some.class);
  }

}
// ---- ThingImpl.java -----------------
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

public class ThingImpl extends ASTWrapperPsiElement implements Thing {

  public ThingImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitThing(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ThingItem getThingItem() {
    return findNotNullChildByClass(ThingItem.class);
  }

}
// ---- ThingItemImpl.java -----------------
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

public class ThingItemImpl extends ASTWrapperPsiElement implements ThingItem {

  public ThingItemImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitThingItem(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- TwoUsagesLeftImpl.java -----------------
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

public class TwoUsagesLeftImpl extends ASTWrapperPsiElement implements TwoUsagesLeft {

  public TwoUsagesLeftImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitTwoUsagesLeft(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public Expr getExpr() {
    return findNotNullChildByClass(Expr.class);
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

  public WithRecursiveImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitWithRecursive(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Some> getSomeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Some.class);
  }

}
// ---- ZomeImpl.java -----------------
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

public class ZomeImpl extends ASTWrapperPsiElement implements Zome {

  public ZomeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitZome(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getTokenOne() {
    return findChildByType(TOKEN_ONE);
  }

  @Override
  @Nullable
  public PsiElement getTokenTwo() {
    return findChildByType(TOKEN_TWO);
  }

}
// ---- Visitor.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class Visitor extends PsiElementVisitor {

  public void visitFooΩInner(@NotNull FooΩInner o) {
    visitPsiElement(o);
  }

  public void visitAExpr(@NotNull AExpr o) {
    visitExpr(o);
  }

  public void visitBExpr(@NotNull BExpr o) {
    visitExpr(o);
  }

  public void visitErlList(@NotNull ErlList o) {
    visitPsiElement(o);
  }

  public void visitExpr(@NotNull Expr o) {
    visitPsiElement(o);
  }

  public void visitImport(@NotNull Import o) {
    visitPsiElement(o);
  }

  public void visitLeftExpr(@NotNull LeftExpr o) {
    visitExpr(o);
  }

  public void visitNotOptimizedChoice(@NotNull NotOptimizedChoice o) {
    visitPsiElement(o);
  }

  public void visitSome(@NotNull Some o) {
    visitPsiElement(o);
  }

  public void visitSomeExpr(@NotNull SomeExpr o) {
    visitExpr(o);
  }

  public void visitSomeSeq(@NotNull SomeSeq o) {
    visitPsiElement(o);
  }

  public void visitThing(@NotNull Thing o) {
    visitPsiElement(o);
  }

  public void visitThingItem(@NotNull ThingItem o) {
    visitPsiElement(o);
  }

  public void visitTwoUsagesLeft(@NotNull TwoUsagesLeft o) {
    visitPsiElement(o);
  }

  public void visitWithRecursive(@NotNull WithRecursive o) {
    visitPsiElement(o);
  }

  public void visitZome(@NotNull Zome o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}