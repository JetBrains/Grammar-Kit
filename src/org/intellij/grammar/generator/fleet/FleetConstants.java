/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.fleet;

interface FleetConstants {

  String GENERATION_GROUP = "Grammar Generator";
  String GPUB_CLASS = "fleet.com.intellij.lang.parser.GeneratedParserUtilBase";
  String PSI_BUILDER_CLASS = "fleet.com.intellij.lang.PsiBuilder";
  String PSI_PARSER_CLASS = "fleet.com.intellij.lang.PsiParser";
  String LIGHT_PSI_PARSER_CLASS = "fleet.com.intellij.lang.LightPsiParser";
  String TOKEN_SET_CLASS = "fleet.com.intellij.psi.tree.TokenSet";
  String IELEMENTTYPE_CLASS = "fleet.com.intellij.psi.tree.IElementType";
  String PSI_ELEMENT_CLASS = "fleet.com.intellij.psi.PsiElement";
  String AST_NODE_CLASS = "fleet.com.intellij.lang.ASTNode";
  String FLEET_NAMESPACE_PREFIX = "fleet.";
}
