/*
 * Copyright 2011-2011 Gregory Shrago
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
package org.intellij.grammar.parser;

import com.intellij.lang.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.TokenType;
import com.intellij.psi.impl.source.resolve.FileContextUtil;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.ICompositeElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.containers.LimitedPool;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * @author gregsh
 */
@SuppressWarnings("StringEquality")
public class GeneratedParserUtilBase {

  private static final Logger LOG = Logger.getInstance("org.intellij.grammar.parser.GeneratedParserUtilBase");
  
  public static final IElementType DUMMY_BLOCK = new DummyBlockElementType();

  public interface Parser {
    boolean parse(PsiBuilder builder);
  }

  public static final Parser TOKEN_ADVANCER = new Parser() {
    @Override
    public boolean parse(PsiBuilder builder) {
      if (builder.eof()) return false;
      builder.advanceLexer();
      return true;
    }
  };

  public static final Parser TRUE_CONDITION = new Parser() {
    @Override
    public boolean parse(PsiBuilder builder) {
      return true;
    }
  };

  public static boolean recursion_guard_(PsiBuilder builder_, int level_, String funcName_) {
    if (level_ > 100) {
      builder_.error("Maximum recursion level (" + 100 + ") reached in" + funcName_);
      return false;
    }
    return true;
  }

  public static boolean consumeToken(PsiBuilder builder, IElementType token) {
    ErrorState state = ErrorState.get(builder);
    IElementType tokenType = builder.getTokenType();
    if (!state.suppressErrors) {
      addVariant(state, builder, getTokenDescription(token));
    }
    if (token == tokenType) {
      builder.advanceLexer();
      return true;
    }
    return false;
  }

  private static String getTokenDescription(IElementType token) {
    return '\'' + token.toString() +'\'';
  }

  public static void addVariant(PsiBuilder builder, String text) {
    addVariant(ErrorState.get(builder), builder, text);
  }

  private static void addVariant(ErrorState state, PsiBuilder builder, String text) {
    final int offset = builder.getCurrentOffset();
    final Iterator<Variant> it = state.variants.descendingIterator();
    while (it.hasNext()) {
      Variant v = it.next();
      if (v.text.equals(text) && state.predicateSign == v.expected && offset == v.offset) return;
      if (v.offset < offset) break;
    }
    Variant variant = state.VARIANTS.alloc();
    variant.init(offset, text, state.predicateSign);
    state.variants.add(variant);
    CompletionState completionState = state.completionState;
    if (completionState != null && state.predicateSign) {
      addCompletionVariant(state, completionState, builder, text, offset);
    }
  }

  public static boolean consumeToken(PsiBuilder builder, String text) {
    ErrorState state = ErrorState.get(builder);
    final CharSequence sequence = builder.getOriginalText();
    final int offset = builder.getCurrentOffset();
    final int endOffset = offset + text.length();
    CharSequence tokenText = sequence.subSequence(offset, Math.min(endOffset, sequence.length()));
    if (!state.suppressErrors) {
      addVariant(state, builder, text);
    }
    if (text.equals(tokenText)) {
      int count = 0;
      while (true) {
        final int nextOffset = builder.rawTokenTypeStart(++ count);
        if (nextOffset > endOffset) {
          return false;
        }
        else if (nextOffset == endOffset) {
          break;
        }
      }
      while (count-- > 0) builder.advanceLexer();
      return true;
    }
    return false;
  }

  private static void addCompletionVariant(ErrorState state,
                                           CompletionState completionState,
                                           PsiBuilder builder,
                                           String text,
                                           int offset) {
    boolean add = false;
    int diff = completionState.offset - offset;
    int length = text.length();
    if (diff == 0) {
      add = true;
    }
    else if (diff > 0 && diff <= length) {
      CharSequence fragment = builder.getOriginalText().subSequence(offset, completionState.offset);
      add = StringUtil.startsWithIgnoreCase(StringUtil.unquoteString(text), fragment.toString());
    }
    else if (diff < 0) {
      for (int i=-1; ; i--) {
        IElementType type = builder.rawLookup(i);
        int tokenStart = builder.rawTokenTypeStart(i);
        if (state.whitespaceTokens.contains(type) || state.commentTokens.contains(type)) {
          diff = completionState.offset - tokenStart;
        }
        else if (/*type == ID && */ tokenStart < completionState.offset) {
          CharSequence fragment = builder.getOriginalText().subSequence(tokenStart, completionState.offset);
          if (StringUtil.startsWithIgnoreCase(StringUtil.unquoteString(text), fragment.toString())) {
            diff = completionState.offset - tokenStart;
          }
          break;
        }
        else break;
      }
      add = diff >= 0 && diff < length;
    }
    add = add && length > 1 && !(text.charAt(0) == '<' && text.charAt(length - 1) == '>') &&
          !(text.charAt(0) == '\'' && text.charAt(length - 1) == '\'' && length < 5);
    if (add) {
      completionState.items.add(StringUtil.unquoteString(text));
    }
  }


  public static final String _SECTION_NOT_ = "_SECTION_NOT_";
  public static final String _SECTION_AND_ = "_SECTION_AND_";
  public static final String _SECTION_RECOVER_ = "_SECTION_RECOVER_";
  public static final String _SECTION_GENERAL_ = "_SECTION_GENERAL_";

  public static void enterErrorRecordingSection(PsiBuilder builder_, int key, @NotNull String sectionType) {
    ErrorState state = ErrorState.get(builder_);
    state.levelCheck.add(new Frame(builder_.getCurrentOffset(), key, sectionType));
    if (sectionType == _SECTION_AND_) {
      if (state.predicateCount == 0 && !state.predicateSign) {
        throw new AssertionError("Incorrect false predicate sign");
      }
      state.predicateCount++;
    }
    else if (sectionType == _SECTION_NOT_) {
      if (state.predicateCount == 0) {
        state.predicateSign = false;
      }
      else {
        state.predicateSign = !state.predicateSign;
      }
      state.predicateCount++;
    }
  }

  public static boolean exitErrorRecordingSection(PsiBuilder builder_,
                                                  boolean result,
                                                  int key,
                                                  boolean pinned,
                                                  @NotNull String sectionType,
                                                  @Nullable Parser eatMore) {
    ErrorState state = ErrorState.get(builder_);

    Frame frame = null;
    if (state.levelCheck.isEmpty() || key != (frame = state.levelCheck.removeLast()).level || !sectionType.equals(frame.section)) {
      LOG.error("Unbalanced error section: got " + new Frame(builder_.getCurrentOffset(), key, sectionType) + ", expected " + frame);
      return result;
    }
    if (sectionType == _SECTION_AND_ || sectionType == _SECTION_NOT_) {
      state.predicateCount--;
      if (sectionType == _SECTION_NOT_) state.predicateSign = !state.predicateSign;
      return result;
    }
    if (sectionType == _SECTION_RECOVER_ && !state.suppressErrors && eatMore != null) {
      state.suppressErrors = true;
      final LighterASTNode latestDoneMarker = result || pinned ? builder_.getLatestDoneMarker() : null;
      PsiBuilder.Marker extensionMarker = null;
      IElementType extensionTokenType = null;
      try {
        if (latestDoneMarker instanceof PsiBuilder.Marker) {
          extensionMarker = ((PsiBuilder.Marker)latestDoneMarker).precede();
          extensionTokenType = latestDoneMarker.getTokenType();
          ((PsiBuilder.Marker)latestDoneMarker).drop();
        }
        final boolean eatMoreFlagOnce =
          !builder_.eof() && (eatMore.parse(builder_) || state.braces != null && builder_.rawLookup(-1) == state.braces[0].getLeftBraceType()
                                                         && builder_.getTokenType() == state.braces[0] .getRightBraceType());
        final int lastErrorPos = state.variants.isEmpty()? builder_.getCurrentOffset() : state.variants.last().offset;
        boolean eatMoreFlag = eatMoreFlagOnce || frame.offset == builder_.getCurrentOffset() && lastErrorPos > frame.offset;
        // advance to the last error pos
        // skip tokens until lastErrorPos. parseAsTree might look better here...
        int parenCount = 0;
        while (eatMoreFlag && builder_.getCurrentOffset() < lastErrorPos) {
          if (state.braces != null) {
            if (builder_.getTokenType() == state.braces[0].getLeftBraceType()) parenCount ++;
            else if (builder_.getTokenType() == state.braces[0].getRightBraceType()) parenCount --;
          }
          builder_.advanceLexer();
          eatMoreFlag = parenCount != 0 || eatMore.parse(builder_);
        }
        if (eatMoreFlag) {
          String tokenText = builder_.getTokenText();
          String expectedText = state.getExpectedText(builder_);
          // todo we could have already reported the error, can we try to drop it and recreate here?
          PsiBuilder.Marker mark = builder_.mark();
          try {
            builder_.advanceLexer();
          }
          finally {
            mark.error(expectedText + "got '" + tokenText + "'");
          }
          parseAsTree(state, builder_, DUMMY_BLOCK, true, TOKEN_ADVANCER, eatMore);
        }
        else if ((!result || eatMoreFlagOnce) && frame.offset != builder_.getCurrentOffset()) {
          reportError(state, builder_, true);
        }
      }
      finally {
        if (extensionMarker != null) {
          extensionMarker.done(extensionTokenType);
        }
        state.suppressErrors = false;
        state.clearExpectedVariants();
      }
    }
    else if (!result && pinned) {
      // do not report if there're errors after current offset
      if (state.variants.isEmpty() || state.variants.last().offset <= builder_.getCurrentOffset()) {
        // do not force, inner recoverRoot might have skipped some tokens
        reportError(state, builder_, false);
      }
    }
    return result;
  }

  private static void reportError(ErrorState state, PsiBuilder builder_, boolean force) {
    String expectedText = state.getExpectedText(builder_);
    boolean notEmpty = StringUtil.isNotEmpty(expectedText);
    if (force || notEmpty) {
      final String gotText = builder_.eof()? "unexpected end of file" :
                             notEmpty? "got '" + builder_.getTokenText() +"'" :
                             "'" + builder_.getTokenText() +"' unexpected";
      builder_.error(expectedText + gotText);
    }
  }


  private static final Key<ErrorState> ERROR_STATE_KEY = Key.create("ERROR_STATE_KEY");
  public static final Key<CompletionState> COMPLETION_STATE_KEY = Key.create("COMPLETION_STATE_KEY");

  public static class CompletionState {
    public final int offset;
    public final Collection<String> items = new THashSet<String>();

    public CompletionState(int offset) {
      this.offset = offset;
    }
  }

  public static class ErrorState {
    int predicateCount;
    boolean predicateSign = true;
    boolean suppressErrors;
    final LinkedList<Frame> levelCheck = new LinkedList<Frame>();
    CompletionState completionState;
    
    private BracePair[] braces;
    private TokenSet whitespaceTokens = TokenSet.EMPTY;
    private TokenSet commentTokens = TokenSet.EMPTY;
    
    TreeSet<Variant> variants = new TreeSet<Variant>();
    final LimitedPool<Variant> VARIANTS = new LimitedPool<Variant>(2000, new LimitedPool.ObjectFactory<Variant>() {
      public Variant create() {
        return new Variant();
      }

      public void cleanup(final Variant v) {
        v.init(0, null, false);
      }
    });


    public static ErrorState get(PsiBuilder builder) {
      ErrorState state = builder.getUserDataUnprotected(ERROR_STATE_KEY);
      if (state == null) {
        builder.putUserDataUnprotected(ERROR_STATE_KEY, state = new ErrorState());
        PsiFile file = builder.getUserDataUnprotected(FileContextUtil.CONTAINING_FILE_KEY);
        state.completionState = file == null? null: file.getUserData(COMPLETION_STATE_KEY);
        if (file != null) {
          Language language = file.getLanguage();
          PairedBraceMatcher matcher = LanguageBraceMatching.INSTANCE.forLanguage(language);
          state.braces = matcher == null? null : matcher.getPairs();
          if (state.braces != null && state.braces.length == 0) state.braces = null;
          ParserDefinition parserDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(language);
          if (parserDefinition != null) {
            state.commentTokens = parserDefinition.getCommentTokens();
            state.whitespaceTokens = parserDefinition.getWhitespaceTokens();
          }
        }
      }
      return state;
    }

    public String getExpectedText(PsiBuilder builder) {
      int offset = builder.getCurrentOffset();
      StringBuilder sb = new StringBuilder();
      if (addExpected(sb, offset, true)) {
        sb.append(" expected, ");
      }
      else if (addExpected(sb, offset, false)) sb.append(" unexpected, ");
      return sb.toString();
    }

    private boolean addExpected(StringBuilder sb, int offset, boolean expected) {
      int count = 0;
      for (Variant variant : variants) {
        if (offset == variant.offset) {
          if (variant.expected != expected) continue;
          if (count++ > 0) sb.append(", ");
          sb.append(variant.text);
        }
      }
      if (count > 1) {
        int idx = sb.lastIndexOf(", ");
        sb.replace(idx, idx + 1, " or");
      }
      return count > 0;
    }

    void clearExpectedVariants() {
      for (Variant v : variants) {
        VARIANTS.recycle(v);
      }
      variants.clear();
    }
  }

  public static class Frame {
    int offset;
    int level;
    String section;

    public Frame(int offset, int level, String section) {
      this.offset = offset;
      this.level = level;
      this.section = section;
    }

    @Override
    public String toString() {
      return "<"+offset+", "+section+", "+level+">";
    }
  }


  public static class Variant implements Comparable<Variant>{
    int offset;
    String text;
    boolean expected;

    public void init(int offset, String text, boolean expected) {
      this.offset = offset;
      this.text = text;
      this.expected = expected;
    }

    @Override
    public String toString() {
      return "<" + offset + ", " + expected + ", " + text + ">";
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Variant variant = (Variant)o;

      if (expected != variant.expected) return false;
      if (offset != variant.offset) return false;
      if (!text.equals(variant.text)) return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result = offset;
      result = 31 * result + text.hashCode();
      result = 31 * result + (expected ? 1 : 0);
      return result;
    }

    @Override
    public int compareTo(Variant o) {
      int diff = offset - o.offset;
      if (diff == 0) diff = text.compareTo(o.text);
      if (diff == 0) diff = (expected?1:0) - (o.expected?1:0);
      return diff;
    }
  }

  @Nullable
  private static IElementType getClosingBracket(ErrorState state, IElementType type) {
    if (state.braces == null) return null;
    for (BracePair pair : state.braces) {
      if (type == pair.getLeftBraceType()) return pair.getRightBraceType();
    }
    return null;
  }


  private static final int MAX_CHILDREN_IN_TREE = 10;
  public static boolean parseAsTree(ErrorState state, final PsiBuilder builder, final IElementType chunkType,
                                    boolean checkBraces, final Parser parser, final Parser eatMoreCondition) {
    final LinkedList<Pair<PsiBuilder.Marker, PsiBuilder.Marker>> parenList = new LinkedList<Pair<PsiBuilder.Marker, PsiBuilder.Marker>>();
    final LinkedList<Pair<PsiBuilder.Marker, Integer>> siblingList = new LinkedList<Pair<PsiBuilder.Marker, Integer>>();
    PsiBuilder.Marker marker = null;

    final Runnable checkSiblingsRunnable = new Runnable() {
      public void run() {
        main:
        while (!siblingList.isEmpty()) {
          final Pair<PsiBuilder.Marker, PsiBuilder.Marker> parenPair = parenList.peek();
          final int rating = siblingList.getFirst().second;
          int count = 0;
          for (Pair<PsiBuilder.Marker, Integer> pair : siblingList) {
            if (pair.second != rating || parenPair != null && pair.first == parenPair.second) break main;
            if (++count >= MAX_CHILDREN_IN_TREE) {
              final PsiBuilder.Marker parentMarker = pair.first.precede();
              while (count-- > 0) {
                siblingList.removeFirst();
              }
              parentMarker.done(chunkType);
              siblingList.addFirst(Pair.create(parentMarker, rating + 1));
              continue main;
            }
          }
          break;
        }
      }
    };
    boolean checkParens = state.braces != null && checkBraces;
    int totalCount = 0;
    try {
      int tokenCount = 0;
      if (checkParens && builder.rawLookup(-1) == state.braces[0].getLeftBraceType()) {
        LighterASTNode doneMarker = builder.getLatestDoneMarker();
        if (doneMarker != null && doneMarker.getStartOffset() == builder.rawTokenTypeStart(-1) && doneMarker.getTokenType() == TokenType.ERROR_ELEMENT) {
          parenList.add(Pair.create(((PsiBuilder.Marker)doneMarker).precede(), (PsiBuilder.Marker)null));
        }
      }
      while (true) {
        final IElementType tokenType = builder.getTokenType();
        if (checkParens && (tokenType == state.braces[0].getLeftBraceType() || tokenType == state.braces[0].getRightBraceType() && !parenList.isEmpty())) {
          if (marker != null) {
            marker.done(chunkType);
            siblingList.addFirst(Pair.create(marker, 1));
            marker = null;
            tokenCount = 0;
          }
          if (tokenType == state.braces[0].getLeftBraceType()) {
            final Pair<PsiBuilder.Marker, Integer> prev = siblingList.peek();
            parenList.addFirst(Pair.create(builder.mark(), prev == null ? null : prev.first));
          }
          checkSiblingsRunnable.run();
          builder.advanceLexer();
          if (tokenType == state.braces[0].getRightBraceType()) {
            final Pair<PsiBuilder.Marker, PsiBuilder.Marker> pair = parenList.removeFirst();
            pair.first.done(chunkType);
            // drop all markers inside parens
            while (!siblingList.isEmpty() && siblingList.getFirst().first != pair.second) {
              siblingList.removeFirst();
            }
            siblingList.addFirst(Pair.create(pair.first, 1));
            checkSiblingsRunnable.run();
          }
        }
        else if (tokenType != null) {
          if (marker == null) {
            marker = builder.mark();
          }
          final boolean result = eatMoreCondition.parse(builder) && parser.parse(builder);
          if (result) {
            tokenCount++;
            totalCount++;
          }
          else break;
        }
        else break;

        if (tokenCount >= MAX_CHILDREN_IN_TREE) {
          marker.done(chunkType);
          siblingList.addFirst(Pair.create(marker, 1));
          checkSiblingsRunnable.run();
          marker = null;
          tokenCount = 0;
        }
      }
    }
    finally {
      if (marker != null) {
        marker.drop();
      }
      for (Pair<PsiBuilder.Marker, PsiBuilder.Marker> pair : parenList) {
        pair.first.drop();
      }
    }
    return totalCount != 0;
  }

  private static class DummyBlockElementType extends IElementType implements ICompositeElementType{
    public DummyBlockElementType() {
      super("DUMMY_BLOCK", Language.ANY);
    }

    @NotNull
    @Override
    public ASTNode createCompositeNode() {
      return new DummyBlock();
    }
  }

  public static class DummyBlock extends CompositePsiElement {
    public DummyBlock() {
      super(DUMMY_BLOCK);
    }

    @Override
    public PsiReference[] getReferences() {
      return PsiReference.EMPTY_ARRAY;
    }

    @Override
    public boolean canNavigateToSource() {
      return false;
    }

    @Override
    public boolean canNavigate() {
      return false;
    }
  }
}
