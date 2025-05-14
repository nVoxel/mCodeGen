package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.builders.IrFileBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irImport
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiImportList
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier

const val JAVA_STATIC_IMPORT_REFERENCE = "staticImportReference"

context(JavaModule, ScenarioScope)
internal fun convertImports(psiImports: PsiImportList, irFileBuilder: IrFileBuilder) {
    psiImports.importStatements.forEach { importStatement ->
        val path = importStatement.qualifiedName ?: return@forEach
        irFileBuilder.addImport(
            import = irImport(
                path = path,
                isWildcard = importStatement.isOnDemand,
            ).build()
        )
    }

    psiImports.importStaticStatements.forEach { importStaticStatement ->
        val path = importStaticStatement.resolveTargetClass()?.qualifiedName ?: return@forEach
        val referenceName = importStaticStatement.referenceName ?: return@forEach

        irFileBuilder.addImport(
            import = irImport(
                path = path,
                isWildcard = importStaticStatement.isOnDemand,
            ).apply {
                addLanguageProperty(PsiModifier.STATIC, true)
                addLanguageProperty(JAVA_STATIC_IMPORT_REFERENCE, referenceName)
            }.build()
        )
    }
}