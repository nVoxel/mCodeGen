package com.voxeldev.mcodegen.dsl.language.swift.souce.generate.extensions

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityInternal
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.swift.SwiftModule
import com.voxeldev.mcodegen.dsl.language.swift.ir.IrActorClassKind
import com.voxeldev.mcodegen.dsl.language.swift.ir.IrStructClassKind
import com.voxeldev.mcodegen.dsl.language.swift.ir.IrVisibilityFileprivate
import com.voxeldev.mcodegen.dsl.language.swift.ir.IrVisibilityOpen
import com.voxeldev.mcodegen.dsl.language.swift.ir.getSwiftElementModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import io.outfoxx.swiftpoet.DeclaredTypeName
import io.outfoxx.swiftpoet.Modifier
import io.outfoxx.swiftpoet.TypeSpec
import org.jetbrains.kotlin.lexer.KtTokens

context(SwiftModule, ScenarioScope)
internal fun convertClass(irClass: IrClass): TypeSpec {
    val className = DeclaredTypeName(
        moduleName = irClass.getSwiftElementModule(),
        simpleName = irClass.simpleName,
    )

    val poetClassBuilder = when (irClass.kind) {
        is IrClassKind.IrClassClassKind -> {
            TypeSpec.classBuilder(className)
        }

        is IrClassKind.IrInterfaceClassKind -> {
            TypeSpec.protocolBuilder(className)
        }

        is IrClassKind.IrEnumClassKind -> {
            TypeSpec.enumBuilder(className)
        }

        is IrStructClassKind -> {
            TypeSpec.structBuilder(className)
        }

        is IrActorClassKind -> {
            TypeSpec.actorBuilder(className)
        }

        // TODO: support kotlin objects, think of annotation classes support in any way
        else -> throw IllegalArgumentException("Unsupported class kind : ${irClass.kind}")
    }.apply {
        when (irClass.visibility) {
            is IrVisibilityPrivate -> addModifiers(Modifier.PRIVATE)

            is IrVisibilityPublic -> {
                if (irClass.languageProperties[KtTokens.OPEN_KEYWORD.value] == true) {
                    addModifiers(Modifier.OPEN)
                } else {
                    addModifiers(Modifier.PUBLIC)
                }
            }

            is IrVisibilityInternal -> addModifiers(Modifier.INTERNAL)

            is IrVisibilityOpen -> addModifiers(Modifier.OPEN)

            is IrVisibilityFileprivate -> addModifiers(Modifier.FILEPRIVATE)
        }

        if (irClass.languageProperties[KtTokens.FINAL_KEYWORD.value] == true) {
            addModifiers(Modifier.FINAL)
        }

        // TODO: support swift-specific modifiers

        // TODO: think of annotations support in any way

        irClass.typeParameters.forEach { irTypeParameter ->
            addTypeVariable(convertTypeParameter(irTypeParameter))
        }

        convertSuperClasses(irClass.superClasses, this)

        convertFields(irClass.fields, this)

        convertFunctions(irClass.methods, this)

        // TODO: support initializers

        irClass.nestedClasses.forEach { nestedIrClass ->
            addType(convertClass(irClass = nestedIrClass))
        }
    }

    return poetClassBuilder.build()
}

