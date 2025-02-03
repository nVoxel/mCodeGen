package com.voxeldev.mcodegen.utils

object GlobalLambdaUtils {

    fun Boolean.lazyAnd(other: () -> Boolean): Boolean = if (!this) false else other()
}