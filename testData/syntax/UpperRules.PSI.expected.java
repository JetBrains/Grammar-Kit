// ---- GeneratedTypes.java -----------------
//header.txt
package generated;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import generated.psi.impl.*;

public interface GeneratedTypes {

  IElementType ABC = new IElementType("ABC", null);
  IElementType ABC_ONE = new IElementType("ABC_ONE", null);
  IElementType ABC_THREE = new IElementType("ABC_THREE", null);
  IElementType ABC_TWO = new IElementType("ABC_TWO", null);
  IElementType JUST_B = new IElementType("JUST_B", null);
  IElementType PINNED_SEQ = new IElementType("PINNED_SEQ", null);
  IElementType PLAIN_SEQ = new IElementType("PLAIN_SEQ", null);
  IElementType PREFIX = new IElementType("PREFIX", null);

  IElementType A = new IElementType("A", null);
  IElementType B = new IElementType("B", null);
  IElementType C = new IElementType("C", null);
  IElementType X = new IElementType("X", null);
}
// ---- Abc.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Abc extends PsiElement {

  @NotNull
  List<AbcThree> getAbcThreeList();

}
// ---- AbcOne.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AbcOne extends Abc {

  @NotNull
  JustB getJustB();

  @Nullable
  Prefix getPrefix();

}
// ---- AbcThree.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AbcThree extends Abc {

}
// ---- AbcTwo.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AbcTwo extends Abc {

  @Nullable
  Prefix getPrefix();

}
// ---- JustB.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JustB extends PsiElement {

}
// ---- PinnedSeq.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PinnedSeq extends PsiElement {

  @NotNull
  Prefix getPrefix();

}
// ---- PlainSeq.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PlainSeq extends PsiElement {

  @NotNull
  Prefix getPrefix();

}
// ---- Prefix.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Prefix extends PsiElement {

}
// ---- AbcImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class AbcImpl extends ASTWrapperPsiElement implements Abc {

  public AbcImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<AbcThree> getAbcThreeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, AbcThree.class);
  }

}
// ---- AbcOneImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class AbcOneImpl extends AbcImpl implements AbcOne {

  public AbcOneImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public JustB getJustB() {
    return findNotNullChildByClass(JustB.class);
  }

  @Override
  @Nullable
  public Prefix getPrefix() {
    return findChildByClass(Prefix.class);
  }

}
// ---- AbcThreeImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class AbcThreeImpl extends AbcImpl implements AbcThree {

  public AbcThreeImpl(@NotNull ASTNode node) {
    super(node);
  }

}
// ---- AbcTwoImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import generated.psi.*;

public class AbcTwoImpl extends AbcImpl implements AbcTwo {

  public AbcTwoImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public Prefix getPrefix() {
    return findChildByClass(Prefix.class);
  }

}
// ---- JustBImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class JustBImpl extends ASTWrapperPsiElement implements JustB {

  public JustBImpl(@NotNull ASTNode node) {
    super(node);
  }

}
// ---- PinnedSeqImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class PinnedSeqImpl extends ASTWrapperPsiElement implements PinnedSeq {

  public PinnedSeqImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public Prefix getPrefix() {
    return findNotNullChildByClass(Prefix.class);
  }

}
// ---- PlainSeqImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class PlainSeqImpl extends ASTWrapperPsiElement implements PlainSeq {

  public PlainSeqImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public Prefix getPrefix() {
    return findNotNullChildByClass(Prefix.class);
  }

}
// ---- PrefixImpl.java -----------------
//header.txt
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class PrefixImpl extends ASTWrapperPsiElement implements Prefix {

  public PrefixImpl(@NotNull ASTNode node) {
    super(node);
  }

}