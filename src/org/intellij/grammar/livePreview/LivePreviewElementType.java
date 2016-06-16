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

package org.intellij.grammar.livePreview;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ObjectUtils;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.intellij.grammar.livePreview.LivePreviewParserDefinition.KEYWORD;

/**
 * @author gregsh
 */
public class LivePreviewElementType extends IElementType {

  public LivePreviewElementType(@NotNull String debugName, @NotNull LivePreviewLanguage language) {
    super(debugName, language, false);
  }

  @NotNull
  @Override
  public LivePreviewLanguage getLanguage() {
    return (LivePreviewLanguage)super.getLanguage();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LivePreviewElementType t = (LivePreviewElementType)o;

    return Comparing.equal(toString(), o.toString()) && getLanguage() == t.getLanguage();
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

    @Nullable
    public BnfRule getRule(Project project) {
      BnfFile file = getLanguage().getGrammar(project);
      return file != null ? file.getRule(ruleName) : null;
    }

  }
}
