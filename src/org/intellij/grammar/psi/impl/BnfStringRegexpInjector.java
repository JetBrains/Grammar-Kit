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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.intellij.grammar.config.Options;
import org.intellij.lang.regexp.RegExpLanguage;
import org.jetbrains.annotations.NotNull;

import static org.intellij.grammar.generator.BnfConstants.REGEXP_PREFIX;

/**
 * @author gregsh
 */
public class BnfStringRegexpInjector implements LanguageInjector {
  @Override
  public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces places) {
    if (!(host instanceof BnfStringImpl)) return;
    if (!Options.BNF_INJECT_REGEXP_IN_BNF.get()) return;

    BnfStringImpl bnfString = (BnfStringImpl)host;
    String text = StringUtil.stripQuotesAroundValue(bnfString.getString().getText());
    if (!text.startsWith(REGEXP_PREFIX)) return;
    places.addPlace(RegExpLanguage.INSTANCE, TextRange.create(REGEXP_PREFIX.length(), text.length()).shiftRight(1), null, null);
  }
}
