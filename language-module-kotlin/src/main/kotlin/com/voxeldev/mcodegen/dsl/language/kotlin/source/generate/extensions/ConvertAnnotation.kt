package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.voxeldev.mcodegen.dsl.ir.IrAnnotation
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(KotlinModule, ScenarioScope)
internal fun convertAnnotation(
    irAnnotation: IrAnnotation,
): AnnotationSpec {
    return AnnotationSpec.builder(ClassName.bestGuess(irAnnotation.name)).apply {
        irAnnotation.parameters.forEach { irAnnotationParameter ->
            addMember(
                "%N = %L",
                irAnnotationParameter.parameterName,
                convertExpression(irAnnotationParameter.parameterValue),
            )
        }
    }.build()
}