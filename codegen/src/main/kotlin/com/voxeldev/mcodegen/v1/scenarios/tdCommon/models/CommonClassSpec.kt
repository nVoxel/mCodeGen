package com.voxeldev.mcodegen.v1.scenarios.tdCommon.models

import org.jetbrains.kotlin.com.intellij.lang.jvm.JvmModifier
import org.jetbrains.kotlin.com.intellij.psi.PsiClass
import org.jetbrains.kotlin.com.intellij.psi.PsiField

data class CommonClassSpec(
    val clazz: PsiClass,
    val commonFields: List<PsiField>,
    val isAbstract: Boolean = clazz.hasModifier(JvmModifier.ABSTRACT),
)