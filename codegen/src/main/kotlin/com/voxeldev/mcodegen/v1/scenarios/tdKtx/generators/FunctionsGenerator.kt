package com.voxeldev.mcodegen.v1.scenarios.tdKtx.generators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.voxeldev.mcodegen.v1.GlobalConstants
import com.voxeldev.mcodegen.v1.scenarios.tdKtx.TdKtxScenarioConstants
import com.voxeldev.mcodegen.v1.utils.GlobalPsiUtils
import org.jetbrains.kotlin.com.intellij.psi.PsiClass

object FunctionsGenerator {
    fun generateFunction(
        objects: List<PsiClass>,
        function: PsiClass,
    ): FunSpec {
        val result = objects.firstOrNull { it.name == function.getResultName() }
            ?: throw java.lang.Exception("Result object not found ${function.getResultName()}")

        val resultName = result.name.toString()
        return FunSpec.builder(
            function.name.toString().decapitalize()
        )
            .receiver(ClassName(TdKtxScenarioConstants.CORE_PACKAGE, TdKtxScenarioConstants.API_NAME))
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.SUSPEND)
            .apply {
                val parameters = createFunctionParameters(function)

                parameters.forEach { addParameter(it) }

                addParameter(
                    ParameterSpec.builder(
                        name = "instanceGetter",
                        type = ClassName(GlobalConstants.TGDRIVE_PACKAGE, "Td${function.name}", "InstanceGetter")
                    ).build()
                )

                val suspendSender = if (resultName == "Ok") {
                    "sendFunctionLaunch"
                } else {
                    returns(
                        ClassName(GlobalConstants.TGDRIVE_PACKAGE, "Td$resultName")
                    )
                    "sendFunctionAsync"
                }
                addStatement(
                    "return this.$suspendSender(instanceGetter.getInstance" +
                            "(${parameters.joinToString { it.name }}))"
                )

                createFunctionDoc(function, result)

            }
            .build()
    }

    private fun FunSpec.Builder.createFunctionDoc(
        function: PsiClass,
        result: PsiClass
    ) {
        val javaDoc = function.constructors.asList().lastOrNull()?.docComment

        val docs = javaDoc?.descriptionElements
            ?.map { it.text.trim() }
            ?.filter { it.isNotBlank() } as? MutableList

        docs?.firstOrNull()?.let {
            docs[0] = it.replace("Creates a function", "Suspend function")
                .replace("Default constructor for a function", "Suspend function")
        }

        docs?.removeAll { it.startsWith("<p>") }
        docs?.removeAll { it.startsWith("{@link") }
        docs?.removeAll { it.startsWith("</p>") }

        if (docs != null) {
            addKdoc(
                docs.joinToString("\n")
            )
        }

        addKdoc("\n\n")
        javaDoc?.tags?.let {
            addKdoc(it.joinToString("\n") {
                "${it.nameElement.text} ${it.dataElements.joinToString(" ") { it.text }}"
            })
            if (it.isNotEmpty()) addKdoc("\n\n")
        }
        if (result.name.toString() != "Ok")
            addKdoc(
                "@return [${result.name.toString()}] ${
                    result.docComment?.descriptionElements?.getOrNull(1)?.text?.trim() ?: ""
                }"
            )
    }

    private fun createFunctionParameters(
        function: PsiClass
    ): List<ParameterSpec> {

        return function.fields.filter { it.name != "CONSTRUCTOR" }.map {
            val nullable = it.annotations.firstOrNull()?.text == "@Nullable"
            ParameterSpec.Companion.builder(
                name = it.name.toString(),
                type = GlobalPsiUtils.createParameterType(it.type)
            ).also {
                if (nullable) it.defaultValue("null")
            }.build()
        }
    }

    fun mapFunctionsByFile(
        objects: List<PsiClass>,
        funcs: List<FunSpec>,
    ): HashMap<String, List<FunSpec>> {

        val filesMap = HashMap<String, List<FunSpec>>()
        val etc = listOf(
            "PhoneNumber", "Email", "Password",
            "Log", "Network", "Authentication",
            "Query", "Database", "Payment", "Passport",
            "Language", "Account", "Emoji"
        ).plus(
            objects.mapNotNull { it.name }
                .sortedBy { it.count() }
                .filter { it != "Text" }
        ).fold(
            funcs.toMutableList()

        ) { f, name ->
            val cont = f.filter { it.name.contains(name) }
            if (cont.size > 1) {
                filesMap[name] = cont
                f.removeAll(cont)
            }
            f
        }

        filesMap["Common"] = etc

        return filesMap
    }

    private fun PsiClass.getResultName(): String = this.docComment?.text
        ?.substringAfterLast("{@link ")
        ?.substringBefore("} </p>")
        ?.split(" ")
        ?.first()
        ?: throw Exception("Result name not found ${this.name}")
}