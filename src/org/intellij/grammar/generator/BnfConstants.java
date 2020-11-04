/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

/**
 * @author gregsh
 */
public interface BnfConstants {
  String BNF_DISPLAY_NAME = "Grammar-Kit BNF";
  String LP_DISPLAY_NAME = "Grammar-Kit Live Preview";

  String GENERATION_GROUP = "Grammar Generator";

  String CLASS_HEADER_DEF = "// This is a generated file. Not intended for manual editing.";

  String REGEXP_PREFIX = "regexp:";

  String GPUB_CLASS = "com.intellij.lang.parser.GeneratedParserUtilBase";
  String PSI_BUILDER_CLASS = "com.intellij.lang.PsiBuilder";
  String PSI_PARSER_CLASS = "com.intellij.lang.PsiParser";
  String LIGHT_PSI_PARSER_CLASS = "com.intellij.lang.LightPsiParser";
  String TOKEN_SET_CLASS = "com.intellij.psi.tree.TokenSet";

  String IELEMENTTYPE_CLASS = "com.intellij.psi.tree.IElementType";
  String IFILEELEMENTTYPE_CLASS = "com.intellij.psi.tree.IFileElementType";
  String PSI_ELEMENT_CLASS = "com.intellij.psi.PsiElement";
  String PSI_TREE_UTIL_CLASS = "com.intellij.psi.util.PsiTreeUtil";
  String PSI_ELEMENT_VISITOR_CLASS = "com.intellij.psi.PsiElementVisitor";
  String AST_NODE_CLASS = "com.intellij.lang.ASTNode";
  String AST_WRAPPER_PSI_ELEMENT_CLASS = "com.intellij.extapi.psi.ASTWrapperPsiElement";
  String COMPOSITE_PSI_ELEMENT_CLASS = "com.intellij.psi.impl.source.tree.CompositePsiElement";

  String OVERRIDE_ANNO = "@java.lang.Override";
  String NOTNULL_ANNO = "@org.jetbrains.annotations.NotNull";
  String NULLABLE_ANNO = "@org.jetbrains.annotations.Nullable";
  String SUPPRESS_WARNINGS_ANNO = "@java.lang.SuppressWarnings";

  String ISTUBELEMENTTYPE_CLASS = "com.intellij.psi.stubs.IStubElementType";
  String STUB_BASED_PSI_ELEMENT = "com.intellij.psi.StubBasedPsiElement";
  String STUB_BASED_PSI_ELEMENT_BASE = "com.intellij.extapi.psi.StubBasedPsiElementBase";
  String RECOVER_AUTO = "#auto";

  String TOKEN_SET_HOLDER_NAME = "TokenSets";
}
