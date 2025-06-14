package com.voxeldev.mcodegen.dsl.language.java

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.ir.builders.irFile
import com.voxeldev.mcodegen.dsl.language.base.LanguageModule
import com.voxeldev.mcodegen.dsl.language.java.source.generate.extensions.convertClass
import com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions.JAVA_STATIC_IMPORT_REFERENCE
import com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions.convertClasses
import com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions.convertImports
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.edit.scenario.EditScenario
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationMapper
import com.voxeldev.mcodegen.dsl.utils.GlobalFileUtils
import com.voxeldev.mcodegen.dsl.utils.GlobalFileUtils.asString
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

object JavaModule : LanguageModule {

    const val JAVA_INDENT_PROPERTY_NAME = "indent"
    private const val JAVA_INDENT_PROPERTY_DEFAULT_VALUE = "    "

    const val JAVA_PSI_CLASS = "psi_class"
    const val JAVA_FILE_PACKAGE = "jvmPackage"

    override val languageName: String = "java"

    context(ScenarioScope)
    override fun parse(sourcePath: String): IrFile {
        val pathToFile = Path(scenarioConfiguration.sourcesDir, sourcePath)
        val fileName = pathToFile.fileName.toString()

        val codeString = File(pathToFile.toString()).asString()

        val psiFile = GlobalFileUtils.parseJavaFile(codeString, fileName)
        val irFileBuilder = irFile(fileName)

        irFileBuilder.addLanguageProperty(JAVA_FILE_PACKAGE, psiFile.packageName)

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
        val mappedSource = mappers.fold(source) { acc, mapper -> mapper.map(acc) }

        val filePackage = mappedSource.languageProperties[JAVA_FILE_PACKAGE] as? String
            ?: throw IllegalStateException("Package not found in the IrFile for Java")

        val staticImports = mappedSource.imports.filter { import ->
            import.languageProperties[PsiModifier.STATIC] == true
                    && import.languageProperties[JAVA_STATIC_IMPORT_REFERENCE] is String
        }.map { import ->
            Pair(
                first = import.path,
                second = if (import.isWildcard) "*" else import.languageProperties[JAVA_STATIC_IMPORT_REFERENCE] as String
            )
        }

        val irClass = mappedSource.declarations.run {
            if (size > 1) {
                throw IllegalStateException("Java file currently cannot have more than one class")
            }
            mapNotNull { irElement -> irElement as? IrClass }
        }.run {
            if (isEmpty()) return
            first()
        }

        val poetFileBuilder = JavaFile.builder(filePackage, convertClass(irClass))

        staticImports.forEach { (className, referenceName) ->
            poetFileBuilder.addStaticImport(ClassName.bestGuess(className), referenceName)
        }

        val indentProperty = scenarioConfiguration.properties.find { property ->
            property.language == languageName && property.propertyName == JAVA_INDENT_PROPERTY_NAME
        }?.propertyValue as? String
        poetFileBuilder.indent(indentProperty ?: JAVA_INDENT_PROPERTY_DEFAULT_VALUE)

        val outputPath = Path(scenarioConfiguration.outputDir, applyToBasePath)
        val outputFile = File(outputPath.toString())

        poetFileBuilder.build().writeToFile(outputFile)
    }

    context(ScenarioScope)
    override fun edit(sourcePath: String, editScenario: EditScenario) {
        val pathToFile = Path(scenarioConfiguration.sourcesDir, sourcePath)
        val fileName = pathToFile.fileName.toString()

        val initialCodeString = File(pathToFile.toString()).asString()

        val modifiedCodeString = editScenario.getSteps().fold(initialCodeString) { acc, editStep ->
            val editStepHandler = scenarioConfiguration.editStepHandlers[editStep.name]
                ?: error("EditStepHandler for ${editStep.name} not found")
            editStepHandler.handleAnyStep(editStep, acc)
        }

        val outputPath = Path(scenarioConfiguration.outputDir, fileName)
        Files.createDirectories(Path(scenarioConfiguration.outputDir))
        File(outputPath.toString()).writeText(modifiedCodeString)
    }
}
