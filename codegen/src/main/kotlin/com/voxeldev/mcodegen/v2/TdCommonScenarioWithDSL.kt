package com.voxeldev.mcodegen.v2

import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.v2.dsl.utils.source.edit.scenario.appendJavaInterfacesEditScenario
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.scenario.baseScenario
import com.voxeldev.mcodegen.dsl.scenario.configuration.baseScenarioConfiguration
import com.voxeldev.mcodegen.dsl.scenario.manager.baseScenarioManager
import com.voxeldev.mcodegen.dsl.scenario.manager.configuration.scenarioManagerConfiguration
import com.voxeldev.mcodegen.dsl.utils.source.unify.UnifyClassesStrategyByNameAndFields
import com.voxeldev.mcodegen.dsl.utils.source.unify.unifySourcesList
import com.voxeldev.mcodegen.v2.dsl.utils.source.edit.step.AddJavaGettersEditStepHandler
import com.voxeldev.mcodegen.v2.dsl.utils.source.edit.step.AddJavaImportEditStepHandler
import com.voxeldev.mcodegen.v2.dsl.utils.source.edit.step.AppendJavaInterfacesEditStepHandler

fun main() {
    val scenarioManager = baseScenarioManager()

    val tdLibScenarioConfguration = baseScenarioConfiguration {
        setSourcesDir("../../Downloads/")
        setOutputDir("generated")

        addEditStepHandler(AddJavaImportEditStepHandler())
        addEditStepHandler(AppendJavaInterfacesEditStepHandler())
        addEditStepHandler(AddJavaGettersEditStepHandler())
    }

    val tdLibScenario = baseScenario(
        name = "TDLib scenario",
        configuration = tdLibScenarioConfguration,
    ) {
        val androidSourceIR : IrFile = JavaModule.parse(sourcePath = "TdApiAndroid.java")
        val desktopSourceIR : IrFile = JavaModule.parse(sourcePath = "TdApiDesktop.java")

        runJavaTests()
        runKotlinTests()

        // val iosSourceIR : IR = SwiftModule.parse(sourcePath = "path/to/ios/source/file.swift")

        val commonClasses = unifySourcesList(
            strategy = UnifyClassesStrategyByNameAndFields(),
            androidSourceIR, desktopSourceIR, /*iosSourceIR*/
        )

        /*KotlinModule.generate(
            source = commonClasses,
            mappers = listOf(kmpCommonInterfacesMapper()),
        )

        KotlinModule.generate(
            source = commonClasses,
            applyToBasePath = "android"
            mappers = listOf(kmpInstanceGetterImplsMapper()),
        )

        KotlinModule.generate(
            source = commonClasses,
            applyToBasePath = "desktop",
            mappers = listOf(kmpInstanceGetterImplsMapper()),
        )

        KotlinModule.generate(
            source = commonClasses,
            applyToBasePath = "android",
            mappers = listOf(kmpInstanceGettersKoinModulesMapper()),
        )

        KotlinModule.generate(
            source = commonClasses,
            applyToBasePath = "desktop",
            mappers = listOf(kmpInstanceGettersKoinModulesMapper()),
        )

        JavaModule.edit(
            sourcePath = "TdApiAndroid.java",
            editScenario = appendJavaInterfacesEditScenario(commonClasses),
        )

        JavaModule.edit(
            sourcePath = "TdApiDesktop.java",
            editScenario = appendJavaInterfacesEditScenario(commonClasses),
        )

        SwiftModule.edit(
            sourcePath = "path/to/ios/source/file.swift",
            editScenario = appendSwiftInterfacesEditScenario(),
        )*/
    }

    val configuration = scenarioManagerConfiguration {
        runScenario(tdLibScenario)
    }

    scenarioManager.runConfiguration(configuration)
}

context(ScenarioScope)
private fun runJavaTests() {
    val testConstructorsIR: IrFile = JavaModule.parse(sourcePath = "test_java_constructors.java")
    JavaModule.generate(testConstructorsIR, "test", listOf())

    val testInitializersIR: IrFile = JavaModule.parse(sourcePath = "test_java_initializers.java")
    JavaModule.generate(testInitializersIR, "test", listOf())

    val testExpressionsIR: IrFile = JavaModule.parse(sourcePath = "test_java_expressions.java")
    JavaModule.generate(testExpressionsIR, "test", listOf())

    val testAnnotationsIR: IrFile = JavaModule.parse(sourcePath = "test_java_annotations.java")
    JavaModule.generate(testAnnotationsIR, "test", listOf())

    val testStatementsIR: IrFile = JavaModule.parse(sourcePath = "test_java_statements.java")
    JavaModule.generate(testStatementsIR, "test", listOf())

    val testGenericsIR: IrFile = JavaModule.parse(sourcePath = "test_java_generic.java")
    JavaModule.generate(testGenericsIR, "test", listOf())
}

context(ScenarioScope)
private fun runKotlinTests() {
    val kotlinTests = KotlinModule.parseMultiple(
        "testStatementsIR" to "test_kotlin_statements.kt",
        "testConstructorsIR" to "test_kotlin_constructors.kt",
        "testExpressionsIR" to "test_kotlin_expressions.kt",
        "testPropertiesIR" to "test_kotlin_properties.kt",
        "testInheritanceIR" to "test_kotlin_inheritance.kt",
        "testInitializersIR" to "test_kotlin_initializers.kt",
        "testAnnotationsIR" to "test_kotlin_annotations.kt",
        "testGenericsIR" to "test_kotlin_generic.kt",
    )

    val testStatementsIR by kotlinTests
    KotlinModule.generate(testStatementsIR, "test", listOf())

    val testConstructorsIR by kotlinTests
    KotlinModule.generate(testConstructorsIR, "test", listOf())

    val testExpressionsIR by kotlinTests
    KotlinModule.generate(testExpressionsIR, "test", listOf())

    val testPropertiesIR by kotlinTests
    KotlinModule.generate(testPropertiesIR, "test", listOf())

    val testInheritanceIR by kotlinTests
    KotlinModule.generate(testInheritanceIR, "test", listOf())

    val testInitializersIR by kotlinTests
    KotlinModule.generate(testInitializersIR, "test", listOf())

    val testAnnotationsIR by kotlinTests
    KotlinModule.generate(testAnnotationsIR, "test", listOf())

    val testGenericsIR by kotlinTests
    KotlinModule.generate(testGenericsIR, "test", listOf())
}

/*
class UnifyStrategyByNameAndMethods : UnifySourcesStrategy {
    override fun unify(sources: List<SourceIR>): SourceIR {
        // Implement the logic to unify the sources
    }
}*/
