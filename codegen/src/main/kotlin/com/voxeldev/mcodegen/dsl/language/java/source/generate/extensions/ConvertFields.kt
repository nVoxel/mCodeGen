package com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeSpec
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind
import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier
import javax.lang.model.element.Modifier
import kotlin.collections.forEach

context(JavaModule, ScenarioScope)
internal fun convertFields(
    irClass: IrClass,
    irFields: List<IrField>,
    poetClassBuilder: TypeSpec.Builder,
) {
    irFields.forEach { irField ->
        poetClassBuilder.addField(convertField(irClass, irField))
    }
}

context(JavaModule, ScenarioScope)
private fun convertField(
    irClass: IrClass,
    irField: IrField,
): FieldSpec {
    return FieldSpec.builder(convertType(irField.type), irField.name).apply {
        if (irClass.kind != IrClassKind.INTERFACE) {
            when (irField.visibility) {
                is IrVisibilityPublic -> addModifiers(Modifier.PUBLIC)
                is IrVisibilityProtected -> addModifiers(Modifier.PROTECTED)
                is IrVisibilityPrivate -> addModifiers(Modifier.PRIVATE)
            }
        }

        if (irField.languageProperties[PsiModifier.STATIC] == true) {
            addModifiers(Modifier.STATIC)
        }

        if (!irField.isMutable) {
            addModifiers(Modifier.FINAL)
        }

        irField.initializer?.let { initializerIrStatement ->
            initializer(convertStatement(initializerIrStatement, addSemicolon = false, addLineBreak = false))
        }
    }.build()
}