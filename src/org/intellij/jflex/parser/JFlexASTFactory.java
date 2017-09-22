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

package org.intellij.jflex.parser;

import com.intellij.lang.ASTFactory;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.JBIterable;
import org.intellij.jflex.psi.JFlexTypes;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * @author gregsh
 */
public class JFlexASTFactory extends ASTFactory {
  private static final Map<IElementType, Constructor<CompositePsiElement>> ourMap =
    JBIterable.from(JFlexTypes.Classes.elementTypes()).toMap(o -> {
      try {
        return (Constructor<CompositePsiElement>)JFlexTypes.Classes.findClass(o).getConstructor(IElementType.class);
      }
      catch (NoSuchMethodException e) {
        throw new AssertionError(e);
      }
    });

  @Nullable
  @Override
  public CompositeElement createComposite(IElementType type) {
    try {
      return ourMap.get(type).newInstance(type);
    }
    catch (Exception e) {
      throw new AssertionError(e);
    }
  }
}
