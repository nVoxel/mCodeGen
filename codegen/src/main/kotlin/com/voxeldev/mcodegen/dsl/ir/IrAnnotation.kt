package com.voxeldev.mcodegen.dsl.ir

data class IrAnnotation(
    val name: String,
    val languageProperties: Map<String, Any>,
)
