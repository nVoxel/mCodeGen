package com.voxeldev.mcodegen.dsl.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrAnnotation

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
    private var languageProperties: MutableMap<String, Any> = mutableMapOf()

    fun addProperty(key: String, value: Any) {
        languageProperties[key] = value
    }

    fun build(): IrAnnotation = IrAnnotation(
        name = name,
        languageProperties = languageProperties
    )
}