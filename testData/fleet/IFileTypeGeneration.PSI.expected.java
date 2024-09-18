// ---- GeneratedTypes.java -----------------
// This is a generated file. Not intended for manual editing.
package fleet.generated;

import fleet.com.intellij.psi.tree.IElementType;

public interface GeneratedTypes {

  IElementType ELEMENT = new IElementType("ELEMENT", null);
  IElementType ENTRY = new IElementType("ENTRY", null);
  IElementType LIST = new IElementType("LIST", null);
  IElementType MAP = new IElementType("MAP", null);

}
// ---- MyFileType.java -----------------
// This is a generated file. Not intended for manual editing.
package fleet.some.filetype.psi;

import fleet.some.language.MyLanguage;
import fleet.com.intellij.psi.tree.IFileElementType;
import fleet.com.intellij.lang.PsiBuilder;
import org.jetbrains.annotations.NotNull;
import fleet.generated.GeneratedParser;

public class MyFileType extends IFileElementType {

  public static final MyFileType INSTANCE = new MyFileType();

  public MyFileType() {
    super("TEST", MyLanguage.INSTANCE);
  }

  @Override
  public void parse(@NotNull PsiBuilder<?> builder) {
    new GeneratedParser().parseLight(this, builder);
  }
}