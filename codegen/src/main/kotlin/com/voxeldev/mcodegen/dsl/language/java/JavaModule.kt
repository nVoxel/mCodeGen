package com.voxeldev.mcodegen.dsl.language.java

import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.builders.IrClassBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irFile
import com.voxeldev.mcodegen.dsl.language.base.LanguageModule
import com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions.convertClasses
import com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions.convertImports
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.edit.scenario.EditScenario
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationMapper
import com.voxeldev.mcodegen.utils.GlobalFileUtils
import com.voxeldev.mcodegen.utils.GlobalFileUtils.asString
import java.io.File
import kotlin.io.path.Path

object JavaModule : LanguageModule {

    internal const val PSI_CLASS = "psi_class"

    internal val visitedClasses = hashMapOf<String, IrClassBuilder>()
    internal val resolvedTypes = hashMapOf<String, IrType>()

    context(ScenarioScope)
    override fun parse(sourcePath: String): IrFile {
        val pathToFile = Path(scenarioConfiguration.sourcesDir, sourcePath)
        val fileName = pathToFile.fileName.toString()

        val codeString = File(pathToFile.toString()).asString()

        val psiFile = GlobalFileUtils.parseJavaFile(codeString, fileName)
        val irFileBuilder = irFile(fileName)

        visitedClasses.clear()
        resolvedTypes.clear()

        irFileBuilder.addLanguageProperty("package", psiFile.packageName)

        psiFile.importList?.let { psiImports -> convertImports(psiImports, irFileBuilder) }

        convertClasses(psiFile.classes, irFileBuilder)

        return irFileBuilder.build()
    }

    context(ScenarioScope)
    override fun generate(
        source: IrFile,
        applyToBasePath: String,
        mappers: List<GenerationMapper>,
    ) {
        TODO()
    }

    context(ScenarioScope)
    override fun edit(sourcePath: String, editScenario: EditScenario) {
        TODO()
    }
}
