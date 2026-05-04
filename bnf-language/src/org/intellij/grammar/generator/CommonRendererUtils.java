/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.codeStyle.NameUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.intellij.grammar.psi.BnfAttributes.getAttribute;

public class CommonRendererUtils {
  public static @NotNull String getBaseName(@NotNull String name) {
    return toIdentifier(name, null, Case.AS_IS);
  }

  public static @NotNull String toIdentifier(@NotNull String text, @Nullable NameFormat format, @NotNull Case textCase) {
    if (text.isEmpty()) return "";
    String fixed = text.replaceAll("[^:\\p{javaJavaIdentifierPart}]", "_");
    boolean allCaps = Case.UPPER.apply(fixed).equals(fixed);
    StringBuilder sb = new StringBuilder();
    if (!Character.isJavaIdentifierStart(fixed.charAt(0)) && sb.isEmpty()) sb.append("_");
    String[] strings = NameUtil.nameToWords(fixed);
    for (int i = 0, len = strings.length; i < len; i++) {
      String s = strings[i];
      if (textCase == Case.CAMEL && s.startsWith("_") && !(i == 0 || i == len - 1)) continue;
      if (textCase == Case.UPPER && !s.startsWith("_") && !(i == 0 || StringUtil.endsWith(sb, "_"))) sb.append("_");
      if (textCase == Case.CAMEL && !allCaps && Case.UPPER.apply(s).equals(s)) {
        sb.append(s);
      }
      else {
        sb.append(textCase.apply(s));
      }
    }
    return format == null ? sb.toString() : format.apply(sb.toString());
  }

  public static @NotNull String getGetterName(@NotNull String text) {
    return toIdentifier(text, NameFormat.from("get"), Case.CAMEL);
  }

  public static @NotNull String getTokenSetConstantName(@NotNull String nextName) {
    return toIdentifier(nextName, null, Case.UPPER) + "_TOKENS";
  }

  public static @NotNull String getWrapperParserConstantName(@NotNull String nextName) {
    return getBaseName(nextName) + "_parser_";
  }

  public static @NotNull String getRulePsiClassName(@NotNull BnfRule rule, @Nullable NameFormat format) {
    return toIdentifier(rule.getName(), format, Case.CAMEL);
  }

  public static @NotNull Couple<@NotNull String> getQualifiedRuleClassName(@NotNull BnfRule rule) {
    BnfFile file = (BnfFile)rule.getContainingFile();
    String psiPackage = getAttribute(rule, KnownAttribute.PSI_PACKAGE);
    String psiImplPackage = getAttribute(rule, KnownAttribute.PSI_IMPL_PACKAGE);
    NameFormat psiFormat = NameFormat.forPsiClass(file);
    NameFormat psiImplFormat = NameFormat.forPsiImplClass(file);
    return Couple.of(psiPackage + "." + getRulePsiClassName(rule, psiFormat),
                     psiImplPackage + "." + getRulePsiClassName(rule, psiImplFormat));
  }

  public static @NotNull String getElementType(@NotNull BnfRule rule, @NotNull Case cas) {
    String elementType = getAttribute(rule, KnownAttribute.ELEMENT_TYPE);
    if ("".equals(elementType)) return "";
    NameFormat prefix = NameFormat.from(getAttribute(rule, KnownAttribute.ELEMENT_TYPE_PREFIX));
    return toIdentifier(elementType != null ? elementType : rule.getName(), prefix, cas);
  }
}
