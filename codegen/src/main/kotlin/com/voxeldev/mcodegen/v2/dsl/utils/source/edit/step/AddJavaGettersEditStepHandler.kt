package com.voxeldev.mcodegen.v2.dsl.utils.source.edit.step

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.voxeldev.mcodegen.dsl.source.edit.step.EditStepHandlerBaseImpl

class AddJavaGettersEditStepHandler : EditStepHandlerBaseImpl<AddJavaGettersEditStep>() {

    override fun handleEditStep(
        editStep: AddJavaGettersEditStep,
        sourceString: String,
    ): String {
        val compilationUnit = JavaParser().parse(sourceString).result.get()

        val classDeclarations = compilationUnit.findAll(ClassOrInterfaceDeclaration::class.java)
        classDeclarations
            .filter { declaration -> editStep.classNamesToAddGetters.contains(declaration.nameAsString) }
            .forEach { declaration ->
                declaration.fields
                    .filter { declarationField ->
                        declarationField.variables.find { variable ->
                            variable.nameAsString in editStep.ignoredFieldNames
                        } == null
                    }
                    .forEach { declarationField ->
                        val getter = declarationField.createGetter()
                        if (editStep.fixGettersForKotlin) {
                            with(getter) {
                                addAnnotation("Override")
                                if (getter.typeAsString == "boolean" && nameAsString.startsWith("getIs")
                                    && nameAsString[5].isUpperCase()) {
                                    // removes get* prefix
                                    setName(nameAsString.drop(3).replaceFirstChar { it.lowercaseChar() })
                                }
                            }
                        }
                    }
            }

        return compilationUnit.toString()
    }
}