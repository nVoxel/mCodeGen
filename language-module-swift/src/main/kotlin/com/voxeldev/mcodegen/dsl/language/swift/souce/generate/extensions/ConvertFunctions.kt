package com.voxeldev.mcodegen.dsl.language.swift.souce.generate.extensions

import com.voxeldev.mcodegen.dsl.ir.IrConstructor
import com.voxeldev.mcodegen.dsl.ir.IrCallable
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityInternal
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.language.swift.SwiftModule
import com.voxeldev.mcodegen.dsl.language.swift.ir.IrVisibilityFileprivate
import com.voxeldev.mcodegen.dsl.language.swift.ir.IrVisibilityOpen
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import io.outfoxx.swiftpoet.FunctionSpec
import io.outfoxx.swiftpoet.Modifier
import io.outfoxx.swiftpoet.ParameterSpec
import io.outfoxx.swiftpoet.TypeSpec
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier
import org.jetbrains.kotlin.lexer.KtTokens

context(SwiftModule, ScenarioScope)
internal fun convertFunctions(
    irMethods: List<IrCallable>,
    poetClassBuilder: TypeSpec.Builder,
    isTopLevel: Boolean = false,
) {
    irMethods.forEach { irMethod ->
        poetClassBuilder.addFunction(convertFunction(irMethod, isTopLevel))
    }
}

context(SwiftModule, ScenarioScope)
internal fun convertFunction(
    irMethod: IrCallable,
    isTopLevel: Boolean,
) : FunctionSpec {
    val poetFunction = if (irMethod is IrConstructor) {
        FunctionSpec.constructorBuilder()
    } else {
        FunctionSpec.builder(irMethod.name).apply {
            returns(convertType(irMethod.returnType))
        }
    }

    poetFunction.apply {
        val modifiers = getModifiers(irMethod, isTopLevel)
        addModifiers(*modifiers)

        irMethod.parameters.forEach { parameter ->
            val poetParameter = ParameterSpec.builder(parameter.name, convertType(parameter.type)).apply {
                // TODO: annotations?

                parameter.defaultValue?.let { defaultValue ->
                    // TODO: support expressions
                }
            }.build()

            addParameter(poetParameter)
        }

        irMethod.typeParameters.forEach { typeParameter ->
            addTypeVariable(convertTypeParameter(typeParameter))
        }

        // TODO: other constructor call + method body
        //  NOTE: should have convenient modifier if other constructor call present
        //  also make sure that super and this call are handled properly
    }

    return poetFunction.build()
}

context(SwiftModule, ScenarioScope)
private fun getModifiers(
    irMethod: IrCallable,
    isTopLevel: Boolean,
): Array<Modifier> {
    return buildList {
        when(irMethod.visibility) {
            is IrVisibilityPrivate ->  {
                if (isTopLevel) {
                    add(Modifier.FILEPRIVATE)
                } else {
                    add(Modifier.PRIVATE)
                }
            }

            is IrVisibilityPublic -> {
                if (irMethod.languageProperties[KtTokens.OPEN_KEYWORD.value] == true) {
                    add(Modifier.OPEN)
                } else {
                    add(Modifier.PUBLIC)
                }
            }

            is IrVisibilityInternal -> add(Modifier.INTERNAL)

            is IrVisibilityOpen -> add(Modifier.OPEN)

            is IrVisibilityFileprivate -> add(Modifier.FILEPRIVATE)
        }

        if (irMethod.languageProperties[PsiModifier.STATIC] == true) {
            add(Modifier.STATIC)
        }

        if (irMethod.languageProperties[KtTokens.FINAL_KEYWORD.value] == true) {
            add(Modifier.FINAL)
        }

        // TODO: support swift-specific modifiers
    }.toTypedArray()
}
