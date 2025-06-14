plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.swiftpoet)

    implementation(projects.dslCore)
    implementation(projects.dslUtils)
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
        freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
        freeCompilerArgs.add("-Xsuppress-warning=CONTEXT_RECEIVERS_DEPRECATED")
    }
}