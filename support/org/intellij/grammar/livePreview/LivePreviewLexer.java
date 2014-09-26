/*
 * Copyright 2011-2014 Gregory Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.intellij.grammar.livePreview;

import com.intellij.lang.Language;
import com.intellij.lexer.LexerBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
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

    myTokens = bnfFile == null? new Token[0] : CachedValuesManager.getCachedValue(bnfFile, new CachedValueProvider<Token[]>() {
      @Nullable
      @Override
      public Result<Token[]> compute() {
        Map<String, String> map = ContainerUtil.newLinkedHashMap();
        Set<String> usedInGrammar = ContainerUtil.newLinkedHashSet();
        collectTokenPattern2Name(bnfFile, map, usedInGrammar);

        Token[] tokens = new Token[map.size()];
        int i = 0;
        String tokenConstantPrefix = getRootAttribute(bnfFile, KnownAttribute.ELEMENT_TYPE_PREFIX);
        for (String pattern : map.keySet()) {
          String tokenName = map.get(pattern);

          tokens[i++] = new Token(pattern, tokenName, usedInGrammar.contains(tokenName), tokenConstantPrefix, language);
        }
        return Result.create(tokens, bnfFile);
      }
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
      constantName = constantPrefix + mappedName.toUpperCase(Locale.ENGLISH);
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
        tokenType = new KeywordTokenType(tokenName, language);
      }
      else if (delegate == TokenType.WHITE_SPACE || delegate == COMMENT) {
        tokenType = delegate; // PreviewTokenType(tokenName, language, delegate);
      }
      else {
        tokenType = new PreviewTokenType(tokenName, language, delegate);
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
        return TokenType.WHITE_SPACE;
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
  public static Map<String, String> collectTokenPattern2Name(@Nullable final BnfFile file) {
    if (file == null) return Collections.emptyMap();

    Map<String, String> map = ContainerUtil.newLinkedHashMap();
    collectTokenPattern2Name(file, map, ContainerUtil.<String>newLinkedHashSet());
    return map;
  }

  private static void collectTokenPattern2Name(@NotNull final BnfFile file, final Map<String, String> map, final Set<String> usedInGrammar) {
    final Map<String, String> origTokens = RuleGraphHelper.getTokenMap(file);
    final Pattern pattern = ParserGeneratorUtil.getAllTokenPattern(origTokens);
    final int[] autoCount = {0};

    GrammarUtil.visitRecursively(file, true, new BnfVisitor() {
      @Override
      public void visitStringLiteralExpression(@NotNull BnfStringLiteralExpression o) {
        String text = o.getText();
        String tokenText = StringUtil.stripQuotesAroundValue(text);
        // add auto-XXX token for all unmatched strings to avoid BAD_CHARACTERs
        if (!map.values().contains(tokenText) &&
            !StringUtil.isJavaIdentifier(tokenText) &&
            (pattern == null || !pattern.matcher(tokenText).matches())) {
          String tokenName = "_AUTO_" + (autoCount[0]++);
          usedInGrammar.add(text);
          map.put(tokenText, tokenName);
        }
        else {
          ContainerUtil.addIfNotNull(usedInGrammar, origTokens.get(tokenText));
        }
      }

      @Override
      public void visitReferenceOrToken(@NotNull BnfReferenceOrToken o) {
        if (GrammarUtil.isExternalReference(o)) return;
        String text = o.getText();
        BnfRule rule = file.getRule(text);
        if (rule != null) return;
        usedInGrammar.add(text);
        if (!map.containsValue(text)) map.put(text, text);
      }
    });
    // fix ordering: origTokens _after_ to handle keywords correctly
    for (String tokenText : origTokens.keySet()) {
      String tokenName = origTokens.get(tokenText);
      map.remove(tokenText);
      map.put(tokenText, tokenName != null ? tokenName : "_AUTO_" + (autoCount[0]++));
    }
  }

  static class PreviewTokenType extends IElementType {
    final IElementType delegate;

    PreviewTokenType(String name, Language language, IElementType delegate) {
      super(name, language, false);
      this.delegate = ObjectUtils.chooseNotNull(delegate, this);
    }
  }

  static class KeywordTokenType extends PreviewTokenType {
    KeywordTokenType(String name, Language language) {
      super(name, language, KEYWORD);
    }
  }
}
