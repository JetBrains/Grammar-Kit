package org.intellij.grammar.livePreview;

import com.intellij.lexer.LexerBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;

/**
 * @author gregsh
 */
public class LivePreviewLexer extends LexerBase {

  private CharSequence myBuffer;
  private int myEndOffset;
  private int myPosition;
  private int myTokenEnd;
  private IElementType myTokenType;

  private final Token[] myTokens;
  private Matcher[] myMatchers;

  public LivePreviewLexer(Project project, final LivePreviewLanguage language) {
    final BnfFile bnfFile = language.getGrammar(project);

    myTokens = bnfFile == null? new Token[0] : CachedValuesManager.getManager(project).getCachedValue(bnfFile, new CachedValueProvider<Token[]>() {
      @Nullable
      @Override
      public Result<Token[]> compute() {
        Map<String, String> map = collectTokenPattern2Name(bnfFile);
        if (!map.containsKey("GENERIC_ID")) map.put("GENERIC_ID", "\\w+");
        //    comment='///.*\n/'
        //    number='/\\d+/'
        //    id='/\\p{Alpha}\\w*/'
        //    string='/(\'([^\'\\\\]|\\\\.)*\'|\"([^\"\\\\]|\\\\.)*\")/'
        Token[] tokens = new Token[map.size() + 1];
        int i = 0;
        String tokenTypeText = getRootAttribute(bnfFile, KnownAttribute.ELEMENT_TYPE_PREFIX);
        for (String pattern : map.keySet()) {
          tokens[i++] = new Token(pattern, tokenTypeText + map.get(pattern).toUpperCase(), language);
        }
        tokens[i] = new Token("\\s+", TokenType.WHITE_SPACE);
        return Result.create(tokens, bnfFile);
      }
    });
  }

  @Override
  public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {
    myBuffer = buffer;
    myEndOffset = endOffset;
    myPosition = startOffset;
    myTokenEnd = myPosition;
    myTokenType = null;
    myMatchers = new Matcher[myTokens.length];
    for (int i = 0; i < myMatchers.length; i++) {
      Pattern pattern = myTokens[i].pattern;
      if (pattern == null) continue;
      myMatchers[i] = pattern.matcher(buffer);
    }
    nextToken();
  }

  private void nextToken() {
    myTokenEnd = myPosition;
    if (myPosition >= myEndOffset) {
      myTokenType = null;
      return;
    }
    if (!findAtOffset(myPosition)) {
      int nextOffset = myPosition;
      while (++nextOffset < myEndOffset) {
        if (findAtOffset(nextOffset)) break;
      }
      myTokenEnd = nextOffset;
      myTokenType = TokenType.BAD_CHARACTER;
    }
  }

  private boolean findAtOffset(int position) {
    myTokenEnd = position;
    myTokenType = null;
    for (int i = 0; i < myMatchers.length; i++) {
      if (myMatchers[i] == null) continue;
      Matcher matcher = myMatchers[i].region(position, myEndOffset);
      if (matcher.lookingAt()) {
        int end = matcher.end();
        if (end > myTokenEnd) {
          myTokenEnd = end;
          myTokenType = myTokens[i].tokenType;
        }
      }
    }
    return myTokenType != null;
  }

  @Override
  public int getState() {
    return 0;
  }

  @Nullable
  @Override
  public IElementType getTokenType() {
    assert myTokenType != null || myPosition == myEndOffset: "not lexed: '" + myBuffer.subSequence(myPosition, myEndOffset) + "'";
    return myTokenType;
  }

  @Override
  public int getTokenStart() {
    return myPosition;
  }

  @Override
  public int getTokenEnd() {
    return myTokenEnd;
  }

  @Override
  public void advance() {
    if (myTokenType != null) {
      myPosition = myTokenEnd;
      nextToken();
    }
  }

  @Override
  public CharSequence getBufferSequence() {
    return myBuffer;
  }

  @Override
  public int getBufferEnd() {
    return myEndOffset;
  }

  public Collection<Token> getTokens() {
    return Arrays.asList(myTokens);
  }

  static class Token {

    final Pattern pattern;
    final IElementType tokenType;

    Token(String pattern, String id, LivePreviewLanguage language) {
      String tokenName;
      if (pattern.startsWith("regexp:")) {
        String patternText = pattern.substring("regexp:".length());
        this.pattern = ParserGeneratorUtil.compilePattern(patternText);
        tokenName = id;
      }
      else {
        this.pattern = ParserGeneratorUtil.compilePattern(StringUtil.escapeToRegexp(pattern));
        tokenName = pattern;
      }
      //noinspection EmptyClass
      if (StringUtil.endsWithIgnoreCase(tokenName, "comment")) tokenType = LivePreviewParserDefinition.COMMENT;
      else if (StringUtil.endsWithIgnoreCase(tokenName, "string")) tokenType = LivePreviewParserDefinition.STRING;
      else if (StringUtil.endsWithIgnoreCase(tokenName, "number")) tokenType = LivePreviewParserDefinition.NUMBER;
      else tokenType = new IElementType(tokenName, language, false) {};
    }

    Token(String pattern, IElementType tokenType) {
      this.pattern = ParserGeneratorUtil.compilePattern(pattern);
      this.tokenType = tokenType;
    }
  }

  @NotNull
  public static Map<String, String> collectTokenPattern2Name(@Nullable final BnfFile file) {
    if (file == null) return Collections.emptyMap();
    final Map<String, String> map = new LinkedHashMap<String, String>();
    List<Pair<String, String>> tokenAttr = file.findAttributeValue(null, KnownAttribute.TOKENS, null);
    for (Pair<String, String> pair : tokenAttr) {
      if (pair.first == null || pair.second == null) continue;
      map.put(pair.second, pair.first);
    }
    GrammarUtil.visitRecursively(file, true, new BnfVisitor() {
      @Override
      public void visitStringLiteralExpression(@NotNull BnfStringLiteralExpression o) {
        String text = o.getText();
        String tokenText = StringUtil.stripQuotesAroundValue(text);
        for (String name : map.keySet()) {
          if (tokenText.equals(map.get(name))) return;
        }
      }

      @Override
      public void visitReferenceOrToken(@NotNull BnfReferenceOrToken o) {
        if (GrammarUtil.isExternalReference(o)) return;
        String text = o.getText();
        BnfRule rule = file.getRule(text);
        if (rule != null) return;
        if (!map.containsValue(text)) map.put(text, text);
      }
    });
    for (Pair<String, String> pair : tokenAttr) {
      if (pair.first == null || pair.second == null) continue;
      map.remove(pair.second);
      map.put(pair.second, pair.first);
    }

    return map;
  }
}
