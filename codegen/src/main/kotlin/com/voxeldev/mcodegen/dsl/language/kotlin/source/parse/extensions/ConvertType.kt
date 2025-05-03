package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrGeneric
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import com.voxeldev.mcodegen.dsl.ir.builders.irFunctionType
import com.voxeldev.mcodegen.dsl.ir.builders.irTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReference
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtFunctionType
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtProjectionKind
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

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

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertType(
    ktType: KtTypeElement,
    preloadedTypeParameters: Map<String, IrGeneric>,
    isNullable: Boolean = false,
): IrType {
    return when (ktType) {
        is KtNullableType -> {                     //  String?
            val inner = ktType.innerType ?: throw IllegalArgumentException("Nullable type doesn't have inner type")
            convertType(inner, preloadedTypeParameters, isNullable = true)
        }

        is KtFunctionType -> {                     //  (A, B) -> C
            val parameterTypes = ktType.parameters.map { param ->
                val paramType = param.typeReference?.typeElement
                    ?: throw IllegalArgumentException("Function type param doesn't have type")
                convertType(paramType, preloadedTypeParameters)
            }

            val returnType = ktType.returnTypeReference?.typeElement?.let { returnType ->
                convertType(returnType, preloadedTypeParameters)
            } ?: irTypePrimitive(IrTypePrimitive.PrimitiveType.Void()).build()

            irFunctionType(parameterTypes, returnType).apply {
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
    genericSymbols: Map<String, IrGeneric>,
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
                    convertType(typeArgType, genericSymbols)
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