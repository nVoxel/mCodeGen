package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions

import com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(KotlinModule, ScenarioScope)
internal fun convertAssignmentOperator(operator: IrAssignmentOperator) : String {
    return when (operator) {
        is IrAssignmentOperator.Assign -> "="
        is IrAssignmentOperator.PlusAssign -> "+="
        is IrAssignmentOperator.MinusAssign -> "-="
        is IrAssignmentOperator.MultiplyAssign -> "*="
        is IrAssignmentOperator.DivideAssign -> "/="
        is IrAssignmentOperator.ModuloAssign -> "%="
        else -> throw IllegalArgumentException("Unsupported assignment operator")
    }
}