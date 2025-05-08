package com.voxeldev.mcodegen.dsl.language.java.ir

import com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator
import com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator
import com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator

data class IrUnaryOperatorUnknown(
    val operator: String,
) : IrUnaryOperator

data class IrBinaryOperatorUnknown(
    val operator: String,
) : IrBinaryOperator

data class IrAssignmentOperatorUnknown(
    val operator: String,
) : IrAssignmentOperator