/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.psi.impl;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.JBIterable;
import org.intellij.grammar.config.Options;
import org.intellij.jflex.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JFlexJavaCodeInjector implements MultiHostInjector {

  @NotNull
  @Override
  public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
    return Collections.singletonList(JFlexJavaCode.class);
  }

  @Override
  public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
    if (!(context instanceof JFlexJavaCodeInjectionHostImpl)) return;
    if (!Options.INJECT_JAVA_IN_JFLEX.get()) return;

    PsiFile file = context.getContainingFile();
    Map<IElementType, String> options = collectOptions(file);
    boolean isPublic = options.containsKey(JFlexTypes.FLEX_OPT_PUBLIC);
    boolean isFinal = options.containsKey(JFlexTypes.FLEX_OPT_FINAL);
    String lexerClass = ObjectUtils.notNull(StringUtil.nullize(options.get(JFlexTypes.FLEX_OPT_CLASS)), "Lexer");
    String returnType = ObjectUtils.notNull(StringUtil.nullize(options.get(JFlexTypes.FLEX_OPT_TYPE)), "int");
    String implementsStr = ObjectUtils.notNull(StringUtil.nullize(options.get(JFlexTypes.FLEX_OPT_IMPLEMENTS)), "");

    JFlexRule lastRule = SyntaxTraverser.revPsiTraverser()
      .withRoot(PsiTreeUtil.findChildOfType(file, JFlexLexicalRulesSectionImpl.class))
      .filter(JFlexRule.class).filter(o -> o.getJavaCode() != null).first();
    int ruleCount = 0;

    registrar.startInjecting(JavaLanguage.INSTANCE);

    JBIterable<JFlexJavaCodeInjectionHostImpl> s = SyntaxTraverser.psiTraverser(file).filter(JFlexJavaCodeInjectionHostImpl.class);
    for (JFlexJavaCodeInjectionHostImpl host : s) {
      PsiElement hostParent = host.getParent();

      if (hostParent instanceof JFlexUserCodeSection) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("/** @noinspection ALL*/");
        if (isPublic) sb.append("public ");
        if (isFinal) sb.append("final ");
        sb.append("class ").append(lexerClass);
        if (implementsStr.isEmpty()) sb.append(" implements ").append(implementsStr);
        sb.append(" {\n\n");

        JBIterable<JFlexStateDefinition> states = SyntaxTraverser.psiTraverser(
          PsiTreeUtil.findChildOfType(file, JFlexDeclarationsSection.class)).filter(JFlexStateDefinition.class);
        sb.append("  public static final int YYINITIAL = 0;\n");
        int i = 1;
        for (JFlexStateDefinition element : states) {
          sb.append("  public static final int ").append(element.getName()).append(" = ").append(i += 2).append(";\n");
        }
        sb.append("\n");
        sb.append("  public ").append(lexerClass).append("(java.io.Reader in) {}\n");
        sb.append("  private int zzState, zzLexicalState;\n");
        sb.append("  private int zzStartRead, zzEndRead, zzCurrentPos, zzMarkedPos;\n");
        sb.append("  private void yybegin(int state) {}\n");
        sb.append("  private void yypushback(int pos) {}\n");
        sb.append("  private int yystate() { return 0; }\n");
        sb.append("  private int yylength() { return 0; }\n");
        sb.append("  private char yycharat(int pos) { return 0; }\n");
        sb.append("\n");
        registrar.addPlace(null, sb.toString(), host, new TextRange(0, host.getTextLength()));
      }
      else if (hostParent instanceof JFlexOption) {
        IElementType optionType = PsiUtilCore.getElementType(hostParent.getFirstChild());
        if (optionType == JFlexTypes.FLEX_OPT_CODE1) {
          registrar.addPlace(null, null, host, new TextRange(0, host.getTextLength()));
        }
        else if (optionType == JFlexTypes.FLEX_OPT_INIT1) {
          registrar.addPlace("\n  {\n", "\n  }\n", host, new TextRange(0, host.getTextLength()));
        }
        else if (optionType == JFlexTypes.FLEX_OPT_EOF1 || optionType == JFlexTypes.FLEX_OPT_EOFVAL1) {
          registrar.addPlace("\n  void yy_do_eof() {\n", "\n  }\n", host, new TextRange(0, host.getTextLength()));
        }
      }
      else if (hostParent instanceof JFlexRule) {
        String prefix = ruleCount == 0 ?
                        "\n" +
                        "  public " + returnType + " advance() throws java.io.IOException {\n" +
                        "    switch(zzLexicalState) {" : "";
        String suffix = hostParent == lastRule ?
                        "\n" +
                        "    }\n" +
                        "    return null;\n" +
                        "  }\n" +
                        "}" : null;
        registrar.addPlace(prefix + "\n    case " + (++ruleCount) + ":\n      ", suffix, host, new TextRange(0, host.getTextLength()));
      }
    }
    registrar.doneInjecting();
  }

  @NotNull
  private static Map<IElementType, String> collectOptions(@NotNull PsiFile file) {
    PsiElement declarationsSection = PsiTreeUtil.findChildOfType(file, JFlexDeclarationsSection.class);
    if (declarationsSection == null) return Collections.emptyMap();
    Map<IElementType, String> result = new LinkedHashMap<>();
    for (JFlexOption o : SyntaxTraverser.psiApi().children(declarationsSection).filter(JFlexOption.class)) {
      IElementType key = PsiUtilCore.getElementType(o.getFirstChild());
      String value = TextRange.create(o.getFirstChild().getTextLength(), o.getTextLength()).substring(o.getText()).trim();
      String prevValue = StringUtil.nullize(result.get(key));
      result.put(key, prevValue == null ? value : value.isEmpty() ? prevValue : prevValue +", " + value);
    }
    return result;
  }
}