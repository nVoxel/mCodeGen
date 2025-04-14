package com.voxeldev.mcodegen.dsl.ir

sealed interface IrType : IrElement {
    val isNullable: Boolean
}

data class IrTypeReference(
    val referencedClass: IrClass,
    override val isNullable: Boolean = true,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrType

data class IrTypePrimitive(
    val name: String,
    override val isNullable: Boolean = true,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrType

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

