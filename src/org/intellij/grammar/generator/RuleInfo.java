/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import java.util.Set;

class RuleInfo {
  final String name;
  final boolean isFake;
  final String elementType;
  final String parserClass;
  final String intfPackage;
  final String implPackage;
  final String intfClass;
  final String implClass;
  final String mixin;
  final String stub;
  String realStubClass;
  Set<String> superInterfaces;
  boolean mixedAST;
  String realSuperClass;
  boolean isAbstract;
  boolean isInElementType;

  RuleInfo(String name, boolean isFake,
           String elementType, String parserClass,
           String intfPackage, String implPackage,
           String intfClass, String implClass, String mixin, String stub) {
    this.name = name;
    this.isFake = isFake;
    this.elementType = elementType;
    this.parserClass = parserClass;
    this.intfPackage = intfPackage;
    this.implPackage = implPackage;
    this.stub = stub;
    this.intfClass = intfPackage + "." + intfClass;
    this.implClass = implPackage + "." + implClass;
    this.mixin = mixin;
  }
}
