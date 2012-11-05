package org.intellij.grammar.expression;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import org.intellij.grammar.BnfParserDefinition;
import org.intellij.grammar.parser.BnfLexer;
import org.intellij.grammar.psi.BnfTypes;
import org.intellij.grammar.psi.impl.BnfCompositeElementImpl;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author gregsh
 */
public class ExpressionParserDefinition extends BnfParserDefinition{
  public static final Language EXPR_LANGUAGE = new Language("EXPR") {

  };

  public static IElementType createType(String str) {
    return new IElementType(str, EXPR_LANGUAGE);
  }

  public static final LanguageFileType FILE_TYPE = new LanguageFileType(EXPR_LANGUAGE) {
    @NotNull
    @Override
    public String getName() {
      return "Expressions";
    }

    @NotNull
    @Override
    public String getDescription() {
      return "";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
      return "expr";
    }

    @Override
    public Icon getIcon() {
      return null;
    }
  };

  public static final IFileElementType EXPR_FILE_TYPE = new IFileElementType("EXPR_FILE_TYPE", EXPR_LANGUAGE);

  @Override
  public IFileElementType getFileNodeType() {
    return EXPR_FILE_TYPE;
  }

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new BnfLexer();
  }

  @Override
  public PsiFile createFile(FileViewProvider fileViewProvider) {
    return new PsiFileBase(fileViewProvider, EXPR_LANGUAGE) {

      @NotNull
      @Override
      public FileType getFileType() {
        return FILE_TYPE;
      }

      @Override
      public void accept(@NotNull PsiElementVisitor visitor) {
        visitor.visitFile(this);
      }
    };
  }

  @Override
  public PsiParser createParser(Project project) {
    return new ExpressionParser();
  }

  @NotNull
  @Override
  public PsiElement createElement(ASTNode astNode) {
    return new BnfCompositeElementImpl(astNode);
  }

  public static IElementType createTokenType(String text) {
    for (IElementType type : new IElementType[]{BnfTypes.BNF_ID, BnfTypes.BNF_NUMBER}) {
      if (type.toString().equals(text)) return type;
    }
    throw new AssertionError(text);
  }
}
