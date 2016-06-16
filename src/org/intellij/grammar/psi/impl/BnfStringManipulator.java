/*
 * Copyright 2011-present Greg Shrago
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

package org.intellij.grammar.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;

/**
 * @author gregsh
 */
public class BnfStringManipulator extends AbstractElementManipulator<BnfStringImpl> {
  @Override
  public BnfStringImpl handleContentChange(BnfStringImpl psi, TextRange range, String newContent) throws IncorrectOperationException {
    final String oldText = psi.getText();
    final String newText = oldText.substring(0, range.getStartOffset()) + newContent + oldText.substring(range.getEndOffset());
    return psi.updateText(newText);
  }

  @Override
  public TextRange getRangeInElement(final BnfStringImpl element) {
    return getStringTokenRange(element);
  }

  public static TextRange getStringTokenRange(final BnfStringImpl element) {
    return TextRange.from(1, element.getTextLength()-2);
  }
}
