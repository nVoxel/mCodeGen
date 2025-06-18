package com.voxeldev.mcodegen.dsl.ir

import com.voxeldev.mcodegen.dsl.ir.utils.MapStringAnySerializer
import kotlinx.serialization.Serializable

/**
 * Represents a source file in the IR (Intermediate Representation) system.
 * This class contains all the necessary information to generate a source file,
 * including its package name, imports, and class definitions.
 */
@Serializable
data class IrFile(
    val name: String,
    val imports: List<IrImport>,
    val declarations: List<IrElement>,
    @Serializable(with = MapStringAnySerializer::class)
    val languageProperties: Map<String, Any> = emptyMap()
)