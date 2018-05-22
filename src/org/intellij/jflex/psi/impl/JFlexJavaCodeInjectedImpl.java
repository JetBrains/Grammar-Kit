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

package org.intellij.jflex.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.tree.IElementType;
import org.intellij.jflex.psi.JFlexJavaCodeInjected;
import org.intellij.jflex.psi.JFlexTypes;
import org.intellij.jflex.injection.EmbeddedJavaLiteralTextEscaper;
import org.jetbrains.annotations.NotNull;


/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 15.03.2008
 * Time: 18:51:14
 */
public class JFlexJavaCodeInjectedImpl extends JFlexCompositeImpl implements JFlexJavaCodeInjected {

    public JFlexJavaCodeInjectedImpl(IElementType type) {
        super(type);
    }

    @Override
    public boolean isValidHost() {
        return true;
    }

    public boolean isMatchAction() {
        ASTNode prev = getNode().getTreePrev();
        return prev != null && prev.getElementType() == JFlexTypes.FLEX_BRACE1;
    }

    public PsiLanguageInjectionHost updateText(@NotNull String text) {
        return this;
    }

    @NotNull
    public LiteralTextEscaper<JFlexJavaCodeInjected> createLiteralTextEscaper() {
        return new EmbeddedJavaLiteralTextEscaper(this);
    }
}
