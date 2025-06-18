package com.voxeldev.mcodegen.dsl.language.kotlin

import com.squareup.kotlinpoet.FileSpec
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.ir.IrCallable
import com.voxeldev.mcodegen.dsl.ir.builders.irFile
import com.voxeldev.mcodegen.dsl.language.base.LanguageModule
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.convertImports
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.edit.scenario.EditScenario
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationListMapper
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationMapper
import com.voxeldev.mcodegen.dsl.utils.GlobalCompilerUtils
import com.voxeldev.mcodegen.dsl.utils.GlobalFileUtils
import com.voxeldev.mcodegen.dsl.utils.GlobalFileUtils.asString
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import java.io.File
import kotlin.io.path.Path
import com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions.convertClass as convertIrClass
import com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions.convertFunction as convertIrMethod
import com.voxeldev.mcodegen.dsl.language.kotlin.source.generate.extensions.convertField as convertIrField
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.convertClass as convertKtClass
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.convertFieldAsProperty as convertKtField
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.convertFunction as convertKtFunction

object KotlinModule : LanguageModule {

    init {
        // note: since some Kotlin version most stdlib sources should be in the kotlin-stdlib artifact
        // but our version is quite old, so we're using kotlin-stdlib-common artifact for stdlib source roots
        GlobalCompilerUtils.addKtSourceRoot(File("/Users/${System.getProperty("user.name")}/Downloads/kotlin-stdlib-1.3.61-sources"))
        GlobalCompilerUtils.addKtSourceRoot(File("/Users/${System.getProperty("user.name")}/Downloads/kotlin-stdlib-common-1.3.61-sources"))
    }

    const val KOTLIN_INDENT_PROPERTY_NAME = "indent"
    private const val KOTLIN_INDENT_PROPERTY_DEFAULT_VALUE = "    "

    const val KOTLIN_FILE_PACKAGE = "jvmPackage"

    override val languageName: String = "kotlin"

    context(ScenarioScope)
    override fun parse(sourcePath: String): IrFile {
        val pathToFile = Path(scenarioConfiguration.sourcesDir, sourcePath)
        val fileName = pathToFile.fileName.toString()

        val codeString = File(pathToFile.toString()).asString()

        val ktFile = GlobalFileUtils.parseKotlinFile(codeString, fileName)
        val bindingContext = GlobalCompilerUtils.getAnalysisResult(ktFile).bindingContext

        return parseSingleFile(fileName, ktFile, bindingContext)
    }

    /**
     * Allows to reuse acquired BindingContext, significantly speeds up parsing with large source roots.
     * @param files Stores pair alias-sourcePath, alias can be used later for map property delegation.
     */
    context(ScenarioScope)
    fun parseMultiple(vararg files: Pair<String, String>): Map<String, IrFile> {
        val aliases = files.map { it.first }
        val sourcePaths = files.map { it.second }

        val pathToFiles = sourcePaths.map { sourcePath ->
            Path(scenarioConfiguration.sourcesDir, sourcePath)
        }
        val fileNames = pathToFiles.map { pathToFile -> pathToFile.fileName.toString() }

        val codeStrings = pathToFiles.map { pathToFile -> File(pathToFile.toString()).asString() }

        val ktFiles = codeStrings.zip(fileNames).map { (codeString, fileName) ->
            GlobalFileUtils.parseKotlinFile(codeString, fileName)
        }

        val bindingContext = GlobalCompilerUtils.getAnalysisResult(*ktFiles.toTypedArray()).bindingContext

        val irFiles = fileNames.zip(ktFiles).map { (fileName, ktFile) ->
            parseSingleFile(fileName, ktFile, bindingContext)
        }

        return aliases.zip(irFiles).toMap()
    }

    context(ScenarioScope)
    private fun parseSingleFile(
        fileName: String,
        ktFile: KtFile,
        bindingContext: BindingContext,
    ): IrFile {
        val irFileBuilder = irFile(fileName)

        irFileBuilder.addLanguageProperty(KOTLIN_FILE_PACKAGE, ktFile.packageFqName.asString())

        with(bindingContext) {
            convertImports(ktFile.importList, irFileBuilder)

            ktFile.declarations.forEach { declaration ->
                when (declaration) {
                    is KtClassOrObject -> {
                        irFileBuilder.addDeclaration(
                            convertKtClass(declaration)
                        )
                    }

                    is KtFunction -> {
                        irFileBuilder.addDeclaration(
                            convertKtFunction(
                                ktClassOrObject = null,
                                ktFunction = declaration,
                            )
                        )
                    }

                    is KtProperty -> {
                        irFileBuilder.addDeclaration(
                            convertKtField(
                                ktClassOrObject = null,
                                ktField = declaration,
                            ) ?: throw IllegalArgumentException("Found Kotlin top-level field without name")
                        )
                    }

                    else -> {
                        println("Unknown top level declaration: $declaration")
                    }
                }
            }
        }

        return irFileBuilder.build()
    }

    context(ScenarioScope)
    fun generateMultiple(
        sources: List<IrFile>,
        applyToBasePath: String,
        mappers: List<GenerationListMapper>,
    ) {
        val mappedSources = mappers.fold(sources) { acc, mapper -> mapper.map(acc) }
        mappedSources.forEach { source ->
            generate(source, applyToBasePath, listOf())
        }
    }

    context(ScenarioScope)
    override fun generate(
        source: IrFile,
        applyToBasePath: String,
        mappers: List<GenerationMapper>
    ) {
        val mappedSource = mappers.fold(source) { acc, mapper -> mapper.map(acc) }

        val filePackage = mappedSource.languageProperties[KOTLIN_FILE_PACKAGE] as? String
            ?: throw IllegalStateException("Package not found in the IrFile for Kotlin")

        val poetFileBuilder = FileSpec.builder(filePackage, mappedSource.name.substringBeforeLast("."))

        mappedSource.declarations.forEach { declaration ->
            when (declaration) {
                is IrClass -> {
                    poetFileBuilder.addType(convertIrClass(declaration))
                }

                is IrCallable -> {
                    poetFileBuilder.addFunction(convertIrMethod(declaration))
                }

                is IrField -> {
                    poetFileBuilder.addProperty(convertIrField(declaration))
                }

                else -> throw IllegalArgumentException("Unknown top-level declaration for Kotlin")
            }
        }

        val indentProperty = scenarioConfiguration.properties.find { property ->
            property.language == languageName && property.propertyName == KOTLIN_INDENT_PROPERTY_NAME
        }?.propertyValue as? String
        poetFileBuilder.indent(indentProperty ?: KOTLIN_INDENT_PROPERTY_DEFAULT_VALUE)

        val outputPath = Path(scenarioConfiguration.outputDir, applyToBasePath)
        poetFileBuilder.build().writeTo(outputPath)
    }

    context(ScenarioScope)
    override fun edit(
        sourcePath: String,
        editScenario: EditScenario
    ) {
        val pathToFile = Path(scenarioConfiguration.sourcesDir, sourcePath)
        val fileName = pathToFile.fileName.toString()

        val initialCodeString = File(pathToFile.toString()).asString()

        val modifiedCodeString = editScenario.getSteps().fold(initialCodeString) { acc, editStep ->
            val editStepHandler = scenarioConfiguration.editStepHandlers[editStep.name]
                ?: error("EditStepHandler for ${editStep.name} not found")
            editStepHandler.handleAnyStep(editStep, acc)
        }

        val outputPath = Path(scenarioConfiguration.outputDir, fileName)
        File(outputPath.toString()).writeText(modifiedCodeString)
    }
}