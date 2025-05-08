package com.voxeldev.mcodegen.dsl.source.edit.step

/**
 * Defines how the source code should be changed when the corresponding [EditStep] is applied to it.
 */
interface EditStepHandler {

    /**
     * The name of the corresponding [EditStep].
     */
    val handlingEditStepName: String

    /**
     * Defines [EditStep] processing logic.
     * @param sourcePath Path to the source code file.
     */
    fun handleEditStep(sourcePath: String)
}