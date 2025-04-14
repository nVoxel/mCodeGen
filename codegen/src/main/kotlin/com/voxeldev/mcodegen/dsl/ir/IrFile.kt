package com.voxeldev.mcodegen.dsl.ir

/**
 * Represents a source file in the IR (Intermediate Representation) system.
 * This class contains all the necessary information to generate a source file,
 * including its package name, imports, and class definitions.
 */
data class IrFile(
    val name: String,
    val imports: List<IrImport>,
    val declarations: List<IrElement>,
    val languageProperties: Map<String, Any> = emptyMap()
)