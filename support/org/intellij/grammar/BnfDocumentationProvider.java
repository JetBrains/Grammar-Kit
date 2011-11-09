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

package org.intellij.grammar;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author gregsh
 */
public class BnfDocumentationProvider implements DocumentationProvider {

  @Nullable
  public String getQuickNavigateInfo(final PsiElement element, PsiElement originalElement) {
    return null;
  }

  @Nullable
  public List<String> getUrlFor(final PsiElement element, final PsiElement originalElement) {
    return null;
  }

  @Nullable
  public String generateDoc(final PsiElement element, final PsiElement originalElement) {
    if (element instanceof BnfRule) {
      BnfExpression expression = ((BnfRule)element).getExpression();

      Set<String> first = BnfFirstNextAnalyzer.calcFirst(expression);
      Set<String> next = BnfFirstNextAnalyzer.calcNext(expression);

      boolean hasNull = first.remove(BnfFirstNextAnalyzer.EMPTY_STRING);
      if (hasNull) first.add("<eof>");
      boolean hasNull2 = next.remove(BnfFirstNextAnalyzer.EMPTY_STRING);
      if (hasNull2) next.add("<eof>");
      String[] firstS = first.toArray(new String[first.size()]);
      Arrays.sort(firstS);
      String[] nextS = next.toArray(new String[next.size()]);
      Arrays.sort(nextS);
      return "<h1>Starts with:</h1>" + StringUtil.escapeXml(StringUtil.join(firstS, " | "))
        + "<br><h1>Followed by:</h1>" + StringUtil.escapeXml(StringUtil.join(nextS, " | "));
    }
    return null;
  }

  @Nullable
  public PsiElement getDocumentationElementForLookupItem(final PsiManager psiManager, final Object object, final PsiElement element) {
    return null;
  }

  @Nullable
  public PsiElement getDocumentationElementForLink(final PsiManager psiManager, final String link, final PsiElement context) {
    return null;
  }
}
