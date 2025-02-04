package org.intellij.grammar.syntax;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.IAttributePostProcessor;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static org.intellij.grammar.KnownAttribute.*;

public class SyntaxBnfAttributePostProcessor implements IAttributePostProcessor {

  public static final Key<Boolean> GENERATE_WITH_SYNTAX = Key.create("GENERATE_WITH_SYNTAX");

  private static final Map<String, String> myAttributesValuesSubstitution = Map.of(
    TOKEN_TYPE_CLASS.getName(),SyntaxConstants.SYNTAX_ELEMENT_TYPE,
    ELEMENT_TYPE_CLASS.getName(), SyntaxConstants.SYNTAX_ELEMENT_TYPE,
    PARSER_UTIL_CLASS.getName(), SyntaxConstants.GPUB_CLASS
  );

  public static PsiFile prepareForGeneration(BnfFile file) {
    file.putUserData(GENERATE_WITH_SYNTAX, true);
    file.putUserData(ATTRIBUTE_POSTPROCESSOR, new SyntaxBnfAttributePostProcessor());
    return file;
  }


  @Override
  public <T> @Nullable T postProcessValue(@NotNull KnownAttribute<T> knownAttribute, @Nullable T value) {
    return findAttributeValue(value, knownAttribute);
  }


  private static <T> T findAttributeValue(@Nullable T value,
                                          @NotNull KnownAttribute<T> knownAttribute) {
    //Bypass adjustment logic for the GENERATE attribute
    if (knownAttribute.getName().equals(GENERATE.getName())) {
      return value;
    }

    if (myAttributesValuesSubstitution.containsKey(knownAttribute.getName())) {
      if (!knownAttribute.getDefaultValue().equals(value)) {
        return value;
      }
      else {
        return (T)myAttributesValuesSubstitution.get(knownAttribute.getName());
      }
    }

    return value;
  }
}