plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.kotlin.jdk8)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.compiler)

    implementation(libs.javapoet)
    implementation(libs.kotlinpoet)

    implementation(libs.jna)
    implementation(libs.javaparser)
}


kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}