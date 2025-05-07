package com.voxeldev.mcodegen.dsl.ir

/**
 * Represents a class in the IR (Intermediate Representation) system.
 * This class contains all the necessary information to generate a class definition,
 * including its name, visibility, methods, and other properties.
 */
open class IrClass(
    val name: String,
    val kind: IrClassKind,
    val visibility: IrVisibility,
    val typeParameters: List<IrTypeParameter>,
    val superClasses: List<IrSuperClass>,
    val fields: List<IrField>,
    val methods: List<IrMethod>,
    val initializers: List<IrClassInitializer>,
    val nestedClasses: List<IrClass>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement

interface IrClassKind {
    data object IrClassClassKind : IrClassKind
    data object IrInterfaceClassKind : IrClassKind
    data object IrEnumClassKind : IrClassKind
    data object IrAnnotationClassKind : IrClassKind
}

data class IrSuperClass(
    val superClassName: String,
    val kind: IrClassKind,
    val types: List<IrType>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement

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

data class IrTypeParameter(
    val name: String,
    val extendsList: List<IrType>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement

data class IrClassInitializer(
    val kind: IrClassInitializerKind,
    val body: IrMethodBody?,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement {
    interface IrClassInitializerKind

    data object IrInstanceClassInitializerKind : IrClassInitializerKind
    data object IrStaticClassInitializerKind : IrClassInitializerKind
}
