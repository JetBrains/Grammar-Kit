/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
