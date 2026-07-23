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
import java.util.zip.ZipFile
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.component.ProjectComponentIdentifier

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

    implementation(project(":base"))
    implementation(project(":parser-runtime"))
    implementation(project(":bnf-language"))
    implementation(project(":jflex-language"))
    implementation(project(":generator"))

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
        java.srcDirs("src")
        resources.srcDirs("resources")
    }
    test {
        java.srcDirs("tests")
        resources.srcDirs("testData")
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

// Local modules bundled into the single self-contained `grammar-kit` jar, derived from the declared
// project dependencies rather than a hand-written list. A newly added `implementation(project(...))`
// module is therefore bundled (and verified) automatically, with no list to keep in sync.
// These must NOT leak into the published POM as transitive `org.jetbrains:*` dependencies — those
// per-module artifacts are not published to Maven Central.
val bundledProjectPaths: List<String> =
    configurations.implementation.get().dependencies
        .withType(ProjectDependency::class.java)
        .map { it.path }
bundledProjectPaths.forEach { evaluationDependsOn(it) }

fun Project.mainSourceSet(): SourceSet =
    extensions.getByType(SourceSetContainer::class.java).getByName("main")

val buildGrammarKitJar by tasks.registering(Jar::class) {
    dependsOn("assemble")
    archiveBaseName = "grammar-kit"
    destinationDirectory = file(artifactsPath)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        from("$rootDir/resources/META-INF/MANIFEST.MF")
    }
    from(sourceSets.main.get().output)
    bundledProjectPaths.forEach { from(project(it).mainSourceSet().output) }
    from(file("$rootDir/parser-runtime/src/org/intellij/grammar/parser/GeneratedParserUtilBase.java")) {
        into("/templates")
    }
    exclude("**/classpath.index")
}

// Make the -sources jar self-contained too, matching the fat jar.
tasks.named<Jar>("sourcesJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    bundledProjectPaths.forEach { from(project(it).mainSourceSet().allSource) }
}

// The publication attaches the fat jar via `artifact(...)`, not a software component, so no Gradle
// module metadata is produced. Disable the task explicitly to avoid a stale/misleading `.module` file.
tasks.withType<GenerateModuleMetadata>().configureEach {
    enabled = false
}

val modernAnnotations = configurations.detachedConfiguration(dependencies.create(libs.annotations.get()))

tasks {
    providers.gradleProperty("javaVersion").get().let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
    }

    // The bundled Kotlin plugin drags an old `org.jetbrains:annotations` (no @Nls/@NonNls TYPE_USE target)
    // onto the test compile classpath. Put the modern annotations first so type-use annotations still resolve.
    named<JavaCompile>("compileTestJava") {
        classpath = modernAnnotations + classpath
    }

    named<Test>("test") {
        useJUnit()
        include("**/BnfTestSuite.class")
        isScanForTestClasses = false
        ignoreFailures = true
        dependsOn("testMain")
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
            val publishingType = "AUTOMATIC"
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

intellijPlatformTesting {
    testIde.register("testMain") {
        task {
            group = "verification"
            description = "Runs MainTest in an isolated JVM (LightPsi bootstrapped fresh by Main.run)"
            useJUnit()
            include("**/MainTest.class")
            isScanForTestClasses = false
            ignoreFailures = true
            testClassesDirs = files(layout.buildDirectory.dir("instrumented/instrumentTestCode"))
        }
        sandboxDirectory = layout.buildDirectory.dir("testMain-sandbox")
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

            // Publish the single fat jar bundling every module. Using `from(components["java"])`
            // here would package only the root module's classes and declare `:base`,
            // `:parser-runtime`, `:bnf-language`, `:jflex-language` and `:generator` as transitive
            // `org.jetbrains:*` dependencies that don't exist on Maven Central (see #2023.3.3 breakage).
            artifact(buildGrammarKitJar)
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

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

// --- Distribution structure guards ------------------------------------------------------------
// Regression tests for the shape of what we ship. These would have caught the 2023.3.3 breakage,
// where the Maven artifact became a thin jar with transitive `org.jetbrains:*` dependencies that
// are not published to Maven Central.

val verifyMavenDistribution by tasks.registering {
    group = "verification"
    description = "Fails unless the Maven Central artifact is a self-contained fat jar with a dependency-free POM."
    dependsOn(buildGrammarKitJar, "generatePomFileForGrammarKitJarPublication")

    val jarFile = buildGrammarKitJar.flatMap { it.archiveFile }
    val pomFile = layout.buildDirectory.file("publications/grammarKitJar/pom-default.xml")
    val moduleFile = layout.buildDirectory.file("publications/grammarKitJar/module.json")

    // The set of local modules the library needs at runtime, discovered from the resolved dependency
    // graph — NOT from the bundling config. This is what makes the guard catch a newly added
    // submodule (direct or transitive): it shows up here, and the fat jar had better contain it.
    val runtimeModuleJars = configurations.named("runtimeClasspath").map { rc ->
        rc.incoming.artifacts.artifacts
            .filter { it.id.componentIdentifier is ProjectComponentIdentifier }
            .associate { (it.id.componentIdentifier as ProjectComponentIdentifier).projectPath to it.file }
    }

    inputs.file(jarFile)
    inputs.file(pomFile)
    inputs.files(runtimeModuleJars.map { it.values })

    doLast {
        val problems = mutableListOf<String>()

        // 1. A self-contained fat jar must declare NO dependencies (any would be an unpublished
        //    org.jetbrains:* module). Checking for the block itself also covers a future new module.
        val pomText = pomFile.get().asFile.readText()
        if (pomText.contains("<dependencies>")) {
            problems += "POM declares <dependencies>; a self-contained fat jar must have none"
        }
        if (pomText.contains("<packaging>pom</packaging>")) {
            problems += "POM packaging is 'pom'; expected a real 'jar' artifact"
        }

        // 2. The fat jar must be a SUPERSET of every runtime module's classes. A module that isn't
        //    bundled — including a brand-new one — leaves its classes missing here and fails the build.
        val fatEntries = ZipFile(jarFile.get().asFile).use { zip ->
            zip.entries().toList().map { it.name }.toSet()
        }
        val modules = runtimeModuleJars.get()
        if (modules.isEmpty()) {
            problems += "No local modules found on the runtime classpath; cannot verify bundling"
        }
        modules.toSortedMap().forEach { (path, jar) ->
            val moduleClasses = ZipFile(jar).use { zip ->
                zip.entries().toList().map { it.name }.filter { it.endsWith(".class") }.toSet()
            }
            val missing = moduleClasses - fatEntries
            if (missing.isNotEmpty()) {
                problems += "Fat jar is missing ${missing.size} class(es) from module '$path' " +
                    "(e.g. ${missing.min()}); add it to buildGrammarKitJar"
            }
        }

        // 3. Standalone generation template + manifest must be present.
        listOf("templates/GeneratedParserUtilBase.java", "META-INF/MANIFEST.MF").forEach {
            if (it !in fatEntries) problems += "Fat jar is missing required entry: $it"
        }

        // 4. No Gradle module metadata: a `.module` file would re-introduce the module variants.
        if (moduleFile.get().asFile.exists()) {
            problems += "Gradle module metadata was generated; it would point consumers at unpublished module variants"
        }

        if (problems.isNotEmpty()) {
            throw GradleException("Maven distribution structure check failed:\n" + problems.joinToString("\n") { "  - $it" })
        }
        logger.lifecycle(
            "Maven distribution OK: self-contained jar (${fatEntries.count { it.endsWith(".class") }} classes) " +
                "bundling ${modules.size} modules ${modules.keys.sorted()}, dependency-free POM, no module metadata."
        )
    }
}

val verifyPluginDistribution by tasks.registering {
    group = "verification"
    description = "Fails unless the plugin distribution bundles every module jar (plus the main jar) under lib/."
    val buildPluginTask = tasks.named("buildPlugin")
    dependsOn(buildPluginTask)

    val zipFile = buildPluginTask.map { it.outputs.files.singleFile }
    val moduleNames = bundledProjectPaths.map { it.removePrefix(":") }
    inputs.file(zipFile)

    doLast {
        val libJars = ZipFile(zipFile.get()).use { zip ->
            zip.entries().toList().map { it.name }
        }.filter { it.matches(Regex(".*/lib/[^/]+\\.jar$")) }

        val problems = mutableListOf<String>()
        moduleNames.forEach { module ->
            if (libJars.none { it.endsWith(".$module.jar") }) {
                problems += "Plugin distribution is missing the '$module' module jar under lib/"
            }
        }
        if (libJars.none { it.substringAfterLast('/').startsWith("grammar-kit-") }) {
            problems += "Plugin distribution is missing the main grammar-kit jar under lib/"
        }

        if (problems.isNotEmpty()) {
            throw GradleException("Plugin distribution structure check failed:\n" + problems.joinToString("\n") { "  - $it" })
        }
        logger.lifecycle("Plugin distribution OK: ${libJars.size} jars under lib/ (all modules bundled).")
    }
}

// Gate CI (`check`) and — critically — the Maven Central upload on the structure guards.
tasks.named("check") {
    dependsOn(verifyMavenDistribution, verifyPluginDistribution)
}
tasks.named("packSonatypeCentralBundle") {
    dependsOn(verifyMavenDistribution)
}
