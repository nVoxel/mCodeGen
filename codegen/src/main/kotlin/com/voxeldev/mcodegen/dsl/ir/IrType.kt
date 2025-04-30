package com.voxeldev.mcodegen.dsl.ir

/**
 * Base class for all types in the IR (Intermediate Representation) system.
 * This class represents the type system used in the code generation process.
 */
interface IrType : IrElement {
    val isNullable: Boolean
}

data class IrTypeReference(
    val referencedClassName: String,
    val typeParameters: List<IrType>,
    override val isNullable: Boolean = true,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrType

data class IrTypePrimitive(
    val primitiveType: PrimitiveType,
    override val isNullable: Boolean = true,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrType {
    interface PrimitiveType {
        class Void : PrimitiveType
        class Boolean : PrimitiveType
        class Byte : PrimitiveType
        class Short : PrimitiveType
        class Int : PrimitiveType
        class Long : PrimitiveType
        class Char : PrimitiveType
        class Float : PrimitiveType
        class Double : PrimitiveType
    }
}

/**
 * Represents a function type in the IR (Intermediate Representation) system.
 * Function types represent the signature of a function, including its parameter types and return type.
 */
data class IrFunctionType(
    val parameterTypes: List<IrType>,
    val returnType: IrType,
    override val isNullable: Boolean = true,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrType

data class IrArrayType(
    val elementType: IrType,
    override val isNullable: Boolean = true,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrType

data class IrGeneric(
    val name: String,
    override val isNullable: Boolean = true,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrType

