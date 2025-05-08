package com.voxeldev.mcodegen.v1.utils

object GlobalPathUtils {
    val currentPath: String = System.getProperty("user.dir")

    val outputPath: String = "$currentPath/generated"
}