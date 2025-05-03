package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.builders.IrFileBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irImport
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.resolve.BindingContext

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertImports(ktImports: KtImportList?, irFileBuilder: IrFileBuilder) {
    ktImports?.imports?.forEach { ktImport ->
        val path = ktImport.importedFqName?.asString() ?: return@forEach
        irFileBuilder.addImport(
            import = irImport(
                path = path,
                isWildcard = ktImport.isAllUnder,
            ).build()
        )
    }
}