/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.editor;

import com.intellij.codeInsight.hints.declarative.*;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Exercises {@link BnfPathAttributeInlayHintsProvider}'s clickable per-segment output. Drives the
 * provider's collector directly with a capturing {@link InlayTreeSink} so assertions can target
 * each text run + its navigation payload.
 */
public class BnfPathInlayHintsTest extends JavaCodeInsightFixtureTestCase {

  public void testSingleInputPathHasClickableSegmentsToTargetDir() throws Exception {
    VirtualFile grammarsDir = myFixture.getTempDirFixture().findOrCreateDir("grammars");
    PsiFile bnf = myFixture.configureByText("a.bnf", "{ inputPath=\"grammars\" }\nr ::=");
    String expected = Path.of(bnf.getVirtualFile().getParent().getPath())
      .resolve("grammars").normalize().toString();

    List<Captured> ps = collectPresentations();
    assertEquals(1, ps.size());
    Captured p = ps.get(0);
    assertEquals(expected, p.tooltip);
    assertEquals(expected, p.joinedText());
    PsiElement last = p.lastTarget();
    assertNotNull("the final segment must be clickable", last);
    assertEquals(grammarsDir, ((PsiDirectory)last).getVirtualFile());
  }

  public void testListPsiInputPathProducesOneInlayPerEntry() throws Exception {
    VirtualFile a = myFixture.getTempDirFixture().findOrCreateDir("a");
    VirtualFile b = myFixture.getTempDirFixture().findOrCreateDir("b");
    myFixture.configureByText("a.bnf", "{ psiInputPath=[\"a\" \"b\"] }\nr ::=");
    List<Captured> ps = collectPresentations();
    assertEquals(2, ps.size());
    assertEquals(a, ((PsiDirectory)ps.get(0).lastTarget()).getVirtualFile());
    assertEquals(b, ((PsiDirectory)ps.get(1).lastTarget()).getVirtualFile());
  }

  public void testUnresolvedTailSegmentsArePlainText() {
    myFixture.configureByText("a.bnf", "{ inputPath=\"doesnotexist/deeper\" }\nr ::=");
    List<Captured> ps = collectPresentations();
    Captured p = ps.get(0);
    // Walk entries in reverse: the trailing "doesnotexist" and "/deeper" segments
    // do not exist on disk → no action.
    int n = p.entries.size();
    assertNull("the last segment must be plain text", p.entries.get(n - 1).action);
    assertNull("the second-to-last segment must be plain text", p.entries.get(n - 2).action);
    // The earlier segments (bnf parent dir prefix) must contain at least one clickable run.
    assertTrue("at least one earlier segment is clickable",
               p.entries.stream().anyMatch(e -> e.action != null));
  }

  public void testFinalSegmentPayloadAndHandlerWiring() throws Exception {
    VirtualFile dir = myFixture.getTempDirFixture().findOrCreateDir("grammars");
    myFixture.configureByText("a.bnf", "{ inputPath=\"grammars\" }\nr ::=");
    Captured p = collectPresentations().get(0);

    InlayActionData action = lastAction(p);
    assertNotNull("expected a clickable final segment", action);
    assertEquals(PsiPointerInlayActionNavigationHandler.HANDLER_ID, action.getHandlerId());

    PsiPointerInlayActionPayload payload = (PsiPointerInlayActionPayload)action.getPayload();
    PsiDirectory target = (PsiDirectory)payload.getPointer().getElement();
    assertEquals(dir, target.getVirtualFile());

    // The platform must have a handler registered for the documented id; that is what consumes
    // the click on this inlay segment.
    assertNotNull("navigation handler must be registered",
                  InlayActionHandler.Companion.getActionHandler(action.getHandlerId()));
  }

  // ---- Helpers -------------------------------------------------------------

  private @NotNull List<Captured> collectPresentations() {
    BnfPathAttributeInlayHintsProvider provider = new BnfPathAttributeInlayHintsProvider();
    PsiFile file = myFixture.getFile();
    InlayHintsCollector collector = provider.createCollector(file, myFixture.getEditor());
    assertNotNull("provider must return a collector for BnfFile", collector);
    SharedBypassCollector bypass = (SharedBypassCollector)collector;
    CapturingSink sink = new CapturingSink();
    ReadAction.run(() -> PsiTreeUtil.processElements(file, element -> {
      bypass.collectFromElement(element, sink);
      return true;
    }));
    return sink.captured;
  }

  private static @Nullable InlayActionData lastAction(@NotNull Captured p) {
    InlayActionData last = null;
    for (Entry e : p.entries) {
      if (e.action != null) last = e.action;
    }
    return last;
  }

  private static final class Entry {
    final String text;
    final InlayActionData action;

    Entry(@NotNull String text, @Nullable InlayActionData action) {
      this.text = text;
      this.action = action;
    }
  }

  private static final class Captured {
    final String tooltip;
    final List<Entry> entries = new ArrayList<>();
    final PresentationTreeBuilder builder = new PresentationTreeBuilder() {
      @Override
      public void list(@NotNull Function1<? super PresentationTreeBuilder, Unit> builder) {
        builder.invoke(this);
      }

      @Override
      public void collapsibleList(@NotNull CollapseState state,
                                  @NotNull Function1<? super CollapsiblePresentationTreeBuilder, Unit> expandedState,
                                  @NotNull Function1<? super CollapsiblePresentationTreeBuilder, Unit> collapsedState) {
        // Not used by the provider under test.
      }

      @Override
      public void text(@NotNull String text, @Nullable InlayActionData actionData) {
        entries.add(new Entry(text, actionData));
      }

      @Override
      public void clickHandlerScope(@NotNull InlayActionData actionData,
                                    @NotNull Function1<? super PresentationTreeBuilder, Unit> builder) {
        builder.invoke(this);
      }
    };

    Captured(@Nullable String tooltip) {
      this.tooltip = tooltip;
    }

    @NotNull String joinedText() {
      StringBuilder sb = new StringBuilder();
      for (Entry e : entries) sb.append(e.text);
      return sb.toString();
    }

    @Nullable PsiElement lastTarget() {
      InlayActionData last = null;
      for (Entry e : entries) {
        if (e.action != null) last = e.action;
      }
      if (last == null) return null;
      return ((PsiPointerInlayActionPayload)last.getPayload()).getPointer().getElement();
    }
  }

  private static final class CapturingSink implements InlayTreeSink {
    final List<Captured> captured = new ArrayList<>();

    @Override
    public void addPresentation(@NotNull InlayPosition position,
                                @Nullable List<InlayPayload> payloads,
                                @Nullable String tooltip,
                                boolean hasBackground,
                                @NotNull Function1<? super PresentationTreeBuilder, Unit> builder) {
      record(tooltip, builder);
    }

    @Override
    public void whenOptionEnabled(@NotNull String optionId, @NotNull Function0<Unit> block) {
      block.invoke();
    }

    private void record(@Nullable String tooltip,
                        @NotNull Function1<? super PresentationTreeBuilder, Unit> builder) {
      Captured pres = new Captured(tooltip);
      builder.invoke(pres.builder);
      captured.add(pres);
    }
  }
}
