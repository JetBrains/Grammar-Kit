/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Per-project generator configuration, shared via {@code .idea/grammarKit.xml}.
 *
 * <p>Each setting holds the text of a command line. When it is empty the corresponding action
 * generates in-process exactly as it always has; when it is set, the action instead runs that
 * command once over the whole selection — see {@link org.intellij.grammar.actions.GrammarKitCliRunner}
 * for the contract. Everything else — output directory, generator flags, JVM selection — belongs
 * in whatever the command line points at.
 */
@Service(Service.Level.PROJECT)
@State(name = "GrammarKitSettings", storages = @Storage("grammarKit.xml"))
public final class GrammarKitSettings implements PersistentStateComponent<GrammarKitSettings.State> {

  public static final class State {
    public String parserCli = "";
    public String jflexCli = "";
  }

  private State myState = new State();

  public static @NotNull GrammarKitSettings getInstance(@NotNull Project project) {
    return project.getService(GrammarKitSettings.class);
  }

  @Override
  public @NotNull State getState() {
    return myState;
  }

  @Override
  public void loadState(@NotNull State state) {
    XmlSerializerUtil.copyBean(state, myState);
  }

  /** Command line for "Generate Parser Code", or an empty string to generate in-process. */
  public @NotNull String getParserCli() {
    return StringUtil.notNullize(myState.parserCli).trim();
  }

  public void setParserCli(@NotNull String cli) {
    myState.parserCli = cli.trim();
  }

  /** Command line for "Run JFlex Generator", or an empty string to run the JFlex jar as before. */
  public @NotNull String getJFlexCli() {
    return StringUtil.notNullize(myState.jflexCli).trim();
  }

  public void setJFlexCli(@NotNull String cli) {
    myState.jflexCli = cli.trim();
  }
}
