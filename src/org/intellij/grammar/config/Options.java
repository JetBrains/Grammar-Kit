/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.config;

import com.intellij.openapi.util.Getter;

/**
 * @author gregsh
 */
public interface Options {

  Getter<String> GEN_DIR = Option.strOption("grammar.kit.gen.dir", "gen");
  Getter<String> GEN_JFLEX_ARGS = Option.strOption("grammar.kit.gen.jflex.args", "");

  Getter<Integer> GPUB_MAX_LEVEL = Option.intOption("grammar.kit.gpub.max.level", 1000);

  Getter<Boolean> INJECT_JAVA_IN_JFLEX = Option.boolOption("grammar.kit.inject.java", true);
  Getter<Boolean> BNF_INJECT_REGEXP_IN_BNF = Option.boolOption("grammar.kit.inject.regexp", true);
}
