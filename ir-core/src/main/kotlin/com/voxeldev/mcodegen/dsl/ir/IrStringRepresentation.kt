package com.voxeldev.mcodegen.dsl.ir

import kotlinx.serialization.Serializable

/**
 * Represents a string representation of an IR (Intermediate Representation) element.
 * This class is used to store how an IR element should be represented as a string
 * in the generated code.
 */
@Serializable
data class IrStringRepresentation(
    val language: String,
    val representation: String,
)