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

package org.intellij.grammar.test;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class StubTest {

  public static class SimpleStub extends StubBase<PsiElement> {
    protected SimpleStub(StubElement parent, IStubElementType elementType) {
      super(parent, elementType);
    }
  }

  public static class SimpleBase extends StubBasedPsiElementBase<SimpleStub> {
    public SimpleBase(@NotNull SimpleStub stub,
                      @NotNull IStubElementType nodeType) {
      super(stub, nodeType);
    }

    public SimpleBase(@NotNull ASTNode node) {
      super(node);
    }

  }

  public static class GenericBase<T extends StubElement> extends StubBasedPsiElementBase<T> {
    public GenericBase(@NotNull T stub,
                       @NotNull IStubElementType nodeType) {
      super(stub, nodeType);
    }

    public GenericBase(@NotNull ASTNode node) {
      super(node);
    }

    public GenericBase(T stub, IElementType nodeType, ASTNode node) {
      super(stub, nodeType, node);
    }
  }
}
