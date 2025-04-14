package com.voxeldev.mcodegen.dsl.ir

data class IrLocation(
    val filePath: String,
    val lineNumber: Int?,
    val columnNumber: Int?,
)
