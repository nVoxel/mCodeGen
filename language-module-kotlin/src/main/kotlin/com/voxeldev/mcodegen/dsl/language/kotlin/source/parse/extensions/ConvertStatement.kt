package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrExpressionUnknown
import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.builders.irBlockStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irBreakStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irContinueStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irDoWhileStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irExpressionStatement
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
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenConditionWithExpression
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.KtWhileExpression
import org.jetbrains.kotlin.resolve.BindingContext

const val KT_LABEL = "ktLabel"

// because there are no statements in Kotlin, we accept expression here
context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertStatement(
    ktClassOrObject: KtClassOrObject?,
    ktExpression: KtExpression,
): IrStatement {
    // try all the statements except IrExpressionStatement
    when (ktExpression) {
        is KtProperty -> {
            val propertyAsField = convertFieldAsProperty(ktClassOrObject, ktExpression)
                ?: throw IllegalArgumentException("Got variable declaration statement without variables")

            return irVariableDeclarationStatement(
                name = propertyAsField.name,
                type = propertyAsField.type,
            ).apply {
                mutable(propertyAsField.isMutable)

                propertyAsField.initializer?.let { initializer ->
                    initializer(initializer)
                }
            }.build()
        }

        is KtBlockExpression -> {
            return irBlockStatement().apply {
                ktExpression.statements.forEach { innerStatement ->
                    addStatement(convertStatement(ktClassOrObject, innerStatement))
                }
            }.build()
        }

        is KtIfExpression -> {
            return irIfStatement(condition = convertExpression(ktClassOrObject, ktExpression.condition)).apply {
                ktExpression.then?.let { thenBranch ->
                    thenStatement(statement = convertStatement(ktClassOrObject, thenBranch))
                }
                ktExpression.`else`?.let { elseBranch ->
                    elseStatement(statement = convertStatement(ktClassOrObject, elseBranch))
                }
            }.build()
        }

        // TODO: think of compatibility Java <--> Kotlin <--> Swift
        // is KtForExpression ->

        is KtWhileExpression -> {
            return irWhileStatement(
                condition = convertExpression(ktClassOrObject, ktExpression.condition)
            ).apply {
                ktExpression.body?.let { body ->
                    body(statement = convertStatement(ktClassOrObject, body))
                }
            }.build()
        }

        is KtDoWhileExpression -> {
            return irDoWhileStatement(
                condition = convertExpression(ktClassOrObject, ktExpression.condition)
            ).apply {
                ktExpression.body?.let { body ->
                    body(statement = convertStatement(ktClassOrObject, body))
                }
            }.build()
        }

        is KtWhenExpression -> {
            ktExpression.subjectExpression?.let { expression ->
                // TODO: should be a statement instead, e.g. when declaring a variable in when()
                convertExpression(ktClassOrObject, expression)
            }?.let { expression ->
                return irSwitchStatement(
                    expression = expression,
                ).apply {
                    ktExpression.entries.forEach { entry ->
                        addCase(
                            case = irSwitchStatementCase().apply {
                                val entryConditions = entry.conditions.mapNotNull { entryCondition ->
                                    when (entryCondition) {
                                        is KtWhenConditionWithExpression -> entryCondition.expression
                                        else -> null // TODO: support KtWhenConditionInRange, KtWhenConditionIsPattern
                                    }
                                }.map { entryCondition -> convertExpression(ktClassOrObject, entryCondition) }

                                if (!entry.isElse) {
                                    entryConditions.forEach { entryCondition ->
                                        addMatchExpression(entryCondition)
                                    }
                                }

                                entry.expression?.let { entryBody ->
                                    body(statement = convertStatement(ktClassOrObject, entryBody))
                                }
                            }.build()
                        )
                    }
                }.build()
            }
        }

        is KtReturnExpression -> {
            ktExpression.returnedExpression?.let { returnValue ->
                return irReturnStatement().apply {
                    expression(expression = convertExpression(ktClassOrObject, returnValue))
                    ktExpression.labeledExpression?.text?.let { label ->
                        addLanguageProperty(KT_LABEL, label) // return@label
                    }
                }.build()
            }
        }

        is KtBreakExpression -> {
            return irBreakStatement().build()
        }

        is KtContinueExpression -> {
            return irContinueStatement().build()
        }

        is KtThrowExpression -> {
            ktExpression.thrownExpression?.let { thrownExpression ->
                return irThrowStatement(
                    expression = convertExpression(ktClassOrObject, thrownExpression),
                ).build()
            }
        }

        is KtTryExpression -> {
            return irTryCatchStatement(
                tryBlock = convertStatement(ktClassOrObject, ktExpression.tryBlock)
            ).apply {
                ktExpression.catchClauses.forEach { catchClause ->
                    val catchParameter = catchClause.catchParameter?.let { catchParameter ->
                        convertFieldAsParameter(ktClassOrObject, catchParameter)
                    } ?: throw IllegalArgumentException("Catch clause doesn't have a parameter")

                    addCatchClause(
                        clause = irTryCatchStatementClause(
                            exceptionType = catchParameter.type,
                        ).apply {
                            exceptionName(catchParameter.name)

                            catchClause.catchBody?.let { catchBody ->
                                body(convertStatement(ktClassOrObject, catchBody))
                            }
                        }.build()
                    )
                }

                ktExpression.finallyBlock?.finalExpression?.let { finalExpression ->
                    finallyBlock(statement = convertStatement(ktClassOrObject, finalExpression))
                }
            }.build()
        }
    }

    // if no matches, then try converting ktExpression to IrExpression
    val asExpression = convertExpression(ktClassOrObject, ktExpression)
    if (asExpression !is IrExpressionUnknown) {
        return irExpressionStatement(asExpression).build()
    }

    // otherwise, return IrStatementUnknown
    return irStatementUnknown().apply {
        addStringRepresentation(
            IrStringRepresentation(languageName, ktExpression.text)
        )
    }.build()
}