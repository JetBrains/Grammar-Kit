package org.intellij.grammar.syntax;

import com.intellij.openapi.util.Key;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.IAttributePostProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static org.intellij.grammar.KnownAttribute.ELEMENT_TYPE_CLASS;
import static org.intellij.grammar.KnownAttribute.GENERATE;

class SyntaxBnfAttributePostProcessor implements IAttributePostProcessor {

  public static final Key<Boolean> GENERATE_WITH_SYNTAX = Key.create("GENERATE_WITH_SYNTAX");

  private static final Map<String, String> myAttributesValuesSubstitution = Map.of(
    ELEMENT_TYPE_CLASS.getName(), SyntaxConstants.SYNTAX_ELEMENT_TYPE
  );


  @Override
  public <T> @Nullable T postProcessValue(@NotNull KnownAttribute<T> knownAttribute, @Nullable T value) {
    return IAttributePostProcessor.super.postProcessValue(knownAttribute, value);
  }


  private static <T> T findAttributeValue(@Nullable T value,
                                          @NotNull KnownAttribute<T> knownAttribute) {
    //Bypass adjustment logic for the GENERATE attribute
    if (knownAttribute.getName().equals(GENERATE.getName())) {
      return value;
    }

    if (myAttributesValuesSubstitution.containsKey(knownAttribute.getName())) {
      if (!knownAttribute.getDefaultValue().equals(value)) {
        return adjustedValue(value);
      }
      else {
        return (T)myAttributesValuesSubstitution.get(knownAttribute.getName());
      }
    }

    //If a generated element name has been requested, return value adjusted accordingly
    if (myDefaultGeneratedNames.containsKey(knownAttribute.getName())) {
      return adjustedValue(value);
    }

//    If a factory attribute is requested, return null to force generation of non-factory methods
//    if (mySuppressedFactories.contains(knownAttribute.getName())) {
//      return null;
//    }

    return value;
  }
}