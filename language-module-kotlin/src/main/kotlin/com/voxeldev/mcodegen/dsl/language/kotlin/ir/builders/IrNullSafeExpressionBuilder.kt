package com.voxeldev.mcodegen.dsl.language.kotlin.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.builders.IrExpressionBuilder
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IrNullSafeExpression

/**
 * Creates a new [IrNullSafeExpressionBuilder] instance with the given method name.
 */
fun irNullSafeExpression(
    receiver: IrExpression,
    selector: IrExpression,
): IrNullSafeExpressionBuilder = IrNullSafeExpressionBuilder(receiver, selector)

/**
 * Builder class for creating [IrNullSafeExpression] instances.
 */
class IrNullSafeExpressionBuilder(
    private val receiver: IrExpression,
    private val selector: IrExpression,
) : IrExpressionBuilder() {
    fun build(): IrNullSafeExpression {
        val properties = buildExpressionProperties()
        return IrNullSafeExpression(
            receiver = receiver,
            selector = selector,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}