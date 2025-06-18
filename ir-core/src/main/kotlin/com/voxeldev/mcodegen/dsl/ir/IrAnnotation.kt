package com.voxeldev.mcodegen.dsl.ir

import com.voxeldev.mcodegen.dsl.ir.utils.MapStringAnySerializer
import kotlinx.serialization.Serializable

/**
 * Represents an annotation in the IR (Intermediate Representation) system.
 * Annotations provide additional metadata about code elements.
 */
@Serializable
data class IrAnnotation(
    val name: String,
    val parameters: List<IrAnnotationParameter>,
    @Serializable(with = MapStringAnySerializer::class)
    val languageProperties: Map<String, Any>,
)

@Serializable
data class IrAnnotationParameter(
    val parameterName: String,
    val parameterValue: IrExpression,
)
