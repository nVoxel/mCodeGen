plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(projects.irCore)

    api(projects.dslUtils)
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
        freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
        freeCompilerArgs.add("-Xsuppress-warning=CONTEXT_RECEIVERS_DEPRECATED")
    }
}