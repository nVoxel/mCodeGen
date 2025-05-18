package com.voxeldev.mcodegen.dsl.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrAnnotation
import com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression
import com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression
import com.voxeldev.mcodegen.dsl.ir.IrCastExpression
import com.voxeldev.mcodegen.dsl.ir.IrEmptyExpression
import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrExpressionUnknown
import com.voxeldev.mcodegen.dsl.ir.IrIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.IrLiteralExpression
import com.voxeldev.mcodegen.dsl.ir.IrLocation
import com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.IrObjectCreationExpression
import com.voxeldev.mcodegen.dsl.ir.IrPropertyAccessExpression
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrTernaryExpression
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrTypeCheckExpression
import com.voxeldev.mcodegen.dsl.ir.IrTypeReferenceIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression

/**
 * Abstract builder class for creating [IrExpression] instances.
 * Provides common functionality for all expression builders.
 */
abstract class IrExpressionBuilder : IrElementBuilder() {
    protected var stringRepresentation: MutableList<IrStringRepresentation> = mutableListOf()

    fun addStringRepresentation(representation: IrStringRepresentation) {
        stringRepresentation.add(representation)
    }

    protected fun buildExpressionProperties(): IrExpressionProperties {
        return IrExpressionProperties(
            stringRepresentation = stringRepresentation,
            location = location,
            annotations = annotations,
            languageProperties = languageProperties,
        )
    }

    protected data class IrExpressionProperties(
        val stringRepresentation: List<IrStringRepresentation>,
        val location: IrLocation?,
        val annotations: List<IrAnnotation>,
        val languageProperties: Map<String, Any>,
    )
}

/**
 * Creates a new [IrEmptyExpressionBuilder] instance with the given value.
 */
fun irEmptyExpression(): IrEmptyExpressionBuilder = IrEmptyExpressionBuilder()

/**
 * Builder class for creating [IrEmptyExpression] instances.
 */
class IrEmptyExpressionBuilder() : IrExpressionBuilder() {
    fun build(): IrEmptyExpression {
        val properties = buildExpressionProperties()
        return IrEmptyExpression(
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrLiteralExpressionBuilder] instance with the given value.
 */
fun irLiteralExpression(value: String): IrLiteralExpressionBuilder = IrLiteralExpressionBuilder(value)

/**
 * Builder class for creating [IrLiteralExpression] instances.
 */
class IrLiteralExpressionBuilder(private val value: String) : IrExpressionBuilder() {
    fun build(): IrLiteralExpression {
        val properties = buildExpressionProperties()
        return IrLiteralExpression(
            value = value,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrIdentifierExpressionBuilder] instance with the given name.
 */
fun irIdentifierExpression(selector: IrExpression): IrIdentifierExpressionBuilder =
    IrIdentifierExpressionBuilder(selector)

/**
 * Builder class for creating [IrIdentifierExpression] instances.
 */
class IrIdentifierExpressionBuilder(private val selector: IrExpression) : IrExpressionBuilder() {
    private var qualifier: IrExpression? = null

    fun qualifier(expression: IrExpression) {
        qualifier = expression
    }

    fun build(): IrIdentifierExpression {
        val properties = buildExpressionProperties()
        return IrIdentifierExpression(
            qualifier = qualifier,
            selector = selector,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrTypeReferenceIdentifierExpressionBuilder] instance with the given name.
 */
fun irTypeReferenceIdentifierExpression(referencedType: IrType): IrTypeReferenceIdentifierExpressionBuilder =
    IrTypeReferenceIdentifierExpressionBuilder(referencedType = referencedType)

/**
 * Builder class for creating [IrTypeReferenceIdentifierExpression] instances.
 */
class IrTypeReferenceIdentifierExpressionBuilder(
    private val referencedType: IrType,
) : IrExpressionBuilder() {
    fun build(): IrTypeReferenceIdentifierExpression {
        val properties = buildExpressionProperties()
        return IrTypeReferenceIdentifierExpression(
            referencedType = referencedType,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrPropertyAccessExpressionBuilder] instance with the given property name.
 */
fun irPropertyAccessExpression(propertyName: String): IrPropertyAccessExpressionBuilder =
    IrPropertyAccessExpressionBuilder(propertyName)

/**
 * Builder class for creating [IrPropertyAccessExpression] instances.
 */
class IrPropertyAccessExpressionBuilder(private val propertyName: String) : IrExpressionBuilder() {
    private var receiver: IrExpression? = null

    fun receiver(receiver: IrExpression?) {
        this.receiver = receiver
    }

    fun build(): IrPropertyAccessExpression {
        val properties = buildExpressionProperties()
        return IrPropertyAccessExpression(
            receiver = receiver,
            propertyName = propertyName,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrMethodCallExpressionBuilder] instance with the given method name.
 */
fun irMethodCallExpression(methodName: String): IrMethodCallExpressionBuilder =
    IrMethodCallExpressionBuilder(methodName)

/**
 * Builder class for creating [IrMethodCallExpression] instances.
 */
class IrMethodCallExpressionBuilder(private val methodName: String) : IrExpressionBuilder() {
    private var receiver: IrExpression? = null
    private var valueArguments: MutableList<IrExpression> = mutableListOf()
    private var typeArguments: MutableList<IrType> = mutableListOf()
    private var irMethodCallKind : IrMethodCallExpression.IrMethodCallKind = IrMethodCallExpression.IrDefaultMethodCallKind

    fun receiver(receiver: IrExpression?) {
        this.receiver = receiver
    }

    fun addValueArgument(argument: IrExpression) {
        valueArguments.add(argument)
    }

    fun addTypeArgument(type: IrType) {
        typeArguments.add(type)
    }

    fun methodCallKind(callKind: IrMethodCallExpression.IrMethodCallKind) {
        irMethodCallKind = callKind
    }

    fun build(): IrMethodCallExpression {
        val properties = buildExpressionProperties()
        return IrMethodCallExpression(
            receiver = receiver,
            methodName = methodName,
            valueArguments = valueArguments,
            typeArguments = typeArguments,
            irMethodCallKind = irMethodCallKind,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrObjectCreationExpressionBuilder] instance with the given class name.
 */
fun irObjectCreationExpression(className: String): IrObjectCreationExpressionBuilder =
    IrObjectCreationExpressionBuilder(className)

/**
 * Builder class for creating [IrObjectCreationExpression] instances.
 */
class IrObjectCreationExpressionBuilder(private val className: String) : IrExpressionBuilder() {
    private var constructorArgs: MutableList<IrExpression> = mutableListOf()

    fun addConstructorArg(arg: IrExpression) {
        constructorArgs.add(arg)
    }

    fun build(): IrObjectCreationExpression {
        val properties = buildExpressionProperties()
        return IrObjectCreationExpression(
            className = className,
            constructorArgs = constructorArgs,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrBinaryExpressionBuilder] instance with the given left operand and operator.
 */
fun irBinaryExpression(
    left: IrExpression,
    operator: IrBinaryExpression.IrBinaryOperator,
    right: IrExpression,
): IrBinaryExpressionBuilder = IrBinaryExpressionBuilder(
    left = left,
    operator = operator,
    right = right,
)

/**
 * Builder class for creating [IrBinaryExpression] instances.
 */
class IrBinaryExpressionBuilder(
    private val left: IrExpression,
    private val operator: IrBinaryExpression.IrBinaryOperator,
    private val right: IrExpression,
) : IrExpressionBuilder() {

    fun build(): IrBinaryExpression {
        val properties = buildExpressionProperties()
        return IrBinaryExpression(
            left = left,
            operator = operator,
            right = right,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrUnaryExpressionBuilder] instance with the given operator.
 */
fun irUnaryExpression(
    operator: IrUnaryExpression.IrUnaryOperator,
    operand: IrExpression,
    isPrefix: Boolean,
): IrUnaryExpressionBuilder = IrUnaryExpressionBuilder(
    operator = operator,
    operand = operand,
    isPrefix = isPrefix,
)

/**
 * Builder class for creating [IrUnaryExpression] instances.
 */
class IrUnaryExpressionBuilder(
    private val operator: IrUnaryExpression.IrUnaryOperator,
    private val operand: IrExpression,
    private val isPrefix: Boolean,
) : IrExpressionBuilder() {

    fun build(): IrUnaryExpression {
        val properties = buildExpressionProperties()
        return IrUnaryExpression(
            operator = operator,
            operand = operand,
            isPrefix = isPrefix,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrAssignmentExpressionBuilder] instance with the given target and operator.
 */
fun irAssignmentExpression(
    target: IrExpression,
    operator: IrAssignmentExpression.IrAssignmentOperator,
    value: IrExpression,
): IrAssignmentExpressionBuilder = IrAssignmentExpressionBuilder(
    target = target,
    operator = operator,
    value = value,
)

/**
 * Builder class for creating [IrAssignmentExpression] instances.
 */
class IrAssignmentExpressionBuilder(
    private val target: IrExpression,
    private val operator: IrAssignmentExpression.IrAssignmentOperator,
    private val value: IrExpression,
) : IrExpressionBuilder() {

    fun build(): IrAssignmentExpression {
        val properties = buildExpressionProperties()
        return IrAssignmentExpression(
            target = target,
            operator = operator,
            value = value,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrTernaryExpressionBuilder] instance with the given condition.
 */
fun irTernaryExpression(
    condition: IrExpression,
    ifTrue: IrExpression,
    ifFalse: IrExpression,
): IrTernaryExpressionBuilder = IrTernaryExpressionBuilder(
    condition = condition,
    ifTrue = ifTrue,
    ifFalse = ifFalse,
)

/**
 * Builder class for creating [IrTernaryExpression] instances.
 */
class IrTernaryExpressionBuilder(
    private val condition: IrExpression,
    private val ifTrue: IrExpression,
    private val ifFalse: IrExpression,
) : IrExpressionBuilder() {

    fun build(): IrTernaryExpression {
        val properties = buildExpressionProperties()
        return IrTernaryExpression(
            condition = condition,
            ifTrue = ifTrue,
            ifFalse = ifFalse,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrCastExpressionBuilder] instance with the given expression and target type.
 */
fun irCastExpression(
    expression: IrExpression,
    targetType: IrType,
): IrCastExpressionBuilder = IrCastExpressionBuilder(
    expression = expression,
    targetType = targetType,
)

/**
 * Builder class for creating [IrCastExpression] instances.
 */
class IrCastExpressionBuilder(
    private val expression: IrExpression,
    private val targetType: IrType,
) : IrExpressionBuilder() {

    fun build(): IrCastExpression {
        val properties = buildExpressionProperties()
        return IrCastExpression(
            expression = expression,
            targetType = targetType,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrTypeCheckExpressionBuilder] instance with the given expression and check type.
 */
fun irTypeCheckExpression(
    expression: IrExpression,
    checkType: IrType,
): IrTypeCheckExpressionBuilder = IrTypeCheckExpressionBuilder(
    expression = expression,
    checkType = checkType,
)

/**
 * Builder class for creating [IrTypeCheckExpression] instances.
 */
class IrTypeCheckExpressionBuilder(
    private val expression: IrExpression,
    private val checkType: IrType,
) : IrExpressionBuilder() {

    fun build(): IrTypeCheckExpression {
        val properties = buildExpressionProperties()
        return IrTypeCheckExpression(
            expression = expression,
            checkType = checkType,
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}

/**
 * Creates a new [IrExpressionUnknownBuilder] instance.
 */
fun irExpressionUnknown(): IrExpressionUnknownBuilder = IrExpressionUnknownBuilder()

/**
 * Builder class for creating [IrExpressionUnknown] instances.
 */
class IrExpressionUnknownBuilder : IrExpressionBuilder() {
    fun build(): IrExpressionUnknown {
        val properties = buildExpressionProperties()
        return IrExpressionUnknown(
            stringRepresentation = properties.stringRepresentation,
            location = properties.location,
            annotations = properties.annotations,
            languageProperties = properties.languageProperties,
        )
    }
}