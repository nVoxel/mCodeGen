package com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions

import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeVariableName
import com.voxeldev.mcodegen.dsl.ir.IrArrayType
import com.voxeldev.mcodegen.dsl.ir.IrFunctionType
import com.voxeldev.mcodegen.dsl.ir.IrGeneric
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(JavaModule, ScenarioScope)
internal fun convertType(
    irType: IrType,
): TypeName {
    return when (irType) {
        is IrArrayType -> ArrayTypeName.of(convertType(irType.elementType))

        is IrGeneric -> TypeVariableName.get(irType.name)

        is IrTypeReference -> {
            val className = ClassName.bestGuess(irType.referencedClassName)
            if (irType.typeParameters.isEmpty()) {
                className
            } else {
                val typeArguments = irType.typeParameters
                    .map { typeParameter -> convertType(typeParameter) }
                    .toTypedArray()
                ParameterizedTypeName.get(className, *typeArguments)
            }
        }

        is IrTypePrimitive -> {
            when (irType.primitiveType) {
                is IrTypePrimitive.PrimitiveType.Void -> TypeName.VOID
                is IrTypePrimitive.PrimitiveType.Boolean -> TypeName.BOOLEAN
                is IrTypePrimitive.PrimitiveType.Byte -> TypeName.BYTE
                is IrTypePrimitive.PrimitiveType.Short -> TypeName.SHORT
                is IrTypePrimitive.PrimitiveType.Int -> TypeName.INT
                is IrTypePrimitive.PrimitiveType.Long -> TypeName.LONG
                is IrTypePrimitive.PrimitiveType.Char -> TypeName.CHAR
                is IrTypePrimitive.PrimitiveType.Float -> TypeName.FLOAT
                is IrTypePrimitive.PrimitiveType.Double -> TypeName.DOUBLE
                else -> throw IllegalArgumentException("Unsupported Java primitive type")
            }
        }

        is IrFunctionType -> throw NotImplementedError("Function types support not implemented for Java")
    }
}