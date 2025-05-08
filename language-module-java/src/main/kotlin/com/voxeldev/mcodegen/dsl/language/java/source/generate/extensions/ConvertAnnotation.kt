package com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.voxeldev.mcodegen.dsl.ir.IrAnnotation
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(JavaModule, ScenarioScope)
internal fun convertAnnotation(
    containingClass: IrClass,
    irAnnotation: IrAnnotation,
): AnnotationSpec {
    return AnnotationSpec.builder(ClassName.bestGuess(irAnnotation.name)).apply {
        irAnnotation.parameters.forEach { irAnnotationParameter ->
            addMember(
                irAnnotationParameter.parameterName,
                convertExpression(containingClass, irAnnotationParameter.parameterValue),
            )
        }
    }.build()
}