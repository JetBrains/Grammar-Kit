package org.intellij.grammar.livePreview;

import com.intellij.lang.Language;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author gregsh
 */
public class LivePreviewSyntaxHighlighterFactory extends SyntaxHighlighterFactory {

  public LivePreviewSyntaxHighlighterFactory() {
  }

  @NotNull
  @Override
  public SyntaxHighlighter getSyntaxHighlighter(@Nullable final Project project, @Nullable VirtualFile virtualFile) {
    final Language language = virtualFile instanceof LightVirtualFile ? ((LightVirtualFile)virtualFile).getLanguage() : null;
    if (!(language instanceof LivePreviewLanguage)) return new PlainSyntaxHighlighter();
    return new SyntaxHighlighterBase() {
      @NotNull
      @Override
      public Lexer getHighlightingLexer() {
        return new LivePreviewLexer(project, (LivePreviewLanguage)language) {
          @Nullable
          @Override
          public IElementType getTokenType() {
            IElementType tokenType = super.getTokenType();
            return tokenType instanceof KeywordTokenType? LivePreviewParserDefinition.KEYWORD : tokenType;
          }
        };
      }

      @NotNull
      @Override
      public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType == LivePreviewParserDefinition.COMMENT) return pack(DefaultLanguageHighlighterColors.LINE_COMMENT);
        if (tokenType == LivePreviewParserDefinition.STRING) return pack(DefaultLanguageHighlighterColors.STRING);
        if (tokenType == LivePreviewParserDefinition.NUMBER) return pack(DefaultLanguageHighlighterColors.NUMBER);
        if (tokenType == LivePreviewParserDefinition.KEYWORD) return pack(DefaultLanguageHighlighterColors.KEYWORD);
        if (tokenType == TokenType.BAD_CHARACTER) return pack(DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
        return EMPTY;
      }
    };
  }
}
