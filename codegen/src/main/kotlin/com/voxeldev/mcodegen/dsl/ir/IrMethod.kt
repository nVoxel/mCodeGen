package com.voxeldev.mcodegen.dsl.ir

data class IrMethod(
    val name: String,
    val returnType: IrType,
    val parameters: List<IrParameter>,
    val body: IrMethodBody?,
    val visibility: IrVisibility,
    val isAbstract: Boolean,
    val isStatic: Boolean,
    val isOverride: Boolean,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrElement

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
