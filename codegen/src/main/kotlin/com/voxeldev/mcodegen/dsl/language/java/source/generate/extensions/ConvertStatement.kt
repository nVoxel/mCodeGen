package com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions

import com.squareup.javapoet.CodeBlock
import com.voxeldev.mcodegen.dsl.ir.IrBlockStatement
import com.voxeldev.mcodegen.dsl.ir.IrBreakStatement
import com.voxeldev.mcodegen.dsl.ir.IrContinueStatement
import com.voxeldev.mcodegen.dsl.ir.IrDoWhileStatement
import com.voxeldev.mcodegen.dsl.ir.IrExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.IrForStatement
import com.voxeldev.mcodegen.dsl.ir.IrIfStatement
import com.voxeldev.mcodegen.dsl.ir.IrReturnStatement
import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.IrSwitchStatement
import com.voxeldev.mcodegen.dsl.ir.IrSwitchStatement.IrSwitchStatementCase
import com.voxeldev.mcodegen.dsl.ir.IrThrowStatement
import com.voxeldev.mcodegen.dsl.ir.IrTryCatchStatement
import com.voxeldev.mcodegen.dsl.ir.IrTryCatchStatement.IrTryCatchStatementClause
import com.voxeldev.mcodegen.dsl.ir.IrVariableDeclarationStatement
import com.voxeldev.mcodegen.dsl.ir.IrWhileStatement
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.ir.IrStatementUnknown
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import kotlin.collections.forEach

context(JavaModule, ScenarioScope)
internal fun convertStatement(
    irStatement: IrStatement,
    addSemicolon: Boolean = true,
    addLineBreak: Boolean = true,
    useCodeBlockBrackets: Boolean = true,
): CodeBlock {
    val poetCodeBlock = CodeBlock.builder()

    when (irStatement) {
        is IrExpressionStatement -> {
            poetCodeBlock.apply {
                add(convertExpression(irStatement.expression))
                if (addSemicolon) add(";")
                if (addLineBreak) add("\n")
            }
        }

        is IrVariableDeclarationStatement -> {
            poetCodeBlock.apply {
                val declarationCodeBlock = CodeBlock.builder().apply {
                    val variables = listOf(irStatement.name)
                        .plus(irStatement.additionalNames)
                        .map { name -> CodeBlock.of("\$N", name) }

                    add("\$T", convertType(irStatement.type))

                    if (variables.isNotEmpty()) {
                        add(" \$L", CodeBlock.join(variables, ", "))
                    }

                    irStatement.initializer?.let { initializer ->
                        add(" = \$L", convertExpression(initializer))
                    }
                }.build()

                add(declarationCodeBlock)
                if (addSemicolon) add(";")
                if (addLineBreak) add("\n")
            }
        }

        is IrBlockStatement -> {
            poetCodeBlock.apply {
                if (useCodeBlockBrackets) add("{")
                add("\n\$>")
                irStatement.statements.forEach { blockStatement ->
                    add(convertStatement(blockStatement))
                }
                add("\$<")
                if (useCodeBlockBrackets) add("}")
                if (addLineBreak) add("\n")
            }
        }

        is IrIfStatement -> {
            poetCodeBlock.apply {
                add("if (\$L) ", convertExpression(irStatement.condition))
                add(
                    convertStatement(
                        irStatement.thenStatement,
                        addLineBreak = irStatement.thenStatement !is IrBlockStatement
                                || irStatement.elseStatement == null,
                    )
                )
                if (irStatement.elseStatement != null) {
                    if (irStatement.thenStatement is IrBlockStatement) {
                        add(" ")
                    }
                    add("else ")
                    add(convertStatement(irStatement.elseStatement))
                }
            }
        }

        is IrForStatement -> {
            poetCodeBlock.apply {
                add("for (")
                irStatement.initializer?.let { initializer ->
                    val addDividers = irStatement.condition != null || irStatement.update != null
                    add("\$L", convertStatement(initializer, addLineBreak = false, addSemicolon = addDividers))
                    if (addDividers) {
                        add(" ")
                    }
                }

                irStatement.condition?.let { condition ->
                    add("\$L", convertExpression(condition))
                    if (irStatement.update != null) {
                        add("; ")
                    }
                }

                irStatement.update?.let { update ->
                    add("\$L", convertStatement(update, addLineBreak = false, addSemicolon = false))
                }

                add(") ")
                add(convertStatement(irStatement.body))
            }
        }

        is IrWhileStatement -> {
            poetCodeBlock.apply {
                add(
                    "while (\$L) \$L",
                    convertExpression(irStatement.condition),
                    convertStatement(irStatement.body),
                )
            }
        }

        is IrDoWhileStatement -> {
            poetCodeBlock.apply {
                add(
                    "do \$L while (\$L)",
                    convertStatement(irStatement.body, addSemicolon = true, addLineBreak = false),
                    convertExpression(irStatement.condition),
                )
                if (addSemicolon) add(";")
                if (addLineBreak) add("\n")
            }
        }

        is IrSwitchStatement -> {
            poetCodeBlock.apply {
                add("switch (\$L) {\n\$>", convertExpression(irStatement.expression))
                add(convertSwitchStatementCases(irStatement.cases))
                add("\$<}")
                if (addLineBreak) add("\n")
            }
        }

        is IrReturnStatement -> {
            poetCodeBlock.apply {
                add("return")
                irStatement.expression?.let { returnExpression ->
                    add(" \$L", convertExpression(returnExpression))
                }
                if (addSemicolon) add(";")
                if (addLineBreak) add("\n")
            }
        }

        is IrBreakStatement -> {
            poetCodeBlock.apply {
                add("break")
                if (addSemicolon) add(";")
                if (addLineBreak) add("\n")
            }
        }

        is IrContinueStatement -> {
            poetCodeBlock.apply {
                add("continue")
                if (addSemicolon) add(";")
                if (addLineBreak) add("\n")
            }
        }

        is IrThrowStatement -> {
            poetCodeBlock.apply {
                add("throw \$L", convertExpression(irStatement.expression))
                if (addSemicolon) add(";")
                if (addLineBreak) add("\n")
            }
        }

        is IrTryCatchStatement -> {
            poetCodeBlock.apply {
                if (irStatement.tryBlock !is IrBlockStatement) {
                    throw IllegalArgumentException("Try-catch statement in Java accepts only block statement as a try body")
                }

                if (irStatement.finallyBlock != null && irStatement.finallyBlock !is IrBlockStatement) {
                    throw IllegalArgumentException("Try-catch statement in Java accepts only block statement as a finally body")
                }

                add("try \$L", convertStatement(irStatement.tryBlock, addLineBreak = false))

                add(convertTryCatchStatementClauses(irStatement.catchClauses))

                irStatement.finallyBlock?.let { irFinallyStatement ->
                    add(" finally \$L", convertStatement(irFinallyStatement, addLineBreak = false))
                }

                if (addLineBreak) add("\n")
            }
        }

        else -> {
            if (irStatement is IrStatementUnknown) {
                val representation = irStatement.stringRepresentation.firstOrNull { it.language == "java" }
                representation?.let {
                    return poetCodeBlock.apply {
                        add(representation.representation)
                        if (addSemicolon) add(";")
                        if (addLineBreak) add("\n")
                    }.build()
                }
            }
            poetCodeBlock.add("Unsupported") // TODO: Remove debug marker
        }
    }

    return poetCodeBlock.build()
}

context(JavaModule, ScenarioScope)
private fun convertSwitchStatementCases(
    cases: List<IrSwitchStatementCase>,
): CodeBlock {
    return CodeBlock.builder().apply {
        cases.forEach { switchStatementCase ->
            if (switchStatementCase.matchExpression != null) {
                add("case \$L:", convertExpression(switchStatementCase.matchExpression))
            } else {
                add("default:")
            }

            if (switchStatementCase.body != null) {
                add(convertStatement(switchStatementCase.body, useCodeBlockBrackets = false))
            }
        }
    }.build()
}

context(JavaModule, ScenarioScope)
private fun convertTryCatchStatementClauses(
    clauses: List<IrTryCatchStatementClause>,
): CodeBlock {
    return CodeBlock.builder().apply {
        clauses.forEach { tryCatchStatementClause ->
            if (tryCatchStatementClause.body != null && tryCatchStatementClause.body !is IrBlockStatement) {
                throw IllegalArgumentException("Try-catch statement in Java accepts only block statement as a clause body")
            }

            add(
                " catch (\$T \$L) ",
                convertType(tryCatchStatementClause.exceptionType),
                tryCatchStatementClause.exceptionName ?: "e",
            )

            tryCatchStatementClause.body?.let { irBodyStatement ->
                add(convertStatement(irBodyStatement, addLineBreak = false))
            }
        }
    }.build()
}