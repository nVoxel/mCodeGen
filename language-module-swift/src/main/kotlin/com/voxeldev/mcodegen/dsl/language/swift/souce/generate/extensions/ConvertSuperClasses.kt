package com.voxeldev.mcodegen.dsl.language.swift.souce.generate.extensions

import com.voxeldev.mcodegen.dsl.ir.IrClassKind
import com.voxeldev.mcodegen.dsl.ir.IrSuperClass
import com.voxeldev.mcodegen.dsl.language.swift.SwiftModule
import com.voxeldev.mcodegen.dsl.language.swift.ir.getSwiftElementModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import io.outfoxx.swiftpoet.DeclaredTypeName
import io.outfoxx.swiftpoet.TypeSpec
import io.outfoxx.swiftpoet.parameterizedBy

context(SwiftModule, ScenarioScope)
internal fun convertSuperClasses(
    superClasses: List<IrSuperClass>,
    poetClassBuilder: TypeSpec.Builder
) {
    // check constraint
    val extends = superClasses.filter { it.kind == IrClassKind.IrClassClassKind }
    require(extends.size < 2) { "Swift class cannot have more then one superclass" }

    superClasses.forEach { superType ->
        val superTypeName = DeclaredTypeName.typeName(
            superType.getSwiftElementModule() + superType.superClassSimpleName
        )
        val superTypeTypeParams = superType.types.map { typeParam ->
            convertType(typeParam)
        }.toTypedArray()

        if (superTypeTypeParams.isNotEmpty()) {
            poetClassBuilder.addSuperType(superTypeName.parameterizedBy(*superTypeTypeParams))
        } else {
            poetClassBuilder.addSuperType(superTypeName)
        }
    }
}