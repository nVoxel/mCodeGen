package com.voxeldev.mcodegen.dsl.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrAnnotation
import com.voxeldev.mcodegen.dsl.ir.IrElement
import com.voxeldev.mcodegen.dsl.ir.IrLocation

/**
 * Abstract builder class for creating [IrElement] instances.
 * Provides common functionality for all IR element builders.
 */
abstract class IrElementBuilder {
    protected var location: IrLocation? = null
    protected var annotations: MutableList<IrAnnotation> = mutableListOf()
    protected var languageProperties: MutableMap<String, Any> = mutableMapOf()

    fun location(location: IrLocation?) = apply { this.location = location }

    fun addAnnotation(annotation: IrAnnotation) = apply {
        annotations.add(annotation)
    }

    fun addLanguageProperty(key: String, value: Any) = apply {
        languageProperties[key] = value
    }
}