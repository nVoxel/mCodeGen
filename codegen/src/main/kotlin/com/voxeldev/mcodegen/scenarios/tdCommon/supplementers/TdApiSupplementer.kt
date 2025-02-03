package com.voxeldev.mcodegen.scenarios.tdCommon.supplementers

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.voxeldev.mcodegen.scenarios.tdCommon.TdCommonScenarioConstants
import com.voxeldev.mcodegen.scenarios.tdCommon.models.CommonClassSpec
import com.voxeldev.mcodegen.utils.GlobalLambdaUtils.lazyAnd
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

object TdApiSupplementer {

    fun supplement(
        outputPath: String,
        sourceCode: String,
        commonClasses: List<CommonClassSpec>
    ) {
        val commonClassesIndices = commonClasses
            .mapIndexed { index, commonClassSpec -> commonClassSpec.clazz.name to index }
            .toMap()

        val compilationUnit = JavaParser().parse(sourceCode).result.get()

        compilationUnit.addImport("com.voxeldev.tgdrive.*")

        val classDeclarations = compilationUnit.findAll(ClassOrInterfaceDeclaration::class.java)
        classDeclarations
            .filter { declaration ->
                (!declaration.extendedTypes.isNullOrEmpty())
                    .lazyAnd { commonClassesIndices.containsKey(declaration.nameAsString) }
            }
            .forEach { declaration ->
                if (declaration.implementedTypes.isNullOrEmpty()) {
                    declaration.addImplementedType("Td${declaration.name}")
                }

                declaration.fields
                    .filter { declarationField ->
                        declarationField.variables.find { variable -> variable.nameAsString == TdCommonScenarioConstants.CONSTRUCTOR_FIELD } == null
                    }
                    .forEach { declarationField ->
                        declarationField.createGetter().apply {
                            addAnnotation("Override")
                            if (typeAsString == "boolean" && nameAsString.startsWith("getIs") && nameAsString[5].isUpperCase()) {
                                setName(nameAsString.drop(3).replaceFirstChar { it.lowercaseChar() }) // removes get* prefix
                            }
                        }
                    }
            }

        Files.createDirectories(Paths.get(outputPath))
        Files.write(
            Paths.get(outputPath, TdCommonScenarioConstants.outputFileName),
            compilationUnit.toString().toByteArray(),
            StandardOpenOption.CREATE,
        )
    }
}