package com.voxeldev.mcodegen.utils

import kotlin.reflect.KClass

object GlobalReflectUtils {

    fun getClassNameOrThrow(clazz: KClass<*>): String {
        return clazz.qualifiedName ?: clazz.simpleName
        ?: throw IllegalArgumentException("The provided class has no name")
    }
}