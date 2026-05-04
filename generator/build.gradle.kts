/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
plugins {
    java
    id("org.jetbrains.intellij.platform.module")
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    compileOnly(libs.annotations)

    implementation(project(":base"))
    implementation(project(":parser-runtime"))
    implementation(project(":bnf-language"))

    intellijPlatform {
        create(
            providers.gradleProperty("platformType"),
            providers.gradleProperty("platformVersion"),
        )
        bundledPlugins("com.intellij.java")
    }
}

sourceSets {
    main {
        java.srcDirs("src")
        resources.srcDirs("resources")
    }
}

tasks {
    providers.gradleProperty("javaVersion").get().let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
    }
}
