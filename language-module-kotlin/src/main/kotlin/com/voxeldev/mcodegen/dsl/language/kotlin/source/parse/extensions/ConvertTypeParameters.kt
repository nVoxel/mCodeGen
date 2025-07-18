package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrTypeGeneric
import com.voxeldev.mcodegen.dsl.ir.IrTypeParameter
import com.voxeldev.mcodegen.dsl.ir.builders.IrClassBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.IrCallableBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeGeneric
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeParameter
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeParameterListOwner
import org.jetbrains.kotlin.resolve.BindingContext

const val KT_VARIANCE = "variance"
const val KT_REIFIED = "reified"

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertTypeParameters(
    ktClassOrObject: KtClassOrObject?,
    ktTypeParameters: List<KtTypeParameter>,
    irMethodBuilder: IrCallableBuilder,
) {
    val preloadedTypeParameters = preloadTypeParameters(ktTypeParameters)
    ktTypeParameters.forEach { ktTypeParameter ->
        irMethodBuilder.addTypeParameter(
            convertTypeParameter(ktClassOrObject, ktTypeParameter, preloadedTypeParameters)
        )
    }
}

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertTypeParameters(
    ktClassOrObject: KtClassOrObject,
    ktTypeParameters: List<KtTypeParameter>,
    irClassBuilder: IrClassBuilder,
) {
    val preloadedTypeParameters = preloadTypeParameters(ktTypeParameters)
    ktTypeParameters.forEach { ktTypeParameter ->
        irClassBuilder.addTypeParameter(
            convertTypeParameter(ktClassOrObject, ktTypeParameter, preloadedTypeParameters)
        )
    }
}

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertTypeParameter(
    ktClassOrObject: KtClassOrObject?,
    ktTypeParameter: KtTypeParameter,
    preloadedTypeParameters: Map<String, IrTypeGeneric>,
): IrTypeParameter {
    val name = ktTypeParameter.name!!

    val irTypeParameter = irTypeParameter(name)

    val directBound = ktTypeParameter.extendsBound?.typeElement?.let { directBoundType ->
        convertKtTypeElement(directBoundType, preloadedTypeParameters)
    }

    val whereBounds = (ktClassOrObject as? KtTypeParameterListOwner)
        ?.typeConstraintList?.constraints
        ?.filter { it.subjectTypeParameterName?.text == name }
        ?.mapNotNull {
            it.boundTypeReference?.typeElement?.let { bound ->
                convertKtTypeElement(bound, preloadedTypeParameters)
            }
        }
        .orEmpty()

    val bounds = listOfNotNull(directBound) + whereBounds

    val props = buildMap<String, Any> {
        put(KT_VARIANCE, ktTypeParameter.variance.label)
        if (ktTypeParameter.hasModifier(KtTokens.REIFIED_KEYWORD)) put(KT_REIFIED, true)
    }

    return irTypeParameter.apply {
        bounds.forEach { boundType ->
            addExtendsType(boundType)
        }

        props.forEach { (key, value) ->
            addLanguageProperty(key, value)
        }
    }.build()
}

context(KotlinModule, BindingContext, ScenarioScope)
internal fun preloadTypeParameters(ktTypeParameters: List<KtTypeParameter>): Map<String, IrTypeGeneric> {
    val genericSymbols = mutableMapOf<String, IrTypeGeneric>()

    ktTypeParameters.forEach { param ->
        val name = param.name!!
        genericSymbols.putIfAbsent(
            name,
            irTypeGeneric(name).apply {
                nullable(true)
            }.build()
        )
    }

    return genericSymbols
}