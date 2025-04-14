package com.voxeldev.mcodegen.dsl.ir

sealed interface IrElement {
    val location: IrLocation?
    val annotations: List<IrAnnotation>
    val languageProperties: Map<String, Any>
}