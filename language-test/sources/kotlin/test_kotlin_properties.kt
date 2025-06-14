package com.voxeldev.mcodegen.dsl.language.kotlin

open class SomeBaseClass(
    open val someOpenVal: Number,
)

class SomeClass<T>(
    val someValOne: String,
    val someValTwo: List<String>,
    override val someOpenVal: Int,
) : SomeBaseClass(someOpenVal) {
    var someValThree: Number = 123
    
    constructor(someValThree: Double) : this("someValThree someValThree", listOf(), 1) {
        this.someValThree = someValThree
    }
}

class SomeAnotherClass constructor(
    val someAnotherValOne: String,
    val someAnotherValTwo: List<String>,
)