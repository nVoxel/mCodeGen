package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.language.java.ir.IrBinaryOperatorUnknown
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaToken

context(JavaModule, ScenarioScope)
internal fun convertBinaryOperator(operator: PsiJavaToken): IrBinaryOperator = when (operator.text) {
    "+" -> IrBinaryOperator.Plus()
    "-" -> IrBinaryOperator.Minus()
    "*" -> IrBinaryOperator.Multiply()
    "/" -> IrBinaryOperator.Divide()
    "%" -> IrBinaryOperator.Modulo()
    "==" -> IrBinaryOperator.Equals()
    "!=" -> IrBinaryOperator.NotEquals()
    ">" -> IrBinaryOperator.Greater()
    ">=" -> IrBinaryOperator.GreaterOrEqual()
    "<" -> IrBinaryOperator.Less()
    "<=" -> IrBinaryOperator.LessOrEqual()
    "&&" -> IrBinaryOperator.And()
    "||" -> IrBinaryOperator.Or()
    "&" -> IrBinaryOperator.BitwiseAnd()
    "|" -> IrBinaryOperator.BitwiseOr()
    "^" -> IrBinaryOperator.BitwiseXor()
    "<<" -> IrBinaryOperator.ShiftLeft()
    ">>" -> IrBinaryOperator.ShiftRight()
    else -> IrBinaryOperatorUnknown(operator = operator.text)
}