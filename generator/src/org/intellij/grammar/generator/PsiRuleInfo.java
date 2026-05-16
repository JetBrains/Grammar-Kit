/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import java.util.Set;

/**
 * Mutable PSI-only state for a BNF rule, populated by
 * {@link JavaPsiGenerator#calcRealSuperClasses} and read during PSI emission.
 * Lives separately from {@link RuleInfo} because the latter is grammar-derived
 * and immutable, while these fields are computed during PSI generation only.
 */
final class PsiRuleInfo {
  Set<String> superInterfaces;
  boolean mixedAST;
  String realSuperClass;
}
