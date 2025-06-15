package com.voxeldev.mcodegen.dsl.language.kotlin

internal class TestFunctions() {

    fun testFuncOne(i: String): Int {
        return i.length
    }

    fun <U> testFuncTwo(i: U): U {
        return i
    }
}