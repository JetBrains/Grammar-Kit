// ---- GeneratedTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;


public interface GeneratedTypes {

  com.intellij.psi.tree.IElementType CREATE_STATEMENT = new com.intellij.psi.tree.IElementType("CREATE_STATEMENT", null);
  com.intellij.psi.tree.IElementType CREATE_TABLE_STATEMENT = new com.intellij.psi.tree.IElementType("CREATE_TABLE_STATEMENT", null);
  com.intellij.psi.tree.IElementType DROP_STATEMENT = new com.intellij.psi.tree.IElementType("DROP_STATEMENT", null);
  com.intellij.psi.tree.IElementType DROP_TABLE_STATEMENT = new com.intellij.psi.tree.IElementType("DROP_TABLE_STATEMENT", null);
  com.intellij.psi.tree.IElementType STATEMENT = new com.intellij.psi.tree.IElementType("STATEMENT", null);
  com.intellij.psi.tree.IElementType TABLE_REF = new com.intellij.psi.tree.IElementType("TABLE_REF", null);

  com.intellij.psi.tree.IElementType create = new com.intellij.psi.tree.IElementType("CREATE", null);
  com.intellij.psi.tree.IElementType drop = new com.intellij.psi.tree.IElementType("DROP", null);
  com.intellij.psi.tree.IElementType global = new com.intellij.psi.tree.IElementType("GLOBAL", null);
  com.intellij.psi.tree.IElementType id = new com.intellij.psi.tree.IElementType("id", null);
  com.intellij.psi.tree.IElementType local = new com.intellij.psi.tree.IElementType("LOCAL", null);
  com.intellij.psi.tree.IElementType table = new com.intellij.psi.tree.IElementType("TABLE", null);
  com.intellij.psi.tree.IElementType temp = new com.intellij.psi.tree.IElementType("TEMP", null);

  class Classes {

    public static java.lang.Class<?> findClass(com.intellij.psi.tree.IElementType elementType) {
      return ourMap.get(elementType);
    }

    public static java.util.Set<com.intellij.psi.tree.IElementType> elementTypes() {
      return java.util.Collections.unmodifiableSet(ourMap.keySet());
    }

    private static final java.util.LinkedHashMap<com.intellij.psi.tree.IElementType, java.lang.Class<?>> ourMap = new java.util.LinkedHashMap<com.intellij.psi.tree.IElementType, java.lang.Class<?>>();

    static {
      ourMap.put(CREATE_TABLE_STATEMENT, CreateTableStatementImpl.class);
      ourMap.put(DROP_TABLE_STATEMENT, DropTableStatementImpl.class);
      ourMap.put(TABLE_REF, TableRefImpl.class);
    }
  }

  class Factory {
    public static com.intellij.psi.PsiElement createElement(com.intellij.lang.ASTNode node) {
      com.intellij.psi.tree.IElementType type = node.getElementType();
      if (type == CREATE_TABLE_STATEMENT) {
        return new generated.psi.impl.CreateTableStatementImpl(node);
      }
      else if (type == DROP_TABLE_STATEMENT) {
        return new generated.psi.impl.DropTableStatementImpl(node);
      }
      else if (type == TABLE_REF) {
        return new generated.psi.impl.TableRefImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
// ---- CreateStatement.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;


public interface CreateStatement extends generated.psi.Statement {

}
// ---- CreateTableStatement.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;


public interface CreateTableStatement extends generated.psi.Statement {

  @org.jetbrains.annotations.NotNull
  generated.psi.TableRef getTableRef();

}
// ---- DropStatement.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;


public interface DropStatement extends generated.psi.Statement {

}
// ---- DropTableStatement.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;


public interface DropTableStatement extends generated.psi.Statement {

  @org.jetbrains.annotations.NotNull
  generated.psi.TableRef getTableRef();

}
// ---- Statement.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;


public interface Statement extends com.intellij.psi.PsiElement {

}
// ---- TableRef.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;


public interface TableRef extends com.intellij.psi.PsiElement {

}
// ---- CreateStatementImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import static generated.GeneratedTypes.*;

public abstract class CreateStatementImpl extends generated.psi.impl.StatementImpl implements generated.psi.CreateStatement {

  public CreateStatementImpl(@org.jetbrains.annotations.NotNull com.intellij.lang.ASTNode node) {
    super(node);
  }

  @java.lang.Override
  public <Val> Val accept(@org.jetbrains.annotations.NotNull generated.psi.Visitor<Val> visitor) {
    return visitor.visitCreateStatement(this);
  }

  @java.lang.Override
  public void accept(@org.jetbrains.annotations.NotNull com.intellij.psi.PsiElementVisitor visitor) {
    if (visitor instanceof generated.psi.Visitor) accept((generated.psi.Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- CreateTableStatementImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import static generated.GeneratedTypes.*;

public class CreateTableStatementImpl extends generated.psi.impl.StatementImpl implements generated.psi.CreateTableStatement {

  public CreateTableStatementImpl(@org.jetbrains.annotations.NotNull com.intellij.lang.ASTNode node) {
    super(node);
  }

  @java.lang.Override
  public <Val> Val accept(@org.jetbrains.annotations.NotNull generated.psi.Visitor<Val> visitor) {
    return visitor.visitCreateTableStatement(this);
  }

  @java.lang.Override
  public void accept(@org.jetbrains.annotations.NotNull com.intellij.psi.PsiElementVisitor visitor) {
    if (visitor instanceof generated.psi.Visitor) accept((generated.psi.Visitor)visitor);
    else super.accept(visitor);
  }

  @java.lang.Override
  @org.jetbrains.annotations.NotNull
  public generated.psi.TableRef getTableRef() {
    return findNotNullChildByClass(generated.psi.TableRef.class);
  }

}
// ---- DropStatementImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import static generated.GeneratedTypes.*;

public abstract class DropStatementImpl extends generated.psi.impl.StatementImpl implements generated.psi.DropStatement {

  public DropStatementImpl(@org.jetbrains.annotations.NotNull com.intellij.lang.ASTNode node) {
    super(node);
  }

  @java.lang.Override
  public <Val> Val accept(@org.jetbrains.annotations.NotNull generated.psi.Visitor<Val> visitor) {
    return visitor.visitDropStatement(this);
  }

  @java.lang.Override
  public void accept(@org.jetbrains.annotations.NotNull com.intellij.psi.PsiElementVisitor visitor) {
    if (visitor instanceof generated.psi.Visitor) accept((generated.psi.Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- DropTableStatementImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import static generated.GeneratedTypes.*;

public class DropTableStatementImpl extends generated.psi.impl.StatementImpl implements generated.psi.DropTableStatement {

  public DropTableStatementImpl(@org.jetbrains.annotations.NotNull com.intellij.lang.ASTNode node) {
    super(node);
  }

  @java.lang.Override
  public <Val> Val accept(@org.jetbrains.annotations.NotNull generated.psi.Visitor<Val> visitor) {
    return visitor.visitDropTableStatement(this);
  }

  @java.lang.Override
  public void accept(@org.jetbrains.annotations.NotNull com.intellij.psi.PsiElementVisitor visitor) {
    if (visitor instanceof generated.psi.Visitor) accept((generated.psi.Visitor)visitor);
    else super.accept(visitor);
  }

  @java.lang.Override
  @org.jetbrains.annotations.NotNull
  public generated.psi.TableRef getTableRef() {
    return findNotNullChildByClass(generated.psi.TableRef.class);
  }

}
// ---- StatementImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import static generated.GeneratedTypes.*;

public abstract class StatementImpl extends com.intellij.extapi.psi.ASTWrapperPsiElement implements generated.psi.Statement {

  public StatementImpl(@org.jetbrains.annotations.NotNull com.intellij.lang.ASTNode node) {
    super(node);
  }

  public <Val> Val accept(@org.jetbrains.annotations.NotNull generated.psi.Visitor<Val> visitor) {
    return visitor.visitStatement(this);
  }

  @java.lang.Override
  public void accept(@org.jetbrains.annotations.NotNull com.intellij.psi.PsiElementVisitor visitor) {
    if (visitor instanceof generated.psi.Visitor) accept((generated.psi.Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- TableRefImpl.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import static generated.GeneratedTypes.*;

public class TableRefImpl extends com.intellij.extapi.psi.ASTWrapperPsiElement implements generated.psi.TableRef {

  public TableRefImpl(@org.jetbrains.annotations.NotNull com.intellij.lang.ASTNode node) {
    super(node);
  }

  public <Val> Val accept(@org.jetbrains.annotations.NotNull generated.psi.Visitor<Val> visitor) {
    return visitor.visitTableRef(this);
  }

  @java.lang.Override
  public void accept(@org.jetbrains.annotations.NotNull com.intellij.psi.PsiElementVisitor visitor) {
    if (visitor instanceof generated.psi.Visitor) accept((generated.psi.Visitor)visitor);
    else super.accept(visitor);
  }

}
// ---- Visitor.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;


public class Visitor<Val> extends com.intellij.psi.PsiElementVisitor {

  public Val visitCreateStatement(@org.jetbrains.annotations.NotNull CreateStatement o) {
    return visitStatement(o);
  }

  public Val visitCreateTableStatement(@org.jetbrains.annotations.NotNull CreateTableStatement o) {
    return visitStatement(o);
  }

  public Val visitDropStatement(@org.jetbrains.annotations.NotNull DropStatement o) {
    return visitStatement(o);
  }

  public Val visitDropTableStatement(@org.jetbrains.annotations.NotNull DropTableStatement o) {
    return visitStatement(o);
  }

  public Val visitStatement(@org.jetbrains.annotations.NotNull Statement o) {
    return visitPsiElement(o);
  }

  public Val visitTableRef(@org.jetbrains.annotations.NotNull TableRef o) {
    return visitPsiElement(o);
  }

  public Val visitPsiElement(@org.jetbrains.annotations.NotNull com.intellij.psi.PsiElement o) {
    visitElement(o);
    return null;
  }

}