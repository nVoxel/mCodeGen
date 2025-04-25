package com.voxeldev.mcodegen.dsl.language.java.ir.builders

import com.voxeldev.mcodegen.dsl.ir.builders.IrStatementBuilder
import com.voxeldev.mcodegen.dsl.language.java.ir.IrStatementUnknown

/**
 * Creates a new [IrStatementUnknownBuilder] instance.
 */
fun irStatementUnknown(): IrStatementUnknownBuilder = IrStatementUnknownBuilder()

/**
 * Builder class for creating [IrStatementUnknown] instances.
 */
class IrStatementUnknownBuilder : IrStatementBuilder() {
    fun build(): IrStatementUnknown {
        val properties = buildStatementProperties()
        return IrStatementUnknown(
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
} 