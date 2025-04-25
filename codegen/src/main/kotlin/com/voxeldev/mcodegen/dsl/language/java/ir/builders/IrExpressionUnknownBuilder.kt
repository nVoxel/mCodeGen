package com.voxeldev.mcodegen.dsl.language.java.ir.builders

import com.voxeldev.mcodegen.dsl.ir.builders.IrExpressionBuilder
import com.voxeldev.mcodegen.dsl.language.java.ir.IrExpressionUnknown

/**
 * Creates a new [IrExpressionUnknownBuilder] instance.
 */
fun irExpressionUnknown(): IrExpressionUnknownBuilder = IrExpressionUnknownBuilder()

/**
 * Builder class for creating [IrExpressionUnknown] instances.
 */
class IrExpressionUnknownBuilder : IrExpressionBuilder() {
    fun build(): IrExpressionUnknown {
        val properties = buildExpressionProperties()
        return IrExpressionUnknown(
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
} 