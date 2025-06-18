package com.voxeldev.mcodegen.dsl.ir

import kotlinx.serialization.Serializable

/**
 * Represents the visibility modifiers in the IR (Intermediate Representation) system.
 * This enum defines the different levels of visibility that can be applied to code elements.
 */
interface IrVisibility {
    val stringRepresentation: List<IrStringRepresentation>
}

@Serializable
data class IrVisibilityPublic(
    override val stringRepresentation: List<IrStringRepresentation>,
) : IrVisibility

@Serializable
data class IrVisibilityProtected(
    override val stringRepresentation: List<IrStringRepresentation>,
) : IrVisibility

@Serializable
data class IrVisibilityInternal(
    override val stringRepresentation: List<IrStringRepresentation>,
) : IrVisibility

@Serializable
data class IrVisibilityPrivate(
    override val stringRepresentation: List<IrStringRepresentation>,
) : IrVisibility
