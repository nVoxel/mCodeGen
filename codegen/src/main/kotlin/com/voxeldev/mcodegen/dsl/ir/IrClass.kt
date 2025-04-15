package com.voxeldev.mcodegen.dsl.ir

/**
 * Represents a class in the IR (Intermediate Representation) system.
 * This class contains all the necessary information to generate a class definition,
 * including its name, visibility, methods, and other properties.
 */
data class IrClass(
    val name: String,
    val kind: IrClassKind,
    val visibility: IrVisibility,
    val superClasses: List<IrClass>,
    val fields: List<IrField>,
    val methods: List<IrMethod>,
    val nestedClasses: List<IrClass>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement

enum class IrClassKind {
    CLASS, INTERFACE, ENUM, ANNOTATION
}

data class IrField(
    val name: String,
    val type: IrType,
    val visibility: IrVisibility,
    val isMutable: Boolean,
    val initializer: IrStatement?,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement
