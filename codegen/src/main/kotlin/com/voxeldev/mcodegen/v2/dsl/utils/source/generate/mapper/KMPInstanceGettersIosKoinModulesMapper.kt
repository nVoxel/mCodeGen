package com.voxeldev.mcodegen.v2.dsl.utils.source.generate.mapper

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.ir.builders.irBlockStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irClass
import com.voxeldev.mcodegen.dsl.ir.builders.irConstructor
import com.voxeldev.mcodegen.dsl.ir.builders.irExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irFile
import com.voxeldev.mcodegen.dsl.ir.builders.irIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irLiteralExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irMethod
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodBody
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irParameter
import com.voxeldev.mcodegen.dsl.ir.builders.irReturnStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReference
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule.KOTLIN_FILE_PACKAGE
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.builders.irLambdaExpression
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.kotlinInternalVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.kotlinPublicVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_PRIMARY_CTOR
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_PRIMARY_CTOR_VAL_OR_VAR_KEYWORD
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_TOP_LEVEL_MEMBER_REFERENCE
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationListMapperBaseImpl
import com.voxeldev.mcodegen.v1.GlobalConstants
import com.voxeldev.mcodegen.v2.constants.TdCommonScenarioConstants
import org.jetbrains.kotlin.lexer.KtTokens


context(ScenarioScope)
fun kmpInstanceGettersIosKoinModulesMapper(
    newFileName: String = "InstanceGettersModule",
    packageName: String = GlobalConstants.TGDRIVE_PACKAGE,
    namePrefix: String = "Td",
): KMPInstanceGettersIosKoinModulesMapper {
    return KMPInstanceGettersIosKoinModulesMapper(packageName, newFileName, namePrefix)
}

context(ScenarioScope)
class KMPInstanceGettersIosKoinModulesMapper internal constructor(
    private val newPackage: String,
    private val newFileName: String,
    private val namePrefix: String,
) : GenerationListMapperBaseImpl() {

    private val namePrefixLower = namePrefix.replaceFirstChar { it.lowercaseChar() }

    private val moduleName = "get$newFileName"

    override fun map(source: List<IrFile>): List<IrFile> {
        val tdApi = source.firstOrNull()
            ?.declarations
            ?.filterIsInstance<IrClass>()
            ?.find { it.name == "org.drinkless.tdlib.TdApi" }
            ?: throw IllegalArgumentException("Provided source doesn't contain TdApi class")

        val submodules: MutableList<List<IrClass>> = mutableListOf()

        var index = 0
        while (true) {
            val start = index * TdCommonScenarioConstants.MAX_LINES_PER_MODULE
            var end = start + TdCommonScenarioConstants.MAX_LINES_PER_MODULE

            if (start >= tdApi.nestedClasses.size) break
            if (end > tdApi.nestedClasses.size) end = tdApi.nestedClasses.size

            submodules.add(
                tdApi.nestedClasses.subList(start, end).filter { irClass ->
                    irClass.languageProperties[KtTokens.ABSTRACT_KEYWORD.value] != true
                }
            )

            index++
        }

        val koinModules = mutableListOf<IrFile>(createMainKoinModule(submodules))
        submodules.forEachIndexed { index, submoduleClasses ->
            koinModules.add(
                createSeparateKoinModule(
                    classes = submoduleClasses,
                    moduleIndex = index + 1,
                )
            )
        }

        return koinModules + createDependenciesClasses(submodules)
    }

    private fun createMainKoinModule(submodules: List<List<IrClass>>): IrFile {
        val mainModuleFile = irFile(newFileName).apply {
            addLanguageProperty(KOTLIN_FILE_PACKAGE, newPackage)
        }

        val moduleMethod = irMethod(
            name = moduleName,
            returnType = irTypeReference("org.koin.core.module.Module").apply {
                nullable(false)
            }.build(),
        ).apply {
            visibility(kotlinPublicVisibility())

            val dependenciesParameter = irParameter(
                name = "dependencies",
                type = irTypeReference(
                    referencedClassName = "$newPackage.InstanceGettersModuleDependencies",
                ).apply {
                    nullable(false)
                }.build()
            ).build()

            addParameter(dependenciesParameter)

            val initializerExpression = irMethodCallExpression(
                methodName = "module",
            ).apply {
                addLanguageProperty(KT_TOP_LEVEL_MEMBER_REFERENCE, "org.koin.dsl")

                val includesExpression = irMethodCallExpression(
                    methodName = "includes",
                ).apply {
                    val submoduleArguments = submodules.mapIndexed { i, submodule ->
                        val currIndex = i + 1

                        irMethodCallExpression(
                            methodName = moduleName + currIndex,
                        ).apply {
                            val dependenciesArgument = irIdentifierExpression(
                                selector = irLiteralExpression(
                                    value = "dependencies.instanceGettersModule${currIndex}Dependencies"
                                ).build()
                            ).build()

                            addValueArgument(dependenciesArgument)
                        }.build()
                    }

                    submoduleArguments.forEach { submodule ->
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

            val moduleMethodBody = irMethodBody().apply {
                addStatement(
                    irReturnStatement().apply {
                        expression(initializerExpression)
                    }.build()
                )
            }.build()

            body(moduleMethodBody)
        }.build()

        mainModuleFile.addDeclaration(moduleMethod)

        return mainModuleFile.build()
    }

    private fun createSeparateKoinModule(
        classes: List<IrClass>,
        moduleIndex: Int,
    ): IrFile {
        val moduleFile = irFile(newFileName + moduleIndex).apply {
            addLanguageProperty(KOTLIN_FILE_PACKAGE, newPackage)
        }

        val moduleMethod = irMethod(
            name = moduleName + moduleIndex,
            returnType = irTypeReference("org.koin.core.module.Module").apply {
                nullable(false)
            }.build(),
        ).apply {
            visibility(kotlinInternalVisibility())

            val moduleInitExpression = irMethodCallExpression(
                methodName = "module",
            ).apply {
                addLanguageProperty(KT_TOP_LEVEL_MEMBER_REFERENCE, "org.koin.dsl")

                val singleExpressions = classes.map { irClass ->
                    irMethodCallExpression(
                        methodName = "single",
                    ).apply {
                        val instanceGetterInterfaceType = irTypeReference(
                            referencedClassName = newPackage + "." + namePrefix +
                                    irClass.name.substringAfterLast(".") + ".InstanceGetter"
                        ).apply {
                            nullable(false)
                        }.build()

                        val instanceGetterIdentifierExpression = irIdentifierExpression(
                            selector = irLiteralExpression(
                                value = "dependencies." + namePrefixLower
                                        + irClass.name.substringAfterLast(".") + "InstanceGetter",
                            ).build()
                        ).build()

                        val singleLambdaBody = irBlockStatement().apply {
                            addStatement(irExpressionStatement(instanceGetterIdentifierExpression).build())
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

            val returnStatement = irReturnStatement().apply {
                expression(moduleInitExpression)
            }.build()

            body(
                irMethodBody().apply {
                    addStatement(returnStatement)
                }.build()
            )

            addParameter(
                parameter = irParameter(
                    name = "dependencies",
                    type = irTypeReference(
                        referencedClassName = "$newPackage.InstanceGettersModule${moduleIndex}Dependencies",
                    ).apply {
                        nullable(false)
                    }.build()
                ).build()
            )
        }.build()

        moduleFile.addDeclaration(moduleMethod)

        return moduleFile.build()
    }

    private fun createDependenciesClasses(submodules: List<List<IrClass>>): IrFile {
        val dependenciesClassesFile = irFile("InstanceGettersModuleDependencies").apply {
            addLanguageProperty(KOTLIN_FILE_PACKAGE, newPackage)
        }

        dependenciesClassesFile.addDeclaration(createMainDependenciesClass(submodules))

        submodules.forEachIndexed { submoduleIndex, submodule ->
            dependenciesClassesFile.addDeclaration(
                declaration = createSeparateDependenciesClass(submoduleIndex + 1, submodule),
            )
        }

        return dependenciesClassesFile.build()
    }

    private fun createMainDependenciesClass(submodules: List<List<IrClass>>): IrClass {
        val className = "InstanceGettersModuleDependencies"

        val constructorParameters = IntRange(1, submodules.size).map { index ->
            irParameter(
                name = "instanceGettersModule${index}Dependencies",
                type = irTypeReference(
                    referencedClassName = "$newPackage.InstanceGettersModule${index}Dependencies"
                ).apply {
                    nullable(false)
                }.build()
            ).apply {
                addLanguageProperty(KT_PRIMARY_CTOR_VAL_OR_VAR_KEYWORD, KtTokens.VAL_KEYWORD.value)
            }.build()
        }

        val primaryConstructor = irConstructor(
            name = className,
            returnType = irTypeReference(
                referencedClassName = "$newPackage.$className"
            ).build()
        ).apply {
            visibility(kotlinPublicVisibility())
            addLanguageProperty(KT_PRIMARY_CTOR, true)

            constructorParameters.forEach { ctorParameter ->
                addParameter(ctorParameter)
            }
        }.build()

        return irClass(name = className).apply {
            visibility(kotlinPublicVisibility())
            addMethod(primaryConstructor)
            addLanguageProperty(KtTokens.DATA_KEYWORD.value, true)
        }.build()
    }

    private fun createSeparateDependenciesClass(submoduleIndex: Int, submodule: List<IrClass>): IrClass {
        val className = "InstanceGettersModule${submoduleIndex}Dependencies"

        val constructorParameters = submodule.map { irClass ->
            irParameter(
                name = namePrefixLower + irClass.name.substringAfterLast(".") + "InstanceGetter",
                type = irTypeReference(
                    referencedClassName = newPackage + "." + namePrefix +
                            irClass.name.substringAfterLast(".") + ".InstanceGetter",
                ).apply {
                    nullable(false)
                }.build()
            ).apply {
                addLanguageProperty(KT_PRIMARY_CTOR_VAL_OR_VAR_KEYWORD, KtTokens.VAL_KEYWORD.value)
            }.build()
        }

        val primaryConstructor = irConstructor(
            name = className,
            returnType = irTypeReference(
                referencedClassName = "$newPackage.$className"
            ).build()
        ).apply {
            visibility(kotlinPublicVisibility())
            addLanguageProperty(KT_PRIMARY_CTOR, true)

            constructorParameters.forEach { ctorParameter ->
                addParameter(ctorParameter)
            }
        }.build()

        return irClass(name = className).apply {
            visibility(kotlinPublicVisibility())
            addMethod(primaryConstructor)
            addLanguageProperty(KtTokens.DATA_KEYWORD.value, true)
        }.build()
    }
}
