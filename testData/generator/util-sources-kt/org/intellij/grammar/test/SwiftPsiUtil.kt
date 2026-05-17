package org.intellij.grammar.test

import com.intellij.util.IncorrectOperationException
import test.psi.SwiftStatement

@Throws(IncorrectOperationException::class)
fun <S : SwiftStatement> replaceWithStatement(self: SwiftStatement, newStatement: S, reformat: Boolean): S = newStatement
