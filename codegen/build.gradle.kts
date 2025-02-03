plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.kotlin.jdk8)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.compiler)

    implementation(libs.jna)
    implementation(libs.kotlinpoet)
    implementation(libs.javaparser)
}


kotlin {
    jvmToolchain(21)
}