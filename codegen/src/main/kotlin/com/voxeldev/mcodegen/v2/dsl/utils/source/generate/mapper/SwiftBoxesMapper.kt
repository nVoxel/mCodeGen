package com.voxeldev.mcodegen.v2.dsl.utils.source.generate.mapper

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.ir.IrLiteralExpression
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrTypeArray
import com.voxeldev.mcodegen.dsl.ir.IrTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression
import com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions.KT_CLASS_SIMPLE_NAME
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.v2.constants.TdCommonScenarioConstants
import io.outfoxx.swiftpoet.CodeBlock
import io.outfoxx.swiftpoet.DeclaredTypeName
import io.outfoxx.swiftpoet.ExtensionSpec
import io.outfoxx.swiftpoet.FileSpec
import io.outfoxx.swiftpoet.FunctionSpec
import io.outfoxx.swiftpoet.GenericQualifiedTypeName
import io.outfoxx.swiftpoet.Modifier
import io.outfoxx.swiftpoet.PropertySpec
import io.outfoxx.swiftpoet.TypeName
import io.outfoxx.swiftpoet.TypeSpec
import io.outfoxx.swiftpoet.parameterizedBy
import org.jetbrains.kotlin.lexer.KtTokens
import kotlin.io.path.Path

// removed on TDLibKit generator side
private val removedClassNames = setOf(
    "Object",
    "Function"
)

// TODO: rewrite this into IR
context(ScenarioScope)
fun mapSwiftBoxes(
    commonClasses: IrFile,
    namePrefix: String = "Td",
    nameSuffix: String = "Box",
    outputDir: String = "ios",
    outputFileName: String = "TdClassBoxes"
) {
    val commonClassesList = commonClasses.declarations
        .filterIsInstance<IrClass>()
        .find { it.name == "org.drinkless.tdlib.TdApi" }
        ?.nestedClasses
        ?: throw IllegalArgumentException("Provided source doesn't contain TdApi class")

    val file = FileSpec.builder(outputFileName)

    commonClassesList.forEach { commonClass ->
        val simpleName = commonClass.languageProperties[KT_CLASS_SIMPLE_NAME] as? String ?: commonClass.name
        val boxedClassName = "${namePrefix}${simpleName}${nameSuffix}"

        if (simpleName in removedClassNames) {
            return@forEach
        }

        val boxedClass = TypeSpec.classBuilder(boxedClassName)

        // region boxed class base setup
        boxedClass.addModifiers(Modifier.FINAL)

        boxedClass.addSuperType(DeclaredTypeName.typeName(".TdBoxBase"))
        boxedClass.addSuperType(DeclaredTypeName.typeName("TGDriveKit.${namePrefix}${simpleName}"))

        boxedClass.addProperty(
            PropertySpec.builder(
                name = "value",
                type = DeclaredTypeName.typeName("TDLibKit.$simpleName"),
                Modifier.PRIVATE
            ).build()
        )

        boxedClass.addProperty(
            PropertySpec.varBuilder(
                name = "swiftValue",
                type = GenericQualifiedTypeName.any(
                    DeclaredTypeName.typeName("Swift.Codable")
                ),
                Modifier.PUBLIC,
                Modifier.OVERRIDE,
            ).apply {
                getter(
                    FunctionSpec.getterBuilder().apply {
                        addStatement("value")
                    }.build()
                )
            }.build()
        )

        boxedClass.addFunction(
            FunctionSpec.constructorBuilder().apply {
                addParameter(
                    label = "_",
                    name = "value",
                    type = DeclaredTypeName.typeName("TDLibKit.$simpleName"),
                )
                addStatement("self.value = value")
            }.build()
        )
        // endregion

        // region boxed class convenience init + fields setup
        if (commonClass.languageProperties[KtTokens.ABSTRACT_KEYWORD.value] != true) {
            val fields = commonClass.fields
                .filter { it.name != TdCommonScenarioConstants.CONSTRUCTOR_FIELD }
                .sortedBy { it.name }

            val fieldsExportedTypes = fields.associate { field ->
                field.name to convertIrTypeToSwiftExportType(field.type, namePrefix)
            }

            boxedClass.addFunction(
                FunctionSpec.constructorBuilder().apply {
                    addModifiers(Modifier.CONVENIENCE)

                    fields.forEach { field ->
                        addParameter(
                            name = field.name,
                            type = fieldsExportedTypes[field.name]!!,
                        )
                    }

                    val codeBlock = CodeBlock.builder()
                    codeBlock.add("self.init(\n%>")
                    codeBlock.add("%T(\n%>", DeclaredTypeName.typeName("TDLibKit.$simpleName"))
                    fields.forEach { field ->
                        codeBlock.add("${field.name}: ${field.name},\n")
                    }
                    codeBlock.add("%<)\n")
                    codeBlock.add("%<)\n")

                    addCode(codeBlock.build())
                }.build()
            )

            val constructorValue = StringBuilder()
            val initializer = commonClass.fields
                .first { it.name == TdCommonScenarioConstants.CONSTRUCTOR_FIELD }
                .initializer as IrExpressionStatement

            when (val expression = initializer.expression) {
                is IrUnaryExpression -> {
                    if (expression.operator is IrUnaryExpression.IrUnaryOperator.Minus) {
                        constructorValue.append("-")
                    } else {
                        throw IllegalArgumentException("Unsupported operator")
                    }

                    val literal = expression.operand as IrLiteralExpression
                    constructorValue.append(literal.value)
                }

                is IrLiteralExpression -> {
                    constructorValue.append(expression.value)
                }
            }

            boxedClass.addFunction(
                FunctionSpec.builder("getConstructor").apply {
                    returns(DeclaredTypeName.typeName("Swift.Int32"))
                    addStatement("return $constructorValue")
                }.build()
            )

            fields.forEach { field ->
                boxedClass.addProperty(
                    PropertySpec.varBuilder(
                        name = field.name,
                        type = fieldsExportedTypes[field.name]!!
                    ).build()
                )
            }
        }
        // endregion

        // region extension setup
        val tdLibKitExtension = ExtensionSpec.builder(DeclaredTypeName.typeName("TDLibKit.$simpleName")).apply {
            addSuperType(DeclaredTypeName.typeName(".TdWrappable"))
            addProperty(
                PropertySpec.varBuilder(
                    name = "asKtObject",
                    type = DeclaredTypeName.typeName("TGDriveKit.TdObject"),
                    Modifier.PUBLIC
                ).apply {
                    val boxedClass = DeclaredTypeName.typeName(".${boxedClassName}")
                    getter(
                        FunctionSpec.getterBuilder().apply {
                            addStatement("%T(self)", boxedClass)
                        }.build()
                    )
                }.build()
            )
        }
        // endregion

        file.addType(boxedClass.build())
        file.addExtension(tdLibKitExtension.build())
    }

    file.addComment("// Generated file. DO NOT MODIFY!")

    file.indent("    ")

    val outputPath = Path(scenarioConfiguration.outputDir, outputDir)
    file.build().writeTo(outputPath)
}

// region converting ir type to swift exported type
private fun convertIrTypeToSwiftExportType(irType: IrType, namePrefix: String, optional: Boolean = true): TypeName {
    return when (irType) {
        is IrTypeReference -> {
            if (irType.referencedClassName == "java.lang.String") {
                DeclaredTypeName.typeName("Swift.String").let { if (optional) it.makeOptional() else it }
            } else {
                DeclaredTypeName
                    .typeName("TGDriveKit.${namePrefix}${irType.referencedClassName.substringAfterLast(".")}")
                    .let { if (optional) it.makeOptional() else it }
            }
        }

        is IrTypePrimitive -> {
            convertIrPrimitiveTypeToSwiftExportedType(irType)
        }

        is IrTypeArray -> {
            convertIrArrayTypeToSwiftExportType(irType, namePrefix, optional)
        }

        else -> throw IllegalArgumentException("Unsupported type")
    }
}

private fun convertIrArrayTypeToSwiftExportType(
    irTypeArray: IrTypeArray,
    namePrefix: String,
    optional: Boolean = true,
): TypeName {
    return when (val elementType = irTypeArray.elementType) {
        is IrTypePrimitive -> {
            when (elementType.primitiveType) {
                is IrTypePrimitive.PrimitiveType.Boolean -> DeclaredTypeName
                    .typeName("TGDriveKit.KotlinBooleanArray")
                    .let { if (optional) it.makeOptional() else it }

                is IrTypePrimitive.PrimitiveType.Byte -> DeclaredTypeName
                    .typeName("TGDriveKit.KotlinByteArray")
                    .let { if (optional) it.makeOptional() else it }

                is IrTypePrimitive.PrimitiveType.Short -> DeclaredTypeName
                    .typeName("TGDriveKit.KotlinShortArray")
                    .let { if (optional) it.makeOptional() else it }

                is IrTypePrimitive.PrimitiveType.Int -> DeclaredTypeName
                    .typeName("TGDriveKit.KotlinIntArray")
                    .let { if (optional) it.makeOptional() else it }

                is IrTypePrimitive.PrimitiveType.Long -> DeclaredTypeName
                    .typeName("TGDriveKit.KotlinLongArray")
                    .let { if (optional) it.makeOptional() else it }

                is IrTypePrimitive.PrimitiveType.Char -> DeclaredTypeName
                    .typeName("TGDriveKit.KotlinCharArray")
                    .let { if (optional) it.makeOptional() else it }

                is IrTypePrimitive.PrimitiveType.Float -> DeclaredTypeName
                    .typeName("TGDriveKit.KotlinFloatArray")
                    .let { if (optional) it.makeOptional() else it }

                is IrTypePrimitive.PrimitiveType.Double -> DeclaredTypeName
                    .typeName("TGDriveKit.KotlinDoubleArray")
                    .let { if (optional) it.makeOptional() else it }

                else -> throw IllegalArgumentException("Unsupported primitive export type")
            }
        }

        else -> {
            DeclaredTypeName.typeName("TGDriveKit.KotlinArray").parameterizedBy(
                convertIrTypeToSwiftExportType(elementType, namePrefix, optional = false)
            ).makeOptional()
        }
    }
}

private fun convertIrPrimitiveTypeToSwiftExportedType(irTypePrimitive: IrTypePrimitive): TypeName {
    return when (irTypePrimitive.primitiveType) {
        is IrTypePrimitive.PrimitiveType.Boolean -> DeclaredTypeName.typeName("Swift.Bool")
        is IrTypePrimitive.PrimitiveType.Byte -> DeclaredTypeName.typeName("Swift.Byte")
        is IrTypePrimitive.PrimitiveType.Short -> DeclaredTypeName.typeName("Swift.Short")
        is IrTypePrimitive.PrimitiveType.Int -> DeclaredTypeName.typeName("Swift.Int32")
        is IrTypePrimitive.PrimitiveType.Long -> DeclaredTypeName.typeName("Swift.Int64")
        is IrTypePrimitive.PrimitiveType.Char -> DeclaredTypeName.typeName("Swift.Char")
        is IrTypePrimitive.PrimitiveType.Float -> DeclaredTypeName.typeName("Swift.Float")
        is IrTypePrimitive.PrimitiveType.Double -> DeclaredTypeName.typeName("Swift.Double")
        else -> throw IllegalArgumentException("Unsupported primitive export type")
    }
}
// endregion
