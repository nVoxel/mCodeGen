package com.voxeldev.mcodegen.scenarios.tdCommon

import com.voxeldev.mcodegen.dsl.scenario.baseScenario
import com.voxeldev.mcodegen.dsl.scenario.configuration.baseScenarioConfiguration
import com.voxeldev.mcodegen.dsl.scenario.manager.baseScenarioManager
import com.voxeldev.mcodegen.dsl.scenario.manager.configuration.scenarioManagerConfiguration


fun main() {
    val scenarioManager = baseScenarioManager()

    val tdLibScenarioConfguration = baseScenarioConfiguration {
        setSourcesDir("source-code-directory")
        setOutputDir("produced-code-directory")
    }

    val tdLibScenario = baseScenario(
        name = "TDLib scenario",
        configuration = tdLibScenarioConfguration,
    ) {
        val androidSourceIR : IR = JavaModule.parse(sourcePath = "path/to/android/source/file.java")
        val desktopSourceIR : IR = JavaModule.parse(sourcePath = "path/to/desktop/source/file.java")
        // val iosSourceIR : IR = SwiftModule.parse(sourcePath = "path/to/ios/source/file.swift")

        val commonClasses = unifySources(
            strategy = UnifyStrategyByNameAndMethods(),
            sources = listOf(androidSourceIR, desktopSourceIR, /*iosSourceIR*/),
        )

        KotlinModule.generate(
            source = commonClasses,
            mappers = listOf(KMPCommonInterfacesMapper()),
        )

        KotlinModule.generate(
            source = commonClasses,
            applyToBasePath = "android"
            mappers = listOf(KMPInstanceGetterImplsMapper()),
        )

        KotlinModule.generate(
            source = commonClasses,
            applyToBasePath = "desktop",
            mappers = listOf(KMPInstanceGetterImplsMapper()),
        )

        KotlinModule.generate(
            source = commonClasses,
            applyToBasePath = "android",
            mappers = listOf(KMPInstanceGettersKoinModulesMapper()),
        )

        KotlinModule.generate(
            source = commonClasses,
            applyToBasePath = "desktop",
            mappers = listOf(KMPInstanceGettersKoinModulesMapper()),
        )

        JavaModule.edit(
            sourcePath = "path/to/android/source/file.java",
            mappers = listOf(AppendInterfacesMapper()),
        )

        JavaModule.edit(
            sourcePath = "path/to/desktop/source/file.java",
            mappers = listOf(AppendJavaInterfacesMapper()),
        )

        // SwiftModule.edit(
        //     sourcePath = "path/to/ios/source/file.swift",
        //     mappers = listOf(AppendSwiftInterfacesMapper()),
        // )
    }

    val configuration = scenarioManagerConfiguration {
        runScenario(tdLibScenario)
    }

    scenarioManager.runConfiguration(configuration)
}

class UnifyStrategyByNameAndMethods : UnifySourcesStrategy {
    override fun unify(sources: List<SourceIR>): SourceIR {
        // Implement the logic to unify the sources
    }
}

class KMPCommonInterfacesMapper : GenerationMapper {

    override fun map(source: IR): IR {
        // Map unified classes to common interfaces
    }
}

class KMPInstanceGetterImplsMapper : GenerationMapper {
    override fun map(source: IR): IR {
        // Map unified classes to their instance getters for specific platform (Android, Desktop)
    }
}

class KMPInstanceGettersKoinModulesMapper : GenerationMapper {
    override fun map(source: IR): IR {
        // Map unified classes to Koin modules for each instance getter (Android, Desktop)
    }
}

class AppendJavaInterfacesScenario : BaseEditScenario {

    override fun getSteps(): List<EditStep> {
        // Define how the source code should be edited using steps
    }
}

class AppendSwiftInterfacesScenario : BaseEditScenario {

    override fun getSteps(): List<EditStep> {
        // Define how the source code should be edited using steps
    }
}