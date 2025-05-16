package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityInternal
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.lexer.KtTokens

context(KotlinModule, ScenarioScope)
internal fun convertFields(
    irFields: List<IrField>,
    poetClassBuilder: TypeSpec.Builder,
) {
    irFields.forEach { irField ->
        val fieldModifiers = getModifiers(irField)

        val poetProperty = PropertySpec.builder(irField.name, convertType(irField.type)).apply {
            addModifiers(fieldModifiers)

            mutable(irField.isMutable)

            irField.annotations.forEach { irAnnotation ->
                addAnnotation(convertAnnotation(irAnnotation))
            }

            irField.initializer?.let { initializer ->
                initializer(convertStatement(initializer, addLineBreak = false))
            }
        }

        poetClassBuilder.addProperty(poetProperty.build())
    }
}

context(KotlinModule, ScenarioScope)
private fun getModifiers(irField: IrField): List<KModifier> {
    return buildList {
        when(irField.visibility) {
            is IrVisibilityProtected -> add(KModifier.PROTECTED)
            is IrVisibilityInternal -> add(KModifier.INTERNAL)
            is IrVisibilityPrivate -> add(KModifier.PRIVATE)
            else -> add(KModifier.PUBLIC)
        }

        if (irField.languageProperties[KtTokens.ABSTRACT_KEYWORD.value] == true) {
            add(KModifier.ABSTRACT)
        }

        if (irField.languageProperties[KtTokens.FINAL_KEYWORD.value] == true) {
            add(KModifier.FINAL)
        }

        if (irField.languageProperties[KtTokens.OPEN_KEYWORD.value] == true) {
            add(KModifier.OPEN)
        }
    }
}
