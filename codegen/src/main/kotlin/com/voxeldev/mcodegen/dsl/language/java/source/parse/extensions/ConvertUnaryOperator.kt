package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.ir.IrUnaryOperatorUnknown
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaToken

context(JavaModule, ScenarioScope)
internal fun convertUnaryOperator(operator: PsiJavaToken): IrUnaryOperator = when (operator.text) {
    "!" -> IrUnaryOperator.Not()
    "+" -> IrUnaryOperator.Plus()
    "-" -> IrUnaryOperator.Minus()
    "++" -> IrUnaryOperator.Increment()
    "--" -> IrUnaryOperator.Decrement()
    else -> IrUnaryOperatorUnknown(operator = operator.text)
}