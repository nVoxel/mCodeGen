package com.voxeldev.mcodegen.dsl.ir.builders

import com.voxeldev.mcodegen.dsl.ir.IrMethod
import com.voxeldev.mcodegen.dsl.ir.IrMethodBody
import com.voxeldev.mcodegen.dsl.ir.IrParameter
import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrVisibility
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic

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
class IrMethodBuilder(
    private val name: String,
    private val returnType: IrType,
) : IrElementBuilder() {
    private var parameters: MutableList<IrParameter> = mutableListOf()
    private var body: IrMethodBody? = null
    private var visibility: IrVisibility = IrVisibilityPublic(IrStringRepresentation("kotlin", "public"))
    private var isAbstract: Boolean = false
    private var isStatic: Boolean = false
    private var isOverride: Boolean = false
    private var isConstructor: Boolean = false

    fun addParameter(parameter: IrParameter) {
        parameters.add(parameter)
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

    fun isConstructor(isConstructor: Boolean) {
        this.isConstructor = isConstructor
    }

    fun build(): IrMethod {
        return IrMethod(
            name = name,
            returnType = returnType,
            parameters = parameters,
            body = body,
            visibility = visibility,
            isAbstract = isAbstract,
            isStatic = isStatic,
            isOverride = isOverride,
            isConstructor = isConstructor,
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

    fun build(): IrParameter {
        return IrParameter(
            name = name,
            type = type,
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