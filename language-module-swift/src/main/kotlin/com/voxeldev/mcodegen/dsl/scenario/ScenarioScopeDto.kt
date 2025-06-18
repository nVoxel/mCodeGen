package com.voxeldev.mcodegen.dsl.scenario

import com.voxeldev.mcodegen.dsl.scenario.configuration.ScenarioConfigurationDto
import com.voxeldev.mcodegen.dsl.scenario.configuration.scenarioConfigurationDto
import kotlinx.serialization.Serializable

internal fun scenarioScopeDto(scenarioScope: ScenarioScope) = ScenarioScopeDto(
    scenarioName = scenarioScope.scenarioName,
    scenarioConfiguration = scenarioConfigurationDto(scenarioScope.scenarioConfiguration),
)

@Serializable
internal data class ScenarioScopeDto(
    val scenarioName: String,
    val scenarioConfiguration: ScenarioConfigurationDto,
)
