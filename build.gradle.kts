/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.gradle.kotlin.dsl.get
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.ChangelogSectionUrlBuilder
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.util.Base64

plugins {
    java
    idea
    `maven-publish`
    signing
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.changelog)
    alias(libs.plugins.githubRelease)
    alias(libs.plugins.publishPlugin)
}

buildscript {
    dependencies {
        classpath("com.squareup.okhttp3:okhttp:5.3.2")
    }
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

repositories {
    mavenCentral()

    maven {
        name = "artifacts"
        url = uri(layout.buildDirectory.dir("artifacts/maven"))
    }

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    compileOnly(libs.annotations)
    testImplementation(libs.junit)

    intellijPlatform {
        create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))

        // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })

        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.Plugin.Java)
    }
}

sourceSets {
    main {
        java.srcDirs("src", "gen")
        resources.srcDirs("resources")
    }
    test {
        java.srcDirs("tests")
        resources.srcDirs("testData")
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
        version = providers.gradleProperty("pluginVersion")

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes = providers.gradleProperty("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                    .withHeader(false)
                    .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
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
        // https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html#specifying-a-release-channel
        channels = providers.gradleProperty("pluginVersion").map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    header = project.provider { project.version.toString() }
    headerParserRegex = """(\d+(\.\d+)+)"""
    itemPrefix = "*"
    groups = emptyList()
    repositoryUrl = "https://github.com/JetBrains/Grammar-Kit"
    sectionUrlBuilder = ChangelogSectionUrlBuilder { repositoryUrl, currentVersion, previousVersion, isUnreleased ->
        repositoryUrl + when {
            isUnreleased -> when (previousVersion) {
                null -> "/commits"
                else -> "/compare/$previousVersion...HEAD"
            }

            previousVersion == null -> "/commits/$currentVersion"

            else -> "/compare/$previousVersion...$currentVersion"
        }
    }
}

val artifactsPath = providers.gradleProperty("artifactsPath")

val buildGrammarKitJar by tasks.registering(Jar::class) {
    dependsOn("assemble")
    archiveBaseName = "grammar-kit"
    destinationDirectory = file(artifactsPath)
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
        dependsOn(patchChangelog)
    }

    val buildGrammarKitZip by registering(Zip::class) {
        dependsOn(buildGrammarKitJar)
        archiveBaseName = "GrammarKit"
        destinationDirectory = file(artifactsPath)
        from(buildGrammarKitJar.map { it.outputs }) {
            into("/GrammarKit/lib")
        }
    }

    val buildExpressionConsoleSample = registering(Jar::class) {
        archiveBaseName = "expression-console-sample"
        destinationDirectory = file(artifactsPath)
        manifest {
            from("$rootDir/tests/org/intellij/grammar/expression/META-INF/MANIFEST.MF")
        }
        from(files("$rootDir/out/test/grammar-kit/org/intellij/grammar/expression")) {
            into("/org/intellij/grammar/expression")
        }
    }

    register("artifacts") {
        dependsOn(buildGrammarKitJar, buildGrammarKitZip, buildExpressionConsoleSample)
    }

    defaultTasks("clean", "artifacts", "test")

    val packSonatypeCentralBundle by registering(Zip::class) {
        group = "publishing"

        dependsOn(":publishAllPublicationsToArtifactsRepository")

        from(layout.buildDirectory.dir("artifacts/maven"))
        archiveFileName.set("bundle.zip")
        destinationDirectory.set(layout.buildDirectory)
    }

    abstract class PublishMavenToCentralPortal : DefaultTask() {

        @get:Input
        abstract val deploymentName: Property<String>

        @get:Input
        abstract val centralPortalUserName: Property<String>

        @get:Input
        abstract val centralPortalToken: Property<String>

        @get:InputFile
        abstract val bundleFile: RegularFileProperty

        @TaskAction
        fun publish() {
            val uriBase = "https://central.sonatype.com/api/v1/publisher/upload"
            val publishingType = "USER_MANAGED"
            val uri = "$uriBase?name=${deploymentName.get()}&publishingType=$publishingType"
            val file = bundleFile.get().asFile
            val userName = centralPortalUserName.orNull
            val token = centralPortalToken.orNull

            val base64Auth = Base64
                .getEncoder()
                .encode("$userName:$token".toByteArray())
                .toString(Charsets.UTF_8)

            println("Sending request to $uri...")

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(uri)
                .header("Authorization", "Bearer $base64Auth")
                .post(
                    MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("bundle", file.name, file.asRequestBody())
                        .build()
                )
                .build()
            client.newCall(request).execute().use { response ->
                val statusCode = response.code
                println("Upload status code: $statusCode")
                println("Upload result: ${response.body!!.string()}")
                if (statusCode != 201) {
                    error("Upload error to Central repository. Status code $statusCode.")
                }
            }
        }
    }

    val publishMavenToCentralPortal by registering(PublishMavenToCentralPortal::class) {
        group = "publishing"

        deploymentName = "${project.name}-$version"
        bundleFile = packSonatypeCentralBundle.flatMap { it.archiveFile }
        centralPortalUserName = providers.gradleProperty("centralPortalUserName")
        centralPortalToken = providers.gradleProperty("centralPortalToken")

        dependsOn(packSonatypeCentralBundle)
    }
}

publishing {
    repositories {
        maven {
            name = "artifacts"
            url = uri(layout.buildDirectory.dir("artifacts/maven"))
        }
    }

    publications {
        create<MavenPublication>("grammarKitJar") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
//            artifact(tasks["sourcesJar"])
//            artifact(tasks["javadocJar"])

            pom {
                name = "JetBrains Grammar-Kit"
                description = "Grammar-Kit library dedicated for language plugin developers."
                url = "https://github.com/JetBrains/Grammar-Kit"
                licenses {
                    license {
                        name = "The Apache Software License, Version 2.0"
                        url = "https://github.com/JetBrains/Grammar-Kit/blob/master/LICENSE.md"
                    }
                }
                developers {
                    developer {
                        id = "gregsh"
                        name = "Greg Shrago"
                        organization = "JetBrains"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/JetBrains/Grammar-Kit.git"
                    developerConnection = "scm:git:ssh://github.com/JetBrains/Grammar-Kit.git"
                    url = "https://github.com/JetBrains/Grammar-Kit"
                }
            }
        }
    }
}

// only available in the release workflow
signing {
    val signingKey = project.findProperty("signingKey") as String?
    val signingPassword = project.findProperty("signingPassword").toString() as String?

    isRequired = !signingKey.isNullOrEmpty() && !signingPassword.isNullOrEmpty()

    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["grammarKitJar"])
}
