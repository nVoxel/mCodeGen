package com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions

import com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.ir.IrBinaryOperatorUnknown
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(JavaModule, ScenarioScope)
internal fun convertBinaryOperator(operator: IrBinaryOperator): String {
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
        is IrBinaryOperator.And -> "&&"
        is IrBinaryOperator.Or -> "||"
        is IrBinaryOperator.BitwiseAnd -> "&"
        is IrBinaryOperator.BitwiseOr -> "|"
        is IrBinaryOperator.BitwiseXor -> "^"
        is IrBinaryOperator.ShiftLeft -> "<<"
        is IrBinaryOperator.ShiftRight -> ">>"
        is IrBinaryOperatorUnknown -> operator.operator
        else -> throw IllegalArgumentException("Unsupported binary operator")
    }
}