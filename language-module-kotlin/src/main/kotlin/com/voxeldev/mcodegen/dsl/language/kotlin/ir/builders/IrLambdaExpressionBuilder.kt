package com.voxeldev.mcodegen.dsl.language.kotlin.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrBlockStatement
import com.voxeldev.mcodegen.dsl.ir.IrParameter
import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.builders.IrExpressionBuilder
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IrLambdaExpression

/**
 * Creates a new [IrLambdaExpressionBuilder] instance with the given method name.
 */
fun irLambdaExpression(): IrLambdaExpressionBuilder =
    IrLambdaExpressionBuilder()

/**
 * Builder class for creating [IrLambdaExpression] instances.
 */
class IrLambdaExpressionBuilder() : IrExpressionBuilder() {
    private val parameters: MutableList<IrParameter> = mutableListOf()
    private var returnType: IrType? = null
    private var targetInterfaceType: IrType? = null
    private var body: IrBlockStatement? = null

    fun addParameter(parameter: IrParameter) {
        parameters.add(parameter)
    }

    fun returnType(type: IrType) {
        returnType = type
    }

    fun targetInterfaceType(type: IrType) {
        targetInterfaceType = type
    }

    fun body(statement: IrBlockStatement) {
        body = statement
    }

    fun build(): IrLambdaExpression {
        val properties = buildExpressionProperties()
        return IrLambdaExpression(
            parameters = parameters,
            returnType = requireNotNull(returnType),
            targetInterfaceType = targetInterfaceType,
            body = requireNotNull(body),
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}