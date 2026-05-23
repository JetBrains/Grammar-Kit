/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.editor;

import com.intellij.codeInsight.hints.declarative.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.intellij.grammar.BnfPaths;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfAttributes;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfListEntry;
import org.intellij.grammar.psi.BnfStringLiteralExpression;
import org.intellij.grammar.psi.BnfValueList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * Shows the absolute filesystem location that an {@code *InputPath} or {@code *OutputPath} BNF
 * attribute resolves to. Path resolution is delegated to {@link BnfPaths}, which is the single
 * source of truth for path-attribute resolution across the IDE and the build context.
 *
 * <p>Each segment of the displayed path is a clickable link to the corresponding directory: a click
 * navigates via the platform's built-in {@code psi.pointer.navigation.handler}. When the path's
 * tail does not exist on disk, the unresolved segments fall back to plain text.
 *
 * <p>Handles both forms: a single string literal ({@code psiInputPath="../psi"}) and a
 * {@link BnfValueList list expression} ({@code psiInputPath=["../psi" "../shared"]}) — one hint
 * per quoted path in the latter case.
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
      BnfFile bnfFile = (BnfFile)attr.getContainingFile();

      if (expression instanceof BnfStringLiteralExpression literal) {
        emitInlayForLiteral(literal, bnfFile, sink);
      }
      else if (expression instanceof BnfValueList list) {
        for (BnfListEntry entry : list.getListEntryList()) {
          BnfStringLiteralExpression literal = entry.getLiteralExpression();
          if (literal != null) emitInlayForLiteral(literal, bnfFile, sink);
        }
      }
    }

    private static void emitInlayForLiteral(@NotNull BnfStringLiteralExpression literal,
                                            @NotNull BnfFile bnfFile,
                                            @NotNull InlayTreeSink sink) {
      String value = BnfAttributes.getLiteralValue(literal);
      if (value == null || value.isEmpty()) return;

      Path resolved = BnfPaths.resolveOnDisk(bnfFile, value);
      if (resolved == null) return;

      Project project = bnfFile.getProject();
      String absolute = resolved.toString();

      int offset = literal.getTextRange().getEndOffset();
      InlineInlayPosition position = new InlineInlayPosition(offset, true, 0);
      Function1<PresentationTreeBuilder, Unit> builderF = builder -> {
        emitClickableSegments(builder, resolved, project);
        return Unit.INSTANCE;
      };
      sink.addPresentation(position, null, absolute, true, builderF);
    }

    private static void emitClickableSegments(@NotNull PresentationTreeBuilder builder,
                                              @NotNull Path absolute,
                                              @NotNull Project project) {
      LocalFileSystem lfs = LocalFileSystem.getInstance();
      PsiManager psiManager = PsiManager.getInstance(project);
      SmartPointerManager pointerManager = SmartPointerManager.getInstance(project);

      Path root = absolute.getRoot();
      Path acc = root;
      String prevText = "";

      // Emit the path's root segment (e.g. "/" on Unix, "C:\" on Windows) as its own run.
      if (root != null) {
        prevText = root.toString();
        emitChunked(builder, prevText, buildAction(acc, lfs, psiManager, pointerManager));
      }

      int nameCount = absolute.getNameCount();
      for (int i = 0; i < nameCount; i++) {
        acc = (acc == null) ? absolute.getName(i) : acc.resolve(absolute.getName(i));
        String accText = acc.toString();
        // The delta naturally carries the file separator for every segment except the first one
        // after the root (the root's toString already ends with a separator on every JDK platform).
        String segmentText = accText.substring(prevText.length());
        emitChunked(builder, segmentText, buildAction(acc, lfs, psiManager, pointerManager));
        prevText = accText;
      }
    }

    private static @Nullable InlayActionData buildAction(@Nullable Path path,
                                                         @NotNull LocalFileSystem lfs,
                                                         @NotNull PsiManager psiManager,
                                                         @NotNull SmartPointerManager pointerManager) {
      if (path == null) return null;
      VirtualFile vf = lfs.findFileByPath(FileUtil.toSystemIndependentName(path.toString()));
      if (vf == null) return null;
      PsiDirectory dir = psiManager.findDirectory(vf);
      if (dir == null) return null;
      SmartPsiElementPointer<PsiDirectory> pointer = pointerManager.createSmartPsiElementPointer(dir);
      return new InlayActionData(new PsiPointerInlayActionPayload(pointer),
                                 PsiPointerInlayActionNavigationHandler.HANDLER_ID);
    }

    private static void emitChunked(@NotNull PresentationTreeBuilder builder,
                                    @NotNull String text,
                                    @Nullable InlayActionData action) {
      // Inline declarative hints truncate each text node to 30 chars
      // (PresentationTreeBuilderImpl.MAX_SEGMENT_TEXT_LENGTH); split into chunks so the whole
      // path is visible. All chunks of the same logical segment share the same action so they
      // behave as one link.
      for (int i = 0; i < text.length(); i += MAX_SEGMENT_LENGTH) {
        builder.text(text.substring(i, Math.min(i + MAX_SEGMENT_LENGTH, text.length())), action);
      }
    }
  }
}
