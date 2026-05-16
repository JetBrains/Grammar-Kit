/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.psi.NavigatablePsiElement;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.BnfRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class RuleImplUtil {
  private RuleImplUtil() {
  }

  /**
   * Resolves the static helper methods that implement extra members of a generated PSI rule (the
   * {@code methods} attribute combined with {@code psiImplUtilClass}).
   * <p>
   * Walks the rule's PSI hierarchy — every class returned by {@link BnfRules#getRuleClasses(BnfRule)} —
   * and for each one walks the {@code psiImplUtilClass} chain via {@link #getSuperClassName} until a
   * matching static method is found whose first parameter accepts the rule's PSI type. If multiple
   * overloads match, {@link #filterOutShadowedRuleImplMethods} keeps only the most specific overload
   * for the matched rule class.
   */
  public static @NotNull List<NavigatablePsiElement> findRuleImplMethods(@NotNull JavaHelper helper,
                                                                         @Nullable String psiImplUtilClass,
                                                                         @Nullable String methodName,
                                                                         @Nullable BnfRule rule) {
    if (rule == null) return Collections.emptyList();
    List<NavigatablePsiElement> methods = Collections.emptyList();
    String selectedSuperClass = null;
    main:
    for (String ruleClass : BnfRules.getRuleClasses(rule)) {
      for (String utilClass = psiImplUtilClass; utilClass != null; utilClass = helper.getSuperClassName(utilClass)) {
        methods = helper.findClassMethods(utilClass, MethodType.STATIC, methodName, false, -1, ruleClass);
        selectedSuperClass = ruleClass;
        if (!methods.isEmpty()) break main;
      }
    }
    return filterOutShadowedRuleImplMethods(helper, selectedSuperClass, methods);
  }

  private static @NotNull List<NavigatablePsiElement> filterOutShadowedRuleImplMethods(@NotNull JavaHelper helper,
                                                                                       @Nullable String selectedClass,
                                                                                       @NotNull List<NavigatablePsiElement> methods) {
    if (methods.size() <= 1) return methods;

    List<NavigatablePsiElement> result = new ArrayList<>(methods);
    Map<String, NavigatablePsiElement> prototypes = new LinkedHashMap<>();
    for (NavigatablePsiElement m2 : methods) {
      List<String> types = JavaHelper.getMethodTypes(m2);
      String proto = m2.getName() + types.subList(3, types.size());
      NavigatablePsiElement m1 = prototypes.get(proto);
      if (m1 == null) {
        prototypes.put(proto, m2);
        continue;
      }
      String type1 = JavaHelper.getMethodTypes(m1).get(1);
      String type2 = types.get(1);
      if (Objects.equals(type1, type2)) continue;
      for (String s = selectedClass; s != null; s = helper.getSuperClassName(s)) {
        if (Objects.equals(type1, s)) {
          result.remove(m2);
        }
        else if (Objects.equals(type2, s)) {
          result.remove(m1);
        }
        else {
          continue;
        }
        break;
      }
    }
    return result;
  }
}
