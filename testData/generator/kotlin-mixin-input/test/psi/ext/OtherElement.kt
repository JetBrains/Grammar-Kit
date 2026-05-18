package test.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import test.psi.OtherElement
import test.stub.OtherElementStub

abstract class OtherElementImplMixin : MyStubbedBase<OtherElementStub>, OtherElement {
    constructor(node: ASTNode) : super(node)
    constructor(stub: OtherElementStub, elementType: IElementType) : super(stub, elementType)
}
