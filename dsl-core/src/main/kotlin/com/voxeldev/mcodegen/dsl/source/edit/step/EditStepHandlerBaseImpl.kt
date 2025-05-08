package com.voxeldev.mcodegen.dsl.source.edit.step

import com.voxeldev.mcodegen.dsl.utils.GlobalReflectUtils

abstract class EditStepHandlerBaseImpl() : EditStepHandler {

    override var handlingEditStepName: String = GlobalReflectUtils.getClassNameOrThrow(clazz = this::class)
}