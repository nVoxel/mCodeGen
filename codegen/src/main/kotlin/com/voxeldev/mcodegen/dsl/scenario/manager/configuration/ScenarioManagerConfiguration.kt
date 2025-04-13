package com.voxeldev.mcodegen.dsl.scenario.manager.configuration

import com.voxeldev.mcodegen.dsl.scenario.Scenario

class ScenarioManagerConfiguration internal constructor() {
    internal val scenarios = mutableListOf<Scenario>()

    fun runScenario(scenario: Scenario) {
        scenarios.add(scenario)
    }
}

fun scenarioManagerConfiguration(block: ScenarioManagerConfiguration.() -> Unit): ScenarioManagerConfiguration {
    val configuration = ScenarioManagerConfiguration()
    configuration.block()
    return configuration
}