package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrAnnotationClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrClassClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrEnumClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrInterfaceClassKind
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityInternal
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IrObjectClassKind
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

        val extends = irClass.superClasses.filter { it.kind == IrClassClassKind }.run {
            if (size > 1) {
                throw IllegalStateException("Kotlin currently does not support more than one superclass")
            }

            firstOrNull()
        }

        extends?.let {
            val superClassName = ClassName.bestGuess(extends.superClassName)
            val superClassTypes = extends.types.map { typeParam ->
                convertType(typeParam)
            }

            if (superClassTypes.isNotEmpty()) {
                superclass(superClassName.parameterizedBy(superClassTypes))
            } else {
                superclass(superClassName)
            }
        }

        val implements = irClass.superClasses.filter { it.kind == IrInterfaceClassKind }

        implements.forEach { implementedInterface ->
            val superInterfaceName = ClassName.bestGuess(implementedInterface.superClassName)
            val superInterfaceTypes = implementedInterface.types.map { typeParam ->
                convertType(typeParam)
            }

            if (superInterfaceTypes.isNotEmpty()) {
                addSuperinterface(superInterfaceName.parameterizedBy(superInterfaceTypes))
            } else {
                addSuperinterface(superInterfaceName)
            }
        }

        irClass.nestedClasses.forEach { nestedIrClass ->
            addType(convertClass(irClass = nestedIrClass))
        }
    }

    return poetClassBuilder.build()
}
