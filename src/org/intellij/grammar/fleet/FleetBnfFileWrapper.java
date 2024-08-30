/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.fleet;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfAttrs;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.impl.BnfFileImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.intellij.grammar.KnownAttribute.*;
import static org.intellij.grammar.fleet.FleetConstants.*;

//Wraps BnfFile to produce fleet-related attribute values.
//Implemented as BnfFileImpl extension to avoid implementation of all methods in BnfFile interface
public class FleetBnfFileWrapper extends BnfFileImpl implements BnfFile {

  private final Map<String, String> myFleetAttributeValuesSubstitution = Map.of(
    PARSER_UTIL_CLASS.getName(), GPUB_CLASS,
    ELEMENT_TYPE_CLASS.getName(), IELEMENTTYPE_CLASS,
    TOKEN_TYPE_CLASS.getName(), IELEMENTTYPE_CLASS
  );

  private final Map<String, String> myDefaultGeneratedNames = Map.of(
    PARSER_CLASS.getName(), PARSER_CLASS_DEFAULT,
    ELEMENT_TYPE_HOLDER_CLASS.getName(), ELEMENT_TYPE_HOLDER_DEFAULT
  );

  private final Set<String> mySuppressedFactories = new HashSet<>(Arrays.asList(ELEMENT_TYPE_FACTORY.getName(),
                                                                                TOKEN_TYPE_FACTORY.getName()));

  public FleetBnfFileWrapper(FileViewProvider viewProvider) {
    super(viewProvider);
  }

  @Override
  public String toString() {
    return "FleetBnfFile:" + getName();
  }

  @Override
  public @NotNull List<BnfRule> getRules() {
    return super.getRules();
  }

  @Override
  public @Nullable BnfRule getRule(@Nullable String ruleName) {
    return super.getRule(ruleName);
  }

  @Override
  public @NotNull List<BnfAttrs> getAttributes() {
    return super.getAttributes();
  }

  @Override
  public @Nullable BnfAttr findAttribute(@Nullable BnfRule rule, @NotNull KnownAttribute<?> knownAttribute, @Nullable String match) {
    return super.findAttribute(rule, knownAttribute, match);
  }

  @Override
  public <T> T findAttributeValue(@Nullable BnfRule rule, @NotNull KnownAttribute<T> knownAttribute, @Nullable String match) {
    //Bypass adjustment logic for the GENERATE attribute
    if (knownAttribute.getName().equals(GENERATE.getName())) {
      return super.findAttributeValue(rule, knownAttribute, match);
    }

    var attributeValue = super.findAttributeValue(null, KnownAttribute.GENERATE, null);
    var adjustPackages =
      !(attributeValue == null ||
        attributeValue.asMap().getOrDefault("adjustPackagesForFleet", "").equals("no"));

    if (myFleetAttributeValuesSubstitution.containsKey(knownAttribute.getName())) {
      if (hasAttributeValue(rule, knownAttribute, match)) {
        return (adjustPackages) ?
               adjustedValue(rule, knownAttribute, match) :
               super.findAttributeValue(rule, knownAttribute, match);
      }
      else {
        return (T)myFleetAttributeValuesSubstitution.get(knownAttribute.getName());
      }
    }

    ////If a generated element name has been requested, return value adjusted accordingly
    if (myDefaultGeneratedNames.containsKey(knownAttribute.getName())) {
      return (adjustPackages) ?
             adjustedValue(rule, knownAttribute, match) :
             super.findAttributeValue(rule, knownAttribute, match);
    }

    //If a factory attribute is requested, return null to force generation of non-factory methods
    if (mySuppressedFactories.contains(knownAttribute.getName())) {
      return null;
    }

    return super.findAttributeValue(rule, knownAttribute, match);
  }

  private <T> T adjustedValue(@Nullable BnfRule rule, @NotNull KnownAttribute<T> knownAttribute, @Nullable String match) {
    var origin = super.findAttributeValue(rule, knownAttribute, match);
    if (origin instanceof String && !((String)origin).startsWith(FLEET_NAMESPACE_PREFIX)) {
      return (T)(FLEET_NAMESPACE_PREFIX + origin);
    }
    return origin;
  }

  @Override
  public @NotNull FileType getFileType() {
    return super.getFileType();
  }
}
