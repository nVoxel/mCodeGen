package com.voxeldev.mcodegen.dsl.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrAnnotation
import com.voxeldev.mcodegen.dsl.ir.IrAnnotationParameter
import com.voxeldev.mcodegen.dsl.ir.IrExpression

/**
 * Creates a new [IrAnnotationBuilder] instance with the given name.
 * This is the entry point for building an [IrAnnotation] using the DSL.
 */
fun irAnnotation(name: String): IrAnnotationBuilder = IrAnnotationBuilder(name)

/**
 * Builder class for creating [IrAnnotation] instances.
 * Provides a fluent API for constructing annotation definitions with default values.
 */
class IrAnnotationBuilder internal constructor(private val name: String) {
    private var parameters: MutableList<IrAnnotationParameter> = mutableListOf()
    private var languageProperties: MutableMap<String, Any> = mutableMapOf()

    fun addParameter(irAnnotationParameter: IrAnnotationParameter) {
        parameters.add(irAnnotationParameter)
    }

    fun addProperty(key: String, value: Any) {
        languageProperties[key] = value
    }

    fun build(): IrAnnotation = IrAnnotation(
        name = name,
        parameters = parameters,
        languageProperties = languageProperties
    )
}

fun irAnnotationParameter(name: String, value: IrExpression): IrAnnotationParameter = IrAnnotationParameter(
    parameterName = name,
    parameterValue = value,
)