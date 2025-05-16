package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrAnnotation
import com.voxeldev.mcodegen.dsl.ir.builders.irAnnotation
import com.voxeldev.mcodegen.dsl.ir.builders.irAnnotationParameter
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.DefaultValueArgument
import org.jetbrains.kotlin.resolve.calls.model.ExpressionValueArgument

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertAnnotation(
    ktClassOrObject: KtClassOrObject?,
    ktAnnotationEntry: KtAnnotationEntry,
): IrAnnotation {
    val annotationDescriptor = this@BindingContext.get(BindingContext.ANNOTATION, ktAnnotationEntry)
        ?: throw IllegalArgumentException("Unable to get descriptor for annotation: ${ktAnnotationEntry.text}")
    val resolvedCall = ktAnnotationEntry.getResolvedCall(this@BindingContext)
        ?: throw IllegalArgumentException("Unable to get resolved call for annotation: ${ktAnnotationEntry.text}")
    val fqName = annotationDescriptor.fqName?.asString() ?: "Ir:UnnamedAnnotation"

    return irAnnotation(fqName).apply {
        resolvedCall.valueArguments.forEach { (valueParameterDescriptor, resolvedValueArgument) ->
            val parameterName = valueParameterDescriptor.name.asString()

            when(resolvedValueArgument) {
                // TODO: support varargs
                // is VarargValueArgument ->

                is ExpressionValueArgument -> {
                    resolvedValueArgument.valueArgument?.getArgumentExpression()?.let { argumentExpression ->
                        addParameter(
                            irAnnotationParameter(
                                name = parameterName,
                                value = convertExpression(ktClassOrObject, argumentExpression),
                            )
                        )
                    }
                }

                is DefaultValueArgument -> {
                    return@forEach
                }

                else -> throw IllegalArgumentException("This kind of annotation argument is not yet supported in Kotlin")
            }
        }
    }.build()
}