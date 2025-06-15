plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.kotlin.jdk8)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.compiler)

    implementation(libs.javapoet)
    implementation(libs.kotlinpoet)
    implementation(libs.swiftpoet)

    implementation(libs.jna)
    implementation(libs.javaparser)

    implementation(projects.dslCore)
    implementation(projects.dslUtils)
    implementation(projects.languageModuleJava)
    implementation(projects.languageModuleKotlin)
    implementation(projects.languageModuleSwift)
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
        freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
        freeCompilerArgs.add("-Xsuppress-warning=CONTEXT_RECEIVERS_DEPRECATED")
    }
}