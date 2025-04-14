package com.voxeldev.mcodegen.dsl.ir

data class IrImport(
    val path: String,
    val isWildcard: Boolean,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement