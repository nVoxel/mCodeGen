package com.voxeldev.mcodegen.dsl.source.edit.step

/**
 * Defines how the source code should be changed. Implementation is delegated to the language module side.
 *
 * Usage example: make source code classes implement generated common interfaces.
 */
interface EditStep {

    /**
     * Name of this EditStep. Used by the LanguageModule to find the corresponding [EditStepHandler].
     * Must be unique in the current mCodeGen configuration.
     */
    val name: String
}