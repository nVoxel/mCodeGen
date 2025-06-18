package com.voxeldev.mcodegen.dsl.ir

import com.voxeldev.mcodegen.dsl.ir.utils.MapStringAnySerializer
import kotlinx.serialization.Serializable

/**
 * Base interface for all IR expressions.
 */
@Serializable
abstract class IrExpression: IrElement {
    abstract val stringRepresentation: List<IrStringRepresentation>
    abstract override val location: IrLocation?
    abstract override val annotations: List<IrAnnotation>
    @Serializable(with = MapStringAnySerializer::class)
    abstract override val languageProperties: Map<String, Any>
}

/**
 * Represents an empty expression, nothing. Can be used as a fallback.
 * If met during statement construction, the statement becomes [IrEmptyStatement] as well.
 * @see IrExpressionUnknown
 */
@Serializable
class IrEmptyExpression(
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrExpression()

/**
 * Represents an integer, float, string, boolean, char, etc.
 */
@Serializable
data class IrLiteralExpression(
    val value: String,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrExpression()

/**
 * An identifier referencing a variable, parameter, constant, etc.
 * Must reference any type that should NOT be dynamically imported.
 * e.g., `myVariable` or `System` or `MyClass`
 */
@Serializable
data class IrIdentifierExpression(
    val qualifier: IrExpression? = null, // right part
    val selector: IrExpression, // left part
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrExpression()

/**
 * An identifier referencing a variable, parameter, constant, class name, etc.
 * Can reference a type that should be dynamically imported.
 * e.g., `myVariable` or `System` or `MyClass`
 */
@Serializable
data class IrTypeReferenceIdentifierExpression(
    val referencedType: IrType, // selector, left part
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrExpression()

/**
 * A property/field or nested reference (object.property, or object.property.inner).
 */
@Serializable
data class IrPropertyAccessExpression(
    val receiver: IrExpression? = null,
    val propertyName: String,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrExpression()


/**
 * A method or function call, optionally with a receiver (e.g. `obj.foo(arg1, arg2)`, or `foo()`)
 */
@Serializable
data class IrMethodCallExpression(
    val receiver: IrExpression? = null, // null if it's a top-level function call
    val methodName: String,
    val valueArguments: List<IrExpression>,
    val typeArguments: List<IrType>, // contains only explicit (written in the code) arguments
    val irMethodCallKind: IrMethodCallKind,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrExpression() {
    interface IrMethodCallKind

    @Serializable data object IrDefaultMethodCallKind : IrMethodCallKind
    @Serializable data object IrThisMethodCallKind : IrMethodCallKind
    @Serializable data object IrSuperMethodCallKind : IrMethodCallKind
}

/**
 * An object creation expression (like `new MyClass(...)` in Java or `MyClass(...)` in Kotlin).
 */
@Serializable
data class IrObjectCreationExpression(
    val className: String,
    val constructorArgs: List<IrExpression>,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrExpression()

/**
 * A binary operation (left op right), e.g. `a + b`, `x == y`, etc.
 */
@Serializable
data class IrBinaryExpression(
    val left: IrExpression,
    val operator: IrBinaryOperator,
    val right: IrExpression,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrExpression() {
    interface IrBinaryOperator {
        @Serializable class Plus : IrBinaryOperator // +
        @Serializable class Minus : IrBinaryOperator // -
        @Serializable class Multiply : IrBinaryOperator // *
        @Serializable class Divide : IrBinaryOperator // /
        @Serializable class Modulo : IrBinaryOperator // %
        @Serializable class Equals : IrBinaryOperator // ==
        @Serializable class NotEquals : IrBinaryOperator // !=
        @Serializable class Greater : IrBinaryOperator // >
        @Serializable class GreaterOrEqual : IrBinaryOperator // >=
        @Serializable class Less : IrBinaryOperator // <
        @Serializable class LessOrEqual : IrBinaryOperator // <=
        @Serializable class And : IrBinaryOperator // &&
        @Serializable class Or : IrBinaryOperator // ||
        @Serializable class BitwiseAnd : IrBinaryOperator // & and
        @Serializable class BitwiseOr : IrBinaryOperator // | or
        @Serializable class BitwiseXor : IrBinaryOperator // ^ xor
        @Serializable class ShiftLeft : IrBinaryOperator // << shl
        @Serializable class ShiftRight : IrBinaryOperator // >> shr
    }
}

/**
 * A unary operation, e.g. `-x`, `!flag`, etc.
 */
@Serializable
data class IrUnaryExpression(
    val operator: IrUnaryOperator,
    val operand: IrExpression,
    val isPrefix: Boolean,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrExpression() {
    interface IrUnaryOperator {
        @Serializable class Not : IrUnaryOperator // !bool
        @Serializable class Plus : IrUnaryOperator // +num
        @Serializable class Minus : IrUnaryOperator // -num
        @Serializable class Increment : IrUnaryOperator // num++ or ++num
        @Serializable class Decrement : IrUnaryOperator // num-- or ++num
    }
}

/**
 * Assignment expression that represents assigning value to target, e.g. `a = b`.
 */
@Serializable
data class IrAssignmentExpression(
    val target: IrExpression, // Typically an IdentifierExpression or PropertyAccessExpression
    val operator: IrAssignmentOperator,
    val value: IrExpression,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrExpression() {
    interface IrAssignmentOperator {
        @Serializable class Assign : IrAssignmentOperator // =
        @Serializable class PlusAssign : IrAssignmentOperator // +=
        @Serializable class MinusAssign : IrAssignmentOperator // -=
        @Serializable class MultiplyAssign : IrAssignmentOperator // *=
        @Serializable class DivideAssign : IrAssignmentOperator // /=
        @Serializable class ModuloAssign : IrAssignmentOperator // %=
    }
}

/**
 * Ternary/conditional expression: condition ? ifTrue : ifFalse
 */
@Serializable
data class IrTernaryExpression(
    val condition: IrExpression,
    val ifTrue: IrExpression,
    val ifFalse: IrExpression,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrExpression()

/**
 * A cast expression, e.g. `(Type) expr` in Java, or `expr as Type` in Kotlin.
 */
@Serializable
data class IrCastExpression(
    val expression: IrExpression,
    val targetType: IrType,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrExpression()

/**
 * A type-check expression (Java `instanceof`, Kotlin `is`, Swift `is`).
 */
@Serializable
data class IrTypeCheckExpression(
    val expression: IrExpression,
    val checkType: IrType,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrExpression()

/**
 * Fallback when the frontend was unable to convert an expression.
 */
@Serializable
data class IrExpressionUnknown(
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation>,
    @Serializable(with = MapStringAnySerializer::class)
    override val languageProperties: Map<String, Any>,
) : IrExpression()
