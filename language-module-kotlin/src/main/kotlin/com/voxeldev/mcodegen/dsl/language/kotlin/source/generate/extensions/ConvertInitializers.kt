package com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec
import com.voxeldev.mcodegen.dsl.ir.IrClassInitializer
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope

context(KotlinModule, ScenarioScope)
internal fun convertInitializers(
    initializers: List<IrClassInitializer>,
    poetClassBuilder: TypeSpec.Builder,
) {
    initializers.forEach { irClassInitializer ->
        poetClassBuilder.addInitializerBlock(convertInitializer(irClassInitializer))
    }
}

context(KotlinModule, ScenarioScope)
private fun convertInitializer(irClassInitializer: IrClassInitializer): CodeBlock {
    val poetCodeBlock = CodeBlock.builder()
    irClassInitializer.body?.statements?.forEach { irStatement ->
        poetCodeBlock.add(convertStatement(irStatement))
    }
    return poetCodeBlock.build()
}
