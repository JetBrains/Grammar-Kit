package test.psi.ext

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IElementType

abstract class MyStubbedBase<StubT : StubElement<*>> : StubBasedPsiElementBase<StubT> {
    constructor(node: ASTNode) : super(node)
    constructor(stub: StubT, nodeType: IElementType) : super(stub, nodeType)
}
