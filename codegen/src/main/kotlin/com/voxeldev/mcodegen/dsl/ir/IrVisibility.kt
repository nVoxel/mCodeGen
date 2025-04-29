package com.voxeldev.mcodegen.dsl.ir

/**
 * Represents the visibility modifiers in the IR (Intermediate Representation) system.
 * This enum defines the different levels of visibility that can be applied to code elements.
 */
open class IrVisibility(
    open val stringRepresentation: List<IrStringRepresentation>,
)

data class IrVisibilityPublic(
    override val stringRepresentation: List<IrStringRepresentation>,
) : IrVisibility(
    stringRepresentation = stringRepresentation,
)

data class IrVisibilityProtected(
    override val stringRepresentation: List<IrStringRepresentation>,
) : IrVisibility(
    stringRepresentation = stringRepresentation,
)

data class IrVisibilityInternal(
    override val stringRepresentation: List<IrStringRepresentation>,
) : IrVisibility(
    stringRepresentation = stringRepresentation,
)

data class IrVisibilityPrivate(
    override val stringRepresentation: List<IrStringRepresentation>,
) : IrVisibility(
    stringRepresentation = stringRepresentation,
)
