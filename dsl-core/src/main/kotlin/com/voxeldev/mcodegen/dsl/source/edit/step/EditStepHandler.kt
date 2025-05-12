package com.voxeldev.mcodegen.dsl.source.edit.step

/**
 * Defines how the source code should be changed when the corresponding [EditStep] is applied to it.
 */
interface EditStepHandler<T : EditStep> : AnyEditStepHandler {

    /**
     * The name of the corresponding [EditStep].
     */
    override val handlingEditStepName: String

    /**
     * Defines [EditStep] processing logic.
     * @param editStep The corresponding [EditStep].
     * @param sourceString Source code of a file.
     * @return Modifier source code of a file.
     */
    fun handleEditStep(editStep: T, sourceString: String): String

    @Suppress("UNCHECKED_CAST")
    override fun handleAnyStep(step: EditStep, source: String): String =
        handleEditStep(step as T, source)
}

interface AnyEditStepHandler {
    val handlingEditStepName: String
    fun handleAnyStep(step: EditStep, source: String): String
}