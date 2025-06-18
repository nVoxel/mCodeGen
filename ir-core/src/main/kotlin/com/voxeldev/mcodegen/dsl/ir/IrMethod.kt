package com.voxeldev.mcodegen.dsl.ir

import com.voxeldev.mcodegen.dsl.ir.utils.MapStringAnySerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Contains all [IrMethod] and [IrConstructor] shared properties.
 */
interface IrCallable : IrElement {
    val name: String
    val returnType: IrType
    val parameters: List<IrParameter>
    val typeParameters: List<IrTypeParameter>
    val body: IrMethodBody?
    val visibility: IrVisibility
    val isAbstract: Boolean
    val isStatic: Boolean
    val isOverride: Boolean
}

/**
 * Represents a method in the IR (Intermediate Representation) system.
 * This class contains all the necessary information to generate a method definition,
 * including its name, return type, parameters, visibility, and body statements.
 */
@Serializable
open class IrMethod(
    override val name: String,
    override val returnType: IrType,
    override val parameters: List<IrParameter>,
    override val typeParameters: List<IrTypeParameter>,
    override val body: IrMethodBody? = null,
    override val visibility: IrVisibility,
    override val isAbstract: Boolean,
    override val isStatic: Boolean,
    override val isOverride: Boolean,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrCallable

/**
 * Represents a class constructor in the IR (Intermediate Representation) system.
 * This class contains all the necessary information to generate a constructor,
 * including parameters, visibility, body statements and other constructor call.
 */
@Serializable
open class IrConstructor(
    val otherConstructorCall: IrMethodCallExpression? = null,
    override val name: String,
    override val returnType: IrType,
    override val parameters: List<IrParameter>,
    override val typeParameters: List<IrTypeParameter>,
    override val body: IrMethodBody? = null,
    override val visibility: IrVisibility,
    override val isAbstract: Boolean,
    override val isStatic: Boolean,
    override val isOverride: Boolean,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrCallable

/**
 * Represents a method parameter in the IR (Intermediate Representation) system.
 * This class contains information about a method parameter, including its name and type.
 */
@Serializable
data class IrParameter(
    val name: String,
    @SerialName("irType")
    val type: IrType,
    val defaultValue: IrExpression? = null,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement

@Serializable
data class IrMethodBody(
    val statements: List<IrStatement>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement
