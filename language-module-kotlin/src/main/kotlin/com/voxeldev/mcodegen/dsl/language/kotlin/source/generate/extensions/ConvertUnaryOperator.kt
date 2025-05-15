package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions

import com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IrUnaryOperatorUnknown
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.NonNullAssert
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(KotlinModule, ScenarioScope)
internal fun convertUnaryOperator(operator: IrUnaryOperator) : String {
    return when (operator) {
        is IrUnaryOperator.Plus -> "+"
        is IrUnaryOperator.Minus -> "-"
        is IrUnaryOperator.Not -> "!"
        is IrUnaryOperator.Increment -> "++"
        is IrUnaryOperator.Decrement -> "--"
        is NonNullAssert -> "!!"
        is IrUnaryOperatorUnknown -> operator.token
        else -> throw IllegalArgumentException("Unsupported unary operator")
    }
}