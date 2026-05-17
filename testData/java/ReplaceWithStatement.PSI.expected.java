// ---- test/FooTypes.java -----------------
//header.txt
package test;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import test.stub.FooParserDefinition;
import test.psi.impl.*;

public interface FooTypes {

  IElementType SWIFT_STATEMENT = FooParserDefinition.createType("SWIFT_STATEMENT");


  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == SWIFT_STATEMENT) {
        return new SwiftStatementImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- test/psi/SwiftStatement.java -----------------
//header.txt
package test.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;

public interface SwiftStatement extends PsiElement {

  <S extends @NotNull SwiftStatement> S replaceWithStatement(S newStatement, boolean reformat) throws IncorrectOperationException;

}
// ---- test/psi/impl/SwiftStatementImpl.java -----------------
//header.txt
package test.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import test.psi.MyPsiTreeUtil;
import static test.FooTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import test.psi.*;
import org.intellij.grammar.test.SwiftPsiUtilKt;
import com.intellij.util.IncorrectOperationException;

public class SwiftStatementImpl extends ASTWrapperPsiElement implements SwiftStatement {

  public SwiftStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitSwiftStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public <S extends @NotNull SwiftStatement> S replaceWithStatement(S newStatement, boolean reformat) throws IncorrectOperationException {
    return SwiftPsiUtilKt.replaceWithStatement(this, newStatement, reformat);
  }

}
// ---- test/psi/Visitor.java -----------------
//header.txt
package test.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class Visitor extends PsiElementVisitor {

  public void visitSwiftStatement(@NotNull SwiftStatement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
