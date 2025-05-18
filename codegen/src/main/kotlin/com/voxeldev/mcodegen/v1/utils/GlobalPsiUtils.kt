package com.voxeldev.mcodegen.v1.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.voxeldev.mcodegen.v1.GlobalConstants
import org.jetbrains.kotlin.com.intellij.psi.PsiArrayType
import org.jetbrains.kotlin.com.intellij.psi.PsiType
import org.jetbrains.kotlin.com.intellij.psi.impl.source.PsiClassReferenceType

object GlobalPsiUtils {

    fun createParameterType(type: PsiType): TypeName {
        return when (type) {
            is PsiArrayType -> {
                return createArrayParameterType(type).copy(nullable = true)
            }

            is PsiClassReferenceType -> if (type.presentableText == "String")
                ClassName("kotlin", type.presentableText).copy(nullable = true)
            else
                ClassName(GlobalConstants.TGDRIVE_PACKAGE, "Td${type.presentableText}").copy(nullable = true)

            else -> ClassName("kotlin", type.presentableText.capitalize())
        }
    }

    private fun createArrayParameterType(type: PsiArrayType): TypeName {
        val componentType = type.componentType

        return if (componentType is PsiArrayType) {
            ClassName("kotlin", "Array").parameterizedBy(
                createArrayParameterType(componentType)
            )
        } else {
            when (componentType.presentableText) {
                "byte", "long", "int" ->
                    ClassName("kotlin", "${componentType.presentableText.capitalize()}Array")

                "String" -> ClassName("kotlin", "Array").parameterizedBy(
                    ClassName(
                        "kotlin",
                        componentType.presentableText.capitalize()
                    )
                )

                else -> ClassName("kotlin", "Array").parameterizedBy(
                    ClassName(
                        GlobalConstants.TGDRIVE_PACKAGE,
                        "Td${componentType.presentableText.capitalize()}"
                    )
                )
            }
        }
    }
}