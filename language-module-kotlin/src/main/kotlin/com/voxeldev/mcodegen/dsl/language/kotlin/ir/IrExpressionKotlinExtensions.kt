package com.voxeldev.mcodegen.dsl.language.kotlin.ir

import com.voxeldev.mcodegen.dsl.ir.IrAnnotation
import com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator
import com.voxeldev.mcodegen.dsl.ir.IrBlockStatement
import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrLocation
import com.voxeldev.mcodegen.dsl.ir.IrParameter
import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator

class IdentityEquals : IrBinaryOperator // ===
class IdentityNotEquals : IrBinaryOperator // !==
class UnsignedShiftRight : IrBinaryOperator // >>>

class Elvis : IrBinaryOperator // ?:
class RangeTo : IrBinaryOperator // ..
class In : IrBinaryOperator // in
class NotIn : IrBinaryOperator // !in

class NonNullAssert : IrUnaryOperator // !!

data class IrBinaryOperatorUnknown(
    val token: String,
) : IrBinaryOperator

data class IrUnaryOperatorUnknown(
    val token: String,
) : IrUnaryOperator

/**
 * A lambda expression, represents a function. For example: { println("Hello world") }
 */
data class IrLambdaExpression(
    val parameters: List<IrParameter>,
    val returnType: IrType?,
    val targetInterfaceType: IrType?,
    val body: IrBlockStatement,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression()

/**
 * An expression that wraps other statement (KtExpression) in brackets.
 */
data class IrParenthesizedExpression(
    val body: IrStatement,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression()

/**
 * An expression for null safe access in Kotlin. For example: someNullable?.value
 */
data class IrNullSafeExpression(
    val receiver: IrExpression, // left part
    val selector: IrExpression, // right part
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression()
