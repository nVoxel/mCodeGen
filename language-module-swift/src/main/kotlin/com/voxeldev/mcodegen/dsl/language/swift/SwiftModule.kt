package com.voxeldev.mcodegen.dsl.language.swift

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.ir.IrMethod
import com.voxeldev.mcodegen.dsl.language.base.LanguageModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.edit.scenario.EditScenario
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationMapper
import com.voxeldev.mcodegen.dsl.utils.GlobalFileUtils.asString
import io.outfoxx.swiftpoet.FileSpec
import java.io.File
import kotlin.io.path.Path
import com.voxeldev.mcodegen.dsl.language.swift.souce.generate.extensions.convertClass as convertIrClass
import com.voxeldev.mcodegen.dsl.language.swift.souce.generate.extensions.convertField as convertIrField
import com.voxeldev.mcodegen.dsl.language.swift.souce.generate.extensions.convertFunction as convertIrMethod

object SwiftModule : LanguageModule {

    const val SWIFT_INDENT_PROPERTY_NAME = "indent"
    private const val SWIFT_INDENT_PROPERTY_DEFAULT_VALUE = "    "

    const val SWIFT_FILE_MODULE = "swiftModule"

    override val languageName: String = "swift"

    context(ScenarioScope)
    override fun parse(sourcePath: String): IrFile {
        TODO("Not yet implemented")
    }

    context(ScenarioScope)
    override fun generate(
        source: IrFile,
        applyToBasePath: String,
        mappers: List<GenerationMapper>
    ) {
        val mappedSource = mappers.fold(source) { acc, mapper -> mapper.map(acc) }

        val fileModule = mappedSource.languageProperties[SWIFT_FILE_MODULE] as? String
        val poetFileBuilder = fileModule?.let {
            FileSpec.builder(fileModule, mappedSource.name.substringBeforeLast("."))
        } ?: FileSpec.builder(mappedSource.name.substringBeforeLast("."))

        mappedSource.declarations.forEach { declaration ->
            when (declaration) {
                is IrClass -> {
                    poetFileBuilder.addType(convertIrClass(declaration))
                }

                is IrMethod -> {
                    poetFileBuilder.addFunction(
                        convertIrMethod(
                            irMethod = declaration,
                            isTopLevel = true,
                        )
                    )
                }

                is IrField -> {
                    poetFileBuilder.addProperty(
                        convertIrField(
                            irField = declaration,
                            isTopLevel = true
                        )
                    )
                }

                else -> throw IllegalArgumentException("Unknown top-level declaration for Swift")
            }
        }

        val indentProperty = scenarioConfiguration.properties.find { property ->
            property.language == languageName && property.propertyName == SWIFT_INDENT_PROPERTY_NAME
        }?.propertyValue as? String
        poetFileBuilder.indent(indentProperty ?: SWIFT_INDENT_PROPERTY_DEFAULT_VALUE)

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