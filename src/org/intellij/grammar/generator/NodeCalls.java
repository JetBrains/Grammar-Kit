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

import static org.intellij.grammar.generator.ParserGeneratorUtil.getWrapperParserMetaMethodName;

/**
 * @author Daniil Ovchinnikov
 */
public final class NodeCalls {
  private NodeCalls() {
  }

  @FunctionalInterface
  interface NodeCall {

    @NotNull
    String render();
  }

  @FunctionalInterface
  interface NodeArgument {

    default boolean referencesMetaParameter() {
      return false;
    }

    @NotNull
    String render();
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
    public @NotNull String render() {
      return String.format("%s(%s, %s)", consumeType.getMethodName(), stateHolder, token);
    }
  }

  static class ConsumeTokenChoiceCall implements NodeCall {

    final ParserGeneratorUtil.ConsumeType consumeType;
    final String tokenSetName;
    final String stateHolder;

    ConsumeTokenChoiceCall(@NotNull ParserGeneratorUtil.ConsumeType consumeType, @NotNull String tokenSetName, String stateHolder) {
      this.consumeType = consumeType;
      this.tokenSetName = tokenSetName;
      this.stateHolder = stateHolder;
    }

    @Override
    public @NotNull String render() {
      return String.format("%s(%s, %s)", consumeType.getMethodName(), stateHolder, tokenSetName);
    }
  }

  static class ConsumeTokensCall implements NodeCall {

    final String methodName;
    final String stateHolder;
    final int pin;
    final List<String> tokens;

    ConsumeTokensCall(@NotNull String methodName, @NotNull String stateHolder, int pin, @NotNull List<String> tokens) {
      this.methodName = methodName;
      this.stateHolder = stateHolder;
      this.pin = pin;
      this.tokens = Collections.unmodifiableList(tokens);
    }

    @Override
    public @NotNull String render() {
      return String.format("%s(%s, %d, %s)", methodName, stateHolder, pin, StringUtil.join(tokens, ", "));
    }
  }

  static class ExpressionMethodCall implements NodeCall {

    final String methodName;
    final String stateHolder;
    final String level;
    final int priority;

    ExpressionMethodCall(@NotNull String methodName, @NotNull String stateHolder, @NotNull String level, int priority) {
      this.methodName = methodName;
      this.stateHolder = stateHolder;
      this.level = level;
      this.priority = priority;
    }

    @Override
    public @NotNull String render() {
      return String.format("%s(%s, %s + 1, %d)", methodName, stateHolder, level, priority);
    }
  }

  static class MetaMethodCall extends MethodCallWithArguments {

    final @Nullable String targetClassName;

    MetaMethodCall(@Nullable String targetClassName, @NotNull String methodName, @NotNull String stateHolder, @NotNull String level,  @NotNull List<NodeArgument> arguments) {
      super(methodName, stateHolder, level, arguments);
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

  static class MetaMethodCallArgument implements NodeArgument {

    final MetaMethodCall call;

    MetaMethodCallArgument(@NotNull MetaMethodCall call) {
      this.call = call;
    }

    @Override
    public boolean referencesMetaParameter() {
      return true;
    }

    private @NotNull String getMethodRef() {
      String ref = getWrapperParserMetaMethodName(call.methodName);
      String className = call.getTargetClassName();
      return className == null ? ref : String.format("%s.%s", className, ref);
    }

    @Override
    public @NotNull String render() {
      String arguments = String.join(", ", ContainerUtil.map(call.arguments, NodeArgument::render));
      return String.format("%s(%s)", getMethodRef(), arguments);
    }
  }

  static class MetaParameterCall implements NodeCall {

    final String metaParameterName;
    final String stateHolder;
    final String level;

    MetaParameterCall(@NotNull String metaParameterName, String stateHolder, String level) {
      this.metaParameterName = metaParameterName;
      this.stateHolder = stateHolder;
      this.level = level;
    }

    @Override
    public @NotNull String render() {
      return String.format("%s.parse(%s, %s)", metaParameterName, stateHolder, level);
    }
  }

  static class MethodCall implements NodeCall {

    final boolean renderClass;
    final String className;
    final String methodName;
    final String stateHolder;
    final String level;

    MethodCall(boolean renderClass, @NotNull String className, @NotNull String methodName, String stateHolder, String level) {
      this.renderClass = renderClass;
      this.className = className;
      this.methodName = methodName;
      this.stateHolder = stateHolder;
      this.level = level;
    }

    @NotNull
    String getMethodName() {
      return methodName;
    }

    @NotNull
    String getClassName() {
      return className;
    }

    @Override
    public @NotNull String render() {
      if (renderClass) {
        return String.format("%s.%s(%s, %s + 1)", className, methodName, stateHolder, level);
      }
      else {
        return String.format("%s(%s, %s + 1)", methodName, stateHolder, level);
      }
    }
  }

  static class MethodCallWithArguments implements NodeCall {

    final String methodName;
    final String stateHolder;
    final String level;
    final List<NodeArgument> arguments;

    MethodCallWithArguments(@NotNull String methodName, @NotNull String stateHolder, @NotNull String level, @NotNull List<NodeArgument> arguments) {
      this.methodName = methodName;
      this.stateHolder = stateHolder;
      this.level = level;
      this.arguments = Collections.unmodifiableList(arguments);
    }

    protected @NotNull String getMethodRef() {
      return methodName;
    }

    @Override
    public @NotNull String render() {
      String argumentStr = arguments.stream()
        .map(NodeArgument::render)
        .map(it -> ", " + it)
        .collect(Collectors.joining());
      return String.format("%s(%s, %s + 1%s)", getMethodRef(), stateHolder, level, argumentStr);
    }
  }


  static class TextArgument implements NodeArgument {

    final String text;

    TextArgument(@NotNull String text) {
      this.text = text;
    }

    @Override
    public @NotNull String render() {
      return text;
    }
  }

  static class MetaParameterArgument extends TextArgument {

    MetaParameterArgument(@NotNull String text) {
      super(text);
    }

    @Override
    public boolean referencesMetaParameter() {
      return true;
    }
  }
}
