package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrEmptyExpression
import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.builders.irBlockStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irBreakStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irContinueStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irDoWhileStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irEmptyStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irForStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irIfStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irReturnStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irStatementUnknown
import com.voxeldev.mcodegen.dsl.ir.builders.irSwitchStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irSwitchStatementCase
import com.voxeldev.mcodegen.dsl.ir.builders.irThrowStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irTryCatchStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irTryCatchStatementClause
import com.voxeldev.mcodegen.dsl.ir.builders.irVariableDeclarationStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irWhileStatement
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiBlockStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiBreakStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiContinueStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiDeclarationStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiDoWhileStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiExpressionStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiForStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiIfStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiReturnStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiSwitchLabelStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiSwitchStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiThrowStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiTryStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiVariable
import org.jetbrains.kotlin.com.intellij.psi.PsiWhileStatement

// TODO: need to rewrite this to handle IrEmptyExpression properly
context(JavaModule, ScenarioScope)
internal fun convertStatement(
    psiStatement: PsiStatement,
    ignoreConstructorCalls: Boolean = false,
): IrStatement {
    return when (psiStatement) {
        is PsiExpressionStatement -> {
            val expression = convertExpression(
                psiStatement.expression,
                ignoreConstructorCalls
            )

            if (expression is IrEmptyExpression) {
                irEmptyStatement().build()
            } else {
                irExpressionStatement(
                    expression = expression
                ).build()
            }
        }

        is PsiDeclarationStatement -> {
            val declaredVariables = psiStatement.declaredElements
                .mapNotNull { it as? PsiVariable }

            if (declaredVariables.isEmpty()) {
                throw IllegalArgumentException("Got variable declaration statement without variables")
            }

            irVariableDeclarationStatement(
                name = declaredVariables[0].name ?: "Ir:UnnamedVariable",
                type = convertType(declaredVariables[0].type)
            ).apply {
                declaredVariables.drop(1).forEach { variable ->
                    addName(variable.name ?: "Ir:UnnamedVariable")
                }

                declaredVariables[0].initializer?.let { variableInitializer ->
                    val initializerExpression = convertExpression(variableInitializer, ignoreConstructorCalls)
                    initializer(initializer = irExpressionStatement(expression = initializerExpression).build())
                }
            }.build()
        }

        is PsiBlockStatement -> {
            irBlockStatement().apply {
                psiStatement.codeBlock.statements.forEach { innerStatement ->
                    addStatement(convertStatement(psiStatement = innerStatement, ignoreConstructorCalls))
                }
            }.build()
        }

        is PsiIfStatement -> {
            irIfStatement(condition = convertExpression(psiStatement.condition, ignoreConstructorCalls)).apply {
                psiStatement.thenBranch?.let { thenBranch ->
                    thenStatement(statement = convertStatement(thenBranch, ignoreConstructorCalls))
                }
                psiStatement.elseBranch?.let { elseBranch ->
                    elseStatement(statement = convertStatement(elseBranch, ignoreConstructorCalls))
                }
            }.build()
        }

        is PsiForStatement -> {
            irForStatement().apply {
                psiStatement.initialization?.let { initialization ->
                    initializer(initializer = convertStatement(initialization, ignoreConstructorCalls))
                }

                psiStatement.condition?.let { condition ->
                    condition(condition = convertExpression(condition, ignoreConstructorCalls))
                }

                psiStatement.update?.let { update ->
                    update(update = convertStatement(update, ignoreConstructorCalls))
                }

                psiStatement.body?.let { body ->
                    body(statement = convertStatement(body, ignoreConstructorCalls))
                }
            }.build()
        }

        is PsiWhileStatement -> {
            irWhileStatement(
                condition = convertExpression(psiStatement.condition, ignoreConstructorCalls),
            ).apply {
                psiStatement.body?.let { body ->
                    body(statement = convertStatement(body, ignoreConstructorCalls))
                }
            }.build()
        }

        is PsiDoWhileStatement -> {
            irDoWhileStatement(
                condition = convertExpression(psiStatement.condition, ignoreConstructorCalls),
            ).apply {
                psiStatement.body?.let { body ->
                    body(statement = convertStatement(body, ignoreConstructorCalls))
                }
            }.build()
        }

        is PsiSwitchStatement -> {
            irSwitchStatement(
                expression = convertExpression(psiStatement.expression, ignoreConstructorCalls)
            ).apply {
                psiStatement.body?.statements?.let { bodyStatements ->
                    val cases = bodyStatements.splitWhen { statement ->
                        statement is PsiSwitchLabelStatement
                    }

                    cases.forEach { case -> // TODO: multiple match expressions
                        val switchLabelStatement = case[0] as? PsiSwitchLabelStatement ?: return@forEach
                        val matchExpressions =
                            switchLabelStatement.caseValues?.expressions
                        addCase(
                            case = irSwitchStatementCase().apply {
                                if (!switchLabelStatement.isDefaultCase) {
                                    matchExpressions?.forEach { matchExpression ->
                                        addMatchExpression(
                                            matchExpression = convertExpression(
                                                matchExpression,
                                                ignoreConstructorCalls
                                            )
                                        )
                                    }
                                }

                                body(
                                    statement = irBlockStatement().apply {
                                        case.drop(1).forEach { caseBodyStatement ->
                                            addStatement(
                                                statement = convertStatement(
                                                    caseBodyStatement,
                                                    ignoreConstructorCalls
                                                )
                                            )
                                        }
                                    }.build()
                                )
                            }.build()
                        )
                    }
                }
            }.build()
        }

        is PsiReturnStatement -> {
            irReturnStatement().apply {
                psiStatement.returnValue?.let { returnValue ->
                    expression(expression = convertExpression(returnValue, ignoreConstructorCalls))
                }
            }.build()
        }

        is PsiBreakStatement -> {
            irBreakStatement().build()
        }

        is PsiContinueStatement -> {
            irContinueStatement().build()
        }

        is PsiThrowStatement -> {
            irThrowStatement(
                expression = convertExpression(psiStatement.exception, ignoreConstructorCalls)
            ).build()
        }

        is PsiTryStatement -> {
            irTryCatchStatement(
                tryBlock = irBlockStatement().apply {
                    psiStatement.tryBlock?.statements?.forEach { tryBlockStatement ->
                        addStatement(statement = convertStatement(tryBlockStatement, ignoreConstructorCalls))
                    }
                }.build()
            ).apply {
                psiStatement.catchSections.forEach { catchSection ->
                    addCatchClause(
                        clause = irTryCatchStatementClause(
                            exceptionType = convertType(catchSection.catchType)
                        ).apply {
                            catchSection.parameter?.name?.let { parameterName ->
                                exceptionName(parameterName)
                            }

                            catchSection.catchBlock?.statements?.let { statements ->
                                body(
                                    statement = irBlockStatement().apply {
                                        statements.forEach { catchBlockStatement ->
                                            addStatement(
                                                statement = convertStatement(
                                                    catchBlockStatement,
                                                    ignoreConstructorCalls
                                                )
                                            )
                                        }
                                    }.build()
                                )
                            }
                        }.build()
                    )
                }

                psiStatement.finallyBlock?.statements?.let { finallyBlockStatements ->
                    finallyBlock(
                        statement = irBlockStatement().apply {
                            finallyBlockStatements.forEach { finallyBlockStatement ->
                                addStatement(
                                    statement = convertStatement(
                                        finallyBlockStatement,
                                        ignoreConstructorCalls
                                    )
                                )
                            }
                        }.build()
                    )
                }
            }.build()
        }

        else -> {
            irStatementUnknown().apply {
                addStringRepresentation(
                    IrStringRepresentation(languageName, psiStatement.text)
                )
            }.build()
        }
    }
}

private fun <T> Array<T>.splitWhen(predicate: (T) -> Boolean): List<List<T>> {
    val result = mutableListOf<MutableList<T>>()
    for (item in this) {
        if (predicate(item) || result.isEmpty()) {
            result.add(mutableListOf())
        }
        result.last().add(item)
    }
    return result
}