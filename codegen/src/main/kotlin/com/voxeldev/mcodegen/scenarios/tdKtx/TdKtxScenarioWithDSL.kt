package com.voxeldev.mcodegen.scenarios.tdCommon

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
        val sourceIR : IR = JavaModule.parse(sourcePath = "path/to/source/file.java")
        
        KotlinModule.generate(
            source = sourceIR,
            mappers = listOf(TdKtxFunctionsMapper()),
        )

        KotlinModule.generate(
            source = sourceIR,
            mappers = listOf(TdKtxUpdatesMapper()),
        )
    }

    val configuration = scenarioManagerConfiguration {
        runScenario(tdKtxScenario)
    }

    scenarioManager.runConfiguration(configuration)
}

class TdKtxFunctionsMapper : GenerationMapper {

    override fun map(source: IR): IR {
        // Map classes to wrappers
    }
}

class TdKtxUpdatesMapper : GenerationMapper {

    override fun map(source: IR): IR {
        // Map classes to wrappers
    }
}