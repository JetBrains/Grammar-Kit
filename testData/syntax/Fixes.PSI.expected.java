// ---- generated/GeneratedTypes.java -----------------
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
  IElementType RECURSIVE_EXTEND_A = new IElementType("RECURSIVE_EXTEND_A", null);
  IElementType RECURSIVE_EXTEND_B = new IElementType("RECURSIVE_EXTEND_B", null);
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
// ---- generated/GeneratedSyntaxElementTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.platform.syntax.SyntaxElementType;

public interface GeneratedSyntaxElementTypes {

  SyntaxElementType A_EXPR = new SyntaxElementType("A_EXPR");
  SyntaxElementType B_EXPR = new SyntaxElementType("B_EXPR");
  SyntaxElementType ERL_LIST = new SyntaxElementType("ERL_LIST");
  SyntaxElementType EXPR = new SyntaxElementType("EXPR");
  SyntaxElementType FOO__ﾉ__Ω__ﾉ_ﾐ_____INNER = new SyntaxElementType("FOO__ﾉ__Ω__ﾉ_ﾐ_____INNER");
  SyntaxElementType IMPORT = new SyntaxElementType("IMPORT");
  SyntaxElementType LEFT_EXPR = new SyntaxElementType("LEFT_EXPR");
  SyntaxElementType NOT_OPTIMIZED_CHOICE = new SyntaxElementType("NOT_OPTIMIZED_CHOICE");
  SyntaxElementType RECURSIVE_EXTEND_A = new SyntaxElementType("RECURSIVE_EXTEND_A");
  SyntaxElementType RECURSIVE_EXTEND_B = new SyntaxElementType("RECURSIVE_EXTEND_B");
  SyntaxElementType SOME = new SyntaxElementType("SOME");
  SyntaxElementType SOME_EXPR = new SyntaxElementType("SOME_EXPR");
  SyntaxElementType SOME_SEQ = new SyntaxElementType("SOME_SEQ");
  SyntaxElementType THING_ITEM = new SyntaxElementType("THING_ITEM");
  SyntaxElementType TWO_USAGES_LEFT = new SyntaxElementType("TWO_USAGES_LEFT");
  SyntaxElementType WITH_RECURSIVE = new SyntaxElementType("WITH_RECURSIVE");
  SyntaxElementType ZOME = new SyntaxElementType("ZOME");

  SyntaxElementType A = new SyntaxElementType("A");
  SyntaxElementType B = new SyntaxElementType("B");
  SyntaxElementType TOKEN_ONE = new SyntaxElementType("token-one");
  SyntaxElementType TOKEN_THREE = new SyntaxElementType("#");
  SyntaxElementType TOKEN_TWO = new SyntaxElementType("token-two");
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
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.A_EXPR, GeneratedTypes.A_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.B_EXPR, GeneratedTypes.B_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ERL_LIST, GeneratedTypes.ERL_LIST),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.EXPR, GeneratedTypes.EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.FOO__ﾉ__Ω__ﾉ_ﾐ_____INNER, GeneratedTypes.FOO__ﾉ__Ω__ﾉ_ﾐ_____INNER),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.IMPORT, GeneratedTypes.IMPORT),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.LEFT_EXPR, GeneratedTypes.LEFT_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.NOT_OPTIMIZED_CHOICE, GeneratedTypes.NOT_OPTIMIZED_CHOICE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.RECURSIVE_EXTEND_A, GeneratedTypes.RECURSIVE_EXTEND_A),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.RECURSIVE_EXTEND_B, GeneratedTypes.RECURSIVE_EXTEND_B),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.SOME, GeneratedTypes.SOME),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.SOME_EXPR, GeneratedTypes.SOME_EXPR),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.SOME_SEQ, GeneratedTypes.SOME_SEQ),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.THING_ITEM, GeneratedTypes.THING_ITEM),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.TWO_USAGES_LEFT, GeneratedTypes.TWO_USAGES_LEFT),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.WITH_RECURSIVE, GeneratedTypes.WITH_RECURSIVE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.ZOME, GeneratedTypes.ZOME),

      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.TOKEN_THREE, GeneratedTypes.TOKEN_THREE),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.A, GeneratedTypes.A),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.TOKEN_TWO, GeneratedTypes.TOKEN_TWO),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.B, GeneratedTypes.B),
      new Pair<SyntaxElementType, IElementType>(GeneratedSyntaxElementTypes.TOKEN_ONE, GeneratedTypes.TOKEN_ONE)
    );
  }
}
// ---- generated/psi/FooΩInner.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface FooΩInner extends PsiElement {

}
// ---- generated/psi/AExpr.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- generated/psi/BExpr.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- generated/psi/ErlList.java -----------------
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
// ---- generated/psi/Expr.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Expr extends PsiElement {

}
// ---- generated/psi/Import.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Import extends PsiElement {

  @NotNull
  Some getSome();

}
// ---- generated/psi/LeftExpr.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface LeftExpr extends Expr {

  @NotNull
  List<Expr> getExprList();

}
// ---- generated/psi/NotOptimizedChoice.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface NotOptimizedChoice extends PsiElement {

}
// ---- generated/psi/RecursiveExtendA.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RecursiveExtendA extends RecursiveExtendB {

}
// ---- generated/psi/RecursiveExtendB.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RecursiveExtendB extends RecursiveExtendA {

}
// ---- generated/psi/Some.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Some extends PsiElement {

}
// ---- generated/psi/SomeExpr.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SomeExpr extends Expr {

}
// ---- generated/psi/SomeSeq.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SomeSeq extends PsiElement {

  @NotNull
  List<Some> getSomeList();

}
// ---- generated/psi/Thing.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Thing extends PsiElement {

  @NotNull
  ThingItem getThingItem();

}
// ---- generated/psi/ThingItem.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ThingItem extends PsiElement {

}
// ---- generated/psi/TwoUsagesLeft.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TwoUsagesLeft extends PsiElement {

  @NotNull
  Expr getExpr();

}
// ---- generated/psi/WithRecursive.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface WithRecursive extends PsiElement {

  @NotNull
  List<Some> getSomeList();

}
// ---- generated/psi/Zome.java -----------------
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
// ---- generated/psi/impl/FooΩInnerImpl.java -----------------
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

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- generated/psi/impl/AExprImpl.java -----------------
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

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitAExpr(this);
  }

  @Override
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
// ---- generated/psi/impl/BExprImpl.java -----------------
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

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitBExpr(this);
  }

  @Override
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
// ---- generated/psi/impl/ErlListImpl.java -----------------
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

  @Override
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
// ---- generated/psi/impl/ExprImpl.java -----------------
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

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- generated/psi/impl/ImportImpl.java -----------------
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

  @Override
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
// ---- generated/psi/impl/LeftExprImpl.java -----------------
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

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitLeftExpr(this);
  }

  @Override
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
// ---- generated/psi/impl/NotOptimizedChoiceImpl.java -----------------
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

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- generated/psi/impl/RecursiveExtendAImpl.java -----------------
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

public class RecursiveExtendAImpl extends RecursiveExtendBImpl implements RecursiveExtendA {

  public RecursiveExtendAImpl(ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitRecursiveExtendA(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- generated/psi/impl/RecursiveExtendBImpl.java -----------------
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

public class RecursiveExtendBImpl extends RecursiveExtendAImpl implements RecursiveExtendB {

  public RecursiveExtendBImpl(ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitRecursiveExtendB(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- generated/psi/impl/SomeImpl.java -----------------
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

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- generated/psi/impl/SomeExprImpl.java -----------------
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

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitSomeExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- generated/psi/impl/SomeSeqImpl.java -----------------
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

  @Override
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
// ---- generated/psi/impl/ThingImpl.java -----------------
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

  @Override
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
// ---- generated/psi/impl/ThingItemImpl.java -----------------
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

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- generated/psi/impl/TwoUsagesLeftImpl.java -----------------
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

  @Override
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
// ---- generated/psi/impl/WithRecursiveImpl.java -----------------
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

  @Override
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
// ---- generated/psi/impl/ZomeImpl.java -----------------
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

  @Override
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
// ---- generated/psi/Visitor.java -----------------
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

  public void visitRecursiveExtendA(@NotNull RecursiveExtendA o) {
    visitRecursiveExtendB(o);
  }

  public void visitRecursiveExtendB(@NotNull RecursiveExtendB o) {
    visitRecursiveExtendA(o);
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