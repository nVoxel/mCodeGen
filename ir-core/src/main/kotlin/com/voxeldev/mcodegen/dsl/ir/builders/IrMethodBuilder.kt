package com.voxeldev.mcodegen.dsl.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrConstructor
import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrMethod
import com.voxeldev.mcodegen.dsl.ir.IrCallable
import com.voxeldev.mcodegen.dsl.ir.IrMethodBody
import com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.IrParameter
import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrTypeParameter
import com.voxeldev.mcodegen.dsl.ir.IrVisibility

abstract class IrCallableBuilder(
    protected val name: String,
    protected val returnType: IrType,
): IrElementBuilder() {
    protected var parameters: MutableList<IrParameter> = mutableListOf()
    protected var typeParameters: MutableList<IrTypeParameter> = mutableListOf()
    protected var body: IrMethodBody? = null
    protected var visibility: IrVisibility? = null
    protected var isAbstract: Boolean = false
    protected var isStatic: Boolean = false
    protected var isOverride: Boolean = false

    fun addParameter(parameter: IrParameter) {
        parameters.add(parameter)
    }

    fun addTypeParameter(typeParameter: IrTypeParameter) {
        typeParameters.add(typeParameter)
    }

    fun body(body: IrMethodBody?) {
        this.body = body
    }

    fun visibility(visibility: IrVisibility) {
        this.visibility = visibility
    }

    fun isAbstract(isAbstract: Boolean) {
        this.isAbstract = isAbstract
    }

    fun isStatic(isStatic: Boolean) {
        this.isStatic = isStatic
    }

    fun isOverride(isOverride: Boolean) {
        this.isOverride = isOverride
    }

    abstract fun build(): IrCallable
}

/**
 * Creates a new [IrMethodBuilder] instance with the given method name and return type.
 */
fun irMethod(
    name: String,
    returnType: IrType,
): IrMethodBuilder = IrMethodBuilder(
    name = name,
    returnType = returnType,
)

/**
 * Builder class for creating [IrMethod] instances.
 */
open class IrMethodBuilder(
    name: String,
    returnType: IrType,
) : IrCallableBuilder(name, returnType) {

    override fun build(): IrMethod {
        return IrMethod(
            name = name,
            returnType = returnType,
            parameters = parameters,
            typeParameters = typeParameters,
            body = body,
            visibility = requireNotNull(visibility),
            isAbstract = isAbstract,
            isStatic = isStatic,
            isOverride = isOverride,
            location = location,
            annotations = annotations,
            languageProperties = languageProperties,
        )
    }
}

/**
 * Creates a new [IrConstructorBuilder] instance with the given method name and return type.
 */
fun irConstructor(
    name: String,
    returnType: IrType,
): IrConstructorBuilder = IrConstructorBuilder(
    name = name,
    returnType = returnType,
)

/**
 * Builder class for creating [IrConstructor] instances.
 */
class IrConstructorBuilder(
    name: String,
    returnType: IrType,
) : IrCallableBuilder(name, returnType) {
    private var otherConstructorCall: IrMethodCallExpression? = null

    fun otherConstructorCall(callExpression: IrMethodCallExpression) {
        otherConstructorCall = callExpression
    }

    override fun build(): IrConstructor {
        return IrConstructor(
            otherConstructorCall = otherConstructorCall,
            name = name,
            returnType = returnType,
            parameters = parameters,
            typeParameters = typeParameters,
            body = body,
            visibility = requireNotNull(visibility),
            isAbstract = isAbstract,
            isStatic = isStatic,
            isOverride = isOverride,
            location = location,
            annotations = annotations,
            languageProperties = languageProperties,
        )
    }
}

/**
 * Creates a new [IrParameterBuilder] instance with the given parameter name and type.
 */
fun irParameter(
    name: String,
    type: IrType,
): IrParameterBuilder = IrParameterBuilder(
    name = name,
    type = type,
)

/**
 * Builder class for creating [IrParameter] instances.
 */
class IrParameterBuilder(
    private val name: String,
    private val type: IrType,
) : IrElementBuilder() {
    private var defaultValue: IrExpression? = null

    fun defaultValue(expression: IrExpression) {
        defaultValue = expression
    }

    fun build(): IrParameter {
        return IrParameter(
            name = name,
            type = type,
            defaultValue = defaultValue,
            location = location,
            annotations = annotations,
            languageProperties = languageProperties,
        )
    }
}

/**
 * Creates a new [IrMethodBodyBuilder] instance.
 */
fun irMethodBody(): IrMethodBodyBuilder = IrMethodBodyBuilder()

/**
 * Builder class for creating [IrMethodBody] instances.
 */
class IrMethodBodyBuilder : IrElementBuilder() {
    private var statements: MutableList<IrStatement> = mutableListOf()

    fun addStatement(statement: IrStatement) {
        statements.add(statement)
    }

    fun build(): IrMethodBody {
        return IrMethodBody(
            statements = statements,
            location = location,
            annotations = annotations,
            languageProperties = languageProperties,
        )
    }
} 