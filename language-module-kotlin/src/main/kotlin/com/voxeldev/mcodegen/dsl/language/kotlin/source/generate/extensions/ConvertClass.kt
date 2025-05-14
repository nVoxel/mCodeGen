package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrAnnotationClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrClassClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrEnumClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrInterfaceClassKind
import com.voxeldev.mcodegen.dsl.ir.IrParameter
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityInternal
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IrObjectClassKind
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_SUPERCLASS_CTOR_PARAMETERS
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.lexer.KtTokens

context(KotlinModule, ScenarioScope)
internal fun convertClass(irClass: IrClass): TypeSpec {
    val name = irClass.languageProperties["simpleName"] as? String ?: irClass.name

    val poetClassBuilder = when (irClass.kind) {
        is IrClassClassKind -> TypeSpec.classBuilder(name)

        is IrInterfaceClassKind -> TypeSpec.interfaceBuilder(name)

        is IrEnumClassKind -> TypeSpec.enumBuilder(name)

        is IrAnnotationClassKind -> TypeSpec.annotationBuilder(name)

        is IrObjectClassKind -> {
            if (irClass.languageProperties[KtTokens.COMPANION_KEYWORD.value] == true) {
                TypeSpec.companionObjectBuilder(name)
            } else {
                TypeSpec.objectBuilder(name)
            }
        }

        else -> throw IllegalArgumentException("Unsupported class kind : ${irClass.kind}")
    }.apply {
        when (irClass.visibility) {
            is IrVisibilityProtected -> addModifiers(KModifier.PROTECTED)
            is IrVisibilityInternal -> addModifiers(KModifier.INTERNAL)
            is IrVisibilityPrivate -> addModifiers(KModifier.PRIVATE)
            else -> addModifiers(KModifier.PUBLIC)
        }

        if (irClass.languageProperties[KtTokens.ABSTRACT_KEYWORD.value] == true) {
            addModifiers(KModifier.ABSTRACT)
        }

        if (irClass.languageProperties[KtTokens.DATA_KEYWORD.value] == true) {
            addModifiers(KModifier.DATA)
        }

        if (irClass.languageProperties[KtTokens.FINAL_KEYWORD.value] == true) {
            addModifiers(KModifier.FINAL)
        }

        if (irClass.languageProperties[KtTokens.OPEN_KEYWORD.value] == true) {
            addModifiers(KModifier.OPEN)
        }

        if (irClass.languageProperties[KtTokens.INNER_KEYWORD.value] == true) {
            addModifiers(KModifier.INNER)
        }

        if (irClass.languageProperties[KtTokens.SEALED_KEYWORD.value] == true) {
            addModifiers(KModifier.SEALED)
        }

        // TODO: convert annotations

        irClass.typeParameters.forEach { irTypeParameter ->
            addTypeVariable(convertTypeParameter(irTypeParameter))
        }

        convertSuperClasses(irClass.superClasses, this)

        val superClassConstructorParams = irClass.languageProperties[KT_SUPERCLASS_CTOR_PARAMETERS] as? List<*>
        superClassConstructorParams
            ?.filterIsInstance<IrParameter>()
            ?.forEach { superClassConstructorParam ->
                val value = superClassConstructorParam.defaultValue ?: return@forEach
                // addSuperclassConstructorParameter() // TODO: convert expression
            }

        convertFields(irClass.fields, this)

        convertFunctions(irClass.methods, this)

        // TODO: convert initializers

        irClass.nestedClasses.forEach { nestedIrClass ->
            addType(convertClass(irClass = nestedIrClass))
        }
    }

    return poetClassBuilder.build()
}
