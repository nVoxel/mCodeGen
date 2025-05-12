package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrMethod
import com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.builders.IrClassBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.IrConstructorBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.IrMethodBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irConstructor
import com.voxeldev.mcodegen.dsl.ir.builders.irMethod
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodBody
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irParameter
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.internalVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.privateVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.protectedVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.publicVisibility
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.inference.returnTypeOrNothing
import org.jetbrains.kotlin.types.isError

const val KT_PRIMARY_CONSTRUCTOR = "ktPrimaryConstructor"

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertFunctions(
    ktClassOrObject: KtClassOrObject,
    functions: List<KtFunction>,
    irClassBuilder: IrClassBuilder,
) {
    functions.forEach { method ->
        val irMethod = convertFunction(ktClassOrObject, method)
        irClassBuilder.addMethod(irMethod)
    }
}

context(KotlinModule, BindingContext, ScenarioScope)
private fun convertFunction(
    ktClassOrObject: KtClassOrObject,
    ktFunction: KtFunction,
): IrMethod {
    val isConstructor = ktFunction is KtPrimaryConstructor || ktFunction is KtSecondaryConstructor

    val irMethodBuilder = if (isConstructor) {
        irConstructor(
            name = ktFunction.name ?: "Ir:UnnamedMethod",
            returnType = convertFunctionType(
                ktClassOrObject = ktClassOrObject,
                ktCallable = ktFunction,
                isConstructor = true,
            )
        ).apply {
            if (ktFunction is KtPrimaryConstructor) {
                addLanguageProperty(KT_PRIMARY_CONSTRUCTOR, true)
            }
        }
    } else {
        irMethod(
            name = ktFunction.name ?: "Ir:UnnamedMethod",
            returnType = convertFunctionType(
                ktClassOrObject = ktClassOrObject,
                ktCallable = ktFunction,
                isConstructor = false,
            )
        )
    }

    convertFunctionModifiers(ktFunction, irMethodBuilder)

    // TODO: convert annotations

    ktFunction.valueParameters.forEach { parameter ->
        irMethodBuilder.addParameter(
            irParameter(
                name = parameter.name ?: "Ir:UnnamedParameter",
                type = convertParameterType(
                    ktClassOrObject = ktClassOrObject,
                    ktParameter = parameter,
                ),
            ).build()
        )
    }

    convertTypeParameters(ktClassOrObject, ktFunction.typeParameters, irMethodBuilder)

    if (ktFunction is KtSecondaryConstructor && irMethodBuilder is IrConstructorBuilder) {
        val callType = ktFunction.getDelegationCall().calleeExpression?.text

        // TODO: support named arguments using BindingContext
        val argumentsExpressions = ktFunction.getDelegationCall().valueArguments.map { argument ->
            convertExpression(ktClassOrObject, argument.getArgumentExpression())
        }

        if (!ktFunction.getDelegationCall().isImplicit && callType != null) {
            val ctorCallExpression = irMethodCallExpression(
                methodName = ktClassOrObject.fqName?.asString() ?: "Ir:UnnamedClass"
            ).apply {
                methodCallKind(
                    when (callType) {
                        "super" -> IrMethodCallExpression.IrSuperMethodCallKind
                        "this" -> IrMethodCallExpression.IrThisMethodCallKind
                        else -> throw IllegalArgumentException("Got unknown constructor call type")
                    }
                )

                argumentsExpressions.forEach { argument ->
                    addArgument(argument)
                }
            }.build()

            irMethodBuilder.otherConstructorCall(ctorCallExpression)
        }
    }

    if (ktFunction.hasBody()) {
        val irMethodBodyBuilder = irMethodBody()
        ktFunction.bodyExpression?.let { body ->
            irMethodBodyBuilder.addStatement(convertStatement(ktClassOrObject, body))
        }
        irMethodBuilder.body(irMethodBodyBuilder.build())
    }

    return irMethodBuilder.build()
}

context(KotlinModule, BindingContext, ScenarioScope)
private fun convertFunctionModifiers(
    ktFunction: KtFunction,
    irMethodBuilder: IrMethodBuilder,
) {
    irMethodBuilder.visibility(
        when {
            ktFunction.hasModifier(KtTokens.PROTECTED_KEYWORD) -> protectedVisibility()
            ktFunction.hasModifier(KtTokens.INTERNAL_KEYWORD) -> internalVisibility()
            ktFunction.hasModifier(KtTokens.PRIVATE_KEYWORD) -> privateVisibility()
            else -> publicVisibility()
        }
    )

    irMethodBuilder.isAbstract(ktFunction.hasModifier(KtTokens.ABSTRACT_KEYWORD))
    irMethodBuilder.isOverride(ktFunction.hasModifier(KtTokens.OVERRIDE_KEYWORD))

    if (ktFunction.hasModifier(KtTokens.FINAL_KEYWORD)) {
        irMethodBuilder.addLanguageProperty(
            KtTokens.FINAL_KEYWORD.value, true
        )
    }

    if (ktFunction.hasModifier(KtTokens.OPEN_KEYWORD)) {
        irMethodBuilder.addLanguageProperty(
            KtTokens.OPEN_KEYWORD.value, true
        )
    }
}

context(KotlinModule, BindingContext, ScenarioScope)
private fun convertFunctionType(
    ktClassOrObject: KtClassOrObject,
    ktCallable: KtCallableDeclaration,
    isConstructor: Boolean,
): IrType {
    val preloadedTypeParameters = preloadTypeParameters(ktClassOrObject.typeParameters)

    // try to convert explicit type
    ktCallable.typeReference?.typeElement?.let { typeElement ->
        return convertKtTypeElement(typeElement, preloadedTypeParameters)
    }

    // try to convert inferred type
    val functionDescriptor = if (isConstructor) {
        this@BindingContext.get(BindingContext.CONSTRUCTOR, ktCallable)
    } else {
        this@BindingContext.get(BindingContext.FUNCTION, ktCallable)
    }

    if (functionDescriptor == null || functionDescriptor.returnTypeOrNothing.isError) {
        // probably caused by missing source roots
        throw IllegalArgumentException("Please, specify the type " +
                "for ${ktCallable.name} ${if (isConstructor) "fun constructor" else "fun"} explicitly")
    }

    return convertKotlinType(functionDescriptor.returnTypeOrNothing, preloadedTypeParameters)
}

context(KotlinModule, BindingContext, ScenarioScope)
private fun convertParameterType(
    ktClassOrObject: KtClassOrObject,
    ktParameter: KtParameter,
): IrType {
    val preloadedTypeParameters = preloadTypeParameters(ktClassOrObject.typeParameters)

    // try to convert explicit type
    ktParameter.typeReference?.typeElement?.let { typeElement ->
        return convertKtTypeElement(typeElement, preloadedTypeParameters)
    }

    // try to convert inferred type
    val parameterDescriptor = this@BindingContext.get(BindingContext.VALUE_PARAMETER, ktParameter)

    if (parameterDescriptor == null || parameterDescriptor.returnTypeOrNothing.isError) {
        // probably caused by missing source roots
        throw IllegalArgumentException(
            "Please, specify the type for ${ktParameter.name} parameter explicitly"
        )
    }

    return convertKotlinType(parameterDescriptor.returnTypeOrNothing, preloadedTypeParameters)
}
