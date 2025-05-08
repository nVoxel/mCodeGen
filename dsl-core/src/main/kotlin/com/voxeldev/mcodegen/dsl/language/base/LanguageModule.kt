package com.voxeldev.mcodegen.dsl.language.base

import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.edit.scenario.EditScenario
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationMapper

interface LanguageModule {

    val languageName: String

    context(ScenarioScope)
    fun parse(sourcePath: String): IrFile

    context(ScenarioScope)
    fun generate(
        source: IrFile,
        applyToBasePath: String = "",
        mappers: List<GenerationMapper>,
    )

    context(ScenarioScope)
    fun edit(sourcePath: String, editScenario: EditScenario)
}