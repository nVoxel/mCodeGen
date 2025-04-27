package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.builders.irArrayType
import com.voxeldev.mcodegen.dsl.ir.builders.irTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReference
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiArrayType
import org.jetbrains.kotlin.com.intellij.psi.PsiClassType
import org.jetbrains.kotlin.com.intellij.psi.PsiPrimitiveType
import org.jetbrains.kotlin.com.intellij.psi.PsiType

context(JavaModule, ScenarioScope)
internal fun convertType(psiType: PsiType?): IrType {
    if (psiType == null) {
        return irTypePrimitive("void").build()
    }

    return when (psiType) {
        is PsiPrimitiveType -> {
            irTypePrimitive(psiType.presentableText).apply {
                nullable(false)
            }.build()
        }

        is PsiClassType -> {
            val resolvedClass = psiType.resolve() ?: return getFallbackType(psiType)
            val resolvedClassName = resolvedClass.qualifiedName ?: return getFallbackType(psiType)

            if (resolvedTypes.contains(resolvedClassName)) {
                resolvedTypes[resolvedClassName]!!
            } else {
                irTypeReference(referencedClassName = resolvedClassName).apply {
                    nullable(true)
                    addLanguageProperty(PSI_CLASS, resolvedClass)
                }.build().also { irTypeReference ->
                    resolvedTypes[resolvedClassName] = irTypeReference
                }
            }
        }

        is PsiArrayType -> {
            irArrayType(convertType(psiType.componentType))
                .build()
        }

        else -> {
            getFallbackType(psiType)
        }
    }
}

private fun getFallbackType(psiType: PsiType): IrType {
    return irTypePrimitive(psiType.presentableText).apply {
        nullable(true)
    }.build()
}