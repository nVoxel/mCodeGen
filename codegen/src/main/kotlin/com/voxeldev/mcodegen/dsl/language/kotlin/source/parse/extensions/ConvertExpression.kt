package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.builders.irAssignmentExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irBinaryExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irCastExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irExpressionUnknown
import com.voxeldev.mcodegen.dsl.ir.builders.irIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irLiteralExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irObjectCreationExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irParameter
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeCheckExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReference
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReferenceIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irUnaryExpression
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule.languageName
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.builders.irLambdaExpression
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.builders.irParenthesizedExpression
import com.voxeldev.mcodegen.dsl.language.kotlin.utils.getLeft
import com.voxeldev.mcodegen.dsl.language.kotlin.utils.getRight
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtUnaryExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getCall
import org.jetbrains.kotlin.resolve.calls.model.ExpressionValueArgument
import org.jetbrains.kotlin.resolve.calls.model.VarargValueArgument
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

const val IDENTIFIER_REFERENCE = "identifierReference"

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertExpression(
    ktClassOrObject: KtClassOrObject,
    ktExpression: KtExpression?
): IrExpression {
    if (ktExpression == null) {
        return irLiteralExpression("").build()
    }

    return when (ktExpression) {
        is KtStringTemplateExpression, is KtConstantExpression -> {
            irLiteralExpression(ktExpression.text).build()
        }

        is KtSimpleNameExpression -> {
            val target = this@BindingContext.get(BindingContext.REFERENCE_TARGET, ktExpression)
            val targetFqName = target?.fqNameSafe?.asString()

            // first, check if the reference is a class (then we should import it)
            if (target is ClassDescriptor && targetFqName != null
                && targetFqName != ktClassOrObject.fqName?.asString()
            ) {
                return irTypeReferenceIdentifierExpression(
                    referencedType = irTypeReference(
                        referencedClassName = targetFqName
                    ).build()
                ).build()
            }

            val targetContainer = target?.containingDeclaration
            val targetContainerFqName = targetContainer?.fqNameSafe?.asString()

            // if it's not a class, check its container (class or file), maybe we should import it
            val identifierReference = if (targetContainer is ClassDescriptor && targetContainerFqName != null
                && targetContainerFqName != ktClassOrObject.fqName?.asString()
            ) {
                targetContainerFqName
            } else if (targetContainer is PackageFragmentDescriptor && targetContainerFqName != null
                && targetContainerFqName != ktClassOrObject.containingKtFile.packageFqName.asString()
            ) {
                "${targetContainerFqName}.${ktExpression.text}"
            } else null

            irIdentifierExpression(
                selector = irLiteralExpression(ktExpression.text).build()
            ).apply {
                identifierReference?.let {
                    addLanguageProperty(IDENTIFIER_REFERENCE, identifierReference)
                }
            }.build()
        }

        is KtDotQualifiedExpression -> {
            val qualifier = convertExpression(ktClassOrObject, ktExpression.receiverExpression)
            val selector = ktExpression.selectorExpression
                ?.let { selector -> convertExpression(ktClassOrObject, selector) }
                ?: throw IllegalArgumentException("KtDotQualifiedExpression right side (selector) is empty")

            // the logic is: if right part is a method call, add left part to the method call expr
            if (selector is IrMethodCallExpression) {
                return selector.copy(
                    receiver = qualifier,
                )
            }

            // if right part is an identifier, add left part to the right part
            if (selector is IrIdentifierExpression) {
                return selector.copy(
                    qualifier = qualifier,
                )
            }

            // otherwise return it as a literal
            println("Returning fallback in KtDotQualifiedExpression")
            irLiteralExpression(ktExpression.text).build()
        }

        is KtCallExpression -> {
            val call = ktExpression.getCall(this@BindingContext)
                ?: throw IllegalArgumentException("Callable expression ${ktExpression.text} doesn't have a call")
            val resolvedCall = this@BindingContext.get(BindingContext.RESOLVED_CALL, call)
                ?: throw IllegalArgumentException("Unable to resolve call of callable expression: ${ktExpression.text}")
            val resolvedCallDescriptor = resolvedCall.resultingDescriptor

            val resolvedCallName = if (resolvedCallDescriptor is ConstructorDescriptor) {
                resolvedCallDescriptor.constructedClass.fqNameSafe.asString()
            } else resolvedCallDescriptor.name.asString()

            val arguments = resolvedCallDescriptor.valueParameters.flatMap { param ->
                val match = resolvedCall.valueArguments[param] ?: return@flatMap listOf()

                when (match) {
                    //TODO: support default args
                    //is DefaultValueArgument ->

                    is ExpressionValueArgument -> {
                        val argumentExpression = match.valueArgument?.getArgumentExpression()
                            ?: throw IllegalArgumentException(
                                "Got ExpressionValueArgument without expression " +
                                        "for $resolvedCallDescriptor function"
                            )

                        listOf(convertExpression(ktClassOrObject, argumentExpression))
                    }

                    is VarargValueArgument -> {
                        val argumentExpressions = match.arguments.mapNotNull { arg ->
                            arg.getArgumentExpression()
                        }

                        argumentExpressions.map { argumentExpression ->
                            convertExpression(ktClassOrObject, argumentExpression)
                        }
                    }

                    else -> listOf()
                }
            }

            if (resolvedCallDescriptor is ConstructorDescriptor) {
                irObjectCreationExpression(resolvedCallName).apply {
                    arguments.forEach { argument ->
                        addConstructorArg(argument)
                    }
                }.build()
            } else {
                irMethodCallExpression(methodName = resolvedCallName).apply {
                    arguments.forEach { argument ->
                        addArgument(argument)
                    }
                }.build()
            }
        }

        is KtBinaryExpression -> {
            val operator = convertBinaryOperator(ktExpression)

            if (operator.isLeft) {
                irBinaryExpression(
                    left = convertExpression(ktClassOrObject, ktExpression.left),
                    operator = operator.getLeft(),
                    right = convertExpression(ktClassOrObject, ktExpression.right),
                ).build()
            } else {
                irAssignmentExpression(
                    target = convertExpression(ktClassOrObject, ktExpression.left),
                    operator = operator.getRight(),
                    value = convertExpression(ktClassOrObject, ktExpression.right),
                ).build()
            }
        }

        is KtUnaryExpression -> {
            irUnaryExpression(
                operator = convertUnaryOperator(ktExpression),
                operand = convertExpression(ktClassOrObject, ktExpression.baseExpression),
                isPrefix = ktExpression is KtPrefixExpression,
            ).build()
        }

        is KtBinaryExpressionWithTypeRHS -> {
            val preloadedTypeParameters = preloadTypeParameters(ktClassOrObject.typeParameters)
            val targetTypeElement = ktExpression.right?.typeElement
                ?: throw IllegalArgumentException("Type cast expression type can not be resolved")

            irCastExpression(
                expression = convertExpression(ktClassOrObject, ktExpression.left),
                targetType = convertKtTypeElement(targetTypeElement, preloadedTypeParameters),
            ).build()
        }

        is KtIsExpression -> {
            val preloadedTypeParameters = preloadTypeParameters(ktClassOrObject.typeParameters)
            val targetTypeElement = ktExpression.typeReference?.typeElement
                ?: throw IllegalArgumentException("Type check expression type can not be resolved")

            irTypeCheckExpression(
                expression = convertExpression(ktClassOrObject, ktExpression.leftHandSide),
                checkType = convertKtTypeElement(targetTypeElement, preloadedTypeParameters),
            ).build()
        }

        is KtLambdaExpression -> {
            irLambdaExpression().apply {
                val literal = ktExpression.functionLiteral
                val functionDescriptor = this@BindingContext.get(BindingContext.FUNCTION, literal)
                    ?: throw java.lang.IllegalArgumentException("Unresolved lambda: ${ktExpression.text}")

                val preloadedTypeParameters = preloadTypeParameters(ktClassOrObject.typeParameters)

                functionDescriptor.extensionReceiverParameter?.let { receiver ->

                    addParameter(
                        irParameter(
                            name = "\$receiver",
                            type = convertKotlinType(receiver.type, preloadedTypeParameters)
                        ).build()
                    )
                }

                // value parameters (can be empty in source)
                if (literal.valueParameters.isNotEmpty()) {
                    // name & type are both written explicitly or can be taken from the descriptor
                    functionDescriptor.valueParameters.forEachIndexed { i, p ->
                        addParameter(
                            irParameter(
                                name = p.name.asString(),
                                type = convertKotlinType(p.type, preloadedTypeParameters),
                            ).build()
                        )
                    }
                } else {
                    // no explicit list, so at most one implicit “it”
                    if (functionDescriptor.valueParameters.isNotEmpty()) {
                        val p = functionDescriptor.valueParameters.single()
                        addParameter(
                            irParameter(
                                name = "it",
                                type = convertKotlinType(p.type, preloadedTypeParameters),
                            ).build()
                        )
                    }
                }

                functionDescriptor.returnType?.let { returnType ->
                    returnType(convertKotlinType(returnType, preloadedTypeParameters))
                } ?: throw IllegalArgumentException("Lambda return type is null")

                val targetInterfaceType = this@BindingContext.getType(ktExpression)
                targetInterfaceType?.let {
                    targetInterfaceType(convertKotlinType(targetInterfaceType, preloadedTypeParameters))
                }

                literal.bodyExpression?.let { body ->
                    body(statement = convertStatement(ktClassOrObject, body))
                }
            }.build()
        }

        is KtParenthesizedExpression -> {
            ktExpression.expression?.let { innerExpression ->
                irParenthesizedExpression(convertStatement(ktClassOrObject, innerExpression)).build()
            } ?: getFallbackExpression(ktExpression)
        }

        else -> getFallbackExpression(ktExpression)
    }
}

private fun getFallbackExpression(ktExpression: KtExpression): IrExpression {
    return irExpressionUnknown().apply {
        addStringRepresentation(
            IrStringRepresentation(languageName, ktExpression.text)
        )
    }.build()
}