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
package org.intellij.grammar.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.Processor;
import gnu.trove.THashMap;
import org.intellij.grammar.BnfFileType;
import org.intellij.grammar.BnfLanguage;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.grammar.psi.BnfAttrs;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: gregory
 * Date: 13.07.11
 * Time: 23:55
 */
public class BnfFileImpl extends PsiFileBase implements BnfFile {
  
  private CachedValue<List<BnfRule>> myRulesValue;
  private CachedValue<Map<String, BnfRule>> myNamesMap;
  private CachedValue<List<BnfAttrs>> myAttributesValue;

  public BnfFileImpl(FileViewProvider fileViewProvider) {
    super(fileViewProvider, BnfLanguage.INSTANCE);
  }
  
  @Override
  public List<BnfRule> getRules() {
    if (myRulesValue == null) {
      myRulesValue = CachedValuesManager.getManager(getProject()).createCachedValue(new CachedValueProvider<List<BnfRule>>() {
        @Override
        public Result<List<BnfRule>> compute() {
          return Result.create(calcRules(), BnfFileImpl.this);
        }
      }, false);
    }
    return myRulesValue.getValue();
  }

  @Nullable
  @Override
  public BnfRule getRule(String ruleName) {
    if (myNamesMap == null) {
      myNamesMap = CachedValuesManager.getManager(getProject()).createCachedValue(new CachedValueProvider<Map<String, BnfRule>>() {
        @Override
        public Result<Map<String, BnfRule>> compute() {
          Map<String, BnfRule> map = new THashMap<String, BnfRule>();
          for (BnfRule rule : getRules()) {
            map.put(rule.getName(), rule);
          }
          return Result.create(map, BnfFileImpl.this);
        }
      }, false);
    }
    return myNamesMap.getValue().get(ruleName);
  }

  @Override
  public List<BnfAttrs> getAttributes() {
    if (myAttributesValue == null) {
      myAttributesValue = CachedValuesManager.getManager(getProject()).createCachedValue(new CachedValueProvider<List<BnfAttrs>>() {
        @Override
        public Result<List<BnfAttrs>> compute() {
          return Result.create(calcAttributes(), BnfFileImpl.this);
        }
      }, false);
    }
    return myAttributesValue.getValue();
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

  private List<BnfRule> calcRules() {
    final List<BnfRule> result = new ArrayList<BnfRule>();
    processChildrenDummyAware(this, new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        if (psiElement instanceof BnfRule) {
          result.add((BnfRule)psiElement);
        }
        return true;
      }
    });
    return result;
  }

  private List<BnfAttrs> calcAttributes() {
    final List<BnfAttrs> result = new ArrayList<BnfAttrs>();
    processChildrenDummyAware(this, new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        if (psiElement instanceof BnfAttrs) result.add((BnfAttrs)psiElement);
        return true;
      }
    });
    return result;
  }

  private static boolean processChildrenDummyAware(PsiElement element, final Processor<PsiElement> processor) {
    return new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        for (PsiElement child = psiElement.getFirstChild(); child != null; child = child.getNextSibling()) {
          if (child instanceof GeneratedParserUtilBase.DummyBlock) {
            if (!process(child)) return false;
          }
          else if (!processor.process(child)) return false;
        }
        return true;
      }
    }.process(element);
  }
}
