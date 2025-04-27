package com.voxeldev.mcodegen.dsl.source.edit.step

import com.voxeldev.mcodegen.utils.GlobalReflectUtils

abstract class EditStepHandlerBaseImpl() : EditStepHandler {

    override var handlingEditStepName: String = GlobalReflectUtils.getClassNameOrThrow(clazz = this::class)
}