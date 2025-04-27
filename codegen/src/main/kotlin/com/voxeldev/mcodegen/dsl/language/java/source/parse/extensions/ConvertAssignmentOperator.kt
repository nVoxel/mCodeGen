package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.ir.IrAssignmentOperatorUnknown
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaToken

context(JavaModule, ScenarioScope)
internal fun convertAssignmentOperator(operator: PsiJavaToken): IrAssignmentOperator = when (operator.text) {
    "=" -> IrAssignmentOperator.Assign()
    "+=" -> IrAssignmentOperator.PlusAssign()
    "-=" -> IrAssignmentOperator.MinusAssign()
    "*=" -> IrAssignmentOperator.MultiplyAssign()
    "/=" -> IrAssignmentOperator.DivideAssign()
    "%=" -> IrAssignmentOperator.ModuloAssign()
    else -> IrAssignmentOperatorUnknown(operator = operator.text)
}