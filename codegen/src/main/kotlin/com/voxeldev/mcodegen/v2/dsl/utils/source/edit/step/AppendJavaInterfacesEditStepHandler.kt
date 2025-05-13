package com.voxeldev.mcodegen.v2.dsl.utils.source.edit.step

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.voxeldev.mcodegen.dsl.source.edit.step.EditStepHandlerBaseImpl

class AppendJavaInterfacesEditStepHandler : EditStepHandlerBaseImpl<AppendJavaInterfacesEditStep>() {

    override fun handleEditStep(
        editStep: AppendJavaInterfacesEditStep,
        sourceString: String,
    ): String {
        val compilationUnit = JavaParser().parse(sourceString).result.get()

        val classDeclarations = compilationUnit.findAll(ClassOrInterfaceDeclaration::class.java)
        classDeclarations
            .filter { declaration -> editStep.classToInterfacesMap.containsKey(declaration.nameAsString) }
            .forEach { declaration ->
                editStep.classToInterfacesMap[declaration.nameAsString]?.forEach { appendingInterface ->
                    declaration.addImplementedType(appendingInterface)
                }
            }

        return compilationUnit.toString()
    }
}