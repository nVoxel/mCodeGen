package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.builders.irArrayType
import com.voxeldev.mcodegen.dsl.ir.builders.irGeneric
import com.voxeldev.mcodegen.dsl.ir.builders.irTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReference
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiArrayType
import org.jetbrains.kotlin.com.intellij.psi.PsiClassType
import org.jetbrains.kotlin.com.intellij.psi.PsiPrimitiveType
import org.jetbrains.kotlin.com.intellij.psi.PsiType
import org.jetbrains.kotlin.com.intellij.psi.PsiTypeParameter

context(JavaModule, ScenarioScope)
internal fun convertType(psiType: PsiType?): IrType {
    if (psiType == null) {
        return irTypePrimitive(IrTypePrimitive.PrimitiveType.Void()).build()
    }

    return when (psiType) {
        is PsiPrimitiveType -> {
            val primitiveType = when (psiType.presentableText) {
                "void" -> IrTypePrimitive.PrimitiveType.Void()
                "boolean" -> IrTypePrimitive.PrimitiveType.Boolean()
                "byte" -> IrTypePrimitive.PrimitiveType.Byte()
                "short" -> IrTypePrimitive.PrimitiveType.Short()
                "int" -> IrTypePrimitive.PrimitiveType.Int()
                "long" -> IrTypePrimitive.PrimitiveType.Long()
                "char" -> IrTypePrimitive.PrimitiveType.Char()
                "float" -> IrTypePrimitive.PrimitiveType.Float()
                "double" -> IrTypePrimitive.PrimitiveType.Double()
                else -> throw IllegalArgumentException("Unsupported Java primitive type")
            }

            irTypePrimitive(primitiveType).apply {
                nullable(false)
            }.build()
        }

        is PsiClassType -> {
            val resolvedClass = psiType.resolve() ?: return getFallbackType(psiType)
            return if (resolvedClass is PsiTypeParameter) {
                val resolvedParameterName = resolvedClass.name ?: return getFallbackType(psiType)

                irGeneric(name = resolvedParameterName).build()
            } else {
                val resolvedClassName = resolvedClass.qualifiedName ?: return getFallbackType(psiType)

                irTypeReference(referencedClassName = resolvedClassName).apply {
                    nullable(true)
                    addLanguageProperty(PSI_CLASS, resolvedClass)
                    psiType.typeArguments()
                        .mapNotNull { it as? PsiType }
                        .forEach { typeArgument ->
                            addTypeParameter(type = convertType(typeArgument))
                        }
                }.build()
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
    return irTypePrimitive(fallbackPrimitiveType(psiType.presentableText)).apply {
        nullable(true)
    }.build()
}

private fun fallbackPrimitiveType(name: String) = FallbackPrimitiveType(name)

class FallbackPrimitiveType internal constructor(
    val name: String,
) : IrTypePrimitive.PrimitiveType
