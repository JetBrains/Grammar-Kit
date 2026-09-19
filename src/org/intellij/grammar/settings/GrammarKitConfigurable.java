/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.HtmlBuilder;
import com.intellij.openapi.util.text.HtmlChunk;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.intellij.grammar.GrammarKitBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.LabelUI;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

/**
 * Settings page for the two generator command lines. Leaving a field empty keeps the
 * corresponding action generating in-process.
 */
public class GrammarKitConfigurable implements Configurable {

  /** How many characters a hint line may hold before it wraps, as in the platform's own comments. */
  private static final int MAX_HINT_LENGTH = 70;
  /** What a hint text puts around the spans to be rendered as code. */
  private static final String CODE_MARKER = "`";

  private final Project myProject;

  private ExpandableTextField myParserCli;
  private ExpandableTextField myJFlexCli;

  public GrammarKitConfigurable(@NotNull Project project) {
    myProject = project;
  }

  @Override
  public @Nls @NotNull String getDisplayName() {
    return GrammarKitBundle.message("settings.grammar.kit.display.name");
  }

  @Override
  public @Nullable JComponent createComponent() {
    myParserCli = new ExpandableTextField();
    myJFlexCli = new ExpandableTextField();

    JPanel panel = new JPanel(new VerticalLayout(JBUI.scale(6)));
    panel.add(new JBLabel(GrammarKitBundle.message("settings.grammar.kit.parser.cli.label")));
    panel.add(myParserCli);
    panel.add(comment(GrammarKitBundle.message("settings.grammar.kit.parser.cli.comment")));
    panel.add(new JBLabel(GrammarKitBundle.message("settings.grammar.kit.jflex.cli.label")));
    panel.add(myJFlexCli);
    panel.add(comment(GrammarKitBundle.message("settings.grammar.kit.jflex.cli.comment")));
    // One label, so that the whole contract can be selected and copied in a single go.
    panel.add(hint(GrammarKitBundle.message("settings.grammar.kit.cli.contract")));
    return panel;
  }

  /**
   * A hint that keeps the newlines of {@code text} and wraps the lines that are
   * still too long, instead of stretching the settings page. Text in paired backticks is
   * rendered as code, the rest is plain text and is escaped: a label that wraps renders HTML.
   * Bundle strings still must avoid {@code &}, which the bundle eats as a mnemonic marker.
   */
  private static @NotNull JLabel hint(@NotNull @Nls String text) {
    return hint(text, JBFont::label, null);
  }

  /**
   * A hint in the given font and color, {@code null} for the plain label one.
   * The font is re-derived in setUI, to survive a look-and-feel change.
   */
  private static @NotNull JLabel hint(@NotNull @Nls String text,
                                      @NotNull Supplier<? extends JBFont> font,
                                      @Nullable Color foreground) {
    String[] lines = StringUtil.splitByLines(text);
    JBLabel label = new JBLabel() {
      @Override
      public void setUI(LabelUI ui) {
        super.setUI(ui);
        setFont(font.get());
      }
    };
    if (foreground != null) label.setForeground(foreground);
    // The font and the colors are read while the copyable pane builds its stylesheet: set them first.
    label.setAllowAutoWrapping(true);
    label.setCopyable(true);
    label.setFocusable(true);
    label.setVerticalTextPosition(SwingConstants.TOP);

    HtmlChunk.Element div = HtmlChunk.div();
    String longest = "";
    for (String line : lines) {
      String plain = line.replace(CODE_MARKER, "");
      if (plain.length() > longest.length()) longest = plain;
    }
    if (longest.length() > MAX_HINT_LENGTH) {
      // An explicit width is what makes the pane wrap instead of asking for one very wide line.
      div = div.attr("width", label.getFontMetrics(label.getFont()).stringWidth(longest.substring(0, MAX_HINT_LENGTH)));
    }
    List<HtmlChunk> texts = ContainerUtil.map(lines, GrammarKitConfigurable::markup);
    label.setText(new HtmlBuilder().appendWithSeparators(HtmlChunk.br(), texts)
                    .wrapWith(div).wrapWith("body").wrapWith("html").toString());
    return label;
  }

  /** One hint line, with the backtick-quoted spans turned into code and everything else escaped. */
  private static @NotNull HtmlChunk markup(@NotNull @Nls String line) {
    HtmlBuilder result = new HtmlBuilder();
    String[] parts = line.split(CODE_MARKER, -1);
    for (int i = 0; i < parts.length; i++) {
      if (parts[i].isEmpty()) continue;
      HtmlChunk part = HtmlChunk.text(parts[i]);
      // An unpaired marker leaves the tail plain rather than swallowing the rest of the line.
      result.append(i % 2 == 1 && i < parts.length - 1 ? part.wrapWith("code") : part);
    }
    return result.toFragment();
  }

  /** A hint that closes off a field: smaller and greyed out, with room before whatever comes next. */
  private static @NotNull JComponent comment(@NotNull @Nls String text) {
    JLabel label = hint(text, JBFont::smallOrNewUiMedium, UIUtil.getContextHelpForeground());
    label.setBorder(JBUI.Borders.emptyBottom(6));
    return label;
  }

  @Override
  public boolean isModified() {
    // The platform may call this after disposeUIResources, e.g. while closing the dialog.
    if (myParserCli == null || myJFlexCli == null) return false;
    GrammarKitSettings settings = GrammarKitSettings.getInstance(myProject);
    return !settings.getParserCli().equals(myParserCli.getText().trim()) ||
           !settings.getJFlexCli().equals(myJFlexCli.getText().trim());
  }

  @Override
  public void apply() {
    if (myParserCli == null || myJFlexCli == null) return;
    GrammarKitSettings settings = GrammarKitSettings.getInstance(myProject);
    settings.setParserCli(myParserCli.getText());
    settings.setJFlexCli(myJFlexCli.getText());
  }

  @Override
  public void reset() {
    if (myParserCli == null || myJFlexCli == null) return;
    GrammarKitSettings settings = GrammarKitSettings.getInstance(myProject);
    myParserCli.setText(settings.getParserCli());
    myJFlexCli.setText(settings.getJFlexCli());
  }

  @Override
  public void disposeUIResources() {
    myParserCli = null;
    myJFlexCli = null;
  }
}
