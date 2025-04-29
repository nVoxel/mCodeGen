package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.builders.irAssignmentExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irBinaryExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irCastExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irLiteralExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irObjectCreationExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irTernaryExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeCheckExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irUnaryExpression
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.ir.builders.irExpressionUnknown
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiAssignmentExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiBinaryExpression
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
internal fun convertExpression(psiExpression: PsiExpression?): IrExpression {
    if (psiExpression == null) {
        return irLiteralExpression("").build()
    }

    return when (psiExpression) {
        is PsiLiteralExpression -> {
            irLiteralExpression(psiExpression.text).build()
        }

        is PsiReferenceExpression -> {
            irIdentifierExpression(psiExpression.text).build()
        }

        is PsiMethodCallExpression -> {
            val methodName = psiExpression.resolveMethod()?.name ?: "Ir:UnnamedMethod"
            irMethodCallExpression(methodName = methodName).apply {
                val receiverExpression = psiExpression.methodExpression.qualifierExpression
                receiverExpression?.let {
                    receiver(convertExpression(receiverExpression))
                }
                psiExpression.argumentList.expressions.forEach { argumentExpression ->
                    addArgument(convertExpression(argumentExpression))
                }
            }.build()
        }

        is PsiNewExpression -> {
            val className = psiExpression.classReference?.qualifiedName ?: "Ir:UnnamedClass"
            irObjectCreationExpression(className).apply {
                psiExpression.argumentList?.expressions?.forEach { argumentExpression ->
                    addConstructorArg(convertExpression(argumentExpression))
                }
            }.build()
        }

        is PsiBinaryExpression -> {
            irBinaryExpression(
                left = convertExpression(psiExpression.lOperand),
                operator = convertBinaryOperator(psiExpression.operationSign),
                right = convertExpression(psiExpression.rOperand),
            ).build()
        }

        is PsiPrefixExpression -> {
            irUnaryExpression(
                operator = convertUnaryOperator(operator = psiExpression.operationSign),
                operand = convertExpression(psiExpression.operand),
                isPrefix = true,
            ).build()
        }

        is PsiPostfixExpression -> {
            irUnaryExpression(
                operator = convertUnaryOperator(operator = psiExpression.operationSign),
                operand = convertExpression(psiExpression.operand),
                isPrefix = false,
            ).build()
        }

        is PsiAssignmentExpression -> {
            irAssignmentExpression(
                target = convertExpression(psiExpression.lExpression),
                operator = convertAssignmentOperator(operator = psiExpression.operationSign),
                value = convertExpression(psiExpression.rExpression)
            ).build()
        }

        is PsiConditionalExpression -> {
            irTernaryExpression(
                condition = convertExpression(psiExpression.condition),
                ifTrue = convertExpression(psiExpression.thenExpression),
                ifFalse = convertExpression(psiExpression.elseExpression),
            ).build()
        }

        is PsiTypeCastExpression -> {
            irCastExpression(
                expression = convertExpression(psiExpression.operand),
                targetType = convertType(psiExpression.castType?.type),
            ).build()
        }

        is PsiInstanceOfExpression -> {
            irTypeCheckExpression(
                expression = convertExpression(psiExpression.operand),
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