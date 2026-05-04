rootProject.name = "grammar-kit"

pluginManagement {
    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        gradlePluginPortal()
    }
}

include(":base")
include(":parser-runtime")
include(":bnf-language")
include(":jflex-language")
include(":generator")
