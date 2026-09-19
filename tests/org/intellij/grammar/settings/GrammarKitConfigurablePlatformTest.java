/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.settings;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.UIUtil;
import org.intellij.grammar.GrammarKitBundle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The settings page must not grow as wide as its longest hint: the hints wrap.
 */
public class GrammarKitConfigurablePlatformTest extends BasePlatformTestCase {

  public void testHintsWrapInsteadOfStretchingThePage() {
    JComponent panel = createPanel();
    String longest = longestContractLine();
    int unwrapped = panel.getFontMetrics(panel.getFont()).stringWidth(longest);
    assertTrue("the page is as wide as its longest hint: " + panel.getPreferredSize().width,
               panel.getPreferredSize().width < unwrapped);

    JLabel hint = hintFor(panel, longest);
    int lineHeight = hint.getFontMetrics(hint.getFont()).getHeight();
    assertTrue("the longest hint still fits on one line: " + hint.getPreferredSize(),
               hint.getPreferredSize().height > lineHeight);
  }

  public void testTheWholeContractIsOneSelectableHint() {
    String[] lines = contractLines();
    assertTrue("the contract is a single line: " + lines.length, lines.length > 1);
    JLabel hint = hintFor(createPanel(), lines[0]);
    for (String line : lines) {
      assertTrue("'" + line + "' is not in the same hint: " + hint.getText(),
                 rendered(hint).contains(line));
    }
  }

  public void testBacktickedSpansAreRenderedAsCode() {
    JLabel hint = hintFor(createPanel(), contractLines()[0]);
    assertFalse("the code markers are shown as is: " + hint.getText(), hint.getText().contains("`"));
    assertTrue(hint.getText(), hint.getText().contains("<code>$PROJECT_DIR$</code>"));
  }

  public void testTheContractKeepsTheNormalFontAndTheFieldCommentsAreSmaller() {
    JComponent panel = createPanel();
    JLabel contract = hintFor(panel, contractLines()[0]);
    assertEquals(JBFont.label().getSize(), contract.getFont().getSize());

    JLabel fieldComment = hintFor(panel, GrammarKitBundle.message("settings.grammar.kit.parser.cli.comment"));
    // Not strictly smaller: some look-and-feels render the small fonts as regular.
    assertEquals(JBFont.smallOrNewUiMedium().getSize(), fieldComment.getFont().getSize());
    assertTrue("the field comment is bigger than the contract: " + fieldComment.getFont(),
               fieldComment.getFont().getSize() <= contract.getFont().getSize());
  }

  public void testOnlyTheFieldCommentsAreGreyedOut() {
    JComponent panel = createPanel();
    JLabel contract = hintFor(panel, contractLines()[0]);
    JLabel fieldComment = hintFor(panel, GrammarKitBundle.message("settings.grammar.kit.parser.cli.comment"));
    assertEquals(UIUtil.getContextHelpForeground(), fieldComment.getForeground());
    assertFalse("the contract is greyed out too: " + contract.getForeground(),
                UIUtil.getContextHelpForeground().equals(contract.getForeground()));
  }

  public void testHintTextIsEscapedNotParsedAsHtml() {
    String text = GrammarKitBundle.message("settings.grammar.kit.cli.contract");
    assertTrue(text, text.contains("<path>"));
    JLabel hint = hintFor(createPanel(), contractLines()[0]);
    assertTrue(hint.getText(), hint.getText().contains("&lt;path&gt;"));
  }

  /** What the label shows, with the markup it was given taken back off. */
  private static String rendered(JLabel label) {
    return StringUtil.unescapeXmlEntities(StringUtil.removeHtmlTags(label.getText()));
  }

  /** The contract lines as they are rendered: without the markers around the code spans. */
  private static String[] contractLines() {
    String text = GrammarKitBundle.message("settings.grammar.kit.cli.contract").replace("`", "");
    return StringUtil.splitByLines(text);
  }

  /** The line that decides how wide the hint wants to be. */
  private static String longestContractLine() {
    String longest = "";
    for (String line : contractLines()) {
      if (line.length() > longest.length()) longest = line;
    }
    return longest;
  }

  private JComponent createPanel() {
    GrammarKitConfigurable configurable = new GrammarKitConfigurable(getProject());
    disposeOnTearDown(configurable::disposeUIResources);
    JComponent panel = configurable.createComponent();
    assertNotNull(panel);
    // Labels report a wrapped height only once they have been laid out at the page width.
    panel.setSize(panel.getPreferredSize());
    panel.doLayout();
    return panel;
  }

  /** The hint label rendering {@code text}, whatever markup it was wrapped into. */
  private static JLabel hintFor(Component root, String text) {
    List<JLabel> labels = new ArrayList<>();
    UIUtil.uiTraverser(root).filter(JLabel.class).addAllTo(labels);
    for (JLabel label : labels) {
      if (label.getText() != null && rendered(label).contains(text)) return label;
    }
    throw new AssertionError("no hint for '" + text + "' among " + labels.size() + " labels");
  }
}
