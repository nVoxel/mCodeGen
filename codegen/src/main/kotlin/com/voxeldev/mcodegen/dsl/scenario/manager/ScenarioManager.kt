package com.voxeldev.mcodegen.dsl.scenario.manager

import com.voxeldev.mcodegen.dsl.scenario.manager.configuration.ScenarioManagerConfiguration

interface ScenarioManager {
    fun runConfiguration(configuration: ScenarioManagerConfiguration)
}