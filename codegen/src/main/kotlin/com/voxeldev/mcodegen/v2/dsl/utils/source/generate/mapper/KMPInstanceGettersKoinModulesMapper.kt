package com.voxeldev.mcodegen.v2.dsl.utils.source.generate.mapper

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.ir.builders.irBlockStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irField
import com.voxeldev.mcodegen.dsl.ir.builders.irFile
import com.voxeldev.mcodegen.dsl.ir.builders.irIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irLiteralExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irObjectCreationExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReference
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule.KOTLIN_FILE_PACKAGE
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.builders.irLambdaExpression
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.kotlinInternalVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.kotlinPublicVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_TOP_LEVEL_MEMBER_REFERENCE
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationListMapperBaseImpl
import com.voxeldev.mcodegen.v1.GlobalConstants
import com.voxeldev.mcodegen.v2.constants.TdCommonScenarioConstants
import org.jetbrains.kotlin.lexer.KtTokens

context(ScenarioScope)
fun kmpInstanceGettersKoinModulesMapper(
    newFileName: String = "InstanceGettersModule",
    packageName: String = GlobalConstants.TGDRIVE_PACKAGE,
    namePrefix: String = "Td",
): KMPInstanceGettersKoinModulesMapper {
    return KMPInstanceGettersKoinModulesMapper(packageName, newFileName, namePrefix)
}

context(ScenarioScope)
class KMPInstanceGettersKoinModulesMapper internal constructor(
    private val newPackage: String,
    private val newFileName: String,
    private val namePrefix: String,
) : GenerationListMapperBaseImpl() {

    private val moduleName = newFileName.replaceFirstChar { it.lowercaseChar() }

    override fun map(source: List<IrFile>): List<IrFile> {
        val tdApi = source.firstOrNull()
            ?.declarations
            ?.filterIsInstance<IrClass>()
            ?.find { it.qualifiedName == "org.drinkless.tdlib.TdApi" }
            ?: throw IllegalArgumentException("Provided source doesn't contain TdApi class")

        val koinModules = mutableListOf<IrFile>()

        var index = 0
        while (true) {
            val start = index * TdCommonScenarioConstants.MAX_LINES_PER_MODULE
            var end = start + TdCommonScenarioConstants.MAX_LINES_PER_MODULE

            if (start >= tdApi.nestedClasses.size) break
            if (end > tdApi.nestedClasses.size) end = tdApi.nestedClasses.size

            koinModules.add(
                createSeparateKoinModule(
                    classes = tdApi.nestedClasses.subList(start, end),
                    moduleIndex = ++index,
                )
            )
        }

        val mainModuleFile = irFile(newFileName).apply {
            addLanguageProperty(KOTLIN_FILE_PACKAGE, newPackage)
        }

        val moduleField = irField(
            name = moduleName,
            type = irTypeReference("org.koin.core.module.Module").apply {
                nullable(false)
            }.build(),
        ).apply {
            visibility(kotlinPublicVisibility())
            mutable(false)

            val initializerExpression = irMethodCallExpression(
                methodName = "module",
            ).apply {
                addLanguageProperty(KT_TOP_LEVEL_MEMBER_REFERENCE, "org.koin.dsl")

                val includesExpression = irMethodCallExpression(
                    methodName = "includes",
                ).apply {
                    val submodules = IntRange(1, index).map { currIndex ->
                        irIdentifierExpression(
                            irLiteralExpression(moduleName + currIndex).build()
                        ).build()
                    }

                    submodules.forEach { submodule ->
                        addValueArgument(submodule)
                    }
                }.build()

                val includesLambda = irLambdaExpression().apply {
                    val lambdaStatement = irExpressionStatement(includesExpression).build()

                    val lambdaBodyStatement = irBlockStatement().apply {
                        addStatement(lambdaStatement)
                    }.build()

                    body(lambdaBodyStatement)
                }.build()

                addValueArgument(includesLambda)
            }.build()

            initializer(irExpressionStatement(initializerExpression).build())
        }.build()

        mainModuleFile.addDeclaration(moduleField)

        return koinModules + mainModuleFile.build()
    }

    private fun createSeparateKoinModule(
        classes: List<IrClass>,
        moduleIndex: Int,
    ): IrFile {
        val moduleFile = irFile(newFileName + moduleIndex).apply {
            addLanguageProperty(KOTLIN_FILE_PACKAGE, newPackage)
        }

        val moduleField = irField(
            name = moduleName + moduleIndex,
            type = irTypeReference("org.koin.core.module.Module").apply {
                nullable(false)
            }.build(),
        ).apply {
            visibility(kotlinInternalVisibility())
            mutable(false)

            val initializerExpression = irMethodCallExpression(
                methodName = "module",
            ).apply {
                addLanguageProperty(KT_TOP_LEVEL_MEMBER_REFERENCE, "org.koin.dsl")

                val singleExpressions = classes
                    .filter { irClass -> irClass.languageProperties[KtTokens.ABSTRACT_KEYWORD.value] != true }
                    .map { irClass ->
                        irMethodCallExpression(
                            methodName = "single",
                        ).apply {
                            val instanceGetterInterfaceType = irTypeReference(
                                referencedClassName = newPackage + "." + namePrefix +
                                        irClass.simpleName + ".InstanceGetter"
                            ).apply {
                                nullable(false)
                            }.build()

                            val instanceGetterName = newPackage + "." + namePrefix +
                                    irClass.simpleName + "InstanceGetterImpl"

                            val instanceGetterCreationExpression =
                                irObjectCreationExpression(instanceGetterName).build()

                            val singleLambdaBody = irBlockStatement().apply {
                                addStatement(irExpressionStatement(instanceGetterCreationExpression).build())
                            }.build()

                            val singleLambda = irLambdaExpression().apply {
                                body(singleLambdaBody)
                            }.build()

                            addTypeArgument(instanceGetterInterfaceType)
                            addValueArgument(singleLambda)
                        }.build()
                    }

                val singlesLambda = irLambdaExpression().apply {
                    val lambdaStatements = singleExpressions.map { singleExpression ->
                        irExpressionStatement(singleExpression).build()
                    }

                    val lambdaBodyStatement = irBlockStatement().apply {
                        lambdaStatements.forEach { lambdaStatement ->
                            addStatement(lambdaStatement)
                        }
                    }.build()

                    body(lambdaBodyStatement)
                }.build()

                addValueArgument(singlesLambda)
            }.build()

            initializer(irExpressionStatement(initializerExpression).build())
        }.build()

        moduleFile.addDeclaration(moduleField)

        return moduleFile.build()
    }
}
