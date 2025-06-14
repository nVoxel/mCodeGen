package com.voxeldev.mcodegen.v2

import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.scenario.baseScenario
import com.voxeldev.mcodegen.dsl.scenario.configuration.baseScenarioConfiguration
import com.voxeldev.mcodegen.dsl.scenario.manager.baseScenarioManager
import com.voxeldev.mcodegen.dsl.scenario.manager.configuration.scenarioManagerConfiguration

fun main() {
    val scenarioManager = baseScenarioManager()

    val testsScenarioConfiguration = baseScenarioConfiguration {
        setSourcesDir("language-test/sources")
        setOutputDir("language-test/results")
    }

    val testsScenario = baseScenario(
        name = "Tests scenario",
        configuration = testsScenarioConfiguration,
    ) {
        runJavaTests()
        runKotlinTests()
    }

    val configuration = scenarioManagerConfiguration {
        runScenario(testsScenario)
    }

    scenarioManager.runConfiguration(configuration)
}

context(ScenarioScope)
private fun runJavaTests() {
    val testConstructorsIR: IrFile = JavaModule.parse(sourcePath = "java/test_java_constructors.java")
    JavaModule.generate(testConstructorsIR, "jvm", listOf())

    val testInitializersIR: IrFile = JavaModule.parse(sourcePath = "java/test_java_initializers.java")
    JavaModule.generate(testInitializersIR, "jvm", listOf())

    val testExpressionsIR: IrFile = JavaModule.parse(sourcePath = "java/test_java_expressions.java")
    JavaModule.generate(testExpressionsIR, "jvm", listOf())

    val testAnnotationsIR: IrFile = JavaModule.parse(sourcePath = "java/test_java_annotations.java")
    JavaModule.generate(testAnnotationsIR, "jvm", listOf())

    val testStatementsIR: IrFile = JavaModule.parse(sourcePath = "java/test_java_statements.java")
    JavaModule.generate(testStatementsIR, "jvm", listOf())

    val testGenericsIR: IrFile = JavaModule.parse(sourcePath = "java/test_java_generic.java")
    JavaModule.generate(testGenericsIR, "jvm", listOf())
}

context(ScenarioScope)
private fun runKotlinTests() {
    val kotlinTests = KotlinModule.parseMultiple(
        "testStatementsIR" to "kotlin/test_kotlin_statements.kt",
        "testConstructorsIR" to "kotlin/test_kotlin_constructors.kt",
        "testExpressionsIR" to "kotlin/test_kotlin_expressions.kt",
        "testPropertiesIR" to "kotlin/test_kotlin_properties.kt",
        "testInheritanceIR" to "kotlin/test_kotlin_inheritance.kt",
        "testInitializersIR" to "kotlin/test_kotlin_initializers.kt",
        "testAnnotationsIR" to "kotlin/test_kotlin_annotations.kt",
        "testGenericsIR" to "kotlin/test_kotlin_generic.kt",
        "testCastIR" to "kotlin/test_kotlin_cast.kt",
    )

    val testStatementsIR by kotlinTests
    KotlinModule.generate(testStatementsIR, "jvm", listOf())

    val testConstructorsIR by kotlinTests
    KotlinModule.generate(testConstructorsIR, "jvm", listOf())

    val testExpressionsIR by kotlinTests
    KotlinModule.generate(testExpressionsIR, "jvm", listOf())

    val testPropertiesIR by kotlinTests
    KotlinModule.generate(testPropertiesIR, "jvm", listOf())

    val testInheritanceIR by kotlinTests
    KotlinModule.generate(testInheritanceIR, "jvm", listOf())

    val testInitializersIR by kotlinTests
    KotlinModule.generate(testInitializersIR, "jvm", listOf())

    val testAnnotationsIR by kotlinTests
    KotlinModule.generate(testAnnotationsIR, "jvm", listOf())

    val testGenericsIR by kotlinTests
    KotlinModule.generate(testGenericsIR, "jvm", listOf())

    val testCastIR by kotlinTests
    KotlinModule.generate(testCastIR, "jvm", listOf())
}
