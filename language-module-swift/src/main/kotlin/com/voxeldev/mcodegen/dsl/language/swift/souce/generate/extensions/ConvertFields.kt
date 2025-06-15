package com.voxeldev.mcodegen.dsl.language.swift.souce.generate.extensions

import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityInternal
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.swift.SwiftModule
import com.voxeldev.mcodegen.dsl.language.swift.ir.IrVisibilityFileprivate
import com.voxeldev.mcodegen.dsl.language.swift.ir.IrVisibilityOpen
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import io.outfoxx.swiftpoet.Modifier
import io.outfoxx.swiftpoet.PropertySpec
import io.outfoxx.swiftpoet.TypeSpec
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier
import org.jetbrains.kotlin.lexer.KtTokens

context(SwiftModule, ScenarioScope)
internal fun convertFields(
    irFields: List<IrField>,
    poetClassBuilder: TypeSpec.Builder,
    isTopLevel: Boolean = false,
) {
    irFields.forEach { irField ->
        poetClassBuilder.addProperty(
            convertField(irField, isTopLevel)
        )
    }
}
context(SwiftModule, ScenarioScope)
internal fun convertField(
    irField: IrField,
    isTopLevel: Boolean,
): PropertySpec {
    val fieldModifiers = getModifiers(irField, isTopLevel)

    return PropertySpec.builder(irField.name, convertType(irField.type)).apply {
        addModifiers(*fieldModifiers)

        mutable(irField.isMutable)

        // TODO: think of annotations support in any way

        // TODO: support initializer
    }.build()
}

context(SwiftModule, ScenarioScope)
private fun getModifiers(
    irField: IrField,
    isTopLevel: Boolean,
): Array<Modifier> {
    return buildList {
        when(irField.visibility) {
            is IrVisibilityPrivate ->  {
                if (isTopLevel) {
                    add(Modifier.FILEPRIVATE)
                } else {
                    add(Modifier.PRIVATE)
                }
            }

            is IrVisibilityPublic -> {
                if (irField.languageProperties[KtTokens.OPEN_KEYWORD.value] == true) {
                    add(Modifier.OPEN)
                } else {
                    add(Modifier.PUBLIC)
                }
            }

            is IrVisibilityInternal -> add(Modifier.INTERNAL)

            is IrVisibilityOpen -> add(Modifier.OPEN)

            is IrVisibilityFileprivate -> add(Modifier.FILEPRIVATE)
        }

        if (irField.languageProperties[PsiModifier.STATIC] == true) {
            add(Modifier.STATIC)
        }

        if (irField.languageProperties[KtTokens.FINAL_KEYWORD.value] == true) {
            add(Modifier.FINAL)
        }

        // TODO: support swift-specific modifiers
    }.toTypedArray()
}
