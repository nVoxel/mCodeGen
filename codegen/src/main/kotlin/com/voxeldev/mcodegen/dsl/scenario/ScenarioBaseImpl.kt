package com.voxeldev.mcodegen.dsl.scenario

import com.voxeldev.mcodegen.dsl.scenario.configuration.ScenarioConfiguration

class ScenarioBaseImpl internal constructor(
    val scenario: () -> Unit,
) : Scenario {
    override fun run() = scenario()
}

fun baseScenario(
    name: String,
    configuration: ScenarioConfiguration,
    scenario: ScenarioScope.() -> Unit,
) : Scenario {
    val scope = ScenarioScope(name, configuration)
    return ScenarioBaseImpl {
        scope.scenario()
    }
}

