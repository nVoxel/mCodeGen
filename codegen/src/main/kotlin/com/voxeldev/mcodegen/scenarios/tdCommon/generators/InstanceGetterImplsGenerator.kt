package com.voxeldev.mcodegen.scenarios.tdCommon.generators

import com.squareup.kotlinpoet.*
import com.voxeldev.mcodegen.GlobalConstants
import com.voxeldev.mcodegen.scenarios.tdCommon.TdCommonScenarioConstants
import com.voxeldev.mcodegen.scenarios.tdCommon.models.CommonClassSpec
import com.voxeldev.mcodegen.utils.GlobalPsiUtils
import java.nio.file.Paths

object InstanceGetterImplsGenerator {

    fun generate(
        outputPath: String,
        commonClasses: List<CommonClassSpec>,
    ) {
        val file = FileSpec.builder(
            packageName = "com.voxeldev.tgdrive",
            fileName = "InstanceGetterImpls",
        ).apply {
            addImport("org.drinkless.tdlib", "TdApi")

            commonClasses.forEach { commonClassSpec ->
                if (commonClassSpec.isAbstract) return@forEach

                addType(
                    TypeSpec.classBuilder("Td${commonClassSpec.clazz.name}InstanceGetterImpl").apply {
                        addSuperinterface(ClassName(GlobalConstants.TGDRIVE_PACKAGE, "Td${commonClassSpec.clazz.name}.InstanceGetter"))

                        addFunction(
                            FunSpec.builder("getInstance").apply {
                                addModifiers(KModifier.OVERRIDE)

                                commonClassSpec.commonFields
                                    .filter { field -> field.name != null && field.name != TdCommonScenarioConstants.CONSTRUCTOR_FIELD }
                                    .forEach { field ->
                                        addParameter(field.name!!, GlobalPsiUtils.createParameterType(field.type))
                                    }

                                val constructorParams = commonClassSpec.commonFields
                                    .filter { field -> field.name != null && field.name != TdCommonScenarioConstants.CONSTRUCTOR_FIELD }
                                    .joinToString(transform = { field -> GlobalPsiUtils.createTypeCastExpression(field) })

                                addCode(
                                    """
                                |return TdApi.${commonClassSpec.clazz.name}(
                                |    $constructorParams
                                |)
                                |""".trimMargin()
                                )

                                returns(ClassName("com.voxeldev.tgdrive", "Td${commonClassSpec.clazz.name}"))
                            }.build()
                        )

                        if (commonClassSpec.commonFields.count { it.name != TdCommonScenarioConstants.CONSTRUCTOR_FIELD } > 0) {
                            addFunction(
                                FunSpec.builder("getInstance").apply {
                                    addModifiers(KModifier.OVERRIDE)

                                    addStatement("return TdApi.${commonClassSpec.clazz.name}()")

                                    returns(ClassName("com.voxeldev.tgdrive", "Td${commonClassSpec.clazz.name}"))
                                }.build()
                            )
                        }
                    }.build()
                )
            }
        }.build()

        file.writeTo(Paths.get(outputPath))
    }
}