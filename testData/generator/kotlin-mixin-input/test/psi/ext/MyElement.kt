package test.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import test.psi.MyElement
import test.stub.MyElementStub

abstract class MyElementImplMixin : MyStubbedBase<MyElementStub>, MyElement {
    constructor(node: ASTNode) : super(node)
    constructor(stub: MyElementStub, elementType: IElementType) : super(stub, elementType)

    override fun getNameIdentifier(): PsiElement? {
        return null
    }
}
