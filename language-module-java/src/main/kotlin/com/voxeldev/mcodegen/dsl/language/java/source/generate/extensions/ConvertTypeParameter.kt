package com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions

import com.squareup.javapoet.TypeVariableName
import com.voxeldev.mcodegen.dsl.ir.IrTypeGeneric
import com.voxeldev.mcodegen.dsl.ir.IrTypeParameter
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(JavaModule, ScenarioScope)
internal fun convertTypeParameter(irTypeParameter: IrTypeParameter): TypeVariableName {
    val bounds = irTypeParameter.extendsList.map { irType ->
        when (irType) {
            is IrTypeGeneric, is IrTypeReference -> convertType(irType)
            else -> throw IllegalArgumentException("This generic type is not supported in Java")
        }
    }.toTypedArray()

    return TypeVariableName.get(irTypeParameter.name, *bounds)
}