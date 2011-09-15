// This is a generated file. Not intended for manual editing.
package ;

import org.jetbrains.annotations.*;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static generated.ParserTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class LeftRule implements PsiParser {

  public static Logger LOG_ = Logger.getInstance("LeftRule");

  @NotNull
  public ASTNode parse(final IElementType root_, final PsiBuilder builder_) {
    final int level_ = 0;
    boolean result_;
    if (root_ == ALIAS_DEFINITION) {
      result_ = alias_definition(builder_, level_ + 1);
    }
    else {
      Marker marker_ = builder_.mark();
      try {
        result_ = from(builder_, level_ + 1);
        while (builder_.getTokenType() != null) {
          builder_.advanceLexer();
        }
      }
      finally {
        marker_.done(root_);
      }
    }
    return builder_.getTreeBuilt();
  }

  /* ********************************************************** */
  // AS? id
  public static boolean alias_definition(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "alias_definition")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = alias_definition_0(builder_, level_ + 1);
      result_ = result_ && consumeToken(builder_, ID);
    }
    finally {
      if (result_) {
        marker_.done(ALIAS_DEFINITION);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }

  // AS?
  private static boolean alias_definition_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "alias_definition_0")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    try {
      consumeToken(builder_, AS);
    }
    finally {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // reference alias_definition?
  static boolean from(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "from")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, REFERENCE);
      result_ = result_ && from_1(builder_, level_ + 1);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }

  // alias_definition?
  private static boolean from_1(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "from_1")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    try {
      alias_definition(builder_, level_ + 1);
    }
    finally {
      marker_.drop();
    }
    return result_;
  }


}
