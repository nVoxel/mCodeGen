package com.voxeldev.mcodegen.dsl.ir

/**
 * Represents a method in the IR (Intermediate Representation) system.
 * This class contains all the necessary information to generate a method definition,
 * including its name, return type, parameters, visibility, and body statements.
 */
data class IrMethod(
    val name: String,
    val returnType: IrType,
    val parameters: List<IrParameter>,
    val body: IrMethodBody?,
    val visibility: IrVisibility,
    val isAbstract: Boolean,
    val isStatic: Boolean,
    val isOverride: Boolean,
    val isConstructor: Boolean,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement

/**
 * Represents a method parameter in the IR (Intermediate Representation) system.
 * This class contains information about a method parameter, including its name and type.
 */
data class IrParameter(
    val name: String,
    val type: IrType,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement

data class IrMethodBody(
    val statements: List<IrStatement>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement
