package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions

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
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(KotlinModule, ScenarioScope)
internal fun convertBinaryOperator(operator: IrBinaryOperator) : String {
    return when (operator) {
        is IrBinaryOperator.Plus -> "+"
        is IrBinaryOperator.Minus -> "-"
        is IrBinaryOperator.Multiply -> "*"
        is IrBinaryOperator.Divide -> "/"
        is IrBinaryOperator.Modulo -> "%"
        is IrBinaryOperator.Equals -> "=="
        is IrBinaryOperator.NotEquals -> "!="
        is IrBinaryOperator.Greater -> ">"
        is IrBinaryOperator.GreaterOrEqual -> ">="
        is IrBinaryOperator.Less -> "<"
        is IrBinaryOperator.LessOrEqual -> "<="
        is IdentityEquals -> "==="
        is IdentityNotEquals -> "!=="
        is IrBinaryOperator.And -> "&&"
        is IrBinaryOperator.Or -> "||"
        is IrBinaryOperator.BitwiseAnd -> "and"
        is IrBinaryOperator.BitwiseOr -> "or"
        is IrBinaryOperator.BitwiseXor -> "xor"
        is IrBinaryOperator.ShiftLeft -> "shl"
        is IrBinaryOperator.ShiftRight -> "shr"
        is UnsignedShiftRight -> "ushr"
        is Elvis -> "?:"
        is RangeTo -> ".."
        is In -> "in"
        is NotIn -> "!in"
        is IrBinaryOperatorUnknown -> operator.token
        else -> throw IllegalArgumentException("Unsupported binary operator")
    }
}
