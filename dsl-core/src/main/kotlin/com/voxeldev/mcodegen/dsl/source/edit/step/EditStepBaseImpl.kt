package com.voxeldev.mcodegen.dsl.source.edit.step

import com.voxeldev.mcodegen.dsl.utils.GlobalReflectUtils

abstract class EditStepBaseImpl : EditStep {

    override val name: String = GlobalReflectUtils.getClassSimpleNameOrThrow(clazz = this::class)
}