package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeVariableName
import com.voxeldev.mcodegen.dsl.ir.IrGeneric
import com.voxeldev.mcodegen.dsl.ir.IrTypeParameter
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_REIFIED
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_VARIANCE
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.types.Variance

context(KotlinModule, ScenarioScope)
internal fun convertTypeParameter(irTypeParameter: IrTypeParameter): TypeVariableName {
    val bounds = irTypeParameter.extendsList.map { irType ->
        when (irType) {
            is IrGeneric, is IrTypeReference -> convertType(irType)
            else -> throw IllegalArgumentException("This generic type is not supported in Kotlin")
        }
    }

    return TypeVariableName(
        name = irTypeParameter.name,
        bounds = bounds,
        variance = (irTypeParameter.languageProperties[KT_VARIANCE] as? String)?.let { variance ->
            when (variance) {
                Variance.IN_VARIANCE.label -> KModifier.IN
                Variance.OUT_VARIANCE.label -> KModifier.OUT
                else -> null
            }
        },
    ).copy(
        reified = irTypeParameter.languageProperties[KT_REIFIED] == true,
    )
}
