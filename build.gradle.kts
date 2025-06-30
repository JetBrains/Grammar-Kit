import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.ChangelogSectionUrlBuilder

/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.6.0"
    id("org.jetbrains.changelog") version "2.2.1"
    id("idea")
    id("maven-publish")
    id("signing")
    id("com.github.breadmoirai.github-release") version "2.5.2"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

group = "org.jetbrains"
version = providers.gradleProperty("pluginVersion").get()

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")

    intellijPlatform {
        intellijIdeaUltimate(providers.gradleProperty("ideaVersion"))
        bundledPlugins(listOf("com.intellij.diagram", "com.intellij.java", "com.intellij.copyright"))
    }
}

sourceSets {
    main {
        java.srcDirs("src", "gen")
        resources.srcDirs("resources")
    }
    test {
        java.srcDirs("tests")
    }
}


idea {
    module {
        generatedSourceDirs.add(file("gen"))
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

intellijPlatform {
    buildSearchableOptions = false

    pluginConfiguration {
        name = providers.gradleProperty("pluginName")
        val changelog = project.changelog // local variable for configuration cache compatibility
        changeNotes = provider {
            changelog.renderItem(
                changelog
                    .getUnreleased()
                    .withHeader(false)
                    .withEmptySections(false),
                Changelog.OutputType.HTML
            )
        }

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceIdeaBuild")
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = providers.gradleProperty("pluginVersion").map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }
}

changelog {
    header.set(project.provider { project.version.toString() })
    headerParserRegex.set("""(\d+(\.\d+)+)""")
    itemPrefix.set("*")
    groups.set(emptyList())
    repositoryUrl.set("https://github.com/JetBrains/Grammar-Kit")
    sectionUrlBuilder.set(ChangelogSectionUrlBuilder { repositoryUrl, currentVersion, previousVersion, isUnreleased ->
        repositoryUrl + when {
            isUnreleased -> when (previousVersion) {
                null -> "/commits"
                else -> "/compare/$previousVersion...HEAD"
            }

            previousVersion == null -> "/commits/$currentVersion"

            else -> "/compare/$previousVersion...$currentVersion"
        }
    })
}

val artifactsPath = providers.gradleProperty("artifactsPath")

val buildGrammarKitJar by tasks.registering(Jar::class) {
    dependsOn("assemble")
    archiveBaseName.set("grammar-kit")
    destinationDirectory.set(file(artifactsPath))
    manifest {
        from("$rootDir/resources/META-INF/MANIFEST.MF")
    }
    from(sourceSets.main.get().output)
    from(file("$rootDir/src/org/intellij/grammar/parser/GeneratedParserUtilBase.java")) {
        into("/templates")
    }
    exclude("**/classpath.index")
}

tasks {
    providers.gradleProperty("javaVersion").get().let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
    }

    withType<Test> {
        useJUnit()
        include("**/BnfTestSuite.class")
        isScanForTestClasses = false
        ignoreFailures = true
    }

    withType<Javadoc>().configureEach {
        (options as StandardJavadocDocletOptions).apply {
            addStringOption("Xdoclint:none", "-quiet")
        }
    }

    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }

    publishPlugin {
        dependsOn("patchChangelog")
    }

    val buildGrammarKitZip by registering(Zip::class) {
        dependsOn(buildGrammarKitJar)
        archiveBaseName.set("GrammarKit")
        destinationDirectory.set(file(artifactsPath))
        from(buildGrammarKitJar.map { it.outputs }) {
            into("/GrammarKit/lib")
        }
    }

    val buildExpressionConsoleSample = registering(Jar::class) {
        archiveBaseName.set("expression-console-sample")
        destinationDirectory.set(file(artifactsPath))
        manifest {
            from("$rootDir/tests/org/intellij/grammar/expression/META-INF/MANIFEST.MF")
        }
        from(files("$rootDir/out/test/grammar-kit/org/intellij/grammar/expression")) {
            into("/org/intellij/grammar/expression")
        }
    }

    val artifacts by registering {
        dependsOn(buildGrammarKitJar, buildGrammarKitZip, buildExpressionConsoleSample)
    }

    defaultTasks("clean", "artifacts", "test")
}

publishing {
    publications {
        create<MavenPublication>("grammarKitJar") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])

            pom {
                name.set("JetBrains Grammar-Kit")
                description.set("Grammar-Kit library dedicated for language plugin developers.")
                url.set("https://github.com/JetBrains/Grammar-Kit")
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("https://github.com/JetBrains/Grammar-Kit/blob/master/LICENSE.md")
                    }
                }
                developers {
                    developer {
                        id.set("gregsh")
                        name.set("Greg Shrago")
                        organization.set("JetBrains")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/JetBrains/Grammar-Kit.git")
                    developerConnection.set("scm:git:ssh://github.com/JetBrains/Grammar-Kit.git")
                    url.set("https://github.com/JetBrains/Grammar-Kit")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            username = providers.gradleProperty("mavenCentralUsername").orNull
            password = providers.gradleProperty("mavenCentralPassword").orNull
        }
    }
}

// only available in release workflow
signing {
    val signingKey = project.findProperty("signingKey").toString()
    val signingPassword = project.findProperty("signingPassword").toString()

    isRequired = signingKey.isNotEmpty() && signingPassword.isNotEmpty()

    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["grammarKitJar"])
}
