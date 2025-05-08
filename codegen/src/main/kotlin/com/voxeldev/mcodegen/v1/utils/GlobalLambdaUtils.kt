package com.voxeldev.mcodegen.v1.utils

object GlobalLambdaUtils {

    fun Boolean.lazyAnd(other: () -> Boolean): Boolean = if (!this) false else other()
}