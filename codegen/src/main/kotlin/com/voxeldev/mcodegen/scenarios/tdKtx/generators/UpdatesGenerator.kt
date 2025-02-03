package com.voxeldev.mcodegen.scenarios.tdKtx.generators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.voxeldev.mcodegen.GlobalConstants
import com.voxeldev.mcodegen.scenarios.tdKtx.TdKtxScenarioConstants
import com.voxeldev.mcodegen.utils.GlobalPsiUtils
import org.jetbrains.kotlin.com.intellij.psi.PsiClass

object UpdatesGenerator {
    fun mapUpdatesByFile(
        objects: List<PsiClass>,
        funcs: List<Pair<FunSpec, List<String>>>,
    ): HashMap<String, List<Pair<FunSpec, List<String>>>> {

        val filesMap = HashMap<String, List<Pair<FunSpec, List<String>>>>()
        val etc =
            objects.mapNotNull { it.name }
                .sortedBy { it.count() }
                .filter { it != "Text" }
                .fold(
                    funcs.toMutableList()

                ) { f, name ->
                    val cont = f.filter { it.first.name.capitalize().contains(name) }
                    if (cont.size > 1) {
                        filesMap[name] = cont
                        f.removeAll(cont)
                    }
                    f
                }

        filesMap["Common"] = etc

        return filesMap
    }

    fun generateUpdate(
        update: PsiClass
    ): Pair<FunSpec, List<String>> {
        val imports = mutableListOf<String>()

        return FunSpec.builder(
            update.name.toString().removePrefix("Update").decapitalize() + "Flow"
        )
            .receiver(
                ClassName(
                    TdKtxScenarioConstants.CORE_PACKAGE,
                    TdKtxScenarioConstants.API_NAME
                )
            )
            .addModifiers(KModifier.PUBLIC)
            .apply {
                update.fields.first()
                    .takeIf { update.fields.size == 2 }
                    ?.let {
                        addStatement(
                            "return this.getUpdatesFlowOfType<Td${update.name}>()" +
                                    "\n    .%M { it.${it.name.toString()} }",
                            MemberName(
                                "kotlinx.coroutines.flow",
                                "mapNotNull"
                            )
                        )

                        imports.add("Td${update.name}")
                    } ?: addStatement(
                    "return this.getUpdatesFlowOfType()"
                )

                createUpdateDoc(update)
            }
            .returns(
                ClassName(
                    "kotlinx.coroutines.flow",
                    "Flow"
                )
                    .parameterizedBy(
                        update.fields.first()
                            .takeIf { update.fields.size == 2 }
                            ?.type
                            ?.let {
                                GlobalPsiUtils.createParameterType(
                                    it
                                ).copy(nullable = false)
                            }
                            ?: ClassName(
                                GlobalConstants.TGDRIVE_PACKAGE,
                                "Td${update.name}"
                            )
                    )
            )
            .build() to imports
    }

    private fun FunSpec.Builder.createUpdateDoc(
        update: PsiClass
    ) {
        update.docComment
            ?.descriptionElements
            ?.firstOrNull { it.text.isNotBlank() }
            ?.let {
                addKdoc(
                    "emits ${
                        update.fields.first()
                            .takeIf { update.fields.size == 2 }
                            ?.let {
                                if (it.name != it.type.presentableText.decapitalize())
                                    "${it.name} [${it.type.presentableText.capitalize()}"
                                else "[${it.type.presentableText.capitalize()}"
                            }
                            ?: "[${update.name.toString()}"
                    }] if ${it.text.trim().decapitalize()}")
            }
    }
}
