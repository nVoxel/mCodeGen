package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrBlockStatement
import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.IrMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.IrObjectCreationExpression
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrTypeGeneric
import com.voxeldev.mcodegen.dsl.ir.IrTypeReferenceIdentifierExpression
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
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.builders.irNullSafeExpression
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.builders.irParenthesizedExpression
import com.voxeldev.mcodegen.dsl.language.kotlin.utils.getLeft
import com.voxeldev.mcodegen.dsl.language.kotlin.utils.getRight
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.isTopLevelInPackage
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtUnaryExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getCall
import org.jetbrains.kotlin.resolve.calls.model.ExpressionValueArgument
import org.jetbrains.kotlin.resolve.calls.model.VarargValueArgument
import org.jetbrains.kotlin.resolve.calls.model.VariableAsFunctionResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

const val KT_CLASS_MEMBER_REFERENCE = "ktClassMemberReference"
const val KT_TOP_LEVEL_MEMBER_REFERENCE = "ktTopLevelMemberReference"

const val KT_SAFE_TYPE_CAST = "ktSafeTypeCast"

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertExpression(
    ktClassOrObject: KtClassOrObject?,
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
            val targetSimpleName = target?.name?.asString() ?: "Ir:UnnamedClass"
            val targetFqName = target?.fqNameSafe?.asString()

            // first, check if the reference is a class (then we should import it)
            if (target is ClassDescriptor && targetFqName != null
                && targetFqName != ktClassOrObject?.fqName?.asString()
            ) {
                return irTypeReferenceIdentifierExpression(
                    referencedType = irTypeReference(
                        referencedClassSimpleName = targetSimpleName,
                        referencedClassQualifiedName = targetFqName
                    ).apply {
                        nullable(false)
                    }.build()
                ).build()
            }

            val expression = irIdentifierExpression(
                selector = irLiteralExpression(ktExpression.text).build()
            )

            val targetContainer = target?.containingDeclaration
            val targetContainerFqName = targetContainer?.fqNameSafe?.asString()

            // if it's not a class, check its container (class or file), maybe we should import it
            if (targetContainer is ClassDescriptor && targetContainerFqName != null
                && targetContainerFqName != ktClassOrObject?.fqName?.asString()
            ) {
                expression.addLanguageProperty(
                    KT_CLASS_MEMBER_REFERENCE,
                    targetContainerFqName,
                )
            } else if (targetContainer is PackageFragmentDescriptor && targetContainerFqName != null
                && targetContainerFqName != ktClassOrObject?.containingKtFile?.packageFqName?.asString()
            ) {
                expression.addLanguageProperty(
                    KT_TOP_LEVEL_MEMBER_REFERENCE,
                    targetContainerFqName,
                )
            }

            expression.build()
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

            // no need for the TypeReferenceIdentifier, as we already
            // store qualified class name in the IrObjectCreationExpression
            if (qualifier is IrTypeReferenceIdentifierExpression && selector is IrObjectCreationExpression) {
                return selector
            }

            // otherwise return it as an identifier
            println("Returning fallback in KtDotQualifiedExpression")
            irIdentifierExpression(
                selector = irLiteralExpression(ktExpression.selectorExpression?.text.toString()).build()
            ).apply {
                qualifier(qualifier)
            }.build()
        }

        is KtCallExpression -> {
            val call = ktExpression.getCall(this@BindingContext)
                ?: throw IllegalArgumentException("Callable expression ${ktExpression.text} doesn't have a call")
            val resolvedCall = this@BindingContext.get(BindingContext.RESOLVED_CALL, call)
                ?: throw IllegalArgumentException("Unable to resolve call of callable expression: ${ktExpression.text}")
            val resolvedCallDescriptor = resolvedCall.resultingDescriptor

            val resolvedCallName = when {
                resolvedCall is VariableAsFunctionResolvedCall -> {
                    resolvedCall.variableCall.resultingDescriptor.name.asString()
                }

                resolvedCallDescriptor is ConstructorDescriptor -> {
                    resolvedCallDescriptor.constructedClass.fqNameSafe.asString()
                }

                else -> {
                    resolvedCallDescriptor.name.asString()
                }
            }

            val valueArguments = resolvedCallDescriptor.valueParameters.flatMap { param ->
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

            val typeArguments = ktExpression.typeArgumentList?.arguments.orEmpty().mapNotNull { argument ->
                argument.typeReference?.let { typeRef ->
                    val preloadedTypeParameters = ktClassOrObject?.let {
                        preloadAllTypeParameters(ktClassOrObject, ktExpression)
                    }.orEmpty()

                    val ktType = this@BindingContext.get(BindingContext.TYPE, typeRef) ?: return@let null
                    convertKotlinType(ktType, preloadedTypeParameters)
                }
            }

            if (resolvedCallDescriptor is ConstructorDescriptor) {
                irObjectCreationExpression(resolvedCallName).apply {
                    valueArguments.forEach { argument ->
                        addConstructorArg(argument)
                    }
                }.build()
            } else {
                irMethodCallExpression(methodName = resolvedCallName).apply {
                    valueArguments.forEach { valueArgument ->
                        addValueArgument(valueArgument)
                    }

                    typeArguments.forEach { typeArgument ->
                        addTypeArgument(typeArgument)
                    }

                    val targetContainerFqName = resolvedCallDescriptor.containingDeclaration.fqNameSafe.asString()
                    if (resolvedCallDescriptor.isTopLevelInPackage(resolvedCallName, targetContainerFqName)) {
                        addLanguageProperty(
                            KT_TOP_LEVEL_MEMBER_REFERENCE,
                            targetContainerFqName,
                        )
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
            val preloadedTypeParameters = ktClassOrObject?.let {
                preloadAllTypeParameters(ktClassOrObject, ktExpression)
            }.orEmpty()
            val targetTypeElement = ktExpression.right?.typeElement
                ?: throw IllegalArgumentException("Type cast expression type can not be resolved")

            val token = (ktExpression.operationReference as? KtOperationReferenceExpression)
                ?.operationSignTokenType?.value

            irCastExpression(
                expression = convertExpression(ktClassOrObject, ktExpression.left),
                targetType = convertKtTypeElement(targetTypeElement, preloadedTypeParameters),
            ).apply {
                if (token == "AS_SAFE") {
                    addLanguageProperty(KT_SAFE_TYPE_CAST, true)
                }
            }.build()
        }

        is KtIsExpression -> {
            val preloadedTypeParameters = ktClassOrObject?.let {
                preloadAllTypeParameters(ktClassOrObject, ktExpression)
            }.orEmpty()
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

                val preloadedTypeParameters = ktClassOrObject?.let {
                    preloadAllTypeParameters(ktClassOrObject, ktExpression)
                }.orEmpty()

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
                    val bodyBlock = convertStatement(ktClassOrObject, body) as? IrBlockStatement
                        ?: throw IllegalArgumentException("IrLambdaExpression body should be IrBlockStatement")
                    body(statement = bodyBlock)
                }
            }.build()
        }

        is KtParenthesizedExpression -> {
            ktExpression.expression?.let { innerExpression ->
                irParenthesizedExpression(convertStatement(ktClassOrObject, innerExpression)).build()
            } ?: getFallbackExpression(ktExpression)
        }

        is KtSafeQualifiedExpression -> {
            val receiver = ktExpression.receiverExpression
            val selector = ktExpression.selectorExpression
                ?: throw IllegalArgumentException("Got KtSafeQualifiedExpression without selector")

            irNullSafeExpression(
                receiver = convertExpression(ktClassOrObject, receiver),
                selector = convertExpression(ktClassOrObject, selector),
            ).build()
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

context(KotlinModule, BindingContext, ScenarioScope)
private fun preloadAllTypeParameters(
    ktClassOrObject: KtClassOrObject?,
    ktExpression: KtExpression,
): Map<String, IrTypeGeneric> {
    val containingFunction = ktExpression.getStrictParentOfType<KtFunction>()
    return preloadTypeParameters(ktClassOrObject?.typeParameters ?: emptyList()).plus(
        containingFunction?.let { preloadTypeParameters(containingFunction.typeParameters) }
            .orEmpty()
    )
}
