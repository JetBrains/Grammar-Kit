/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.psi;

import com.intellij.psi.PsiFile;
import org.intellij.grammar.KnownAttribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author gregsh
 */
public interface BnfFile extends PsiFile {
  @NotNull
  List<BnfRule> getRules();

  @NotNull
  List<BnfAttrs> getAttributes();

  @Nullable
  BnfRule getRule(@Nullable String ruleName);

  @Nullable
  BnfAttr findAttribute(@Nullable BnfRule rule, @NotNull KnownAttribute<?> knownAttribute, @Nullable String match);

  @Nullable
  <T> T findAttributeValue(@Nullable BnfRule rule, @NotNull KnownAttribute<T> knownAttribute, @Nullable String match);
}
