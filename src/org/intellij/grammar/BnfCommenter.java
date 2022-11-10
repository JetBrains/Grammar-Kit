/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar;

import com.intellij.lang.CodeDocumentationAwareCommenter;
import com.intellij.psi.PsiComment;
import com.intellij.psi.tree.IElementType;

/**
 * @author gregsh
 */
public class BnfCommenter implements CodeDocumentationAwareCommenter {
  @Override
  public String getLineCommentPrefix() {
    return "//";
  }

  @Override
  public String getBlockCommentPrefix() {
    return "/*";
  }

  @Override
  public String getBlockCommentSuffix() {
    return "*/";
  }

  @Override
  public String getCommentedBlockCommentPrefix() {
    return null;
  }

  @Override
  public String getCommentedBlockCommentSuffix() {
    return null;
  }

  @Override
  public IElementType getLineCommentTokenType() {
    return BnfParserDefinition.BNF_LINE_COMMENT;
  }

  @Override
  public IElementType getBlockCommentTokenType() {
    return BnfParserDefinition.BNF_BLOCK_COMMENT;
  }

  @Override
  public IElementType getDocumentationCommentTokenType() {
    return null;
  }

  @Override
  public String getDocumentationCommentPrefix() {
    return null;
  }

  @Override
  public String getDocumentationCommentLinePrefix() {
    return null;
  }

  @Override
  public String getDocumentationCommentSuffix() {
    return null;
  }

  @Override
  public boolean isDocumentationComment(PsiComment element) {
    return false;
  }
}
