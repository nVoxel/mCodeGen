package com.voxeldev.mcodegen.dsl.language.swift

import com.voxeldev.mcodegen.dsl.ir.IrCallable
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.language.base.LanguageModule
import com.voxeldev.mcodegen.dsl.language.swift.ir.serialization.allSerializers
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.scenario.scenarioScopeDto
import com.voxeldev.mcodegen.dsl.source.edit.scenario.EditScenario
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationMapper
import io.outfoxx.swiftpoet.FileSpec
import kotlinx.serialization.json.Json
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.pathString
import com.voxeldev.mcodegen.dsl.language.swift.souce.generate.extensions.convertClass as convertIrClass
import com.voxeldev.mcodegen.dsl.language.swift.souce.generate.extensions.convertField as convertIrField
import com.voxeldev.mcodegen.dsl.language.swift.souce.generate.extensions.convertFunction as convertIrMethod

object SwiftModule : LanguageModule {

    const val SWIFT_INDENT_PROPERTY_NAME = "indent"
    private const val SWIFT_INDENT_PROPERTY_DEFAULT_VALUE = "    "

    const val SWIFT_FILE_MODULE = "swiftModule"

    override val languageName: String = "swift"

    private val swiftCompanionExecPath = Path("swift-companion", ".build", "release", "swift-companion")

    private val json = Json {
        serializersModule = allSerializers
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    /**
     * Before using this method don't forget to build swift-companion tool using this command:
     * `cd swift-companion &&  swift build -c release --product swift-companion`.
     */
    context(ScenarioScope)
    override fun parse(sourcePath: String): IrFile {
        val scopeSerialized = Files.createTempFile("scenario-scope-", ".json").toFile()
        scopeSerialized.writeText(
            Json.encodeToString(
                scenarioScopeDto(this@ScenarioScope),
            )
        )

        val companionArgs = mapOf(
            "-scope" to scopeSerialized.absolutePath,
            "-source" to sourcePath,
        ).flatMap { entry ->
            listOf(entry.key, entry.value)
        }.toTypedArray()

        val swiftCompanionOutputJson = ProcessBuilder(
            swiftCompanionExecPath.toAbsolutePath().pathString,
            *companionArgs,
        )
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .start()
            .inputStream
            .readAllBytes()
            .decodeToString()

        return runCatching {
            json.decodeFromString<IrFile>(swiftCompanionOutputJson)
        }.getOrElse {
            error("Failed to decode swift-companion response JSON. Output was:\n$swiftCompanionOutputJson")
        }
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

                is IrCallable -> {
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
        TODO()
    }
}