package com.voxeldev.mcodegen.dsl.scenario.configuration

class ScenarioConfigurationBaseImpl private constructor(
    override val sourcesDir: String,
    override val outputDir: String,
) : ScenarioConfiguration {
    class Builder {
        private var sourcesDir: String = ""
        private var outputDir: String = ""

        fun setSourcesDir(dir: String) = apply {
            require(dir.isNotBlank()) { "Sources directory cannot be blank" }
            sourcesDir = dir
        }

        fun setOutputDir(dir: String) = apply {
            require(dir.isNotBlank()) { "Output directory cannot be blank" }
            outputDir = dir
        }

        fun build(): ScenarioConfigurationBaseImpl {
            require(sourcesDir.isNotBlank()) { "Sources directory must be set" }
            require(outputDir.isNotBlank()) { "Output directory must be set" }
            return ScenarioConfigurationBaseImpl(sourcesDir, outputDir)
        }
    }
}

fun baseScenarioConfiguration(block: ScenarioConfigurationBaseImpl.Builder.() -> Unit): ScenarioConfiguration {
    return ScenarioConfigurationBaseImpl.Builder().apply(block).build()
}
