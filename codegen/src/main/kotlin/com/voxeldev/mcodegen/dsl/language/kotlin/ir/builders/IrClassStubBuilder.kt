package com.voxeldev.mcodegen.dsl.language.kotlin.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassInitializer
import com.voxeldev.mcodegen.dsl.ir.IrClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrClassClassKind
import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.IrMethod
import com.voxeldev.mcodegen.dsl.ir.IrSuperClass
import com.voxeldev.mcodegen.dsl.ir.IrTypeParameter
import com.voxeldev.mcodegen.dsl.ir.IrVisibility
import com.voxeldev.mcodegen.dsl.ir.builders.IrElementBuilder
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IrClassStub

/**
 * Creates a new [IrClassStubBuilder] instance with the given name.
 * This is the entry point for building an [IrClassStub] using the DSL.
 */
fun irClassStub(name: String): IrClassStubBuilder = IrClassStubBuilder(name)

/**
 * Builder class for creating [IrClassStub] instances.
 * Provides a fluent API for constructing class definitions with default values.
 */
class IrClassStubBuilder internal constructor(private val name: String) : IrElementBuilder() {
    private var kind: IrClassKind = IrClassClassKind
    private var visibility: IrVisibility? = null
    private var typeParameters: MutableList<IrTypeParameter> = mutableListOf()
    private var superClasses: MutableList<IrSuperClass> = mutableListOf()
    private var fields: MutableList<IrField> = mutableListOf()
    private var methods: MutableList<IrMethod> = mutableListOf()
    private var initializers: MutableList<IrClassInitializer> = mutableListOf()
    private var nestedClasses: MutableList<IrClass> = mutableListOf()

    fun kind(kind: IrClassKind) {
        this.kind = kind
    }

    fun visibility(visibility: IrVisibility) {
        this.visibility = visibility
    }

    fun addTypeParameter(typeParameter: IrTypeParameter) {
        typeParameters.add(typeParameter)
    }

    fun addSuperClass(superClass: IrSuperClass) {
        superClasses.add(superClass)
    }

    fun addField(field: IrField) {
        fields.add(field)
    }

    fun addMethod(method: IrMethod) {
        methods.add(method)
    }

    fun addNestedClass(nestedClass: IrClass) {
        nestedClasses.add(nestedClass)
    }

    fun addInitializer(initializer: IrClassInitializer) {
        initializers.add(initializer)
    }

    fun build(): IrClassStub {
        return IrClassStub(
            name = name,
            kind = kind,
            visibility = requireNotNull(visibility),
            typeParameters = typeParameters,
            superClasses = superClasses,
            fields = fields,
            methods = methods,
            initializers = initializers,
            nestedClasses = nestedClasses,
            location = location,
            annotations = annotations,
            languageProperties = languageProperties
        )
    }
}