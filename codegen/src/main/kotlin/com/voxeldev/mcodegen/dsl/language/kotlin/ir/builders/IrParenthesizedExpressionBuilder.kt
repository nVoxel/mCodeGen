package com.voxeldev.mcodegen.dsl.language.kotlin.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.builders.IrExpressionBuilder
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IrParenthesizedExpression

/**
 * Creates a new [IrParenthesizedExpressionBuilder] instance with the given method name.
 */
fun irParenthesizedExpression(body: IrStatement): IrParenthesizedExpressionBuilder =
    IrParenthesizedExpressionBuilder(body)

/**
 * Builder class for creating [IrParenthesizedExpression] instances.
 */
class IrParenthesizedExpressionBuilder(private val body: IrStatement) : IrExpressionBuilder() {
    fun build(): IrParenthesizedExpression {
        val properties = buildExpressionProperties()
        return IrParenthesizedExpression(
            body = body,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}