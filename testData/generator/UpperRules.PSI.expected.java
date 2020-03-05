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
  IElementType ABC_PIN = new IElementType("ABC_PIN", null);
  IElementType ABC_SEQ = new IElementType("ABC_SEQ", null);
  IElementType ABC_THREE = new IElementType("ABC_THREE", null);
  IElementType ABC_TWO = new IElementType("ABC_TWO", null);
  IElementType JUST_B = new IElementType("JUST_B", null);
  IElementType PREFIX = new IElementType("PREFIX", null);
  IElementType ROOT = new IElementType("ROOT", null);

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

}
// ---- AbcOne.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AbcOne extends PsiElement {

}
// ---- AbcPin.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AbcPin extends PsiElement {

  @NotNull
  Prefix getPrefix();

}
// ---- AbcSeq.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AbcSeq extends PsiElement {

}
// ---- AbcTwo.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AbcTwo extends PsiElement {

}
// ---- Prefix.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Prefix extends PsiElement {

}
// ---- Root.java -----------------
//header.txt
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Root extends PsiElement {

  @NotNull
  List<Abc> getAbcList();

  @NotNull
  List<AbcPin> getAbcPinList();

  @NotNull
  List<AbcSeq> getAbcSeqList();

}
