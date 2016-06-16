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
package org.intellij.grammar.parser;

import com.intellij.lang.PsiBuilder;

/**
 * @author gregsh
 */
public class GrammarParserUtil extends GeneratedParserUtilBase {
  public static boolean parseGrammar(PsiBuilder builder_, int level, Parser parser) {
    ErrorState state = ErrorState.get(builder_);
    return parseAsTree(state, builder_, level, DUMMY_BLOCK, true, parser, TRUE_CONDITION);
  }
}
