package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrTypeGeneric
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeFunction
import com.voxeldev.mcodegen.dsl.ir.builders.irTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReference
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.isBuiltinFunctionalType
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.psi.KtFunctionType
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtProjectionKind
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType

private val primitiveTypes = mapOf(
    "Unit" to IrTypePrimitive.PrimitiveType.Void(),
    "Boolean" to IrTypePrimitive.PrimitiveType.Boolean(),
    "Byte" to IrTypePrimitive.PrimitiveType.Byte(),
    "Short" to IrTypePrimitive.PrimitiveType.Short(),
    "Int" to IrTypePrimitive.PrimitiveType.Int(),
    "Long" to IrTypePrimitive.PrimitiveType.Long(),
    "Char" to IrTypePrimitive.PrimitiveType.Char(),
    "Float" to IrTypePrimitive.PrimitiveType.Float(),
    "Double" to IrTypePrimitive.PrimitiveType.Double(),
)

private val primitiveFqNames = mapOf(
    "kotlin.Unit" to IrTypePrimitive.PrimitiveType.Void(),
    "kotlin.Boolean" to IrTypePrimitive.PrimitiveType.Boolean(),
    "kotlin.Byte" to IrTypePrimitive.PrimitiveType.Byte(),
    "kotlin.Short" to IrTypePrimitive.PrimitiveType.Short(),
    "kotlin.Int" to IrTypePrimitive.PrimitiveType.Int(),
    "kotlin.Long" to IrTypePrimitive.PrimitiveType.Long(),
    "kotlin.Char" to IrTypePrimitive.PrimitiveType.Char(),
    "kotlin.Float" to IrTypePrimitive.PrimitiveType.Float(),
    "kotlin.Double" to IrTypePrimitive.PrimitiveType.Double(),
)

// types received from the BindingContext
context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertKotlinType(
    kotlinType: KotlinType,
    preloadedTypeParameters: Map<String, IrTypeGeneric>,
    isNullable: Boolean = kotlinType.isMarkedNullable,
): IrType {

    // ---------------- Primitive kotlin.* types ----------------
    val primitive = primitiveFqNames[kotlinType.asPrimitiveNameOrNull() ?: ""]
    if (primitive != null)
        return IrTypePrimitive(primitive, isNullable)

    // -------------- Function types  (A, B) -> R ---------------
    if (kotlinType.isBuiltinFunctionalType) {
        val args = kotlinType.arguments
        val arity = args.size - 1 // last = return type
        val paramIr = (0 until arity).map { idx ->
            convertKotlinType(args[idx].type, preloadedTypeParameters, args[idx].type.isMarkedNullable)
        }
        val returnIr = convertKotlinType(
            kotlinType = args.last().type,
            isNullable = args.last().type.isMarkedNullable,
            preloadedTypeParameters = preloadedTypeParameters
        )

        return irTypeFunction(paramIr, returnIr).apply { nullable(isNullable) }.build()
    }

    // -------------- Type parameters declared on the class -----
    (kotlinType.constructor.declarationDescriptor as? TypeParameterDescriptor)?.let { tpDesc ->
        preloadedTypeParameters[tpDesc.name.asString()]?.let { g ->
            return g.copy(isNullable = isNullable)
        }
    }

    // -------------- Classes / interfaces / type aliases -------
    val classDesc = kotlinType.constructor.declarationDescriptor as? ClassDescriptor
    val fqName = classDesc?.fqNameSafe?.asString() ?: kotlinType.toString()   // fallback: render the type

    val irArgs = kotlinType.arguments.map { proj ->
        when {
            proj.isStarProjection -> {
                irTypeReference("*").apply { nullable(false) }.build()
            }

            else -> {
                val argType = proj.type
                convertKotlinType(argType, preloadedTypeParameters, argType.isMarkedNullable)
            }
        }
    }

    return IrTypeReference(
        referencedClassName = fqName,
        typeParameters = irArgs,
        isNullable = isNullable
    )
}

// types received directly from the PSI
context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertKtTypeElement(
    ktType: KtTypeElement,
    preloadedTypeParameters: Map<String, IrTypeGeneric>,
    isNullable: Boolean = false,
): IrType {
    return when (ktType) {
        is KtNullableType -> {                     //  String?
            val inner = ktType.innerType ?: throw IllegalArgumentException("Nullable type doesn't have inner type")
            convertKtTypeElement(inner, preloadedTypeParameters, isNullable = true)
        }

        is KtFunctionType -> {                     //  (A, B) -> C
            val parameterTypes = ktType.parameters.map { param ->
                val paramType = param.typeReference?.typeElement
                    ?: throw IllegalArgumentException("Function type param doesn't have type")
                convertKtTypeElement(paramType, preloadedTypeParameters)
            }

            val returnType = ktType.returnTypeReference?.typeElement?.let { returnType ->
                convertKtTypeElement(returnType, preloadedTypeParameters)
            } ?: irTypePrimitive(IrTypePrimitive.PrimitiveType.Void()).build()

            irTypeFunction(parameterTypes, returnType).apply {
                nullable(isNullable)
            }.build()
        }

        is KtUserType -> convertUserType(ktType, isNullable = isNullable, genericSymbols = preloadedTypeParameters)

        else -> throw IllegalArgumentException("Unsupported KtTypeElement")
    }
}

context(KotlinModule, BindingContext, ScenarioScope)
private fun convertUserType(
    userType: KtUserType,
    isNullable: Boolean,
    genericSymbols: Map<String, IrTypeGeneric>,
): IrType {
    val simpleName = userType.referenceExpression!!.text

    val descriptor = this@BindingContext[BindingContext.REFERENCE_TARGET, userType.referenceExpression]
    val fqName = when (descriptor) {
        is ClassDescriptor -> descriptor.fqNameSafe.asString()
        is ClassConstructorDescriptor -> descriptor.containingDeclaration.fqNameSafe.asString()
        else -> null
    }

    //  ----------  Primitive kotlin.* types  ----------
    primitiveTypes[simpleName]?.let { prim ->
        return IrTypePrimitive(prim, isNullable)
    }

    //  ----------  Any generic the class declared itself ----------
    genericSymbols[simpleName]?.let { genericParam ->
        return genericParam.copy(isNullable = isNullable)
    }

    //  ----------  Regular class  ----------
    val projections = userType.typeArguments
    val irTypeArgs = projections.map { proj ->
        when (proj.projectionKind) {
            KtProjectionKind.STAR -> {
                irTypeReference("*").apply {
                    nullable(false)
                }.build()
            }

            else -> {
                proj.typeReference?.typeElement?.let { typeArgType ->
                    convertKtTypeElement(typeArgType, genericSymbols)
                } ?: irTypePrimitive(IrTypePrimitive.PrimitiveType.Void()).build()
            }
        }
    }

    return IrTypeReference(
        referencedClassName = fqName ?: simpleName,
        typeParameters = irTypeArgs,
        isNullable = isNullable
    )
}

/** Returns Kotlin built-in short name for primitive types, otherwise `null`. */
private fun KotlinType.asPrimitiveNameOrNull(): String? =
    (constructor.declarationDescriptor as? ClassDescriptor)
        ?.takeIf { KotlinBuiltIns.isPrimitiveType(this) || KotlinBuiltIns.isUnit(this) }
        ?.fqNameSafe
        ?.asString()