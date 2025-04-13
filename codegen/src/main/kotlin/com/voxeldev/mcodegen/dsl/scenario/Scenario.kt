package com.voxeldev.mcodegen.dsl.scenario

import com.voxeldev.mcodegen.dsl.scenario.configuration.ScenarioConfiguration

interface Scenario {
    fun run()
}

data class ScenarioScope(
    val scenarioName: String,
    val scenarioConfiguration: ScenarioConfiguration,
)