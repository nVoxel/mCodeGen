package com.voxeldev.mcodegen.dsl.language.kotlin

import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.ir.builders.irFile
import com.voxeldev.mcodegen.dsl.language.base.LanguageModule
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.convertClasses
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.convertImports
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.edit.scenario.EditScenario
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationMapper
import com.voxeldev.mcodegen.utils.GlobalCompilerUtils
import com.voxeldev.mcodegen.utils.GlobalFileUtils
import com.voxeldev.mcodegen.utils.GlobalFileUtils.asString
import org.jetbrains.kotlin.psi.KtClassOrObject
import java.io.File
import kotlin.io.path.Path

object KotlinModule : LanguageModule {

    init {
        // note: since some Kotlin version most stdlib sources should be in the kotlin-stdlib artifact
        // but our version is quite old, so we're using kotlin-stdlib-common artifact for stdlib source roots
        GlobalCompilerUtils.addKtSourceRoot(File("/Users/${System.getProperty("user.name")}/Downloads/kotlin-stdlib-1.3.61-sources"))
        GlobalCompilerUtils.addKtSourceRoot(File("/Users/${System.getProperty("user.name")}/Downloads/kotlin-stdlib-common-1.3.61-sources"))
    }

    override val languageName: String = "kotlin"

    context(ScenarioScope)
    override fun parse(sourcePath: String): IrFile {
        val pathToFile = Path(scenarioConfiguration.sourcesDir, sourcePath)
        val fileName = pathToFile.fileName.toString()

        val codeString = File(pathToFile.toString()).asString()

        val ktFile = GlobalFileUtils.parseKotlinFile(codeString, fileName)
        val bindingContext = GlobalCompilerUtils.getAnalysisResult(ktFile).bindingContext

        val irFileBuilder = irFile(fileName)

        with(bindingContext) {
            convertImports(ktFile.importList, irFileBuilder)

            // TODO: convert top level declarations

            convertClasses(ktFile.declarations.filterIsInstance<KtClassOrObject>(), irFileBuilder)
        }

        return irFileBuilder.build()
    }

    context(ScenarioScope)
    override fun generate(
        source: IrFile,
        applyToBasePath: String,
        mappers: List<GenerationMapper>
    ) {
        TODO("Not yet implemented")
    }

    context(ScenarioScope)
    override fun edit(
        sourcePath: String,
        editScenario: EditScenario
    ) {
        TODO("Not yet implemented")
    }
}