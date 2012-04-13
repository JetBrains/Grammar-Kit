/*
 * Copyright 2011-2012 Gregory Shrago
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

import org.intellij.grammar.generator.BnfConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author gregsh
 */
@SuppressWarnings("StaticVariableOfConcreteClass")
public class KnownAttribute<T> {
  private static final Map<String, KnownAttribute> ourAttributes = new TreeMap<String, KnownAttribute>();

  public static Collection<KnownAttribute> getAttributes() { return Collections.unmodifiableCollection(ourAttributes.values()); }
  public static KnownAttribute getAttribute(String name) { return ourAttributes.get(name); }

  public static final KnownAttribute<String> CLASS_HEADER               = create(true, "classHeader", BnfConstants.CLASS_HEADER_DEF);
  public static final KnownAttribute<Boolean> GENERATE_PSI              = create(true, "generatePsi", true);
  public static final KnownAttribute<Boolean> GENERATE_TOKENS           = create(true, "generateTokens", true);
  public static final KnownAttribute<Boolean> GENERATE_PARSER_UTIL      = create(true, "generateStubParser", true);
  public static final KnownAttribute<Boolean> GENERATE_MEMOIZATION      = create(true, "memoization", false);
  public static final KnownAttribute<Integer> GENERATE_FIRST_CHECK      = create(true, "generateFirstCheck", 2);
  public static final KnownAttribute<Boolean> EXTENDED_PIN              = create(true, "extendedPin", true);
  public static final KnownAttribute<String> PSI_CLASS_PREFIX           = create(true, "psiClassPrefix", "");
  public static final KnownAttribute<String> PSI_IMPL_CLASS_SUFFIX      = create(true, "psiImplClassSuffix", "Impl");
  public static final KnownAttribute<String> PSI_PACKAGE                = create(true, "psiPackage", "generated.psi");
  public static final KnownAttribute<String> PSI_IMPL_PACKAGE           = create(true, "psiImplPackage", "generated.psi.impl");
  public static final KnownAttribute<String> PSI_VISITOR_CLASS          = create(true, "psiVisitorClass", "Visitor");
  public static final KnownAttribute<String> ELEMENT_TYPE_CLASS         = create(true, "elementTypeClass", BnfConstants.IELEMENTTYPE_CLASS);
  public static final KnownAttribute<String> TOKEN_TYPE_CLASS           = create(true, "tokenTypeClass", BnfConstants.IELEMENTTYPE_CLASS);
  public static final KnownAttribute<String> PARSER_CLASS               = create(true, "parserClass", "generated.Parser");
  public static final KnownAttribute<String> PARSER_UTIL_CLASS          = create(true, "stubParserClass", "generated.ParserUtil");
  public static final KnownAttribute<String> ELEMENT_TYPE_HOLDER_CLASS  = create(true, "elementTypeHolderClass", "generated.ParserTypes");
  public static final KnownAttribute<String> ELEMENT_TYPE_PREFIX        = create(true, "elementTypePrefix", "");
  public static final KnownAttribute<String> ELEMENT_TYPE_FACTORY       = create(true, "elementTypeFactory", null);
  public static final KnownAttribute<String> TOKEN_TYPE_FACTORY         = create(true, "tokenTypeFactory", null);
  public static final KnownAttribute<String> PARSER_IMPORTS             = create(true, "parserImports", "");

  public static final KnownAttribute<String> EXTENDS                    = create(false, "extends", "generated.CompositeElementImpl");
  public static final KnownAttribute<String> IMPLEMENTS                 = create(false, "implements", "generated.CompositeElement");
  public static final KnownAttribute<String> ELEMENT_TYPE               = create(false, "elementType", null);
  public static final KnownAttribute<String> METHOD_RENAMES             = create(false, "methodRenames", null);
  public static final KnownAttribute<Object> PIN                        = create(false, "pin", (Object)(-1));
  public static final KnownAttribute<String> MIXIN                      = create(false, "mixin", null);
  public static final KnownAttribute<String> RECOVER_UNTIL              = create(false, "recoverUntil", null);
  public static final KnownAttribute<Map<String, String>> PSI_ACCESSORS = create(false, "methods", Collections.<String, String>emptyMap());

  private final boolean myGlobal;
  private final String myName;
  private final T myDefaultValue;
  private final String myDocumentation;

  public static <T> KnownAttribute<T> create(String name, @Nullable T defaultValue) {
    return new KnownAttribute<T>(name, defaultValue);
  }

  private static <T> KnownAttribute<T> create(boolean global, String name, @Nullable T defaultValue) {
    return new KnownAttribute<T>(global, name, defaultValue);
  }

  private KnownAttribute(String name, T defaultValue) {
    myName = name;
    myDefaultValue = defaultValue;
    myDocumentation = name;
    myGlobal = false;
  }

  private KnownAttribute(boolean global, String name, T defaultValue) {
    myName = name;
    myDefaultValue = defaultValue;
    myDocumentation = name;
    myGlobal = global;
    KnownAttribute prev = ourAttributes.put(name, this);
    assert prev == null : name + " attribute already defined";
  }

  @NotNull
  public String getName() {
    return myName;
  }

  public String getDescription() {
    return myDocumentation;
  }

  public boolean isGlobal() {
    return myGlobal;
  }

  public T getDefaultValue() {
    return myDefaultValue;
  }

  @Override
  public String toString() {
    return myName;
  }
}
