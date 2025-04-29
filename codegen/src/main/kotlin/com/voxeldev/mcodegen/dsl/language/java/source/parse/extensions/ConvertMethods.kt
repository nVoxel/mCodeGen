package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrMethod
import com.voxeldev.mcodegen.dsl.ir.builders.IrClassBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irAnnotation
import com.voxeldev.mcodegen.dsl.ir.builders.irMethod
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodBody
import com.voxeldev.mcodegen.dsl.ir.builders.irParameter
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.ir.packagePrivateVisibility
import com.voxeldev.mcodegen.dsl.language.java.ir.privateVisibility
import com.voxeldev.mcodegen.dsl.language.java.ir.protectedVisibility
import com.voxeldev.mcodegen.dsl.language.java.ir.publicVisibility
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiClass
import org.jetbrains.kotlin.com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier

context(JavaModule, ScenarioScope)
internal fun convertMethods(psiClass: PsiClass, psiMethods: Array<PsiMethod>, irClassBuilder: IrClassBuilder) {
    psiMethods.forEach { psiMethod ->
        irClassBuilder.addMethod(convertMethod(psiClass, psiMethod))
    }
}

context(JavaModule, ScenarioScope)
internal fun convertMethod(psiClass: PsiClass, psiMethod: PsiMethod): IrMethod {
    val irMethodBuilder = irMethod(
        name = psiMethod.name,
        returnType = convertType(psiMethod.returnType),
    )

    irMethodBuilder.isConstructor(isConstructor = psiMethod.isConstructor)

    irMethodBuilder.visibility(
        when {
            psiMethod.hasModifierProperty(PsiModifier.PUBLIC) -> publicVisibility()
            psiMethod.hasModifierProperty(PsiModifier.PROTECTED) -> protectedVisibility()
            psiMethod.hasModifierProperty(PsiModifier.PRIVATE) -> privateVisibility()
            else -> if (psiClass.isInterface) publicVisibility() else packagePrivateVisibility()
        }
    )

    irMethodBuilder.isAbstract(psiMethod.hasModifierProperty(PsiModifier.ABSTRACT))
    irMethodBuilder.isStatic(psiMethod.hasModifierProperty(PsiModifier.STATIC))
    irMethodBuilder.isOverride(psiMethod.hasAnnotation(Override::class.qualifiedName!!))

    if (psiMethod.hasModifierProperty(PsiModifier.NATIVE)) {
        irMethodBuilder.addLanguageProperty(
            PsiModifier.NATIVE, true
        )
    }

    psiMethod.annotations.forEach { annotation ->
        irMethodBuilder.addAnnotation(
            irAnnotation(annotation.qualifiedName ?: annotation.text).build()
        )
    }

    psiMethod.parameterList.parameters.forEach { parameter ->
        irMethodBuilder.addParameter(
            irParameter(
                name = parameter.name ?: "",
                type = convertType(parameter.type),
            ).build()
        )
    }

    psiMethod.typeParameters.forEach { typeParameter ->
        irMethodBuilder.addTypeParameter(convertTypeParameter(typeParameter))
    }

    // Convert method body if present
    if (psiMethod.body != null) {
        val irMethodBodyBuilder = irMethodBody()
        psiMethod.body?.statements?.forEach { statement ->
            irMethodBodyBuilder.addStatement(
                statement = convertStatement(psiStatement = statement),
            )
        }
        irMethodBuilder.body(irMethodBodyBuilder.build())
    }

    return irMethodBuilder.build()
}