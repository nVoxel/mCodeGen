package com.voxeldev.mcodegen.utils

object GlobalPathUtils {
    val currentPath: String = System.getProperty("user.dir")

    val outputPath: String = "$currentPath/generated"
}