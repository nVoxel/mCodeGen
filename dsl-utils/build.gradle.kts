plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.kotlin.jdk8)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.compiler)
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
        freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
        freeCompilerArgs.add("-Xsuppress-warning=CONTEXT_RECEIVERS_DEPRECATED")
    }
}