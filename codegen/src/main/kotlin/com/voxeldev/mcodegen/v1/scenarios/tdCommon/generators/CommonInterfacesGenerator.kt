package com.voxeldev.mcodegen.v1.scenarios.tdCommon.generators

import com.squareup.kotlinpoet.*
import com.voxeldev.mcodegen.v1.GlobalConstants
import com.voxeldev.mcodegen.v1.scenarios.tdCommon.TdCommonScenarioConstants
import com.voxeldev.mcodegen.v1.scenarios.tdCommon.models.CommonClassSpec
import com.voxeldev.mcodegen.v1.utils.GlobalPsiUtils
import org.jetbrains.kotlin.com.intellij.psi.PsiLiteralExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiPrefixExpression
import java.nio.file.Paths

object CommonInterfacesGenerator {

    fun generate(commonClasses: List<CommonClassSpec>) {
        val file = FileSpec.builder(
            packageName = "com.voxeldev.tgdrive",
            fileName = "TdCommon"
        ).apply {
            commonClasses.forEach { commonClassSpec ->
                addType(
                    TypeSpec.interfaceBuilder("Td${commonClassSpec.clazz.name}").apply {
                        runCatching {
                            val superClass = commonClassSpec.clazz.superClass ?: return@runCatching
                            addSuperinterface(ClassName(GlobalConstants.TGDRIVE_PACKAGE, "Td${superClass.name}"))
                        }

                        if (commonClassSpec.isAbstract) {
                            return@apply
                        }

                        commonClassSpec.commonFields.forEach { field ->
                            if (field.name == TdCommonScenarioConstants.CONSTRUCTOR_FIELD) {
                                val constructorValue = field.children.run {
                                    find { it is PsiPrefixExpression } as? PsiPrefixExpression
                                        ?: find { it is PsiLiteralExpression } as? PsiLiteralExpression
                                } ?: return@forEach

                                // todo: probably can get rid of it
                                addFunction(
                                    FunSpec
                                        .builder("getConstructor")
                                        .returns(Int::class)
                                        .addStatement("return ${constructorValue.text}")
                                        .build()
                                )
                            } else {
                                addProperty(
                                    name = field.name!!,
                                    type = GlobalPsiUtils.createParameterType(field.type)
                                )
                            }
                        }

                        addType(
                            TypeSpec.interfaceBuilder("InstanceGetter").apply {
                                addFunction(
                                    FunSpec.builder("getInstance")
                                        .apply {
                                            addModifiers(KModifier.ABSTRACT)

                                            commonClassSpec.commonFields
                                                .filter { field -> field.name != null && field.name != TdCommonScenarioConstants.CONSTRUCTOR_FIELD }
                                                .forEach { field ->
                                                    addParameter(
                                                        field.name!!,
                                                        GlobalPsiUtils.createParameterType(field.type)
                                                    )
                                                }

                                            returns(ClassName(GlobalConstants.TGDRIVE_PACKAGE, "Td${commonClassSpec.clazz.name}"))
                                        }.build()
                                )

                                // generate empty constructor
                                if (commonClassSpec.commonFields.count { it.name != TdCommonScenarioConstants.CONSTRUCTOR_FIELD } > 0) {
                                    addFunction(
                                        FunSpec.builder("getInstance")
                                            .apply {
                                                addModifiers(KModifier.ABSTRACT)
                                                returns(ClassName(GlobalConstants.TGDRIVE_PACKAGE, "Td${commonClassSpec.clazz.name}"))
                                            }.build()
                                    )
                                }
                            }.build()
                        )
                    }.build()
                )
            }
        }.build()

        file.writeTo(Paths.get(TdCommonScenarioConstants.outputPathCommon))
    }
}