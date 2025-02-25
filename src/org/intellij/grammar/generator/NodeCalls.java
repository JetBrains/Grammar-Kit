/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Daniil Ovchinnikov
 */
public final class NodeCalls {
  private NodeCalls() {
  }

  @FunctionalInterface
  interface NodeCall {

    @NotNull
    String render(@NotNull Renderer renderer, @NotNull Names names);
  }

  @FunctionalInterface
  interface NodeArgument {

    default boolean referencesMetaParameter() {
      return false;
    }

    @NotNull
    String render(@NotNull Renderer renderer);
  }

  record ConsumeTokenCall(
    @NotNull ParserGeneratorUtil.ConsumeType consumeType,
    @NotNull String token
  ) implements NodeCall {
    @Override
    public @NotNull String render(@NotNull Renderer renderer, @NotNull Names names) {
      return String.format("%s(%s, %s)", consumeType.getMethodName(), names.builder, token);
    }
  }

  record ConsumeTokenChoiceCall(
    @NotNull ParserGeneratorUtil.ConsumeType consumeType,
    @NotNull String tokenSetName
  ) implements NodeCall {
    @Override
    public @NotNull String render(@NotNull Renderer renderer, @NotNull Names names) {
      return String.format("%s(%s, %s)", consumeType.getMethodName(), names.builder, tokenSetName);
    }
  }

  record ConsumeTokensCall(
    @NotNull String methodName,
    int pin,
    @NotNull List<String> tokens
  ) implements NodeCall {
    @Override
    public @NotNull String render(@NotNull Renderer renderer, @NotNull Names names) {
      return String.format("%s(%s, %d, %s)", methodName, names.builder, pin, StringUtil.join(tokens, ", "));
    }
  }

  record ExpressionMethodCall(@NotNull String methodName, int priority) implements NodeCall {
    @Override
    public @NotNull String render(@NotNull Renderer renderer, @NotNull Names names) {
      return String.format("%s(%s, %s + 1, %d)", methodName, names.builder, names.level, priority);
    }
  }

  static class MetaMethodCall extends MethodCallWithArguments {

    private final @Nullable String targetClassName;

    MetaMethodCall(@Nullable String targetClassName, @NotNull String methodName, @NotNull List<NodeArgument> arguments) {
      super(methodName, arguments);
      this.targetClassName = targetClassName;
    }

    boolean referencesMetaParameter() {
      return ContainerUtil.exists(arguments, NodeArgument::referencesMetaParameter);
    }

    @Nullable
    String getTargetClassName() {
      return targetClassName;
    }

    @Override
    protected @NotNull String getMethodRef() {
      String ref = super.getMethodRef();
      return targetClassName == null ? ref : String.format("%s.%s", targetClassName, ref);
    }
  }

  record MetaMethodCallArgument(MetaMethodCall call) implements NodeArgument {

    MetaMethodCallArgument(@NotNull MetaMethodCall call) {
      this.call = call;
    }

    @Override
    public boolean referencesMetaParameter() {
      return true;
    }

    private @NotNull String getMethodRef(@NotNull Renderer renderer) {
      String ref = renderer.getWrapperParserMetaMethodName(call.methodName);
      String className = call.getTargetClassName();
      return className == null ? ref : String.format("%s.%s", className, ref);
    }

    @Override
    public @NotNull String render(@NotNull Renderer renderer) {
      String arguments = String.join(", ", ContainerUtil.map(call.arguments, argument -> argument.render(renderer)));
      return String.format("%s(%s)", getMethodRef(renderer), arguments);
    }
  }

  record MetaParameterCall(String metaParameterName) implements NodeCall {
    @Override
    public @NotNull String render(@NotNull Renderer renderer, @NotNull Names names) {
      return String.format("%s.parse(%s, %s)", metaParameterName, names.builder, names.level);
    }
  }

  record MethodCall(boolean renderClass, @NotNull String className, @NotNull String methodName) implements NodeCall {
    @Override
    public @NotNull String render(@NotNull Renderer renderer, @NotNull Names names) {
      if (renderClass) {
        return String.format("%s.%s(%s, %s + 1)", className, methodName, names.builder, names.level);
      }
      else {
        return String.format("%s(%s, %s + 1)", methodName, names.builder, names.level);
      }
    }
  }

  static class MethodCallWithArguments implements NodeCall {

    protected final String methodName;
    protected final List<NodeArgument> arguments;

    MethodCallWithArguments(@NotNull String methodName, @NotNull List<NodeArgument> arguments) {
      this.methodName = methodName;
      this.arguments = Collections.unmodifiableList(arguments);
    }

    protected @NotNull String getMethodRef() {
      return methodName;
    }

    @Override
    public @NotNull String render(@NotNull Renderer renderer, @NotNull Names names) {
      String argumentStr = arguments.stream()
        .map(argument -> argument.render(renderer))
        .map(it -> ", " + it)
        .collect(Collectors.joining());
      return String.format("%s(%s, %s + 1%s)", getMethodRef(), names.builder, names.level, argumentStr);
    }
  }

  record TextArgument(@NotNull String text) implements NodeArgument {
    @Override
    public @NotNull String render(@NotNull Renderer renderer) {
      return text;
    }
  }

  record MetaParameterArgument(@NotNull String text) implements NodeArgument {
    @Override
    public @NotNull String render(@NotNull Renderer renderer) {
      return text;
    }

    @Override
    public boolean referencesMetaParameter() {
      return true;
    }
  }
}
