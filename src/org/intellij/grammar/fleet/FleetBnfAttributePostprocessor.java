/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.fleet;

import com.intellij.openapi.util.Key;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.IAttributePostProcessor;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.intellij.grammar.KnownAttribute.*;
import static org.intellij.grammar.fleet.FleetConstants.*;

//Wraps BnfFile to produce fleet-related attribute values.
//Implemented as BnfFileImpl extension to avoid implementation of all methods in BnfFile interface
public class FleetBnfAttributePostprocessor implements IAttributePostProcessor {

  public static final Key<Boolean> GENERATE_FOR_FLEET = Key.create("GENERATE_FOR_FLEET");

  private final Map<String, String> myFleetAttributeValuesSubstitution = Map.of(
    PARSER_UTIL_CLASS.getName(), GPUB_CLASS,
    ELEMENT_TYPE_CLASS.getName(), IELEMENTTYPE_CLASS,
    TOKEN_TYPE_CLASS.getName(), IELEMENTTYPE_CLASS
  );

  private final Map<String, String> myDefaultGeneratedNames = Map.of(
    PARSER_CLASS.getName(), PARSER_CLASS_DEFAULT,
    ELEMENT_TYPE_HOLDER_CLASS.getName(), ELEMENT_TYPE_HOLDER_DEFAULT
  );

  private final List<String> mySuppressedFactories = List.of(ELEMENT_TYPE_FACTORY.getName(),
                                                                    TOKEN_TYPE_FACTORY.getName());

  @Override
  public <T> @Nullable T postProcessValue(@NotNull KnownAttribute<T> knownAttribute, @Nullable T value) {
    return findAttributeValue(value, knownAttribute);
  }

  public static BnfFile prepareForFleetGeneration(@NotNull BnfFile bnfFile) {
    bnfFile.putUserData(GENERATE_FOR_FLEET, true);
    bnfFile.putUserData(ATTRIBUTE_POSTPROCESSOR, new FleetBnfAttributePostprocessor());
    return bnfFile;
  }

  private static <T> T findAttributeValue(@Nullable T value,
                                          @NotNull KnownAttribute<T> knownAttribute) {
    //Bypass adjustment logic for the GENERATE attribute
    if (knownAttribute.getName().equals(GENERATE.getName())) {
      return value;
    }

    if (myFleetAttributeValuesSubstitution.containsKey(knownAttribute.getName())) {
      if (!knownAttribute.getDefaultValue().equals(value)) {
        return adjustedValue(value);
      }
      else {
        return (T)myFleetAttributeValuesSubstitution.get(knownAttribute.getName());
      }
    }

    ////If a generated element name has been requested, return value adjusted accordingly
    if (myDefaultGeneratedNames.containsKey(knownAttribute.getName())) {
      return adjustedValue(value);
    }

    //If a factory attribute is requested, return null to force generation of non-factory methods
    if (mySuppressedFactories.contains(knownAttribute.getName())) {
      return null;
    }

    return value;
  }

  private static <T> T adjustedValue(@Nullable T origin) {
    if (origin instanceof String && !((String)origin).startsWith(FLEET_NAMESPACE_PREFIX)) {
      return (T)(FLEET_NAMESPACE_PREFIX + origin);
    }
    return origin;
  }
}
