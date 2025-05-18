package com.voxeldev.mcodegen.dsl.source.generate.mapper

import com.voxeldev.mcodegen.dsl.ir.IrFile

interface GenerationListMapper {

    fun map(sources: List<IrFile>): List<IrFile>
}