// ---- generated/GeneratedTypes.java -----------------
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
// ---- generated/psi/Abc.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Abc extends PsiElement {

  @NotNull
  List<AbcThree> getAbcThreeList();

}
// ---- generated/psi/AbcOne.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AbcOne extends Abc {

  @NotNull
  JustB getJustB();

}
// ---- generated/psi/AbcThree.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AbcThree extends Abc {

}
// ---- generated/psi/AbcTwo.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AbcTwo extends Abc {

}
// ---- generated/psi/JustB.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JustB extends PsiElement {

}
// ---- generated/psi/PinnedSeq.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PinnedSeq extends PsiElement {

  @NotNull
  Prefix getPrefix();

}
// ---- generated/psi/PlainSeq.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PlainSeq extends PsiElement {

  @NotNull
  Prefix getPrefix();

}
// ---- generated/psi/Prefix.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Prefix extends PsiElement {

}
// ---- generated/psi/impl/AbcImpl.java -----------------
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
// ---- generated/psi/impl/AbcOneImpl.java -----------------
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

}
// ---- generated/psi/impl/AbcThreeImpl.java -----------------
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
// ---- generated/psi/impl/AbcTwoImpl.java -----------------
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

}
// ---- generated/psi/impl/JustBImpl.java -----------------
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
// ---- generated/psi/impl/PinnedSeqImpl.java -----------------
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
// ---- generated/psi/impl/PlainSeqImpl.java -----------------
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
// ---- generated/psi/impl/PrefixImpl.java -----------------
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