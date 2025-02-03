rootProject.name = "mCodeGen"

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        jcenter()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        jcenter()
        maven {
            url = uri("http://zoidberg.ukp.informatik.tu-darmstadt.de/artifactory/public-releases/")
            isAllowInsecureProtocol = true
        }
    }
}

include("codegen")
