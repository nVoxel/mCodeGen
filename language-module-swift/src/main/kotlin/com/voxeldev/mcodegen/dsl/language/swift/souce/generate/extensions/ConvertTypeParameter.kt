package com.voxeldev.mcodegen.dsl.language.swift.souce.generate.extensions

import com.voxeldev.mcodegen.dsl.ir.IrTypeGeneric
import com.voxeldev.mcodegen.dsl.ir.IrTypeParameter
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import com.voxeldev.mcodegen.dsl.language.swift.SwiftModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import io.outfoxx.swiftpoet.TypeVariableName

context(SwiftModule, ScenarioScope)
internal fun convertTypeParameter(irTypeParameter: IrTypeParameter): TypeVariableName {
    val bounds = irTypeParameter.extendsList.map { irType ->
        when (irType) {
            is IrTypeGeneric, is IrTypeReference -> {
                val typeSpec = convertType(irType)
                TypeVariableName.bound(typeSpec)
            }
            else -> throw IllegalArgumentException("This generic type is not supported in Swift")
        }
    }

    return TypeVariableName.typeVariable(
        name = irTypeParameter.name,
        bounds = bounds,
    )
}