package com.voxeldev.mcodegen.v2

import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.baseScenario
import com.voxeldev.mcodegen.dsl.scenario.configuration.baseScenarioConfiguration
import com.voxeldev.mcodegen.dsl.scenario.manager.baseScenarioManager
import com.voxeldev.mcodegen.dsl.scenario.manager.configuration.scenarioManagerConfiguration
import com.voxeldev.mcodegen.dsl.utils.source.unify.UnifyClassesStrategyByNameAndFields
import com.voxeldev.mcodegen.dsl.utils.source.unify.unifySourcesList
import com.voxeldev.mcodegen.v2.dsl.utils.source.edit.scenario.appendJavaInterfacesEditScenario
import com.voxeldev.mcodegen.v2.dsl.utils.source.edit.step.AddJavaGettersEditStepHandler
import com.voxeldev.mcodegen.v2.dsl.utils.source.edit.step.AddJavaImportEditStepHandler
import com.voxeldev.mcodegen.v2.dsl.utils.source.edit.step.AppendJavaInterfacesEditStepHandler
import com.voxeldev.mcodegen.v2.dsl.utils.source.generate.mapper.kmpCommonInterfacesMapper
import com.voxeldev.mcodegen.v2.dsl.utils.source.generate.mapper.kmpInstanceGetterImplsMapper
import com.voxeldev.mcodegen.v2.dsl.utils.source.generate.mapper.kmpInstanceGettersIosKoinModulesMapper
import com.voxeldev.mcodegen.v2.dsl.utils.source.generate.mapper.kmpInstanceGettersKoinModulesMapper
import com.voxeldev.mcodegen.v2.dsl.utils.source.generate.mapper.mapSwiftBoxes

fun main() {
    val scenarioManager = baseScenarioManager()

    val tdLibScenarioConfiguration = baseScenarioConfiguration {
        setSourcesDir("../../Downloads/")
        setOutputDir("generated")

        addEditStepHandler(AddJavaImportEditStepHandler())
        addEditStepHandler(AppendJavaInterfacesEditStepHandler())
        addEditStepHandler(AddJavaGettersEditStepHandler())
    }

    val tdLibScenario = baseScenario(
        name = "TdCommonScenario",
        configuration = tdLibScenarioConfiguration,
    ) {
        val androidSourceIR : IrFile = JavaModule.parse(sourcePath = "TdApiAndroid.java")
        val desktopSourceIR : IrFile = JavaModule.parse(sourcePath = "TdApiDesktop.java")
        // val iosSourceIR : IR = SwiftModule.parse(sourcePath = "path/to/ios/source/file.swift")

        val commonClasses = unifySourcesList(
            strategy = UnifyClassesStrategyByNameAndFields(),
            androidSourceIR, desktopSourceIR, /*iosSourceIR*/
        )

        mapSwiftBoxes(commonClasses)

        KotlinModule.generate(
            source = commonClasses,
            applyToBasePath = "common",
            mappers = listOf(kmpCommonInterfacesMapper()),
        )

        KotlinModule.generate(
            source = commonClasses,
            applyToBasePath = "android",
            mappers = listOf(kmpInstanceGetterImplsMapper()),
        )

        KotlinModule.generate(
            source = commonClasses,
            applyToBasePath = "desktop",
            mappers = listOf(kmpInstanceGetterImplsMapper()),
        )

        KotlinModule.generateMultiple(
            sources = listOf(commonClasses),
            applyToBasePath = "android",
            mappers = listOf(kmpInstanceGettersKoinModulesMapper()),
        )

        KotlinModule.generateMultiple(
            sources = listOf(commonClasses),
            applyToBasePath = "desktop",
            mappers = listOf(kmpInstanceGettersKoinModulesMapper()),
        )

        KotlinModule.generateMultiple(
            sources = listOf(commonClasses),
            applyToBasePath = "ios",
            mappers = listOf(kmpInstanceGettersIosKoinModulesMapper()),
        )

        JavaModule.edit(
            sourcePath = "TdApiAndroid.java",
            editScenario = appendJavaInterfacesEditScenario(commonClasses),
        )

        JavaModule.edit(
            sourcePath = "TdApiDesktop.java",
            editScenario = appendJavaInterfacesEditScenario(commonClasses),
        )

        /*SwiftModule.edit(
            sourcePath = "path/to/ios/source/file.swift",
            editScenario = appendSwiftInterfacesEditScenario(),
        )*/
    }

    val configuration = scenarioManagerConfiguration {
        runScenario(tdLibScenario)
    }

    scenarioManager.runConfiguration(configuration)
}
