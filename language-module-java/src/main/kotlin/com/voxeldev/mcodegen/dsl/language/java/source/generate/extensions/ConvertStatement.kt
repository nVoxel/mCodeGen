package com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions

import com.squareup.javapoet.CodeBlock
import com.voxeldev.mcodegen.dsl.ir.IrBlockStatement
import com.voxeldev.mcodegen.dsl.ir.IrBreakStatement
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrContinueStatement
import com.voxeldev.mcodegen.dsl.ir.IrDoWhileStatement
import com.voxeldev.mcodegen.dsl.ir.IrEmptyStatement
import com.voxeldev.mcodegen.dsl.ir.IrExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.IrForStatement
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
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(JavaModule, ScenarioScope)
internal fun convertStatement(
    containingClass: IrClass,
    irStatement: IrStatement,
    addSemicolon: Boolean = true,
    addLineBreak: Boolean = true,
    useCodeBlockBrackets: Boolean = true,
): CodeBlock {
    val poetCodeBlock = CodeBlock.builder()

    when (irStatement) {
        is IrExpressionStatement -> {
            poetCodeBlock.apply {
                add(convertExpression(containingClass, irStatement.expression))
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
                        add(
                            " = \$L",
                            convertStatement(
                                containingClass = containingClass,
                                irStatement = initializer,
                                addSemicolon = false,
                                addLineBreak = false,
                            )
                        )
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
                    add(convertStatement(containingClass, blockStatement))
                }
                add("\$<")
                if (useCodeBlockBrackets) add("}")
                if (addLineBreak) add("\n")
            }
        }

        is IrIfStatement -> {
            poetCodeBlock.apply {
                add("if (\$L) ", convertExpression(containingClass, irStatement.condition))
                add(
                    convertStatement(
                        containingClass,
                        irStatement.thenStatement,
                        addLineBreak = irStatement.thenStatement !is IrBlockStatement
                                || irStatement.elseStatement == null,
                    )
                )
                irStatement.elseStatement?.let { elseStatement ->
                    if (irStatement.thenStatement is IrBlockStatement) {
                        add(" ")
                    }
                    add("else ")
                    add(convertStatement(containingClass, elseStatement))
                }
            }
        }

        is IrForStatement -> {
            poetCodeBlock.apply {
                add("for (")
                irStatement.initializer?.let { initializer ->
                    val addDividers = irStatement.condition != null || irStatement.update != null
                    add(
                        "\$L",
                        convertStatement(
                            containingClass,
                            initializer,
                            addLineBreak = false,
                            addSemicolon = addDividers,
                        )
                    )
                    if (addDividers) {
                        add(" ")
                    }
                }

                irStatement.condition?.let { condition ->
                    add("\$L", convertExpression(containingClass, condition))
                    if (irStatement.update != null) {
                        add("; ")
                    }
                }

                irStatement.update?.let { update ->
                    add("\$L", convertStatement(containingClass, update, addLineBreak = false, addSemicolon = false))
                }

                add(") ")
                add(convertStatement(containingClass, irStatement.body))
            }
        }

        is IrWhileStatement -> {
            poetCodeBlock.apply {
                add(
                    "while (\$L) \$L",
                    convertExpression(containingClass, irStatement.condition),
                    convertStatement(containingClass, irStatement.body),
                )
            }
        }

        is IrDoWhileStatement -> {
            poetCodeBlock.apply {
                add(
                    "do \$L while (\$L)",
                    convertStatement(containingClass, irStatement.body, addSemicolon = true, addLineBreak = false),
                    convertExpression(containingClass, irStatement.condition),
                )
                if (addSemicolon) add(";")
                if (addLineBreak) add("\n")
            }
        }

        is IrSwitchStatement -> {
            poetCodeBlock.apply {
                add("switch (\$L) {\n\$>", convertExpression(containingClass, irStatement.expression))
                add(convertSwitchStatementCases(containingClass, irStatement.cases))
                add("\$<}")
                if (addLineBreak) add("\n")
            }
        }

        is IrReturnStatement -> {
            poetCodeBlock.apply {
                add("return")
                irStatement.expression?.let { returnExpression ->
                    add(" \$L", convertExpression(containingClass, returnExpression))
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
                add("throw \$L", convertExpression(containingClass, irStatement.expression))
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

                add("try \$L", convertStatement(containingClass, irStatement.tryBlock, addLineBreak = false))

                add(convertTryCatchStatementClauses(containingClass, irStatement.catchClauses))

                irStatement.finallyBlock?.let { irFinallyStatement ->
                    add(" finally \$L", convertStatement(containingClass, irFinallyStatement, addLineBreak = false))
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
    containingClass: IrClass,
    cases: List<IrSwitchStatementCase>,
): CodeBlock {
    return CodeBlock.builder().apply {
        cases.forEach { switchStatementCase ->
            if (switchStatementCase.matchExpressions.isNotEmpty()) {
                add("case ")

                val matchExpressions = switchStatementCase.matchExpressions.map { matchExpression ->
                    convertExpression(containingClass, matchExpression)
                }
                add(CodeBlock.join(matchExpressions, ", "))

                add(":")
            } else {
                add("default:")
            }

            switchStatementCase.body?.let { body ->
                add(convertStatement(containingClass, body, useCodeBlockBrackets = false))
            }
        }
    }.build()
}

context(JavaModule, ScenarioScope)
private fun convertTryCatchStatementClauses(
    containingClass: IrClass,
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
                add(convertStatement(containingClass, irBodyStatement, addLineBreak = false))
            }
        }
    }.build()
}