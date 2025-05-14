package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrTypeParameter
import com.voxeldev.mcodegen.dsl.ir.builders.irGeneric
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeParameter
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReference
import com.voxeldev.mcodegen.dsl.language.java.JavaModule
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.com.intellij.psi.PsiTypeParameter

context(JavaModule, ScenarioScope)
internal fun convertTypeParameter(psiTypeParameter: PsiTypeParameter): IrTypeParameter {
    val irTypeParameter = irTypeParameter(psiTypeParameter.name ?: "Ir:UnnamedTypeParameter")
    psiTypeParameter.extendsListTypes.forEach { extendsType ->
        val referencedClass = extendsType.resolve() ?: return@forEach

        when(referencedClass) {
            is PsiTypeParameter -> {
                val referencedGenericName = referencedClass.name ?: return@forEach
                irTypeParameter.addExtendsType(
                    extendsType = irGeneric(name = referencedGenericName).build()
                )
            }

            else -> { // TODO: cases like <T : List<String>> are probably unsupported
                val referencedClassName = referencedClass.qualifiedName ?: return@forEach
                irTypeParameter.addExtendsType(
                    extendsType = irTypeReference(referencedClassName).apply {
                        addLanguageProperty(JAVA_PSI_CLASS, referencedClass)
                    }.build()
                )
            }
        }
    }
    return irTypeParameter.build()
}