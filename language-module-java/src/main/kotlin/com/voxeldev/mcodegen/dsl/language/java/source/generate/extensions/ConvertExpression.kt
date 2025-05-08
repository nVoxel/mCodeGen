package com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression
import com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression
import com.voxeldev.mcodegen.dsl.ir.IrCastExpression
import com.voxeldev.mcodegen.dsl.ir.IrClass
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
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(JavaModule, ScenarioScope)
internal fun convertExpression(
    containingClass: IrClass,
    irExpression: IrExpression,
): CodeBlock {
    val poetCodeBlock = CodeBlock.builder()

    when (irExpression) {
        is IrLiteralExpression -> {
            poetCodeBlock.add("\$L", irExpression.value)
        }

        is IrIdentifierExpression -> {
            irExpression.qualifier?.let { qualifier ->
                poetCodeBlock.add(
                    "\$L.",
                    convertExpression(containingClass, qualifier)
                )
            }
            poetCodeBlock.add(
                "\$L",
                convertExpression(containingClass, irExpression.selector)
            )
        }

        is IrTypeReferenceIdentifierExpression -> {
            poetCodeBlock.add(
                "\$T",
                convertType(irExpression.referencedType)
            )
        }

        is IrMethodCallExpression -> {
            poetCodeBlock.apply {
                irExpression.receiver?.let { receiver ->
                    add("\$L.", convertExpression(containingClass, receiver))
                }

                when (irExpression.irMethodCallKind) {
                    is IrMethodCallExpression.IrThisMethodCallKind -> {
                        add("this(")
                    }

                    is IrMethodCallExpression.IrSuperMethodCallKind -> {
                        add("super(")
                    }

                    else -> {
                        add("\$N(", irExpression.methodName)
                    }
                }

                add(
                    CodeBlock.join(
                        irExpression.arguments.map { argument -> convertExpression(containingClass, argument) },
                        ", "
                    )
                )

                add(")")
            }
        }

        is IrObjectCreationExpression -> {
            val className = ClassName.bestGuess(irExpression.className)

            poetCodeBlock.apply {
                add("new \$T(", className)
                add(
                    CodeBlock.join(
                        irExpression.constructorArgs.map { argument -> convertExpression(containingClass, argument) },
                        ", "
                    )
                )
                add(")")
            }
        }

        is IrBinaryExpression -> {
            poetCodeBlock.apply {
                add(
                    "\$L \$L \$L",
                    convertExpression(containingClass, irExpression.left),
                    convertBinaryOperator(irExpression.operator),
                    convertExpression(containingClass, irExpression.right),
                )
            }
        }

        is IrUnaryExpression -> {
            poetCodeBlock.apply {
                if (irExpression.isPrefix) {
                    add(
                        "\$L\$L",
                        convertUnaryOperator(irExpression.operator),
                        convertExpression(containingClass, irExpression.operand),
                    )
                } else {
                    add(
                        "\$L\$L",
                        convertExpression(containingClass, irExpression.operand),
                        convertUnaryOperator(irExpression.operator),
                    )
                }
            }
        }

        is IrAssignmentExpression -> {
            poetCodeBlock.apply {
                add(
                    "\$L \$L \$L",
                    convertExpression(containingClass, irExpression.target),
                    convertAssignmentOperator(irExpression.operator),
                    convertExpression(containingClass, irExpression.value),
                )
            }
        }

        is IrTernaryExpression -> {
            poetCodeBlock.apply {
                add(
                    "\$L ? \$L : \$L",
                    convertExpression(containingClass, irExpression.condition),
                    convertExpression(containingClass, irExpression.ifTrue),
                    convertExpression(containingClass, irExpression.ifFalse),
                )
            }
        }

        is IrCastExpression -> {
            poetCodeBlock.apply {
                add(
                    "(\$T) \$L",
                    convertType(irExpression.targetType),
                    convertExpression(containingClass, irExpression.expression),
                )
            }
        }

        is IrTypeCheckExpression -> {
            poetCodeBlock.apply {
                add(
                    "\$L instanceof \$T",
                    convertExpression(containingClass, irExpression.expression),
                    convertType(irExpression.checkType),
                )
            }
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