package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrClassClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrInterfaceClassKind
import com.voxeldev.mcodegen.dsl.ir.IrSuperClass
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(KotlinModule, ScenarioScope)
internal fun convertSuperClasses(
    superClasses: List<IrSuperClass>,
    poetClassBuilder: TypeSpec.Builder,
) {
    val extends = superClasses.filter { it.kind == IrClassClassKind }.run {
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
            poetClassBuilder.superclass(superClassName.parameterizedBy(superClassTypes))
        } else {
            poetClassBuilder.superclass(superClassName)
        }
    }

    val implements = superClasses.filter { it.kind == IrInterfaceClassKind }

    implements.forEach { implementedInterface ->
        val superInterfaceName = ClassName.bestGuess(implementedInterface.superClassName)
        val superInterfaceTypes = implementedInterface.types.map { typeParam ->
            convertType(typeParam)
        }

        if (superInterfaceTypes.isNotEmpty()) {
            poetClassBuilder.addSuperinterface(superInterfaceName.parameterizedBy(superInterfaceTypes))
        } else {
            poetClassBuilder.addSuperinterface(superInterfaceName)
        }
    }
}