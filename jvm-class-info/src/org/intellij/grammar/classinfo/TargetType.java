/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

/**
 * Mirror of {@link java.lang.annotation.ElementType} for use inside {@link ClassSymbol#annotationTargets}.
 * Decoupled from the JDK enum so the model stays JDK-version-independent and so Kotlin / ASM
 * providers can populate it without each importing {@code ElementType}.
 * <p>
 * The set on a {@link ClassSymbol} answers a single question: does this annotation type apply to
 * type-use positions? Callers compare against {@link #TYPE_USE} via {@link TypeUseAnnotationLifter}.
 */
public enum TargetType {
  TYPE,
  FIELD,
  METHOD,
  PARAMETER,
  CONSTRUCTOR,
  LOCAL_VARIABLE,
  ANNOTATION_TYPE,
  PACKAGE,
  TYPE_PARAMETER,
  TYPE_USE,
  MODULE,
  RECORD_COMPONENT
}
