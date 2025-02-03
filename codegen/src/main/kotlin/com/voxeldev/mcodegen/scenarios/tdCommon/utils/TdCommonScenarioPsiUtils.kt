package com.voxeldev.mcodegen.scenarios.tdCommon.utils

import com.voxeldev.mcodegen.scenarios.tdCommon.models.CommonClassSpec
import org.jetbrains.kotlin.com.intellij.psi.PsiClass
import org.jetbrains.kotlin.com.intellij.psi.PsiField

object TdCommonScenarioPsiUtils {
    fun getCommonClasses(
        classes1: Array<PsiClass>,
        classes2: Array<PsiClass>
    ): List<CommonClassSpec> {
        val classes1HashMap = classes1.mapIndexed { index, clazz -> clazz.name to index }.toMap()
        return classes2
            .filter { clazz -> classes1HashMap.containsKey(clazz.name) }
            .map { clazz ->
                CommonClassSpec(
                    clazz = clazz,
                    commonFields = getCommonFields(
                        class1 = classes1[classes1HashMap[clazz.name]!!],
                        class2 = clazz
                    )
                )
            }
    }

    private fun getCommonFields(
        class1: PsiClass,
        class2: PsiClass
    ): List<PsiField> {
        val fields1 = class1.fields
        val fields2 = class2.fields

        val fields1Names = fields1.mapIndexed { index, field -> field.name to index }.toMap()

        return fields2.filter { field2 ->
            val field1Index = fields1Names[field2.name] ?: return@filter false
            return@filter fields1[field1Index].type.presentableText == field2.type.presentableText
        }
    }
}