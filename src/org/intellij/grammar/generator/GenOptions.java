/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.BnfFile;

import java.util.Map;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getGenerateOption;
import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;

/**
 * @author gregsh
 */
public class GenOptions {
  public final Names names;
  public final int generateFirstCheck;
  public final boolean generateTokenTypes;
  public final boolean generateTokenSets;
  public final boolean generateElementTypes;
  public final String generateExactTypes;
  public final boolean generateExtendedPin;
  private final boolean generatePsi;
  private final boolean generatePsiFactory;
  private final boolean generatePsiClassesMap;
  public final boolean generateVisitor;
  public final String visitorValue;
  public final boolean generateFQN;
  public final Case generateTokenCase;
  public final Case generateElementCase;
  public final boolean generateTokenAccessors;
  public final boolean generateTokenAccessorsSet;
  public final int javaVersion;

  //These properties are overridden in FleetGenOptions for Fleet-compatible generation
  public boolean getGeneratePsi(){ return generatePsi;}
  public boolean getGeneratePsiFactory(){ return generatePsiFactory; }
  public boolean getGeneratePsiClassesMap(){ return generatePsiClassesMap; }

  public GenOptions(BnfFile myFile) {
    Map<String, String> genOptions = getRootAttribute(myFile, KnownAttribute.GENERATE).asMap();
    names = Names.forName(genOptions.get("names"));
    generatePsi = getGenerateOption(myFile, KnownAttribute.GENERATE_PSI, genOptions, "psi");
    generatePsiFactory = !"no".equals(genOptions.get("psi-factory"));
    generatePsiClassesMap = "yes".equals(genOptions.get("psi-classes-map"));
    generateTokenTypes = getGenerateOption(myFile, KnownAttribute.GENERATE_TOKENS, genOptions, "tokens");
    generateTokenSets = generateTokenTypes && "yes".equals(genOptions.get("token-sets"));
    generateElementTypes = !"no".equals(genOptions.get("elements"));
    generateExactTypes = StringUtil.notNullize(genOptions.get("exact-types"));
    generateFirstCheck = getGenerateOption(myFile, KnownAttribute.GENERATE_FIRST_CHECK, genOptions, "first-check", "firstCheck");
    generateExtendedPin = getGenerateOption(myFile, KnownAttribute.EXTENDED_PIN, genOptions, "extended-pin", "extendedPin");
    generateTokenAccessors = getGenerateOption(myFile, KnownAttribute.GENERATE_TOKEN_ACCESSORS, genOptions, "token-accessors", "tokenAccessors");
    generateTokenAccessorsSet = genOptions.containsKey("token-accessors") || genOptions.containsKey("tokenAccessors");
    generateVisitor = !"no".equals(genOptions.get("visitor"));
    visitorValue = "void".equals(genOptions.get("visitor-value")) ? null : StringUtil.nullize(genOptions.get("visitor-value"));
    generateFQN = "yes".equals(genOptions.get("fqn"));

    generateTokenCase = ParserGeneratorUtil.enumFromString(genOptions.get("token-case"), Case.UPPER);
    generateElementCase = ParserGeneratorUtil.enumFromString(genOptions.get("element-case"), Case.UPPER);
    javaVersion = StringUtil.parseInt(genOptions.get("java"), 11);
  }
}
