package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.joinToCode
import com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression
import com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression
import com.voxeldev.mcodegen.dsl.ir.IrCastExpression
import com.voxeldev.mcodegen.dsl.ir.IrEmptyExpression
import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrExpressionUnknown
import com.voxeldev.mcodegen.dsl.ir.IrIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.IrLiteralExpression
import com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.IrObjectCreationExpression
import com.voxeldev.mcodegen.dsl.ir.IrTernaryExpression
import com.voxeldev.mcodegen.dsl.ir.IrTypeCheckExpression
import com.voxeldev.mcodegen.dsl.ir.IrTypeReferenceIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IrLambdaExpression
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IrParenthesizedExpression
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_CLASS_MEMBER_REFERENCE
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_TOP_LEVEL_MEMBER_REFERENCE
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

private val lambdaIgnoredParameterNames = setOf(
    "it",
    "\$receiver",
)

context(KotlinModule, ScenarioScope)
internal fun convertExpression(irExpression: IrExpression): CodeBlock {
    val poetCodeBlock = CodeBlock.builder()

    when (irExpression) {
        is IrLiteralExpression -> {
            poetCodeBlock.add("%L", irExpression.value)
        }

        is IrIdentifierExpression -> {
            irExpression.qualifier?.let { qualifier ->
                poetCodeBlock.add(
                    "%L.",
                    convertExpression(qualifier)
                )
            }

            val expressionText = irExpression.selector as? IrLiteralExpression
            val classMemberReference = irExpression.languageProperties[KT_CLASS_MEMBER_REFERENCE] as? String
            val topLevelMemberReference = irExpression.languageProperties[KT_TOP_LEVEL_MEMBER_REFERENCE] as? String

            if (expressionText != null && classMemberReference != null) {
                poetCodeBlock.add(
                    "%M",
                    MemberName(ClassName.bestGuess(classMemberReference), expressionText.value)
                )
            } else if (expressionText != null && topLevelMemberReference != null) {
                poetCodeBlock.add(
                    "%M",
                    MemberName(topLevelMemberReference, expressionText.value)
                )
            } else {
                poetCodeBlock.add(
                    "%L",
                    convertExpression(irExpression.selector)
                )
            }
        }

        is IrTypeReferenceIdentifierExpression -> {
            poetCodeBlock.add(
                "%T",
                convertType(irExpression.referencedType)
            )
        }

        is IrMethodCallExpression -> {
            poetCodeBlock.apply {
                irExpression.receiver?.let { receiver ->
                    add(
                        "%L.",
                        convertExpression(receiver)
                    )
                }

                when (irExpression.irMethodCallKind) {
                    is IrMethodCallExpression.IrThisMethodCallKind -> {
                        add("this(")
                    }

                    is IrMethodCallExpression.IrSuperMethodCallKind -> {
                        add("super(")
                    }

                    else -> {
                        add("%N(", irExpression.methodName)
                    }
                }

                val arguments = irExpression.arguments.map { argument -> convertExpression(argument) }
                add(arguments.joinToCode(separator = ", "))

                add(")")
            }
        }

        is IrObjectCreationExpression -> {
            val className = ClassName.bestGuess(irExpression.className)

            poetCodeBlock.apply {
                add("%T(", className)

                val arguments = irExpression.constructorArgs.map { argument -> convertExpression(argument) }
                add(arguments.joinToCode(separator = ", "))

                add(")")
            }
        }

        is IrBinaryExpression -> {
            poetCodeBlock.add(
                "%L %L %L",
                convertExpression(irExpression.left),
                convertBinaryOperator(irExpression.operator),
                convertExpression(irExpression.right),
            )
        }

        is IrUnaryExpression -> {
            poetCodeBlock.apply {
                if (irExpression.isPrefix) {
                    add(
                        "%L%L",
                        convertUnaryOperator(irExpression.operator),
                        convertExpression(irExpression.operand),
                    )
                } else {
                    add(
                        "%L%L",
                        convertExpression(irExpression.operand),
                        convertUnaryOperator(irExpression.operator),
                    )
                }
            }
        }

        is IrAssignmentExpression -> {
            poetCodeBlock.add(
                "%L %L %L",
                convertExpression(irExpression.target),
                convertAssignmentOperator(irExpression.operator),
                convertExpression(irExpression.value),
            )
        }

        is IrTernaryExpression -> {
            poetCodeBlock.apply {
                add(
                    "if (%L) %L else %L",
                    convertExpression(irExpression.condition),
                    convertExpression(irExpression.ifTrue),
                    convertExpression(irExpression.ifFalse),
                )
            }
        }

        is IrCastExpression -> {
            poetCodeBlock.add(
                "%L as %T",
                convertExpression(irExpression.expression),
                convertType(irExpression.targetType),
            )
        }

        is IrTypeCheckExpression -> {
            poetCodeBlock.add(
                "%L is %T",
                convertExpression(irExpression.expression),
                convertType(irExpression.checkType),
            )
        }

        is IrLambdaExpression -> {
            poetCodeBlock.apply {
                add("{")

                val parameterNames = irExpression.parameters
                    .filter { parameter -> parameter.name !in lambdaIgnoredParameterNames }
                if (parameterNames.isNotEmpty()) {
                    add(
                        " %L ->",
                        parameterNames.joinToString(", ") { parameter -> parameter.name },
                    )
                }

                add("\n")

                irExpression.body.statements.forEach { bodyStatement ->
                    add("%L", convertStatement(bodyStatement))
                }

                add("}")
            }
        }

        is IrParenthesizedExpression -> {
            poetCodeBlock.add(
                "(%L)",
                convertStatement(irExpression.body, addLineBreak = false),
            )
        }

        is IrEmptyExpression -> {
            // no-op
        }

        else -> {
            if (irExpression is IrExpressionUnknown) {
                val representation = irExpression.stringRepresentation.firstOrNull { it.language == languageName }
                representation?.let {
                    return poetCodeBlock.add(representation.representation).build()
                }
            }

            poetCodeBlock.add("Unsupported") // TODO: Remove debug marker
        }
    }

    return poetCodeBlock.build()
}
