package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.BOOLEAN_ARRAY
import com.squareup.kotlinpoet.BYTE
import com.squareup.kotlinpoet.BYTE_ARRAY
import com.squareup.kotlinpoet.CHAR
import com.squareup.kotlinpoet.CHAR_ARRAY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.DOUBLE_ARRAY
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.FLOAT_ARRAY
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.INT_ARRAY
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.LONG_ARRAY
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.SHORT
import com.squareup.kotlinpoet.SHORT_ARRAY
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.UNIT
import com.voxeldev.mcodegen.dsl.ir.IrTypeArray
import com.voxeldev.mcodegen.dsl.ir.IrTypeFunction
import com.voxeldev.mcodegen.dsl.ir.IrTypeGeneric
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(KotlinModule, ScenarioScope)
internal fun convertType(irType: IrType): TypeName {
    val baseType = when (irType) {
        is IrTypeArray -> {
            val elementName = convertType(irType.elementType)

            when (val elementType = irType.elementType) {
                is IrTypePrimitive -> {
                    when (elementType.primitiveType) {
                        is IrTypePrimitive.PrimitiveType.Boolean -> BOOLEAN_ARRAY
                        is IrTypePrimitive.PrimitiveType.Byte -> BYTE_ARRAY
                        is IrTypePrimitive.PrimitiveType.Short -> SHORT_ARRAY
                        is IrTypePrimitive.PrimitiveType.Int -> INT_ARRAY
                        is IrTypePrimitive.PrimitiveType.Long -> LONG_ARRAY
                        is IrTypePrimitive.PrimitiveType.Char -> CHAR_ARRAY
                        is IrTypePrimitive.PrimitiveType.Float -> FLOAT_ARRAY
                        is IrTypePrimitive.PrimitiveType.Double -> DOUBLE_ARRAY
                        // TODO: support primitive U-types (UInt, etc.)
                        else -> ClassName("kotlin", "Array").parameterizedBy(elementName)
                    }
                }

                else -> ClassName("kotlin", "Array").parameterizedBy(elementName)
            }
        }

        is IrTypeGeneric -> TypeVariableName(irType.name)

        is IrTypeReference -> {
            val className = ClassName.bestGuess(irType.referencedClassName)
            if (irType.typeParameters.isEmpty()) {
                className
            } else {
                val typeArguments = irType.typeParameters
                    .map { typeArgument -> convertType(typeArgument) }
                className.parameterizedBy(typeArguments)
            }
        }

        is IrTypePrimitive -> {
            when (irType.primitiveType) {
                is IrTypePrimitive.PrimitiveType.Void -> UNIT
                is IrTypePrimitive.PrimitiveType.Boolean -> BOOLEAN
                is IrTypePrimitive.PrimitiveType.Byte -> BYTE
                is IrTypePrimitive.PrimitiveType.Short -> SHORT
                is IrTypePrimitive.PrimitiveType.Int -> INT
                is IrTypePrimitive.PrimitiveType.Long -> LONG
                is IrTypePrimitive.PrimitiveType.Char -> CHAR
                is IrTypePrimitive.PrimitiveType.Float -> FLOAT
                is IrTypePrimitive.PrimitiveType.Double -> DOUBLE
                else -> throw IllegalArgumentException("Unsupported Kotlin primitive type")
            }
        }

        is IrTypeFunction -> {
            val parameterTypes = irType.parameterTypes
                .map { parameterType -> convertType(parameterType) }
                .toTypedArray()

            LambdaTypeName.get(
                parameters = parameterTypes,
                returnType = convertType(irType.returnType)
            )
        }

        else -> throw NotImplementedError("Support of this type is not implemented for Kotlin")
    }

    return baseType.copy(nullable = irType.isNullable)
}