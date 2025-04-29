// ---- generated/GeneratedTypes.java -----------------
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
// ---- generated/GeneratedSyntaxElementTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;


public interface GeneratedSyntaxElementTypes {

  com.intellij.platform.syntax.SyntaxElementType CREATE_STATEMENT = new com.intellij.platform.syntax.SyntaxElementType("CREATE_STATEMENT");
  com.intellij.platform.syntax.SyntaxElementType CREATE_TABLE_STATEMENT = new com.intellij.platform.syntax.SyntaxElementType("CREATE_TABLE_STATEMENT");
  com.intellij.platform.syntax.SyntaxElementType DROP_STATEMENT = new com.intellij.platform.syntax.SyntaxElementType("DROP_STATEMENT");
  com.intellij.platform.syntax.SyntaxElementType DROP_TABLE_STATEMENT = new com.intellij.platform.syntax.SyntaxElementType("DROP_TABLE_STATEMENT");
  com.intellij.platform.syntax.SyntaxElementType STATEMENT = new com.intellij.platform.syntax.SyntaxElementType("STATEMENT");
  com.intellij.platform.syntax.SyntaxElementType TABLE_REF = new com.intellij.platform.syntax.SyntaxElementType("TABLE_REF");

  com.intellij.platform.syntax.SyntaxElementType create = new com.intellij.platform.syntax.SyntaxElementType("CREATE");
  com.intellij.platform.syntax.SyntaxElementType drop = new com.intellij.platform.syntax.SyntaxElementType("DROP");
  com.intellij.platform.syntax.SyntaxElementType global = new com.intellij.platform.syntax.SyntaxElementType("GLOBAL");
  com.intellij.platform.syntax.SyntaxElementType id = new com.intellij.platform.syntax.SyntaxElementType("id");
  com.intellij.platform.syntax.SyntaxElementType local = new com.intellij.platform.syntax.SyntaxElementType("LOCAL");
  com.intellij.platform.syntax.SyntaxElementType table = new com.intellij.platform.syntax.SyntaxElementType("TABLE");
  com.intellij.platform.syntax.SyntaxElementType temp = new com.intellij.platform.syntax.SyntaxElementType("TEMP");
}
// ---- generated/GeneratedSyntaxElementTypeConverterFactory.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;


public class GeneratedSyntaxElementTypeConverterFactory implements com.intellij.platform.syntax.psi.ElementTypeConverterFactory {

  @java.lang.Override
  public @org.jetbrains.annotations.NotNull com.intellij.platform.syntax.psi.ElementTypeConverter getElementTypeConverter() {
    return com.intellij.platform.syntax.psi.ElementTypeConverterKt.elementTypeConverterOf(
      new kotlin.Pair<com.intellij.platform.syntax.SyntaxElementType, com.intellij.psi.tree.IElementType>(generated.GeneratedSyntaxElementTypes.CREATE_STATEMENT, generated.GeneratedTypes.CREATE_STATEMENT),
      new kotlin.Pair<com.intellij.platform.syntax.SyntaxElementType, com.intellij.psi.tree.IElementType>(generated.GeneratedSyntaxElementTypes.CREATE_TABLE_STATEMENT, generated.GeneratedTypes.CREATE_TABLE_STATEMENT),
      new kotlin.Pair<com.intellij.platform.syntax.SyntaxElementType, com.intellij.psi.tree.IElementType>(generated.GeneratedSyntaxElementTypes.DROP_STATEMENT, generated.GeneratedTypes.DROP_STATEMENT),
      new kotlin.Pair<com.intellij.platform.syntax.SyntaxElementType, com.intellij.psi.tree.IElementType>(generated.GeneratedSyntaxElementTypes.DROP_TABLE_STATEMENT, generated.GeneratedTypes.DROP_TABLE_STATEMENT),
      new kotlin.Pair<com.intellij.platform.syntax.SyntaxElementType, com.intellij.psi.tree.IElementType>(generated.GeneratedSyntaxElementTypes.STATEMENT, generated.GeneratedTypes.STATEMENT),
      new kotlin.Pair<com.intellij.platform.syntax.SyntaxElementType, com.intellij.psi.tree.IElementType>(generated.GeneratedSyntaxElementTypes.TABLE_REF, generated.GeneratedTypes.TABLE_REF),

      new kotlin.Pair<com.intellij.platform.syntax.SyntaxElementType, com.intellij.psi.tree.IElementType>(generated.GeneratedSyntaxElementTypes.create, generated.GeneratedTypes.create),
      new kotlin.Pair<com.intellij.platform.syntax.SyntaxElementType, com.intellij.psi.tree.IElementType>(generated.GeneratedSyntaxElementTypes.table, generated.GeneratedTypes.table),
      new kotlin.Pair<com.intellij.platform.syntax.SyntaxElementType, com.intellij.psi.tree.IElementType>(generated.GeneratedSyntaxElementTypes.temp, generated.GeneratedTypes.temp),
      new kotlin.Pair<com.intellij.platform.syntax.SyntaxElementType, com.intellij.psi.tree.IElementType>(generated.GeneratedSyntaxElementTypes.global, generated.GeneratedTypes.global),
      new kotlin.Pair<com.intellij.platform.syntax.SyntaxElementType, com.intellij.psi.tree.IElementType>(generated.GeneratedSyntaxElementTypes.local, generated.GeneratedTypes.local),
      new kotlin.Pair<com.intellij.platform.syntax.SyntaxElementType, com.intellij.psi.tree.IElementType>(generated.GeneratedSyntaxElementTypes.drop, generated.GeneratedTypes.drop),
      new kotlin.Pair<com.intellij.platform.syntax.SyntaxElementType, com.intellij.psi.tree.IElementType>(generated.GeneratedSyntaxElementTypes.id, generated.GeneratedTypes.id)
    );
  }
}
// ---- generated/psi/CreateStatement.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;


public interface CreateStatement extends generated.psi.Statement {

}
// ---- generated/psi/CreateTableStatement.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;


public interface CreateTableStatement extends generated.psi.Statement {

  @org.jetbrains.annotations.NotNull
  generated.psi.TableRef getTableRef();

}
// ---- generated/psi/DropStatement.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;


public interface DropStatement extends generated.psi.Statement {

}
// ---- generated/psi/DropTableStatement.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;


public interface DropTableStatement extends generated.psi.Statement {

  @org.jetbrains.annotations.NotNull
  generated.psi.TableRef getTableRef();

}
// ---- generated/psi/Statement.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;


public interface Statement extends com.intellij.psi.PsiElement {

}
// ---- generated/psi/TableRef.java -----------------
// This is a generated file. Not intended for manual editing.
package generated.psi;


public interface TableRef extends com.intellij.psi.PsiElement {

}
// ---- generated/psi/impl/CreateStatementImpl.java -----------------
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
// ---- generated/psi/impl/CreateTableStatementImpl.java -----------------
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
// ---- generated/psi/impl/DropStatementImpl.java -----------------
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
// ---- generated/psi/impl/DropTableStatementImpl.java -----------------
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
// ---- generated/psi/impl/StatementImpl.java -----------------
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
// ---- generated/psi/impl/TableRefImpl.java -----------------
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
// ---- generated/psi/Visitor.java -----------------
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