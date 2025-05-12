package com.voxeldev.mcodegen.dsl.source.edit.step

import com.voxeldev.mcodegen.dsl.utils.GlobalReflectUtils

abstract class EditStepHandlerBaseImpl<T : EditStep>() : EditStepHandler<T> {

    override var handlingEditStepName: String =
        GlobalReflectUtils.getClassSimpleNameOrThrow(clazz = this::class)
            .removeSuffix("Handler")
}