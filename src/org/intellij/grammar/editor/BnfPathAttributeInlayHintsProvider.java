/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.editor;

import com.intellij.codeInsight.hints.declarative.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.intellij.grammar.BnfPaths;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfAttributes;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfStringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * Shows the absolute filesystem location that an {@code *InputPath} or {@code *OutputPath} BNF
 * attribute resolves to. Path resolution is delegated to {@link BnfPaths}, which is the single
 * source of truth for path-attribute resolution across the IDE and the build context.
 */
final class BnfPathAttributeInlayHintsProvider implements InlayHintsProvider, DumbAware {

  private static final int MAX_SEGMENT_LENGTH = 30;

  @Override
  public @Nullable InlayHintsCollector createCollector(@NotNull PsiFile file, @NotNull Editor editor) {
    return file instanceof BnfFile ? new Collector() : null;
  }

  private static final class Collector implements SharedBypassCollector {
    @Override
    public void collectFromElement(@NotNull PsiElement element, @NotNull InlayTreeSink sink) {
      if (!(element instanceof BnfAttr attr)) return;
      if (BnfPaths.pathAttributeByName(attr.getName()) == null) return;

      BnfExpression expression = attr.getExpression();
      if (!(expression instanceof BnfStringLiteralExpression literal)) return;

      String value = BnfAttributes.getLiteralValue(literal);
      if (value == null || value.isEmpty()) return;

      Path resolved = BnfPaths.resolveOnDisk((BnfFile)attr.getContainingFile(), value);
      if (resolved == null) return;

      String absolute = resolved.toString();

      int offset = literal.getTextRange().getEndOffset();
      InlineInlayPosition position = new InlineInlayPosition(offset, true, 0);
      Function1<PresentationTreeBuilder, Unit> builderF = builder -> {
        // Inline declarative hints truncate each text node to 30 chars
        // (PresentationTreeBuilderImpl.MAX_SEGMENT_TEXT_LENGTH); split into chunks to keep the
        // whole path visible.
        for (int i = 0; i < absolute.length(); i += MAX_SEGMENT_LENGTH) {
          builder.text(absolute.substring(i, Math.min(i + MAX_SEGMENT_LENGTH, absolute.length())), null);
        }
        return Unit.INSTANCE;
      };
      sink.addPresentation(position, null, null, true, builderF);
    }
  }
}
