/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import org.intellij.grammar.fleet.FleetBnfFileWrapper;
import org.intellij.grammar.fleet.FleetConstants;
import org.intellij.grammar.psi.BnfFile;

public class IntelliJPlatformConstants {
  public final String PsiBuilderClass;
  public final String IElementTypeClass;
  public final String PsiElementClass;
  public final String AstNodeClass;
  public final String PsiParserClass;
  public final String LightPsiParserClass;
  public final String TokenSetClass;

  private IntelliJPlatformConstants(String builder,
                                    String iElementTypeClass,
                                    String psiElementClass,
                                    String astNodeClass,
                                    String psiParserClass,
                                    String lightPsiParserClass,
                                    String tokenSetClass) {
    PsiBuilderClass = builder;
    IElementTypeClass = iElementTypeClass;
    PsiElementClass = psiElementClass;
    AstNodeClass = astNodeClass;
    PsiParserClass = psiParserClass;
    LightPsiParserClass = lightPsiParserClass;
    TokenSetClass = tokenSetClass;
  }

  public static final IntelliJPlatformConstants IdeaConstantSet =
    new IntelliJPlatformConstants(BnfConstants.PSI_BUILDER_CLASS,
                                  BnfConstants.IELEMENTTYPE_CLASS,
                                  BnfConstants.PSI_ELEMENT_CLASS,
                                  BnfConstants.AST_NODE_CLASS,
                                  BnfConstants.PSI_PARSER_CLASS,
                                  BnfConstants.LIGHT_PSI_PARSER_CLASS,
                                  BnfConstants.TOKEN_SET_CLASS);

  public static final IntelliJPlatformConstants FleetConstantSet =
    new IntelliJPlatformConstants(FleetConstants.PSI_BUILDER_CLASS,
                                  FleetConstants.IELEMENTTYPE_CLASS,
                                  FleetConstants.PSI_ELEMENT_CLASS,
                                  FleetConstants.AST_NODE_CLASS,
                                  FleetConstants.PSI_PARSER_CLASS,
                                  FleetConstants.LIGHT_PSI_PARSER_CLASS,
                                  FleetConstants.TOKEN_SET_CLASS);

  public static IntelliJPlatformConstants getConstantSetForBnf(BnfFile file) {
    return (file instanceof FleetBnfFileWrapper) ? FleetConstantSet : IdeaConstantSet;
  }
}
