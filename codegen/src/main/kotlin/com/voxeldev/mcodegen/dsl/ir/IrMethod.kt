package com.voxeldev.mcodegen.dsl.ir

/**
 * Represents a method in the IR (Intermediate Representation) system.
 * This class contains all the necessary information to generate a method definition,
 * including its name, return type, parameters, visibility, and body statements.
 */
open class IrMethod(
    val name: String,
    val returnType: IrType,
    val parameters: List<IrParameter>,
    val typeParameters: List<IrTypeParameter>,
    val body: IrMethodBody?,
    val visibility: IrVisibility,
    val isAbstract: Boolean,
    val isStatic: Boolean,
    val isOverride: Boolean,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement

/**
 * Represents a class constructor in the IR (Intermediate Representation) system.
 * This class contains all the necessary information to generate a constructor,
 * including parameters, visibility, body statements and other constructor call.
 */
open class IrConstructor(
    val otherConstructorCall: IrMethodCallExpression?,
    name: String,
    returnType: IrType,
    parameters: List<IrParameter>,
    typeParameters: List<IrTypeParameter>,
    body: IrMethodBody?,
    visibility: IrVisibility,
    isAbstract: Boolean,
    isStatic: Boolean,
    isOverride: Boolean,
    location: IrLocation? = null,
    annotations: List<IrAnnotation> = emptyList(),
    languageProperties: Map<String, Any> = emptyMap()
) : IrMethod(
    name = name,
    returnType = returnType,
    parameters = parameters,
    typeParameters = typeParameters,
    body = body,
    visibility = visibility,
    isAbstract = isAbstract,
    isStatic = isStatic,
    isOverride = isOverride,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

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
