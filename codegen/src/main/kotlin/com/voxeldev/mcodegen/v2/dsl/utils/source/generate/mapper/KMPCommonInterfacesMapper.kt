package com.voxeldev.mcodegen.v2.dsl.utils.source.generate.mapper

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrClassClassKind
import com.voxeldev.mcodegen.dsl.ir.IrExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrTypeArray
import com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import com.voxeldev.mcodegen.dsl.ir.builders.irClass
import com.voxeldev.mcodegen.dsl.ir.builders.irField
import com.voxeldev.mcodegen.dsl.ir.builders.irFile
import com.voxeldev.mcodegen.dsl.ir.builders.irMethod
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodBody
import com.voxeldev.mcodegen.dsl.ir.builders.irParameter
import com.voxeldev.mcodegen.dsl.ir.builders.irReturnStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irSuperClass
import com.voxeldev.mcodegen.dsl.ir.builders.irTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReference
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule.KOTLIN_FILE_PACKAGE
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationMapperBaseImpl
import com.voxeldev.mcodegen.v1.GlobalConstants
import com.voxeldev.mcodegen.v2.constants.TdCommonScenarioConstants
import org.jetbrains.kotlin.lexer.KtTokens

context(ScenarioScope)
fun kmpCommonInterfacesMapper(
    newFileName: String = "TdCommon",
    packageName: String = GlobalConstants.TGDRIVE_PACKAGE,
    namePrefix: String = "Td",
): KMPCommonInterfacesMapper {
    return KMPCommonInterfacesMapper(packageName, newFileName, namePrefix)
}

context(ScenarioScope)
class KMPCommonInterfacesMapper internal constructor(
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
            .find { it.qualifiedName == "org.drinkless.tdlib.TdApi" }
            ?: throw IllegalArgumentException("Provided source doesn't contain TdApi class")

        tdApi.nestedClasses.forEach { irClass ->
            val commonInterfaceName = namePrefix + irClass.simpleName

            val commonInterface = irClass(
                qualifiedName = "${newPackage}.${commonInterfaceName}",
                simpleName = commonInterfaceName,
            ).apply {
                kind(IrClassKind.IrInterfaceClassKind)
                visibility(irClass.visibility)

                irClass.superClasses.firstOrNull { it.kind == IrClassClassKind }?.let { superClass ->
                    val newSuperInterfaceName = convertClassName(superClass.superClassName, newPackage, namePrefix)

                    addSuperClass(
                        irSuperClass(
                            superClassName = newSuperInterfaceName,
                            kind = IrClassKind.IrInterfaceClassKind,
                        ).build()
                    )
                }

                if (irClass.languageProperties[KtTokens.ABSTRACT_KEYWORD.value] == true) {
                    return@apply
                }

                irClass.fields.forEach { field ->
                    if (field.name == TdCommonScenarioConstants.CONSTRUCTOR_FIELD) {
                        val initializer = field.initializer as? IrExpressionStatement ?: return@forEach

                        addMethod(
                            irMethod(
                                name = "getConstructor",
                                returnType = irTypePrimitive(IrTypePrimitive.PrimitiveType.Int()).apply {
                                    nullable(false)
                                }.build(),
                            ).apply {
                                visibility(field.visibility)

                                val body = irMethodBody().apply {
                                    addStatement(
                                        irReturnStatement().apply {
                                            expression(initializer.expression)
                                        }.build()
                                    )
                                }.build()

                                body(body)
                            }.build()
                        )
                    } else {
                        addField(
                            irField(
                                name = field.name,
                                type = convertType(field.type, newPackage, namePrefix),
                            ).apply {
                                visibility(field.visibility)
                                mutable(false)
                            }.build()
                        )
                    }
                }

                val instanceGetter = irClass(
                    qualifiedName = "${newPackage}.${commonInterfaceName}.InstanceGetter",
                    simpleName = "InstanceGetter",
                ).apply {
                    kind(IrClassKind.IrInterfaceClassKind)
                    visibility(irClass.visibility)

                    val getInstanceMethod = irMethod(
                        name = "getInstance",
                        returnType = irTypeReference(
                            referencedClassSimpleName = commonInterfaceName,
                        ).apply {
                            nullable(false)
                        }.build()
                    ).apply {
                        visibility(irClass.visibility)
                        isAbstract(true)

                        irClass.fields
                            .filter { field -> field.name != TdCommonScenarioConstants.CONSTRUCTOR_FIELD }
                            .forEach { field ->
                                addParameter(
                                    irParameter(
                                        name = field.name,
                                        type = convertType(field.type, newPackage, namePrefix),
                                    ).build()
                                )
                            }
                    }.build()

                    addMethod(getInstanceMethod)

                    // generate empty constructor
                    if (irClass.fields.any { field -> field.name != TdCommonScenarioConstants.CONSTRUCTOR_FIELD }) {
                        val getInstanceEmptyMethod = irMethod(
                            name = "getInstance",
                            returnType = irTypeReference(
                                referencedClassSimpleName = commonInterfaceName,
                            ).apply {
                                nullable(false)
                            }.build()
                        ).apply {
                            visibility(irClass.visibility)
                            isAbstract(true)
                        }.build()

                        addMethod(getInstanceEmptyMethod)
                    }
                }.build()

                addNestedClass(instanceGetter)
            }.build()

            resultFile.addDeclaration(commonInterface)
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
        "${packageName}.${namePrefix}${className.substringAfterLast(".")}"
    }

    private fun convertType(
        sourceType: IrType,
        packageName: String,
        namePrefix: String,
        isNullable: Boolean = sourceType.isNullable,
    ): IrType {
        return when (sourceType) {
            is IrTypeReference -> {
                sourceType.copy(
                    referencedClassSimpleName = "${namePrefix}.${sourceType.referencedClassSimpleName}",
                    referencedClassQualifiedName = convertClassName(
                        className = sourceType.getQualifiedNameIfPresent(),
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
}
