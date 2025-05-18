package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.builders.irAssignmentExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irBinaryExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irCastExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irEmptyExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irExpressionUnknown
import com.voxeldev.mcodegen.dsl.ir.builders.irIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irLiteralExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irObjectCreationExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irTernaryExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeCheckExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReference
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReferenceIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irUnaryExpression
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiAssignmentExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiBinaryExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiClass
import org.jetbrains.kotlin.com.intellij.psi.PsiConditionalExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiInstanceOfExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiLiteralExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiNewExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiPostfixExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiPrefixExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiReferenceExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiTypeCastExpression

context(JavaModule, ScenarioScope)
internal fun convertExpression(
    psiExpression: PsiExpression?,
    ignoreConstructorCalls: Boolean = false,
): IrExpression {
    if (psiExpression == null) {
        return irEmptyExpression().build()
    }

    return when (psiExpression) {
        is PsiLiteralExpression -> {
            irLiteralExpression(psiExpression.text).build()
        }

        is PsiReferenceExpression -> {
            // left part of expression
            val qualifier = psiExpression.qualifierExpression?.let { qualifier ->
                convertExpression(qualifier, ignoreConstructorCalls)
            }

            // right part of expression
            val selector = psiExpression.resolve()
                ?: throw IllegalArgumentException("PsiReferenceExpression cannot be resolved")

            // right part of expression as text
            val selectorText = psiExpression.referenceName
                ?: throw IllegalArgumentException("PsiReferenceExpression name cannot be resolved")

            if (selector is PsiClass) {
                irTypeReferenceIdentifierExpression(
                    referencedType = irTypeReference(
                        referencedClassName = selector.qualifiedName ?: "Ir:UnnamedClass"
                    ).build(),
                ).build()
            } else {
                irIdentifierExpression(
                    selector = irLiteralExpression(selectorText).build()
                ).apply {
                    qualifier?.let { qualifier(qualifier) }
                }.build()
            }
        }

        is PsiMethodCallExpression -> {
            val methodName = psiExpression.resolveMethod()?.name ?: "Ir:UnnamedMethod"

            val methodCallKind = when (psiExpression.methodExpression.referenceName) {
                "this" -> IrMethodCallExpression.IrThisMethodCallKind
                "super" -> IrMethodCallExpression.IrSuperMethodCallKind
                else -> IrMethodCallExpression.IrDefaultMethodCallKind
            }

            if (ignoreConstructorCalls && methodCallKind != IrMethodCallExpression.IrDefaultMethodCallKind) {
                irEmptyExpression().build() // ignoring constructor call
            } else {
                irMethodCallExpression(methodName = methodName).apply {
                    val receiverExpression = psiExpression.methodExpression.qualifierExpression
                    receiverExpression?.let {
                        receiver(convertExpression(receiverExpression, ignoreConstructorCalls))
                    }
                    psiExpression.argumentList.expressions.forEach { argumentExpression ->
                        addValueArgument(convertExpression(argumentExpression, ignoreConstructorCalls))
                    }
                    methodCallKind(
                        callKind = methodCallKind
                    )
                }.build()
            }
        }

        is PsiNewExpression -> {
            val className = psiExpression.classReference?.qualifiedName ?: "Ir:UnnamedClass"
            irObjectCreationExpression(className).apply {
                psiExpression.argumentList?.expressions?.forEach { argumentExpression ->
                    addConstructorArg(convertExpression(argumentExpression, ignoreConstructorCalls))
                }
            }.build()
        }

        is PsiBinaryExpression -> {
            irBinaryExpression(
                left = convertExpression(psiExpression.lOperand, ignoreConstructorCalls),
                operator = convertBinaryOperator(psiExpression.operationSign),
                right = convertExpression(psiExpression.rOperand, ignoreConstructorCalls),
            ).build()
        }

        is PsiPrefixExpression -> {
            irUnaryExpression(
                operator = convertUnaryOperator(operator = psiExpression.operationSign),
                operand = convertExpression(psiExpression.operand, ignoreConstructorCalls),
                isPrefix = true,
            ).build()
        }

        is PsiPostfixExpression -> {
            irUnaryExpression(
                operator = convertUnaryOperator(operator = psiExpression.operationSign),
                operand = convertExpression(psiExpression.operand, ignoreConstructorCalls),
                isPrefix = false,
            ).build()
        }

        is PsiAssignmentExpression -> {
            irAssignmentExpression(
                target = convertExpression(psiExpression.lExpression, ignoreConstructorCalls),
                operator = convertAssignmentOperator(operator = psiExpression.operationSign),
                value = convertExpression(psiExpression.rExpression, ignoreConstructorCalls)
            ).build()
        }

        is PsiConditionalExpression -> {
            irTernaryExpression(
                condition = convertExpression(psiExpression.condition, ignoreConstructorCalls),
                ifTrue = convertExpression(psiExpression.thenExpression, ignoreConstructorCalls),
                ifFalse = convertExpression(psiExpression.elseExpression, ignoreConstructorCalls),
            ).build()
        }

        is PsiTypeCastExpression -> {
            irCastExpression(
                expression = convertExpression(psiExpression.operand, ignoreConstructorCalls),
                targetType = convertType(psiExpression.castType?.type),
            ).build()
        }

        is PsiInstanceOfExpression -> {
            irTypeCheckExpression(
                expression = convertExpression(psiExpression.operand, ignoreConstructorCalls),
                checkType = convertType(psiExpression.checkType?.type)
            ).build()
        }

        else -> {
            irExpressionUnknown().apply {
                addStringRepresentation(
                    IrStringRepresentation(languageName, psiExpression.text)
                )
            }.build()
        }
    }
}