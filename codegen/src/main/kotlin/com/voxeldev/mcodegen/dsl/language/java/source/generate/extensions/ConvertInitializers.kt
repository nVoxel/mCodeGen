package com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions

import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.TypeSpec
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassInitializer
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(JavaModule, ScenarioScope)
internal fun convertInitializers(
    containingClass: IrClass,
    irClassInitializers: List<IrClassInitializer>,
    poetClassBuilder: TypeSpec.Builder,
) {
    irClassInitializers.forEach { irClassInitializer ->
        if (irClassInitializer.kind == IrClassInitializer.IrStaticClassInitializerKind) {
            poetClassBuilder.addStaticBlock(convertInitializer(containingClass, irClassInitializer))
        } else {
            poetClassBuilder.addInitializerBlock(convertInitializer(containingClass, irClassInitializer))
        }
    }
}

context(JavaModule, ScenarioScope)
private fun convertInitializer(
    containingClass: IrClass,
    irClassInitializer: IrClassInitializer,
): CodeBlock {
    val poetCodeBlock = CodeBlock.builder()
    irClassInitializer.body?.statements?.forEach { irStatement ->
        poetCodeBlock.add(convertStatement(containingClass, irStatement))
    }
    return poetCodeBlock.build()
}