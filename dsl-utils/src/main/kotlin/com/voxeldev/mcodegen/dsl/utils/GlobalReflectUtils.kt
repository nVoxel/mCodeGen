package com.voxeldev.mcodegen.dsl.utils

import kotlin.reflect.KClass

object GlobalReflectUtils {

    fun getClassSimpleNameOrThrow(clazz: KClass<*>): String {
        return clazz.simpleName
        ?: throw IllegalArgumentException("The provided class has no name")
    }
}