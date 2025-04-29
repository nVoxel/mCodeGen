package com.voxeldev.mcodegen.scenarios.tdCommon

import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.baseScenario
import com.voxeldev.mcodegen.dsl.scenario.configuration.baseScenarioConfiguration
import com.voxeldev.mcodegen.dsl.scenario.manager.baseScenarioManager
import com.voxeldev.mcodegen.dsl.scenario.manager.configuration.scenarioManagerConfiguration


fun main() {
    val scenarioManager = baseScenarioManager()

    val tdLibScenarioConfguration = baseScenarioConfiguration {
        setSourcesDir("../../Downloads/")
        setOutputDir("generated")
    }

    val tdLibScenario = baseScenario(
        name = "TDLib scenario",
        configuration = tdLibScenarioConfguration,
    ) {
        val androidSourceIR : IrFile = JavaModule.parse(sourcePath = "TdApiAndroid.java")
        val desktopSourceIR : IrFile = JavaModule.parse(sourcePath = "TdApiDesktop.java")

        // for test
        val testStatementsIR : IrFile = JavaModule.parse(sourcePath = "test_java_statements.java")
        JavaModule.generate(testStatementsIR, "test", listOf())

        // for test
        val testExpressionsIR : IrFile = JavaModule.parse(sourcePath = "test_java_expressions.java")
        JavaModule.generate(testExpressionsIR, "test", listOf())

        // for test
        val testGenericsIR : IrFile = JavaModule.parse(sourcePath = "test_java_generic.java")
        JavaModule.generate(testGenericsIR, "test", listOf())

        // val iosSourceIR : IR = SwiftModule.parse(sourcePath = "path/to/ios/source/file.swift")

        /*val commonClasses = unifySources(
            strategy = UnifyStrategyByNameAndMethods(),
            sources = listOf(androidSourceIR, desktopSourceIR, *//*iosSourceIR*//*),
        )

        KotlinModule.generate(
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
            sourcePath = "path/to/android/source/file.java",
            editScenario = appendJavaInterfacesEditScenario(),
        )

        JavaModule.edit(
            sourcePath = "path/to/desktop/source/file.java",
            editScenario = appendJavaInterfacesEditScenario(),
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

/*
class UnifyStrategyByNameAndMethods : UnifySourcesStrategy {
    override fun unify(sources: List<SourceIR>): SourceIR {
        // Implement the logic to unify the sources
    }
}*/
