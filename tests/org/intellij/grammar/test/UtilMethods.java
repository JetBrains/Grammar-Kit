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

package org.intellij.grammar.test;

public class UtilMethods {
  interface X { }

  interface Y { }

  class Z extends Exception { }

  public static <T extends X & Y> void foo0(Object bar, T param) throws Z, RuntimeException { }
  public static <T> void foo1(Object bar, T param) { }
  public static <T, K> void foo2(Object bar, T param, K k) { }
  public static <T extends X, K extends Y> void foo3(Object bar, T param, K k) { }
  public static <T extends X, K> void foo4(Object bar, T param, K k) { }
  public static <T, K extends X> void foo5(Object bar, T param, K k) { }
}
