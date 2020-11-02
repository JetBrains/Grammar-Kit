/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.livePreview;

import com.intellij.lexer.LexerBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.Case;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;
import static org.intellij.grammar.livePreview.LivePreviewParserDefinition.*;

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

    myTokens = bnfFile == null? new Token[0] : CachedValuesManager.getCachedValue(bnfFile, () -> {
      Set<String> usedInGrammar = new LinkedHashSet<>();
      Map<String, String> map = collectTokenPattern2Name(bnfFile, usedInGrammar);

      Token[] tokens = new Token[map.size()];
      int i = 0;
      String tokenConstantPrefix = getRootAttribute(bnfFile, KnownAttribute.ELEMENT_TYPE_PREFIX);
      for (String pattern : map.keySet()) {
        String tokenName = map.get(pattern);

        tokens[i++] = new Token(pattern, tokenName, usedInGrammar.contains(tokenName), tokenConstantPrefix, language);
      }
      return CachedValueProvider.Result.create(tokens, bnfFile);
    });
  }

  @Override
  public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
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
      myTokenType = com.intellij.psi.TokenType.BAD_CHARACTER;
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
    if (myTokenType == null && myPosition != myEndOffset) {
      nextToken();
      assert false : "not lexed: '" + myBuffer.subSequence(myPosition, myEndOffset) + "'";
    }
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

  @NotNull
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

    final String constantName;
    final Pattern pattern;
    final IElementType tokenType;

    Token(String pattern, String mappedName, boolean usedInGrammar, String constantPrefix, LivePreviewLanguage language) {
      constantName = constantPrefix + Case.UPPER.apply(mappedName);
      String tokenName;
      boolean keyword;
      if (ParserGeneratorUtil.isRegexpToken(pattern)) {
        String patternText = ParserGeneratorUtil.getRegexpTokenRegexp(pattern);
        this.pattern = ParserGeneratorUtil.compilePattern(patternText);
        tokenName = mappedName;
        keyword = false;
      }
      else {
        this.pattern = ParserGeneratorUtil.compilePattern(StringUtil.escapeToRegexp(pattern));
        tokenName = pattern;
        keyword = StringUtil.isJavaIdentifier(pattern);
      }

      IElementType delegate = keyword ? null : guessDelegateType(tokenName, this.pattern, usedInGrammar);
      if (keyword) {
        tokenType = new LivePreviewElementType.KeywordType(tokenName, language);
      }
      else if (delegate == com.intellij.psi.TokenType.WHITE_SPACE || delegate == COMMENT) {
        tokenType = delegate; // PreviewTokenType(tokenName, language, delegate);
      }
      else {
        tokenType = new LivePreviewElementType.TokenType(delegate, tokenName, language);
      }
    }

    @Override
    public String toString() {
      return "Token{" +
             constantName +
             ", pattern=" + pattern +
             ", tokenType=" + tokenType +
             '}';
    }
  }

  @Nullable
  private static IElementType guessDelegateType(@NotNull String tokenName,
                                                @Nullable Pattern pattern,
                                                boolean usedInGrammar) {
    if (pattern != null) {
      if (!usedInGrammar && (pattern.matcher(" ").matches() || pattern.matcher("\n").matches())) {
        return com.intellij.psi.TokenType.WHITE_SPACE;
      }
      else if (pattern.matcher("1234").matches()) {
        return NUMBER;
      }
      else if (pattern.matcher("\"sdf\"").matches() || pattern.matcher("\'sdf\'").matches()) {
        return STRING;
      }
    }
    if (!usedInGrammar && StringUtil.endsWithIgnoreCase(tokenName, "comment")) {
      return COMMENT;
    }
    return null;
  }

  @NotNull
  public static Map<String, String> collectTokenPattern2Name(@NotNull BnfFile file, @Nullable Set<String> usedInGrammar) {
    return ParserGeneratorUtil.collectTokenPattern2Name(file, true, new LinkedHashMap<>(), usedInGrammar);
  }
}
