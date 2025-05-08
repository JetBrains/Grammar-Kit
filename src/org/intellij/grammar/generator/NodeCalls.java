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
    String render(@NotNull Renderer renderer);
  }

  @FunctionalInterface
  interface NodeArgument {

    default boolean referencesMetaParameter() {
      return false;
    }

    @NotNull
    String render(@NotNull Renderer renderer);
  }

  static class ConsumeTokenCall implements NodeCall {

    final ParserGeneratorUtil.ConsumeType consumeType;
    final String token;
    final String stateHolder;

    ConsumeTokenCall(@NotNull ParserGeneratorUtil.ConsumeType consumeType, @NotNull String token, @NotNull String stateHolder) {
      this.consumeType = consumeType;
      this.token = token;
      this.stateHolder = stateHolder;
    }

    @Override
    public @NotNull String render(@NotNull Renderer renderer) {
      return String.format("%s(%s, %s)", consumeType.getMethodName(), stateHolder, token);
    }
  }

  record ConsumeTokenChoiceCall(@NotNull ParserGeneratorUtil.ConsumeType consumeType,
                                @NotNull String stateHolder,
                                @NotNull String tokenSetName) implements NodeCall {

    @Override
    public @NotNull String render(@NotNull Renderer renderer) {
      return String.format("%s(%s, %s)", consumeType.getMethodName(), stateHolder, tokenSetName);
    }
  }

  record ConsumeTokensCall(
    @NotNull String methodName,
    @NotNull String builder,
    int pin,
    @NotNull List<String> tokens
  ) implements NodeCall {
    @Override
    public @NotNull String render(@NotNull Renderer renderer) {
      return String.format("%s(%s, %d, %s)", methodName, builder, pin, StringUtil.join(tokens, ", "));
    }
  }

  record ExpressionMethodCall(@NotNull String methodName, 
                              @NotNull String builder,
                              @NotNull String level,
                              int priority) implements NodeCall {
    @Override
    public @NotNull String render(@NotNull Renderer renderer) {
      return String.format("%s(%s, %s + 1, %d)", methodName, builder, level, priority);
    }
  }

  static class MetaMethodCall extends MethodCallWithArguments {

    private final @Nullable String targetClassName;

    MetaMethodCall(@Nullable String targetClassName, @NotNull String methodName, @NotNull String builder, @NotNull String level,  @NotNull List<NodeArgument> arguments) {
      super(methodName, builder, level, arguments);
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

  record MetaParameterCall(String metaParameterName, String builder, String level) implements NodeCall {
    @Override
    public @NotNull String render(@NotNull Renderer renderer) {
      return String.format("%s.parse(%s, %s)", metaParameterName, builder, level);
    }
  }

  record MethodCall(boolean renderClass, 
                    @NotNull String className, 
                    @NotNull String methodName, 
                    @NotNull String builder, 
                    @NotNull String level) implements NodeCall {

    @NotNull
    String getMethodName() {
      return methodName;
    }

    @NotNull
    String getClassName() {
      return className;
    }

    @Override
    public @NotNull String render(@NotNull Renderer renderer) {
      if (renderClass) {
        return String.format("%s.%s(%s, %s + 1)", className, methodName, builder, level);
      }
      else {
        return String.format("%s(%s, %s + 1)", methodName, builder, level);
      }
    }
  }

  static class MethodCallWithArguments implements NodeCall {

    protected final String methodName;
    final String builder;
    final String level;
    protected final List<NodeArgument> arguments;

    MethodCallWithArguments(@NotNull String methodName, @NotNull String builder, @NotNull String level, @NotNull List<NodeArgument> arguments) {
      this.methodName = methodName;
      this.builder = builder;
      this.level = level;
      this.arguments = Collections.unmodifiableList(arguments);
    }

    protected @NotNull String getMethodRef() {
      return methodName;
    }

    @Override
    public @NotNull String render(@NotNull Renderer renderer) {
      String argumentStr = arguments.stream()
        .map(argument -> argument.render(renderer))
        .map(it -> ", " + it)
        .collect(Collectors.joining());
      return String.format("%s(%s, %s + 1%s)", getMethodRef(), builder, level, argumentStr);
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
