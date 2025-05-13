package com.voxeldev.mcodegen.v2.dsl.utils.source.edit.step

import com.github.javaparser.JavaParser
import com.voxeldev.mcodegen.dsl.source.edit.step.EditStepHandlerBaseImpl

class AddJavaImportEditStepHandler : EditStepHandlerBaseImpl<AddJavaImportEditStep>() {

    override fun handleEditStep(
        editStep: AddJavaImportEditStep,
        sourceString: String,
    ): String {
        val compilationUnit = JavaParser().parse(sourceString).result.get()

        compilationUnit.addImport(editStep.import)

        return compilationUnit.toString()
    }
}