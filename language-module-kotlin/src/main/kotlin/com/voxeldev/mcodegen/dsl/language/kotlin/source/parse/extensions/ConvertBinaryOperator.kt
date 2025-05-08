package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator
import com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.Elvis
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IdentityEquals
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IdentityNotEquals
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.In
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IrBinaryOperatorUnknown
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.NotIn
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.RangeTo
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.UnsignedShiftRight
import com.voxeldev.mcodegen.dsl.language.kotlin.utils.Either
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.lexer.KtTokens.ANDAND
import org.jetbrains.kotlin.lexer.KtTokens.DIV
import org.jetbrains.kotlin.lexer.KtTokens.DIVEQ
import org.jetbrains.kotlin.lexer.KtTokens.ELVIS
import org.jetbrains.kotlin.lexer.KtTokens.EQ
import org.jetbrains.kotlin.lexer.KtTokens.EQEQ
import org.jetbrains.kotlin.lexer.KtTokens.EQEQEQ
import org.jetbrains.kotlin.lexer.KtTokens.EXCLEQ
import org.jetbrains.kotlin.lexer.KtTokens.EXCLEQEQEQ
import org.jetbrains.kotlin.lexer.KtTokens.GT
import org.jetbrains.kotlin.lexer.KtTokens.GTEQ
import org.jetbrains.kotlin.lexer.KtTokens.IDENTIFIER
import org.jetbrains.kotlin.lexer.KtTokens.IN_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.LT
import org.jetbrains.kotlin.lexer.KtTokens.LTEQ
import org.jetbrains.kotlin.lexer.KtTokens.MINUS
import org.jetbrains.kotlin.lexer.KtTokens.MINUSEQ
import org.jetbrains.kotlin.lexer.KtTokens.MUL
import org.jetbrains.kotlin.lexer.KtTokens.MULTEQ
import org.jetbrains.kotlin.lexer.KtTokens.NOT_IN
import org.jetbrains.kotlin.lexer.KtTokens.OROR
import org.jetbrains.kotlin.lexer.KtTokens.PERC
import org.jetbrains.kotlin.lexer.KtTokens.PERCEQ
import org.jetbrains.kotlin.lexer.KtTokens.PLUS
import org.jetbrains.kotlin.lexer.KtTokens.PLUSEQ
import org.jetbrains.kotlin.lexer.KtTokens.RANGE
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.resolve.BindingContext

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertBinaryOperator(expression: KtBinaryExpression): Either<IrBinaryOperator, IrAssignmentOperator> {
    return when (val token = expression.operationToken) {
        // arithmetic
        PLUS -> Either.Left(IrBinaryOperator.Plus())
        MINUS-> Either.Left(IrBinaryOperator.Minus())
        MUL -> Either.Left(IrBinaryOperator.Multiply())
        DIV -> Either.Left(IrBinaryOperator.Divide())
        PERC -> Either.Left(IrBinaryOperator.Modulo())

        // comparison
        EQEQ -> Either.Left(IrBinaryOperator.Equals())
        EXCLEQ -> Either.Left(IrBinaryOperator.NotEquals())
        GT -> Either.Left(IrBinaryOperator.Greater())
        GTEQ -> Either.Left(IrBinaryOperator.GreaterOrEqual())
        LT -> Either.Left(IrBinaryOperator.Less())
        LTEQ -> Either.Left(IrBinaryOperator.LessOrEqual())

        // identity (=== / !==)
        EQEQEQ -> Either.Left(IdentityEquals())
        EXCLEQEQEQ -> Either.Left(IdentityNotEquals())

        // boolean-logical
        ANDAND -> Either.Left(IrBinaryOperator.And())
        OROR -> Either.Left(IrBinaryOperator.Or())

        // bitwise / bit-shift
        IDENTIFIER -> {
            when (expression.operationReference.getReferencedName()) {
                "and" -> Either.Left(IrBinaryOperator.BitwiseAnd())
                "or" -> Either.Left(IrBinaryOperator.BitwiseOr())
                "xor" -> Either.Left(IrBinaryOperator.BitwiseXor())
                "shl" -> Either.Left(IrBinaryOperator.ShiftLeft())
                "shr" -> Either.Left(IrBinaryOperator.ShiftRight())
                "ushr" -> Either.Left(UnsignedShiftRight())
                else -> Either.Left(fallbackBinaryOperator(expression, token))
            }
        }

        // elvis, range, in-checks, etc. – add if your IR needs them
        ELVIS -> Either.Left(Elvis())
        RANGE -> Either.Left(RangeTo())
        IN_KEYWORD -> Either.Left(In())
        NOT_IN -> Either.Left(NotIn())

        // plain assignment
        EQ -> Either.Right(IrAssignmentOperator.Assign())

        // compound arithmetic assignments
        PLUSEQ -> Either.Right(IrAssignmentOperator.PlusAssign())
        MINUSEQ -> Either.Right(IrAssignmentOperator.PlusAssign())
        MULTEQ -> Either.Right(IrAssignmentOperator.PlusAssign())
        DIVEQ -> Either.Right(IrAssignmentOperator.PlusAssign())
        PERCEQ -> Either.Right(IrAssignmentOperator.PlusAssign())

        else -> Either.Left(fallbackBinaryOperator(expression, token))
    }
}

private fun fallbackBinaryOperator(
    expression: KtBinaryExpression,
    token: IElementType
) = IrBinaryOperatorUnknown(token = expression.operationReference.text ?: token.toString())