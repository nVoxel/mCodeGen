package com.voxeldev.mcodegen.v2.dsl.utils.source.edit.step

import com.voxeldev.mcodegen.dsl.source.edit.step.EditStepBaseImpl

data class AddJavaGettersEditStep(
    val classNamesToAddGetters: Set<String>,
    val ignoredFieldNames: Set<String>,
    val fixGettersForKotlin: Boolean,
) : EditStepBaseImpl()