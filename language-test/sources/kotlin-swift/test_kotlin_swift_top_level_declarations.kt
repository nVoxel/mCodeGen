package com.voxeldev.mcodegen.dsl.language.kotlin

private fun testTopLevelMethod(testArg: String = "testTopLevelMethod testArg") {
    print(testArg)
}

private var testTopLevelVar: String = "testTopLevelVar testTopLevelVar"

private val testTopLevelVal: String = "testTopLevelVal testTopLevelVal"