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

fun properties(key: String) = project.findProperty(key)?.toString()

plugins {
    id("org.jetbrains.intellij") version "1.2.1"
    id("org.jetbrains.changelog") version "1.3.1"
    id("idea")
    id("java")
    id("maven-publish")
    id("signing")
    id("com.github.breadmoirai.github-release") version "2.2.12"
}

version = properties("pluginVersion")

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:22.0.0")
}

idea {
    module {
        generatedSourceDirs.add(file("gen"))
    }
}

intellij {
    version.set(properties("ideaVersion"))
    type.set("IU")
    plugins.set(listOf("uml", "java"))
    updateSinceUntilBuild.set(false)
}

changelog {
    header.set("${{ -> version.get() }}")
    headerParserRegex.set("""(\d+(\.\d+)+)""")
    itemPrefix.set("*")
    unreleasedTerm.set("Unreleased")
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
        changeNotes.set(provider {
            changelog.run {
                getOrNull(project.version.toString()) ?: getLatest()
            }.toHTML() + "<a href=\"https://github.com/JetBrains/Grammar-Kit/blob/master/CHANGELOG.md\">Full change log...</a>"
        })
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
            artifact(buildGrammarKitJar)

            pom {
                name.set("JetBrains Grammar-Kit")
                description.set("Grammar-Kit library didicated for language plugin developers.")
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

    isRequired = !signingKey.isNullOrEmpty() && !signingPassword.isNullOrEmpty()

    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["grammarKitJar"])
}
