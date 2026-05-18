package test.psi.ext

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class ParentImplMixin(node: ASTNode) : ASTWrapperPsiElement(node)
