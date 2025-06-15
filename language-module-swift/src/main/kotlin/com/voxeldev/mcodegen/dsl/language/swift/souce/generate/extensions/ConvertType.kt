package com.voxeldev.mcodegen.dsl.language.swift.souce.generate.extensions

import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrTypeArray
import com.voxeldev.mcodegen.dsl.ir.IrTypeFunction
import com.voxeldev.mcodegen.dsl.ir.IrTypeGeneric
import com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import com.voxeldev.mcodegen.dsl.language.swift.SwiftModule
import com.voxeldev.mcodegen.dsl.language.swift.ir.getSwiftElementModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import io.outfoxx.swiftpoet.ARRAY
import io.outfoxx.swiftpoet.BOOL
import io.outfoxx.swiftpoet.DOUBLE
import io.outfoxx.swiftpoet.DeclaredTypeName
import io.outfoxx.swiftpoet.FLOAT
import io.outfoxx.swiftpoet.FunctionTypeName
import io.outfoxx.swiftpoet.INT16
import io.outfoxx.swiftpoet.INT32
import io.outfoxx.swiftpoet.INT64
import io.outfoxx.swiftpoet.INT8
import io.outfoxx.swiftpoet.STRING
import io.outfoxx.swiftpoet.TypeName
import io.outfoxx.swiftpoet.TypeVariableName
import io.outfoxx.swiftpoet.VOID
import io.outfoxx.swiftpoet.parameterizedBy

context(SwiftModule, ScenarioScope)
internal fun convertType(irType: IrType): TypeName {
    val baseType = when (irType) {
        is IrTypeArray -> {
            val elementName = convertType(irType.elementType)
            ARRAY.parameterizedBy(elementName)
        }

        is IrTypeGeneric -> TypeVariableName(irType.name)

        is IrTypeReference -> {
            if (irType.referencedClassSimpleName == "String") {
                STRING
            } else {
                val className = DeclaredTypeName.qualifiedTypeName(
                    irType.getSwiftElementModule() + irType.referencedClassSimpleName
                )

                if (irType.typeParameters.isEmpty()) {
                    className
                } else {
                    val typeArguments = irType.typeParameters
                        .map { typeArgument -> convertType(typeArgument) }
                        .toTypedArray()
                    className.parameterizedBy(*typeArguments)
                }
            }
        }

        is IrTypePrimitive -> {
            when (irType.primitiveType) {
                is IrTypePrimitive.PrimitiveType.Void -> VOID
                is IrTypePrimitive.PrimitiveType.Boolean -> BOOL
                is IrTypePrimitive.PrimitiveType.Byte -> INT8
                is IrTypePrimitive.PrimitiveType.Short -> INT16
                is IrTypePrimitive.PrimitiveType.Int -> INT32
                is IrTypePrimitive.PrimitiveType.Long -> INT64
                is IrTypePrimitive.PrimitiveType.Char -> DeclaredTypeName.typeName("Swift.Character")
                is IrTypePrimitive.PrimitiveType.Float -> FLOAT
                is IrTypePrimitive.PrimitiveType.Double -> DOUBLE
                else -> throw IllegalArgumentException("Unsupported Swift primitive type")
            }
        }

        is IrTypeFunction -> {
            val parameterTypes = irType.parameterTypes
                .map { parameterType -> convertType(parameterType) }
                .toTypedArray()

            FunctionTypeName.get(
                parameters = parameterTypes,
                returnType = convertType(irType.returnType)
            )
        }

        else -> throw NotImplementedError("Support of this type is not implemented for Swift")
    }

    return if (irType.isNullable) {
        baseType.makeOptional()
    } else {
        baseType
    }
}
