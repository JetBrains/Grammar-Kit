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

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.AtomicClearableLazyValue;
import com.intellij.openapi.util.ClearableLazyValue;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import gnu.trove.THashMap;
import org.intellij.grammar.BnfFileType;
import org.intellij.grammar.BnfLanguage;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * User: gregory
 * Date: 13.07.11
 * Time: 23:55
 */
public class BnfFileImpl extends PsiFileBase implements BnfFile {
  
  private final ClearableLazyValue<Map<String, BnfRule>> myRules = lazyValue(this::calcRules);
  private final ClearableLazyValue<List<BnfAttrs>> myGlobalAttributes = lazyValue(this::calcAttributes);
  private final ClearableLazyValue<Map<String, List<AttributeInfo>>> myAttributeValues = lazyValue(this::calcAttributeValues);

  public BnfFileImpl(FileViewProvider fileViewProvider) {
    super(fileViewProvider, BnfLanguage.INSTANCE);
  }

  @Override
  public void subtreeChanged() {
    super.subtreeChanged();
    myRules.drop();
    myGlobalAttributes.drop();
    myAttributeValues.drop();
  }

  @NotNull
  @Override
  public List<BnfRule> getRules() {
    return new ArrayList<>(myRules.getValue().values());
  }

  @Nullable
  @Override
  public BnfRule getRule(@Nullable String ruleName) {
    return ruleName == null ? null : myRules.getValue().get(ruleName);
  }

  @NotNull
  @Override
  public List<BnfAttrs> getAttributes() {
    return myGlobalAttributes.getValue();
  }

  @Override
  @Nullable
  public BnfAttr findAttribute(@Nullable BnfRule rule, @NotNull KnownAttribute<?> knownAttribute, @Nullable String match) {
    AttributeInfo result = getMatchingAttributes(rule, knownAttribute, match).first();
    if (result == null) return null;
    return PsiTreeUtil.getParentOfType(findElementAt(result.attrOffset), BnfAttr.class);
  }

  public <T> T findAttributeValue(@Nullable BnfRule rule, @NotNull KnownAttribute<T> knownAttribute, @Nullable String match) {
    T combined = null;
    boolean copied = false;
    for (AttributeInfo info : getMatchingAttributes(rule, knownAttribute, match)) {
      T cur = knownAttribute.ensureValue(info.value);
      if (combined != null && info.pattern == null) continue;
      if (knownAttribute == KnownAttribute.PIN && match != null &&
          info.pattern == null && !info.global && SUB_EXPRESSION.matcher(match).matches()) {
        // do not pin nested sequences for local pin=N
        return null;
      }
      if (!(cur instanceof KnownAttribute.ListValue)) {
        return cur;
      }
      if (combined == null) combined = cur;
      else if (copied) ((KnownAttribute.ListValue)combined).addAll((KnownAttribute.ListValue)cur);
      else {
        copied = true;
        KnownAttribute.ListValue copy = new KnownAttribute.ListValue();
        copy.addAll((KnownAttribute.ListValue)combined);
        copy.addAll((KnownAttribute.ListValue)cur);
        combined = (T)copy;
      }
    }
    return combined != null ? combined : knownAttribute.getDefaultValue();
  }

  private static final Pattern SUB_EXPRESSION = Pattern.compile(".*(_\\d+)+");

  @NotNull
  private <T> JBIterable<AttributeInfo> getMatchingAttributes(@Nullable BnfRule rule,
                                                              @NotNull KnownAttribute<T> knownAttribute,
                                                              @Nullable String match) {
    List<AttributeInfo> list = myAttributeValues.getValue().get(knownAttribute.getName());
    if (list == null) return JBIterable.empty();
    BnfAttrs globalAttrs = rule == null ? ContainerUtil.getFirstItem(getAttributes()) : null;
    int offset = rule == null ?
                 globalAttrs == null ? 0 :
                 globalAttrs.getTextRange().getEndOffset() :
                 rule.getTextRange().getEndOffset();
    if (offset == 0) return JBIterable.empty();
    AttributeInfo key = new AttributeInfo(0, offset, true, null, null);
    int index = Collections.binarySearch(list, key);
    int ruleStartOffset = rule == null ? offset : rule.getTextRange().getStartOffset();
    String toMatch = match == null ? rule == null ? null : rule.getName() : match;

    return JBIterable.generate(
      Math.min(list.size() - 1, index < 0 ? -index - 1 : index),
      i -> i > 0 ? i - 1 : null)
      .map(i -> {
        AttributeInfo info = list.get(i);
        if (offset < info.offset || !info.global && ruleStartOffset > info.offset) return null;
        if (info.pattern == null || toMatch != null && info.pattern.matcher(toMatch).matches()) {
          return info;
        }
        return null;
      }).filter(Conditions.notNull());
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return BnfFileType.INSTANCE;
  }

  @Override
  public String toString() {
    return "BnfFile:" + getName();
  }

  private Map<String, BnfRule> calcRules() {
    Map<String, BnfRule> result = new LinkedHashMap<>();
    for (BnfRule o : GrammarUtil.bnfTraverser(this).filter(BnfRule.class)) {
      if (!result.containsKey(o.getName())) {
        result.put(o.getName(), o);
      }
    }
    return result;
  }

  private List<BnfAttrs> calcAttributes() {
    return GrammarUtil.bnfTraverser(this)
      .expand(Conditions.notInstanceOf(BnfRule.class))
      .filter(BnfAttrs.class)
      .toList();
  }

  private Map<String, List<AttributeInfo>> calcAttributeValues() {
    Map<String, List<AttributeInfo>> result = new THashMap<>();
    JBIterable<BnfAttrs> allAttrs = GrammarUtil.bnfTraverser(this)
      .expand(Conditions.notInstanceOf(BnfExpression.class))
      .filter(BnfAttrs.class);
    for (BnfAttrs attrs : allAttrs) {
      boolean isRule = attrs.getParent() instanceof BnfRule;
      TextRange baseRange = attrs.getTextRange();
      List<BnfAttr> attrList = attrs.getAttrList();
      for (int pass = 0; pass < 2; pass ++) {
        for (int i = attrList.size()-1; i >= 0; i --) {
          BnfAttr attr = attrList.get(i);
          BnfAttrPattern attrPattern = attr.getAttrPattern();
          // pass 0: w/o patterns, pass 1: w/ pattern
          if ((pass == 0) == (attrPattern != null)) continue;

          Pattern pattern = null;
          if (attrPattern != null) {
            BnfLiteralExpression expression = attrPattern.getLiteralExpression();
            pattern = expression == null ? null : ParserGeneratorUtil.compilePattern(GrammarUtil.unquote(expression.getText()));
          }
          List<AttributeInfo> list = result.get(attr.getName());
          if (list == null) result.put(attr.getName(), list = new ArrayList<>());
          Object value = ParserGeneratorUtil.getAttributeValue(attr.getExpression());
          int offset = attr.getTextRange().getStartOffset();
          int infoOffset = pattern == null? baseRange.getStartOffset() + 1: baseRange.getStartOffset() + (baseRange.getEndOffset() - offset);
          list.add(new AttributeInfo(offset, infoOffset, !isRule, pattern, value));
        }
      }
    }
    return result;
  }

  @NotNull
  private static <T> AtomicClearableLazyValue<T> lazyValue(Supplier<T> producer) {
    return new AtomicClearableLazyValue<T>() {
      @NotNull
      @Override
      protected T compute() {
        return producer.get();
      }
    };
  }

  private static class AttributeInfo implements Comparable<AttributeInfo> {
    final int attrOffset;
    final int offset;
    final boolean global;
    final Pattern pattern;
    final Object value;

    private AttributeInfo(int attrOffset, int offset, boolean global, Pattern pattern, Object value) {
      this.attrOffset = attrOffset;
      this.offset = offset;
      this.global = global;
      this.pattern = pattern;
      this.value = value;
    }

    @Override
    public int compareTo(@NotNull AttributeInfo o) {
      return offset - o.offset;
    }

    @Override
    public String toString() {
      return (global ? "" : "rule:") + offset + (pattern == null? "" : " (" + pattern + ")") + " = " + value;
    }
  }
}
