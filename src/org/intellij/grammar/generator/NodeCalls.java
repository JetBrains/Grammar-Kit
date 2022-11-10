/*
 * Copyright 2011-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
public class NodeCalls {
  private NodeCalls() {
  }

  interface NodeCall {

    @NotNull
    String render(@NotNull Names names);
  }

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

    ConsumeTokenCall(@NotNull ParserGeneratorUtil.ConsumeType consumeType, @NotNull String token) {
      this.consumeType = consumeType;
      this.token = token;
    }

    @Override
    public @NotNull String render(@NotNull Names names) {
      return String.format("%s(%s, %s)", consumeType.getMethodName(), names.builder, token);
    }
  }

  static class ConsumeTokenChoiceCall implements NodeCall {

    final ParserGeneratorUtil.ConsumeType consumeType;
    final String tokenSetName;

    ConsumeTokenChoiceCall(@NotNull ParserGeneratorUtil.ConsumeType consumeType, @NotNull String tokenSetName) {
      this.consumeType = consumeType;
      this.tokenSetName = tokenSetName;
    }

    @Override
    public @NotNull String render(@NotNull Names names) {
      return String.format("%s(%s, %s)", consumeType.getMethodName(), names.builder, tokenSetName);
    }
  }

  static class ConsumeTokensCall implements NodeCall {

    final String methodName;
    final int pin;
    final List<String> tokens;

    ConsumeTokensCall(@NotNull String methodName, int pin, @NotNull List<String> tokens) {
      this.methodName = methodName;
      this.pin = pin;
      this.tokens = Collections.unmodifiableList(tokens);
    }

    @Override
    public @NotNull String render(@NotNull Names names) {
      return String.format("%s(%s, %d, %s)", methodName, names.builder, pin, StringUtil.join(tokens, ", "));
    }
  }

  static class ExpressionMethodCall implements NodeCall {

    final String methodName;
    final int priority;

    ExpressionMethodCall(@NotNull String methodName, int priority) {
      this.methodName = methodName;
      this.priority = priority;
    }

    @Override
    public @NotNull String render(@NotNull Names names) {
      return String.format("%s(%s, %s + 1, %d)", methodName, names.builder, names.level, priority);
    }
  }

  static class MetaMethodCall extends MethodCallWithArguments {

    final @Nullable String targetClassName;

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

    MetaParameterCall(@NotNull String metaParameterName) {
      this.metaParameterName = metaParameterName;
    }

    @Override
    public @NotNull String render(@NotNull Names names) {
      return String.format("%s.parse(%s, %s)", metaParameterName, names.builder, names.level);
    }
  }

  static class MethodCall implements NodeCall {

    final boolean renderClass;
    final String className;
    final String methodName;

    MethodCall(boolean renderClass, @NotNull String className, @NotNull String methodName) {
      this.renderClass = renderClass;
      this.className = className;
      this.methodName = methodName;
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
    public @NotNull String render(@NotNull Names names) {
      if (renderClass) {
        return String.format("%s.%s(%s, %s + 1)", className, methodName, names.builder, names.level);
      }
      else {
        return String.format("%s(%s, %s + 1)", methodName, names.builder, names.level);
      }
    }
  }

  static class MethodCallWithArguments implements NodeCall {

    final String methodName;
    final List<NodeArgument> arguments;

    MethodCallWithArguments(@NotNull String methodName, @NotNull List<NodeArgument> arguments) {
      this.methodName = methodName;
      this.arguments = Collections.unmodifiableList(arguments);
    }

    protected @NotNull String getMethodRef() {
      return methodName;
    }

    @Override
    public @NotNull String render(@NotNull Names names) {
      String argumentStr = arguments.stream()
        .map(NodeArgument::render)
        .map(it -> ", " + it)
        .collect(Collectors.joining());
      return String.format("%s(%s, %s + 1%s)", getMethodRef(), names.builder, names.level, argumentStr);
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
