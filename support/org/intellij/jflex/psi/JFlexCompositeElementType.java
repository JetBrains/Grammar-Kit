package org.intellij.jflex.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.jflex.JFlexLanguage;

/**
 * @author gregsh
 */
public class JFlexCompositeElementType extends IElementType {
  public JFlexCompositeElementType(String debug) {
    super(debug, JFlexLanguage.INSTANCE);
  }
}
