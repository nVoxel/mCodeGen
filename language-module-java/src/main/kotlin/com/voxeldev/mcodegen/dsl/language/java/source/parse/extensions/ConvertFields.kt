package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.builders.IrClassBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irField
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.ir.javaPackagePrivateVisibility
import com.voxeldev.mcodegen.dsl.language.java.ir.javaPrivateVisibility
import com.voxeldev.mcodegen.dsl.language.java.ir.javaProtectedVisibility
import com.voxeldev.mcodegen.dsl.language.java.ir.javaPublicVisibility
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiClass
import org.jetbrains.kotlin.com.intellij.psi.PsiField
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier

context(JavaModule, ScenarioScope)
internal fun convertFields(psiClass: PsiClass, psiFields: Array<PsiField>, irClassBuilder: IrClassBuilder) {
    psiFields.forEach { psiField ->
        val irField = convertField(psiClass, psiField) ?: return@forEach
        irClassBuilder.addField(irField)
    }
}

context(JavaModule, ScenarioScope)
private fun convertField(psiClass: PsiClass, psiField: PsiField): IrField? {
    val name = psiField.name ?: return null

    val irFieldBuilder = irField(
        name = name,
        type = convertType(psiField.type),
    )

    irFieldBuilder.visibility(
        when {
            psiField.hasModifierProperty(PsiModifier.PUBLIC) -> javaPublicVisibility()
            psiField.hasModifierProperty(PsiModifier.PROTECTED) -> javaProtectedVisibility()
            psiField.hasModifierProperty(PsiModifier.PRIVATE) -> javaPrivateVisibility()
            else -> if (psiClass.isInterface) javaPublicVisibility() else javaPackagePrivateVisibility()
        }
    )

    if (psiField.hasModifierProperty(PsiModifier.STATIC)) {
        irFieldBuilder.addLanguageProperty(
            PsiModifier.STATIC, true
        )
    }

    irFieldBuilder.mutable(isMutable = !psiField.hasModifierProperty(PsiModifier.FINAL))

    psiField.annotations.forEach { annotation ->
        irFieldBuilder.addAnnotation(convertAnnotation(annotation))
    }

    if (psiField.hasInitializer()) {
        irFieldBuilder.initializer(
            initializer = irExpressionStatement(
                expression = convertExpression(psiField.initializer!!)
            ).build()
        )
    }

    return irFieldBuilder.build()
}