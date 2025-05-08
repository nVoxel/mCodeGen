package com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions

import com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.ir.IrAssignmentOperatorUnknown
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(JavaModule, ScenarioScope)
internal fun convertAssignmentOperator(operator: IrAssignmentOperator): String {
    return when (operator) {
        is IrAssignmentOperator.Assign -> "="
        is IrAssignmentOperator.PlusAssign -> "+="
        is IrAssignmentOperator.MinusAssign -> "-="
        is IrAssignmentOperator.MultiplyAssign -> "*="
        is IrAssignmentOperator.DivideAssign -> "/="
        is IrAssignmentOperator.ModuloAssign -> "%="
        is IrAssignmentOperatorUnknown -> operator.operator
        else -> throw IllegalArgumentException("Unsupported assignment operator")
    }
}