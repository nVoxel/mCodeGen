package com.voxeldev.mcodegen.dsl.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrAnnotation
import com.voxeldev.mcodegen.dsl.ir.IrArrayType
import com.voxeldev.mcodegen.dsl.ir.IrFunctionType
import com.voxeldev.mcodegen.dsl.ir.IrGeneric
import com.voxeldev.mcodegen.dsl.ir.IrLocation
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference

/**
 * Abstract builder class for creating [IrType] instances.
 * Provides common functionality for all type builders.
 */
abstract class IrTypeBuilder : IrElementBuilder() {
    protected var isNullable: Boolean = true

    fun nullable(isNullable: Boolean) {
        this.isNullable = isNullable
    }

    protected fun buildTypeProperties(): IrTypeProperties {
        return IrTypeProperties(
            isNullable = isNullable,
            location = location,
            annotations = annotations,
            languageProperties = languageProperties,
        )
    }

    protected data class IrTypeProperties(
        val isNullable: Boolean,
        val location: IrLocation?,
        val annotations: List<IrAnnotation>,
        val languageProperties: Map<String, Any>,
    )
}

/**
 * Creates a new [IrTypeReferenceBuilder] instance with the given referenced class.
 */
fun irTypeReference(referencedClassName: String): IrTypeReferenceBuilder =
    IrTypeReferenceBuilder(referencedClassName)

/**
 * Builder class for creating [IrTypeReference] instances.
 */
class IrTypeReferenceBuilder(private val referencedClassName: String) : IrTypeBuilder() {
    fun build(): IrTypeReference {
        val properties = buildTypeProperties()
        return IrTypeReference(
            referencedClassName = referencedClassName,
            isNullable = properties.isNullable,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrTypePrimitiveBuilder] instance with the given name.
 */
fun irTypePrimitive(name: String): IrTypePrimitiveBuilder =
    IrTypePrimitiveBuilder(name)

/**
 * Builder class for creating [IrTypePrimitive] instances.
 */
class IrTypePrimitiveBuilder(private val name: String) : IrTypeBuilder() {
    fun build(): IrTypePrimitive {
        val properties = buildTypeProperties()
        return IrTypePrimitive(
            name = name,
            isNullable = properties.isNullable,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrFunctionTypeBuilder] instance with the given parameter types and return type.
 */
fun irFunctionType(
    parameterTypes: List<IrType>,
    returnType: IrType,
): IrFunctionTypeBuilder = IrFunctionTypeBuilder(
    parameterTypes = parameterTypes,
    returnType = returnType,
)

/**
 * Builder class for creating [IrFunctionType] instances.
 */
class IrFunctionTypeBuilder(
    private val parameterTypes: List<IrType>,
    private val returnType: IrType,
) : IrTypeBuilder() {
    fun build(): IrFunctionType {
        val properties = buildTypeProperties()
        return IrFunctionType(
            parameterTypes = parameterTypes,
            returnType = returnType,
            isNullable = properties.isNullable,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrArrayTypeBuilder] instance with the given element type.
 */
fun irArrayType(elementType: IrType): IrArrayTypeBuilder =
    IrArrayTypeBuilder(elementType)

/**
 * Builder class for creating [IrArrayType] instances.
 */
class IrArrayTypeBuilder(private val elementType: IrType) : IrTypeBuilder() {
    fun build(): IrArrayType {
        val properties = buildTypeProperties()
        return IrArrayType(
            elementType = elementType,
            isNullable = properties.isNullable,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrGenericBuilder] instance with the given name.
 */
fun irGeneric(name: String): IrGenericBuilder =
    IrGenericBuilder(name)

/**
 * Builder class for creating [IrGeneric] instances.
 */
class IrGenericBuilder(private val name: String) : IrTypeBuilder() {
    fun build(): IrGeneric {
        val properties = buildTypeProperties()
        return IrGeneric(
            name = name,
            isNullable = properties.isNullable,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
} 