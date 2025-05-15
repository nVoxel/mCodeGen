package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.joinToCode
import com.voxeldev.mcodegen.dsl.ir.IrBlockStatement
import com.voxeldev.mcodegen.dsl.ir.IrBreakStatement
import com.voxeldev.mcodegen.dsl.ir.IrContinueStatement
import com.voxeldev.mcodegen.dsl.ir.IrDoWhileStatement
import com.voxeldev.mcodegen.dsl.ir.IrEmptyStatement
import com.voxeldev.mcodegen.dsl.ir.IrExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.IrIfStatement
import com.voxeldev.mcodegen.dsl.ir.IrReturnStatement
import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.IrStatementUnknown
import com.voxeldev.mcodegen.dsl.ir.IrSwitchStatement
import com.voxeldev.mcodegen.dsl.ir.IrSwitchStatement.IrSwitchStatementCase
import com.voxeldev.mcodegen.dsl.ir.IrThrowStatement
import com.voxeldev.mcodegen.dsl.ir.IrTryCatchStatement
import com.voxeldev.mcodegen.dsl.ir.IrTryCatchStatement.IrTryCatchStatementClause
import com.voxeldev.mcodegen.dsl.ir.IrVariableDeclarationStatement
import com.voxeldev.mcodegen.dsl.ir.IrWhileStatement
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_LABEL
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(KotlinModule, ScenarioScope)
internal fun convertStatement(
    irStatement: IrStatement,
    addLineBreak: Boolean = true,
    useCodeBlockBrackets: Boolean = true,
): CodeBlock {
    val poetCodeBlock = CodeBlock.builder()

    when (irStatement) {
        is IrExpressionStatement -> {
            poetCodeBlock.apply {
                add(convertExpression(irStatement.expression))
                if (addLineBreak) add("\n")
            }
        }

        is IrVariableDeclarationStatement -> {
            poetCodeBlock.apply {
                if (irStatement.additionalNames.isNotEmpty()) {
                    println("Additional names in IrVariableDeclarationStatement are ignored in Kotlin")
                }

                val declarationCodeBlock = CodeBlock.builder().apply {
                    if (irStatement.isMutable) {
                        add("var ")
                    } else {
                        add("val ")
                    }

                    add("%N", irStatement.name)

                    add(": %T", convertType(irStatement.type))

                    irStatement.initializer?.let { initializer ->
                        add(
                            " = %L",
                            convertStatement(
                                irStatement = initializer,
                                addLineBreak = false,
                            )
                        )
                    }
                }.build()

                add(declarationCodeBlock)
                if (addLineBreak) add("\n")
            }
        }

        is IrBlockStatement -> {
            poetCodeBlock.apply {
                if (useCodeBlockBrackets) add("{")
                add("\n")
                indent()
                irStatement.statements.forEach { blockStatement ->
                    add(convertStatement(blockStatement))
                }
                unindent()
                if (useCodeBlockBrackets) add("}")
                if (addLineBreak) add("\n")
            }
        }

        is IrIfStatement -> {
            poetCodeBlock.apply {
                add("if (%L) ", convertExpression(irStatement.condition))
                add(
                    convertStatement(
                        irStatement.thenStatement,
                        addLineBreak = irStatement.elseStatement == null,
                    )
                )
                irStatement.elseStatement?.let { elseStatement ->
                    add(" else ")
                    add(convertStatement(elseStatement))
                }
            }
        }

        // TODO: think of compatibility Java <--> Kotlin <--> Swift
        // is IrForStatement ->

        is IrWhileStatement -> {
            poetCodeBlock.apply {
                add(
                    "while (%L) %L",
                    convertExpression(irStatement.condition),
                    convertStatement(irStatement.body),
                )
            }
        }

        is IrDoWhileStatement -> {
            poetCodeBlock.apply {
                add(
                    "do %L while (%L)",
                    convertStatement(irStatement.body, addLineBreak = false),
                    convertExpression(irStatement.condition),
                )
                if (addLineBreak) add("\n")
            }
        }

        is IrSwitchStatement -> {
            poetCodeBlock.apply {
                add("when (%L) {\n", convertExpression(irStatement.expression))
                indent()
                add(convertSwitchStatementCases(irStatement.cases))
                unindent()
                add("}")
                if (addLineBreak) add("\n")
            }
        }

        is IrReturnStatement -> {
            poetCodeBlock.apply {
                add("return")

                val returnLabel = irStatement.languageProperties[KT_LABEL] as? String
                returnLabel?.let {
                    add(returnLabel)
                }

                irStatement.expression?.let { returnExpression ->
                    add(" %L", convertExpression(returnExpression))
                }

                if (addLineBreak) add("\n")
            }
        }

        is IrBreakStatement -> {
            poetCodeBlock.apply {
                add("break")
                if (addLineBreak) add("\n")
            }
        }

        is IrContinueStatement -> {
            poetCodeBlock.apply {
                add("continue")
                if (addLineBreak) add("\n")
            }
        }

        is IrThrowStatement -> {
            poetCodeBlock.apply {
                add("throw %L", convertExpression(irStatement.expression))
                if (addLineBreak) add("\n")
            }
        }

        is IrTryCatchStatement -> {
            poetCodeBlock.apply {
                if (irStatement.tryBlock !is IrBlockStatement) {
                    throw IllegalArgumentException("Try-catch statement in Kotlin accepts only block statement as a try body")
                }

                if (irStatement.finallyBlock != null && irStatement.finallyBlock !is IrBlockStatement) {
                    throw IllegalArgumentException("Try-catch statement in Kotlin accepts only block statement as a finally body")
                }

                add("try %L", convertStatement(irStatement.tryBlock, addLineBreak = false))

                add(convertTryCatchStatementClauses(irStatement.catchClauses))

                irStatement.finallyBlock?.let { irFinallyStatement ->
                    add(" finally %L", convertStatement(irFinallyStatement, addLineBreak = false))
                }

                if (addLineBreak) add("\n")
            }
        }

        is IrEmptyStatement -> {
            // no-op
        }

        else -> {
            if (irStatement is IrStatementUnknown) {
                val representation = irStatement.stringRepresentation.firstOrNull { it.language == languageName }
                representation?.let {
                    return poetCodeBlock.apply {
                        add(representation.representation)
                        if (addLineBreak) add("\n")
                    }.build()
                }
            }
            poetCodeBlock.add("Unsupported") // TODO: Remove debug marker
        }
    }

    return poetCodeBlock.build()
}

context(KotlinModule, ScenarioScope)
private fun convertSwitchStatementCases(
    cases: List<IrSwitchStatementCase>,
): CodeBlock {
    return CodeBlock.builder().apply {
        cases.forEach { switchStatementCase ->
            if (switchStatementCase.matchExpressions.isNotEmpty()) {
                val matchExpressions = switchStatementCase.matchExpressions.map { matchExpression ->
                    convertExpression(matchExpression)
                }
                add(matchExpressions.joinToCode(separator = ", "))

                add(" -> ")
            } else {
                add("else -> ")
            }

            switchStatementCase.body?.let { body ->
                add(convertStatement(body))
            }
        }
    }.build()
}

context(KotlinModule, ScenarioScope)
private fun convertTryCatchStatementClauses(
    clauses: List<IrTryCatchStatementClause>,
): CodeBlock {
    return CodeBlock.builder().apply {
        clauses.forEach { tryCatchStatementClause ->
            if (tryCatchStatementClause.body != null && tryCatchStatementClause.body !is IrBlockStatement) {
                throw IllegalArgumentException("Try-catch statement in Kotlin accepts only block statement as a clause body")
            }

            add(
                " catch (%L: %T) ",
                tryCatchStatementClause.exceptionName ?: "e",
                convertType(tryCatchStatementClause.exceptionType),
            )

            tryCatchStatementClause.body?.let { irBodyStatement ->
                add(convertStatement(irBodyStatement, addLineBreak = false))
            }
        }
    }.build()
}
