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

package org.intellij.grammar;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.generator.BnfConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author gregsh
 */
@SuppressWarnings("StaticVariableOfConcreteClass")
public class KnownAttribute<T> {
  private static final Map<String, KnownAttribute> ourAttributes = new TreeMap<String, KnownAttribute>();

  @NotNull
  public static Collection<KnownAttribute> getAttributes() { return Collections.unmodifiableCollection(ourAttributes.values()); }
  @Nullable
  public static KnownAttribute getAttribute(@Nullable String name) { return name == null ? null : ourAttributes.get(name); }

  private static final ListValue EMPTY_LIST = new ListValue();

  public static final KnownAttribute<String>       CLASS_HEADER              = create(true, String.class, "classHeader", BnfConstants.CLASS_HEADER_DEF);
  public static final KnownAttribute<ListValue>    GENERATE                  = create(true, ListValue.class, "generate", EMPTY_LIST);
  public static final KnownAttribute<Boolean>      GENERATE_PSI              = create(true, Boolean.class, "generatePsi", true);
  public static final KnownAttribute<Boolean>      GENERATE_TOKENS           = create(true, Boolean.class, "generateTokens", true);
  public static final KnownAttribute<Boolean>      GENERATE_TOKEN_ACCESSORS  = create(true, Boolean.class, "generateTokenAccessors", false);
  public static final KnownAttribute<Integer>      GENERATE_FIRST_CHECK      = create(true, Integer.class, "generateFirstCheck", 2);
  public static final KnownAttribute<Boolean>      EXTENDED_PIN              = create(true, Boolean.class, "extendedPin", true);

  public static final KnownAttribute<ListValue>    PARSER_IMPORTS            = create(true, ListValue.class, "parserImports", EMPTY_LIST);
  public static final KnownAttribute<String>       PSI_CLASS_PREFIX          = create(true, String.class, "psiClassPrefix", "");
  public static final KnownAttribute<String>       PSI_IMPL_CLASS_SUFFIX     = create(true, String.class, "psiImplClassSuffix", "Impl");
  public static final KnownAttribute<String>       PSI_TREE_UTIL_CLASS       = create(true, String.class, "psiTreeUtilClass", BnfConstants.PSI_TREE_UTIL_CLASS);
  public static final KnownAttribute<String>       PSI_PACKAGE               = create(true, String.class, "psiPackage", "generated.psi");
  public static final KnownAttribute<String>       PSI_IMPL_PACKAGE          = create(true, String.class, "psiImplPackage", "generated.psi.impl");
  public static final KnownAttribute<String>       PSI_VISITOR_NAME          = create(true, String.class, "psiVisitorName", "Visitor");
  public static final KnownAttribute<String>       PSI_IMPL_UTIL_CLASS       = create(true, String.class, "psiImplUtilClass", null);
  public static final KnownAttribute<String>       ELEMENT_TYPE_CLASS        = create(true, String.class, "elementTypeClass", BnfConstants.IELEMENTTYPE_CLASS);
  public static final KnownAttribute<String>       TOKEN_TYPE_CLASS          = create(true, String.class, "tokenTypeClass", BnfConstants.IELEMENTTYPE_CLASS);
  public static final KnownAttribute<String>       PARSER_CLASS              = create(true, String.class, "parserClass", "generated.GeneratedParser");
  public static final KnownAttribute<String>       PARSER_UTIL_CLASS         = create(true, String.class, "parserUtilClass", BnfConstants.GPUB_CLASS);
  public static final KnownAttribute<String>       ELEMENT_TYPE_HOLDER_CLASS = create(true, String.class, "elementTypeHolderClass", "generated.GeneratedTypes");
  public static final KnownAttribute<String>       ELEMENT_TYPE_PREFIX       = create(true, String.class, "elementTypePrefix", "");
  public static final KnownAttribute<String>       ELEMENT_TYPE_FACTORY      = create(true, String.class, "elementTypeFactory", null);
  public static final KnownAttribute<String>       TOKEN_TYPE_FACTORY        = create(true, String.class, "tokenTypeFactory", null);

  public static final KnownAttribute<String>       EXTENDS                   = create(false, String.class, "extends", BnfConstants.AST_WRAPPER_PSI_ELEMENT_CLASS);
  public static final KnownAttribute<ListValue>    IMPLEMENTS                = create(false, ListValue.class, "implements", ListValue.singleValue( null, BnfConstants.PSI_ELEMENT_CLASS));
  public static final KnownAttribute<String>       ELEMENT_TYPE              = create(false, String.class, "elementType", null);
  public static final KnownAttribute<Object>       PIN                       = create(false, Object.class, "pin", (Object)(-1));
  public static final KnownAttribute<String>       MIXIN                     = create(false, String.class, "mixin", null);
  public static final KnownAttribute<String>       RECOVER_WHILE             = create(false, String.class, "recoverWhile", null);
  public static final KnownAttribute<String>       NAME                      = create(false, String.class, "name", null);

  public static final KnownAttribute<Boolean>      RIGHT_ASSOCIATIVE         = create(false, Boolean.class, "rightAssociative", false);
  public static final KnownAttribute<String>       CONSUME_TOKEN_METHOD      = create(false, String.class,  "consumeTokenMethod", "consumeToken");

  public static final KnownAttribute<String>       STUB_CLASS                = create(false, String.class,  "stubClass", null);

  public static final KnownAttribute<ListValue>    METHODS                   = create(false, ListValue.class, "methods", EMPTY_LIST);
  public static final KnownAttribute<ListValue>    HOOKS                     = create(false, ListValue.class, "hooks", EMPTY_LIST);
  public static final KnownAttribute<ListValue>    TOKENS                    = create(true, ListValue.class, "tokens", EMPTY_LIST);

  private final boolean myGlobal;
  private final String myName;
  private final Class<T> myClazz;
  private final T myDefaultValue;

  public static <T> KnownAttribute<T> create(Class<T> clazz, String name, @Nullable T defaultValue) {
    return new KnownAttribute<T>(name, clazz, defaultValue);
  }

  private static <T> KnownAttribute<T> create(boolean global, Class<T> clazz, String name, @Nullable T defaultValue) {
    return new KnownAttribute<T>(global, name, clazz, defaultValue);
  }

  private KnownAttribute(String name, Class<T> clazz, T defaultValue) {
    myName = name;
    myClazz = clazz;
    myDefaultValue = defaultValue;
    myGlobal = false;
  }

  private KnownAttribute(boolean global, String name, Class<T> clazz, T defaultValue) {
    myName = name;
    myClazz = clazz;
    myDefaultValue = defaultValue;
    myGlobal = global;
    KnownAttribute prev = ourAttributes.put(name, this);
    assert prev == null : name + " attribute already defined";
  }

  @NotNull
  public String getName() {
    return myName;
  }

  public boolean isGlobal() {
    return myGlobal;
  }

  public T getDefaultValue() {
    return myDefaultValue;
  }

  public T ensureValue(Object o) {
    if (o == null) return getDefaultValue();
    if (myClazz == ListValue.class && o instanceof String) {
      return (T)ListValue.singleValue(null, (String)o);
    }
    if (myClazz.isInstance(o)) return (T)o;
    return getDefaultValue();
  }

  @Override
  public String toString() {
    return myName;
  }

  public String getDescription() {
    try {
      InputStream resourceAsStream = getClass().getResourceAsStream("/messages/attributeDescriptions/" + getName() + ".html");
      return resourceAsStream == null? null : FileUtil.loadTextAndClose(resourceAsStream);
    }
    catch (IOException e) {
      return null;
    }
  }

  // returns a non-registered attribute for migration purposes
  @NotNull
  public KnownAttribute<T> alias(String deprecatedName) {
    return new KnownAttribute<T>(deprecatedName, myClazz, null);
  }

  @Nullable
  public static KnownAttribute getCompatibleAttribute(String name) {
    KnownAttribute attr = getAttribute(name);
    if (attr == null && "recoverUntil".equals(name)) return RECOVER_WHILE;
    if (attr == null && "stubParserClass".equals(name)) return PARSER_UTIL_CLASS;
    return attr;
  }

  public static class ListValue extends LinkedList<Pair<String, String>> {
    @NotNull
    public static ListValue singleValue(String s1, String s2) {
      ListValue t = new ListValue();
      t.add(Pair.create(s1, s2));
      return t;
    }

    @NotNull
    public List<String> asStrings() {
      List<String> t = ContainerUtil.newArrayList();
      for (Pair<String, String> pair : this) {
        if (pair.first != null) t.add(pair.first);
        else if (pair.second != null) t.add(pair.second);
      }
      return t;
    }

    @NotNull
    public Map<String, String> asMap() {
      return asMap(false);
    }

    @NotNull
    public Map<String, String> asInverseMap() {
      return asMap(true);
    }

    @NotNull
    private Map<String, String> asMap(boolean inverse) {
      Map<String, String> t = ContainerUtil.newLinkedHashMap();
      for (Pair<String, String> pair : this) {
        String key = inverse ? pair.second : pair.first;
        String value = inverse ? pair.first : pair.second;
        if (key != null) t.put(key, value);
      }
      return Collections.unmodifiableMap(t);
    }
  }
}
