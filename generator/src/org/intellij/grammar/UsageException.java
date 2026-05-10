/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

final class UsageException extends Exception {
  final int exitCode;

  UsageException(int exitCode, String message) {
    super(message);
    this.exitCode = exitCode;
  }
}
