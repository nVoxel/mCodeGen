package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrMethod
import com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.builders.IrClassBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.IrConstructorBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irConstructor
import com.voxeldev.mcodegen.dsl.ir.builders.irMethod
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodBody
import com.voxeldev.mcodegen.dsl.ir.builders.irParameter
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.ir.packagePrivateVisibility
import com.voxeldev.mcodegen.dsl.language.java.ir.privateVisibility
import com.voxeldev.mcodegen.dsl.language.java.ir.protectedVisibility
import com.voxeldev.mcodegen.dsl.language.java.ir.publicVisibility
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiAnnotationMethod
import org.jetbrains.kotlin.com.intellij.psi.PsiClass
import org.jetbrains.kotlin.com.intellij.psi.PsiExpressionStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier

const val JAVA_METHOD_DEFAULT_VALUE = "javaMethodDefaultValue"

context(JavaModule, ScenarioScope)
internal fun convertMethods(psiClass: PsiClass, psiMethods: Array<PsiMethod>, irClassBuilder: IrClassBuilder) {
    psiMethods.forEach { psiMethod ->
        irClassBuilder.addMethod(convertMethod(psiClass, psiMethod))
    }
}

context(JavaModule, ScenarioScope)
internal fun convertMethod(psiClass: PsiClass, psiMethod: PsiMethod): IrMethod {
    val irMethodBuilder = if (psiMethod.isConstructor) {
        irConstructor(
            name = psiMethod.name,
            returnType = convertType(psiMethod.returnType),
        )
    } else {
        irMethod(
            name = psiMethod.name,
            returnType = convertType(psiMethod.returnType),
        )
    }

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
        irMethodBuilder.addAnnotation(convertAnnotation(annotation))
    }

    psiMethod.parameterList.parameters.forEach { parameter ->
        irMethodBuilder.addParameter(
            irParameter(
                name = parameter.name ?: "Ir:UnnamedParameter",
                type = convertType(parameter.type),
            ).build()
        )
    }

    psiMethod.typeParameters.forEach { typeParameter ->
        irMethodBuilder.addTypeParameter(convertTypeParameter(typeParameter))
    }

    if (psiMethod is PsiAnnotationMethod) {
        psiMethod.defaultValue?.let { defaultValue ->
            irMethodBuilder.addLanguageProperty(
                JAVA_METHOD_DEFAULT_VALUE,
                convertAnnotationMemberValue(defaultValue),
            )
        }
    }

    val body = psiMethod.body

    if (psiMethod.isConstructor && irMethodBuilder is IrConstructorBuilder && body != null) {
        val otherConstructorCall = body.statements
            .filterIsInstance<PsiExpressionStatement>()
            .map { statement -> statement.expression }
            .filterIsInstance<PsiMethodCallExpression>()
            .firstOrNull { expression ->
                val referenceName = expression.methodExpression.referenceName
                referenceName == "this" || referenceName == "super"
            }

        val otherConstructorCallExpression = otherConstructorCall?.let {
            convertExpression(otherConstructorCall) as? IrMethodCallExpression
        }

        otherConstructorCallExpression?.let {
            irMethodBuilder.otherConstructorCall(otherConstructorCallExpression)
        }
    }

    // Convert method body if present
    if (psiMethod.body != null) {
        val irMethodBodyBuilder = irMethodBody()

        psiMethod.body?.statements?.forEach { statement ->
            irMethodBodyBuilder.addStatement(
                statement = convertStatement(
                    psiStatement = statement,
                    // ignore constructor calls as we have processed them earlier
                    ignoreConstructorCalls = psiMethod.isConstructor,
                ),
            )
        }
        irMethodBuilder.body(irMethodBodyBuilder.build())
    }

    return irMethodBuilder.build()
}