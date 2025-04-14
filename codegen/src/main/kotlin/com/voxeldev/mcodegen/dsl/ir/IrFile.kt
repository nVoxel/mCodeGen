package com.voxeldev.mcodegen.dsl.ir

data class IrFile(
    val name: String,
    val imports: List<IrImport>,
    val declarations: List<IrElement>,
    val languageProperties: Map<String, Any> = emptyMap()
)