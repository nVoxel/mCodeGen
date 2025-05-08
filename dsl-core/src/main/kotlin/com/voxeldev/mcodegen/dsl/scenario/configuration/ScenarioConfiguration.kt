package com.voxeldev.mcodegen.dsl.scenario.configuration

interface ScenarioConfiguration {
    val sourcesDir: String
    val outputDir: String
    val properties: List<ScenarioConfigurationProperty>
}

fun scenarioConfigurationProperty(language: String, propertyName: String, propertyValue: Any) =
    ScenarioConfigurationProperty(
        language = language,
        propertyName = propertyName,
        propertyValue = propertyValue,
    )

open class ScenarioConfigurationProperty internal constructor(
    val language: String,
    open val propertyName: String,
    open val propertyValue: Any,
)
