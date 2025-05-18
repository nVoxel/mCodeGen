package com.voxeldev.mcodegen.v2.dsl.utils.source.generate.mapper

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind
import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrField
import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrTypeArray
import com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import com.voxeldev.mcodegen.dsl.ir.builders.irBlockStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irCastExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irClass
import com.voxeldev.mcodegen.dsl.ir.builders.irExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irFile
import com.voxeldev.mcodegen.dsl.ir.builders.irIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irLiteralExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irMethod
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodBody
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irObjectCreationExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irParameter
import com.voxeldev.mcodegen.dsl.ir.builders.irReturnStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irSuperClass
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReference
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule.KOTLIN_FILE_PACKAGE
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.builders.irLambdaExpression
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.builders.irNullSafeExpression
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_CLASS_SIMPLE_NAME
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_SAFE_TYPE_CAST
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationMapperBaseImpl
import com.voxeldev.mcodegen.v1.GlobalConstants
import com.voxeldev.mcodegen.v2.constants.TdCommonScenarioConstants
import org.jetbrains.kotlin.lexer.KtTokens

context(ScenarioScope)
fun kmpInstanceGetterImplsMapper(
    newFileName: String = "InstanceGetterImpls",
    packageName: String = GlobalConstants.TGDRIVE_PACKAGE,
    namePrefix: String = "Td",
): KMPInstanceGetterImplsMapper {
    return KMPInstanceGetterImplsMapper(packageName, newFileName, namePrefix)
}

context(ScenarioScope)
class KMPInstanceGetterImplsMapper internal constructor(
    private val newPackage: String,
    private val newFileName: String,
    private val namePrefix: String,
) : GenerationMapperBaseImpl() {

    override fun map(source: IrFile): IrFile {
        val resultFile = irFile(newFileName).apply {
            addLanguageProperty(KOTLIN_FILE_PACKAGE, newPackage)
        }

        val tdApi = source.declarations
            .filterIsInstance<IrClass>()
            .find { it.name == "org.drinkless.tdlib.TdApi" }
            ?: throw IllegalArgumentException("Provided source doesn't contain TdApi class")

        tdApi.nestedClasses.forEach { irClass ->
            if (irClass.languageProperties[KtTokens.ABSTRACT_KEYWORD.value] == true) {
                return@forEach
            }

            val sourceClassSimpleName = irClass.languageProperties[KT_CLASS_SIMPLE_NAME] as? String ?: irClass.name

            val commonInterfaceName = namePrefix + sourceClassSimpleName

            val instanceGetterImpl = irClass(
                name = commonInterfaceName + "InstanceGetterImpl"
            ).apply {
                kind(IrClassKind.IrClassClassKind)
                visibility(irClass.visibility)

                addSuperClass(
                    irSuperClass(
                        superClassName = convertClassName(sourceClassSimpleName, newPackage, namePrefix)
                                + ".InstanceGetter",
                        kind = IrClassKind.IrInterfaceClassKind,
                    ).build()
                )

                val getInstanceMethodImpl = irMethod(
                    name = "getInstance",
                    returnType = irTypeReference(
                        referencedClassName = commonInterfaceName,
                    ).apply {
                        nullable(false)
                    }.build()
                ).apply {
                    visibility(irClass.visibility)
                    isOverride(true)

                    val allFields = irClass.fields
                        .filter { field -> field.name != TdCommonScenarioConstants.CONSTRUCTOR_FIELD }

                    allFields.forEach { field ->
                        addParameter(
                            irParameter(
                                name = field.name,
                                type = convertType(field.type, newPackage, namePrefix),
                            ).build()
                        )
                    }

                    val bodyReturnStatement = irReturnStatement().apply {
                        val constructorCall = irObjectCreationExpression(
                            className = "org.drinkless.tdlib.TdApi.$sourceClassSimpleName",
                        ).apply {
                            allFields.forEach { field ->
                                addConstructorArg(
                                    convertConstructorArgument(field)
                                )
                            }
                        }.build()

                        expression(constructorCall)
                    }.build()

                    body(
                        irMethodBody().apply {
                            addStatement(bodyReturnStatement)
                        }.build()
                    )
                }.build()

                addMethod(getInstanceMethodImpl)

                // generate empty constructor
                if (irClass.fields.any { field -> field.name != TdCommonScenarioConstants.CONSTRUCTOR_FIELD }) {
                    val getInstanceEmptyMethodImpl = irMethod(
                        name = "getInstance",
                        returnType = irTypeReference(
                            referencedClassName = commonInterfaceName,
                        ).apply {
                            nullable(false)
                        }.build()
                    ).apply {
                        visibility(irClass.visibility)
                        isOverride(true)

                        val bodyReturnStatement = irReturnStatement().apply {
                            val constructorCall = irObjectCreationExpression(
                                className = "org.drinkless.tdlib.TdApi.$sourceClassSimpleName",
                            ).build()

                            expression(constructorCall)
                        }.build()

                        body(
                            irMethodBody().apply {
                                addStatement(bodyReturnStatement)
                            }.build()
                        )
                    }.build()

                    addMethod(getInstanceEmptyMethodImpl)
                }
            }.build()

            resultFile.addDeclaration(instanceGetterImpl)
        }

        return resultFile.build()
    }

    private fun convertClassName(
        className: String,
        packageName: String,
        namePrefix: String,
    ): String = if (className == "java.lang.String") {
        "kotlin.String"
    } else {
        "${packageName}.$namePrefix${className.substringAfterLast(".")}"
    }

    // Expressions tree for type cast: https://miro.com/app/board/uXjVI0bO8W8=/
    private fun convertType(
        sourceType: IrType,
        packageName: String,
        namePrefix: String,
        isNullable: Boolean = sourceType.isNullable,
    ): IrType {
        return when (sourceType) {
            is IrTypeReference -> {
                sourceType.copy(
                    referencedClassName = convertClassName(
                        className = sourceType.referencedClassName,
                        packageName = packageName,
                        namePrefix = namePrefix
                    ),
                    isNullable = isNullable,
                )
            }

            is IrTypeArray -> {
                sourceType.copy(
                    elementType = convertType(
                        sourceType = sourceType.elementType,
                        packageName = packageName,
                        namePrefix = namePrefix,
                        isNullable = false,
                    ),
                    isNullable = isNullable,
                )
            }

            else -> {
                if (sourceType is IrTypePrimitive) {
                    return sourceType.copy(
                        isNullable = isNullable,
                    )
                } else {
                    throw IllegalArgumentException("Met unexpected type in KMPCommonInterfacesMapper")
                }
            }
        }
    }

    private fun convertConstructorArgument(
        irField: IrField,
    ): IrExpression {
        return when (val irType = irField.type) {
            is IrTypeArray -> {
                createArrayTypeCast(
                    variableName = irField.name,
                    irType = irType,
                )
            }

            else -> {
                createTypeCast(
                    variableName = irField.name,
                    irType = irType,
                )
            }
        }
    }

    private fun createTypeCast(
        variableName: String,
        irType: IrType,
    ): IrExpression = if (irType is IrTypeReference && irType.referencedClassName != "java.lang.String") {
        irCastExpression(
            expression = irIdentifierExpression(
                selector = irLiteralExpression(variableName).build()
            ).build(),
            targetType = irTypeReference(
                referencedClassName = irType.referencedClassName
            ).apply {
                nullable(false)
            }.build()
        ).apply {
            addLanguageProperty(KT_SAFE_TYPE_CAST, true)
        }.build()
    } else {
        irLiteralExpression(variableName).build()
    }

    private fun createArrayTypeCast(
        variableName: String,
        irType: IrTypeArray,
    ): IrExpression {
        val elementType = irType.elementType

        if (elementType is IrTypePrimitive
            || elementType is IrTypeReference && elementType.referencedClassName == "kotlin.String") {
            return irIdentifierExpression(
                selector = irLiteralExpression(variableName).build()
            ).build()
        }

        val mapMethodLambdaExpression = irLambdaExpression().apply {
            val convertStatement = irExpressionStatement(
                expression = when (elementType) {
                    is IrTypeArray -> {
                        createArrayTypeCast(
                            variableName = "it",
                            irType = elementType,
                        )
                    }

                    else -> {
                        createTypeCast(
                            variableName = "it",
                            irType = elementType,
                        )
                    }
                }
            ).build()

            body(
                irBlockStatement().apply {
                    addStatement(convertStatement)
                }.build()
            )
        }.build()

        val mapMethodExpression = irMethodCallExpression(
            methodName = "map",
        ).apply {
            addValueArgument(mapMethodLambdaExpression)
        }.build()

        val variableIdentifierExpression = irIdentifierExpression(
            selector = irLiteralExpression(variableName).build()
        ).build()

        val mapExpression = irNullSafeExpression(
            receiver = variableIdentifierExpression,
            selector = mapMethodExpression,
        ).build()

        val arrayTypeCastMethodExpression = irMethodCallExpression(
            methodName = "toTypedArray"
        ).build()

        return irNullSafeExpression(
            receiver = mapExpression,
            selector = arrayTypeCastMethodExpression,
        ).build()
    }
}