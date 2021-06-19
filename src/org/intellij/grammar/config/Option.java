/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.config;

import com.intellij.openapi.util.Getter;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ObjectUtils;

/**
 * @author gregsh
 */
abstract class Option<T> implements Getter<T> {
  public final String id;
  public final T defValue;

  Option(String id, T defValue) {
    this.id = id;
    this.defValue = defValue;
  }

  public abstract T get();

  String innerValue() {
    return System.getProperty(id);
  }

  static Option<Integer> intOption(String id, int def) {
    return new Option<>(id, def) {
      @Override
      public Integer get() {
        return StringUtil.parseInt(innerValue(), defValue);
      }
    };
  }

  static Option<String> strOption(String id, String def) {
    return new Option<>(id, def) {
      @Override
      public String get() {
        return ObjectUtils.chooseNotNull(innerValue(), defValue);
      }
    };
  }

  static Option<Boolean> boolOption(String id, boolean def) {
    return new Option<>(id, def) {
      @Override
      public Boolean get() {
        String s = innerValue();
        return "yes".equals(s) || "true".equals(s) ||
               !"no".equals(s) && !"false".equals(s) && def;
      }
    };
  }
}
