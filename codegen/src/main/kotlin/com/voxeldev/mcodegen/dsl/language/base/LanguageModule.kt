package com.voxeldev.mcodegen.dsl.language.base

import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

interface LanguageModule {

    context(ScenarioScope)
    fun parse(sourcePath: String): IrFile

    context(ScenarioScope)
    fun generateJava(source: IrFile, mappers: Any)

    context(ScenarioScope)
    fun editJava(sourcePath: String, mappers: Any)
}