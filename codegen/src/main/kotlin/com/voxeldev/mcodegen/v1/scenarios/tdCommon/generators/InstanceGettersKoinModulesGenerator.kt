package com.voxeldev.mcodegen.v1.scenarios.tdCommon.generators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.voxeldev.mcodegen.v1.scenarios.tdCommon.TdCommonScenarioConstants
import com.voxeldev.mcodegen.v1.scenarios.tdCommon.models.CommonClassSpec
import java.nio.file.Paths

object InstanceGettersKoinModulesGenerator {

    fun generate(
        outputPath: String,
        commonClasses: List<CommonClassSpec>,
    ) {
        var index = 0

        while (true) {
            val start = index * TdCommonScenarioConstants.MAX_LINES_PER_MODULE
            var end = start + TdCommonScenarioConstants.MAX_LINES_PER_MODULE

            if (start >= commonClasses.size) break
            if (end > commonClasses.size) end = commonClasses.size

            generateSeparateKoinModule(outputPath, commonClasses.subList(start, end), ++index)
        }

        val file = FileSpec.builder(
            packageName = "com.voxeldev.tgdrive",
            fileName = "InstanceGettersModule",
        ).apply {
            addImport("org.koin.dsl", "module")

            addProperty(
                PropertySpec.builder(
                    name = "instanceGettersModule",
                    type = ClassName("org.koin.core.module", "Module"),
                ).apply {
                    val instanceGetterChildModules = IntRange(1, index).joinToString(
                        transform = { index -> "instanceGettersModule$index" }
                    )

                    initializer(
                        """
                    |module {
                    |includes($instanceGetterChildModules)
                    |}""".trimMargin()
                    )
                }.build()
            )
        }.build()

        file.writeTo(Paths.get(outputPath))
    }

    private fun generateSeparateKoinModule(
        outputPath: String,
        commonClasses: List<CommonClassSpec>,
        moduleIndex: Int,
    ) {
        val file = FileSpec.builder(
            packageName = "com.voxeldev.tgdrive",
            fileName = "InstanceGettersModule$moduleIndex",
        ).apply {
            addImport("org.koin.dsl", "module")

            addProperty(
                PropertySpec.builder(
                    name = "instanceGettersModule$moduleIndex",
                    type = ClassName("org.koin.core.module", "Module"),
                ).apply {
                    addModifiers(KModifier.INTERNAL)

                    val instanceGetterDefinitions = commonClasses
                        .filter { commonClassSpec -> !commonClassSpec.isAbstract }
                        .joinToString(
                            separator = System.lineSeparator(),
                            transform = { commonClassSpec ->
                                val className = commonClassSpec.clazz.name
                                "|single<Td${className}.InstanceGetter> { Td${className}InstanceGetterImpl() }"
                            }
                        )

                    initializer(
                        """
                    |module {
                    $instanceGetterDefinitions
                    |}""".trimMargin()
                    )
                }.build()
            )
        }.build()

        file.writeTo(Paths.get(outputPath))
    }
}