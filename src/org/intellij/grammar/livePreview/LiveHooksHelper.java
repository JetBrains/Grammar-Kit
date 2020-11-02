/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.livePreview;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespacesAndCommentsBinder;
import com.intellij.lang.WhitespacesBinders;
import com.intellij.util.ObjectUtils;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gregsh
 */
public class LiveHooksHelper {

  public static void registerHook(PsiBuilder builder, String name, String value) {
    final GeneratedParserUtilBase.Hook hookObj = getHook(name);
    if (hookObj == null) return;
    Object hookParam = ObjectUtils.notNull(getHookParam(value), value);
    GeneratedParserUtilBase.register_hook_(builder, (builder1, marker, param) -> {
      try {
        return hookObj.run(builder1, marker, param);
      }
      catch (Exception e) {
        builder1.error("hook crashed: " + e.toString());
        return marker;
      }
    }, hookParam);
  }

  private static final Map<String, Object> ourHooks = new HashMap<>();
  private static final Map<String, Object> ourBinders = new HashMap<>();

  static {
    collectStaticFields(GeneratedParserUtilBase.class, GeneratedParserUtilBase.Hook.class, ourHooks);
    collectStaticFields(WhitespacesBinders.class, WhitespacesAndCommentsBinder.class, ourBinders);
    ourBinders.put("null", null);
  }


  public static GeneratedParserUtilBase.Hook getHook(String name) {
    return (GeneratedParserUtilBase.Hook)ourHooks.get(name);
  }

  public static Object getHookParam(@NotNull String value) {
    String[] args = value.trim().split("\\s*,\\s*");
    if (args.length == 1) return ourBinders.get(args[0]);
    Object[] res = new WhitespacesAndCommentsBinder[args.length];
    for (int i = 0; i < args.length; i++) {
      if (!ourBinders.containsKey(args[i])) return null;
      res[i] = ourBinders.get(args[i]);
    }
    return res;
  }

  private static void collectStaticFields(Class<?> where, Class<?> what, Map<String, Object> result) {
    for (Field field : where.getFields()) {
      int m = field.getModifiers();
      if ((m & Modifier.STATIC) != 0 && (m & Modifier.FINAL) != 0 && (m & Modifier.PUBLIC) != 0) {
        if (what.isAssignableFrom(field.getType())) {
          try {
            result.put(field.getName(), field.get(null));
          }
          catch (IllegalAccessException ignored) {
          }
        }
      }
    }
  }
}
