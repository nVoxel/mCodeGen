package com.voxeldev.mcodegen.dsl.ir

/**
 * Represents a location in the source code in the IR (Intermediate Representation) system.
 * This class contains information about where an element is defined in the source code,
 * including file path, line number, and column number.
 */
data class IrLocation(
    val filePath: String,
    val lineNumber: Int,
    val columnNumber: Int
)
