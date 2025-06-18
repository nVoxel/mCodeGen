package com.voxeldev.mcodegen.dsl.ir

import com.voxeldev.mcodegen.dsl.ir.utils.MapStringAnySerializer
import kotlinx.serialization.Serializable

/**
 * Represents an import statement in the IR (Intermediate Representation) system.
 * This class contains the information needed to generate an import statement in the target language.
 */
@Serializable
data class IrImport(
    val path: String,
    val isWildcard: Boolean,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement