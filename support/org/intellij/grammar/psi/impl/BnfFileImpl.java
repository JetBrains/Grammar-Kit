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
import org.intellij.grammar.BnfFileType;
import org.intellij.grammar.BnfLanguage;
import org.intellij.grammar.psi.BnfAttrs;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * User: gregory
 * Date: 13.07.11
 * Time: 23:55
 */
public class BnfFileImpl extends PsiFileBase implements BnfFile {
  
  private CachedValue<List<BnfRule>> myRulesValue;
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
    GrammarUtil.processChildrenDummyAware(this, new Processor<PsiElement>() {
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
    GrammarUtil.processChildrenDummyAware(this, new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        if (psiElement instanceof BnfAttrs) result.add((BnfAttrs)psiElement);
        return true;
      }
    });
    return result;
  }
}
