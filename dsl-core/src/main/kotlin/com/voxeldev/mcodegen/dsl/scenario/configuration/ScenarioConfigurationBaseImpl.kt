package com.voxeldev.mcodegen.dsl.scenario.configuration

import com.voxeldev.mcodegen.dsl.source.edit.step.EditStepHandler

class ScenarioConfigurationBaseImpl private constructor(
    override val sourcesDir: String,
    override val outputDir: String,
    override val editStepHandlers: Map<String, EditStepHandler<*>>,
    override val properties: List<ScenarioConfigurationProperty>,
) : ScenarioConfiguration {
    class Builder {
        private var sourcesDir: String = ""
        private var outputDir: String = ""
        private val editStepHandlers: MutableMap<String, EditStepHandler<*>> = hashMapOf()
        private val properties: MutableList<ScenarioConfigurationProperty> = mutableListOf()

        fun setSourcesDir(dir: String) {
            require(dir.isNotBlank()) { "Sources directory cannot be blank" }
            sourcesDir = dir
        }

        fun setOutputDir(dir: String) {
            require(dir.isNotBlank()) { "Output directory cannot be blank" }
            outputDir = dir
        }

        fun addEditStepHandler(editStepHandler: EditStepHandler<*>) {
            editStepHandlers[editStepHandler.handlingEditStepName] = editStepHandler
        }

        fun addProperty(property: ScenarioConfigurationProperty) {
            properties.add(property)
        }

        fun build(): ScenarioConfigurationBaseImpl {
            require(sourcesDir.isNotBlank()) { "Sources directory must be set" }
            require(outputDir.isNotBlank()) { "Output directory must be set" }
            return ScenarioConfigurationBaseImpl(sourcesDir, outputDir, editStepHandlers, properties)
        }
    }
}

fun baseScenarioConfiguration(block: ScenarioConfigurationBaseImpl.Builder.() -> Unit): ScenarioConfiguration {
    return ScenarioConfigurationBaseImpl.Builder().apply(block).build()
}
