package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.builders.irBlockStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irBreakStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irContinueStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irDoWhileStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irForStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irIfStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irReturnStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irSwitchStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irSwitchStatementCase
import com.voxeldev.mcodegen.dsl.ir.builders.irThrowStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irTryCatchStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irTryCatchStatementClause
import com.voxeldev.mcodegen.dsl.ir.builders.irVariableDeclarationStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irWhileStatement
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.ir.builders.irStatementUnknown
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

context(JavaModule, ScenarioScope)
internal fun convertStatement(psiStatement: PsiStatement): IrStatement {
    return when (psiStatement) {
        is PsiExpressionStatement -> {
            irExpressionStatement(expression = convertExpression(psiStatement.expression)).build()
        }

        is PsiDeclarationStatement -> {
            irBlockStatement().apply {
                psiStatement.declaredElements
                    .mapNotNull { it as? PsiVariable }
                    .forEach { variable ->
                        addStatement(
                            statement = irVariableDeclarationStatement(
                                name = variable.name ?: "Ir:UnnamedVariable",
                                type = variable.type.canonicalText,
                            ).apply {
                                variable.initializer?.let { variableInitializer ->
                                    initializer(initializer = convertExpression(variableInitializer))
                                }
                            }.build()
                        )
                    }
            }.build()
        }

        is PsiBlockStatement -> {
            irBlockStatement().apply {
                psiStatement.codeBlock.statements.forEach { innerStatement ->
                    addStatement(convertStatement(psiStatement = innerStatement))
                }
            }.build()
        }

        is PsiIfStatement -> {
            irIfStatement(condition = convertExpression(psiStatement.condition)).apply {
                psiStatement.thenBranch?.let { thenBranch ->
                    thenStatement(statement = convertStatement(thenBranch))
                }
                psiStatement.elseBranch?.let { elseBranch ->
                    elseStatement(statement = convertStatement(elseBranch))
                }
            }.build()
        }

        is PsiForStatement -> {
            irForStatement().apply {
                psiStatement.initialization?.let { initialization ->
                    initializer(initializer = convertStatement(initialization))
                }

                psiStatement.condition?.let { condition ->
                    condition(condition = convertExpression(condition))
                }

                psiStatement.update?.let { update ->
                    update(update = convertStatement(update))
                }

                psiStatement.body?.let { body ->
                    body(statement = convertStatement(body))
                }
            }.build()
        }

        is PsiWhileStatement -> {
            irWhileStatement(
                condition = convertExpression(psiStatement.condition),
            ).apply {
                psiStatement.body?.let { body ->
                    body(statement = convertStatement(body))
                }
            }.build()
        }

        is PsiDoWhileStatement -> {
            irDoWhileStatement(
                condition = convertExpression(psiStatement.condition),
            ).apply {
                psiStatement.body?.let { body ->
                    body(statement = convertStatement(body))
                }
            }.build()
        }

        is PsiSwitchStatement -> {
            irSwitchStatement(
                expression = convertExpression(psiStatement.expression)
            ).apply {
                psiStatement.body?.statements?.let { bodyStatements ->
                    val cases = bodyStatements.splitWhen { statement ->
                        statement is PsiSwitchLabelStatement
                    }.filter { it.size > 1 }

                    cases.forEach { case ->
                        val switchLabelStatement = case[0] as? PsiSwitchLabelStatement ?: return@forEach
                        val matchExpression =
                            switchLabelStatement.caseValues?.expressions?.getOrNull(0) ?: return@forEach
                        addCase(
                            case = irSwitchStatementCase().apply {
                                if (!switchLabelStatement.isDefaultCase) {
                                    matchExpression(matchExpression = convertExpression(matchExpression))
                                }

                                body(
                                    statement = irBlockStatement().apply {
                                        case.drop(1).forEach { caseBodyStatement ->
                                            addStatement(statement = convertStatement(caseBodyStatement))
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
                    expression(expression = convertExpression(returnValue))
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
                expression = convertExpression(psiStatement.exception)
            ).build()
        }

        is PsiTryStatement -> {
            irTryCatchStatement(
                tryBlock = irBlockStatement().apply {
                    psiStatement.tryBlock?.statements?.forEach { tryBlockStatement ->
                        addStatement(statement = convertStatement(tryBlockStatement))
                    }
                }.build()
            ).apply {
                psiStatement.catchSections.forEach { catchSection ->
                    addCatchClause(
                        clause = irTryCatchStatementClause(
                            exceptionType = catchSection.catchType?.canonicalText ?: "Ir:UnnamedType"
                        ).apply {
                            catchSection.parameter?.name?.let { parameterName ->
                                exceptionName(parameterName)
                            }

                            catchSection.catchBlock?.statements?.let { statements ->
                                body(
                                    statement = irBlockStatement().apply {
                                        statements.forEach { catchBlockStatement ->
                                            addStatement(statement = convertStatement(catchBlockStatement))
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
                                addStatement(statement = convertStatement(finallyBlockStatement))
                            }
                        }.build()
                    )
                }
            }.build()
        }

        else -> {
            irStatementUnknown().apply {
                addStringRepresentation(
                    IrStringRepresentation("java", psiStatement.text)
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