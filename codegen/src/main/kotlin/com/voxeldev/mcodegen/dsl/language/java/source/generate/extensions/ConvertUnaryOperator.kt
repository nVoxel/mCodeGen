package com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions

import com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.ir.IrUnaryOperatorUnknown
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(JavaModule, ScenarioScope)
internal fun convertUnaryOperator(operator: IrUnaryOperator): String {
    return when (operator) {
        is IrUnaryOperator.Not -> "!"
        is IrUnaryOperator.Plus -> "+"
        is IrUnaryOperator.Minus -> "-"
        is IrUnaryOperator.Increment -> "++"
        is IrUnaryOperator.Decrement -> "--"
        is IrUnaryOperatorUnknown -> operator.operator
        else -> throw IllegalArgumentException("Unsupported unary operator")
    }
}