package org.intellij.grammar.syntax;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.IAttributePostProcessor;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.intellij.grammar.KnownAttribute.*;

public class SyntaxBnfAttributePostProcessor implements IAttributePostProcessor {

  public static final Key<Boolean> GENERATE_WITH_SYNTAX = Key.create("GENERATE_WITH_SYNTAX");

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
    if (!knownAttribute.getName().equals(PARSER_UTIL_CLASS.getName())) {
      return value;
    }

    if (!knownAttribute.getDefaultValue().equals(value)) {
      return value;
    }

    return (T)SyntaxConstants.RUNTIME_CLASS;
  }
}