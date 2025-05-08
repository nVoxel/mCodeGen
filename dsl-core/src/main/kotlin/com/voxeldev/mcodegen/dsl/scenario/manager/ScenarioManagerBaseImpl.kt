package com.voxeldev.mcodegen.dsl.scenario.manager

import com.voxeldev.mcodegen.dsl.scenario.manager.configuration.ScenarioManagerConfiguration

class ScenarioManagerBaseImpl internal constructor() : ScenarioManager {
    override fun runConfiguration(configuration: ScenarioManagerConfiguration) {
        configuration.scenarios.forEach { scenario -> scenario.run() }
    }
}

fun baseScenarioManager() = ScenarioManagerBaseImpl()