/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.fleet;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.ClearableLazyValue;
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
import static org.intellij.grammar.generator.fleet.FleetConstants.*;


public class FleetBnfFileImpl extends BnfFileImpl implements BnfFile {

  private final Map<String, String> myOverriddenDefaults = Map.of(
    PARSER_CLASS.getName(), PARSER_CLASS_DEFAULT,
    PARSER_UTIL_CLASS.getName(), GPUB_CLASS,
    ELEMENT_TYPE_CLASS.getName(), IELEMENTTYPE_CLASS,
    TOKEN_TYPE_CLASS.getName(), IELEMENTTYPE_CLASS,
    ELEMENT_TYPE_HOLDER_CLASS.getName(), ELEMENT_TYPE_HOLDER_DEFAULT
  );

  private final ClearableLazyValue<Map<String, String>> myOverriddenAttributeValues =
    lazyValue(FleetBnfFileImpl::calcFleetAttributeOverridingValues);

  public FleetBnfFileImpl(FileViewProvider viewProvider) {
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
    var map = super.findAttributeValue(null, KnownAttribute.GENERATE, null).asMap();
    boolean adjustPackages = !map.getOrDefault(ADJUST_PACKAGES_FOR_FLEET.getName(), "").equals("no");
    if (myOverriddenDefaults.containsKey(knownAttribute.getName())) {
      if (adjustPackages){
        return (hasAttributeValue(knownAttribute)) ?
               adjustedValue(rule, knownAttribute, match) :
               (T)myOverriddenDefaults.get(knownAttribute.getName());
      }
    }

    //Class
    if (myOverriddenAttributeValues.getValue().containsKey(knownAttribute.getName())) {
      return (T)myOverriddenAttributeValues.getValue().get(knownAttribute.getName());
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

  private static Map<String, String> calcFleetAttributeOverridingValues() {
    var valueMap = new HashMap<String, String>();
    valueMap.put(ELEMENT_TYPE_FACTORY.getName(), null);
    valueMap.put(TOKEN_TYPE_FACTORY.getName(), null);
    valueMap.put(ELEMENT_TYPE_CLASS.getName(), IELEMENTTYPE_CLASS);
    valueMap.put(TOKEN_TYPE_CLASS.getName(), IELEMENTTYPE_CLASS);
    return valueMap;
  }
}
