package com.voxeldev.mcodegen.dsl.ir

/**
 * Base interface for all IR (Intermediate Representation) elements in the code generation system.
 * This interface defines the common properties that all IR elements must have.
 */
sealed interface IrElement {
    val location: IrLocation?
    val annotations: List<IrAnnotation>
    val languageProperties: Map<String, Any>
}