package com.voxeldev.mcodegen.dsl.ir

/**
 * Represents an annotation in the IR (Intermediate Representation) system.
 * Annotations provide additional metadata about code elements.
 */
data class IrAnnotation(
    val name: String,
    val parameters: List<IrAnnotationParameter>,
    val languageProperties: Map<String, Any>,
)

data class IrAnnotationParameter(
    val parameterName: String,
    val parameterValue: IrExpression,
)
