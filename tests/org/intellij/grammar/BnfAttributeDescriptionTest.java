package org.intellij.grammar;

import junit.framework.TestCase;

/**
 * @author gregsh
 */
public class BnfAttributeDescriptionTest extends TestCase {
  public void testAttributeDescriptions() {
    for (KnownAttribute attribute : KnownAttribute.getAttributes()) {
      assertNotNull("No description for attribute: " + attribute.getName(), attribute.getDescription());
    }
  }
}
