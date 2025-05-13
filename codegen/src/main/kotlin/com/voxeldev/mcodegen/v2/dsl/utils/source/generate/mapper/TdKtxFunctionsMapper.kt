package com.voxeldev.mcodegen.v2.dsl.utils.source.generate.mapper

import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationMapperBaseImpl

context(ScenarioScope)
fun tdKtxFunctionsMapper(): TdKtxFunctionsMapper {
    return TdKtxFunctionsMapper()
}

context(ScenarioScope)
class TdKtxFunctionsMapper internal constructor(): GenerationMapperBaseImpl() {

    override fun map(source: IrFile): IrFile {
        TODO("Not yet implemented")
    }
}