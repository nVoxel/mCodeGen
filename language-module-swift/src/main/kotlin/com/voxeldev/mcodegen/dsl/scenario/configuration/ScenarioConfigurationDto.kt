package com.voxeldev.mcodegen.dsl.scenario.configuration

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.io.path.Path
import kotlin.io.path.pathString

internal fun scenarioConfigurationDto(scenarioConfiguration: ScenarioConfiguration) = ScenarioConfigurationDto(
    sourcesDir = Path(scenarioConfiguration.sourcesDir).toAbsolutePath().pathString,
    outputDir = Path(scenarioConfiguration.outputDir).toAbsolutePath().pathString,
    properties = scenarioConfiguration.properties.map { property -> scenarioConfigurationPropertyDto(property) },
)

@Serializable
internal data class ScenarioConfigurationDto(
    val sourcesDir: String,
    val outputDir: String,
    // TODO: val editStepHandlers: Map<String, EditStepHandler<*>>,
    val properties: List<ScenarioConfigurationPropertyDto>,
)

internal fun scenarioConfigurationPropertyDto(scenarioConfigurationProperty: ScenarioConfigurationProperty) =
    ScenarioConfigurationPropertyDto (
        language = scenarioConfigurationProperty.language,
        propertyName = scenarioConfigurationProperty.propertyName,
        propertyValue = Json.encodeToJsonElement(scenarioConfigurationProperty.propertyValue),
    )

@Serializable
internal data class ScenarioConfigurationPropertyDto(
    val language: String,
    val propertyName: String,
    val propertyValue: JsonElement,
)
