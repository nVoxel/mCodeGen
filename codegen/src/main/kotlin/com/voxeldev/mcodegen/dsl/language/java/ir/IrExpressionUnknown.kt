package com.voxeldev.mcodegen.dsl.language.java.ir

import com.voxeldev.mcodegen.dsl.ir.IrAnnotation
import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrLocation
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation

data class IrExpressionUnknown(
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)