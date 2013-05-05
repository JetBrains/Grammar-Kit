/*
 * Copyright 2011-2013 Gregory Shrago
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

package org.intellij.grammar.generator;

/**
 * @author gregsh
 */
public interface BnfConstants {
  String GENERATION_GROUP = "Grammar Generator";

  String CLASS_HEADER_DEF = "// This is a generated file. Not intended for manual editing.";

  String GPUB_CLASS = "com.intellij.lang.parser.GeneratedParserUtilBase";

  String IELEMENTTYPE_CLASS = "com.intellij.psi.tree.IElementType";
  String PSI_ELEMENT_CLASS = "com.intellij.psi.PsiElement";
  String PSI_TREE_UTIL_CLASS = "com.intellij.psi.util.PsiTreeUtil";
  String PSI_ELEMENT_VISITOR_CLASS = "com.intellij.psi.PsiElementVisitor";
  String AST_NODE_CLASS = "com.intellij.lang.ASTNode";
  String AST_WRAPPER_PSI_ELEMENT_CLASS = "com.intellij.extapi.psi.ASTWrapperPsiElement";

  String ISTUBELEMENTTYPE_CLASS = "com.intellij.psi.stubs.IStubElementType";
  String STUB_BASED_PSI_ELEMENT = "com.intellij.psi.StubBasedPsiElement";
  String STUB_BASED_PSI_ELEMENT_BASE = "com.intellij.extapi.psi.StubBasedPsiElementBase";
}
