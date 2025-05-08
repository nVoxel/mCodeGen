package com.voxeldev.mcodegen.v2

import com.voxeldev.mcodegen.v1.GlobalConstants
import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.baseScenario
import com.voxeldev.mcodegen.dsl.scenario.configuration.baseScenarioConfiguration
import com.voxeldev.mcodegen.dsl.scenario.manager.baseScenarioManager
import com.voxeldev.mcodegen.dsl.scenario.manager.configuration.scenarioManagerConfiguration

fun main() {
    val scenarioManager = baseScenarioManager()

    val tdKtxScenarioConfiguration = baseScenarioConfiguration {
        setSourcesDir("source-code-directory")
        setOutputDir("produced-code-directory")
    }

    val tdKtxScenario = baseScenario(
        name = "TdKtx scenario",
        configuration = tdKtxScenarioConfiguration,
    ) {
        val sourceIR : IrFile = JavaModule.parse(sourcePath = GlobalConstants.ANDROID_SOURCE_PATH)
        
        /*KotlinModule.generate(
            source = sourceIR,
            mappers = listOf(tdKtxFunctionsMapper()),
        )

        KotlinModule.generate(
            source = sourceIR,
            mappers = listOf(tdKtxUpdatesMapper()),
        )*/
    }

    val configuration = scenarioManagerConfiguration {
        runScenario(tdKtxScenario)
    }

    scenarioManager.runConfiguration(configuration)
}
