/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.parser

import com.intellij.lang.PsiBuilder
import org.intellij.grammar.config.Options

open class KotlinGeneratedParserUtilBase {
  companion object {
    private val MAX_RECURSION_LEVEL = Options.GPUB_MAX_LEVEL.get()
    private val MAX_VARIANTS_SIZE = 10000
    private val MAX_VARIANTS_TO_DISPLAY = 50
    private val MAX_ERROR_TOKEN_TEXT = 20

    private const val INITIAL_VARIANTS_SIZE: Int = 1000
    private const val VARIANTS_POOL_SIZE: Int = 10000
    private const val FRAMES_POOL_SIZE: Int = 500

    val TOKEN_ADVANCER: Parser = { builder, _ ->
      if (builder.eof()) false
      else {
        builder.advanceLexer()
        true
      }
    }
    val TRUE_CONDITION: Parser = { _, _ -> true }
  }
}

typealias Parser = (PsiBuilder, Int) -> Boolean
typealias Hook<T> = (PsiBuilder, PsiBuilder.Marker?, T) -> PsiBuilder.Marker?