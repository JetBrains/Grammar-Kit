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

package org.intellij.grammar.livePreview;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespacesAndCommentsBinder;
import com.intellij.lang.WhitespacesBinders;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author gregsh
 */
public class LiveHooksHelper {

  public static void registerHook(PsiBuilder builder, String name, String value) {
    final GeneratedParserUtilBase.Hook hookObj = getHook(name);
    if (hookObj == null) return;
    Object hookParam = ObjectUtils.notNull(getHookParam(value), value);
    GeneratedParserUtilBase.register_hook_(builder, new GeneratedParserUtilBase.Hook<Object>() {
      @Override
      public PsiBuilder.Marker run(PsiBuilder builder, PsiBuilder.Marker marker, Object param) {
        try {
          return hookObj.run(builder, marker, param);
        }
        catch (Exception e) {
          builder.error("hook crashed: " + e.toString());
          return marker;
        }
      }
    }, hookParam);
  }

  private static final Map<String, Object> ourHooks = ContainerUtil.newHashMap();
  private static final Map<String, Object> ourBinders = ContainerUtil.newHashMap();

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
