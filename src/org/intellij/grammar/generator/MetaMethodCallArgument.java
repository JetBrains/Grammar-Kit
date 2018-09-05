/*
 * Copyright 2011-present Greg Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.intellij.grammar.generator;

import org.jetbrains.annotations.NotNull;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getWrapperParserMetaMethodName;

class MetaMethodCallArgument implements NodeArgument {

  private final @NotNull MetaMethodCall myCall;

  MetaMethodCallArgument(@NotNull MetaMethodCall call) {
    myCall = call;
  }

  @Override
  public boolean referencesMetaParameter() {
    return true;
  }

  @NotNull
  private String getMethodRef() {
    String ref = getWrapperParserMetaMethodName(myCall.getMethodName());
    String className = myCall.getTargetClassName();
    return className == null ? ref : String.format("%s.%s", className, ref);
  }

  @NotNull
  @Override
  public String render() {
    String arguments = String.join(", ", myCall.getArgumentStrings());
    return String.format("%s(%s)", getMethodRef(), arguments);
  }
}
