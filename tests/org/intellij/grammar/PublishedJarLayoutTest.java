/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar;

import junit.framework.TestCase;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Guards the shape of the {@code org.jetbrains:grammar-kit} Maven publication: a single
 * self-contained fat jar with no dangling submodule dependencies in the POM. A regression
 * here (e.g., reverting to {@code from(components["java"])}) would break downstream consumers
 * silently — there is no other test that exercises the publication.
 *
 * <p>Driven by the {@code testPublication} Gradle task, which publishes to the in-tree
 * {@code build/artifacts/maven} repo first and passes its location via system properties.
 */
public class PublishedJarLayoutTest extends TestCase {

  private static final String GROUP_PATH = "org/jetbrains/grammar-kit";

  private static final List<String> REQUIRED_FAT_JAR_ENTRIES = Arrays.asList(
      "org/intellij/grammar/Main.class",
      "org/intellij/grammar/java/JavaHelperFactory.class",
      "org/intellij/grammar/classinfo/java/JavaSyntaxClassExtractor.class",
      "org/intellij/grammar/classinfo/kotlin/KotlinSyntaxClassExtractor.class",
      "org/intellij/grammar/parser/GeneratedParserUtilBase.class",
      "org/intellij/grammar/util/Case.class",
      "org/intellij/jflex/parser/JFlexFileType.class",
      "templates/GeneratedParserUtilBase.java"
  );

  private static final Set<String> FORBIDDEN_DEP_ARTIFACT_IDS = new HashSet<>(Arrays.asList(
      "base", "parser-runtime", "jvm-class-info", "bnf-language", "jflex-language", "generator"
  ));

  private Path versionDir;
  private String version;

  @Override
  protected void setUp() {
    version = System.getProperty("grammar.kit.published.version");
    String repo = System.getProperty("grammar.kit.published.repo");
    assertNotNull("system property grammar.kit.published.version must be set by Gradle", version);
    assertNotNull("system property grammar.kit.published.repo must be set by Gradle", repo);
    versionDir = new File(repo).toPath().resolve(GROUP_PATH).resolve(version);
    assertTrue("expected published version dir at " + versionDir + " — did publishGrammarKitJarPublicationToArtifactsRepository run?",
               Files.isDirectory(versionDir));
  }

  public void testMainJarExistsWithoutClassifier() {
    Path jar = versionDir.resolve("grammar-kit-" + version + ".jar");
    assertTrue("missing main jar (no classifier): " + jar, Files.isRegularFile(jar));
    long size = jar.toFile().length();
    assertTrue("main jar suspiciously small (" + size + " bytes) — looks like a root-only jar instead of a fat jar",
               size > 500_000);
    Path baseClassifier = versionDir.resolve("grammar-kit-" + version + "-base.jar");
    assertFalse("the -base classifier jar must NOT be published: " + baseClassifier, Files.isRegularFile(baseClassifier));
  }

  public void testFatJarContainsAllSubmoduleEntrypoints() throws Exception {
    Path jar = versionDir.resolve("grammar-kit-" + version + ".jar");
    Set<String> present = new LinkedHashSet<>();
    try (JarFile jarFile = new JarFile(jar.toFile())) {
      Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        present.add(entries.nextElement().getName());
      }
    }
    Set<String> missing = new LinkedHashSet<>();
    for (String required : REQUIRED_FAT_JAR_ENTRIES) {
      if (!present.contains(required)) missing.add(required);
    }
    assertTrue("fat jar is missing required entries: " + missing, missing.isEmpty());
  }

  public void testPomHasNoSubmoduleDependencies() throws Exception {
    Path pom = versionDir.resolve("grammar-kit-" + version + ".pom");
    assertTrue("missing POM: " + pom, Files.isRegularFile(pom));
    String text = new String(Files.readAllBytes(pom), "UTF-8");

    // Packaging: must not be pom (which would mean no main jar)
    int packagingIdx = text.indexOf("<packaging>");
    if (packagingIdx >= 0) {
      int end = text.indexOf("</packaging>", packagingIdx);
      String packaging = text.substring(packagingIdx + "<packaging>".length(), end).trim();
      assertEquals("POM packaging must be 'jar' (or omitted), not '" + packaging + "'", "jar", packaging);
    }

    for (String forbidden : FORBIDDEN_DEP_ARTIFACT_IDS) {
      String needle = "<artifactId>" + forbidden + "</artifactId>";
      assertFalse("POM must not declare a runtime dep on submodule '" + forbidden + "'", text.contains(needle));
    }
  }

  public void testNoGradleModuleMetadataPublished() {
    Path module = versionDir.resolve("grammar-kit-" + version + ".module");
    assertFalse("Gradle module metadata file must not be published (it would re-expose project deps): " + module,
                Files.isRegularFile(module));
  }
}
