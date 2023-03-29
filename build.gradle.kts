import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.ChangelogSectionUrlBuilder

/*
 * Copyright 2011-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.3"
    id("org.jetbrains.changelog") version "2.0.0"
    id("idea")
    id("maven-publish")
    id("signing")
    id("com.github.breadmoirai.github-release") version "2.4.1"
}

version = properties("pluginVersion")

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.0")
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

intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("ideaVersion"))
    type.set("IU")
    plugins.set(listOf("uml", "java"))
    updateSinceUntilBuild.set(false)
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

val artifactsPath = properties("artifactsPath")

val buildGrammarKitJar = tasks.create<Jar>("buildGrammarKitJar") {
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
}

tasks {
    properties("javaVersion").let {
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
        gradleVersion = properties("gradleVersion")
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

    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        sinceBuild.set(properties("pluginSinceIdeaBuild"))
        changeNotes.set(provider {
            changelog.renderItem(
                    changelog
                            .getUnreleased()
                            .withHeader(false)
                            .withEmptySections(false),
                    Changelog.OutputType.HTML
            )
        })
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("PUBLISH_TOKEN"))
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels.set(listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first()))
    }

    val buildGrammarKitZip = create<Zip>("buildGrammarKitZip") {
        dependsOn(buildGrammarKitJar)
        archiveBaseName.set("GrammarKit")
        destinationDirectory.set(file(artifactsPath))
        from(buildGrammarKitJar.outputs) {
            into("/GrammarKit/lib")
        }
    }

    val buildExpressionConsoleSample = create<Jar>("buildExpressionConsoleSample") {
        archiveBaseName.set("expression-console-sample")
        destinationDirectory.set(file(artifactsPath))
        manifest {
            from("$rootDir/tests/org/intellij/grammar/expression/META-INF/MANIFEST.MF")
        }
        from(files("$rootDir/out/test/grammar-kit/org/intellij/grammar/expression")) {
            into("/org/intellij/grammar/expression")
        }
    }

    create("artifacts") {
        dependsOn(buildGrammarKitJar, buildGrammarKitZip, buildExpressionConsoleSample)
    }

    defaultTasks("clean", "artifacts", "test")
}

publishing {
    publications {
        create<MavenPublication>("grammarKitJar") {
            groupId = "org.jetbrains"
            artifactId = "grammar-kit"
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
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")

            credentials {
                username = properties("mavenCentralUsername")
                password = properties("mavenCentralPassword")
            }
        }
    }
}

signing {
    val signingKey = properties("signingKey")
    val signingPassword = properties("signingPassword")

    isRequired = signingKey.isNotEmpty() && signingPassword.isNotEmpty()

    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["grammarKitJar"])
}
