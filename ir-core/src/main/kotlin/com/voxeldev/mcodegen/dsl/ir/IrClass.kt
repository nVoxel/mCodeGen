package com.voxeldev.mcodegen.dsl.ir

import com.voxeldev.mcodegen.dsl.ir.utils.MapStringAnySerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a class in the IR (Intermediate Representation) system.
 * This class contains all the necessary information to generate a class definition,
 * including its name, visibility, methods, and other properties.
 */
@Serializable
open class IrClass(
    val qualifiedName: String? = null,
    val simpleName: String,
    val kind: IrClassKind,
    val visibility: IrVisibility,
    val typeParameters: List<IrTypeParameter>,
    val superClasses: List<IrSuperClass>,
    val fields: List<IrField>,
    val methods: List<IrCallable>,
    val initializers: List<IrClassInitializer>,
    val nestedClasses: List<IrClass>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement {

    /**
     * @return Qualified class name or simple class name if qualified is null
     */
    open fun getQualifiedNameIfPresent(): String = qualifiedName ?: run {
        println("IrClass qualified name was null: $simpleName")
        simpleName
    }
}

interface IrClassKind {
    @Serializable
    data object IrClassClassKind : IrClassKind
    @Serializable
    data object IrInterfaceClassKind : IrClassKind
    @Serializable
    data object IrEnumClassKind : IrClassKind
    @Serializable
    data object IrAnnotationClassKind : IrClassKind
}

@Serializable
data class IrSuperClass(
    val superClassSimpleName: String,
    val superClassQualifiedName: String? = null,
    val kind: IrClassKind,
    val types: List<IrType>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement {

    /**
     * @return Qualified super class name or simple super class name if qualified is null
     */
    fun getQualifiedNameIfPresent(): String = superClassQualifiedName ?: run {
        println("IrSuperClass qualified name was null: $superClassSimpleName")
        superClassSimpleName
    }
}

@Serializable
data class IrField(
    val name: String,
    @SerialName("irType")
    val type: IrType,
    val visibility: IrVisibility,
    val isMutable: Boolean,
    val initializer: IrStatement? = null,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement

@Serializable
data class IrTypeParameter(
    val name: String,
    val extendsList: List<IrType>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement

@Serializable
data class IrClassInitializer(
    val kind: IrClassInitializerKind,
    val body: IrMethodBody? = null,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement {
    interface IrClassInitializerKind

    @Serializable
    data object IrInstanceClassInitializerKind : IrClassInitializerKind
    @Serializable
    data object IrStaticClassInitializerKind : IrClassInitializerKind
}
