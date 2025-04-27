package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.mapper

import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationMapperBaseImpl

context(ScenarioScope)
fun tdKtxUpdatesMapper(): TdKtxUpdatesMapper {
    return TdKtxUpdatesMapper()
}

context(ScenarioScope)
class TdKtxUpdatesMapper internal constructor(): GenerationMapperBaseImpl() {

    override fun map(source: IrFile): IrFile {
        TODO("Not yet implemented")
    }
}