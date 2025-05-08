package com.voxeldev.mcodegen.dsl.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrImport

/**
 * Creates a new [IrImportBuilder] instance with the given import path and wildcard flag.
 */
fun irImport(
    path: String,
    isWildcard: Boolean,
): IrImportBuilder = IrImportBuilder(
    path = path,
    isWildcard = isWildcard,
)

/**
 * Builder class for creating [IrImport] instances.
 */
class IrImportBuilder(
    private val path: String,
    private val isWildcard: Boolean,
) : IrElementBuilder() {

    fun build(): IrImport {
        return IrImport(
            path = path,
            isWildcard = isWildcard,
            location = location,
            annotations = annotations,
            languageProperties = languageProperties,
        )
    }
} 