/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.openapi.util.text.StringUtil;
import org.apache.velocity.VelocityContext;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;
import static org.intellij.grammar.generator.fleet.FleetConstants.*;

public class BnfGenerateFleetLexerAction extends BnfGenerateLexerAction {


  private static final String FLEET_LEXER_FLEX_TEMPLATE = "/templates/fleet.lexer.flex.template";

  @Override
  protected String getLexerFlexTemplate() {
    return FLEET_LEXER_FLEX_TEMPLATE;
  }


  @Override
  protected VelocityContext makeContext(BnfFile bnfFile,
                                        @Nullable String packageName,
                                        Map<String, String> simpleTokens,
                                        Map<String, String> regexpTokens,
                                        int[] maxLen) {
    var context = new VelocityContext();
    context.put("lexerClass", getLexerName(bnfFile));
    if (adjustPackage(bnfFile)) {
      var original = StringUtil.notNullize(packageName, StringUtil.getPackageName(getRootAttribute(bnfFile, KnownAttribute.PARSER_CLASS)));
      if (!original.isEmpty()) {
        context.put("packageName", FLEET_NAMESPACE_PREFIX + original);
      }
      else {
        context.put("packageName", FLEET_NAMESPACE);
      }
    }
    else {
      context.put("packageName",
                  StringUtil.notNullize(packageName, StringUtil.getPackageName(getRootAttribute(bnfFile, KnownAttribute.PARSER_CLASS))));
    }
    context.put("tokenPrefix", getRootAttribute(bnfFile, KnownAttribute.ELEMENT_TYPE_PREFIX));
    if (adjustPackage(bnfFile)) {
      context.put("typesClass", FLEET_NAMESPACE_PREFIX + getRootAttribute(bnfFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS));
    }
    else {
      context.put("typesClass", getRootAttribute(bnfFile, KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS));
    }
    context.put("simpleTokens", simpleTokens);
    context.put("regexpTokens", regexpTokens);
    context.put("StringUtil", StringUtil.class);
    context.put("maxTokenLength", maxLen[0]);
    return context;
  }

  private static boolean adjustPackage(BnfFile file) {
    return getRootAttribute(file, KnownAttribute.GENERATE).stream()
      .noneMatch(pair -> pair.first.equals("adjustPackagesForFleet") && pair.second.equals("no"));
  }
}
