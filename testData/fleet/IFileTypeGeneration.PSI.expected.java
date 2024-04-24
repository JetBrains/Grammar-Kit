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
import org.jetbrains.annotations.NotNull;
import fleet.generated.GeneratedParser;
import fleet.com.intellij.psi.builder.FleetPsiBuilder;

public class MyFileType extends IFileElementType {

  public static final MyFileType INSTANCE = new MyFileType();

  public MyFileType() {
    super("MyFileName", MyLanguage.INSTANCE)
  }

  @Override
  public void parse(@NotNull FleetPsiBuilder<?> builder) {
    new GeneratedParser().parseLight(this, builder);
  }
}