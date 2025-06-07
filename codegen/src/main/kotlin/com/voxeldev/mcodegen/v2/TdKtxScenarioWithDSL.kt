package com.voxeldev.mcodegen.v2

import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.baseScenario
import com.voxeldev.mcodegen.dsl.scenario.configuration.baseScenarioConfiguration
import com.voxeldev.mcodegen.dsl.scenario.manager.baseScenarioManager
import com.voxeldev.mcodegen.dsl.scenario.manager.configuration.scenarioManagerConfiguration
import com.voxeldev.mcodegen.dsl.utils.source.unify.UnifyClassesStrategyByNameAndFields
import com.voxeldev.mcodegen.dsl.utils.source.unify.unifySourcesList
import com.voxeldev.mcodegen.v2.dsl.utils.source.generate.mapper.tdKtxFunctionsMapper
import com.voxeldev.mcodegen.v2.dsl.utils.source.generate.mapper.tdKtxUpdatesMapper

fun main() {
    val scenarioManager = baseScenarioManager()

    val tdKtxScenarioConfiguration = baseScenarioConfiguration {
        setSourcesDir("../../Downloads/")
        setOutputDir("generated")
    }

    val tdKtxScenario = baseScenario(
        name = "TdKtxScenario",
        configuration = tdKtxScenarioConfiguration,
    ) {
        val androidSourceIR : IrFile = JavaModule.parse(sourcePath = "TdApiAndroid.java")
        val desktopSourceIR : IrFile = JavaModule.parse(sourcePath = "TdApiDesktop.java")
        // val iosSourceIR : IR = SwiftModule.parse(sourcePath = "path/to/ios/source/file.swift")

        val commonClasses = unifySourcesList(
            strategy = UnifyClassesStrategyByNameAndFields(),
            androidSourceIR, desktopSourceIR, /*iosSourceIR*/
        )
        
        KotlinModule.generate(
            source = commonClasses,
            mappers = listOf(tdKtxFunctionsMapper()),
        )

        KotlinModule.generate(
            source = commonClasses,
            mappers = listOf(tdKtxUpdatesMapper()),
        )
    }

    val configuration = scenarioManagerConfiguration {
        runScenario(tdKtxScenario)
    }

    scenarioManager.runConfiguration(configuration)
}
