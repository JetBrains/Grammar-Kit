// ---- GeneratedParser.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import org.jetbrains.annotations.*;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static generated.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class GeneratedParser implements PsiParser {

  public static Logger LOG_ = Logger.getInstance("generated.GeneratedParser");

  @NotNull
  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this);
    if (root_ == ELEMENT) {
      result_ = element(builder_, level_ + 1);
    }
    else if (root_ == ENTRY) {
      result_ = entry(builder_, level_ + 1);
    }
    else if (root_ == LIST) {
      result_ = list(builder_, level_ + 1);
    }
    else if (root_ == MAP) {
      result_ = map(builder_, level_ + 1);
    }
    else {
      Marker marker_ = builder_.mark();
      enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_, null);
      result_ = parse_root_(root_, builder_, level_);
      exitErrorRecordingSection(builder_, level_, result_, true, _SECTION_RECOVER_, TOKEN_ADVANCER);
      marker_.done(root_);
    }
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return grammar(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // 'id'
  public static boolean element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<element>");
    result_ = consumeToken(builder_, "id");
    if (result_) {
      marker_.done(ELEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // 'name' '->' element
  public static boolean entry(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "entry")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<entry>");
    result_ = consumeToken(builder_, "name");
    result_ = result_ && consumeToken(builder_, "->");
    result_ = result_ && element(builder_, level_ + 1);
    if (result_) {
      marker_.done(ENTRY);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // list | map
  static boolean grammar(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = list(builder_, level_ + 1);
    if (!result_) result_ = map(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // '(' element (',' element) * ')'
  public static boolean list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<list>");
    result_ = consumeToken(builder_, "(");
    result_ = result_ && element(builder_, level_ + 1);
    result_ = result_ && list_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ")");
    if (result_) {
      marker_.done(LIST);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (',' element) *
  private static boolean list_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!list_2_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "list_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // ',' element
  private static boolean list_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ",");
    result_ = result_ && element(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // '(' entry (',' entry) * ')'
  public static boolean map(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "map")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<map>");
    result_ = consumeToken(builder_, "(");
    result_ = result_ && entry(builder_, level_ + 1);
    result_ = result_ && map_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ")");
    if (result_) {
      marker_.done(MAP);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (',' entry) *
  private static boolean map_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "map_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!map_2_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "map_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // ',' entry
  private static boolean map_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "map_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ",");
    result_ = result_ && entry(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

}