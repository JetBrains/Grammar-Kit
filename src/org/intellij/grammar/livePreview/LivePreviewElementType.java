/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.livePreview;

import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ObjectUtils;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static org.intellij.grammar.livePreview.LivePreviewParserDefinition.KEYWORD;

/**
 * @author gregsh
 */
public class LivePreviewElementType extends IElementType {

  public LivePreviewElementType(@NotNull String debugName, @NotNull LivePreviewLanguage language) {
    super(debugName, language, false);
  }

  @Override
  public @NotNull LivePreviewLanguage getLanguage() {
    return (LivePreviewLanguage)super.getLanguage();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LivePreviewElementType t = (LivePreviewElementType)o;

    return Objects.equals(toString(), o.toString()) && getLanguage() == t.getLanguage();
  }

  @Override
  public int hashCode() {
    return 31 * toString().hashCode() + getLanguage().hashCode();
  }



  public static class TokenType extends LivePreviewElementType {
    final IElementType delegate;

    TokenType(@Nullable IElementType delegate, @NotNull String name, @NotNull LivePreviewLanguage language) {
      super(name, language);
      this.delegate = ObjectUtils.chooseNotNull(delegate, this);
    }
  }

  public static class KeywordType extends TokenType {
    KeywordType(@NotNull String name, @NotNull LivePreviewLanguage language) {
      super(KEYWORD, name, language);
    }
  }

  public static class RuleType extends LivePreviewElementType {
    final String ruleName;

    RuleType(@NotNull String elementType, @NotNull BnfRule rule, @NotNull LivePreviewLanguage language) {
      super(elementType, language);
      ruleName = rule.getName();
    }

    public @Nullable BnfRule getRule(Project project) {
      BnfFile file = getLanguage().getGrammar(project);
      return file != null ? file.getRule(ruleName) : null;
    }

  }
}
