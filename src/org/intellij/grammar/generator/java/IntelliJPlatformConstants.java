/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.java;

import org.intellij.grammar.syntax.SyntaxConstants;

public class IntelliJPlatformConstants {
  public final String ParserStateHolder;
  public final String BuilderClass;
  public final String ParserElementTypeClass;
  public final String ElementTypeBaseClass;
  public final String PsiElementClass;
  public final String AstNodeClass;
  public final String PsiParserClass;
  public final String LightPsiParserClass;
  public final String TokenSetClass;
  public final String ParserNodeSetClass;
  public final String ParserOutputType;

  private IntelliJPlatformConstants(String stateHolder,
                                    String builder,
                                    String iElementTypeClass,
                                    String elementTypeBaseClass,
                                    String psiElementClass,
                                    String astNodeClass,
                                    String psiParserClass,
                                    String lightPsiParserClass,
                                    String tokenSetClass,
                                    String parserNodeSetClass,
                                    String parserOutputType) {
    ParserStateHolder = stateHolder;
    BuilderClass = builder;
    ParserElementTypeClass = iElementTypeClass;
    ElementTypeBaseClass = elementTypeBaseClass;
    PsiElementClass = psiElementClass;
    AstNodeClass = astNodeClass;
    PsiParserClass = psiParserClass;
    LightPsiParserClass = lightPsiParserClass;
    TokenSetClass = tokenSetClass;
    ParserNodeSetClass = parserNodeSetClass;
    ParserOutputType = parserOutputType;
  }

  private IntelliJPlatformConstants(String builder,
                                    String iElementTypeClass,
                                    String psiElementClass,
                                    String astNodeClass,
                                    String psiParserClass,
                                    String lightPsiParserClass,
                                    String tokenSetClass) {
    BuilderClass = builder;
    ParserStateHolder = builder;
    ParserElementTypeClass = iElementTypeClass;
    ElementTypeBaseClass = iElementTypeClass;
    PsiElementClass = psiElementClass;
    AstNodeClass = astNodeClass;
    PsiParserClass = psiParserClass;
    LightPsiParserClass = lightPsiParserClass;
    TokenSetClass = tokenSetClass;
    ParserNodeSetClass = tokenSetClass;
    ParserOutputType = astNodeClass;
  }

  public static final IntelliJPlatformConstants ClassicConstantSet =
    new IntelliJPlatformConstants(BnfConstants.PSI_BUILDER_CLASS,
                                  BnfConstants.IELEMENTTYPE_CLASS,
                                  BnfConstants.PSI_ELEMENT_CLASS,
                                  BnfConstants.AST_NODE_CLASS,
                                  BnfConstants.PSI_PARSER_CLASS,
                                  BnfConstants.LIGHT_PSI_PARSER_CLASS,
                                  BnfConstants.TOKEN_SET_CLASS);

  public static final IntelliJPlatformConstants SyntaxConstantSet =
    new IntelliJPlatformConstants(SyntaxConstants.RUNTIME_CLASS,
                                  SyntaxConstants.SYNTAX_BUILDER_CLASS,
                                  SyntaxConstants.SYNTAX_ELEMENT_TYPE,
                                  BnfConstants.IELEMENTTYPE_CLASS,
                                  BnfConstants.PSI_ELEMENT_CLASS,
                                  BnfConstants.AST_NODE_CLASS,
                                  BnfConstants.PSI_PARSER_CLASS,
                                  BnfConstants.LIGHT_PSI_PARSER_CLASS,
                                  BnfConstants.TOKEN_SET_CLASS,
                                  SyntaxConstants.TOKEN_SET_CLASS,
                                  SyntaxConstants.PRODUCTION_RESULT);
}
