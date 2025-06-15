package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrTypeParameter
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeGeneric
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
                    extendsType = irTypeGeneric(name = referencedGenericName).build()
                )
            }

            else -> { // TODO: cases like <T : List<String>> are probably unsupported
                val referencedClassSimpleName = referencedClass.name ?: return@forEach
                val referencedClassQualifiedName = referencedClass.qualifiedName ?: return@forEach
                irTypeParameter.addExtendsType(
                    extendsType = irTypeReference(
                        referencedClassSimpleName = referencedClassSimpleName,
                        referencedClassQualifiedName = referencedClassQualifiedName,
                    ).apply {
                        addLanguageProperty(JAVA_PSI_CLASS, referencedClass)
                    }.build()
                )
            }
        }
    }
    return irTypeParameter.build()
}