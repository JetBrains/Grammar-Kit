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

package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.PatternUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.BnfFile;

import java.util.Map;
import java.util.regex.Pattern;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getGenerateOption;
import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;

/**
 * @author gregsh
 */
public class GenOptions {
  public final Names names;
  public final int generateFirstCheck;
  public final Pattern generateRootRules;
  public final boolean generateTokenTypes;
  public final boolean generateElementTypes;
  public final boolean generateExtendedPin;
  public final boolean generatePsi;
  public final boolean generatePsiFactory;
  public final boolean generateVisitor;
  public final String visitorValue;
  public final Case generateTokenCase;
  public final Case generateElementCase;
  public final boolean generateTokenAccessors;
  public final boolean generateTokenAccessorsSet;

  public GenOptions(BnfFile myFile) {
    Map<String, String> genOptions = getRootAttribute(myFile, KnownAttribute.GENERATE).asMap();
    names = Names.forName(genOptions.get("names"));
    generatePsi = getGenerateOption(myFile, KnownAttribute.GENERATE_PSI, genOptions.get("psi"));
    generatePsiFactory = !"no".equals(genOptions.get("psi-factory"));
    generateTokenTypes = getGenerateOption(myFile, KnownAttribute.GENERATE_TOKENS, genOptions.get("tokens"));
    generateElementTypes = !"no".equals(genOptions.get("elements"));
    generateFirstCheck = getGenerateOption(myFile, KnownAttribute.GENERATE_FIRST_CHECK, genOptions.get("firstCheck"));
    generateExtendedPin = getGenerateOption(myFile, KnownAttribute.EXTENDED_PIN, genOptions.get("extendedPin"));
    generateTokenAccessors = getGenerateOption(myFile, KnownAttribute.GENERATE_TOKEN_ACCESSORS, genOptions.get("tokenAccessors"));
    generateTokenAccessorsSet = genOptions.containsKey("tokenAccessors");
    generateRootRules = PatternUtil.compileSafe(genOptions.get("root-rules"), null);
    generateVisitor = !"no".equals(genOptions.get("visitor"));
    visitorValue = "void".equals(genOptions.get("visitor-value")) ? null : StringUtil.nullize(genOptions.get("visitor-value"));

    generateTokenCase = ParserGeneratorUtil.enumFromString(genOptions.get("token-case"), Case.UPPER);
    generateElementCase = ParserGeneratorUtil.enumFromString(genOptions.get("element-case"), Case.UPPER);
  }
}
