package com.voxeldev.mcodegen.dsl.ir

/**
 * Base interface for all IR expressions.
 */
open class IrExpression(
    open val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrElement

/**
 * Represents an integer, float, string, boolean, char, etc.
 */
data class IrLiteralExpression(
    val value: Any,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * An identifier referencing a variable, parameter, constant, class name, etc.
 * e.g., `myVariable` or `System` or `MyClass`
 */
data class IrIdentifierExpression(
    val name: Any,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * A property/field or nested reference (object.property, or object.property.inner).
 */
data class IrPropertyAccessExpression(
    val receiver: IrExpression?,
    val propertyName: String,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)


/**
 * A method or function call, optionally with a receiver (e.g. `obj.foo(arg1, arg2)`, or `foo()`)
 */
data class IrMethodCallExpression(
    val receiver: IrExpression?, // null if it's a top-level function call
    val methodName: String,
    val arguments: List<IrExpression>,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * An object creation expression (like `new MyClass(...)` in Java or `MyClass(...)` in Kotlin).
 */
data class IrObjectCreationExpression(
    val className: String,
    val constructorArgs: List<IrExpression>,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * A binary operation (left op right), e.g. `a + b`, `x == y`, etc.
 */
data class IrBinaryExpression(
    val left: IrExpression,
    val operator: IrBinaryOperator,
    val right: IrExpression,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
) {
    interface IrBinaryOperator {
        class Plus : IrBinaryOperator // +
        class Minus : IrBinaryOperator // -
        class Multiply : IrBinaryOperator // *
        class Divide : IrBinaryOperator // /
        class Modulo : IrBinaryOperator // %
        class Equals : IrBinaryOperator // ==
        class NotEquals : IrBinaryOperator // !=
        class Greater : IrBinaryOperator // >
        class GreaterOrEqual : IrBinaryOperator // >=
        class Less : IrBinaryOperator // <
        class LessOrEqual : IrBinaryOperator // <=
        class And : IrBinaryOperator // &&
        class Or : IrBinaryOperator // ||
        class BitwiseAnd : IrBinaryOperator // & and
        class BitwiseOr : IrBinaryOperator // | or
        class BitwiseXor : IrBinaryOperator // ^ xor
        class ShiftLeft : IrBinaryOperator // << shl
        class ShiftRight : IrBinaryOperator // >> shr
    }
}

/**
 * A unary operation, e.g. `-x`, `!flag`, etc.
 */
data class IrUnaryExpression(
    val operator: IrUnaryOperator,
    val operand: IrExpression,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
) {
    interface IrUnaryOperator {
        class Not : IrUnaryOperator // !bool
        class Plus : IrUnaryOperator // +num
        class Minus : IrUnaryOperator // -num
        class PreIncrement : IrUnaryOperator // ++num
        class PreDecrement : IrUnaryOperator // --num
        class PostIncrement : IrUnaryOperator // num++
        class PostDecrement : IrUnaryOperator // num--
    }
}

/**
 * Assignment expression that represents assigning value to target, e.g. `a = b`.
 */
data class IrAssignmentExpression(
    val target: IrExpression, // Typically an IdentifierExpression or PropertyAccessExpression
    val operator: IrAssignmentOperator,
    val value: IrExpression,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
) {
    interface IrAssignmentOperator {
        class Assign : IrAssignmentOperator // =
        class PlusAssign : IrAssignmentOperator // +=
        class MinusAssign : IrAssignmentOperator // -=
        class MultiplyAssign : IrAssignmentOperator // *=
        class DivideAssign : IrAssignmentOperator // /=
        class ModuloAssign : IrAssignmentOperator // %=
    }
}

/**
 * Ternary/conditional expression: condition ? ifTrue : ifFalse
 */
data class IrTernaryExpression(
    val condition: IrExpression,
    val ifTrue: IrExpression,
    val ifFalse: IrExpression,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * A cast expression, e.g. `(Type) expr` in Java, or `expr as Type` in Kotlin.
 */
data class IrCastExpression(
    val expression: IrExpression,
    val targetType: String,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)

/**
 * A type-check expression (Java `instanceof`, Kotlin `is`, Swift `is`).
 */
data class IrTypeCheckExpression(
    val expression: IrExpression,
    val checkType: String,
    override val stringRepresentation: List<IrStringRepresentation>,
    override val location: IrLocation?,
    override val annotations: List<IrAnnotation>,
    override val languageProperties: Map<String, Any>,
) : IrExpression(
    stringRepresentation = stringRepresentation,
    location = location,
    annotations = annotations,
    languageProperties = languageProperties,
)
