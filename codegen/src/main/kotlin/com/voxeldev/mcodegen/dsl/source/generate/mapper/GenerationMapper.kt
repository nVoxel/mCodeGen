package com.voxeldev.mcodegen.dsl.source.generate.mapper

import com.voxeldev.mcodegen.dsl.ir.IrFile

interface GenerationMapper {

    fun map(source: IrFile): IrFile
}