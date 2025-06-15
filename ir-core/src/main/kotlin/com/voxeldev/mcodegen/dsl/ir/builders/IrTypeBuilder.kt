package com.voxeldev.mcodegen.dsl.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrAnnotation
import com.voxeldev.mcodegen.dsl.ir.IrTypeArray
import com.voxeldev.mcodegen.dsl.ir.IrTypeFunction
import com.voxeldev.mcodegen.dsl.ir.IrTypeGeneric
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
fun irTypeReference(
    referencedClassSimpleName: String,
    referencedClassQualifiedName: String? = null,
): IrTypeReferenceBuilder =
    IrTypeReferenceBuilder(referencedClassSimpleName, referencedClassQualifiedName)

/**
 * Builder class for creating [IrTypeReference] instances.
 */
class IrTypeReferenceBuilder(
    private val referencedClassSimpleName: String,
    private var referencedClassQualifiedName: String?,
) : IrTypeBuilder() {

    private val typeParameters: MutableList<IrType> = mutableListOf()

    fun referencedClassQualifiedName(referencedClassQualifiedName: String?) {
        this.referencedClassQualifiedName = referencedClassQualifiedName
    }

    fun addTypeParameter(type: IrType) {
        typeParameters.add(type)
    }

    fun build(): IrTypeReference {
        val properties = buildTypeProperties()
        return IrTypeReference(
            referencedClassSimpleName = referencedClassSimpleName,
            referencedClassQualifiedName = referencedClassQualifiedName,
            typeParameters = typeParameters,
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
fun irTypePrimitive(primitiveType: IrTypePrimitive.PrimitiveType): IrTypePrimitiveBuilder =
    IrTypePrimitiveBuilder(primitiveType)

/**
 * Builder class for creating [IrTypePrimitive] instances.
 */
class IrTypePrimitiveBuilder(private val primitiveType: IrTypePrimitive.PrimitiveType) : IrTypeBuilder() {
    fun build(): IrTypePrimitive {
        val properties = buildTypeProperties()
        return IrTypePrimitive(
            primitiveType = primitiveType,
            isNullable = properties.isNullable,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrTypeFunctionBuilder] instance with the given parameter types and return type.
 */
fun irTypeFunction(
    parameterTypes: List<IrType>,
    returnType: IrType,
): IrTypeFunctionBuilder = IrTypeFunctionBuilder(
    parameterTypes = parameterTypes,
    returnType = returnType,
)

/**
 * Builder class for creating [IrTypeFunction] instances.
 */
class IrTypeFunctionBuilder(
    private val parameterTypes: List<IrType>,
    private val returnType: IrType,
) : IrTypeBuilder() {
    fun build(): IrTypeFunction {
        val properties = buildTypeProperties()
        return IrTypeFunction(
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
 * Creates a new [IrTypeArrayBuilder] instance with the given element type.
 */
fun irTypeArray(elementType: IrType): IrTypeArrayBuilder =
    IrTypeArrayBuilder(elementType)

/**
 * Builder class for creating [IrTypeArray] instances.
 */
class IrTypeArrayBuilder(private val elementType: IrType) : IrTypeBuilder() {
    fun build(): IrTypeArray {
        val properties = buildTypeProperties()
        return IrTypeArray(
            elementType = elementType,
            isNullable = properties.isNullable,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrTypeGenericBuilder] instance with the given name.
 */
fun irTypeGeneric(name: String): IrTypeGenericBuilder =
    IrTypeGenericBuilder(name)

/**
 * Builder class for creating [IrTypeGeneric] instances.
 */
class IrTypeGenericBuilder(private val name: String) : IrTypeBuilder() {
    fun build(): IrTypeGeneric {
        val properties = buildTypeProperties()
        return IrTypeGeneric(
            name = name,
            isNullable = properties.isNullable,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
} 