/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.editor;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import gnu.trove.THashSet;
import org.intellij.grammar.BnfIcons;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author gregsh
 */
public class BnfRuleLineMarkerProvider extends RelatedItemLineMarkerProvider {

  @Override
  public void collectNavigationMarkers(@NotNull List<? extends PsiElement> elements,
                                       @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result,
                                       boolean forNavigation) {
    Set<PsiElement> visited = forNavigation ? new THashSet<>() : null;
    for (PsiElement element : elements) {
      PsiElement parent = element.getParent();
      boolean isRuleId = parent instanceof BnfRule && (forNavigation || element == ((BnfRule)parent).getId());
      if (!(isRuleId || forNavigation && element instanceof BnfExpression)) continue;
      List<PsiElement> items = new ArrayList<>();
      NavigatablePsiElement method = getMethod(element);
      if (method != null && (!forNavigation || visited.add(method))) {
        items.add(method);
      }
      boolean hasPSI = false;
      if (isRuleId) {
        BnfRule rule = RuleGraphHelper.getSynonymTargetOrSelf((BnfRule)parent);
        if (RuleGraphHelper.hasPsiClass(rule)) {
          hasPSI = true;
          JavaHelper javaHelper = JavaHelper.getJavaHelper(rule);
          Couple<String> names = ParserGeneratorUtil.getQualifiedRuleClassName(rule);
          for (String className : new String[]{names.first, names.second}) {
            NavigatablePsiElement aClass = javaHelper.findClass(className);
            if (aClass != null && (!forNavigation || visited.add(aClass))) {
              items.add(aClass);
            }
          }
        }
      }
      if (!items.isEmpty()) {
        AnAction action = ActionManager.getInstance().getAction("GotoRelated");
        String tooltipAd = "";
        String popupTitleAd = "";
        if (action != null) {
          String shortcutText = KeymapUtil.getFirstKeyboardShortcutText(action);
          String actionText = StringUtil.isEmpty(shortcutText) ? "'" + action.getTemplatePresentation().getText() + "' action" : shortcutText;
          tooltipAd = "\nGo to sub-expression code via " + actionText;
          popupTitleAd = " (for sub-expressions use " + actionText + ")";
        }
        String title = "parser " + (hasPSI ? "and PSI " : "") + "code";
        NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(BnfIcons.RELATED_METHOD).
          setTargets(items).
          setTooltipText("Click to navigate to "+title + tooltipAd).
          setPopupTitle(StringUtil.capitalize(title) + popupTitleAd);
        result.add(builder.createLineMarkerInfo(element));
      }
    }
  }

  private static @Nullable NavigatablePsiElement getMethod(PsiElement element) {
    BnfRule rule = PsiTreeUtil.getParentOfType(element, BnfRule.class);
    if (rule == null) return null;
    String parserClass = ParserGeneratorUtil.getAttribute(rule, KnownAttribute.PARSER_CLASS);
    if (StringUtil.isEmpty(parserClass)) return null;
    JavaHelper helper = JavaHelper.getJavaHelper(element);
    List<NavigatablePsiElement> methods = helper.findClassMethods(
      parserClass, JavaHelper.MethodType.STATIC, GrammarUtil.getMethodName(rule, element), -1);
    return ContainerUtil.getFirstItem(methods);
  }
}
