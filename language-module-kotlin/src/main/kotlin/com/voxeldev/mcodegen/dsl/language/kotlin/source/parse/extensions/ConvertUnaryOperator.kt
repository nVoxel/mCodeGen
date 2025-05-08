package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IrUnaryOperatorUnknown
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.NonNullAssert
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.lexer.KtTokens.EXCL
import org.jetbrains.kotlin.lexer.KtTokens.EXCLEXCL
import org.jetbrains.kotlin.lexer.KtTokens.MINUS
import org.jetbrains.kotlin.lexer.KtTokens.MINUSMINUS
import org.jetbrains.kotlin.lexer.KtTokens.PLUS
import org.jetbrains.kotlin.lexer.KtTokens.PLUSPLUS
import org.jetbrains.kotlin.psi.KtUnaryExpression
import org.jetbrains.kotlin.resolve.BindingContext

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertUnaryOperator(expression: KtUnaryExpression): IrUnaryOperator {
    return when (val token = expression.operationToken) {
        PLUS -> IrUnaryOperator.Plus()
        MINUS -> IrUnaryOperator.Minus()
        EXCL -> IrUnaryOperator.Not()
        PLUSPLUS -> IrUnaryOperator.Increment()
        MINUSMINUS -> IrUnaryOperator.Decrement()
        EXCLEXCL -> NonNullAssert()
        else -> fallbackUnaryOperator(expression, token)
    }
}

private fun fallbackUnaryOperator(
    expression: KtUnaryExpression,
    token: IElementType
) = IrUnaryOperatorUnknown(token = expression.operationReference.text ?: token.toString())