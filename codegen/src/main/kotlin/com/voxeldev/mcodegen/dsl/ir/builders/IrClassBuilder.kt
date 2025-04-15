package com.voxeldev.mcodegen.dsl.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind
import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.IrMethod
import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrVisibility
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic

/**
 * Creates a new [IrClassBuilder] instance with the given name.
 * This is the entry point for building an [IrClass] using the DSL.
 */
fun irClass(name: String): IrClassBuilder = IrClassBuilder(name)

/**
 * Builder class for creating [IrClass] instances.
 * Provides a fluent API for constructing class definitions with default values.
 */
class IrClassBuilder internal constructor(private val name: String) : IrElementBuilder() {
    private var kind: IrClassKind = IrClassKind.CLASS
    private var visibility: IrVisibility = IrVisibilityPublic(IrStringRepresentation("kotlin", "public"))
    private var superClasses: MutableList<IrClass> = mutableListOf()
    private var fields: MutableList<IrField> = mutableListOf()
    private var methods: MutableList<IrMethod> = mutableListOf()
    private var nestedClasses: MutableList<IrClass> = mutableListOf()

    fun kind(kind: IrClassKind) = apply { this.kind = kind }
    fun visibility(visibility: IrVisibility) = apply { this.visibility = visibility }

    fun addSuperClass(superClass: IrClass) = apply {
        superClasses.add(superClass)
    }

    fun addField(field: IrField) = apply {
        fields.add(field)
    }

    fun addMethod(method: IrMethod) = apply {
        methods.add(method)
    }

    fun addNestedClass(nestedClass: IrClass) = apply {
        nestedClasses.add(nestedClass)
    }

    fun build(): IrClass {
        return IrClass(
            name = name,
            kind = kind,
            visibility = visibility,
            superClasses = superClasses,
            fields = fields,
            methods = methods,
            nestedClasses = nestedClasses,
            location = location,
            annotations = annotations,
            languageProperties = languageProperties
        )
    }
}

/**
 * Creates a new [IrFieldBuilder] instance with the given name and type.
 * This is the entry point for building an [IrField] using the DSL.
 */
fun irField(name: String, type: IrType): IrFieldBuilder = IrFieldBuilder(name, type)

/**
 * Builder class for creating [IrField] instances.
 * Provides a fluent API for constructing field definitions with default values.
 */
class IrFieldBuilder internal constructor(
    private val name: String,
    private val type: IrType
) : IrElementBuilder() {
    private var visibility: IrVisibility = IrVisibilityPublic(IrStringRepresentation("kotlin", "public"))
    private var isMutable: Boolean = false
    private var initializer: IrStatement? = null

    fun visibility(visibility: IrVisibility) = apply { this.visibility = visibility }
    fun mutable(isMutable: Boolean = true) = apply { this.isMutable = isMutable }
    fun initializer(initializer: IrStatement?) = apply { this.initializer = initializer }

    fun build(): IrField {
        return IrField(
            name = name,
            type = type,
            visibility = visibility,
            isMutable = isMutable,
            initializer = initializer,
            location = location,
            annotations = annotations,
            languageProperties = languageProperties
        )
    }
}