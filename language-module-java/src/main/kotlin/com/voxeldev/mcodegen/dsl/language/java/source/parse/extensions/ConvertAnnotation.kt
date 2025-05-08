package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrAnnotation
import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.builders.irAnnotation
import com.voxeldev.mcodegen.dsl.ir.builders.irAnnotationParameter
import com.voxeldev.mcodegen.dsl.ir.builders.irExpressionUnknown
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiAnnotation
import org.jetbrains.kotlin.com.intellij.psi.PsiAnnotationMemberValue
import org.jetbrains.kotlin.com.intellij.psi.PsiExpression

context(JavaModule, ScenarioScope)
internal fun convertAnnotation(psiAnnotation: PsiAnnotation): IrAnnotation {
    val irAnnotation = irAnnotation(psiAnnotation.qualifiedName ?: "Ir:UnnamedAnnotation")
    psiAnnotation.attributes.forEach { attribute ->
        val attributeName = attribute.attributeName
        val attributeValueRaw = psiAnnotation.findAttributeValue(attributeName)
            ?: throw IllegalArgumentException("Got an annotation attribute without value")
        val attributeValue = convertAnnotationMemberValue(attributeValueRaw)

        irAnnotation.addParameter(
            irAnnotationParameter = irAnnotationParameter(
                name = attributeName,
                value = attributeValue,
            )
        )
    }
    return irAnnotation.build()
}

context(JavaModule, ScenarioScope)
internal fun convertAnnotationMemberValue(psiAnnotationMemberValue: PsiAnnotationMemberValue): IrExpression {
    return when (psiAnnotationMemberValue) {
        is PsiExpression -> convertExpression(psiAnnotationMemberValue)
        else -> { // TODO: can be not only PsiExpression
            irExpressionUnknown().apply {
                addStringRepresentation(
                    IrStringRepresentation(languageName, psiAnnotationMemberValue.text)
                )
            }.build()
        }
    }
}