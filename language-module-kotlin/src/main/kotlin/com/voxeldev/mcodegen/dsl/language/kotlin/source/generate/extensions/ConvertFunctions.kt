package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.voxeldev.mcodegen.dsl.ir.IrConstructor
import com.voxeldev.mcodegen.dsl.ir.IrMethod
import com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.IrParameter
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityInternal
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_PRIMARY_CTOR
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_PRIMARY_CTOR_PARAMETER_VISIBILITY
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_PRIMARY_CTOR_VAL_OR_VAR_KEYWORD
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.lexer.KtTokens

context(KotlinModule, ScenarioScope)
internal fun convertFunctions(
    irMethods: List<IrMethod>,
    poetClassBuilder: TypeSpec.Builder,
) {
    irMethods.forEach { irMethod ->
        if (irMethod is IrConstructor && irMethod.languageProperties[KT_PRIMARY_CTOR] == true) {
            convertPrimaryConstructor(irMethod, poetClassBuilder)
        } else {
            poetClassBuilder.addFunction(convertFunction(irMethod))
        }
    }
}

context(KotlinModule, ScenarioScope)
private fun convertFunction(
    irMethod: IrMethod,
) : FunSpec {
    val poetFun = if (irMethod is IrConstructor) {
        FunSpec.constructorBuilder()
    } else {
        FunSpec.builder(irMethod.name).apply {
            returns(convertType(irMethod.returnType))
        }
    }

    poetFun.apply {
        val modifiers = getModifiers(irMethod)
        addModifiers(modifiers)

        irMethod.parameters.forEach { parameter ->
            val poetParameter = ParameterSpec.builder(parameter.name, convertType(parameter.type)).apply {
                parameter.annotations.forEach { irAnnotation ->
                    addAnnotation(convertAnnotation(irAnnotation))
                }

                parameter.defaultValue?.let { defaultValue ->
                    defaultValue(convertExpression(defaultValue))
                }
            }.build()

            addParameter(poetParameter)
        }

        irMethod.typeParameters.forEach { typeParameter ->
            addTypeVariable(convertTypeParameter(typeParameter))
        }

        val otherConstructorCall = (irMethod as? IrConstructor)?.otherConstructorCall
        if (otherConstructorCall != null) {
            val arguments = otherConstructorCall.arguments.map { argument -> convertExpression(argument) }

            when (otherConstructorCall.irMethodCallKind) {
                IrMethodCallExpression.IrSuperMethodCallKind -> callSuperConstructor(arguments)
                IrMethodCallExpression.IrThisMethodCallKind -> callThisConstructor(arguments)
                else -> throw IllegalArgumentException("Got unknown constructor call type")
            }
        }

        irMethod.body?.let { irMethodBody ->
            val bodyCodeBlock = CodeBlock.builder()
            irMethodBody.statements.forEach { irBodyStatement ->
                bodyCodeBlock.add(convertStatement(irBodyStatement))
            }
            addCode(bodyCodeBlock.build())
        }
    }

    return poetFun.build()
}

context(KotlinModule, ScenarioScope)
private fun convertPrimaryConstructor(
    irConstructor: IrConstructor,
    poetClassBuilder: TypeSpec.Builder,
) {
    if (irConstructor.parameters.isNotEmpty()) {
        val poetConstructor = FunSpec.constructorBuilder()

        irConstructor.parameters.forEach { parameter ->
            val parameterType = convertType(parameter.type)

            val fieldModifiers = getPrimaryConstructorFieldModifiers(parameter)

            val poetParameter = ParameterSpec.builder(parameter.name, parameterType).apply {
                addModifiers(fieldModifiers)

                parameter.annotations.forEach { irAnnotation ->
                    addAnnotation(convertAnnotation(irAnnotation))
                }

                parameter.defaultValue?.let { defaultValue ->
                    defaultValue(convertExpression(defaultValue))
                }
            }

            poetConstructor.addParameter(poetParameter.build())

            if (parameter.languageProperties[KT_PRIMARY_CTOR_VAL_OR_VAR_KEYWORD] != null) {
                val poetProperty = PropertySpec.builder(parameter.name, parameterType)
                    .initializer(parameter.name)

                if (parameter.languageProperties[KT_PRIMARY_CTOR_VAL_OR_VAR_KEYWORD] == KtTokens.VAR_KEYWORD.value) {
                    poetProperty.mutable()
                }

                poetClassBuilder.addProperty(poetProperty.build())
            }
        }
        poetClassBuilder.primaryConstructor(poetConstructor.build())
    }
}

context(KotlinModule, ScenarioScope)
private fun getModifiers(irMethod: IrMethod): List<KModifier> {
    return buildList {
        when(irMethod.visibility) {
            is IrVisibilityProtected -> add(KModifier.PROTECTED)
            is IrVisibilityInternal -> add(KModifier.INTERNAL)
            is IrVisibilityPrivate -> add(KModifier.PRIVATE)
            is IrVisibilityPublic -> add(KModifier.PUBLIC)
        }

        if (irMethod.isAbstract) {
            add(KModifier.ABSTRACT)
        }

        if (irMethod.isOverride) {
            add(KModifier.OVERRIDE)
        }

        if (irMethod.languageProperties[KtTokens.FINAL_KEYWORD.value] == true) {
            add(KModifier.FINAL)
        }

        if (irMethod.languageProperties[KtTokens.OPEN_KEYWORD.value] == true) {
            add(KModifier.OPEN)
        }

        if (irMethod.languageProperties[KtTokens.INLINE_KEYWORD.value] == true) {
            add(KModifier.INLINE)
        }

        if (irMethod.languageProperties[KtTokens.OPERATOR_KEYWORD.value] == true) {
            add(KModifier.OPERATOR)
        }

        if (irMethod.languageProperties[KtTokens.SUSPEND_KEYWORD.value] == true) {
            add(KModifier.SUSPEND)
        }

        if (irMethod.languageProperties[KtTokens.TAILREC_KEYWORD.value] == true) {
            add(KModifier.TAILREC)
        }
    }
}

context(KotlinModule, ScenarioScope)
private fun getPrimaryConstructorFieldModifiers(irParameter: IrParameter): List<KModifier> {
    return buildList {
        when(irParameter.languageProperties[KT_PRIMARY_CTOR_PARAMETER_VISIBILITY]) {
            is IrVisibilityProtected -> add(KModifier.PROTECTED)
            is IrVisibilityInternal -> add(KModifier.INTERNAL)
            is IrVisibilityPrivate -> add(KModifier.PRIVATE)
            is IrVisibilityPublic -> add(KModifier.PUBLIC)
        }

        if (irParameter.languageProperties[KtTokens.ABSTRACT_KEYWORD.value] == true) {
            add(KModifier.ABSTRACT)
        }

        if (irParameter.languageProperties[KtTokens.OVERRIDE_KEYWORD.value] == true) {
            add(KModifier.OVERRIDE)
        }

        if (irParameter.languageProperties[KtTokens.FINAL_KEYWORD.value] == true) {
            add(KModifier.FINAL)
        }

        if (irParameter.languageProperties[KtTokens.OPEN_KEYWORD.value] == true) {
            add(KModifier.OPEN)
        }
    }
}
