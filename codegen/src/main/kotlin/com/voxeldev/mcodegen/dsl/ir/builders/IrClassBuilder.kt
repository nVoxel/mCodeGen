package com.voxeldev.mcodegen.dsl.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassInitializer
import com.voxeldev.mcodegen.dsl.ir.IrClassInitializer.IrClassInitializerKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind
import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.IrMethod
import com.voxeldev.mcodegen.dsl.ir.IrMethodBody
import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrTypeParameter
import com.voxeldev.mcodegen.dsl.ir.IrVisibility

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
    private var visibility: IrVisibility? = null
    private var typeParameters: MutableList<IrTypeParameter> = mutableListOf()
    private var superClasses: MutableList<IrClass> = mutableListOf()
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

    fun addSuperClass(superClass: IrClass) {
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

    fun build(): IrClass {
        return IrClass(
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
    private var visibility: IrVisibility? = null
    private var isMutable: Boolean = true
    private var initializer: IrStatement? = null

    fun visibility(visibility: IrVisibility) {
        this.visibility = visibility
    }

    fun mutable(isMutable: Boolean = true) {
        this.isMutable = isMutable
    }

    fun initializer(initializer: IrStatement?) {
        this.initializer = initializer
    }

    fun build(): IrField {
        return IrField(
            name = name,
            type = type,
            visibility = requireNotNull(visibility),
            isMutable = isMutable,
            initializer = initializer,
            location = location,
            annotations = annotations,
            languageProperties = languageProperties
        )
    }
}

fun irTypeParameter(name: String): IrTypeParameterBuilder = IrTypeParameterBuilder(name)

class IrTypeParameterBuilder internal constructor(
    private val name: String
) : IrElementBuilder() {
    private var extendsList: MutableList<IrType> = mutableListOf()

    fun addExtendsType(extendsType: IrType) {
        this.extendsList.add(extendsType)
    }

    fun build(): IrTypeParameter {
        return IrTypeParameter(
            name = name,
            extendsList = extendsList,
            location = location,
            annotations = annotations,
            languageProperties = languageProperties
        )
    }
}

fun irClassInitializer(kind: IrClassInitializerKind): IrClassInitializerBuilder = IrClassInitializerBuilder(kind)

class IrClassInitializerBuilder internal constructor(
    private val kind: IrClassInitializerKind,
) : IrElementBuilder() {
    private var initializerBody: IrMethodBody? = null

    fun body(body: IrMethodBody) {
        initializerBody = body
    }

    fun build(): IrClassInitializer {
        return IrClassInitializer(
            kind = kind,
            body = initializerBody,
            location = location,
            annotations = annotations,
            languageProperties = languageProperties
        )
    }
}