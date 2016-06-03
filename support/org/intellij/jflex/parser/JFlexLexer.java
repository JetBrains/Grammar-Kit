/*
 * Copyright 2011-2016 Gregory Shrago
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

package org.intellij.jflex.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * TODO IDEA 15 compatibility: extend FlexAdapter
 * Workaround com.intellij.lexer.FlexAdapter#getTokenStart()
 *
 * @author gregsh
 */
public class JFlexLexer extends LexerBase {
  private final FlexLexer myFlex;
  private IElementType myTokenType;
  private CharSequence myText;

  private int myStart;
  private int myEnd;
  private int myState;

  public JFlexLexer() {
    myFlex = new _JFlexLexer();
  }

  public FlexLexer getFlex() {
    return myFlex;
  }

  @Override
  public void start(@NotNull final CharSequence buffer, int startOffset, int endOffset, final int initialState) {
    myText = buffer;
    myEnd = endOffset;
    myFlex.reset(myText, startOffset, endOffset, initialState);
    myTokenType = null;
  }

  @Override
  public int getState() {
    if (myTokenType == null) locateToken();
    return myState;
  }

  @Override
  public IElementType getTokenType() {
    if (myTokenType == null) locateToken();
    return myTokenType;
  }

  @Override
  public int getTokenStart() {
    if (myTokenType == null) locateToken();
    return myStart;
  }

  @Override
  public int getTokenEnd() {
    if (myTokenType == null) locateToken();
    return myFlex.getTokenEnd();
  }

  @Override
  public void advance() {
    if (myTokenType == null) locateToken();
    myTokenType = null;
  }

  @NotNull
  @Override
  public CharSequence getBufferSequence() {
    return myText;
  }

  @Override
  public int getBufferEnd() {
    return myEnd;
  }

  protected void locateToken() {
    if (myTokenType != null) return;
    try {
      myState = myFlex.yystate();
      myStart = myFlex.getTokenEnd();
      myTokenType = myFlex.advance();
    }
    catch (IOException e) { /*Can't happen*/ }
    catch (Error e) {
      // add lexer class name to the error
      final Error error = new Error(myFlex.getClass().getName() + ": " + e.getMessage());
      error.setStackTrace(e.getStackTrace());
      throw error;
    }
  }
}
